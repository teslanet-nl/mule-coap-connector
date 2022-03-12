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
package nl.teslanet.mule.connectors.coap.test.modules.attributes;


import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.californium.core.coap.CoAP.Code;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import nl.teslanet.mule.connectors.coap.api.CoAPRequestCode;
import nl.teslanet.mule.connectors.coap.internal.attributes.AttributeUtils;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidRequestCodeException;


/**
 * Tests the implementation of the AttributeUtils class requestCode methods.
 *
 */
@RunWith( Parameterized.class )
public class AttributeUtilsRequestCodeTest
{
    /**
     * @return the collection of test parameters.
     */
    @Parameters( name= "requestCode= {0}" )
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []
            { { CoAPRequestCode.GET, Code.GET }, { CoAPRequestCode.POST, Code.POST }, { CoAPRequestCode.PUT, Code.PUT }, { CoAPRequestCode.DELETE, Code.DELETE } }
        );
    }

    /**
     * Actual attributeValue parameter value.
     */
    @Parameter( 0 )
    public CoAPRequestCode attributeValue;

    /**
     * Actual Cf value.
     */
    @Parameter( 1 )
    public Code cfVlalue;

    /**
     * Test translation requestCode attribute to Cf request Code.
     * @throws InternalInvalidRequestCodeException
     */
    @Test
    public void testToRequestCode() throws InternalInvalidRequestCodeException
    {
        assertEquals( cfVlalue, AttributeUtils.toRequestCode( attributeValue ) );
    }

    /**
     * Test translation  Cf request Code to requestCode attribute.
     * @throws InternalInvalidRequestCodeException
     */
    @Test
    public void testToRequestCodeAttribute() throws InternalInvalidRequestCodeException
    {
        assertEquals( attributeValue, AttributeUtils.toRequestCodeAttribute( cfVlalue ) );
    }
}
