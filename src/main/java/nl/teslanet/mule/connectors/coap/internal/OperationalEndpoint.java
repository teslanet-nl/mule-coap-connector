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
package nl.teslanet.mule.connectors.coap.internal;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.interceptors.MessageTracer;
import org.eclipse.californium.elements.UdpMulticastConnector;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedMultiPskStore;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier.Builder;

import nl.teslanet.mule.connectors.coap.api.config.SocketParams;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.DTLSEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.Endpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.MulticastUDPEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPClientEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPServerEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TLSClientEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TLSServerEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.UDPEndpoint;
import nl.teslanet.mule.connectors.coap.internal.client.Client;
import nl.teslanet.mule.connectors.coap.internal.config.CfNetworkConfigVisitor;
import nl.teslanet.mule.connectors.coap.internal.config.MulticastVisitor;
import nl.teslanet.mule.connectors.coap.internal.exceptions.EndpointConstructionException;
import nl.teslanet.mule.connectors.coap.internal.server.Server;
import nl.teslanet.mule.connectors.coap.internal.utils.MuleInputStreamFactory;


/**
 * Operational endpoint represents the endpoint used by CoAP clients or servers
 *
 */
public final class OperationalEndpoint
{
    /**
     * registry of operational endpoints
     */
    static final Map< String, OperationalEndpoint > registry= Collections.synchronizedMap( new HashMap< String, OperationalEndpoint >() );

    /**
     * COnfigured name of the endpoint
     */
    private String configName= null;

    /**
     * The underlying coap endpoint
     */
    private CoapEndpoint coapEndpoint= null;

    /**
     * The server using the endpoint
     */
    private Server server= null;

    /**
     * The clients using the endpoint
     */
    private HashSet< Client > clients= new HashSet< Client >();

    /**
     * Create an endpoint not attached to a server or return existing when already created
     * @param config the configuration for the endpoint
     * @return the endpoint created from the configuration
     * @throws Exception
     */
    private static OperationalEndpoint create( Endpoint config ) throws Exception
    {
        OperationalEndpoint operationalEndpoint;
        //check sub-class before superclass
        if ( DTLSEndpoint.class.isInstance( config ) )
        {
            operationalEndpoint= new OperationalEndpoint( (DTLSEndpoint) config );
        }
        else if ( MulticastUDPEndpoint.class.isInstance( config ) )
        {
            operationalEndpoint= new OperationalEndpoint( (MulticastUDPEndpoint) config );
        }
        else if ( UDPEndpoint.class.isInstance( config ) )
        {
            operationalEndpoint= new OperationalEndpoint( (UDPEndpoint) config );
        }
        else if ( TLSServerEndpoint.class.isInstance( config ) )
        {
            operationalEndpoint= new OperationalEndpoint( (TLSServerEndpoint) config );
        }
        else if ( TLSClientEndpoint.class.isInstance( config ) )
        {
            operationalEndpoint= new OperationalEndpoint( (TLSClientEndpoint) config );
        }
        else if ( TCPServerEndpoint.class.isInstance( config ) )
        {
            operationalEndpoint= new OperationalEndpoint( (TCPServerEndpoint) config );
        }
        else if ( TCPClientEndpoint.class.isInstance( config ) )
        {
            operationalEndpoint= new OperationalEndpoint( (TCPClientEndpoint) config );
        }
        else
        {
            //TODO make specific exception
            throw new Exception( "endpoint has unknown type: " + config );
        }
        if ( config.logCoapMessages )
        {
            operationalEndpoint.coapEndpoint.addInterceptor( new MessageTracer() );
        }
        return operationalEndpoint;
    }

    /**
     * Create an endpoint attached to a server or return existing when already created
     * @param server the server attached to the endpint
     * @param config the endpoint configuration
     * @return the operational endpoint
     * @throws Exception when endpoint is of an unknown type
     */
    public synchronized static OperationalEndpoint getOrCreate( Server server, Endpoint config ) throws Exception
    {
        OperationalEndpoint operationalEndpoint= null;

        if ( registry.containsKey( config.configName ) )
        {
            // endpoint already created
            operationalEndpoint= registry.get( config.configName );
            if ( server != null && operationalEndpoint.server != null )
            {
                //multiple server usage of the endpoint is not allowed
                if ( server != operationalEndpoint.server )
                {
                    //TODO make exception specific
                    throw new Exception( "Using endpoint '" + config.configName + "' in multiple servers not allowed" );
                }
            }
            operationalEndpoint.server= server;
            return operationalEndpoint;
        }
        // need to create endpoint
        operationalEndpoint= create( config );
        operationalEndpoint.server= server;
        registry.put( operationalEndpoint.getConfigName(), operationalEndpoint );
        return operationalEndpoint;
    }

    //TODO make exception specific
    /**
     * Create an endpoint or return existing when already created
     * @param client The client using this endpoint.
     * @param config The endpoint configuration.
     * @return OperationalEndpoint instance that applies to the endpoint configuration.
     * @throws Exception When an error occurs.
     */
    public synchronized static OperationalEndpoint getOrCreate( Client client, Endpoint config ) throws Exception
    {
        OperationalEndpoint operationalEndpoint= null;
        if ( client == null )
        {
            //TODO make exception specific
            throw new Exception( "Using endpoint '" + config.configName + "': client missing" );
        }
        if ( registry.containsKey( config.configName ) )
        {
            // endpoint already created
            operationalEndpoint= registry.get( config.configName );
            operationalEndpoint.clients.add( client );
            return operationalEndpoint;
        }
        // need to create endpoint
        operationalEndpoint= create( config );
        operationalEndpoint.clients.add( client );
        registry.put( operationalEndpoint.getConfigName(), operationalEndpoint );
        return operationalEndpoint;
    }

    /**
     * Find endpoints that a server is attached to.
     * @param server The server instance.
     * @return List of OperationalEndpoint keys that the server is attached to.
     */
    public static List< String > find( Server server )
    {
        ArrayList< String > found= new ArrayList< String >();
        for ( Entry< String, OperationalEndpoint > entry : registry.entrySet() )
        {
            if ( entry.getValue().server == server )
            {
                found.add( entry.getKey() );
            }
        }
        return found;
    }

    /**
     * Find endpoints that a client is attached to.
     * @param client The client instance.
     * @return List of OperationalEndpoint keys that the client is attached to.
     */
    public static List< String > find( Client client )
    {
        ArrayList< String > found= new ArrayList< String >();
        for ( Entry< String, OperationalEndpoint > entry : registry.entrySet() )
        {
            if ( entry.getValue().clients.contains( client ) )
            {
                found.add( entry.getKey() );
            }
        }
        return found;
    }

    /**
     * Dispose of all endpoints used by server.
     * The endpoint will only be destroyed when not in use by any client
     * @param server The server instance.
     */
    public synchronized static void disposeAll( Server server )
    {
        List< String > names= find( server );
        for ( String endpointName : names )
        {
            OperationalEndpoint endpoint= registry.get( endpointName );
            //TODO check for client usage
            if ( endpoint != null )
            {
                endpoint.server= null;
                if ( endpoint.clients.isEmpty() && endpoint.server == null )
                {
                    registry.remove( endpointName );
                    //TODO review
                    endpoint.coapEndpoint.destroy();
                }
            }
        }
        freeEndpointResoures();
    }

    /**
     * Dispose of all endpoints used by client.
     * De endpoint is only destroyed when not in use by any client
     * @param client the client instance.
     */
    public synchronized static void disposeAll( Client client )
    {
        List< String > names= find( client );
        for ( String endpointName : names )
        {
            OperationalEndpoint endpoint= registry.get( endpointName );

            //TODO check for client usage
            if ( endpoint != null )
            {
                endpoint.clients.remove( client );
                if ( endpoint.clients.isEmpty() && endpoint.server == null )
                {
                    registry.remove( endpointName );
                    //TODO review
                    endpoint.coapEndpoint.destroy();
                }
            }
        }
        freeEndpointResoures();
    }

    /**
     * Frees resources that are used by endpoints when possible.
     */
    private static void freeEndpointResoures()
    {
        if ( registry.isEmpty())
        {
            //Schedulers are not needed any more, so stop them
            CoAPConnector.stopIoScheduler();
            CoAPConnector.stopLightScheduler();
        }
    }

    /**
     * Get the name of this endpoint.
     * @return the configured endpoint name
     */
    private String getConfigName()
    {
        return configName;
    }

    /**
     * @return the coapEndpoint
     */
    public CoapEndpoint getCoapEndpoint()
    {
        return coapEndpoint;
    }

    /**
     * Constructor for an operational UDP endpoint.
     * @param config the UDP endpoint configuration
     */
    private OperationalEndpoint( UDPEndpoint config )
    {
        CfNetworkConfigVisitor visitor= new CfNetworkConfigVisitor();
        config.accept( visitor );
        this.configName= config.configName;
        CoapEndpoint.Builder builder= new CoapEndpoint.Builder();
        InetSocketAddress localAddress= getInetSocketAddress( config.socketParams, false );
        builder.setInetSocketAddress( localAddress );
        builder.setNetworkConfig( visitor.getNetworkConfig() );
        this.coapEndpoint= builder.build();
        this.coapEndpoint.setExecutors( CoAPConnector.getIoScheduler(), CoAPConnector.getLightScheduler() );
    }

    //TODO use connector builder, rename interfaceAddress param
    /**
     * Constructor for an operational UDP endpoint.
     * @param config the UDP endpoint configuration
     */
    private OperationalEndpoint( MulticastUDPEndpoint config )
    {
        CfNetworkConfigVisitor visitor= new CfNetworkConfigVisitor();
        config.accept( visitor );
        this.configName= config.configName;
        CoapEndpoint.Builder builder= new CoapEndpoint.Builder();
        builder.setNetworkConfig( visitor.getNetworkConfig() );
        MulticastVisitor multicastVisitor= new MulticastVisitor();
        config.accept( multicastVisitor );
        UdpMulticastConnector.Builder connectorBuilder= new UdpMulticastConnector.Builder();
        //TODO add feature to add interface per multicast-group
        NetworkInterface networkInterface= multicastVisitor.getInterfaceAddress();
        for ( InetAddress group : multicastVisitor.getMulticastGroups())
        {
            connectorBuilder.addMulticastGroup( group, networkInterface );
        }
        //TODO move to visitor
        connectorBuilder.setLocalAddress( getInetSocketAddress( config.socketParams, false ) );
        //TODO add outgoing params
        builder.setConnector( connectorBuilder.build() );
        this.coapEndpoint= builder.build();
        this.coapEndpoint.setExecutors( CoAPConnector.getIoScheduler(), CoAPConnector.getLightScheduler() );
    }

    /**
     * Constructor for an operational DTLS endpoint.
     * @param config the UDP endpoint configuration
     * @throws EndpointConstructionException 
     */
    private OperationalEndpoint( DTLSEndpoint config ) throws EndpointConstructionException
    {
        MuleInputStreamFactory streamFactory= new MuleInputStreamFactory();
        SslContextUtil.configure( streamFactory.getScheme(), streamFactory );
        // Pre-shared secrets
        //TODO improve security (-> not in memory ) 
        AdvancedMultiPskStore pskStore= new AdvancedMultiPskStore();
        Builder verifierBuilder= StaticNewAdvancedCertificateVerifier.builder();
        verifierBuilder.setTrustAllRPKs();
        try
        {
            // load the key store
            SslContextUtil.Credentials serverCredentials= SslContextUtil.loadCredentials(
                streamFactory.getScheme() + config.encryptionParams.keyStoreLocation,
                config.encryptionParams.privateKeyAlias,
                ( config.encryptionParams.keyStorePassword != null ? config.encryptionParams.keyStorePassword.toCharArray() : null ),
                ( config.encryptionParams.privateKeyPassword != null ? config.encryptionParams.privateKeyPassword.toCharArray() : null ) );
            //load trust store
            Certificate[] trustedCertificates= SslContextUtil.loadTrustedCertificates(
                streamFactory.getScheme() + config.encryptionParams.trustStoreLocation,
                config.encryptionParams.trustedRootCertificateAlias,
                ( config.encryptionParams.trustStorePassword != null ? config.encryptionParams.trustStorePassword.toCharArray() : null ) );

            verifierBuilder.setTrustedCertificates( trustedCertificates );
            DtlsConnectorConfig.Builder builder= new DtlsConnectorConfig.Builder();
            builder.setAddress( getInetSocketAddress( config.socketParams, true ) );
            builder.setAdvancedPskStore( pskStore );
            builder.setIdentity( serverCredentials.getPrivateKey(), serverCredentials.getCertificateChain(), CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509 );
            builder.setAdvancedCertificateVerifier( verifierBuilder.build() );
            DTLSConnector dtlsConnector= new DTLSConnector( builder.build() );
            CoapEndpoint.Builder endpointBuilder= new CoapEndpoint.Builder();
            CfNetworkConfigVisitor visitor= new CfNetworkConfigVisitor();
            config.accept( visitor );
            endpointBuilder.setNetworkConfig( visitor.getNetworkConfig() );
            endpointBuilder.setConnector( dtlsConnector );
            this.coapEndpoint= endpointBuilder.build();
            this.coapEndpoint.setExecutors( CoAPConnector.getIoScheduler(), CoAPConnector.getLightScheduler() );

        }
        catch ( Exception e )
        {
            throw new EndpointConstructionException( "cannot construct secure endpoint", e );
        }
    }

    private OperationalEndpoint( TCPServerEndpoint config )
    {
        // TODO Auto-generated constructor stub
    }

    private OperationalEndpoint( TCPClientEndpoint config )
    {
        // TODO Auto-generated constructor stub
    }

    private OperationalEndpoint( TLSServerEndpoint config )
    {
        // TODO Auto-generated constructor stub
    }

    private OperationalEndpoint( TLSClientEndpoint config )
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * Get socket address from the socket configuration. 
     * @param socketParams The socket configuration.
     * @param isSsecure Indication whether coap ({@code False}) or coaps ({@code True}) is used. 
     * @return The InetSocketAddress that conforms to the configuration.
     */
    public InetSocketAddress getInetSocketAddress( SocketParams socketParams, boolean isSsecure )
    {
        int defaultPort= ( isSsecure ? CoAP.DEFAULT_COAP_SECURE_PORT : CoAP.DEFAULT_COAP_PORT );
        int port= ( socketParams != null && socketParams.bindToPort != null ? socketParams.bindToPort : defaultPort );

        if ( socketParams != null && socketParams.bindToHost != null )
        {
            return new InetSocketAddress( socketParams.bindToHost, port );
        }
        else
        {
            return new InetSocketAddress( port );
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {

        return configName + " ( " + coapEndpoint + " )";
    }

}
