/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2020 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.test.client.properties;


import java.net.InetSocketAddress;
import java.net.SocketException;

import nl.teslanet.shaded.org.eclipse.californium.core.CoapResource;
import nl.teslanet.shaded.org.eclipse.californium.core.CoapServer;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.CoAP;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.CoAP.ResponseCode;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.Response;
import nl.teslanet.shaded.org.eclipse.californium.core.network.CoapEndpoint;
import nl.teslanet.shaded.org.eclipse.californium.core.network.config.NetworkConfig;
import nl.teslanet.shaded.org.eclipse.californium.core.server.resources.CoapExchange;
import nl.teslanet.shaded.org.eclipse.californium.core.server.resources.Resource;


/**
 * Server used to test observing client 
 *
 */
public class PropertiesTestServer extends CoapServer
{
    OptionStrategy strategy;

    /**
     * Network configuration is set to standards 
     */
    private static NetworkConfig networkConfig= NetworkConfig.createStandardWithoutFile();

    /**
     * Default Constructor for test server.
     */
    public PropertiesTestServer( OptionStrategy strategy ) throws SocketException
    {
        this( strategy, CoAP.DEFAULT_COAP_PORT );
    }

    /**
     * Constructor for test server.
     */
    public PropertiesTestServer( OptionStrategy strategy, int port ) throws SocketException
    {
        super( networkConfig );
        addEndpoints( port );
        addResources();
        this.strategy= strategy;
    }

    private void addResources()
    {
        // provide an instance of an observable resource
        add( new ValidateOptionResource( "property" ) );
        Resource parent= getRoot().getChild( "property" );
        parent.add( new ValidateOptionResource( "validate" ) );
        parent.add( new SetOptionResource( "setoption" ) );
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
     * Resource that validates inbound options
     */
    class ValidateOptionResource extends CoapResource
    {
        /**
         * Constructor
         * @param name of the resource
         */
        public ValidateOptionResource( String name )
        {
            // set resource name
            super( name );

            // set display name
            getAttributes().setTitle( name );
        }

        @Override
        public void handleGET( CoapExchange exchange )
        {
            if ( strategy.validateOption( exchange.advanced().getRequest() ) )
            {
                exchange.respond( ResponseCode.CONTENT );
            }
            else
            {
                exchange.respond( ResponseCode.BAD_OPTION );
            }
        }

        @Override
        public void handlePOST( CoapExchange exchange )
        {
            if ( strategy.validateOption( exchange.advanced().getRequest() ) )
            {
                exchange.respond( ResponseCode.CHANGED );
            }
            else
            {
                exchange.respond( ResponseCode.BAD_OPTION );
            }
        }

        @Override
        public void handlePUT( CoapExchange exchange )
        {
            if ( strategy.validateOption( exchange.advanced().getRequest() ) )
            {
                exchange.respond( ResponseCode.CHANGED );
            }
            else
            {
                exchange.respond( ResponseCode.BAD_OPTION );
            }
        }

        @Override
        public void handleDELETE( CoapExchange exchange )
        {
            if ( strategy.validateOption( exchange.advanced().getRequest() ) )
            {
                exchange.respond( ResponseCode.DELETED );
            }
            else
            {
                exchange.respond( ResponseCode.BAD_OPTION );
            }
        }
    }

    /**
     * Resource that sets outbound options
     */
    class SetOptionResource extends CoapResource
    {
        /**
         * Constructor
         * @param name of the resource
         */
        public SetOptionResource( String name )
        {
            // set resource name
            super( name );

            // set display name
            getAttributes().setTitle( name );
        }

        @Override
        public void handleGET( CoapExchange exchange )
        {
            Response response= new Response( ResponseCode.CONTENT );
            strategy.setOption( response );
            exchange.respond( response );
        }

        @Override
        public void handlePOST( CoapExchange exchange )
        {
            Response response= new Response( ResponseCode.CHANGED );
            strategy.setOption( response );
            exchange.respond( response );
        }

        @Override
        public void handlePUT( CoapExchange exchange )
        {
            Response response= new Response( ResponseCode.CHANGED );
            strategy.setOption( response );
            exchange.respond( response );
        }

        @Override
        public void handleDELETE( CoapExchange exchange )
        {
            Response response= new Response( ResponseCode.DELETED );
            strategy.setOption( response );
            exchange.respond( response );
        }
    }
}
