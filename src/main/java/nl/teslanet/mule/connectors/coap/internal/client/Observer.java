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
import java.util.List;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.teslanet.mule.connectors.coap.api.ReceivedResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.query.QueryParamConfig;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUriException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.StartException;


/**
 * The Observer message source observes a CoAP resource on a server. 
 * The CoAP notifications received from the server are delivered to the flow. 
 * The resource is observed during runtime of the flow. When 
 * an observe relation with the server could not be established or is lost, 
 * the flow is notified of the error and the observe request is retried continuously. 
 */
@Alias("observer")
@MediaType(value= MediaType.APPLICATION_OCTET_STREAM, strict= false)
public class Observer extends Source< InputStream, ReceivedResponseAttributes >
{
    private final Logger LOGGER= LoggerFactory.getLogger( Observer.class );

    @Config
    private Client client;

    /**
     * When true the server is expected to acknowledge reception of the observe request.
     */
    @Parameter
    @Optional(defaultValue= "true")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @Summary("When true the server is expected to acknowledge reception of the observe request.")
    private boolean confirmable= true;

    /**
     * The hostname or ip of the server to observe.
     * This overrides client host configuration.
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @Summary("The hostname or ip of the server to observe. This overrides client host configuration.")
    private String host= null;

    /**
     * The port the server is listening on.
     * This overrides client port configuration. 
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @Summary("The port the server is listening on. This overrides client port configuration.")
    private Integer port= null;

    /**
    * The path on the server of the resource to observe.
    */
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @Example(value= "/some/resource/path")
    @Summary("The path on the server of the resource to observe.")
    private String path= null;

    /**
     * The query parameters to send with the observe request.
     */
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @Optional
    @DisplayName("Query parameters")
    @Summary("The query parameters to send with the observe request.")
    private List< QueryParamConfig > queryParamConfigs= null;

    //TODO add options confi
    /**
     * The CoAP relation that has been established
     */
    private CoapObserveRelation coapRelation= null;;

    /* (non-Javadoc)
     * @see org.mule.runtime.extension.api.runtime.source.Source#onStart(org.mule.runtime.extension.api.runtime.source.SourceCallback)
     */
    @Override
    public void onStart( SourceCallback< InputStream, ReceivedResponseAttributes > sourceCallback ) throws MuleException
    {
        String uri;
        try
        {
            uri= client.getURI( host, port, path, client.toQueryString( queryParamConfigs ) ).toString();
        }
        catch ( InternalUriException e )
        {
            throw new StartException( e );
        }

        //TODO add client/observer/handler name for logging
        CoapHandler handler= new CoapHandler()
            {
                @Override
                public void onError()
                {
                    LOGGER.warn( "permanent observer failed on resource {} ", uri );
                    if ( coapRelation != null )
                    {
                        //TODO wait time?
                        if ( coapRelation.isCanceled() )
                        {
                            coapRelation= client.doObserveRequest( confirmable, uri, this );
                            LOGGER.info( "permanent observer recreated on {}", uri );
                        }
                        else
                        {
                            coapRelation.reregister();
                            LOGGER.info( "permanent observer reregistrered on {}", uri );
                        } ;
                    }
                    try
                    {
                        client.processMuleFlow( uri, Code.GET, null, sourceCallback );
                    }
                    catch ( InternalResponseException e )
                    {
                        LOGGER.error( "Could not proces an error on notification", e );
                    }
                }

                @Override
                public void onLoad( CoapResponse response )
                {
                    if ( !coapRelation.isCanceled() )
                    {
                        try
                        {
                            client.processMuleFlow( uri, Code.GET, response, sourceCallback );
                        }
                        catch ( InternalResponseException e )
                        {
                            LOGGER.error( "Could not proces a notification", e );
                        }
                    }
                }

            };
        coapRelation= client.doObserveRequest( confirmable, uri, handler );
        LOGGER.info( "permanent observer started on resource {}", uri );
    }

    /* (non-Javadoc)
     * @see org.mule.runtime.extension.api.runtime.source.Source#onStop()
     */
    @Override
    public void onStop()
    {
        String uri;
        try
        {
            uri= client.getURI( host, port, path, client.toQueryString( queryParamConfigs ) ).toString();
        }
        catch ( InternalUriException e )
        {
            // throw nothing, set something for logging
            uri= coapRelation.toString();
        }
        if ( coapRelation != null )
        {
            //TODO make type of canceling configurable
            coapRelation.proactiveCancel();
            coapRelation= null;
        }
        LOGGER.info( "permanent observer started on resource { " + uri + " }" );
    }
}
