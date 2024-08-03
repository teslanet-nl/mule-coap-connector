/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2024 (teslanet.nl) Rogier Cobben
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
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.util.Set;

import javax.crypto.SecretKey;

import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedMultiPskStore;
import org.eclipse.californium.scandium.dtls.pskstore.MultiPskFileStore;
import org.eclipse.californium.scandium.dtls.x509.CertificateProvider;
import org.eclipse.californium.scandium.dtls.x509.SingleCertificateProvider;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;
import org.eclipse.californium.scandium.util.SecretUtil;

import nl.teslanet.mule.connectors.coap.api.config.ConfigException;
import nl.teslanet.mule.connectors.coap.api.config.security.KeyStore;
import nl.teslanet.mule.connectors.coap.api.config.security.PreSharedKey;
import nl.teslanet.mule.connectors.coap.api.config.security.PreSharedKeyGroup;
import nl.teslanet.mule.connectors.coap.api.config.security.PreSharedKeyStore;
import nl.teslanet.mule.connectors.coap.api.config.security.TrustStore;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.EndpointConstructionException;
import nl.teslanet.mule.connectors.coap.internal.utils.MuleInputStreamFactory;


/**
 * Configuration visitor that collects multi-cast UDP Endpoint configuration
 *
 */
public class DtlsEndpointConfigVisitor extends EndpointConfigVisitor
{
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
     * Pre-shared-file is configured.
     */
    private boolean preSharedKeyFileAvailable= false;

    /**
     * Pre-shared-file location.
     */
    private String preSharedKeyStoreLocation= null;

    /**
     * Pre-shared-file password.
     */
    private String preSharedKeyStorePassword= null;

    /**
     * Visit SecurityParams is NOOP.
     */

    /**
     * Visit PreSharedKeyGroup parameters.
     * @param toVisit The object to visit.
     * @throws ConfigException When visit is not successful.
     */
    @Override
    public void visit( PreSharedKeyGroup toVisit ) throws ConfigException
    {
        super.visit( toVisit );
        preSharedKeys= toVisit.preSharedKeys;
    }

    /**
     * Visit pre-shared key file parameters.
     * @param toVisit The object to visit.
     * @throws ConfigException When visit is not successful.
     */
    @Override
    public void visit( PreSharedKeyStore toVisit ) throws ConfigException
    {
        super.visit( toVisit );
        preSharedKeyFileAvailable= true;
        preSharedKeyStoreLocation= toVisit.path;
        if ( toVisit.password != null ) preSharedKeyStorePassword= toVisit.password;
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
     * Build the endpoint using the configuration this visitor has collected.
     * @return The Endpoint.
     * @throws EndpointConstructionException When the configuration cannot be used to create an endpoint.
     */
    @Override
    public CoapEndpoint getEndpoint() throws EndpointConstructionException
    {
        MuleInputStreamFactory streamFactory= new MuleInputStreamFactory();
        DtlsConnectorConfig.Builder connectBuilder= new DtlsConnectorConfig.Builder( getConfiguration() );
        connectBuilder.setAddress( getLocalAddress() );
        connectBuilder.setReuseAddress( isReuseAddress() );
        // Pre-shared secrets
        if ( preSharedKeys != null )
        {
            AdvancedMultiPskStore pskStore= new AdvancedMultiPskStore();
            try
            {
                for ( PreSharedKey key : preSharedKeys )
                {
                    if ( key.getHost() == null )
                    {
                        pskStore.setKey( key.getIdentity(), key.getKey().getByteArray() );
                    }
                    else if ( key.getVirtualHost() != null )
                    {
                        InetSocketAddress address= new InetSocketAddress( key.getHost(), key.getPort() );
                        pskStore.addKnownPeer( address, key.getVirtualHost(), key.getIdentity(), key.getKey().getByteArray() );
                    }
                    else
                    {
                        InetSocketAddress address= new InetSocketAddress( key.getHost(), key.getPort() );
                        pskStore.addKnownPeer( address, key.getIdentity(), key.getKey().getByteArray() );
                    }
                }
            }
            catch ( OptionValueException e )
            {
                throw new EndpointConstructionException( String.format( "DTLS Endpoint { %s } preshared key host error.", getEndpointName() ), e );
            }
            connectBuilder.setAdvancedPskStore( pskStore );
        }
        else if ( preSharedKeyFileAvailable )
        {
            MultiPskFileStore pskFile= new MultiPskFileStore();
            InputStream pskStream;
            try
            {
                pskStream= streamFactory.create( streamFactory.getScheme() + preSharedKeyStoreLocation );
            }
            catch ( IOException e )
            {
                throw new EndpointConstructionException(
                    String.format( "DTLS Endpoint { %s } preshared key file { %s } error.", getEndpointName(), preSharedKeyStoreLocation ),
                    e
                );
            }
            if ( preSharedKeyStorePassword != null )
            {
                SecretKey secretKey= SecretUtil.create( preSharedKeyStorePassword.getBytes(), "PASSWORD" );
                pskFile.loadPskCredentials( pskStream, secretKey );
            }
            else
            {
                pskFile.loadPskCredentials( pskStream );
            }
            connectBuilder.setAdvancedPskStore( pskFile );
        }
        SslContextUtil.configure( streamFactory.getScheme(), streamFactory );
        if ( keyStoreAvailable )
        {
            // load the key store
            SslContextUtil.Credentials serverCredentials;

            try
            {
                serverCredentials= SslContextUtil.loadCredentials( streamFactory.getScheme() + keyStoreLocation, privateKeyAlias, keyStorePassword, privateKeyPassword );
                CertificateProvider identityProvider= new SingleCertificateProvider( serverCredentials.getPrivateKey(), serverCredentials.getPublicKey() );
                connectBuilder.setCertificateIdentityProvider( identityProvider );
            }
            catch ( IOException | GeneralSecurityException e )
            {
                throw new EndpointConstructionException( String.format( "DTLS Endpoint { %s } keystore error.", getEndpointName() ), e );
            }
        }
        if ( trustStoreAvailable )
        {
            //load trust store
            try
            {
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
            catch ( IOException | GeneralSecurityException e )
            {
                throw new EndpointConstructionException( String.format( "DTLS Endpoint { %s } truststore error.", getEndpointName() ), e );
            }
        }
        DTLSConnector dtlsConnector= new DTLSConnector( connectBuilder.build() );
        CoapEndpoint.Builder endpointBuilder= new CoapEndpoint.Builder();
        endpointBuilder.setConfiguration( getConfiguration() );
        endpointBuilder.setConnector( dtlsConnector );
        return endpointBuilder.build();
    }
}
