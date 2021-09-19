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
package nl.teslanet.mule.connectors.coap.test.client.basic;


import java.net.InetSocketAddress;
import java.net.SocketException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;


/**
 * Server used to test client 
 *
 */
public class ResponseTestServer extends CoapServer
{
    /**
     * Network configuration is set to standards 
     */
    private static NetworkConfig networkConfig= NetworkConfig.createStandardWithoutFile();

    /**
     * Default Constructor for test server.
     */
    public ResponseTestServer() throws SocketException
    {
        this( CoAP.DEFAULT_COAP_PORT );
    }

    /**
     * Constructor for test server.
     */
    public ResponseTestServer( int port ) throws SocketException
    {
        super( networkConfig );
        addEndpoints( port );
        addResources();
    }

    private void addResources()
    {
        // provide an instance of a Hello-World resource
        add( new ResponseResource( "response", ResponseCode.CONTENT ) );
        Resource parent= getRoot().getChild( "response" );
        for ( ResponseCode code : ResponseCode.values() )
        {
            parent.add( new ResponseResource( code.name(), code ) );
        }
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
     * Resource that to test payloads
     */
    class ResponseResource extends CoapResource
    {
        /**
         * the request payload size to verify
         */
        private ResponseCode responseCode;

        /**
         * @param responseCode the response to return
         */
        public ResponseResource( String name, ResponseCode responseCode )
        {
            // set resource name
            super( name );

            // set display name
            getAttributes().setTitle( name );

            //set response
            this.responseCode= responseCode;
        }

        @Override
        public void handleGET( CoapExchange exchange )
        {
            handleRequest( exchange );
        }

        @Override
        public void handlePOST( CoapExchange exchange )
        {
            handleRequest( exchange );
        }

        @Override
        public void handlePUT( CoapExchange exchange )
        {
            handleRequest( exchange );
        }

        @Override
        public void handleDELETE( CoapExchange exchange )
        {
            handleRequest( exchange );
        }

        /**
         * Generic handler
         * @param exchange
         */
        private void handleRequest( CoapExchange exchange )
        {
            exchange.respond( responseCode, "Response is: " + responseCode.name() );
        }
    }
}
