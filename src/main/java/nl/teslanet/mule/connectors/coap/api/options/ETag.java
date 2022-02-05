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


import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;
import nl.teslanet.mule.connectors.coap.api.error.InvalidOptionValueException;


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

    /**
     * The hash code of this etag, is initialized lazily.
     */
    //private Integer hashCode= null;

    /**
     * Default constructor that create empty etag.
     */
    public ETag()
    {
        this.value= OptionUtils.EMPTY_BYTES;
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
            this.value= OptionUtils.EMPTY_BYTES;
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
        try
        {
            this.value= OptionUtils.toBytes( hexString, 8 );
        }
        catch ( InvalidOptionValueException e )
        {
            throw new InvalidETagException( "Cannot parse hexadecimal etag value: " + hexString );
        }
    }

    /**
     * Constructs an etag from a Long value.
     * @param longValue The value to create an ETag from.
     */
    public ETag( Long longValue )
    {
        this.value= OptionUtils.toBytes( longValue );
    }

    /**
     * Get the etag value as byte array.
     * Cf does clone the byte array also, however not cloning here would make the class mutable.
     * This is considered acceptable becaus etags are smaal objects.
     * @return byte array containing the etag value.
     */
    public byte[] getValue()
    {
        return value.clone();
    }

    /**
     * Get the etag value as long.
     * @return Long containing the etag value.
     */
    public long getValueAsNumber()
    {
        return OptionUtils.toLong( value );
    }

    /**
     * Get etag value as UTF-8 string.
     * @return The UTF-8 string interpretation.
     */
    public String getValueAsString()
    {
        return OptionUtils.toString( value );
    }

    /**
     * Get the etag value as hexadecimal string.
     * Hexadecimal values a-f will be lower case.
     * @return The string containing the hexadecimal representation or empty string when etag has no value.
     */
    public String getValueAsHexString()
    {
        return OptionUtils.toHexString( value );
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "ETag { " + getValueAsHexString() + " }";
    }

    /**
     * Static function that creates etag from byte array.
     * @param bytes The byte array containing etag value.
     * @return The etag object created.
     * @throws InvalidETagException when the number of bytes > 8
     */
    public static ETag valueOf( byte[] bytes ) throws InvalidETagException
    {
        return new ETag( bytes );
    }

    /**
     * Static function that creates etag from Long.
     * @param longValue The long value to create etag from.
     * @return The etag object created.
     */
    public static ETag valueOf( Long longValue )
    {
        return new ETag( longValue );
    }

    /**
     * Static function that creates etag from hexadecimal string.
     * @param hexString the hexadecimal string to create etag from.
     * @return The etag object created.
     * @throws InvalidETagException when given 
     */
    public static ETag valueOf( String hexString ) throws InvalidETagException
    {
        return new ETag( hexString );
    }

    /**
     * Convenience method to create a list of etags form a list of byte arrays.
     * @param bytesList The List of Byte arrays to make a list of etags from.
     * @return The list of etags. 
     * @throws InvalidETagException when the etag could not be created from bytes
     */
    public static List< ETag > getList( List< byte[] > bytesList ) throws InvalidETagException
    {
        LinkedList< ETag > result= new LinkedList<>();
        for ( byte[] bytes : bytesList )
        {
            result.add( new ETag( bytes ) );
        }
        return result;
    }

    /**
     * Check a collection of etags whether it contains the etag.
     * When given collection is null the etag is considered not found.
     * @param etags The collection of etags to check.
     * @return True when the etag is found in the collection, otherwise false.
     */
    public boolean isIn( Collection< ETag > etags )
    {
        if ( etags == null ) return false;
        return etags.contains( this );
    }

    /**
     * Check if the etag is empty. etags whether it contains the etag.
     * @return True when the etag is empty, otherwise false.
     */
    public boolean isEmpty()
    {
        return( value.length == 0 );
    }

    /**
     * Compares this object with the specified object for order.
     */
    @Override
    public int compareTo( ETag other )
    {
        if ( other == null ) return 1;
        if ( this == other ) return 0;
        if ( this.value.length < other.value.length ) return -1;
        if ( this.value.length > other.value.length ) return 1;
        for ( int i= 0; i < this.value.length; i++ )
        {
            if ( this.value[i] < other.value[i] ) return -1;
            if ( this.value[i] > other.value[i] ) return 1;
        }
        return 0;
    }

    /**
     * Returns a hash code value for the object.
     */
    @Override
    public int hashCode()
    {
        final int prime= 31;
        int result= 1;
        result= prime * result + Arrays.hashCode( value );
        return result;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     */
    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( !( obj instanceof ETag ) )
        {
            return false;
        }
        ETag other= (ETag) obj;
        return Arrays.equals( value, other.value );
    }
}