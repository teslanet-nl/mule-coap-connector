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
package nl.teslanet.mule.connectors.coap.test.client.query;


import java.net.InetSocketAddress;
import java.net.SocketException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;


/**
 * Server used to test client 
 *
 */
public class QueryTestServer extends CoapServer
{
    /**
     * Network configuration is set to standards 
     */
    private static NetworkConfig networkConfig= NetworkConfig.createStandardWithoutFile();

    /**
     * Default Constructor for test server.
     */
    public QueryTestServer() throws SocketException
    {
        this( CoAP.DEFAULT_COAP_PORT );
    }

    /**
     * Constructor for test server.
     */
    public QueryTestServer( int port ) throws SocketException
    {
        super( networkConfig );
        addEndpoints( port );
        addResources();
    }

    /**
     * Provide resources.
     */
    private void addResources()
    {
        add( new NoneResource( "query" ) );
        getRoot().getChild( "query" ).add( new QueryResource( "test" ) );
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
     * Resource
     */
    class QueryResource extends CoapResource
    {
        public QueryResource( String name )
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
            exchange.respond( ResponseCode.CONTENT, "GET called on: " + exchange.advanced().getRequest().getURI() );
        }

        @Override
        public void handlePOST( CoapExchange exchange )
        {
            // respond to the request
            exchange.respond( ResponseCode.CREATED, "POST called on: " + exchange.advanced().getRequest().getURI() );
        }

        @Override
        public void handlePUT( CoapExchange exchange )
        {
            // respond to the request
            exchange.respond( ResponseCode.CHANGED, "PUT called on: " + exchange.advanced().getRequest().getURI() );
        }

        @Override
        public void handleDELETE( CoapExchange exchange )
        {
            // respond to the request
            exchange.respond( ResponseCode.DELETED, "DELETE called on: " + exchange.advanced().getRequest().getURI() );
        }
    }
}
