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
package nl.teslanet.mule.connectors.coap.api;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Configuration of a Multicast group.
 */
@Alias("multicast-group")
public class MulticastGroupConfig
{
    /**
    * The name of the resource that will be used to identify it in CoAP uri's.
    */
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Summary("The multicast group. This can be the CoAP broadcast address '224.0.1.187' for IPv4, 'FF0X::FD' for IPv6, or a CoAP IPv6 multicast address.")
    @Example("224.0.1.187")
    public String group;

    /**
    * The name of the resource that will be used to identify it in CoAP uri's.
    */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Summary("The network interface to use for this multicast group.")
    @Example("eth0")
    public String networkInterface;

    public MulticastGroupConfig( )
    {
        //NOOP
    }
    
    public MulticastGroupConfig( String group, String networkInterface )
    {
        this.group= group;
        this.networkInterface= networkInterface;
    }

    /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
    public String toString()
    {
        return "MulticastGroupConfig { " + group + ( networkInterface != null ? "|" + networkInterface + " }" : " }");
    }
}
