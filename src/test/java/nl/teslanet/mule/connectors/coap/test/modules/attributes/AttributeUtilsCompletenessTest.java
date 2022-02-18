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


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.api.CoAPRequestCode;
import nl.teslanet.mule.connectors.coap.api.CoAPResponseCode;
import nl.teslanet.mule.connectors.coap.internal.attributes.AttributeUtils;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidRequestCodeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidResponseCodeException;


/**
 * Tests the implementation of the AttributeUtils class on completeness.
 *
 */
public class AttributeUtilsCompletenessTest
{
    /**
     * Test whether all requestCode attributes can be translated to Cf Code.
     * @throws InternalInvalidRequestCodeException
     */
    @Test
    public void testToRequestCode() throws InternalInvalidRequestCodeException
    {
        for ( CoAPRequestCode requestCode : CoAPRequestCode.values() )
        {
            assertNotNull( requestCode + " translation to Cf Code failed", AttributeUtils.toRequestCode( requestCode ) );
        }
    }

    /**
     * Test whether null throws NPE.
     * @throws InternalInvalidRequestCodeException
     */
    @Test
    public void testToRequestCodeNull() throws InternalInvalidRequestCodeException
    {
        assertThrows( "translation null to Cf Code did not throw exception", NullPointerException.class, () -> {
            AttributeUtils.toRequestCode( null );
        } );
    }

    /**
     * Test whether all requestCode attributes can be translated to Cf Code.
     * @throws InternalInvalidRequestCodeException
     */
    @Test
    public void testToRequestCodeAttribute() throws InternalInvalidRequestCodeException
    {
        for ( Code requestCode : Code.values() )
        {
            //TODO implement FETCH, PATCH, iPATCH
            if ( requestCode == Code.FETCH ) continue;
            if ( requestCode == Code.PATCH ) continue;
            if ( requestCode == Code.IPATCH ) continue;
            //not IANA code, ignore
            if ( requestCode == Code.CUSTOM_30 ) continue;
            assertNotNull( requestCode + " translation to requestCode attribute failed", AttributeUtils.toRequestCodeAttribute( requestCode ) );
        }
    }

    /**
     * Test whether null throws NPE.
     * @throws InternalInvalidRequestCodeException
     */
    @Test
    public void testToRequestCodeAttributeNull() throws InternalInvalidRequestCodeException
    {
        assertThrows( "translation null to requestCode attribute did not throw exception", NullPointerException.class, () -> {
            AttributeUtils.toRequestCodeAttribute( null );
        } );
    }

    /**
    * Test whether all responseCode attributes can be translated to Cf ResponseCode.
    * @throws InternalInvalidResponseCodeException 
    */
    @Test
    public void toResponseCode() throws InternalInvalidResponseCodeException
    {
        for ( CoAPResponseCode responseCode : CoAPResponseCode.values() )
        {
            assertNotNull( responseCode + " translation to Cf ResponseCode failed", AttributeUtils.toResponseCode( responseCode ) );
        }
    }

    /**
     * Test whether null throws NPE.
     * @throws InternalInvalidResponseCodeException
     */
    @Test
    public void testToResponseCodeNull() throws InternalInvalidResponseCodeException
    {
        assertThrows( "translation null to Cf ResponseCode did not throw exception", NullPointerException.class, () -> {
            AttributeUtils.toResponseCode( null );
        } );
    }

    /**
    * Test whether all responseCode attributes can be translated to Cf ResponseCode.
    * @throws InternalInvalidResponseCodeException 
    */
    @Test
    public void toResponseCodeAttribute() throws InternalInvalidResponseCodeException
    {
        for ( ResponseCode responseCode : ResponseCode.values() )
        {
            //not IANA code, ignore
            if ( responseCode == ResponseCode._UNKNOWN_SUCCESS_CODE ) continue;
            assertNotNull( responseCode + " translation to Cf ResponseCode failed", AttributeUtils.toResponseCodeAttribute( responseCode ) );
        }
    }

    /**
     * Test whether null throws NPE.
     * @throws InternalInvalidResponseCodeException
     */
    @Test
    public void testToResponseCodeAttributeNull() throws InternalInvalidResponseCodeException
    {
        assertThrows( "translation null to Cf ResponseCode did not throw exception", NullPointerException.class, () -> {
            AttributeUtils.toResponseCodeAttribute( null );
        } );
    }

    /**
     * Test whether all responseCode attributes can be translated to Cf ResponseCode using default.
     * @throws InternalInvalidRequestCodeException
     * @throws InternalInvalidResponseCodeException 
     */
    @Test
    public void toResponseCodeUsingDefault() throws InternalInvalidRequestCodeException, InternalInvalidResponseCodeException
    {
        for ( CoAPResponseCode responseCode : CoAPResponseCode.values() )
        {
            assertNotNull( responseCode + " translation to Cf ResponseCode failed", AttributeUtils.toResponseCode( responseCode, responseCode ) );
        }
    }

    /**
     * Test whether all responseCode attributes can be translated to Cf ResponseCode using null and default.
     * @throws InternalInvalidResponseCodeException 
     */
    @Test
    public void toResponseCodeUsingNullAndDefault() throws InternalInvalidResponseCodeException
    {
        for ( CoAPResponseCode responseCode : CoAPResponseCode.values() )
        {
            assertNotNull( responseCode + " default translation to Cf ResponseCode failed", AttributeUtils.toResponseCode( null, responseCode ) );
        }
    }

    /**
     * Test whether all responseCode attributes can be translated to Cf ResponseCode using null and null default.
     * @throws InternalInvalidResponseCodeException 
     */
    @Test
    public void toResponseCodeUsingNullAndNullDefault() throws InternalInvalidResponseCodeException
    {
        assertThrows( "null default translation to Cf ResponseCode did not throw NPE", NullPointerException.class, () -> {
            AttributeUtils.toResponseCode( null, null );
        } );
    }
}
