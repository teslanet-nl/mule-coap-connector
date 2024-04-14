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


import org.eclipse.californium.core.coap.Option;

import nl.teslanet.mule.connectors.coap.test.utils.TestOptions;


/**
 * Test outbound other property, bytearray value
 *
 */
public class OptOtherOutbound1Test extends AbstractOutboundPropertiesTestCase
{
    /**
     * Test other option
     * @return the option to use in test
     */
    private Option getOption()
    {
        byte[] value= "sometestvalue".getBytes();
        return new Option( TestOptions.OTHER_OPTION_65008, value );
    }

    @Override
    protected String getPropertyName()
    {
        return "coap.opt.other";
    }

    @Override
    protected String getOutboundOptionAlias()
    {
        return getOption().getDefinition().getName();
    }

    @Override
    protected Object getOutboundPropertyValue()
    {
        return getOption().getValue();
    }

    @Override
    protected OptionStrategy getStrategy()
    {
        return new OptOtherStrategy( getOption() );
    }
}
