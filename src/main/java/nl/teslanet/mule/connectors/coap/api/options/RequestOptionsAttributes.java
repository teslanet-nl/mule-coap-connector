/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2025 (teslanet.nl) Rogier Cobben
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

import nl.teslanet.mule.connectors.coap.api.binary.BytesValue;
import nl.teslanet.mule.connectors.coap.api.query.QueryParamAttribute;


/**
 * The CoAP option parameters of a request.
 *
 */
public class RequestOptionsAttributes
{
    /**
     * Accept option set on the request. 
     */
    protected Integer accept= null;

    /**
     * Content format of the request payload. 
     */
    protected Integer contentFormat= null;

    /**
     * Resource states known by client that issues the request. (to enable a Valid response.)
     * The list contains the entity tags representing known states.
     */
    protected List< BytesValue > etags= null;

    /**
     * If {@code true}, execute request only if the resource already exists.
     */
    protected boolean ifExists= false;

    /**
     * Execute request only if the resource state is known by client. (to prevent concurrent mutations)
     * The list contains the entity tags representing known states.
     */
    protected List< BytesValue > ifMatch= null;

    /**
     * If {@code true}, execute request only if the resource does not exist.
     */
    protected boolean ifNoneMatch= false;

    /**
     * The request is an observe request (observe=0) or an observe cancel request (observe=1)
     */
    protected Integer observe= null;

    /**
     * The other options of the request.
     */
    protected List< OtherOptionAttribute > other= null;

    /**
     * If {@code true}, client requests to provide size2 option in the response
     * (indicating response payload size).
     */
    protected boolean requireResponseSize= false;

    /**
     * Indicates client interest or disinterest in a response or category of responses.
     */
    protected RequireResponseAttribute requireResponse= null;

    /**
     * The scheme to use when the server is a forwarding proxy.
     */
    protected String proxyScheme= null;

    /**
     * The uri to use when the server is a forwarding proxy.
     */
    protected String proxyUri= null;

    /**
     * The indicated request payload size [bytes].
     */
    protected Integer requestSize= null;

    /**
     * The request uri host.
     */
    protected String uriHost= null;

    /**
     * The request uri port..
     */
    protected Integer uriPort= null;

    /**
     * The request uri host path.
     */
    protected List< String > uriPath= null;

    /**
     * The request uri query parameters.
     */
    protected List< QueryParamAttribute > uriQuery;

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
    public List< BytesValue > getIfMatch()
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
    public List< BytesValue > getEtags()
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
    public boolean isRequireResponseSize()
    {
        return requireResponseSize;
    }

    /**
     * @return the provideResponse
     */
    public RequireResponseAttribute getRequireResponse()
    {
        return requireResponse;
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
