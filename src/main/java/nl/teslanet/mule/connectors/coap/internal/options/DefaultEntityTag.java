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


import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;


/**
 * The Entity-tag implementation. For convenience several methods are 
 * offered to get different representations of the entity and to
 * ease the handling of Entity-tag values in Mule flows. 
 * Entity-tags can be constructed from long, byte array as well as from strings 
 * as utf-8 bytes or hexadecimal representation. 
 * An Entity-tag value object is immutable and comparable to other Entity-tags.
 */
public class DefaultEntityTag extends DefaultBytesValue
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
     * Constructs an Entity-tag from a byte array value.
     * @param bytes The byte array containing the Entity-tag value
     * @throws OptionValueException When given array has length that is not 1..8 bytes
     */
    public DefaultEntityTag( byte[] bytes ) throws OptionValueException
    {
        super( bytes );
        checkValid();
    }

    /**
     * Constructs an Entity-tag from a string using UTF-8 encoding.
     * @param string contains the string representation of the Entity-tag value.
     * @throws OptionValueException when given string does not represent a Entity-tag length of 1..8 bytes
     */
    public DefaultEntityTag( String string ) throws OptionValueException
    {
        super( string, 1, 8 );
        checkValid();
    }

    /**
     * Constructs an Entity-tag from string interpreted as number using given radix.
     * @param string contains the string representation of the Entity-tag value.
     * @throws OptionValueException when given string does not represent a Entity-tag length of 1..8 bytes
     * @throws OptionValueException when given string cannot be converted to bytes.
     */
    public DefaultEntityTag( String string, int radix ) throws OptionValueException
    {
        super( string, radix );
        checkValid();
    }

    /**
     * Constructs an Entity-tag from a Integer value.
     * @param intValue The value to create an Entity-tag from.
     * @throws OptionValueException When intValue is invalid.
     */
    public DefaultEntityTag( int intValue ) throws OptionValueException
    {
        super( intValue );
        checkValid();
    }

    /**
     * Constructs an Entity-tag from a Long value.
     * @param longValue The value to create an Entity-tag from.
     * @throws OptionValueException When longValue <= 0.
     */
    public DefaultEntityTag( long longValue ) throws OptionValueException
    {
        super( longValue );
        checkValid();
    }

    /**
     * Constructs an Entity-tag from a Long value.
     * @param bytes The value to create an Entity-tag from.
     * @throws OptionValueException When longValue <= 0.
     */
    public DefaultEntityTag( DefaultBytesValue bytes ) throws OptionValueException
    {
        super( bytes );
        checkValid();
    }

    /**
     * Check validity of this Entity-tag.
     * @throws OptionValueException When value has length that is not 1..8 bytes
     */
    private void checkValid() throws OptionValueException
    {
        if ( value.length == 0 || value.length > 8 )
        {
            throw new OptionValueException(
                MSG_PREFIX_CONSTRUCT_ERROR + "must be between 1..8 bytes. Actual length is { " + value.length
                    + MSG_POSTFIX_CONSTRUCT_ERROR
            );
        }
    }

    /**
     * Static function that creates Entity-tag from byte array.
     * @param bytes The byte array containing Entity-tag value.
     * @return The Entity-tag object created.
     * @throws OptionValueException when byte array was empty or too large. 
     */
    public static DefaultEntityTag valueOf( byte[] bytes ) throws OptionValueException
    {
        return new DefaultEntityTag( bytes );
    }

    /**
     * Static function that creates Entity-tag from Integer.
     * @param intValue The integer value to create Entity-tag from.
     * @return The Entity-tag value created.
     * @throws OptionValueException when byte array was empty or too large. 
     */
    public static DefaultEntityTag valueOf( int intValue ) throws OptionValueException
    {
        return new DefaultEntityTag( intValue );
    }

    /**
     * Static function that creates Entity-tag from Long.
     * @param longValue The long value to create Entity-tag from.
     * @return The Entity-tag object created.
     * @throws OptionValueException When longValue <= 0.
     */
    public static DefaultEntityTag valueOf( long longValue ) throws OptionValueException
    {
        return new DefaultEntityTag( longValue );
    }

    /**
     * Static function that creates Entity-tag from string using utf-8 representation.
     * @param string the string to create Entity-tag from.
     * @return The Entity-tag object created.
     * @throws OptionValueException when given string was too large. 
     */
    public static DefaultEntityTag valueOf( String string ) throws OptionValueException
    {
        return new DefaultEntityTag( string );
    }

    /**
     * Static function that creates Entity-tag from string interpreted as number using given radix.
     * @param string the string to create Entity-tag from.
     * @return The Entity-tag object created.
     * @throws OptionValueException when given string cannot be converted. 
     */
    public static DefaultEntityTag valueOf( String string, int radix ) throws OptionValueException
    {
        return new DefaultEntityTag( string, radix );
    }
}