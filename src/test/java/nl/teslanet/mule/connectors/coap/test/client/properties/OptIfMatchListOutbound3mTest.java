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
package nl.teslanet.mule.connectors.coap.test.client.properties;


import java.util.LinkedList;

import nl.teslanet.mule.connectors.coap.api.options.ETag;
import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;


/**
 * Test outbound If Match list property, multiple String value
 *
 */
public class OptIfMatchListOutbound3mTest extends AbstractOutboundPropertiesTest
{
    /**
     * Test value
     * @return the value to use in test
     * @throws InvalidETagException 
     */
    private LinkedList< ETag > getValue() throws InvalidETagException
    {
        LinkedList< ETag > list= new LinkedList< ETag >();
        list.add( new ETag( "68656C6C6F" ) );
        list.add( new ETag( "6F6C6C61" ) );
        list.add( new ETag( "686F69" ) );

        return list;
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractPropertiesTest#getPropertyName()
     */
    @Override
    protected String getPropertyName()
    {
        return "coap.opt.if_match.list";
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractPropertiesTest#getOutboundPropertyValue()
     */
    @Override
    protected Object getOutboundPropertyValue() throws InvalidETagException
    {
        LinkedList< String > propertyValue= new LinkedList< String >();
        for ( ETag value : getValue() )
        {
            propertyValue.add( new String( value.asBytes() ) );
        }
        return propertyValue;
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractOutboundPropertiesTest#getStrategy()
     */
    @Override
    protected OptionStrategy getStrategy() throws InvalidETagException
    {
        return new OptIfMatchListStrategy( getValue() );
    }
}
