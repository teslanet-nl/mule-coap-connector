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
package nl.teslanet.mule.connectors.coap.api.config;


import java.util.List;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.MulticastGroupConfig;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.OutgoingMulticastConfig;


/**
 * Configuration of multi-cast.
 *
 */
public class MulticastParams implements VisitableConfig
{
    /**
     * Parameters for outgoing multicast traffic.
     */
    @ParameterGroup(name= "Outgoing")
    public OutgoingMulticastConfig outgoingMulticastConfig;
    
    /**
     * {@code true}, to disable loopback mode, {@code false}, otherwise.
     */
    @Parameter
    @Optional( defaultValue = "false" )
    @Summary( value= "When True loopback mode is disabled. Default is false." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowInlineDefinition= true, allowReferences= false )
    public boolean disableLoopback= false;

    /**
    * The list of multi-cast groups the endpint supports.
    */
    @Parameter
    @Optional
    @NullSafe
    @Summary(value= "The list of multi-cast groups the endpint supports.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowInlineDefinition= true, allowReferences= false)
    @DisplayName("Join multicast groups")
    public List< MulticastGroupConfig > join;

    /**
     * Default Constructor used by Mule. 
     * Mandatory and Nullsafe params are set by Mule.
     */
    public MulticastParams()
    {
        //NOOP
    }
    
    /**
     * Constructor for manually constructing the endpoint.
     * (Mule uses default constructor and sets Nullsafe params.)
     * @param joinMulticastGroups List of groups to join. 
     */
    public MulticastParams( List< MulticastGroupConfig > joinMulticastGroups )
    {
        join= joinMulticastGroups;
        outgoingMulticastConfig= new OutgoingMulticastConfig();
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.VisitableConfig#accept(nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor)
     */
    @Override
    public void accept( ConfigVisitor visitor )
    {
        visitor.visit( this );
    }
}
