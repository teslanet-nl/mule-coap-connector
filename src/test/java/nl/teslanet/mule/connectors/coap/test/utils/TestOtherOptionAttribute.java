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
package nl.teslanet.mule.connectors.coap.test.utils;


import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.option.OptionDefinition;
import org.mule.runtime.api.util.IOUtils;

import nl.teslanet.mule.connectors.coap.api.config.options.OptionFormat;
import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;
import nl.teslanet.mule.connectors.coap.api.options.OtherOptionAttribute;
import nl.teslanet.mule.connectors.coap.internal.utils.MessageUtils;


/**
 * Test other option attribute. 
 */
public final class TestOtherOptionAttribute extends OtherOptionAttribute
{
    /**
     * Wrong type exception message format.
     */
    private final String FORMAT_WRONG_TYPE= "OtherOption { %s } is not of type %s.";

    /**
     * The definition of the option.
     */
    private final Option option;

    /**
     * Constructor
     * @param number The number of the option.
     * @param value The value of the option.
     * @param optionType 
     */
    public TestOtherOptionAttribute( OptionDefinition optionDef, byte[] value )
    {
        super();
        this.option= optionDef.create( value );
    }

    /**
     * Constructor from Cf Option
     * @param number The number of the option.
     * @param value The value of the option.
     * @param optionType 
     */
    public TestOtherOptionAttribute( Option option )
    {
        super();
        this.option= option;
    }

    /**
     * Get the alias of this other option.
     *
     * @return The option number.
     */
    @Override
    public String getAlias()
    {
        return option.getDefinition().getName();
    }

    /**
     * Get the number of this other option.
     *
     * @return The option number.
     */
    @Override
    public int getNumber()
    {

        return option.getDefinition().getNumber();
    }

    /**
     * Get the format of this other option.
     * @return The option format.
     */
    @Override
    public OptionFormat getFormat()
    {
        return MessageUtils.toOptionFormat( option.getDefinition().getFormat() );
    }

    /**
     * Check if the option is empty. options whether it contains the option.
     * @return True when the option is empty, otherwise false.
     */
    @Override
    public boolean isEmpty()
    {
        return option.getLength() == 0;
    }

    /**
     * Get the the length of the other option.
     * @return The number of bytes of this option.
     */
    @Override
    public int getLength()
    {
        return option.getLength();
    }

    /**
     * Get the value of this other option as byte array.
     * @return The option value if any, otherwise null.
     */
    protected byte[] getValueAsBytes()
    {
        return option.getValue();
    }

    /**
     * Get the value of this other option.
     *
     * @return The option value if any, otherwise null.
     */
    @Override
    public InputStream getValue()
    {
        return OptionUtils.toInputStream( getValueAsBytes() );
    }

    /**
     * Get the value as number.
     * @return long containing the value.
     */
    @Override
    public long getValueAsNumber()
    {
        if ( option.getDefinition().getFormat() != org.eclipse.californium.core.coap.OptionNumberRegistry.OptionFormat.INTEGER )
            throw new NumberFormatException( String.format( FORMAT_WRONG_TYPE, option.getDefinition().getName(), OptionFormat.INTEGER.toString() ) );
        return option.getLongValue();
    }

    /**
     * Get the option value as hexadecimal string.
     * Hexadecimal values a-f will be lower case.
     * @return The string containing the hexadecimal representation or empty string when the value is empty.
     */
    @Override
    public String getValueAsHex()
    {
        return OptionUtils.toHexString( option.getValue() );
    }

    /**
     * Get value as UTF-8 string.
     * @return The UTF-8 string interpretation.
     */
    @Override
    public String getValueAsString()
    {
        if ( option.getDefinition().getFormat() != org.eclipse.californium.core.coap.OptionNumberRegistry.OptionFormat.STRING )
            throw new NumberFormatException( String.format( FORMAT_WRONG_TYPE, option.getDefinition().getName(), OptionFormat.STRING.toString() ) );
        return option.getStringValue();
    }

    /**
     * Checks if option is critical.
     *
     * @return {@code true} if is option critical, otherwise {@code false}.
     */
    @Override
    public boolean isCritical()
    {
        return OptionUtils.isCritical( getNumber() );
    }

    /**
     * Checks if option with this number is unsafe.
     *
     * @return {@code true}  if this is an unsafe option, otherwise {@code false}.
     */
    @Override
    public boolean isUnsafe()
    {
        return OptionUtils.isUnsafe( getNumber() );
    }

    /**
     * Checks if option with this number is a NoCacheKey option.
     *
     * @return {@code true} if this is NoCacheKey option, otherwise {@code false}.
     */
    @Override
    public boolean isNoCacheKey()
    {
        return OptionUtils.isNoCacheKey( getNumber() );
    }

    /**
     * Returns a hash code value for the object.
     */
    @Override
    public int hashCode()
    {
        final int prime= 31;
        int result= 1;
        result= prime * result + Objects.hash( getNumber() );
        result= prime * result + Arrays.hashCode( getValueAsBytes() );
        return result;
    }

    /**
     * Indicates whether some other object is "equal to" this.
     */
    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( !( obj instanceof OtherOptionAttribute ) )
        {
            return false;
        }
        OtherOptionAttribute other= (OtherOptionAttribute) obj;
        return ( getNumber() == other.getNumber() ) && Arrays.equals( getValueAsBytes(), IOUtils.toByteArray( getValue() ) );
    }

    /**
     * Get the string representation.
     */
    @Override
    public String toString()
    {
        StringBuilder builder= new StringBuilder();
        builder.append( "TestOption{" );
        builder.append( " alias=" ).append( getAlias() );
        builder.append( ", number=" ).append( getNumber() );
        switch ( option.getDefinition().getFormat() )
        {
            case EMPTY:
                builder.append( ", empty" );
                break;
            case INTEGER:
                builder.append( ", valueAsNumber=" ).append( option.getLongValue() );
                break;
            case STRING:
                builder.append( ", valueAsString=" ).append( option.getStringValue() );
                break;
            case OPAQUE:
            case UNKNOWN:
            default:
                builder.append( ", valueAsHex=" ).append( OptionUtils.toHexString( option.getValue() ) );
                break;
        }
        builder.append( " }" );
        return builder.toString();
    }
}
