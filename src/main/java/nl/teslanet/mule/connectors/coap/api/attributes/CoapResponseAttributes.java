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
package nl.teslanet.mule.connectors.coap.api.attributes;


import nl.teslanet.mule.connectors.coap.api.options.ResponseOptionsAttributes;


/**
 * The attributes of a CoAP response that was received from a server.
 *
 */
public class CoapResponseAttributes extends Attributes
{
    /**
     * True when response is received and indicates success.
     */
    protected Result result= null;

    /**
     * The CoAP response type of the server response.
     */
    protected String responseType= null;

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
    protected ResponseOptionsAttributes responseOptions= null;

    /**
     * @return the result
     */
    public Result getResult()
    {
        return result;
    }

    /**
     * @return the success
     */
    public boolean isNoResponse()
    {
        return result == Result.NO_RESPONSE;
    }

    /**
     * @return the success
     */
    public boolean isClientError()
    {
        return result == Result.CLIENT_ERROR;
    }

    /**
     * @return the success
     */
    public boolean isServerError()
    {
        return result == Result.SERVER_ERROR;
    }

    /**
     * @return the success
     */
    public boolean isSuccess()
    {
        return result == Result.SUCCESS;
    }

    /**
     * @return the notification
     */
    public boolean isNotification()
    {
        return notification;
    }

    /**
     * @return The responseType
     */
    public String getResponseType()
    {
        return responseType;
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
    public ResponseOptionsAttributes getResponseOptions()
    {
        return responseOptions;
    }
}
