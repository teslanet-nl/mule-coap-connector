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


//TODO RC remove dependency
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
     * The uri of the resource the request was issued on. 
     */
    protected String requestUri= null;

    /**
     * The address of the server that issued the response.
     */
    protected String remoteAddress= null;

    /**
     * The port that was used by the server that issued the response.
     */
    protected Integer remotePort= null;

    /**
     * True when response is received and indicates success.
     */
    protected boolean success= false;

    /**
     * True when response is a notification.
     */
    protected boolean notification= false;

    /**
     * The CoAP response code of the server response.
     */
    protected String responseCode= null;

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
     * @return the requestUri
     */
    public String getRequestUri()
    {
        return requestUri;
    }

    /**
     * @return the remoteAddress
     */
    public String getRemoteAddress()
    {
        return remoteAddress;
    }

    /**
     * @return the remotePort
     */
    public Integer getRemotePort()
    {
        return remotePort;
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
     * @param success the success to set
     */
    public void setSuccess( boolean success )
    {
        this.success= success;
    }

    /**
     * @return the responseCode
     */
    public String getResponseCode()
    {
        return responseCode;
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
