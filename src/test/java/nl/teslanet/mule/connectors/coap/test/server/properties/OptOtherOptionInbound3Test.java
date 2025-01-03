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
package nl.teslanet.mule.connectors.coap.test.server.properties;


import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.junit.Before;

import nl.teslanet.mule.connectors.coap.test.utils.TestOptions;


/**
 * Test inbound other option
 *
 */
public class OptOtherOptionInbound3Test extends AbstractInboundPropertyTestcase
{
    private Option option;

    @Before
    public void initializeOption()
    {
        byte[] value= { (byte) 0x12, (byte) 0xFF, (byte) 0x45 };
        option= new Option( TestOptions.OTHER_OPTION_65009, value );
    }

    @Override
    protected void addOption( OptionSet options )
    {
        options.addOption( option );
    }

    @Override
    protected String getPropertyName()
    {
        return "coap.opt.other.65009";
    }

    @Override
    protected boolean propertyValueIsByteArray()
    {
        return true;
    }

    @Override
    protected Object getExpectedPropertyValue()
    {
        byte[] value= { (byte) 0x12, (byte) 0xFF, (byte) 0x45 };
        return value;
    }

    protected boolean getExpectedSuccess()
    {
        return false;
    }

    @Override
    protected ResponseCode getExpectedResponseCode( Code requestCode )
    {
        return ResponseCode.BAD_OPTION;
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.test.server.properties.AbstractInboundPropertyTestcase#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/properties/testserver-options-other65009-inbound.xml";
    };
}
