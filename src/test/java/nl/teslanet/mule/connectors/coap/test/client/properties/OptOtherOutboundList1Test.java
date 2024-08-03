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
package nl.teslanet.mule.connectors.coap.test.client.properties;


import java.util.ArrayList;

import nl.teslanet.mule.connectors.coap.test.utils.TestOptions;


/**
 * Test outbound other property, bytearray value
 *
 */
public class OptOtherOutboundList1Test extends AbstractOutboundPropertiesTestCase
{
    byte[][] values= { { (byte) 0x01, (byte) 0x02, (byte) 0x03 }, { (byte) 0x04, (byte) 0x05, (byte) 0x06 }, { (byte) 0x07, (byte) 0x08, (byte) 0x09 }

    };

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
        return "coap.opt.other.list";
    }

    @Override
    protected String getOutboundOptionAlias()
    {
        return TestOptions.OTHER_OPTION_65008.getName();
    }

    @Override
    protected Object getOutboundPropertyValue()
    {
        ArrayList< OtherOptionSpec > others= new ArrayList<>();
        for ( byte[] value : values )
        {
            others.add( new OtherOptionSpec( getOutboundOptionAlias(), value ) );
        }
        return others;
    }

    @Override
    protected OptionStrategy getStrategy()
    {
        return new OptOtherStrategy( TestOptions.OTHER_OPTION_65008.getNumber(), values );
    }
}
