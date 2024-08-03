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
import java.util.ArrayList;

import org.eclipse.californium.core.coap.option.OpaqueOptionDefinition;

import nl.teslanet.mule.connectors.coap.api.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;
import nl.teslanet.mule.connectors.coap.api.options.OtherOptionAttribute;
import nl.teslanet.mule.connectors.coap.test.utils.TestOtherOptionAttribute;


/**
 * Abstract class for testing inbound other options
 *
 */
public abstract class AbstractOtherOptionInboundPropertyTestCase extends AbstractInboundPropertyTestCase
{
    abstract protected String getOptionAlias();

    abstract protected int getOptionNumber();

    abstract protected byte[][] getOptionValues();

    /**
     * Fetch the inbound property from the response attributes.
     */
    @Override
    protected Object fetchInboundProperty( CoapResponseAttributes attributes )
    {
        ArrayList< OtherOptionAttribute > list= new ArrayList<>();
        for ( OtherOptionAttribute option : attributes.getOptions().getOther() )
        {
            if ( getOptionAlias().equals( option.getAlias() ) )
            {
                OtherOptionAttribute otherOption;
                try
                {
                    otherOption= new TestOtherOptionAttribute(
                        new OpaqueOptionDefinition( getOptionNumber(), getOptionAlias() ),
                        OptionUtils.toBytesFromHex( option.getValueAsHex() )
                    );
                }
                catch ( OptionValueException e )
                {
                    throw new RuntimeException( e );
                }
                list.add( otherOption );
            }
        }
        return Collections.unmodifiableList( list );
    }

    /**
     * Get expected inbound property value.
     */
    @Override
    protected Object getExpectedInboundPropertyValue()
    {
        ArrayList< OtherOptionAttribute > list= new ArrayList<>();
        for ( int i= 0; i < getOptionValues().length; i++ )
        {
            OtherOptionAttribute otherOption= new TestOtherOptionAttribute( new OpaqueOptionDefinition( getOptionNumber(), getOptionAlias() ), getOptionValues()[i] );
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