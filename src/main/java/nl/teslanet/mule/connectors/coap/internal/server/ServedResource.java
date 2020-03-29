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


import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;

import nl.teslanet.mule.connectors.coap.api.CoAPResponseCode;
import nl.teslanet.mule.connectors.coap.api.ReceivedRequestAttributes;
import nl.teslanet.mule.connectors.coap.api.ResourceConfig;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.options.MediaTypeMediator;
import nl.teslanet.mule.connectors.coap.internal.options.CoAPOptions;


/**
 * The ServedResource class represents a CoAP resource that is active on the server.
 * A ServedResource object handles all requests from clients on the CoAP resource.
 */
public class ServedResource extends CoapResource
{
    //TODO review logging
    /** The logger. */
    protected final Logger LOGGER= Logger.getLogger( ServedResource.class.getCanonicalName() );

    /**
     * The callback of the messagesource.
     * It is used to handle messages over to the Mule flow that should process the request.
     */
    private SourceCallback< byte[], ReceivedRequestAttributes > callback= null;

    /**
     * Flag that indicates whether the resource accepts Get requests.
     */
    private Boolean get= false;

    /**
     * Flag that indicates whether the resource accepts Put requests.
     */
    private Boolean put= false;

    /**
     * Flag that indicates whether the resource accepts Post requests.
     */
    private Boolean post= false;

    /**
     * Flag that indicates whether the resource accepts Delete requests.
     */
    private Boolean delete= false;

    /**
     * Flag that indicates whether the resource should acknowledge before processing the request.
     */
    private Boolean earlyAck= false;

    /**
     * Constuctor that creates a ServedResource object according to given configuration.
     * The ServedResource and its child resources will be constructed.
     * @param resourceConfig the description of the resource to create. 
     */
    public ServedResource( ResourceConfig resourceConfig )
    {
        super( resourceConfig.getResourceName() );

        //TODO make use of visible/invisible?

        get= resourceConfig.isGet();
        put= resourceConfig.isPut();
        post= resourceConfig.isPost();
        delete= resourceConfig.isDelete();
        earlyAck= resourceConfig.isEarlyAck();

        if ( resourceConfig.isObservable() )
        {
            setObservable( true );
            getAttributes().setObservable();
        }
        else
        {
            setObservable( false );
        }

        //process info configuration
        if ( resourceConfig.getInfo() != null )
        {
            if ( resourceConfig.getInfo().getTitle() != null )
            {
                getAttributes().setTitle( resourceConfig.getInfo().getTitle() );
            } ;
            if ( resourceConfig.getInfo().getRt() != null )
            {
                for ( String rt : resourceConfig.getInfo().getRt().split( "\\s*,\\s*" ) )
                {
                    getAttributes().addResourceType( rt );
                }
            } ;
            if ( resourceConfig.getInfo().getIfdesc() != null )
            {
                for ( String ifdesc : resourceConfig.getInfo().getIfdesc().split( "\\s*,\\s*" ) )
                {
                    getAttributes().addInterfaceDescription( ifdesc );
                }
            } ;
            if ( resourceConfig.getInfo().getCt() != null )
            {
                for ( String ct : resourceConfig.getInfo().getCt().split( "\\s*,\\s*" ) )
                {
                    getAttributes().addContentType( Integer.parseInt( ct ) );
                }
            }
            if ( resourceConfig.getInfo().getSz() != null )
            {
                getAttributes().setMaximumSizeEstimate( resourceConfig.getInfo().getSz() );
            }
        }
        //process resource configuration
        if ( resourceConfig.getSubResources() != null )
        {
            //also create children (recursively) 
            for ( ResourceConfig childResourceConfig : resourceConfig.getSubResources() )
            {
                ServedResource child= new ServedResource( childResourceConfig );
                add( child );
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.californium.core.CoapResource#handleGET(org.eclipse.californium.core.server.resources.CoapExchange)
     */
    @Override
    public void handleGET( CoapExchange exchange )
    {
        if ( !isGet() )
        {
            // default implementation is to respond METHOD_NOT_ALLOWED
            super.handleGET( exchange );
        }
        else
        {
            handleRequest( exchange, CoAPResponseCode.CONTENT );
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.californium.core.CoapResource#handlePUT(org.eclipse.californium.core.server.resources.CoapExchange)
     */
    @Override
    public void handlePUT( CoapExchange exchange )
    {
        if ( !isPut() )
        {
            // default implementation is to respond METHOD_NOT_ALLOWED
            super.handlePUT( exchange );
        }
        else
        {
            handleRequest( exchange, CoAPResponseCode.CHANGED );
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.californium.core.CoapResource#handlePOST(org.eclipse.californium.core.server.resources.CoapExchange)
     */
    @Override
    public void handlePOST( CoapExchange exchange )
    {
        if ( !isPost() )
        {
            // default implementation is to respond METHOD_NOT_ALLOWED
            super.handlePOST( exchange );
        }
        else
        {
            handleRequest( exchange, CoAPResponseCode.CHANGED );
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.californium.core.CoapResource#handleDELETE(org.eclipse.californium.core.server.resources.CoapExchange)
     */
    @Override
    public void handleDELETE( CoapExchange exchange )
    {
        if ( !isDelete() )
        {
            // default implementation is to respond METHOD_NOT_ALLOWED
            super.handleDELETE( exchange );
        }
        else
        {
            handleRequest( exchange, CoAPResponseCode.DELETED );
        }
    }

    /**
     * Generic handler for processing requests.
     * @param exchange the CoAP exchange context of the request.
     * @param defaultResponseCode the response code that will be used when the Mule flow hasn't set one.
     */
    private void handleRequest( CoapExchange exchange, CoAPResponseCode defaultCoAPResponseCode )
    {
        if ( !hasCallback() )
        {
            exchange.respond( ResponseCode.INTERNAL_SERVER_ERROR, "NO LISTENER" );
            return;
        }
        if ( isEarlyAck() )
        {
            exchange.accept();
        }
        ReceivedRequestAttributes requestAttributes;
        try
        {
            requestAttributes= createRequestAttributes( exchange );
        }
        catch ( Exception e1 )
        {
            // cannot process the request when an option is not valid,
            // probably Californium has already dealt with this, so shouldn't occur
            Response response= new Response( ResponseCode.BAD_OPTION );
            response.setPayload( e1.getMessage() );
            exchange.respond( response );
            return;
        }
        SourceCallbackContext requestcontext= callback.createContext();
        requestcontext.addVariable( "defaultCoAPResponseCode", defaultCoAPResponseCode );
        requestcontext.addVariable( "CoapExchange", exchange );
        callback.handle(
            Result.< byte[], ReceivedRequestAttributes > builder().output( exchange.getRequestPayload() ).attributes( requestAttributes ).mediaType(
                MediaTypeMediator.toMediaType( exchange.getRequestOptions().getContentFormat() ) ).build(),
            requestcontext );

    }

    private ReceivedRequestAttributes createRequestAttributes( CoapExchange exchange ) throws InternalInvalidOptionValueException
    {
        ReceivedRequestAttributes attributes= new ReceivedRequestAttributes();
        attributes.setRequestCode( exchange.getRequestCode().toString() );
        attributes.setConfirmable( exchange.advanced().getRequest().isConfirmable() );
        attributes.setLocalAddress( exchange.advanced().getEndpoint().getAddress().toString() );
        attributes.setRequestUri( this.getURI() );
        attributes.setRelation( ( exchange.advanced().getRelation() != null ? exchange.advanced().getRelation().getKey() : null ) );
        attributes.setRemoteAddress( exchange.getSourceAddress().toString() );
        attributes.setRemotePort( exchange.getSourcePort() );

        CoAPOptions.copyOptions( exchange.getRequestOptions(), attributes.getOptions() );
        return attributes;
    }

    /**
     * @return the get flag
     */
    public Boolean isGet()
    {
        return get;
    }

    /**
     * @return the put flag
     */
    public Boolean isPut()
    {
        return put;
    }

    /**
     * @return the post flag
     */
    public Boolean isPost()
    {
        return post;
    }

    /**
     * @return the delete flag
     */
    public Boolean isDelete()
    {
        return delete;
    }

    /**
     * @return the earlyAck flag
     */
    public Boolean isEarlyAck()
    {
        return earlyAck;
    }

    /**
     * set the Mule callback for this resource.
     */
    public void setCallback( SourceCallback< byte[], ReceivedRequestAttributes > callback )
    {
        this.callback= callback;
    }

    /**
     * Get the Mule MessageSource callback
     * @return the callback
     */
    public SourceCallback< byte[], ReceivedRequestAttributes > getCallback()
    {
        return callback;
    }

    /**
     * Test whether the resource callback has been set.
     * @return true when callback is set.
     */
    public boolean hasCallback()
    {
        return( callback != null );
    }

}
