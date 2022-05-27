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


import org.mule.runtime.extension.api.annotation.param.ParameterGroup;


/**
 * Endpoint configuration
 */
public class Endpoint
{
    @ParameterGroup( name= "Configure one of the endpoint types" )
    private EndpointConfig endpointConfig;

    /**
     * @return The endpoint configuration.
     */
    public EndpointConfig getEndpointConfig()
    {
        return endpointConfig;
    }

    /**
     * @param endpointConfig The endpoint configuration to set.
     */
    public void setEndpointConfig( EndpointConfig endpointConfig )
    {
        this.endpointConfig= endpointConfig;
    }

    /**
     * @return The endpoint that is configured.
     */
    public AbstractEndpoint getEndpoint()
    {
        return endpointConfig.getEndpoint();
    }
}
