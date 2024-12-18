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
package nl.teslanet.mule.connectors.coap.internal.server;


import java.io.InputStream;

import javax.inject.Inject;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.mule.runtime.api.exception.DefaultMuleException;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.transformation.TransformationService;
import org.mule.runtime.extension.api.annotation.Alias;
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

import nl.teslanet.mule.connectors.coap.api.CoapResponseCode;
import nl.teslanet.mule.connectors.coap.api.ResponseParams;
import nl.teslanet.mule.connectors.coap.api.attributes.CoapRequestAttributes;
import nl.teslanet.mule.connectors.coap.api.error.InvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalExchangeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidByteArrayValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidResponseCodeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResourceUriException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUnkownOptionException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUriPatternException;
import nl.teslanet.mule.connectors.coap.internal.options.MediaTypeMediator;
import nl.teslanet.mule.connectors.coap.internal.utils.AttributeUtils;
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
public class Listener extends Source< InputStream, CoapRequestAttributes >
{
    private static final Logger LOGGER= LoggerFactory.getLogger( Listener.class );

    @Config
    private Server server;

    /**
     * Mule transformation service.
     */
    @Inject
    private TransformationService transformationService;

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
    @Summary( "The listener will receive GET requests." )
    private boolean get;

    /**
     * The listener listens out for Post requests.
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @DisplayName( "Accept Post requests" )
    @Summary( "The listener will receive POST requests." )
    private boolean post;

    /**
     * The listener listens out for Put requests.
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @DisplayName( "Accept Put requests" )
    @Summary( "The listener will receive PUT requests." )
    private boolean put;

    /**
     * The listener listens out for Delete requests.
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @DisplayName( "Accept Delete requests" )
    @Summary( "The listener will receive DELETE requests." )
    private boolean delete;

    /**
     * The listener listens out for Fetch requests.
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @DisplayName( "Accept Fetch requests" )
    @Summary( "The listener will receive FETCH requests." )
    private boolean fetch;

    /**
     * The listener listens out for Patch requests.
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @DisplayName( "Accept Patch requests" )
    @Summary( "The listener will receive PATCH requests." )
    private boolean patch;

    /**
     * The listener listens out for iPatch requests.
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @DisplayName( "Accept iPatch requests" )
    @Summary( "The listener will receive iPatch requests." )
    private boolean ipatch;

    /**
     * The operational listener that will handle requests.
     */
    OperationalListener operationalListener= null;

    /**
     * Called by Mule to start the listener.
     */
    @Override
    public void onStart( SourceCallback< InputStream, CoapRequestAttributes > sourceCallback ) throws MuleException
    {
        try
        {
            operationalListener= new OperationalListener(
                pathPattern,
                new RequestCodeFlags( get, post, put, delete, fetch, patch, ipatch ),
                sourceCallback
            );
            server.addListener( operationalListener );
        }
        catch ( InternalResourceUriException | InternalUriPatternException e )
        {
            throw new DefaultMuleException( this + " start failed.", e );
        }
        LOGGER.info( "{} started.", this );
    }

    /**
     * Called when processing incoming request was successful.
     * @param response
     * @param callbackContext
     * @throws InternalInvalidByteArrayValueException
     * @throws InternalInvalidResponseCodeException
     * @throws InternalExchangeException 
     * @throws InternalUnkownOptionException When given option was not defined. 
     * @throws InvalidOptionValueException When given option value is invalid.
     */
    @OnSuccess
    @MediaType( value= "*/*", strict= false )
    public void onSuccess(
        @Optional
        @NullSafe
        @Alias( "response" )
        @Placement( tab= "Response", order= 1 )
        ResponseParams response,
        SourceCallbackContext callbackContext
    ) throws InternalInvalidByteArrayValueException,
        InternalInvalidResponseCodeException,
        InternalInvalidOptionValueException,
        InternalExchangeException,
        InternalUnkownOptionException
    {
        CoapResponseCode defaultCoapResponseCode= (CoapResponseCode) callbackContext
            .getVariable( Server.VARNAME_DEFAULT_RESPONSE_CODE )
            .orElseThrow(
                () -> new InternalInvalidResponseCodeException( "Internal error: no defaultCoAPResponseCode provided" )
            );
        Response coapResponse= new Response(
            AttributeUtils.toResponseCode( response.getResponseCode(), defaultCoapResponseCode )
        );
        TypedValue< Object > responsePayload= response.getResponsePayload();
        coapResponse
            .getOptions()
            .setContentFormat( MediaTypeMediator.toContentFormat( responsePayload.getDataType().getMediaType() ) );
        MessageUtils
            .copyOptions( response.getResponseOptionsParams(), coapResponse.getOptions(), transformationService );
        MessageUtils
            .copyOptions(
                response.getResponseOptionsParams().getOtherOptions(),
                coapResponse.getOptions(),
                transformationService
            );
        //TODO add streaming & blockwise cooperation
        try
        {
            coapResponse.setPayload( MessageUtils.toBytes( responsePayload, transformationService ) );
        }
        catch ( Exception e )
        {
            throw new InternalInvalidByteArrayValueException( "Cannot convert payload to byte[]", e );
        }
        ( (CoapExchange) callbackContext
            .getVariable( Server.VARNAME_COAP_EXCHANGE )
            .orElseThrow(
                () -> new InternalExchangeException( "Not able to issue CoAP response: no exchange object provided." )
            ) ).respond( coapResponse );
    }

    /**
     * Returns internal server error when needed.
     * @param sourceResult Provides processing context.
     * @throws InternalExchangeException When the exchange object was absent.
     */
    @OnTerminate
    public void onTerminate( SourceResult sourceResult ) throws InternalExchangeException
    {
        if ( !sourceResult.isSuccess() )
        {
            CoapExchange exchange= (CoapExchange) sourceResult
                .getSourceCallbackContext()
                .getVariable( Server.VARNAME_COAP_EXCHANGE )
                .orElseThrow(
                    () -> new InternalExchangeException(
                        "Not able to issue CoAP internal server error response: no exchange object provided."
                    )
                );
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

    /**
     * Called by Mule when stopping the listener.
     */
    @Override
    public void onStop()
    {
        server.removeListener( operationalListener );
        operationalListener= null;
        LOGGER.info( "{} stopped.", this );
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
