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
package nl.teslanet.mule.connectors.coap.test.client.discovery;


import java.net.SocketException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;


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

    private void addResources()
    {
        add( new PostResource( "service" ) );
        getRoot().getChild( "service" ).add( new CtResource( "resource_with_ct" ) );
        getRoot().getChild( "service" ).add( new IfResource( "resource_with_if" ) );
        getRoot().getChild( "service" ).add( new ObsResource( "resource_with_obs" ) );
        getRoot().getChild( "service" ).add( new RtResource( "resource_with_rt" ) );
        getRoot().getChild( "service" ).add( new SzResource( "resource_with_sz" ) );
        getRoot().getChild( "service" ).add( new TitleResource( "resource_with_title" ) );
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
            getRoot().getChild( "service" ).add( new DeleteResource( exchange.getRequestText() ) );
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
            if ( getParent().delete( this ) )
            {
                exchange.respond( ResponseCode.DELETED, "DELETE called on: " + this.getURI() );
            }
            else
            {
                exchange.respond( ResponseCode.BAD_REQUEST );
            }
        }
    }

    /**
     * Resource with content types
     */
    class CtResource extends CoapResource
    {
        public CtResource( String name )
        {
            // set resource name
            super( name );
            //set ContentType
            getAttributes().addContentType( 0 );
            getAttributes().addContentType( 41 );
        }
    }

    /**
     * Resource with interface descriptions
     */
    class IfResource extends CoapResource
    {
        public IfResource( String name )
        {
            // set resource name
            super( name );
            //set interface descriptions
            getAttributes().addInterfaceDescription( "if1" );
            getAttributes().addInterfaceDescription( "if2" );
        }
    }

    //TODO add observe type option
    /**
     * Resource that is observable
     */
    class ObsResource extends CoapResource
    {
        public ObsResource( String name )
        {
            // set resource name
            super( name );
            //set observable
            setObservable( true );
            getAttributes().setObservable();
        }
    }

    /**
     * Resource with resource type
     */
    class RtResource extends CoapResource
    {
        public RtResource( String name )
        {
            // set resource name
            super( name );
            //set interface descriptions
            getAttributes().addResourceType( "rt1" );
            getAttributes().addResourceType( "rt2" );
        }
    }

    /**
     * Resource with size
     */
    class SzResource extends CoapResource
    {
        public SzResource( String name )
        {
            // set resource name
            super( name );
            //set size description
            getAttributes().setMaximumSizeEstimate( 123456 );
        }
    }

    /**
     * Resource with title
     */
    class TitleResource extends CoapResource
    {
        public TitleResource( String name )
        {
            // set resource name
            super( name );
            // set display name
            getAttributes().setTitle( "Title is " + name );
        }
    }

}
