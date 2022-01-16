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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
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
import org.mule.runtime.api.transformation.TransformationService;
import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.Sources;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.RefName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.teslanet.mule.connectors.coap.api.AbstractAddressBuilder;
import nl.teslanet.mule.connectors.coap.api.AbstractResourceRequestBuilder;
import nl.teslanet.mule.connectors.coap.api.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.DiscoverBuilder;
import nl.teslanet.mule.connectors.coap.api.ObserverStartBuilder;
import nl.teslanet.mule.connectors.coap.api.ObserverStopBuilder;
import nl.teslanet.mule.connectors.coap.api.PingBuilder;
import nl.teslanet.mule.connectors.coap.api.RequestBuilder;
import nl.teslanet.mule.connectors.coap.api.RequestBuilder.CoAPRequestCode;
import nl.teslanet.mule.connectors.coap.api.ResponseHandlerBuilder;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.Endpoint;
import nl.teslanet.mule.connectors.coap.api.error.UriException;
import nl.teslanet.mule.connectors.coap.api.options.RequestOptions;
import nl.teslanet.mule.connectors.coap.internal.CoAPConnector;
import nl.teslanet.mule.connectors.coap.internal.OperationalEndpoint;
import nl.teslanet.mule.connectors.coap.internal.attributes.AttributeUtils;
import nl.teslanet.mule.connectors.coap.internal.attributes.DefaultResponseAttributes;
import nl.teslanet.mule.connectors.coap.internal.exceptions.EndpointConstructionException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalClientErrorResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalEndpointException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidByteArrayValueException;
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
import nl.teslanet.mule.connectors.coap.internal.options.DefaultResponseOptionsAttributes;
import nl.teslanet.mule.connectors.coap.internal.options.MediaTypeMediator;
import nl.teslanet.mule.connectors.coap.internal.utils.MessageUtils;


/**
 * The Client configuration defines the endpoint to use 
 * and provides for default request parameters.
 */
@Configuration( name= "client" )
@Sources( value=
{ Observer.class, ResponseHandler.class } )
@Operations( ClientOperations.class )
public class Client implements Initialisable, Disposable, Startable, Stoppable
{
    /**
     * Logger of the class
     */
    private static final Logger logger= LoggerFactory.getLogger( Client.class.getCanonicalName() );

    @RefName
    private String clientName= null;

    /**
     * Mule transformation service.
     */
    @Inject
    private TransformationService transformationService;

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
     * The endpoint this client uses.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false, allowInlineDefinition= true )
    @Summary( value= "The endpoint the client uses." )
    @Placement( order= 1, tab= "Endpoint" )
    private Endpoint endpoint;

    /**
     * @return the endpoint
     */
    public Endpoint getEndpoint()
    {
        return endpoint;
    }

    /**
     * @param endpoint the endpoint to set
     */
    public void setEndpoint( Endpoint endpoint )
    {
        this.endpoint= endpoint;
    }

    /**
     * The actual URI scheme
     */
    private String scheme;

    //TODO add default confirmable, path and query?

    /**
     * When {@code true} synchronous operations will throw an exception when CoAP error codes are received or a timeout has occurred. 
     * Otherwise a result is returned in these cases with attribute.success set to {@code False}.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @Summary(
        "When true, synchronous operations will throw an exception when CoAP error codes are received or a timeout has occurred. Otherwise a result is returned in these cases, with attribute.success set to false."
    )
    private boolean throwExceptionOnErrorResponse= true;

    @ParameterGroup( name= "Default request uri" )
    private RequestConfig requestDefaults;

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
    private ConcurrentHashMap< URI, ObserveRelation > observeRelations= new ConcurrentHashMap<>();

    /**
     * The list of response handlers
     */
    private ConcurrentHashMap< String, SourceCallback< InputStream, CoapResponseAttributes > > handlers= new ConcurrentHashMap<>();

    /**
     * Start the client. After starting the client is ready for issuing requests.
     */
    @Override
    public void start() throws MuleException
    {
        coapClient= new CoapClient();
        coapClient.setEndpoint( operationalEndpoint.getCoapEndpoint() );
        logger.info( this + " connected to " + operationalEndpoint );
        logger.info( this + " started." );
    }

    /**
     * Stop the client. When stopped no more requests can be sent using this client.
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
        logger.info( this + " stopped." );
    }

    /**
     * Intitialise the client. The client is assigned an endpoint, which is created when needed. 
     */
    @Override
    public void initialise() throws InitialisationException
    {
        AbstractEndpoint abstractEndpoint;
        CoAPConnector.setSchedulerService( schedulerService, schedulerConfig );
        if ( endpoint == null )
        {
            //user wants default endpoint
            abstractEndpoint= new DefaultClientEndpoint( this.toString() + " endpoint" );
        }
        else
        {
            abstractEndpoint= endpoint.getEndpoint();
            //mule should have set the mandatory endpoint.
            if ( abstractEndpoint == null ) throw new InitialisationException( new IllegalArgumentException( "Unexpected null value in client endpoint." ), this );
        }
        if ( abstractEndpoint.configName == null )
        {
            //inline endpoint will get this as name
            abstractEndpoint.configName= this.toString();
        }
        try
        {
            operationalEndpoint= OperationalEndpoint.getOrCreate( this, abstractEndpoint );
        }
        catch ( EndpointConstructionException e )
        {
            throw new InitialisationException( e, this );
        }
        scheme= operationalEndpoint.getCoapEndpoint().getUri().getScheme();
        logger.info( this + " initialised." );
    }

    /**
     * The client is disposed of. The client is detached from the endpoint, which will be deleted when not used anymore.
     */
    @Override
    public void dispose()
    {
        OperationalEndpoint.disposeAll( this );
        operationalEndpoint= null;
        logger.info( this + " disposed." );
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
    synchronized void addHandler( String handlerName, SourceCallback< InputStream, CoapResponseAttributes > callback ) throws InternalInvalidHandlerNameException
    {
        if ( handlerName == null || handlerName.isEmpty() ) throw new InternalInvalidHandlerNameException( "empty response handler name not allowed" );
        if ( handlers.get( handlerName ) != null ) throw new InternalInvalidHandlerNameException( "responsehandler name { " + handlerName + " } not unique" );
        handlers.put( handlerName, callback );
    }

    /**
     * Get handler that can process async responses.
     * @param handlerName the name of the handler
     * @return The handler with given name.
     * @throws InternalInvalidHandlerNameException When the handler with given name could not be found. 
     */
    private SourceCallback< InputStream, CoapResponseAttributes > getHandler( String handlerName ) throws InternalInvalidHandlerNameException
    {
        if ( handlerName == null || handlerName.isEmpty() ) throw new InternalInvalidHandlerNameException( "empty response handler name is not allowed" );
        SourceCallback< InputStream, CoapResponseAttributes > handler= handlers.get( handlerName );
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
    synchronized void addRelation( URI uri, ObserveRelation relation ) throws InternalInvalidObserverException, InternalUriException
    {
        if ( uri == null )
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
    ObserveRelation getRelation( URI uri ) throws InternalUriException
    {
        if ( uri == null )
        {
            throw new InternalUriException( "empty uri is invalid: { " + uri + " }" );
        }
        return observeRelations.get( uri );
    }

    /**
     * Remove an observe entry.
     * @param uri The observed uri.
     */
    synchronized void removeRelation( URI uri )
    {
        observeRelations.remove( uri );
    }

    /**
     * Get the relations.
     * @param uri The observed uri.
     * @return 
     */
    Map< URI, ObserveRelation > getRelations()
    {
        return observeRelations;
    }

    /**
     * Get a querystring containing containing query parameters that can be use as part of a an Uri-string.
     * @param queryParamList List of query parameters.
     * @return The querystring. 
     */
    /*
    String toQueryString( List< ? extends QueryParamAttribute > queryParamList )
    {
        if ( queryParamList == null ) return null;
    
        StringBuilder builder= new StringBuilder();
        boolean first;
        Iterator< ? extends QueryParamAttribute > it;
        for ( first= true, it= queryParamList.iterator(); it.hasNext(); )
        {
            QueryParamAttribute queryParam= it.next();
            if ( queryParam.hasKey() )
            {
                if ( first )
                {
                    builder.append( "&" );
                    first= false;
                }
                builder.append( queryParam.getKey() );
                if ( queryParam.hasValue() )
                {
                    builder.append( "=" );
                    builder.append( queryParam.getValue() );
                }
            }
        }
        return builder.toString();
    }
    */
    /**
     * Passes asynchronous response to the muleflow.
     * @param requestUri The Uri of the request that caused the response.
     * @param requestCode The code of the request that caused the response.
     * @param response The coap response to process.
     * @param callback The callback method of the muleflow.
     * @throws InternalResponseException When the received CoAP response contains values that cannot be processed.
     */
    void processMuleFlow( URI requestUri, CoAPRequestCode requestCode, CoapResponse response, SourceCallback< InputStream, CoapResponseAttributes > callback )
        throws InternalResponseException

    {
        DefaultResponseAttributes responseAttributes;
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

    // TODO add custom timeout
    /**
     * Issue a request on a CoAP resource residing on a server.
     * @param builder Builder containing request parameters.
     * @param options The request options.
     * @param handlerBuilder Builder containing the name of the handle when the response is handled asynchronously
     * @return The result of the request including payload and attributes.
     * @throws InternalInvalidHandlerNameException  When the handlerName is not null but does not reference an existing handler. 
     * @throws InternalRequestException When the Request could not be issued.
     * @throws InternalResponseException When the received response appears to be invalid and cannot be processed.
     * @throws InternalEndpointException When CoAP communication failed.
     * @throws InternalInvalidRequestCodeException When the request code has invalid value.
     * @throws InternalNoResponseException When timeout has occurred.
     * @throws InternalClientErrorResponseException When response indicates client error.
     * @throws InternalInvalidResponseCodeException When response indicates other error.
     * @throws InternalServerErrorResponseException When response indicates server error.
     * @throws InternalUriException When given request uri parameters are invalid.
     */
    Result< InputStream, CoapResponseAttributes > doRequest( RequestBuilder builder, RequestOptions options, ResponseHandlerBuilder handlerBuilder )
        throws InternalInvalidHandlerNameException,
        InternalRequestException,
        InternalResponseException,
        InternalEndpointException,
        InternalInvalidRequestCodeException,
        InternalNoResponseException,
        InternalClientErrorResponseException,
        InternalInvalidResponseCodeException,
        InternalServerErrorResponseException,
        InternalUriException
    {
        Result< InputStream, CoapResponseAttributes > result= null;
        CoapHandler handler= null;
        Request request= new Request( AttributeUtils.toRequestCode( builder.getRequestCode() ), ( builder.isConfirmable() ? Type.CON : Type.NON ) );
        URI uri= getURI( builder );
        request.setURI( uri );
        TypedValue< Object > requestPayload= builder.getRequestPayload();
        if ( requestPayload.getValue() != null )
        {
            boolean sendPayload;
            switch ( request.getCode() )
            {
                case GET:
                case DELETE:
                    if ( builder.isForcePayload() )
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
                    request.setPayload( MessageUtils.toBytes( requestPayload, transformationService ) );
                }
                catch ( RuntimeException | InternalInvalidByteArrayValueException e )
                {
                    throw new InternalRequestException( "cannot convert payload to byte[]", e );
                }
                request.getOptions().setContentFormat( MediaTypeMediator.toContentFormat( requestPayload.getDataType().getMediaType() ) );
            }
        }
        try
        {
            MessageUtils.copyOptions( options, request.getOptions(), transformationService );
        }
        catch ( InternalInvalidOptionValueException e )
        {
            throw new InternalRequestException( "cannot process request options", e );
        }
        if ( handlerBuilder != null )
        {
            SourceCallback< InputStream, CoapResponseAttributes > callback;
            callback= getHandler( handlerBuilder.getResponseHandler() );
            handler= createCoapHandler( handlerBuilder.getResponseHandler(), uri, builder.getRequestCode(), callback );
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
            DefaultResponseAttributes responseAttributes;
            try
            {
                responseAttributes= createReceivedResponseAttributes( uri, builder.getRequestCode(), response );
            }
            catch ( InternalInvalidOptionValueException | InternalInvalidResponseCodeException e )
            {
                throw new InternalResponseException( "CoAP response cannot be processed", e );
            }
            if ( response == null )
            {
                result= Result.< InputStream, CoapResponseAttributes > builder().output( null ).attributes( responseAttributes ).mediaType( MediaType.ANY ).build();
            }
            else
            {
                byte[] payload= response.getPayload();
                if ( payload != null )
                {
                    result= Result.< InputStream, CoapResponseAttributes > builder().output( new ByteArrayInputStream( response.getPayload() ) ).length(
                        payload.length
                    ).attributes( responseAttributes ).mediaType( MediaTypeMediator.toMediaType( response.getOptions().getContentFormat() ) ).build();
                }
                else
                {
                    result= Result.< InputStream, CoapResponseAttributes > builder().output( null ).attributes( responseAttributes ).mediaType(
                        MediaTypeMediator.toMediaType( response.getOptions().getContentFormat() )
                    ).build();
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
    private DefaultResponseAttributes createReceivedResponseAttributes( URI requestUri, CoAPRequestCode requestCode, CoapResponse response )
        throws InternalInvalidOptionValueException,
        InternalInvalidResponseCodeException
    {
        DefaultResponseAttributes attributes= new DefaultResponseAttributes();
        attributes.setRequestCode( requestCode.name() );
        attributes.setLocalAddress( operationalEndpoint.getCoapEndpoint().getAddress().toString() );
        attributes.setRequestPath( requestUri.getPath() );
        attributes.setRequestQuery( requestUri.getQuery() );
        if ( response == null )
        {
            attributes.setSuccess( false );
        }
        else
        {
            attributes.setSuccess( response.isSuccess() );
            attributes.setResponseCode( AttributeUtils.toResponseCodeAttribute( response.getCode() ).name() );
            attributes.setConfirmable( response.advanced().isConfirmable() );
            attributes.setRemoteAddress( response.advanced().getSourceContext().getPeerAddress().toString() );
            attributes.setNotification( response.advanced().isNotification() );
            attributes.setOptions( new DefaultResponseOptionsAttributes( response.getOptions() ) );
            attributes.setLocationUri( MessageUtils.uriString( attributes.getOptions().getLocationPath(), attributes.getOptions().getLocationQuery() ) );
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
        final URI requestUri,
        final CoAPRequestCode requestCode,
        final SourceCallback< InputStream, CoapResponseAttributes > callback
    )
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
                        logger.error( handlerDescription + "cannot proces an error on asynchronous request or response", e );
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
                        logger.error( handlerDescription + "cannot proces an asynchronous response", e );
                        try
                        {
                            thisClient.processMuleFlow( requestUri, requestCode, null, callback );
                        }
                        catch ( InternalResponseException e1 )
                        {
                            //this should never happen
                            logger.error( handlerDescription + "cannot proces an error on asynchronous response", e );
                        }
                    }
                }
            };
    }

    /**
     * Obtain actual host to use for request.
     * @param host The optional host value.
     * @return The actual host value to use.
     * @throws InternalUriException When no host value is obtained.
     */
    private String actualHost( String host ) throws InternalUriException
    {
        if ( host != null ) return host;
        String defaultHost= requestDefaults.getHost();
        if ( defaultHost == null )
        {
            throw new InternalUriException( "client { " + getClientName() + " } cannot form valid uri using host { " + defaultHost + " }" );
        }
        return defaultHost;
    }

    /**
     * Obtain actual port to use for request.
     * @param port The optional port value.
     * @return The actual port value to use.
     */
    public Integer actualPort( Integer port )
    {
        if ( port != null ) return port;
        Integer defaultPort= requestDefaults.getPort();
        if ( defaultPort == null )
        {
            defaultPort= -1;
        }
        return defaultPort;
    }

    /**
     * Obtain actual path to use for request.
     * @param path The optional path value.
     * @return The actual path value to use.
     * @throws InternalUriException hen no path value is obtained.
     */
    private String actualPath( String path ) throws InternalUriException
    {
        if ( path != null ) return path;
        String defaultPath= requestDefaults.getPath();
        if ( defaultPath == null )
        {
            throw new InternalUriException( "client { " + getClientName() + " } cannot form valid uri using path { null }" );
        }
        return defaultPath;
    }

    /**
     * Obtain actual query parameters to use for request.
     * @param query The optional query parameters.
     * @return The actual query parameters to use.
     */
    //TODO RC remove
    //    private MultiMap< String, String > actualQuery( MultiMap< String, String > query )
    //    {
    //        //TODO merge?
    //        if ( query != null )
    //        {
    //            return query;
    //        }
    //        else
    //        {
    //            return requestDefaults.getQueryParamConfigs();
    //        }
    //    }

    /**
     * Obtain query parameters from MultiMap
     * @param query The optional query parameters.
     * @return The actual query parameters to use.
     */
    /*
    private List< DefaultQueryParamAttribute > getQueryParams( MultiMap< String, String > query )
    {
        ArrayList< DefaultQueryParamAttribute > params= new ArrayList<>();
        if ( query != null )
        {
            for ( String key : query.keySet() )
            {
                for ( String value : query.getAll( key ) )
                {
                    params.add( new DefaultQueryParamAttribute( key, value ) );
                }
            }
        }
        return params;
    }
    */

    /**
     * Get an URI object describing the given CoAP resource.
     * @param builder The provider of uri parameters.
     * @return The URI object. 
     * @throws UriException cannot form valid uri with given parameters
     * @throws InternalUriException 
     */
    private URI getURI( AbstractResourceRequestBuilder builder ) throws InternalUriException
    {
        String host= actualHost( builder.getHost() );
        Integer port= actualPort( builder.getPort() );
        String path= actualPath( builder.getPath() );
        String query= MessageUtils.queryString( requestDefaults.getQueryParamConfigs(), builder.getQueryParams() );
        URI uri;
        try
        {
            uri= new URI( scheme, null, host, port, path, query, null );
        }
        catch ( Exception e )
        {
            throw new InternalUriException(
                "cannot form valid uri using: { scheme= " + scheme + ", host= " + host + ", port= " + port + ", path= " + path + ", query= " + query + " }"
            );
        }
        return uri;
    }

    /**
     * Get an URI object describing the request.
     * @param builder The provider of request parameters.
     * @return The URI object. 
     * @throws InternalUriException When no valid uri could be built with given parameters.
     */
    private URI getURI( DiscoverBuilder uriBuilder ) throws InternalUriException
    {
        String host= actualHost( uriBuilder.getHost() );
        Integer port= actualPort( uriBuilder.getPort() );
        String query= MessageUtils.queryString( requestDefaults.getQueryParamConfigs(), uriBuilder.getQueryParams() );
        URI uri;
        try
        {
            uri= new URI( scheme, null, host, port, "/.well-known/core", query, null );
        }
        catch ( Exception e )
        {
            throw new InternalUriException( "cannot form valid uri using: { scheme= " + scheme + ", host= " + host + ", port= " + port + ", query= " + query + " }" );
        }
        return uri;
    }

    /**
     * Get an URI object describing the request.
     * @param builder The provider of request parameters.
     * @return The URI object. 
     * @throws InternalUriException When no valid uri could be built with given parameters.
     */
    private URI getURI( ObserverStopBuilder uriBuilder ) throws InternalUriException
    {
        String host= actualHost( uriBuilder.getHost() );
        Integer port= actualPort( uriBuilder.getPort() );
        String path= actualPath( uriBuilder.getPath() );
        String query= MessageUtils.queryString( requestDefaults.getQueryParamConfigs(), uriBuilder.getQueryParams() );
        URI uri;
        try
        {
            uri= new URI( scheme, null, host, port, path, query, null );
        }
        catch ( Exception e )
        {
            throw new InternalUriException(
                "cannot form valid uri using: { scheme= " + scheme + ", host= " + host + ", port= " + port + ", path= " + path + ", query= " + query + " }"
            );
        }
        return uri;
    }

    /**
     * Get an URI object describing the address to request.
     * @param uriBuilder The provider of request parameters.
     * @return The URI object. 
     * @throws InternalUriException When no valid uri could be built with given parameters.
     */
    private URI getURI( AbstractAddressBuilder uriBuilder ) throws InternalUriException
    {
        String host= actualHost( uriBuilder.getHost() );
        Integer port= actualPort( uriBuilder.getPort() );
        URI uri;
        try
        {
            uri= new URI( scheme, null, host, port, null, null, null );
        }
        catch ( Exception e )
        {
            throw new InternalUriException( "cannot form valid uri using: { scheme= " + scheme + ", host= " + host + ", port= " + port + " }" );
        }
        return uri;
    }

    /**
     * Create URI from request configuration.
     * @param uriBuilder The builder containing input parameters.
     * @return The constructed URI
     * @throws InternalUriException When the URI cannot be constructed using the builder.
     */
    public URI getURI( RequestConfig uriBuilder ) throws InternalUriException
    {
        String host= actualHost( uriBuilder.getHost() );
        Integer port= actualPort( uriBuilder.getPort() );
        String path= actualPath( uriBuilder.getPath() );
        String query= MessageUtils.queryString2( requestDefaults.getQueryParamConfigs(), uriBuilder.getQueryParamConfigs() );
        URI uri;
        try
        {
            uri= new URI( scheme, null, host, port, path, query, null );
        }
        catch ( Exception e )
        {
            throw new InternalUriException(
                "cannot form valid uri using: { scheme= " + scheme + ", host= " + host + ", port= " + port + ", path= " + path + ", query= " + query + " }"
            );
        }
        return uri;
    }

    /**
     * See if server is reachable
     * @param pingbuilder The ping request parameters.
     * @return true 
     * @throws ConnectorException
     * @throws IOException
     * @throws InternalUriException
     */
    Boolean ping( PingBuilder pingbuilder ) throws ConnectorException, IOException, InternalUriException
    {
        Request request= new Request( null, Type.CON );
        request.setToken( Token.EMPTY );
        try
        {
            request.setURI( getURI( pingbuilder ) );
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
    Set< WebLink > discover( DiscoverBuilder discoverBuilder ) throws InternalUriException,
        InternalNoResponseException,
        InternalUnexpectedResponseException,
        ConnectorException,
        IOException,
        InternalClientErrorResponseException,
        InternalInvalidResponseCodeException,
        InternalServerErrorResponseException,
        InternalResponseException
    {
        Request request= new Request( Code.GET, ( discoverBuilder.isConfirmable() ? Type.CON : Type.NON ) );
        request.setURI( getURI( discoverBuilder ) );
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
    synchronized void startObserver( ObserverStartBuilder observerStartBuilder, ResponseHandlerBuilder handlerBuilder ) throws InternalInvalidObserverException,
        InternalUriException,
        InternalInvalidHandlerNameException
    {
        SourceCallback< InputStream, CoapResponseAttributes > handler= getHandler( handlerBuilder.getResponseHandler() );
        URI uri= getURI( observerStartBuilder );
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
            observerStartBuilder.isConfirmable(),
            uri,
            ( requestUri, requestCode, response ) -> this.processMuleFlow( requestUri, requestCode, response, handler )
        );
        addRelation( uri, relation );
        relation.start();
    }

    /**
     * Stop observing a resource of a CoAP server.
     * @param builder The parameters identifying the observer to stop.
     * @throws InternalUriException
     * @throws InternalInvalidObserverException
     */
    synchronized void stopObserver( ObserverStopBuilder builder ) throws InternalUriException, InternalInvalidObserverException
    {
        URI uri= getURI( builder );
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
