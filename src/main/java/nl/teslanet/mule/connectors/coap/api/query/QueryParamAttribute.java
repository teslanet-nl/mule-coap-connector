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
package nl.teslanet.mule.connectors.coap.api.query;


/**
 * CoAP Query Parameter as attribute. 
 * This class is immutable.
 */
public class QueryParamAttribute extends AbstractQueryParam
{
    /**
     * The key of the query parmeter.
     */
    protected String key= null;

    /**
     * The value of the query parameter.
     */
    protected String value= null;

    /**r
     * The Constructor.
     * @param key The queryparameter key.
     * @param value The optional queryparameter value.
     */
    public QueryParamAttribute( String key, String value )
    {
        super();
        this.key= key;
        this.value= value;
    }

    /**
     * @return the key of the query parameter.
     */
    @Override
    public String getKey()
    {
        return key;
    }

    /**
     * @return the value of the query parameter.
     */
    @Override
    public String getValue()
    {
        return value;
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals( Object object )
    {
        if ( object instanceof QueryParamAttribute )
        {
            return super.equals( object );
        }
        return false;
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return super.hashCode();
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
