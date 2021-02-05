/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2021 (teslanet.nl) Rogier Cobben
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
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractSecureTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.Data;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;
import nl.teslanet.shaded.org.eclipse.californium.core.CoapClient;
import nl.teslanet.shaded.org.eclipse.californium.core.CoapResponse;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.CoAP.Code;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.Request;
import nl.teslanet.shaded.org.eclipse.californium.core.network.CoapEndpoint;
import nl.teslanet.shaded.org.eclipse.californium.core.network.config.NetworkConfig;
import nl.teslanet.shaded.org.eclipse.californium.scandium.DTLSConnector;
import nl.teslanet.shaded.org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import nl.teslanet.shaded.org.eclipse.californium.scandium.config.DtlsConnectorConfig.Builder;
import nl.teslanet.shaded.org.eclipse.californium.scandium.dtls.CertificateType;
import nl.teslanet.shaded.org.eclipse.californium.scandium.dtls.pskstore.AdvancedMultiPskStore;
import nl.teslanet.shaded.org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;

/**
 * Template class for testing using a secure client
 *
 */
public abstract class AbstractSecureClientTestCase extends AbstractSecureTestCase
{
    private static URI uri= null;

    private CoapClient client= null;

    private static ArrayList< Code > inboundCalls;

    private static ArrayList< Code > outboundCalls;

    private static HashMap< Code, String > paths;

    private static CoapEndpoint endpoint;

    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/secure/testserver1.xml";
    };

    @BeforeClass
    static public void setUpClass() throws Exception
    {
        inboundCalls= new ArrayList< Code >();
        inboundCalls.add( Code.GET );
        inboundCalls.add( Code.PUT );
        inboundCalls.add( Code.POST );
        inboundCalls.add( Code.DELETE );
        outboundCalls= new ArrayList< Code >();
        outboundCalls.add( Code.GET );
        outboundCalls.add( Code.PUT );
        outboundCalls.add( Code.POST );
        outboundCalls.add( Code.DELETE );

        paths= new HashMap< Code, String >();
        paths.put( Code.GET, "/service/get_me" );
        paths.put( Code.PUT, "/service/put_me" );
        paths.put( Code.POST, "/service/post_me" );
        paths.put( Code.DELETE, "/service/delete_me" );

        uri= new URI( "coaps", "127.0.0.1", null, null, null );

    }

    @AfterClass
    static public void tearDownClass()
    {
    }

    @Before
    public void setUp() throws Exception
    {
        //keyStore
        KeyStore keyStore= KeyStore.getInstance( "JKS" );
        InputStream in= Data.readResourceAsStream( "certs/keyStore.jks" );
        keyStore.load( in, "endPass".toCharArray() );
        //in.close();

        //trustStore
        KeyStore trustStore= KeyStore.getInstance( "JKS" );
        in= Data.readResourceAsStream( "certs/trustStore.jks" );
        trustStore.load( in, "rootPass".toCharArray() );
        //in.close();

        // You can load multiple certificates if needed
        Certificate[] trustedCertificates= new Certificate [1];
        trustedCertificates[0]= trustStore.getCertificate( "root" );

        //pskStore
        AdvancedMultiPskStore pskStore= new AdvancedMultiPskStore();

        //verifier builder
        StaticNewAdvancedCertificateVerifier.Builder verifierBuilder= StaticNewAdvancedCertificateVerifier.builder();
        verifierBuilder.setTrustAllRPKs();
        verifierBuilder.setTrustedCertificates( trustedCertificates );

        //dtls builder
        Builder dtlsBuilder= new DtlsConnectorConfig.Builder();
        dtlsBuilder.setAdvancedPskStore( pskStore );
        dtlsBuilder.setIdentity( (PrivateKey) keyStore.getKey( "client", "endPass".toCharArray() ), keyStore.getCertificateChain( "client" ), CertificateType.X_509 );
        dtlsBuilder.setAdvancedCertificateVerifier( verifierBuilder.build() );
        dtlsBuilder.setEnableAddressReuse( false );
        dtlsBuilder.setConnectionThreadCount( 1 );

        //connector

        DTLSConnector dtlsConnector= new DTLSConnector( dtlsBuilder.build() );

        //endpoint

        CoapEndpoint.Builder endpointBuilder= new CoapEndpoint.Builder();
        endpointBuilder.setConnector( dtlsConnector );
        NetworkConfig config= NetworkConfig.createStandardWithoutFile();
        config.setInt( NetworkConfig.Keys.ACK_TIMEOUT, 20000 );
        config.setLong( NetworkConfig.Keys.EXCHANGE_LIFETIME, 30000L );
        //config.setLong(NetworkConfig.Keys.DTLS_AUTO_RESUME_TIMEOUT, 30000L );
        endpointBuilder.setNetworkConfig( config );
        endpoint= endpointBuilder.build();
        endpoint.start();

        client= new CoapClient();
        client.setTimeout( 3000L );
        client.setEndpoint( endpoint );
    }

    @After
    public void tearDown() throws Exception
    {
        if ( client != null ) client.shutdown();
        client= null;
        endpoint.stop();
        endpoint.destroy();
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

    @Test(timeout= 20000L)
    public void testInbound() throws Exception
    {
        expectException();
        MuleEventSpy spy= spyMessage();
        String payload= "test-payload";

        for ( Code call : inboundCalls )
        {
            spy.clear();
            client.useLateNegotiation();
            Request request= new Request( call );
            request.setURI( uri.resolve( getPath( call ) ) );
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

            CoapResponse response= client.advanced( request );

            assertNotNull( "get gave no response", response );
            assertTrue( "response indicates failure: " + response.getCode() + " msg: " + response.getResponseText(), response.isSuccess() );
            assertEquals( "Spy has collected wrong number of events", 1, spy.getEvents().size() );
            assertEquals( "payload has wrong class", payload.getBytes().getClass(), spy.getEvents().get( 0 ).getContent().getClass() );
            assertArrayEquals( "property has wrong value", payload.getBytes(), (byte[]) spy.getEvents().get( 0 ).getContent() );

            client.shutdown();
        }
    }

    @Test
    public void testOutbound() throws Exception
    {
        expectException();
        String payload= "test-payload";
        MuleEventSpy spy= spyMessage( payload.getBytes() );

        for ( Code call : outboundCalls )
        {
            spy.clear();
            Request request= new Request( call );
            request.setURI( uri.resolve( getPath( call ) ) );
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

            CoapResponse response= client.advanced( request );

            assertNotNull( "get gave no response", response );
            assertTrue( "response indicates failure: " + response.getCode() + " msg: " + response.getResponseText(), response.isSuccess() );
            assertArrayEquals( "wrong payload in response", payload.getBytes(), response.getPayload() );
            assertEquals( "wrong number of spied events", 1, spy.getEvents().size() );
            client.shutdown();
        }
    }

}
