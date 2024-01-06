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
package nl.teslanet.mule.connectors.coap.internal.utils;


import java.util.Optional;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidDurationException;


/**
 * Utilities for security classes.
 *
 */
public class TimeUtils
{
    /**
     * Duration string pattern group begin part.
     */
    private static final String GROUP_BEGIN= "^(?:";

    /**
     * Duration string pattern day part.
     */
    private static final String DAY_PART= "(?:([0-9]{1,5})(d))?";

    /**
     * Duration string pattern hour part.
     */
    private static final String HOUR_PART= "(?:([0-9]{1,5})(h))?";

    /**
     * Duration string pattern minute part.
     */
    private static final String MINUTE_PART= "(?:([0-9]{1,5})(m))?";

    /**
     * Duration string pattern second part.
     */
    private static final String SECOND_PART= "(?:([0-9]{1,5})(s))?";

    /**
     * Duration string pattern millisecond part.
     */
    private static final String MILLISECOND_PART= "(?:([0-9]{1,5})(ms))?";

    /**
     * Duration string pattern microsecond part.
     */
    private static final String MICROSECOND_PART= "(?:([0-9]{1,5})(us))?";

    /**
     * Duration string pattern nanosecond part.
     */
    private static final String NANOSECOND_PART= "(?:([0-9]{1,5})(ns))?";

    /**
     * Duration string pattern group begin part.
     */
    private static final String GROUP_END= ")$";

    /**
     * Duration string pattern group begin part.
     */
    private static final String OR= "|";

    /**
     * Duration string pattern single second part.
     */
    private static final String SECOND_SINGLE= "^(?:([0-9]{1,9})(s))$";

    /**
     * Duration string pattern single millisecond  part.
     */
    private static final String MILLISECOND_SINGLE= "^(?:([0-9]{1,12})(ms))$";

    /**
     * Duration string pattern single microsecond part.
     */

    private static final String MICROSECOND_SINGLE= "^(?:([0-9]{1,15})(us))$";

    /**
     * Duration string pattern single nanosecond part.
     */
    private static final String NANOSECOND_SINGLE= "^(?:([0-9]{1,18})(ns))$";

    private static final Pattern DURATION_PATTERN= Pattern.compile(
        GROUP_BEGIN + DAY_PART + HOUR_PART + MINUTE_PART + SECOND_PART + MILLISECOND_PART + MICROSECOND_PART + NANOSECOND_PART + GROUP_END + OR + SECOND_SINGLE + OR
            + MILLISECOND_SINGLE + OR + MICROSECOND_SINGLE + OR + NANOSECOND_SINGLE
    );

    /**
     * The duration parts.
     *
     */
    public enum NanosPart
    {
        /**
         * * Number of nanoseconds in a day.
         */
        DAY("d", 24L * 60L * 60L * 1000L * 1000L * 1000L),

        /**
         * Number of nanoseconds in a hour.
         */
        HOUR("h", 60L * 60L * 1000L * 1000L * 1000L),

        /**
         * Number of nanoseconds in a minute.
         */
        MINUTE("m", 60L * 1000L * 1000L * 1000L),

        /**
         * Number of nanoseconds in a second.
         */
        SECOND("s", 1000L * 1000L * 1000L),

        /**
         * Number of nanoseconds in a millisecond.
         */
        MILLISECOND("ms", 1000L * 1000L),

        /**
         * Number of nanoseconds in a microsecond.
         */

        MICROSECOND("us", 1000L),

        /**
         * Number of nanoseconds.
         */
        NANO_SECOND("ns", 1L);

        /**
         * Unit of the part.
         */
        private final String unit;

        /**
         * Number of nanos of the part.
         */
        private final long nanos;

        /**
         * Constructor.
         * 
         * @param partNanos The Number of nanos of the part.
         */
        NanosPart( String unit, long nanos )
        {
            this.unit= unit;
            this.nanos= nanos;
        }

        /**
         * @return The unit of the part.
         */
        public String getUnit()
        {
            return unit;
        }

        /**
         * @return The Number of nanos of the part.
         */
        public long getNanos()
        {
            return nanos;
        }
    }

    /**
     * Do not create objects.
     */
    private TimeUtils()
    {
        // NOOP
    }

    /**
     * Convert duration expression to nanoseconds value. Valid expressions are
     * combinations of integer & unit groups where the unit is d, h, s, ms, ms, us
     * or ns, in this order. Any but one group can be omitted.
     * 
     * @param expression The duration expression
     * @return The value in nanoseconds.
     * @throws InternalInvalidDurationException When the expression is invalid.
     */
    public static long toNanos( String expression ) throws InternalInvalidDurationException
    {
        long result= 0L;
        String stripped= StringUtils.deleteWhitespace( expression );
        if ( StringUtils.isEmpty( stripped ) )
        {
            throw new InternalInvalidDurationException( "Empty duration is not allowed." );
        }
        Scanner scanner= new Scanner( stripped );
        try
        {
            MatchResult groups;
            scanner.findInLine( DURATION_PATTERN );
            groups= scanner.match();
            int count= groups.groupCount();
            if ( count < 2 || ( count % 2 ) != 0 )
            {
                // regex should prevent this condition
                throw new InternalInvalidDurationException( "Duration should contain value and unit." );
            }
            result= calculateNanos( groups );
        }
        catch ( Exception e )
        {
            throw new InternalInvalidDurationException( "Duration expression has wrong syntax." );
        }
        finally
        {
            scanner.close();
        }
        return result;
    }

    /**
     * Calculate the duration from the scanned groups.
     * 
     * @param groups The scanned groups containing duration parts.
     * @return The calculated value.
     */
    private static long calculateNanos( MatchResult groups )
    {
        long result= 0L;
        for ( int i= 1; i <= groups.groupCount(); i= i + 2 )
        {
            String valueString;
            long value= 0L;
            String unit;
            valueString= groups.group( i );
            unit= groups.group( i + 1 );
            if ( !StringUtils.isEmpty( valueString ) )
            {
                value= Long.valueOf( valueString );
            }
            Optional< NanosPart > optionalPart= findNanosPart( unit );
            if ( optionalPart.isPresent() )
            {
                result= Math.addExact( result, Math.multiplyExact( value, optionalPart.get().getNanos() ) );
            }
        }
        return result;
    }

    /**
     * Convert nanos seconds to a normalized string.
     * 
     * @param nanos
     * @return The normalized duration string.
     * @throws InternalInvalidDurationException When the nanos seconds value is
     *                                          negative or out of range.
     */
    public static String nanosToString( long nanos ) throws InternalInvalidDurationException
    {
        if ( nanos < 0 )
        {
            throw new InternalInvalidDurationException( "Negative value is not allowed." );
        }
        StringBuilder builder= new StringBuilder();
        if ( nanos == 0L )
        {
            builder.append( 0L );
            builder.append( "ms" );
        }
        else
        {
            long rest= nanos;
            boolean hasPrevious= false;
            for ( NanosPart part : NanosPart.values() )
            {
                long value= rest / part.getNanos();
                if ( value > 0 )
                {
                    if ( hasPrevious ) builder.append( " " );
                    hasPrevious= true;
                    rest-= value * part.getNanos();
                    builder.append( value );
                    builder.append( part.getUnit() );
                }
            }
        }
        return builder.toString();
    }

    /**
     * Find nanos part enum from unit.
     * 
     * @param partUnit The unit
     * @return The Optinal NanosPart, empty when not found.
     */
    private static Optional< NanosPart > findNanosPart( String partUnit )
    {
        for ( NanosPart part : NanosPart.values() )
        {
            if ( part.getUnit().equals( partUnit ) )
            {
                return Optional.of( part );
            }
        }
        return Optional.empty();
    }
}