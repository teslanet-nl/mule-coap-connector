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
import java.util.Collections;

import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;

import nl.teslanet.mule.connectors.coap.test.utils.TestOptions;


/**
 * Test inbound other option
 *
 */
public class OptOtherOptionOutboundListTest extends AbstractOutboundPropertyTestcase
{
    static final private ArrayList< String > values= new ArrayList<>();
    static
    {
        values.add( "one" );
        values.add( "two" );
        values.add( "three" );
    }

    /**
     * Specification of orher option
     *
     */
    public class OtherOptionSpec
    {

        public String alias;

        public byte[] value;

        /**
         * Constructor.
         * @param alias The alias.
         * @param value The value.
         */
        public OtherOptionSpec( String alias, byte[] value )
        {
            super();
            this.alias= alias;
            this.value= value;
        }

        /**
         * @return the alias
         */
        public String getAlias()
        {
            return alias;
        }

        /**
         * @return the value
         */
        public byte[] getValue()
        {
            return value;
        }
    }

    @Override
    protected String getPropertyName()
    {
        return "coap.opt.other.65003.list";
    }

    @Override
    protected Object getPropertyValue() throws Exception
    {
        ArrayList< OtherOptionSpec > others= new ArrayList<>();
        for ( String value : values )
        {
            others.add( new OtherOptionSpec( TestOptions.OTHER_OPTION_65003.getName(), value.getBytes() ) );
        }
        return others;
    }

    @Override
    protected Object getExpectedOptionValue() throws Exception
    {
        return Collections.unmodifiableList( values );
    };

    protected boolean optionValueIsCollectionOfStringable()
    {
        return true;
    }

    @Override
    protected Object fetchOption( OptionSet options )
    {
        ArrayList< String > result= new ArrayList<>();
        for ( Option other : options.getOthers() )
        {
            if ( other.getNumber() == 65003 )
            {
                result.add( other.getStringValue() );
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.test.server.properties.AbstractInboundPropertyTestcase#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/properties/testserver-options-other65003-list.xml";
    }
}
