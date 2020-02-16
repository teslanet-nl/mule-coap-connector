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


import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;


//TODO constructor for long for convenience:
//Long millis= ...;
//ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
//buffer.putLong( millis );
//etags.put( key, new ETag( buffer.array()));

/**
 * Implementation of the Etag concept for convenience.
 * It eases the handling of Etag values in Mule flows. 
 * Etags can be constructed from byte array as well as from strings that 
 * contain a hexadecimal representation. 
 * An etag object is immutable and comparable to other etags.
 */
public final class ETag implements Comparable< ETag >
{
    /**
     * The etag value
     */
    private final byte[] value;

    private int hashCode= 0;

    /**
     * Constructs an etag from a byte array value.
     * @param etag byte array containing the etag value
     * @throws InvalidETagException when given etag has not a length of 1..8 bytes
     */
    public ETag( byte[] etag ) throws InvalidETagException
    {
        if ( etag == null )
        {
            throw new InvalidETagException( "ETag value null is not allowed" );
        }
        if ( etag.length < 1 || etag.length > 8 )
        {
            throw new InvalidETagException( "ETag length invalid, must be between 1..8 bytes. Given length is: " + etag.length );
        }
        this.value= etag.clone();

    }

    /**
     * Constructs an etag from a string containing the hexadecimal representation
     * where two characters convert to one byte which contains the designated value.
     * For instance the string '11FF' will result in an etag value of two bytes containing
     * the decimal values of 17 and 255.   
     * @param hexString contains the hexadecimal representation of the etag value.
     * @throws InvalidETagException when given string does not represent a etag length of 1..8 bytes
     */
    public ETag( String hexString ) throws InvalidETagException
    {
        if ( hexString == null )
        {
            throw new InvalidETagException( "Given hexString is null." );
        }
        int length= hexString.length() / 2;
        //check even number of characters
        if ( length * 2 != hexString.length() )
        {
            throw new InvalidETagException( "Given hexString must have even number of characters. The number found: " + hexString.length() );
        }
        if ( length < 1 || length > 8 )
        {
            throw new InvalidETagException( "ETag length invalid, must be between 1..8 bytes. Given length is: " + length );
        }
        value= new byte [length];
        for ( int i= 0; i < value.length; i++ )
        {
            int index= i * 2;
            int v= Integer.parseInt( hexString.substring( index, index + 2 ).toLowerCase(), 16 );
            value[i]= (byte) v;
        }
    }

    /**
     * Gets the etag value as byte array.
     * @return Byte array containing the etag value.
     */
    public byte[] asBytes()
    {
        return value.clone();
    }

    /**
     * Gets the etag value as string containing the hexadecimal representation.
     * Hexadecimal values a-f will be lower case.
     * @return The string containing the hexadecimal representation.
     */
    @Override
    public String toString()
    {
        return toHexString( value );
    }

    /**
     * Static function that creates etag from byte array.
     * @param etag The byte array containing etag value.
     * @return The etag object created.
     * @throws InvalidETagException when given etag has not a length of 1..8 bytes
     */
    static public ETag create( byte[] etag ) throws InvalidETagException
    {
        return new ETag( etag );
    }

    /**
     * Static function that creates etag from hexadecimal string.
     * @param hexString the hexadecimal string to create etag from.
     * @return The etag object created.
     * @throws InvalidETagException when given string does not represent a etag length of 1..8 bytes
     */
    static public ETag createFromHexString( String hexString ) throws InvalidETagException
    {
        return new ETag( hexString );
    }

    /**
     * Convenience method to create a list of etags form a list of byte arrays.
     * @param etags The List of Byte array to make a list of etags from.
     * @return The list of etags. 
     * @throws InvalidETagException when a given etag has not a length of 1..8 bytes
     */
    static public List< ETag > getList( List< byte[] > etags ) throws InvalidETagException
    {
        LinkedList< ETag > result= new LinkedList< ETag >();
        for ( byte[] etag : etags )
        {
            result.add( new ETag( etag ) );
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
        ETag other= (ETag) o;
        if ( null == other ) return false;
        if ( this == other ) return true;
        return Arrays.equals( this.value, other.value );
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( ETag other )
    {
        if ( null == other ) return 1;
        if ( this == other ) return 0;
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
        if ( this.hashCode == 0 )
        {
            this.hashCode= Arrays.hashCode( this.value );
        }
        return this.hashCode;
    }

    /**
     * Converts an etag value to a string containing the hexadecimal representation.
     * Hexadecimal values a-f will be lower case.
     * @param bytes The etag value.
     * @return The string containing the hexadecimal representation.
     */
    public static String toHexString( byte[] bytes )
    {
        StringBuilder sb= new StringBuilder();
        if ( bytes == null )
        {
            //sb.append( "null" );
        }
        else
        {
            for ( byte b : bytes )
            {
                sb.append( String.format( "%02x", b & 0xFF ) );
            }
        }
        return sb.toString();
    }

}
