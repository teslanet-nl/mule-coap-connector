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
package nl.teslanet.mule.connectors.coap.api.config.endpoint;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.ExclusiveOptionals;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Exclusive optional specification of the interface to use for outgoing multicast.
 */
@ExclusiveOptionals( isOneRequired= false )
public class OutgoingMulticastConfig
{
    /**
     * Network interface of socket for outgoing multicast traffic. 
     * Alternative to {@link #outgoingAddress}.
     */
    @Parameter
    @Optional
    @Summary( "Network interface of socket for outgoing multicast traffic. Alternative to outgoingAddress." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowInlineDefinition= true, allowReferences= false )
    public String outgoingInterface= null;

    /**
     * Address of network interface for outgoing multicast traffic. 
     * Alternative to {@link #outgoingInterface}.
     */
    @Parameter
    @Optional
    @Summary( "Address of network interface for outgoing multicast traffic. Alternative to outgoingInterface." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowInlineDefinition= true, allowReferences= false )
    public String outgoingAddress= null;
}