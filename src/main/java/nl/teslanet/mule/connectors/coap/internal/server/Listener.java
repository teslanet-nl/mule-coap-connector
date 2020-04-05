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
package nl.teslanet.mule.connectors.coap.internal.server;


import java.io.InputStream;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.mule.runtime.api.exception.DefaultMuleException;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.execution.OnSuccess;
import org.mule.runtime.extension.api.annotation.execution.OnTerminate;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.annotation.source.EmitsResponse;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.mule.runtime.extension.api.runtime.source.SourceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.teslanet.mule.connectors.coap.api.CoAPResponseCode;
import nl.teslanet.mule.connectors.coap.api.ReceivedRequestAttributes;
import nl.teslanet.mule.connectors.coap.api.ResponseBuilder;
import nl.teslanet.mule.connectors.coap.api.error.InvalidResourceUriException;
import nl.teslanet.mule.connectors.coap.api.options.ResponseOptions;
import nl.teslanet.mule.connectors.coap.internal.attributes.AttibuteUtils;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidByteArrayValueException;
import nl.teslanet.mule.connectors.coap.internal.options.CoAPOptions;
import nl.teslanet.mule.connectors.coap.internal.options.MediaTypeMediator;


/**
 * The Listener message source receives requests on resources of the CoAP server.
 * The requests on the resources that apply to the resource uri-pattern given, will be listened.
 * When multiple listeners apply the listener with the most specific uri-pattern 
 * will process the requests and deliver it to the flow.
 */
@Alias("listener")
@EmitsResponse
@MediaType(value= MediaType.APPLICATION_OCTET_STREAM, strict= false)
public class Listener extends Source< InputStream, ReceivedRequestAttributes >
{
    private final Logger LOGGER= LoggerFactory.getLogger( Listener.class );

    @Config
    private Server server;

    /**
     * The uriPattern defines the resources the listener listens on.
     * The listener will get requests to process for a resource when it 
     * has the most specific pattern that complies to the resources path. 
     */
    @Parameter
    @Optional(defaultValue= "/*")
    private String uriPattern;

    /**
     * The operational listener that will handle requests.
     */
    OperationalListener operationalListener= null;

    @Override
    public void onStart( SourceCallback< InputStream, ReceivedRequestAttributes > sourceCallback ) throws MuleException
    {
        try
        {
            operationalListener= new OperationalListener( uriPattern, sourceCallback );
        }
        catch ( InvalidResourceUriException e )
        {
            new DefaultMuleException( "CoAP listener on { " + uriPattern + " } could not be started.", e );
        }
        server.addListener( operationalListener );
        LOGGER.info( "listener start: " + uriPattern );
    }

    @OnSuccess
    @MediaType(value= "*/*", strict= false)
    public void onSuccess(
        @Placement(tab= "Response", order= 1) ResponseBuilder response,
        @Optional @Expression(ExpressionSupport.SUPPORTED) @Placement(tab= "Response Options", order= 1) @Summary("The CoAP options of the response.") ResponseOptions responseOptions,
        SourceCallbackContext callbackContext ) throws Exception
    {
        {
            CoAPResponseCode defaultCoapResponseCode= (CoAPResponseCode) callbackContext.getVariable( "defaultCoAPResponseCode" ).get();
            CoapExchange exchange= (CoapExchange) callbackContext.getVariable( "CoapExchange" ).get();
            Response cfResponse= new Response( AttibuteUtils.toResponseCode( response.getResponseCode(), defaultCoapResponseCode ) );
            //TODO give user control
            TypedValue< InputStream > payload= response.getResponsePayload();
            cfResponse.getOptions().setContentFormat( MediaTypeMediator.toContentFormat( payload.getDataType().getMediaType() ) );
            if ( responseOptions != null )
            {
                CoAPOptions.copyOptions( responseOptions, cfResponse.getOptions(), false );
            }
            //TODO add streaming & blockwise cooperation
            try
            {
                cfResponse.setPayload( IOUtils.toByteArray( payload.getValue() ));
            }
            catch ( RuntimeException e )
            {
                throw new InternalInvalidByteArrayValueException( "Cannot convert payload to byte[]", e );
            }
            finally
            {
                IOUtils.closeQuietly( payload.getValue() );
            }
            exchange.respond( cfResponse );
        }
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
        LOGGER.info( "listener stop" );

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
     * @param config the config to set
     */
    //    public void setServer( Server config )
    //    {
    //        this.server= server;
    //    }

    /**
     * Gets the uriPattern describing the resources the listener listens on.
     * @return the uriPattern
     */
    public String getUriPattern()
    {
        return uriPattern;
    }

    /**
     * Sets the uriPattern describing the resources the listener listens on.
     * @param uriPattern the uriPattern to set
     */
    //    public void setUriPattern( String uriPattern )
    //    {
    //        this.uriPattern= uriPattern;
    //    }

}
