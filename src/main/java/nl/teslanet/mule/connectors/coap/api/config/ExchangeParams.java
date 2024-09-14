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
package nl.teslanet.mule.connectors.coap.api.config;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.config.congestion.CongestionControl;
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
     * Used by udp tcp.
     */
    @Parameter
    @Optional( defaultValue= "150000" )
    @Summary( "The maximum number of active peers supported." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer maxActivePeers= null;

    /**
     * The maximum time a peer may be inactive for before it is
     * considered stale and all state associated with it can be discarded.
     */
    @Parameter
    @Optional( defaultValue= "10m" )
    @Summary(
        "The maximum number time a peer may be inactive for before it is considered stale\n and all state associated with it can be discarded."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String maxPeerInactivityPeriod= null;

    /**
     * The minimum spacing time before retransmission is tried.
     * Used by udp transmission.
     */
    @Parameter
    @Optional( defaultValue= "2s" )
    @Summary( "The minimum spacing time before retransmission is tried." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String ackTimeout= null;

    /**
     * Factor for spreading retransmission timing.
     * Used by udp transmission.
     */
    @Parameter
    @Optional( defaultValue= "1.5" )
    @Summary( value= "Factor for spreading retransmission timing." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Float ackRandomFactor= null;

    /**
     * The back-off factor for retransmissions. Every subsequent retransmission time
     * spacing is enlarged using this factor.
     * Used by udp transmission.
     */
    @Parameter
    @Optional( defaultValue= "2.0" )
    @Summary(
                    value= "The back-off factor for retransmissions. \nEvery subsequent retransmission time, spacing is enlarged using this factor."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Float ackTimeoutScale= null;

    /**
     * The maximum number of retransmissions that are attempted when no
     * acknowledgement is received.
     * Used by udp transmission.
     */
    @Parameter
    @Optional( defaultValue= "4" )
    @Summary( value= "The maximum number of retransmissions that are attempted \nwhen no acknowledgement is received." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer maxRetransmit= null;

    /**
     * Maximum CoAP acknowledge timeout for CON messages. Not RFC7252 compliant.
     * Used by udp.
     */
    @Parameter
    @Optional( defaultValue= "1m" )
    @Summary( "Maximum CoAP acknowledge timeout for CON messages." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String maxAckTimeout= null;

    /**
     * The time from starting to send a Confirmable message
     * to the time when an acknowledgement is no longer expected.
     * Default values is 247 seconds.
     */
    @Parameter
    @Optional( defaultValue= "247s" )
    @Summary(
                    value= "The duration between starting to send a Confirmable message \nto the moment when an acknowledgement is no longer expected."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String exchangeLifetime= null;

    /**
     * The duration from sending a Non-confirmable message to the
     * moment its Message ID can be safely reused.
     * Default values is 145000 (145 seconds).
     * Used by udp transmission derived.
     */
    @Parameter
    @Optional( defaultValue= "145s" )
    @Summary(
                    value= "The duration from sending a Non-confirmable message to the moment when its Message ID can be safely reused."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String nonLifetime= null;

    /**
     * The duration a datagram is expected to take
     * from the start of its transmission to the completion of its
     * reception. Default values is 100 (100 seconds).
     * See
     * <a href="https://datatracker.ietf.org/doc/html/rfc7252#section-4.8.2"
     * target="_blank">RFC7252, 4.8.2. Time Values Derived from Transmission
     * Parameters</a>.
      * Used by udp transmission derived.
    */
    @Parameter
    @Optional( defaultValue= "100s" )
    @Summary(
                    value= "The maximum duration a datagram is expected to take from the start \n of its transmission to the completion of its reception."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String maxLatency= null;

    /**
     * The maximum duration from the first transmission of 
     * a Confirmable message to the time when the sender gives up on 
     * receiving an acknowledgement or reset. Default value is 93 seconds.
     * See
     * <a href="https://datatracker.ietf.org/doc/html/rfc7252#section-4.8.2"
     * target="_blank">RFC7252, 4.8.2. Time Values Derived from Transmission
     * Parameters</a>.
     * Used by udp transmission derived.
     */
    @Parameter
    @Optional( defaultValue= "93s" )
    @Summary(
                    value= "The maximum duration from the first transmission of \n"
                        + "a Confirmable message to the time when the sender gives up on \n"
                        + "receiving an acknowledgement or reset."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String maxTransmitWait= null;

    /**
     * The expected maximum response delay over all servers 
     * that the client can send a multicast request to. See
     * <a href="https://datatracker.ietf.org/doc/html/rfc7390#section-2.5"
     * target="_blank">RFC7390, 2.5. Request and Response Model</a>
     * Default values is 250 seconds).
     * Used by udp multicast.
     */
    @Parameter
    @Optional( defaultValue= "250s" )
    @Summary(
                    value= "The expected maximum response delay over all servers \n"
                        + "that the client can send a multicast request to."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String maxResponseDelay= null;

    /**
     * Maximum number of simultaneous outstanding interactions with a peer. 
     * (rfc7252 specifies default=1)
     * Used by udp transmission.
     */
    @Parameter
    @Optional( defaultValue= "1" )
    @Summary(
                    value= "Maximum number of simultaneous outstanding interactions with a peer. \n(rfc7252 specifies default=1)"
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer nstart= null;

    /**
     * The maximum token length (bytes).
     * Used by udp tcp.
     */
    @Parameter
    @Optional( defaultValue= "8" )
    @Summary( value= "The maximum token length [bytes]." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer tokenSizeLimit= null;

    /**
     * Base for multicast MID range. Multicast requests use MIDs in the range [base...65536). 
     * A negative or zero value disables multicast requests.
     * Used by udp and multicast.
     */
    @Parameter
    @Optional( defaultValue= "65000" )
    @Summary(
                    value= "Base for multicast MID range. Multicast requests use MIDs in the range [base...65536).\nA negative or zero value disables multicast requests."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer multicastMidBase= null;

    /**
     * LEISURE is the period of time (in milliseconds [ms]) of the spreading of responses to a
     * multicast request, for network congestion prevention.
     * Used by udp transmission- multicast.
     * @see <a href="https://www.rfc-editor.org/rfc/rfc7252.html#section-8.2" >IETF RFC 7252 - 8.2. Request/Response Layer</a>
     */
    @Parameter
    @Optional( defaultValue= "5s" )
    @Summary( value= "The spreading time of responses to a multicast request, \nfor network congestion prevention." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String leisure= null;

    /**
     * When enabled empty messages that contain token, options or payload are considered a format error.
     * Used by udp tcp.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Summary(
                    value= "When enabled empty messages that contain token, options or payload are considered a format error."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean strictEmptyMessageFormat= true;

    /**
     * When {@code true} the message IDs will start at a random index. Otherwise the
     * first message ID returned will be {@code 0}.
     * Used by udp.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Summary(
                    value= "When true the message IDs will start at a random index. Otherwise the first message ID returned will be 0."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean useRandomMidStart= true;

    /**
     * The message identity tracker used. The tracker maintains the administration
     * of message id's uses in the CoAP exchanges. These use different strategies
     * like maintaining a map of individual entries or use groups where id's get
     * cleaned by group. Supported values are {@code NULL}, {@code GROUPED}, or
     * {@code MAPBASED}.
     * Used by udp.
     */
    @Parameter
    @Optional( )
    @NullSafe( defaultImplementingType= GroupedMidTracker.class )
    @Summary( value= "The message identity tracker strategy to use." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public MidTracker midTracker= null;

    /**
     * The deduplicator type used to deduplicate incoming messages. Available
     * deduplicators are MARK_AND_SWEEP, PEERS_MARK_AND_SWEEP and CROP_ROTATION.
     * Used by udp.
     */
    @Parameter
    @Optional( )
    @Summary( " The deduplicator type used to deduplicate incoming messages." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Deduplicator deduplicator= null;

    /**
     * Configuration of the congestion control algorithm, if any.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public CongestionControl congestionControl= null;

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

    /**
     * Accept visitor.
     */
    @Override
    public void accept( ConfigVisitor visitor ) throws ConfigException
    {
        visitor.visit( this );
        midTracker.accept( visitor );
        if ( deduplicator != null ) deduplicator.accept( visitor );
        if ( congestionControl != null ) congestionControl.accept( visitor );
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj )
    {
        if ( obj == null )
        {
            return false;
        }
        if ( obj == this )
        {
            return true;
        }
        if ( obj.getClass() != getClass() )
        {
            return false;
        }
        ExchangeParams rhs= (ExchangeParams) obj;
        return new EqualsBuilder()
            .append( ackRandomFactor, rhs.ackRandomFactor )
            .append( ackTimeout, rhs.ackTimeout )
            .append( ackTimeoutScale, rhs.ackTimeoutScale )
            .append( congestionControl, rhs.congestionControl )
            .append( deduplicator, rhs.deduplicator )
            .append( exchangeLifetime, rhs.exchangeLifetime )
            .append( leisure, rhs.leisure )
            .append( maxAckTimeout, rhs.maxAckTimeout )
            .append( maxActivePeers, rhs.maxActivePeers )
            .append( maxLatency, rhs.maxLatency )
            .append( maxPeerInactivityPeriod, rhs.maxPeerInactivityPeriod )
            .append( maxResponseDelay, rhs.maxResponseDelay )
            .append( maxRetransmit, rhs.maxRetransmit )
            .append( maxTransmitWait, rhs.maxTransmitWait )
            .append( midTracker, rhs.midTracker )
            .append( multicastMidBase, rhs.multicastMidBase )
            .append( nonLifetime, rhs.nonLifetime )
            .append( nstart, rhs.nstart )
            .append( strictEmptyMessageFormat, rhs.strictEmptyMessageFormat )
            .append( tokenSizeLimit, rhs.tokenSizeLimit )
            .append( useRandomMidStart, rhs.useRandomMidStart )
            .isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 21, 41 )
            .append( ackRandomFactor )
            .append( ackTimeout )
            .append( ackTimeoutScale )
            .append( congestionControl )
            .append( deduplicator )
            .append( exchangeLifetime )
            .append( leisure )
            .append( maxAckTimeout )
            .append( maxActivePeers )
            .append( maxLatency )
            .append( maxPeerInactivityPeriod )
            .append( maxResponseDelay )
            .append( maxRetransmit )
            .append( maxTransmitWait )
            .append( midTracker )
            .append( multicastMidBase )
            .append( nonLifetime )
            .append( nstart )
            .append( strictEmptyMessageFormat )
            .append( tokenSizeLimit )
            .append( useRandomMidStart )
            .toHashCode();
    }
}
