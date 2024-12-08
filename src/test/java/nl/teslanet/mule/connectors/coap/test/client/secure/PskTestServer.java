/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2023 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.test.client.secure;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedMultiPskStore;


/**
 * Server used to test client 
 *
 */
public class PskTestServer extends CoapServer
{
    /**
     * Default Constructor for test server.
     * @throws IOException 
     * @throws GeneralSecurityException 
     */
    public PskTestServer() throws IOException, GeneralSecurityException
    {
        this( CoAP.DEFAULT_COAP_SECURE_PORT );
    }

    /**
     * Constructor for test server listening on non default port.
     * @throws IOException 
     * @throws GeneralSecurityException 
     */
    public PskTestServer( int port ) throws IOException, GeneralSecurityException
    {
        super( Configuration.createStandardWithoutFile() );

        // Pre-shared secrets
        AdvancedMultiPskStore pskStore= new AdvancedMultiPskStore();
        pskStore.setKey( "password", "sesame".getBytes() ); // from ETSI Plugtportest test spec
        
        DtlsConnectorConfig.Builder builder= new DtlsConnectorConfig.Builder( Configuration.createStandardWithoutFile() );
        builder.setAddress( new InetSocketAddress( "localhost", port ) );
        builder.setAdvancedPskStore( pskStore );
        DTLSConnector dtlsConnector= new DTLSConnector( builder.build() );
        CoapEndpoint.Builder endpointBuilder= new CoapEndpoint.Builder();
        endpointBuilder.setConnector( dtlsConnector );

        addEndpoint( endpointBuilder.build() );
        addResources();
    }

    /**
     * Add server resources
     */
    private void addResources()
    {
        // provide an instance of a Hello-World resource
        add( new GetResource( "secure" ) );
        getRoot().getChild( "secure" ).add( new GetResource( "get_me" ) );
        getRoot().getChild( "secure" ).add( new NoneResource( "do_not_get_me" ) );
        getRoot().getChild( "secure" ).add( new PutResource( "put_me" ) );
        getRoot().getChild( "secure" ).add( new NoneResource( "do_not_put_me" ) );
        getRoot().getChild( "secure" ).add( new PostResource( "post_me" ) );
        getRoot().getChild( "secure" ).add( new NoneResource( "do_not_post_me" ) );
        getRoot().getChild( "secure" ).add( new DeleteResource( "delete_me" ) );
        getRoot().getChild( "secure" ).add( new NoneResource( "do_not_delete_me" ) );
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
