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


import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.option.OpaqueOptionDefinition;


/**
 * The strategy to use by the testserver
 */
public class OptOtherStrategy implements OptionStrategy
{
    /**
     * option test values
     */
    private LinkedList< Option > values= new LinkedList<>();

    /**
     * Constructor setting one value
     * @param value the test value
     */
    public OptOtherStrategy( Option value )
    {
        values.add( value );
    }

    /**
     * Constructor setting multiple values.
     * @param optionNumber The number of the option.
     * @param optionValues The values to set.
     */
    public OptOtherStrategy( int optionNumber, byte[][] optionValues )
    {
        for ( int i= 0; i < optionValues.length; i++ )
        {
            values.add( new Option( new OpaqueOptionDefinition( optionNumber, String.valueOf( optionNumber ), false ), optionValues[i] ) );
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.transport.coap.client.test.properties.OptionStrategy#setOption(org.eclipse.californium.core.coap.Response)
     */
    @Override
    public void setOption( Response response )
    {
        for ( Option option : values )
        {
            response.getOptions().addOption( option );
        }
    }

    /**
     * validate all expected options are contained in the request in the right order.
     */
    @Override
    public boolean validateOption( Request request )
    {
        int optionNumber= values.element().getNumber();
        Iterator< Option > expectedIterator= values.iterator();
        for ( Option option : request.getOptions().getOthers() )
        {
            if ( option.getNumber() == optionNumber )
            {
                Option expected= expectedIterator.next();
                if ( !Arrays.equals( expected.getValue(), option.getValue() ) ) return false;
            }
        }
        return !expectedIterator.hasNext();
    }
}
