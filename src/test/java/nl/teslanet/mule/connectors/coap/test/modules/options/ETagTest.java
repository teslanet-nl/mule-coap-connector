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
package nl.teslanet.mule.connectors.coap.test.modules.options;


import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;
import nl.teslanet.mule.connectors.coap.api.options.ETag;


/**
 * Tests the implementation of the Etag class.
 *
 */
public class ETagTest
{
    @Test
    public void testConstructorNullValue() throws InvalidETagException
    {
        String etagValue1= null;
        byte[] etagValue2= null;
        String etagValue3= null;
        Long etagValue4= null;
        byte[] etagValue5= {};
        String etagValue6= "";

        ETag etag0= new ETag();
        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );
        ETag etag5= new ETag( etagValue5 );
        ETag etag6= new ETag( etagValue6 );

        assertEquals( "ETag contruction from null String failed", "", etag0.getHexString() );
        assertEquals( "ETag contruction from null String failed", "", etag1.getHexString() );
        assertEquals( "ETag contruction from null Byte[] failed", "", etag2.getHexString() );
        assertEquals( "ETag contruction from null Byte[] failed", "", etag3.getHexString() );
        assertEquals( "ETag contruction from null Long failed", "", etag4.getHexString() );
        assertEquals( "ETag contruction from empty Byte[] failed", "", etag5.getHexString() );
        assertEquals( "ETag contruction from empty String failed", "", etag6.getHexString() );
    }

    @Test
    public void testConstructor() throws InvalidETagException
    {
        String etagValue1= "afb990";
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue3= "FF";
        Long etagValue4= 255L;

        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );

        assertTrue( "ETag contruction from String failed", etag1.getHexString().equals( etagValue1 ) );
        assertTrue( "ETag contruction from Byte[] failed", etag2.getHexString().equals( etagValue1 ) );
        assertTrue( "ETag contruction from Byte[] failed", etag3.getHexString().equals( "ff" ) );
        assertTrue( "ETag contruction from Long failed", etag4.getHexString().equals( "00000000000000ff" ) );
    }

    @Test
    public void testCreateNullValue() throws InvalidETagException
    {
        String etagValue1= null;
        byte[] etagValue2= null;
        String etagValue3= null;
        Long etagValue4= null;
        byte[] etagValue5= {};
        String etagValue6= "";

        ETag etag1= ETag.valueOf( etagValue1 );
        ETag etag2= ETag.valueOf( etagValue2 );
        ETag etag3= ETag.valueOf( etagValue3 );
        ETag etag4= ETag.valueOf( etagValue4 );
        ETag etag5= ETag.valueOf( etagValue5 );
        ETag etag6= ETag.valueOf( etagValue6 );

        assertEquals( "ETag contruction from null failed", "", etag1.getHexString() );
        assertEquals( "ETag contruction from String failed", "", etag1.getHexString() );
        assertEquals( "ETag contruction from Byte[] failed", "", etag2.getHexString() );
        assertEquals( "ETag contruction from Byte[] failed", "", etag3.getHexString() );
        assertEquals( "ETag contruction from Long failed", "", etag4.getHexString() );
        assertEquals( "ETag contruction from empty Byte[] failed", "", etag5.getHexString() );
        assertEquals( "ETag contruction from empty String failed", "", etag6.getHexString() );
    }

    @Test
    public void testCreate() throws InvalidETagException
    {
        String etagValue1= "afb990";
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue3= "FF";
        Long etagValue4= 255L;

        ETag etag1= ETag.valueOf( etagValue1 );
        ETag etag2= ETag.valueOf( etagValue2 );
        ETag etag3= ETag.valueOf( etagValue3 );
        ETag etag4= ETag.valueOf( etagValue4 );

        assertTrue( "ETag contruction from null failed", etag1.getHexString().equals( etagValue1 ) );
        assertTrue( "ETag contruction from String failed", etag1.getHexString().equals( etagValue1 ) );
        assertTrue( "ETag contruction from Byte[] failed", etag2.getHexString().equals( etagValue1 ) );
        assertTrue( "ETag contruction from Byte[] failed", etag3.getHexString().equals( "ff" ) );
        assertTrue( "ETag contruction from Long failed", etag4.getHexString().equals( "00000000000000ff" ) );
    }

    @Test
    public void testNotEmpty() throws InvalidETagException
    {
        String etagValue1= "afb990";
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue3= "FF";
        Long etagValue4= 255L;

        ETag etag1= ETag.valueOf( etagValue1 );
        ETag etag2= ETag.valueOf( etagValue2 );
        ETag etag3= ETag.valueOf( etagValue3 );
        ETag etag4= ETag.valueOf( etagValue4 );

        assertFalse( "ETag should evaluate not empty", etag1.isEmpty() );
        assertFalse( "ETag should evaluate not empty", etag2.isEmpty() );
        assertFalse( "ETag should evaluate not empty", etag3.isEmpty() );
        assertFalse( "ETag should evaluate not empty", etag4.isEmpty() );
    }

    @Test
    public void testEmpty() throws InvalidETagException
    {
        String etagValue1= "";
        byte[] etagValue2= {};
        String etagValue3= "";
        Long etagValue4= null;

        ETag etag1= ETag.valueOf( etagValue1 );
        ETag etag2= ETag.valueOf( etagValue2 );
        ETag etag3= ETag.valueOf( etagValue3 );
        ETag etag4= ETag.valueOf( etagValue4 );

        assertTrue( "ETag should evaluate not empty", etag1.isEmpty() );
        assertTrue( "ETag should evaluate not empty", etag2.isEmpty() );
        assertTrue( "ETag should evaluate not empty", etag3.isEmpty() );
        assertTrue( "ETag should evaluate not empty", etag4.isEmpty() );
    }

    @Test
    public void testConstructorETagLargeByteArray() throws InvalidETagException
    {
        byte[] etagValue1= new byte [9];
        for ( int i= 0; i < 9; i++ )
        {
            etagValue1[i]= (byte) i;
        }
        InvalidETagException e= assertThrows( InvalidETagException.class, () -> {
            @SuppressWarnings( "unused" )
            ETag etag1= new ETag( etagValue1 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Given length is: 9" ) );
    }

    @Test
    public void testConstructorInvalidString() throws InvalidETagException
    {
        String etagValue1= "10aaZZ";
        InvalidETagException e= assertThrows( InvalidETagException.class, () -> {
            @SuppressWarnings( "unused" )
            ETag etag1= new ETag( etagValue1 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "ETag value cannot be parsed as hexadecimal: " + etagValue1 ) );
    }

    @Test
    public void testConstructorUnevenString1() throws InvalidETagException
    {
        String etagValue1= "1";
        InvalidETagException e= assertThrows( InvalidETagException.class, () -> {
            @SuppressWarnings( "unused" )
            ETag etag1= new ETag( etagValue1 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "number found: 1" ) );
    }

    @Test
    public void testConstructorInvalidETagUnevenString2() throws Exception
    {
        String etagValue1= "1122334455667";
        InvalidETagException e= assertThrows( InvalidETagException.class, () -> {
            @SuppressWarnings( "unused" )
            ETag etag1= new ETag( etagValue1 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "number found: 13" ) );
    }

    @Test
    public void testConstructorETagLargeString() throws InvalidETagException
    {
        String etagValue1= "112233445566778899";
        InvalidETagException e= assertThrows( InvalidETagException.class, () -> {
            @SuppressWarnings( "unused" )
            ETag etag1= new ETag( etagValue1 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Given length is: 9" ) );
    }

    @Test
    public void testAsBytes() throws InvalidETagException
    {
        byte[] etagValue1= { (byte) 0x00 };
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };

        ETag etag0= new ETag();
        ETag etag1= new ETag( "00" );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );

        assertArrayEquals( "ETag.asBytes gives wrong value", new byte [0], etag0.getBytes() );
        assertArrayEquals( "ETag.asBytes gives wrong value", etagValue1, etag1.getBytes() );
        assertArrayEquals( "ETag.asBytes gives wrong value", etagValue3, etag2.getBytes() );
        assertArrayEquals( "ETag.asBytes gives wrong value", etagValue3, etag3.getBytes() );
        assertNotEquals( (Object) etagValue1, (Object) etag1.getBytes() );
        assertNotEquals( (Object) etagValue3, (Object) etag3.getBytes() );
    }

    @Test
    public void testToString() throws InvalidETagException
    {
        byte[] etagValue1= { (byte) 0x00 };
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };

        ETag etag0= new ETag();
        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );

        assertTrue( "ETag.toString gives wrong value", etag0.getHexString().equals( "" ) );
        assertTrue( "ETag.toString gives wrong value", etag1.getHexString().equals( "00" ) );
        assertTrue( "ETag.toString gives wrong value", etag2.getHexString().equals( etagValue2 ) );
        assertTrue( "ETag.toString gives wrong value", etag3.getHexString().equals( etagValue2 ) );
    }

    @Test
    public void testEquals() throws InvalidETagException
    {
        String etagValue1= "ffb990";
        byte[] etagValue2= { (byte) 0xFF, (byte) 0xB9, (byte) 0x90 };
        String etagValue3= "afb991";
        String etagValue4= "afb99100112233";
        String etagValue5= "00";

        ETag etag0= new ETag();
        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );
        ETag etag5= new ETag( etagValue5 );

        assertTrue( "ETag.equals failed to compare to equal etag", etag0.equals( etag0 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag1.equals( etag1 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag2.equals( etag2 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag3.equals( etag3 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag4.equals( etag4 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag5.equals( etag5 ) );

        assertTrue( "ETag.equals failed to compare to equal etag", etag0.equals( new ETag() ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag1.equals( new ETag( etagValue1 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag2.equals( new ETag( etagValue2 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag3.equals( new ETag( etagValue3 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag4.equals( new ETag( etagValue4 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag5.equals( new ETag( etagValue5 ) ) );

        assertTrue( "ETag.equals failed to compare to equal etag", etag1.equals( etag2 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag2.equals( etag1 ) );

        assertFalse( "ETag.equals failed to compare to unequal etag", etag0.equals( etag1 ) );
        assertFalse( "ETag.equals failed to compare to unequal etag", etag1.equals( etag3 ) );
        assertFalse( "ETag.equals failed to compare to unequal etag", etag3.equals( etag1 ) );
        assertFalse( "ETag.equals failed to compare to unequal etag", etag1.equals( etag4 ) );
        assertFalse( "ETag.equals failed to compare to unequal etag", etag4.equals( etag1 ) );
        assertFalse( "ETag.equals failed to compare to unequal etag", etag5.equals( etag2 ) );

        assertFalse( "ETag.equals failed to compare to null", etag0.equals( null ) );
        assertFalse( "ETag.equals failed to compare to null", etag5.equals( null ) );

    }

    @Test
    public void testEqualsLong() throws InvalidETagException
    {
        Long etagValue0= null;
        Long etagValue1= 255L;
        String etagValue2= "00000000000000FF";
        Long etagValue3= -45677L;
        Long etagValue4= null;

        ETag etag0= new ETag( etagValue0 );
        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );

        assertTrue( "ETag.equals failed to compare to equal etag", etag0.equals( etag0 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag1.equals( etag1 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag2.equals( etag2 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag3.equals( etag3 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag4.equals( etag4 ) );

        assertTrue( "ETag.equals failed to compare to equal etag", etag0.equals( new ETag( etagValue0 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag1.equals( new ETag( etagValue1 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag2.equals( new ETag( etagValue2 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag3.equals( new ETag( etagValue3 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag4.equals( new ETag( etagValue4 ) ) );

        assertTrue( "ETag.equals failed to compare to equal etag", etag0.equals( new ETag() ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag1.equals( etag2 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag2.equals( etag1 ) );

        assertFalse( "ETag.equals failed to compare to unequal etag", etag0.equals( etag1 ) );
        assertFalse( "ETag.equals failed to compare to unequal etag", etag1.equals( etag3 ) );
        assertFalse( "ETag.equals failed to compare to unequal etag", etag2.equals( etag3 ) );
        assertFalse( "ETag.equals failed to compare to unequal etag", etag1.equals( etag4 ) );
        assertFalse( "ETag.equals failed to compare to unequal etag", etag2.equals( etag4 ) );

        assertFalse( "ETag.equals failed to compare to null", etag0.equals( null ) );
        assertFalse( "ETag.equals failed to compare to null", etag1.equals( null ) );
        assertFalse( "ETag.equals failed to compare to null", etag1.equals( new ETag( etagValue4 ) ) );
        assertFalse( "ETag.equals failed to compare to equal etag", etag4.equals( null ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag4.equals( new ETag( etagValue4 ) ) );
    }

    @SuppressWarnings( "unlikely-arg-type" )
    @Test
    public void testEqualsToWrongClass() throws InvalidETagException
    {
        String etagValue1= "1122334455667788";
        ETag etag1= new ETag( etagValue1 );;
        assertFalse( "ETag.equals Boolean returned true", etag1.equals( new Boolean( false ) ) );
    }

    @Test
    public void testCompareTo() throws InvalidETagException
    {
        String etagValue1= "00";
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue4= "afb991";
        String etagValue5= "afb99100112233";

        ETag etag0= new ETag();
        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );
        ETag etag5= new ETag( etagValue5 );

        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag0.compareTo( etag0 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag1.compareTo( etag1 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag2.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag3.compareTo( etag3 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag4.compareTo( etag4 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag5.compareTo( etag5 ) );

        assertEquals( "ETag.compareTo failed to compare", -1, etag0.compareTo( etag5 ) );
        assertEquals( "ETag.compareTo failed to compare", 1, etag1.compareTo( etag0 ) );
        assertEquals( "ETag.compareTo failed to compare", 1, etag2.compareTo( etag1 ) );
        assertEquals( "ETag.compareTo failed to compare", 0, etag3.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare", 1, etag4.compareTo( etag3 ) );
        assertEquals( "ETag.compareTo failed to compare", 1, etag5.compareTo( etag4 ) );

        assertEquals( "ETag.compareTo failed to compare", -1, etag0.compareTo( etag1 ) );
        assertEquals( "ETag.compareTo failed to compare", -1, etag1.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare", 0, etag2.compareTo( etag3 ) );
        assertEquals( "ETag.compareTo failed to compare", -1, etag3.compareTo( etag4 ) );
        assertEquals( "ETag.compareTo failed to compare", -1, etag4.compareTo( etag5 ) );
        assertEquals( "ETag.compareTo failed to compare", 1, etag5.compareTo( etag0 ) );

        assertEquals( "ETag.compareTo failed to compare to null", 1, etag0.compareTo( null ) );
        assertEquals( "ETag.compareTo failed to compare to null", 1, etag5.compareTo( null ) );
    }

    @Test
    public void testCompareToLong() throws InvalidETagException
    {
        Long etagValue1= 255L;
        String etagValue2= "00000000000000FF";
        Long etagValue3= -45677L;
        Long etagValue4= null;

        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );

        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag1.compareTo( etag1 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag2.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag3.compareTo( etag3 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag4.compareTo( etag4 ) );

        assertEquals( "ETag.compareTo failed to compare to equal etag", 1, etag1.compareTo( etag4 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag2.compareTo( etag1 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", -1, etag3.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", -1, etag4.compareTo( etag3 ) );

        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag1.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 1, etag2.compareTo( etag3 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 1, etag3.compareTo( etag4 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", -1, etag4.compareTo( etag1 ) );

        assertEquals( "ETag.compareTo failed to compare to null", 1, etag4.compareTo( null ) );
    }

    @Test
    public void testHashCode() throws InvalidETagException
    {
        String etagValue0= null;
        String etagValue1= "00";
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue4= "afb991";
        String etagValue5= "afb99100112233";

        ETag etag0= new ETag();
        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );
        ETag etag5= new ETag( etagValue5 );

        assertEquals( "ETag.hashCode failed to compare to equal etag", etag0.hashCode(), new ETag( etagValue0 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag1.hashCode(), new ETag( etagValue1 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag2.hashCode(), new ETag( etagValue2 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag3.hashCode(), new ETag( etagValue3 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag4.hashCode(), new ETag( etagValue4 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag5.hashCode(), new ETag( etagValue5 ).hashCode() );

        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag0.hashCode(), new ETag( etagValue4 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag1.hashCode(), new ETag( etagValue5 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag2.hashCode(), new ETag( etagValue1 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag3.hashCode(), new ETag( etagValue2 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag4.hashCode(), new ETag( etagValue3 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag5.hashCode(), new ETag( etagValue4 ).hashCode() );

        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag0.hashCode(), new ETag( etagValue3 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag1.hashCode(), new ETag( etagValue2 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag2.hashCode(), new ETag( etagValue3 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag3.hashCode(), new ETag( etagValue4 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag4.hashCode(), new ETag( etagValue5 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag5.hashCode(), new ETag( etagValue1 ).hashCode() );

    }

    @Test
    public void testHashCodeLong() throws InvalidETagException
    {
        String etagValue0= null;
        String etagValue1= "00";
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue4= "afb991";
        String etagValue5= "afb99100112233";

        ETag etag0= new ETag( etagValue0 );
        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );
        ETag etag5= new ETag( etagValue5 );

        assertEquals( "ETag.hashCode failed to compare to equal etag", etag0.hashCode(), new ETag( etagValue0 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag1.hashCode(), new ETag( etagValue1 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag2.hashCode(), new ETag( etagValue2 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag3.hashCode(), new ETag( etagValue3 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag4.hashCode(), new ETag( etagValue4 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag5.hashCode(), new ETag( etagValue5 ).hashCode() );

        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag0.hashCode(), new ETag( etagValue2 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag1.hashCode(), new ETag( etagValue5 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag2.hashCode(), new ETag( etagValue1 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag3.hashCode(), new ETag( etagValue2 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag4.hashCode(), new ETag( etagValue3 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag5.hashCode(), new ETag( etagValue4 ).hashCode() );

        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag1.hashCode(), new ETag( etagValue3 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag1.hashCode(), new ETag( etagValue2 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag2.hashCode(), new ETag( etagValue3 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag3.hashCode(), new ETag( etagValue4 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag4.hashCode(), new ETag( etagValue5 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag5.hashCode(), new ETag( etagValue1 ).hashCode() );

    }

    @Test
    public void testToHexString()
    {
        byte[] etagValue1= { (byte) 0x00 };
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] etagValue3= { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x88 };
        String hexValue1= "00";
        String hexValue2= "afb990";
        String hexValue3= "1122334455667788";

        assertEquals( "ETag.toHexString gives wrong value", "", ETag.toHexString( null ) );
        assertEquals( "ETag.toHexString gives wrong value", hexValue1, ETag.toHexString( etagValue1 ) );
        assertEquals( "ETag.toHexString gives wrong value", hexValue2, ETag.toHexString( etagValue2 ) );
        assertEquals( "ETag.toHexString gives wrong value", hexValue3, ETag.toHexString( etagValue3 ) );
    }

    @Test
    public void testIsIn() throws InvalidETagException
    {
        String etagValue1= "00";
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue4= "afb991";
        String etagValue5= "afb99100112233";

        ETag etag0= new ETag();
        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );
        ETag etag5= new ETag( etagValue5 );

        LinkedList< ETag > list= new LinkedList< ETag >();
        LinkedList< ETag > listofone= new LinkedList< ETag >();
        LinkedList< ETag > emptylist= new LinkedList< ETag >();

        list.add( etag0 );
        list.add( etag1 );
        list.add( etag2 );
        list.add( etag3 );
        list.add( etag4 );

        listofone.add( etag4 );

        assertTrue( "ETag.isIn gives wrong value", etag0.isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", etag1.isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", etag2.isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", etag3.isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", etag4.isIn( list ) );
        assertFalse( "ETag.isIn gives wrong value", etag5.isIn( list ) );

        assertTrue( "ETag.isIn gives wrong value", new ETag().isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", new ETag( etagValue1 ).isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", new ETag( etagValue2 ).isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", new ETag( etagValue3 ).isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", new ETag( etagValue4 ).isIn( list ) );
        assertFalse( "ETag.isIn gives wrong value", new ETag( etagValue5 ).isIn( list ) );

        assertTrue( "ETag.isIn gives wrong value", new ETag( etagValue4 ).isIn( listofone ) );
        assertFalse( "ETag.isIn gives wrong value", new ETag( etagValue5 ).isIn( listofone ) );
        assertFalse( "ETag.isIn gives wrong value", new ETag().isIn( listofone ) );

        assertFalse( "ETag.isIn gives wrong value", new ETag( etagValue4 ).isIn( emptylist ) );
        assertFalse( "ETag.isIn gives wrong value", new ETag( etagValue5 ).isIn( emptylist ) );
        assertFalse( "ETag.isIn gives wrong value", new ETag().isIn( emptylist ) );
    }

    @Test
    public void testGetList() throws InvalidETagException
    {
        byte[] etagValue1= { (byte) 0x00, };
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] etagValue4= { (byte) 0xAF, (byte) 0xB9, (byte) 0x91 };
        byte[] etagValue5= { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x88 };

        ETag etag0= new ETag();
        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );
        ETag etag5= new ETag( etagValue5 );

        LinkedList< byte[] > byteslist= new LinkedList< byte[] >();
        LinkedList< byte[] > byteslistofone= new LinkedList< byte[] >();
        LinkedList< byte[] > bytesemptylist= new LinkedList< byte[] >();

        byteslist.add( null );
        byteslist.add( etagValue1 );
        byteslist.add( etagValue2 );
        byteslist.add( etagValue3 );
        byteslist.add( etagValue4 );

        byteslistofone.add( etagValue4 );

        List< ETag > list= ETag.getList( byteslist );
        List< ETag > listofone= ETag.getList( byteslistofone );
        List< ETag > emptylist= ETag.getList( bytesemptylist );

        assertTrue( "ETag.getList doesn't contain etag", etag0.isIn( list ) );
        assertTrue( "ETag.getList doesn't contain etag", etag1.isIn( list ) );
        assertTrue( "ETag.getList doesn't contain etag", etag2.isIn( list ) );
        assertTrue( "ETag.getList doesn't contain etag", etag3.isIn( list ) );
        assertTrue( "ETag.getList doesn't contain etag", etag4.isIn( list ) );
        assertFalse( "ETag.getList does contain etag", etag5.isIn( list ) );

        assertTrue( "ETag.getList doesn't contain etag", list.contains( etag0 ) );
        assertTrue( "ETag.getList doesn't contain etag", list.contains( etag1 ) );
        assertTrue( "ETag.getList doesn't contain etag", list.contains( etag2 ) );
        assertTrue( "ETag.getList doesn't contain etag", list.contains( etag3 ) );
        assertTrue( "ETag.getList doesn't contain etag", list.contains( etag4 ) );
        assertFalse( "ETag.getList does contain etag", list.contains( etag5 ) );

        assertTrue( "ETag.getList doesn't contain etag", new ETag().isIn( list ) );
        assertTrue( "ETag.getList doesn't contain etag", new ETag( etagValue1 ).isIn( list ) );
        assertTrue( "ETag.getList doesn't contain etag", new ETag( etagValue2 ).isIn( list ) );
        assertTrue( "ETag.getList doesn't contain etag", new ETag( etagValue3 ).isIn( list ) );
        assertTrue( "ETag.getList doesn't contain etag", new ETag( etagValue4 ).isIn( list ) );
        assertFalse( "ETag.getList does contain etag", new ETag( etagValue5 ).isIn( list ) );

        assertTrue( "ETag.getList doesn't contain etag", new ETag( etagValue4 ).isIn( listofone ) );
        assertFalse( "ETag.getList does contain etag", new ETag( etagValue5 ).isIn( listofone ) );
        assertFalse( "ETag.getList does contain etag", new ETag().isIn( listofone ) );

        assertFalse( "ETag.getList does contain etag", new ETag( etagValue4 ).isIn( emptylist ) );
        assertFalse( "ETag.getList does contain etag", new ETag( etagValue5 ).isIn( emptylist ) );
        assertFalse( "ETag.getList does contain etag", new ETag().isIn( emptylist ) );
    }
}
