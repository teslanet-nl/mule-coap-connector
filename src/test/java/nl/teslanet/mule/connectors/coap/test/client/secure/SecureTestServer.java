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
package nl.teslanet.mule.connectors.coap.test.client.secure;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedMultiPskStore;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier.Builder;


/**
 * Server used to test client 
 *
 */
public class SecureTestServer extends CoapServer
{
    //TODO add logging options to connector
    static
    {
        //        CaliforniumLogger.initialize();
        //        CaliforniumLogger.setLevel( Level.CONFIG );
        //        ScandiumLogger.initialize();
        //        ScandiumLogger.setLevel( Level.FINER );
    }

    // allows configuration via Californium.properties
    //public static final int DTLS_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_SECURE_PORT);

    /**
     * Set truststore password
     */
    private static final String TRUST_STORE_PASSWORD= "rootPass";

    /**
     * Set key (store) password
     */
    private final static String KEY_STORE_PASSWORD= "endPass";

    /**
     * Set key password
     */
    private final static String KEY_PASSWORD= "endPass";

    /**
     * Set key store location on classpath
     */
    private static final String KEY_STORE_LOCATION= "certs/keyStore.jks";

    /**
     * Set trust store location  on classpath
     */
    private static final String TRUST_STORE_LOCATION= "certs/trustStore.jks";

    /**
     * Default Constructor for test server.
     * @throws IOException 
     * @throws GeneralSecurityException 
     */
    public SecureTestServer() throws IOException, GeneralSecurityException
    {
        this( CoAP.DEFAULT_COAP_SECURE_PORT );
    }

    /**
     * Constructor for test server listening on non default port.
     * @throws IOException 
     * @throws GeneralSecurityException 
     */
    public SecureTestServer( int port ) throws IOException, GeneralSecurityException
    {
        super( NetworkConfig.createStandardWithoutFile() );

        // Pre-shared secrets
        AdvancedMultiPskStore pskStore= new AdvancedMultiPskStore();
        pskStore.setKey( "password", "sesame".getBytes() ); // from ETSI Plugtportest test spec

        // load the credentials
        Builder verifierBuilder= StaticNewAdvancedCertificateVerifier.builder();
        verifierBuilder.setTrustAllRPKs();
        SslContextUtil.Credentials serverCredentials= SslContextUtil.loadCredentials(
            SslContextUtil.CLASSPATH_SCHEME + KEY_STORE_LOCATION,
            "server",
            KEY_STORE_PASSWORD.toCharArray(),
            KEY_PASSWORD.toCharArray() );
        //load trust store
        Certificate[] trustedCertificates= SslContextUtil.loadTrustedCertificates(
            SslContextUtil.CLASSPATH_SCHEME + TRUST_STORE_LOCATION,
            "root",
            TRUST_STORE_PASSWORD.toCharArray() );

        verifierBuilder.setTrustedCertificates( trustedCertificates );
        DtlsConnectorConfig.Builder builder= new DtlsConnectorConfig.Builder();
        builder.setAdvancedCertificateVerifier( verifierBuilder.build() );
        builder.setAddress( new InetSocketAddress( "localhost", port ) );
        builder.setRecommendedCipherSuitesOnly( false );
        builder.setAdvancedPskStore( pskStore );
        builder.setIdentity( serverCredentials.getPrivateKey(), serverCredentials.getCertificateChain(), CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509 );
        DTLSConnector dtlsConnector= new DTLSConnector( builder.build() );
        CoapEndpoint.Builder endpointBuilder= new CoapEndpoint.Builder();
        //endpointBuilder.setNetworkConfig( visitor.getNetworkConfig() );
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
