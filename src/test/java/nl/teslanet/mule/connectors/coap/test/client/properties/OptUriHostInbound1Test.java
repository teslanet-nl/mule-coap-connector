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

import nl.teslanet.mule.connectors.coap.api.CoapResponseAttributes;

/**
 * Test inbound uri host property
 *
 */
public class OptUriHostInbound1Test extends AbstractInboundPropertyTestCase
{

    /**
     * Test value
     * @return the value to use in test
     */
    private String getValue()
    {
        return new String( "some.server.org" );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractPropertiesTest#getPropertyName()
     */
    @Override
    protected String getPropertyName()
    {
        return "coap.opt.uri_host";
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractInboundPropertyTestCase#getPropertyType()
     */
    @Override
    protected PropertyType getPropertyType()
    {
        return PropertyType.Object;
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractPropertiesTest#getExpectedInboundPropertyValue()
     */
    @Override
    protected Object getExpectedInboundPropertyValue()
    {
        return getValue();
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractInboundPropertyTestCase#getStrategy()
     */
    @Override
    protected OptionStrategy getStrategy()
    {
        return new OptUriHostStrategy( getValue() );
    }

    @Override
    protected Object fetchInboundProperty( CoapResponseAttributes attributes )
    {
        return "XXX";
    }
}
