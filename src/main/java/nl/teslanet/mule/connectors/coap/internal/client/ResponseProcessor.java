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
package nl.teslanet.mule.connectors.coap.internal.client;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.californium.core.CoapResponse;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;

import nl.teslanet.mule.connectors.coap.api.CoapMessageType;
import nl.teslanet.mule.connectors.coap.api.CoapRequestCode;
import nl.teslanet.mule.connectors.coap.api.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.internal.attributes.AttributeUtils;
import nl.teslanet.mule.connectors.coap.internal.attributes.DefaultResponseAttributes;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidHandlerException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidMessageTypeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidResponseCodeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResponseException;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultResponseOptionsAttributes;
import nl.teslanet.mule.connectors.coap.internal.options.MediaTypeMediator;
import nl.teslanet.mule.connectors.coap.internal.utils.MessageUtils;


/**
 * Response processor that is able to proces responses.
 */
public class ResponseProcessor
{
    /**
     * The registry of response processors
     */
    private static ConcurrentHashMap< String, ResponseProcessor > registry= new ConcurrentHashMap<>();

    /**
     * Name of the handler.
     */
    private String handlerName= null;

    /**
     * The list of response listeners registered on the responsehandler.
     */
    private CopyOnWriteArrayList< SourceCallback< InputStream, CoapResponseAttributes > > listeners= new CopyOnWriteArrayList<>();

    /**
     * @param handlerName
     */
    public ResponseProcessor( String handlerName )
    {
        super();
        this.handlerName= handlerName;
    }

    /**
     * Get reponse processor of the handler..
     * @param handlerName the name of the handler
     * @throws InternalInvalidHandlerExceptions When handler name is invalid
     */
    public static ResponseProcessor getResponseProcessor( String handlerName ) throws InternalInvalidHandlerException
    {
        if ( handlerName == null || handlerName.isEmpty() ) throw new InternalInvalidHandlerException( "empty response handler name not allowed" );
        registry.putIfAbsent( handlerName, new ResponseProcessor( handlerName ) );
        return registry.get( handlerName );
    }

    /**
     * Remove a handler
     * @param handlerName the name of the handler to remove
     * @throws InternalInvalidHandlerException When handler name is invalid
     */
    public static void removeHandler( String handlerName ) throws InternalInvalidHandlerException
    {
        if ( handlerName == null || handlerName.isEmpty() ) throw new InternalInvalidHandlerException( "empty response handler name not allowed" );
        registry.remove( handlerName );
    }

    /**
     * @return The handler name.
     */
    public String getHandlerName()
    {
        return handlerName;
    }

    /**
     * Add listener to process events.
     */
    synchronized void addListener( SourceCallback< InputStream, CoapResponseAttributes > listener )
    {
        listeners.add( listener );
    }

    /**
     * Remove a listener
     */
    void removeListener( SourceCallback< InputStream, CoapResponseAttributes > listener )
    {
        listeners.remove( listener );
    }

    /**
     * Get listeners.
     * @return The listener of this handler.
     */
    public List< SourceCallback< InputStream, CoapResponseAttributes > > getListeners()
    {
        return listeners;
    }

    /**
     * Passes asynchronous response to the muleflow.
     * @param localAddress The local address of the endpoint that has received the response.
     * @param requestUri The Uri of the request that caused the response.
     * @param requestCode The code of the request that caused the response.
     * @param response The coap response to process.
     * @param processor The processor method of the muleflow.
     * @throws InternalResponseException When the received CoAP response contains values that cannot be processed.
     */
    public static void processMuleFlow(
        String localAddress,
        String requestUri,
        CoapMessageType requestType,
        CoapRequestCode requestCode,
        CoapResponse response,
        ResponseProcessor processor
    ) throws InternalResponseException

    {
        DefaultResponseAttributes responseAttributes;
        try
        {
            responseAttributes= createReceivedResponseAttributes( localAddress, requestUri, requestType, requestCode, response );
        }
        catch ( InternalInvalidOptionValueException | InternalInvalidResponseCodeException | InternalInvalidMessageTypeException e )
        {
            throw new InternalResponseException( "cannot proces received response", e );
        }
        for ( SourceCallback< InputStream, CoapResponseAttributes > callback : processor.getListeners() )
        {
            callMuleFlow( responseAttributes, response, callback );
        }
    }

    /**
     * Passes asynchronous response to the muleflow.
     * @param localAddress The local address of the endpoint that has received the response.
     * @param requestUri The Uri of the request that caused the response.
     * @param requestCode The code of the request that caused the response.
     * @param response The coap response to process.
     * @param callback The callback method of the muleflow.
     * @throws InternalResponseException When the received CoAP response contains values that cannot be processed.
     */
    public static void processMuleFlow(
        String localAddress,
        String requestUri,
        CoapMessageType requestType,
        CoapRequestCode requestCode,
        CoapResponse response,
        SourceCallback< InputStream, CoapResponseAttributes > callback
    ) throws InternalResponseException

    {
        DefaultResponseAttributes responseAttributes;
        try
        {
            responseAttributes= createReceivedResponseAttributes( localAddress, requestUri, requestType, requestCode, response );
        }
        catch ( InternalInvalidOptionValueException | InternalInvalidResponseCodeException | InternalInvalidMessageTypeException e )
        {
            throw new InternalResponseException( "cannot proces received response", e );
        }
        callMuleFlow( responseAttributes, response, callback );
    }

    /**
     * Call Muleflow to hand over the response.
     * @param responseAttributes The response attributes giving context o.f the response.
     * @param response The CoAP response received.
     * @param callback The callback that will handle the response.
     */
    private static void callMuleFlow( DefaultResponseAttributes responseAttributes, CoapResponse response, SourceCallback< InputStream, CoapResponseAttributes > callback )
    {
        SourceCallbackContext requestcontext= callback.createContext();
        //not needed yet: requestcontext.addVariable( "CoapExchange", exchange );
        if ( response != null )
        {
            byte[] responsePayload= response.getPayload();
            if ( responsePayload != null )
            {
                callback.handle(
                    Result.< InputStream, CoapResponseAttributes > builder().output( new ByteArrayInputStream( responsePayload ) ).attributes( responseAttributes ).mediaType(
                        MediaTypeMediator.toMediaType( response.getOptions().getContentFormat() )
                    ).build(),
                    requestcontext
                );
            }
            else
            {
                callback.handle(
                    Result.< InputStream, CoapResponseAttributes > builder().attributes( responseAttributes ).mediaType(
                        MediaTypeMediator.toMediaType( response.getOptions().getContentFormat() )
                    ).build(),
                    requestcontext
                );
            }
        }
        else
        {
            callback.handle( Result.< InputStream, CoapResponseAttributes > builder().attributes( responseAttributes ).mediaType( MediaType.ANY ).build(), requestcontext );
        }
    }

    /**
     * Create response attributes that describe a response that is received.
     * @param localAddress The local address of the endpoint that has received the response.
     * @param requestUri The requested resource uri.
     * @param requestCode The request code issued.
     * @param response The received response, if any.
     * @return The attributes created attributes derived from given request and response information.
     * @throws InternalInvalidOptionValueException When an option value is not expected.
     * @throws InternalInvalidResponseCodeException When the responseCode could not be interpreted.
     * @throws InternalInvalidMessageTypeException When the responseType could not be interpreted.
     */
    static DefaultResponseAttributes createReceivedResponseAttributes(
        String localAddress,
        String requestUri,
        CoapMessageType requestType,
        CoapRequestCode requestCode,
        CoapResponse response
    ) throws InternalInvalidMessageTypeException,
        InternalInvalidResponseCodeException,
        InternalInvalidOptionValueException

    {
        DefaultResponseAttributes attributes= new DefaultResponseAttributes();
        attributes.setRequestType( requestType.name() );
        attributes.setRequestCode( requestCode.name() );
        attributes.setLocalAddress( localAddress );
        attributes.setRequestUri( requestUri );
        if ( response == null )
        {
            attributes.setSuccess( false );
        }
        else
        {
            attributes.setSuccess( response.isSuccess() );
            attributes.setResponseType( AttributeUtils.toMessageTypeAttribute( response.advanced().getType() ).name() );
            attributes.setResponseCode( AttributeUtils.toResponseCodeAttribute( response.getCode() ).name() );
            attributes.setRemoteAddress( response.advanced().getSourceContext().getPeerAddress().toString() );
            attributes.setNotification( response.advanced().isNotification() );
            attributes.setOptions( new DefaultResponseOptionsAttributes( response.getOptions() ) );
            attributes.setLocationUri( MessageUtils.uriString( attributes.getOptions().getLocationPath(), attributes.getOptions().getLocationQuery() ) );
        }
        return attributes;
    }

}
