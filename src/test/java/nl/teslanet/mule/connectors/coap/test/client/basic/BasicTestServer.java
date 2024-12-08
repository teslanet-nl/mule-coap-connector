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
package nl.teslanet.mule.connectors.coap.test.client.basic;


import java.net.InetSocketAddress;
import java.net.SocketException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.elements.config.Configuration;


/**
 * Server used to test client 
 *
 */
public class BasicTestServer extends CoapServer
{
    /**
     * Network configuration is set to standards 
     */
    private static Configuration networkConfig= Configuration.createStandardWithoutFile();

    /**
     * Default Constructor for test server.
     */
    public BasicTestServer() throws SocketException
    {
        this( CoAP.DEFAULT_COAP_PORT );
    }

    /**
     * Constructor for test server.
     */
    public BasicTestServer( int port ) throws SocketException
    {
        super( networkConfig );
        addEndpoints( port );
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
        getRoot().getChild( "basic" ).add( new FetchResource( "fetch_me" ) );
        getRoot().getChild( "basic" ).add( new NoneResource( "do_not_fetch_me" ) );
        getRoot().getChild( "basic" ).add( new PatchResource( "patch_me" ) );
        getRoot().getChild( "basic" ).add( new NoneResource( "do_not_patch_me" ) );
        getRoot().getChild( "basic" ).add( new IpatchResource( "ipatch_me" ) );
        getRoot().getChild( "basic" ).add( new NoneResource( "do_not_ipatch_me" ) );
    }

    /**
     * Add test endpoints listening on default CoAP port.
     */
    private void addEndpoints( int port )
    {
        CoapEndpoint.Builder builder= new CoapEndpoint.Builder();
        builder.setInetSocketAddress( new InetSocketAddress( port ) );
        builder.setConfiguration( networkConfig );
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
            exchange.respond( ResponseCode.CONTENT, "GET called on: " + exchange.advanced().getRequest().getURI() );
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
            exchange.respond( ResponseCode.CREATED, "POST called on: " + exchange.advanced().getRequest().getURI() );
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
            exchange.respond( ResponseCode.CHANGED, "PUT called on: " + exchange.advanced().getRequest().getURI() );
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
            exchange.respond( ResponseCode.DELETED, "DELETE called on: " + exchange.advanced().getRequest().getURI() );
        }
    }

    /**
     * Resource that allows FETCH only
     */
    class FetchResource extends CoapResource
    {
        public FetchResource( String name )
        {
            // set resource name
            super( name );
            // set display name
            getAttributes().setTitle( name );
        }

        @Override
        public void handleFETCH( CoapExchange exchange )
        {
            // respond to the request
            exchange.respond( ResponseCode.CONTENT, "FETCH called on: " + exchange.advanced().getRequest().getURI() );
        }
    }

    /**
     * Resource that allows PATCH only
     */
    class PatchResource extends CoapResource
    {
        public PatchResource( String name )
        {
            // set resource name
            super( name );
            // set display name
            getAttributes().setTitle( name );
        }

        @Override
        public void handlePATCH( CoapExchange exchange )
        {
            // respond to the request
            exchange.respond( ResponseCode.CREATED, "PATCH called on: " + exchange.advanced().getRequest().getURI() );
        }
    }

    /**
     * Resource that allows IPATCH only
     */
    class IpatchResource extends CoapResource
    {
        public IpatchResource( String name )
        {
            // set resource name
            super( name );
            // set display name
            getAttributes().setTitle( name );
        }

        @Override
        public void handleIPATCH( CoapExchange exchange )
        {
            // respond to the request
            exchange.respond( ResponseCode.CHANGED, "IPATCH called on: " + exchange.advanced().getRequest().getURI() );
        }
    }
}
