/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2026 (teslanet.nl) Rogier Cobben
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.elements.config.Configuration;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.api.Defs;
import nl.teslanet.mule.connectors.coap.api.NewSubResourceConfig;
import nl.teslanet.mule.connectors.coap.api.NewSubResourceParams;
import nl.teslanet.mule.connectors.coap.api.ResourceConfig;
import nl.teslanet.mule.connectors.coap.api.ResourceParams;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResourceRegistryException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResourceUriException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUriPatternException;
import nl.teslanet.mule.connectors.coap.internal.server.OperationalListener;
import nl.teslanet.mule.connectors.coap.internal.server.OperationalServer;
import nl.teslanet.mule.connectors.coap.internal.server.RequestCodeFlags;
import nl.teslanet.mule.connectors.coap.internal.server.ServedResource;


/**
 * Test resource server.
 */
public class ResourceRegistryTest
{
    Configuration config= Configuration.createStandardWithoutFile();

    @Test
    public void testConstructor() throws InternalResourceRegistryException, InternalResourceUriException
    {
        OperationalServer server;

        server= new OperationalServer( config );
        assertNotNull( server );
        assertNotNull( "register should set root resource", server.getResource( Defs.COAP_ROOTRESOURCE ) );
    }

    @Test
    public void testAddResourceWithoutName() throws InternalResourceUriException, InternalResourceRegistryException
    {
        OperationalServer server= new OperationalServer( config );
        ResourceConfig resourceConfig= new ResourceConfig();

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            server.add( "/", resourceConfig );
        } );
        assertEquals( "exception has wrong message", "An empty resource name is invalid.", e.getMessage() );

        resourceConfig.setResourceName( "" );
        e= assertThrows( InternalResourceUriException.class, () -> {
            server.add( "/", resourceConfig );
        } );
        assertEquals( "exception has wrong message", "An empty resource name is invalid.", e.getMessage() );
    }

    @Test
    public void testAddResourceWithoutName2() throws InternalResourceUriException, InternalResourceRegistryException
    {
        OperationalServer server= new OperationalServer( config );
        ResourceParams resourceParams= new ResourceParams();

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            server.add( resourceParams );
        } );
        assertEquals( "exception has wrong message", "An empty resource path is invalid.", e.getMessage() );

        resourceParams.setResourcePath( "/" );
        e= assertThrows( InternalResourceUriException.class, () -> {
            server.add( resourceParams );
        } );
        assertEquals( "exception has wrong message", "An empty resource path is invalid.", e.getMessage() );
    }

    @Test
    public void testAddResourceWithSlash() throws InternalResourceUriException, InternalResourceRegistryException
    {
        OperationalServer server= new OperationalServer( config );
        ResourceConfig resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( "x/y" );

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );
        } );
        assertEquals( "exception has wrong message", "'/' in 'x/y' is not supported by the implementation!", e.getCause().getMessage() );
    }

    @Test
    public void testAddResourceRoot() throws InternalResourceUriException, InternalResourceRegistryException
    {
        OperationalServer server= new OperationalServer( config );
        ResourceConfig resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( "" );

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );
        } );
        assertEquals( "exception has wrong message", "An empty resource name is invalid.", e.getMessage() );
    }

    @Test
    public void testAddResourceRoot2() throws InternalResourceUriException, InternalResourceRegistryException
    {
        OperationalServer server= new OperationalServer( config );
        ResourceParams resourceParams= new ResourceParams();
        resourceParams.setResourcePath( "" );

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            server.add( resourceParams );
        } );
        assertEquals( "exception has wrong message", "An empty resource path is invalid.", e.getMessage() );
    }

    @Test
    public void testAddResourceTwice() throws InternalResourceUriException, InternalResourceRegistryException
    {
        String resourceName= "double";
        OperationalServer server= new OperationalServer( config );
        ResourceConfig resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( resourceName );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );
        assertNotNull( "server does not contain resource", server.getResource( "/" + resourceName ) );
    }

    @Test
    public void testAddResourceTwice2() throws InternalResourceUriException, InternalResourceRegistryException
    {
        String resourcePath= "/double";
        OperationalServer server= new OperationalServer( config );
        ResourceParams resourceParams= new ResourceParams();
        resourceParams.setResourcePath( resourcePath );
        server.add( resourceParams );
        server.add( resourceParams );
        assertNotNull( "server does not contain resource", server.getResource( resourcePath ) );
    }

    @Test
    public void testAddResourceConfig() throws InternalResourceUriException, InternalResourceRegistryException
    {
        OperationalServer server= new OperationalServer( config );
        ResourceConfig resourceConfig;
        String name1= "resource1";
        String uri1= "/resource1";
        String name2= "resource2";
        String uri2= "/resource1/resource2";
        String name3= "resource3";
        String uri3= "/resource1/resource2/resource3";
        String name4= "resource4";
        String uri4= "/resource1/resource4";

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );
        assertNotNull( server );
        assertEquals( "server does not contain resource1", uri1, server.getResource( uri1 ).getURI() );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        server.add( uri1, resourceConfig );
        assertNotNull( server );
        assertEquals( "server does not contain resource2", uri2, server.getResource( uri2 ).getURI() );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        server.add( uri2, resourceConfig );
        assertNotNull( server );
        assertEquals( "server does not contain resource3", uri3, server.getResource( uri3 ).getURI() );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        server.add( uri1, resourceConfig );
        assertNotNull( server );
        assertEquals( "server does not contain resource4", uri4, server.getResource( uri4 ).getURI() );
    }

    @Test
    public void testAddResourceParams() throws InternalResourceUriException, InternalResourceRegistryException
    {
        OperationalServer server= new OperationalServer( config );
        ResourceParams resourceParams;
        String uri1= "/resource1";
        String uri2= "/resource1/resource2";
        String uri3= "/resource1/resource2/resource3";
        String uri4= "/resource1/resource4";

        resourceParams= new ResourceParams();
        resourceParams.setResourcePath( uri1 );
        server.add( resourceParams );
        assertNotNull( server );
        assertEquals( "server does not contain resource1", uri1, server.getResource( uri1 ).getURI() );

        resourceParams= new ResourceParams();
        resourceParams.setResourcePath( uri2 );
        server.add( resourceParams );
        assertNotNull( server );
        assertEquals( "server does not contain resource2", uri2, server.getResource( uri2 ).getURI() );

        resourceParams= new ResourceParams();
        resourceParams.setResourcePath( uri3 );
        server.add( resourceParams );
        assertNotNull( server );
        assertEquals( "server does not contain resource3", uri3, server.getResource( uri3 ).getURI() );

        resourceParams= new ResourceParams();
        resourceParams.setResourcePath( uri4 );
        server.add( resourceParams );
        assertNotNull( server );
        assertEquals( "server does not contain resource4", uri4, server.getResource( uri4 ).getURI() );
    }

    @Test
    public void testRemoveRoot() throws InternalUriPatternException, InternalResourceUriException
    {
        OperationalServer server= new OperationalServer( config );
        assertNotNull( "register should have root resource", server.getResource( Defs.COAP_ROOTRESOURCE ) );
        InternalUriPatternException e= assertThrows( InternalUriPatternException.class, () -> {
            server.remove( "/" );
        } );
        assertEquals( "Reference to root resource is not allowed. { / }", e.getMessage() );
    }

    @Test
    public void testRemoveRootCoapResource() throws InternalResourceUriException, InternalResourceRegistryException
    {
        OperationalServer server= new OperationalServer( config );
        server.remove( server.getResource( Defs.COAP_ROOTRESOURCE ) );
        assertNotNull( "register should still have root resource", server.getResource( Defs.COAP_ROOTRESOURCE ) );
    }

    @Test
    public void testRemoveWildcard() throws InternalUriPatternException, InternalResourceUriException
    {
        OperationalServer server= new OperationalServer( config );
        assertNotNull( "register should have root resource", server.getResource( Defs.COAP_ROOTRESOURCE ) );
        InternalUriPatternException e= assertThrows( InternalUriPatternException.class, () -> {
            server.remove( "*" );
        } );
        assertEquals( "Including root resource is not allowed. { * }", e.getMessage() );
    }

    @Test
    public void testRemoveResource1() throws InternalResourceUriException, InternalUriPatternException
    {
        OperationalServer server= new OperationalServer( config );
        ResourceConfig resourceConfig;
        String name1= "resource1";
        String uri1= "/resource1";
        String name2= "resource2";
        String uri2= "/resource1/resource2";
        String name3= "resource3";
        String uri3= "/resource1/resource2/resource3";
        String name4= "resource4";
        String uri4= "/resource1/resource4";

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        server.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        server.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        server.add( uri1, resourceConfig );

        assertEquals( "server does not contain resource1", uri1, server.getResource( uri1 ).getURI() );
        assertEquals( "server does not contain resource2", uri2, server.getResource( uri2 ).getURI() );
        assertEquals( "server does not contain resource3", uri3, server.getResource( uri3 ).getURI() );
        assertEquals( "server does not contain resource4", uri4, server.getResource( uri4 ).getURI() );

        server.remove( uri4 );

        assertEquals( "server does not contain resource1", uri1, server.getResource( uri1 ).getURI() );
        assertEquals( "server does not contain resource2", uri2, server.getResource( uri2 ).getURI() );
        assertEquals( "server does not contain resource3", uri3, server.getResource( uri3 ).getURI() );
        assertTrue( "server must not contain resource3", server.findResources( uri4, true ).isEmpty() );
    }

    @Test
    public void testRemoveResource2() throws InternalResourceUriException, InternalUriPatternException
    {
        OperationalServer server= new OperationalServer( config );
        ResourceConfig resourceConfig;
        String name1= "resource1";
        String uri1= "/resource1";
        String name2= "resource2";
        String uri2= "/resource1/resource2";
        String name3= "resource3";
        String uri3= "/resource1/resource2/resource3";
        String name4= "resource4";
        String uri4= "/resource1/resource4";

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        server.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        server.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        server.add( uri1, resourceConfig );

        assertEquals( "server does not contain resource1", uri1, server.getResource( uri1 ).getURI() );
        assertEquals( "server does not contain resource2", uri2, server.getResource( uri2 ).getURI() );
        assertEquals( "server does not contain resource3", uri3, server.getResource( uri3 ).getURI() );
        assertEquals( "server does not contain resource4", uri4, server.getResource( uri4 ).getURI() );

        server.remove( uri2 );

        assertEquals( "server does not contain resource1", uri1, server.getResource( uri1 ).getURI() );
        assertTrue( "server must not contain resource2", server.findResources( uri2, true ).isEmpty() );
        assertTrue( "server must not contain resource3", server.findResources( uri3, true ).isEmpty() );
        assertEquals( "server does not contain resource4", uri4, server.getResource( uri4 ).getURI() );
    }

    @Test
    public void testAddOperationalListener() throws InternalResourceUriException, InternalUriPatternException
    {
        TestSourceCallBack callback= new TestSourceCallBack();

        OperationalServer server= new OperationalServer( config );
        ArrayList< String > uris= new ArrayList<>();
        uris.add( "/resource1" );
        uris.add( "/resource1/resource2" );
        uris.add( "/resource1/resource2/resource3" );
        uris.add( "/resource1/resource4" );
        uris.add( "/resource1/resource5" );
        uris.add( "/resource1/resource5/resource6" );
        uris.add( "/resource1/resource5/resource6/resource7" );
        uris.add( "/resource1/resource5/resource6/resource8" );
        ArrayList< RequestCodeFlags > flags= new ArrayList<>();
        flags.add( new RequestCodeFlags( false, false, false, false, false, false, false ) );
        flags.add( new RequestCodeFlags( true, false, false, false, false, false, false ) );
        flags.add( new RequestCodeFlags( true, true, false, false, false, false, false ) );
        flags.add( new RequestCodeFlags( true, true, true, false, false, false, false ) );
        flags.add( new RequestCodeFlags( true, true, true, true, false, false, false ) );
        flags.add( new RequestCodeFlags( true, true, true, true, true, false, false ) );
        flags.add( new RequestCodeFlags( true, true, true, true, true, true, false ) );
        flags.add( new RequestCodeFlags( true, true, true, true, true, true, true ) );

        //see that no exceptions occur
        for ( int i= 0; i < 8; i++ )
        {
            server.add( new OperationalListener( uris.get( i ), flags.get( i ), callback ) );
        }
        //assert
        assertNotNull( "assert failed", server );
    }

    @Test
    public void testCallBack() throws InternalResourceUriException, InternalResourceRegistryException, InternalUriPatternException
    {
        OperationalServer server= new OperationalServer( config );
        TestSourceCallBack callback0= new TestSourceCallBack();
        TestSourceCallBack callback1= new TestSourceCallBack();
        TestSourceCallBack callback2= new TestSourceCallBack();
        TestSourceCallBack callback3= new TestSourceCallBack();
        TestSourceCallBack callback4= new TestSourceCallBack();
        ResourceConfig resourceConfig;
        String name1= "resource1";
        String uri1= "/resource1";
        String name2= "resource2";
        String uri2= "/resource1/resource2";
        String name3= "resource3";
        String uri3= "/resource1/resource2/resource3";
        String name4= "resource4";
        String uri4= "/resource1/resource4";
        RequestCodeFlags flags0= new RequestCodeFlags( true, true, true, true, true, true, true );
        RequestCodeFlags flags1= new RequestCodeFlags( true, true, true, true, true, true, true );
        RequestCodeFlags flags2= new RequestCodeFlags( true, true, true, true, true, true, true );
        RequestCodeFlags flags3= new RequestCodeFlags( true, true, true, true, true, true, true );
        RequestCodeFlags flags4= new RequestCodeFlags( true, true, true, true, true, true, true );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        server.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        server.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        server.add( uri1, resourceConfig );

        OperationalListener listener;
        listener= new OperationalListener( Defs.COAP_ROOTRESOURCE_NAME, flags0, callback0 );
        server.add( listener );

        listener= new OperationalListener( uri1, flags1, callback1 );
        server.add( listener );

        listener= new OperationalListener( uri2, flags2, callback2 );
        server.add( listener );

        listener= new OperationalListener( uri3, flags3, callback3 );
        server.add( listener );

        listener= new OperationalListener( uri4, flags4, callback4 );
        server.add( listener );

        assertEquals( "resource1 has wrong callback", callback0, server.getResource( Defs.COAP_ROOTRESOURCE ).getGetCallback() );
        assertEquals( "resource1 has wrong callback", callback0, server.getResource( Defs.COAP_ROOTRESOURCE ).getPostCallback() );
        assertEquals( "resource1 has wrong callback", callback0, server.getResource( Defs.COAP_ROOTRESOURCE ).getPutCallback() );
        assertEquals( "resource1 has wrong callback", callback0, server.getResource( Defs.COAP_ROOTRESOURCE ).getDeleteCallback() );
        assertEquals( "resource1 has wrong callback", callback0, server.getResource( Defs.COAP_ROOTRESOURCE ).getFetchCallback() );
        assertEquals( "resource1 has wrong callback", callback0, server.getResource( Defs.COAP_ROOTRESOURCE ).getPatchCallback() );
        assertEquals( "resource1 has wrong callback", callback0, server.getResource( Defs.COAP_ROOTRESOURCE ).getIpatchCallback() );

        assertEquals( "resource1 has wrong callback", callback1, server.getResource( uri1 ).getGetCallback() );
        assertEquals( "resource1 has wrong callback", callback1, server.getResource( uri1 ).getPostCallback() );
        assertEquals( "resource1 has wrong callback", callback1, server.getResource( uri1 ).getPutCallback() );
        assertEquals( "resource1 has wrong callback", callback1, server.getResource( uri1 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong callback", callback1, server.getResource( uri1 ).getFetchCallback() );
        assertEquals( "resource1 has wrong callback", callback1, server.getResource( uri1 ).getPatchCallback() );
        assertEquals( "resource1 has wrong callback", callback1, server.getResource( uri1 ).getIpatchCallback() );

        assertEquals( "resource2 has wrong callback", callback2, server.getResource( uri2 ).getGetCallback() );
        assertEquals( "resource2 has wrong callback", callback2, server.getResource( uri2 ).getPostCallback() );
        assertEquals( "resource2 has wrong callback", callback2, server.getResource( uri2 ).getPutCallback() );
        assertEquals( "resource2 has wrong callback", callback2, server.getResource( uri2 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong callback", callback2, server.getResource( uri2 ).getFetchCallback() );
        assertEquals( "resource1 has wrong callback", callback2, server.getResource( uri2 ).getPatchCallback() );
        assertEquals( "resource1 has wrong callback", callback2, server.getResource( uri2 ).getIpatchCallback() );

        assertEquals( "resource3 has wrong callback", callback3, server.getResource( uri3 ).getGetCallback() );
        assertEquals( "resource3 has wrong callback", callback3, server.getResource( uri3 ).getPostCallback() );
        assertEquals( "resource3 has wrong callback", callback3, server.getResource( uri3 ).getPutCallback() );
        assertEquals( "resource3 has wrong callback", callback3, server.getResource( uri3 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong callback", callback3, server.getResource( uri3 ).getFetchCallback() );
        assertEquals( "resource1 has wrong callback", callback3, server.getResource( uri3 ).getPatchCallback() );
        assertEquals( "resource1 has wrong callback", callback3, server.getResource( uri3 ).getIpatchCallback() );

        assertEquals( "resource4 has wrong callback", callback4, server.getResource( uri4 ).getGetCallback() );
        assertEquals( "resource4 has wrong callback", callback4, server.getResource( uri4 ).getPostCallback() );
        assertEquals( "resource4 has wrong callback", callback4, server.getResource( uri4 ).getPutCallback() );
        assertEquals( "resource4 has wrong callback", callback4, server.getResource( uri4 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong callback", callback4, server.getResource( uri4 ).getFetchCallback() );
        assertEquals( "resource1 has wrong callback", callback4, server.getResource( uri4 ).getPatchCallback() );
        assertEquals( "resource1 has wrong callback", callback4, server.getResource( uri4 ).getIpatchCallback() );
    }

    @Test
    public void testCallBackWithRequestCodeFlags() throws InternalResourceUriException, InternalResourceRegistryException, InternalUriPatternException
    {
        OperationalServer server= new OperationalServer( config );
        TestSourceCallBack callback1= new TestSourceCallBack();
        TestSourceCallBack callback2= new TestSourceCallBack();
        TestSourceCallBack callback3= new TestSourceCallBack();
        TestSourceCallBack callback4= new TestSourceCallBack();
        TestSourceCallBack callback5= new TestSourceCallBack();
        TestSourceCallBack callback6= new TestSourceCallBack();
        TestSourceCallBack callback7= new TestSourceCallBack();
        ResourceConfig resourceConfig;
        String name1= "resource1";
        String uri1= "/resource1";
        RequestCodeFlags flags1= new RequestCodeFlags( true, false, false, false, false, false, false );
        RequestCodeFlags flags2= new RequestCodeFlags( false, true, false, false, false, false, false );
        RequestCodeFlags flags3= new RequestCodeFlags( false, false, true, false, false, false, false );
        RequestCodeFlags flags4= new RequestCodeFlags( false, false, false, true, false, false, false );
        RequestCodeFlags flags5= new RequestCodeFlags( false, false, false, false, true, false, false );
        RequestCodeFlags flags6= new RequestCodeFlags( false, false, false, false, false, true, false );
        RequestCodeFlags flags7= new RequestCodeFlags( false, false, false, false, false, false, true );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );

        OperationalListener listener;
        listener= new OperationalListener( uri1, flags1, callback1 );
        server.add( listener );

        listener= new OperationalListener( uri1, flags2, callback2 );
        server.add( listener );

        listener= new OperationalListener( uri1, flags3, callback3 );
        server.add( listener );

        listener= new OperationalListener( uri1, flags4, callback4 );
        server.add( listener );

        listener= new OperationalListener( uri1, flags5, callback5 );
        server.add( listener );

        listener= new OperationalListener( uri1, flags6, callback6 );
        server.add( listener );

        listener= new OperationalListener( uri1, flags7, callback7 );
        server.add( listener );

        assertEquals( "resource1 has wrong callback", callback1, server.getResource( uri1 ).getGetCallback() );
        assertEquals( "resource1 has wrong callback", callback2, server.getResource( uri1 ).getPostCallback() );
        assertEquals( "resource1 has wrong callback", callback3, server.getResource( uri1 ).getPutCallback() );
        assertEquals( "resource1 has wrong callback", callback4, server.getResource( uri1 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong callback", callback5, server.getResource( uri1 ).getFetchCallback() );
        assertEquals( "resource1 has wrong callback", callback6, server.getResource( uri1 ).getPatchCallback() );
        assertEquals( "resource1 has wrong callback", callback7, server.getResource( uri1 ).getIpatchCallback() );
    }

    @Test
    public void testCallBackWithWildcard1() throws InternalResourceUriException, InternalResourceRegistryException, InternalUriPatternException
    {
        OperationalServer server= new OperationalServer( config );
        TestSourceCallBack callback0= new TestSourceCallBack();
        TestSourceCallBack callback1= new TestSourceCallBack();
        TestSourceCallBack callback2= new TestSourceCallBack();
        ResourceConfig resourceConfig;
        String uri0= Defs.COAP_ROOTRESOURCE;
        String name1= "resource1";
        String uri1= "/resource1";
        String name2= "resource2";
        String uri2= "/resource1/resource2";
        String name3= "resource3";
        String uri3= "/resource1/resource2/resource3";
        String name4= "resource4";
        String uri4= "/resource1/resource4";
        RequestCodeFlags flags0= new RequestCodeFlags( true, true, true, true, true, true, true );
        RequestCodeFlags flags1= new RequestCodeFlags( true, true, true, true, true, true, true );
        RequestCodeFlags flags2= new RequestCodeFlags( true, true, true, true, true, true, true );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        server.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        server.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        server.add( uri1, resourceConfig );

        OperationalListener listener;
        listener= new OperationalListener( "*", flags0, callback0 );
        server.add( listener );

        listener= new OperationalListener( "/*", flags1, callback1 );
        server.add( listener );

        listener= new OperationalListener( uri2, flags2, callback2 );
        server.add( listener );

        assertEquals( "root resource has wrong get callback", callback0, server.getResource( uri0 ).getGetCallback() );
        assertEquals( "root resource has wrong post callback", callback0, server.getResource( uri0 ).getPostCallback() );
        assertEquals( "root resource has wrong put callback", callback0, server.getResource( uri0 ).getPutCallback() );
        assertEquals( "root resource has wrong delete callback", callback0, server.getResource( uri0 ).getDeleteCallback() );
        assertEquals( "root resource has wrong fetch callback", callback0, server.getResource( uri0 ).getFetchCallback() );
        assertEquals( "root resource has wrong patch callback", callback0, server.getResource( uri0 ).getPatchCallback() );
        assertEquals( "root resource has wrong ipatch callback", callback0, server.getResource( uri0 ).getIpatchCallback() );

        assertEquals( "resource1 has wrong get callback", callback1, server.getResource( uri1 ).getGetCallback() );
        assertEquals( "resource1 has wrong post callback", callback1, server.getResource( uri1 ).getPostCallback() );
        assertEquals( "resource1 has wrong put callback", callback1, server.getResource( uri1 ).getPutCallback() );
        assertEquals( "resource1 has wrong delete callback", callback1, server.getResource( uri1 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong fetch callback", callback1, server.getResource( uri1 ).getFetchCallback() );
        assertEquals( "resource1 has wrong patch callback", callback1, server.getResource( uri1 ).getPatchCallback() );
        assertEquals( "resource1 has wrong ipatch callback", callback1, server.getResource( uri1 ).getIpatchCallback() );

        assertEquals( "resource2 has wrong get callback", callback2, server.getResource( uri2 ).getGetCallback() );
        assertEquals( "resource2 has wrong post callback", callback2, server.getResource( uri2 ).getPostCallback() );
        assertEquals( "resource2 has wrong put callback", callback2, server.getResource( uri2 ).getPutCallback() );
        assertEquals( "resource2 has wrong delete callback", callback2, server.getResource( uri2 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong fetch callback", callback2, server.getResource( uri2 ).getFetchCallback() );
        assertEquals( "resource1 has wrong patch callback", callback2, server.getResource( uri2 ).getPatchCallback() );
        assertEquals( "resource1 has wrong ipatch callback", callback2, server.getResource( uri2 ).getIpatchCallback() );

        assertEquals( "resource3 has wrong get callback", callback1, server.getResource( uri3 ).getGetCallback() );
        assertEquals( "resource3 has wrong post callback", callback1, server.getResource( uri3 ).getPostCallback() );
        assertEquals( "resource3 has wrong put callback", callback1, server.getResource( uri3 ).getPutCallback() );
        assertEquals( "resource3 has wrong delete callback", callback1, server.getResource( uri3 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong fetch callback", callback1, server.getResource( uri3 ).getFetchCallback() );
        assertEquals( "resource1 has wrong patch callback", callback1, server.getResource( uri3 ).getPatchCallback() );
        assertEquals( "resource1 has wrong ipatch callback", callback1, server.getResource( uri3 ).getIpatchCallback() );

        assertEquals( "resource4 has wrong get callback", callback1, server.getResource( uri4 ).getGetCallback() );
        assertEquals( "resource4 has wrong post callback", callback1, server.getResource( uri4 ).getPostCallback() );
        assertEquals( "resource4 has wrong put callback", callback1, server.getResource( uri4 ).getPutCallback() );
        assertEquals( "resource4 has wrong delete callback", callback1, server.getResource( uri4 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong fetch callback", callback1, server.getResource( uri4 ).getFetchCallback() );
        assertEquals( "resource1 has wrong patch callback", callback1, server.getResource( uri4 ).getPatchCallback() );
        assertEquals( "resource1 has wrong ipatch callback", callback1, server.getResource( uri4 ).getIpatchCallback() );
    }

    @Test
    public void testCallBackWithWildcardAndRequestCodeFlags1() throws InternalResourceUriException, InternalResourceRegistryException, InternalUriPatternException
    {
        OperationalServer server= new OperationalServer( config );
        TestSourceCallBack callback1= new TestSourceCallBack();
        TestSourceCallBack callback2= new TestSourceCallBack();
        TestSourceCallBack callback3= new TestSourceCallBack();
        TestSourceCallBack callback4= new TestSourceCallBack();
        TestSourceCallBack callback5= new TestSourceCallBack();
        TestSourceCallBack callback6= new TestSourceCallBack();
        TestSourceCallBack callback7= new TestSourceCallBack();
        TestSourceCallBack callback8= new TestSourceCallBack();
        ResourceConfig resourceConfig;
        String name1= "resource1";
        String uri1= "/resource1";
        String name2= "resource2";
        String uri2= "/resource1/resource2";
        String name3= "resource3";
        String uri3= "/resource1/resource2/resource3";
        String name4= "resource4";
        String uri4= "/resource1/resource4";
        RequestCodeFlags flags1= new RequestCodeFlags( true, true, true, true, true, true, true );
        RequestCodeFlags flags2= new RequestCodeFlags( true, false, false, false, false, false, false );
        RequestCodeFlags flags3= new RequestCodeFlags( false, true, false, false, false, false, false );
        RequestCodeFlags flags4= new RequestCodeFlags( false, false, true, false, false, false, false );
        RequestCodeFlags flags5= new RequestCodeFlags( false, false, false, true, false, false, false );
        RequestCodeFlags flags6= new RequestCodeFlags( false, false, false, false, true, false, false );
        RequestCodeFlags flags7= new RequestCodeFlags( false, false, false, false, false, true, false );
        RequestCodeFlags flags8= new RequestCodeFlags( false, false, false, false, false, false, true );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        server.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        server.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        server.add( uri1, resourceConfig );

        OperationalListener listener;
        listener= new OperationalListener( "/*", flags1, callback1 );
        server.add( listener );
        listener= new OperationalListener( uri2, flags2, callback2 );
        server.add( listener );
        listener= new OperationalListener( uri2, flags3, callback3 );
        server.add( listener );
        listener= new OperationalListener( uri2, flags4, callback4 );
        server.add( listener );
        listener= new OperationalListener( uri2, flags5, callback5 );
        server.add( listener );
        listener= new OperationalListener( uri2, flags6, callback6 );
        server.add( listener );
        listener= new OperationalListener( uri2, flags7, callback7 );
        server.add( listener );
        listener= new OperationalListener( uri2, flags8, callback8 );
        server.add( listener );

        assertEquals( "resource1 has wrong get callback", callback1, server.getResource( uri1 ).getGetCallback() );
        assertEquals( "resource1 has wrong post callback", callback1, server.getResource( uri1 ).getPostCallback() );
        assertEquals( "resource1 has wrong put callback", callback1, server.getResource( uri1 ).getPutCallback() );
        assertEquals( "resource1 has wrong delete callback", callback1, server.getResource( uri1 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong fetch callback", callback1, server.getResource( uri1 ).getFetchCallback() );
        assertEquals( "resource1 has wrong patch callback", callback1, server.getResource( uri1 ).getPatchCallback() );
        assertEquals( "resource1 has wrong ipatch callback", callback1, server.getResource( uri1 ).getIpatchCallback() );

        assertEquals( "resource2 has wrong get callback", callback2, server.getResource( uri2 ).getGetCallback() );
        assertEquals( "resource2 has wrong post callback", callback3, server.getResource( uri2 ).getPostCallback() );
        assertEquals( "resource2 has wrong put callback", callback4, server.getResource( uri2 ).getPutCallback() );
        assertEquals( "resource2 has wrong delete callback", callback5, server.getResource( uri2 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong fetch callback", callback6, server.getResource( uri2 ).getFetchCallback() );
        assertEquals( "resource1 has wrong patch callback", callback7, server.getResource( uri2 ).getPatchCallback() );
        assertEquals( "resource1 has wrong ipatch callback", callback8, server.getResource( uri2 ).getIpatchCallback() );

        assertEquals( "resource3 has wrong get callback", callback1, server.getResource( uri3 ).getGetCallback() );
        assertEquals( "resource3 has wrong post callback", callback1, server.getResource( uri3 ).getPostCallback() );
        assertEquals( "resource3 has wrong put callback", callback1, server.getResource( uri3 ).getPutCallback() );
        assertEquals( "resource3 has wrong delete callback", callback1, server.getResource( uri3 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong fetch callback", callback1, server.getResource( uri3 ).getFetchCallback() );
        assertEquals( "resource1 has wrong patch callback", callback1, server.getResource( uri3 ).getPatchCallback() );
        assertEquals( "resource1 has wrong ipatch callback", callback1, server.getResource( uri3 ).getIpatchCallback() );

        assertEquals( "resource4 has wrong get callback", callback1, server.getResource( uri4 ).getGetCallback() );
        assertEquals( "resource4 has wrong post callback", callback1, server.getResource( uri4 ).getPostCallback() );
        assertEquals( "resource4 has wrong put callback", callback1, server.getResource( uri4 ).getPutCallback() );
        assertEquals( "resource4 has wrong delete callback", callback1, server.getResource( uri4 ).getDeleteCallback() );
        assertEquals( "resource4 has wrong fetch callback", callback1, server.getResource( uri4 ).getFetchCallback() );
        assertEquals( "resource4 has wrong patch callback", callback1, server.getResource( uri4 ).getPatchCallback() );
        assertEquals( "resource4 has wrong ipatch callback", callback1, server.getResource( uri4 ).getIpatchCallback() );
    }

    @Test
    public void testCallBackWithWildcard2() throws InternalResourceUriException, InternalResourceRegistryException, InternalUriPatternException
    {
        OperationalServer server= new OperationalServer( config );
        TestSourceCallBack callback0= new TestSourceCallBack();
        TestSourceCallBack callback1= new TestSourceCallBack();
        TestSourceCallBack callback2= new TestSourceCallBack();
        TestSourceCallBack callback3= new TestSourceCallBack();
        TestSourceCallBack callback4= new TestSourceCallBack();
        ResourceConfig resourceConfig;
        String name1= "resource1";
        String uri1= "/resource1";
        String name2= "resource2";
        String uri2= "/resource1/resource2";
        String name3= "resource3";
        String uri3= "/resource1/resource2/resource3";
        String name4= "resource4";
        String uri4= "/resource1/resource4";
        RequestCodeFlags flags0= new RequestCodeFlags( true, true, true, true, true, true, true );
        RequestCodeFlags flags1= new RequestCodeFlags( false, true, true, true, true, true, true );
        RequestCodeFlags flags2= new RequestCodeFlags( false, false, true, false, false, true, false );
        RequestCodeFlags flags3= new RequestCodeFlags( true, true, false, true, true, false, true );
        RequestCodeFlags flags4= new RequestCodeFlags( true, true, true, true, true, true, true );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        server.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        server.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        server.add( uri1, resourceConfig );

        OperationalListener listener;
        listener= new OperationalListener( "*", flags0, callback0 );
        server.add( listener );

        listener= new OperationalListener( "/*", flags1, callback1 );
        server.add( listener );

        listener= new OperationalListener( "/resource1/resource2/*", flags2, callback2 );
        server.add( listener );

        listener= new OperationalListener( "/resource1/resource2/*", flags3, callback3 );
        server.add( listener );

        listener= new OperationalListener( uri4, flags4, callback4 );
        server.add( listener );

        assertEquals( "resource1 has wrong get callback", callback0, server.getResource( Defs.COAP_ROOTRESOURCE ).getGetCallback() );
        assertEquals( "resource1 has wrong post callback", callback0, server.getResource( Defs.COAP_ROOTRESOURCE ).getPostCallback() );
        assertEquals( "resource1 has wrong put callback", callback0, server.getResource( Defs.COAP_ROOTRESOURCE ).getPutCallback() );
        assertEquals( "resource1 has wrong delete callback", callback0, server.getResource( Defs.COAP_ROOTRESOURCE ).getDeleteCallback() );
        assertEquals( "resource1 has wrong fetch callback", callback0, server.getResource( Defs.COAP_ROOTRESOURCE ).getFetchCallback() );
        assertEquals( "resource1 has wrong patch callback", callback0, server.getResource( Defs.COAP_ROOTRESOURCE ).getPatchCallback() );
        assertEquals( "resource1 has wrong ipatch callback", callback0, server.getResource( Defs.COAP_ROOTRESOURCE ).getIpatchCallback() );

        assertEquals( "resource1 has wrong get callback", callback0, server.getResource( uri1 ).getGetCallback() );
        assertEquals( "resource1 has wrong post callback", callback1, server.getResource( uri1 ).getPostCallback() );
        assertEquals( "resource1 has wrong put callback", callback1, server.getResource( uri1 ).getPutCallback() );
        assertEquals( "resource1 has wrong delete callback", callback1, server.getResource( uri1 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong fetch callback", callback1, server.getResource( uri1 ).getFetchCallback() );
        assertEquals( "resource1 has wrong patch callback", callback1, server.getResource( uri1 ).getPatchCallback() );
        assertEquals( "resource1 has wrong ipatch callback", callback1, server.getResource( uri1 ).getIpatchCallback() );

        assertEquals( "resource2 has wrong get callback", callback0, server.getResource( uri2 ).getGetCallback() );
        assertEquals( "resource2 has wrong post callback", callback1, server.getResource( uri2 ).getPostCallback() );
        assertEquals( "resource2 has wrong put callback", callback1, server.getResource( uri2 ).getPutCallback() );
        assertEquals( "resource2 has wrong delete callback", callback1, server.getResource( uri2 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong fetch callback", callback1, server.getResource( uri2 ).getFetchCallback() );
        assertEquals( "resource1 has wrong patch callback", callback1, server.getResource( uri2 ).getPatchCallback() );
        assertEquals( "resource1 has wrong ipatch callback", callback1, server.getResource( uri2 ).getIpatchCallback() );

        assertEquals( "resource3 has wrong get callback", callback3, server.getResource( uri3 ).getGetCallback() );
        assertEquals( "resource3 has wrong post callback", callback3, server.getResource( uri3 ).getPostCallback() );
        assertEquals( "resource3 has wrong put callback", callback2, server.getResource( uri3 ).getPutCallback() ); //note callback2
        assertEquals( "resource3 has wrong delete callback", callback3, server.getResource( uri3 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong fetch callback", callback3, server.getResource( uri3 ).getFetchCallback() );
        assertEquals( "resource1 has wrong patch callback", callback2, server.getResource( uri3 ).getPatchCallback() );
        assertEquals( "resource1 has wrong ipatch callback", callback3, server.getResource( uri3 ).getIpatchCallback() );

        assertEquals( "resource4 has wrong get callback", callback4, server.getResource( uri4 ).getGetCallback() );
        assertEquals( "resource4 has wrong post callback", callback4, server.getResource( uri4 ).getPostCallback() );
        assertEquals( "resource4 has wrong put callback", callback4, server.getResource( uri4 ).getPutCallback() );
        assertEquals( "resource4 has wrong delete callback", callback4, server.getResource( uri4 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong fetch callback", callback4, server.getResource( uri4 ).getFetchCallback() );
        assertEquals( "resource1 has wrong patch callback", callback4, server.getResource( uri4 ).getPatchCallback() );
        assertEquals( "resource1 has wrong ipatch callback", callback4, server.getResource( uri4 ).getIpatchCallback() );
    }

    @Test
    public void testCallBackWithWildcard3() throws InternalResourceUriException, InternalResourceRegistryException, InternalUriPatternException
    {
        OperationalServer server= new OperationalServer( config );
        TestSourceCallBack callback1= new TestSourceCallBack();
        TestSourceCallBack callback2= new TestSourceCallBack();
        TestSourceCallBack callback3= new TestSourceCallBack();
        TestSourceCallBack callback4= new TestSourceCallBack();
        ResourceConfig resourceConfig;
        String name1= "resource1";
        String uri1= "/resource1";
        String name2= "resource2";
        String uri2= "/resource1/resource2";
        String name3= "resource3";
        String uri3= "/resource1/resource2/resource3";
        String name4= "resource4";
        String uri4= "/resource1/resource4";
        RequestCodeFlags flags1= new RequestCodeFlags( true, true, true, true, true, true, true );
        RequestCodeFlags flags2= new RequestCodeFlags( false, false, true, false, false, true, false );
        RequestCodeFlags flags3= new RequestCodeFlags( true, true, false, true, true, false, true );
        RequestCodeFlags flags4= new RequestCodeFlags( true, true, true, true, true, true, true );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        server.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        server.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        server.add( uri1, resourceConfig );

        OperationalListener listener;
        listener= new OperationalListener( uri1, flags1, callback1 );
        server.add( listener );

        listener= new OperationalListener( "/resource1/*", flags2, callback2 );
        server.add( listener );

        listener= new OperationalListener( "/resource1/resource2/*", flags3, callback3 );
        server.add( listener );

        listener= new OperationalListener( uri4, flags4, callback4 );
        server.add( listener );

        assertEquals( "resource1 has wrong get callback", callback1, server.getResource( uri1 ).getGetCallback() );
        assertEquals( "resource1 has wrong post callback", callback1, server.getResource( uri1 ).getPostCallback() );
        assertEquals( "resource1 has wrong put callback", callback1, server.getResource( uri1 ).getPutCallback() );
        assertEquals( "resource1 has wrong delete callback", callback1, server.getResource( uri1 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong fetch callback", callback1, server.getResource( uri1 ).getFetchCallback() );
        assertEquals( "resource1 has wrong patch callback", callback1, server.getResource( uri1 ).getPatchCallback() );
        assertEquals( "resource1 has wrong ipatch callback", callback1, server.getResource( uri1 ).getIpatchCallback() );

        assertEquals( "resource2 has wrong get callback", null, server.getResource( uri2 ).getGetCallback() );
        assertEquals( "resource2 has wrong post callback", null, server.getResource( uri2 ).getPostCallback() );
        assertEquals( "resource2 has wrong put callback", callback2, server.getResource( uri2 ).getPutCallback() ); //note callback2
        assertEquals( "resource2 has wrong delete callback", null, server.getResource( uri2 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong fetch callback", null, server.getResource( uri2 ).getFetchCallback() );
        assertEquals( "resource1 has wrong patch callback", callback2, server.getResource( uri2 ).getPatchCallback() );
        assertEquals( "resource1 has wrong ipatch callback", null, server.getResource( uri2 ).getIpatchCallback() );

        assertEquals( "resource3 has wrong get callback", callback3, server.getResource( uri3 ).getGetCallback() );
        assertEquals( "resource3 has wrong post callback", callback3, server.getResource( uri3 ).getPostCallback() );
        assertEquals( "resource3 has wrong put callback", callback2, server.getResource( uri3 ).getPutCallback() ); //note callback2
        assertEquals( "resource3 has wrong delete callback", callback3, server.getResource( uri3 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong fetch callback", callback3, server.getResource( uri3 ).getFetchCallback() );
        assertEquals( "resource1 has wrong patch callback", callback2, server.getResource( uri3 ).getPatchCallback() );
        assertEquals( "resource1 has wrong ipatch callback", callback3, server.getResource( uri3 ).getIpatchCallback() );

        assertEquals( "resource4 has wrong get callback", callback4, server.getResource( uri4 ).getGetCallback() );
        assertEquals( "resource4 has wrong post callback", callback4, server.getResource( uri4 ).getPostCallback() );
        assertEquals( "resource4 has wrong put callback", callback4, server.getResource( uri4 ).getPutCallback() );
        assertEquals( "resource4 has wrong delete callback", callback4, server.getResource( uri4 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong fetch callback", callback4, server.getResource( uri4 ).getFetchCallback() );
        assertEquals( "resource1 has wrong patch callback", callback4, server.getResource( uri4 ).getPatchCallback() );
        assertEquals( "resource1 has wrong ipatch callback", callback4, server.getResource( uri4 ).getIpatchCallback() );

    }

    @Test
    public void testGetResourceNonexistent() throws InternalResourceUriException, InternalResourceRegistryException
    {
        OperationalServer server= new OperationalServer( config );
        ResourceConfig resourceConfig;
        String name1= "resource1";
        String uri1= "/resource1";
        String name2= "resource2";
        String uri2= "/resource1/resource2";
        String name3= "resource3";
        String uri4= "/resource1/resource4";

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        server.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        server.add( uri2, resourceConfig );

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            server.getResource( uri4 ).getURI();
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( uri4 ) );
        assertTrue( "exception has wrong message", e.getMessage().contains( "does not exist" ) );
    }

    /**
     * Test the getResource method
     * @throws InternalResourceUriException when uri is invalid
     * @throws InternalResourceRegistryException 
     */
    @Test
    public void testGetResource() throws InternalResourceUriException, InternalResourceRegistryException
    {
        OperationalServer server= new OperationalServer( config );
        ResourceConfig resourceConfig;
        String name1= "resource1";
        String uri1= "/resource1";
        String name2= "resource2";
        String uri2= "/resource1/resource2";
        String name3= "resource3";
        String uri3= "/resource1/resource2/resource3";
        String name4= "resource4";
        String uri4= "/resource1/resource4";

        assertNotNull( server );
        assertEquals( "server does not contain resource1", Defs.COAP_ROOTRESOURCE_NAME, server.getResource( Defs.COAP_ROOTRESOURCE ).getURI() );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );
        assertEquals( "server does not contain resource1", uri1, server.getResource( uri1 ).getURI() );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        resourceConfig.setNewSubResource( new NewSubResourceConfig( true, false ) );
        server.add( uri1, resourceConfig );
        assertEquals( "server does not contain resource2", uri2, server.getResource( uri2 ).getURI() );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        server.add( uri2, resourceConfig );
        assertEquals( "server does not contain resource3", uri3, server.getResource( uri3 ).getURI() );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        server.add( uri1, resourceConfig );
        assertEquals( "server does not contain resource4", uri4, server.getResource( uri4 ).getURI() );
    }

    /**
     * Test the findResource method
     * @throws InternalResourceUriException 
     * @throws InternalUriPatternException 
     */
    @Test
    public void testFindResources() throws InternalResourceUriException, InternalUriPatternException
    {
        OperationalServer server= new OperationalServer( config );
        ResourceConfig resourceConfig;
        String uri0= "/";
        String name1= "resource1";
        String uri1= "/resource1";
        String name2= "resource2";
        String uri2= "/resource1/resource2";
        String name3= "resource3";
        String uri3= "/resource1/resource2/resource3";
        String name4= "resource4";
        String uri4= "/resource1/resource4";

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        resourceConfig.setNewSubResource( new NewSubResourceConfig( true, false ) );
        server.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        server.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        server.add( uri1, resourceConfig );

        List< ServedResource > resources;

        resources= server.findResources( uri0, true );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 1, resources.size() );
        assertEquals( "wrong resources found", 0x1000, resourcesPresent( resources ) );

        resources= server.findResources( uri1, true );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 1, resources.size() );
        assertEquals( "wrong resources found", 0x1, resourcesPresent( resources ) );

        resources= server.findResources( uri2, true );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 1, resources.size() );
        assertEquals( "wrong resources found", 0x2, resourcesPresent( resources ) );

        resources= server.findResources( uri3, true );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 1, resources.size() );
        assertEquals( "wrong resources found", 0x4, resourcesPresent( resources ) );

        resources= server.findResources( uri4, true );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 1, resources.size() );
        assertEquals( "wrong resources found", 0x8, resourcesPresent( resources ) );

        resources= server.findResources( "*", true );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 5, resources.size() );
        assertEquals( "wrong resources found", 0x1000 | 0x1 | 0x2 | 0x4 | 0x8, resourcesPresent( resources ) );

        resources= server.findResources( "/*", true );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 4, resources.size() );
        assertEquals( "wrong resources found", 0x1 | 0x2 | 0x4 | 0x8, resourcesPresent( resources ) );

        resources= server.findResources( "/resource1/*", true );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 3, resources.size() );
        assertEquals( "wrong resources found", 0x2 | 0x4 | 0x8, resourcesPresent( resources ) );

        resources= server.findResources( "/resource1/resource2/*", true );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 1, resources.size() );
        assertEquals( "wrong resources found", 0x4, resourcesPresent( resources ) );

        resources= server.findResources( "/resource1/resource2/resource3/*", true );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 0, resources.size() );
        assertEquals( "wrong resources found", 0x0, resourcesPresent( resources ) );

        resources= server.findResources( "/resource1/resource4/*", true );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 0, resources.size() );
        assertEquals( "wrong resources found", 0x0, resourcesPresent( resources ) );
    }

    /**
     * Test the findResources method using invalid pathPattern.
     * @throws InternalResourceUriException 
     * @throws InternalUriPatternException 
     */
    @Test
    public void testFindResourcesError0() throws InternalResourceUriException, InternalUriPatternException
    {
        OperationalServer server= new OperationalServer( config );
        ResourceConfig resourceConfig;
        String name1= "resource1";
        String uri1= "/resource1";
        String name2= "resource2";
        String uri2= "/resource1/resource2";
        String name3= "resource3";
        String name4= "resource4";

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        resourceConfig.setNewSubResource( new NewSubResourceConfig( true, false ) );
        server.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        server.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        server.add( uri1, resourceConfig );

        InternalUriPatternException e= assertThrows( InternalUriPatternException.class, () -> {
            server.findResources( null, true );
        } );
        assertEquals( "Resource path pattern value is not allowed. { null }", e.getMessage() );
    }

    /**
     * Test the findResources method using invalid pathPattern.
     * @throws InternalResourceUriException 
     * @throws InternalUriPatternException 
     */
    @Test
    public void testFindResourcesError1() throws InternalResourceUriException, InternalUriPatternException
    {
        OperationalServer server= new OperationalServer( config );
        ResourceConfig resourceConfig;
        String uri0= "/";
        String name1= "resource1";
        String uri1= "/resource1";
        String name2= "resource2";
        String uri2= "/resource1/resource2";
        String name3= "resource3";
        String name4= "resource4";

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        resourceConfig.setNewSubResource( new NewSubResourceConfig( true, false ) );
        server.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        server.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        server.add( uri1, resourceConfig );

        InternalUriPatternException e= assertThrows( InternalUriPatternException.class, () -> {
            server.findResources( uri0, false );
        } );
        assertEquals( "Reference to root resource is not allowed. { / }", e.getMessage() );
    }

    /**
     * Test the findResources method using invalid pathPattern.
     * @throws InternalResourceUriException 
     * @throws InternalUriPatternException 
     */
    @Test
    public void testFindResourcesError2() throws InternalResourceUriException, InternalUriPatternException
    {
        OperationalServer server= new OperationalServer( config );
        ResourceConfig resourceConfig;
        String name1= "resource1";
        String uri1= "/resource1";
        String name2= "resource2";
        String uri2= "/resource1/resource2";
        String name3= "resource3";
        String name4= "resource4";

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        resourceConfig.setNewSubResource( new NewSubResourceConfig( true, false ) );
        server.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        server.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        server.add( uri1, resourceConfig );

        InternalUriPatternException e= assertThrows( InternalUriPatternException.class, () -> {
            server.findResources( String.valueOf( Defs.COAP_URI_WILDCARD ), false );
        } );
        assertEquals( "Including root resource is not allowed. { * }", e.getMessage() );
    }

    /**
     * Test the findResource method using config.
     * @throws InternalResourceUriException when uri is invalid
     * @throws InternalResourceRegistryException 
     */
    @Test
    public void testFindResource1() throws InternalResourceUriException, InternalResourceRegistryException
    {
        OperationalServer server= new OperationalServer( config );
        ResourceConfig resourceConfig;
        String uri0= "";
        ArrayList< String > list0= new ArrayList<>();
        String name1= "resource1";
        String uri1= "/resource1";
        ArrayList< String > list1= new ArrayList<>();
        list1.add( name1 );
        String name2= "resource2";
        String uri2= "/resource1/resource2";
        ArrayList< String > list2= new ArrayList<>( list1 );
        list2.add( name2 );
        String name3= "resource3";
        String uri3= "/resource1/resource2/resource3";
        ArrayList< String > list3= new ArrayList<>( list2 );
        list3.add( name3 );
        String name4= "resource4";
        String uri4= "/resource1/resource4";
        ArrayList< String > list4= new ArrayList<>( list1 );
        list4.add( name4 );

        String name5= "new1";
        String uri5= "/resource1/resource2/";
        ArrayList< String > list5= new ArrayList<>( list2 );
        list5.add( name5 );

        String name6= "new2";
        // /resource1/resource4/
        ArrayList< String > list6= new ArrayList<>( list4 );
        list6.add( name6 );

        // /resource1/resource2/new1/new2";
        String uri7= "/resource1/resource2/";
        ArrayList< String > list7= new ArrayList<>( list2 );
        list7.add( name5 );
        list7.add( name6 );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        server.add( Defs.COAP_ROOTRESOURCE, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        resourceConfig.setNewSubResource( new NewSubResourceConfig( true, false ) );
        server.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        server.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        server.add( uri1, resourceConfig );

        Resource resource;

        resource= server.findResource( list0, false );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri0, resource.getURI() );

        resource= server.findResource( list1, false );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri1, resource.getURI() );

        resource= server.findResource( list2, false );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri2, resource.getURI() );

        resource= server.findResource( list3, false );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri3, resource.getURI() );

        resource= server.findResource( list4, false );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri4, resource.getURI() );

        resource= server.findResource( list1, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri1, resource.getURI() );

        resource= server.findResource( list2, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri2, resource.getURI() );

        resource= server.findResource( list3, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri3, resource.getURI() );

        resource= server.findResource( list4, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri4, resource.getURI() );

        resource= server.findResource( list5, false );
        assertNull( resource );

        resource= server.findResource( list6, false );
        assertNull( resource );

        resource= server.findResource( list7, false );
        assertNull( resource );

        resource= server.findResource( list5, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri5, resource.getURI() );

        resource= server.findResource( list6, true );
        assertNull( resource );

        resource= server.findResource( list7, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri7, resource.getURI() );
    }

    /**
     * Test the findResource method using resourceParams.
     * @throws InternalResourceUriException when uri is invalid
     * @throws InternalResourceRegistryException 
     */
    @Test
    public void testFindResource2() throws InternalResourceUriException, InternalResourceRegistryException
    {
        OperationalServer server= new OperationalServer( config );
        ResourceParams resourceParams;
        ArrayList< String > list0= new ArrayList<>();
        String name1= "resource1";
        String uri1= "/resource1";
        ArrayList< String > list1= new ArrayList<>();
        list1.add( name1 );
        String name2= "resource2";
        String uri2= "/resource1/resource2";
        ArrayList< String > list2= new ArrayList<>( list1 );
        list2.add( name2 );
        String name3= "resource3";
        String uri3= "/resource1/resource2/resource3";
        ArrayList< String > list3= new ArrayList<>( list2 );
        list3.add( name3 );
        String name4= "resource4";
        String uri4= "/resource1/resource4";
        ArrayList< String > list4= new ArrayList<>( list1 );
        list4.add( name4 );

        String name10= "new10";
        String uri10= "/";
        ArrayList< String > list10= new ArrayList<>( list0 );
        list10.add( name10 );

        String name5= "new1";
        String uri5= "/resource1/resource2/";
        ArrayList< String > list5= new ArrayList<>( list2 );
        list5.add( name5 );

        String name6= "new2";
        // /resource1/resource4/
        ArrayList< String > list6= new ArrayList<>( list4 );
        list6.add( name6 );

        // /resource1/resource2/new1/new2";
        String uri7= "/resource1/resource2/";
        ArrayList< String > list7= new ArrayList<>( list2 );
        list7.add( name5 );
        list7.add( name6 );

        resourceParams= new ResourceParams();
        resourceParams.setNewSubResource( new NewSubResourceParams( true, false ) );
        server.getResource( Defs.COAP_ROOTRESOURCE ).configure( resourceParams );
        server.getResource( Defs.COAP_ROOTRESOURCE ).buildVirtualSubResource( resourceParams ).ifPresent( server::add );;

        resourceParams= new ResourceParams();
        resourceParams.setResourcePath( uri1 );
        server.add( resourceParams );

        resourceParams= new ResourceParams();
        resourceParams.setResourcePath( uri2 );
        resourceParams.setNewSubResource( new NewSubResourceParams( true, false ) );
        server.add( resourceParams );

        resourceParams= new ResourceParams();
        resourceParams.setResourcePath( uri3 );
        server.add( resourceParams );

        resourceParams= new ResourceParams();
        resourceParams.setResourcePath( uri4 );
        server.add( resourceParams );

        Resource resource;

        resource= server.findResource( list1, false );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri1, resource.getURI() );

        resource= server.findResource( list2, false );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri2, resource.getURI() );

        resource= server.findResource( list3, false );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri3, resource.getURI() );

        resource= server.findResource( list4, false );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri4, resource.getURI() );

        resource= server.findResource( list1, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri1, resource.getURI() );

        resource= server.findResource( list2, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri2, resource.getURI() );

        resource= server.findResource( list3, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri3, resource.getURI() );

        resource= server.findResource( list4, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri4, resource.getURI() );

        resource= server.findResource( list10, false );
        assertNull( resource );

        resource= server.findResource( list5, false );
        assertNull( resource );

        resource= server.findResource( list6, false );
        assertNull( resource );

        resource= server.findResource( list7, false );
        assertNull( resource );

        resource= server.findResource( list10, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri10, resource.getURI() );

        resource= server.findResource( list5, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri5, resource.getURI() );

        resource= server.findResource( list6, true );
        assertNull( resource );

        resource= server.findResource( list7, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri7, resource.getURI() );
    }

    /**
     * Check the existence of resources.
     * @param resources the list of resources to check
     * @return bitflags of the resources that are present in the list
     */
    int resourcesPresent( List< ServedResource > resources )
    {
        final String name0= "";
        final String name1= "resource1";
        final String name2= "resource2";
        final String name3= "resource3";
        final String name4= "resource4";
        final String name5= "resource5";
        final String name6= "resource6";
        final String name7= "resource7";
        int resourceFlags= 0;

        for ( ServedResource resource : resources )
        {
            switch ( resource.getName() )
            {
                case name0:
                    resourceFlags|= 0x1000;
                    break;
                case name1:
                    resourceFlags|= 0x1;
                    break;
                case name2:
                    resourceFlags|= 0x2;
                    break;
                case name3:
                    resourceFlags|= 0x4;
                    break;
                case name4:
                    resourceFlags|= 0x8;
                    break;
                case name5:
                    resourceFlags|= 0x10;
                    break;
                case name6:
                    resourceFlags|= 0x20;
                    break;
                case name7:
                    resourceFlags|= 0x40;
                    break;
            }
        }
        return resourceFlags;
    }
}
