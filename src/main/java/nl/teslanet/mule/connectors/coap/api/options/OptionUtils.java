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
package nl.teslanet.mule.connectors.coap.api.options;


import java.io.ByteArrayInputStream;
import java.io.InputStream;

import nl.teslanet.mule.connectors.coap.api.Defs;


/**
 * Utilities for handling CoAP options. 
 *
 */
public class OptionUtils
{
    /**
     * Empty byte array.
     */
    public static final byte[] EMPTY_BYTES= new byte [0];

    /**
     * Empty byte array.
     */
    public static final String EMPTY_STRING= "";

    /**
     * Length exception message format.
     */
    private static final String LENGTH_ERROR_FORMAT= "Given value result in array length {%d}, which is not between {%d}..{%d} bytes.";

    /**
     * private constructor
     */
    private OptionUtils()
    {
        //NOOP
    }

    /**
     * Checks if option with this number is critical.
     *
     * @return {@code true} if is option critical
     */
    public static boolean isCritical( int optionNumber )
    {
        return ( optionNumber & 1 ) != 0;
    }

    /**
     * Checks if option with this number is a NoCacheKey.
     *
     * @return  {@code true} if is NoCacheKey
     */
    public static boolean isNoCacheKey( int optionNumber )
    {
        return ( optionNumber & 0x1E ) == 0x1C;
    }

    /**
     * Checks if option with this number is unsafe.
     *
     * @return  {@code true}  if is unsafe
     */
    public static boolean isUnsafe( int optionNumber )
    {
        return ( optionNumber & 2 ) != 0;
    }

    /**
     * Convert an integer value to byte array. 
     * The resulting bits conforming to the CoAP defined uint format for integer option values, 
     * where leading zero bytes should be omitted.
     * 
     * @see <a href=
     *      "https://www.rfc-editor.org/rfc/rfc7252.html#section-3.2">IETF RFC 7252 -
     *      3.2. Option Value Formats</a>
     *      
     * @param value The integer value to convert.
     * @return The converted value as bytes. 
     */
    public static byte[] toBytes( int value )
    {
        int length= 0;
        for ( ; length < Integer.BYTES && ( value < 0 || value >= ( 1 << ( length * Byte.SIZE ) ) ); length++ );
        byte[] result= new byte [length];
        for ( int i= 0; i < length; i++ )
        {
            result[length - i - 1]= (byte) ( value >> i * 8 );
        }
        return result;
    }

    /**
     * Convert an integer value to byte array. 
     * The resulting bits conforming to the CoAP defined uint format for integer option values, 
     * where leading zero bytes should be omitted.
     * 
     * @see <a href=
     *      "https://www.rfc-editor.org/rfc/rfc7252.html#section-3.2">IETF RFC 7252 -
     *      3.2. Option Value Formats</a>
     *      
     * @param value The integer value to convert.
     * @param minLength The minimum allowed length of the resulting byte array.
     * @param maxLength The maximum allowed length of the resulting byte array.
     * @return The converted value as bytes. 
     * @throws OptionValueException When value cannot be converted to bytes.
     */
    public static byte[] toBytes( int value, int minLength, int maxLength ) throws OptionValueException
    {
        byte[] result= toBytes( value );
        int length= result.length;
        if ( length < minLength || length > maxLength )
        {
            throw new OptionValueException( String.format( LENGTH_ERROR_FORMAT, length, minLength, maxLength ) );
        }
        return result;
    }

    /**
     * Convert a long value to byte array. 
     * The resulting bits conforming to the CoAP defined uint format for integer option values, 
     * where leading zero bytes should be omitted.
     * 
     * @see <a href=
     *      "https://www.rfc-editor.org/rfc/rfc7252.html#section-3.2">IETF RFC 7252 -
     *      3.2. Option Value Formats</a>
     *      
     * @param value The long value to convert.
     * @return The converted value as bytes. 
     */
    public static byte[] toBytes( long value )
    {
        int length= 0;
        for ( ; length < Long.BYTES && ( value < 0 || value >= ( 1L << ( length * Byte.SIZE ) ) ); length++ );
        byte[] result= new byte [length];
        for ( int i= 0; i < length; i++ )
        {
            result[length - i - 1]= (byte) ( value >> i * 8 );
        }
        return result;
    }

    /**
     * Convert a long value to byte array. 
     * The resulting bits conforming to the CoAP defined uint format for integer option values, 
     * where leading zero bytes should be omitted.
     * 
     * @see <a href=
     *      "https://www.rfc-editor.org/rfc/rfc7252.html#section-3.2">IETF RFC 7252 -
     *      3.2. Option Value Formats</a>
     *      
     * @param value The long value to convert.
     * @param minLength The minimum allowed length of the resulting byte array.
     * @param maxLength The maximum allowed length of the resulting byte array.
     * @return The converted value as bytes. 
     * @throws OptionValueException When value cannot be converted to bytes.
     */
    public static byte[] toBytes( long value, int minLength, int maxLength ) throws OptionValueException
    {
        byte[] result= toBytes( value );
        int length= result.length;
        if ( length < minLength || length > maxLength )
        {
            throw new OptionValueException( String.format( LENGTH_ERROR_FORMAT, length, minLength, maxLength ) );
        }
        return result;
    }

    /**
     * Converts a string representation of an option to byte array.
     * The bytes are written using UTF-8 encoding.
     * @param string The string representing the bytes value.
     * @return The resulting byte array. When given String is empty the length of the byte array will be zero.
     * @throws OptionValueException When String does not contain a convertible hexadecimal value or is exceeding maximum length.
     */
    public static byte[] toBytes( String string ) throws OptionValueException
    {
        return toBytes( string, 0, Integer.MAX_VALUE );
    }

    /**
     * Converts a string representation of an option to byte array.
     * The bytes are written using UTF-8 encoding.
     * @param string The string representing the bytes value.
     * @param minLength The minimum allowed length of the resulting byte array.
     * @param maxLength The maximum allowed length of the resulting byte array.
     * @return The resulting byte array. When given String is empty the length of the byte array will be zero.
     * @throws OptionValueException When String does not contain a convertible hexadecimal value or is exceeding maximum length.
     */
    public static byte[] toBytes( String string, int minLength, int maxLength ) throws OptionValueException
    {
        byte[] bytes= OptionUtils.EMPTY_BYTES;
        if ( string == null || string.isEmpty() )
        {
            if ( minLength > 0 )
            {
                throw new OptionValueException( "Given string is null or empty." );
            }
        }
        else
        {
            bytes= string.getBytes( Defs.COAP_CHARSET );
        }
        if ( bytes.length < minLength || bytes.length > maxLength )
        {
            throw new OptionValueException( String.format( LENGTH_ERROR_FORMAT, bytes.length, minLength, maxLength ) );
        }
        return bytes;
    }

    /**
     * Converts a hexadecimal representation of an option to byte array.
     * @param hexString The string representing the bytes value.
     * @return The resulting byte array. When given String is empty the length of the byte array will be zero.
     * @throws OptionValueException When String does not contain a convertible hexadecimal value or is exceeding maximum length.
     */
    public static byte[] toBytesFromHex( String hexString ) throws OptionValueException
    {
        return toBytesFromHex( hexString, 0, Integer.MAX_VALUE );
    }

    /**
     * Converts a hexadecimal representation of an option to byte array.
     * @param hexString The string representing the bytes value.
     * @param minLength The minimum allowed length of the resulting byte array.
     * @param maxLength The maximum allowed length of the resulting byte array.
     * @return The resulting byte array. When given String is empty the length of the byte array will be zero.
     * @throws OptionValueException When String does not contain a convertible hexadecimal value or is exceeding maximum length.
     */
    public static byte[] toBytesFromHex( String hexString, int minLength, int maxLength ) throws OptionValueException
    {
        if ( hexString == null || hexString.isEmpty() )
        {
            if ( minLength > 0 )
            {
                throw new OptionValueException( "Given string is null or empty." );
            }
            else
            {
                return OptionUtils.EMPTY_BYTES;
            }
        }
        int length= hexString.length() / 2;
        if ( length * 2 != hexString.length() )
        {
            throw new OptionValueException( String.format( "Given hexString must have even number of characters. Actual number is {%d}.", hexString.length() ) );
        }
        if ( length < minLength || length > maxLength )
        {
            throw new OptionValueException( String.format( LENGTH_ERROR_FORMAT, length, minLength, maxLength ) );
        }
        byte[] bytes= new byte [length];
        try
        {
            for ( int i= 0; i < bytes.length; i++ )
            {
                int index= i * 2;
                int v= Integer.parseInt( hexString.substring( index, index + 2 ).toLowerCase(), 16 );
                bytes[i]= (byte) v;
            }
        }
        catch ( NumberFormatException e )
        {
            throw new OptionValueException( "Cannot parse given value as hexadecimal." );
        }
        return bytes;
    }

    /**
     * Converts an etag value to a String containing the hexadecimal representation.
     * Hexadecimal values a-f will be lower case.
     * @param bytes The option value.
     * @return The string containing the hexadecimal representation.
     */
    public static String toHexString( byte[] bytes )
    {
        if ( bytes == null || bytes.length <= 0 )
        {
            return EMPTY_STRING;
        }
        StringBuilder sb= new StringBuilder();
        for ( byte b : bytes )
        {
            sb.append( String.format( "%02x", b & 0xFF ) );
        }
        return sb.toString();
    }

    /**
     * Warps bytes into an InputStream.
     * @param bytes The bytes to expose as inputstream.
     * @return The inputStream.
     */
    public static InputStream toInputStream( byte[] bytes )
    {
        if ( bytes == null || bytes.length <= 0 )
        {
            return new ByteArrayInputStream( EMPTY_BYTES );
        }
        else
        {
            return new ByteArrayInputStream( bytes );
        }
    }

    /**
     * Converts bytes to long. The size of the array must not exceed Long.BYTES.
     * @param bytes The etag value.
     * @return The string containing the hexadecimal representation.
     */
    public static long toLong( byte[] bytes )
    {
        long result= 0L;
        if ( bytes != null && bytes.length > 0 )
        {
            if ( bytes.length > Long.BYTES ) throw new NumberFormatException( "byte array size too large for long" );
            for ( int i= 0; i < bytes.length; i++ )
            {
                result|= ( ( bytes[bytes.length - i - 1] ) & 0xFFL ) << i * 8;
            }
        }
        return result;
    }

    /**
     * Converts an option value interpreted as UTF-8 string.
     * @param bytes The option value.
     * @return The UTF-8 string interpretation.
     */
    public static String toString( byte[] bytes )
    {
        if ( bytes == null || bytes.length == 0 )
        {
            return OptionUtils.EMPTY_STRING;
        }
        else
        {
            return new String( bytes, Defs.COAP_CHARSET );
        }
    }
}
