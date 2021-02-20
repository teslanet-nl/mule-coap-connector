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
package nl.teslanet.mule.connectors.coap.internal.client;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Token;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.scheduler.SchedulerConfig;
import org.mule.runtime.api.scheduler.SchedulerService;
import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.Sources;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.RefName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.annotation.param.reference.ConfigReference;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.teslanet.mule.connectors.coap.api.ReceivedResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.RequestBuilder.CoAPRequestCode;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.Endpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.UDPEndpoint;
import nl.teslanet.mule.connectors.coap.api.error.UriException;
import nl.teslanet.mule.connectors.coap.api.options.RequestOptions;
import nl.teslanet.mule.connectors.coap.api.query.AbstractQueryParam;
import nl.teslanet.mule.connectors.coap.internal.CoAPConnector;
import nl.teslanet.mule.connectors.coap.internal.OperationalEndpoint;
import nl.teslanet.mule.connectors.coap.internal.attributes.AttributeUtils;
import nl.teslanet.mule.connectors.coap.internal.exceptions.EndpointConstructionException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalClientErrorResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalEndpointException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidHandlerNameException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidObserverException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidRequestCodeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidResponseCodeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalNoResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalRequestException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalServerErrorResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUnexpectedResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUriException;
import nl.teslanet.mule.connectors.coap.internal.options.CoAPOptions;
import nl.teslanet.mule.connectors.coap.internal.options.MediaTypeMediator;
import nl.teslanet.mule.connectors.coap.internal.utils.MessageUtils;


/**
 * The Client is used to send requests to resources on CoAP servers and/or to observe these. 
 * The CoAP server address (host, port) configured here is default for the requests using 
 * this client. The request can override if necessary.
 */
@Configuration(name= "client")
@Sources(value= { Observer.class, ResponseHandler.class })
@Operations(ClientOperations.class)
public class Client implements Initialisable, Disposable, Startable, Stoppable
{
    /**
     * Logger of the class
     */
    private static final Logger LOGGER= LoggerFactory.getLogger( Client.class.getCanonicalName() );

    @RefName
    private String clientName= null;

    /**
     * Injected Scheduler service.
     */
    @Inject
    private SchedulerService schedulerService;

    /**
     * Injected scheduler configuration.
     */
    @Inject
    private SchedulerConfig schedulerConfig;

    /**
     * Endpoint configuration to use for the client.
     */
    @Parameter
    @Optional
    @ConfigReference(namespace= "COAP", name= "UDP_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "DTLS_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "TCP_CLIENT_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "TCP_SERVER_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "TLS_CLIENT_ENDPOINT")
    @ConfigReference(namespace= "COAP", name= "TLS_SERVER_ENDPOINT")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @Placement(order= 1, tab= "Endpoint")
    @ParameterDsl(allowReferences= true, allowInlineDefinition= true)
    private Endpoint endpoint= null;

    /**
     * The actual URI scheme
     */
    private String scheme;

    //TODO add default confirmable, path and query?

    /**
     * The default hostname or ip of the server to address. 
     * Can be overridden by the host setting on operations.
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @Summary("The default hostname or ip of the server to address. Can be overridden by the host setting on operations.")
    private String host= null;

    /**
     * The default port the server is listening on.
     * Can be overridden by the port setting on the operation. 
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @Summary("The default port the server is listening on. Can be overridden by the port setting on operations.")
    private Integer port= null;

    /**
     * When {@code true} synchronous operations will throw an exception when CoAP error codes are received or a timeout has occurred. 
     * Otherwise a result is returned in these cases with attribute.success set to {@code False}.
     */
    @Parameter
    @Optional(defaultValue= "true")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @Summary("When true, synchronous operations will throw an exception when CoAP error codes are received or a timeout has occurred. Otherwise a result is returned in these cases, with attribute.success set to false.")
    private boolean throwExceptionOnErrorResponse= true;

    /**
     * The endpoint the client uses.
     */
    private OperationalEndpoint operationalEndpoint= null;

    /**
     * The Californium CoAP client instance.
     */
    private CoapClient coapClient= null;

    /**
     * observe relations contains the active observers.
     */
    private ConcurrentHashMap< String, ObserveRelation > observeRelations= new ConcurrentHashMap<>();

    /**
     * The list of response handlers
     */
    private ConcurrentHashMap< String, SourceCallback< InputStream, ReceivedResponseAttributes > > handlers= new ConcurrentHashMap<>();

    /* (non-Javadoc)
     * @see org.mule.runtime.api.lifecycle.Startable#start()
     */
    @Override
    public void start() throws MuleException
    {
        coapClient= new CoapClient();
        coapClient.setEndpoint( operationalEndpoint.getCoapEndpoint() );
        LOGGER.info( this + " connected to " + operationalEndpoint );
        LOGGER.info( this + " started." );
    }

    /* (non-Javadoc)
     * @see org.mule.runtime.api.lifecycle.Stoppable#stop()
     */
    @Override
    public void stop() throws MuleException
    {
        for ( ObserveRelation relation : observeRelations.values() )
        {
            relation.stop();
        }
        observeRelations.clear();
        handlers.clear();
        coapClient.shutdown();
        coapClient= null;
        LOGGER.info( this + " stopped." );
    }

    /* (non-Javadoc)
     * @see org.mule.runtime.api.lifecycle.Initialisable#initialise()
     */
    @Override
    public void initialise() throws InitialisationException
    {
        CoAPConnector.setSchedulerService( schedulerService, schedulerConfig );
        if ( endpoint == null )
        {
            //user wants default endpoint
            endpoint= new UDPEndpoint( this.toString() );
        }
        if ( endpoint.configName == null )
        {
            //inline endpoint will get this as name
            endpoint.configName= this.toString();
        }
        try
        {
            operationalEndpoint= OperationalEndpoint.getOrCreate( this, endpoint );
        }
        catch ( EndpointConstructionException e )
        {
            throw new InitialisationException( e, this );
        }
        scheme= operationalEndpoint.getCoapEndpoint().getUri().getScheme();
        LOGGER.info( this + " initialised." );
    }

    /* (non-Javadoc)
     * @see org.mule.runtime.api.lifecycle.Disposable#dispose()
     */
    @Override
    public void dispose()
    {
        OperationalEndpoint.disposeAll( this );
        operationalEndpoint= null;
        LOGGER.info( this + " disposed." );
    }

    /**
     * @return the clientName
     */
    public String getClientName()
    {
        return clientName;
    }

    /**
     * @return the coapClient
     */
    CoapClient getCoapClient()
    {
        return coapClient;
    }

    /**
     * Add handler to process responses.
     * @param handlerName the name of the handler
     * @param callback the source callback that will process the responses
     * @throws InternalInvalidHandlerNameException 
     */
    synchronized void addHandler( String handlerName, SourceCallback< InputStream, ReceivedResponseAttributes > callback ) throws InternalInvalidHandlerNameException
    {
        if ( handlerName == null || handlerName.isEmpty() ) throw new InternalInvalidHandlerNameException( "empty response handler name not allowed" );
        if ( handlers.get( handlerName ) != null ) throw new InternalInvalidHandlerNameException( "responsehandler name { " + handlerName + " } not unique" );
        handlers.put( handlerName, callback );
    }

    /**
     * get handler to process responses with given name..
     * @param handlerName the name of the handler
     * @return The handler.
     * @throws InternalInvalidHandlerNameException When the handler with given name could not be found. 
     */
    SourceCallback< InputStream, ReceivedResponseAttributes > getHandler( String handlerName ) throws InternalInvalidHandlerNameException
    {
        if ( handlerName == null || handlerName.isEmpty() ) throw new InternalInvalidHandlerNameException( "empty response handler name is not allowed" );
        SourceCallback< InputStream, ReceivedResponseAttributes > handler= handlers.get( handlerName );
        if ( handler == null ) throw new InternalInvalidHandlerNameException( "response handler { " + handlerName + " } not found." );
        return handler;
    }

    /**
     * Remove a handler
     * @param handlerName the name of the handler to remove
     */
    void removeHandler( String handlerName )
    {
        handlers.remove( handlerName );
    }

    /**
     * Add an observer entry defined by the uri that is observed.
     * @param uri The observed uri.
     * @param relation The observe relation.
     * @throws InternalInvalidObserverException when uri is already observed
     * @throws InternalUriException when uri is not valid
     */
    synchronized void addRelation( String uri, ObserveRelation relation ) throws InternalInvalidObserverException, InternalUriException
    {
        if ( uri == null || uri.isEmpty() )
        {
            throw new InternalUriException( "empty uri is invalid: { " + uri + " }" );
        }
        if ( observeRelations.get( uri ) != null )
        {
            throw new InternalInvalidObserverException( "observer already exists: { " + uri + " }" );
        }
        observeRelations.put( uri, relation );
    }

    /**
     * Get an observer entry defined by the uri that is observed.
     * @param uri The observed uri.
     * @return The relation of the observer, null when no observer is found.
     * @throws InternalUriException when uri is not valid
     */
    ObserveRelation getRelation( String uri ) throws InternalUriException
    {
        if ( uri == null || uri.isEmpty() )
        {
            throw new InternalUriException( "empty uri is invalid: { " + uri + " }" );
        }
        return observeRelations.get( uri );
    }

    /**
     * Remove an observe entry.
     * @param uri The observed uri.
     */
    synchronized void removeRelation( String uri )
    {
        observeRelations.remove( uri );
    }

    /**
     * Get the relations.
     * @param uri The observed uri.
     * @return 
     */
    Map< String, ObserveRelation > getRelations()
    {
        return observeRelations;
    }

    /**
     * Get a querystring containing containing query parameters that can be use as part of a an Uri-string.
     * @param list List of query parameters.
     * @return The querystring. 
     */
    String toQueryString( List< ? extends AbstractQueryParam > list )
    {
        if ( list == null ) return null;

        StringBuilder builder= new StringBuilder();
        boolean first;
        Iterator< ? extends AbstractQueryParam > it;
        for ( first= true, it= list.iterator(); it.hasNext(); )
        {
            AbstractQueryParam queryParamWithExpressionSupport= it.next();
            if ( queryParamWithExpressionSupport.hasKey() )
            {
                if ( first )
                {
                    builder.append( "&" );
                    first= false;
                }
                builder.append( queryParamWithExpressionSupport.getKey() );
                if ( queryParamWithExpressionSupport.hasValue() )
                {
                    builder.append( "=" );
                    builder.append( queryParamWithExpressionSupport.getValue() );
                }
            }
        }
        return builder.toString();
    }

    /**
     * Passes asynchronous response to the muleflow  
     * @param response The coap response to process
     * @param callback The callback method of the muleflow.
     * @param requestAttributes The attributes of the originating request.
     * @throws InternalResponseException When the received CoAP response contains values that cannot be processed.
     */
    void processMuleFlow( String requestUri, CoAPRequestCode requestCode, CoapResponse response, SourceCallback< InputStream, ReceivedResponseAttributes > callback )
        throws InternalResponseException

    {
        ReceivedResponseAttributes responseAttributes;
        try
        {
            responseAttributes= createReceivedResponseAttributes( requestUri, requestCode, response );
        }
        catch ( InternalInvalidOptionValueException | InternalInvalidResponseCodeException e )
        {
            throw new InternalResponseException( "cannot proces received response", e );
        }
        SourceCallbackContext requestcontext= callback.createContext();
        //not needed yet: requestcontext.addVariable( "CoapExchange", exchange );
        if ( response != null )
        {
            byte[] responsePayload= response.getPayload();
            if ( responsePayload != null )
            {
                callback.handle(
                    Result.< InputStream, ReceivedResponseAttributes > builder().output( new ByteArrayInputStream( responsePayload ) ).attributes( responseAttributes ).mediaType(
                        MediaTypeMediator.toMediaType( response.getOptions().getContentFormat() ) ).build(),
                    requestcontext );
            }
            else
            {
                callback.handle(
                    Result.< InputStream, ReceivedResponseAttributes > builder().attributes( responseAttributes ).mediaType(
                        MediaTypeMediator.toMediaType( response.getOptions().getContentFormat() ) ).build(),
                    requestcontext );
            }
        }
        else
        {
            callback.handle( Result.< InputStream, ReceivedResponseAttributes > builder().attributes( responseAttributes ).mediaType( MediaType.ANY ).build(), requestcontext );
        }
    }

    // TODO add custom timeout
    /**
     * Issue a request on a CoAP resource residing on a server.
     * When throwExceptionOnErrorResponse is {@code true } an appropriate exception is thrown.
     * @param confirmable indicating the requst must be confirmed
     * @param requestCode indicating the request type to issue
     * @param uri addresses the resource of the server for which the request is intended 
     * @param requestPayload is the contents of the request
     * @param forcePayload when true given payload will also be sent when not appropriate for the CoAP message, otherwise the payload will be ignored.
     * @param options the CoAP options to add to the request
     * @param handlerName when a handler is specified the response will be handled asynchronously
     * @return The result of the request including payload and options.
     * @throws InternalInvalidHandlerNameException When the handlerName is not null but does not reference an existing handler. 
     * @throws InternalRequestException When the Request could not be issued.
     * @throws InternalResponseException When the received response appears to be invalid and cannot be processed.
     * @throws InternalEndpointException When CoAP communication failed.
     * @throws InternalInvalidRequestCodeException When the request code has invalid value.
     * @throws InternalServerErrorResponseException When response indicates server error.
     * @throws InternalInvalidResponseCodeException When response indicates other error.
     * @throws InternalClientErrorResponseException When response indicates client error.
     * @throws InternalNoResponseException When timeout has occurred.
     */
    Result< InputStream, ReceivedResponseAttributes > doRequest(
        boolean confirmable,
        CoAPRequestCode requestCode,
        String uri,
        TypedValue< Object > requestPayload,
        boolean forcePayload,
        RequestOptions options,
        String handlerName ) throws InternalInvalidHandlerNameException,
        InternalRequestException,
        InternalResponseException,
        InternalEndpointException,
        InternalInvalidRequestCodeException,
        InternalNoResponseException,
        InternalClientErrorResponseException,
        InternalInvalidResponseCodeException,
        InternalServerErrorResponseException
    {
        Result< InputStream, ReceivedResponseAttributes > result= null;
        CoapHandler handler= null;
        Request request= new Request( AttributeUtils.toRequestCode( requestCode ), ( confirmable ? Type.CON : Type.NON ) );
        request.setURI( uri );
        if ( requestPayload.getValue() != null )
        {
            boolean sendPayload;
            switch ( request.getCode() )
            {
                case GET:
                case DELETE:
                    if ( forcePayload )
                    {
                        request.setUnintendedPayload();
                        sendPayload= true;
                    }
                    else
                    {
                        sendPayload= false;
                    }
                    break;
                default:
                    sendPayload= true;
                    break;
            }
            if ( sendPayload )
            {
                //TODO add streaming & blockwise cooperation
                try
                {
                    request.setPayload( MessageUtils.toByteArray( requestPayload ) );
                }
                catch ( RuntimeException | IOException e )
                {
                    throw new InternalRequestException( "cannot convert payload to byte[]", e );
                }
                request.getOptions().setContentFormat( MediaTypeMediator.toContentFormat( requestPayload.getDataType().getMediaType() ) );
            }
        }
        try
        {
            CoAPOptions.copyOptions( options, request.getOptions(), false );
        }
        catch ( InternalInvalidOptionValueException e )
        {
            throw new InternalRequestException( "cannot process request options", e );
        }
        if ( handlerName != null )
        {
            SourceCallback< InputStream, ReceivedResponseAttributes > callback;
            callback= getHandler( handlerName );
            handler= createCoapHandler( handlerName, uri, requestCode, callback );
        }
        if ( handler == null )
        {
            // send out synchronous request
            CoapResponse response= null;
            try
            {
                response= coapClient.advanced( request );
            }
            catch ( ConnectorException | IOException e )
            {
                throw new InternalEndpointException( "CoAP request failed", e );
            }
            throwExceptionWhenNeeded( throwExceptionOnErrorResponse, response );
            ReceivedResponseAttributes responseAttributes;
            try
            {
                responseAttributes= createReceivedResponseAttributes( request.getURI(), requestCode, response );
            }
            catch ( InternalInvalidOptionValueException | InternalInvalidResponseCodeException e )
            {
                throw new InternalResponseException( "CoAP response cannot be processed", e );
            }
            if ( response == null )
            {
                result= Result.< InputStream, ReceivedResponseAttributes > builder().output( null ).attributes( responseAttributes ).mediaType( MediaType.ANY ).build();
            }
            else
            {
                byte[] payload= response.getPayload();
                if ( payload != null )
                {
                    result= Result.< InputStream, ReceivedResponseAttributes > builder().output( new ByteArrayInputStream( response.getPayload() ) ).length(
                        payload.length ).attributes( responseAttributes ).mediaType( MediaTypeMediator.toMediaType( response.getOptions().getContentFormat() ) ).build();
                }
                else
                {
                    result= Result.< InputStream, ReceivedResponseAttributes > builder().output( null ).attributes( responseAttributes ).mediaType(
                        MediaTypeMediator.toMediaType( response.getOptions().getContentFormat() ) ).build();
                }
            }
        }
        else
        {
            // asynchronous request
            coapClient.advanced( handler, request );
            // nothing to return
        }
        return result;
    }

    /**
     * Throw an exception when the response is giving cause for.
     * @param response The response to inspect.
     * @throws InternalNoResponseException When timeout has occurred.
     * @throws InternalClientErrorResponseException When response indicates client error.
     * @throws InternalInvalidResponseCodeException When response has invalid code.
     * @throws InternalServerErrorResponseException When response indicates server error.
     * @throws InternalResponseException When response indicates other error.
     */
    private void throwExceptionWhenNeeded( CoapResponse response ) throws InternalNoResponseException,
        InternalClientErrorResponseException,
        InternalInvalidResponseCodeException,
        InternalServerErrorResponseException,
        InternalResponseException
    {
        throwExceptionWhenNeeded( true, response );
    }

    /**
     * Throw an exception when the response is giving cause for, only if an exception is wanted.
     * @param needed When ({@code true} the exception is wanted, otherwise it is not.
     * @param response The response to inspect.
     * @throws InternalNoResponseException When timeout has occurred.
     * @throws InternalClientErrorResponseException When response indicates client error.
     * @throws InternalInvalidResponseCodeException When response has invalid code.
     * @throws InternalServerErrorResponseException When response indicates server error.
     * @throws InternalResponseException When response indicates other error.
     */
    private void throwExceptionWhenNeeded( boolean needed, CoapResponse response ) throws InternalNoResponseException,
        InternalClientErrorResponseException,
        InternalInvalidResponseCodeException,
        InternalServerErrorResponseException,
        InternalResponseException
    {
        if ( needed )
        {
            if ( response == null )
            {
                throw new InternalNoResponseException();
            }
            if ( !response.isSuccess() )
            {
                String message= "Response code received: " + AttributeUtils.toResponseCodeAttribute( response.getCode() );
                if ( ResponseCode.isClientError( response.getCode() ) )
                {
                    throw new InternalClientErrorResponseException( message );
                }
                else if ( ResponseCode.isServerError( response.getCode() ) )
                {
                    throw new InternalServerErrorResponseException( message );
                }
                else
                {
                    throw new InternalResponseException( message );

                }
            }
        }
    }

    /**
     * Create response attributes that describe a response that is received.
     * @param request the request that caused the response.
     * @param response the response that is received from the server.
     * @return the created attributes derived from given request and response.
     * @throws InternalInvalidOptionValueException 
     * @throws InternalInvalidResponseCodeException When responseCode is unknown.
     */
    private ReceivedResponseAttributes createReceivedResponseAttributes( String requestUri, CoAPRequestCode requestCode, CoapResponse response )
        throws InternalInvalidOptionValueException,
        InternalInvalidResponseCodeException
    {
        ReceivedResponseAttributes attributes= new ReceivedResponseAttributes();
        attributes.setRequestCode( requestCode.name() );
        attributes.setLocalAddress( operationalEndpoint.getCoapEndpoint().getAddress().toString() );
        attributes.setRequestUri( requestUri );
        if ( response == null )
        {
            attributes.setSuccess( false );
        }
        else
        {
            attributes.setSuccess( response.isSuccess() );
            attributes.setResponseCode( AttributeUtils.toResponseCodeAttribute( response.getCode() ).name() );
            attributes.setConfirmable( response.advanced().isConfirmable() );
            attributes.setNotification( response.advanced().isNotification() );
            attributes.setRemoteAddress( response.advanced().getSourceContext().getPeerAddress().getHostString() );
            attributes.setRemotePort( response.advanced().getSourceContext().getPeerAddress().getPort() );
            CoAPOptions.copyOptions( response.getOptions(), attributes.getOptions() );
        }
        return attributes;
    }

    /**
     * Create a Handler of CoAP responses.
     * @param uri 
     * @param client  The Coap client that produced the response
     * @param callback The Listening Messageprocessor that needs to be called
     * @param requestCode The coap request code from the request context
     * @return
     */
    private CoapHandler createCoapHandler(
        final String handlerName,
        final String requestUri,
        final CoAPRequestCode requestCode,
        final SourceCallback< InputStream, ReceivedResponseAttributes > callback )
    {
        final Client thisClient= this;
        final String handlerDescription= "Handler { " + thisClient.getClientName() + "::" + handlerName + " } ";

        return new CoapHandler()
            {
                /**
                 * Callback for errors that occur on an asynchronous request or response.
                 */
                @Override
                public void onError()
                {
                    try
                    {
                        thisClient.processMuleFlow( requestUri, requestCode, null, callback );
                    }
                    catch ( InternalResponseException e )
                    {
                        //this should never happen
                        LOGGER.error( handlerDescription + "cannot proces an error on asynchronous request or response", e );
                    }
                }

                /**
                 * Callback for delivering an asynchronous response.
                 * @param response The response received.
                 */
                @Override
                public void onLoad( CoapResponse response )
                {
                    try
                    {
                        thisClient.processMuleFlow( requestUri, requestCode, response, callback );
                    }
                    catch ( InternalResponseException e )
                    {
                        LOGGER.error( handlerDescription + "cannot proces an asynchronous response", e );
                        try
                        {
                            thisClient.processMuleFlow( requestUri, requestCode, null, callback );
                        }
                        catch ( InternalResponseException e1 )
                        {
                            //this should never happen
                            LOGGER.error( handlerDescription + "cannot proces an error on asynchronous response", e );
                        }
                    }
                }
            };
    }

    /**
     * Get an URI object describing the given CoAP resource.
     * @param host The host address of the server.
     * @param port The port the server is listening on.
     * @param path The path of the resource.
     * @param query String containing query parameters.
     * @return The URI object. 
     * @throws UriException cannot form valid uri with given parameters
     * @throws InternalUriException 
     */
    URI getURI( String host, Integer port, String path, String query ) throws InternalUriException
    {
        String actualHost= ( host != null ? host : this.host );
        Integer actualPort= ( port != null ? port : this.port );
        if ( actualHost == null )
        {
            throw new InternalUriException( "client { " + getClientName() + " } cannot form valid uri using host { " + actualHost + " }" );
        }
        if ( actualPort == null )
        {
            switch ( scheme )
            {
                //TODO add coap+ws, coaps+ws
                case CoAP.COAP_TCP_URI_SCHEME:
                case CoAP.COAP_SECURE_URI_SCHEME:
                    actualPort= CoAP.DEFAULT_COAP_SECURE_PORT;
                    break;
                default:
                    actualPort= CoAP.DEFAULT_COAP_PORT;
            }
        }
        URI uri;
        try
        {
            uri= new URI( scheme, null, actualHost, actualPort, path, query, null );
        }
        catch ( Exception e )
        {
            throw new InternalUriException(
                "cannot form valid uri using: { scheme= " + scheme + ", host= " + actualHost + ", port= " + actualPort + ", path= " + path + ", query= " + query + " }" );
        }
        return uri;
    }

    /**
     * See if server is reachable
     * @param host hostname or ip of server to ping, when null client configuration is used
     * @param port portnumber of server to ping, when null client configuration is used
     * @return true 
     * @throws ConnectorException
     * @throws IOException
     * @throws InternalUriException
     */
    Boolean ping( String host, Integer port ) throws ConnectorException, IOException, InternalUriException
    {
        Request request= new Request( null, Type.CON );
        request.setToken( Token.EMPTY );
        try
        {
            request.setURI( getURI( host, port, null, null ) );
        }
        catch ( Exception e )
        {
            throw new InternalUriException( e );
        }
        coapClient.advanced( request );
        return request.isRejected();

    }

    /**
     * Discover resources of server
     * @param confirmable when true the request is confirmable
     * @param host hostname or ip of server to ping, when null client configuration is used
     * @param port portnumber of server to ping, when null client configuration is used
     * @param queryString
     * @return The set containg the discovered resources.
     * @throws InternalUriException When no valid uri could be constructed.
     * @throws InternalUnexpectedResponseException When resonse does not contain link format payload.
     * @throws ConnectorException
     * @throws IOException
     * @throws InternalNoResponseException When timeout has occurred.
     * @throws InternalClientErrorResponseException When response indicates client error.
     * @throws InternalInvalidResponseCodeException When response has invalid code.
     * @throws InternalServerErrorResponseException When response indicates server error.
     * @throws InternalResponseException When response indicates other error.
     */
    Set< WebLink > discover( boolean confirmable, String host, Integer port, List< ? extends AbstractQueryParam > query ) throws InternalUriException,
        InternalNoResponseException,
        InternalUnexpectedResponseException,
        ConnectorException,
        IOException,
        InternalClientErrorResponseException,
        InternalInvalidResponseCodeException,
        InternalServerErrorResponseException,
        InternalResponseException
    {
        Request request= new Request( Code.GET, ( confirmable ? Type.CON : Type.NON ) );
        request.setURI( getURI( host, port, "/.well-known/core", toQueryString( query ) ) );
        CoapResponse response= coapClient.advanced( request );

        throwExceptionWhenNeeded( response );
        // check if Link Format
        if ( response.getOptions().getContentFormat() != MediaTypeRegistry.APPLICATION_LINK_FORMAT )
        {
            throw new InternalUnexpectedResponseException( "no link format received on discover request from { " + request.getURI() + " }" );
        }
        // parse and return
        return LinkFormat.parse( response.getResponseText() );
    }

    /**
     * Start observing a resource of a CoAP server.
     * @param handlerName the name of the response-handler that will process responses
     * @param confirmable when true the 
     * @param host hostname or ip of the server, when null client configuration is used
     * @param port portnumber of the server, when null client configuration is used
     * @param path path of the resource on the server
     * @param queryParameters uri-query parameters 
     * @throws InternalInvalidObserverException
     * @throws InternalUriException
     * @throws InternalInvalidHandlerNameException
     */
    synchronized void startObserver( String handlerName, boolean confirmable, String host, Integer port, String path, List< ? extends AbstractQueryParam > queryParameters )
        throws InternalInvalidObserverException,
        InternalUriException,
        InternalInvalidHandlerNameException
    {
        SourceCallback< InputStream, ReceivedResponseAttributes > handler= getHandler( handlerName );
        String uri= getURI( host, port, path, toQueryString( queryParameters ) ).toString();

        ObserveRelation relation= getRelation( uri );
        if ( relation != null )
        {
            // only one observe relation allowed per uri
            // TODO proactive or not, configurable?
            relation.stop();
            removeRelation( uri );
        }
        relation= new ObserveRelation(
            "CoAP Observer { " + getClientName() + "::" + uri + " }",
            coapClient,
            confirmable,
            uri,
            ( requestUri, requestCode, response ) -> this.processMuleFlow( requestUri, requestCode, response, handler ) );
        addRelation( uri, relation );
        relation.start();
    }

    /**
     * Stop observing a resource of a CoAP server.
     * @param host hostname or ip of the server, when null client configuration is used
     * @param port portnumber of the server, when null client configuration is used
     * @param path path of the resource on the server
     * @param queryParameters uri-query parameters 
     * @throws InternalUriException
     * @throws InternalInvalidObserverException
     */
    synchronized void stopObserver( String host, Integer port, String path, List< ? extends AbstractQueryParam > queryParameters ) throws InternalUriException,
        InternalInvalidObserverException
    {
        String uri= getURI( host, port, path, toQueryString( queryParameters ) ).toString();
        ObserveRelation relation= getRelation( uri );
        if ( relation != null )
        {
            relation.stop();
            removeRelation( uri );
        }
        else
        {
            throw new InternalInvalidObserverException( this + " cannot stop observer, observer nonexistent on resource { " + uri + " }" );
        }
    }

    /**
     * Get String repesentation.
     */
    @Override
    public String toString()
    {
        return "CoAP Client { " + getClientName() + " }";
    }
}
