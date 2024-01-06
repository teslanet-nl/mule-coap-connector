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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidDurationException;
import nl.teslanet.mule.connectors.coap.internal.utils.TimeUtils;
import nl.teslanet.mule.connectors.coap.internal.utils.TimeUtils.NanosPart;


/**
 * Tests the implementation of TimeUtils.toNanos.
 *
 */
@RunWith( Parameterized.class )
public class TimeUtilsTest
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
                { "0  ns", "0ms", 0L },
                { "0   d", "0ms", 0L },
                { "0   h", "0ms", 0L },
                { "0   m", "0ms", 0L },
                { "0   s", "0ms", 0L },
                { "0  ms", "0ms", 0L },
                { "0  us", "0ms", 0L },
                { "1  ns", "1ns", 1L },
                { "1   d", "1d", 24L * 60L * 60L * 1000L * 1000L * 1000L },
                { "1   h", "1h", 60L * 60L * 1000L * 1000L * 1000L },
                { "1   m", "1m", 60L * 1000L * 1000L * 1000L },
                { "1   s", "1s", 1000L * 1000L * 1000L },
                { "1  ms", "1ms", 1000L * 1000L },
                { "1  us", "1us", 1000L },
                { "1  ns", "1ns", 1L },
                { "22  h ", "22h", 22L * NanosPart.HOUR.getNanos() },
                { "33  m ", "33m", 33L * NanosPart.MINUTE.getNanos() },
                { "44  s ", "44s", 44L * NanosPart.SECOND.getNanos() },
                { "555 ms", "555ms", 555L * NanosPart.MILLISECOND.getNanos() },
                { "666us", "666us", 666L * NanosPart.MICROSECOND.getNanos() },
                { "022h", "22h", 22L * NanosPart.HOUR.getNanos() },
                { "033m", "33m", 33L * NanosPart.MINUTE.getNanos() },
                { "00044s", "44s", 44L * NanosPart.SECOND.getNanos() },
                { "00555ms", "555ms", 555L * NanosPart.MILLISECOND.getNanos() },
                { "0066600us", "66ms 600us", 66600L * NanosPart.MICROSECOND.getNanos() },
                { "77777ns", "77us 777ns", 77777L },
                { "  11d  3h   ", "11d 3h", 11L * NanosPart.DAY.getNanos() + 3L * NanosPart.HOUR.getNanos() },
                { " 22  h ", "22h", 22L * NanosPart.HOUR.getNanos() },
                { "  33 m    ", "33m", 33L * NanosPart.MINUTE.getNanos() },
                { "   44 \n s", "44s", 44L * NanosPart.SECOND.getNanos() },
                { "  555 \t  ms  ", "555ms", 555L * NanosPart.MILLISECOND.getNanos() },
                { "  666us   ", "666us", 666L * NanosPart.MICROSECOND.getNanos() },
                { " 123456789  s    ", "1428d 21h 33m 9s", 123456789000000000L },
                { " 123456789012  ms    ", "1428d 21h 33m 9s 12ms", 123456789012000000L },
                { " 123456789012345  us    ", "1428d 21h 33m 9s 12ms 345us", 123456789012345000L },
                { " 123456789012345678  ns    ", "1428d 21h 33m 9s 12ms 345us 678ns", 123456789012345678L },
                { "40s88ms", "40s 88ms", 40L * NanosPart.SECOND.getNanos() + 88L * NanosPart.MILLISECOND.getNanos() },
                {
                    "123d 42h 330m 447s 4564ms 66344us 96545ns",
                    "124d 23h 37m 31s 630ms 440us 545ns",
                    123L * NanosPart.DAY.getNanos() + 42L * NanosPart.HOUR.getNanos() + 330L * NanosPart.MINUTE.getNanos() + 447L * NanosPart.SECOND.getNanos()
                        + 4564L * NanosPart.MILLISECOND.getNanos() + 66344L * NanosPart.MICROSECOND.getNanos() + 96545L },
                {
                    "  123 d 42 h 330 m 447 s 4564 ms 66344   us 96545  ns   ",
                            "124d 23h 37m 31s 630ms 440us 545ns",
                            123L * NanosPart.DAY.getNanos() + 42L * NanosPart.HOUR.getNanos() + 330L * NanosPart.MINUTE.getNanos() + 447L * NanosPart.SECOND.getNanos()
                                + 4564L * NanosPart.MILLISECOND.getNanos() + 66344L * NanosPart.MICROSECOND.getNanos() + 96545L } ,
            {
                "  99999 d 99999 h 99999 m 99999 s 99999 ms 99999 us 99999 ns   ",
                "104236d 5h 27m 19s 99ms 98us 999ns",
                99999L * NanosPart.DAY.getNanos() + 99999L * NanosPart.HOUR.getNanos() + 99999L * NanosPart.MINUTE.getNanos() + 99999L * NanosPart.SECOND.getNanos()
                    + 99999L * NanosPart.MILLISECOND.getNanos() + 99999L * NanosPart.MICROSECOND.getNanos() + 99999L } }
        );
    }

    /**
     * Input to test.
     */
    @Parameter( 0 )
    public String input;

    /**
     * The normalized string.
     */
    @Parameter( 1 )
    public String normalized;

    /**
     * The nanos value
     */
    @Parameter( 2 )
    public Long nanos;

    /**
     * Test TimeUtils.toNanos
     * @throws InternalInvalidDurationException when input is invalid.
     */
    @Test
    public void testToNanos() throws InternalInvalidDurationException
    {
        assertEquals( "duration has wrong value", nanos, (Long) TimeUtils.toNanos( input ) );
    }

    /**
     * Test TimeUtils.nanosToString
     * @throws InternalInvalidDurationException when input is invalid.
     */
    @Test
    public void testToNormalizedString() throws InternalInvalidDurationException
    {
        assertEquals( "duration has wrong value", normalized, TimeUtils.nanosToString( nanos ) );
    }
}
