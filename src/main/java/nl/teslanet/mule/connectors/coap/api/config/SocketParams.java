/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2025 (teslanet.nl) Rogier Cobben
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
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Socket configuration parameters.
 */
public class SocketParams implements VisitableConfig
{
    /**
     * The hostname or IP address the CoAP endpoint binds to. If none is given
     * anyLocalAddress is used (typically 0.0.0.0 or ::0)
     */
    @Parameter
    @Optional
    @Summary(
        "The hostname or IP address the CoAP endpoint binds to. If empty anyLocalAddress is used (typically 0.0.0.0 or ::0)"
    )
    @Example( value= "0.0.0.0" )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String bindToHost= null;

    /**
     * The port the CoAP endpoint binds to. 
     * When empty a free ephemeral port will be used, which is usual for client endpoints.
     * A server endpoint usually binds to one of the standard CoAP ports 5683 (coap) and 5684 (coaps), or to another predetermined port value. 
     */
    @Parameter
    @Optional
    @Summary(
        "The port the CoAP endpoint binds to.\n"
            + "When empty a free ephemeral port will be used, which is usual for client endpoints.\n"
            + "A server endpoint usually binds to one of the standard CoAP ports 5683 (coap) and 5684 (coaps), or to another predetermined port value."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer bindToPort= null;

    /**
     * Receive buffer size [bytes].
     */
    @Parameter
    @Optional
    @Summary( value= "Receive buffer size [bytes]. \nThe size is os-defined by default and must be at least 64 bytes." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer receiveBuffer= null;

    /**
     * Send buffer size [bytes].
     */
    @Parameter
    @Optional
    @Summary( value= "Send buffer size [bytes]. \nThe size is os-defined by default and must be at least 64 bytes." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer sendBuffer= null;

    /**
     * Reuse address when true.
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @Summary( value= "Reuse address when true." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean reuseAddress= false;

    /**
     * Accept visitor.
     */
    @Override
    public void accept( ConfigVisitor visitor ) throws ConfigException
    {
        visitor.visit( this );
    }
}
