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
package nl.teslanet.mule.connectors.coap.internal.options;


import nl.teslanet.mule.connectors.coap.api.entity.EntityTagAttribute;
import nl.teslanet.mule.connectors.coap.api.entity.EntityTagException;
import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;


/**
 * The Entity-tag implementation. For convenience several methods are 
 * offered to get different representations of the entity and to
 * ease the handling of Entity-tag values in Mule flows. 
 * Entity-tags can be constructed from long, byte array as well as from strings 
 * as utf-8 bytes or hexadecimal representation. 
 * An Entity-tag value object is immutable and comparable to other Entity-tags.
 */
public class DefaultEntityTag extends EntityTagAttribute
{
    /**
     * Class serial version.
     */
    private static final long serialVersionUID= 1L;

    /**
     * Constructor Error message prefix.
     */
    private static final String MSG_PREFIX_CONSTRUCT_ERROR= "Cannot construct Entity-tag value, ";

    /**
     * Constructor Error message prefix.
     */
    private static final String MSG_POSTFIX_CONSTRUCT_ERROR= " } is invalid.";

    /**
     * The Entity-tag value
     */
    private final byte[] value;

    /**
     * Constructs an Entity-tag from a byte array value.
     * @param bytes The byte array containing the Entity-tag value
     * @throws EntityTagException When given array has length that is not 1..8 bytes
     */
    public DefaultEntityTag( byte[] bytes ) throws EntityTagException
    {
        if ( bytes == null )
        {
            throw new EntityTagException( MSG_PREFIX_CONSTRUCT_ERROR + "value { null" + MSG_POSTFIX_CONSTRUCT_ERROR );
        }
        else if ( bytes.length <= 0 || bytes.length > 8 )
        {
            throw new EntityTagException( MSG_PREFIX_CONSTRUCT_ERROR + "must be between 1..8 bytes. Actual length is { " + bytes.length + MSG_POSTFIX_CONSTRUCT_ERROR );
        }
        else
        {
            this.value= bytes.clone();
        }
    }

    /**
     * Constructs an Entity-tag from a string using UTF-8 encoding.
     * @param string contains the string representation of the Entity-tag value.
     * @throws EntityTagException when given string does not represent a Entity-tag length of 1..8 bytes
     */
    public DefaultEntityTag( String string ) throws EntityTagException
    {
        try
        {
            this.value= OptionUtils.toBytes( string, 1, 8 );
        }
        catch ( OptionValueException e )
        {
            throw new EntityTagException( MSG_PREFIX_CONSTRUCT_ERROR + "string { " + string + MSG_POSTFIX_CONSTRUCT_ERROR, e );
        }
    }

    /**
     * Constructs an Entity-tag from string interpreted as number using given radix.
     * @param string contains the string representation of the Entity-tag value.
     * @throws EntityTagException when given string does not represent a Entity-tag length of 1..8 bytes
     */
    public DefaultEntityTag( String string, int radix ) throws EntityTagException
    {
        if ( string == null )
        {
            throw new EntityTagException( MSG_PREFIX_CONSTRUCT_ERROR + "string { null" + MSG_POSTFIX_CONSTRUCT_ERROR );
        }
        else if ( string.length() <= 0 )
        {
            throw new EntityTagException( MSG_PREFIX_CONSTRUCT_ERROR + "empty string is invalid." );
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
                throw new EntityTagException( MSG_PREFIX_CONSTRUCT_ERROR + "string value of { " + string + " } using radix {" + radix + MSG_POSTFIX_CONSTRUCT_ERROR );
            }
        }
    }

    /**
     * Constructs an Entity-tag from a Integer value.
     * @param intValue The value to create an Entity-tag from.
     * @throws EntityTagException When intValue <= 0.
     */
    public DefaultEntityTag( int intValue ) throws EntityTagException
    {
        this.value= OptionUtils.toBytes( intValue );
        if ( this.value.length <= 0 ) throw new EntityTagException( MSG_PREFIX_CONSTRUCT_ERROR + "int value of { " + intValue + MSG_POSTFIX_CONSTRUCT_ERROR );

    }

    /**
     * Constructs an Entity-tag from a Long value.
     * @param longValue The value to create an Entity-tag from.
     * @throws EntityTagException When longValue <= 0.
     */
    public DefaultEntityTag( long longValue ) throws EntityTagException
    {
        this.value= OptionUtils.toBytes( longValue );
        if ( this.value.length <= 0 ) throw new EntityTagException( MSG_PREFIX_CONSTRUCT_ERROR + "long value of { " + longValue + MSG_POSTFIX_CONSTRUCT_ERROR );
    }

    /**
     * Get the Entity-tag value as byte array.
     * Cf does clone the byte array also, however not cloning here would make the class mutable.
     * This is considered acceptable because Entity-tags are small objects.
     * @return byte array containing the Entity-tag value.
     */
    @Override
    public byte[] getValue()
    {
        return value.clone();
    }

    /**
     * Get the Entity-tag value as long.
     * @return Long containing the Entity-tag value.
     */
    @Override
    public long getValueAsNumber()
    {
        return OptionUtils.toLong( value );
    }

    /**
     * Get Entity-tag value as UTF-8 string.
     * @return The UTF-8 string interpretation.
     */
    @Override
    public String getValueAsString()
    {
        return OptionUtils.toString( value );
    }

    /**
     * Get the Entity-tag value as hexadecimal string.
     * Hexadecimal values a-f will be lower case.
     * @return The string containing the hexadecimal representation or empty string when Entity-tag has no value.
     */
    @Override
    public String getValueAsHex()
    {
        return OptionUtils.toHexString( value );
    }

    /**
     * Static function that creates Entity-tag from byte array.
     * @param bytes The byte array containing Entity-tag value.
     * @return The Entity-tag object created.
     * @throws EntityTagException when byte array was empty or too large. 
     */
    public static DefaultEntityTag valueOf( byte[] bytes ) throws EntityTagException
    {
        return new DefaultEntityTag( bytes );
    }

    /**
     * Static function that creates Entity-tag from Integer.
     * @param intValue The integer value to create Entity-tag from.
     * @return The Entity-tag value created.
     * @throws EntityTagException when byte array was empty or too large. 
     */
    public static DefaultEntityTag valueOf( int intValue ) throws EntityTagException
    {
        return new DefaultEntityTag( intValue );
    }

    /**
     * Static function that creates Entity-tag from Long.
     * @param longValue The long value to create Entity-tag from.
     * @return The Entity-tag object created.
     * @throws EntityTagException When longValue <= 0.
     */
    public static DefaultEntityTag valueOf( long longValue ) throws EntityTagException
    {
        return new DefaultEntityTag( longValue );
    }

    /**
     * Static function that creates Entity-tag from string using utf-8 representation.
     * @param string the string to create Entity-tag from.
     * @return The Entity-tag object created.
     * @throws EntityTagException when given string was too large. 
     */
    public static DefaultEntityTag valueOf( String string ) throws EntityTagException
    {
        return new DefaultEntityTag( string );
    }

    /**
     * Static function that creates Entity-tag from string interpreted as number using given radix.
     * @param string the string to create Entity-tag from.
     * @return The Entity-tag object created.
     * @throws EntityTagException when given string cannot be converted. 
     */
    public static DefaultEntityTag valueOf( String string, int radix ) throws EntityTagException
    {
        return new DefaultEntityTag( string, radix );
    }
}