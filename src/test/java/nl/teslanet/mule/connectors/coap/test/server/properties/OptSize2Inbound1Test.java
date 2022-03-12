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
package nl.teslanet.mule.connectors.coap.test.server.properties;


import org.eclipse.californium.core.coap.OptionSet;


/**
 * Test inbound size2 option
 *
 */
public class OptSize2Inbound1Test extends AbstractInboundPropertyTestcase
{

    @Override
    protected void addOption( OptionSet options )
    {
        options.setSize2( 0 );
    }

    @Override
    protected String getPropertyName()
    {
        return "coap.opt.size2";
    }

    @Override
    protected Object getExpectedPropertyValue()
    {
        return new Boolean( true );
    }

    @Override
    protected Object getReplacementValue() throws Exception
    {
        return Integer.valueOf( 1024 );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.test.server.properties.AbstractInboundPropertyTestcase#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/properties/testserver-options-size2.xml";
    };
}
