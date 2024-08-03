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
package nl.teslanet.mule.connectors.coap.api.entity;


import java.io.Serializable;
import java.util.Arrays;

import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;


/**
 * The Entity-tag attribute. 
 * This class is actually an interface but Mule needs a concrete class.
 */
public class EntityTagAttribute implements Comparable< EntityTagAttribute >, Serializable
{
    /**
     * Class serial version.
     */
    private static final long serialVersionUID= 1L;

    /**
     * Get the Entity-tag value as byte array.
     * @return byte array containing the Entity-tag value.
     */
    public byte[] getValue()
    {
        return OptionUtils.EMPTY_BYTES;
    }

    /**
     * Get the Entity-tag value as long.
     * @return Long containing the Entity-tag value.
     */
    public long getValueAsNumber()
    {
        return 0L;
    }

    /**
     * Get Entity-tag value as UTF-8 string.
     * @return The UTF-8 string interpretation.
     */
    public String getValueAsString()
    {
        return OptionUtils.EMPTY_STRING;
    }

    /**
     * Get the Entity-tag value as hexadecimal string.
     * Hexadecimal values a-f will be lower case.
     * @return The string containing the hexadecimal representation or empty string when Entity-tag has no value.
     */
    public String getValueAsHex()
    {
        return OptionUtils.EMPTY_STRING;
    }

    /**
     * The string representation of the object.
     */
    @Override
    public String toString()
    {
        return "Entity-tag{ " + getValueAsHex() + " }";
    }

    /**
     * Compares this object with the specified object for order.
     */
    @Override
    public int compareTo( EntityTagAttribute other )
    {
        if ( other == null ) return 1;
        if ( this == other ) return 0;
        if ( this.getValue().length < other.getValue().length ) return -1;
        if ( this.getValue().length > other.getValue().length ) return 1;
        for ( int i= 0; i < this.getValue().length; i++ )
        {
            if ( this.getValue()[i] < other.getValue()[i] ) return -1;
            if ( this.getValue()[i] > other.getValue()[i] ) return 1;
        }
        return 0;
    }

    /**
     * Returns a hash code value for the object.
     */
    @Override
    public int hashCode()
    {
        return Arrays.hashCode( getValue() );
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
        if ( !( obj instanceof EntityTagAttribute ) )
        {
            return false;
        }
        EntityTagAttribute other= (EntityTagAttribute) obj;
        return Arrays.equals( getValue(), other.getValue() );
    }
}