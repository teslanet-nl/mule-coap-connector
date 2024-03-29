/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2022 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.internal.attributes;


import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import nl.teslanet.mule.connectors.coap.api.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultResponseOptionsAttributes;


/**
 * The attributes of a CoAP response that was received from a server.
 *
 */
public class DefaultResponseAttributes extends CoapResponseAttributes
{
    /**
     * @param requestType the requestType to set
     */
    public void setRequestType( String requestType )
    {
        this.requestType= requestType;
    }

    /**
     * @param requestCode the requestCode to set
     */
    public void setRequestCode( String requestCode )
    {
        this.requestCode= requestCode;
    }

    /**
     * @param localAddress the localAddress to set
     */
    public void setLocalAddress( String localAddress )
    {
        this.localAddress= localAddress;
    }

    /**
     * @param requestUri The request uri to set.
     */
    public void setRequestUri( String requestUri )
    {
        this.requestUri= requestUri;
    }

    /**
     * @param remoteAddress the remoteAddress to set
     */
    public void setRemoteAddress( String remoteAddress )
    {
        this.remoteAddress= remoteAddress;
    }

    /**
     * @param notification the notification to set
     */
    public void setNotification( boolean notification )
    {
        this.notification= notification;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess( boolean success )
    {
        this.success= success;
    }

    /**
     * @param responseType the responseCode to set
     */
    public void setResponseType( String responseType )
    {
        this.responseType= responseType;
    }

    /**
     * @param responseCode the responseCode to set
     */
    public void setResponseCode( String responseCode )
    {
        this.responseCode= responseCode;
    }

    /**
     * Set the location of a created resource.
     */
    public void setLocationUri( String locationUri )
    {
        this.locationUri= locationUri;
    }

    /**
    * @param responseOptions the options to set
    */
    public void setOptions( DefaultResponseOptionsAttributes responseOptions )
    {
        this.options= responseOptions;
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
