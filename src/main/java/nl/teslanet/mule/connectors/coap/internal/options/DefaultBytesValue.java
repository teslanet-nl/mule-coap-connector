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


import nl.teslanet.mule.connectors.coap.api.binary.BytesValue;
import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;


/**
 * The bytes implementation. For convenience several methods are 
 * offered to get different representations of the entity and to
 * ease the handling of bytes values in Mule flows. 
 * Bytes can be constructed from long, byte array as well as from strings 
 * as utf-8 bytes or hexadecimal representation. 
 * An bytes value object is immutable and comparable to other bytes.
 */
public class DefaultBytesValue extends BytesValue
{
    /**
     * Class serial version.
     */
    private static final long serialVersionUID= 1L;

    /**
     * Constructor Error message prefix.
     */
    private static final String MSG_PREFIX_CONSTRUCT_ERROR= "Cannot construct bytes value, ";

    /**
     * Constructor Error message prefix.
     */
    private static final String MSG_POSTFIX_CONSTRUCT_ERROR= " } is invalid.";

    /**
     * The BytesValue value
     */
    protected final byte[] value;

    /**
     * Constructs a BytesValue from a byte array value.
     * @param bytes The bytes valuee
     */
    public DefaultBytesValue( DefaultBytesValue bytes )
    {
        if ( bytes == null )
        {
            this.value= OptionUtils.EMPTY_BYTES;
        }
        else
        {
            this.value= bytes.value;
        }
    }

    /**
     * Constructs an BytesValue from a byte array value.
     * @param bytes The byte array containing the BytesValue value
     */
    public DefaultBytesValue( byte[] bytes )
    {
        if ( bytes == null || bytes.length == 0 )
        {
            this.value= OptionUtils.EMPTY_BYTES;
        }
        else
        {
            this.value= bytes.clone();
        }
    }

    /**
     * Constructs an bytes from a string using UTF-8 encoding.
     * @param string contains the string representation of the bytes value.
     * @throws OptionValueException when given string cannot be converted to bytes.
     */
    public DefaultBytesValue( String string ) throws OptionValueException
    {
        this.value= OptionUtils.toBytes( string );
    }

    /**
     * Constructs bytes from a string using UTF-8 encoding.
     * @param string contains the string representation of the bytes value.
     * @throws OptionValueException when given string does not result in min..max bytes
     */
    protected DefaultBytesValue( String string, int min, int max ) throws OptionValueException
    {
        this.value= OptionUtils.toBytes( string, min, max );
    }

    /**
     * Constructs an bytes from string interpreted as number using given radix.
     * @param string contains the string representation of the bytes value.
     * @throws OptionValueException when given string does not represent a bytes length of 1..8 bytes
     */
    public DefaultBytesValue( String string, int radix ) throws OptionValueException
    {
        if ( string == null || string.length() == 0 )
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
                throw new OptionValueException(
                    MSG_PREFIX_CONSTRUCT_ERROR + "string value of { " + string + " } using radix {" + radix
                        + MSG_POSTFIX_CONSTRUCT_ERROR
                );
            }
        }
    }

    /**
     * Constructs an bytes from a Integer value.
     * @param intValue The value to create an bytes from.
     */
    public DefaultBytesValue( int intValue )
    {
        this.value= OptionUtils.toBytes( intValue );
    }

    /**
     * Constructs an bytes from a Long value.
     * @param longValue The value to create an bytes from.
     */
    public DefaultBytesValue( long longValue )
    {
        this.value= OptionUtils.toBytes( longValue );
    }

    /**
     * Get the bytes value as byte array.
     * Cf does clone the byte array also, however not cloning here would make the class mutable.
     * This is considered acceptable because bytes are supposed to be small objects.
     * @return byte array containing the bytes value.
     */
    @Override
    public byte[] getValue()
    {
        return value.clone();
    }

    /**
     * Get the bytes value as long.
     * @return Long containing the bytes value.
     */
    @Override
    public long getValueAsNumber()
    {
        return OptionUtils.toLong( value );
    }

    /**
     * Get bytes value as UTF-8 string.
     * @return The UTF-8 string interpretation.
     */
    @Override
    public String getValueAsString()
    {
        return OptionUtils.toString( value );
    }

    /**
     * Get the bytes value as hexadecimal string.
     * Hexadecimal values a-f will be lower case.
     * @return The string containing the hexadecimal representation or empty string when bytes has no value.
     */
    @Override
    public String getValueAsHex()
    {
        return OptionUtils.toHexString( value );
    }

    /**
     * Static function that creates bytes from byte array.
     * @param bytes The byte array containing bytes value.
     * @return The bytes object created.
     * @throws OptionValueException when byte array was empty or too large. 
     */
    public static DefaultBytesValue valueOf( byte[] bytes ) throws OptionValueException
    {
        return new DefaultBytesValue( bytes );
    }

    /**
     * Static function that creates bytes from Integer.
     * @param intValue The integer value to create bytes from.
     * @return The bytes value created.
     * @throws OptionValueException when byte array was empty or too large. 
     */
    public static DefaultBytesValue valueOf( int intValue ) throws OptionValueException
    {
        return new DefaultBytesValue( intValue );
    }

    /**
     * Static function that creates bytes from Long.
     * @param longValue The long value to create bytes from.
     * @return The bytes object created.
     * @throws OptionValueException When longValue <= 0.
     */
    public static DefaultBytesValue valueOf( long longValue ) throws OptionValueException
    {
        return new DefaultBytesValue( longValue );
    }

    /**
     * Static function that creates bytes from string using utf-8 representation.
     * @param string the string to create bytes from.
     * @return The bytes object created.
     * @throws OptionValueException when given string was too large. 
     */
    public static DefaultBytesValue valueOf( String string ) throws OptionValueException
    {
        return new DefaultBytesValue( string );
    }

    /**
     * Static function that creates bytes from string interpreted as number using given radix.
     * @param string the string to create bytes from.
     * @return The bytes object created.
     * @throws OptionValueException when given string cannot be converted. 
     */
    public static DefaultBytesValue valueOf( String string, int radix ) throws OptionValueException
    {
        return new DefaultBytesValue( string, radix );
    }
}