/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2024 (teslanet.nl) Rogier Cobben
 * 
 * Contributors:
 *     (teslanet.nl) Rogier Cobben - initial creation
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */
package nl.teslanet.mule.connectors.coap.test.server.secure;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.SingleNodeConnectionIdGenerator;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedSinglePskStore;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;


/**
 * Test PSK Server.
 */
public class PskStoreEncodedServerTest extends AbstractTestCase
{
    private CoapClient clientValid= null;

    private CoapClient clientInvalidId= null;

    private CoapClient clientInvalidKey= null;

    private static ArrayList< Code > calls;

    private static HashMap< Code, String > paths;

    private static CoapEndpoint endpoint;

    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/secure/testserver-pskfile-encoded.xml";
    };

    protected URI getUri() throws URISyntaxException
    {
        return new URI( "coaps://127.0.0.1:5684" );
    };

    @BeforeClass
    static public void setUpClass() throws Exception
    {
        calls= new ArrayList< Code >();
        calls.add( Code.GET );
        calls.add( Code.PUT );
        calls.add( Code.POST );
        calls.add( Code.DELETE );

        paths= new HashMap< Code, String >();
        paths.put( Code.GET, "/service/get_me" );
        paths.put( Code.PUT, "/service/put_me" );
        paths.put( Code.POST, "/service/post_me" );
        paths.put( Code.DELETE, "/service/delete_me" );
    }

    @AfterClass
    static public void tearDownClass()
    {
    }

    public CoapClient createClient( String identity, String key ) throws Exception
    {
        //dtls config
        Configuration config= Configuration.createStandardWithoutFile();
        DtlsConnectorConfig.Builder dtlsBuilder= new DtlsConnectorConfig.Builder( config );
        dtlsBuilder.setAdvancedPskStore( new AdvancedSinglePskStore( identity, key.getBytes() ) );
        dtlsBuilder.setConnectionIdGenerator( new SingleNodeConnectionIdGenerator( 0 ) );
        //connector
        DTLSConnector dtlsConnector= new DTLSConnector( dtlsBuilder.build() );
        //endpoint
        CoapEndpoint.Builder endpointBuilder= new CoapEndpoint.Builder();
        endpointBuilder.setConnector( dtlsConnector );
        config.set( CoapConfig.ACK_TIMEOUT, 20, TimeUnit.SECONDS );
        config.set( CoapConfig.EXCHANGE_LIFETIME, 30L, TimeUnit.SECONDS );
        endpointBuilder.setConfiguration( config );
        endpoint= endpointBuilder.build();
        endpoint.start();

        CoapClient client= new CoapClient( getUri() );
        client.setTimeout( 5000L );
        client.setEndpoint( endpoint );
        return client;
    }

    @Before
    public void setUp() throws Exception
    {
        clientValid= createClient( "Client_identity", "secretPSK" );
        clientInvalidId= createClient( "Client_identitynNonvalid", "secretPSK" );
        clientInvalidKey= createClient( "Client_identity", "secretPSKNonvalid" );
    }

    public void tearDownClient( CoapClient client ) throws Exception
    {
        if ( client != null )
        {
            client.shutdown();
            client.getEndpoint().stop();
            client.getEndpoint().destroy();
            client= null;
        }
    }

    @After
    public void tearDown() throws Exception
    {
        tearDownClient( clientValid );
        tearDownClient( clientInvalidId );
        tearDownClient( clientInvalidKey );
    }

    protected void expectException()
    {
        // Default None      
    }

    protected String getPath( Code call )
    {
        return paths.get( call );
    }

    /**
     * Create spy for requests.
     * @return the spy
     */
    private MuleEventSpy spyMessage()
    {
        return spyMessage( null );
    }

    /**
     * Create spy for requests with a message replacement
     * @return the spy
     */
    private MuleEventSpy spyMessage( byte[] replacement )
    {
        MuleEventSpy spy= new MuleEventSpy( "securityTest", null, replacement );
        spy.clear();
        return spy;
    }

    @Test
    public void testInboundPayload() throws Exception
    {
        expectException();
        MuleEventSpy spy= spyMessage();
        String payload= "test-payload";

        for ( Code call : calls )
        {
            spy.clear();
            clientValid.useLateNegotiation();
            Request request= new Request( call );
            request.setURI( getUri().resolve( getPath( call ) ) );
            switch ( call )
            {
                case DELETE:
                case GET:
                    request.setUnintendedPayload();
                    break;
                default:
                    break;
            }
            request.setPayload( payload );

            CoapResponse response= clientValid.advanced( request );

            assertNotNull( "get gave no response", response );
            assertTrue( "response indicates failure: " + response.getCode() + " msg: " + response.getResponseText(), response.isSuccess() );
            assertEquals( "Spy has collected wrong number of events", 1, spy.getEvents().size() );
            assertEquals( "payload has wrong class", payload.getBytes().getClass(), spy.getEvents().get( 0 ).getContent().getClass() );
            assertArrayEquals( "property has wrong value", payload.getBytes(), (byte[]) spy.getEvents().get( 0 ).getContent() );
        }
    }

    @Test
    public void testOutboundPayload() throws Exception
    {
        expectException();
        String payload= "test-payload";
        MuleEventSpy spy= spyMessage( payload.getBytes() );

        for ( Code call : calls )
        {
            spy.clear();
            Request request= new Request( call );
            request.setURI( getUri().resolve( getPath( call ) ) );
            switch ( call )
            {
                case DELETE:
                case GET:
                    request.setUnintendedPayload();
                    break;
                default:
                    break;
            }
            request.setPayload( "nothing important" );

            CoapResponse response= clientValid.advanced( request );

            assertNotNull( "get gave no response", response );
            assertTrue( "response indicates failure: " + response.getCode() + " msg: " + response.getResponseText(), response.isSuccess() );
            assertArrayEquals( "wrong payload in response", payload.getBytes(), response.getPayload() );
            assertEquals( "wrong number of spied events", 1, spy.getEvents().size() );
        }
    }

    @Test
    public void testInvalidId() throws Exception
    {
        expectException();
        MuleEventSpy spy= spyMessage();
        String payload= "test-payload";

        for ( Code call : calls )
        {
            spy.clear();
            clientInvalidId.useLateNegotiation();
            Request request= new Request( call );
            request.setURI( getUri().resolve( getPath( call ) ) );
            switch ( call )
            {
                case DELETE:
                case GET:
                    request.setUnintendedPayload();
                    break;
                default:
                    break;
            }
            request.setPayload( payload );

            CoapResponse response= clientInvalidId.advanced( request );

            assertNull( "get gave invalid response", response );
            assertEquals( "Spy has collected wrong number of events", 0, spy.getEvents().size() );
        }
    }

    @Test
    public void testInvalidKey() throws Exception
    {
        expectException();
        MuleEventSpy spy= spyMessage();
        String payload= "test-payload";

        for ( Code call : calls )
        {
            spy.clear();
            clientInvalidKey.useLateNegotiation();
            Request request= new Request( call );
            request.setURI( getUri().resolve( getPath( call ) ) );
            switch ( call )
            {
                case DELETE:
                case GET:
                    request.setUnintendedPayload();
                    break;
                default:
                    break;
            }
            request.setPayload( payload );

            CoapResponse response= clientInvalidKey.advanced( request );

            assertNull( "get gave invalid response", response );
            assertEquals( "Spy has collected wrong number of events", 0, spy.getEvents().size() );
        }
    }

}
