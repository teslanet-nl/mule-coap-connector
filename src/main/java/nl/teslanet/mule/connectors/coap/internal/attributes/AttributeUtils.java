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
package nl.teslanet.mule.connectors.coap.internal.attributes;


import java.util.List;

import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;

import nl.teslanet.mule.connectors.coap.api.RequestBuilder.CoAPRequestCode;
import nl.teslanet.mule.connectors.coap.api.ResponseBuilder.CoAPResponseCode;
import nl.teslanet.mule.connectors.coap.api.query.QueryParamAttribute;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidRequestCodeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidResponseCodeException;


/**
 * Utilities for handling attributes
 *
 */
public class AttributeUtils
{
    /**
     * No instances needed.
     */
    private AttributeUtils()
    {
        //NOOP
    }

    /**
     * Translate requestCode attribute to Cf request Code.
     * @param requestCodeAttribute to convert
     * @return the converted requestCode
     * @throws InternalInvalidRequestCodeException when requestCode cannot be converted
     */
    public static Code toRequestCode( CoAPRequestCode requestCodeAttribute ) throws InternalInvalidRequestCodeException
    {
        switch ( requestCodeAttribute )
        {
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
     * Translate Cf request Code to requestCode attribute.
     * @param requestCode to convert
     * @return the converted requestCode attribute
     * @throws InternalInvalidRequestCodeException when requestCode cannot be converted
     */
    public static CoAPRequestCode toRequestCodeAttribute( Code requestCode ) throws InternalInvalidRequestCodeException
    {
        switch ( requestCode )
        {
            case GET:
                return CoAPRequestCode.GET;
            case POST:
                return CoAPRequestCode.POST;
            case PUT:
                return CoAPRequestCode.PUT;
            case DELETE:
                return CoAPRequestCode.DELETE;
            default:
                throw new InternalInvalidRequestCodeException( "invalid request code { " + requestCode + " }" );
        }
    }

    /**
     * Translate responseCode attribute to Cf ResponseCode with default value.
     * @param reponseCodeAttribute to be converted
     * @param defaultResponseCodeAttribute is the responseCode to use when input is empty
     * @return the converted Cf responseCode
     * @throws InternalInvalidResponseCodeException
     */
    public static ResponseCode toResponseCode( CoAPResponseCode reponseCodeAttribute, CoAPResponseCode defaultResponseCodeAttribute ) throws InternalInvalidResponseCodeException
    {
        if ( reponseCodeAttribute != null )
        {
            return toResponseCode( reponseCodeAttribute );
        }
        else
        {
            return toResponseCode( defaultResponseCodeAttribute );
        }
    }

    /**
     * Translate responseCode attribute to Cf ResponseCode.
     * @param reponseCodeAttribute the code to convert
     * @return the converted Cf responseCode
     * @throws InternalInvalidResponseCodeException
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
                throw new InternalInvalidResponseCodeException( reponseCodeAttribute );
        }
    }

    /**
     * Translate Cf Response to CoderesponseCode attribute.
     * @param reponseCode the code to convert
     * @return the converted Cf responseCode
     * @throws InternalInvalidResponseCodeException
     */
    public static CoAPResponseCode toResponseCodeAttribute( ResponseCode reponseCode ) throws InternalInvalidResponseCodeException
    {
        switch ( reponseCode )
        {
            //success
            case CREATED:
                return CoAPResponseCode.CREATED;
            case DELETED:
                return CoAPResponseCode.DELETED;
            case VALID:
                return CoAPResponseCode.VALID;
            case CHANGED:
                return CoAPResponseCode.CHANGED;
            case CONTENT:
                return CoAPResponseCode.CONTENT;
            case CONTINUE:
                return CoAPResponseCode.CONTINUE;

            //client error
            case BAD_REQUEST:
                return CoAPResponseCode.BAD_REQUEST;
            case UNAUTHORIZED:
                return CoAPResponseCode.UNAUTHORIZED;
            case BAD_OPTION:
                return CoAPResponseCode.BAD_OPTION;
            case FORBIDDEN:
                return CoAPResponseCode.FORBIDDEN;
            case NOT_FOUND:
                return CoAPResponseCode.NOT_FOUND;
            case METHOD_NOT_ALLOWED:
                return CoAPResponseCode.METHOD_NOT_ALLOWED;
            case NOT_ACCEPTABLE:
                return CoAPResponseCode.NOT_ACCEPTABLE;
            case REQUEST_ENTITY_INCOMPLETE:
                return CoAPResponseCode.REQUEST_ENTITY_INCOMPLETE;
            case CONFLICT:
                return CoAPResponseCode.CONFLICT;
            case PRECONDITION_FAILED:
                return CoAPResponseCode.PRECONDITION_FAILED;
            case REQUEST_ENTITY_TOO_LARGE:
                return CoAPResponseCode.REQUEST_ENTITY_TOO_LARGE;
            case UNSUPPORTED_CONTENT_FORMAT:
                return CoAPResponseCode.UNSUPPORTED_CONTENT_FORMAT;
            case UNPROCESSABLE_ENTITY:
                return CoAPResponseCode.UNPROCESSABLE_ENTITY;
            case TOO_MANY_REQUESTS:
                return CoAPResponseCode.TOO_MANY_REQUESTS;

            //sever error
            case INTERNAL_SERVER_ERROR:
                return CoAPResponseCode.INTERNAL_SERVER_ERROR;
            case NOT_IMPLEMENTED:
                return CoAPResponseCode.NOT_IMPLEMENTED;
            case BAD_GATEWAY:
                return CoAPResponseCode.BAD_GATEWAY;
            case SERVICE_UNAVAILABLE:
                return CoAPResponseCode.SERVICE_UNAVAILABLE;
            case GATEWAY_TIMEOUT:
                return CoAPResponseCode.GATEWAY_TIMEOUT;
            case PROXY_NOT_SUPPORTED:
                return CoAPResponseCode.PROXY_NOT_SUPPORTED;
            default:
                throw new InternalInvalidResponseCodeException( reponseCode );
        }
    }

    /**
     * Add query parameter string with optional value to multiMap.
     * @param multiMap The map to add the parameter to.
     * @param parameterString The string containing the key and optional value.
     */
    public static void addQueryParam( List< QueryParamAttribute > list, String parameterString )
    {
        String key;
        String value;
        int separatorIndex= parameterString.indexOf( "=" );
        if ( separatorIndex < 0 )
        {
            key= parameterString;
            value= null;
        }
        else
        {
            key= parameterString.substring( 0, separatorIndex );
            value= parameterString.substring( separatorIndex + 1 );
        }
        list.add( new QueryParamAttribute( key, value ) );
    }
}
