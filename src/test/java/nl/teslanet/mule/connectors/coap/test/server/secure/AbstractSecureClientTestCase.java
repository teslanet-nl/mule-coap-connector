/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2022 (teslanet.nl) Rogier Cobben
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

import java.net.URI;
import java.security.cert.Certificate;
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
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.SingleNodeConnectionIdGenerator;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedSinglePskStore;
import org.eclipse.californium.scandium.dtls.x509.SingleCertificateProvider;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;

/**
 * Template class for testing using a secure client
 *
 */
public abstract class AbstractSecureClientTestCase extends AbstractTestCase
{
    private static final char[] KEY_STORE_PASSWORD = "endPass".toCharArray();
    private static final String KEY_STORE_LOCATION = "certs/keyStore.jks";
    private static final char[] TRUST_STORE_PASSWORD = "rootPass".toCharArray();
    private static final String TRUST_STORE_LOCATION = "certs/trustStore.jks";

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
        Configuration config= Configuration.createStandardWithoutFile();
        // load key store
        SslContextUtil.Credentials clientCredentials = SslContextUtil.loadCredentials(
                SslContextUtil.CLASSPATH_SCHEME + KEY_STORE_LOCATION, "client", KEY_STORE_PASSWORD,
                KEY_STORE_PASSWORD);
        Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(
                SslContextUtil.CLASSPATH_SCHEME + TRUST_STORE_LOCATION, "root", TRUST_STORE_PASSWORD);

        DtlsConnectorConfig.Builder dtlsBuilder = new DtlsConnectorConfig.Builder(config);
        dtlsBuilder.setAdvancedPskStore(new AdvancedSinglePskStore("Client_identity", "secretPSK".getBytes()));
        dtlsBuilder.setCertificateIdentityProvider(new SingleCertificateProvider(clientCredentials.getPrivateKey(), clientCredentials.getCertificateChain(),
                CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509));
        dtlsBuilder.setAdvancedCertificateVerifier(StaticNewAdvancedCertificateVerifier.builder()
                .setTrustedCertificates(trustedCertificates).setTrustAllRPKs().build());
        dtlsBuilder.setConnectionIdGenerator(new SingleNodeConnectionIdGenerator(0));

        //connector

        DTLSConnector dtlsConnector = new DTLSConnector(dtlsBuilder.build());

        //endpoint

        CoapEndpoint.Builder endpointBuilder= new CoapEndpoint.Builder();
        endpointBuilder.setConnector( dtlsConnector );
        config.set( CoapConfig.ACK_TIMEOUT, 20000, TimeUnit.MILLISECONDS );
        config.set(CoapConfig.EXCHANGE_LIFETIME, 30000L, TimeUnit.MILLISECONDS );
        //config.setLong(Configuration.Keys.DTLS_AUTO_RESUME_TIMEOUT, 30000L );
        endpointBuilder.setConfiguration( config );
        endpoint= endpointBuilder.build();
        endpoint.start();

        client= new CoapClient();
        client.setTimeout( 5000L );
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

    @Test(timeout= 60000L)
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
