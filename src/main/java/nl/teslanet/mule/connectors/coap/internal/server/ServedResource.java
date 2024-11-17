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


import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import nl.teslanet.mule.connectors.coap.api.CoapResponseCode;
import nl.teslanet.mule.connectors.coap.api.ConfigurableResource;
import nl.teslanet.mule.connectors.coap.api.ResourceConfig;
import nl.teslanet.mule.connectors.coap.api.ResourceParams;
import nl.teslanet.mule.connectors.coap.api.attributes.CoapRequestAttributes;
import nl.teslanet.mule.connectors.coap.internal.attributes.CoapRequestAttributesImpl;
import nl.teslanet.mule.connectors.coap.internal.attributes.CoapRequestOptionsAttributesImpl;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidMessageTypeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidRequestCodeException;
import nl.teslanet.mule.connectors.coap.internal.options.MediaTypeMediator;
import nl.teslanet.mule.connectors.coap.internal.utils.AttributeUtils;


/**
 * The ServedResource class represents a CoAP resource that is active on the server.
 * A ServedResource object handles all requests from clients on the CoAP resource.
 */
public class ServedResource extends CoapResource
{
    /**
     * This class logger.
     */
    private static final Logger LOGGER= LoggerFactory.getLogger( ServedResource.class );

    /**
     * Marker for logging error responses.
     */
    private static final Marker ERROR_RESPONSE_MARKER= MarkerFactory.getMarker( "ERROR_RESPONSE" );

    /**
     * No listener message log format.
     */
    private static final String NO_LISTENER_LOG_FORMAT= "NO LISTENER for request { {}, {} }";

    /**
     * Regular expression for splitting comma separated values.
     */
    private static final String CSV_REGEX= ",";

    /**
     * The callback of the messagesource for Get requests.
     * It is used to hand messages over to the Mule flow that should process the request.
     */
    private SourceCallback< InputStream, CoapRequestAttributes > getCallback= null;

    /**
     * The callback of the messagesource for Post requests.
     * It is used to hand messages over to the Mule flow that should process the request.
     */
    private SourceCallback< InputStream, CoapRequestAttributes > postCallback= null;

    /**
     * The callback of the messagesource for Put requests.
     * It is used to hand messages over to the Mule flow that should process the request.
     */
    private SourceCallback< InputStream, CoapRequestAttributes > putCallback= null;

    /**
     * The callback of the messagesource for Delete requests.
     * It is used to hand messages over to the Mule flow that should process the request.
     */
    private SourceCallback< InputStream, CoapRequestAttributes > deleteCallback= null;

    /**
     * The callback of the messagesource for Fetch requests.
     * It is used to hand messages over to the Mule flow that should process the request.
     */
    private SourceCallback< InputStream, CoapRequestAttributes > fetchCallback= null;

    /**
     * The callback of the messagesource for Patch requests.
     * It is used to hand messages over to the Mule flow that should process the request.
     */
    private SourceCallback< InputStream, CoapRequestAttributes > patchCallback= null;

    /**
     * The callback of the messagesource for iPatch requests.
     * It is used to hand messages over to the Mule flow that should process the request.
     */
    private SourceCallback< InputStream, CoapRequestAttributes > ipatchCallback= null;

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
        super( resource.getResourceName() );
        configure( resource );

        //process subresources
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
        configure( resource );
    }

    /**
     * Configure the resource.
     * @param resource the configuration to set. 
     */
    private void configure( ConfigurableResource resource )
    {
        requestCodeFlags.setGet( resource.isGet() );
        requestCodeFlags.setPost( resource.isPost() );
        requestCodeFlags.setPut( resource.isPut() );
        requestCodeFlags.setDelete( resource.isDelete() );
        requestCodeFlags.setFetch( resource.isFetch() );
        requestCodeFlags.setPatch( resource.isPatch() );
        requestCodeFlags.setIpatch( resource.isIpatch() );
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
        if ( resource.getCoreInfo() != null )
        {
            if ( resource.getCoreInfo().getTitle() != null )
            {
                getAttributes().setTitle( resource.getCoreInfo().getTitle() );
            }
            if ( resource.getCoreInfo().getRt() != null )
            {
                for ( String rt : StringUtils.deleteWhitespace( resource.getCoreInfo().getRt() ).split( CSV_REGEX ) )
                {
                    getAttributes().addResourceType( rt );
                }
            }
            if ( resource.getCoreInfo().getIfdesc() != null )
            {
                for (
                    String ifdesc : StringUtils
                        .deleteWhitespace( resource.getCoreInfo().getIfdesc() )
                        .split( CSV_REGEX )
                )
                {
                    getAttributes().addInterfaceDescription( ifdesc );
                }
            }
            if ( resource.getCoreInfo().getCt() != null )
            {
                for ( String ct : StringUtils.deleteWhitespace( resource.getCoreInfo().getCt() ).split( CSV_REGEX ) )
                {
                    getAttributes().addContentType( Integer.parseInt( ct ) );
                }
            }
            if ( resource.getCoreInfo().getSz() != null )
            {
                getAttributes().setMaximumSizeEstimate( resource.getCoreInfo().getSz() );
            }
        }
    }

    /**
     * Override default handler of Cf.
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
            handleRequest( getCallback, exchange, CoapResponseCode.CONTENT );
        }
    }

    /**
     * Override default handler of Cf.
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
            handleRequest( putCallback, exchange, CoapResponseCode.CHANGED );
        }
    }

    /**
     * Override default handler of Cf.
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
            handleRequest( postCallback, exchange, CoapResponseCode.CHANGED );
        }
    }

    /**
     * Override default handler of Cf.
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
            handleRequest( deleteCallback, exchange, CoapResponseCode.DELETED );
        }
    }

    /**
     * Override default handler of Cf.
     */
    @Override
    public void handleFETCH( CoapExchange exchange )
    {
        if ( requestCodeFlags.isNotFetch() )
        {
            // default implementation is to respond METHOD_NOT_ALLOWED
            super.handleFETCH( exchange );
        }
        else
        {
            handleRequest( fetchCallback, exchange, CoapResponseCode.CONTENT );
        }
    }

    /**
     * Override default handler of Cf.
     */
    @Override
    public void handlePATCH( CoapExchange exchange )
    {
        if ( requestCodeFlags.isNotPatch() )
        {
            // default implementation is to respond METHOD_NOT_ALLOWED
            super.handlePATCH( exchange );
        }
        else
        {
            handleRequest( patchCallback, exchange, CoapResponseCode.CHANGED );
        }
    }

    /**
     * Override default handler of Cf.
     */
    @Override
    public void handleIPATCH( CoapExchange exchange )
    {
        if ( requestCodeFlags.isNotIpatch() )
        {
            // default implementation is to respond METHOD_NOT_ALLOWED
            super.handleIPATCH( exchange );
        }
        else
        {
            handleRequest( ipatchCallback, exchange, CoapResponseCode.CHANGED );
        }
    }

    /**
     * Generic handler for processing requests.
     * @param exchange the CoAP exchange context of the request.
     * @param defaultResponseCode the response code that will be used when the Mule flow hasn't set one.
     */
    private void handleRequest(
        SourceCallback< InputStream, CoapRequestAttributes > callback,
        CoapExchange exchange,
        CoapResponseCode defaultCoAPResponseCode
    )
    {
        if ( callback == null )
        {
            exchange.respond( ResponseCode.NOT_IMPLEMENTED, "NO LISTENER" );
            if ( LOGGER.isWarnEnabled( ERROR_RESPONSE_MARKER ) )
            {
                try
                {
                    LOGGER
                        .warn(
                            ERROR_RESPONSE_MARKER,
                            NO_LISTENER_LOG_FORMAT,
                            AttributeUtils.toRequestCodeAttribute( exchange.advanced().getCurrentRequest().getCode() ),
                            exchange.advanced().getCurrentRequest().getURI()
                        );
                }
                catch ( InternalInvalidRequestCodeException e )
                {
                    LOGGER
                        .warn(
                            ERROR_RESPONSE_MARKER,
                            NO_LISTENER_LOG_FORMAT,
                            exchange.advanced().getCurrentRequest().getCode(),
                            exchange.advanced().getCurrentRequest().getURI()
                        );
                }
            }
            return;
        }
        if ( earlyAck )
        {
            exchange.accept();
        }
        CoapRequestAttributes requestAttributes;
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
        requestcontext.addVariable( Server.VARNAME_DEFAULT_RESPONSE_CODE, defaultCoAPResponseCode );
        requestcontext.addVariable( Server.VARNAME_COAP_EXCHANGE, exchange );
        //TODO add streaming & blockwise cooperation
        byte[] requestPayload= exchange.getRequestPayload();
        // payload is always initialized, no need to check null
        callback
            .handle(
                Result
                    .< InputStream, CoapRequestAttributes > builder()
                    .output( new ByteArrayInputStream( requestPayload ) )
                    .length( requestPayload.length )
                    .attributes( requestAttributes )
                    .mediaType( MediaTypeMediator.toMediaType( exchange.getRequestOptions().getContentFormat() ) )
                    .build(),
                requestcontext
            );
    }

    /**
     * Create request attributes.
     * @param coapExchange
     * @return The attributes to add to the mule message.
     * @throws InternalInvalidOptionValueException When request options could not be interpreted.
     * @throws InternalInvalidMessageTypeException When request type could not be interpreted.
     * @throws InternalInvalidRequestCodeException When request code could not be interpreted.
     */
    private CoapRequestAttributesImpl createRequestAttributes( CoapExchange coapExchange )
        throws InternalInvalidOptionValueException,
        InternalInvalidMessageTypeException,
        InternalInvalidRequestCodeException
    {
        Exchange exchange= coapExchange.advanced();
        CoapRequestAttributesImpl attributes= new CoapRequestAttributesImpl();
        attributes
            .setRequestType(
                AttributeUtils.toMessageTypeAttribute( coapExchange.advanced().getRequest().getType() ).name()
            );
        attributes.setRequestCode( AttributeUtils.toRequestCodeAttribute( coapExchange.getRequestCode() ).name() );
        attributes.setLocalAddress( exchange.getEndpoint().getAddress().toString() );
        attributes.setRemoteAddress( coapExchange.getSourceSocketAddress().toString() );
        attributes.setRequestUri( exchange.getRequest().getURI() );
        attributes.setRequestOptions( new CoapRequestOptionsAttributesImpl( coapExchange.getRequestOptions() ) );
        attributes
            .setRelation( ( exchange.getRelation() != null ? exchange.getRelation().getKeyToken().toString() : null ) );
        return attributes;
    }

    /**
     * set the Mule callback for this resource for Get requests.
     */
    public void setGetCallback( SourceCallback< InputStream, CoapRequestAttributes > sourceCallback )
    {
        getCallback= sourceCallback;
    }

    /**
     * Get the Mule MessageSource callback for Get requests.
     * @return the callback
     */
    public SourceCallback< InputStream, CoapRequestAttributes > getGetCallback()
    {
        return getCallback;
    }

    /**
     * set the Mule callback for this resource for Post requests.
     */
    public void setPostCallback( SourceCallback< InputStream, CoapRequestAttributes > sourceCallback )
    {
        postCallback= sourceCallback;
    }

    /**
     * Get the Mule MessageSource callback for Post requests.
     * @return the callback
     */
    public SourceCallback< InputStream, CoapRequestAttributes > getPostCallback()
    {
        return postCallback;
    }

    /**
     * set the Mule callback for this resource for Put requests.
     */
    public void setPutCallback( SourceCallback< InputStream, CoapRequestAttributes > sourceCallback )
    {
        putCallback= sourceCallback;
    }

    /**
     * Get the Mule MessageSource callback for Put requests.
     * @return the callback
     */
    public SourceCallback< InputStream, CoapRequestAttributes > getPutCallback()
    {
        return putCallback;
    }

    /**
     * set the Mule callback for this resource for Delete requests.
     */
    public void setDeleteCallback( SourceCallback< InputStream, CoapRequestAttributes > sourceCallback )
    {
        deleteCallback= sourceCallback;
    }

    /**
     * Get the Mule MessageSource callback for Delete requests.
     * @return the callback
     */
    public SourceCallback< InputStream, CoapRequestAttributes > getDeleteCallback()
    {
        return deleteCallback;
    }

    /**
     * set the Mule callback for this resource for Fetch requests.
     */
    public void setFetchCallback( SourceCallback< InputStream, CoapRequestAttributes > sourceCallback )
    {
        fetchCallback= sourceCallback;
    }

    /**
     * Get the Mule MessageSource callback for Fetch requests.
     * @return the callback
     */
    public SourceCallback< InputStream, CoapRequestAttributes > getFetchCallback()
    {
        return fetchCallback;
    }

    /**
     * set the Mule callback for this resource for Patch requests.
     */
    public void setPatchCallback( SourceCallback< InputStream, CoapRequestAttributes > sourceCallback )
    {
        patchCallback= sourceCallback;
    }

    /**
     * Get the Mule MessageSource callback for Patch requests.
     * @return the callback
     */
    public SourceCallback< InputStream, CoapRequestAttributes > getPatchCallback()
    {
        return patchCallback;
    }

    /**
     * set the Mule callback for this resource for Ipatch requests.
     */
    public void setIpatchCallback( SourceCallback< InputStream, CoapRequestAttributes > sourceCallback )
    {
        ipatchCallback= sourceCallback;
    }

    /**
     * Get the Mule MessageSource callback for Ipatch requests.
     * @return the callback
     */
    public SourceCallback< InputStream, CoapRequestAttributes > getIpatchCallback()
    {
        return ipatchCallback;
    }
}
