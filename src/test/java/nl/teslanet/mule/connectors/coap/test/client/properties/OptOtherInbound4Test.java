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


import nl.teslanet.mule.connectors.coap.api.ReceivedResponseAttributes;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.Option;


/**
 * Test inbound other option property
 *
 */
public class OptOtherInbound4Test extends AbstractOtherOptionInboundPropertyTestCase
{

    /**
     * Test other option
     * @return the option to use in test
     */
    @Override
    protected Option getOption()
    {
        byte[] value= { (byte) 0x12, (byte) 0x67, (byte) 0x45, (byte) 0x45 };
        return new Option( 65008, value );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractPropertiesTest#getPropertyName()
     */
    @Override
    protected String getPropertyName()
    {
        return "coap.opt.other." + getOption().getNumber();
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractInboundPropertyTestCase#getPropertyType()
     */
    @Override
    protected PropertyType getPropertyType()
    {
        return PropertyType.ByteArray;
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractPropertiesTest#getExpectedInboundPropertyValue()
     */
    @Override
    protected Object getExpectedInboundPropertyValue()
    {
        return getOption().getValue();
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractInboundPropertyTestCase#getStrategy()
     */
    @Override
    protected OptionStrategy getStrategy()
    {
        return new OptOtherStrategy( getOption() );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.test.client.properties.AbstractOtherOptionInboundPropertyTestCase#fetchInboundProperty(nl.teslanet.mule.connectors.coap.api.ReceivedResponseAttributes)
     */
    @Override
    protected Object fetchInboundProperty( ReceivedResponseAttributes attributes )
    {
        return attributes.getOptions().getOtherOptions().get( "65008" );
    }
}
