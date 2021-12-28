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
package nl.teslanet.mule.connectors.coap.api.options;


/**
 * Other option interface.
 */
public class OtherOptionAttribute
{
    /**
     * The number of the other option.
     */
    protected int number;

    //TODO RC to inputstream
    /**
     * The value of the other option.
     */
    protected byte[] value= null;

    /**
     * Get the number of this other option.
     *
     * @return The option number.
     */
    public int getNumber()
    {
        return number;
    }

    /**
     * Get the value of this other option.
     *
     * @return The option value if any, otherwise null.
     */
    public byte[] getValue()
    {
        return value;
    }

    /**
     * Checks if option is critical.
     *
     * @return {@code true} if is option critical, otherwise {@code false}.
     */
    public boolean isCritical()
    {
        // Critical: onum & 1
        return ( getNumber() & 1 ) != 0;
    }

    /**
     * Checks if option with this number is unsafe.
     *
     * @return {@code true}  if this is an unsafe option, otherwise {@code false}.
     */
    public boolean isUnSafe()
    {
        // UnSafe: onum & 2
        return ( getNumber() & 2 ) != 0;
    }

    /**
     * Checks if option with this number is a NoCacheKey option.
     *
     * @return {@code true} if this is NoCacheKey option, otherwise {@code false}.
     */
    public boolean isNoCacheKey()
    {
        // NoCacheKey: (onum & 0x1e) == 0x1c
        return ( getNumber() & 0x1E ) == 0x1C;
    }
}
