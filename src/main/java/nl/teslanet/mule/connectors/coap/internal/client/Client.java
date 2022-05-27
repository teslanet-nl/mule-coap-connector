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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.teslanet.mule.connectors.coap.api.AbstractAddressParams;
import nl.teslanet.mule.connectors.coap.api.AbstractQueryParams;
import nl.teslanet.mule.connectors.coap.api.AbstractResourceParams;
import nl.teslanet.mule.connectors.coap.api.AbstractResourceRequestParams;
import nl.teslanet.mule.connectors.coap.api.CoapMessageType;
import nl.teslanet.mule.connectors.coap.api.CoapRequestCode;
import nl.teslanet.mule.connectors.coap.api.CoapRequestType;
import nl.teslanet.mule.connectors.coap.api.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.Defs;
import nl.teslanet.mule.connectors.coap.api.DiscoverParams;
import nl.teslanet.mule.connectors.coap.api.ObserverAddParams;
import nl.teslanet.mule.connectors.coap.api.ObserverExistsParams;
import nl.teslanet.mule.connectors.coap.api.ObserverRemoveParams;
import nl.teslanet.mule.connectors.coap.api.PingParams;
import nl.teslanet.mule.connectors.coap.api.Proxy;
import nl.teslanet.mule.connectors.coap.api.ProxyConfig;
import nl.teslanet.mule.connectors.coap.api.RemoteEndpoint;
import nl.teslanet.mule.connectors.coap.api.RemoteEndpointConfig;
import nl.teslanet.mule.connectors.coap.api.RequestParams;
import nl.teslanet.mule.connectors.coap.api.ResponseHandlerParams;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.Endpoint;
import nl.teslanet.mule.connectors.coap.api.options.RequestOptions;
import nl.teslanet.mule.connectors.coap.internal.CoapConnector;
import nl.teslanet.mule.connectors.coap.internal.OperationalEndpoint;
import nl.teslanet.mule.connectors.coap.internal.attributes.AttributeUtils;
import nl.teslanet.mule.connectors.coap.internal.attributes.DefaultResponseAttributes;
import nl.teslanet.mule.connectors.coap.internal.exceptions.EndpointConstructionException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalClientErrorResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalEndpointException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidByteArrayValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidHandlerException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidMessageTypeException;
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
import nl.teslanet.mule.connectors.coap.internal.options.MediaTypeMediator;
import nl.teslanet.mule.connectors.coap.internal.utils.MessageUtils;


/**
 * The Client configuration defines the endpoint to use 
 * and provides for default request parameters.
 */
@Configuration( name= "client" )
@Sources( value=
{ Observer.class } )
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

    /**
     * Configuration of request defaults.
     */
    @ParameterGroup( name= "Request defaults" )
    private RequestConfig requestConfig;

    /**
     * The actual URI scheme
     */
    private String scheme;

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
        CoapConnector.setSchedulerService( schedulerService, schedulerConfig );
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
     * Get the localAddress.
     * @param uri The observed uri.
     * @return The local Address of the endpoint.
     */
    String getLocalAddress()
    {
        return operationalEndpoint.getCoapEndpoint().getAddress().toString();
    }

    // TODO add custom timeout
    /**
     * Issue a request on a CoAP resource residing on a server.
     * @param requestParams Builder containing request parameters.
     * @param options The request options.
     * @param handlerBuilder Builder containing the name of the handle when the response is handled asynchronously
     * @return The result of the request including payload and attributes.
     * @throws InternalInvalidHandlerException  When the handlerName is not null but does not reference an existing handler. 
     * @throws InternalRequestException When the Request could not be issued.
     * @throws InternalResponseException When the received response appears to be invalid and cannot be processed.
     * @throws InternalEndpointException When CoAP communication failed.
     * @throws InternalInvalidRequestCodeException When the request code has invalid value.
     * @throws InternalNoResponseException When timeout has occurred.
     * @throws InternalClientErrorResponseException When response indicates client error.
     * @throws InternalInvalidResponseCodeException When response indicates other error.
     * @throws InternalServerErrorResponseException When response indicates server error.
     * @throws InternalUriException When given request uri parameters are invalid.
     * @throws InternalInvalidMessageTypeException When request message type is invalid.
     */
    Result< InputStream, CoapResponseAttributes > doRequest( RequestParams requestParams, RequestOptions options, ResponseHandlerParams handlerBuilder )
        throws InternalInvalidHandlerException,
        InternalRequestException,
        InternalResponseException,
        InternalEndpointException,
        InternalInvalidRequestCodeException,
        InternalNoResponseException,
        InternalClientErrorResponseException,
        InternalInvalidResponseCodeException,
        InternalServerErrorResponseException,
        InternalUriException,
        InternalInvalidMessageTypeException
    {
        Result< InputStream, CoapResponseAttributes > result= null;
        CoapHandler handler= null;
        CoapRequestBuilderImpl builder= new CoapRequestBuilderImpl( requestParams );
        Request request= builder.build();
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
            // asynchronous request
            ResponseProcessor processor= ResponseProcessor.getResponseProcessor( handlerBuilder.getResponseHandler().getHandlerName() );
            handler= createCoapHandler(
                getLocalAddress(),
                handlerBuilder.getResponseHandler().getHandlerName(),
                request.getURI(),
                builder.buildMessageType(),
                builder.buildRequestCode(),
                processor
            );
            coapClient.advanced( handler, request );
            // nothing to return
        }
        else
        {
            // send out synchronous request
            result= executeSynchronous( request );
        }
        return result;
    }

    /**
     * Execute the request synchronously.
     * @param request The request to execute.
     * @return The result.
     * @throws InternalEndpointException The Endpoint cannot execute the request.
     * @throws InternalResponseException  The response could not be interpreted.
     * @throws InternalServerErrorResponseException The response received indicates server error.
     * @throws InternalInvalidResponseCodeException The response contained an invalid response code.
     * @throws InternalClientErrorResponseException The response received indicates client error.
     * @throws InternalNoResponseException No response was received within exchange lifetime.
     * @throws InternalInvalidMessageTypeException When request message type is invalid.
     * @throws InternalInvalidRequestCodeException When request code is invalid.
     */
    private Result< InputStream, CoapResponseAttributes > executeSynchronous( Request request ) throws InternalEndpointException,
        InternalNoResponseException,
        InternalClientErrorResponseException,
        InternalInvalidResponseCodeException,
        InternalServerErrorResponseException,
        InternalResponseException,
        InternalInvalidMessageTypeException,
        InternalInvalidRequestCodeException
    {
        CoapResponse response;
        CoapMessageType type= AttributeUtils.toMessageTypeAttribute( request.getType() );
        CoapRequestCode code= AttributeUtils.toRequestCodeAttribute( request.getCode() );
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
            responseAttributes= ResponseProcessor.createReceivedResponseAttributes( getLocalAddress(), request.getURI(), type, code, response );
        }
        catch ( InternalInvalidOptionValueException | InternalInvalidResponseCodeException | InternalInvalidMessageTypeException e )
        {
            throw new InternalResponseException( "CoAP response cannot be processed", e );
        }
        Result< InputStream, CoapResponseAttributes > result;
        if ( response == null )
        {
            result= Result.< InputStream, CoapResponseAttributes > builder().output( null ).attributes( responseAttributes ).mediaType( MediaType.ANY ).build();
        }
        else
        {
            byte[] payload= response.getPayload();
            if ( payload != null )
            {
                result= Result.< InputStream, CoapResponseAttributes > builder().output( new ByteArrayInputStream( response.getPayload() ) ).length( payload.length ).attributes(
                    responseAttributes
                ).mediaType( MediaTypeMediator.toMediaType( response.getOptions().getContentFormat() ) ).build();
            }
            else
            {
                result= Result.< InputStream, CoapResponseAttributes > builder().output( null ).attributes( responseAttributes ).mediaType(
                    MediaTypeMediator.toMediaType( response.getOptions().getContentFormat() )
                ).build();
            }
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
     * Create a Handler of CoAP responses.
     * @param uri 
     * @param client  The Coap client that produced the response
     * @param callback The Listening Messageprocessor that needs to be called
     * @param requestCode The coap request code from the request context
     * @return
     */
    private CoapHandler createCoapHandler(
        final String localAddress,
        final String handlerName,
        final String requestUri,
        final CoapMessageType requestType,
        final CoapRequestCode requestCode,
        final ResponseProcessor processor
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
                        ResponseProcessor.processMuleFlow( localAddress, requestUri, requestType, requestCode, null, processor );
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
                        ResponseProcessor.processMuleFlow( localAddress, requestUri, requestType, requestCode, response, processor );
                    }
                    catch ( InternalResponseException e )
                    {
                        logger.error( handlerDescription + "cannot proces an asynchronous response", e );
                        try
                        {
                            ResponseProcessor.processMuleFlow( localAddress, requestUri, requestType, requestCode, null, processor );
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
     * See if server is reachable
     * @param pingbuilder The ping request parameters.
     * @return true 
     * @throws ConnectorException
     * @throws IOException
     * @throws InternalUriException
     */
    Boolean ping( PingParams pingbuilder ) throws ConnectorException, IOException, InternalUriException
    {
        Request request;
        try
        {
            request= new CoapRequestBuilderImpl( pingbuilder ).buildEmpty();
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
     * @throws InternalRequestException When request parameters are invalid.
     * @throws InternalInvalidRequestCodeException When request code is invalid.
     */
    Set< WebLink > discover( DiscoverParams discoverParams ) throws InternalUriException,
        InternalNoResponseException,
        InternalUnexpectedResponseException,
        ConnectorException,
        IOException,
        InternalClientErrorResponseException,
        InternalInvalidResponseCodeException,
        InternalServerErrorResponseException,
        InternalResponseException,
        InternalInvalidRequestCodeException,
        InternalRequestException
    {
        Request request= new CoapRequestBuilderImpl( discoverParams ).build();
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
     * Start observing a resource on a CoAP server.
     * @param params The observer parameters.
     * @param handlerBuilder The response handler parameters.
     * @throws InternalInvalidObserverException When the observer parameters are invalid.
     * @throws InternalUriException When the uri parameters of the resource to observe are invalid.
     * @throws InternalInvalidHandlerException When the handler parameters are invalid.
     */
    synchronized void startObserver( ObserverAddParams params, ResponseHandlerParams handlerBuilder ) throws InternalInvalidObserverException,
        InternalUriException,
        InternalInvalidHandlerException
    {
        ResponseProcessor processor= ResponseProcessor.getResponseProcessor( handlerBuilder.getResponseHandler().getHandlerName() );
        CoapRequestBuilderImpl requestBuilder= new CoapRequestBuilderImpl( params );
        URI uri= requestBuilder.buildResourceUri();
        String localAddress= getLocalAddress();
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
            requestBuilder,
            ( requestUri, requestType, requestCode, response ) -> ResponseProcessor.processMuleFlow( localAddress, requestUri, requestType, requestCode, response, processor )
        );
        addRelation( uri, relation );
        relation.start();
    }

    /**
     * Stop observing a resource of a CoAP server.
     * @param params The parameters identifying the observer to stop.
     * @throws InternalUriException
     * @throws InternalInvalidObserverException
     */
    synchronized void stopObserver( ObserverRemoveParams params ) throws InternalUriException, InternalInvalidObserverException
    {
        CoapRequestBuilderImpl requestBuilder= new CoapRequestBuilderImpl( params );
        URI uri= requestBuilder.buildResourceUri();
        ObserveRelation relation= getRelation( uri );
        if ( relation != null )
        {
            //TODO Cf lacking support for NON observe cancel
            relation.stop();
            removeRelation( uri );
        }
        else
        {
            throw new InternalInvalidObserverException( this + " cannot stop observer, observer nonexistent on resource { " + uri + " }" );
        }
    }

    /**
     * Query existence of an observer.
     * @param params The parameters defining the observer.
     * @return True when the observer exists, otherwise False.
     * @throws InternalUriException
     */
    public boolean observerExists( ObserverExistsParams params ) throws InternalUriException
    {
        CoapRequestBuilderImpl requestBuilder= new CoapRequestBuilderImpl( params );
        URI uri= requestBuilder.buildResourceUri();
        ObserveRelation relation= getRelation( uri );
        return( relation != null );
    }

    /**
     * Get String repesentation.
     */
    @Override
    public String toString()
    {
        return "CoAP Client { " + getClientName() + " }";
    }

    /**
     * Builder for constructing requests on services that can be virtual or reside behind a proxy.
     * In latter cases the eindpoint address will differ from the service address.
     */
    class CoapRequestBuilderImpl implements CoapRequestBuilder
    {
        /**
         * The Observe flag;
         */
        Boolean observe= null;

        /**
         * The Confirmable flag;
         */
        boolean confirmable= true;

        /**
        * The CoAP request code.
        */
        CoapRequestCode requestCode= null;

        //TODO add proxyUri
        /**
         * Scheme part of the uri to use by a forwarding proxy.
         */
        String proxyScheme= null;

        /**
         * Host part of the endpoint uri.
         */
        String endpointHost= null;

        /**
         * Port part of the endpoint uri.
         */
        int endpointPort= -1;

        /**
         * Host part of the resource uri.
         */
        String resourceHost= null;

        /**
         * Port part of the service uri.
         */
        int resourcePort= -1;

        /**
         * Path part of the service uri.
         */
        String resourcePath= null;

        /**
         * Query part of the service uri.
         */
        String resourceQuery= null;

        /**
         * Flag to always add the given payload to the request, 
         * even when request code indicates it should not have one.
         */
        boolean forcePayload= false;

        /**
         * The request payload
         */
        TypedValue< Object > requestPayload= null;

        /**
         * Set the optional payload of the CoAP request.
         * @param request The request to set the payload on.
         * @throws InternalRequestException When the payload could not be added to the request.
         */
        private void setPayload( Request request ) throws InternalRequestException
        {
            if ( requestPayload != null && requestPayload.getValue() != null )
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
                        request.setPayload( MessageUtils.toBytes( requestPayload, transformationService ) );
                    }
                    catch ( RuntimeException | InternalInvalidByteArrayValueException e )
                    {
                        throw new InternalRequestException( "cannot convert payload to byte[]", e );
                    }
                    request.getOptions().setContentFormat( MediaTypeMediator.toContentFormat( requestPayload.getDataType().getMediaType() ) );
                }
            }
        }

        /**
        * Set the endpoint address.
        * @param host Host to use.
        * @param port Optional port to use.
        */
        private void setEndpointAddress( String host, Integer port )
        {
            endpointHost= host;
            if ( port != null ) endpointPort= port;
        }

        /**
         * Set proxyScheme if provided.
         * @param remoteEndpoint The candidate provider of the proxyScheme.
         */
        private void setProxyScheme( RemoteEndpointConfig remoteEndpoint )
        {
            if ( remoteEndpoint instanceof ProxyConfig )
            {
                ProxyConfig proxy= (ProxyConfig) remoteEndpoint;
                if ( proxy.getForwardToScheme() != null ) proxyScheme= proxy.getForwardToScheme();
            }
        }

        /**
         * Set proxyScheme if provided.
         * @param remoteEndpoint The candidate provider of the proxyScheme.
         */
        private void setProxyScheme( RemoteEndpoint remoteEndpoint )
        {
            if ( remoteEndpoint instanceof Proxy )
            {
                Proxy proxy= (Proxy) remoteEndpoint;
                if ( proxy.getForwardToScheme() != null ) proxyScheme= proxy.getForwardToScheme();
            }
        }

        /**
         * Constructor using PingBuilder and client defaults.
         * @param params Provides URI components.
         * @return The constructed UriBuilder object.
         */
        public CoapRequestBuilderImpl( PingParams params )
        {
            if ( params.getPingAddress() != null )
            {
                setEndpointAddress( params.getPingAddress().getEndpointHost(), params.getPingAddress().getEndpointPort() );
            }
            else if ( requestConfig.getRemoteEndpointConfig() != null )
            {
                setEndpointAddress( requestConfig.getRemoteEndpointConfig().getEndpointHost(), requestConfig.getRemoteEndpointConfig().getEndpointPort() );
            }
            else
            {
                setEndpointAddress( requestConfig.getHost(), requestConfig.getPort() );
            }
        }

        /**
         * Constructor using DiscoverBuilder and client defaults.
         * @param params Provides request parameters.
         * @return The constructed UriBuilder object.
         */
        public CoapRequestBuilderImpl( DiscoverParams params )
        {
            this( (AbstractQueryParams) params );
            //get request type
            if ( params.getType() == CoapRequestType.CONFIRMABLE )
            {
                confirmable= true;
            }
            else if ( params.getType() == CoapRequestType.NON_CONFIRMABLE )
            {
                confirmable= false;
            }
            else
            {
                confirmable= requestConfig.isConfirmable();
            }
            requestCode= CoapRequestCode.GET;
            resourcePath= Defs.COAP_URI_WELLKNOWN_CORE;
        }

        /**
         * Constructor using RequestBuilder and client defaults.
         * @param params Provides request parameters.
         * @return The constructed UriBuilder object.
         */
        public CoapRequestBuilderImpl( RequestParams params )
        {
            this( (AbstractResourceRequestParams) params );
            requestCode= params.getRequestCode();
            forcePayload= params.isForcePayload();
            requestPayload= params.getRequestPayload();
        }

        /**
         * Constructor using AbstractResourceRequestParams and client defaults.
         * @param params Provides request parameters.
         * @return The constructed builder object.
         */
        private CoapRequestBuilderImpl( AbstractResourceRequestParams params )
        {
            this( (AbstractResourceParams) params );
            //get request type
            if ( params.getType() == CoapRequestType.CONFIRMABLE )
            {
                confirmable= true;
            }
            else if ( params.getType() == CoapRequestType.NON_CONFIRMABLE )
            {
                confirmable= false;
            }
            else
            {
                confirmable= requestConfig.isConfirmable();
            }
        }

        /**
         * Constructor using AbstractResourceParams and client defaults.
         * @param params Provides request parameters.
         * @return The constructed builder object.
         */
        private CoapRequestBuilderImpl( AbstractResourceParams params )
        {
            this( (AbstractQueryParams) params );
            if ( params.getPath() != null )
            {
                resourcePath= params.getPath();
            }
            else if ( requestConfig.getPath() != null )
            {
                resourcePath= requestConfig.getPath();
            }
        }

        /**
         * Constructor using AbstractQueryParams and client defaults.
         * @param params Provides request parameters.
         * @return The constructed builder object.
         */
        private CoapRequestBuilderImpl( AbstractQueryParams params )
        {
            this( (AbstractAddressParams) params );
            resourceQuery= MessageUtils.queryString( requestConfig.getQueryConfigs(), params.getQueryParams() );
        }

        /**
         * Constructor using AbstractAddressParams and client defaults.
         * @param params Provides request parameters.
         * @return The constructed builder object.
         */
        private CoapRequestBuilderImpl( AbstractAddressParams params )
        {
            //get the endpoint address
            if ( params.getRemoteEndpoint() != null )
            {
                setEndpointAddress( params.getRemoteEndpoint().getEndpointHost(), params.getRemoteEndpoint().getEndpointPort() );
            }
            else if ( requestConfig.getRemoteEndpointConfig() != null )
            {
                setEndpointAddress( requestConfig.getRemoteEndpointConfig().getEndpointHost(), requestConfig.getRemoteEndpointConfig().getEndpointPort() );
            }
            else
            {
                if ( params.getHost() != null )
                {
                    endpointHost= params.getHost();
                }
                else
                {
                    endpointHost= requestConfig.getHost();
                }
                if ( params.getPort() != null )
                {
                    endpointPort= params.getPort();
                }
                else if ( requestConfig.getPort() != null )
                {
                    endpointPort= requestConfig.getPort();
                }
            }
            if ( params.getRemoteEndpoint() != null )
            {
                setProxyScheme( params.getRemoteEndpoint() );
            }
            else if ( requestConfig.getRemoteEndpointConfig() != null )
            {
                setProxyScheme( requestConfig.getRemoteEndpointConfig() );
            }
            if ( params.getHost() != null )
            {
                resourceHost= params.getHost();
            }
            else
            {
                resourceHost= requestConfig.getHost();
            }
            if ( params.getPort() != null )
            {
                resourcePort= params.getPort();
            }
            else if ( requestConfig.getPort() != null )
            {
                resourcePort= requestConfig.getPort();
            }
        }

        /**
         * Constructor using ObserverAddParams and client defaults.
         * @param params Provides request parameters.
         * @return The constructed UriBuilder object.
         */
        public CoapRequestBuilderImpl( ObserverAddParams params )
        {
            this( (AbstractResourceParams) params );
            observe= Boolean.TRUE;
            requestCode= CoapRequestCode.GET;
        }

        /**
         * Constructor using ObserverRemoveParams and client defaults.
         * @param params Provides request parameters.
         * @return The constructed UriBuilder object.
         */
        public CoapRequestBuilderImpl( ObserverRemoveParams params )
        {
            this( (AbstractResourceParams) params );
            observe= Boolean.FALSE;
            requestCode= CoapRequestCode.GET;
        }

        public CoapRequestBuilderImpl( ObserverConfig params )
        {
            observe= Boolean.TRUE;
            requestCode= CoapRequestCode.GET;
            //get request type
            if ( params.getType() == CoapRequestType.CONFIRMABLE )
            {
                confirmable= true;
            }
            else if ( params.getType() == CoapRequestType.NON_CONFIRMABLE )
            {
                confirmable= false;
            }
            else
            {
                confirmable= requestConfig.isConfirmable();
            }
            //get the endpoint address
            if ( params.getRemoteEndpointConfig() != null )
            {
                setEndpointAddress( params.getRemoteEndpointConfig().getEndpointHost(), params.getRemoteEndpointConfig().getEndpointPort() );
            }
            else if ( requestConfig.getRemoteEndpointConfig() != null )
            {
                setEndpointAddress( requestConfig.getRemoteEndpointConfig().getEndpointHost(), requestConfig.getRemoteEndpointConfig().getEndpointPort() );
            }
            else
            {
                if ( params.getHost() != null )
                {
                    endpointHost= params.getHost();
                }
                else
                {
                    endpointHost= requestConfig.getHost();
                }
                if ( params.getPort() != null )
                {
                    endpointPort= params.getPort();
                }
                else if ( requestConfig.getPort() != null )
                {
                    endpointPort= requestConfig.getPort();
                }
            }
            if ( params.getRemoteEndpointConfig() != null )
            {
                setProxyScheme( params.getRemoteEndpointConfig() );
            }
            else if ( requestConfig.getRemoteEndpointConfig() != null )
            {
                setProxyScheme( requestConfig.getRemoteEndpointConfig() );
            }
            if ( params.getHost() != null )
            {
                resourceHost= params.getHost();
            }
            else
            {
                resourceHost= requestConfig.getHost();
            }
            if ( params.getPort() != null )
            {
                resourcePort= params.getPort();
            }
            else if ( requestConfig.getPort() != null )
            {
                resourcePort= requestConfig.getPort();
            }
            resourceQuery= MessageUtils.queryString( requestConfig.getQueryConfigs(), params.getQueryConfigs() );
            if ( params.getPath() != null )
            {
                resourcePath= params.getPath();
            }
            else if ( requestConfig.getPath() != null )
            {
                resourcePath= requestConfig.getPath();
            }
        }

        /**
         * Build the message type of the request.
         * @return The message type of the request to build.
         */
        @Override
        public CoapMessageType buildMessageType()
        {
            if ( confirmable )
            {
                return CoapMessageType.CONFIRMABLE;
            }
            else
            {
                return CoapMessageType.NON_CONFIRMABLE;
            }
        }

        /**
         * Build the code of the request.
         * @return The requestCode of the request to build.
         */
        @Override
        public CoapRequestCode buildRequestCode()
        {
            return requestCode;
        }

        /**
         * Build the URI of the endpoint to address the request.
         * @return The endpoint URI.
         * @throws InternalUriException When URI components cannot be assembled to a valid URI.
         */
        @Override
        public URI buildEndpointUri() throws InternalUriException
        {
            URI uri;
            try
            {
                uri= new URI( scheme, null, endpointHost, endpointPort, null, null, null );
            }
            catch ( Exception e )
            {
                throw new InternalUriException( "cannot form valid uri using: { scheme= " + scheme + ", host= " + endpointHost + ", port= " + endpointPort + " }" );
            }
            return uri;
        }

        /**
         * Build the URI of the resource to request.
         * @return The resource URI.
         * @throws InternalUriException When URI components cannot be assembled to a valid URI.
         */
        @Override
        public URI buildResourceUri() throws InternalUriException
        {
            if ( resourcePath == null )
            {
                throw new InternalUriException( "client { " + getClientName() + " } cannot form valid uri using path { null }" );
            }
            URI uri;
            try
            {
                uri= new URI( ( proxyScheme != null ? proxyScheme : scheme ), null, resourceHost, resourcePort, resourcePath, resourceQuery, null );
            }
            catch ( Exception e )
            {
                throw new InternalUriException(
                    "cannot form valid uri using: { scheme= " + proxyScheme + ", host= " + resourceHost + ", port= " + resourcePort + ", path= " + resourcePath + ", query= "
                        + resourceQuery + " }"
                );
            }
            return uri;
        }

        /**
         * Build an empty CoAP request based on given parameters.
         * @return The CoAP request.
         * @throws InternalUriException When parameters do not assemble to valid URI.
         */
        @Override
        public Request buildEmpty() throws InternalUriException
        {
            Request request= new Request( null, ( confirmable ? Type.CON : Type.NON ) );
            request.setURI( buildEndpointUri() );
            request.setToken( Token.EMPTY );
            return request;
        }

        /**
         * Build the CoAP request based on given parameters.
         * @return The CoAP request.
         * @throws InternalInvalidRequestCodeException When given requestCode is invalid.
         * @throws InternalUriException When parameters do not assemble to valid URI.
         * @throws InternalRequestException When given payload could not be added the request.
         */
        @Override
        public Request build() throws InternalInvalidRequestCodeException, InternalUriException, InternalRequestException
        {
            Request request= new Request( AttributeUtils.toRequestCode( requestCode ), ( confirmable ? Type.CON : Type.NON ) );
            request.setURI( buildEndpointUri() );
            request.setOptions( buildResourceUri() );
            if ( observe != null )
            {
                if ( observe.booleanValue() )
                {
                    request.setObserve();
                }
                else
                {
                    request.setObserveCancel();
                }
            }
            if ( proxyScheme != null ) request.getOptions().setProxyScheme( proxyScheme );
            setPayload( request );
            return request;
        }
    }
}
