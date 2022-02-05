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
package nl.teslanet.mule.connectors.coap.api;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * The parameters describing an address of an server to access.
 *
 */
public abstract class AbstractVirtualHostingBuilder extends AbstractAddressBuilder
{
    /**
     * RFC 7252: The Uri-Host Option specifies the Internet host of the resource
      being requested. Explicit Uri-Host and Uri-Port Options are typically used when an endpoint hosts multiple virtual servers.
     * 
     * @see <a href=
     *      "https://datatracker.ietf.org/doc/html/rfc7252/#section-5.10.1">IETF RFC 7252 - 5.10.2. Uri-Host, Uri-Port, Uri-Path, and Uri-Query</a>
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @Placement( order= 111, tab= Placement.ADVANCED_TAB )
    @Summary( "Hostname or ip-address of the endpoint that is hosting the virtual server." )
    private String endpointHost= null;

    /**
     * RFC 7252: The Uri-Port Option specifies the transport-layer port number of the resource.
     * Explicit Uri-Host and Uri-Port Options are typically used when an endpoint hosts multiple virtual servers.
     * 
     * @see <a href=
     *      "https://datatracker.ietf.org/doc/html/rfc7252/#section-5.10.1">IETF RFC 7252 - 5.10.2. Uri-Host, Uri-Port, Uri-Path, and Uri-Query</a>
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @Placement( order= 112, tab= Placement.ADVANCED_TAB )
    @Summary( "Port number of the endpoint that is hosting the virtual server." )
    private Integer endpointPort= null;

    /**
     * @return the endpointHost
     */
    public String getEndpointHost()
    {
        return endpointHost;
    }

    /**
     * @param endpointHost the endpointHost to set
     */
    public void setEndpointHost( String endpointHost )
    {
        this.endpointHost= endpointHost;
    }

    /**
     * @return the endpointPort
     */
    public Integer getEndpointPort()
    {
        return endpointPort;
    }

    /**
     * @param endpointPort the endpointPort to set
     */
    public void setEndpointPort( Integer endpointPort )
    {
        this.endpointPort= endpointPort;
    }
}
