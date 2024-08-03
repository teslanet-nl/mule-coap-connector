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
package nl.teslanet.mule.connectors.coap.api.options;


import java.util.List;

import nl.teslanet.mule.connectors.coap.api.entity.EntityTagAttribute;
import nl.teslanet.mule.connectors.coap.api.query.QueryParamAttribute;


/**
 * The CoAP option parameters of a request.
 *
 */
public class RequestOptionsAttributes
{
    /**
     * Execute request only if the resource already exists.
     */
    protected boolean ifExists= false;

    protected List< EntityTagAttribute > ifMatch= null;

    protected String uriHost= null;

    protected List< EntityTagAttribute > etags= null;

    protected boolean ifNoneMatch= false;

    protected Integer uriPort= null;

    protected List< String > uriPath= null;

    protected Integer contentFormat= null;

    protected List< QueryParamAttribute > uriQuery;

    protected Integer accept= null;

    protected boolean provideResponseSize= false;

    protected String proxyUri= null;

    protected String proxyScheme= null;

    protected Integer requestSize= null;

    protected Integer observe= null;

    protected List< OtherOptionAttribute > other= null;

    /**
     * @return The ifExists option.
     */
    public boolean isIfExists()
    {
        return ifExists;
    }

    /**
     * This method always returns null. 
     * Use getIfMatchValue instead.
     * @return null.
     */
    public List< EntityTagAttribute > getIfMatch()
    {
        return ifMatch;
    }

    /**
     * This method always returns null. 
     */
    public String getUriHost()
    {
        return uriHost;
    }

    /**
     * This method always returns null. 
     * Use getEtagsValue instead.
     * @return null.
     */
    public List< EntityTagAttribute > getEtags()
    {
        return etags;
    }

    /**
     * @return the ifNoneMatch value
     */
    public boolean isIfNoneMatch()
    {
        return ifNoneMatch;
    }

    /**
     * This method always returns null. 
     */
    public Integer getUriPort()
    {
        return uriPort;
    }

    /**
     * This method always returns null. 
     */
    public List< String > getUriPath()
    {
        return uriPath;
    }

    /**
     * @return The contentFormat option.
     */
    public Integer getContentFormat()
    {
        return contentFormat;
    }

    /**
     * @return The uri query.
     */
    public List< QueryParamAttribute > getUriQuery()
    {
        return uriQuery;
    }

    /**
     * @return the accept
     */
    public Integer getAccept()
    {
        return accept;
    }

    /**
     * @return The true when Size2 option is requested, otherwise null.
     */
    public boolean isProvideResponseSize()
    {
        return provideResponseSize;
    }

    /**
     * @return the proxyUri
     */
    public String getProxyUri()
    {
        return proxyUri;
    }

    /**
     * @return the proxyScheme
     */
    public String getProxyScheme()
    {
        return proxyScheme;
    }

    /**
     * @return The Size1 option if present, otherwise null.
     */
    public Integer getRquestSize()
    {
        return requestSize;
    }

    /**
     * This method always returns null. 
     */
    public Integer getObserve()
    {
        return observe;
    }

    /**
     * @return the other request options.
     */
    public List< OtherOptionAttribute > getOther()
    {
        return other;
    }
}
