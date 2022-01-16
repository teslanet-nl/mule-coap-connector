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


import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import nl.teslanet.mule.connectors.coap.api.options.ResponseOptionsAttributes;


/**
 * The attributes of a CoAP response that was received from a server.
 *
 */
public class CoapResponseAttributes
{
    /**
     * The CoAP request code that was issued.
     */
    protected String requestCode= null;

    /**
     * When true, the request issued was confirmable.
     */
    protected boolean confirmable= false;

    /**
     * The client address the request was issued from. 
     */
    protected String localAddress= null;

    /**
     * The path of the resource the request was issued on. 
     */
    protected String requestPath= null;

    /**
     * The query parameters of the request.
     */
    protected String requestQuery= null;

    /**
     * The address of the server that issued the response.
     */
    protected String remoteAddress= null;

    /**
     * True when response is received and indicates success.
     */
    protected boolean success= false;

    /**
     * The CoAP response code of the server response.
     */
    protected String responseCode= null;

    /**
     * True when response is a notification.
     */
    protected boolean notification= false;

    /**
     * The uri of the resource that has been created. 
     */
    protected String locationUri= null;

    /**
     * The CoAP options that accompanied the response.
     */
    protected ResponseOptionsAttributes options= null;

    /**
     * @return The requestCode
     */
    public String getRequestCode()
    {
        return requestCode;
    }

    /**
     * @return the confirmable
     */
    public Boolean getConfirmable()
    {
        return confirmable;
    }

    /**
     * @return the localAddress
     */
    public String getLocalAddress()
    {
        return localAddress;
    }

    /**
     * @return The request path.
     */
    public String getRequestPath()
    {
        return requestPath;
    }

    /**
     * @return The request query.
     */
    public String getRequestQuery()
    {
        return requestQuery;
    }

    /**
     * @return the remoteAddress
     */
    public String getRemoteAddress()
    {
        return remoteAddress;
    }

    /**
     * @return the success
     */
    public boolean isSuccess()
    {
        return success;
    }

    /**
     * @return the notification
     */
    public boolean isNotification()
    {
        return notification;
    }

    /**
     * @return the responseCode
     */
    public String getResponseCode()
    {
        return responseCode;
    }

    /**
     * The location of a created resource.
     * @return the location Uri derived from the location options.
     */
    public String getLocationUri()
    {
        return locationUri;
    }

    /**
     * @return The options
     */
    public ResponseOptionsAttributes getOptions()
    {
        return options;
    }

    /**
     * Get the string representation.
     */
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString( this, MULTI_LINE_STYLE );
    }
}
