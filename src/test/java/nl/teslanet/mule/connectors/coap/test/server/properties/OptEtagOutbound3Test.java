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


import nl.teslanet.mule.connectors.coap.api.error.InvalidEntityTagException;
import nl.teslanet.mule.connectors.coap.api.options.EntityTag;
import org.eclipse.californium.core.coap.OptionSet;


public class OptEtagOutbound3Test extends AbstractOutboundPropertyTestcase
{
    @Override
    protected String getPropertyName()
    {
        return "coap.opt.etag";
    }

    @Override
    protected Object fetchOption( OptionSet options )
    {
        return options.getETags().get( 0 );
    }

    @Override
    protected Object getPropertyValue() throws InvalidEntityTagException
    {
        return new String( "hello" );
    }

    @Override
    protected Object getExpectedOptionValue() throws InvalidEntityTagException
    {
        return new EntityTag( 0x68656C6C6FL ).getValue();
    }

    @Override
    protected boolean optionValueIsByteArray()
    {
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/properties/testserver-options-etag-outbound.xml";
    };
}
