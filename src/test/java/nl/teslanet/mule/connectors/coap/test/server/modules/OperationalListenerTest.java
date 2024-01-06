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
package nl.teslanet.mule.connectors.coap.test.server.modules;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;

import nl.teslanet.mule.connectors.coap.api.CoapRequestAttributes;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResourceUriException;
import nl.teslanet.mule.connectors.coap.internal.server.OperationalListener;
import nl.teslanet.mule.connectors.coap.internal.server.RequestCodeFlags;


/**
 * Test operational listener.
 *
 */
@RunWith( Parameterized.class )
public class OperationalListenerTest
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
                { "/some_resource", new RequestCodeFlags(), new TestSourceCallBack() },
                { "/some_resource/child", new RequestCodeFlags(), new TestSourceCallBack() },
                { "/some_resource/child/child", new RequestCodeFlags( true, false, false, false, false, false, false ), new TestSourceCallBack() },
                { "some_resource", new RequestCodeFlags( true, false, false, false, false, false, false ), new TestSourceCallBack() },
                { "some_resource/child", new RequestCodeFlags( true, true, false, false, false, false, false ), new TestSourceCallBack() },
                { "some_resource/child/child", new RequestCodeFlags( true, true, false, false, false, false, false ), new TestSourceCallBack() },
                { "/*", new RequestCodeFlags( true, true, false, false, false, false, false ), new TestSourceCallBack() },
                { "/some_resource/*", new RequestCodeFlags( true, true, false, false, false, false, false ), new TestSourceCallBack() },
                { "/some_resource/child/*", new RequestCodeFlags( true, true, true, false, false, false, false ), new TestSourceCallBack() },
                { "*", new RequestCodeFlags( true, true, true, false, false, false, false ), new TestSourceCallBack() },
                { "some_resource/*", new RequestCodeFlags( true, true, true, false, false, false, false ), new TestSourceCallBack() },
                { "some_resource/child/*", new RequestCodeFlags( true, true, true, false, false, false, false ), new TestSourceCallBack() },
                { "/some_resource*", new RequestCodeFlags( true, true, true, false, false, false, false ), new TestSourceCallBack() },
                { "/some_resource/child/*", new RequestCodeFlags( true, true, true, false, false, false, false ), new TestSourceCallBack() },
                { "some_resource/child/child*", new RequestCodeFlags( true, true, true, false, false, false, false ), new TestSourceCallBack() },
                { "/some_resource*", new RequestCodeFlags( true, true, true, true, false, false, false ), new TestSourceCallBack() },
                { "/some_resource/child/*", new RequestCodeFlags( true, true, true, true, false, false, false ), new TestSourceCallBack() },
                { "some_resource/child/child*", new RequestCodeFlags( true, true, true, true, true, false, false ), new TestSourceCallBack() },
                { "/some_resource*", new RequestCodeFlags( true, true, true, true, true, false, false ), new TestSourceCallBack() },
                { "/some_resource/child/*", new RequestCodeFlags( true, true, true, true, true, true, false ), new TestSourceCallBack() },
                { "some_resource/child/child*", new RequestCodeFlags( true, true, true, true, true, true, false ), new TestSourceCallBack() },
                { "/some_resource*", new RequestCodeFlags( true, true, true, true, true, true, true ), new TestSourceCallBack() },
                { "/some_resource/child/*", new RequestCodeFlags( true, true, true, true, true, true, true ), new TestSourceCallBack() },
                { "some_resource/child/child*", new RequestCodeFlags( true, true, true, true, true, true, true ), new TestSourceCallBack() },

            }
        );
    }

    /**
     * Request uri to test
     */
    @Parameter( 0 )
    public String uri;

    /**
     * The request code flags to test..
     */
    @Parameter( 1 )
    public RequestCodeFlags flags;

    /**
     * The callback to test..
     */
    @Parameter( 2 )
    public TestSourceCallBack callback;

    @Test
    public void testConstructor() throws InternalResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        OperationalListener listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        assertOperationalListener( listener, uri, flags, callback );
    }

    @Test
    public void testSetUri() throws InternalResourceUriException
    {
        String initialUri= "/initial";
        OperationalListener listener= new OperationalListener( initialUri, new RequestCodeFlags( flags ), callback );
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, flags, callback );
    }

    @Test
    public void testSetCallback() throws InternalResourceUriException
    {
        OperationalListener listener= new OperationalListener( uri, flags, callback );

        TestSourceCallBack newCallback= new TestSourceCallBack();
        listener.setCallback( newCallback );
        assertOperationalListener( listener, uri, flags, newCallback );
    }

    private void assertOperationalListener(
        OperationalListener testListener,
        String testUri,
        RequestCodeFlags testFlags,
        SourceCallback< InputStream, CoapRequestAttributes > testCallback
    )
    {
        String expectedUri= ( testUri.startsWith( "/" ) ? testUri : "/" + testUri );

        assertNotNull( "listener construction failed", testListener );
        assertEquals( "listener uri has wrong value", expectedUri, testListener.getUriPattern() );
        assertEquals( "listener callback has wrong value", testCallback, testListener.getCallback() );
        assertEquals( "listener flags has wrong value", flags, testListener.getRequestCodeFlags() );
    }
}
