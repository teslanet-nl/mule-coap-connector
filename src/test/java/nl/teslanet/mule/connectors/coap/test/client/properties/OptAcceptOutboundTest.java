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


import nl.teslanet.shaded.org.eclipse.californium.core.coap.Request;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.Response;


/**
 * Test Accept option
 *
 */
public class OptAcceptOutboundTest extends AbstractOutboundPropertiesTestCase
{
    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractPropertiesTest#getPropertyName()
     */
    @Override
    protected String getPropertyName()
    {
        return "coap.opt.accept";
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractPropertiesTest#getOutboundPropertyValue()
     */
    @Override
    protected Object getOutboundPropertyValue()
    {
        return new Integer( 41 );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractPropertiesTest#getStrategy()
     */
    @Override
    protected OptionStrategy getStrategy()
    {
        return new Strategy();
    }

    /**
     * The strategy to use by the testserver
     */
    public class Strategy implements OptionStrategy
    {

        @Override
        public void setOption( Response response )
        {
            response.getOptions().setAccept( 41 );
        }

        @Override
        public boolean validateOption( Request request )
        {
            int accept= request.getOptions().getAccept();
            return accept == 41;
        }
    }
}
