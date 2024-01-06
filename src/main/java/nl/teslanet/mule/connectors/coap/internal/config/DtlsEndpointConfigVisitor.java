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
package nl.teslanet.mule.connectors.coap.internal.config;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.util.Set;

import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedMultiPskStore;
import org.eclipse.californium.scandium.dtls.x509.CertificateProvider;
import org.eclipse.californium.scandium.dtls.x509.SingleCertificateProvider;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;

import nl.teslanet.mule.connectors.coap.api.config.ConfigException;
import nl.teslanet.mule.connectors.coap.api.config.SocketParams;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.security.KeyStore;
import nl.teslanet.mule.connectors.coap.api.config.security.PreSharedKey;
import nl.teslanet.mule.connectors.coap.api.config.security.SecurityParams;
import nl.teslanet.mule.connectors.coap.api.config.security.TrustStore;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;
import nl.teslanet.mule.connectors.coap.internal.utils.MuleInputStreamFactory;


/**
 * Configuration visitor that collects multi-cast UDP Endpoint configuration
 *
 */
public class DtlsEndpointConfigVisitor extends ConfigurationVisitor
{
    /**
     * The name of the endpont 
     */
    private String endpointName;

    /**
     * Network interface to use for multicast.
     */
    private InetSocketAddress localAddress= null;

    /**
     * The pre-shared keys, if any.
     */
    private Set< PreSharedKey > preSharedKeys= null;

    /**
     * Key store is configured.
     */
    private boolean keyStoreAvailable= false;

    /**
     * The key store location.
     */
    private String keyStoreLocation= null;

    /**
     * Private key alias.
     */
    private String privateKeyAlias= null;

    /**
     * Trust store is configured.
     */
    private boolean trustStoreAvailable= false;

    /**
     * Trusted root certificate Alias.
     */
    private String trustedRootCertificateAlias= null;

    /**
     * The trust store location.
     */
    private String trustStoreLocation= null;

    /**
     * The key store password.
     */
    private char[] keyStorePassword= null;

    /**
     * Private key password.
     */
    private char[] privateKeyPassword= null;

    /**
     * Trust store password.
     */
    private char[] trustStorePassword= null;

    /**
     * Visit Endpoint configuration object.
     * @param toVisit the object to visit.
     * @throws ConfigException When visit is not successful.
     */
    @Override
    public void visit( AbstractEndpoint toVisit ) throws ConfigException
    {
        super.visit( toVisit );
        endpointName= toVisit.configName;
    }

    /**
     * Visit socket parameters.
     * @param toVisit The object to visit.
     */
    @Override
    public void visit( SocketParams toVisit )
    {
        super.visit( toVisit );
        int port= ( toVisit.bindToPort != null ? toVisit.bindToPort : 0 );

        if ( toVisit.bindToHost != null )
        {
            localAddress= new InetSocketAddress( toVisit.bindToHost, port );
        }
        else
        {
            localAddress= new InetSocketAddress( port );
        }
    }

    /**
     * Visit security parameters.
     * @param toVisit The object to visit.
     * @throws ConfigException When visit is not successful.
     */
    @Override
    public void visit( SecurityParams toVisit ) throws ConfigException
    {
        super.visit( toVisit );
        preSharedKeys= toVisit.preSharedKeys;
    }

    /**
     * Visit key store parameters.
     * @param toVisit The object to visit.
     * @throws ConfigException When visit is not successful.
     */
    @Override
    public void visit( KeyStore toVisit ) throws ConfigException
    {
        super.visit( toVisit );
        keyStoreAvailable= true;
        keyStoreLocation= toVisit.path;
        privateKeyAlias= toVisit.privateKeyAlias;
        if ( toVisit.password != null ) keyStorePassword= toVisit.password.toCharArray();
        if ( toVisit.privateKeyPassword != null ) privateKeyPassword= toVisit.privateKeyPassword.toCharArray();
    }

    /**
     * Visit trust store parameters.
     * @param toVisit The object to visit.
     * @throws ConfigException When visit is not successful.
     */
    @Override
    public void visit( TrustStore toVisit ) throws ConfigException
    {
        super.visit( toVisit );
        trustStoreAvailable= true;
        trustStoreLocation= toVisit.path;
        trustedRootCertificateAlias= toVisit.rootCertificateAlias;
        if ( toVisit.password != null ) trustStorePassword= toVisit.password.toCharArray();
    }

    /**
     * @return The configured endpoint name.
     */
    public String getEndpointName()
    {
        return endpointName;
    }

    /**
     * Get the Builder that is ready to build the endpoint.
     * @return The Endpoint Builder.
     * @throws GeneralSecurityException 
     * @throws IOException 
     * @throws OptionValueException 
     */
    public CoapEndpoint.Builder getEndpointBuilder() throws IOException, GeneralSecurityException, OptionValueException
    {
        DtlsConnectorConfig.Builder connectBuilder= new DtlsConnectorConfig.Builder( getConfiguration() );
        connectBuilder.setAddress( localAddress );
        // Pre-shared secrets
        if ( preSharedKeys != null )
        {
            //TODO cf3 support for virtual host
            AdvancedMultiPskStore pskStore= new AdvancedMultiPskStore();
            for ( PreSharedKey key : preSharedKeys )
            {
                if ( key.getHost() == null )
                {
                    pskStore.setKey( key.getIdentity(), key.getKey().getByteArray() );
                }
                else
                {
                    InetSocketAddress address= new InetSocketAddress( key.getHost(), key.getPort() );
                    pskStore.addKnownPeer( address, key.getIdentity(), key.getKey().getByteArray() );
                }
            }
            connectBuilder.setAdvancedPskStore( pskStore );
        }
        MuleInputStreamFactory streamFactory= new MuleInputStreamFactory();
        SslContextUtil.configure( streamFactory.getScheme(), streamFactory );
        if ( keyStoreAvailable )
        {
            // load the key store
            SslContextUtil.Credentials serverCredentials= SslContextUtil.loadCredentials(
                streamFactory.getScheme() + keyStoreLocation,
                privateKeyAlias,
                keyStorePassword,
                privateKeyPassword
            );
            CertificateProvider identityProvider= new SingleCertificateProvider( serverCredentials.getPrivateKey(), serverCredentials.getPublicKey() );
            connectBuilder.setCertificateIdentityProvider( identityProvider );
        }
        if ( trustStoreAvailable )
        {
            //load trust store
            Certificate[] trustedCertificates= SslContextUtil.loadTrustedCertificates(
                streamFactory.getScheme() + trustStoreLocation,
                trustedRootCertificateAlias,
                trustStorePassword
            );
            StaticNewAdvancedCertificateVerifier.Builder verifierBuilder= StaticNewAdvancedCertificateVerifier.builder();
            verifierBuilder.setTrustAllRPKs();
            verifierBuilder.setTrustedCertificates( trustedCertificates );
            connectBuilder.setAdvancedCertificateVerifier( verifierBuilder.build() );
        }
        DTLSConnector dtlsConnector= new DTLSConnector( connectBuilder.build() );
        CoapEndpoint.Builder endpointBuilder= new CoapEndpoint.Builder();
        endpointBuilder.setConfiguration( getConfiguration() );
        endpointBuilder.setConnector( dtlsConnector );
        return endpointBuilder;
    }
}
