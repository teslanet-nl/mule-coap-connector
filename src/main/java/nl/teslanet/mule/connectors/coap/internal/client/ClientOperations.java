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


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.operation.Result;

import nl.teslanet.mule.connectors.coap.api.DiscoverParams;
import nl.teslanet.mule.connectors.coap.api.DiscoveredResource;
import nl.teslanet.mule.connectors.coap.api.ObserverAddParams;
import nl.teslanet.mule.connectors.coap.api.ObserverExistsParams;
import nl.teslanet.mule.connectors.coap.api.ObserverRemoveParams;
import nl.teslanet.mule.connectors.coap.api.PingParams;
import nl.teslanet.mule.connectors.coap.api.RequestParams;
import nl.teslanet.mule.connectors.coap.api.ResponseHandlerParams;
import nl.teslanet.mule.connectors.coap.api.attributes.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.error.ClientErrorResponseException;
import nl.teslanet.mule.connectors.coap.api.error.EndpointException;
import nl.teslanet.mule.connectors.coap.api.error.InvalidHandlerException;
import nl.teslanet.mule.connectors.coap.api.error.InvalidObserverException;
import nl.teslanet.mule.connectors.coap.api.error.InvalidRequestCodeException;
import nl.teslanet.mule.connectors.coap.api.error.NoResponseException;
import nl.teslanet.mule.connectors.coap.api.error.RequestException;
import nl.teslanet.mule.connectors.coap.api.error.ResponseException;
import nl.teslanet.mule.connectors.coap.api.error.ServerErrorResponseException;
import nl.teslanet.mule.connectors.coap.api.error.UriException;
import nl.teslanet.mule.connectors.coap.api.options.RequestOptionsParams;
import nl.teslanet.mule.connectors.coap.internal.exceptions.DiscoverErrorProvider;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalClientErrorResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalEndpointException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidHandlerException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidObserverException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidRequestCodeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidResponseCodeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalNoResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalRequestException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalServerErrorResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUnexpectedResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUriException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.ObserverAddErrorProvider;
import nl.teslanet.mule.connectors.coap.internal.exceptions.ObserverExistsErrorProvider;
import nl.teslanet.mule.connectors.coap.internal.exceptions.ObserverRemoveErrorProvider;
import nl.teslanet.mule.connectors.coap.internal.exceptions.PingErrorProvider;
import nl.teslanet.mule.connectors.coap.internal.exceptions.RequestAsyncErrorProvider;
import nl.teslanet.mule.connectors.coap.internal.exceptions.RequestErrorProvider;


/**
 * This class is a container for CoAP client operations.
 */
public class ClientOperations
{
    /**
     * Request failure message.
     */
    static final String REQUEST_ERROR_MSG= " failed to execute request.";

    /**
     * Async Request failure message.
     */
    static final String ASYNC_REQUEST_ERROR_MSG= " failed to execute async request.";

    /**
     * Ping failure message.
     */
    static final String PING_ERROR_MSG= " failed to execute ping.";

    /**
     * Discover failure message.
     */
    static final String DISCOVERY_ERROR_MSG= " failed to execute discover.";

    /**
     * Observer add failure message.
     */
    static final String OBSERVER_ADD_ERROR_MSG= " failed to add observer.";

    /**
     * Observer remove failure message.
     */
    static final String OBSERVER_REMOVE_ERROR_MSG= " failed to remove observer.";

    /**
     * Observer exists failure message.
     */
    static final String OBSERVER_EXISTS_ERROR_MSG= " failed to query observer existence.";

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
        @ParameterGroup( name= "Request options" )
        @Summary( "The CoAP options to send with the request." )
        @Placement( tab= "Options", order= 1 )
        RequestOptionsParams requestOptions
    )
    {
        try
        {
            return client.doRequest( requestParams, requestOptions, null );
        }
        catch ( InternalEndpointException e )
        {
            throw new EndpointException( client + REQUEST_ERROR_MSG, e );
        }
        catch ( InternalInvalidRequestCodeException | InternalInvalidHandlerException | InternalRequestException e )
        {
            throw new RequestException( client + REQUEST_ERROR_MSG, e );
        }
        catch ( InternalResponseException | InternalInvalidResponseCodeException e )
        {
            throw new ResponseException( client + REQUEST_ERROR_MSG, e );
        }
        catch ( InternalUriException e )
        {
            throw new UriException( client + REQUEST_ERROR_MSG, e );
        }
        catch ( InternalNoResponseException e )
        {
            throw new NoResponseException( client + REQUEST_ERROR_MSG, e );
        }
        catch ( InternalClientErrorResponseException e )
        {
            throw new ClientErrorResponseException( client + REQUEST_ERROR_MSG, e );
        }
        catch ( InternalServerErrorResponseException e )
        {
            throw new ServerErrorResponseException( client + REQUEST_ERROR_MSG, e );
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
        @ParameterGroup( name= "Request options" )
        @Summary( "The CoAP options to send with the request." )
        @Placement( tab= "Options", order= 1 )
        RequestOptionsParams requestOptions
    )
    {
        try
        {
            client.doRequest( requestParams, requestOptions, responseHandlerParams );
        }
        catch ( InternalEndpointException e )
        {
            throw new EndpointException( client + ASYNC_REQUEST_ERROR_MSG, e );
        }
        catch ( InternalInvalidRequestCodeException | InternalResponseException | InternalRequestException e )
        {
            throw new RequestException( client + ASYNC_REQUEST_ERROR_MSG, e );
        }
        catch ( InternalInvalidHandlerException e )
        {
            throw new InvalidHandlerException( client + ASYNC_REQUEST_ERROR_MSG, e );
        }
        catch ( InternalUriException e )
        {
            throw new UriException( client + ASYNC_REQUEST_ERROR_MSG, e );
        }
        catch ( InternalInvalidResponseCodeException e )
        {
            throw new ResponseException( client + ASYNC_REQUEST_ERROR_MSG, e );
        }
        catch ( InternalNoResponseException e )
        {
            throw new NoResponseException( client + ASYNC_REQUEST_ERROR_MSG, e );
        }
        catch ( InternalClientErrorResponseException e )
        {
            throw new ClientErrorResponseException( client + ASYNC_REQUEST_ERROR_MSG, e );
        }
        catch ( InternalServerErrorResponseException e )
        {
            throw new ServerErrorResponseException( client + ASYNC_REQUEST_ERROR_MSG, e );
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
    public boolean ping( @Config
    Client client, @ParameterGroup( name= "Ping address" )
    PingParams pingParams )
    {
        try
        {
            return client.ping( pingParams );
        }
        catch ( ConnectorException | IOException e )
        {
            throw new EndpointException( client + PING_ERROR_MSG, e );
        }
        catch ( InternalUriException e )
        {
            throw new UriException( client + PING_ERROR_MSG, e );
        }
    }

    /**
     * The Discover processor retrieves information about CoAP resources of a
     * server.
     * 
     * @param client         The client to use to issue the request.
     * @param discoverParams The attributes of the discover request
     * @return The description of resources on the server that have been discovered.
     */
    @Throws( { DiscoverErrorProvider.class } )
    public Set< DiscoveredResource > discover( @Config
    Client client, @ParameterGroup( name= "Discover address" )
    DiscoverParams discoverParams )
    {
        Set< WebLink > links= null;
        try
        {
            links= client.discover( discoverParams );
        }
        catch ( IOException | ConnectorException e )
        {
            throw new EndpointException( client + DISCOVERY_ERROR_MSG, e );
        }
        catch ( InternalUriException e )
        {
            throw new UriException( client + DISCOVERY_ERROR_MSG, e );
        }
        catch (
            InternalClientErrorResponseException | InternalUnexpectedResponseException
            | InternalInvalidResponseCodeException | InternalResponseException e
        )
        {
            throw new ResponseException( client + DISCOVERY_ERROR_MSG, e );
        }
        catch ( InternalNoResponseException e )
        {
            throw new NoResponseException( client + DISCOVERY_ERROR_MSG, e );
        }
        catch ( InternalServerErrorResponseException e )
        {
            throw new ServerErrorResponseException( client + DISCOVERY_ERROR_MSG, e );
        }
        catch ( InternalInvalidRequestCodeException e )
        {
            throw new InvalidRequestCodeException( client + DISCOVERY_ERROR_MSG, e );
        }
        catch ( InternalRequestException e )
        {
            throw new RequestException( client + DISCOVERY_ERROR_MSG, e );
        }
        ConcurrentSkipListSet< DiscoveredResource > resultSet= new ConcurrentSkipListSet<>();
        for ( WebLink link : links )
        {
            resultSet
                .add(
                    new DiscoveredResource(
                        link.getURI(),
                        link.getAttributes().hasObservable(),
                        link.getAttributes().getTitle(),
                        link.getAttributes().getInterfaceDescriptions(),
                        link.getAttributes().getResourceTypes(),
                        link.getAttributes().getMaximumSizeEstimate(),
                        link.getAttributes().getContentTypes()
                    )
                );
        }
        return resultSet;
    }

    /**
     * The ObserverAdd processor creates an observer. 
     * A request to observe the specified resource is sent to the server.
     * The client defaults are used to issue the request.
     * @param client The client instance that the observer belongs to.
     * @param responseHandlerParams Name of the response handler that will process the
     *                           notification received from server.
     * @param observerAddParams Parameters of the observe request. These will override client defaults.
     */
    @Throws( { ObserverAddErrorProvider.class } )
    public void observerAdd( @Config
    Client client, @ParameterGroup( name= "Notification handling" )
    ResponseHandlerParams responseHandlerParams, @ParameterGroup( name= "Observe request" )
    ObserverAddParams observerAddParams )
    {
        try
        {
            client.addObserver( observerAddParams, responseHandlerParams );
        }
        catch ( InternalUriException e )
        {
            throw new UriException( client + OBSERVER_ADD_ERROR_MSG, e );
        }
        catch ( InternalInvalidObserverException e )
        {
            throw new InvalidObserverException( client + OBSERVER_ADD_ERROR_MSG, e );
        }
        catch ( InternalInvalidHandlerException e )
        {
            throw new InvalidHandlerException( client + OBSERVER_ADD_ERROR_MSG, e );
        }
    }

    /**
     * The ObserverRemove processor removes an observer. 
     * A request to terminate observe the specified resource is sent to the server.
     * The client defaults are used to issue the request.
     * @param client The client instance that the observer belongs to.
     * @param observerRemoveParams Parameters of the observe request. These will override client defaults.
     */
    @Throws( { ObserverRemoveErrorProvider.class } )
    public void observerRemove( @Config
    Client client, @ParameterGroup( name= "Observe request" )
    ObserverRemoveParams observerRemoveParams )
    {
        try
        {
            client.removeObserver( observerRemoveParams );
        }
        catch ( InternalUriException e )
        {
            throw new UriException( client + OBSERVER_REMOVE_ERROR_MSG, e );
        }
        catch ( InternalInvalidObserverException e )
        {
            throw new InvalidObserverException( client + OBSERVER_REMOVE_ERROR_MSG, e );
        }
    }

    /**
     * Establish whether an observer with given parameters exists.
     * 
     * @param client The client instance of which the observers are searched.
     * @param observerExistsParams The the list of observer uri parameters.
     * @return True when the observer exists, otherwise False.
     */
    @Throws( { ObserverExistsErrorProvider.class } )
    public boolean observerExists( @Config
    Client client, @ParameterGroup( name= "Observer uri" )
    ObserverExistsParams observerExistsParams )
    {
        try
        {
            return client.observerExists( observerExistsParams );
        }
        catch ( InternalUriException e )
        {
            throw new UriException( client + OBSERVER_EXISTS_ERROR_MSG, e );
        }
    }

    /**
     * This processor returns a list of observers. The list contains the uri's of
     * the active observers of the CoAP client.
     * 
     * @param client The client instance of which the observers are listed.
     * @return the set of observed uri's
     */
    public Set< String > observerList( @Config
    Client client )
    {
        return client
            .getRelations()
            .keySet()
            .stream()
            .map( URI::toString )
            .collect( Collectors.toCollection( ConcurrentSkipListSet< String >::new ) );
    }
}
