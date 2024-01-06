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
package nl.teslanet.mule.connectors.coap.internal.endpoint;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.interceptors.MessageTracer;

import nl.teslanet.mule.connectors.coap.api.config.ConfigException;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.DTLSEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.MulticastUDPEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPClientEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPServerEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TLSClientEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TLSServerEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.UDPEndpoint;
import nl.teslanet.mule.connectors.coap.internal.CoapConnector;
import nl.teslanet.mule.connectors.coap.internal.client.Client;
import nl.teslanet.mule.connectors.coap.internal.config.DtlsEndpointConfigVisitor;
import nl.teslanet.mule.connectors.coap.internal.config.EndpointConfigVisitor;
import nl.teslanet.mule.connectors.coap.internal.config.MulticastUdpEndpointConfigVisitor;
import nl.teslanet.mule.connectors.coap.internal.exceptions.EndpointConstructionException;
import nl.teslanet.mule.connectors.coap.internal.server.Server;


/**
 * Operational endpoint represents the endpoint used by CoAP clients or servers
 *
 */
public final class OperationalEndpoint
{
    /**
     * Error message prefix
     */
    private static final String ENDPOINT_MSG_PREFIX= "CoAP Endpoint { ";

    /**
     * registry of operational endpoints
     */
    private static final Map< String, OperationalEndpoint > registry= Collections.synchronizedMap( new HashMap<>() );

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
    private HashSet< Client > clients= new HashSet<>();

    /**
     * Create an endpoint not attached to a server or return existing when already created
     * @param config the configuration for the endpoint
     * @return the endpoint created from the configuration
     * @throws EndpointConstructionException 
     * @throws Exception
     */
    private static OperationalEndpoint create( AbstractEndpoint config ) throws EndpointConstructionException
    {
        OperationalEndpoint operationalEndpoint;
        //check sub-class before superclass
        if ( config instanceof DTLSEndpoint )
        {
            operationalEndpoint= new OperationalEndpoint( (DTLSEndpoint) config );
        }
        else if ( config instanceof MulticastUDPEndpoint )
        {
            operationalEndpoint= new OperationalEndpoint( (MulticastUDPEndpoint) config );
        }
        else if ( config instanceof UDPEndpoint )
        {
            operationalEndpoint= new OperationalEndpoint( (UDPEndpoint) config );
        }
        else if ( config instanceof TLSServerEndpoint )
        {
            operationalEndpoint= new OperationalEndpoint( (TLSServerEndpoint) config );
        }
        else if ( config instanceof TLSClientEndpoint )
        {
            operationalEndpoint= new OperationalEndpoint( (TLSClientEndpoint) config );
        }
        else if ( config instanceof TCPServerEndpoint )
        {
            operationalEndpoint= new OperationalEndpoint( (TCPServerEndpoint) config );
        }
        else if ( config instanceof TCPClientEndpoint )
        {
            operationalEndpoint= new OperationalEndpoint( (TCPClientEndpoint) config );
        }
        else
        {
            throw new EndpointConstructionException( ENDPOINT_MSG_PREFIX + config.configName + " }: has unknown type { " + config.getClass().getCanonicalName() + " }" );
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
     * @throws EndpointConstructionException when endpoint cannot be created or used
     */
    public static synchronized OperationalEndpoint getOrCreate( Server server, AbstractEndpoint config ) throws EndpointConstructionException
    {
        OperationalEndpoint operationalEndpoint= null;

        if ( registry.containsKey( config.configName ) )
        {
            // endpoint already created
            operationalEndpoint= registry.get( config.configName );
            // endpoint must match, multiple server usage of the endpoint is not allowed
            if ( server != null && operationalEndpoint.server != null && server != operationalEndpoint.server )
            {
                throw new EndpointConstructionException( ENDPOINT_MSG_PREFIX + config.configName + " }: usage by multiple servers not allowed." );
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

    /**
     * Create an endpoint or return existing when already created
     * @param client The client using this endpoint.
     * @param config The endpoint configuration.
     * @return OperationalEndpoint instance that applies to the endpoint configuration.
     * @throws EndpointConstructionException When client parameter is empty
     */
    public static synchronized OperationalEndpoint getOrCreate( Client client, AbstractEndpoint config ) throws EndpointConstructionException
    {
        OperationalEndpoint operationalEndpoint= null;
        if ( client == null )
        {
            throw new EndpointConstructionException( ENDPOINT_MSG_PREFIX + config.configName + " }: no client configured." );
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
        ArrayList< String > found= new ArrayList<>();
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
        ArrayList< String > found= new ArrayList<>();
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
    public static synchronized void disposeAll( Server server )
    {
        List< String > names= find( server );
        for ( String endpointName : names )
        {
            OperationalEndpoint endpoint= registry.get( endpointName );
            if ( endpoint != null )
            {
                endpoint.server= null;
                if ( endpoint.clients.isEmpty() && endpoint.server == null )
                {
                    registry.remove( endpointName );
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
    public static synchronized void disposeAll( Client client )
    {
        List< String > names= find( client );
        for ( String endpointName : names )
        {
            OperationalEndpoint endpoint= registry.get( endpointName );

            if ( endpoint != null )
            {
                endpoint.clients.remove( client );
                if ( endpoint.clients.isEmpty() && endpoint.server == null )
                {
                    registry.remove( endpointName );
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
        if ( registry.isEmpty() )
        {
            //Schedulers are not needed any more, so stop them
            CoapConnector.stopIoScheduler();
            CoapConnector.stopLightScheduler();
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
     * @throws EndpointConstructionException When the endpoint could not be constructed.
     */
    private OperationalEndpoint( UDPEndpoint config ) throws EndpointConstructionException
    {
        EndpointConfigVisitor visitor= new EndpointConfigVisitor();
        try
        {
            config.accept( visitor );
        }
        catch ( ConfigException e )
        {
            throw new EndpointConstructionException( e );
        }
        this.configName= visitor.getEndpointName();
        this.coapEndpoint= visitor.getEndpointBuilder().build();
        this.coapEndpoint.setExecutors( CoapConnector.getIoScheduler(), CoapConnector.getLightScheduler() );
    }

    /**
     * Constructor for an operational Multicast UDP endpoint.
     * @param config the Multicast UDP endpoint configuration
     * @throws EndpointConstructionException When the endpoint could not be constructed.
    
     */
    private OperationalEndpoint( MulticastUDPEndpoint config ) throws EndpointConstructionException
    {
        MulticastUdpEndpointConfigVisitor visitor= new MulticastUdpEndpointConfigVisitor();
        try
        {
            config.accept( visitor );
        }
        catch ( ConfigException e )
        {
            throw new EndpointConstructionException( e );
        }
        this.configName= visitor.getEndpointName();
        this.coapEndpoint= visitor.getEndpointBuilder().build();
        this.coapEndpoint.setExecutors( CoapConnector.getIoScheduler(), CoapConnector.getLightScheduler() );
    }

    /**
     * Constructor for an operational DTLS endpoint.
     * @param config the UDP endpoint configuration
     * @throws EndpointConstructionException 
     */
    private OperationalEndpoint( DTLSEndpoint config ) throws EndpointConstructionException
    {
        DtlsEndpointConfigVisitor visitor= new DtlsEndpointConfigVisitor();
        try
        {
            config.accept( visitor );
        }
        catch ( ConfigException e )
        {
            throw new EndpointConstructionException( e );
        }
        this.configName= visitor.getEndpointName();
        try
        {
            this.coapEndpoint= visitor.getEndpointBuilder().build();
            this.coapEndpoint.setExecutors( CoapConnector.getIoScheduler(), CoapConnector.getLightScheduler() );
        }
        catch ( Exception e )
        {
            throw new EndpointConstructionException( ENDPOINT_MSG_PREFIX + config.configName + " } construction DTLS Endpoint failed.", e );
        }
    }

    private OperationalEndpoint( TCPServerEndpoint config ) throws EndpointConstructionException
    {
        throw new EndpointConstructionException( ENDPOINT_MSG_PREFIX + config.configName + " } TCP Server Endpoint NIY." );
    }

    private OperationalEndpoint( TCPClientEndpoint config ) throws EndpointConstructionException
    {
        throw new EndpointConstructionException( ENDPOINT_MSG_PREFIX + config.configName + " } TCP Client Endpoint NIY." );
    }

    private OperationalEndpoint( TLSServerEndpoint config ) throws EndpointConstructionException
    {
        throw new EndpointConstructionException( ENDPOINT_MSG_PREFIX + config.configName + " } TLS Server Endpoint NIY." );
    }

    private OperationalEndpoint( TLSClientEndpoint config ) throws EndpointConstructionException
    {
        throw new EndpointConstructionException( ENDPOINT_MSG_PREFIX + config.configName + " } TLS Client Endpoint NIY." );
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return ENDPOINT_MSG_PREFIX + configName + " }";
    }
}
