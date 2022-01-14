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
package nl.teslanet.mule.connectors.coap.internal.query;

import static java.util.Objects.hash;

import java.util.Objects;

/**
 * CoAP Query Parameter as attribute. 
 * This class is immutable.
 */
public class DefaultQueryParamAttribute
{
    /**
     * The key of the query parmeter.
     */
    protected String key= null;

    /**
     * The value of the query parameter.
     */
    protected String value= null;

    /**
     * The constructor.
     * @param key
     * @param value
     */
    public DefaultQueryParamAttribute( String key, String value )
    {
        super();
        this.key= key;
        this.value= value;
    }

    /**
     * @return the key of the query parameter.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * @return the value of the query parameter.
     */
    public String getValue()
    {
        return value;
    }
    
    /**
     * @return {@code true} when parameter has a key that is not empty, otherwise {@code false}.
     */

    public boolean hasKey()
    {
        return( getKey() != null && !getKey().isEmpty() );
    }

    /**
     * @return {@code true} when parameter has a value that is not empty, otherwise {@code false}.
     */
    public boolean hasValue()
    {
        return( getValue() != null && !getValue().isEmpty() );
    }

    /**
     * Check equality.
     */
    @Override
    public boolean equals( Object object )
    {
        if ( object instanceof DefaultQueryParamAttribute )
        {
            DefaultQueryParamAttribute other= (DefaultQueryParamAttribute) object;
            return Objects.equals( getKey(), other.getKey() ) && Objects.equals( getValue(), other.getValue() );
        }
        return false;
    }

    /**
     * Get the hash code of this object.
     */
    @Override
    public int hashCode()
    {
        return hash( getKey(), getValue() );
    }
}
