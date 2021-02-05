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
package nl.teslanet.mule.connectors.coap.test.client.properties;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import nl.teslanet.shaded.org.eclipse.californium.core.coap.Request;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.Response;


/**
 * The strategy to use by the testserver
 */
public class OptUriPathStrategy implements OptionStrategy
{
    private LinkedList< String > value;

    /**
     * Constructor 
     * @param value the test value
     */
    public OptUriPathStrategy( LinkedList< String > value )
    {
        this.value= value;
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.OptionStrategy#setOption(org.eclipse.californium.core.coap.Response)
     */
    @Override
    public void setOption( Response response )
    {
        for ( String segment : value )
        {
            response.getOptions().addUriPath( segment );
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.OptionStrategy#validateOption(org.eclipse.californium.core.coap.Request)
     */
    @Override
    public boolean validateOption( Request request )
    {
        List< String > uriPath= request.getOptions().getUriPath();
        if ( uriPath.size() != value.size() )
        {
            return false;
        }
        Iterator< String > it1= value.iterator();
        Iterator< String > it2= uriPath.iterator();
        while ( it1.hasNext() )
        {
            if ( !it1.next().equals( it2.next() ) ) return false;
        }
        return true;
    }
}
