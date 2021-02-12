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
package nl.teslanet.mule.connectors.coap.test.utils;


import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;


/**
 * Utilities to control timing in tests
 *
 */
public class Timing
{
    /**
     * Actual poll interval that defines time resolution.
     */
    private static long pollInterval= 100L;

    /**
     * Take a pauze.
     */
    public static void pauze()
    {
        pauze( 2000L );
    }

    /**
     * Take a pauze for given milliseconds.
     */
    public static void pauze( long millis )
    {
        if ( millis < pollInterval * 10 )
        {
            pollInterval= millis / 10;
            Awaitility.setDefaultPollInterval( pollInterval, TimeUnit.MILLISECONDS );
        }
        try
        {
            Awaitility.await().atMost( millis, TimeUnit.MILLISECONDS ).until( () -> {
                return false;
            } );
        }
        catch ( ConditionTimeoutException e )
        {
            //as expected
        }
    }
}
