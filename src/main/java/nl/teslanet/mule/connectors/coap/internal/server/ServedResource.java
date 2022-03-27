/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2022 (teslanet.nl) Rogier Cobben
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

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;

import nl.teslanet.mule.connectors.coap.api.CoAPRequestAttributes;
import nl.teslanet.mule.connectors.coap.api.CoAPResponseCode;
import nl.teslanet.mule.connectors.coap.api.ResourceParams;
import nl.teslanet.mule.connectors.coap.api.ResourceConfig;
import nl.teslanet.mule.connectors.coap.internal.attributes.AttributeUtils;
import nl.teslanet.mule.connectors.coap.internal.attributes.DefaultRequestAttributes;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidMessageTypeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidRequestCodeException;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultRequestOptionsAttributes;
import nl.teslanet.mule.connectors.coap.internal.options.MediaTypeMediator;


/**
 * The ServedResource class represents a CoAP resource that is active on the server.
 * A ServedResource object handles all requests from clients on the CoAP resource.
 */
public class ServedResource extends CoapResource
{
    /**
     * Regular expression for splitting comma separated values.
     */
    private static final String CSV_REGEX= "\\s*,\\s*";

    /**
     * The callback of the messagesource for Get requests.
     * It is used to hand messages over to the Mule flow that should process the request.
     */
    private SourceCallback< InputStream, CoAPRequestAttributes > getCallback= null;

    /**
     * The callback of the messagesource for Post requests.
     * It is used to hand messages over to the Mule flow that should process the request.
     */
    private SourceCallback< InputStream, CoAPRequestAttributes > postCallback= null;

    /**
     * The callback of the messagesource for Put requests.
     * It is used to hand messages over to the Mule flow that should process the request.
     */
    private SourceCallback< InputStream, CoAPRequestAttributes > putCallback= null;

    /**
     * The callback of the messagesource for Delete requests.
     * It is used to hand messages over to the Mule flow that should process the request.
     */
    private SourceCallback< InputStream, CoAPRequestAttributes > deleteCallback= null;

    /**
     * RequestCode flags indicating which requests the resource accepts.
     */
    private RequestCodeFlags requestCodeFlags= new RequestCodeFlags();

    /**
     * Flag that indicates whether the resource should acknowledge before processing the request.
     */
    private boolean earlyAck= false;

    /**
     * Constuctor that creates a ServedResource object according to given configuration.
     * The ServedResource and its child resources will be constructed.
     * @param resource the configuration of the resource to create. 
     */
    public ServedResource( ResourceConfig resource )
    {
        //TODO add / replacement
        super( resource.getResourceName() );

        //TODO make use of visible/invisible?

        requestCodeFlags.setGet( resource.isGet() );
        requestCodeFlags.setPost( resource.isPost() );
        requestCodeFlags.setPut( resource.isPut() );
        requestCodeFlags.setDelete( resource.isDelete() );
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
        if ( resource.getCoreInfoConfig() != null )
        {
            if ( resource.getCoreInfoConfig().getTitle() != null )
            {
                getAttributes().setTitle( resource.getCoreInfoConfig().getTitle() );
            }
            if ( resource.getCoreInfoConfig().getRt() != null )
            {
                for ( String rt : resource.getCoreInfoConfig().getRt().split( CSV_REGEX ) )
                {
                    getAttributes().addResourceType( rt );
                }
            }
            if ( resource.getCoreInfoConfig().getIfdesc() != null )
            {
                for ( String ifdesc : resource.getCoreInfoConfig().getIfdesc().split( CSV_REGEX ) )
                {
                    getAttributes().addInterfaceDescription( ifdesc );
                }
            }
            if ( resource.getCoreInfoConfig().getCt() != null )
            {
                for ( String ct : resource.getCoreInfoConfig().getCt().split( CSV_REGEX ) )
                {
                    getAttributes().addContentType( Integer.parseInt( ct ) );
                }
            }
            if ( resource.getCoreInfoConfig().getSz() != null )
            {
                getAttributes().setMaximumSizeEstimate( resource.getCoreInfoConfig().getSz() );
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
    public ServedResource( ResourceParams resource )
    {
        super( ResourceRegistry.getUriResourceName( resource.getResourcePath() ) );

        //TODO make use of visible/invisible?

        requestCodeFlags.setGet( resource.isGet() );
        requestCodeFlags.setPost( resource.isPost() );
        requestCodeFlags.setPut( resource.isPut() );
        requestCodeFlags.setDelete( resource.isDelete() );
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
            }
            if ( resource.getInfo().getRt() != null )
            {
                for ( String rt : resource.getInfo().getRt().split( CSV_REGEX ) )
                {
                    getAttributes().addResourceType( rt );
                }
            }
            if ( resource.getInfo().getIfdesc() != null )
            {
                for ( String ifdesc : resource.getInfo().getIfdesc().split( CSV_REGEX ) )
                {
                    getAttributes().addInterfaceDescription( ifdesc );
                }
            }
            if ( resource.getInfo().getCt() != null )
            {
                for ( String ct : resource.getInfo().getCt().split( CSV_REGEX ) )
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

    /**
     * @return True when this resource will handle get requests.
     */
    boolean isHandlingGet()
    {
        return requestCodeFlags.isGet();
    }

    /* (non-Javadoc)
     * @see org.eclipse.californium.core.CoapResource#handleGET(org.eclipse.californium.core.server.resources.CoapExchange)
     */
    @Override
    public void handleGET( CoapExchange exchange )
    {
        if ( requestCodeFlags.isNotGet() )
        {
            // default implementation is to respond METHOD_NOT_ALLOWED
            super.handleGET( exchange );
        }
        else
        {
            handleRequest( getCallback, exchange, CoAPResponseCode.CONTENT );
        }
    }

    /**
     * @return True when this resource will handle put requests.
     */
    boolean isHandlingPut()
    {
        return requestCodeFlags.isPut();
    }

    /* (non-Javadoc)
     * @see org.eclipse.californium.core.CoapResource#handlePUT(org.eclipse.californium.core.server.resources.CoapExchange)
     */
    @Override
    public void handlePUT( CoapExchange exchange )
    {
        if ( requestCodeFlags.isNotPut() )
        {
            // default implementation is to respond METHOD_NOT_ALLOWED
            super.handlePUT( exchange );
        }
        else
        {
            handleRequest( putCallback, exchange, CoAPResponseCode.CHANGED );
        }
    }

    /**
     * @return True when this resource will handle post requests.
     */
    boolean isHandlingPost()
    {
        return requestCodeFlags.isPost();
    }

    /* (non-Javadoc)
     * @see org.eclipse.californium.core.CoapResource#handlePOST(org.eclipse.californium.core.server.resources.CoapExchange)
     */
    @Override
    public void handlePOST( CoapExchange exchange )
    {
        if ( requestCodeFlags.isNotPost() )
        {
            // default implementation is to respond METHOD_NOT_ALLOWED
            super.handlePOST( exchange );
        }
        else
        {
            handleRequest( postCallback, exchange, CoAPResponseCode.CHANGED );
        }
    }

    /**
     * @return True when this resource will handle delete requests.
     */
    boolean isHandlingDelete()
    {
        return requestCodeFlags.isDelete();
    }

    /* (non-Javadoc)
     * @see org.eclipse.californium.core.CoapResource#handleDELETE(org.eclipse.californium.core.server.resources.CoapExchange)
     */
    @Override
    public void handleDELETE( CoapExchange exchange )
    {
        if ( requestCodeFlags.isNotDelete() )
        {
            // default implementation is to respond METHOD_NOT_ALLOWED
            super.handleDELETE( exchange );
        }
        else
        {
            handleRequest( deleteCallback, exchange, CoAPResponseCode.DELETED );
        }
    }

    /**
     * Generic handler for processing requests.
     * @param exchange the CoAP exchange context of the request.
     * @param defaultResponseCode the response code that will be used when the Mule flow hasn't set one.
     */
    private void handleRequest( SourceCallback< InputStream, CoAPRequestAttributes > callback, CoapExchange exchange, CoAPResponseCode defaultCoAPResponseCode )
    {
        if ( callback == null )
        {
            exchange.respond( ResponseCode.INTERNAL_SERVER_ERROR, "NO LISTENER" );
            return;
        }
        if ( earlyAck )
        {
            exchange.accept();
        }
        CoAPRequestAttributes requestAttributes;
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
                Result.< InputStream, CoAPRequestAttributes > builder().output( new ByteArrayInputStream( requestPayload ) ).length( requestPayload.length ).attributes(
                    requestAttributes
                ).mediaType( MediaTypeMediator.toMediaType( exchange.getRequestOptions().getContentFormat() ) ).build(),
                requestcontext
            );
        }
        else
        {
            callback.handle(
                Result.< InputStream, CoAPRequestAttributes > builder().attributes( requestAttributes ).output( null ).mediaType(
                    MediaTypeMediator.toMediaType( exchange.getRequestOptions().getContentFormat() )
                ).build(),
                requestcontext
            );
        }

    }

    /**
     * Create request attributes.
     * @param coapExchange
     * @return The attributes to add to the mule message.
     * @throws InternalInvalidOptionValueException When request options could not be interpreted.
     * @throws InternalInvalidMessageTypeException When request type could not be interpreted.
     * @throws InternalInvalidRequestCodeException When request code could not be interpreted.
     */
    private DefaultRequestAttributes createRequestAttributes( CoapExchange coapExchange ) throws InternalInvalidOptionValueException,
        InternalInvalidMessageTypeException,
        InternalInvalidRequestCodeException
    {
        Exchange exchange= coapExchange.advanced();
        DefaultRequestAttributes attributes= new DefaultRequestAttributes();
        attributes.setRequestType( AttributeUtils.toMessageTypeAttribute( coapExchange.advanced().getRequest().getType() ).name() );
        attributes.setRequestCode( AttributeUtils.toRequestCodeAttribute( coapExchange.getRequestCode() ).name() );
        attributes.setLocalAddress( exchange.getEndpoint().getAddress().toString() );
        attributes.setRemoteAddress( coapExchange.getSourceSocketAddress().toString() );
        attributes.setRequestUri( exchange.getRequest().getURI() );
        attributes.setRequestOptionAttributes( new DefaultRequestOptionsAttributes( coapExchange.getRequestOptions() ) );
        attributes.setRelation( ( exchange.getRelation() != null ? exchange.getRelation().getKey() : null ) );
        return attributes;
    }

    /**
     * set the Mule callback for this resource for Get requests.
     */
    public void setGetCallback( SourceCallback< InputStream, CoAPRequestAttributes > sourceCallback )
    {
        getCallback= sourceCallback;
    }

    /**
     * Get the Mule MessageSource callback for Get requests.
     * @return the callback
     */
    public SourceCallback< InputStream, CoAPRequestAttributes > getGetCallback()
    {
        return getCallback;
    }

    /**
     * set the Mule callback for this resource for Post requests.
     */
    public void setPostCallback( SourceCallback< InputStream, CoAPRequestAttributes > sourceCallback )
    {
        postCallback= sourceCallback;
    }

    /**
     * Get the Mule MessageSource callback for Post requests.
     * @return the callback
     */
    public SourceCallback< InputStream, CoAPRequestAttributes > getPostCallback()
    {
        return postCallback;
    }

    /**
     * set the Mule callback for this resource for Put requests.
     */
    public void setPutCallback( SourceCallback< InputStream, CoAPRequestAttributes > sourceCallback )
    {
        putCallback= sourceCallback;
    }

    /**
     * Get the Mule MessageSource callback for Put requests.
     * @return the callback
     */
    public SourceCallback< InputStream, CoAPRequestAttributes > getPutCallback()
    {
        return putCallback;
    }

    /**
     * set the Mule callback for this resource for Delete requests.
     */
    public void setDeleteCallback( SourceCallback< InputStream, CoAPRequestAttributes > sourceCallback )
    {
        deleteCallback= sourceCallback;
    }

    /**
     * Get the Mule MessageSource callback for Delete requests.
     * @return the callback
     */
    public SourceCallback< InputStream, CoAPRequestAttributes > getDeleteCallback()
    {
        return deleteCallback;
    }
}
