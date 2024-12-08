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


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Configuration of notification parameters
 *
 */
public class NotificationParams implements VisitableConfig
{
    /**
     * The maximum number of observes supported on the CoAP server endpoint.
     * A negative value indicates unlimited.
     */
    @Parameter
    @Optional( defaultValue= "50000" )
    @Summary( "The maximum number of observes supported on the CoAP server endpoint.\nA negative value indicates unlimited." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer maxObserves= null;

    /**
     * The time that may pass sending only Non-Confirmable
     * notifications to an observing client. After this period the first
     * notification will be Confirmable to verify the client is listening. When this
     * notification isn't acknowledged, the CoAP relation is considered stale and
     * removed.
     */
    @Parameter
    @Optional( defaultValue= "2m" )
    @Summary(
        "The time that may pass sending only Non-Confirmable notifications to an observing client. \nAfter this period the first notification will be Confirmable to verify the client is listening."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String checkIntervalTime= null;

    /**
     * The maximum number of notifications that may pass before a Confirmable notification must be sent to an
     * observing client, to verify that this client is listening. When this
     * notification isn't acknowledged, the CoAP relation is considered stale and
     * removed.
     */
    @Parameter
    @Optional( defaultValue= "100" )
    @Summary( " The maximum number of notifications that may pass before a Confirmable notification must be sent to an observing client, to verify that this client is listening." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer checkIntervalCount= null;

    /**
     * The time a client waits for re-registration after Max-Age is expired.
     */
    @Parameter
    @Optional( defaultValue= "2s" )
    @Summary( "The time a client waits for re-registration after Max-Age is expired." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String reregistrationBackoff= null;

    /**
     * Accept visitor.
     */
    @Override
    public void accept( ConfigVisitor visitor ) throws ConfigException
    {
        visitor.visit( this );
    }
}
