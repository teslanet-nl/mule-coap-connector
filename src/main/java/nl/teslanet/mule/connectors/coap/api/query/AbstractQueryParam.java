/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2022 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.api.query;


import static java.util.Objects.hash;

import java.util.Objects;


/**
 * Abstract class represents a query parameter that can have a key and a value. 
 *
 */
public abstract class AbstractQueryParam
{
    /**
     * @return the key of the query parameter.
     */
    public abstract String getKey();

    /**
     * @return the value of the query parameter.
     */
    public abstract String getValue();

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
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals( Object object )
    {
        if ( object instanceof AbstractQueryParam )
        {
            AbstractQueryParam other= (AbstractQueryParam) object;
            return Objects.equals( getKey(), other.getKey() ) && Objects.equals( getValue(), other.getValue() );
        }
        return false;
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return hash( getKey(), getValue() );
    }
    
    /**
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        String actualValue= getValue();
        if ( actualValue != null )
        {
            return getKey() + "=" + actualValue;
        }
        else
        {
            return getKey();
        }
    }
}
