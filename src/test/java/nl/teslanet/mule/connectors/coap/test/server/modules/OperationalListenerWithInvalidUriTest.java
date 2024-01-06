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
package nl.teslanet.mule.connectors.coap.test.server.modules;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResourceUriException;
import nl.teslanet.mule.connectors.coap.internal.server.OperationalListener;
import nl.teslanet.mule.connectors.coap.internal.server.RequestCodeFlags;


/**
 * Test operational listener with invalid uri.
 *
 */
@RunWith( Parameterized.class )
public class OperationalListenerWithInvalidUriTest
{
    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters( name= "uri= {0}" )
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []
            {
                { null, "null value is not allowed." },
                { "", "invalid uriPattern {  }, uri cannot be empty." },
                { "/", "invalid uriPattern { / }, uri cannot be empty." },
                { "**", "invalid uriPattern { ** }, wildcard needs to be last character." },
                { "/some_resource/**", "invalid uriPattern { /some_resource/** }, wildcard needs to be last character." },
                { "/some_resource/*/*", "invalid uriPattern { /some_resource/*/* }, wildcard needs to be last character." },
                { "/some_resource/*/child/*", "invalid uriPattern { /some_resource/*/child/* }, wildcard needs to be last character." },
                { "/some_resource/*/child", "invalid uriPattern { /some_resource/*/child }, wildcard needs to be last character." },
                { "/some_resource*/child/*", "invalid uriPattern { /some_resource*/child/* }, wildcard needs to be last character." } }
        );
    }

    /**
     * Request uri to test
     */
    @Parameter( 0 )
    public String uri;

    /**
     * The expected message.
     */
    @Parameter( 1 )
    public String expectedMessage;

    @Test
    public void testConstructorWithInvalidUri() throws InternalResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        RequestCodeFlags flags= new RequestCodeFlags( true, true, true, false, false, false, false );

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            new OperationalListener( uri, flags, callback );
        } );
        assertEquals( "exception has wrong message", expectedMessage, e.getMessage() );
    }
}
