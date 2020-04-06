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


import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

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

import nl.teslanet.mule.connectors.coap.api.DiscoverAttributes;
import nl.teslanet.mule.connectors.coap.api.DiscoveredResource;
import nl.teslanet.mule.connectors.coap.api.ObserverAttributes;
import nl.teslanet.mule.connectors.coap.api.PingAttributes;
import nl.teslanet.mule.connectors.coap.api.ReceivedResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.RequestBuilder;
import nl.teslanet.mule.connectors.coap.api.error.EndpointException;
import nl.teslanet.mule.connectors.coap.api.error.ExchangeException;
import nl.teslanet.mule.connectors.coap.api.error.InvalidHandlerNameException;
import nl.teslanet.mule.connectors.coap.api.error.InvalidObserverException;
import nl.teslanet.mule.connectors.coap.api.error.InvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.api.error.InvalidRequestCodeException;
import nl.teslanet.mule.connectors.coap.api.error.MalformedUriException;
import nl.teslanet.mule.connectors.coap.api.options.RequestOptions;
import nl.teslanet.mule.connectors.coap.internal.attributes.AttibuteUtils;
import nl.teslanet.mule.connectors.coap.internal.exceptions.DiscoverErrorProvider;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidByteArrayValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidHandlerNameException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidObserverException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidRequestCodeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalMalformedUriException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalNoResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUnexpectedResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.ObserverStartErrorProvider;
import nl.teslanet.mule.connectors.coap.internal.exceptions.ObserverStopErrorProvider;
import nl.teslanet.mule.connectors.coap.internal.exceptions.PingErrorProvider;
import nl.teslanet.mule.connectors.coap.internal.exceptions.RequestAsyncErrorProvider;
import nl.teslanet.mule.connectors.coap.internal.exceptions.RequestErrorProvider;


//TODO add error tests
/**
 * This class is a container for CoAP server operations.
 */
public class ClientOperations
{
    /**
     * The Request Processor issues a request on a CoAP server. The processor blocks
     * until a response is received or a timeout occurs.
     * 
     * @param client         The client to use to issue the request.
     * @param requestBuilder The request attributes
     * @param requestPayload The payload of the request
     * @return The result containing the response received (if any) and the context
     *         of the request.
     */
    /**
     * The Request Processor issues a request on a CoAP server. The processor blocks
     * until a response is received or a timeout occurs.
     * 
     * @param client
     * @param requestBuilder The request attributes.
     * @param requestOptions The request options.
     * @return the result of the request which contains the received server response
     *         - if any.
     */
    @MediaType(value= "*/*", strict= false)
    @Throws({ RequestErrorProvider.class })
    public Result< InputStream, ReceivedResponseAttributes > request(
        @Config Client client,
        @ParameterGroup(name= "Request") RequestBuilder requestBuilder,
        @Optional @NullSafe @Summary("The CoAP options to send with the request.") @Placement(tab= "Options", order= 1) RequestOptions requestOptions )
    {
        try
        {
            String uri= client.getURI(
                requestBuilder.getHost(),
                requestBuilder.getPort(),
                requestBuilder.getPath(),
                client.toQueryString( requestBuilder.getQueryParams() ) ).toString();
            return client.doRequest(
                requestBuilder.isConfirmable(),
                AttibuteUtils.toCode( requestBuilder.getRequestCode() ),
                uri,
                requestBuilder.getRequestPayload(),
                requestOptions,
                null );
        }
        catch ( InternalInvalidHandlerNameException e )
        {
            // TODO should not occur, restructure doRequest to solve
            throw new InvalidHandlerNameException( e.getMessage(), e );
        }
        catch ( ConnectorException e )
        {
            throw new EndpointException( e.getMessage(), e );
        }
        catch ( IOException e )
        {
            throw new ExchangeException( e.getMessage(), e );
        }
        catch ( InternalMalformedUriException e )
        {
            throw new MalformedUriException( e.getMessage(), e );
        }
        catch ( InternalInvalidRequestCodeException e )
        {
            throw new InvalidRequestCodeException( e.getMessage(), e );
        }
        catch ( InternalInvalidOptionValueException e )
        {
            throw new InvalidOptionValueException( e.getMessage(), e );
        }
        catch ( InternalInvalidByteArrayValueException e )
        {
            throw new InvalidOptionValueException( e.getMessage(), e );
        }
    }

    /**
     * The RequestAsync Processor issues a request on a CoAP server asynchronously.
     * The processor doea not wait for the response and will not block. The handling
     * of a response (if any) is delegated to the response handler.
     * 
     * @param client          The client to use for the request.
     * @param responseHandler Name of the handler that will receive the response.
     * @param requestBuilder  The request attributes.
     * @param requestPayload  The payload of the request.
     */
    @Throws({ RequestAsyncErrorProvider.class })
    public void requestAsync(
        @Config Client client,
        @Alias( "responseHandler" ) @Expression(ExpressionSupport.SUPPORTED) @Summary("The name of the Response handler that will receive the response. The handler must exist.") String responseHandler,
        @ParameterGroup(name= "Request") RequestBuilder requestBuilder,
        @Optional @NullSafe @Expression(ExpressionSupport.SUPPORTED) @Summary("The CoAP options to send with the request.") @Placement(tab= "Options", order= 1) RequestOptions requestOptions )
    {
        try
        {
            String uri= client.getURI(
                requestBuilder.getHost(),
                requestBuilder.getPort(),
                requestBuilder.getPath(),
                client.toQueryString( requestBuilder.getQueryParams() ) ).toString();
            client.doRequest(
                requestBuilder.isConfirmable(),
                AttibuteUtils.toCode( requestBuilder.getRequestCode() ),
                uri,
                requestBuilder.getRequestPayload(),
                requestOptions,
                responseHandler );
        }
        catch ( InternalInvalidHandlerNameException e )
        {
            throw new InvalidHandlerNameException( e.getMessage(), e );
        }
        catch ( ConnectorException e )
        {
            throw new EndpointException( e.getMessage(), e );
        }
        catch ( IOException e )
        {
            throw new ExchangeException( e.getMessage(), e );
        }
        catch ( InternalMalformedUriException e )
        {
            throw new MalformedUriException( e.getMessage(), e );
        }
        catch ( InternalInvalidRequestCodeException e )
        {
            throw new InvalidRequestCodeException( e.getMessage(), e );
        }
        catch ( InternalInvalidOptionValueException e )
        {
            throw new InvalidOptionValueException( e.getMessage(), e );
        }
        catch ( InternalInvalidByteArrayValueException e )
        {
            throw new InvalidOptionValueException( e.getMessage(), e );
        }
    }

    // TODO add custom timeout
    /**
     * The Ping processor checks whether a CoAP server is reachable.
     * 
     * @param client         The client to use to issue the request.
     * @param pingAttributes The request attributes to use.
     * @return {@code True} when the server has responded, {@code False} otherwise.
     */
    @Throws({ PingErrorProvider.class })
    public Boolean ping( @Config Client client, @ParameterGroup(name= "Ping") PingAttributes pingAttributes )
    {
        try
        {
            return client.ping( pingAttributes.getHost(), pingAttributes.getPort() );
        }
        catch ( InternalMalformedUriException e )
        {
            throw new MalformedUriException( e.getMessage() );
        }
        catch ( RuntimeException e )
        {
            // could be interrupted which is no problem
        }
        catch ( ConnectorException e )
        {
            // not reachable
        }
        catch ( IOException e )
        {
            // not reachable
        }
        return false;
    }

    /**
     * The Discover processor retrieves information about CoAP resources of a
     * server.
     * 
     * @param client             The client to use to issue the request.
     * @param discoverAttributes The attributes of the discover request
     * @return The resources description on the server that have been discovered.
     */
    @Throws({ DiscoverErrorProvider.class })
    public CopyOnWriteArraySet< DiscoveredResource > discover( @Config Client client, @ParameterGroup(name= "Discover") DiscoverAttributes discoverAttributes )
    {
        Set< WebLink > links= null;
        try
        {
            links= client.discover( discoverAttributes.isConfirmable(), discoverAttributes.getHost(), discoverAttributes.getPort(), discoverAttributes.getQueryParams() );
        }
        catch ( InternalMalformedUriException e )
        {
            throw new MalformedUriException( e.getMessage() );
        }
        catch ( InternalNoResponseException e )
        {
            throw new ExchangeException( e.getMessage() );
        }
        catch ( InternalUnexpectedResponseException e )
        {
            throw new ExchangeException( e.getMessage() );
        }
        catch ( ConnectorException e )
        {
            throw new EndpointException( e );
        }
        catch ( IOException e )
        {
            throw new ExchangeException( e );
        }
        CopyOnWriteArraySet< DiscoveredResource > resultSet= new CopyOnWriteArraySet< DiscoveredResource >();
        for ( WebLink link : links )
        {
            // TODO change members in resourceinfo to list?
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
                    ctBuilder.toString() ) );
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
    @Throws({ ObserverStartErrorProvider.class })
    public void observerStart( @Config Client client, String handlerName, @ParameterGroup(name= "Observer") ObserverAttributes observerAttributes )
    {
        try
        {
            client.startObserver(
                handlerName,
                observerAttributes.isConfirmable(),
                observerAttributes.getHost(),
                observerAttributes.getPort(),
                observerAttributes.getPath(),
                observerAttributes.getQueryParams() );
        }
        catch ( InternalMalformedUriException e )
        {
            throw new MalformedUriException( e.getMessage() );
        }
        catch ( InternalInvalidObserverException e )
        {
            throw new InvalidObserverException( e.getMessage() );
        }
        catch ( InternalInvalidHandlerNameException e )
        {
            throw new InvalidHandlerNameException( e.getMessage() );
        }
    }

    /**
     * Stop a running observer.
     * 
     * @param client             The client instance that stops the observer.
     * @param observerAttributes Attributes of the observe request
     */
    @Throws({ ObserverStopErrorProvider.class })
    public void observerStop( @Config Client client, @ParameterGroup(name= "Observer") ObserverAttributes observerAttributes )
    {
        // TODO confirmable is not applicable
        try
        {
            client.stopObserver( observerAttributes.getHost(), observerAttributes.getPort(), observerAttributes.getPath(), observerAttributes.getQueryParams() );
        }
        catch ( InternalMalformedUriException e )
        {
            throw new MalformedUriException( e.getMessage(), e );
        }
        catch ( InternalInvalidObserverException e )
        {
            throw new InvalidObserverException( e.getMessage() );
        }
    }

    /**
     * This processor returns a list of observers. The list contains the uri's of
     * the active observers of the CoAP client.
     * 
     * @param client The client instance of which the observers are listed.
     * @return the list of observed uri's
     */
    public List< String > observerList( @Config Client client )
    {
        CopyOnWriteArrayList< String > list= new CopyOnWriteArrayList< String >();
        list.addAll( client.getRelations().keySet() );
        return list;
    }

}
