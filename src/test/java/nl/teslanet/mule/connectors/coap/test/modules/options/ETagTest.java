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
package nl.teslanet.mule.connectors.coap.test.modules.options;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

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
        Long etagValue7= 0L;
        Integer etagValue8= null;
        Integer etagValue9= 0;

        ETag etag0= new ETag();
        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );
        ETag etag5= new ETag( etagValue5 );
        ETag etag6= new ETag( etagValue6 );
        ETag etag7= new ETag( etagValue7 );
        ETag etag8= new ETag( etagValue8 );
        ETag etag9= new ETag( etagValue9 );

        assertEquals( "ETag contruction from null String failed", "", etag0.getValueAsHexString() );
        assertEquals( "ETag contruction from null String failed", "", etag1.getValueAsHexString() );
        assertEquals( "ETag contruction from null Byte[] failed", "", etag2.getValueAsHexString() );
        assertEquals( "ETag contruction from null Byte[] failed", "", etag3.getValueAsHexString() );
        assertEquals( "ETag contruction from null Long failed", "", etag4.getValueAsHexString() );
        assertEquals( "ETag contruction from empty Byte[] failed", "", etag5.getValueAsHexString() );
        assertEquals( "ETag contruction from empty String failed", "", etag6.getValueAsHexString() );
        assertEquals( "ETag contruction from empty String failed", "", etag7.getValueAsHexString() );
        assertEquals( "ETag contruction from empty String failed", "", etag8.getValueAsHexString() );
        assertEquals( "ETag contruction from empty String failed", "", etag9.getValueAsHexString() );
    }

    @Test
    public void testConstructor() throws InvalidETagException
    {
        String etagValue1= "h\u20ACy";
        String etagValue2= "68e282ac79";
        byte[] etagValue3= { (byte) 0x68, (byte) 0xe2, (byte) 0x82, (byte) 0xac, (byte) 0x79 };
        Long etagValue4= 0x68e282ac79L;
        Integer etagValue5= 0xe282ac79;
        byte[] etagValue6= { (byte) 0xe2, (byte) 0x82, (byte) 0xac, (byte) 0x79 };

        ETag etag0= new ETag();
        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2, 16 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );
        ETag etag5= new ETag( etagValue5 );

        assertArrayEquals( "ETag default contruction failed", new byte [0], etag0.getValue() );
        assertArrayEquals( "ETag contruction from String failed", etagValue3, etag1.getValue() );
        assertArrayEquals( "ETag contruction from Hex String failed", etagValue3, etag2.getValue() );
        assertArrayEquals( "ETag contruction from Byte[] failed", etagValue3, etag3.getValue() );
        assertArrayEquals( "ETag contruction from Long failed", etagValue3, etag4.getValue() );
        assertArrayEquals( "ETag contruction from Long failed", etagValue6, etag5.getValue() );
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
            ETag etag1= new ETag( etagValue1, 16 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Cannot construct etag value" ) );
        assertTrue( "exception has wrong message", e.getMessage().contains( etagValue1 ) );
    }

    @Test
    public void testConstructorUnevenString1() throws InvalidETagException
    {
        String etagValue1= "1";
        ETag etag1= new ETag( etagValue1, 16 );
        assertEquals( "exception has wrong message", 1L, etag1.getValueAsNumber() );
    }

    @Test
    public void testConstructorETagUnevenString2() throws Exception
    {
        String etagValue1= "1122334455667";
        ETag etag1= new ETag( etagValue1, 16 );
        assertEquals( "exception has wrong message", 0x1122334455667L, etag1.getValueAsNumber() );
    }

    @Test
    public void testConstructorETagLargeString() throws InvalidETagException
    {
        String etagValue1= "112233445566778899";
        InvalidETagException e= assertThrows( InvalidETagException.class, () -> {
            @SuppressWarnings( "unused" )
            ETag etag1= new ETag( etagValue1, 16 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Cannot construct etag value" ) );
        assertTrue( "exception has wrong message", e.getMessage().contains( etagValue1 ) );
    }

    @Test
    public void testConstructorETagLargeString2() throws InvalidETagException
    {
        String etagValue1= "this is too large to fit into an etag";
        InvalidETagException e= assertThrows( InvalidETagException.class, () -> {
            @SuppressWarnings( "unused" )
            ETag etag1= new ETag( etagValue1 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Cannot construct etag value" ) );
        assertTrue( "exception has wrong message", e.getMessage().contains( etagValue1 ) );
    }

    @Test
    public void testGetBytes() throws InvalidETagException
    {
        byte[] etagValue0= {};
        byte[] etagValue00= { 0x00 };
        byte[] etagValue1= { (byte) 0xFF };
        String etagValue2= "h\u20ACy";
        String etagValue3= "68e282ac79";
        byte[] etagValue4= { (byte) 0x68, (byte) 0xe2, (byte) 0x82, (byte) 0xac, (byte) 0x79 };
        Long etagValue5= 255L;
        Integer etagValue6= 255;

        ETag etag0= new ETag( etagValue0 );
        ETag etag00= new ETag( etagValue00 );
        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3, 16 );
        ETag etag4= new ETag( etagValue4 );
        ETag etag5= new ETag( etagValue5 );
        ETag etag6= new ETag( etagValue6 );

        assertArrayEquals( "ETag.asBytes gives wrong value", etagValue0, etag0.getValue() );
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
    public void testGetLong() throws InvalidETagException
    {
        byte[] etagValue0= {};
        byte[] etagValue1= { (byte) 0x00 };
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] etagValue3= { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x88 };
        long hexValue0= 0;
        long hexValue1= 0;
        long hexValue2= 0xafb990L;
        long hexValue3= 0x1122334455667788L;

        assertEquals( "ETag.toHexString gives wrong value", hexValue0, new ETag( etagValue0 ).getValueAsNumber() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue1, new ETag( etagValue1 ).getValueAsNumber() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue2, new ETag( etagValue2 ).getValueAsNumber() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue3, new ETag( etagValue3 ).getValueAsNumber() );
    }

    @Test
    public void testGetHexString() throws InvalidETagException
    {
        byte[] etagValue0= {};
        byte[] etagValue1= { (byte) 0x00 };
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] etagValue3= { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x88 };
        String hexValue0= "";
        String hexValue1= "00";
        String hexValue2= "afb990";
        String hexValue3= "1122334455667788";

        assertEquals( "ETag.toHexString gives wrong value", hexValue0, new ETag( etagValue0 ).getValueAsHexString() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue1, new ETag( etagValue1 ).getValueAsHexString() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue2, new ETag( etagValue2 ).getValueAsHexString() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue3, new ETag( etagValue3 ).getValueAsHexString() );
    }

    @Test
    public void testToString() throws InvalidETagException
    {
        byte[] etagValue0= {};
        byte[] etagValue1= { (byte) 0x00 };
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] etagValue3= { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x88 };
        String hexValue0= "ETag {  }";
        String hexValue1= "ETag { 00 }";
        String hexValue2= "ETag { afb990 }";
        String hexValue3= "ETag { 1122334455667788 }";

        assertEquals( "ETag.toHexString gives wrong value", hexValue0, new ETag( etagValue0 ).toString() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue1, new ETag( etagValue1 ).toString() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue2, new ETag( etagValue2 ).toString() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue3, new ETag( etagValue3 ).toString() );
    }

    @Test
    public void testValueOf() throws InvalidETagException
    {
        String etagValue1= "h\u20ACy";
        String etagValue2= "68e282ac79";
        byte[] etagValue3= { (byte) 0x68, (byte) 0xe2, (byte) 0x82, (byte) 0xac, (byte) 0x79 };
        Long etagValue4= 0x68e282ac79L;
        Integer etagValue5= 0xe282ac79;
        String etagValue6= "e282ac79";

        ETag etag0= ETag.valueOf( (String) null );
        ETag etag00= ETag.valueOf( (String) null, 16 );
        ETag etag000= ETag.valueOf( (Long) null );
        ETag etag0000= ETag.valueOf( (byte[]) null );
        ETag etag1= ETag.valueOf( etagValue1 );
        ETag etag2= ETag.valueOf( etagValue2, 16 );
        ETag etag3= ETag.valueOf( etagValue3 );
        ETag etag4= ETag.valueOf( etagValue4 );
        ETag etag5= ETag.valueOf( etagValue5 );

        assertTrue( "ETag contruction from null failed", etag0.getValueAsHexString().equals( "" ) );
        assertTrue( "ETag contruction from null failed", etag00.getValueAsHexString().equals( "" ) );
        assertTrue( "ETag contruction from null failed", etag000.getValueAsHexString().equals( "" ) );
        assertTrue( "ETag contruction from null failed", etag0000.getValueAsHexString().equals( "" ) );
        assertTrue( "ETag contruction from String failed", etag1.getValueAsHexString().equals( etagValue2 ) );
        assertTrue( "ETag contruction from Byte[] failed", etag2.getValueAsHexString().equals( etagValue2 ) );
        assertTrue( "ETag contruction from Byte[] failed", etag3.getValueAsHexString().equals( etagValue2 ) );
        assertTrue( "ETag contruction from Long failed", etag4.getValueAsHexString().equals( etagValue2 ) );
        assertTrue( "ETag contruction from Long failed", etag5.getValueAsHexString().equals( etagValue6 ) );
    }

    @Test
    public void testValueOfNull() throws InvalidETagException
    {
        String etagValue1= null;
        byte[] etagValue2= null;
        String etagValue3= null;
        Long etagValue4= null;
        byte[] etagValue5= {};
        String etagValue6= "";
        long etagValue7= 0L;
        Integer etagValue8= null;
        int etagValue9= 0;

        ETag etag1= ETag.valueOf( etagValue1 );
        ETag etag2= ETag.valueOf( etagValue2 );
        ETag etag3= ETag.valueOf( etagValue3 );
        ETag etag4= ETag.valueOf( etagValue4 );
        ETag etag5= ETag.valueOf( etagValue5 );
        ETag etag6= ETag.valueOf( etagValue6 );
        ETag etag7= ETag.valueOf( etagValue7 );
        ETag etag8= ETag.valueOf( etagValue8 );
        ETag etag9= ETag.valueOf( etagValue9 );

        assertEquals( "ETag contruction from null failed", "", etag1.getValueAsHexString() );
        assertEquals( "ETag contruction from String failed", "", etag1.getValueAsHexString() );
        assertEquals( "ETag contruction from Byte[] failed", "", etag2.getValueAsHexString() );
        assertEquals( "ETag contruction from Byte[] failed", "", etag3.getValueAsHexString() );
        assertEquals( "ETag contruction from null Long failed", "", etag4.getValueAsHexString() );
        assertEquals( "ETag contruction from empty Byte[] failed", "", etag5.getValueAsHexString() );
        assertEquals( "ETag contruction from empty String failed", "", etag6.getValueAsHexString() );
        assertEquals( "ETag contruction from 0 long failed", "", etag7.getValueAsHexString() );
        assertEquals( "ETag contruction from null Integer failed", "", etag8.getValueAsHexString() );
        assertEquals( "ETag contruction from 0 int failed", "", etag9.getValueAsHexString() );
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

    @Test
    public void testIsIn() throws InvalidETagException
    {
        byte[] etagValue1= { (byte) 0x00 };
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue4= "afb991";
        String etagValue5= "afb99100112233";
        String etagValue6= "00";

        ETag etag0= new ETag();
        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2, 16 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4, 16 );
        ETag etag5= new ETag( etagValue5, 16 );
        ETag etag6= new ETag( etagValue6 );

        LinkedList< ETag > list= new LinkedList< ETag >();
        LinkedList< ETag > listOfOne= new LinkedList< ETag >();
        LinkedList< ETag > emptyList= new LinkedList< ETag >();

        list.add( etag0 );
        list.add( etag1 );
        list.add( etag2 );
        list.add( etag3 );
        list.add( etag4 );

        listOfOne.add( etag4 );

        assertTrue( "ETag.isIn gives wrong value", etag0.isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", etag1.isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", etag2.isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", etag3.isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", etag4.isIn( list ) );
        assertFalse( "ETag.isIn gives wrong value", etag5.isIn( list ) );
        assertFalse( "ETag.isIn gives wrong value", etag6.isIn( list ) );

        assertTrue( "ETag.isIn gives wrong value", new ETag().isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", new ETag( etagValue1 ).isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", new ETag( etagValue2, 16 ).isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", new ETag( etagValue3 ).isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", new ETag( etagValue4, 16 ).isIn( list ) );
        assertFalse( "ETag.isIn gives wrong value", new ETag( etagValue5, 16 ).isIn( list ) );
        assertFalse( "ETag.isIn gives wrong value", new ETag( etagValue6 ).isIn( list ) );

        assertTrue( "ETag.isIn gives wrong value", new ETag( etagValue4, 16 ).isIn( listOfOne ) );
        assertFalse( "ETag.isIn gives wrong value", new ETag( etagValue5, 16 ).isIn( listOfOne ) );
        assertFalse( "ETag.isIn gives wrong value", new ETag().isIn( listOfOne ) );

        assertFalse( "ETag.isIn gives wrong value", new ETag( etagValue4, 16 ).isIn( emptyList ) );
        assertFalse( "ETag.isIn gives wrong value", new ETag( etagValue5, 16 ).isIn( emptyList ) );
        assertFalse( "ETag.isIn gives wrong value", new ETag().isIn( emptyList ) );
    }

    @Test
    public void testNotEmpty() throws InvalidETagException
    {
        String etagValue1= "afb990";
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue3= "FF";
        Long etagValue4= 255L;
        Integer etagValue5= 255;

        ETag etag1= ETag.valueOf( etagValue1 );
        ETag etag2= ETag.valueOf( etagValue2 );
        ETag etag3= ETag.valueOf( etagValue3 );
        ETag etag4= ETag.valueOf( etagValue4 );
        ETag etag5= ETag.valueOf( etagValue5 );

        assertFalse( "ETag should evaluate not empty", etag1.isEmpty() );
        assertFalse( "ETag should evaluate not empty", etag2.isEmpty() );
        assertFalse( "ETag should evaluate not empty", etag3.isEmpty() );
        assertFalse( "ETag should evaluate not empty", etag4.isEmpty() );
        assertFalse( "ETag should evaluate not empty", etag5.isEmpty() );
    }

    @Test
    public void testEmpty() throws InvalidETagException
    {
        String etagValue1= "";
        byte[] etagValue2= {};
        String etagValue3= "";
        Long etagValue4= 0L;
        Integer etagValue5= 0;

        ETag etag1= ETag.valueOf( etagValue1 );
        ETag etag2= ETag.valueOf( etagValue2 );
        ETag etag3= ETag.valueOf( etagValue3 );
        ETag etag4= ETag.valueOf( etagValue4 );
        ETag etag5= ETag.valueOf( etagValue5 );

        assertTrue( "ETag should evaluate not empty", etag1.isEmpty() );
        assertTrue( "ETag should evaluate not empty", etag2.isEmpty() );
        assertTrue( "ETag should evaluate not empty", etag3.isEmpty() );
        assertTrue( "ETag should evaluate not empty", etag4.isEmpty() );
        assertTrue( "ETag should evaluate not empty", etag5.isEmpty() );
    }

    @Test
    public void testCompareTo() throws InvalidETagException
    {
        byte[] etagValue1= { (byte) 0x00 };
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue4= "afb991";
        String etagValue5= "afb99100112233";

        ETag etag0= new ETag();
        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2, 16 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4, 16 );
        ETag etag5= new ETag( etagValue5, 16 );

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
    public void testCompareToInteger() throws InvalidETagException
    {
        Integer etagValue1= 255;
        String etagValue2= "FF";
        Integer etagValue3= -223423477;
        Integer etagValue4= null;

        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2, 16 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );

        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag1.compareTo( etag1 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag2.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag3.compareTo( etag3 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag4.compareTo( etag4 ) );

        assertEquals( "ETag.compareTo failed to compare to equal etag", 1, etag1.compareTo( etag4 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag2.compareTo( etag1 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 1, etag3.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", -1, etag4.compareTo( etag3 ) );

        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag1.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", -1, etag2.compareTo( etag3 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 1, etag3.compareTo( etag4 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", -1, etag4.compareTo( etag1 ) );

        assertEquals( "ETag.compareTo failed to compare to null", 1, etag4.compareTo( null ) );
    }

    @Test
    public void testCompareToLong() throws InvalidETagException
    {
        Long etagValue1= 255L;
        String etagValue2= "FF";
        Long etagValue3= -45623423423423477L;
        Long etagValue4= null;

        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2, 16 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4 );

        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag1.compareTo( etag1 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag2.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag3.compareTo( etag3 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag4.compareTo( etag4 ) );

        assertEquals( "ETag.compareTo failed to compare to equal etag", 1, etag1.compareTo( etag4 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag2.compareTo( etag1 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 1, etag3.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", -1, etag4.compareTo( etag3 ) );

        assertEquals( "ETag.compareTo failed to compare to equal etag", 0, etag1.compareTo( etag2 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", -1, etag2.compareTo( etag3 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", 1, etag3.compareTo( etag4 ) );
        assertEquals( "ETag.compareTo failed to compare to equal etag", -1, etag4.compareTo( etag1 ) );

        assertEquals( "ETag.compareTo failed to compare to null", 1, etag4.compareTo( null ) );
    }

    @Test
    public void testHashCode() throws InvalidETagException
    {
        String etagValue0= null;
        byte[] etagValue1= { (byte) 0x00 };
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue4= "afb991";
        String etagValue5= "afb99100112233";
        String etagValue6= "00";

        ETag etag0= new ETag();
        ETag etag1= new ETag( etagValue1 );
        ETag etag2= new ETag( etagValue2, 16 );
        ETag etag3= new ETag( etagValue3 );
        ETag etag4= new ETag( etagValue4, 16 );
        ETag etag5= new ETag( etagValue5, 16 );
        ETag etag6= new ETag( etagValue6 );

        assertEquals( "ETag.hashCode failed to compare to equal etag", etag0.hashCode(), new ETag( etagValue0 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag1.hashCode(), new ETag( etagValue1 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag2.hashCode(), new ETag( etagValue2, 16 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag3.hashCode(), new ETag( etagValue3 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag4.hashCode(), new ETag( etagValue4, 16 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag5.hashCode(), new ETag( etagValue5, 16 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag6.hashCode(), new ETag( etagValue6 ).hashCode() );

        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag0.hashCode(), new ETag( etagValue4 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag1.hashCode(), new ETag( etagValue5, 16 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag2.hashCode(), new ETag( etagValue1 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag3.hashCode(), new ETag( etagValue2, 16 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag4.hashCode(), new ETag( etagValue3 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag5.hashCode(), new ETag( etagValue4, 16 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag6.hashCode(), new ETag( etagValue3 ).hashCode() );

        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag0.hashCode(), new ETag( etagValue3 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag1.hashCode(), new ETag( etagValue2, 16 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag2.hashCode(), new ETag( etagValue3 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag3.hashCode(), new ETag( etagValue4, 16 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag4.hashCode(), new ETag( etagValue5, 16 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag5.hashCode(), new ETag( etagValue1 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag6.hashCode(), new ETag( etagValue1 ).hashCode() );
    }

    @Test
    public void testEquals() throws InvalidETagException
    {
        String etagValue1= "ffb990";
        byte[] etagValue2= { (byte) 0xFF, (byte) 0xB9, (byte) 0x90 };
        String etagValue3= "afb991";
        String etagValue4= "afb99100112233";
        byte[] etagValue5= { (byte) 0x00 };

        ETag etag0= new ETag();
        ETag etag1= new ETag( etagValue1, 16 );
        ETag etag2= new ETag( etagValue2 );
        ETag etag3= new ETag( etagValue3, 16 );
        ETag etag4= new ETag( etagValue4, 16 );
        ETag etag5= new ETag( etagValue5 );

        assertTrue( "ETag.equals failed to compare to equal etag", etag0.equals( etag0 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag1.equals( etag1 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag2.equals( etag2 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag3.equals( etag3 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag4.equals( etag4 ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag5.equals( etag5 ) );

        assertTrue( "ETag.equals failed to compare to equal etag", etag0.equals( new ETag() ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag1.equals( new ETag( etagValue1, 16 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag2.equals( new ETag( etagValue2 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag3.equals( new ETag( etagValue3, 16 ) ) );
        assertTrue( "ETag.equals failed to compare to equal etag", etag4.equals( new ETag( etagValue4, 16 ) ) );
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

    @SuppressWarnings( "unlikely-arg-type" )
    @Test
    public void testEqualsToWrongClass() throws InvalidETagException
    {
        String etagValue1= "1122334455667788";
        ETag etag1= new ETag( etagValue1, 16 );
        assertFalse( "ETag.equals Boolean returned true", etag1.equals( new Boolean( false ) ) );
    }
}
