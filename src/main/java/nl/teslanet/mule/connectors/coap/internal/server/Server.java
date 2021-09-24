/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2021 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.internal.server;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.scheduler.SchedulerConfig;
import org.mule.runtime.api.scheduler.SchedulerService;
import org.mule.runtime.core.api.lifecycle.StartException;
import org.mule.runtime.core.api.lifecycle.StopException;
import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.Sources;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.RefName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.teslanet.mule.connectors.coap.api.ResourceConfig;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.GlobalEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.UDPEndpoint;
import nl.teslanet.mule.connectors.coap.internal.CoAPConnector;
import nl.teslanet.mule.connectors.coap.internal.OperationalEndpoint;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResourceRegistryException;


/**
 * The Server is used to receive requests on resources from CoAP clients.
 * The configuration is static, which means Mule will create one instance per
 * configuration.
 */
@Configuration(name= "server")
@Sources(value= { Listener.class })
@Operations(ServerOperations.class)
public class Server implements Initialisable, Disposable, Startable, Stoppable
{
    private final Logger LOGGER= LoggerFactory.getLogger( Server.class.getCanonicalName() );

    /**
     * The name of the server.
     */
    @RefName
    private String serverName;

    /**
     * Injected Scheduler service.
     */
    @Inject
    private SchedulerService schedulerService;

    /**
     * Injected scheduler configuration.
     */
    @Inject
    private SchedulerConfig schedulerConfig;

    @Parameter
    @Optional
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Summary(value= "Locally defined endpoint the server uses.")
    @Placement(order= 1, tab= "Endpoint")
    AbstractEndpoint endpoint;

    /**
     * The root resources of the server.
     */
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Summary(value= "Global endpoints the server uses.")
    @Placement(order= 1, tab= "Global-Endpoints")
    private List< GlobalEndpoint > globalEndpoints;

    /**
     * The root resources of the server.
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Summary(value= "The root resources of the server.")
    private List< ResourceConfig > resources;

    /**
     * Thread pool size of endpoint executor. Default value is equal to the number
     * of cores.
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Placement(tab= "Advanced")
    @Summary("Thread pool size of endpoint executor. Default value is equal to the number of cores.")
    private Integer protocolStageThreadCount= null;

    /**
     * The Californium CoAP server instance.
     */
    private CoapServer server= null;

    /**
     * The registry of resources and listeners. 
     */
    private ResourceRegistry registry= null;

    /* (non-Javadoc)
     * @see org.mule.runtime.api.lifecycle.Initialisable#initialise()
     */
    @Override
    public void initialise() throws InitialisationException
    {
        CoAPConnector.setSchedulerService( schedulerService, schedulerConfig );
        NetworkConfig networkConfig= NetworkConfig.createStandardWithoutFile();
        if ( protocolStageThreadCount != null )
        {
            networkConfig.setInt( NetworkConfig.Keys.PROTOCOL_STAGE_THREAD_COUNT, protocolStageThreadCount );
        }
        server= new CoapServer( networkConfig );
        try
        {
            registry= new ResourceRegistry( server.getRoot() );
        }
        catch ( InternalResourceRegistryException e1 )
        {
            throw new InitialisationException( e1, this );

        }
        ArrayList< AbstractEndpoint > endpoints= new ArrayList<>();

        if ( endpoint != null )
        {
            //add inline endpoint config
            endpoints.add( endpoint );
        }
        else if ( globalEndpoints.isEmpty() )
        {
            // user wants default endpoint
            endpoints.add( new UDPEndpoint( this.toString() ) );
            LOGGER.info( this + " using default udp endpoint." );
        }
        for ( GlobalEndpoint globalEndpoint : globalEndpoints )
        {
            endpoints.add( globalEndpoint.getEndpoint() );
        }
        int endpointNr= 0;
        for ( AbstractEndpoint endpoint : endpoints )
        {
            if ( endpoint.configName == null )
            {
                // inline endpoint will get this as name
                endpoint.configName= ( this.toString() + "-" + endpointNr++ );
            }
            try
            {
                OperationalEndpoint operationalEndpoint= OperationalEndpoint.getOrCreate( this, endpoint );
                server.addEndpoint( operationalEndpoint.getCoapEndpoint() );
                LOGGER.info( this + " connected to " + operationalEndpoint );
            }
            catch ( Exception e )
            {
                throw new InitialisationException( e, this );
            }
        }
        LOGGER.info( this + " initalised." );
    }

    @Override
    public void dispose()
    {
        server.destroy();
        OperationalEndpoint.disposeAll( this );
        LOGGER.info( this + " disposed." );
    }

    /* (non-Javadoc)
     * @see org.mule.runtime.api.lifecycle.Startable#start()
     */
    @Override
    public void start() throws MuleException
    {
        try
        {
            if ( resources != null )
            {
                for ( ResourceConfig resourceConfig : resources )
                {
                    registry.add( null, resourceConfig );
                }
                server.start();
            }
        }
        catch ( Exception e )
        {
            throw new StartException( e, this );
        }
        LOGGER.info( this + " started." );

    }

    /* (non-Javadoc)
     * @see org.mule.runtime.api.lifecycle.Stoppable#stop()
     */
    @Override
    public void stop() throws MuleException
    {
        try
        {
            //remove all resources from registry before stopping 
            //to get observing clients notified of the fact that resources 
            //are not available anymore. 
            registry.remove( "/*" );
            //stop server after waiting
            //TODO
            Thread.sleep( 2000L );
            server.stop();
        }
        catch ( Exception e )
        {
            throw new StopException( e, this );
        }
        LOGGER.info( this + " stopped" );
    }

    /**
     * Add listener to the server
     * @param operationalListener the listener to add
     */
    void addListener( OperationalListener operationalListener )
    {
        registry.add( operationalListener );
    }

    /**
     * Remove the listener from the server
     * @param listener the listener to remove
     */
    void removeListener( OperationalListener listener )
    {
        registry.remove( listener );
    }

    /**
     * @return the serverName
     */
    public String getServerName()
    {
        return serverName;
    }

    /**
     * @return the registry
     */
    public ResourceRegistry getRegistry()
    {
        return registry;
    }

    /**
     * @return the resources
     */
    public List< ResourceConfig > getResources()
    {
        return resources;
    }

    /**
     * @param resources the resources to set
     */
    public void setResources( List< ResourceConfig > resources )
    {
        this.resources= resources;
    }

    /**
     * @return the endpointRefs of the server.
     */
    public List< GlobalEndpoint > getEndpointRefs()
    {
        return globalEndpoints;
    }

    /**
     * @param globalEndpoints the endpoints references to set.
     */
    public void setEndpointRefs( List< GlobalEndpoint > globalEndpoints )
    {
        this.globalEndpoints= globalEndpoints;
    }

    /**
     * Get String repesentation.
     */
    @Override
    public String toString()
    {
        return "CoAP Server { " + getServerName() + " }";
    }
}
