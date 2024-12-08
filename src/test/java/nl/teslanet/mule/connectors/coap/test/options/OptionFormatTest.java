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
package nl.teslanet.mule.connectors.coap.test.options;


import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import nl.teslanet.mule.connectors.coap.api.options.OptionFormat;
import nl.teslanet.mule.connectors.coap.internal.utils.MessageUtils;


/**
 * Test option format handling.
 *
 */
@RunWith( Parameterized.class )
public class OptionFormatTest
{
    /**
     * Cf format.
     */
    @Parameter( 0 )
    public org.eclipse.californium.core.coap.OptionNumberRegistry.OptionFormat cfFormat;

    /**
     * Format.
     */
    @Parameter( 1 )
    public OptionFormat format;

    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters( name= "format= {0}" )
    public static Collection< Object[] > data()
    {
        return Arrays
            .asList( new Object [] []
            {
                { org.eclipse.californium.core.coap.OptionNumberRegistry.OptionFormat.EMPTY, OptionFormat.EMPTY },
                { org.eclipse.californium.core.coap.OptionNumberRegistry.OptionFormat.INTEGER, OptionFormat.INTEGER },
                { org.eclipse.californium.core.coap.OptionNumberRegistry.OptionFormat.STRING, OptionFormat.STRING },
                { org.eclipse.californium.core.coap.OptionNumberRegistry.OptionFormat.OPAQUE, OptionFormat.OPAQUE },
                { org.eclipse.californium.core.coap.OptionNumberRegistry.OptionFormat.UNKNOWN, OptionFormat.OPAQUE } }
            );
    }

    /**
     * Test format conversion.
     */
    @Test
    public void test2ToOptionFormat()
    {
        OptionFormat format2= MessageUtils.toOptionFormat( cfFormat );
        assertEquals( "wrong option format conversion", format, format2 );
    }
}
