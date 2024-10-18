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
package nl.teslanet.mule.connectors.coap.internal.attributes;


import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import nl.teslanet.mule.connectors.coap.api.Defs;
import nl.teslanet.mule.connectors.coap.api.attributes.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;
import nl.teslanet.mule.connectors.coap.api.options.RequestOptionsAttributes;
import nl.teslanet.mule.connectors.coap.api.options.ResponseOptionsAttributes;


/**
 * The attributes of a CoAP response that was received from a server.
 *
 */
public class DefaultResponseAttributes extends CoapResponseAttributes
{
    /**
     * The request URI object.
     */
    private URI requestUriObject= null;

    /**
     * @param requestType the requestType to set
     */
    public void setRequestType( String requestType )
    {
        this.requestType= requestType;
    }

    /**
     * @param requestCode The requestCode to set.
     */
    public void setRequestCode( String requestCode )
    {
        this.requestCode= requestCode;
    }

    /**
      * @param localAddress The localAddress to set.
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
     * @param uri The request uri to set.
     */
    public synchronized void setRequestUriObject( URI uri )
    {
        this.requestUriObject= uri;
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
     * @param requestOptionAttributes The options to set.
     */
    public void setRequestOptions( RequestOptionsAttributes requestOptionAttributes )
    {
        this.requestOptions= requestOptionAttributes;
    }

    /**
    * @param responseOptions the options to set
    */
    public void setResponseOptions( ResponseOptionsAttributes responseOptions )
    {
        this.responseOptions= responseOptions;
    }

    /**
     * @return the request uri
     */
    @Override
    public String getRequestUri()
    {
        if ( requestUri != null )
        {
            return requestUri;
        }
        else
        {
            return getOrCreateRequestUri().toASCIIString();
        }
    }

    /**
     * @return the requestScheme
     */
    @Override
    public String getRequestScheme()
    {
        return getOrCreateRequestUri().getScheme();
    }

    /**
     * @return the requestHost
     */
    @Override
    public String getRequestHost()
    {
        return getOrCreateRequestUri().getHost();
    }

    /**
     * @return the requestPort
     */
    @Override
    public int getRequestPort()
    {
        return getOrCreateRequestUri().getPort();
    }

    /**
     * @return the requestPath
     */
    @Override
    public String getRequestPath()
    {
        String path= getOrCreateRequestUri().getPath();
        if ( path != null )
        {
            return path;
        }
        else
        {
            return Defs.COAP_URI_ROOTRESOURCE;
        }
    }

    /**
     * @return the requestQuery
     */
    @Override
    public String getRequestQuery()
    {
        String query= getOrCreateRequestUri().getQuery();
        if ( query != null )
        {
            return getOrCreateRequestUri().getPath();
        }
        else
        {
            return OptionUtils.EMPTY_STRING;
        }
    }

    /**
     * @return  The request uri object.
     */
    private synchronized URI getOrCreateRequestUri()
    {
        if ( requestUriObject == null )
        {
            try
            {
                requestUriObject= new URI( requestUri );
            }
            catch ( URISyntaxException e )
            {
                throw new IllegalStateException( "cannot create URI from request", e );
            }
        }
        return requestUriObject;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString( this, AttributeToStringStyle.getInstance() );
    }
}
