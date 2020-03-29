/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2020 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.api.options;

/**
 * Utilities for handling CoAP options. 
 *
 */
public class OptionUtils
{
    /**
     * private constructor
     */
    private OptionUtils()
    {
        //NOOP
    }

    /**
     * Checks if option with this number is critical.
     *
     * @return {@code true} if is option critical
     */
    public static boolean isCritical( int optionNumber )
    {
        // Critical = (onum & 1);
        return ( optionNumber & 1 ) != 0;
    }

    /**
     * Checks if option with this number is unsafe.
     *
     * @return  {@code true}  if is unsafe
     */
    public static boolean isUnSafe( int optionNumber )
    {
        // UnSafe = (onum & 2);
        return ( optionNumber & 2 ) != 0;
    }

    /**
     * Checks if option with this number is a NoCacheKey.
     *
     * @return  {@code true} if is NoCacheKey
     */
    public static boolean isNoCacheKey( int optionNumber )
    {
        // NoCacheKey = ((onum & 0x1e) == 0x1c);
        return ( optionNumber & 0x1E ) == 0x1C;
    }
}
