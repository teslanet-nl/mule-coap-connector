/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2023 (teslanet.nl) Rogier Cobben
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

import nl.teslanet.mule.connectors.coap.api.entity.EntityTag;
import nl.teslanet.mule.connectors.coap.api.entity.EntityTagException;


/**
 * Test outbound Etag list property, multiple values
 *
 */
public class OptEtagListOutbound2mTest extends AbstractOutboundPropertiesTestCase
{
    /**
     * Test value
     * @return the value to use in test
     * @throws EntityTagException 
     */
    private LinkedList< EntityTag > getValue() throws EntityTagException
    {
        LinkedList< EntityTag > list= new LinkedList< EntityTag >();
        list.add( new EntityTag( 0xA0L ) );
        list.add( new EntityTag( 0x11FFL ) );
        list.add( new EntityTag( 0x1122334455667788L ) );

        return list;
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractPropertiesTest#getPropertyName()
     */
    @Override
    protected String getPropertyName()
    {
        return "coap.opt.etag.list";
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractPropertiesTest#getOutboundPropertyValue()
     */
    @Override
    protected Object getOutboundPropertyValue() throws EntityTagException
    {
        LinkedList< byte[] > propertyValue= new LinkedList< byte[] >();
        for ( EntityTag value : getValue() )
        {
            propertyValue.add( value.getValue() );
        }
        return propertyValue;
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractOutboundPropertiesTest#getStrategy()
     */
    @Override
    protected OptionStrategy getStrategy() throws EntityTagException
    {
        return new OptEtagListStrategy( getValue() );
    }
}
