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
package nl.teslanet.mule.connectors.coap.test.entitytag;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultEntityTag;
import nl.teslanet.mule.connectors.coap.internal.utils.MessageUtils;


/**
 * Tests the implementation of the Etag class.
 *
 */
public class ETagTest
{
    @Test
    public void testConstructorNullAndMinusValue() throws OptionValueException
    {
        String etagValue1= null;
        byte[] etagValue2= null;
        String etagValue3= null;
        byte[] etagValue4= {};
        String etagValue5= "";
        long etagValue6= 0L;
        int etagValue7= 0;

        OptionValueException e;
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag1= new DefaultEntityTag( etagValue1 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Given string is null or empty." ) );
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag1= new DefaultEntityTag( etagValue1 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Given string is null or empty." ) );
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag2= new DefaultEntityTag( etagValue2 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Cannot construct Entity-tag value" ) );
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag3= new DefaultEntityTag( etagValue3 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Given string is null or empty." ) );
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag4= new DefaultEntityTag( etagValue4 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Cannot construct Entity-tag value" ) );
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag5= new DefaultEntityTag( etagValue5 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Given string is null or empty." ) );
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag6= new DefaultEntityTag( etagValue6 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Cannot construct Entity-tag value" ) );
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag8= new DefaultEntityTag( etagValue7 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Cannot construct Entity-tag value" ) );
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag10= new DefaultEntityTag( etagValue1, 16 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Cannot construct Entity-tag value" ) );
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag11= new DefaultEntityTag( etagValue5, 16 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Cannot construct Entity-tag value" ) );
    }

    @Test
    public void testConstructor() throws OptionValueException
    {
        String etagValue1= "h\u20ACy";
        String etagValue2= "68e282ac79";
        byte[] etagValue3= { (byte) 0x68, (byte) 0xe2, (byte) 0x82, (byte) 0xac, (byte) 0x79 };
        Long etagValue4= 0x68e282ac79L;
        Integer etagValue5= 0xe282ac79;
        byte[] etagValue6= { (byte) 0xe2, (byte) 0x82, (byte) 0xac, (byte) 0x79 };

        DefaultEntityTag etag1= new DefaultEntityTag( etagValue1 );
        DefaultEntityTag etag2= new DefaultEntityTag( etagValue2, 16 );
        DefaultEntityTag etag3= new DefaultEntityTag( etagValue3 );
        DefaultEntityTag etag4= new DefaultEntityTag( etagValue4 );
        DefaultEntityTag etag5= new DefaultEntityTag( etagValue5 );

        assertArrayEquals( "ETag contruction from String failed", etagValue3, etag1.getValue() );
        assertArrayEquals( "ETag contruction from Hex String failed", etagValue3, etag2.getValue() );
        assertArrayEquals( "ETag contruction from Byte[] failed", etagValue3, etag3.getValue() );
        assertArrayEquals( "ETag contruction from Long failed", etagValue3, etag4.getValue() );
        assertArrayEquals( "ETag contruction from Long failed", etagValue6, etag5.getValue() );
    }

    @Test
    public void testConstructorETagLargeByteArray() throws OptionValueException
    {
        byte[] etagValue1= new byte [9];
        for ( int i= 0; i < 9; i++ )
        {
            etagValue1[i]= (byte) i;
        }
        OptionValueException e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag1= new DefaultEntityTag( etagValue1 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Actual length is { 9 }" ) );
    }

    @Test
    public void testConstructorInvalidString() throws OptionValueException
    {
        String etagValue1= "10aaZZ";
        OptionValueException e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag1= new DefaultEntityTag( etagValue1, 16 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Cannot construct bytes value" ) );
        assertTrue( "exception has wrong message", e.getMessage().contains( etagValue1 ) );
    }

    @Test
    public void testConstructorUnevenString1() throws OptionValueException
    {
        String etagValue1= "1";
        DefaultEntityTag etag1= new DefaultEntityTag( etagValue1, 16 );
        assertEquals( "exception has wrong message", 1L, etag1.getValueAsNumber() );
    }

    @Test
    public void testConstructorETagUnevenString2() throws Exception
    {
        String etagValue1= "1122334455667";
        DefaultEntityTag etag1= new DefaultEntityTag( etagValue1, 16 );
        assertEquals( "exception has wrong message", 0x1122334455667L, etag1.getValueAsNumber() );
    }

    @Test
    public void testConstructorETagLargeString() throws OptionValueException
    {
        String etagValue1= "112233445566778899";
        OptionValueException e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag1= new DefaultEntityTag( etagValue1, 16 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Cannot construct bytes value" ) );
        assertTrue( "exception has wrong message", e.getMessage().contains( etagValue1 ) );
    }

    @Test
    public void testConstructorETagLargeString2() throws OptionValueException
    {
        String etagValue1= "this is too large to fit into an etag";
        OptionValueException e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag1= new DefaultEntityTag( etagValue1 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Given value result in array length" ) );
    }

    @Test
    public void testGetBytes() throws OptionValueException
    {
        byte[] etagValue00= { 0x00 };
        byte[] etagValue1= { (byte) 0xFF };
        String etagValue2= "h\u20ACy";
        String etagValue3= "68e282ac79";
        byte[] etagValue4= { (byte) 0x68, (byte) 0xe2, (byte) 0x82, (byte) 0xac, (byte) 0x79 };
        Long etagValue5= 255L;
        Integer etagValue6= 255;

        DefaultEntityTag etag00= new DefaultEntityTag( etagValue00 );
        DefaultEntityTag etag1= new DefaultEntityTag( etagValue1 );
        DefaultEntityTag etag2= new DefaultEntityTag( etagValue2 );
        DefaultEntityTag etag3= new DefaultEntityTag( etagValue3, 16 );
        DefaultEntityTag etag4= new DefaultEntityTag( etagValue4 );
        DefaultEntityTag etag5= new DefaultEntityTag( etagValue5 );
        DefaultEntityTag etag6= new DefaultEntityTag( etagValue6 );

        assertArrayEquals( "ETag.asBytes gives wrong value", etagValue00, etag00.getValue() );
        assertArrayEquals( "ETag.asBytes gives wrong value", etagValue1, etag1.getValue() );
        assertArrayEquals( "ETag.asBytes gives wrong value", etagValue4, etag2.getValue() );
        assertArrayEquals( "ETag.asBytes gives wrong value", etagValue4, etag3.getValue() );
        assertArrayEquals( "ETag.asBytes gives wrong value", etagValue4, etag4.getValue() );
        assertArrayEquals( "ETag.asBytes gives wrong value", etagValue1, etag5.getValue() );
        assertArrayEquals( "ETag.asBytes gives wrong value", etagValue1, etag6.getValue() );
        assertNotEquals( (Object) etagValue1, (Object) etag1.getValue() );
        assertNotEquals( (Object) etagValue3, (Object) etag3.getValue() );
    }

    @Test
    public void testGetLong() throws OptionValueException
    {
        byte[] etagValue1= { (byte) 0x01 };
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] etagValue3= { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77,
            (byte) 0x88 };
        long hexValue1= 1;
        long hexValue2= 0xafb990L;
        long hexValue3= 0x1122334455667788L;

        assertEquals(
            "ETag.toHexString gives wrong value",
            hexValue1,
            new DefaultEntityTag( etagValue1 ).getValueAsNumber()
        );
        assertEquals(
            "ETag.toHexString gives wrong value",
            hexValue2,
            new DefaultEntityTag( etagValue2 ).getValueAsNumber()
        );
        assertEquals(
            "ETag.toHexString gives wrong value",
            hexValue3,
            new DefaultEntityTag( etagValue3 ).getValueAsNumber()
        );
    }

    @Test
    public void testGetHexString() throws OptionValueException
    {
        byte[] etagValue1= { (byte) 0x01 };
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] etagValue3= { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77,
            (byte) 0x88 };
        String hexValue1= "01";
        String hexValue2= "afb990";
        String hexValue3= "1122334455667788";

        assertEquals(
            "ETag.toHexString gives wrong value",
            hexValue1,
            new DefaultEntityTag( etagValue1 ).getValueAsHex()
        );
        assertEquals(
            "ETag.toHexString gives wrong value",
            hexValue2,
            new DefaultEntityTag( etagValue2 ).getValueAsHex()
        );
        assertEquals(
            "ETag.toHexString gives wrong value",
            hexValue3,
            new DefaultEntityTag( etagValue3 ).getValueAsHex()
        );
    }

    @Test
    public void testToString() throws OptionValueException
    {
        byte[] etagValue1= { (byte) 0x01 };
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] etagValue3= { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77,
            (byte) 0x88 };
        String hexValue1= "BytesValue{ 01 }";
        String hexValue2= "BytesValue{ afb990 }";
        String hexValue3= "BytesValue{ 1122334455667788 }";

        assertEquals( "ETag.toHexString gives wrong value", hexValue1, new DefaultEntityTag( etagValue1 ).toString() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue2, new DefaultEntityTag( etagValue2 ).toString() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue3, new DefaultEntityTag( etagValue3 ).toString() );
    }

    @Test
    public void testValueOf() throws OptionValueException
    {
        String etagValue1= "h\u20ACy";
        String etagValue2= "68e282ac79";
        byte[] etagValue3= { (byte) 0x68, (byte) 0xe2, (byte) 0x82, (byte) 0xac, (byte) 0x79 };
        long etagValue4= 0x68e282ac79L;
        int etagValue5= 0xe282ac79;
        String etagValue6= "e282ac79";

        DefaultEntityTag etag1= DefaultEntityTag.valueOf( etagValue1 );
        DefaultEntityTag etag2= DefaultEntityTag.valueOf( etagValue2, 16 );
        DefaultEntityTag etag3= DefaultEntityTag.valueOf( etagValue3 );
        DefaultEntityTag etag4= DefaultEntityTag.valueOf( etagValue4 );
        DefaultEntityTag etag5= DefaultEntityTag.valueOf( etagValue5 );

        assertEquals( "ETag contruction from String failed", etagValue2, etag1.getValueAsHex() );
        assertEquals( "ETag contruction from Byte[] failed", etagValue2, etag2.getValueAsHex() );
        assertEquals( "ETag contruction from Byte[] failed", etagValue2, etag3.getValueAsHex() );
        assertEquals( "ETag contruction from Long failed", etagValue2, etag4.getValueAsHex() );
        assertEquals( "ETag contruction from Long failed", etagValue6, etag5.getValueAsHex() );
    }

    @Test
    public void testValueOfNull() throws OptionValueException
    {
        String etagValue1= null;
        byte[] etagValue2= null;
        String etagValue3= null;
        long etagValue4= 0L;
        byte[] etagValue5= {};
        String etagValue6= "";
        long etagValue7= 0L;
        int etagValue8= 0;
        int etagValue9= 0;

        @SuppressWarnings( "unused" )
        OptionValueException e;
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag1= DefaultEntityTag.valueOf( etagValue1 );
        } );
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag2= DefaultEntityTag.valueOf( etagValue2 );
        } );
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag3= DefaultEntityTag.valueOf( etagValue3 );
        } );
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag4= DefaultEntityTag.valueOf( etagValue4 );
        } );
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag5= DefaultEntityTag.valueOf( etagValue5 );
        } );
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag6= DefaultEntityTag.valueOf( etagValue6 );
        } );
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag7= DefaultEntityTag.valueOf( etagValue7 );
        } );
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag8= DefaultEntityTag.valueOf( etagValue8 );
        } );
        e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultEntityTag etag9= DefaultEntityTag.valueOf( etagValue9 );
        } );
    }

    @Test
    public void testGetList() throws OptionValueException
    {
        byte[] etagValue1= { (byte) 0x01, };
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] etagValue4= { (byte) 0xAF, (byte) 0xB9, (byte) 0x91 };
        byte[] etagValue5= { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77,
            (byte) 0x88 };

        DefaultEntityTag etag1= new DefaultEntityTag( etagValue1 );
        DefaultEntityTag etag2= new DefaultEntityTag( etagValue2 );
        DefaultEntityTag etag3= new DefaultEntityTag( etagValue3 );
        DefaultEntityTag etag4= new DefaultEntityTag( etagValue4 );
        DefaultEntityTag etag5= new DefaultEntityTag( etagValue5 );

        LinkedList< byte[] > byteslist= new LinkedList< byte[] >();
        LinkedList< byte[] > byteslistofone= new LinkedList< byte[] >();
        LinkedList< byte[] > bytesemptylist= new LinkedList< byte[] >();

        byteslist.add( etagValue1 );
        byteslist.add( etagValue2 );
        byteslist.add( etagValue3 );
        byteslist.add( etagValue4 );

        byteslistofone.add( etagValue4 );

        List< DefaultEntityTag > list= MessageUtils.getList( byteslist ).get();
        List< DefaultEntityTag > listofone= MessageUtils.getList( byteslistofone ).get();
        List< DefaultEntityTag > emptylist= MessageUtils.getList( bytesemptylist ).get();

        assertTrue( "ETag.getList doesn't contain etag", MessageUtils.isIn( etag1, list ) );
        assertTrue( "ETag.getList doesn't contain etag", MessageUtils.isIn( etag2, list ) );
        assertTrue( "ETag.getList doesn't contain etag", MessageUtils.isIn( etag3, list ) );
        assertTrue( "ETag.getList doesn't contain etag", MessageUtils.isIn( etag4, list ) );
        assertFalse( "ETag.getList does contain etag", MessageUtils.isIn( etag5, list ) );

        assertTrue( "ETag.getList doesn't contain etag", list.contains( etag1 ) );
        assertTrue( "ETag.getList doesn't contain etag", list.contains( etag2 ) );
        assertTrue( "ETag.getList doesn't contain etag", list.contains( etag3 ) );
        assertTrue( "ETag.getList doesn't contain etag", list.contains( etag4 ) );
        assertFalse( "ETag.getList does contain etag", list.contains( etag5 ) );

        assertTrue(
            "ETag.getList doesn't contain etag",
            MessageUtils.isIn( new DefaultEntityTag( etagValue1 ), list )
        );
        assertTrue(
            "ETag.getList doesn't contain etag",
            MessageUtils.isIn( new DefaultEntityTag( etagValue2 ), list )
        );
        assertTrue(
            "ETag.getList doesn't contain etag",
            MessageUtils.isIn( new DefaultEntityTag( etagValue3 ), list )
        );
        assertTrue(
            "ETag.getList doesn't contain etag",
            MessageUtils.isIn( new DefaultEntityTag( etagValue4 ), list )
        );
        assertFalse( "ETag.getList does contain etag", MessageUtils.isIn( new DefaultEntityTag( etagValue5 ), list ) );

        assertTrue(
            "ETag.getList doesn't contain etag",
            MessageUtils.isIn( new DefaultEntityTag( etagValue4 ), listofone )
        );
        assertFalse(
            "ETag.getList does contain etag",
            MessageUtils.isIn( new DefaultEntityTag( etagValue5 ), listofone )
        );

        assertFalse(
            "ETag.getList does contain etag",
            MessageUtils.isIn( new DefaultEntityTag( etagValue4 ), emptylist )
        );
        assertFalse(
            "ETag.getList does contain etag",
            MessageUtils.isIn( new DefaultEntityTag( etagValue5 ), emptylist )
        );
    }

    @Test
    public void testIsIn() throws OptionValueException
    {
        byte[] etagValue1= { (byte) 0x01 };
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue4= "afb991";
        String etagValue5= "afb99100112233";
        String etagValue6= "00";

        DefaultEntityTag etag1= new DefaultEntityTag( etagValue1 );
        DefaultEntityTag etag2= new DefaultEntityTag( etagValue2, 16 );
        DefaultEntityTag etag3= new DefaultEntityTag( etagValue3 );
        DefaultEntityTag etag4= new DefaultEntityTag( etagValue4, 16 );
        DefaultEntityTag etag5= new DefaultEntityTag( etagValue5, 16 );
        DefaultEntityTag etag6= new DefaultEntityTag( etagValue6 );

        LinkedList< DefaultEntityTag > list= new LinkedList< DefaultEntityTag >();
        LinkedList< DefaultEntityTag > listOfOne= new LinkedList< DefaultEntityTag >();
        LinkedList< DefaultEntityTag > emptyList= new LinkedList< DefaultEntityTag >();

        list.add( etag1 );
        list.add( etag2 );
        list.add( etag3 );
        list.add( etag4 );

        listOfOne.add( etag4 );

        assertTrue( "ETag.isIn gives wrong value", MessageUtils.isIn( etag1, list ) );
        assertTrue( "ETag.isIn gives wrong value", MessageUtils.isIn( etag2, list ) );
        assertTrue( "ETag.isIn gives wrong value", MessageUtils.isIn( etag3, list ) );
        assertTrue( "ETag.isIn gives wrong value", MessageUtils.isIn( etag4, list ) );
        assertFalse( "ETag.isIn gives wrong value", MessageUtils.isIn( etag5, list ) );
        assertFalse( "ETag.isIn gives wrong value", MessageUtils.isIn( etag6, list ) );
        assertFalse( "ETag.isIn gives wrong value", MessageUtils.isIn( etag5, null ) );

        assertTrue( "ETag.isIn gives wrong value", MessageUtils.isIn( new DefaultEntityTag( etagValue1 ), list ) );
        assertTrue( "ETag.isIn gives wrong value", MessageUtils.isIn( new DefaultEntityTag( etagValue2, 16 ), list ) );
        assertTrue( "ETag.isIn gives wrong value", MessageUtils.isIn( new DefaultEntityTag( etagValue3 ), list ) );
        assertTrue( "ETag.isIn gives wrong value", MessageUtils.isIn( new DefaultEntityTag( etagValue4, 16 ), list ) );
        assertFalse( "ETag.isIn gives wrong value", MessageUtils.isIn( new DefaultEntityTag( etagValue5, 16 ), list ) );
        assertFalse( "ETag.isIn gives wrong value", MessageUtils.isIn( new DefaultEntityTag( etagValue6 ), list ) );

        assertTrue(
            "ETag.isIn gives wrong value",
            MessageUtils.isIn( new DefaultEntityTag( etagValue4, 16 ), listOfOne )
        );
        assertFalse(
            "ETag.isIn gives wrong value",
            MessageUtils.isIn( new DefaultEntityTag( etagValue5, 16 ), listOfOne )
        );

        assertFalse(
            "ETag.isIn gives wrong value",
            MessageUtils.isIn( new DefaultEntityTag( etagValue4, 16 ), emptyList )
        );
        assertFalse(
            "ETag.isIn gives wrong value",
            MessageUtils.isIn( new DefaultEntityTag( etagValue5, 16 ), emptyList )
        );
    }

    @Test
    public void testCompareTo() throws OptionValueException
    {
        byte[] etagValue1= { (byte) 0x00 };
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue4= "afb991";
        String etagValue5= "afb99100112233";

        DefaultEntityTag etag1= new DefaultEntityTag( etagValue1 );
        DefaultEntityTag etag2= new DefaultEntityTag( etagValue2, 16 );
        DefaultEntityTag etag3= new DefaultEntityTag( etagValue3 );
        DefaultEntityTag etag4= new DefaultEntityTag( etagValue4, 16 );
        DefaultEntityTag etag5= new DefaultEntityTag( etagValue5, 16 );

        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag1.compareTo( etag1 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag2.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag3.compareTo( etag3 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag4.compareTo( etag4 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag5.compareTo( etag5 ) );

        assertEquals( "ETag.compareTo failed to compare", 1, etag2.compareTo( etag1 ) );
        assertEquals( "ETag.compareTo failed to compare", 0, etag3.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare", 1, etag4.compareTo( etag3 ) );
        assertEquals( "ETag.compareTo failed to compare", 1, etag5.compareTo( etag4 ) );

        assertEquals( "ETag.compareTo failed to compare", -1, etag1.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare", 0, etag2.compareTo( etag3 ) );
        assertEquals( "ETag.compareTo failed to compare", -1, etag3.compareTo( etag4 ) );
        assertEquals( "ETag.compareTo failed to compare", -1, etag4.compareTo( etag5 ) );

        assertEquals( "ETag.compareTo failed to compare to null", 1, etag5.compareTo( null ) );
    }

    @Test
    public void testCompareToInteger() throws OptionValueException
    {
        int etagValue1= 255;
        String etagValue2= "FF";
        int etagValue3= -223423477;

        DefaultEntityTag etag1= new DefaultEntityTag( etagValue1 );
        DefaultEntityTag etag2= new DefaultEntityTag( etagValue2, 16 );
        DefaultEntityTag etag3= new DefaultEntityTag( etagValue3 );

        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag1.compareTo( etag1 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag2.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag3.compareTo( etag3 ) );

        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag2.compareTo( etag1 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 1, etag3.compareTo( etag2 ) );

        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag1.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", -1, etag2.compareTo( etag3 ) );
    }

    @Test
    public void testCompareToLong() throws OptionValueException
    {
        long etagValue1= 255L;
        String etagValue2= "FF";
        long etagValue3= -45623423423423477L;

        DefaultEntityTag etag1= new DefaultEntityTag( etagValue1 );
        DefaultEntityTag etag2= new DefaultEntityTag( etagValue2, 16 );
        DefaultEntityTag etag3= new DefaultEntityTag( etagValue3 );

        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag1.compareTo( etag1 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag2.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag3.compareTo( etag3 ) );

        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag2.compareTo( etag1 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 1, etag3.compareTo( etag2 ) );

        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag1.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", -1, etag2.compareTo( etag3 ) );
    }

    @Test
    public void testHashCode() throws OptionValueException
    {
        byte[] etagValue1= { (byte) 0x01 };
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue4= "afb991";
        String etagValue5= "afb99100112233";
        String etagValue6= "01";

        DefaultEntityTag etag1= new DefaultEntityTag( etagValue1 );
        DefaultEntityTag etag2= new DefaultEntityTag( etagValue2, 16 );
        DefaultEntityTag etag3= new DefaultEntityTag( etagValue3 );
        DefaultEntityTag etag4= new DefaultEntityTag( etagValue4, 16 );
        DefaultEntityTag etag5= new DefaultEntityTag( etagValue5, 16 );
        DefaultEntityTag etag6= new DefaultEntityTag( etagValue6 );

        assertEquals(
            "ETag.hashCode failed to compare to equal etag",
            etag1.hashCode(),
            new DefaultEntityTag( etagValue1 ).hashCode()
        );
        assertEquals(
            "ETag.hashCode failed to compare to equal etag",
            etag2.hashCode(),
            new DefaultEntityTag( etagValue2, 16 ).hashCode()
        );
        assertEquals(
            "ETag.hashCode failed to compare to equal etag",
            etag3.hashCode(),
            new DefaultEntityTag( etagValue3 ).hashCode()
        );
        assertEquals(
            "ETag.hashCode failed to compare to equal etag",
            etag4.hashCode(),
            new DefaultEntityTag( etagValue4, 16 ).hashCode()
        );
        assertEquals(
            "ETag.hashCode failed to compare to equal etag",
            etag5.hashCode(),
            new DefaultEntityTag( etagValue5, 16 ).hashCode()
        );
        assertEquals(
            "ETag.hashCode failed to compare to equal etag",
            etag6.hashCode(),
            new DefaultEntityTag( etagValue6 ).hashCode()
        );

        assertNotEquals(
            "ETag.hashCode failed to compare to unequal etag",
            etag1.hashCode(),
            new DefaultEntityTag( etagValue5, 16 ).hashCode()
        );
        assertNotEquals(
            "ETag.hashCode failed to compare to unequal etag",
            etag2.hashCode(),
            new DefaultEntityTag( etagValue1 ).hashCode()
        );
        assertEquals(
            "ETag.hashCode failed to compare to equal etag",
            etag3.hashCode(),
            new DefaultEntityTag( etagValue2, 16 ).hashCode()
        );
        assertNotEquals(
            "ETag.hashCode failed to compare to unequal etag",
            etag4.hashCode(),
            new DefaultEntityTag( etagValue3 ).hashCode()
        );
        assertNotEquals(
            "ETag.hashCode failed to compare to unequal etag",
            etag5.hashCode(),
            new DefaultEntityTag( etagValue4, 16 ).hashCode()
        );
        assertNotEquals(
            "ETag.hashCode failed to compare to unequal etag",
            etag6.hashCode(),
            new DefaultEntityTag( etagValue3 ).hashCode()
        );

        assertNotEquals(
            "ETag.hashCode failed to compare to unequal etag",
            etag1.hashCode(),
            new DefaultEntityTag( etagValue2, 16 ).hashCode()
        );
        assertEquals(
            "ETag.hashCode failed to compare to equal etag",
            etag2.hashCode(),
            new DefaultEntityTag( etagValue3 ).hashCode()
        );
        assertNotEquals(
            "ETag.hashCode failed to compare to unequal etag",
            etag3.hashCode(),
            new DefaultEntityTag( etagValue4, 16 ).hashCode()
        );
        assertNotEquals(
            "ETag.hashCode failed to compare to unequal etag",
            etag4.hashCode(),
            new DefaultEntityTag( etagValue5, 16 ).hashCode()
        );
        assertNotEquals(
            "ETag.hashCode failed to compare to unequal etag",
            etag5.hashCode(),
            new DefaultEntityTag( etagValue1 ).hashCode()
        );
        assertNotEquals(
            "ETag.hashCode failed to compare to unequal etag",
            etag6.hashCode(),
            new DefaultEntityTag( etagValue1 ).hashCode()
        );
    }

    @Test
    public void testEquals() throws OptionValueException
    {
        String etagValue1= "ffb990";
        byte[] etagValue2= { (byte) 0xFF, (byte) 0xB9, (byte) 0x90 };
        String etagValue3= "afb991";
        String etagValue4= "afb99100112233";
        byte[] etagValue5= { (byte) 0x00 };

        DefaultEntityTag etag1= new DefaultEntityTag( etagValue1, 16 );
        DefaultEntityTag etag2= new DefaultEntityTag( etagValue2 );
        DefaultEntityTag etag3= new DefaultEntityTag( etagValue3, 16 );
        DefaultEntityTag etag4= new DefaultEntityTag( etagValue4, 16 );
        DefaultEntityTag etag5= new DefaultEntityTag( etagValue5 );

        assertEquals( "ETag.equals failed to compare to equal etag", etag1, etag1 );
        assertEquals( "ETag.equals failed to compare to equal etag", etag2, etag2 );
        assertEquals( "ETag.equals failed to compare to equal etag", etag3, etag3 );
        assertEquals( "ETag.equals failed to compare to equal etag", etag4, etag4 );
        assertEquals( "ETag.equals failed to compare to equal etag", etag5, etag5 );

        assertEquals( "ETag.equals failed to compare to equal etag", etag1, new DefaultEntityTag( etagValue1, 16 ) );
        assertEquals( "ETag.equals failed to compare to equal etag", etag2, new DefaultEntityTag( etagValue2 ) );
        assertEquals( "ETag.equals failed to compare to equal etag", etag3, new DefaultEntityTag( etagValue3, 16 ) );
        assertEquals( "ETag.equals failed to compare to equal etag", etag4, new DefaultEntityTag( etagValue4, 16 ) );
        assertEquals( "ETag.equals failed to compare to equal etag", etag5, new DefaultEntityTag( etagValue5 ) );

        assertEquals( "ETag.equals failed to compare to equal etag", etag1, etag2 );
        assertEquals( "ETag.equals failed to compare to equal etag", etag2, etag1 );

        assertNotEquals( "ETag.equals failed to compare to unequal etag", etag1, etag3 );
        assertNotEquals( "ETag.equals failed to compare to unequal etag", etag3, etag1 );
        assertNotEquals( "ETag.equals failed to compare to unequal etag", etag1, etag4 );
        assertNotEquals( "ETag.equals failed to compare to unequal etag", etag4, etag1 );
        assertNotEquals( "ETag.equals failed to compare to unequal etag", etag5, etag2 );

        assertNotEquals( "ETag.equals failed to compare to null", etag5, null );
    }

    @Test
    public void testEqualsToWrongClass() throws OptionValueException
    {
        String etagValue1= "1122334455667788";
        DefaultEntityTag etag1= new DefaultEntityTag( etagValue1, 16 );
        assertNotEquals( "ETag.equals Boolean returned true", etag1, Boolean.FALSE );
    }
}
