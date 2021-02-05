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
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Configuration of which it is not clear whether it is really used.
 *
 */
public class TcpParams implements VisitableConfig
{
    /**
     * The number of TCP worker threads. Default value is 1.
     */
    @Parameter
    @Optional(defaultValue= "1")
    @Summary(value= "The number of TCP worker threads. Default value is 1.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public Integer tcpWorkerThreads= null;

    /**
     * The default tcp connect timeout in milliseconds [ms].
     */
    @Parameter
    @Optional(defaultValue= "10000")
    @Summary(value= "The default tcp connect timeout in milliseconds [ms].")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public Integer tcpConnectTimeout= null;

    /**
     * The default tcp connection idle timeout in seconds [s].
     */
    @Parameter
    @Optional(defaultValue= "10")
    @Summary(value= "The default tcp connection idle timeout in seconds [s].")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public Integer tcpConnectionIdleTimeout= null;

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.VisitableConfig#accept(nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor)
     */
    @Override
    public void accept( ConfigVisitor visitor )
    {
        visitor.visit( this );
    }

}
