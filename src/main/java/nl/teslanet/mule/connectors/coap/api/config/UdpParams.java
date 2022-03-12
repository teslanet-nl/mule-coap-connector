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
package nl.teslanet.mule.connectors.coap.api.config;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.config.midtracker.GroupedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.MidTracker;


/**
 * Configuration of which it is not clear whether it is really used.
 *
 */
public class UdpParams implements VisitableConfig
{
    /**
     * When {@code true} the message IDs will start at a random index. Otherwise the
     * first message ID returned will be {@code 0}.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Summary( value= "When true the message IDs will start at a random index. Otherwise the first message ID returned will be 0." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Boolean useRandomMidStart= null;

    /**
     * The message identity tracker used. The tracker maintains the administration
     * of message id's uses in the CoAP exchanges. These use different strategies
     * like maintaining a map of individual entries or use groups where id's get
     * cleaned by group. Supported values are {@code NULL}, {@code GROUPED}, or
     * {@code MAPBASED}.
     */
    @Parameter
    @Optional( )
    @NullSafe( defaultImplementingType= GroupedMidTracker.class )
    @Summary( value= "The message identity tracker strategy to use." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public MidTracker midTracker= null;

    /**
     * Receiver thread pool size. Default value is 1, or equal to the number of
     * cores on Windows.
     */
    @Parameter
    @Optional
    @Summary( value= "Receiver thread pool size. Default value is 1, or equal to the number of cores on Windows." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer networkStageReceiverThreadCount= null;

    /**
     * Sender thread pool size. Default value is 1, or equal to the number of cores
     * on Windows.
     */
    @Parameter
    @Optional
    @Summary( value= "Sender thread pool size. Default value is 1, or equal to the number of cores on Windows." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer networkStageSenderThreadCount= null;

    /**
     * UDP datagram size [bytes].
     */
    @Parameter
    @Optional( defaultValue= "2048" )
    @Summary( value= "UDP datagram size [bytes]." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer udpConnectorDatagramSize= null;

    /**
     * UDP receive buffer size [bytes].
     */
    @Parameter
    @Optional
    @Summary( value= "UDP receive buffer size [bytes]." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer udpConnectorReceiveBuffer= null;

    /**
     * UDP send buffer size [bytes].
     */
    @Parameter
    @Optional
    @Summary( value= "UDP send buffer size [bytes]." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer udpConnectorSendBuffer= null;

    /**
     * Default udp params used by Mule. 
     * Containing mandatory and Nullsafe params are set by Mule.
     */
    public UdpParams()
    {
        //noop
    }

    /**
     * Constructor for manually constructing the udp params.
     * @param midTracker The manually set MidTracker.
     */
    public UdpParams( MidTracker midTracker )
    {
        this.midTracker= midTracker;
    }

    /**
     * Accept the visitor. Let the visitor visit this object and its member objects. 
     */
    @Override
    public void accept( ConfigVisitor visitor )
    {
        visitor.visit( this );
        midTracker.accept( visitor );
    }

}
