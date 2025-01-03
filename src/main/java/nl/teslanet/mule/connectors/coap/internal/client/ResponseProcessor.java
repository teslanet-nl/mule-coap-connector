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

import nl.teslanet.mule.connectors.coap.api.attributes.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.internal.attributes.CoapRequestOptionsAttributesImpl;
import nl.teslanet.mule.connectors.coap.internal.attributes.CoapResponseAttributesImpl;
import nl.teslanet.mule.connectors.coap.internal.attributes.CoapResponseOptionsAttributesImpl;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidHandlerException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidMessageTypeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidResponseCodeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUriException;
import nl.teslanet.mule.connectors.coap.internal.options.MediaTypeMediator;
import nl.teslanet.mule.connectors.coap.internal.utils.AttributeUtils;
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
        if ( handlerName == null || handlerName.isEmpty() ) throw new InternalInvalidHandlerException(
            "empty response handler name not allowed"
        );
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
        if ( handlerName == null || handlerName.isEmpty() ) throw new InternalInvalidHandlerException(
            "empty response handler name not allowed"
        );
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
     * @param requestBuilder The builder containing request parameters
     * @param response The coap response to process.
     * @param processor The processor method of the muleflow.
     * @throws InternalResponseException When the received CoAP response contains values that cannot be processed.
     */
    public static void processMuleFlow(
        String localAddress,
        CoapRequestBuilder requestBuilder,
        CoapResponse response,
        ResponseProcessor processor
    ) throws InternalResponseException

    {
        CoapResponseAttributesImpl responseAttributes;
        try
        {
            responseAttributes= createReceivedResponseAttributes( localAddress, requestBuilder, response );
        }
        catch (
            InternalInvalidOptionValueException | InternalInvalidResponseCodeException
            | InternalInvalidMessageTypeException | InternalUriException e
        )
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
     * @param requestBuilder The builder containing request parameters
     * @param response The received response.
     * @param callback The callback method of the muleflow.
     * @throws InternalResponseException When the received CoAP response contains values that cannot be processed.
     */
    public static void processMuleFlow(
        String localAddress,
        CoapRequestBuilder requestBuilder,
        CoapResponse response,
        SourceCallback< InputStream, CoapResponseAttributes > callback
    ) throws InternalResponseException

    {
        CoapResponseAttributesImpl responseAttributes;
        try
        {
            responseAttributes= createReceivedResponseAttributes( localAddress, requestBuilder, response );
        }
        catch (
            InternalInvalidOptionValueException | InternalInvalidResponseCodeException
            | InternalInvalidMessageTypeException | InternalUriException e
        )
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
    private static void callMuleFlow(
        CoapResponseAttributesImpl responseAttributes,
        CoapResponse response,
        SourceCallback< InputStream, CoapResponseAttributes > callback
    )
    {
        SourceCallbackContext requestcontext= callback.createContext();
        //not needed yet in request context: addVariable CoapExchange
        if ( response != null )
        {
            byte[] responsePayload= response.getPayload();
            callback
                .handle(
                    Result
                        .< InputStream, CoapResponseAttributes > builder()
                        .output( new ByteArrayInputStream( responsePayload ) )
                        .attributes( responseAttributes )
                        .mediaType( MediaTypeMediator.toMediaType( response.getOptions().getContentFormat() ) )
                        .build(),
                    requestcontext
                );
        }
        else
        {
            callback
                .handle(
                    Result
                        .< InputStream, CoapResponseAttributes > builder()
                        .attributes( responseAttributes )
                        .mediaType( MediaType.ANY )
                        .build(),
                    requestcontext
                );
        }
    }

    /**
     * Create response attributes of a response that is received.
     * @param localAddress The local address of the endpoint that has received the response.
     * @param requestBuilder The builder containing request parameters
     * @param response The received response.
     * @return The created response attributes.
     * @throws InternalInvalidMessageTypeException When the responseType could not be interpreted.
     * @throws InternalInvalidResponseCodeException When the response code is invalid.
     * @throws InternalInvalidOptionValueExceptionrs. When an option value is invalid.
     * @throws InternalUriException When the request uri is invalid.
     */
    static CoapResponseAttributesImpl createReceivedResponseAttributes(
        String localAddress,
        CoapRequestBuilder requestBuilder,
        CoapResponse response
    ) throws InternalInvalidMessageTypeException,
        InternalInvalidResponseCodeException,
        InternalInvalidOptionValueException,
        InternalUriException

    {
        CoapResponseAttributesImpl attributes= new CoapResponseAttributesImpl();
        attributes.setLocalAddress( localAddress );
        attributes.setRequestType( requestBuilder.buildMessageType().name() );
        attributes.setRequestCode( requestBuilder.buildRequestCode().name() );
        attributes.setRequestUriObject( requestBuilder.buildResourceUri() );
        attributes.setRequestOptions( new CoapRequestOptionsAttributesImpl( requestBuilder.buildOptionSet() ) );
        if ( response == null )
        {
            attributes.setResult( nl.teslanet.mule.connectors.coap.api.attributes.Result.NO_RESPONSE );
        }
        else
        {
            attributes.setResult( AttributeUtils.toResult( response.getCode() ) );
            attributes.setResponseType( AttributeUtils.toMessageTypeAttribute( response.advanced().getType() ).name() );
            attributes.setResponseCode( AttributeUtils.toResponseCodeAttribute( response.getCode() ).name() );
            attributes.setRemoteAddress( response.advanced().getSourceContext().getPeerAddress().toString() );
            attributes.setNotification( response.advanced().isNotification() );
            attributes.setResponseOptions( new CoapResponseOptionsAttributesImpl( response.getOptions() ) );
            attributes
                .setLocationUri(
                    MessageUtils
                        .uriString(
                            attributes.getResponseOptions().getLocationPath(),
                            attributes.getResponseOptions().getLocationQuery()
                        )
                );
        }
        return attributes;
    }

}
