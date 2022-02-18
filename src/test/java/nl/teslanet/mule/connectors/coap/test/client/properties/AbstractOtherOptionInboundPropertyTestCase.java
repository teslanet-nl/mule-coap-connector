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
package nl.teslanet.mule.connectors.coap.test.client.properties;


import java.util.Collections;
import java.util.LinkedList;

import nl.teslanet.mule.connectors.coap.api.CoAPResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.options.OtherOptionAttribute;


/**
 * Abstract class for testing inbound other options
 *
 */
public abstract class AbstractOtherOptionInboundPropertyTestCase extends AbstractInboundPropertyTestCase
{
    abstract protected int getOptionNumber();

    abstract protected byte[][] getOptionValues();

    /**
     * Fetch the inbound property from the response attributes.
     */
    @Override
    protected Object fetchInboundProperty( CoAPResponseAttributes attributes )
    {
        LinkedList< OtherOptionAttribute > found= new LinkedList<>();
        for ( OtherOptionAttribute otherOption : attributes.getOptions().getOtherOptions() )
        {
            if ( otherOption.getNumber() == getOptionNumber() )
            {
                found.add( otherOption );
            }
        }
        return Collections.unmodifiableList( found );
    }

    /**
     * Get expected inbound property value.
     */
    @Override
    protected Object getExpectedInboundPropertyValue()
    {
        LinkedList< OtherOptionAttribute > list= new LinkedList<>();
        for ( int i= 0; i < getOptionValues().length; i++ )
        {
            OtherOptionAttribute otherOption= new OtherOptionAttribute( getOptionNumber(), getOptionValues()[i] );
            list.add( otherOption );
        }
        return Collections.unmodifiableList( list );
    }

    /**
     * The assertion is done on collection of objects.
     * @return The type of the property expected.
     */
    @Override
    protected PropertyType getPropertyType()
    {
        return PropertyType.CollectionOfObject;
    }
    
    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.AbstractInboundPropertyTestCase#getStrategy()
     */
    @Override
    protected OptionStrategy getStrategy()
    {
        return new OptOtherStrategy( getOptionNumber(), getOptionValues() );
    }

}