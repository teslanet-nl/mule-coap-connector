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
package nl.teslanet.mule.connectors.coap.api.config.endpoint;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Reference to a global enpoint
 */
public class GlobalEndpoint
{
//    @ParameterGroup(name= "Set one reference only.")
//    @Summary(value= "Set othe reference to a glocal endpoint.")
//    private EndpointReferenceGroup endpointReferenceGroup;
//
//    /**
//     * @return the endpoint reference
//     */
//    public EndpointReferenceGroup getEndpointReference()
//    {
//        return endpointReferenceGroup;
//    }
//
//    /**
//     * @param endpointReferenceGroup the endpoint reference to set
//     */
//    public void setEndpointReference( EndpointReferenceGroup endpointReferenceGroup )
//    {
//        this.endpointReferenceGroup= endpointReferenceGroup;
//    }
    
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= true, allowInlineDefinition= false)
    @Summary(value= "Global endpoint the server uses.")
    @Placement(order= 1, tab= "Endpoint")
    private AbstractEndpoint endpoint;

    /**
     * @return the endpoint
     */
    public AbstractEndpoint getEndpoint()
    {
        return endpoint;
    }

    /**
     * @param endpoint the endpoint to set
     */
    public void setEndpoint( AbstractEndpoint endpoint )
    {
        this.endpoint= endpoint;
    }

}
