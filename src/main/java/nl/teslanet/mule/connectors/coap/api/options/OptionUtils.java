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

    /**
     * Convert an integer value to byte array. 
     * The resulting bits conforming to the CoAP defined uint format for integer option values, 
     * where leading zero bytes should be omitted.
     * 
     * @see <a href=
     *      "https://www.rfc-editor.org/rfc/rfc7252.html#section-3.2">IETF RFC 7252 -
     *      3.2. Option Value Formats</a>
     *      
     * @param value The integer value to convert.
     * @return The converted value as bytes. 
     */
    public static byte[] toBytes( int value )
    {
        int length= 0;
        for ( ; length < 4 && ( value < 0 || value >= ( 1 << ( length * 8 ) ) ); length++ );
        byte[] result= new byte [length];
        for ( int i= 0; i < length; i++ )
        {
            result[length - i - 1]= (byte) ( value >> i * 8 );
        }
        return result;
    }

    /**
     * Convert a long value to byte array. 
     * The resulting bits conforming to the CoAP defined uint format for integer option values, 
     * where leading zero bytes should be omitted.
     * 
     * @see <a href=
     *      "https://www.rfc-editor.org/rfc/rfc7252.html#section-3.2">IETF RFC 7252 -
     *      3.2. Option Value Formats</a>
     *      
     * @param value The long value to convert.
     * @return The converted value as bytes. 
     */
    public static byte[] toBytes( long value )
    {
        int length= 0;
        for ( ; length < 8 && ( value < 0 || value >= ( 1L << ( length * 8 ) ) ); length++ );
        byte[] result= new byte [length];
        for ( int i= 0; i < length; i++ )
        {
            result[length - i - 1]= (byte) ( value >> i * 8 );
        }
        return result;
    }
}
