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
package nl.teslanet.mule.connectors.coap.internal.attributes;


import java.util.List;

import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;

import nl.teslanet.mule.connectors.coap.api.CoapMessageType;
import nl.teslanet.mule.connectors.coap.api.CoapRequestCode;
import nl.teslanet.mule.connectors.coap.api.CoapResponseCode;
import nl.teslanet.mule.connectors.coap.api.query.QueryParamAttribute;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidMessageTypeException;
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
     * Translate to Cf message type..
     * @param type to convert
     * @return the converted message type
     * @throws InternalInvalidRequestCodeException when requestCode cannot be converted
     */
    public static Type toMessageType( CoapMessageType type ) throws InternalInvalidMessageTypeException
    {
        switch ( type )
        {
            case CONFIRMABLE:
                return Type.CON;
            case NON_CONFIRMABLE:
                return Type.NON;
            case ACKNOWLEDGEMENT:
                return Type.ACK;
            case RESET:
                return Type.RST;
            default:
                throw new InternalInvalidMessageTypeException( "Invalid message type { " + type + " }" );
        }
    }

    /**
     * Translate Cf message type to messageType attribute.
     * @param type to convert
     * @return the converted message attribute
     * @throws InternalInvalidRequestCodeException when requestCode cannot be converted
     */
    public static CoapMessageType toMessageTypeAttribute( Type type ) throws InternalInvalidMessageTypeException
    {
        switch ( type )
        {
            case CON:
                return CoapMessageType.CONFIRMABLE;
            case NON:
                return CoapMessageType.NON_CONFIRMABLE;
            case ACK:
                return CoapMessageType.ACKNOWLEDGEMENT;
            case RST:
                return CoapMessageType.RESET;
            default:
                throw new InternalInvalidMessageTypeException( "Invalid message type { " + type + " }" );
        }
    }

    /**
     * Translate requestCode attribute to Cf request Code.
     * @param requestCodeAttribute to convert
     * @return the converted requestCode
     * @throws InternalInvalidRequestCodeException when requestCode cannot be converted
     */
    public static Code toRequestCode( CoapRequestCode requestCodeAttribute ) throws InternalInvalidRequestCodeException
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
            case FETCH:
                return Code.FETCH;
            case PATCH:
                return Code.PATCH;
            case iPATCH:
                return Code.IPATCH;
            default:
                throw new InternalInvalidRequestCodeException( "invalid request code { " + requestCodeAttribute + " }" );
        }
    }

    /**
     * Translate Cf request Code to requestCode attribute value.
     * @param code to convert
     * @return the converted requestCode
     * @throws InternalInvalidRequestCodeException when requestCode cannot be converted
     */
    public static CoapRequestCode toRequestCodeAttribute( Code code ) throws InternalInvalidRequestCodeException
    {
        switch ( code )
        {
            case GET:
                return CoapRequestCode.GET;
            case POST:
                return CoapRequestCode.POST;
            case PUT:
                return CoapRequestCode.PUT;
            case DELETE:
                return CoapRequestCode.DELETE;
            case FETCH:
                return CoapRequestCode.FETCH;
            case PATCH:
                return CoapRequestCode.PATCH;
            case IPATCH:
                return CoapRequestCode.iPATCH;
            default:
                throw new InternalInvalidRequestCodeException( "invalid request code { " + code + " }" );
        }
    }

    /**
     * Translate responseCode attribute to Cf ResponseCode with default value.
     * @param reponseCodeAttribute to be converted
     * @param defaultResponseCodeAttribute is the responseCode to use when input is empty
     * @return the converted Cf responseCode
     * @throws InternalInvalidResponseCodeException
     */
    public static ResponseCode toResponseCode( CoapResponseCode reponseCodeAttribute, CoapResponseCode defaultResponseCodeAttribute ) throws InternalInvalidResponseCodeException
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
    public static ResponseCode toResponseCode( CoapResponseCode reponseCodeAttribute ) throws InternalInvalidResponseCodeException
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
    public static CoapResponseCode toResponseCodeAttribute( ResponseCode reponseCode ) throws InternalInvalidResponseCodeException
    {
        switch ( reponseCode )
        {
            //success
            case CREATED:
                return CoapResponseCode.CREATED;
            case DELETED:
                return CoapResponseCode.DELETED;
            case VALID:
                return CoapResponseCode.VALID;
            case CHANGED:
                return CoapResponseCode.CHANGED;
            case CONTENT:
                return CoapResponseCode.CONTENT;
            case CONTINUE:
                return CoapResponseCode.CONTINUE;

            //client error
            case BAD_REQUEST:
                return CoapResponseCode.BAD_REQUEST;
            case UNAUTHORIZED:
                return CoapResponseCode.UNAUTHORIZED;
            case BAD_OPTION:
                return CoapResponseCode.BAD_OPTION;
            case FORBIDDEN:
                return CoapResponseCode.FORBIDDEN;
            case NOT_FOUND:
                return CoapResponseCode.NOT_FOUND;
            case METHOD_NOT_ALLOWED:
                return CoapResponseCode.METHOD_NOT_ALLOWED;
            case NOT_ACCEPTABLE:
                return CoapResponseCode.NOT_ACCEPTABLE;
            case REQUEST_ENTITY_INCOMPLETE:
                return CoapResponseCode.REQUEST_ENTITY_INCOMPLETE;
            case CONFLICT:
                return CoapResponseCode.CONFLICT;
            case PRECONDITION_FAILED:
                return CoapResponseCode.PRECONDITION_FAILED;
            case REQUEST_ENTITY_TOO_LARGE:
                return CoapResponseCode.REQUEST_ENTITY_TOO_LARGE;
            case UNSUPPORTED_CONTENT_FORMAT:
                return CoapResponseCode.UNSUPPORTED_CONTENT_FORMAT;
            case UNPROCESSABLE_ENTITY:
                return CoapResponseCode.UNPROCESSABLE_ENTITY;
            case TOO_MANY_REQUESTS:
                return CoapResponseCode.TOO_MANY_REQUESTS;

            //sever error
            case INTERNAL_SERVER_ERROR:
                return CoapResponseCode.INTERNAL_SERVER_ERROR;
            case NOT_IMPLEMENTED:
                return CoapResponseCode.NOT_IMPLEMENTED;
            case BAD_GATEWAY:
                return CoapResponseCode.BAD_GATEWAY;
            case SERVICE_UNAVAILABLE:
                return CoapResponseCode.SERVICE_UNAVAILABLE;
            case GATEWAY_TIMEOUT:
                return CoapResponseCode.GATEWAY_TIMEOUT;
            case PROXY_NOT_SUPPORTED:
                return CoapResponseCode.PROXY_NOT_SUPPORTED;
            default:
                throw new InternalInvalidResponseCodeException( reponseCode );
        }
    }

    /**
     * Add query parameter string with optional value to multiMap.
     * @param list The list to add the parameter to.
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
