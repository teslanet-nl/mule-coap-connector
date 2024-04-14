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
package nl.teslanet.mule.connectors.coap.test.server.modules;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.eclipse.californium.core.coap.option.EmptyOptionDefinition;
import org.eclipse.californium.core.coap.option.IntegerOptionDefinition;
import org.eclipse.californium.core.coap.option.OpaqueOptionDefinition;
import org.eclipse.californium.core.coap.option.OptionDefinition;
import org.eclipse.californium.core.coap.option.StringOptionDefinition;
import org.junit.Test;
import org.mule.runtime.core.api.util.IOUtils;

import nl.teslanet.mule.connectors.coap.api.config.options.OptionFormat;
import nl.teslanet.mule.connectors.coap.api.options.OtherOptionAttribute;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultOtherOptionAttribute;


/**
 * Test the RequestCodeFlags class.
 */
public class OtherOptionsAttributesTest
{
    private void assertAttribute( OtherOptionAttribute attribute, String alias, OptionFormat format, int number, byte[] value, String asHex, Long asNumber, String asString )
    {
        assertEquals( "OtherOptionAttribute has wrong alias", alias, attribute.getAlias() );
        assertEquals( "OtherOptionAttribute has wrong format", format, attribute.getFormat() );
        assertEquals( "OtherOptionAttribute has wrong number", number, attribute.getNumber() );
        assertEquals( "OtherOptionAttribute has wrong length", value.length, attribute.getLength() );
        assertArrayEquals( "OtherOptionAttribute has wrong bytes value", value, IOUtils.toByteArray( attribute.getValue() ) );
        assertEquals( "OtherOptionAttribute has wrong Hex value", asHex, attribute.getValueAsHex() );
        if ( asNumber == null )
        {
            Exception e= assertThrows( NumberFormatException.class, () -> attribute.getValueAsNumber() );
            assertEquals( "Exception has wrong message", "Option{ " + alias + " } is not of type INTEGER.", e.getMessage() );
        }
        else
        {
            assertEquals( "OtherOptionAttribute has wrong long value", asNumber.longValue(), attribute.getValueAsNumber() );
        }
        if ( asString == null )
        {
            Exception e= assertThrows( NumberFormatException.class, () -> attribute.getValueAsString() );
            assertEquals( "Exception has wrong message", "Option{ " + alias + " } is not of type STRING.", e.getMessage() );
        }
        else
        {
            assertEquals( "OtherOptionAttribute has wrong string value", asString, attribute.getValueAsString() );
        }
    }

    @Test
    public void constructorEmpty0Test()
    {
        String alias= "";
        int number= 0;
        byte[] value= {};

        OptionDefinition def= new EmptyOptionDefinition( number, alias );
        OtherOptionAttribute attribute= new DefaultOtherOptionAttribute( def, value );

        assertAttribute( attribute, alias, OptionFormat.EMPTY, number, value, "", null, null );
    }

    @Test
    public void constructorOpaque0Test()
    {
        String alias= "";
        int number= 0;
        byte[] value= {};

        OptionDefinition def= new OpaqueOptionDefinition( number, alias );
        OtherOptionAttribute attribute= new DefaultOtherOptionAttribute( def, value );

        assertAttribute( attribute, alias, OptionFormat.OPAQUE, number, value, "", null, null );
    }

    @Test
    public void constructorInteger0Test()
    {
        String alias= "";
        int number= 0;
        byte[] value= {};

        OptionDefinition def= new IntegerOptionDefinition( number, alias );
        OtherOptionAttribute attribute= new DefaultOtherOptionAttribute( def, value );

        assertAttribute( attribute, alias, OptionFormat.INTEGER, number, value, "", 0L, null );
    }

    @Test
    public void constructorString0Test()
    {
        String alias= "";
        int number= 0;
        byte[] value= {};

        OptionDefinition def= new StringOptionDefinition( number, alias );
        OtherOptionAttribute attribute= new DefaultOtherOptionAttribute( def, value );

        assertAttribute( attribute, alias, OptionFormat.STRING, number, value, "", null, "" );
    }

    @Test
    public void constructorEmptyNullTest()
    {
        String alias= null;
        int number= 0;
        byte[] value= null;

        OptionDefinition def= new EmptyOptionDefinition( number, alias );
        assertThrows( NullPointerException.class, () -> new DefaultOtherOptionAttribute( def, value ) );
    }

    @Test
    public void constructorOpaqueNullTest()
    {
        String alias= null;
        int number= 0;
        byte[] value= null;

        OptionDefinition def= new OpaqueOptionDefinition( number, alias );
        assertThrows( NullPointerException.class, () -> new DefaultOtherOptionAttribute( def, value ) );
    }

    public void constructorIntegerNullTest()
    {
        String alias= null;
        int number= 0;
        byte[] value= null;

        OptionDefinition def= new IntegerOptionDefinition( number, alias );
        assertThrows( NullPointerException.class, () -> new DefaultOtherOptionAttribute( def, value ) );
    }

    public void constructorStringNullTest()
    {
        String alias= null;
        int number= 0;
        byte[] value= null;

        OptionDefinition def= new StringOptionDefinition( number, alias );
        assertThrows( NullPointerException.class, () -> new DefaultOtherOptionAttribute( def, value ) );
    }

    @Test
    public void constructorEmpty1Test()
    {
        String alias= "option-66123";
        int number= 6123;
        byte[] value= { 0x68, 0x6f, 0x69 };

        OptionDefinition def= new EmptyOptionDefinition( number, alias );
        assertThrows( IllegalArgumentException.class, () -> new DefaultOtherOptionAttribute( def, value ) );
    }

    @Test
    public void constructorOpaque1Test()
    {
        String alias= "option-66123";
        int number= 6123;
        byte[] value= { 0x68, 0x6f, 0x69 };

        OptionDefinition def= new OpaqueOptionDefinition( number, alias );
        OtherOptionAttribute attribute= new DefaultOtherOptionAttribute( def, value );

        assertAttribute( attribute, alias, OptionFormat.OPAQUE, number, value, "686f69", null, null );
    }

    @Test
    public void constructorInteger1Test()
    {
        String alias= "option-66123";
        int number= 6123;
        byte[] value= { 0x68, 0x6f, 0x69 };

        OptionDefinition def= new IntegerOptionDefinition( number, alias );
        OtherOptionAttribute attribute= new DefaultOtherOptionAttribute( def, value );

        assertAttribute( attribute, alias, OptionFormat.INTEGER, number, value, "686f69", 6844265L, null );
    }

    @Test
    public void constructorString1Test()
    {
        String alias= "option-66123";
        int number= 6123;
        byte[] value= { 0x68, 0x6f, 0x69 };

        OptionDefinition def= new StringOptionDefinition( number, alias );
        OtherOptionAttribute attribute= new DefaultOtherOptionAttribute( def, value );

        assertAttribute( attribute, alias, OptionFormat.STRING, number, value, "686f69", null, "hoi" );
    }

    @Test
    public void constructorEmpty3Test()
    {
        String alias= "option-65123";
        int number= 65123;
        String text= "This is a rather long option value.";
        byte[] value= text.getBytes();

        OptionDefinition def= new EmptyOptionDefinition( number, alias );
        assertThrows( IllegalArgumentException.class, () -> new DefaultOtherOptionAttribute( def, value ) );
    }

    @Test
    public void constructorOpaque3Test()
    {
        String alias= "option-65123";
        int number= 65123;
        String text= "This is a rather long option value.";
        byte[] value= text.getBytes();

        OptionDefinition def= new OpaqueOptionDefinition( number, alias );
        OtherOptionAttribute attribute= new DefaultOtherOptionAttribute( def, value );

        assertAttribute( attribute, alias, OptionFormat.OPAQUE, number, value, "54686973206973206120726174686572206c6f6e67206f7074696f6e2076616c75652e", null, null );
    }

    @Test
    public void constructorInteger3Test()
    {
        String alias= "option-65123";
        int number= 65123;
        String text= "This is a rather long option value.";
        byte[] value= text.getBytes();

        OptionDefinition def= new IntegerOptionDefinition( number, alias );
        assertThrows( IllegalArgumentException.class, () -> new DefaultOtherOptionAttribute( def, value ) );
    }

    @Test
    public void constructorString3Test()
    {
        String alias= "option-65123";
        int number= 65123;
        String text= "This is a rather long option value.";
        byte[] value= text.getBytes();

        OptionDefinition def= new StringOptionDefinition( number, alias );
        OtherOptionAttribute attribute= new DefaultOtherOptionAttribute( def, value );

        assertAttribute( attribute, alias, OptionFormat.STRING, number, value, "54686973206973206120726174686572206c6f6e67206f7074696f6e2076616c75652e", null, text );
    }

    @Test
    public void traits000Test()
    {
        String alias= "Location-Path";
        int number= 8; //Location-Path
        byte[] value= { 0x68, 0x6f, 0x69 };

        OpaqueOptionDefinition def= new OpaqueOptionDefinition( number, alias );
        OtherOptionAttribute attribute= new DefaultOtherOptionAttribute( def, value );

        assertFalse( "OtherOptionAttribute has wrong critical trait", attribute.isCritical() );
        assertFalse( "OtherOptionAttribute has wrong unSafe trait", attribute.isUnsafe() );
        assertFalse( "OtherOptionAttribute has wrong noCacheKey trait", attribute.isNoCacheKey() );
    }

    @Test
    public void traits001Test()
    {
        String alias= "Size1";
        int number= 60; //Size1
        byte[] value= { 0x68, 0x6f, 0x69 };

        OpaqueOptionDefinition def= new OpaqueOptionDefinition( number, alias );
        OtherOptionAttribute attribute= new DefaultOtherOptionAttribute( def, value );

        assertFalse( "OtherOptionAttribute has wrong critical trait", attribute.isCritical() );
        assertFalse( "OtherOptionAttribute has wrong unSafe trait", attribute.isUnsafe() );
        assertTrue( "OtherOptionAttribute has wrong noCacheKey trait", attribute.isNoCacheKey() );
    }

    @Test
    public void traits010Test()
    {
        String alias= "No-Response";
        int number= 258; //No-Response
        byte[] value= { 0x68, 0x6f, 0x69 };

        OpaqueOptionDefinition def= new OpaqueOptionDefinition( number, alias );
        OtherOptionAttribute attribute= new DefaultOtherOptionAttribute( def, value );

        assertFalse( "OtherOptionAttribute has wrong critical trait", attribute.isCritical() );
        assertTrue( "OtherOptionAttribute has wrong unSafe trait", attribute.isUnsafe() );
        assertFalse( "OtherOptionAttribute has wrong noCacheKey trait", attribute.isNoCacheKey() );
    }

    @Test
    public void traits011Test()
    {
        String alias= "Max-Age";
        int number= 14; //Max-Age
        byte[] value= { 0x68, 0x6f, 0x69 };

        OpaqueOptionDefinition def= new OpaqueOptionDefinition( number, alias );
        OtherOptionAttribute attribute= new DefaultOtherOptionAttribute( def, value );

        assertFalse( "OtherOptionAttribute has wrong critical trait", attribute.isCritical() );
        assertTrue( "OtherOptionAttribute has wrong unSafe trait", attribute.isUnsafe() );
        //When Unsafe the option noCacheKey flag has no meaning. 
        assertFalse( "OtherOptionAttribute has wrong noCacheKey trait", attribute.isNoCacheKey() );
    }

    @Test
    public void traits100Test()
    {
        String alias= "If-Match";
        int number= 1; //If-Match
        byte[] value= { 0x68, 0x6f, 0x69 };

        OpaqueOptionDefinition def= new OpaqueOptionDefinition( number, alias );
        OtherOptionAttribute attribute= new DefaultOtherOptionAttribute( def, value );

        assertTrue( "OtherOptionAttribute has wrong critical trait", attribute.isCritical() );
        assertFalse( "OtherOptionAttribute has wrong unSafe trait", attribute.isUnsafe() );
        assertFalse( "OtherOptionAttribute has wrong noCacheKey trait", attribute.isNoCacheKey() );
    }

    @Test
    public void traits101Test()
    {
        String alias= "option-65021";
        int number= 65021;
        byte[] value= { 0x68, 0x6f, 0x69 };

        OpaqueOptionDefinition def= new OpaqueOptionDefinition( number, alias );
        OtherOptionAttribute attribute= new DefaultOtherOptionAttribute( def, value );

        assertTrue( "OtherOptionAttribute has wrong critical trait", attribute.isCritical() );
        assertFalse( "OtherOptionAttribute has wrong unSafe trait", attribute.isUnsafe() );
        assertTrue( "OtherOptionAttribute has wrong noCacheKey trait", attribute.isNoCacheKey() );
    }

    @Test
    public void traits110Test()
    {
        String alias= "Uri-Host";
        int number= 3; //Uri-Host
        byte[] value= { 0x68, 0x6f, 0x69 };

        OpaqueOptionDefinition def= new OpaqueOptionDefinition( number, alias );
        OtherOptionAttribute attribute= new DefaultOtherOptionAttribute( def, value );

        assertTrue( "OtherOptionAttribute has wrong critical trait", attribute.isCritical() );
        assertTrue( "OtherOptionAttribute has wrong unSafe trait", attribute.isUnsafe() );
        assertFalse( "OtherOptionAttribute has wrong noCacheKey trait", attribute.isNoCacheKey() );
    }

    @Test
    public void traits111Test()
    {
        String alias= "option-65023";
        int number= 65023;
        byte[] value= { 0x68, 0x6f, 0x69 };

        OpaqueOptionDefinition def= new OpaqueOptionDefinition( number, alias );
        OtherOptionAttribute attribute= new DefaultOtherOptionAttribute( def, value );

        assertTrue( "OtherOptionAttribute has wrong critical trait", attribute.isCritical() );
        assertTrue( "OtherOptionAttribute has wrong unSafe trait", attribute.isUnsafe() );
        //When Unsafe the option noCacheKey flag has no meaning. 
        assertFalse( "OtherOptionAttribute has wrong noCacheKey trait", attribute.isNoCacheKey() );
    }

    @Test
    public void equalsTest()
    {
        String alias= "option-65023";
        int number1= 65023;
        byte[] value1= { 0x68, 0x6f, 0x69 };
        int number2= 65021;
        byte[] value2= { 0x68, 0x6f, 0x70 };

        OpaqueOptionDefinition def1= new OpaqueOptionDefinition( number1, alias );
        OtherOptionAttribute attribute1= new DefaultOtherOptionAttribute( def1, value1 );
        OpaqueOptionDefinition def2= new OpaqueOptionDefinition( number2, alias );
        OtherOptionAttribute attribute2= new DefaultOtherOptionAttribute( def2, value2 );
        OpaqueOptionDefinition def3= new OpaqueOptionDefinition( number1, alias );
        OtherOptionAttribute attribute3= new DefaultOtherOptionAttribute( def3, value1 );
        OpaqueOptionDefinition def4= new OpaqueOptionDefinition( number1, alias );
        OtherOptionAttribute attribute4= new DefaultOtherOptionAttribute( def4, value2 );
        OpaqueOptionDefinition def5= new OpaqueOptionDefinition( number2, alias );
        OtherOptionAttribute attribute5= new DefaultOtherOptionAttribute( def5, value1 );

        assertEquals( "attribute 1 equals attribute 1 returns wrong result", attribute1, attribute1 );
        assertNotEquals( "attribute 1 equals null returns wrong result", attribute1, null );
        assertNotEquals( "attribute 1 equals attribute 2 returns wrong result", attribute1, attribute2 );
        assertNotEquals( "attribute 2 equals attribute 1 returns wrong result", attribute2, attribute1 );
        assertEquals( "attribute 1 equals attribute 3 returns wrong result", attribute1, attribute3 );
        assertEquals( "attribute 3 equals attribute 1 returns wrong result", attribute3, attribute1 );
        assertNotEquals( "attribute 4 equals attribute 1 returns wrong result", attribute4, attribute1 );
        assertNotEquals( "attribute 4 equals attribute 2 returns wrong result", attribute4, attribute2 );
        assertNotEquals( "attribute 5 equals attribute 1 returns wrong result", attribute5, attribute1 );
        assertNotEquals( "attribute 5 equals attribute 2 returns wrong result", attribute5, attribute2 );
    }

    @Test
    public void hashCodeTest()
    {
        String alias= "option-65023";
        int number1= 65023;
        byte[] value1= { 0x68, 0x6f, 0x69 };
        int number2= 65021;
        byte[] value2= { 0x68, 0x6f, 0x70 };

        OpaqueOptionDefinition def1= new OpaqueOptionDefinition( number1, alias );
        OtherOptionAttribute attribute1= new DefaultOtherOptionAttribute( def1, value1 );
        OpaqueOptionDefinition def2= new OpaqueOptionDefinition( number2, alias );
        OtherOptionAttribute attribute2= new DefaultOtherOptionAttribute( def2, value2 );
        OpaqueOptionDefinition def3= new OpaqueOptionDefinition( number1, alias );
        OtherOptionAttribute attribute3= new DefaultOtherOptionAttribute( def3, value1 );
        OpaqueOptionDefinition def4= new OpaqueOptionDefinition( number1, alias );
        OtherOptionAttribute attribute4= new DefaultOtherOptionAttribute( def4, value2 );
        OpaqueOptionDefinition def5= new OpaqueOptionDefinition( number2, alias );
        OtherOptionAttribute attribute5= new DefaultOtherOptionAttribute( def5, value1 );

        assertEquals( "hashcode flag 1 has wrong value", 2150916, attribute1.hashCode() );
        assertEquals( "hashcode flag 2 has wrong value", 2150861, attribute2.hashCode() );
        assertEquals( "hashcode flag 3 has wrong value", 2150916, attribute3.hashCode() );
        assertEquals( "hashcode flag 4 has wrong value", 2150923, attribute4.hashCode() );
        assertEquals( "hashcode flag 5 has wrong value", 2150854, attribute5.hashCode() );
    }
}
