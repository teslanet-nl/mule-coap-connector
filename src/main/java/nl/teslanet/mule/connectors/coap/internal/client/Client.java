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
package nl.teslanet.mule.connectors.coap.internal.client;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.Code;
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
import nl.teslanet.mule.connectors.coap.api.config.endpoint.Endpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.UDPEndpoint;
import nl.teslanet.mule.connectors.coap.api.error.MalformedUriException;
import nl.teslanet.mule.connectors.coap.api.options.RequestOptions;
import nl.teslanet.mule.connectors.coap.api.query.AbstractQueryParam;
import nl.teslanet.mule.connectors.coap.internal.OperationalEndpoint;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidByteArrayValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidHandlerNameException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidObserverException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidRequestCodeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalMalformedUriException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalNoResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUnexpectedResponseException;
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
    private final Logger LOGGER= LoggerFactory.getLogger( Client.class );

    @RefName
    private String clientName= null;

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
     * The endpoint the client uses.
     */
    private OperationalEndpoint operationalEndpoint= null;

    /**
     * The californium coap client 
     */
    private CoapClient coapClient= null;

    //    /**
    //     * observe relations that are permanent and initialized on startup
    //     */
    //    private ConcurrentSkipListMap< String, CoapObserveRelation > staticRelations= new ConcurrentSkipListMap< String, CoapObserveRelation >();

    /**
     * observe relations that are dynamically created and removed in runtime
     */
    private ConcurrentSkipListMap< String, CoapObserveRelation > dynamicRelations= new ConcurrentSkipListMap< String, CoapObserveRelation >();

    /**
     * The list of response handlers
     */
    private ConcurrentSkipListMap< String, SourceCallback< InputStream, ReceivedResponseAttributes > > handlers= new ConcurrentSkipListMap< String, SourceCallback< InputStream, ReceivedResponseAttributes > >();;

    /* (non-Javadoc)
     * @see org.mule.runtime.api.lifecycle.Startable#start()
     */
    @Override
    public void start() throws MuleException
    {
        coapClient= new CoapClient();
        coapClient.setEndpoint( operationalEndpoint.getCoapEndpoint() );
        LOGGER.info( "CoAP client '" + clientName + "' started { " + operationalEndpoint.getCoapEndpoint().getUri() + " }" );
    }

    /* (non-Javadoc)
     * @see org.mule.runtime.api.lifecycle.Stoppable#stop()
     */
    @Override
    public void stop() throws MuleException
    {
        //        for ( CoapObserveRelation relation : staticRelations.values() )
        //        {
        //            relation.proactiveCancel();
        //        }
        //        staticRelations.clear();
        for ( CoapObserveRelation relation : dynamicRelations.values() )
        {
            relation.proactiveCancel();
        }
        dynamicRelations.clear();
        handlers.clear();
        coapClient.shutdown();
        coapClient= null;
        LOGGER.info( "CoAP client '" + clientName + "' stopped { " + operationalEndpoint.getCoapEndpoint().getUri() + " }" );
    }

    /* (non-Javadoc)
     * @see org.mule.runtime.api.lifecycle.Initialisable#initialise()
     */
    @Override
    public void initialise() throws InitialisationException
    {
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
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            throw new InitialisationException( e, this );
        }
        scheme= operationalEndpoint.getCoapEndpoint().getUri().getScheme();
        LOGGER.info( "CoAP client '" + clientName + "' initialised { " + operationalEndpoint.getCoapEndpoint().getUri() + " }" );
    }

    /* (non-Javadoc)
     * @see org.mule.runtime.api.lifecycle.Disposable#dispose()
     */
    @Override
    public void dispose()
    {
        URI uri= operationalEndpoint.getCoapEndpoint().getUri();
        OperationalEndpoint.disposeAll( this );
        operationalEndpoint= null;
        LOGGER.info( "CoAP client '" + clientName + "' disposed { " + uri + " }" );
    }

    /**
     * Add handler to process responses.
     * @param handlerName the name of the handler
     * @param callback the source callback that will process the responses
     * @throws InternalInvalidHandlerNameException 
     */
    void addHandler( String handlerName, SourceCallback< InputStream, ReceivedResponseAttributes > callback ) throws InternalInvalidHandlerNameException
    {
        if ( handlerName == null || handlerName.isEmpty() ) throw new InternalInvalidHandlerNameException( "empty response handler name not allowed" );
        if ( handlers.get( handlerName ) != null ) throw new InternalInvalidHandlerNameException( "responsehandler name { " + handlerName + " } not unique" );
        handlers.put( handlerName, callback );
    }

    /**
     * get handler to process responses.
     * @param handlerName the name of the handler
     * @return the handler or null when no handler with that name is found.
     * @throws InternalInvalidHandlerNameException 
     */
    SourceCallback< InputStream, ReceivedResponseAttributes > getHandler( String handlerName ) throws InternalInvalidHandlerNameException
    {
        if ( handlerName == null || handlerName.isEmpty() ) throw new InternalInvalidHandlerNameException( "empty response handler name is not allowed" );
        return handlers.get( handlerName );
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
     * @throws InternalMalformedUriException when uri is not valid
     */
    void addRelation( String uri, CoapObserveRelation relation ) throws InternalInvalidObserverException, InternalMalformedUriException
    {
        if ( uri == null || uri.isEmpty() )
        {
            throw new InternalMalformedUriException( "empty uri is invalid: { " + uri + " }" );
        }
        if ( dynamicRelations.get( uri ) != null )
        {
            throw new InternalInvalidObserverException( "observer already exists: { " + uri + " }" );
        }
        dynamicRelations.put( uri, relation );
    }

    /**
     * Get an observer entry defined by the uri that is observed.
     * @param uri The observed uri.
     * @return The relation of the observer, null when no observer is found.
     * @throws InternalMalformedUriException when uri is not valid
     */
    CoapObserveRelation getRelation( String uri ) throws InternalMalformedUriException
    {
        if ( uri == null || uri.isEmpty() )
        {
            throw new InternalMalformedUriException( "empty uri is invalid: { " + uri + " }" );
        }
        return dynamicRelations.get( uri );
    }

    /**
     * Remove an observe entry.
     * @param uri The observed uri.
     */
    void removeRelation( String uri )
    {
        dynamicRelations.remove( uri );
    }

    /**
     * Get the relations.
     * @param uri The observed uri.
     * @return 
     */
    Map< String, CoapObserveRelation > getRelations()
    {
        return dynamicRelations;
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
     * Passes asynchronous response over to the muleflow  
     * @param response the coap response to process
     * @param callback the callback method of the muleflow.
     * @param requestAttributes the attributes of the originating request
     * @throws InternalInvalidOptionValueException 
     */
    void processMuleFlow( String requestUri, Code requestCode, CoapResponse response, SourceCallback< InputStream, ReceivedResponseAttributes > callback )
        throws InternalInvalidOptionValueException
    {
        ReceivedResponseAttributes responseAttributes;
        responseAttributes= createReceivedResponseAttributes( requestUri, requestCode, response );
        SourceCallbackContext requestcontext= callback.createContext();
        //requestcontext.addVariable( "CoapExchange", exchange );
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

    // TODO add custom timeout
    // TODO destination context?
    /**
     * Issue a request on a CoAP resource residing on a server.
     * @param confirmable indicating the requst must be confirmed
     * @param requestCode indicating the request type to issue
     * @param uri addresses the resource of the server for which the request is intended 
     * @param requestPayload is the contents of the request
     * @param options the CoAP options to add to the request
     * @param otherOptions the not standard options to add to the request
     * @param handlerName when a handler is specified the response will be handled asynchronously
     * @return the result of the request
     * @throws InternalInvalidHandlerNameException
     * @throws ConnectorException
     * @throws IOException
     * @throws InternalMalformedUriException
     * @throws InternalInvalidRequestCodeException
     * @throws InternalInvalidOptionValueException
     * @throws InternalInvalidByteArrayValueException 
     */
    Result< InputStream, ReceivedResponseAttributes > doRequest(
        boolean confirmable,
        Code requestCode,
        String uri,
        TypedValue< Object > requestPayload,
        RequestOptions options,
        String handlerName ) throws InternalInvalidHandlerNameException,
        ConnectorException,
        IOException,
        InternalMalformedUriException,
        InternalInvalidRequestCodeException,
        InternalInvalidOptionValueException,
        InternalInvalidByteArrayValueException
    {
        Result< InputStream, ReceivedResponseAttributes > result= null;
        CoapHandler handler= null;
        Request request= new Request( requestCode, ( confirmable ? Type.CON : Type.NON ) );
        request.setURI( uri );
        if ( requestPayload.getValue() != null )
        {
            //TODO review unintended payloads
            if ( request.getCode() == Code.GET || request.getCode() == Code.DELETE )
            {
                request.setUnintendedPayload();
            }
            //TODO add streaming & blockwise cooperation
            try
            {
                request.setPayload( MessageUtils.toByteArray( requestPayload ) );
            }
            catch ( RuntimeException e )
            {
                throw new InternalInvalidByteArrayValueException( "cannot convert payload to byte[]", e );
            }
            request.getOptions().setContentFormat( MediaTypeMediator.toContentFormat( requestPayload.getDataType().getMediaType() ) );
        }
        CoAPOptions.copyOptions( options, request.getOptions(), false );
        if ( handlerName != null )
        {
            final SourceCallback< InputStream, ReceivedResponseAttributes > callback= getHandler( handlerName );
            // verify handler existence
            if ( callback == null ) throw new InternalInvalidHandlerNameException( "referenced handler { " + handlerName + " } not found" );
            handler= createCoapHandler( uri, request.getCode(), callback );
        }
        if ( handler == null )
        {
            // send out synchronous request
            CoapResponse response= coapClient.advanced( request );
            ReceivedResponseAttributes responseAttributes;
            responseAttributes= createReceivedResponseAttributes( request.getURI(), request.getCode(), response );
            //TODO error when no response configurable
            if ( response == null )
            {
                result= Result.< InputStream, ReceivedResponseAttributes > builder().output( null ).attributes( responseAttributes ).mediaType( MediaType.ANY ).build();
            }
            else
            {
                byte[] payload= response.getPayload();
                if ( payload != null )
                {
                    result= Result.< InputStream, ReceivedResponseAttributes > builder()
                            .output( new ByteArrayInputStream( response.getPayload() ) )
                            .length( payload.length )
                            .attributes(
                        responseAttributes ).mediaType( MediaTypeMediator.toMediaType( response.getOptions().getContentFormat() ) ).build();
                }
                else
                {
                    result= Result.< InputStream, ReceivedResponseAttributes > builder()
                            .output( null )
                            .attributes( responseAttributes )
                            .mediaType(
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
     * Create response attributes that describe a response that is received.
     * @param request the request that caused the response.
     * @param response the response that is received from the server.
     * @return the created attributes derived from given request and response.
     * @throws InternalInvalidOptionValueException 
     */
    private ReceivedResponseAttributes createReceivedResponseAttributes( String requestUri, Code requestCode, CoapResponse response ) throws InternalInvalidOptionValueException
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
            //TODO make code representation independent of Cf
            attributes.setResponseCode( response.getCode().name() );
            attributes.setConfirmable( response.advanced().isConfirmable() );
            //attributes.setRelation( ( exchange.advanced().getRelation() != null ? exchange.advanced().getRelation().getKey() : null ) );
            attributes.setNotification( response.advanced().isNotification() );
            //attributes.setRemoteAddress( response.advanced().getDestinationContext().getPeerAddress().getHostString() );
            //attributes.setRemotePort( response.advanced().getDestinationContext().getPeerAddress().getPort() );
            CoAPOptions.copyOptions( response.getOptions(), attributes.getOptions() );
        }
        return attributes;
    }

    /**
     * Create a Handler of CoAP responses.
     * @param client  The Coap client that produced the response
     * @param callback The Listening Messageprocessor that needs to be called
     * @param requestCode The coap request code from the request context
     * @return
     */
    private CoapHandler createCoapHandler( final String requestUri, final Code requestCode, final SourceCallback< InputStream, ReceivedResponseAttributes > callback )
    {
        final Client thisClient= this;

        return new CoapHandler()
            {
                @Override
                public void onError()
                {
                    try
                    {
                        thisClient.processMuleFlow( requestUri, requestCode, null, callback );
                    }
                    catch ( InternalInvalidOptionValueException e )
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLoad( CoapResponse response )
                {
                    try
                    {
                        thisClient.processMuleFlow( requestUri, requestCode, response, callback );
                    }
                    catch ( InternalInvalidOptionValueException e )
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
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
     * @throws MalformedUriException cannot form valid uri with given parameters
     * @throws InternalMalformedUriException 
     */
    URI getURI( String host, Integer port, String path, String query ) throws InternalMalformedUriException
    {
        String actualHost= ( host != null ? host : this.host );
        Integer actualPort= ( port != null ? port : this.port );
        if ( actualHost == null )
        {
            throw new InternalMalformedUriException( "cannot form valid uri using: { host= " + actualHost + " }" );
        }
        if ( actualPort == null )
        {
            actualPort= ( scheme.equals( CoAP.COAP_SECURE_URI_SCHEME ) ? CoAP.DEFAULT_COAP_SECURE_PORT : CoAP.DEFAULT_COAP_PORT );
        }
        URI uri;
        try
        {
            uri= new URI( scheme, null, actualHost, actualPort, path, query, null );
        }
        catch ( Exception e )
        {
            throw new InternalMalformedUriException(
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
     * @throws InternalMalformedUriException
     */
    Boolean ping( String host, Integer port ) throws ConnectorException, IOException, InternalMalformedUriException
    {
        Request request= new Request( null, Type.CON );
        request.setToken( Token.EMPTY );
        request.setURI( getURI( host, port, null, null ) );
        coapClient.advanced( request );
        return request.isRejected();
    }

    /**
     * Discover resources of server
     * @param confirmable when true the request is confirmable
     * @param host hostname or ip of server to ping, when null client configuration is used
     * @param port portnumber of server to ping, when null client configuration is used
     * @param queryString
     * @return
     * @throws InternalMalformedUriException 
     * @throws InternalNoResponseException 
     * @throws InternalUnexpectedResponseException 
     * @throws IOException 
     * @throws ConnectorException 
     */
    Set< WebLink > discover( boolean confirmable, String host, Integer port, List< ? extends AbstractQueryParam > query ) throws InternalMalformedUriException,
        InternalNoResponseException,
        InternalUnexpectedResponseException,
        ConnectorException,
        IOException
    {
        Request request= new Request( Code.GET, ( confirmable ? Type.CON : Type.NON ) );
        request.setURI( getURI( host, port, "/.well-known/core", toQueryString( query ) ) );
        CoapResponse response= coapClient.advanced( request );

        // if no response, return null (e.g., timeout)
        if ( response == null )
        {
            throw new InternalNoResponseException( "discover request failed on { " + request.getURI() + " }" );
        }
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
     * @throws InternalMalformedUriException
     * @throws InternalInvalidHandlerNameException
     */
    void startObserver( String handlerName, boolean confirmable, String host, Integer port, String path, List< ? extends AbstractQueryParam > queryParameters )
        throws InternalInvalidObserverException,
        InternalMalformedUriException,
        InternalInvalidHandlerNameException
    {
        String uri= getURI( host, port, path, toQueryString( queryParameters ) ).toString();
        SourceCallback< InputStream, ReceivedResponseAttributes > callback= getHandler( handlerName );
        if ( callback == null ) throw new InternalInvalidHandlerNameException( "referenced handler { " + handlerName + " } not found" );

        CoapHandler handler= new CoapHandler()
            {
                @Override
                public void onError()
                {
                    LOGGER.warn( "observer { " + uri + " } failed, restoring relation." );
                    CoapObserveRelation coapRelation;
                    try
                    {
                        coapRelation= getRelation( uri );
                        if ( coapRelation != null )
                        {
                            if ( coapRelation.isCanceled() )
                            {
                                removeRelation( uri );
                                coapRelation= doObserveRequest( confirmable, uri, this );
                                addRelation( uri, coapRelation );
                            }
                            else
                            {
                                coapRelation.reregister();
                            } ;
                        }
                    }
                    catch ( InternalMalformedUriException e )
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch ( InternalInvalidObserverException e )
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    try
                    {
                        processMuleFlow( uri, Code.GET, null, callback );
                    }
                    catch ( InternalInvalidOptionValueException e )
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLoad( CoapResponse response )
                {
                    try
                    {
                        processMuleFlow( uri, Code.GET, response, callback );
                    }
                    catch ( InternalInvalidOptionValueException e )
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            };

        CoapObserveRelation relation= getRelation( uri );
        if ( relation != null )
        {
            // only one observe relation allowed per uri
            // TODO proactive or not, configurable?
            relation.proactiveCancel();
            removeRelation( uri );
        }
        relation= doObserveRequest( confirmable, uri, handler );
        addRelation( uri, relation );
        LOGGER.info( "observer started on resource { " + uri + " }" );
    }

    /**
     * Stop observing a resource of a CoAP server.
     * @param host hostname or ip of the server, when null client configuration is used
     * @param port portnumber of the server, when null client configuration is used
     * @param path path of the resource on the server
     * @param queryParameters uri-query parameters 
     * @throws InternalMalformedUriException
     * @throws InternalInvalidObserverException
     */
    void stopObserver( String host, Integer port, String path, List< ? extends AbstractQueryParam > queryParameters ) throws InternalMalformedUriException,
        InternalInvalidObserverException
    {
        String uri= getURI( host, port, path, toQueryString( queryParameters ) ).toString();
        CoapObserveRelation relation= getRelation( uri );
        if ( relation != null )
        {
            relation.proactiveCancel();
            removeRelation( uri );
            LOGGER.info( "observer stopped { " + uri + " }" );
        }
        else
        {
            throw new InternalInvalidObserverException( "cannot stop observer, observer nonexistent on resource { " + uri + " }" );
        }
    }

    /**
     * Send an observe request for a resource with given uri
     * @param confirmable when true the request will be sent confirmable
     * @param uri the uri of the resource to observe
     * @param handler the handler that will receive notifications from the observed resource
     * @return the established relation with the resource when the request succeeded, otherwise null 
     */
    CoapObserveRelation doObserveRequest( boolean confirmable, String uri, CoapHandler handler )
    {
        Request request= new Request( Code.GET, ( confirmable ? Type.CON : Type.NON ) );
        request.setURI( uri );
        //TODO add (other) options
        request.setObserve();
        return coapClient.observe( request, handler );
    }

}
