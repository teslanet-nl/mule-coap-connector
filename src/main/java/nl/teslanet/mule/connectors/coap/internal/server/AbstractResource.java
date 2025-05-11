/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2024 - 2025 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.internal.server;


import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import nl.teslanet.mule.connectors.coap.api.CoapResponseCode;
import nl.teslanet.mule.connectors.coap.api.attributes.CoapRequestAttributes;
import nl.teslanet.mule.connectors.coap.internal.attributes.CoapRequestAttributesImpl;
import nl.teslanet.mule.connectors.coap.internal.attributes.CoapRequestOptionsAttributesImpl;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidMessageTypeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidRequestCodeException;
import nl.teslanet.mule.connectors.coap.internal.options.MediaTypeMediator;
import nl.teslanet.mule.connectors.coap.internal.utils.AttributeUtils;


/**
 * The AbstractResource class represents a CoAP resource that is active on the server.
 * A ServedResource object handles all requests from clients on the CoAP resource.
 */
public class AbstractResource extends CoapResource
{
    /**
     * This class logger.
     */
    private static final Logger LOGGER= LoggerFactory.getLogger( AbstractResource.class );

    /**
     * Marker for logging error responses.
     */
    protected static final Marker ERROR_RESPONSE_MARKER= MarkerFactory.getMarker( "ERROR_RESPONSE" );

    /**
     * No listener message log format.
     */
    protected static final String NO_LISTENER_LOG_FORMAT= "NO LISTENER for request { {}, {} }";

    /**
     * Regular expression for splitting comma separated values.
     */
    protected static final String CSV_REGEX= ",";

    /**
     * Flag that indicates whether the resource should acknowledge before processing the request.
     */
    protected boolean earlyAck= false;

    /**
     * Resource constructor.
     * @param name The name of the resource.
     * @param visible Resource visibility to customers.
     */
    public AbstractResource( String name, boolean visible )
    {
        super( name, visible );
    }

    /**
     * Generic handler for processing requests.
     * @param callback The callback of the Mule flow that will process the request.  
     * @param exchange The CoAP exchange context of the request.
     * @param defaultCoapResponseCode The response code that will be used when the Mule flow hasn't set one.
     */
    protected void handleRequest(
        SourceCallback< InputStream, CoapRequestAttributes > callback,
        CoapExchange exchange,
        CoapResponseCode defaultCoapResponseCode
    )
    {
        if ( callback == null )
        {
            exchange.respond( ResponseCode.NOT_IMPLEMENTED, "NO LISTENER" );
            if ( LOGGER.isWarnEnabled( ERROR_RESPONSE_MARKER ) )
            {
                try
                {
                    LOGGER
                        .warn(
                            ERROR_RESPONSE_MARKER,
                            NO_LISTENER_LOG_FORMAT,
                            AttributeUtils.toRequestCodeAttribute( exchange.advanced().getCurrentRequest().getCode() ),
                            exchange.advanced().getCurrentRequest().getURI()
                        );
                }
                catch ( InternalInvalidRequestCodeException e )
                {
                    LOGGER
                        .warn(
                            ERROR_RESPONSE_MARKER,
                            NO_LISTENER_LOG_FORMAT,
                            exchange.advanced().getCurrentRequest().getCode(),
                            exchange.advanced().getCurrentRequest().getURI()
                        );
                }
            }
            return;
        }
        if ( earlyAck )
        {
            exchange.accept();
        }
        CoapRequestAttributes requestAttributes;
        try
        {
            requestAttributes= createRequestAttributes( exchange );
        }
        catch ( Exception e1 )
        {
            // cannot process the request when an option is not valid,
            // probably Californium has already dealt with this, so shouldn't occur
            Response response= new Response( ResponseCode.BAD_OPTION );
            response.setPayload( e1.getMessage() );
            exchange.respond( response );
            return;
        }
        SourceCallbackContext requestcontext= callback.createContext();
        requestcontext.addVariable( Server.VARNAME_DEFAULT_RESPONSE_CODE, defaultCoapResponseCode );
        requestcontext.addVariable( Server.VARNAME_COAP_EXCHANGE, exchange );
        //TODO add streaming & blockwise cooperation
        byte[] requestPayload= exchange.getRequestPayload();
        // payload is always initialized, no need to check null
        callback
            .handle(
                Result
                    .< InputStream, CoapRequestAttributes > builder()
                    .output( new ByteArrayInputStream( requestPayload ) )
                    .length( requestPayload.length )
                    .attributes( requestAttributes )
                    .mediaType( MediaTypeMediator.toMediaType( exchange.getRequestOptions().getContentFormat() ) )
                    .build(),
                requestcontext
            );
    }

    /**
     * Create request attributes.
     * @param coapExchange
     * @return The attributes to add to the mule message.
     * @throws InternalInvalidOptionValueException When request options could not be interpreted.
     * @throws InternalInvalidMessageTypeException When request type could not be interpreted.
     * @throws InternalInvalidRequestCodeException When request code could not be interpreted.
     */
    protected CoapRequestAttributesImpl createRequestAttributes( CoapExchange coapExchange )
        throws InternalInvalidOptionValueException,
        InternalInvalidMessageTypeException,
        InternalInvalidRequestCodeException
    {
        Exchange exchange= coapExchange.advanced();
        CoapRequestAttributesImpl attributes= new CoapRequestAttributesImpl();
        attributes
            .setRequestType(
                AttributeUtils.toMessageTypeAttribute( coapExchange.advanced().getRequest().getType() ).name()
            );
        attributes.setRequestCode( AttributeUtils.toRequestCodeAttribute( coapExchange.getRequestCode() ).name() );
        attributes.setLocalAddress( exchange.getEndpoint().getAddress().toString() );
        attributes.setRemoteAddress( coapExchange.getSourceSocketAddress().toString() );
        attributes.setRequestUri( exchange.getRequest().getURI() );
        attributes.setRequestOptions( new CoapRequestOptionsAttributesImpl( coapExchange.getRequestOptions() ) );
        attributes
            .setRelation( ( exchange.getRelation() != null ? exchange.getRelation().getKeyToken().toString() : null ) );
        return attributes;
    }

    /**
     * set the Mule callback for this resource for Get requests.
     */
    public void setGetCallback( SourceCallback< InputStream, CoapRequestAttributes > sourceCallback )
    {
        //NOOP
    }

    /**
     * Get the Mule MessageSource callback for Get requests.
     * @return the callback
     */
    public SourceCallback< InputStream, CoapRequestAttributes > getGetCallback()
    {
        return null;
    }

    /**
     * set the Mule callback for this resource for Post requests.
     */
    public void setPostCallback( SourceCallback< InputStream, CoapRequestAttributes > sourceCallback )
    {
        //NOOP
    }

    /**
     * Get the Mule MessageSource callback for Post requests.
     * @return the callback
     */
    public SourceCallback< InputStream, CoapRequestAttributes > getPostCallback()
    {
        return null;
    }

    /**
     * set the Mule callback for this resource for Put requests.
     */
    public void setPutCallback( SourceCallback< InputStream, CoapRequestAttributes > sourceCallback )
    {
        //NOOP
    }

    /**
     * Get the Mule MessageSource callback for Put requests.
     * @return the callback
     */
    public SourceCallback< InputStream, CoapRequestAttributes > getPutCallback()
    {
        return null;
    }

    /**
     * set the Mule callback for this resource for Delete requests.
     */
    public void setDeleteCallback( SourceCallback< InputStream, CoapRequestAttributes > sourceCallback )
    {
        //NOOP
    }

    /**
     * Get the Mule MessageSource callback for Delete requests.
     * @return the callback
     */
    public SourceCallback< InputStream, CoapRequestAttributes > getDeleteCallback()
    {
        return null;
    }

    /**
     * set the Mule callback for this resource for Fetch requests.
     */
    public void setFetchCallback( SourceCallback< InputStream, CoapRequestAttributes > sourceCallback )
    {
        //NOOP
    }

    /**
     * Get the Mule MessageSource callback for Fetch requests.
     * @return the callback
     */
    public SourceCallback< InputStream, CoapRequestAttributes > getFetchCallback()
    {
        return null;
    }

    /**
     * set the Mule callback for this resource for Patch requests.
     */
    public void setPatchCallback( SourceCallback< InputStream, CoapRequestAttributes > sourceCallback )
    {
        //NOOP    
    }

    /**
     * Get the Mule MessageSource callback for Patch requests.
     * @return the callback
     */
    public SourceCallback< InputStream, CoapRequestAttributes > getPatchCallback()
    {
        return null;
    }

    /**
     * set the Mule callback for this resource for Ipatch requests.
     */
    public void setIpatchCallback( SourceCallback< InputStream, CoapRequestAttributes > sourceCallback )
    {
        //NOOP
    }

    /**
     * Get the Mule MessageSource callback for Ipatch requests.
     * @return the callback
     */
    public SourceCallback< InputStream, CoapRequestAttributes > getIpatchCallback()
    {
        return null;
    }
}
