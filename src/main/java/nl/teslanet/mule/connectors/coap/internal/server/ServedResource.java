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


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;

import nl.teslanet.mule.connectors.coap.api.ResponseBuilder.CoAPResponseCode;
import nl.teslanet.mule.connectors.coap.api.ReceivedRequestAttributes;
import nl.teslanet.mule.connectors.coap.api.ResourceBuilder;
import nl.teslanet.mule.connectors.coap.api.ResourceConfig;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.options.CoAPOptions;
import nl.teslanet.mule.connectors.coap.internal.options.MediaTypeMediator;


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
    private SourceCallback< InputStream, ReceivedRequestAttributes > callback= null;

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
     * @param resource the configuration of the resource to create. 
     */
    public ServedResource( ResourceConfig resource )
    {
        super( resource.getResourceName() );

        //TODO make use of visible/invisible?

        get= resource.isGet();
        put= resource.isPut();
        post= resource.isPost();
        delete= resource.isDelete();
        earlyAck= resource.isEarlyAck();

        if ( resource.isObservable() )
        {
            setObservable( true );
            getAttributes().setObservable();
        }
        else
        {
            setObservable( false );
        }

        //process info configuration
        if ( resource.getInfoConfig() != null )
        {
            if ( resource.getInfoConfig().getTitle() != null )
            {
                getAttributes().setTitle( resource.getInfoConfig().getTitle() );
            } ;
            if ( resource.getInfoConfig().getRt() != null )
            {
                for ( String rt : resource.getInfoConfig().getRt().split( "\\s*,\\s*" ) )
                {
                    getAttributes().addResourceType( rt );
                }
            } ;
            if ( resource.getInfoConfig().getIfdesc() != null )
            {
                for ( String ifdesc : resource.getInfoConfig().getIfdesc().split( "\\s*,\\s*" ) )
                {
                    getAttributes().addInterfaceDescription( ifdesc );
                }
            } ;
            if ( resource.getInfoConfig().getCt() != null )
            {
                for ( String ct : resource.getInfoConfig().getCt().split( "\\s*,\\s*" ) )
                {
                    getAttributes().addContentType( Integer.parseInt( ct ) );
                }
            }
            if ( resource.getInfoConfig().getSz() != null )
            {
                getAttributes().setMaximumSizeEstimate( resource.getInfoConfig().getSz() );
            }
        }
        //process resource configuration
        if ( resource.getSubResources() != null )
        {
            //also create children (recursively) 
            for ( ResourceConfig childResourceConfig : resource.getSubResources() )
            {
                ServedResource child= new ServedResource( childResourceConfig );
                add( child );
            }
        }
    }

    /**
     * Constructor that creates a ServedResource object using given builder.
     * The ServedResource and its child resources will be constructed.
     * @param resource the builder of the resource to create. 
     */
    public ServedResource( ResourceBuilder resource )
    {
        super( ResourceRegistry.getUriResourceName( resource.getResourcePath() ) );

        //TODO make use of visible/invisible?

        get= resource.isGet();
        put= resource.isPut();
        post= resource.isPost();
        delete= resource.isDelete();
        earlyAck= resource.isEarlyAck();

        if ( resource.isObservable() )
        {
            setObservable( true );
            getAttributes().setObservable();
        }
        else
        {
            setObservable( false );
        }

        //process info configuration
        if ( resource.getInfo() != null )
        {
            if ( resource.getInfo().getTitle() != null )
            {
                getAttributes().setTitle( resource.getInfo().getTitle() );
            } ;
            if ( resource.getInfo().getRt() != null )
            {
                for ( String rt : resource.getInfo().getRt().split( "\\s*,\\s*" ) )
                {
                    getAttributes().addResourceType( rt );
                }
            } ;
            if ( resource.getInfo().getIfdesc() != null )
            {
                for ( String ifdesc : resource.getInfo().getIfdesc().split( "\\s*,\\s*" ) )
                {
                    getAttributes().addInterfaceDescription( ifdesc );
                }
            } ;
            if ( resource.getInfo().getCt() != null )
            {
                for ( String ct : resource.getInfo().getCt().split( "\\s*,\\s*" ) )
                {
                    getAttributes().addContentType( Integer.parseInt( ct ) );
                }
            }
            if ( resource.getInfo().getSz() != null )
            {
                getAttributes().setMaximumSizeEstimate( resource.getInfo().getSz() );
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
        //TODO add streaming & blockwise cooperation
        byte[] requestPayload= exchange.getRequestPayload();
        if ( requestPayload != null )
        {
            callback.handle(
                Result.< InputStream, ReceivedRequestAttributes > builder().output( new ByteArrayInputStream( requestPayload ) ).length( requestPayload.length ).attributes(
                    requestAttributes ).mediaType( MediaTypeMediator.toMediaType( exchange.getRequestOptions().getContentFormat() ) ).build(),
                requestcontext );
        }
        else
        {
            callback.handle(
                Result.< InputStream, ReceivedRequestAttributes > builder().attributes( requestAttributes ).output( null ).mediaType(
                    MediaTypeMediator.toMediaType( exchange.getRequestOptions().getContentFormat() ) ).build(),
                requestcontext );
        }

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
    public void setCallback( SourceCallback< InputStream, ReceivedRequestAttributes > sourceCallback )
    {
        this.callback= sourceCallback;
    }

    /**
     * Get the Mule MessageSource callback
     * @return the callback
     */
    public SourceCallback< InputStream, ReceivedRequestAttributes > getCallback()
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
