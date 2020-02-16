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
package nl.teslanet.mule.connectors.coap.api.config;


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
    @Optional(defaultValue= "512")
    @Summary("The block size [bytes] to use when doing a blockwise transfer. This value serves as the upper limit for block size in blockwise transfers.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public Integer preferredBlockSize= null;

    /**
     * The maximum payload size [bytes] that can be transferred in a single
     * message, i.e. without requiring a blockwise transfer. This value cannot
     * exceed the network's MTU.
     */
    @Parameter
    @Optional(defaultValue= "1024")
    @Summary("The maximum payload size [bytes] that can be transferred in a single message, i.e. without requiring a blockwise transfer. This value cannot exceed the network's MTU.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public Integer maxMessageSize= null;

    /**
     * The maximum size [bytes] of a resource body that will be accepted as the
     * payload of a POST/PUT or the response to a GET request in a
     * <em>transparent</em> blockwise transfer. Note that this option does not
     * prevent local clients or resource implementations from sending large bodies
     * as part of a request or response to a peer.
     */
    @Parameter
    @Optional(defaultValue= "8192")
    @Summary("The maximum size [bytes] of a resource body that will be accepted as the payload of a POST/PUT or the response to a GET request in a transparent blockwise transfer.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public Integer maxResourceBodySize= null;

    /**
     * The maximum amount of time in milliseconds [ms], allowed between transfers
     * of individual blocks in a blockwise transfer, before the blockwise transfer
     * state is discarded.
     */
    @Parameter
    @Optional(defaultValue= "300000")
    @Summary(" The maximum amount of time in milliseconds [ms], allowed between transfers of individual blocks in a blockwise transfer, before the blockwise transfer state is discarded.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public Integer blockwiseStatusLifetime= null;

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.VisitableConfig#accept(nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor)
     */
    @Override
    public void accept( ConfigVisitor visitor )
    {
        visitor.visit( this );
    }
}
