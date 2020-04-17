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
package nl.teslanet.mule.connectors.coap.internal.server;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.mule.runtime.api.exception.DefaultMuleException;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.scheduler.SchedulerConfig;
import org.mule.runtime.api.scheduler.SchedulerService;
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
import org.mule.runtime.extension.api.annotation.param.reference.ConfigReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.teslanet.mule.connectors.coap.api.ResourceConfig;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.Endpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.UDPEndpoint;
import nl.teslanet.mule.connectors.coap.internal.CoAPConnector;
import nl.teslanet.mule.connectors.coap.internal.OperationalEndpoint;


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
    private final Logger LOGGER= LoggerFactory.getLogger( Server.class );

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

    // mule sdk does not seem to support a list of root level configurations
    // @Parameter
    // @Optional
    // @ConfigReference(namespace = "COAP", name = "UDP_ENDPOINT")
    // @ConfigReference(namespace = "COAP", name = "TCP_ENDPOINT")
    // @ConfigReference(namespace = "COAP", name = "DTLS_ENDPOINT")
    // @ConfigReference(namespace = "COAP", name = "TLS_ENDPOINT")
    // @Expression(ExpressionSupport.NOT_SUPPORTED)
    // @ParameterDsl(allowReferences = true, allowInlineDefinition = true)
    // private List<Endpoint> endpoints;

    // sort of workaround
    @Parameter
    @Optional
    @ConfigReference(namespace= "COAP", name= "UDP_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "MULTICAST_UDP_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "DTLS_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "TCP_CLIENT_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "TCP_SERVER_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "TLS_CLIENT_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "TLS_SERVER_ENDPOINT")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= true)
    @Placement(order= 1, tab= "Endpoint")
    Endpoint endpoint;

    @Parameter
    @Optional
    @ConfigReference(namespace= "COAP", name= "UDP_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "MULTICAST_UDP_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "DTLS_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "TCP_CLIENT_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "TCP_SERVER_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "TLS_CLIENT_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "TLS_SERVER_ENDPOINT")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= true)
    @Placement(order= 1, tab= "Endpoint 1")
    Endpoint endpoint1;

    @Parameter
    @Optional
    @ConfigReference(namespace= "COAP", name= "UDP_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "MULTICAST_UDP_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "DTLS_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "TCP_CLIENT_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "TCP_SERVER_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "TLS_CLIENT_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "TLS_SERVER_ENDPOINT")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= true)
    @Placement(order= 1, tab= "Endpoint 2")
    Endpoint endpoint2;

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
        registry= new ResourceRegistry( server.getRoot() );
        //workaround, Mule sdk does not support list of root-configurations
        ArrayList< Endpoint > endpoints= new ArrayList< Endpoint >();
        if ( endpoint != null )
        {
            endpoints.add( endpoint );
        }
        if ( endpoint1 != null )
        {
            endpoints.add( endpoint1 );
        }
        if ( endpoint2 != null )
        {
            endpoints.add( endpoint2 );
        }
        if ( endpoints.isEmpty() )
        {
            // user wants default endpoint
            endpoints.add( new UDPEndpoint() );
        }
        int endpointNr= 0;
        for ( Endpoint endpoint : endpoints )
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
                LOGGER.info( "CoAP server '" + serverName + "' (" + this + ") initalised {" + operationalEndpoint + " } scheduler: " + schedulerService );
            }
            catch ( Exception e )
            {
                // TODO Auto-generated catch block
                throw new InitialisationException( e, this );
            }
        }
    }

    @Override
    public void dispose()
    {
        server.destroy();
        OperationalEndpoint.disposeAll( this );
        LOGGER.info( "CoAP server '" + serverName + "' (" + this + ") disposed" );
    }

    /* (non-Javadoc)
     * @see org.mule.runtime.api.lifecycle.Startable#start()
     */
    @Override
    public void start() throws MuleException
    {
        //TODO add testcase when null
        if ( resources != null ) try
        {
            for ( ResourceConfig resourceConfig : resources )
            {
                registry.add( null, resourceConfig );
            }
        }
        catch ( Exception e )
        {
            //make exception specific
            throw new DefaultMuleException( "CoAP configuration error", e );
        }
        server.start();
        LOGGER.info( "CoAP server '" + serverName + "' (" + this + ") started -> " + resources );

    }

    /* (non-Javadoc)
     * @see org.mule.runtime.api.lifecycle.Stoppable#stop()
     */
    @Override
    public void stop() throws MuleException
    {
        //stop server
        server.stop();
        //remove all resources from registry
        registry.remove( "/*" );
        LOGGER.info( "CoAP server '" + serverName + "' (" + this + ") stopped" );
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
        // TODO Auto-generated method stub
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

}
