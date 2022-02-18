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
package nl.teslanet.mule.connectors.coap.test.server.modules;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import java.io.InputStream;

import org.junit.Test;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;

import nl.teslanet.mule.connectors.coap.api.CoAPRequestAttributes;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResourceUriException;
import nl.teslanet.mule.connectors.coap.internal.server.OperationalListener;
import nl.teslanet.mule.connectors.coap.internal.server.RequestCodeFlags;


/**
 * Test operational listener
 *
 */
public class OperationalListenerTest
{
    @Test
    public void testConstructor() throws InternalResourceUriException
    {
        String uri= "/some_resource";
        RequestCodeFlags flags= new RequestCodeFlags();
        OperationalListener listener;
        TestSourceCallBack callback;

        callback= new TestSourceCallBack();
        listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        //        Field serverField= listener.getClass().getField( "server" );
        //        serverField.set( listener, server );
        //        Field uriPatternField= listener.getClass().getField( "uriPattern" );
        assertOperationalListener( listener, uri, flags, callback );

        uri= "/some_resource/child";
        listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        assertOperationalListener( listener, uri, flags, callback );

        uri= "/some_resource/child/child";
        flags= new RequestCodeFlags( true, false, false, false );
        listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        assertOperationalListener( listener, uri, flags, callback );

        uri= "some_resource";
        listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        assertOperationalListener( listener, "/" + uri, flags, callback );

        uri= "some_resource/child";
        flags= new RequestCodeFlags( true, true, false, false );
        listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        assertOperationalListener( listener, "/" + uri, flags, callback );

        uri= "some_resource/child/child";
        listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        assertOperationalListener( listener, "/" + uri, flags, callback );

        uri= "/*";
        listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        assertOperationalListener( listener, uri, flags, callback );

        uri= "/some_resource/*";
        listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        assertOperationalListener( listener, uri, flags, callback );

        uri= "/some_resource/child/*";
        flags= new RequestCodeFlags( true, true, true, false );
        callback= new TestSourceCallBack();
        listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        assertOperationalListener( listener, uri, flags, callback );

        uri= "*";
        listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        assertOperationalListener( listener, "/" + uri, flags, callback );

        uri= "some_resource/*";
        listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        assertOperationalListener( listener, "/" + uri, flags, callback );

        uri= "some_resource/child/*";
        listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        assertOperationalListener( listener, "/" + uri, flags, callback );

        uri= "/some_resource*";
        flags= new RequestCodeFlags( true, true, true, true );
        listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        assertOperationalListener( listener, uri, flags, callback );

        uri= "/some_resource/child*";
        listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        assertOperationalListener( listener, uri, flags, callback );

        uri= "/some_resource/child/child*";
        listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        assertOperationalListener( listener, uri, flags, callback );
    }

    @Test
    public void testConstructorWithInvalidUriNull() throws InternalResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri= null;
        RequestCodeFlags flags= new RequestCodeFlags( true, true, true, false );

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            @SuppressWarnings("unused")
            OperationalListener listener= new OperationalListener( uri, flags, callback );
        } );
        assertEquals( "exception has wrong message", "null value is not allowed.", e.getMessage());
    }

    @Test
    public void testConstructorWithInvalidUriEmpty1() throws InternalResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri= "";
        RequestCodeFlags flags= new RequestCodeFlags( true, true, true, false );

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            @SuppressWarnings("unused")
            OperationalListener listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        } );
        assertEquals( "exception has wrong message", "invalid uriPattern {  }, uri cannot be empty." , e.getMessage());
    }

    @Test
    public void testConstructorWithInvalidUriEmpty2() throws InternalResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri= "/";
        RequestCodeFlags flags= new RequestCodeFlags( true, true, true, false );

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            @SuppressWarnings("unused")
            OperationalListener listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        } );
        assertEquals( "exception has wrong message", "invalid uriPattern { / }, uri cannot be empty." , e.getMessage());
    }

    @Test
    public void testConstructorWithInvalidUriMultipleWildcard1() throws InternalResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri= "**";
        RequestCodeFlags flags= new RequestCodeFlags( true, true, true, false );

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        } );
        assertEquals( "exception has wrong message", "invalid uriPattern { ** }, wildcard needs to be last character." , e.getMessage());
    }

    @Test
    public void testConstructorWithInvalidUriMultipleWildcard2() throws InternalResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri= "/some_resource/**";
        RequestCodeFlags flags= new RequestCodeFlags( true, true, true, false );

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        } );
        assertEquals( "exception has wrong message", "invalid uriPattern { /some_resource/** }, wildcard needs to be last character." , e.getMessage());
    }

    @Test
    public void testConstructorWithInvalidUriMultipleWildcard3() throws InternalResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri= "/some_resource/*/*";
        RequestCodeFlags flags= new RequestCodeFlags( true, true, true, false );

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        } );
        assertEquals( "exception has wrong message", "invalid uriPattern { /some_resource/*/* }, wildcard needs to be last character.", e.getMessage());
    }

    @Test
    public void testConstructorWithInvalidUriMultipleWildcard4() throws InternalResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri= "/some_resource/*/child/*";
        RequestCodeFlags flags= new RequestCodeFlags( true, true, true, false );

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        } );
        assertEquals( "exception has wrong message", "invalid uriPattern { /some_resource/*/child/* }, wildcard needs to be last character.", e.getMessage());
    }

    @Test
    public void testConstructorWithInvalidUriMultipleWildcard5() throws InternalResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri= "/some_resource/*/child";
        RequestCodeFlags flags= new RequestCodeFlags( true, true, true, false );

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        } );
        assertEquals( "exception has wrong message", "invalid uriPattern { /some_resource/*/child }, wildcard needs to be last character.", e.getMessage());
    }

    @Test
    public void testConstructorWithInvalidUriMultipleWildcard6() throws InternalResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri= "/some_resource*/child/*";
        RequestCodeFlags flags= new RequestCodeFlags( true, true, true, false );

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            new OperationalListener( uri, new RequestCodeFlags( flags ), callback );
        } );
        assertEquals( "exception has wrong message", "invalid uriPattern { /some_resource*/child/* }, wildcard needs to be last character.", e.getMessage());
    }

    @Test
    public void testSetUri() throws InternalResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri= "/initial";
        RequestCodeFlags flags= new RequestCodeFlags( true, true, true, false );
        OperationalListener listener= new OperationalListener( uri, new RequestCodeFlags( flags ), callback );

        uri= "/some_resource";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, flags, callback );

        uri= "/some_resource/child";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, flags, callback );

        uri= "/some_resource/child/child";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, flags, callback );

        uri= "some_resource";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, "/" + uri, flags, callback );

        uri= "some_resource/child";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, "/" + uri, flags, callback );

        uri= "some_resource/child/child";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, "/" + uri, flags, callback );

        uri= "/*";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, flags, callback );

        uri= "/some_resource/*";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, flags, callback );

        uri= "/some_resource/child/*";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, flags, callback );

        uri= "*";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, "/" + uri, flags, callback );

        uri= "some_resource/*";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, "/" + uri, flags, callback );

        uri= "some_resource/child/*";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, "/" + uri, flags, callback );

        uri= "/some_resource*";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, flags, callback );

        uri= "/some_resource/child*";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, flags, callback );

        uri= "/some_resource/child/child*";
        listener.setUriPattern( uri );
        assertOperationalListener( listener, uri, flags, callback );
    }

    @Test
    public void testSetCallback() throws InternalResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        String uri= "/initial";
        RequestCodeFlags flags= new RequestCodeFlags( true, true, true, false );
        OperationalListener listener= new OperationalListener( uri, flags, callback );

        callback= new TestSourceCallBack();
        listener.setCallback( callback );
        assertOperationalListener( listener, uri, flags, callback );

        callback= new TestSourceCallBack();
        listener.setCallback( callback );
        assertOperationalListener( listener, uri, flags, callback );
    }

    private void assertOperationalListener( OperationalListener listener, String uri, RequestCodeFlags flags, SourceCallback< InputStream, CoAPRequestAttributes > callback )
    {
        assertNotNull( "listener construction failed", listener );
        assertEquals( "listener uri has wrong value", uri, listener.getUriPattern() );
        assertEquals( "listener callback has wrong value", callback, listener.getCallback() );
        assertEquals( "listener flags has wrong value", flags, listener.getRequestCodeFlags() );
    }
}
