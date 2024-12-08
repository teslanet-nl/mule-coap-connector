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
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Configuration of which it is not clear whether it is really used.
 *
 */
public class UdpParams implements VisitableConfig
{
    /**
     * Receiver thread pool size. Default value is CORES > 3 ? 2 : 1.
     */
    @Parameter
    @Optional( defaultValue= "1" )
    @Summary( value= "Receiver thread pool size. Default value is CORES > 3 ? 2 : 1." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer receiverThreadCount= null;

    /**
     * Sender thread pool size. Default value is CORES > 3 ? 2 : 1.
     */
    @Parameter
    @Optional( defaultValue= "1" )
    @Summary( value= "Sender thread pool size. Default value is CORES > 3 ? 2 : 1." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer senderThreadCount= null;

    /**
     * Maximum UDP datagram size [bytes] that can be received.
     */
    @Parameter
    @Optional( defaultValue= "2048" )
    @Summary(
                    value= "Maximum UDP datagram size [bytes] that can be received. The default size is 2048 bytes and must be at least 64 bytes."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer datagramSize= null;

    /**
     * Maximum number of pending outbound messages. The number is unlimited by default and must be at least 32.
     */
    @Parameter
    @Optional
    @Summary(
                    value= "Maximum number of pending outbound messages. The number is unlimited by default and must be at least 32."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer outCapacity= null;

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
        UdpParams rhs= (UdpParams) obj;
        return new EqualsBuilder()
            .append( datagramSize, rhs.datagramSize )
            .append( outCapacity, rhs.outCapacity )
            .append( receiverThreadCount, rhs.receiverThreadCount )
            .append( senderThreadCount, rhs.senderThreadCount )
            .isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 15, 35 )
            .append( datagramSize )
            .append( outCapacity )
            .append( receiverThreadCount )
            .append( senderThreadCount )
            .toHashCode();
    }
}
