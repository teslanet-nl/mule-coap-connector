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
package nl.teslanet.mule.connectors.coap.test.client.blockwise;


import java.net.SocketException;

import nl.teslanet.mule.connectors.coap.test.utils.Data;
import nl.teslanet.shaded.org.eclipse.californium.core.CoapResource;
import nl.teslanet.shaded.org.eclipse.californium.core.CoapServer;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.CoAP;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.CoAP.ResponseCode;
import nl.teslanet.shaded.org.eclipse.californium.core.network.config.NetworkConfig;
import nl.teslanet.shaded.org.eclipse.californium.core.server.resources.CoapExchange;


/**
 * Server used to test client 
 *
 */
public class TestServer extends CoapServer
{
    /**
     * Default Constructor for test server.
     */
    public TestServer() throws SocketException
    {
        this( CoAP.DEFAULT_COAP_PORT );
    }

    /**
     * Constructor for test server.
     */
    public TestServer( int port ) throws SocketException
    {
        super( NetworkConfig.createStandardWithoutFile(), port );
        addResources();
    }

    /**
     * Constructor for test server.
     */
    public TestServer( int port, int resource_size ) throws SocketException
    {
        super( NetworkConfig.createStandardWithoutFile().setInt( NetworkConfig.Keys.MAX_RESOURCE_BODY_SIZE, resource_size ), port );
        addResources();
    }

    /**
     * Add server resources
     */
    private void addResources()
    {
        // provide an instance of a Hello-World resource
        add( new PayloadResource( "blockwise", 0, 0 ) );
        getRoot().getChild( "blockwise" ).add( new PayloadResource( "rq0", -1, 2 ) );
        getRoot().getChild( "blockwise" ).add( new PayloadResource( "rsp0", 2, 0 ) );
        getRoot().getChild( "blockwise" ).add( new PayloadResource( "rq10", 10, 2 ) );
        getRoot().getChild( "blockwise" ).add( new PayloadResource( "rsp10", 2, 10 ) );
        getRoot().getChild( "blockwise" ).add( new PayloadResource( "rq8192", 8192, 2 ) );
        getRoot().getChild( "blockwise" ).add( new PayloadResource( "rsp8192", 2, 8192 ) );
        getRoot().getChild( "blockwise" ).add( new PayloadResource( "rq16000", 16000, 2 ) );
        getRoot().getChild( "blockwise" ).add( new PayloadResource( "rsp16000", 2, 16000 ) );
        getRoot().getChild( "blockwise" ).add( new PayloadResource( "rq16001", 16001, 2 ) );
        getRoot().getChild( "blockwise" ).add( new PayloadResource( "rsp16001", 2, 16001 ) );
    }

    /**
     * Resource that to test payloads
     */
    class PayloadResource extends CoapResource
    {
        /**
         * the request payload size to verify
         */
        private int requestPayloadSize;

        /**
         * the response payload to return
         */
        private int responsePayloadSize;

        /**
         * @param name identifies the resource
         * @param requestPayloadSize the request payload size to verify
         * @param responsePayloadSize the response payload to return
         */
        public PayloadResource( String name, int requestPayloadSize, int responsePayloadSize )
        {
            // set resource name
            super( name );

            // set display name
            getAttributes().setTitle( name );

            //set payload sizes
            this.requestPayloadSize= requestPayloadSize;
            this.responsePayloadSize= responsePayloadSize;
        }

        @Override
        public void handleGET( CoapExchange exchange )
        {
            handleRequest( exchange, ResponseCode.CONTENT, ResponseCode.BAD_REQUEST );
        }

        @Override
        public void handlePOST( CoapExchange exchange )
        {
            handleRequest( exchange, ResponseCode.CREATED, ResponseCode.BAD_REQUEST );
        }

        @Override
        public void handlePUT( CoapExchange exchange )
        {
            handleRequest( exchange, ResponseCode.CHANGED, ResponseCode.BAD_REQUEST );
        }

        @Override
        public void handleDELETE( CoapExchange exchange )
        {
            handleRequest( exchange, ResponseCode.DELETED, ResponseCode.BAD_REQUEST );
        }

        /**
         * Generic handler
         * @param exchange
         * @param okResponse response code when valid request
         * @param nokResponse response code when request not valid
         */
        private void handleRequest( CoapExchange exchange, ResponseCode okResponse, ResponseCode nokResponse )
        {
            if ( Data.validateContent( exchange.getRequestPayload(), requestPayloadSize ) )
            {
                exchange.respond( okResponse, Data.getContent( responsePayloadSize ) );
            }
            else
            {
                exchange.respond( nokResponse );
            }
        }
    }
}
