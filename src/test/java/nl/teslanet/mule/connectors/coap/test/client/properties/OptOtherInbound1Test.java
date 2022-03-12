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

/**
 * Test inbound other option property
 *
 */
public class OptOtherInbound1Test extends AbstractOtherOptionInboundPropertyTestCase
{
    byte[][] values= {{ (byte) 0x12, (byte) 0xFF, (byte) 0x45 }};

    @Override
    protected String getPropertyName()
    {
        return "coap.opt.other." + getOptionNumber();
    }

    @Override
    protected int getOptionNumber()
    {
        return 65012;
    }

    @Override
    protected byte[][] getOptionValues()
    {
        return  values;
    }
}
