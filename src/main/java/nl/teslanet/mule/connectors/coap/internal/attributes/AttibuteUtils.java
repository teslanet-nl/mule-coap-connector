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
package nl.teslanet.mule.connectors.coap.internal.attributes;


import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.mule.runtime.api.metadata.MediaType;

import nl.teslanet.mule.connectors.coap.api.CoAPRequestCode;
import nl.teslanet.mule.connectors.coap.api.CoAPResponseCode;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidRequestCodeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidResponseCodeException;


//TODO add unit test
/**
 * Utilities for handling attributes
 *
 */
public class AttibuteUtils
{
    /**
     * Translate attribute to Californium request Code.
     * @param requestCodeAttribute the attribute to transform
     * @param defaultrequestCodeAttribute the request code to use when requestCodeAttribute is null
     * @return the Californium request code
     * @throws InternalInvalidRequestCodeException when invalid request code is given
     */
    public static Code toCode( CoAPRequestCode requestCodeAttribute, CoAPRequestCode defaultrequestCodeAttribute ) throws InternalInvalidRequestCodeException
    {
        if ( requestCodeAttribute != null )
        {
            return toCode( requestCodeAttribute );
        }
        else
        {
            return toCode( defaultrequestCodeAttribute );
        }
    }

    /**
     * Translate attribute to Californium request Code.
     * @param requestCodeAttribute the attribute to transform
     * @return the Californium request code
     * @throws InternalInvalidRequestCodeException 
     * @throws Exception when invalid attribute is given
     */
    public static Code toCode( CoAPRequestCode requestCodeAttribute ) throws InternalInvalidRequestCodeException
    {
        switch ( requestCodeAttribute )
        {
            //success
            case GET:
                return Code.GET;
            case POST:
                return Code.POST;
            case PUT:
                return Code.PUT;
            case DELETE:
                return Code.DELETE;
            default:
                throw new InternalInvalidRequestCodeException( "invalid request code { " + requestCodeAttribute + " }" );
        }
    }

    /**
     * Translate attribute to Californium ResponseCode.
     * @param reponseCodeAttribute the attribute to transform
     * @param defaultreponseCodeAttribute the responscode to use when reponseCodeAttribute is null
     * @return the Californium response code
     * @throws InternalInvalidResponseCodeException 
     * @throws Exception when invalid responsecode is given
     */
    public static ResponseCode toResponseCode( CoAPResponseCode reponseCodeAttribute, CoAPResponseCode defaultreponseCodeAttribute ) throws InternalInvalidResponseCodeException
    {
        if ( reponseCodeAttribute != null )
        {
            return toResponseCode( reponseCodeAttribute );
        }
        else
        {
            return toResponseCode( defaultreponseCodeAttribute );
        }
    }

    /**
     * Translate attribute to Californium ResponseCode.
     * @param reponseCodeAttribute the attribute to transform
     * @return the Californium response code
     * @throws InternalInvalidResponseCodeException 
     * @throws Exception when invalid responsecode is given
     */
    public static ResponseCode toResponseCode( CoAPResponseCode reponseCodeAttribute ) throws InternalInvalidResponseCodeException
    {
        switch ( reponseCodeAttribute )
        {
            //success
            case CREATED:
                return ResponseCode.CREATED;
            case DELETED:
                return ResponseCode.DELETED;
            case VALID:
                return ResponseCode.VALID;
            case CHANGED:
                return ResponseCode.CHANGED;
            case CONTENT:
                return ResponseCode.CONTENT;
            case CONTINUE:
                return ResponseCode.CONTINUE;

            //client error
            case BAD_REQUEST:
                return ResponseCode.BAD_REQUEST;
            case UNAUTHORIZED:
                return ResponseCode.UNAUTHORIZED;
            case BAD_OPTION:
                return ResponseCode.BAD_OPTION;
            case FORBIDDEN:
                return ResponseCode.FORBIDDEN;
            case NOT_FOUND:
                return ResponseCode.NOT_FOUND;
            case METHOD_NOT_ALLOWED:
                return ResponseCode.METHOD_NOT_ALLOWED;
            case NOT_ACCEPTABLE:
                return ResponseCode.NOT_ACCEPTABLE;
            case REQUEST_ENTITY_INCOMPLETE:
                return ResponseCode.REQUEST_ENTITY_INCOMPLETE;
            case CONFLICT:
                return ResponseCode.CONFLICT;
            case PRECONDITION_FAILED:
                return ResponseCode.PRECONDITION_FAILED;
            case REQUEST_ENTITY_TOO_LARGE:
                return ResponseCode.REQUEST_ENTITY_TOO_LARGE;
            case UNSUPPORTED_CONTENT_FORMAT:
                return ResponseCode.UNSUPPORTED_CONTENT_FORMAT;
            case UNPROCESSABLE_ENTITY:
                return ResponseCode.UNPROCESSABLE_ENTITY;
            case TOO_MANY_REQUESTS:
                return ResponseCode.TOO_MANY_REQUESTS;

            //sever error
            case INTERNAL_SERVER_ERROR:
                return ResponseCode.INTERNAL_SERVER_ERROR;
            case NOT_IMPLEMENTED:
                return ResponseCode.NOT_IMPLEMENTED;
            case BAD_GATEWAY:
                return ResponseCode.BAD_GATEWAY;
            case SERVICE_UNAVAILABLE:
                return ResponseCode.SERVICE_UNAVAILABLE;
            case GATEWAY_TIMEOUT:
                return ResponseCode.GATEWAY_TIMEOUT;
            case PROXY_NOT_SUPPORTED:
                return ResponseCode.PROXY_NOT_SUPPORTED;
            default:
                throw new InternalInvalidResponseCodeException( "invalid response code { " + reponseCodeAttribute + " }" );
        }
    }

    /**
     * Translate coap content format to Mule MediaType.
     * @param contentFormat The CoAP content format number.
     * @return The MediaType that applies to the content format.
     */
    public static MediaType toMediaType( int contentFormat )
    {
        return MediaType.parse( MediaTypeRegistry.toString( contentFormat ) );
    }
}
