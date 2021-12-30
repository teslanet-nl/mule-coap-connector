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
package nl.teslanet.mule.connectors.coap.internal.attributes;


//TODO RC remove dependency
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import nl.teslanet.mule.connectors.coap.api.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.options.ResponseOptionsAttributes;


/**
 * The attributes of a CoAP response that was received from a server.
 *
 */
public class DefaultResponseAttributes extends CoapResponseAttributes
{
    /**
     * @param requestCode the requestCode to set
     */
    public void setRequestCode( String requestCode )
    {
        this.requestCode= requestCode;
    }

    /**
     * @param confirmable the confirmable to set
     */
    public void setConfirmable( Boolean confirmable )
    {
        this.confirmable= confirmable;
    }

    /**
     * @param localAddress the localAddress to set
     */
    public void setLocalAddress( String localAddress )
    {
        this.localAddress= localAddress;
    }

    /**
     * @param requestUri the requestUri to set
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
     * @param remotePort the remotePort to set
     */
    public void setRemotePort( Integer remotePort )
    {
        this.remotePort= remotePort;
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
     * @param string the responseCode to set
     */
    public void setResponseCode( String string )
    {
        this.responseCode= string;
    }

    /**
    * @param responseOptions the options to set
    */
    public void setOptions( ResponseOptionsAttributes responseOptions )
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
