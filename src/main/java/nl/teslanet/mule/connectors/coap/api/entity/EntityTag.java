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
package nl.teslanet.mule.connectors.coap.api.entity;


import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;


/**
 * Implementation of the Entity-tag concept for convenience.
 * It eases the handling of Etag values in Mule flows. 
 * Etags can be constructed from long, byte array as well as from strings 
 * as utf-8 bytes or hexadecimal representation. 
 * An etag object is immutable and comparable to other etags.
 */
public final class EntityTag implements Comparable< EntityTag >
{
    /**
     * The etag value
     */
    private final byte[] value;

    /**
     * Default constructor that create empty etag.
     */
    public EntityTag()
    {
        this.value= OptionUtils.EMPTY_BYTES;
    }

    /**
     * Constructs an etag from a byte array value.
     * @param bytes The byte array containing the etag value
     * @throws EntityTagException When given array has length that is not 0..8 bytes
     */
    public EntityTag( byte[] bytes ) throws EntityTagException
    {
        if ( bytes == null || bytes.length <= 0 )
        {
            this.value= OptionUtils.EMPTY_BYTES;
        }
        else if ( bytes.length > 8 )
        {
            throw new EntityTagException( "ETag length is invalid, must be between 0..8 bytes. Given length is: " + bytes.length );
        }
        else
        {
            this.value= bytes.clone();
        }
    }

    /**
     * Constructs an etag from a string using UTF-8 encoding.
     * @param string contains the string representation of the etag value.
     * @throws EntityTagException when given string does not represent a etag length of 0..8 bytes
     */
    public EntityTag( String string ) throws EntityTagException
    {
        try
        {
            this.value= OptionUtils.toBytes( string, 0, 8 );
        }
        catch ( OptionValueException e )
        {
            throw new EntityTagException( "Cannot construct etag value of string { " + string + " }", e );
        }
    }

    /**
     * Constructs an etag from string interpreted as number using given radix.
     * @param string contains the string representation of the etag value.
     * @throws EntityTagException when given string does not represent a etag length of 0..8 bytes
     */
    public EntityTag( String string, int radix ) throws EntityTagException
    {
        if ( string == null || string.length() <= 0 )
        {
            this.value= OptionUtils.EMPTY_BYTES;
        }
        else
        {
            try
            {
                long longValue= Long.parseLong( string, radix );
                this.value= OptionUtils.toBytes( longValue, 0, 8 );
            }
            catch ( NumberFormatException | OptionValueException e )
            {
                throw new EntityTagException( "Cannot construct etag value of { " + string + " } using radix {" + radix + " }" );
            }
        }
    }

    /**
     * Constructs an etag from a Integer value.
     * @param intValue The value to create an ETag from.
     */
    public EntityTag( int intValue )
    {
        this.value= OptionUtils.toBytes( intValue );
    }

    /**
     * Constructs an etag from a Long value.
     * @param longValue The value to create an ETag from.
     */
    public EntityTag( long longValue )
    {
        this.value= OptionUtils.toBytes( longValue );

    }

    /**
     * Get the etag value as byte array.
     * Cf does clone the byte array also, however not cloning here would make the class mutable.
     * This is considered acceptable because etags are small objects.
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
    public String getValueAsHex()
    {
        return OptionUtils.toHexString( value );
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "ETag{ " + getValueAsHex() + " }";
    }

    /**
     * Static function that creates etag from byte array.
     * @param bytes The byte array containing etag value.
     * @return The etag object created.
     * @throws EntityTagException when byte array was too large. 
     */
    public static EntityTag valueOf( byte[] bytes ) throws EntityTagException
    {
        return new EntityTag( bytes );
    }

    /**
     * Static function that creates etag from Integer.
     * @param intValue The integer value to create etag from.
     * @return The etag object created.
     */
    public static EntityTag valueOf( int intValue )
    {
        return new EntityTag( intValue );
    }

    /**
     * Static function that creates etag from Long.
     * @param longValue The long value to create etag from.
     * @return The etag object created.
     */
    public static EntityTag valueOf( long longValue )
    {
        return new EntityTag( longValue );
    }

    /**
     * Static function that creates etag from string using utf-8 representation.
     * @param string the string to create etag from.
     * @return The etag object created.
     * @throws EntityTagException when given string was too large. 
     */
    public static EntityTag valueOf( String string ) throws EntityTagException
    {
        return new EntityTag( string );
    }

    /**
     * Static function that creates etag from string interpreted as number using given radix.
     * @param string the string to create etag from.
     * @return The etag object created.
     * @throws EntityTagException when given string cannot be converted. 
     */
    public static EntityTag valueOf( String string, int radix ) throws EntityTagException
    {
        return new EntityTag( string, radix );
    }

    /**
     * Convenience method to create a list of etags form a list of byte arrays.
     * @param bytesList The List of Byte arrays to make a list of etags from.
     * @return The list of etags. 
     * @throws EntityTagException when the etag could not be created from bytes
     */
    public static List< EntityTag > getList( List< byte[] > bytesList ) throws EntityTagException
    {
        LinkedList< EntityTag > result= new LinkedList<>();
        for ( byte[] bytes : bytesList )
        {
            result.add( new EntityTag( bytes ) );
        }
        return result;
    }

    /**
     * Check a collection of etags whether it contains the etag.
     * When given collection is null the etag is considered not found.
     * @param etags The collection of etags to check.
     * @return True when the etag is found in the collection, otherwise false.
     */
    public boolean isIn( Collection< EntityTag > etags )
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
    public int compareTo( EntityTag other )
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
        return Arrays.hashCode( value );
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
        if ( !( obj instanceof EntityTag ) )
        {
            return false;
        }
        EntityTag other= (EntityTag) obj;
        return Arrays.equals( value, other.value );
    }
}