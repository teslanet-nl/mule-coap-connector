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
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AdditionalEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.Endpoint;
import nl.teslanet.mule.connectors.coap.internal.CoAPConnector;
import nl.teslanet.mule.connectors.coap.internal.OperationalEndpoint;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResourceRegistryException;


/**
 * The Server is used to receive requests on resources from CoAP clients.
 * The configuration is static, which means Mule will create one instance per
 * configuration.
 */
@Configuration( name= "server" )
@Sources( value=
{ Listener.class } )
@Operations( ServerOperations.class )
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

    /**
     * Main endpoint the server uses.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( value= "Main endpoint the server uses." )
    @Placement( order= 1, tab= "Endpoint" )
    Endpoint endpoint;

    /**
     * The additional endpoints the server uses.
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( value= "Additional endpoints the server uses." )
    @Placement( order= 4, tab= "Advanced" )
    private List< AdditionalEndpoint > additionalEndpoints;

    /**
     * The root resources of the server.
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( value= "The root resources of the server." )
    private List< ResourceConfig > resources;

    /**
     * Notify observing clients of shutdown of the server.
     * When true observing clients are notified by Not-Found notifications.
     * Default value is true.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Summary( value= "Notify observing clients of shutdown of the server.\nWhen true observing clients are notified by Not-Found notifications. \nDefault value is 100 ms." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Placement( order= 1, tab= "Advanced" )
    public boolean notifyOnShutdown= true;

    /**
     * The linger time (in milliseconds [ms]) during shutdown of the server 
     * which gives active exchanges time to complete.
     * Default value is 100 ms.
     */
    @Parameter
    @Optional( defaultValue= "100" )
    @Summary( value= "The linger time (in milliseconds [ms]) during shutdown of the server \nwhich gives active exchanges time to complete. \nDefault value is 100 ms." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Placement( order= 2, tab= "Advanced" )
    public long shutdownLinger= 100L;

    /**
     * Thread pool size of endpoint executor. Default value is equal to the number
     * of cores.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Placement( order= 3, tab= "Advanced" )
    @Summary( "Thread pool size of endpoint executor. Default value is equal to the number of cores." )
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
            if ( endpoint.getEndpoint() == null ) throw new InitialisationException( new IllegalArgumentException( "Unexpected null value in EndpointConfig." ), this );
            //add main endpoint config
            endpoints.add( endpoint.getEndpoint() );
        }
        else if ( additionalEndpoints.isEmpty() )
        {
            // user wants default endpoint
            endpoints.add( new DefaultServerEndpoint( this.toString() + "-endpoint" ) );
            LOGGER.info( this + " using default udp endpoint." );
        }
        for ( AdditionalEndpoint additionalEndpoint : additionalEndpoints )
        {
            if ( additionalEndpoint.getEndpoint() == null )
                throw new InitialisationException( new IllegalArgumentException( "Unexpected null value in additionalEndpoint." ), this );
            endpoints.add( additionalEndpoint.getEndpoint() );
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

    /**
     * Dispose of the server. Endpoints will be disconnected and cleaned up when needed.
     */
    @Override
    public void dispose()
    {
        server.destroy();
        OperationalEndpoint.disposeAll( this );
        LOGGER.info( this + " disposed." );
    }

    /**
     * Start the server. When started the server will accept requests.
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

    /**
     * Stop the server. All resources of the server will be removed and observing clients will be notified of this.
     * After  
     */
    @Override
    public void stop() throws MuleException
    {
        try
        {
            if ( notifyOnShutdown )
            {
                //remove all resources from registry before stopping 
                //to get observing clients notified of the fact that resources 
                //are not available anymore. 
                registry.remove( "/*" );
            }
            //stop server after waiting to get notifications sent.
            Thread.sleep( shutdownLinger );
            server.stop();
            if ( !notifyOnShutdown )
            {
                //cleanup still needed
                registry.remove( "/*" );
            }
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
    public List< AdditionalEndpoint > getAdditionalEndpoints()
    {
        return additionalEndpoints;
    }

    /**
     * @param endpoints the endpoints references to set.
     */
    public void setAdditionalEndpoints( List< AdditionalEndpoint > endpoints )
    {
        this.additionalEndpoints= endpoints;
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
