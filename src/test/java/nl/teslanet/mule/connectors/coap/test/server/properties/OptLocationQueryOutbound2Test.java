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
package nl.teslanet.mule.connectors.coap.test.server.properties;


import java.util.LinkedList;

import org.eclipse.californium.core.coap.OptionSet;

import nl.teslanet.mule.connectors.coap.api.query.QueryParam;


public class OptLocationQueryOutbound2Test extends AbstractOutboundPropertyTestcase
{
    @Override
    protected String getPropertyName()
    {
        return "coap.opt.location_query";
    }

    @Override
    protected boolean optionValueIsCollectionOfStringable()
    {
        return true;
    }

    @Override
    protected Object fetchOption( OptionSet options )
    {
        return options.getLocationQuery();
    }

    @Override
    protected Object getPropertyValue()
    {
        LinkedList< QueryParam > list= new LinkedList<>();
        list.add( new QueryParam( "first", "1" ) );
        list.add( new QueryParam( "second", "2" ) );
        list.add( new QueryParam( "third", null ) );

        return list;
    }

    @Override
    protected Object getExpectedOptionValue()
    {
        LinkedList< String > list= new LinkedList< String >();
        list.add( "first=1" );
        list.add( "second=2" );
        list.add( "third" );

        return list;
    }
    
    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/properties/testserver-options-locationQuery.xml";
    };
}
