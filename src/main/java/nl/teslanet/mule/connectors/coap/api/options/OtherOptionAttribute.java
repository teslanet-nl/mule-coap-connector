/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2024 (teslanet.nl) Rogier Cobben
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


import java.io.InputStream;


/**
 * Other option attribute.
 * This class is actually an interface but Mule needs a concrete class.
 */
public class OtherOptionAttribute
{
    /**
     * Get the alias of this other option.
     * @return The option number.
     */
    public String getAlias()
    {
        return null;
    }

    /**
     * Get the number of this other option.
     * @return The option number.
     */
    public int getNumber()
    {
        return 0;
    }

    /**
     * Get the format of this other option.
     * @return The option type.
     */
    public OptionFormat getFormat()
    {
        return null;
    }

    /**
     * Get the the length of the other option.
     * @return The number of bytes of this option.
     */
    public int getLength()
    {
        return 0;
    }

    /**
     * Get the value of this other option.
     * @return The option value if any, otherwise null.
     */
    public InputStream getValue()
    {
        return null;
    }

    /**
     * Get the value as number.
     * @return long containing the value.
     */
    public long getValueAsNumber()
    {
        return 0;
    }

    /**
     * Get the option value as hexadecimal string.
     * Hexadecimal values a-f will be lower case.
     * @return The string containing the hexadecimal representation or empty string when the value is empty.
     */
    public String getValueAsHex()
    {
        return null;
    }

    /**
     * Get value as UTF-8 string.
     * @return The UTF-8 string interpretation.
     */
    public String getValueAsString()
    {
        return null;
    }

    /**
     * Checks if option is critical.
     * @return {@code true} if is option critical, otherwise {@code false}.
     */
    public boolean isCritical()
    {
        return false;
    }

    /**
     * Checks if option with this number is unsafe.
     * @return {@code true}  if this is an unsafe option, otherwise {@code false}.
     */
    public boolean isUnsafe()
    {
        return false;
    }

    /**
     * Checks if option with this number is a NoCacheKey option.
     * @return {@code true} if this is NoCacheKey option, otherwise {@code false}.
     */
    public boolean isNoCacheKey()
    {
        return false;
    }

    /**
     * Check if the option is empty.
     * @return True when the option is empty, otherwise false.
     */
    public boolean isEmpty()
    {
        return false;
    }
}
