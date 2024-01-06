/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2023 (teslanet.nl) Rogier Cobben
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
import nl.teslanet.mule.connectors.coap.api.entity.EntityTag;
import nl.teslanet.mule.connectors.coap.api.entity.EntityTagException;


/**
 * Test inbound etag property, multiple values
 *
 */
public class OptEtagInbound1Test extends AbstractInboundPropertyTestCase
{

    /**
     * Test value
     * @return the value to use in test
     * @throws InvalidEntityTagException 
     */
    private EntityTag getValue() throws EntityTagException
    {
        return new EntityTag( 0x1122334455667788L );
    }

    @Override
    protected String getPropertyName()
    {
        return "coap.opt.etag";
    }

    @Override
    protected PropertyType getPropertyType()
    {
        return PropertyType.ETag;
    }

    @Override
    protected Object getExpectedInboundPropertyValue() throws EntityTagException
    {
        return getValue();
    }

    @Override
    protected OptionStrategy getStrategy() throws EntityTagException
    {
        return new OptEtagStrategy( getValue() );
    }

    @Override
    protected Object fetchInboundProperty( CoapResponseAttributes attributes )
    {
        return attributes.getOptions().getEtag();
    }
}
