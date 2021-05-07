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
package nl.teslanet.mule.connectors.coap.api.config;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.config.deduplication.Deduplicator;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.GroupedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.MidTracker;


/**
 * Configuration of coap exchange parameters
 *
 */
public class ExchangeParams implements VisitableConfig
{
    /**
     * The maximum number of active peers supported.
     */
    @Parameter
    @Optional(defaultValue= "150000")
    @Summary("The maximum number of active peers supported.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public Integer maxActivePeers= null;

    /**
     * The maximum number of seconds [s] a peer may be inactive for before it is
     * considered stale and all state associated with it can be discarded.
     */
    @Parameter
    @Optional(defaultValue= "600")
    @Summary("The maximum number of seconds [s] a peer may be inactive for before it is considered stale\n and all state associated with it can be discarded.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public Integer maxPeerInactivityPeriod= null;

    /**
     * The minimum spacing (in milliseconds [ms]) before retransmission is tried.
     */
    @Parameter
    @Optional(defaultValue= "2000")
    @Summary("The maximum number of seconds [s] a peer may be inactive for before it is considered stale\n and all state associated with it can be discarded.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Placement(tab= "Exchange", order= 1)
    public Integer ackTimeout= null;

    /**
     * Factor for spreading retransmission timing.
     */
    @Parameter
    @Optional(defaultValue= "1.5")
    @Summary(value= "Factor for spreading retransmission timing.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Placement(tab= "Exchange", order= 2)
    public Float ackRandomFactor= null;

    /**
     * The back-off factor for retransmissions. Every subsequent retransmission time
     * spacing is enlarged using this factor.
     */
    @Parameter
    @Optional(defaultValue= "2.0")
    @Summary(value= "The back-off factor for retransmissions. \nEvery subsequent retransmission time, spacing is enlarged using this factor.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Placement(tab= "Exchange", order= 3)
    public Float ackTimeoutScale= null;

    /**
     * The maximum number of retransmissions that are attempted when no
     * acknowledgement is received.
     */
    @Parameter
    @Optional(defaultValue= "4")
    @Summary(value= "The maximum number of retransmissions that are attempted \nwhen no acknowledgement is received.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Placement(tab= "Exchange", order= 4)
    public Integer maxRetransmit= null;

    /**
     * The time (in milliseconds [ms]) from starting to send a Confirmable message
     * to the time when an acknowledgement is no longer expected.
     * Default values is 247000 (247 seconds).
     */
    @Parameter
    @Optional(defaultValue= "247000")
    @Summary(value= "The time duration (in milliseconds [ms]) between starting to send a Confirmable message to the time when an acknowledgement is no longer expected. Default values is 247000 (247 seconds).")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Placement(tab= "Exchange", order= 5)
    public Long exchangeLifetime= null;

    /**
     * The time (in milliseconds [ms]) from sending a Non-confirmable message to the
     * time its Message ID can be safely reused.
     * Default values is 145000 (145 seconds).
     */
    @Parameter
    @Optional(defaultValue= "145000")
    @Summary(value= "The time (in milliseconds [ms]) from sending a Non-confirmable message to the time its Message ID can be safely reused. Default values is 145000 (145 seconds).")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Placement(tab= "Exchange", order= 6)
    public Long nonLifetime= null;

    /**
     * Maximum number of simultaneous outstanding interactions with a peer. 
     * (rfc7252 specifies default=1)
     */
    @Parameter
    @Optional(defaultValue= "1")
    @Summary(value= "Maximum number of simultaneous outstanding interactions with a peer. \n(rfc7252 specifies default=1)")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public Integer nstart= null;

    //TODO move to UDP params
    /**
     * When {@code true} the message IDs will start at a random index. Otherwise the
     * first message ID returned will be {@code 0}.
     */
    @Parameter
    @Optional(defaultValue= "true")
    @Summary(value= "When true the message IDs will start at a random index. Otherwise the first message ID returned will be 0.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public Boolean useRandomMidStart= null;

    //TODO move to UDP params
    /**
     * The message identity tracker used. The tracker maintains the administration
     * of message id's uses in the CoAP exchanges. These use different strategies
     * like maintaining a map of individual entries or use groups where id's get
     * cleaned by group. Supported values are {@code NULL}, {@code GROUPED}, or
     * {@code MAPBASED}.
     */
    @Parameter
    @Optional()
    @NullSafe(defaultImplementingType= GroupedMidTracker.class)
    @Summary(value= "The message identity tracker strategy to use.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public MidTracker midTracker= null;

    /**
     * The maximum token length (bytes).
     */
    @Parameter
    @Optional(defaultValue= "8")
    @Summary(value= " The maximum token length [bytes].")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public Integer tokenSizeLimit= null;

    /**
     * The deduplicator type used to deduplicate incoming messages. Available
     * deduplicators are MARK_AND_SWEEP and CROP_ROTATION.
     */
    @Parameter
    @Optional()
    @Summary(" The deduplicator type used to deduplicate incoming messages.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public Deduplicator deduplicator= null;

    /**
     * Default exchange params used by Mule. 
     * Containing mandatory and Nullsafe params are set by Mule.
     */
    public ExchangeParams()
    {
        //noop
    }

    /**
     * Constructor for manually constructing the exchange params.
     * @param midTracker The manually set MidTracker.
     */
    public ExchangeParams( MidTracker midTracker )
    {
        this.midTracker= midTracker;
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.VisitableConfig#accept(nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor)
     */
    @Override
    public void accept( ConfigVisitor visitor )
    {
        visitor.visit( this );
        midTracker.accept( visitor );
        if ( deduplicator != null ) deduplicator.accept( visitor );
    }
}
