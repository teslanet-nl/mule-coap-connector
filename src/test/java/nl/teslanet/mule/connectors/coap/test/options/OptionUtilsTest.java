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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.eclipse.californium.core.coap.option.StandardOptionRegistry;
import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;
import org.mule.runtime.api.util.IOUtils;

import nl.teslanet.mule.connectors.coap.api.entity.EntityTagException;
import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;


/**
 * Tests the option utils.
 *
 */
public class OptionUtilsTest
{
    @Test
    public void testIsCriticalOption()
    {
        assertTrue( "isCritical( Uri-Path ) failed", OptionUtils.isCritical( StandardOptionRegistry.URI_PATH.getNumber() ) );
        assertFalse( "isCritical( Request-Tag ) failed", OptionUtils.isCritical( StandardOptionRegistry.REQUEST_TAG.getNumber() ) );
    }

    @Test
    public void testIsUnsafeOption()
    {
        assertTrue( "isUnsafe( Uri-Host ) failed", OptionUtils.isUnsafe( StandardOptionRegistry.URI_HOST.getNumber() ) );
        assertFalse( "isUnsafe( Accept ) failed", OptionUtils.isUnsafe( StandardOptionRegistry.ACCEPT.getNumber() ) );
    }

    @Test
    public void testIsNoCacheKeyOption()
    {
        assertTrue( "isNoCacheKey( Size1 ) failed", OptionUtils.isNoCacheKey( StandardOptionRegistry.SIZE1.getNumber() ) );
        assertFalse( "isNoCacheKey( ETag ) failed", OptionUtils.isNoCacheKey( StandardOptionRegistry.ETAG.getNumber() ) );
    }

    @Test
    public void testFromIntToBytes()
    {
        int nullValue= 0;
        int smallValue= 1;
        int mediumValue= 486;
        int largeValue= Integer.MAX_VALUE;
        int negativeValue= Integer.MIN_VALUE;

        byte[] nullExpected= {};
        byte[] smallExpected= { (byte) 0x01 };
        byte[] mediumExpected= { (byte) 0x01, (byte) 0xe6 };
        byte[] largeExpected= { (byte) 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff };
        byte[] negativeExpected= { (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

        assertArrayEquals( "from int to bytes failed", nullExpected, OptionUtils.toBytes( nullValue ) );
        assertArrayEquals( "from int to bytes failed", smallExpected, OptionUtils.toBytes( smallValue ) );
        assertArrayEquals( "from int to bytes failed", mediumExpected, OptionUtils.toBytes( mediumValue ) );
        assertArrayEquals( "from int to bytes failed", largeExpected, OptionUtils.toBytes( largeValue ) );
        assertArrayEquals( "from int to bytes failed", negativeExpected, OptionUtils.toBytes( negativeValue ) );
    }

    @Test
    public void testFromIntToSizedBytes() throws ArrayComparisonFailure, OptionValueException
    {
        int minLength= 2;
        int maxLength= 3;

        int nullValue= 0;
        int smallValue= 1;
        int mediumValue= 486;
        int largeValue= Integer.MAX_VALUE;
        int negativeValue= Integer.MIN_VALUE;

        byte[] mediumExpected= { (byte) 0x01, (byte) 0xe6 };

        OptionValueException e;

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytes( nullValue, minLength, maxLength ) );
        assertEquals( "exception has wrong message", "Given value result in array length {0}, which is not between {2}..{3} bytes.", e.getMessage() );

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytes( smallValue, minLength, maxLength ) );
        assertEquals( "exception has wrong message", "Given value result in array length {1}, which is not between {2}..{3} bytes.", e.getMessage() );

        assertArrayEquals( "from int to bytes failed", mediumExpected, OptionUtils.toBytes( mediumValue, minLength, maxLength ) );

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytes( largeValue, minLength, maxLength ) );
        assertEquals( "exception has wrong message", "Given value result in array length {4}, which is not between {2}..{3} bytes.", e.getMessage() );

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytes( negativeValue, minLength, maxLength ) );
        assertEquals( "exception has wrong message", "Given value result in array length {4}, which is not between {2}..{3} bytes.", e.getMessage() );
    }

    @Test
    public void testFromLongToBytes()
    {
        long nullValue= 0;
        long smallValue= 1;
        long mediumValue= 486L;
        long largeValue= Long.MAX_VALUE;
        long negativeValue= Long.MIN_VALUE;

        byte[] nullExpected= {};
        byte[] smallExpected= { (byte) 0x01 };
        byte[] mediumExpected= { (byte) 0x01, (byte) 0xe6 };
        byte[] largeExpected= { (byte) 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };
        byte[] negativeExpected= { (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

        assertArrayEquals( "from long to bytes failed", nullExpected, OptionUtils.toBytes( nullValue ) );
        assertArrayEquals( "from long to bytes failed", smallExpected, OptionUtils.toBytes( smallValue ) );
        assertArrayEquals( "from long to bytes failed", mediumExpected, OptionUtils.toBytes( mediumValue ) );
        assertArrayEquals( "from long to bytes failed", largeExpected, OptionUtils.toBytes( largeValue ) );
        assertArrayEquals( "from long to bytes failed", negativeExpected, OptionUtils.toBytes( negativeValue ) );
    }

    @Test
    public void testFromLongToSizedBytes() throws ArrayComparisonFailure, OptionValueException
    {
        int minLength= 2;
        int maxLength= 3;

        long nullValue= 0;
        long smallValue= 1;
        long mediumValue= 486L;
        long largeValue= Long.MAX_VALUE;
        long negativeValue= Long.MIN_VALUE;

        byte[] mediumExpected= { (byte) 0x01, (byte) 0xe6 };

        OptionValueException e;

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytes( nullValue, minLength, maxLength ) );
        assertEquals( "exception has wrong message", "Given value result in array length {0}, which is not between {2}..{3} bytes.", e.getMessage() );

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytes( smallValue, minLength, maxLength ) );
        assertEquals( "exception has wrong message", "Given value result in array length {1}, which is not between {2}..{3} bytes.", e.getMessage() );

        assertArrayEquals( "from int to bytes failed", mediumExpected, OptionUtils.toBytes( mediumValue, minLength, maxLength ) );

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytes( largeValue, minLength, maxLength ) );
        assertEquals( "exception has wrong message", "Given value result in array length {8}, which is not between {2}..{3} bytes.", e.getMessage() );

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytes( negativeValue, minLength, maxLength ) );
        assertEquals( "exception has wrong message", "Given value result in array length {8}, which is not between {2}..{3} bytes.", e.getMessage() );
    }

    @Test
    public void testFromStringToBytes() throws ArrayComparisonFailure, OptionValueException
    {
        String nullValue= null;
        String emptyValue= OptionUtils.EMPTY_STRING;
        String smallValue= "e";
        String mediumValue= "486L";
        String largeValue= "this is a lóng string";

        byte[] emptyExpected= {};
        byte[] smallExpected= { (byte) 0x65 };
        byte[] mediumExpected= { (byte) 0x34, (byte) 0x38, (byte) 0x36, (byte) 0x4c };
        byte[] largeExpected= {
            (byte) 0x74,
            (byte) 0x68,
            (byte) 0x69,
            (byte) 0x73,
            (byte) 0x20,
            (byte) 0x69,
            (byte) 0x73,
            (byte) 0x20,
            (byte) 0x61,
            (byte) 0x20,
            (byte) 0x6c,
            (byte) 0xc3,
            (byte) 0xb3,
            (byte) 0x6e,
            (byte) 0x67,
            (byte) 0x20,
            (byte) 0x73,
            (byte) 0x74,
            (byte) 0x72,
            (byte) 0x69,
            (byte) 0x6e,
            (byte) 0x67 };

        assertArrayEquals( "from string to bytes failed", emptyExpected, OptionUtils.toBytes( nullValue ) );
        assertArrayEquals( "from string to bytes failed", emptyExpected, OptionUtils.toBytes( emptyValue ) );
        assertArrayEquals( "from string to bytes failed", smallExpected, OptionUtils.toBytes( smallValue ) );
        assertArrayEquals( "from string to bytes failed", mediumExpected, OptionUtils.toBytes( mediumValue ) );
        assertArrayEquals( "from string to bytes failed", largeExpected, OptionUtils.toBytes( largeValue ) );
    }

    @Test
    public void testFromStringToSizedBytes() throws ArrayComparisonFailure, OptionValueException
    {
        int minLength= 2;
        int maxLength= 4;

        String nullValue= null;
        String emptyValue= OptionUtils.EMPTY_STRING;
        String smallValue= "e";
        String mediumValue= "486L";
        String largeValue= "this is a lóng string";

        byte[] mediumExpected= { (byte) 0x34, (byte) 0x38, (byte) 0x36, (byte) 0x4c };

        OptionValueException e;

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytes( nullValue, minLength, maxLength ) );
        assertEquals( "exception has wrong message", "Given string is null or empty.", e.getMessage() );

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytes( emptyValue, minLength, maxLength ) );
        assertEquals( "exception has wrong message", "Given string is null or empty.", e.getMessage() );

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytes( smallValue, minLength, maxLength ) );
        assertEquals( "exception has wrong message", "Given value result in array length {1}, which is not between {2}..{4} bytes.", e.getMessage() );

        assertArrayEquals( "from int to bytes failed", mediumExpected, OptionUtils.toBytes( mediumValue, minLength, maxLength ) );

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytes( largeValue, minLength, maxLength ) );
        assertEquals( "exception has wrong message", "Given value result in array length {22}, which is not between {2}..{4} bytes.", e.getMessage() );
    }

    @Test
    public void testFromHexStringToBytes() throws ArrayComparisonFailure, OptionValueException
    {
        String nullValue= null;
        String emptyValue= OptionUtils.EMPTY_STRING;
        String smallValue= "ef";
        String mediumValue= "486a99ff";
        String largeValue= "486a99ff486a99ff486a99ff486a99ff";
        String unevenValue= "ef1";
        String wrongValue= "ef1x";

        byte[] emptyExpected= {};
        byte[] smallExpected= { (byte) 0xef };
        byte[] mediumExpected= { (byte) 0x48, (byte) 0x6a, (byte) 0x99, (byte) 0xff };
        byte[] largeExpected= {
            (byte) 0x48,
            (byte) 0x6a,
            (byte) 0x99,
            (byte) 0xff,
            (byte) 0x48,
            (byte) 0x6a,
            (byte) 0x99,
            (byte) 0xff,
            (byte) 0x48,
            (byte) 0x6a,
            (byte) 0x99,
            (byte) 0xff,
            (byte) 0x48,
            (byte) 0x6a,
            (byte) 0x99,
            (byte) 0xff };

        assertArrayEquals( "from string to bytes failed", emptyExpected, OptionUtils.toBytesFromHex( nullValue ) );
        assertArrayEquals( "from string to bytes failed", emptyExpected, OptionUtils.toBytesFromHex( emptyValue ) );
        assertArrayEquals( "from string to bytes failed", smallExpected, OptionUtils.toBytesFromHex( smallValue ) );
        assertArrayEquals( "from string to bytes failed", mediumExpected, OptionUtils.toBytesFromHex( mediumValue ) );
        assertArrayEquals( "from string to bytes failed", largeExpected, OptionUtils.toBytesFromHex( largeValue ) );

        OptionValueException e;

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytesFromHex( unevenValue ) );
        assertEquals( "exception has wrong message", "Given hexString must have even number of characters. Actual number is {3}.", e.getMessage() );

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytesFromHex( wrongValue ) );
        assertEquals( "exception has wrong message", "Cannot parse given value as hexadecimal.", e.getMessage() );
    }

    @Test
    public void testFromHexStringToSizedBytes() throws ArrayComparisonFailure, OptionValueException
    {
        int minLength= 2;
        int maxLength= 4;

        String nullValue= null;
        String emptyValue= OptionUtils.EMPTY_STRING;
        String smallValue= "ef";
        String mediumValue= "486a99ff";
        String largeValue= "486a99ff486a99ff486a99ff486a99ff";
        String unevenValue= "ef1";
        String wrongValue= "ef1x";

        byte[] mediumExpected= { (byte) 0x48, (byte) 0x6a, (byte) 0x99, (byte) 0xff };

        OptionValueException e;

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytesFromHex( nullValue, minLength, maxLength ) );
        assertEquals( "exception has wrong message", "Given string is null or empty.", e.getMessage() );

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytesFromHex( emptyValue, minLength, maxLength ) );
        assertEquals( "exception has wrong message", "Given string is null or empty.", e.getMessage() );

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytesFromHex( smallValue, minLength, maxLength ) );
        assertEquals( "exception has wrong message", "Given value result in array length {1}, which is not between {2}..{4} bytes.", e.getMessage() );

        assertArrayEquals( "from int to bytes failed", mediumExpected, OptionUtils.toBytesFromHex( mediumValue, minLength, maxLength ) );

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytesFromHex( largeValue, minLength, maxLength ) );
        assertEquals( "exception has wrong message", "Given value result in array length {16}, which is not between {2}..{4} bytes.", e.getMessage() );

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytesFromHex( unevenValue ) );
        assertEquals( "exception has wrong message", "Given hexString must have even number of characters. Actual number is {3}.", e.getMessage() );

        e= assertThrows( OptionValueException.class, () -> OptionUtils.toBytesFromHex( wrongValue ) );
        assertEquals( "exception has wrong message", "Cannot parse given value as hexadecimal.", e.getMessage() );
    }

    @Test
    public void testFromBytesToHexString() throws ArrayComparisonFailure, OptionValueException
    {
        byte[] nullValue= null;
        byte[] emptyValue= {};
        byte[] smallValue= { (byte) 0xef };
        byte[] mediumValue= { (byte) 0x48, (byte) 0x6a, (byte) 0x99, (byte) 0xff };
        byte[] largeValue= {
            (byte) 0x48,
            (byte) 0x6a,
            (byte) 0x99,
            (byte) 0xff,
            (byte) 0x48,
            (byte) 0x6a,
            (byte) 0x99,
            (byte) 0xff,
            (byte) 0x48,
            (byte) 0x6a,
            (byte) 0x99,
            (byte) 0xff,
            (byte) 0x48,
            (byte) 0x6a,
            (byte) 0x99,
            (byte) 0xff };

        String emptyExpected= OptionUtils.EMPTY_STRING;
        String smallExpected= "ef";
        String mediumExpected= "486a99ff";
        String largeExpected= "486a99ff486a99ff486a99ff486a99ff";

        assertEquals( "from bytes to hexstring failed", emptyExpected, OptionUtils.toHexString( nullValue ) );
        assertEquals( "from bytes to hexstring failed", emptyExpected, OptionUtils.toHexString( emptyValue ) );
        assertEquals( "from bytes to hexstring failed", smallExpected, OptionUtils.toHexString( smallValue ) );
        assertEquals( "from bytes to hexstring failed", mediumExpected, OptionUtils.toHexString( mediumValue ) );
        assertEquals( "from bytes to hexstring failed", largeExpected, OptionUtils.toHexString( largeValue ) );
    }

    @Test
    public void testFromBytesToInputStream() throws ArrayComparisonFailure, OptionValueException
    {
        byte[] nullValue= null;
        byte[] emptyValue= {};
        byte[] smallValue= { (byte) 0xef };
        byte[] mediumValue= { (byte) 0x48, (byte) 0x6a, (byte) 0x99, (byte) 0xff };
        byte[] largeValue= {
            (byte) 0x48,
            (byte) 0x6a,
            (byte) 0x99,
            (byte) 0xff,
            (byte) 0x48,
            (byte) 0x6a,
            (byte) 0x99,
            (byte) 0xff,
            (byte) 0x48,
            (byte) 0x6a,
            (byte) 0x99,
            (byte) 0xff,
            (byte) 0x48,
            (byte) 0x6a,
            (byte) 0x99,
            (byte) 0xff };

        assertArrayEquals( "from bytes to hexstring failed", emptyValue, IOUtils.toByteArray( OptionUtils.toInputStream( nullValue ) ) );
        assertArrayEquals( "from bytes to hexstring failed", emptyValue, IOUtils.toByteArray( OptionUtils.toInputStream( emptyValue ) ) );
        assertArrayEquals( "from bytes to hexstring failed", smallValue, IOUtils.toByteArray( OptionUtils.toInputStream( smallValue ) ) );
        assertArrayEquals( "from bytes to hexstring failed", mediumValue, IOUtils.toByteArray( OptionUtils.toInputStream( mediumValue ) ) );
        assertArrayEquals( "from bytes to hexstring failed", largeValue, IOUtils.toByteArray( OptionUtils.toInputStream( largeValue ) ) );
    }

    @Test
    public void testFromBytesToLong() throws ArrayComparisonFailure, OptionValueException
    {
        byte[] nullValue= null;
        byte[] emptyValue= {};
        byte[] smallValue= { (byte) 0xef };
        byte[] mediumValue= { (byte) 0x48, (byte) 0x6a, (byte) 0x99, (byte) 0xff };
        byte[] largeValue= {
            (byte) 0x48,
            (byte) 0x6a,
            (byte) 0x99,
            (byte) 0xff,
            (byte) 0x48,
            (byte) 0x6a,
            (byte) 0x99,
            (byte) 0xff,
            (byte) 0x48,
            (byte) 0x6a,
            (byte) 0x99,
            (byte) 0xff,
            (byte) 0x48,
            (byte) 0x6a,
            (byte) 0x99,
            (byte) 0xff };

        long emptyExpected= 0L;
        long smallExpected= 239L;
        long mediumExpected= 1214945791L;

        assertEquals( "from bytes to hexstring failed", emptyExpected, OptionUtils.toLong( nullValue ) );
        assertEquals( "from bytes to hexstring failed", emptyExpected, OptionUtils.toLong( emptyValue ) );
        assertEquals( "from bytes to hexstring failed", smallExpected, OptionUtils.toLong( smallValue ) );
        assertEquals( "from bytes to hexstring failed", mediumExpected, OptionUtils.toLong( mediumValue ) );

        NumberFormatException e;

        e= assertThrows( NumberFormatException.class, () -> OptionUtils.toLong( largeValue ) );
        assertEquals( "exception has wrong message", "byte array size too large for long", e.getMessage() );
    }

    @Test
    public void testToString() throws EntityTagException
    {
        byte[] nullValue= null;
        byte[] emptyValue= {};
        byte[] smallValue= { (byte) 0x65 };
        byte[] mediumValue= { (byte) 0x34, (byte) 0x38, (byte) 0x36, (byte) 0x4c };
        byte[] largeValue= {
            (byte) 0x74,
            (byte) 0x68,
            (byte) 0x69,
            (byte) 0x73,
            (byte) 0x20,
            (byte) 0x69,
            (byte) 0x73,
            (byte) 0x20,
            (byte) 0x61,
            (byte) 0x20,
            (byte) 0x6c,
            (byte) 0xc3,
            (byte) 0xb3,
            (byte) 0x6e,
            (byte) 0x67,
            (byte) 0x20,
            (byte) 0x73,
            (byte) 0x74,
            (byte) 0x72,
            (byte) 0x69,
            (byte) 0x6e,
            (byte) 0x67 };

        String emptyExpected= OptionUtils.EMPTY_STRING;
        String smallExpected= "e";
        String mediumExpected= "486L";
        String largeExpected= "this is a lóng string";

        assertEquals( "from bytes to string failed", emptyExpected, OptionUtils.toString( nullValue ) );
        assertEquals( "from bytes to string failed", emptyExpected, OptionUtils.toString( emptyValue ) );
        assertEquals( "from bytes to string failed", smallExpected, OptionUtils.toString( smallValue ) );
        assertEquals( "from bytes to string failed", mediumExpected, OptionUtils.toString( mediumValue ) );
        assertEquals( "from bytes to string failed", largeExpected, OptionUtils.toString( largeValue ) );
    }
}
