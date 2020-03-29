/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2020 (teslanet.nl) Rogier Cobben
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


import static java.util.Objects.hash;

import java.util.Objects;


/**
 * Abstract class representing CoAP other options. 
 *
 */
public abstract class AbstractOtherOption
{
    /**
     * @return the key of the query parameter.
     */
    abstract public Integer getOptionNr();

    //TODO support other objects / arrays
    /**
     * @return the value of the query parameter.
     */
    abstract public Object getValue();

    /**
     * @return {@code true} when parameter has a key that is not empty, otherwise {@code false}.
     */
    public boolean hasKey()
    {
        return( getOptionNr() != null );
    }

    /**
     * @return {@code true} when parameter has a value that is not empty, otherwise {@code false}.
     */
    public boolean hasValue()
    {
        return( getValue() != null );
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object object )
    {
        if ( object instanceof AbstractOtherOption )
        {
            AbstractOtherOption other= (AbstractOtherOption) object;
            return Objects.equals( getOptionNr(), other.getOptionNr() ) && Objects.equals( getValue(), other.getValue() );
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return hash( getOptionNr(), getValue() );
    }
}
