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


import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;


//TODO RC unittests
/**
 * Other option interface.
 */
public class OtherOptionAttribute
{
    /**
     * The number of the other option.
     */
    protected final int number;

    /**
     * The value of the other option.
     */
    protected final byte[] value;

    /**
     * Default constructor.
     */
    public OtherOptionAttribute()
    {
        super();
        this.number= 0;
        this.value= OptionUtils.EMPTY_BYTES;
    }

    /**
     * Constructor setting members.
     * @param number The number of the option.
     * @param value The value of the option.
     */
    public OtherOptionAttribute( int number, byte[] value )
    {
        super();
        this.number= number;
        this.value= value;
    }

    /**
     * Get the number of this other option.
     *
     * @return The option number.
     */
    public int getNumber()
    {
        return number;
    }

    /**
     * Get the value of this other option.
     *
     * @return The option value if any, otherwise null.
     */
    public InputStream getValue()
    {
        return OptionUtils.toInputStream( value );
    }

    /**
     * Get the value as number.
     * @return long containing the value.
     */
    public long getValueAsNumber()
    {
        return OptionUtils.toLong( value );
    }

    /**
     * Get the option value as hexadecimal string.
     * Hexadecimal values a-f will be lower case.
     * @return The string containing the hexadecimal representation or empty string when the value is empty.
     */
    public String getValueAsHexString()
    {
        return OptionUtils.toHexString( value );
    }

    /**
     * Get value as UTF-8 string.
     * @return The UTF-8 string interpretation.
     */
    public String getValueAsString()
    {
        return OptionUtils.toString( value );
    }

    /**
     * Checks if option is critical.
     *
     * @return {@code true} if is option critical, otherwise {@code false}.
     */
    public boolean isCritical()
    {
        return OptionUtils.isCritical( getNumber() );
    }

    /**
     * Checks if option with this number is unsafe.
     *
     * @return {@code true}  if this is an unsafe option, otherwise {@code false}.
     */
    public boolean isUnSafe()
    {
        return OptionUtils.isUnSafe( getNumber() );
    }

    /**
     * Checks if option with this number is a NoCacheKey option.
     *
     * @return {@code true} if this is NoCacheKey option, otherwise {@code false}.
     */
    public boolean isNoCacheKey()
    {
        return OptionUtils.isNoCacheKey( getNumber() );
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
     * Returns a hash code value for the object.
     */
    @Override
    public int hashCode()
    {
        final int prime= 31;
        int result= 1;
        result= prime * result + Arrays.hashCode( value );
        result= prime * result + Objects.hash( number );
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
        if ( !( obj instanceof OtherOptionAttribute ) )
        {
            return false;
        }
        OtherOptionAttribute other= (OtherOptionAttribute) obj;
        return getNumber() == other.getNumber() && Arrays.equals( value, other.value );
    }
}
