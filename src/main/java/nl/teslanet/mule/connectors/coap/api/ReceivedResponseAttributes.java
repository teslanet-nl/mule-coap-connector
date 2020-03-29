/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2020 (teslanet.nl) Rogier Cobben
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

import nl.teslanet.mule.connectors.coap.api.options.OptionAttributes;


/**
 * The attributes of a CoAP response that was received from a server.
 *
 */
public class ReceivedResponseAttributes
{
    /**
     * The CoAP request code that was issued.
     */
    private String requestCode= null;

    /**
     * When true, the request issued was confirmable.
     */
    private boolean confirmable= false;

    /**
     * The client address the request was issued from. 
     */
    private String localAddress= null;

    /**
     * The uri of the resource the request was issued on. 
     */
    private String requestUri= null;

    //TODO drop?
    /**
     * The key of the observe relation when the request is an observe request or notification. Null otherwise. 
     */
    private String relation= null;

    /**
     * The address of the server that issued the response.
     */
    private String remoteAddress= null;

    /**
     * The port that was used by the server that issued the response.
     */
    private Integer remotePort= null;

    /**
     * True when response is received and indicates success.
     */
    private boolean success;

    /**
     * True when response is a notification.
     */
    private boolean notification;

    /**
     * The CoAP response code of the server response.
     */
    private String responseCode;

    /**
     * The CoAP options that accompanied the response.
     */
    private OptionAttributes options= new OptionAttributes();

    /**
     * @return the requestCode
     */
    public String getRequestCode()
    {
        return requestCode;
    }

    /**
     * @param requestCode the requestCode to set
     */
    public void setRequestCode( String requestCode )
    {
        this.requestCode= requestCode;
    }

    /**
     * @return the confirmable
     */
    public Boolean getConfirmable()
    {
        return confirmable;
    }

    /**
     * @param confirmable the confirmable to set
     */
    public void setConfirmable( Boolean confirmable )
    {
        this.confirmable= confirmable;
    }

    /**
     * @return the localAddress
     */
    public String getLocalAddress()
    {
        return localAddress;
    }

    /**
     * @param localAddress the localAddress to set
     */
    public void setLocalAddress( String localAddress )
    {
        this.localAddress= localAddress;
    }

    /**
     * @return the requestUri
     */
    public String getRequestUri()
    {
        return requestUri;
    }

    /**
     * @param requestUri the requestUri to set
     */
    public void setRequestUri( String requestUri )
    {
        this.requestUri= requestUri;
    }

    /**
     * @return the relation
     */
    public String getRelation()
    {
        return relation;
    }

    /**
     * @param relation the relation to set
     */
    public void setRelation( String relation )
    {
        this.relation= relation;
    }

    /**
     * @return the remoteAddress
     */
    public String getRemoteAddress()
    {
        return remoteAddress;
    }

    /**
     * @param remoteAddress the remoteAddress to set
     */
    public void setRemoteAddress( String remoteAddress )
    {
        this.remoteAddress= remoteAddress;
    }

    /**
     * @return the remotePort
     */
    public Integer getRemotePort()
    {
        return remotePort;
    }

    /**
     * @param remotePort the remotePort to set
     */
    public void setRemotePort( Integer remotePort )
    {
        this.remotePort= remotePort;
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
     * @return the responseCode
     */
    public String getResponseCode()
    {
        return responseCode;
    }

    /**
     * @param string the responseCode to set
     */
    public void setResponseCode( String string )
    {
        this.responseCode= string;
    }

    /**
     * @return the options
     */
    public OptionAttributes getOptions()
    {
        return options;
    }

    /**
     * @param options the options to set
     */
    public void setOptions( OptionAttributes options )
    {
        this.options= options;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString( this, MULTI_LINE_STYLE );
    }

}
