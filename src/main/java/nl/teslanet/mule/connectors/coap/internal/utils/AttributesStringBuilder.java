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
package nl.teslanet.mule.connectors.coap.internal.utils;


import java.util.Collection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * String builder for attributes.
 * The builder omits fields with null values for brevity.
 */
public class AttributesStringBuilder extends ToStringBuilder
{
    /**
     * Constructs a builder for the specified object.
     *
     * @param object The Object to build a {@code toString} for, not recommended to be null
     */
    public AttributesStringBuilder( final Object object )
    {
        super( object, new AttributeToStringStyle(), null );
    }

    /**
     * Constructs a builder for the specified object using the defined output style and build buffer.
     * @param object The Object to build a {@code toString} for, not recommended to be null
     * @param style The style of the {@code toString} to create.
     * @param buffer The string buffer to use for building.
     */
    public AttributesStringBuilder( Object object, ToStringStyle style, StringBuffer buffer )
    {
        super( object, style, buffer );
    }

    /**
     * Append to the {@code toString} a {@code boolean}
     * value when true.
     *
     * @param fieldName  the field name
     * @param value  the value to add to the {@code toString}
     * @return {@code this} instance.
     */
    @Override
    public ToStringBuilder append( final String fieldName, final boolean value )
    {
        if ( value )
        {
            super.append( fieldName, value );
        }
        return this;
    }

    /**
     * Append to the {@code toString} an {@link Object}
     * value when not null or an empty collection.
     *
     * @param fieldName  the field name
     * @param obj  the value to add to the {@code toString}
     * @return {@code this} instance.
     */
    @Override
    public ToStringBuilder append( final String fieldName, final Object obj )
    {
        if ( obj != null && ( !( obj instanceof Collection< ? > ) || !( ( (Collection< ? >) obj ).isEmpty() ) ) )
        {
            super.append( fieldName, obj );
        }
        return this;
    }

    /**
     * Append to the {@code toString} an {@link Object}
     * value.
     *
     * @param fieldName  the field name
     * @param obj  the value to add to the {@code toString}
     * @param fullDetail  {@code true} for detail,
     *  {@code false} for summary info
     * @return {@code this} instance.
     */
    @Override
    public ToStringBuilder append( final String fieldName, final Object obj, final boolean fullDetail )
    {
        super.append( fieldName, obj, fullDetail );
        return this;
    }
}