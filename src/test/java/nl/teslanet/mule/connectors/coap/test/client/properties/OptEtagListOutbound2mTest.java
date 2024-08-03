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
package nl.teslanet.mule.connectors.coap.test.client.properties;


import java.util.LinkedList;

import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.TypedValue;

import nl.teslanet.mule.connectors.coap.api.entity.EntityTag;
import nl.teslanet.mule.connectors.coap.api.entity.EntityTagException;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultEntityTag;


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
    private LinkedList< DefaultEntityTag > getValue() throws EntityTagException
    {
        LinkedList< DefaultEntityTag > list= new LinkedList< DefaultEntityTag >();
        list.add( new DefaultEntityTag( 0xA0L ) );
        list.add( new DefaultEntityTag( 0x11FFL ) );
        list.add( new DefaultEntityTag( 0x1122334455667788L ) );

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
        LinkedList< EntityTag > list= new LinkedList< EntityTag >();
        for ( DefaultEntityTag value : getValue() )
        {
            EntityTag etag= new EntityTag();
            etag.setValue( new TypedValue< Object >( value.getValue(), DataType.fromObject( value.getValue() ) ) );
            list.add( etag );
        }
        return list;
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
