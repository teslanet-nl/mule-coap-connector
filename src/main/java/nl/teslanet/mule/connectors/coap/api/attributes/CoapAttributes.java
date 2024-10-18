/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2024 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.api.attributes;


import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;
import nl.teslanet.mule.connectors.coap.api.options.RequestOptionsAttributes;


/**
 * The common attributes of a CoAP request or response that was received.
 *
 */
public class CoapAttributes
{
    /**
     * The CoAP request type that was issued.
     */
    protected String requestType= null;

    /**
     * The CoAP request code that was issued.
     */
    protected String requestCode= null;

    /**
     * The server address the request was issued on.
     */
    protected String localAddress= null;

    /**
     * The uri of the request.
     */
    protected String requestUri= null;

    /**
     * The address of the client that issued the request.
     */
    protected String remoteAddress= null;

    /**
     * The CoAP options that accompanied the request.
     */
    protected RequestOptionsAttributes requestOptions= null;

    /**
     * @return The requestType.
     */
    public String getRequestType()
    {
        return requestType;
    }

    /**
     * @return The requestCode.
     */
    public String getRequestCode()
    {
        return requestCode;
    }

    /**
     * @return The localAddress.
     */
    public String getLocalAddress()
    {
        return localAddress;
    }

    /**
     * @return The request uri.
     */
    public String getRequestUri()
    {
        return requestUri;
    }

    /**
     * @return the requestScheme
     */
    public String getRequestScheme()
    {
        return OptionUtils.EMPTY_STRING;
    }

    /**
     * @return the requestHost
     */
    public String getRequestHost()
    {
        return OptionUtils.EMPTY_STRING;
    }

    /**
     * @return the requestPort
     */
    public int getRequestPort()
    {
        return 0;
    }

    /**
     * @return the requestPath
     */
    public String getRequestPath()
    {
        return OptionUtils.EMPTY_STRING;
    }

    /**
     * @return the requestQuery
     */
    public String getRequestQuery()
    {
        return OptionUtils.EMPTY_STRING;
    }

    /**
     * @return The remoteHost.
     */
    public String getRemoteAddress()
    {
        return remoteAddress;
    }

    /**
     * @return The request options.
     */
    public RequestOptionsAttributes getRequestOptions()
    {
        return requestOptions;
    }
}
