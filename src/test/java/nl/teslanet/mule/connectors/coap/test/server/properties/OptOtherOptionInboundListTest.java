/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2024 (teslanet.nl) Rogier Cobben
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


import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;

import nl.teslanet.mule.connectors.coap.api.options.OtherOptionAttribute;
import nl.teslanet.mule.connectors.coap.test.utils.TestOptions;


/**
 * Test inbound other option
 *
 */
public class OptOtherOptionInboundListTest extends AbstractInboundPropertyTestcase
{
    static final private ArrayList< String > values= new ArrayList<>();
    static
    {
        values.add( "one" );
        values.add( "two" );
        values.add( "three" );
    }

    @Override
    protected void addOption( OptionSet options )
    {
        for ( String value : values )
        {
            options.addOption( new Option( TestOptions.OTHER_OPTION_65003, value.getBytes() ) );
        }
    }

    @Override
    protected String getPropertyName()
    {
        return "coap.opt.other.65003.list";
    }

    @Override
    protected boolean optionValueIsCollectionOfStringable()
    {
        return true;
    }

    @Override
    protected Object fetchOption( Object object )
    {
        @SuppressWarnings( "unchecked" )
        Collection< OtherOptionAttribute > otherOptions= (Collection< OtherOptionAttribute >) object;
        ArrayList< String > results= new ArrayList<>();
        for ( OtherOptionAttribute option : otherOptions )
        {
            results.add( option.getValueAsString() );
        }
        return results;
    }

    @Override
    protected Object getExpectedPropertyValue()
    {
        return values;
    }

    @Override
    protected Object getReplacementValue() throws Exception
    {
        return new ArrayList<>();
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.test.server.properties.AbstractInboundPropertyTestcase#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/properties/testserver-options-other65003-list.xml";
    };

}
