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
package nl.teslanet.mule.connectors.coap.test.server.properties;


import org.eclipse.californium.core.coap.OptionSet;
import org.junit.Ignore;

@Ignore
public class OptLocationQueryInbound2Test extends AbstractInboundPropertyTestcase
{
    @Override
    protected void addOption( OptionSet options )
    {
        options.setLocationQuery( "first=1&second=2" );
    }

    @Override
    protected String getPropertyName()
    {
        return "coap.opt.location_query";
    }

    @Override
    protected Object getExpectedPropertyValue()
    {
        //TODO also prepending ?
        return new String( "first=1&second=2" );
    }
    
    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/properties/testserver-options-locationQuery.xml";
    };
}
