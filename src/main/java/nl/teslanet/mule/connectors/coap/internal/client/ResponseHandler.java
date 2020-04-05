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


import java.io.InputStream;

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.teslanet.mule.connectors.coap.api.ReceivedResponseAttributes;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidHandlerNameException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.StartException;


/**
 * The Response Handler message source receives responses on asynchronous CoAP client requests and observe notifications.
 * The CoAP messages received are delivered to the flow.
 */
@MediaType(value= MediaType.APPLICATION_OCTET_STREAM, strict= false)
public class ResponseHandler extends Source< InputStream, ReceivedResponseAttributes >
{

    private final Logger LOGGER= LoggerFactory.getLogger( ResponseHandler.class );

    @Config
    private Client client;

    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private String handlerName;

    /* (non-Javadoc)
     * @see org.mule.runtime.extension.api.runtime.source.Source#onStart(org.mule.runtime.extension.api.runtime.source.SourceCallback)
     */
    @Override
    public void onStart( SourceCallback< InputStream, ReceivedResponseAttributes > sourceCallback ) throws MuleException
    {
        try
        {
            client.addHandler( handlerName, sourceCallback );
        }
        catch ( InternalInvalidHandlerNameException e )
        {
            throw new StartException( e );
        }
        LOGGER.info( "Start response handler '" + handlerName + "' for " + client.toString() );
    }

    /* (non-Javadoc)
     * @see org.mule.runtime.extension.api.runtime.source.Source#onStop()
     */
    @Override
    public void onStop()
    {
        client.removeHandler( handlerName );
        LOGGER.info( "Stop response handler '" + handlerName + "' for " + client.toString() );

    }
    //    @OnSuccess
    //    @MediaType(value= "*/*", strict= false)
    //    public void onSuccess( @Content TypedValue< byte[] > responseBody, ResponseAttributes attributes, SourceCallbackContext callbackContext ) throws Exception
    //    {
    //        {
    //            CoapExchange exchange= (CoapExchange) callbackContext.getVariable( "CoapExchange" ).get();
    //            Response response= new Response( AttibuteUtils.toResponseCode( attributes.getResponseCode() ) );
    //            //TODO give user control
    //            response.getOptions().setContentFormat( MediaTypeMediator.toContentFormat( responseBody.getDataType().getMediaType() ) );
    //            if ( attributes.getOptions() != null )
    //            {
    //                Options.fillOptionSet( response.getOptions(), attributes.getOptions(), false );
    //            }
    //            response.setPayload( responseBody.getValue() );
    //            exchange.respond( response );
    //        }
    //    }
    //
    //    @OnTerminate
    //    public void onTerminate( SourceResult sourceResult )
    //    {
    //        if ( !sourceResult.isSuccess() )
    //        {
    //            CoapExchange exchange= (CoapExchange) sourceResult.getSourceCallbackContext().getVariable( "CoapExchange" ).get();
    //            if ( sourceResult.getInvocationError().isPresent() )
    //            {
    //                exchange.respond( ResponseCode.INTERNAL_SERVER_ERROR, "EXCEPTION IN PROCESSING REQUEST" );
    //            }
    //            else if ( sourceResult.getResponseError().isPresent() )
    //            {
    //                exchange.respond( ResponseCode.INTERNAL_SERVER_ERROR, "EXCEPTION IN PROCESSING FLOW" );
    //            }
    //            else
    //            {
    //                exchange.respond( ResponseCode.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR" );
    //            }
    //        }
    //    }

}
