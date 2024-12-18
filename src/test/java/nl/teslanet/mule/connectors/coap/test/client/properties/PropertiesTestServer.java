/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2023 (teslanet.nl) Rogier Cobben
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

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.option.MapBasedOptionRegistry;
import org.eclipse.californium.core.coap.option.StandardOptionRegistry;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.interceptors.MessageTracer;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.elements.config.Configuration;

import nl.teslanet.mule.connectors.coap.test.utils.TestOptions;


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
    private static Configuration networkConfig= Configuration.createStandardWithoutFile();

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
        builder.setOptionRegistry(
            new MapBasedOptionRegistry(
                StandardOptionRegistry.getDefaultOptionRegistry(),
                TestOptions.OTHER_OPTION_65008,
                TestOptions.OTHER_OPTION_65009,
                TestOptions.OTHER_OPTION_65013,
                TestOptions.OTHER_OPTION_65016,
                TestOptions.OTHER_OPTION_65018,
                TestOptions.OTHER_OPTION_65020,
                TestOptions.OTHER_OPTION_65304
            )
        );
        builder.setInetSocketAddress( new InetSocketAddress( port ) );
        builder.setConfiguration( networkConfig );
        addEndpoint( builder.build() );
        getEndpoints().forEach( endpoint -> endpoint.addInterceptor( new MessageTracer() ) );
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
            proces( exchange, ResponseCode.CONTENT );
        }

        @Override
        public void handlePOST( CoapExchange exchange )
        {
            proces( exchange, ResponseCode.CHANGED );
        }

        @Override
        public void handlePUT( CoapExchange exchange )
        {
            proces( exchange, ResponseCode.CHANGED );
        }

        @Override
        public void handleDELETE( CoapExchange exchange )
        {
            proces( exchange, ResponseCode.DELETED );
        }

        void proces( CoapExchange exchange, ResponseCode okResponse )
        {
            if ( strategy.validateOption( exchange.advanced().getRequest() ) )
            {
                exchange.respond( okResponse );
            }
            else
            {
                exchange.respond( ResponseCode.BAD_GATEWAY );
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
