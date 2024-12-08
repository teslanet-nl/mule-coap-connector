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


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultBytesValue;


/**
 * Tests the implementation of the DefaultBytesValue class.
 *
 */
public class ByteValueTest
{
    @Test
    public void testConstructorNull() throws OptionValueException
    {
        String bytesValue1= null;
        byte[] bytesValue2= null;
        String bytesValue3= null;
        byte[] bytesValue4= {};
        String bytesValue5= "";
        long bytesValue6= 0L;
        int bytesValue7= 0;

        DefaultBytesValue bytes1= new DefaultBytesValue( bytesValue1 );
        assertArrayEquals( "bytes should be empty", OptionUtils.EMPTY_BYTES, bytes1.getValue() );

        DefaultBytesValue bytes2= new DefaultBytesValue( bytesValue2 );
        assertArrayEquals( "bytes should be empty", OptionUtils.EMPTY_BYTES, bytes2.getValue() );

        DefaultBytesValue bytes3= new DefaultBytesValue( bytesValue3 );
        assertArrayEquals( "bytes should be empty", OptionUtils.EMPTY_BYTES, bytes3.getValue() );

        DefaultBytesValue bytes4= new DefaultBytesValue( bytesValue4 );
        assertArrayEquals( "bytes should be empty", OptionUtils.EMPTY_BYTES, bytes4.getValue() );

        DefaultBytesValue bytes5= new DefaultBytesValue( bytesValue5 );
        assertArrayEquals( "bytes should be empty", OptionUtils.EMPTY_BYTES, bytes5.getValue() );

        DefaultBytesValue bytes6= new DefaultBytesValue( bytesValue6 );
        assertArrayEquals( "bytes should be empty", OptionUtils.EMPTY_BYTES, bytes6.getValue() );

        DefaultBytesValue bytes7= new DefaultBytesValue( bytesValue7 );
        assertArrayEquals( "bytes should be empty", OptionUtils.EMPTY_BYTES, bytes7.getValue() );

        DefaultBytesValue bytes10= new DefaultBytesValue( bytesValue1, 16 );
        assertArrayEquals( "bytes should be empty", OptionUtils.EMPTY_BYTES, bytes10.getValue() );

        DefaultBytesValue bytes11= new DefaultBytesValue( bytesValue5, 16 );
        assertArrayEquals( "bytes should be empty", OptionUtils.EMPTY_BYTES, bytes11.getValue() );
    }

    @Test
    public void testConstructor() throws OptionValueException
    {
        String bytesValue1= "h\u20ACy";
        String bytesValue2= "68e282ac79";
        byte[] bytesValue3= { (byte) 0x68, (byte) 0xe2, (byte) 0x82, (byte) 0xac, (byte) 0x79 };
        Long bytesValue4= 0x68e282ac79L;
        Integer bytesValue5= 0xe282ac79;
        byte[] bytesValue6= { (byte) 0xe2, (byte) 0x82, (byte) 0xac, (byte) 0x79 };

        DefaultBytesValue bytes1= new DefaultBytesValue( bytesValue1 );
        DefaultBytesValue bytes2= new DefaultBytesValue( bytesValue2, 16 );
        DefaultBytesValue bytes3= new DefaultBytesValue( bytesValue3 );
        DefaultBytesValue bytes4= new DefaultBytesValue( bytesValue4 );
        DefaultBytesValue bytes5= new DefaultBytesValue( bytesValue5 );

        assertArrayEquals( "Bytes contruction from String failed", bytesValue3, bytes1.getValue() );
        assertArrayEquals( "Bytes contruction from Hex String failed", bytesValue3, bytes2.getValue() );
        assertArrayEquals( "Bytes contruction from Byte[] failed", bytesValue3, bytes3.getValue() );
        assertArrayEquals( "Bytes contruction from Long failed", bytesValue3, bytes4.getValue() );
        assertArrayEquals( "Bytes contruction from Long failed", bytesValue6, bytes5.getValue() );
    }

    @Test
    public void testConstructorBytesLargeByteArray() throws OptionValueException
    {
        byte[] bytesValue1= new byte [19];
        for ( int i= 0; i < 19; i++ )
        {
            bytesValue1[i]= (byte) i;
        }
        DefaultBytesValue bytes1= new DefaultBytesValue( bytesValue1 );
        assertArrayEquals( "Bytes value should be equal.", bytesValue1, bytes1.getValue() );
    }

    @Test
    public void testConstructorInvalidString() throws OptionValueException
    {
        String bytesValue1= "10aaZZ";
        OptionValueException e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultBytesValue bytes1= new DefaultBytesValue( bytesValue1, 16 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Cannot construct bytes value" ) );
        assertTrue( "exception has wrong message", e.getMessage().contains( bytesValue1 ) );
    }

    @Test
    public void testConstructorUnevenString1() throws OptionValueException
    {
        String bytesValue1= "1";
        DefaultBytesValue bytes1= new DefaultBytesValue( bytesValue1, 16 );
        assertEquals( "exception has wrong message", 1L, bytes1.getValueAsNumber() );
    }

    @Test
    public void testConstructorBytesUnevenString2() throws Exception
    {
        String bytesValue1= "1122334455667";
        DefaultBytesValue bytes1= new DefaultBytesValue( bytesValue1, 16 );
        assertEquals( "exception has wrong message", 0x1122334455667L, bytes1.getValueAsNumber() );
    }

    @Test
    public void testConstructorBytesLargeString() throws OptionValueException
    {
        String bytesValue1= "112233445566778899";
        OptionValueException e= assertThrows( OptionValueException.class, () -> {
            @SuppressWarnings( "unused" )
            DefaultBytesValue bytes1= new DefaultBytesValue( bytesValue1, 16 );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Cannot construct bytes value" ) );
        assertTrue( "exception has wrong message", e.getMessage().contains( bytesValue1 ) );
    }

    @Test
    public void testConstructorBytesLargeString2() throws OptionValueException
    {
        String bytesValue1= "this is not too large to fit into an bytes value";
        DefaultBytesValue bytes1= new DefaultBytesValue( bytesValue1 );
        assertEquals( "String value should be equal.", bytesValue1, bytes1.getValueAsString() );
    }

    @Test
    public void testGetBytes() throws OptionValueException
    {
        byte[] bytesValue00= { 0x00 };
        byte[] bytesValue1= { (byte) 0xFF };
        String bytesValue2= "h\u20ACy";
        String bytesValue3= "68e282ac79";
        byte[] bytesValue4= { (byte) 0x68, (byte) 0xe2, (byte) 0x82, (byte) 0xac, (byte) 0x79 };
        Long bytesValue5= 255L;
        Integer bytesValue6= 255;

        DefaultBytesValue bytes00= new DefaultBytesValue( bytesValue00 );
        DefaultBytesValue bytes1= new DefaultBytesValue( bytesValue1 );
        DefaultBytesValue bytes2= new DefaultBytesValue( bytesValue2 );
        DefaultBytesValue bytes3= new DefaultBytesValue( bytesValue3, 16 );
        DefaultBytesValue bytes4= new DefaultBytesValue( bytesValue4 );
        DefaultBytesValue bytes5= new DefaultBytesValue( bytesValue5 );
        DefaultBytesValue bytes6= new DefaultBytesValue( bytesValue6 );

        assertArrayEquals( "Bytes.asBytes gives wrong value", bytesValue00, bytes00.getValue() );
        assertArrayEquals( "Bytes.asBytes gives wrong value", bytesValue1, bytes1.getValue() );
        assertArrayEquals( "Bytes.asBytes gives wrong value", bytesValue4, bytes2.getValue() );
        assertArrayEquals( "Bytes.asBytes gives wrong value", bytesValue4, bytes3.getValue() );
        assertArrayEquals( "Bytes.asBytes gives wrong value", bytesValue4, bytes4.getValue() );
        assertArrayEquals( "Bytes.asBytes gives wrong value", bytesValue1, bytes5.getValue() );
        assertArrayEquals( "Bytes.asBytes gives wrong value", bytesValue1, bytes6.getValue() );
        assertNotEquals( (Object) bytesValue1, (Object) bytes1.getValue() );
        assertNotEquals( (Object) bytesValue3, (Object) bytes3.getValue() );
    }

    @Test
    public void testGetLong() throws OptionValueException
    {
        byte[] bytesValue1= { (byte) 0x01 };
        byte[] bytesValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] bytesValue3= { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77,
            (byte) 0x88 };
        long hexValue1= 1;
        long hexValue2= 0xafb990L;
        long hexValue3= 0x1122334455667788L;

        assertEquals(
            "Bytes.toHexString gives wrong value",
            hexValue1,
            new DefaultBytesValue( bytesValue1 ).getValueAsNumber()
        );
        assertEquals(
            "Bytes.toHexString gives wrong value",
            hexValue2,
            new DefaultBytesValue( bytesValue2 ).getValueAsNumber()
        );
        assertEquals(
            "Bytes.toHexString gives wrong value",
            hexValue3,
            new DefaultBytesValue( bytesValue3 ).getValueAsNumber()
        );
    }

    @Test
    public void testGetHexString() throws OptionValueException
    {
        byte[] bytesValue1= { (byte) 0x01 };
        byte[] bytesValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] bytesValue3= { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77,
            (byte) 0x88 };
        String hexValue1= "01";
        String hexValue2= "afb990";
        String hexValue3= "1122334455667788";

        assertEquals(
            "Bytes.toHexString gives wrong value",
            hexValue1,
            new DefaultBytesValue( bytesValue1 ).getValueAsHex()
        );
        assertEquals(
            "Bytes.toHexString gives wrong value",
            hexValue2,
            new DefaultBytesValue( bytesValue2 ).getValueAsHex()
        );
        assertEquals(
            "Bytes.toHexString gives wrong value",
            hexValue3,
            new DefaultBytesValue( bytesValue3 ).getValueAsHex()
        );
    }

    @Test
    public void testToString() throws OptionValueException
    {
        byte[] bytesValue1= { (byte) 0x01 };
        byte[] bytesValue2= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        byte[] bytesValue3= { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77,
            (byte) 0x88 };
        String hexValue1= "BytesValue{ 01 }";
        String hexValue2= "BytesValue{ afb990 }";
        String hexValue3= "BytesValue{ 1122334455667788 }";

        assertEquals(
            "Bytes.toHexString gives wrong value",
            hexValue1,
            new DefaultBytesValue( bytesValue1 ).toString()
        );
        assertEquals(
            "Bytes.toHexString gives wrong value",
            hexValue2,
            new DefaultBytesValue( bytesValue2 ).toString()
        );
        assertEquals(
            "Bytes.toHexString gives wrong value",
            hexValue3,
            new DefaultBytesValue( bytesValue3 ).toString()
        );
    }

    @Test
    public void testValueOf() throws OptionValueException
    {
        String bytesValue1= "h\u20ACy";
        String bytesValue2= "68e282ac79";
        byte[] bytesValue3= { (byte) 0x68, (byte) 0xe2, (byte) 0x82, (byte) 0xac, (byte) 0x79 };
        long bytesValue4= 0x68e282ac79L;
        int bytesValue5= 0xe282ac79;
        String bytesValue6= "e282ac79";

        DefaultBytesValue bytes1= DefaultBytesValue.valueOf( bytesValue1 );
        DefaultBytesValue bytes2= DefaultBytesValue.valueOf( bytesValue2, 16 );
        DefaultBytesValue bytes3= DefaultBytesValue.valueOf( bytesValue3 );
        DefaultBytesValue bytes4= DefaultBytesValue.valueOf( bytesValue4 );
        DefaultBytesValue bytes5= DefaultBytesValue.valueOf( bytesValue5 );

        assertEquals( "Bytes contruction from String failed", bytesValue2, bytes1.getValueAsHex() );
        assertEquals( "Bytes contruction from Byte[] failed", bytesValue2, bytes2.getValueAsHex() );
        assertEquals( "Bytes contruction from Byte[] failed", bytesValue2, bytes3.getValueAsHex() );
        assertEquals( "Bytes contruction from Long failed", bytesValue2, bytes4.getValueAsHex() );
        assertEquals( "Bytes contruction from Long failed", bytesValue6, bytes5.getValueAsHex() );
    }

    @Test
    public void testValueOfNull() throws OptionValueException
    {
        String bytesValue1= null;
        byte[] bytesValue2= null;
        String bytesValue3= null;
        long bytesValue4= 0L;
        byte[] bytesValue5= {};
        String bytesValue6= "";
        long bytesValue7= 0L;
        int bytesValue8= 0;
        int bytesValue9= 0;

        DefaultBytesValue bytes1= DefaultBytesValue.valueOf( bytesValue1 );
        assertArrayEquals( "bytes should be empty", OptionUtils.EMPTY_BYTES, bytes1.getValue() );

        DefaultBytesValue bytes2= DefaultBytesValue.valueOf( bytesValue2 );
        assertArrayEquals( "bytes should be empty", OptionUtils.EMPTY_BYTES, bytes2.getValue() );

        DefaultBytesValue bytes3= DefaultBytesValue.valueOf( bytesValue3 );
        assertArrayEquals( "bytes should be empty", OptionUtils.EMPTY_BYTES, bytes3.getValue() );

        DefaultBytesValue bytes4= DefaultBytesValue.valueOf( bytesValue4 );
        assertArrayEquals( "bytes should be empty", OptionUtils.EMPTY_BYTES, bytes4.getValue() );

        DefaultBytesValue bytes5= DefaultBytesValue.valueOf( bytesValue5 );
        assertArrayEquals( "bytes should be empty", OptionUtils.EMPTY_BYTES, bytes5.getValue() );

        DefaultBytesValue bytes6= DefaultBytesValue.valueOf( bytesValue6 );
        assertArrayEquals( "bytes should be empty", OptionUtils.EMPTY_BYTES, bytes6.getValue() );

        DefaultBytesValue bytes7= DefaultBytesValue.valueOf( bytesValue7 );
        assertArrayEquals( "bytes should be empty", OptionUtils.EMPTY_BYTES, bytes7.getValue() );

        DefaultBytesValue bytes8= DefaultBytesValue.valueOf( bytesValue8 );
        assertArrayEquals( "bytes should be empty", OptionUtils.EMPTY_BYTES, bytes8.getValue() );

        DefaultBytesValue bytes9= DefaultBytesValue.valueOf( bytesValue9 );
        assertArrayEquals( "bytes should be empty", OptionUtils.EMPTY_BYTES, bytes9.getValue() );
    }

    @Test
    public void testCompareTo() throws OptionValueException
    {
        byte[] bytesValue1= { (byte) 0x00 };
        String bytesValue2= "afb990";
        byte[] bytesValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String bytesValue4= "afb991";
        String bytesValue5= "afb99100112233";

        DefaultBytesValue bytes1= new DefaultBytesValue( bytesValue1 );
        DefaultBytesValue bytes2= new DefaultBytesValue( bytesValue2, 16 );
        DefaultBytesValue bytes3= new DefaultBytesValue( bytesValue3 );
        DefaultBytesValue bytes4= new DefaultBytesValue( bytesValue4, 16 );
        DefaultBytesValue bytes5= new DefaultBytesValue( bytesValue5, 16 );

        assertEquals( "Bytes.compareTo failed to compare to equal bytes", 0, bytes1.compareTo( bytes1 ) );
        assertEquals( "Bytes.compareTo failed to compare to equal bytes", 0, bytes2.compareTo( bytes2 ) );
        assertEquals( "Bytes.compareTo failed to compare to equal bytes", 0, bytes3.compareTo( bytes3 ) );
        assertEquals( "Bytes.compareTo failed to compare to equal bytes", 0, bytes4.compareTo( bytes4 ) );
        assertEquals( "Bytes.compareTo failed to compare to equal bytes", 0, bytes5.compareTo( bytes5 ) );

        assertEquals( "Bytes.compareTo failed to compare", 1, bytes2.compareTo( bytes1 ) );
        assertEquals( "Bytes.compareTo failed to compare", 0, bytes3.compareTo( bytes2 ) );
        assertEquals( "Bytes.compareTo failed to compare", 1, bytes4.compareTo( bytes3 ) );
        assertEquals( "Bytes.compareTo failed to compare", 1, bytes5.compareTo( bytes4 ) );

        assertEquals( "Bytes.compareTo failed to compare", -1, bytes1.compareTo( bytes2 ) );
        assertEquals( "Bytes.compareTo failed to compare", 0, bytes2.compareTo( bytes3 ) );
        assertEquals( "Bytes.compareTo failed to compare", -1, bytes3.compareTo( bytes4 ) );
        assertEquals( "Bytes.compareTo failed to compare", -1, bytes4.compareTo( bytes5 ) );

        assertEquals( "Bytes.compareTo failed to compare to null", 1, bytes5.compareTo( null ) );
    }

    @Test
    public void testCompareToInteger() throws OptionValueException
    {
        int bytesValue1= 255;
        String bytesValue2= "FF";
        int bytesValue3= -223423477;

        DefaultBytesValue bytes1= new DefaultBytesValue( bytesValue1 );
        DefaultBytesValue bytes2= new DefaultBytesValue( bytesValue2, 16 );
        DefaultBytesValue bytes3= new DefaultBytesValue( bytesValue3 );

        assertEquals( "Bytes.compareTo failed to compare to equal bytes", 0, bytes1.compareTo( bytes1 ) );
        assertEquals( "Bytes.compareTo failed to compare to equal bytes", 0, bytes2.compareTo( bytes2 ) );
        assertEquals( "Bytes.compareTo failed to compare to equal bytes", 0, bytes3.compareTo( bytes3 ) );

        assertEquals( "Bytes.compareTo failed to compare to equal bytes", 0, bytes2.compareTo( bytes1 ) );
        assertEquals( "Bytes.compareTo failed to compare to equal bytes", 1, bytes3.compareTo( bytes2 ) );

        assertEquals( "Bytes.compareTo failed to compare to equal bytes", 0, bytes1.compareTo( bytes2 ) );
        assertEquals( "Bytes.compareTo failed to compare to equal bytes", -1, bytes2.compareTo( bytes3 ) );
    }

    @Test
    public void testCompareToLong() throws OptionValueException
    {
        long bytesValue1= 255L;
        String bytesValue2= "FF";
        long bytesValue3= -45623423423423477L;

        DefaultBytesValue bytes1= new DefaultBytesValue( bytesValue1 );
        DefaultBytesValue bytes2= new DefaultBytesValue( bytesValue2, 16 );
        DefaultBytesValue bytes3= new DefaultBytesValue( bytesValue3 );

        assertEquals( "Bytes.compareTo failed to compare to equal bytes", 0, bytes1.compareTo( bytes1 ) );
        assertEquals( "Bytes.compareTo failed to compare to equal bytes", 0, bytes2.compareTo( bytes2 ) );
        assertEquals( "Bytes.compareTo failed to compare to equal bytes", 0, bytes3.compareTo( bytes3 ) );

        assertEquals( "Bytes.compareTo failed to compare to equal bytes", 0, bytes2.compareTo( bytes1 ) );
        assertEquals( "Bytes.compareTo failed to compare to equal bytes", 1, bytes3.compareTo( bytes2 ) );

        assertEquals( "Bytes.compareTo failed to compare to equal bytes", 0, bytes1.compareTo( bytes2 ) );
        assertEquals( "Bytes.compareTo failed to compare to equal bytes", -1, bytes2.compareTo( bytes3 ) );
    }

    @Test
    public void testHashCode() throws OptionValueException
    {
        byte[] bytesValue1= { (byte) 0x01 };
        String bytesValue2= "afb990";
        byte[] bytesValue3= { (byte) 0xAF, (byte) 0xB9, (byte) 0x90 };
        String bytesValue4= "afb991";
        String bytesValue5= "afb99100112233";
        String bytesValue6= "01";

        DefaultBytesValue bytes1= new DefaultBytesValue( bytesValue1 );
        DefaultBytesValue bytes2= new DefaultBytesValue( bytesValue2, 16 );
        DefaultBytesValue bytes3= new DefaultBytesValue( bytesValue3 );
        DefaultBytesValue bytes4= new DefaultBytesValue( bytesValue4, 16 );
        DefaultBytesValue bytes5= new DefaultBytesValue( bytesValue5, 16 );
        DefaultBytesValue bytes6= new DefaultBytesValue( bytesValue6 );

        assertEquals(
            "Bytes.hashCode failed to compare to equal bytes",
            bytes1.hashCode(),
            new DefaultBytesValue( bytesValue1 ).hashCode()
        );
        assertEquals(
            "Bytes.hashCode failed to compare to equal bytes",
            bytes2.hashCode(),
            new DefaultBytesValue( bytesValue2, 16 ).hashCode()
        );
        assertEquals(
            "Bytes.hashCode failed to compare to equal bytes",
            bytes3.hashCode(),
            new DefaultBytesValue( bytesValue3 ).hashCode()
        );
        assertEquals(
            "Bytes.hashCode failed to compare to equal bytes",
            bytes4.hashCode(),
            new DefaultBytesValue( bytesValue4, 16 ).hashCode()
        );
        assertEquals(
            "Bytes.hashCode failed to compare to equal bytes",
            bytes5.hashCode(),
            new DefaultBytesValue( bytesValue5, 16 ).hashCode()
        );
        assertEquals(
            "Bytes.hashCode failed to compare to equal bytes",
            bytes6.hashCode(),
            new DefaultBytesValue( bytesValue6 ).hashCode()
        );

        assertNotEquals(
            "Bytes.hashCode failed to compare to unequal bytes",
            bytes1.hashCode(),
            new DefaultBytesValue( bytesValue5, 16 ).hashCode()
        );
        assertNotEquals(
            "Bytes.hashCode failed to compare to unequal bytes",
            bytes2.hashCode(),
            new DefaultBytesValue( bytesValue1 ).hashCode()
        );
        assertEquals(
            "Bytes.hashCode failed to compare to equal bytes",
            bytes3.hashCode(),
            new DefaultBytesValue( bytesValue2, 16 ).hashCode()
        );
        assertNotEquals(
            "Bytes.hashCode failed to compare to unequal bytes",
            bytes4.hashCode(),
            new DefaultBytesValue( bytesValue3 ).hashCode()
        );
        assertNotEquals(
            "Bytes.hashCode failed to compare to unequal bytes",
            bytes5.hashCode(),
            new DefaultBytesValue( bytesValue4, 16 ).hashCode()
        );
        assertNotEquals(
            "Bytes.hashCode failed to compare to unequal bytes",
            bytes6.hashCode(),
            new DefaultBytesValue( bytesValue3 ).hashCode()
        );

        assertNotEquals(
            "Bytes.hashCode failed to compare to unequal bytes",
            bytes1.hashCode(),
            new DefaultBytesValue( bytesValue2, 16 ).hashCode()
        );
        assertEquals(
            "Bytes.hashCode failed to compare to equal bytes",
            bytes2.hashCode(),
            new DefaultBytesValue( bytesValue3 ).hashCode()
        );
        assertNotEquals(
            "Bytes.hashCode failed to compare to unequal bytes",
            bytes3.hashCode(),
            new DefaultBytesValue( bytesValue4, 16 ).hashCode()
        );
        assertNotEquals(
            "Bytes.hashCode failed to compare to unequal bytes",
            bytes4.hashCode(),
            new DefaultBytesValue( bytesValue5, 16 ).hashCode()
        );
        assertNotEquals(
            "Bytes.hashCode failed to compare to unequal bytes",
            bytes5.hashCode(),
            new DefaultBytesValue( bytesValue1 ).hashCode()
        );
        assertNotEquals(
            "Bytes.hashCode failed to compare to unequal bytes",
            bytes6.hashCode(),
            new DefaultBytesValue( bytesValue1 ).hashCode()
        );
    }

    @Test
    public void testEquals() throws OptionValueException
    {
        String bytesValue1= "ffb990";
        byte[] bytesValue2= { (byte) 0xFF, (byte) 0xB9, (byte) 0x90 };
        String bytesValue3= "afb991";
        String bytesValue4= "afb99100112233";
        byte[] bytesValue5= { (byte) 0x00 };

        DefaultBytesValue bytes1= new DefaultBytesValue( bytesValue1, 16 );
        DefaultBytesValue bytes2= new DefaultBytesValue( bytesValue2 );
        DefaultBytesValue bytes3= new DefaultBytesValue( bytesValue3, 16 );
        DefaultBytesValue bytes4= new DefaultBytesValue( bytesValue4, 16 );
        DefaultBytesValue bytes5= new DefaultBytesValue( bytesValue5 );

        assertEquals( "Bytes.equals failed to compare to equal bytes", bytes1, bytes1 );
        assertEquals( "Bytes.equals failed to compare to equal bytes", bytes2, bytes2 );
        assertEquals( "Bytes.equals failed to compare to equal bytes", bytes3, bytes3 );
        assertEquals( "Bytes.equals failed to compare to equal bytes", bytes4, bytes4 );
        assertEquals( "Bytes.equals failed to compare to equal bytes", bytes5, bytes5 );

        assertEquals(
            "Bytes.equals failed to compare to equal bytes",
            bytes1,
            new DefaultBytesValue( bytesValue1, 16 )
        );
        assertEquals( "Bytes.equals failed to compare to equal bytes", bytes2, new DefaultBytesValue( bytesValue2 ) );
        assertEquals(
            "Bytes.equals failed to compare to equal bytes",
            bytes3,
            new DefaultBytesValue( bytesValue3, 16 )
        );
        assertEquals(
            "Bytes.equals failed to compare to equal bytes",
            bytes4,
            new DefaultBytesValue( bytesValue4, 16 )
        );
        assertEquals( "Bytes.equals failed to compare to equal bytes", bytes5, new DefaultBytesValue( bytesValue5 ) );

        assertEquals( "Bytes.equals failed to compare to equal bytes", bytes1, bytes2 );
        assertEquals( "Bytes.equals failed to compare to equal bytes", bytes2, bytes1 );

        assertNotEquals( "Bytes.equals failed to compare to unequal bytes", bytes1, bytes3 );
        assertNotEquals( "Bytes.equals failed to compare to unequal bytes", bytes3, bytes1 );
        assertNotEquals( "Bytes.equals failed to compare to unequal bytes", bytes1, bytes4 );
        assertNotEquals( "Bytes.equals failed to compare to unequal bytes", bytes4, bytes1 );
        assertNotEquals( "Bytes.equals failed to compare to unequal bytes", bytes5, bytes2 );

        assertNotEquals( "Bytes.equals failed to compare to null", bytes5, null );
    }

    @Test
    public void testEqualsToWrongClass() throws OptionValueException
    {
        String bytesValue1= "1122334455667788";
        DefaultBytesValue bytes1= new DefaultBytesValue( bytesValue1, 16 );
        assertNotEquals( "Bytes.equals Boolean returned true", bytes1, Boolean.FALSE );
    }
}
