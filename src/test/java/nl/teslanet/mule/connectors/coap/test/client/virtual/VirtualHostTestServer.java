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
package nl.teslanet.mule.connectors.coap.test.client.virtual;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;

import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;


/**
 * Server used to test client 
 *
 */
public class VirtualHostTestServer extends CoapServer
{
    /**
     * Network configuration is set to standards 
     */
    private static NetworkConfig networkConfig= NetworkConfig.createStandardWithoutFile();

    /**
     * Spy collecting events.
     */
    private MuleEventSpy spy;

    /**
     * Default Constructor for test server.
     */
    public VirtualHostTestServer( String spyId ) throws SocketException
    {
        this( CoAP.DEFAULT_COAP_SECURE_PORT );
        this.spy= new MuleEventSpy( spyId );
    }

    /**
     * Constructor for test server.
     */
    public VirtualHostTestServer( int port ) throws SocketException
    {
        super( networkConfig );
        addEndpoints( port );
        addResources();
        this.spy= new MuleEventSpy( Integer.toString( port ) );
    }

    private void addResources()
    {
        // provide an instance of a echo resource
        add( new SpyResource( "echo" ) );
    }

    /**
     * Add test endpoints listening on default CoAP port.
     */
    private void addEndpoints( int port )
    {
        CoapEndpoint.Builder builder= new CoapEndpoint.Builder();
        builder.setInetSocketAddress( new InetSocketAddress( port ) );
        builder.setNetworkConfig( networkConfig );
        addEndpoint( builder.build() );
    }

    /**
     * Resource that allows PUT only
     */
    class SpyResource extends CoapResource
    {
        public SpyResource( String name )
        {
            // set resource name
            super( name );
            // set display name
            getAttributes().setTitle( name );
        }

        @Override
        public void handleGET( CoapExchange exchange )
        {
            try
            {
                spy.event( exchange.getRequestOptions() );
            }
            catch ( IOException e )
            {
                exchange.respond( ResponseCode.INTERNAL_SERVER_ERROR, exchange.getRequestPayload() );
            }
            // respond to the request
            exchange.respond( ResponseCode.CONTENT, exchange.getRequestPayload() );
        }

        @Override
        public void handlePUT( CoapExchange exchange )
        {
            try
            {
                spy.event( exchange.getRequestOptions() );
            }
            catch ( IOException e )
            {
                exchange.respond( ResponseCode.INTERNAL_SERVER_ERROR, exchange.getRequestPayload() );
            }
            // respond to the request
            exchange.respond( ResponseCode.CHANGED, exchange.getRequestPayload() );
        }

        @Override
        public void handlePOST( CoapExchange exchange )
        {
            try
            {
                spy.event( exchange.getRequestOptions() );
            }
            catch ( IOException e )
            {
                exchange.respond( ResponseCode.INTERNAL_SERVER_ERROR, exchange.getRequestPayload() );
            }
            // respond to the request
            exchange.respond( ResponseCode.CHANGED, exchange.getRequestPayload() );
        }

        @Override
        public void handleDELETE( CoapExchange exchange )
        {
            try
            {
                spy.event( exchange.getRequestOptions() );
            }
            catch ( IOException e )
            {
                exchange.respond( ResponseCode.INTERNAL_SERVER_ERROR, exchange.getRequestPayload() );
            }
            // respond to the request
            exchange.respond( ResponseCode.DELETED, exchange.getRequestPayload() );
        }
    }
}
