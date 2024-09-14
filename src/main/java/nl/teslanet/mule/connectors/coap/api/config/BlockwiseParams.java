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
 * Configuration of blockwise parameters
 *
 */
public class BlockwiseParams implements VisitableConfig
{
    /**
     * The block size [bytes] to use when doing a blockwise transfer. This value
     * serves as the upper limit for block size in blockwise transfers.
     */
    @Parameter
    @Optional( defaultValue= "512" )
    @Summary(
        "The block size [bytes] to use when doing a blockwise transfer. This value serves as the upper limit for block size in blockwise transfers."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer preferredBlockSize= null;

    /**
     * The maximum payload size [bytes] that can be transferred in a single
     * message, i.e. without requiring a blockwise transfer. This value cannot
     * exceed the network's MTU.
     */
    @Parameter
    @Optional( defaultValue= "1024" )
    @Summary(
        "The maximum payload size [bytes] that can be transferred in a single message, i.e. without requiring a blockwise transfer. This value cannot exceed the network's MTU."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer maxMessageSize= null;

    /**
     * The maximum size [bytes] of a resource body that will be accepted as the
     * payload of a POST/PUT or the response to a GET request in a
     * <em>transparent</em> blockwise transfer. Note that this option does not
     * prevent local clients or resource implementations from sending large bodies
     * as part of a request or response to a peer.
     */
    @Parameter
    @Optional( defaultValue= "8192" )
    @Summary(
        "The maximum size [bytes] of a resource body that will be accepted as the payload of a POST/PUT or the response to a GET request in a transparent blockwise transfer."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer maxResourceBodySize= null;

    /**
     * The maximum amount of time in milliseconds [ms], allowed between transfers
     * of individual blocks in a blockwise transfer, before the blockwise transfer
     * state is discarded.
     */
    @Parameter
    @Optional( defaultValue= "5m" )
    @Summary(
        " The maximum amount of time allowed between transfers of individual blocks in a blockwise transfer, before the blockwise transfer state is discarded."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String statusLifetime= null;

    /**
     * The interval in milliseconds [ms] for removing expired/stale blockwise entries.
     */
    @Parameter
    @Optional( defaultValue= "5s" )
    @Summary( "The interval for removing expired/stale blockwise entries." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String statusInterval= null;

    /**
     * When activated the the Block1 option should be included in the error-responses.
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @Summary( "When activated the the Block1 option should be included in the error-responses." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean strictBlock1Option= false;

    /**
     * When activated the Block2 option is also included when client requests early 
     * blockwise negotiation but the response can be sent on one packet.
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @Summary(
        "When activated the Block2 option is also included when client requests early \nblockwise negotiation but the response can be sent on one packet."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean strictBlock2Option= false;

    /**
     * When activated CoAP client will try to use block mode or adapt 
     * the block size when receiving a 4.13 Entity too large response code.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Summary(
        "When activated CoAP client will try to use block mode or adapt \nthe block size when receiving a 4.13 Entity too large response code."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean entityTooLargeFailover= true;

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
        BlockwiseParams rhs= (BlockwiseParams) obj;
        return new EqualsBuilder()
            .append( entityTooLargeFailover, rhs.entityTooLargeFailover )
            .append( maxMessageSize, rhs.maxMessageSize )
            .append( maxResourceBodySize, rhs.maxResourceBodySize )
            .append( preferredBlockSize, rhs.preferredBlockSize )
            .append( statusInterval, rhs.statusInterval )
            .append( statusLifetime, rhs.statusLifetime )
            .append( strictBlock1Option, rhs.strictBlock1Option )
            .append( strictBlock2Option, rhs.strictBlock2Option )
            .isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 19, 39 )
            .append( entityTooLargeFailover )
            .append( maxMessageSize )
            .append( maxResourceBodySize )
            .append( preferredBlockSize )
            .append( statusInterval )
            .append( statusLifetime )
            .append( strictBlock1Option )
            .append( strictBlock2Option )
            .toHashCode();
    }
}
