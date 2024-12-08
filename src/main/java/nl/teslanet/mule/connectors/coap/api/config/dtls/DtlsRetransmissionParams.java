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
package nl.teslanet.mule.connectors.coap.api.config.dtls;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.config.ConfigException;
import nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor;
import nl.teslanet.mule.connectors.coap.api.config.VisitableConfig;


/**
 * Configuration items that are DTLS specific.
 *
 */
public class DtlsRetransmissionParams implements VisitableConfig
{
    /**
     * The initial DTLS retransmission timeout.
     */
    @Parameter
    @Optional( defaultValue= "2s" )
    @Summary( value= "The initial DTLS retransmission timeout." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String initialTimeout= null;

    /**
     * The maximum DTLS retransmission timeout.
     */
    @Parameter
    @Optional( defaultValue= "60s" )
    @Summary( value= "The maximum DTLS retransmission timeout." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String maxTimeout= null;

    /**
     * DTLS random factor for initial retransmission timeout.
     */
    @Parameter
    @Optional( defaultValue= "1.0" )
    @Summary( value= "DTLS random factor for initial retransmission timeout." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Float timeoutRandomFactor= null;

    /**
     * Random factor applied to the initial retransmission timeout.
     */
    @Parameter
    @Optional( defaultValue= "2.0" )
    @Summary( value= "DTLS scale factor for retransmission backoff-timeout." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Float timeoutScaleFactor;

    /**
     * DTLS additional initial timeout for ECC related flights.
     */
    @Parameter
    @Optional( defaultValue= "0ms" )
    @Summary( value= "DTLS additional initial timeout for ECC related flights." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String additionalEccTimeout= null;

    /**
     * DTLS maximum number of flight retransmissions.
     */
    @Parameter
    @Optional( defaultValue= "4" )
    @Summary( value= "DTLS maximum number of flight retransmissions." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer maxRetransmissions= null;

    /**
     * Number of flight-retransmissions before switching to backoff mode using single handshake messages in single record datagrams.
     */
    @Parameter
    @Optional
    @Summary(
                    value= "Number of flight-retransmissions before switching to backoff mode using single handshake messages in single record datagrams."
                        + "\nSet value to 0 to disable. Default value is max retransmissions / 2."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer backoffThreshold= null;

    /**
     * Stop retransmission on receiving the first message of the next flight, not waiting for the last message.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Summary(
                    value= "Stop retransmission on receiving the first message of the next flight, not waiting for the last message."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean earlyStop= true;

    /**
     * Accept visitor.
     */
    @Override
    public void accept( ConfigVisitor visitor ) throws ConfigException
    {
        visitor.visit( this );
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
        DtlsRetransmissionParams rhs= (DtlsRetransmissionParams) obj;
        return new EqualsBuilder()
            .append( initialTimeout, rhs.initialTimeout )
            .append( maxTimeout, rhs.maxTimeout )
            .append( timeoutRandomFactor, rhs.timeoutRandomFactor )
            .append( timeoutScaleFactor, rhs.timeoutScaleFactor )
            .append( additionalEccTimeout, rhs.additionalEccTimeout )
            .append( maxRetransmissions, rhs.maxRetransmissions )
            .append( backoffThreshold, rhs.backoffThreshold )
            .append( earlyStop, rhs.earlyStop )
            .isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 15, 35 )
            .append( initialTimeout )
            .append( maxTimeout )
            .append( timeoutRandomFactor )
            .append( timeoutScaleFactor )
            .append( additionalEccTimeout )
            .append( maxRetransmissions )
            .append( backoffThreshold )
            .append( earlyStop )
            .toHashCode();
    }
}
