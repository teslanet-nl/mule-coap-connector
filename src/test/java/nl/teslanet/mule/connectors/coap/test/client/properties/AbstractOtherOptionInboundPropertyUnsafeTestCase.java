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


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import nl.teslanet.mule.connectors.coap.api.attributes.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.options.OtherOptionAttribute;


/**
 * Abstract class for testing inbound other options
 *
 */
public abstract class AbstractOtherOptionInboundPropertyUnsafeTestCase
    extends AbstractOtherOptionInboundPropertyTestCase
{
    abstract protected boolean isUnsafe();

    /**
     * Fetch the inbound property from the response attributes.
     */
    @Override
    protected Object fetchInboundProperty( CoapResponseAttributes attributes )
    {
        @SuppressWarnings( "unchecked" )
        List< OtherOptionAttribute > options= (List< OtherOptionAttribute >) super.fetchInboundProperty( attributes );
        List< Boolean > unsafes= new LinkedList<>();
        for ( OtherOptionAttribute otherOption : options )
        {
            unsafes.add( otherOption.isUnsafe() );
        }
        return Collections.unmodifiableList( unsafes );
    }

    /**
     * Get expected inbound property value.
     */
    @Override
    protected Object getExpectedInboundPropertyValue()
    {
        LinkedList< Boolean > unsafes= new LinkedList<>();
        for ( int i= 0; i < getOptionValues().length; i++ )
        {
            unsafes.add( isUnsafe() );
        }
        return Collections.unmodifiableList( unsafes );
    }
}