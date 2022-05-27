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


import java.util.LinkedList;

import nl.teslanet.mule.connectors.coap.api.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.query.QueryParamAttribute;


/**
 * Test inbound location query property, multiple values
 *
 */
public class OptLocationQueryInbound1mTest extends AbstractInboundPropertyTestCase
{

    /**
     * Test value
     * @return the value to use in test
     */
    private LinkedList< QueryParamAttribute > getValue()
    {
        LinkedList< QueryParamAttribute > list= new LinkedList<>();
        list.add( new QueryParamAttribute( "first", "1" ) );
        list.add( new QueryParamAttribute( "second", "2" ) );
        list.add( new QueryParamAttribute( "third", "3" ) );

        return list;
    }

    @Override
    protected String getPropertyName()
    {
        return "coap.opt.location_query";
    }

    @Override
    protected PropertyType getPropertyType()
    {
        return PropertyType.CollectionOfObject;
    }

    @Override
    protected Object getExpectedInboundPropertyValue()
    {
        return getValue();
    }

    @Override
    protected OptionStrategy getStrategy()
    {
        return new OptLocationQueryStrategy( getValue() );
    }

    @Override
    protected Object fetchInboundProperty( CoapResponseAttributes attributes )
    {
        return attributes.getOptions().getLocationQuery();
    }
}
