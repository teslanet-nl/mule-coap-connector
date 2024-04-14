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

import nl.teslanet.mule.connectors.coap.api.entity.EntityTag;
import nl.teslanet.mule.connectors.coap.api.entity.EntityTagException;
import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;


/**
 * Tests the implementation of the Etag class.
 *
 */
public class ETagTest
{
    @Test
    public void testConstructorNullValue() throws EntityTagException
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

        EntityTag etag0= new EntityTag();
        EntityTag etag1= new EntityTag( etagValue1 );
        EntityTag etag2= new EntityTag( etagValue2 );
        EntityTag etag3= new EntityTag( etagValue3 );
        EntityTag etag4= new EntityTag( etagValue4 );
        EntityTag etag5= new EntityTag( etagValue5 );
        EntityTag etag6= new EntityTag( etagValue6 );
        EntityTag etag7= new EntityTag( etagValue7 );
        EntityTag etag8= new EntityTag( etagValue8 );
        EntityTag etag9= new EntityTag( etagValue9 );
        EntityTag etag10= new EntityTag( etagValue1, 16 );
        EntityTag etag11= new EntityTag( etagValue6, 16 );

        assertEquals( "ETag contruction from null String failed", "", etag0.getValueAsHex() );
        assertEquals( "ETag contruction from null String failed", "", etag1.getValueAsHex() );
        assertEquals( "ETag contruction from null Byte[] failed", "", etag2.getValueAsHex() );
        assertEquals( "ETag contruction from null Byte[] failed", "", etag3.getValueAsHex() );
        assertEquals( "ETag contruction from null Long failed", "", etag4.getValueAsHex() );
        assertEquals( "ETag contruction from empty Byte[] failed", "", etag5.getValueAsHex() );
        assertEquals( "ETag contruction from empty String failed", "", etag6.getValueAsHex() );
        assertEquals( "ETag contruction from empty String failed", "", etag7.getValueAsHex() );
        assertEquals( "ETag contruction from empty String failed", "", etag8.getValueAsHex() );
        assertEquals( "ETag contruction from empty String failed", "", etag9.getValueAsHex() );
        assertEquals( "ETag contruction with radix from null String failed", "", etag10.getValueAsHex() );
        assertEquals( "ETag contruction with radix from empty String failed", "", etag11.getValueAsHex() );
    }

    @Test
    public void testConstructor() throws EntityTagException
    {
        String etagValue1= "h\u20ACy";
        String etagValue2= "68e282ac79";
        byte[] etagValue3= { (byte) 0x68, (byte) 0xe2, (byte) 0x82, (byte) 0xac, (byte) 0x79 };
        Long etagValue4= 0x68e282ac79L;
        Integer etagValue5= 0xe282ac79;
        byte[] etagValue6= { (byte) 0xe2, (byte) 0x82, (byte) 0xac, (byte) 0x79 };

        EntityTag etag0= new EntityTag();
        EntityTag etag1= new EntityTag( etagValue1 );
        EntityTag etag2= new EntityTag( etagValue2, 16 );
        EntityTag etag3= new EntityTag( etagValue3 );
        EntityTag etag4= new EntityTag( etagValue4 );
        EntityTag etag5= new EntityTag( etagValue5 );

        assertArrayEquals( "ETag default contruction failed", new byte [0], etag0.getValue() );
        assertArrayEquals( "ETag contruction from String failed", etagValue3, etag1.getValue() );
        assertArrayEquals( "ETag contruction from Hex String failed", etagValue3, etag2.getValue() );
        assertArrayEquals( "ETag contruction from Byte[] failed", etagValue3, etag3.getValue() );
        assertArrayEquals( "ETag contruction from Long failed", etagValue3, etag4.getValue() );
        assertArrayEquals( "ETag contruction from Long failed", etagValue6, etag5.getValue() );
    }

    @Test
    public void testConstructorETagLargeByteArray() throws EntityTagException
    {
        byte[] etagValue1= new byte [9];
        for ( int i= 0; i < 9; i++ )
        {
            etagValue1[i]= (byte) i;
        }
        EntityTagException e= assertThrows( EntityTagException.class, () -> {
            @SuppressWarnings( "unused" )
            EntityTag etag1= new EntityTag( etagValue1 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Given length is: 9" ) );
    }

    @Test
    public void testConstructorInvalidString() throws EntityTagException
    {
        String etagValue1= "10aaZZ";
        EntityTagException e= assertThrows( EntityTagException.class, () -> {
            @SuppressWarnings( "unused" )
            EntityTag etag1= new EntityTag( etagValue1, 16 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Cannot construct etag value" ) );
        assertTrue( "exception has wrong message", e.getMessage().contains( etagValue1 ) );
    }

    @Test
    public void testConstructorUnevenString1() throws EntityTagException
    {
        String etagValue1= "1";
        EntityTag etag1= new EntityTag( etagValue1, 16 );
        assertEquals( "exception has wrong message", 1L, etag1.getValueAsNumber() );
    }

    @Test
    public void testConstructorETagUnevenString2() throws Exception
    {
        String etagValue1= "1122334455667";
        EntityTag etag1= new EntityTag( etagValue1, 16 );
        assertEquals( "exception has wrong message", 0x1122334455667L, etag1.getValueAsNumber() );
    }

    @Test
    public void testConstructorETagLargeString() throws EntityTagException
    {
        String etagValue1= "112233445566778899";
        EntityTagException e= assertThrows( EntityTagException.class, () -> {
            @SuppressWarnings( "unused" )
            EntityTag etag1= new EntityTag( etagValue1, 16 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Cannot construct etag value" ) );
        assertTrue( "exception has wrong message", e.getMessage().contains( etagValue1 ) );
    }

    @Test
    public void testConstructorETagLargeString2() throws EntityTagException
    {
        String etagValue1= "this is too large to fit into an etag";
        EntityTagException e= assertThrows( EntityTagException.class, () -> {
            @SuppressWarnings( "unused" )
            EntityTag etag1= new EntityTag( etagValue1 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Cannot construct etag value" ) );
        assertTrue( "exception has wrong message", e.getMessage().contains( etagValue1 ) );
    }

    @Test
    public void testGetBytes() throws EntityTagException
    {
        byte[] etagValue0= {};
        byte[] etagValue00= { 0x00 };
        byte[] etagValue1= { (byte) 0xFF };
        String etagValue2= "h\u20ACy";
        String etagValue3= "68e282ac79";
        byte[] etagValue4= { (byte) 0x68, (byte) 0xe2, (byte) 0x82, (byte) 0xac, (byte) 0x79 };
        Long etagValue5= 255L;
        Integer etagValue6= 255;

        EntityTag etag0= new EntityTag( etagValue0 );
        EntityTag etag00= new EntityTag( etagValue00 );
        EntityTag etag1= new EntityTag( etagValue1 );
        EntityTag etag2= new EntityTag( etagValue2 );
        EntityTag etag3= new EntityTag( etagValue3, 16 );
        EntityTag etag4= new EntityTag( etagValue4 );
        EntityTag etag5= new EntityTag( etagValue5 );
        EntityTag etag6= new EntityTag( etagValue6 );

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
    public void testGetLong() throws EntityTagException
    {
        byte[] etagValue0= {};
        byte[] etagValue1= { (byte) 0x00 };
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] etagValue3= { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x88 };
        long hexValue0= 0;
        long hexValue1= 0;
        long hexValue2= 0xafb990L;
        long hexValue3= 0x1122334455667788L;

        assertEquals( "ETag.toHexString gives wrong value", hexValue0, new EntityTag( etagValue0 ).getValueAsNumber() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue1, new EntityTag( etagValue1 ).getValueAsNumber() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue2, new EntityTag( etagValue2 ).getValueAsNumber() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue3, new EntityTag( etagValue3 ).getValueAsNumber() );
    }

    @Test
    public void testGetHexString() throws EntityTagException
    {
        byte[] etagValue0= {};
        byte[] etagValue1= { (byte) 0x00 };
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] etagValue3= { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x88 };
        String hexValue0= "";
        String hexValue1= "00";
        String hexValue2= "afb990";
        String hexValue3= "1122334455667788";

        assertEquals( "ETag.toHexString gives wrong value", hexValue0, new EntityTag( etagValue0 ).getValueAsHex() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue1, new EntityTag( etagValue1 ).getValueAsHex() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue2, new EntityTag( etagValue2 ).getValueAsHex() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue3, new EntityTag( etagValue3 ).getValueAsHex() );
    }

    @Test
    public void testToString() throws EntityTagException
    {
        byte[] etagValue0= {};
        byte[] etagValue1= { (byte) 0x00 };
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] etagValue3= { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x88 };
        String hexValue0= "ETag{  }";
        String hexValue1= "ETag{ 00 }";
        String hexValue2= "ETag{ afb990 }";
        String hexValue3= "ETag{ 1122334455667788 }";

        assertEquals( "ETag.toHexString gives wrong value", hexValue0, new EntityTag( etagValue0 ).toString() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue1, new EntityTag( etagValue1 ).toString() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue2, new EntityTag( etagValue2 ).toString() );
        assertEquals( "ETag.toHexString gives wrong value", hexValue3, new EntityTag( etagValue3 ).toString() );
    }

    @Test
    public void testValueOf() throws EntityTagException
    {
        String etagValue1= "h\u20ACy";
        String etagValue2= "68e282ac79";
        byte[] etagValue3= { (byte) 0x68, (byte) 0xe2, (byte) 0x82, (byte) 0xac, (byte) 0x79 };
        long etagValue4= 0x68e282ac79L;
        int etagValue5= 0xe282ac79;
        String etagValue6= "e282ac79";

        EntityTag etag0= EntityTag.valueOf( (String) null );
        EntityTag etag00= EntityTag.valueOf( (String) null, 16 );
        EntityTag etag000= EntityTag.valueOf( 0L );
        EntityTag etag0000= EntityTag.valueOf( (byte[]) null );
        EntityTag etag1= EntityTag.valueOf( etagValue1 );
        EntityTag etag2= EntityTag.valueOf( etagValue2, 16 );
        EntityTag etag3= EntityTag.valueOf( etagValue3 );
        EntityTag etag4= EntityTag.valueOf( etagValue4 );
        EntityTag etag5= EntityTag.valueOf( etagValue5 );

        assertEquals( "ETag contruction from null failed", OptionUtils.EMPTY_STRING, etag0.getValueAsHex() );
        assertEquals( "ETag contruction from null failed", OptionUtils.EMPTY_STRING, etag00.getValueAsHex() );
        assertEquals( "ETag contruction from null failed", OptionUtils.EMPTY_STRING, etag000.getValueAsHex() );
        assertEquals( "ETag contruction from null failed", OptionUtils.EMPTY_STRING, etag0000.getValueAsHex() );
        assertEquals( "ETag contruction from String failed", etagValue2, etag1.getValueAsHex() );
        assertEquals( "ETag contruction from Byte[] failed", etagValue2, etag2.getValueAsHex() );
        assertEquals( "ETag contruction from Byte[] failed", etagValue2, etag3.getValueAsHex() );
        assertEquals( "ETag contruction from Long failed", etagValue2, etag4.getValueAsHex() );
        assertEquals( "ETag contruction from Long failed", etagValue6, etag5.getValueAsHex() );
    }

    @Test
    public void testValueOfNull() throws EntityTagException
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

        EntityTag etag1= EntityTag.valueOf( etagValue1 );
        EntityTag etag2= EntityTag.valueOf( etagValue2 );
        EntityTag etag3= EntityTag.valueOf( etagValue3 );
        EntityTag etag4= EntityTag.valueOf( etagValue4 );
        EntityTag etag5= EntityTag.valueOf( etagValue5 );
        EntityTag etag6= EntityTag.valueOf( etagValue6 );
        EntityTag etag7= EntityTag.valueOf( etagValue7 );
        EntityTag etag8= EntityTag.valueOf( etagValue8 );
        EntityTag etag9= EntityTag.valueOf( etagValue9 );

        assertEquals( "ETag contruction from null failed", "", etag1.getValueAsHex() );
        assertEquals( "ETag contruction from String failed", "", etag1.getValueAsHex() );
        assertEquals( "ETag contruction from Byte[] failed", "", etag2.getValueAsHex() );
        assertEquals( "ETag contruction from Byte[] failed", "", etag3.getValueAsHex() );
        assertEquals( "ETag contruction from null Long failed", "", etag4.getValueAsHex() );
        assertEquals( "ETag contruction from empty Byte[] failed", "", etag5.getValueAsHex() );
        assertEquals( "ETag contruction from empty String failed", "", etag6.getValueAsHex() );
        assertEquals( "ETag contruction from 0 long failed", "", etag7.getValueAsHex() );
        assertEquals( "ETag contruction from null Integer failed", "", etag8.getValueAsHex() );
        assertEquals( "ETag contruction from 0 int failed", "", etag9.getValueAsHex() );
    }

    @Test
    public void testGetList() throws EntityTagException
    {
        byte[] etagValue1= { (byte) 0x00, };
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] etagValue4= { (byte) 0xAF, (byte) 0xB9, (byte) 0x91 };
        byte[] etagValue5= { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x88 };

        EntityTag etag0= new EntityTag();
        EntityTag etag1= new EntityTag( etagValue1 );
        EntityTag etag2= new EntityTag( etagValue2 );
        EntityTag etag3= new EntityTag( etagValue3 );
        EntityTag etag4= new EntityTag( etagValue4 );
        EntityTag etag5= new EntityTag( etagValue5 );

        LinkedList< byte[] > byteslist= new LinkedList< byte[] >();
        LinkedList< byte[] > byteslistofone= new LinkedList< byte[] >();
        LinkedList< byte[] > bytesemptylist= new LinkedList< byte[] >();

        byteslist.add( null );
        byteslist.add( etagValue1 );
        byteslist.add( etagValue2 );
        byteslist.add( etagValue3 );
        byteslist.add( etagValue4 );

        byteslistofone.add( etagValue4 );

        List< EntityTag > list= EntityTag.getList( byteslist );
        List< EntityTag > listofone= EntityTag.getList( byteslistofone );
        List< EntityTag > emptylist= EntityTag.getList( bytesemptylist );

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

        assertTrue( "ETag.getList doesn't contain etag", new EntityTag().isIn( list ) );
        assertTrue( "ETag.getList doesn't contain etag", new EntityTag( etagValue1 ).isIn( list ) );
        assertTrue( "ETag.getList doesn't contain etag", new EntityTag( etagValue2 ).isIn( list ) );
        assertTrue( "ETag.getList doesn't contain etag", new EntityTag( etagValue3 ).isIn( list ) );
        assertTrue( "ETag.getList doesn't contain etag", new EntityTag( etagValue4 ).isIn( list ) );
        assertFalse( "ETag.getList does contain etag", new EntityTag( etagValue5 ).isIn( list ) );

        assertTrue( "ETag.getList doesn't contain etag", new EntityTag( etagValue4 ).isIn( listofone ) );
        assertFalse( "ETag.getList does contain etag", new EntityTag( etagValue5 ).isIn( listofone ) );
        assertFalse( "ETag.getList does contain etag", new EntityTag().isIn( listofone ) );

        assertFalse( "ETag.getList does contain etag", new EntityTag( etagValue4 ).isIn( emptylist ) );
        assertFalse( "ETag.getList does contain etag", new EntityTag( etagValue5 ).isIn( emptylist ) );
        assertFalse( "ETag.getList does contain etag", new EntityTag().isIn( emptylist ) );
    }

    @Test
    public void testIsIn() throws EntityTagException
    {
        byte[] etagValue1= { (byte) 0x00 };
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue4= "afb991";
        String etagValue5= "afb99100112233";
        String etagValue6= "00";

        EntityTag etag0= new EntityTag();
        EntityTag etag1= new EntityTag( etagValue1 );
        EntityTag etag2= new EntityTag( etagValue2, 16 );
        EntityTag etag3= new EntityTag( etagValue3 );
        EntityTag etag4= new EntityTag( etagValue4, 16 );
        EntityTag etag5= new EntityTag( etagValue5, 16 );
        EntityTag etag6= new EntityTag( etagValue6 );

        LinkedList< EntityTag > list= new LinkedList< EntityTag >();
        LinkedList< EntityTag > listOfOne= new LinkedList< EntityTag >();
        LinkedList< EntityTag > emptyList= new LinkedList< EntityTag >();

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
        assertFalse( "ETag.isIn gives wrong value", etag5.isIn( null ) );

        assertTrue( "ETag.isIn gives wrong value", new EntityTag().isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", new EntityTag( etagValue1 ).isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", new EntityTag( etagValue2, 16 ).isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", new EntityTag( etagValue3 ).isIn( list ) );
        assertTrue( "ETag.isIn gives wrong value", new EntityTag( etagValue4, 16 ).isIn( list ) );
        assertFalse( "ETag.isIn gives wrong value", new EntityTag( etagValue5, 16 ).isIn( list ) );
        assertFalse( "ETag.isIn gives wrong value", new EntityTag( etagValue6 ).isIn( list ) );

        assertTrue( "ETag.isIn gives wrong value", new EntityTag( etagValue4, 16 ).isIn( listOfOne ) );
        assertFalse( "ETag.isIn gives wrong value", new EntityTag( etagValue5, 16 ).isIn( listOfOne ) );
        assertFalse( "ETag.isIn gives wrong value", new EntityTag().isIn( listOfOne ) );

        assertFalse( "ETag.isIn gives wrong value", new EntityTag( etagValue4, 16 ).isIn( emptyList ) );
        assertFalse( "ETag.isIn gives wrong value", new EntityTag( etagValue5, 16 ).isIn( emptyList ) );
        assertFalse( "ETag.isIn gives wrong value", new EntityTag().isIn( emptyList ) );
    }

    @Test
    public void testNotEmpty() throws EntityTagException
    {
        String etagValue1= "afb990";
        byte[] etagValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue3= "FF";
        Long etagValue4= 255L;
        Integer etagValue5= 255;

        EntityTag etag1= EntityTag.valueOf( etagValue1 );
        EntityTag etag2= EntityTag.valueOf( etagValue2 );
        EntityTag etag3= EntityTag.valueOf( etagValue3 );
        EntityTag etag4= EntityTag.valueOf( etagValue4 );
        EntityTag etag5= EntityTag.valueOf( etagValue5 );

        assertFalse( "ETag should evaluate not empty", etag1.isEmpty() );
        assertFalse( "ETag should evaluate not empty", etag2.isEmpty() );
        assertFalse( "ETag should evaluate not empty", etag3.isEmpty() );
        assertFalse( "ETag should evaluate not empty", etag4.isEmpty() );
        assertFalse( "ETag should evaluate not empty", etag5.isEmpty() );
    }

    @Test
    public void testEmpty() throws EntityTagException
    {
        String etagValue1= "";
        byte[] etagValue2= {};
        String etagValue3= "";
        Long etagValue4= 0L;
        Integer etagValue5= 0;

        EntityTag etag1= EntityTag.valueOf( etagValue1 );
        EntityTag etag2= EntityTag.valueOf( etagValue2 );
        EntityTag etag3= EntityTag.valueOf( etagValue3 );
        EntityTag etag4= EntityTag.valueOf( etagValue4 );
        EntityTag etag5= EntityTag.valueOf( etagValue5 );

        assertTrue( "ETag should evaluate not empty", etag1.isEmpty() );
        assertTrue( "ETag should evaluate not empty", etag2.isEmpty() );
        assertTrue( "ETag should evaluate not empty", etag3.isEmpty() );
        assertTrue( "ETag should evaluate not empty", etag4.isEmpty() );
        assertTrue( "ETag should evaluate not empty", etag5.isEmpty() );
    }

    @Test
    public void testCompareTo() throws EntityTagException
    {
        byte[] etagValue1= { (byte) 0x00 };
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue4= "afb991";
        String etagValue5= "afb99100112233";

        EntityTag etag0= new EntityTag();
        EntityTag etag1= new EntityTag( etagValue1 );
        EntityTag etag2= new EntityTag( etagValue2, 16 );
        EntityTag etag3= new EntityTag( etagValue3 );
        EntityTag etag4= new EntityTag( etagValue4, 16 );
        EntityTag etag5= new EntityTag( etagValue5, 16 );

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
    public void testCompareToInteger() throws EntityTagException
    {
        int etagValue1= 255;
        String etagValue2= "FF";
        int etagValue3= -223423477;
        int etagValue4= 0;

        EntityTag etag1= new EntityTag( etagValue1 );
        EntityTag etag2= new EntityTag( etagValue2, 16 );
        EntityTag etag3= new EntityTag( etagValue3 );
        EntityTag etag4= new EntityTag( etagValue4 );

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
    public void testCompareToLong() throws EntityTagException
    {
        long etagValue1= 255L;
        String etagValue2= "FF";
        long etagValue3= -45623423423423477L;
        long etagValue4= 0L;

        EntityTag etag1= new EntityTag( etagValue1 );
        EntityTag etag2= new EntityTag( etagValue2, 16 );
        EntityTag etag3= new EntityTag( etagValue3 );
        EntityTag etag4= new EntityTag( etagValue4 );

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
    public void testHashCode() throws EntityTagException
    {
        String etagValue0= null;
        byte[] etagValue1= { (byte) 0x00 };
        String etagValue2= "afb990";
        byte[] etagValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String etagValue4= "afb991";
        String etagValue5= "afb99100112233";
        String etagValue6= "00";

        EntityTag etag0= new EntityTag();
        EntityTag etag1= new EntityTag( etagValue1 );
        EntityTag etag2= new EntityTag( etagValue2, 16 );
        EntityTag etag3= new EntityTag( etagValue3 );
        EntityTag etag4= new EntityTag( etagValue4, 16 );
        EntityTag etag5= new EntityTag( etagValue5, 16 );
        EntityTag etag6= new EntityTag( etagValue6 );

        assertEquals( "ETag.hashCode failed to compare to equal etag", etag0.hashCode(), new EntityTag( etagValue0 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag1.hashCode(), new EntityTag( etagValue1 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag2.hashCode(), new EntityTag( etagValue2, 16 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag3.hashCode(), new EntityTag( etagValue3 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag4.hashCode(), new EntityTag( etagValue4, 16 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag5.hashCode(), new EntityTag( etagValue5, 16 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag6.hashCode(), new EntityTag( etagValue6 ).hashCode() );

        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag0.hashCode(), new EntityTag( etagValue4 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag1.hashCode(), new EntityTag( etagValue5, 16 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag2.hashCode(), new EntityTag( etagValue1 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag3.hashCode(), new EntityTag( etagValue2, 16 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag4.hashCode(), new EntityTag( etagValue3 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag5.hashCode(), new EntityTag( etagValue4, 16 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag6.hashCode(), new EntityTag( etagValue3 ).hashCode() );

        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag0.hashCode(), new EntityTag( etagValue3 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag1.hashCode(), new EntityTag( etagValue2, 16 ).hashCode() );
        assertEquals( "ETag.hashCode failed to compare to equal etag", etag2.hashCode(), new EntityTag( etagValue3 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag3.hashCode(), new EntityTag( etagValue4, 16 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag4.hashCode(), new EntityTag( etagValue5, 16 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag5.hashCode(), new EntityTag( etagValue1 ).hashCode() );
        assertNotEquals( "ETag.hashCode failed to compare to unequal etag", etag6.hashCode(), new EntityTag( etagValue1 ).hashCode() );
    }

    @Test
    public void testEquals() throws EntityTagException
    {
        String etagValue1= "ffb990";
        byte[] etagValue2= { (byte) 0xFF, (byte) 0xB9, (byte) 0x90 };
        String etagValue3= "afb991";
        String etagValue4= "afb99100112233";
        byte[] etagValue5= { (byte) 0x00 };

        EntityTag etag0= new EntityTag();
        EntityTag etag1= new EntityTag( etagValue1, 16 );
        EntityTag etag2= new EntityTag( etagValue2 );
        EntityTag etag3= new EntityTag( etagValue3, 16 );
        EntityTag etag4= new EntityTag( etagValue4, 16 );
        EntityTag etag5= new EntityTag( etagValue5 );

        assertEquals( "ETag.equals failed to compare to equal etag", etag0, etag0 );
        assertEquals( "ETag.equals failed to compare to equal etag", etag1, etag1 );
        assertEquals( "ETag.equals failed to compare to equal etag", etag2, etag2 );
        assertEquals( "ETag.equals failed to compare to equal etag", etag3, etag3 );
        assertEquals( "ETag.equals failed to compare to equal etag", etag4, etag4 );
        assertEquals( "ETag.equals failed to compare to equal etag", etag5, etag5 );

        assertEquals( "ETag.equals failed to compare to equal etag", etag0, new EntityTag() );
        assertEquals( "ETag.equals failed to compare to equal etag", etag1, new EntityTag( etagValue1, 16 ) );
        assertEquals( "ETag.equals failed to compare to equal etag", etag2, new EntityTag( etagValue2 ) );
        assertEquals( "ETag.equals failed to compare to equal etag", etag3, new EntityTag( etagValue3, 16 ) );
        assertEquals( "ETag.equals failed to compare to equal etag", etag4, new EntityTag( etagValue4, 16 ) );
        assertEquals( "ETag.equals failed to compare to equal etag", etag5, new EntityTag( etagValue5 ) );

        assertEquals( "ETag.equals failed to compare to equal etag", etag1, etag2 );
        assertEquals( "ETag.equals failed to compare to equal etag", etag2, etag1 );

        assertNotEquals( "ETag.equals failed to compare to unequal etag", etag0, etag1 );
        assertNotEquals( "ETag.equals failed to compare to unequal etag", etag1, etag3 );
        assertNotEquals( "ETag.equals failed to compare to unequal etag", etag3, etag1 );
        assertNotEquals( "ETag.equals failed to compare to unequal etag", etag1, etag4 );
        assertNotEquals( "ETag.equals failed to compare to unequal etag", etag4, etag1 );
        assertNotEquals( "ETag.equals failed to compare to unequal etag", etag5, etag2 );

        assertNotEquals( "ETag.equals failed to compare to null", etag0, null );
        assertNotEquals( "ETag.equals failed to compare to null", etag5, null );

    }

    @Test
    public void testEqualsToWrongClass() throws EntityTagException
    {
        String etagValue1= "1122334455667788";
        EntityTag etag1= new EntityTag( etagValue1, 16 );
        assertNotEquals( "ETag.equals Boolean returned true", etag1, Boolean.FALSE );
    }
}
