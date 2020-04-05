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
package nl.teslanet.mule.connectors.coap.test.modules.options;


import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;
import nl.teslanet.mule.connectors.coap.api.options.ETag;


/**
 * Tests the implementation of the Etag class.
 *
 */
public class ETagTest
{

    @Rule
    public ExpectedException exception= ExpectedException.none();

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

        assertTrue( "ETag contruction from String failed", etag1.toString().equals( etagValue1 ) );
        assertTrue( "ETag contruction from Byte[] failed", etag2.toString().equals( etagValue1 ) );
        assertTrue( "ETag contruction from Byte[] failed", etag3.toString().equals( "ff" ) );
        assertTrue( "ETag contruction from Long failed", etag4.toString().equals( "00000000000000ff" ) );
    }

    @Test
    public void testCreate() throws InvalidETagException
    {
        String etagValue1= "afb990";
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue3= "FF";
        Long etagValue4= 255L;

        ETag etag1= ETag.create( etagValue1 );
        ETag etag2= ETag.create( etagValue2 );
        ETag etag3= ETag.create( etagValue3 );
        ETag etag4= ETag.create( etagValue4 );

        assertTrue( "ETag contruction from String failed", etag1.toString().equals( etagValue1 ) );
        assertTrue( "ETag contruction from Byte[] failed", etag2.toString().equals( etagValue1 ) );
        assertTrue( "ETag contruction from Byte[] failed", etag3.toString().equals( "ff" ) );
        assertTrue( "ETag contruction from Long failed", etag4.toString().equals( "00000000000000ff" ) );
    }

    @Test
    public void testConstructorETagNullByteArray() throws InvalidETagException
    {
        byte[] etagValue1= null;
        ETag etag1= new ETag( etagValue1 );
        assertNotNull( etag1 );
        assertEquals( etag1, new ETag( "" ) );
    }

    @Test
    public void testCreateETagNullByteArray() throws InvalidETagException
    {
        byte[] etagValue1= null;
        ETag etag1= ETag.create( etagValue1 );
        assertNotNull( etag1 );
        assertEquals( etag1, new ETag( "" ) );
    }

    @Test
    public void testConstructorETagEmptyByteArray() throws InvalidETagException
    {
        byte[] etagValue1= {};
        ETag etag1= new ETag( etagValue1 );
        assertNotNull( etag1 );
        assertTrue( etag1.equals( new ETag( "" ) ) );
    }

    @Test
    public void testCreateETagEmptyByteArray() throws InvalidETagException
    {
        byte[] etagValue1= {};
        ETag etag1= ETag.create( etagValue1 );;
        assertNotNull( etag1 );
        assertEquals( etag1, new ETag( "" ) );
    }

    @Test
    public void testConstructorETagLargeByteArray() throws InvalidETagException
    {
        byte[] etagValue1= new byte [9];
        for ( int i= 0; i < 9; i++ )
        {
            etagValue1[i]= (byte) i;
        }
        exception.expect( InvalidETagException.class );
        exception.expectMessage( "Given length is: 9" );
        ETag etag1= new ETag( etagValue1 );;
        assertNotNull( etag1 );
    }

    @Test
    public void testConstructorETagEmptyString() throws InvalidETagException
    {
        String etagValue1= "";
        ETag etag1= new ETag( etagValue1 );
        assertNotNull( etag1 );
        assertTrue( etag1.equals( new ETag( "" ) ) );
    }

    @Test
    public void testConstructorUnevenString1() throws InvalidETagException
    {
        String etagValue1= "1";
        exception.expect( InvalidETagException.class );
        exception.expectMessage( "number found: 1" );
        ETag etag1= new ETag( etagValue1 );;
        assertNotNull( etag1 );
    }

    @Test
    public void testConstructorInvalidETagUnevenString2() throws Exception
    {
        String etagValue1= "1122334455667";
        exception.expect( InvalidETagException.class );
        exception.expectMessage( "number found: 13" );
        ETag etag1= new ETag( etagValue1 );;
        assertNotNull( etag1 );
    }

    @Test
    public void testConstructorETagLargeString() throws InvalidETagException
    {
        String etagValue1= "112233445566778899";
        exception.expect( InvalidETagException.class );
        exception.expectMessage( "Given length is: 9" );
        ETag etag1= new ETag( etagValue1 );;
        assertNotNull( etag1 );
    }

    @Test
    public void testAsBytes() throws InvalidETagException
    {
        byte[] etagValue1= { (byte) 0x00 };
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };

        ETag etag1= new ETag( "00" );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );

        assertArrayEquals( "ETag.asBytes gives wrong value", etag1.asBytes(), etagValue1 );
        assertArrayEquals( "ETag.asBytes gives wrong value", etag2.asBytes(), etagValue3 );
        assertArrayEquals( "ETag.asBytes gives wrong value", etag3.asBytes(), etagValue3 );
        assertNotEquals( (Object) etag1.asBytes(), (Object) etagValue1 );
        assertNotEquals( (Object) etag3.asBytes(), (Object) etagValue3 );
    }

    @Test
    public void testToString() throws InvalidETagException
    {
        byte[] etagValue1= { (byte) 0x00 };
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };

        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );

        assertTrue( "ETag.toString gives wrong value", etag1.toString().equals( "00" ) );
        assertTrue( "ETag.toString gives wrong value", etag2.toString().equals( etagValue2 ) );
        assertTrue( "ETag.toString gives wrong value", etag3.toString().equals( etagValue2 ) );
    }

    @Test
    public void testEquals() throws InvalidETagException
    {
        String etagValue1= "ffb990";
        byte[] etagValue2= { (byte) 0xFF, (byte) 0xB9, (byte) 0x90 };
        String etagValue3= "afb991";
        String etagValue4= "afb99100112233";
        String etagValue5= "00";

        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );
        ETag etag5= new ETag( etagValue5 );

        assertTrue( "ETag.equals failed to compare to equal etag", etag1.equals( etag1 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag2.equals( etag2 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag3.equals( etag3 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag4.equals( etag4 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag5.equals( etag5 ) );

        assertTrue( "ETag.equals failed to compare to equal etag", etag1.equals( new ETag( etagValue1 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag2.equals( new ETag( etagValue2 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag3.equals( new ETag( etagValue3 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag4.equals( new ETag( etagValue4 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag5.equals( new ETag( etagValue5 ) ) );

        assertTrue( "ETag.equals failed to compare to equal etag", etag1.equals( etag2 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag2.equals( etag1 ) );

        assertFalse( "ETag.equals failed to compare to unequal etag", etag1.equals( etag3 ) );
        assertFalse( "ETag.equals failed to compare to unequal etag", etag3.equals( etag1 ) );
        assertFalse( "ETag.equals failed to compare to unequal etag", etag1.equals( etag4 ) );
        assertFalse( "ETag.equals failed to compare to unequal etag", etag4.equals( etag1 ) );
        assertFalse( "ETag.equals failed to compare to unequal etag", etag5.equals( etag2 ) );

        assertFalse( "ETag.equals failed to compare to null", etag5.equals( null ) );

    }

    @Test
    public void testEqualsLong() throws InvalidETagException
    {
        Long etagValue1= 255L;
        String etagValue2= "00000000000000FF";
        Long etagValue3= -45677L;
        Long etagValue4= null;

        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );

        assertTrue( "ETag.equals failed to compare to equal etag", etag1.equals( etag1 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag2.equals( etag2 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag3.equals( etag3 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag4.equals( etag4 ) );

        assertTrue( "ETag.equals failed to compare to equal etag", etag1.equals( new ETag( etagValue1 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag2.equals( new ETag( etagValue2 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag3.equals( new ETag( etagValue3 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag4.equals( new ETag( etagValue4 ) ) );

        assertTrue( "ETag.equals failed to compare to equal etag", etag1.equals( etag2 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag2.equals( etag1 ) );

        assertFalse( "ETag.equals failed to compare to unequal etag", etag1.equals( etag3 ) );
        assertFalse( "ETag.equals failed to compare to unequal etag", etag2.equals( etag3 ) );
        assertFalse( "ETag.equals failed to compare to unequal etag", etag1.equals( etag4 ) );
        assertFalse( "ETag.equals failed to compare to unequal etag", etag2.equals( etag4 ) );

        assertFalse( "ETag.equals failed to compare to null", etag1.equals( null ) );
        assertFalse( "ETag.equals failed to compare to null", etag1.equals( new ETag( etagValue4 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag4.equals( null ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag4.equals( new ETag( etagValue4 ) ) );
    }

    @SuppressWarnings("unlikely-arg-type")
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

        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );
        ETag etag5= new ETag( etagValue5 );

        assertEquals( "ETag.compareTo failed to compare to equal etag", etag1.compareTo( etag1 ), 0 );
        assertEquals( "ETag.compareTo failed to compare to equal etag", etag2.compareTo( etag2 ), 0 );
        assertEquals( "ETag.compareTo failed to compare to equal etag", etag3.compareTo( etag3 ), 0 );
        assertEquals( "ETag.compareTo failed to compare to equal etag", etag4.compareTo( etag4 ), 0 );
        assertEquals( "ETag.compareTo failed to compare to equal etag", etag5.compareTo( etag5 ), 0 );

        assertEquals( "ETag.compareTo failed to compare to equal etag", etag1.compareTo( etag5 ), -1 );
        assertEquals( "ETag.compareTo failed to compare to equal etag", etag2.compareTo( etag1 ), 1 );
        assertEquals( "ETag.compareTo failed to compare to equal etag", etag3.compareTo( etag2 ), 0 );
        assertEquals( "ETag.compareTo failed to compare to equal etag", etag4.compareTo( etag3 ), 1 );
        assertEquals( "ETag.compareTo failed to compare to equal etag", etag5.compareTo( etag4 ), 1 );

        assertEquals( "ETag.compareTo failed to compare to equal etag", etag1.compareTo( etag2 ), -1 );
        assertEquals( "ETag.compareTo failed to compare to equal etag", etag2.compareTo( etag3 ), 0 );
        assertEquals( "ETag.compareTo failed to compare to equal etag", etag3.compareTo( etag4 ), -1 );
        assertEquals( "ETag.compareTo failed to compare to equal etag", etag4.compareTo( etag5 ), -1 );
        assertEquals( "ETag.compareTo failed to compare to equal etag", etag5.compareTo( etag1 ), 1 );

        assertEquals( "ETag.compareTo failed to compare to null", etag5.compareTo( null ), 1 );

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

        assertEquals( "ETag.compareTo failed to compare to null", 0, etag4.compareTo( null ) );
    }

    @Test
    public void testHashCode() throws InvalidETagException
    {
        String etagValue1= "00";
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue4= "afb991";
        String etagValue5= "afb99100112233";

        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );
        ETag etag5= new ETag( etagValue5 );

        assertEquals( "ETag.hashCode failed to compare to equal etag", etag1.hashCode(), new ETag( etagValue1 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag2.hashCode(), new ETag( etagValue2 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag3.hashCode(), new ETag( etagValue3 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag4.hashCode(), new ETag( etagValue4 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag5.hashCode(), new ETag( etagValue5 ).hashCode() );

        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag1.hashCode(), new ETag( etagValue5 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag2.hashCode(), new ETag( etagValue1 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag3.hashCode(), new ETag( etagValue2 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag4.hashCode(), new ETag( etagValue3 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag5.hashCode(), new ETag( etagValue4 ).hashCode() );

        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag1.hashCode(), new ETag( etagValue2 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag2.hashCode(), new ETag( etagValue3 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag3.hashCode(), new ETag( etagValue4 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag4.hashCode(), new ETag( etagValue5 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag5.hashCode(), new ETag( etagValue1 ).hashCode() );

    }

    @Test
    public void testHashCodeLong() throws InvalidETagException
    {
        String etagValue1= "00";
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue4= "afb991";
        String etagValue5= "afb99100112233";

        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );
        ETag etag5= new ETag( etagValue5 );

        assertEquals( "ETag.hashCode failed to compare to equal etag", etag1.hashCode(), new ETag( etagValue1 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag2.hashCode(), new ETag( etagValue2 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag3.hashCode(), new ETag( etagValue3 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag4.hashCode(), new ETag( etagValue4 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag5.hashCode(), new ETag( etagValue5 ).hashCode() );

        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag1.hashCode(), new ETag( etagValue5 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag2.hashCode(), new ETag( etagValue1 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag3.hashCode(), new ETag( etagValue2 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag4.hashCode(), new ETag( etagValue3 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag5.hashCode(), new ETag( etagValue4 ).hashCode() );

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

        assertEquals( "ETag.toHexString gives wrong value", ETag.toHexString( etagValue1 ), ( hexValue1 ) );
        assertEquals( "ETag.toHexString gives wrong value", ETag.toHexString( etagValue2 ), ( hexValue2 ) );
        assertEquals( "ETag.toHexString gives wrong value", ETag.toHexString( etagValue3 ), ( hexValue3 ) );
    }

    @Test
    public void testIsIn() throws InvalidETagException
    {
        String etagValue1= "00";
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue4= "afb991";
        String etagValue5= "afb99100112233";

        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );
        ETag etag5= new ETag( etagValue5 );

        LinkedList< ETag > list= new LinkedList< ETag >();
        LinkedList< ETag > listofone= new LinkedList< ETag >();
        LinkedList< ETag > emptylist= new LinkedList< ETag >();

        list.add( etag1 );
        list.add( etag2 );
        list.add( etag3 );
        list.add( etag4 );

        listofone.add( etag4 );

        assertTrue( "ETag.isIn gives wrong value", etag1.isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", etag2.isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", etag3.isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", etag4.isIn( list ) );
        assertFalse( "ETag.isIn gives wrong value", etag5.isIn( list ) );

        assertTrue( "ETag.isIn gives wrong value", new ETag( etagValue1 ).isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", new ETag( etagValue2 ).isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", new ETag( etagValue3 ).isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", new ETag( etagValue4 ).isIn( list ) );
        assertFalse( "ETag.isIn gives wrong value", new ETag( etagValue5 ).isIn( list ) );

        assertTrue( "ETag.isIn gives wrong value", new ETag( etagValue4 ).isIn( listofone ) );
        assertFalse( "ETag.isIn gives wrong value", new ETag( etagValue5 ).isIn( listofone ) );

        assertFalse( "ETag.isIn gives wrong value", new ETag( etagValue4 ).isIn( emptylist ) );
        assertFalse( "ETag.isIn gives wrong value", new ETag( etagValue5 ).isIn( emptylist ) );
    }

    @Test
    public void testGetList() throws InvalidETagException
    {
        byte[] etagValue1= { (byte) 0x00, };
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] etagValue4= { (byte) 0xAF, (byte) 0xB9, (byte) 0x91 };
        byte[] etagValue5= { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x88 };

        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );
        ETag etag5= new ETag( etagValue5 );

        LinkedList< byte[] > byteslist= new LinkedList< byte[] >();
        LinkedList< byte[] > byteslistofone= new LinkedList< byte[] >();
        LinkedList< byte[] > bytesemptylist= new LinkedList< byte[] >();

        byteslist.add( etagValue1 );
        byteslist.add( etagValue2 );
        byteslist.add( etagValue3 );
        byteslist.add( etagValue4 );

        byteslistofone.add( etagValue4 );

        List< ETag > list= ETag.getList( byteslist );
        List< ETag > listofone= ETag.getList( byteslistofone );
        List< ETag > emptylist= ETag.getList( bytesemptylist );

        assertTrue( "ETag.getList doesn't contain etag", etag1.isIn( list ) );
        assertTrue( "ETag.getList doesn't contain etag", etag2.isIn( list ) );
        assertTrue( "ETag.getList doesn't contain etag", etag3.isIn( list ) );
        assertTrue( "ETag.getList doesn't contain etag", etag4.isIn( list ) );
        assertFalse( "ETag.getList does contain etag", etag5.isIn( list ) );

        assertTrue( "ETag.getList doesn't contain etag", list.contains( etag1 ) );
        assertTrue( "ETag.getList doesn't contain etag", list.contains( etag2 ) );
        assertTrue( "ETag.getList doesn't contain etag", list.contains( etag3 ) );
        assertTrue( "ETag.getList doesn't contain etag", list.contains( etag4 ) );
        assertFalse( "ETag.getList does contain etag", list.contains( etag5 ) );

        assertTrue( "ETag.getList doesn't contain etag", new ETag( etagValue1 ).isIn( list ) );
        assertTrue( "ETag.getList doesn't contain etag", new ETag( etagValue2 ).isIn( list ) );
        assertTrue( "ETag.getList doesn't contain etag", new ETag( etagValue3 ).isIn( list ) );
        assertTrue( "ETag.getList doesn't contain etag", new ETag( etagValue4 ).isIn( list ) );
        assertFalse( "ETag.getList does contain etag", new ETag( etagValue5 ).isIn( list ) );

        assertTrue( "ETag.getList doesn't contain etag", new ETag( etagValue4 ).isIn( listofone ) );
        assertFalse( "ETag.getList does contain etag", new ETag( etagValue5 ).isIn( listofone ) );

        assertFalse( "ETag.getList does contain etag", new ETag( etagValue4 ).isIn( emptylist ) );
        assertFalse( "ETag.getList does contain etag", new ETag( etagValue5 ).isIn( emptylist ) );
    }

}
