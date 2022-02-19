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
package nl.teslanet.mule.connectors.coap.test.client.multicast;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.network.interceptors.MessageTracer;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.elements.Connector;
import org.eclipse.californium.elements.UDPConnector;
import org.eclipse.californium.elements.UdpMulticastConnector;


/**
 * Server used to test client 
 *
 */
public class MulticastTestServer extends CoapServer
{
    /**
     * Network configuration is set to standards 
     */
    private static NetworkConfig networkConfig= NetworkConfig.createStandardWithoutFile();

    /**
     * Default Constructor for test server.
     * @throws UnknownHostException 
     */
    public MulticastTestServer() throws SocketException, UnknownHostException
    {
        this( 5690, 5683 );
    }

    /**
     * Constructor for test server.
     * @throws UnknownHostException 
     */
    public MulticastTestServer( int port, int multicastPort ) throws SocketException, UnknownHostException
    {
        super( networkConfig );
        addEndpoints( port, multicastPort );
        addResources();
    }

    private void addResources()
    {
        // provide an instance of a Hello-World resource
        add( new GetResource( "basic" ) );
        getRoot().getChild( "basic" ).add( new GetResource( "get_me" ) );
        getRoot().getChild( "basic" ).add( new NoneResource( "do_not_get_me" ) );
        getRoot().getChild( "basic" ).add( new PutResource( "put_me" ) );
        getRoot().getChild( "basic" ).add( new NoneResource( "do_not_put_me" ) );
        getRoot().getChild( "basic" ).add( new PostResource( "post_me" ) );
        getRoot().getChild( "basic" ).add( new NoneResource( "do_not_post_me" ) );
        getRoot().getChild( "basic" ).add( new DeleteResource( "delete_me" ) );
        getRoot().getChild( "basic" ).add( new NoneResource( "do_not_delete_me" ) );
    }

    /**
     * Add test endpoints listening on default CoAP port.
     * @throws UnknownHostException 
     * @throws SocketException 
     */
    private void addEndpoints( int port, int multicastPort ) throws UnknownHostException, SocketException
    {
        UDPConnector udpConnector = new UDPConnector(new InetSocketAddress( "127.0.0.1", port));
        udpConnector.setReuseAddress(true);

        UdpMulticastConnector.Builder multiCastConnectorBuilder= new UdpMulticastConnector.Builder();
        multiCastConnectorBuilder.setLocalAddress( InetAddress.getByName( "224.0.1.187" ), multicastPort );
        multiCastConnectorBuilder.setOutgoingMulticastInterface(InetAddress.getByName( "127.0.0.1" )  );
        multiCastConnectorBuilder.addMulticastGroup( InetAddress.getByName( "224.0.1.187" ), NetworkInterface.getByName( "lo" ));
        UdpMulticastConnector receiver= multiCastConnectorBuilder.build();
        receiver.setReuseAddress( true );
        receiver.setLoopbackMode( true );
        CoapEndpoint.Builder builder= new CoapEndpoint.Builder();
        builder.setNetworkConfig( networkConfig );
        builder.setConnector( udpConnector );
        
        CoapEndpoint endpoint= builder.build(); 
        endpoint.addMulticastReceiver( receiver );
        endpoint.addInterceptor( new MessageTracer() );
        addEndpoint( endpoint );
    }

    /**
     * Resource without operations
     */
    class NoneResource extends CoapResource
    {
        public NoneResource( String name )
        {
            // set resource name
            super( name );
            // set display name
            getAttributes().setTitle( name );
        }
    }

    /**
     * Resource that allows GET only
     */
    class GetResource extends CoapResource
    {
        public GetResource( String name )
        {
            // set resource name
            super( name );
            // set display name
            getAttributes().setTitle( name );
        }

        @Override
        public void handleGET( CoapExchange exchange )
        {
            // respond to the request
            exchange.respond( ResponseCode.CONTENT, "GET called on: " + this.getURI() );
        }
    }

    /**
     * Resource that allows POST only
     */
    class PostResource extends CoapResource
    {
        public PostResource( String name )
        {
            // set resource name
            super( name );
            // set display name
            getAttributes().setTitle( name );
        }

        @Override
        public void handlePOST( CoapExchange exchange )
        {
            // respond to the request
            exchange.respond( ResponseCode.CREATED, "POST called on: " + this.getURI() );
        }
    }

    /**
     * Resource that allows PUT only
     */
    class PutResource extends CoapResource
    {
        public PutResource( String name )
        {
            // set resource name
            super( name );
            // set display name
            getAttributes().setTitle( name );
        }

        @Override
        public void handlePUT( CoapExchange exchange )
        {
            // respond to the request
            exchange.respond( ResponseCode.CHANGED, "PUT called on: " + this.getURI() );
        }
    }

    /**
     * Resource that allows DELETE only
     */
    class DeleteResource extends CoapResource
    {
        public DeleteResource( String name )
        {
            // set resource name
            super( name );
            // set display name
            getAttributes().setTitle( name );
        }

        @Override
        public void handleDELETE( CoapExchange exchange )
        {
            // respond to the request
            exchange.respond( ResponseCode.DELETED, "DELETE called on: " + this.getURI() );
        }
    }
}
