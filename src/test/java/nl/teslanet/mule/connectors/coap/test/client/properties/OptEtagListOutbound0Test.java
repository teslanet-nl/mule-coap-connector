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


import java.util.ArrayList;
import java.util.LinkedList;

import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.TypedValue;

import nl.teslanet.mule.connectors.coap.api.error.RequestException;
import nl.teslanet.mule.connectors.coap.api.options.EntityTag;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultEntityTag;


/**
 * Test outbound Etag list property, single null value
 *
 */
public class OptEtagListOutbound0Test extends AbstractOutboundPropertiesTestCase
{
    /**
     * @return The test value.
     * @throws OptionValueException
     */
    private Object getValue()
    {
        return "";
    }

    /**
     * Get property name.
     */
    @Override
    protected String getPropertyName()
    {
        return "coap.opt.etag.list";
    }

    /**
     * null ETag is not allowed.
     * @return The IllegalArgumentException
     */
    @Override
    protected Exception getExpectedException()
    {
        return new RequestException( "CoAP Client { config } failed to execute request." );
    }

    /**
     * Get property value.
     */
    @Override
    protected Object getOutboundPropertyValue() throws OptionValueException
    {
        LinkedList< EntityTag > list= new LinkedList< EntityTag >();
        EntityTag etag= new EntityTag();
        etag.setValue( new TypedValue< Object >( getValue(), DataType.fromObject( getValue() ) ) );
        list.add( etag );
        return list;
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractOutboundPropertiesTest#getStrategy()
     */
    @Override
    protected OptionStrategy getStrategy() throws OptionValueException
    {
        return new OptEtagListStrategy( new ArrayList< DefaultEntityTag >() );
    }
}
