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
package nl.teslanet.mule.connectors.coap.internal.client;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * The configured defaults of a CoAP proxy.
 *
 */
public class ProxyConfig
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
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @Placement( tab= "Advanced" )
    @Summary( "The default proxy-Scheme Option, that is used to make a request to a forward-proxy.\nUse either proxy-scheme or proxy-host and proxy-port." )
    private String proxyScheme= null;

    /**
     * RFC 7252: The Proxy-Uri Option is used to make a request to a forward-proxy.
     * 
     * @see <a href=
     *      "https://tools.ietf.org/html/rfc7252#section-5.10.2">IETF RFC 7252 -
     *      5.10.2. Proxy-Uri and Proxy-Scheme</a>
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @Placement( tab= "Advanced" )
    @Summary( "The default proxy host Option, that  is used to make a request to a forward-proxy.\nUse either proxy-scheme or proxy-host and proxy-port." )
    private String proxyHost= null;

    /**
     * RFC 7252: The Proxy-Uri Option is used to make a request to a forward-proxy.
     * 
     * @see <a href=
     *      "https://tools.ietf.org/html/rfc7252#section-5.10.2">IETF RFC 7252 -
     *      5.10.2. Proxy-Uri and Proxy-Scheme</a>
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @Placement( tab= "Advanced" )
    @Summary( "The default proxy port Option, that is used to make a request to a forward-proxy.\nUse either proxy-scheme or proxy-host and proxy-port." )
    private String proxyPort= null;

    /**
     * @return the proxyScheme
     */
    public String getProxyScheme()
    {
        return proxyScheme;
    }

    /**
     * @param proxyScheme the proxyScheme to set
     */
    public void setProxyScheme( String proxyScheme )
    {
        this.proxyScheme= proxyScheme;
    }

    /**
     * @return the proxyHost
     */
    public String getProxyHost()
    {
        return proxyHost;
    }

    /**
     * @param proxyHost the proxyHost to set
     */
    public void setProxyHost( String proxyHost )
    {
        this.proxyHost= proxyHost;
    }

    /**
     * @return the proxyPort
     */
    public String getProxyPort()
    {
        return proxyPort;
    }

    /**
     * @param proxyPort the proxyPort to set
     */
    public void setProxyPort( String proxyPort )
    {
        this.proxyPort= proxyPort;
    }
}
