/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2026 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.api;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Configuration of a CoAP resource.
 * The configuration is used to construct resources on a CoAP server.
 */
@Alias( "resource" )
public class ResourceConfig extends RootResourceConfig
{
    /**
    * The name of the resource that will be used to identify it in CoAP uri's.
    */
    @Parameter
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The name of the resource that will be used to identify it in CoAP uri's." )
    @Example( "my_resource" )
    private String resourceName;

    /**
    * @return The resourceName
    */
    public String getResourceName()
    {
        return resourceName;
    }

    /**
     * @param resourceName The name to set
     */
    public void setResourceName( String resourceName )
    {
        this.resourceName= resourceName;
    }

    /**
     * The string representation.
     */
    @Override
    public String toString()
    {
        return "CoAP Resource config { " + resourceName + " }";
    }
}
