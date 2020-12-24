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
package nl.teslanet.mule.connectors.coap.api.options;


import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;


/**
 * Implementation of the Etag concept for convenience.
 * It eases the handling of Etag values in Mule flows. 
 * Etags can be constructed from long, byte array as well as from strings that 
 * contain a hexadecimal representation. 
 * An etag object is immutable and comparable to other etags.
 */
public final class ETag implements Comparable< ETag >
{
    /**
     * The etag value
     */
    private final byte[] value;

    private Integer hashCode= null;

    /**
     * Default constructor that create empty etag.
     */
    public ETag()
    {
        this.value= null;
    }

    /**
     * Constructs an etag from a byte array value.
     * @param bytes byte array containing the etag value
     * @throws InvalidETagException when given array has a length greater than 8 bytes
     */
    public ETag( byte[] bytes ) throws InvalidETagException
    {
        if ( bytes == null || bytes.length == 0 )
        {
            this.value= null;
        }
        else if ( bytes.length > 8 )
        {
            throw new InvalidETagException( "ETag length is invalid, must be between 0..8 bytes. Given length is: " + bytes.length );
        }
        else
        {
            this.value= bytes.clone();
        }
    }

    /**
     * Constructs an etag from a string containing the hexadecimal representation
     * where two characters convert to one byte containing the indicated byte value.
     * For instance the string '11FF' will result in an etag value of two bytes containing
     * the decimal values of 17 and 255. Only the first 16 characters will be regarded.
     * @param hexString contains the hexadecimal representation of the etag value.
     * @throws InvalidETagException when given string does not represent a etag length of 0..8 bytes
     */
    public ETag( String hexString ) throws InvalidETagException
    {
        this.value= toBytes( hexString );
    }

    /**
     * Constructs an etag from a Long value.
     * @param longValue The value to create an ETag from.
     */
    public ETag( Long longValue )
    {
        this.value= toBytes( longValue );
    }

    /**
     * Gets the etag value as byte array.
     * @return Byte array containing the etag value.
     */
    public byte[] getBytes()
    {
        if ( value == null )
        {
            return null;
        }
        return value.clone();
    }

    /**
     * Gets the etag value as Long.
     * @return Long containing the etag value.
     */
    public Long getLong()
    {
        if ( value == null )
        {
            return null;
        }
        return toLong( value );
    }

    /**
     * Gets the string with containing the hexadecimal representation.
     * Hexadecimal values a-f will be lower case.
     * @return The string containing the hexadecimal representation or {@code null} when etag has no value.
     */
    public String getHexString()
    {
        if ( value == null )
        {
            return null;
        }
        return toHexString( value );
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        if ( value == null )
        {
            return "ETag{ null }";
        }
        return "ETag{ " + toHexString( value ) + " }";
    }

    /**
     * Static function that creates etag from byte array.
     * @param bytes The byte array containing etag value.
     * @return The etag object created.
     * @throws InvalidETagException when the number of bytes > 8
     */
    static public ETag valueOf( byte[] bytes ) throws InvalidETagException
    {
        return new ETag( bytes );
    }

    /**
     * Static function that creates etag from Long.
     * @param longValue The long value to create etag from.
     * @return The etag object created.
     */
    static public ETag valueOf( Long longValue )
    {
        return new ETag( longValue );
    }

    /**
     * Static function that creates etag from hexadecimal string.
     * @param hexString the hexadecimal string to create etag from.
     * @return The etag object created.
     * @throws InvalidETagException when given 
     */
    static public ETag valueOf( String hexString ) throws InvalidETagException
    {
        return new ETag( hexString );
    }

    /**
     * Convenience method to create a list of etags form a list of byte arrays.
     * @param bytesList The List of Byte array to make a list of etags from.
     * @return The list of etags. 
     * @throws InvalidETagException when etag could not be created from bytes
     */
    static public List< ETag > getList( List< byte[] > bytesList ) throws InvalidETagException
    {
        LinkedList< ETag > result= new LinkedList< ETag >();
        for ( byte[] bytes : bytesList )
        {
            result.add( new ETag( bytes ) );
        }
        return result;
    }

    /**
     * Check a collection of etags whether it contains the etag.
     * @param etags The collection of etags to check.
     * @return True when the etag is found in the collection, otherwise false.
     */
    public boolean isIn( Collection< ETag > etags )
    {
        for ( ETag e : etags )
        {
            if ( this.equals( e ) ) return true;
        }
        return false;
    }

    /**
     * Check etag on equality to another etag. 
     * Etags are equal when there byte arrays contain the same sequence of bytes. 
     * @param o The etag object to test for equality
     * @return True 
     */
    @Override
    public boolean equals( Object o )
    {
        if ( o == null && this.value == null ) return true;
        if ( !( o instanceof ETag ) )
        {
            return false;
        }
        ETag other= (ETag) o;
        return Arrays.equals( this.value, other.value );
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( ETag other )
    {
        if ( this == other ) return 0;
        if ( null == other && null == this.value ) return 0;
        if ( null == other && null != this.value ) return 1;
        if ( null == other.value && null == this.value ) return 0;
        if ( null == other.value && null != this.value ) return 1;
        if ( null == this.value ) return -1;
        if ( this.value.length < other.value.length ) return -1;
        if ( this.value.length > other.value.length ) return 1;
        for ( int i= 0; i < this.value.length && i < other.value.length; i++ )
        {
            if ( this.value[i] < other.value[i] ) return -1;
            if ( this.value[i] > other.value[i] ) return 1;
        }
        return 0;
    }

    @Override
    public int hashCode()
    {
        if ( this.hashCode == null )
        {
            this.hashCode= Arrays.hashCode( this.value );
        }
        return this.hashCode;
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
            return null;
        }
        StringBuilder sb= new StringBuilder();
        for ( byte b : bytes )
        {
            sb.append( String.format( "%02x", b & 0xFF ) );
        }
        return sb.toString();
    }

    /**
     * Converts an etag value to a Long containing the hexadecimal representation.
     * Hexadecimal values a-f will be lower case.
     * @param bytes The etag value.
     * @return The string containing the hexadecimal representation.
     */
    public static Long toLong( byte[] bytes )
    {
        if ( bytes == null || bytes.length <= 0 )
        {
            return null;
        }
        ByteBuffer buffer= ByteBuffer.wrap( bytes );
        return Long.valueOf( buffer.getLong() );
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
            return null;
        }
        ByteBuffer byteBuffer= ByteBuffer.allocate( Long.BYTES );
        byteBuffer.putLong( longValue );
        byteBuffer.flip();
        return byteBuffer.array();

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
            return null;
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
            for ( int i= 0; i < bytes.length; i++ )
            {
                int index= i * 2;
                int v= Integer.parseInt( hexString.substring( index, index + 2 ).toLowerCase(), 16 );
                bytes[i]= (byte) v;
            }
            return bytes;
        }
    }
}