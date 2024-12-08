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

import nl.teslanet.mule.connectors.coap.api.Defs;
import nl.teslanet.mule.connectors.coap.api.attributes.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.attributes.Result;
import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;
import nl.teslanet.mule.connectors.coap.api.options.RequestOptionsAttributes;
import nl.teslanet.mule.connectors.coap.api.options.ResponseOptionsAttributes;
import nl.teslanet.mule.connectors.coap.internal.utils.AttributesStringBuilder;


/**
 * The attributes of a CoAP response that was received from a server.
 *
 */
public class CoapResponseAttributesImpl extends CoapResponseAttributes
{
    /**
     * The request URI object.
     */
    private URI requestUriObject= null;

    /**
     * Default constructor.
     */
    public CoapResponseAttributesImpl()
    {
        super();
    }

    /**
    }
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
     * @param result The result to set
     */
    public void setResult( Result result )
    {
        this.result= result;
    }

    /**
     * @param responseType The responseCode to set
     */
    public void setResponseType( String responseType )
    {
        this.responseType= responseType;
    }

    /**
     * @param responseCode The responseCode to set
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
    * @param responseOptions The options to set
    */
    public void setResponseOptions( ResponseOptionsAttributes responseOptions )
    {
        this.responseOptions= responseOptions;
    }

    /**
     * @return The request uri
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
     * @return The requestScheme
     */
    @Override
    public String getRequestScheme()
    {
        return getOrCreateRequestUri().getScheme();
    }

    /**
     * @return The requestHost
     */
    @Override
    public String getRequestHost()
    {
        return getOrCreateRequestUri().getHost();
    }

    /**
     * @return The requestPort
     */
    @Override
    public int getRequestPort()
    {
        return getOrCreateRequestUri().getPort();
    }

    /**
     * @return The requestPath
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
     * @return The requestQuery
     */
    @Override
    public String getRequestQuery()
    {
        String query= getOrCreateRequestUri().getQuery();
        if ( query != null )
        {
            return query;
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
                requestUriObject= new URI( ( requestUri == null ? OptionUtils.EMPTY_STRING : requestUri ) );
            }
            catch ( URISyntaxException e )
            {
                throw new IllegalStateException( "cannot create URI from request uri", e );
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
        AttributesStringBuilder builder= new AttributesStringBuilder( this );
        builder
            .append( "localAddress", localAddress )
            .append( "remoteAddress", remoteAddress )
            .append( "requestType", requestType )
            .append( "requestCode", requestCode )
            .append( "requestOptions", requestOptions )
            .append( "requestUri", getRequestUri() )
            .append( "notification", notification )
            .append( "responseType", responseType )
            .append( "responseCode", responseCode )
            .append( "locationUri", locationUri )
            .append( "responseOptions", responseOptions )
            .append( "result", result.name() );
        return builder.toString();
    }
}
