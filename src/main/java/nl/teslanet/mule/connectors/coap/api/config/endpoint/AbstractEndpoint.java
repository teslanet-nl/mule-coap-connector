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
package nl.teslanet.mule.connectors.coap.api.config.endpoint;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.RefName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.config.BlockwiseParams;
import nl.teslanet.mule.connectors.coap.api.config.ConfigException;
import nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor;
import nl.teslanet.mule.connectors.coap.api.config.ExchangeParams;
import nl.teslanet.mule.connectors.coap.api.config.LogHealthStatus;
import nl.teslanet.mule.connectors.coap.api.config.NotificationParams;
import nl.teslanet.mule.connectors.coap.api.config.OptionParams;
import nl.teslanet.mule.connectors.coap.api.config.SocketParams;
import nl.teslanet.mule.connectors.coap.api.config.VisitableConfig;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.GroupedMidTracker;


/**
 * Endpoint configuration parameters
 *
 */
public abstract class AbstractEndpoint implements VisitableConfig
{
    @RefName
    public String configName;

    /**
     * The socketParams to use
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Placement( order= 1 )
    public SocketParams socketParams;

    /**
     * The parameters for blockwise transfer.
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public BlockwiseParams blockwiseParams= null;

    /**
     * The parameters for notification.
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public NotificationParams notificationParams= null;

    /**
    * The option parameters.
    */
    @Parameter
    @Optional
    @NullSafe
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public OptionParams optionParams= null;

    /**
     * The coap exchange parameters.
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public ExchangeParams exchangeParams= null;

    /**
     * When activated logHealthStatus is periodically logged.
     */
    @Parameter
    @Optional
    @Summary( "When activated logHealthStatus is periodically logged." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public LogHealthStatus logHealthStatus= null;

    /**
     * When activated incoming and outgoing CoAP messages are logged.
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @Summary( "When activated incoming and outgoing CoAP messages are logged." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean logCoapMessages= false;

    /**
     * Default Constructor used by Mule. 
     * Mandatory and Nullsafe params are set by Mule.
     */
    protected AbstractEndpoint()
    {
        //noop
    }

    /**
     * Constructor for manually constructing the endpoint.
     * (Mule uses default constructor and sets Nullsafe params.)
     * @param name The manually set name of the endpoint
     */
    protected AbstractEndpoint( String name )
    {
        this.configName= name;
        //initialise nullsafe params
        socketParams= new SocketParams();
        blockwiseParams= new BlockwiseParams();
        notificationParams= new NotificationParams();
        optionParams= new OptionParams();
        exchangeParams= new ExchangeParams( new GroupedMidTracker() );
    }

    /**
     * Constructor for manually constructing the endpoint.
     * (Mule uses default constructor and sets Nullsafe params.)
     * @param name The manually set name of the endpoint
     * @param port The manually set port to bind to.
     */
    protected AbstractEndpoint( String name, int port )
    {
        this( name );
        socketParams.bindToPort= port;
    }

    /**
     * Accept visitor.
     */
    @Override
    public void accept( ConfigVisitor visitor ) throws ConfigException
    {
        visitor.visit( this );
        socketParams.accept( visitor );
        blockwiseParams.accept( visitor );
        notificationParams.accept( visitor );
        if ( optionParams != null ) optionParams.accept( visitor );
        exchangeParams.accept( visitor );
        if ( logHealthStatus != null ) logHealthStatus.accept( visitor );
    }
}
