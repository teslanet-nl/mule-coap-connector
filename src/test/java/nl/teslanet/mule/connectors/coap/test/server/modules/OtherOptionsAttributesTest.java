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
package nl.teslanet.mule.connectors.coap.test.server.modules;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mule.runtime.core.api.util.IOUtils;

import nl.teslanet.mule.connectors.coap.api.options.OtherOptionAttribute;


/**
 * Test the RequestCodeFlags class.
 */
public class OtherOptionsAttributesTest
{
    @Test
    public void defaultConstructorTest()
    {
        OtherOptionAttribute attribute= new OtherOptionAttribute();

        assertEquals( "OtherOptionAttribute has wrong number", 0, attribute.getNumber() );
        assertArrayEquals( "OtherOptionAttribute has wrong bytes value", new byte [0], IOUtils.toByteArray( attribute.getValue() ) );
        assertEquals( "OtherOptionAttribute has wrong Hex value", "", attribute.getValueAsHexString() );
        assertEquals( "OtherOptionAttribute has wrong long value", 0L, attribute.getValueAsNumber() );
        assertEquals( "OtherOptionAttribute has wrong String value", "", attribute.getValueAsString() );
    }

    @Test
    public void constructor0Test()
    {
        int number= 0;
        byte[] value= {};

        OtherOptionAttribute attribute= new OtherOptionAttribute( number, value );

        assertEquals( "OtherOptionAttribute has wrong number", 0, attribute.getNumber() );
        assertArrayEquals( "OtherOptionAttribute has wrong bytes value", new byte [0], IOUtils.toByteArray( attribute.getValue() ) );
        assertEquals( "OtherOptionAttribute has wrong Hex value", "", attribute.getValueAsHexString() );
        assertEquals( "OtherOptionAttribute has wrong long value", 0L, attribute.getValueAsNumber() );
        assertEquals( "OtherOptionAttribute has wrong String value", "", attribute.getValueAsString() );
    }

    @Test
    public void constructorNullTest()
    {
        int number= 0;
        byte[] value= null;

        OtherOptionAttribute attribute= new OtherOptionAttribute( number, value );

        assertEquals( "OtherOptionAttribute has wrong number", number, attribute.getNumber() );
        assertArrayEquals( "OtherOptionAttribute has wrong bytes value", new byte [0], IOUtils.toByteArray( attribute.getValue() ) );
        assertEquals( "OtherOptionAttribute has wrong Hex value", "", attribute.getValueAsHexString() );
        assertEquals( "OtherOptionAttribute has wrong long value", 0L, attribute.getValueAsNumber() );
        assertEquals( "OtherOptionAttribute has wrong String value", "", attribute.getValueAsString() );
    }

    @Test
    public void constructor1Test()
    {
        int number= 6123;
        byte[] value= { 0x68, 0x6f, 0x69 };

        OtherOptionAttribute attribute= new OtherOptionAttribute( number, value );

        assertEquals( "OtherOptionAttribute has wrong number", number, attribute.getNumber() );
        assertArrayEquals( "OtherOptionAttribute has wrong bytes value", value, IOUtils.toByteArray( attribute.getValue() ) );
        assertEquals( "OtherOptionAttribute has wrong Hex value", "686f69", attribute.getValueAsHexString() );
        assertEquals( "OtherOptionAttribute has wrong long value", 0x686f69L, attribute.getValueAsNumber() );
        assertEquals( "OtherOptionAttribute has wrong String value", "hoi", attribute.getValueAsString() );
    }

    @Test
    public void constructor3Test()
    {
        int number= 66123;
        String text= "This is a rather long option value.";
        byte[] value= text.getBytes();

        OtherOptionAttribute attribute= new OtherOptionAttribute( number, value );

        assertEquals( "OtherOptionAttribute has wrong number", number, attribute.getNumber() );
        assertArrayEquals( "OtherOptionAttribute has wrong bytes value", value, IOUtils.toByteArray( attribute.getValue() ) );
        assertEquals( "OtherOptionAttribute has wrong Hex value", "54686973206973206120726174686572206c6f6e67206f7074696f6e2076616c75652e", attribute.getValueAsHexString() );

        NumberFormatException e= assertThrows( NumberFormatException.class, () -> {
            @SuppressWarnings( "unused" )
            long longValue= attribute.getValueAsNumber();
        } );
        assertTrue( "Exception has wrong message", e.getMessage().contains( "too large" ) );
        assertEquals( "OtherOptionAttribute has wrong String value", text, attribute.getValueAsString() );
    }

    @Test
    public void traits000Test()
    {
        int number= 8; //Location-Path
        byte[] value= { 0x68, 0x6f, 0x69 };

        OtherOptionAttribute attribute= new OtherOptionAttribute( number, value );

        assertFalse( "OtherOptionAttribute has wrong critical trait", attribute.isCritical() );
        assertFalse( "OtherOptionAttribute has wrong unSafe trait", attribute.isUnSafe() );
        assertFalse( "OtherOptionAttribute has wrong noCacheKey trait", attribute.isNoCacheKey() );
    }

    @Test
    public void traits001Test()
    {
        int number= 60; //Size1
        byte[] value= { 0x68, 0x6f, 0x69 };

        OtherOptionAttribute attribute= new OtherOptionAttribute( number, value );

        assertFalse( "OtherOptionAttribute has wrong critical trait", attribute.isCritical() );
        assertFalse( "OtherOptionAttribute has wrong unSafe trait", attribute.isUnSafe() );
        assertTrue( "OtherOptionAttribute has wrong noCacheKey trait", attribute.isNoCacheKey() );
    }

    @Test
    public void traits010Test()
    {
        int number= 258; //No-Response
        byte[] value= { 0x68, 0x6f, 0x69 };

        OtherOptionAttribute attribute= new OtherOptionAttribute( number, value );

        assertFalse( "OtherOptionAttribute has wrong critical trait", attribute.isCritical() );
        assertTrue( "OtherOptionAttribute has wrong unSafe trait", attribute.isUnSafe() );
        assertFalse( "OtherOptionAttribute has wrong noCacheKey trait", attribute.isNoCacheKey() );
    }

    @Test
    public void traits011Test()
    {
        int number= 14; //Max-Age
        byte[] value= { 0x68, 0x6f, 0x69 };

        OtherOptionAttribute attribute= new OtherOptionAttribute( number, value );

        assertFalse( "OtherOptionAttribute has wrong critical trait", attribute.isCritical() );
        assertTrue( "OtherOptionAttribute has wrong unSafe trait", attribute.isUnSafe() );
        //When Unsafe the option noCacheKey flag has no meaning. 
        assertFalse( "OtherOptionAttribute has wrong noCacheKey trait", attribute.isNoCacheKey() );
    }

    @Test
    public void traits100Test()
    {
        int number= 1; //If-Match
        byte[] value= { 0x68, 0x6f, 0x69 };

        OtherOptionAttribute attribute= new OtherOptionAttribute( number, value );

        assertTrue( "OtherOptionAttribute has wrong critical trait", attribute.isCritical() );
        assertFalse( "OtherOptionAttribute has wrong unSafe trait", attribute.isUnSafe() );
        assertFalse( "OtherOptionAttribute has wrong noCacheKey trait", attribute.isNoCacheKey() );
    }

    @Test
    public void traits101Test()
    {
        int number= 65021;
        byte[] value= { 0x68, 0x6f, 0x69 };

        OtherOptionAttribute attribute= new OtherOptionAttribute( number, value );

        assertTrue( "OtherOptionAttribute has wrong critical trait", attribute.isCritical() );
        assertFalse( "OtherOptionAttribute has wrong unSafe trait", attribute.isUnSafe() );
        assertTrue( "OtherOptionAttribute has wrong noCacheKey trait", attribute.isNoCacheKey() );
    }

    @Test
    public void traits110Test()
    {
        int number= 3; //Uri-Host
        byte[] value= { 0x68, 0x6f, 0x69 };

        OtherOptionAttribute attribute= new OtherOptionAttribute( number, value );

        assertTrue( "OtherOptionAttribute has wrong critical trait", attribute.isCritical() );
        assertTrue( "OtherOptionAttribute has wrong unSafe trait", attribute.isUnSafe() );
        assertFalse( "OtherOptionAttribute has wrong noCacheKey trait", attribute.isNoCacheKey() );
    }

    @Test
    public void traits111Test()
    {
        int number= 65023;
        byte[] value= { 0x68, 0x6f, 0x69 };

        OtherOptionAttribute attribute= new OtherOptionAttribute( number, value );

        assertTrue( "OtherOptionAttribute has wrong critical trait", attribute.isCritical() );
        assertTrue( "OtherOptionAttribute has wrong unSafe trait", attribute.isUnSafe() );
        //When Unsafe the option noCacheKey flag has no meaning. 
        assertFalse( "OtherOptionAttribute has wrong noCacheKey trait", attribute.isNoCacheKey() );
    }

    @Test
    public void equalsTest()
    {
        int number1= 65023;
        byte[] value1= { 0x68, 0x6f, 0x69 };
        int number2= 65021;
        byte[] value2= { 0x68, 0x6f, 0x70 };

        OtherOptionAttribute attribute1= new OtherOptionAttribute( number1, value1 );
        OtherOptionAttribute attribute2= new OtherOptionAttribute( number2, value2 );
        OtherOptionAttribute attribute3= new OtherOptionAttribute( number1, value1 );
        OtherOptionAttribute attribute4= new OtherOptionAttribute( number1, value2 );
        OtherOptionAttribute attribute5= new OtherOptionAttribute( number2, value1 );

        assertTrue( "attribute 1 equals attribute 1 returns wrong result", attribute1.equals( attribute1 ) );
        assertFalse( "attribute 1 equals null returns wrong result", attribute1.equals( null ) );
        assertFalse( "attribute 1 equals attribute 2 returns wrong result", attribute1.equals( attribute2 ) );
        assertFalse( "attribute 2 equals attribute 1 returns wrong result", attribute2.equals( attribute1 ) );
        assertTrue( "attribute 1 equals attribute 3 returns wrong result", attribute1.equals( attribute3 ) );
        assertTrue( "attribute 3 equals attribute 1 returns wrong result", attribute3.equals( attribute1 ) );
        assertFalse( "attribute 4 equals attribute 1 returns wrong result", attribute4.equals( attribute1 ) );
        assertFalse( "attribute 4 equals attribute 2 returns wrong result", attribute4.equals( attribute2 ) );
        assertFalse( "attribute 5 equals attribute 1 returns wrong result", attribute5.equals( attribute1 ) );
        assertFalse( "attribute 5 equals attribute 2 returns wrong result", attribute5.equals( attribute2 ) );
    }

    @Test
    public void hashCodeTest()
    {
        int number1= 65023;
        byte[] value1= { 0x68, 0x6f, 0x69 };
        int number2= 65021;
        byte[] value2= { 0x68, 0x6f, 0x70 };

        OtherOptionAttribute attribute1= new OtherOptionAttribute( number1, value1 );
        OtherOptionAttribute attribute2= new OtherOptionAttribute( number2, value2 );
        OtherOptionAttribute attribute3= new OtherOptionAttribute( number1, value1 );
        OtherOptionAttribute attribute4= new OtherOptionAttribute( number1, value2 );
        OtherOptionAttribute attribute5= new OtherOptionAttribute( number2, value1 );

        assertEquals( "hashcode flag 1 has wrong value", 4197726, attribute1.hashCode() );
        assertEquals( "hashcode flag 2 has wrong value", 4197941, attribute2.hashCode() );
        assertEquals( "hashcode flag 3 has wrong value", 4197726, attribute3.hashCode() );
        assertEquals( "hashcode flag 4 has wrong value", 4197943, attribute4.hashCode() );
        assertEquals( "hashcode flag 5 has wrong value", 4197724, attribute5.hashCode() );
    }
}
