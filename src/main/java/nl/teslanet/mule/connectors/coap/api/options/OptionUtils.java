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
package nl.teslanet.mule.connectors.coap.api.options;


import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;


/**
 * Utilities for handling CoAP options. 
 *
 */
public class OptionUtils
{
    /**
     * Empty byte array.
     */
    public static final byte[] emptyBytes= new byte [0];

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
     * Checks if option with this number is unsafe.
     *
     * @return  {@code true}  if is unsafe
     */
    public static boolean isUnSafe( int optionNumber )
    {
        return ( optionNumber & 2 ) != 0;
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
        for ( ; length < 4 && ( value < 0 || value >= ( 1 << ( length * 8 ) ) ); length++ );
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
     * @return The converted value as bytes. 
     */
    public static byte[] toBytes( long value )
    {
        int length= 0;
        for ( ; length < 8 && ( value < 0 || value >= ( 1L << ( length * 8 ) ) ); length++ );
        byte[] result= new byte [length];
        for ( int i= 0; i < length; i++ )
        {
            result[length - i - 1]= (byte) ( value >> i * 8 );
        }
        return result;
    }

    /**
     * Converts an etag value to a String containing the hexadecimal representation.
     * Hexadecimal values a-f will be lower case.
     * @param bytes The etag value.
     * @return The string containing the hexadecimal representation.
     */
    public static String toHexString( byte[] bytes )
    {
        if ( bytes == null || bytes.length <= 0 )
        {
            return new String();
        }
        StringBuilder sb= new StringBuilder();
        for ( byte b : bytes )
        {
            sb.append( String.format( "%02x", b & 0xFF ) );
        }
        return sb.toString();
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
     * Converts an etag value to a Long containing the hexadecimal representation.
     * Hexadecimal values a-f will be lower case.
     * @param longValue The etag value.
     * @return The string containing the hexadecimal representation.
     */
    public static byte[] toBytes( Long longValue )
    {
        if ( longValue == null )
        {
            return OptionUtils.emptyBytes;
        }
        return toBytes( (long) longValue );
    }

    /**
     * Converts a hexadecimal representation of an etag to byte array.
     * @param hexString representing the etag value. When empty a null value is returned. 
     * @return the byte array or null when given String is null.
     * @throws InvalidETagException when String does not contain a convertible etag value.
     */
    public static byte[] toBytes( String hexString ) throws InvalidETagException
    {
        if ( hexString == null || hexString.length() <= 0 )
        {
            return OptionUtils.emptyBytes;
        }
        else
        {
            int length= hexString.length() / 2;
            if ( length * 2 != hexString.length() )
            {
                throw new InvalidETagException( "Given hexString must have even number of characters. The number found: " + hexString.length() );
            }
            if ( length > 8 )
            {
                throw new InvalidETagException( "ETag length invalid, must be between 0..8 bytes. Given length is: " + length );
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
                throw new InvalidETagException( "Cannot parse ETag value as hexadecimal: " + hexString );
            }
            return bytes;
        }
    }
}
