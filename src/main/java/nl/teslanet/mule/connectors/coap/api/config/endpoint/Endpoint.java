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


import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Endpoint configuration
 */
public class Endpoint
{
    @ParameterGroup( name= "Define one of the endpoint types" )
    @Summary( value= "Endpoint configuration." )
    private EndpointGroup endpointGroup;

    /**
     * @return the endpoint group
     */
    public EndpointGroup getEndpointGroup()
    {
        return endpointGroup;
    }

    /**
     * @param endpointGroup the endpoint group to set
     */
    public void setEndpointGroup( EndpointGroup endpointGroup )
    {
        this.endpointGroup= endpointGroup;
    }

    /**
     * @return the endpoint that is configured.
     */
    public AbstractEndpoint getEndpoint()
    {
        return endpointGroup.getEndpoint();
    }
}
