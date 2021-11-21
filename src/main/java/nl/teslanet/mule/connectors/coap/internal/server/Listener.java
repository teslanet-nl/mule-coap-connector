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
package nl.teslanet.mule.connectors.coap.internal.server;


import java.io.IOException;
import java.io.InputStream;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.mule.runtime.api.exception.DefaultMuleException;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.execution.OnSuccess;
import org.mule.runtime.extension.api.annotation.execution.OnTerminate;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.annotation.source.EmitsResponse;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.mule.runtime.extension.api.runtime.source.SourceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.teslanet.mule.connectors.coap.api.ReceivedRequestAttributes;
import nl.teslanet.mule.connectors.coap.api.ResponseBuilder;
import nl.teslanet.mule.connectors.coap.api.ResponseBuilder.CoAPResponseCode;
import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;
import nl.teslanet.mule.connectors.coap.api.options.ResponseOptions;
import nl.teslanet.mule.connectors.coap.internal.attributes.AttributeUtils;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidByteArrayValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidResponseCodeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResourceUriException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUriPatternException;
import nl.teslanet.mule.connectors.coap.internal.options.MediaTypeMediator;
import nl.teslanet.mule.connectors.coap.internal.utils.MessageUtils;


/**
 * The Listener message source receives requests on resources of the CoAP server.
 * The requests on the resources that apply to the resource uri-pattern and the request-methods specified, will be listened to.
 * When multiple listeners apply the listener with the most specific uri-pattern 
 * will process the requests and deliver it to the flow. When patterns are equally specific, one of the listeners will be selected.  
 */
@Alias( "listener" )
@EmitsResponse
@MediaType( value= MediaType.APPLICATION_OCTET_STREAM, strict= false )
public class Listener extends Source< InputStream, ReceivedRequestAttributes >
{
    private static final Logger logger= LoggerFactory.getLogger( Listener.class );

    @Config
    private Server server;

    /**
     * The pathPattern defines the resources the listener listens on.
     * The listener will receive the requests to process for a resource when it 
     * has the most specific pattern that complies to the resources path.
     */
    @Parameter
    @Optional( defaultValue= "/*" )
    @Summary( "The pathPattern defines the resources the listener listens on." )
    private String pathPattern;

    /**
     * The listener listens out for Get requests.
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @DisplayName( "Accept Get requests" )
    @Summary( "The listener will only receive GET requests." )
    private boolean get;

    /**
     * The listener listens out for Post requests.
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @DisplayName( "Accept Post requests" )
    @Summary( "The listener will only receive POST requests." )
    private boolean post;

    /**
     * The listener listens out for Put requests.
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @DisplayName( "Accept Put requests" )
    @Summary( "The listener will only receive PUT requests." )
    private boolean put;

    /**
     * The listener listens out for Delete requests.
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @DisplayName( "Accept Delete requests" )
    @Summary( "The listener will only receive DELETE requests." )
    private boolean delete;

    /**
     * The operational listener that will handle requests.
     */
    OperationalListener operationalListener= null;

    @Override
    public void onStart( SourceCallback< InputStream, ReceivedRequestAttributes > sourceCallback ) throws MuleException
    {
        try
        {
            operationalListener= new OperationalListener( pathPattern, new RequestCodeFlags( get, post, put, delete ), sourceCallback );
            server.addListener( operationalListener );
        }
        catch ( InternalResourceUriException | InternalUriPatternException e )
        {
            new DefaultMuleException( this + " start failed.", e );
        }
        logger.info( this + " started." );
    }

    @OnSuccess
    @MediaType( value= "*/*", strict= false )
    public void onSuccess(
        @Optional
        @NullSafe
        @Alias( "response" )
        @Placement( tab= "Response", order= 1 )
        ResponseBuilder response,
        @Optional
        @NullSafe
        @Alias( "response-options" )
        @Expression( ExpressionSupport.SUPPORTED )
        @Summary( "The CoAP options to send with the response." )
        @Placement( tab= "Response", order= 2 )
        ResponseOptions responseOptions,
        SourceCallbackContext callbackContext
    ) throws InternalInvalidByteArrayValueException,
        InternalInvalidResponseCodeException,
        IOException,
        InvalidETagException,
        InternalInvalidOptionValueException
    {
        CoAPResponseCode defaultCoapResponseCode= (CoAPResponseCode) callbackContext.getVariable( "defaultCoAPResponseCode" ).get();
        Response coapResponse= new Response( AttributeUtils.toResponseCode( response.getResponseCode(), defaultCoapResponseCode ) );
        //TODO give user control
        TypedValue< Object > responsePayload= response.getResponsePayload();
        coapResponse.getOptions().setContentFormat( MediaTypeMediator.toContentFormat( responsePayload.getDataType().getMediaType() ) );
        if ( responseOptions != null )
        {
            MessageUtils.copyOptions( responseOptions, coapResponse.getOptions() );
        }
        //TODO add streaming & blockwise cooperation
        try
        {
            coapResponse.setPayload( MessageUtils.payloadToByteArray( responsePayload ) );
        }
        catch ( Exception e )
        {
            throw new InternalInvalidByteArrayValueException( "Cannot convert payload to byte[]", e );
        }
        ( (CoapExchange) callbackContext.getVariable( "CoapExchange" ).get() ).respond( coapResponse );
    }

    @OnTerminate
    public void onTerminate( SourceResult sourceResult )
    {
        if ( !sourceResult.isSuccess() )
        {
            CoapExchange exchange= (CoapExchange) sourceResult.getSourceCallbackContext().getVariable( "CoapExchange" ).get();
            if ( sourceResult.getInvocationError().isPresent() )
            {
                exchange.respond( ResponseCode.INTERNAL_SERVER_ERROR, "EXCEPTION IN PROCESSING REQUEST" );
            }
            else if ( sourceResult.getResponseError().isPresent() )
            {
                exchange.respond( ResponseCode.INTERNAL_SERVER_ERROR, "EXCEPTION IN PROCESSING FLOW" );
            }
            else
            {
                exchange.respond( ResponseCode.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR" );
            }
        }
    }

    @Override
    public void onStop()
    {
        server.removeListener( operationalListener );
        operationalListener= null;
        logger.info( this + " stopped." );
    }

    /**
     * Gets the server
     * @return the config
     */
    public Server getServer()
    {
        return server;
    }

    /**
     * Gets the uriPattern describing the resources the listener listens on.
     * @return the uriPattern
     */
    public String getUriPattern()
    {
        return pathPattern;
    }

    /**
     * @return true if the listener listens on Delete requests, false otherwise.
     */
    public boolean isDelete()
    {
        return delete;
    }

    /**
     * @return true if the listener listens on Get requests, false otherwise.
     */
    public boolean isGet()
    {
        return get;
    }

    /**
     * @return true if the listener listens on Post requests, false otherwise.
     */
    public boolean isPost()
    {
        return post;
    }

    /**
     * @return true if the listener listens on Put requests, false otherwise.
     */
    public boolean isPut()
    {
        return put;
    }

    /**
     * Get String repesentation.
     */
    @Override
    public String toString()
    {
        return "CoAP Listener { " + server.getServerName() + "::" + pathPattern + " }";
    }
}
