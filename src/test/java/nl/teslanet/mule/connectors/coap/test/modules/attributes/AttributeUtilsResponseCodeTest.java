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
package nl.teslanet.mule.connectors.coap.test.modules.attributes;


import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import nl.teslanet.mule.connectors.coap.api.ResponseParams.CoAPResponseCode;
import nl.teslanet.mule.connectors.coap.internal.attributes.AttributeUtils;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidResponseCodeException;


/**
 * Tests the implementation of the AttributeUtils class responseCode methods.
 *
 */
@RunWith(Parameterized.class)
public class AttributeUtilsResponseCodeTest
{
    /**
     * @return the collection of test parameters.
     */
    @Parameters(name= "responseCode= {0}")
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []{
                { CoAPResponseCode.CREATED, ResponseCode.CREATED },
                { CoAPResponseCode.DELETED, ResponseCode.DELETED },
                { CoAPResponseCode.VALID, ResponseCode.VALID },
                { CoAPResponseCode.CHANGED, ResponseCode.CHANGED },
                { CoAPResponseCode.CONTENT, ResponseCode.CONTENT },
                { CoAPResponseCode.CONTINUE, ResponseCode.CONTINUE },

                //client error
                { CoAPResponseCode.BAD_REQUEST, ResponseCode.BAD_REQUEST },
                { CoAPResponseCode.UNAUTHORIZED, ResponseCode.UNAUTHORIZED },
                { CoAPResponseCode.BAD_OPTION, ResponseCode.BAD_OPTION },
                { CoAPResponseCode.FORBIDDEN, ResponseCode.FORBIDDEN },
                { CoAPResponseCode.NOT_FOUND, ResponseCode.NOT_FOUND },
                { CoAPResponseCode.METHOD_NOT_ALLOWED, ResponseCode.METHOD_NOT_ALLOWED },
                { CoAPResponseCode.NOT_ACCEPTABLE, ResponseCode.NOT_ACCEPTABLE },
                { CoAPResponseCode.REQUEST_ENTITY_INCOMPLETE, ResponseCode.REQUEST_ENTITY_INCOMPLETE },
                { CoAPResponseCode.CONFLICT, ResponseCode.CONFLICT },
                { CoAPResponseCode.PRECONDITION_FAILED, ResponseCode.PRECONDITION_FAILED },
                { CoAPResponseCode.REQUEST_ENTITY_TOO_LARGE, ResponseCode.REQUEST_ENTITY_TOO_LARGE },
                { CoAPResponseCode.UNSUPPORTED_CONTENT_FORMAT, ResponseCode.UNSUPPORTED_CONTENT_FORMAT },
                { CoAPResponseCode.UNPROCESSABLE_ENTITY, ResponseCode.UNPROCESSABLE_ENTITY },
                { CoAPResponseCode.TOO_MANY_REQUESTS, ResponseCode.TOO_MANY_REQUESTS },

                //sever error
                { CoAPResponseCode.INTERNAL_SERVER_ERROR, ResponseCode.INTERNAL_SERVER_ERROR },
                { CoAPResponseCode.NOT_IMPLEMENTED, ResponseCode.NOT_IMPLEMENTED },
                { CoAPResponseCode.BAD_GATEWAY, ResponseCode.BAD_GATEWAY },
                { CoAPResponseCode.SERVICE_UNAVAILABLE, ResponseCode.SERVICE_UNAVAILABLE },
                { CoAPResponseCode.GATEWAY_TIMEOUT, ResponseCode.GATEWAY_TIMEOUT },
                { CoAPResponseCode.PROXY_NOT_SUPPORTED, ResponseCode.PROXY_NOT_SUPPORTED } } );
    }

    /**
     * Actual attributeValue parameter value.
     */
    @Parameter(0)
    public CoAPResponseCode attributeValue;

    /**
     * Actual expected value.
     */
    @Parameter(1)
    public ResponseCode cfValue;

    /**
     * Test translation of responseCode attribute to Cf ResponseCode.
     * @throws InternalInvalidResponseCodeException
     */
    @Test
    public void testToRequestCode() throws InternalInvalidResponseCodeException
    {
        assertEquals( cfValue, AttributeUtils.toResponseCode( attributeValue ) );
    }

    /**
     * Test translation of responseCode attribute to Cf ResponseCode with default value.
     * @throws InternalInvalidResponseCodeException
     */
    @Test
    public void testToRequestCodeWithDefault() throws InternalInvalidResponseCodeException
    {
        assertEquals( cfValue, AttributeUtils.toResponseCode( null, attributeValue ) );
    }

    /**
    * Test translation of Cf ResponseCode to responseCode attribute.
    * @throws InternalInvalidResponseCodeException
    */
    @Test
    public void testToResponseCodeAttribute() throws InternalInvalidResponseCodeException
    {
        assertEquals( attributeValue, AttributeUtils.toResponseCodeAttribute( cfValue ) );
    }
}
