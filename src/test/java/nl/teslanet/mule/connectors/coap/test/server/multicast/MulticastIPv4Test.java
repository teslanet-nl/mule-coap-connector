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
package nl.teslanet.mule.connectors.coap.test.server.multicast;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.network.interceptors.MessageTracer;
import org.eclipse.californium.elements.UdpMulticastConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractServerTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.Data;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;


//TODO add IPv6 test
@RunnerDelegateTo(Parameterized.class)
public class MulticastIPv4Test extends AbstractServerTestCase
{
    @Parameters(name= "Request= {0}, port= {1}, path= {2}, contentSize= {3}")
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []{
                //default maxResourceBodySize on server
                { Code.GET, 5683, "/service/get_me", 10, true, false, "listen1" },
                { Code.PUT, 5683, "/service/put_me", 10, false, false, "listen1" },
                { Code.POST, 5683, "/service/post_me", 10, false, false, "listen1" },
                { Code.DELETE, 5683, "/service/delete_me", 10, true, false, "listen1" }, } );
    }

    /**
     * Request code to test
     */
    @Parameter(0)
    public Code requestCode;

    /**
     * Test server port
     */
    @Parameter(1)
    public int port;

    /**
     * Test resource to call
     */
    @Parameter(2)
    public String resourcePath;

    /**
     * Test message content size
     */
    @Parameter(3)
    public int contentSize;

    /**
     * True when request is not supposed to have a payload, but does
     */
    @Parameter(4)
    public boolean unintendedPayload;

    /**
     * True when response should be 4.13 Request Entity Too Large
     */
    @Parameter(5)
    public boolean expectTooLarge;

    /**
     * The Id used for spying the Mule flow
     */
    @Parameter(6)
    public String spyId;

    /**
     * MAX_RESOURCE_BODY_SIZE of the testclient.
     */
    public int maxResourceBodySize= 16001;

    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/multicast/testserverIPv4.xml";
    };

    @Before
    public void additionalSetUp() throws UnknownHostException, SocketException
    {
        try
        {
            client.setURI( new URI( "coap", null, "224.0.1.187", port, resourcePath, null, null ).toString() );
        }
        catch ( URISyntaxException e )
        {
            throw new RuntimeException( e );
        }
        NetworkConfig config= NetworkConfig.createStandardWithoutFile();
        config.setInt( NetworkConfig.Keys.MAX_RESOURCE_BODY_SIZE, maxResourceBodySize );

        //unicast connector
        //UDPConnector udpConnector = new UDPConnector(new InetSocketAddress( "127.0.0.1", port));
        //udpConnector.setReuseAddress(true);
        //multicast connector
        UdpMulticastConnector.Builder multiCastConnectorBuilder= new UdpMulticastConnector.Builder();
        multiCastConnectorBuilder.setLocalAddress( InetAddress.getByName( "127.0.0.1" ), 0 );
        multiCastConnectorBuilder.setOutgoingMulticastInterface(NetworkInterface.getByName( "lo" )  );
        multiCastConnectorBuilder.addMulticastGroup( InetAddress.getByName( "224.0.1.187" ), NetworkInterface.getByName( "lo" ));
        UdpMulticastConnector sender= multiCastConnectorBuilder.build();
        sender.setReuseAddress( true );
        sender.setLoopbackMode( true );
        //endpoint
        CoapEndpoint.Builder builder= new CoapEndpoint.Builder();
        builder.setNetworkConfig( config );
        builder.setConnector( sender );
        CoapEndpoint endpoint= builder.build(); 
        //endpoint.addMulticastReceiver( receiver );
        endpoint.addInterceptor( new MessageTracer() );
        client.setEndpoint( endpoint );
    }

    @After
    public void additionalTearDown()
    {
        client.getEndpoint().destroy();
    }

    protected void validateInboundResponse( CoapResponse response, MuleEventSpy spy )
    {
        if ( !expectTooLarge )
        {
            assertNotNull( "no response", response );
            assertTrue( "response indicates failure: " + response.getCode() + " msg: " + response.getResponseText(), response.isSuccess() );
            assertEquals( "wrong spy activation count", 1, spy.getEvents().size() );
            assertTrue( "wrong payload in requets", Data.validateContent( (byte[]) spy.getEvents().get( 0 ).getContent(), contentSize ) );
        }
        else
        {
            assertNotNull( "no response", response );
            assertEquals( "response is not REQUEST_ENTITY_TOO_LARGE : " + response.getResponseText(), ResponseCode.REQUEST_ENTITY_TOO_LARGE, response.getCode() );
        }
    }

    protected void validateOutboundResponse( CoapResponse response, MuleEventSpy spy )
    {
        assertNotNull( "no response on: " + requestCode, response );
        assertTrue( "response indicates failure: " + response.getCode() + " msg: " + response.getResponseText(), response.isSuccess() );
        assertEquals( "wrong spy activation count", 1, spy.getEvents().size() );
        assertTrue( "wrong payload in response", Data.validateContent( response.getPayload(), contentSize ) );
    }

    @Test(timeout= 20000L)
    public void testMulticast() throws Exception
    {
        MultiCoapHandler handler= new MultiCoapHandler();
        //spyRequestMessage();
        MuleEventSpy spy= new MuleEventSpy( spyId );
        spy.clear();

        //spyActivated.set( false );
        client.useLateNegotiation();
        Request request= new Request( requestCode, Type.NON );
        if ( unintendedPayload ) request.setUnintendedPayload();
        request.setPayload( Data.getContent( contentSize ) );

        client.advanced( handler, request );
        while ( handler.waitOn( 15000 ) );
        validateInboundResponse( handler.getResponse(), spy );
    }

    private class MultiCoapHandler implements CoapHandler
    {

        private boolean on;

        CoapResponse response= null;

        public synchronized boolean waitOn( long timeout )
        {
            on= false;
            try
            {
                wait( timeout );
            }
            catch ( InterruptedException e )
            {
            }
            return on;
        }

        private synchronized void on()
        {
            on= true;
            notifyAll();
        }

        @Override
        public void onLoad( CoapResponse response )
        {
            this.response= response;
            on();
        }

        @Override
        public void onError()
        {
            System.err.println( "error" );
        }

        public CoapResponse getResponse()
        {
            return response;
        }
    };
}
