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

import org.junit.Test;

import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidDurationException;
import nl.teslanet.mule.connectors.coap.internal.utils.TimeUtils;


/**
 * Tests the implementation of TimeUtils on invalid input.
 *
 */
public class TimeUtilsInvalidTest
{
    @Test
    public void testNullToNanos()
    {
        String durationValue1= null;
        String durationValue2= "";
        String durationValue3= "  ";

        InternalInvalidDurationException e1= assertThrows( InternalInvalidDurationException.class, () -> {
            @SuppressWarnings( "unused" )
            long value= TimeUtils.toNanos( durationValue1 );
        } );
        assertEquals( "exception has wrong message", "Empty duration is not allowed.", e1.getMessage() );
        InternalInvalidDurationException e2= assertThrows( InternalInvalidDurationException.class, () -> {
            @SuppressWarnings( "unused" )
            long value= TimeUtils.toNanos( durationValue2 );
        } );
        assertEquals( "exception has wrong message", "Empty duration is not allowed.", e2.getMessage() );
        InternalInvalidDurationException e3= assertThrows( InternalInvalidDurationException.class, () -> {
            @SuppressWarnings( "unused" )
            long value= TimeUtils.toNanos( durationValue3 );
        } );
        assertEquals( "exception has wrong message", "Empty duration is not allowed.", e3.getMessage() );
    }

    @Test
    public void testLargeToNanos()
    {
        String durationValue1= "106751d23h47m16s854ms775us807ns";

        InternalInvalidDurationException e1= assertThrows( InternalInvalidDurationException.class, () -> {
            @SuppressWarnings( "unused" )
            long value= TimeUtils.toNanos( durationValue1 );
        } );
        assertEquals( "exception has wrong message", "Duration expression has wrong syntax.", e1.getMessage() );
    }

    @Test
    public void testInvalidNanosToString()
    {
        Long durationValue1= null;
        long durationValue2= -1;
        long durationValue3= Long.MIN_VALUE;

        assertThrows( NullPointerException.class, () -> {
            @SuppressWarnings( { "unused", "null" } )
            String result= TimeUtils.nanosToString( durationValue1 );
        } );
        InternalInvalidDurationException e2= assertThrows( InternalInvalidDurationException.class, () -> {
            @SuppressWarnings( "unused" )
            String result= TimeUtils.nanosToString( durationValue2 );
        } );
        assertEquals( "exception has wrong message", "Negative value is not allowed.", e2.getMessage() );
        InternalInvalidDurationException e3= assertThrows( InternalInvalidDurationException.class, () -> {
            @SuppressWarnings( "unused" )
            String result= TimeUtils.nanosToString( durationValue3 );
        } );
        assertEquals( "exception has wrong message", "Negative value is not allowed.", e3.getMessage() );
    }
}
