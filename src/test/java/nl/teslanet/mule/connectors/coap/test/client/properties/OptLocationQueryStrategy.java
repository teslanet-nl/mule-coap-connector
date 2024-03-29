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
package nl.teslanet.mule.connectors.coap.test.client.properties;


import java.util.Iterator;
import java.util.List;

import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;

import nl.teslanet.mule.connectors.coap.api.query.QueryParamAttribute;


/**
 * The strategy to use by the testserver
 */
public class OptLocationQueryStrategy implements OptionStrategy
{
    private List< QueryParamAttribute > value;

    /**
     * Constructor 
     * @param value the test value
     */
    public OptLocationQueryStrategy( List< QueryParamAttribute > value )
    {
        this.value= value;
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.OptionStrategy#setOption(org.eclipse.californium.core.coap.Response)
     */
    @Override
    public void setOption( Response response )
    {
        for ( QueryParamAttribute segment : value )
        {
            response.getOptions().addLocationQuery( segment.toString() );
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.OptionStrategy#validateOption(org.eclipse.californium.core.coap.Request)
     */
    @Override
    public boolean validateOption( Request request )
    {
        List< String > locationQuery= request.getOptions().getLocationQuery();
        if ( locationQuery.size() != value.size() )
        {
            return false;
        }
        Iterator< QueryParamAttribute > it1= value.iterator();
        Iterator< String > it2= locationQuery.iterator();
        while ( it1.hasNext() )
        {
            if ( !it1.next().toString().equals( it2.next() ) ) return false;
        }
        return true;
    }
}
