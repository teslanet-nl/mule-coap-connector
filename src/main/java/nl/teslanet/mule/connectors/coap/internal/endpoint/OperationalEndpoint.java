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
package nl.teslanet.mule.connectors.coap.internal.endpoint;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.interceptors.MessageTracer;
import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.api.scheduler.SchedulerConfig;
import org.mule.runtime.api.scheduler.SchedulerService;

import nl.teslanet.mule.connectors.coap.api.config.ConfigException;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.DTLSEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.MulticastUDPEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPClientEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPServerEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TLSClientEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TLSServerEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.UDPEndpoint;
import nl.teslanet.mule.connectors.coap.internal.client.Client;
import nl.teslanet.mule.connectors.coap.internal.config.DtlsEndpointConfigVisitor;
import nl.teslanet.mule.connectors.coap.internal.config.MulticastUdpEndpointConfigVisitor;
import nl.teslanet.mule.connectors.coap.internal.config.UdpEndpointConfigVisitor;
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
    private Optional< Server > server= Optional.empty();

    /**
     * The clients using the endpoint
     */
    private HashSet< Client > clients= new HashSet<>();

    /**
     * Flag indicating the schedulers are set.
     */
    private boolean schedulerIsSet= false;

    /**
     * Create an endpoint not attached to a server or return existing when already created
     * @param config the configuration for the endpoint
     * @param lightscheduler 
     * @return the endpoint created from the configuration
     * @throws EndpointConstructionException When the endpoint could not be constructed.
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
            throw new EndpointConstructionException(
                ENDPOINT_MSG_PREFIX + config.configName + " }: has unknown type { " + config
                    .getClass()
                    .getCanonicalName() + " }"
            );
        }
        if ( config.logTraffic )
        {
            operationalEndpoint.coapEndpoint.addInterceptor( new MessageTracer() );
        }
        return operationalEndpoint;
    }

    /**
     * Create an endpoint attached to a server or return existing when already created
     * @param server the server attached to the endpint
     * @param config The endpoint configuration.
     * @return the operational endpoint
     * @throws EndpointConstructionException when endpoint cannot be created or used
     */
    public static synchronized OperationalEndpoint getOrCreate( Server server, AbstractEndpoint config )
        throws EndpointConstructionException
    {
        OperationalEndpoint operationalEndpoint= null;
        Optional< Server > actualServer= Optional.of( server );

        if ( registry.containsKey( config.configName ) )
        {
            // endpoint already created
            operationalEndpoint= registry.get( config.configName );
            // endpoint must match, multiple server usage of the endpoint is not allowed
            if ( operationalEndpoint.server.isPresent() && !operationalEndpoint.server.equals( actualServer ) )
            {
                throw new EndpointConstructionException(
                    ENDPOINT_MSG_PREFIX + config.configName + " }: usage by multiple servers not allowed."
                );
            }
            operationalEndpoint.server= actualServer;
            return operationalEndpoint;
        }
        // need to create endpoint
        operationalEndpoint= create( config );
        operationalEndpoint.server= actualServer;
        registry.put( operationalEndpoint.getConfigName(), operationalEndpoint );
        return operationalEndpoint;
    }

    /**
     * Create an endpoint or return existing when already created
     * @param client The client using this endpoint.
     * @param config The endpoint configuration.
     * @return OperationalEndpoint instance that applies to the endpoint configuration.
     * @throws EndpointConstructionException When the endpoint could not be constructed.
     */
    public static synchronized OperationalEndpoint getOrCreate( Client client, AbstractEndpoint config )
        throws EndpointConstructionException
    {
        OperationalEndpoint operationalEndpoint= null;
        if ( client == null )
        {
            throw new EndpointConstructionException(
                ENDPOINT_MSG_PREFIX + config.configName + " }: no client configured."
            );
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
        Optional< Server > actualServer= Optional.of( server );
        ArrayList< String > found= new ArrayList<>();
        for ( Entry< String, OperationalEndpoint > entry : registry.entrySet() )
        {
            if ( actualServer.equals( entry.getValue().server ) )
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
                endpoint.server= Optional.empty();
                if ( endpoint.clients.isEmpty() )
                {
                    registry.remove( endpointName );
                    endpoint.coapEndpoint.destroy();
                }
            }
        }
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
                if ( endpoint.clients.isEmpty() && !endpoint.server.isPresent() )
                {
                    registry.remove( endpointName );
                    endpoint.coapEndpoint.destroy();
                }
            }
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
        UdpEndpointConfigVisitor visitor= new UdpEndpointConfigVisitor();
        try
        {
            config.accept( visitor );
        }
        catch ( ConfigException e )
        {
            throw new EndpointConstructionException( e );
        }
        this.configName= visitor.getEndpointName();
        this.coapEndpoint= visitor.getEndpoint();
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
        this.coapEndpoint= visitor.getEndpoint();
    }

    /**
     * Constructor for an operational DTLS endpoint.
     * @param config The endpoint configuration.
     * @throws EndpointConstructionException When the endpoint could not be constructed.
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
        this.coapEndpoint= visitor.getEndpoint();
    }

    /**
     * Constructor for an operational TCP server endpoint.
     * @param config The endpoint configuration.
     * @throws EndpointConstructionException When the endpoint could not be constructed.
     */
    private OperationalEndpoint( TCPServerEndpoint config ) throws EndpointConstructionException
    {
        throw new EndpointConstructionException(
            ENDPOINT_MSG_PREFIX + config.configName + " } TCP Server Endpoint NIY."
        );
    }

    /**
     * Constructor for an operational TCP client endpoint.
     * @param config The endpoint configuration.
     * @throws EndpointConstructionException 
     */
    private OperationalEndpoint( TCPClientEndpoint config ) throws EndpointConstructionException
    {
        throw new EndpointConstructionException(
            ENDPOINT_MSG_PREFIX + config.configName + " } TCP Client Endpoint NIY."
        );
    }

    /**
     * Constructor for an operational TLS serevr endpoint.
     * @param config The endpoint configuration.
     * @throws EndpointConstructionException When the endpoint could not be constructed.
     */
    private OperationalEndpoint( TLSServerEndpoint config ) throws EndpointConstructionException
    {
        throw new EndpointConstructionException(
            ENDPOINT_MSG_PREFIX + config.configName + " } TLS Server Endpoint NIY."
        );
    }

    /**
     * Constructor for an operational TLS client endpoint.
     * @param config The endpoint configuration.
     * @throws EndpointConstructionException When the endpoint could not be constructed.
     */
    private OperationalEndpoint( TLSClientEndpoint config ) throws EndpointConstructionException
    {
        throw new EndpointConstructionException(
            ENDPOINT_MSG_PREFIX + config.configName + " } TLS Client Endpoint NIY."
        );
    }

    /**
     * Set the schedulers of the endpoint when needed. 
     * When the endpoint is used by a server the schedulers will be set by the server.
     * @param schedulerService The SchedulerService that delivers the schedulers.
     * @param schedulerConfig The scheduler configuration to use.
     */
    public synchronized void setSchedulersIfNeeded( SchedulerService schedulerService, SchedulerConfig schedulerConfig )
    {
        if ( !server.isPresent() && !schedulerIsSet )
        {
            Scheduler ioScheduler= schedulerService.ioScheduler( schedulerConfig );
            Scheduler cpuLightScheduler= schedulerService.cpuLightScheduler( schedulerConfig );
            coapEndpoint.setExecutors( ioScheduler, cpuLightScheduler );
            schedulerIsSet= true;
        }
    }

    /**
     * String representation of the object.
     */
    public String toString()
    {
        return ENDPOINT_MSG_PREFIX + configName + " }";
    }
}
