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
 * Parameters of a shared server that will handle requests on behalf of a virtual server.
 *
 */
public class Proxy extends RemoteEndpoint
{
    /**
     * RFC 7252: When a Proxy-Scheme Option is present, the absolute-URI is
     * constructed as follows: a CoAP URI is constructed from the Uri-* options as
     * defined in Section 6.5. In the resulting URI, the initial scheme up to, but
     * not including, the following colon is then replaced by the content of the
     * Proxy-Scheme Option.
     * 
     * @see <a href=
     *      "https://tools.ietf.org/html/rfc7252#section-5.10.2">IETF RFC 7252 - 5.10.2. Proxy-Uri and Proxy-Scheme</a>
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @Placement( order= 121, tab= Placement.ADVANCED_TAB )
    @Summary( "The scheme to use for the request that is forwarded." )
    private String forwardToScheme= null;

    /**
     * @return the forwardToScheme
     */
    public String getForwardToScheme()
    {
        return forwardToScheme;
    }

    /**
     * @param forwardToScheme the forwardToScheme to set
     */
    public void setForwardToScheme( String forwardToScheme )
    {
        this.forwardToScheme= forwardToScheme;
    }
}
