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


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.operation.Result;

import nl.teslanet.mule.connectors.coap.api.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.DiscoverParams;
import nl.teslanet.mule.connectors.coap.api.DiscoveredResource;
import nl.teslanet.mule.connectors.coap.api.ObserverAddParams;
import nl.teslanet.mule.connectors.coap.api.ObserverRemoveParams;
import nl.teslanet.mule.connectors.coap.api.PingParams;
import nl.teslanet.mule.connectors.coap.api.RequestParams;
import nl.teslanet.mule.connectors.coap.api.ResponseHandlerParams;
import nl.teslanet.mule.connectors.coap.api.error.ClientErrorResponseException;
import nl.teslanet.mule.connectors.coap.api.error.EndpointException;
import nl.teslanet.mule.connectors.coap.api.error.InvalidHandlerNameException;
import nl.teslanet.mule.connectors.coap.api.error.InvalidObserverException;
import nl.teslanet.mule.connectors.coap.api.error.InvalidRequestCodeException;
import nl.teslanet.mule.connectors.coap.api.error.NoResponseException;
import nl.teslanet.mule.connectors.coap.api.error.RequestException;
import nl.teslanet.mule.connectors.coap.api.error.ResponseException;
import nl.teslanet.mule.connectors.coap.api.error.ServerErrorResponseException;
import nl.teslanet.mule.connectors.coap.api.error.UriException;
import nl.teslanet.mule.connectors.coap.api.options.RequestOptions;
import nl.teslanet.mule.connectors.coap.internal.exceptions.DiscoverErrorProvider;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalClientErrorResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalEndpointException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidHandlerNameException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidObserverException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidRequestCodeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidResponseCodeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalNoResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalRequestException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalServerErrorResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUnexpectedResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUriException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.ObserverStartErrorProvider;
import nl.teslanet.mule.connectors.coap.internal.exceptions.ObserverStopErrorProvider;
import nl.teslanet.mule.connectors.coap.internal.exceptions.PingErrorProvider;
import nl.teslanet.mule.connectors.coap.internal.exceptions.RequestAsyncErrorProvider;
import nl.teslanet.mule.connectors.coap.internal.exceptions.RequestErrorProvider;


/**
 * This class is a container for CoAP client operations.
 */
public class ClientOperations
{
    //TODO RC review error messages
    /**
     * The Request Processor issues a request on a CoAP server. The processor blocks
     * until a response is received or a timeout occurs.
     * 
     * @param client         The client used for issuing the request.
     * @param requestParams Builder that delivers the request parameters.
     * @param requestOptions The CoAP options to send with the request.
     * @return The result of the request which contains the received server response, if any.
     */
    @MediaType( value= "*/*", strict= false )
    @Throws(
        { RequestErrorProvider.class }
    )
    public Result< InputStream, CoapResponseAttributes > request( @Config
    Client client, @ParameterGroup( name= "Request" )
    RequestParams requestParams,
        @Alias( "request-options" )
        @Optional
        @NullSafe
        @Summary( "The CoAP options to send with the request." )
        @Placement( tab= "Options", order= 1 )
        RequestOptions requestOptions
    )
    {
        String errorMsg= ": request failed.";
        try
        {
            return client.doRequest( requestParams, requestOptions, null );
        }
        catch ( InternalEndpointException e )
        {
            throw new EndpointException( client + errorMsg, e );
        }
        catch ( InternalInvalidRequestCodeException | InternalInvalidHandlerNameException | InternalRequestException e )
        {
            throw new RequestException( client + errorMsg, e );
        }
        catch ( InternalResponseException | InternalInvalidResponseCodeException e )
        {
            throw new ResponseException( client + errorMsg, e );
        }
        catch ( InternalUriException e )
        {
            throw new UriException( client + errorMsg, e );
        }
        catch ( InternalNoResponseException e )
        {
            throw new NoResponseException( client + errorMsg, e );
        }
        catch ( InternalClientErrorResponseException e )
        {
            throw new ClientErrorResponseException( client + errorMsg, e );
        }
        catch ( InternalServerErrorResponseException e )
        {
            throw new ServerErrorResponseException( client + errorMsg, e );
        }
    }

    /**
     * The RequestAsync Processor issues a request on a CoAP server asynchronously.
     * The processor doea not wait for the response and will not block. The handling
     * of a response (if any) is delegated to the response handler.
     * 
     * @param client          The client used for issuing the request.
     * @param responseHandlerParams Builder that delivers the response handler parameters.
     * @param requestParams Builder that delivers the request parameters.
     * @param requestOptions The CoAP options to send with the request.
     */
    @Throws( { RequestAsyncErrorProvider.class } )
    public void requestAsync( @Config
    Client client, @ParameterGroup( name= "Response handling" )
    ResponseHandlerParams responseHandlerParams, @ParameterGroup( name= "Request" )
    RequestParams requestParams,
        @Optional
        @NullSafe
        @Expression( ExpressionSupport.SUPPORTED )
        @Summary( "The CoAP options to send with the request." )
        @Placement( tab= "Options", order= 1 )
        RequestOptions requestOptions
    )
    {
        String errorMsg= ": async request failed.";
        try
        {
            client.doRequest( requestParams, requestOptions, responseHandlerParams );
        }
        catch ( InternalEndpointException e )
        {
            throw new EndpointException( client + errorMsg, e );
        }
        catch ( InternalInvalidRequestCodeException | InternalResponseException | InternalRequestException e )
        {
            throw new RequestException( client + errorMsg, e );
        }
        catch ( InternalInvalidHandlerNameException e )
        {
            throw new InvalidHandlerNameException( client + errorMsg, e );
        }
        catch ( InternalUriException e )
        {
            throw new UriException( client + errorMsg, e );
        }
        catch ( InternalInvalidResponseCodeException e )
        {
            throw new ResponseException( client + errorMsg, e );
        }
        catch ( InternalNoResponseException e )
        {
            throw new NoResponseException( client + errorMsg, e );
        }
        catch ( InternalClientErrorResponseException e )
        {
            throw new ClientErrorResponseException( client + errorMsg, e );
        }
        catch ( InternalServerErrorResponseException e )
        {
            throw new ServerErrorResponseException( client + errorMsg, e );
        }
    }

    // TODO add custom timeout
    /**
     * The Ping processor checks whether a CoAP server is reachable.
     * 
     * @param client         The client to use to issue the request.
     * @param pingParams The request attributes to use.
     * @return {@code True} when the server has responded, {@code False} otherwise.
     */
    @Throws( { PingErrorProvider.class } )
    public Boolean ping( @Config
    Client client, @ParameterGroup( name= "Ping address" )
    PingParams pingParams )
    {
        String errorMsg= ": ping failed.";
        try
        {
            return client.ping( pingParams );
        }
        catch ( ConnectorException | IOException e )
        {
            throw new EndpointException( client + errorMsg, e );
        }
        catch ( InternalUriException e )
        {
            throw new UriException( client + errorMsg, e );
        }
    }

    /**
     * The Discover processor retrieves information about CoAP resources of a
     * server.
     * 
     * @param client             The client to use to issue the request.
     * @param discoverParams The attributes of the discover request
     * @return The resources description on the server that have been discovered.
     */
    @Throws( { DiscoverErrorProvider.class } )
    public Set< DiscoveredResource > discover( @Config
    Client client, @ParameterGroup( name= "Discover address" )
    DiscoverParams discoverParams )
    {
        String errorMsg= ": discover failed.";
        Set< WebLink > links= null;
        try
        {
            links= client.discover( discoverParams );
        }
        catch ( IOException | ConnectorException e )
        {
            throw new EndpointException( client + errorMsg, e );
        }
        catch ( InternalUriException e )
        {
            throw new UriException( client + errorMsg, e );
        }
        catch ( InternalUnexpectedResponseException | InternalInvalidResponseCodeException | InternalResponseException e )
        {
            throw new ResponseException( client + errorMsg, e );
        }
        catch ( InternalNoResponseException e )
        {
            throw new NoResponseException( client + errorMsg, e );
        }
        catch ( InternalClientErrorResponseException e )
        {
            throw new ResponseException( client + errorMsg, e );
        }
        catch ( InternalServerErrorResponseException e )
        {
            throw new ServerErrorResponseException( client + errorMsg, e );
        }
        catch ( InternalInvalidRequestCodeException e )
        {
            throw new InvalidRequestCodeException( client + errorMsg, e );
        }
        catch ( InternalRequestException e )
        {
            throw new RequestException( client + errorMsg, e );
        }
        CopyOnWriteArraySet< DiscoveredResource > resultSet= new CopyOnWriteArraySet< DiscoveredResource >();
        for ( WebLink link : links )
        {
            // TODO RC change members in resourceinfo to list?
            StringBuilder ifBuilder= new StringBuilder();
            Iterator< String > ifIterator= link.getAttributes().getInterfaceDescriptions().iterator();
            while ( ifIterator.hasNext() )
            {
                ifBuilder.append( ifIterator.next() );
                if ( ifIterator.hasNext() ) ifBuilder.append( ", " );
            }

            StringBuilder rtBuilder= new StringBuilder();
            Iterator< String > rtIterator= link.getAttributes().getResourceTypes().iterator();
            while ( rtIterator.hasNext() )
            {
                rtBuilder.append( rtIterator.next() );
                if ( rtIterator.hasNext() ) rtBuilder.append( ", " );
            }
            StringBuilder ctBuilder= new StringBuilder();
            Iterator< String > ctIterator= link.getAttributes().getContentTypes().iterator();
            while ( ctIterator.hasNext() )
            {
                ctBuilder.append( ctIterator.next() );
                if ( ctIterator.hasNext() ) ctBuilder.append( ", " );
            }
            resultSet.add(
                new DiscoveredResource(
                    link.getURI(),
                    link.getAttributes().hasObservable(),
                    link.getAttributes().getTitle(),
                    ifBuilder.toString(),
                    rtBuilder.toString(),
                    link.getAttributes().getMaximumSizeEstimate(),
                    ctBuilder.toString()
                )
            );
        }
        return resultSet;
    }

    /**
     * The Start Observer processor creates an observer on the CoAP client. It
     * starts observing the specified server resource immediately.
     * 
     * @param client             The client instance that starts the observer.
     * @param handlerName        Name of the response handler that will process the
     *                           notification received from server.
     * @param observerAttributes Attributes of the observe request.
     */
    /**
     * @param client
     * @param responseHandlerParams Name of the response handler that will process the notification received from server.
     * @param observerStartBuilder The observe request parameters.
     */
    @Throws( { ObserverStartErrorProvider.class } )
    public void observerStart( @Config
    Client client, @ParameterGroup( name= "Notification handling" )
    ResponseHandlerParams responseHandlerParams, @ParameterGroup( name= "Observe uri" )
    ObserverAddParams observerStartBuilder )
    {
        String errorMsg= ": observer start failed.";
        try
        {
            client.startObserver( observerStartBuilder, responseHandlerParams );
        }
        catch ( InternalUriException e )
        {
            throw new UriException( client + errorMsg, e );
        }
        catch ( InternalInvalidObserverException e )
        {
            throw new InvalidObserverException( client + errorMsg, e );
        }
        catch ( InternalInvalidHandlerNameException e )
        {
            throw new InvalidHandlerNameException( client + errorMsg, e );
        }
    }

    /**
     * Stop a running observer.
     * @param client The client instance that stops the observer.
     * @param observerStopBuilder Parameters of the observer
     */
    @Throws( { ObserverStopErrorProvider.class } )
    public void observerStop( @Config
    Client client, @ParameterGroup( name= "Observe uri" )
    ObserverRemoveParams observerStopBuilder )
    {
        String errorMsg= ": observer stop failed.";
        try
        {
            client.stopObserver( observerStopBuilder );
        }
        catch ( InternalUriException e )
        {
            throw new UriException( client + errorMsg, e );
        }
        catch ( InternalInvalidObserverException e )
        {
            throw new InvalidObserverException( client + errorMsg, e );
        }
    }

    /**
     * This processor returns a list of observers. The list contains the uri's of
     * the active observers of the CoAP client.
     * 
     * @param client The client instance of which the observers are listed.
     * @return the list of observed uri's
     */
    public List< String > observerList( @Config
    Client client )
    {
        List< String > list= client.getRelations().keySet().stream().map( URI::toString ).collect( Collectors.toList() );
        return Collections.unmodifiableList( list );
    }

}
