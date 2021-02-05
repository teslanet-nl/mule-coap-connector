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
package nl.teslanet.mule.connectors.coap.test.client.exceptionhandling;


import java.net.SocketException;

import nl.teslanet.shaded.org.eclipse.californium.core.CoapResource;
import nl.teslanet.shaded.org.eclipse.californium.core.CoapServer;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.CoAP;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.CoAP.ResponseCode;
import nl.teslanet.shaded.org.eclipse.californium.core.network.config.NetworkConfig;
import nl.teslanet.shaded.org.eclipse.californium.core.server.resources.CoapExchange;
import nl.teslanet.shaded.org.eclipse.californium.core.server.resources.Resource;


/**
 * Server used to test client 
 *
 */
public class ExceptionHandlingTestServer extends CoapServer
{
    /**
     * Default Constructor for test server.
     */
    public ExceptionHandlingTestServer() throws SocketException
    {
        this( CoAP.DEFAULT_COAP_PORT );
    }

    /**
     * Constructor for test server.
     */
    public ExceptionHandlingTestServer( int port ) throws SocketException
    {
        super( NetworkConfig.createStandardWithoutFile(), port );
        addResources();
    }

    private void addResources()
    {
        add( new TestResource( "service" ) );
        Resource parent= getRoot().getChild( "service" );
        parent.add( new TestResource( "get_me" ) );
        parent.add( new TestResource( "put_me" ) );
        parent.add( new TestResource( "post_me" ) );
        parent.add( new TestResource( "delete_me" ) );
    }

    /**
     * Resource that to test payloads
     */
    class TestResource extends CoapResource
    {
        /**
         * resource constructor
         */
        public TestResource( String name )
        {
            // set resource name
            super( name );

            // set display name
            getAttributes().setTitle( name );
        }

        @Override
        public void handleGET( CoapExchange exchange )
        {
            ResponseCode responseCode= ResponseCode.CONTENT;
            exchange.respond( responseCode, "Response is: " + responseCode.name() );
        }

        @Override
        public void handlePOST( CoapExchange exchange )
        {
            ResponseCode responseCode= ResponseCode.CREATED;
            exchange.respond( responseCode, "Response is: " + responseCode.name() );
        }

        @Override
        public void handlePUT( CoapExchange exchange )
        {
            ResponseCode responseCode= ResponseCode.CHANGED;
            exchange.respond( responseCode, "Response is: " + responseCode.name() );
        }

        @Override
        public void handleDELETE( CoapExchange exchange )
        {
            ResponseCode responseCode= ResponseCode.DELETED;
            exchange.respond( responseCode, "Response is: " + responseCode.name() );
        }
    }
}
