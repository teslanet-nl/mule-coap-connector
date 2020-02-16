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
package nl.teslanet.mule.connectors.coap.test.server.modules;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;

import nl.teslanet.mule.connectors.coap.api.ReceivedRequestAttributes;
import nl.teslanet.mule.connectors.coap.api.error.InvalidResourceUriException;
import nl.teslanet.mule.connectors.coap.internal.server.OperationalListener;


public class OperationalListenerTest
{

    @Rule
    public ExpectedException exception= ExpectedException.none();

    @Before
    public void setUp() throws Exception
    {

    }

    @After
    public void tearDown() throws Exception
    {

    }

    @Test
    public void testConstructor() throws InvalidResourceUriException
    {
        String uri;
        OperationalListener listener;
        TestSourceCallBack callback;

        uri= "/some_resource";
        callback= new TestSourceCallBack();
        listener= new OperationalListener( uri, callback );
        //        Field serverField= listener.getClass().getField( "server" );
        //        serverField.set( listener, server );
        //        Field uriPatternField= listener.getClass().getField( "uriPattern" );
        assertOperationalListener( listener, uri, callback );

        uri= "/some_resource/child";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, uri, callback );

        uri= "/some_resource/child/child";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, uri, callback );

        uri= "some_resource";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, "/" + uri, callback );

        uri= "some_resource/child";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, "/" + uri, callback );

        uri= "some_resource/child/child";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, "/" + uri, callback );

        uri= "/*";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, uri, callback );

        uri= "/some_resource/*";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, uri, callback );

        uri= "/some_resource/child/*";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, uri, callback );

        uri= "*";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, "/" + uri, callback );

        uri= "some_resource/*";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, "/" + uri, callback );

        uri= "some_resource/child/*";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, "/" + uri, callback );

        uri= "/some_resource*";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, uri, callback );

        uri= "/some_resource/child*";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, uri, callback );

        uri= "/some_resource/child/child*";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, uri, callback );
    }

    @Test
    public void testConstructorWithInvalidUriNull() throws InvalidResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri;
        OperationalListener listener;

        exception.expect( InvalidResourceUriException.class );
        exception.expectMessage( "Invalid CoAP resource uri" );
        exception.expectMessage( "null is not allowed" );

        uri= null;
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, uri, callback );
    }

    @Test
    public void testConstructorWithInvalidUriEmpty1() throws InvalidResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri;
        OperationalListener listener;

        exception.expect( InvalidResourceUriException.class );
        exception.expectMessage( "Invalid CoAP resource uri" );
        exception.expectMessage( "uri cannot be empty" );
        uri= "";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, uri, callback );
    }

    @Test
    public void testConstructorWithInvalidUriEmpty2() throws InvalidResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri;
        OperationalListener listener;

        exception.expect( InvalidResourceUriException.class );
        exception.expectMessage( "Invalid CoAP resource uri" );
        exception.expectMessage( "uri cannot be empty" );
        uri= "/";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, uri, callback );
    }

    @Test
    public void testConstructorWithInvalidUriMultipleWildcard1() throws InvalidResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri;
        OperationalListener listener;

        exception.expect( InvalidResourceUriException.class );
        exception.expectMessage( "Invalid CoAP resource uri" );
        exception.expectMessage( "wildcard needs to be last character" );
        uri= "**";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, uri, callback );
    }

    @Test
    public void testConstructorWithInvalidUriMultipleWildcard2() throws InvalidResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri;
        OperationalListener listener;

        exception.expect( InvalidResourceUriException.class );
        exception.expectMessage( "Invalid CoAP resource uri" );
        exception.expectMessage( "wildcard needs to be last character" );
        uri= "/some_resource/**";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, uri, callback );
    }

    @Test
    public void testConstructorWithInvalidUriMultipleWildcard3() throws InvalidResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri;
        OperationalListener listener;

        exception.expect( InvalidResourceUriException.class );
        exception.expectMessage( "Invalid CoAP resource uri" );
        exception.expectMessage( "wildcard needs to be last character" );
        uri= "/some_resource/*/*";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, uri, callback );
    }

    @Test
    public void testConstructorWithInvalidUriMultipleWildcard4() throws InvalidResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri;
        OperationalListener listener;

        exception.expect( InvalidResourceUriException.class );
        exception.expectMessage( "Invalid CoAP resource uri" );
        exception.expectMessage( "wildcard needs to be last character" );
        uri= "/some_resource/*/child/*";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, uri, callback );
    }

    @Test
    public void testConstructorWithInvalidUriMultipleWildcard5() throws InvalidResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri;
        OperationalListener listener;

        exception.expect( InvalidResourceUriException.class );
        exception.expectMessage( "Invalid CoAP resource uri" );
        exception.expectMessage( "wildcard needs to be last character" );
        uri= "/some_resource/*/child";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, uri, callback );
    }

    @Test
    public void testConstructorWithInvalidUriMultipleWildcard6() throws InvalidResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri;
        OperationalListener listener;

        exception.expect( InvalidResourceUriException.class );
        exception.expectMessage( "Invalid CoAP resource uri" );
        exception.expectMessage( "wildcard needs to be last character" );
        uri= "/some_resource*/child/*";
        listener= new OperationalListener( uri, callback );
        assertOperationalListener( listener, uri, callback );
    }

    @Test
    public void testSetUri() throws InvalidResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri= "/initial";
        OperationalListener listener= new OperationalListener( uri, callback );

        uri= "/some_resource";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, callback );

        uri= "/some_resource/child";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, callback );

        uri= "/some_resource/child/child";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, callback );

        uri= "some_resource";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, "/" + uri, callback );

        uri= "some_resource/child";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, "/" + uri, callback );

        uri= "some_resource/child/child";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, "/" + uri, callback );

        uri= "/*";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, callback );

        uri= "/some_resource/*";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, callback );

        uri= "/some_resource/child/*";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, callback );

        uri= "*";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, "/" + uri, callback );

        uri= "some_resource/*";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, "/" + uri, callback );

        uri= "some_resource/child/*";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, "/" + uri, callback );

        uri= "/some_resource*";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, callback );

        uri= "/some_resource/child*";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, callback );

        uri= "/some_resource/child/child*";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, callback );
    }

    @Test
    public void testSetCallback() throws InvalidResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri= "/initial";
        OperationalListener listener= new OperationalListener( uri, callback );

        callback= new TestSourceCallBack();
        listener.setCallback( callback );
        assertOperationalListener( listener, uri, callback );

        callback= new TestSourceCallBack();
        listener.setCallback( callback );
        assertOperationalListener( listener, uri, callback );
    }

    private void assertOperationalListener( OperationalListener listener, String uri, SourceCallback< byte[], ReceivedRequestAttributes > callback )
    {
        assertNotNull( "listener construction failed", listener );
        assertEquals( "listener uri has wrong value", uri, listener.getUriPattern() );
        assertEquals( "listener callback has wrong value", callback, listener.getCallback() );

    }
}
