/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2024 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.test.options;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.californium.core.coap.option.OptionDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import nl.teslanet.mule.connectors.coap.api.config.options.OtherOptionConfig;
import nl.teslanet.mule.connectors.coap.api.options.OptionFormat;
import nl.teslanet.mule.connectors.coap.internal.utils.MessageUtils;


/**
 * Test option definition handling.
 *
 */
@RunWith( Parameterized.class )
public class OptionDefinitionTest
{
    /**
     * The option alias.
     */
    @Parameter( 0 )
    public String alias;

    /**
     * The option number.
     */
    @Parameter( 1 )
    public int number;

    /**
     * The option format.
     */
    @Parameter( 2 )
    public OptionFormat format;

    /**
     * The option multiplicity.
     */
    @Parameter( 3 )
    public boolean singleValue;

    /**
     * The option minimum length.
     */
    @Parameter( 4 )
    public Integer minBytes;

    /**
     * The option maximum length.
     */
    @Parameter( 5 )
    public Integer maxBytes;

    /**
     * The expected multiplicity.
     */
    @Parameter( 6 )
    public boolean expectedSingleValue;

    /**
     * The expected minimum length.
     */
    @Parameter( 7 )
    public Integer expectedMinBytes;

    /**
     * The expected maximum length.
     */
    @Parameter( 8 )
    public Integer expectedMaxBytes;

    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters( name= "alias= {0}" )
    public static Collection< Object[] > data()
    {
        return Arrays
            .asList( new Object [] []
            {
                { "option-empty-single", 65001, OptionFormat.EMPTY, true, 0, 0, true, 0, 0 },
                { "option-empty", 65002, OptionFormat.EMPTY, false, 0, 0, true, 0, 0 },
                { "option-integer-1-4-single", 65003, OptionFormat.INTEGER, true, 1, 4, true, 1, 4 },
                { "option-integer-1-4", 65004, OptionFormat.INTEGER, false, 1, 4, false, 1, 4 },
                { "option-integer-3-3", 65005, OptionFormat.INTEGER, false, 3, 3, false, 3, null },
                {
                    "option-integer-0-max",
                    65006,
                    OptionFormat.INTEGER,
                    false,
                    0,
                    Integer.MAX_VALUE,
                    false,
                    0,
                    Integer.MAX_VALUE },
                { "option-integer-neg", 65007, OptionFormat.INTEGER, false, -3, -4, false, -3, -4 },
                { "option-string-1-4-single", 65003, OptionFormat.STRING, true, 1, 4, true, 1, 4 },
                { "option-string-1-4", 65004, OptionFormat.STRING, false, 1, 4, false, 1, 4 },
                { "option-string-3-3", 65005, OptionFormat.STRING, false, 3, 3, false, 3, null },
                {
                    "option-string-0-max",
                    65006,
                    OptionFormat.STRING,
                    false,
                    0,
                    Integer.MAX_VALUE,
                    false,
                    0,
                    Integer.MAX_VALUE },
                { "option-string-neg", 65007, OptionFormat.STRING, false, -3, -4, false, -3, -4 },
                { "option-opaque-1-4-single", 65003, OptionFormat.OPAQUE, true, 1, 4, true, 1, 4 },
                { "option-opaque-1-4", 65004, OptionFormat.OPAQUE, false, 1, 4, false, 1, 4 },
                { "option-opaque-3-3", 65005, OptionFormat.OPAQUE, false, 3, 3, false, 3, null },
                {
                    "option-opaque-0-max",
                    65006,
                    OptionFormat.OPAQUE,
                    false,
                    0,
                    Integer.MAX_VALUE,
                    false,
                    0,
                    Integer.MAX_VALUE },
                { "option-opaque-neg", 65007, OptionFormat.OPAQUE, false, -3, -4, false, -3, -4 },

            } );
    }

    @SuppressWarnings( "deprecation" )
    @Test
    public void testToCfOptionDefinition()
    {
        OtherOptionConfig config= new OtherOptionConfig( alias, number, format, singleValue, minBytes, maxBytes );
        OptionDefinition converted= MessageUtils.toCfOptionDefinition( config );

        assertEquals( "wrong option definition alias", alias, converted.getName() );
        assertEquals( "wrong option definition number", number, converted.getNumber() );
        assertEquals(
            "wrong option definition format",
            format,
            MessageUtils.toOptionFormat( converted.getFormat() )
        );
        assertEquals( "wrong option definition singleValue", expectedSingleValue, converted.isSingleValue() );
        assertEquals(
            "wrong option definition singleValue",
            minBytes,
            Integer.valueOf( converted.getValueLengths()[0] )
        );
        assertEquals(
            "wrong option definition singleValue",
            maxBytes,
            Integer.valueOf( converted.getValueLengths().length > 1 ? converted.getValueLengths()[1] : null )
        );
    }

    @Test
    public void testEquals()
    {
        OptionDefinition converted1= MessageUtils
            .toCfOptionDefinition( new OtherOptionConfig( alias, number, format, singleValue, minBytes, maxBytes ) );
        OptionDefinition converted2= MessageUtils
            .toCfOptionDefinition( new OtherOptionConfig( alias, number, format, singleValue, minBytes, maxBytes ) );

        assertTrue( "isEqual definition gave wrong result", MessageUtils.isEqual( converted1, converted2 ) );
    }

    @Test
    public void testUnEquals()
    {
        OptionDefinition converted1= MessageUtils
            .toCfOptionDefinition( new OtherOptionConfig( alias, number, format, singleValue, minBytes, maxBytes ) );
        OptionDefinition converted2= MessageUtils
            .toCfOptionDefinition(
                new OtherOptionConfig( "different", number, format, singleValue, minBytes, maxBytes )
            );
        OptionDefinition converted3= MessageUtils
            .toCfOptionDefinition( new OtherOptionConfig( alias, 65000, format, singleValue, minBytes, maxBytes ) );
        OptionDefinition converted4= MessageUtils
            .toCfOptionDefinition(
                new OtherOptionConfig(
                    alias,
                    number,
                    format == OptionFormat.INTEGER ? OptionFormat.OPAQUE : OptionFormat.INTEGER,
                    singleValue,
                    minBytes,
                    maxBytes
                )
            );
        OptionDefinition converted5= MessageUtils
            .toCfOptionDefinition( new OtherOptionConfig( alias, number, format, !singleValue, minBytes, maxBytes ) );
        OptionDefinition converted6= MessageUtils
            .toCfOptionDefinition(
                new OtherOptionConfig( alias, number, format, singleValue, minBytes + 1, maxBytes )
            );
        OptionDefinition converted7= MessageUtils
            .toCfOptionDefinition(
                new OtherOptionConfig( alias, number, format, singleValue, minBytes, maxBytes - 1 )
            );

        assertFalse( "isEqual definition gave wrong result", MessageUtils.isEqual( converted1, converted2 ) );
        assertFalse( "isEqual definition gave wrong result", MessageUtils.isEqual( converted1, converted3 ) );
        assertFalse( "isEqual definition gave wrong result", MessageUtils.isEqual( converted1, converted4 ) );
        assertFalse(
            "isEqual definition gave wrong result",
            MessageUtils.isEqual( converted1, converted5 ) && format != OptionFormat.EMPTY
        );
        assertFalse(
            "isEqual definition gave wrong result",
            MessageUtils.isEqual( converted1, converted6 ) && format != OptionFormat.EMPTY
        );
        assertFalse(
            "isEqual definition gave wrong result",
            MessageUtils.isEqual( converted1, converted7 ) && format != OptionFormat.EMPTY
        );
    }
}
