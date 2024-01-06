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
package nl.teslanet.mule.connectors.coap.test.modules;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidDurationException;
import nl.teslanet.mule.connectors.coap.internal.utils.TimeUtils;


/**
 * Tests the implementation of TimeUtils.toNanos.
 *
 */
@RunWith( Parameterized.class )
public class TimeUtilsInvalidSyntaxTest
{
    /**
     * @return the collection of test parameters.
     */
    @Parameters( name= "duration= {0}" )
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []
            {
                { "1x" },
                { "ms" },
                { "any" },
                { "1" },
                { "1S" },
                { "1h 2x" },
                { "1.1m" },
                { ",1s" },
                { "111.0ms" },
                { "d 1us" },
                { "1  d s ns" },
                { "22 33" },
                { "33" },
                { "123456d" },
                { "123456m" },
                { "1234567890s" },
                { "1234567890123ms" },
                { "1234567890123456us" },
                { "1234567890123456789ns" },
                { "3h11d" },
                { "3m11h" },
                { "3s11m" },
                { "3ms11s" },
                { "3us11ms" },
                { "3ns11us" },
                { "s 22" },
                { "(33m)" },
                { "-44s" },
                { "+555ms" },
                { "666ús" },
                { "0xaaff ms" },
                { "88ms40s" },
                { "1234d42h330m447s4564ms66344us965456", },
                { "  123 d 42 h 330 m 447 s 4564 ms 66344   us 965456  ns .  " } }
        );
    }

    /**
     * Input to test.
     */
    @Parameter( 0 )
    public String input;

    /**
     * Test TimeUtils.toNanos
     * @throws InternalInvalidDurationException when input is invalid.
     */
    @Test
    public void testToRequestCode() throws InternalInvalidDurationException
    {
        InternalInvalidDurationException e= assertThrows( InternalInvalidDurationException.class, () -> {
            @SuppressWarnings( "unused" )
            long value= TimeUtils.toNanos( input );
        } );
        assertEquals( "exception has wrong message", "Duration expression has wrong syntax.", e.getMessage() );
    }
}
