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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.Resource;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.api.NewSubResourceConfig;
import nl.teslanet.mule.connectors.coap.api.NewSubResourceParams;
import nl.teslanet.mule.connectors.coap.api.ResourceConfig;
import nl.teslanet.mule.connectors.coap.api.ResourceParams;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResourceRegistryException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResourceUriException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUriPatternException;
import nl.teslanet.mule.connectors.coap.internal.server.OperationalListener;
import nl.teslanet.mule.connectors.coap.internal.server.RequestCodeFlags;
import nl.teslanet.mule.connectors.coap.internal.server.ResourceRegistry;
import nl.teslanet.mule.connectors.coap.internal.server.ServedResource;


/**
 * Test resource registry.
 */
public class ResourceRegistryTest
{
    @Test
    public void testConstructor() throws InternalResourceRegistryException, InternalResourceUriException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry;

        registry= new ResourceRegistry( root );
        assertNotNull( registry );
        assertEquals( "register should not expose root resource", null, registry.getResource( "" ) );
    }

    @Test
    public void testConstructorWithoutRootResource()
    {
        InternalResourceRegistryException e= assertThrows( InternalResourceRegistryException.class, () -> {
            new ResourceRegistry( null );
        } );
        assertTrue(
            "exception has wrong message",
            e.getMessage().contains( "Cannot construct a ResourceRegistry without root resource" )
        );
    }

    @Test
    public void testAddResourceWithoutName() throws InternalResourceUriException, InternalResourceRegistryException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
        ResourceConfig resourceConfig= new ResourceConfig();

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            registry.add( null, resourceConfig );
        } );
        assertEquals( "exception has wrong message", "name must not be null!", e.getCause().getMessage() );
    }

    @Test
    public void testAddResourceWithSlash() throws InternalResourceUriException, InternalResourceRegistryException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
        ResourceConfig resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( "x/y" );

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            registry.add( null, resourceConfig );
        } );
        assertEquals(
            "exception has wrong message",
            "'/' in 'x/y' is not supported by the implementation!",
            e.getCause().getMessage()
        );
    }

    @Test
    public void testAddResourceConfig() throws InternalResourceUriException, InternalResourceRegistryException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
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
        registry.add( null, resourceConfig );
        assertNotNull( registry );
        assertEquals( "registry does not contain resource1", uri1, registry.getResource( uri1 ).getURI() );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        registry.add( uri1, resourceConfig );
        assertNotNull( registry );
        assertEquals( "registry does not contain resource2", uri2, registry.getResource( uri2 ).getURI() );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        registry.add( uri2, resourceConfig );
        assertNotNull( registry );
        assertEquals( "registry does not contain resource3", uri3, registry.getResource( uri3 ).getURI() );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        registry.add( uri1, resourceConfig );
        assertNotNull( registry );
        assertEquals( "registry does not contain resource4", uri4, registry.getResource( uri4 ).getURI() );
    }

    @Test
    public void testAddResourceParams() throws InternalResourceUriException, InternalResourceRegistryException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
        ResourceParams resourceParams;
        String uri1= "/resource1";
        String uri2= "/resource1/resource2";
        String uri3= "/resource1/resource2/resource3";
        String uri4= "/resource1/resource4";

        resourceParams= new ResourceParams();
        resourceParams.setResourcePath( uri1 );
        registry.add( resourceParams );
        assertNotNull( registry );
        assertEquals( "registry does not contain resource1", uri1, registry.getResource( uri1 ).getURI() );

        resourceParams= new ResourceParams();
        resourceParams.setResourcePath( uri2 );
        registry.add( resourceParams );
        assertNotNull( registry );
        assertEquals( "registry does not contain resource2", uri2, registry.getResource( uri2 ).getURI() );

        resourceParams= new ResourceParams();
        resourceParams.setResourcePath( uri3 );
        registry.add( resourceParams );
        assertNotNull( registry );
        assertEquals( "registry does not contain resource3", uri3, registry.getResource( uri3 ).getURI() );

        resourceParams= new ResourceParams();
        resourceParams.setResourcePath( uri4 );
        registry.add( resourceParams );
        assertNotNull( registry );
        assertEquals( "registry does not contain resource4", uri4, registry.getResource( uri4 ).getURI() );
    }

    @Test
    public void testRemoveResource1() throws InternalResourceUriException, InternalResourceRegistryException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
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
        registry.add( null, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        registry.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        registry.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        registry.add( uri1, resourceConfig );

        assertEquals( "registry does not contain resource1", uri1, registry.getResource( uri1 ).getURI() );
        assertEquals( "registry does not contain resource2", uri2, registry.getResource( uri2 ).getURI() );
        assertEquals( "registry does not contain resource3", uri3, registry.getResource( uri3 ).getURI() );
        assertEquals( "registry does not contain resource4", uri4, registry.getResource( uri4 ).getURI() );

        registry.remove( uri4 );

        assertEquals( "registry does not contain resource1", uri1, registry.getResource( uri1 ).getURI() );
        assertEquals( "registry does not contain resource2", uri2, registry.getResource( uri2 ).getURI() );
        assertEquals( "registry does not contain resource3", uri3, registry.getResource( uri3 ).getURI() );
        assertTrue( "registry must not contain resource3", registry.findResources( uri4 ).isEmpty() );
    }

    @Test
    public void testRemoveResource2() throws InternalResourceUriException, InternalResourceRegistryException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
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
        registry.add( null, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        registry.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        registry.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        registry.add( uri1, resourceConfig );

        assertEquals( "registry does not contain resource1", uri1, registry.getResource( uri1 ).getURI() );
        assertEquals( "registry does not contain resource2", uri2, registry.getResource( uri2 ).getURI() );
        assertEquals( "registry does not contain resource3", uri3, registry.getResource( uri3 ).getURI() );
        assertEquals( "registry does not contain resource4", uri4, registry.getResource( uri4 ).getURI() );

        registry.remove( uri2 );

        assertEquals( "registry does not contain resource1", uri1, registry.getResource( uri1 ).getURI() );
        assertTrue( "registry must not contain resource2", registry.findResources( uri2 ).isEmpty() );
        assertTrue( "registry must not contain resource3", registry.findResources( uri3 ).isEmpty() );
        assertEquals( "registry does not contain resource4", uri4, registry.getResource( uri4 ).getURI() );
    }

    @Test
    public void testAddOperationalListener() throws InternalResourceUriException,
        InternalResourceRegistryException,
        InternalUriPatternException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
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
            registry.add( new OperationalListener( uris.get( i ), flags.get( i ), callback ) );
        }
        //assert
        assertNotNull( "assert failed", registry );
    }

    @Test
    public void testCallBack() throws InternalResourceUriException,
        InternalResourceRegistryException,
        InternalUriPatternException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
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
        RequestCodeFlags flags2= new RequestCodeFlags( true, true, true, true, true, true, true );
        RequestCodeFlags flags3= new RequestCodeFlags( true, true, true, true, true, true, true );
        RequestCodeFlags flags4= new RequestCodeFlags( true, true, true, true, true, true, true );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        registry.add( null, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        registry.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        registry.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        registry.add( uri1, resourceConfig );

        OperationalListener listener;
        listener= new OperationalListener( uri1, flags1, callback1 );
        registry.add( listener );

        listener= new OperationalListener( uri2, flags2, callback2 );
        registry.add( listener );

        listener= new OperationalListener( uri3, flags3, callback3 );
        registry.add( listener );

        listener= new OperationalListener( uri4, flags4, callback4 );
        registry.add( listener );

        assertEquals( "resource1 has wrong callback", callback1, registry.getResource( uri1 ).getGetCallback() );
        assertEquals( "resource1 has wrong callback", callback1, registry.getResource( uri1 ).getPostCallback() );
        assertEquals( "resource1 has wrong callback", callback1, registry.getResource( uri1 ).getPutCallback() );
        assertEquals( "resource1 has wrong callback", callback1, registry.getResource( uri1 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong callback", callback1, registry.getResource( uri1 ).getFetchCallback() );
        assertEquals( "resource1 has wrong callback", callback1, registry.getResource( uri1 ).getPatchCallback() );
        assertEquals( "resource1 has wrong callback", callback1, registry.getResource( uri1 ).getIpatchCallback() );

        assertEquals( "resource2 has wrong callback", callback2, registry.getResource( uri2 ).getGetCallback() );
        assertEquals( "resource2 has wrong callback", callback2, registry.getResource( uri2 ).getPostCallback() );
        assertEquals( "resource2 has wrong callback", callback2, registry.getResource( uri2 ).getPutCallback() );
        assertEquals( "resource2 has wrong callback", callback2, registry.getResource( uri2 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong callback", callback2, registry.getResource( uri2 ).getFetchCallback() );
        assertEquals( "resource1 has wrong callback", callback2, registry.getResource( uri2 ).getPatchCallback() );
        assertEquals( "resource1 has wrong callback", callback2, registry.getResource( uri2 ).getIpatchCallback() );

        assertEquals( "resource3 has wrong callback", callback3, registry.getResource( uri3 ).getGetCallback() );
        assertEquals( "resource3 has wrong callback", callback3, registry.getResource( uri3 ).getPostCallback() );
        assertEquals( "resource3 has wrong callback", callback3, registry.getResource( uri3 ).getPutCallback() );
        assertEquals( "resource3 has wrong callback", callback3, registry.getResource( uri3 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong callback", callback3, registry.getResource( uri3 ).getFetchCallback() );
        assertEquals( "resource1 has wrong callback", callback3, registry.getResource( uri3 ).getPatchCallback() );
        assertEquals( "resource1 has wrong callback", callback3, registry.getResource( uri3 ).getIpatchCallback() );

        assertEquals( "resource4 has wrong callback", callback4, registry.getResource( uri4 ).getGetCallback() );
        assertEquals( "resource4 has wrong callback", callback4, registry.getResource( uri4 ).getPostCallback() );
        assertEquals( "resource4 has wrong callback", callback4, registry.getResource( uri4 ).getPutCallback() );
        assertEquals( "resource4 has wrong callback", callback4, registry.getResource( uri4 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong callback", callback4, registry.getResource( uri4 ).getFetchCallback() );
        assertEquals( "resource1 has wrong callback", callback4, registry.getResource( uri4 ).getPatchCallback() );
        assertEquals( "resource1 has wrong callback", callback4, registry.getResource( uri4 ).getIpatchCallback() );
    }

    @Test
    public void testCallBackWithRequestCodeFlags() throws InternalResourceUriException,
        InternalResourceRegistryException,
        InternalUriPatternException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
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
        registry.add( null, resourceConfig );

        OperationalListener listener;
        listener= new OperationalListener( uri1, flags1, callback1 );
        registry.add( listener );

        listener= new OperationalListener( uri1, flags2, callback2 );
        registry.add( listener );

        listener= new OperationalListener( uri1, flags3, callback3 );
        registry.add( listener );

        listener= new OperationalListener( uri1, flags4, callback4 );
        registry.add( listener );

        listener= new OperationalListener( uri1, flags5, callback5 );
        registry.add( listener );

        listener= new OperationalListener( uri1, flags6, callback6 );
        registry.add( listener );

        listener= new OperationalListener( uri1, flags7, callback7 );
        registry.add( listener );

        assertEquals( "resource1 has wrong callback", callback1, registry.getResource( uri1 ).getGetCallback() );
        assertEquals( "resource1 has wrong callback", callback2, registry.getResource( uri1 ).getPostCallback() );
        assertEquals( "resource1 has wrong callback", callback3, registry.getResource( uri1 ).getPutCallback() );
        assertEquals( "resource1 has wrong callback", callback4, registry.getResource( uri1 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong callback", callback5, registry.getResource( uri1 ).getFetchCallback() );
        assertEquals( "resource1 has wrong callback", callback6, registry.getResource( uri1 ).getPatchCallback() );
        assertEquals( "resource1 has wrong callback", callback7, registry.getResource( uri1 ).getIpatchCallback() );
    }

    @Test
    public void testCallBackWithWildcard1() throws InternalResourceUriException,
        InternalResourceRegistryException,
        InternalUriPatternException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
        TestSourceCallBack callback1= new TestSourceCallBack();
        TestSourceCallBack callback2= new TestSourceCallBack();
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
        RequestCodeFlags flags2= new RequestCodeFlags( true, true, true, true, true, true, true );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        registry.add( null, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        registry.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        registry.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        registry.add( uri1, resourceConfig );

        OperationalListener listener;
        listener= new OperationalListener( "/*", flags1, callback1 );
        registry.add( listener );

        listener= new OperationalListener( uri2, flags2, callback2 );
        registry.add( listener );

        assertEquals( "resource1 has wrong get callback", callback1, registry.getResource( uri1 ).getGetCallback() );
        assertEquals( "resource1 has wrong post callback", callback1, registry.getResource( uri1 ).getPostCallback() );
        assertEquals( "resource1 has wrong put callback", callback1, registry.getResource( uri1 ).getPutCallback() );
        assertEquals(
            "resource1 has wrong delete callback",
            callback1,
            registry.getResource( uri1 ).getDeleteCallback()
        );
        assertEquals(
            "resource1 has wrong fetch callback",
            callback1,
            registry.getResource( uri1 ).getFetchCallback()
        );
        assertEquals(
            "resource1 has wrong patch callback",
            callback1,
            registry.getResource( uri1 ).getPatchCallback()
        );
        assertEquals(
            "resource1 has wrong ipatch callback",
            callback1,
            registry.getResource( uri1 ).getIpatchCallback()
        );

        assertEquals( "resource2 has wrong get callback", callback2, registry.getResource( uri2 ).getGetCallback() );
        assertEquals( "resource2 has wrong post callback", callback2, registry.getResource( uri2 ).getPostCallback() );
        assertEquals( "resource2 has wrong put callback", callback2, registry.getResource( uri2 ).getPutCallback() );
        assertEquals(
            "resource2 has wrong delete callback",
            callback2,
            registry.getResource( uri2 ).getDeleteCallback()
        );
        assertEquals(
            "resource1 has wrong fetch callback",
            callback2,
            registry.getResource( uri2 ).getFetchCallback()
        );
        assertEquals(
            "resource1 has wrong patch callback",
            callback2,
            registry.getResource( uri2 ).getPatchCallback()
        );
        assertEquals(
            "resource1 has wrong ipatch callback",
            callback2,
            registry.getResource( uri2 ).getIpatchCallback()
        );

        assertEquals( "resource3 has wrong get callback", callback1, registry.getResource( uri3 ).getGetCallback() );
        assertEquals( "resource3 has wrong post callback", callback1, registry.getResource( uri3 ).getPostCallback() );
        assertEquals( "resource3 has wrong put callback", callback1, registry.getResource( uri3 ).getPutCallback() );
        assertEquals(
            "resource3 has wrong delete callback",
            callback1,
            registry.getResource( uri3 ).getDeleteCallback()
        );
        assertEquals(
            "resource1 has wrong fetch callback",
            callback1,
            registry.getResource( uri3 ).getFetchCallback()
        );
        assertEquals(
            "resource1 has wrong patch callback",
            callback1,
            registry.getResource( uri3 ).getPatchCallback()
        );
        assertEquals(
            "resource1 has wrong ipatch callback",
            callback1,
            registry.getResource( uri3 ).getIpatchCallback()
        );

        assertEquals( "resource4 has wrong get callback", callback1, registry.getResource( uri4 ).getGetCallback() );
        assertEquals( "resource4 has wrong post callback", callback1, registry.getResource( uri4 ).getPostCallback() );
        assertEquals( "resource4 has wrong put callback", callback1, registry.getResource( uri4 ).getPutCallback() );
        assertEquals(
            "resource4 has wrong delete callback",
            callback1,
            registry.getResource( uri4 ).getDeleteCallback()
        );
        assertEquals(
            "resource1 has wrong fetch callback",
            callback1,
            registry.getResource( uri4 ).getFetchCallback()
        );
        assertEquals(
            "resource1 has wrong patch callback",
            callback1,
            registry.getResource( uri4 ).getPatchCallback()
        );
        assertEquals(
            "resource1 has wrong ipatch callback",
            callback1,
            registry.getResource( uri4 ).getIpatchCallback()
        );
    }

    @Test
    public void testCallBackWithWildcardAndRequestCodeFlags1() throws InternalResourceUriException,
        InternalResourceRegistryException,
        InternalUriPatternException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
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
        registry.add( null, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        registry.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        registry.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        registry.add( uri1, resourceConfig );

        OperationalListener listener;
        listener= new OperationalListener( "/*", flags1, callback1 );
        registry.add( listener );
        listener= new OperationalListener( uri2, flags2, callback2 );
        registry.add( listener );
        listener= new OperationalListener( uri2, flags3, callback3 );
        registry.add( listener );
        listener= new OperationalListener( uri2, flags4, callback4 );
        registry.add( listener );
        listener= new OperationalListener( uri2, flags5, callback5 );
        registry.add( listener );
        listener= new OperationalListener( uri2, flags6, callback6 );
        registry.add( listener );
        listener= new OperationalListener( uri2, flags7, callback7 );
        registry.add( listener );
        listener= new OperationalListener( uri2, flags8, callback8 );
        registry.add( listener );

        assertEquals( "resource1 has wrong get callback", callback1, registry.getResource( uri1 ).getGetCallback() );
        assertEquals( "resource1 has wrong post callback", callback1, registry.getResource( uri1 ).getPostCallback() );
        assertEquals( "resource1 has wrong put callback", callback1, registry.getResource( uri1 ).getPutCallback() );
        assertEquals(
            "resource1 has wrong delete callback",
            callback1,
            registry.getResource( uri1 ).getDeleteCallback()
        );
        assertEquals(
            "resource1 has wrong fetch callback",
            callback1,
            registry.getResource( uri1 ).getFetchCallback()
        );
        assertEquals(
            "resource1 has wrong patch callback",
            callback1,
            registry.getResource( uri1 ).getPatchCallback()
        );
        assertEquals(
            "resource1 has wrong ipatch callback",
            callback1,
            registry.getResource( uri1 ).getIpatchCallback()
        );

        assertEquals( "resource2 has wrong get callback", callback2, registry.getResource( uri2 ).getGetCallback() );
        assertEquals( "resource2 has wrong post callback", callback3, registry.getResource( uri2 ).getPostCallback() );
        assertEquals( "resource2 has wrong put callback", callback4, registry.getResource( uri2 ).getPutCallback() );
        assertEquals(
            "resource2 has wrong delete callback",
            callback5,
            registry.getResource( uri2 ).getDeleteCallback()
        );
        assertEquals(
            "resource1 has wrong fetch callback",
            callback6,
            registry.getResource( uri2 ).getFetchCallback()
        );
        assertEquals(
            "resource1 has wrong patch callback",
            callback7,
            registry.getResource( uri2 ).getPatchCallback()
        );
        assertEquals(
            "resource1 has wrong ipatch callback",
            callback8,
            registry.getResource( uri2 ).getIpatchCallback()
        );

        assertEquals( "resource3 has wrong get callback", callback1, registry.getResource( uri3 ).getGetCallback() );
        assertEquals( "resource3 has wrong post callback", callback1, registry.getResource( uri3 ).getPostCallback() );
        assertEquals( "resource3 has wrong put callback", callback1, registry.getResource( uri3 ).getPutCallback() );
        assertEquals(
            "resource3 has wrong delete callback",
            callback1,
            registry.getResource( uri3 ).getDeleteCallback()
        );
        assertEquals(
            "resource1 has wrong fetch callback",
            callback1,
            registry.getResource( uri3 ).getFetchCallback()
        );
        assertEquals(
            "resource1 has wrong patch callback",
            callback1,
            registry.getResource( uri3 ).getPatchCallback()
        );
        assertEquals(
            "resource1 has wrong ipatch callback",
            callback1,
            registry.getResource( uri3 ).getIpatchCallback()
        );

        assertEquals( "resource4 has wrong get callback", callback1, registry.getResource( uri4 ).getGetCallback() );
        assertEquals( "resource4 has wrong post callback", callback1, registry.getResource( uri4 ).getPostCallback() );
        assertEquals( "resource4 has wrong put callback", callback1, registry.getResource( uri4 ).getPutCallback() );
        assertEquals(
            "resource4 has wrong delete callback",
            callback1,
            registry.getResource( uri4 ).getDeleteCallback()
        );
        assertEquals(
            "resource4 has wrong fetch callback",
            callback1,
            registry.getResource( uri4 ).getFetchCallback()
        );
        assertEquals(
            "resource4 has wrong patch callback",
            callback1,
            registry.getResource( uri4 ).getPatchCallback()
        );
        assertEquals(
            "resource4 has wrong ipatch callback",
            callback1,
            registry.getResource( uri4 ).getIpatchCallback()
        );
    }

    @Test
    public void testCallBackWithWildcard2() throws InternalResourceUriException,
        InternalResourceRegistryException,
        InternalUriPatternException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
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
        registry.add( null, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        registry.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        registry.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        registry.add( uri1, resourceConfig );

        OperationalListener listener;
        listener= new OperationalListener( "/*", flags1, callback1 );
        registry.add( listener );

        listener= new OperationalListener( "/resource1/resource2/*", flags2, callback2 );
        registry.add( listener );

        listener= new OperationalListener( "/resource1/resource2/*", flags3, callback3 );
        registry.add( listener );

        listener= new OperationalListener( uri4, flags4, callback4 );
        registry.add( listener );

        assertEquals( "resource1 has wrong get callback", callback1, registry.getResource( uri1 ).getGetCallback() );
        assertEquals( "resource1 has wrong post callback", callback1, registry.getResource( uri1 ).getPostCallback() );
        assertEquals( "resource1 has wrong put callback", callback1, registry.getResource( uri1 ).getPutCallback() );
        assertEquals(
            "resource1 has wrong delete callback",
            callback1,
            registry.getResource( uri1 ).getDeleteCallback()
        );
        assertEquals(
            "resource1 has wrong fetch callback",
            callback1,
            registry.getResource( uri1 ).getFetchCallback()
        );
        assertEquals(
            "resource1 has wrong patch callback",
            callback1,
            registry.getResource( uri1 ).getPatchCallback()
        );
        assertEquals(
            "resource1 has wrong ipatch callback",
            callback1,
            registry.getResource( uri1 ).getIpatchCallback()
        );

        assertEquals( "resource2 has wrong get callback", callback1, registry.getResource( uri2 ).getGetCallback() );
        assertEquals( "resource2 has wrong post callback", callback1, registry.getResource( uri2 ).getPostCallback() );
        assertEquals( "resource2 has wrong put callback", callback1, registry.getResource( uri2 ).getPutCallback() );
        assertEquals(
            "resource2 has wrong delete callback",
            callback1,
            registry.getResource( uri2 ).getDeleteCallback()
        );
        assertEquals(
            "resource1 has wrong fetch callback",
            callback1,
            registry.getResource( uri2 ).getFetchCallback()
        );
        assertEquals(
            "resource1 has wrong patch callback",
            callback1,
            registry.getResource( uri2 ).getPatchCallback()
        );
        assertEquals(
            "resource1 has wrong ipatch callback",
            callback1,
            registry.getResource( uri2 ).getIpatchCallback()
        );

        assertEquals( "resource3 has wrong get callback", callback3, registry.getResource( uri3 ).getGetCallback() );
        assertEquals( "resource3 has wrong post callback", callback3, registry.getResource( uri3 ).getPostCallback() );
        assertEquals( "resource3 has wrong put callback", callback2, registry.getResource( uri3 ).getPutCallback() ); //note callback2
        assertEquals(
            "resource3 has wrong delete callback",
            callback3,
            registry.getResource( uri3 ).getDeleteCallback()
        );
        assertEquals(
            "resource1 has wrong fetch callback",
            callback3,
            registry.getResource( uri3 ).getFetchCallback()
        );
        assertEquals(
            "resource1 has wrong patch callback",
            callback2,
            registry.getResource( uri3 ).getPatchCallback()
        );
        assertEquals(
            "resource1 has wrong ipatch callback",
            callback3,
            registry.getResource( uri3 ).getIpatchCallback()
        );

        assertEquals( "resource4 has wrong get callback", callback4, registry.getResource( uri4 ).getGetCallback() );
        assertEquals( "resource4 has wrong post callback", callback4, registry.getResource( uri4 ).getPostCallback() );
        assertEquals( "resource4 has wrong put callback", callback4, registry.getResource( uri4 ).getPutCallback() );
        assertEquals(
            "resource4 has wrong delete callback",
            callback4,
            registry.getResource( uri4 ).getDeleteCallback()
        );
        assertEquals(
            "resource1 has wrong fetch callback",
            callback4,
            registry.getResource( uri4 ).getFetchCallback()
        );
        assertEquals(
            "resource1 has wrong patch callback",
            callback4,
            registry.getResource( uri4 ).getPatchCallback()
        );
        assertEquals(
            "resource1 has wrong ipatch callback",
            callback4,
            registry.getResource( uri4 ).getIpatchCallback()
        );
    }

    @Test
    public void testCallBackWithWildcard3() throws InternalResourceUriException,
        InternalResourceRegistryException,
        InternalUriPatternException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
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
        registry.add( null, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        registry.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        registry.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        registry.add( uri1, resourceConfig );

        OperationalListener listener;
        listener= new OperationalListener( uri1, flags1, callback1 );
        registry.add( listener );

        listener= new OperationalListener( "/resource1/*", flags2, callback2 );
        registry.add( listener );

        listener= new OperationalListener( "/resource1/resource2/*", flags3, callback3 );
        registry.add( listener );

        listener= new OperationalListener( uri4, flags4, callback4 );
        registry.add( listener );

        assertEquals( "resource1 has wrong get callback", callback1, registry.getResource( uri1 ).getGetCallback() );
        assertEquals( "resource1 has wrong post callback", callback1, registry.getResource( uri1 ).getPostCallback() );
        assertEquals( "resource1 has wrong put callback", callback1, registry.getResource( uri1 ).getPutCallback() );
        assertEquals(
            "resource1 has wrong delete callback",
            callback1,
            registry.getResource( uri1 ).getDeleteCallback()
        );
        assertEquals(
            "resource1 has wrong fetch callback",
            callback1,
            registry.getResource( uri1 ).getFetchCallback()
        );
        assertEquals(
            "resource1 has wrong patch callback",
            callback1,
            registry.getResource( uri1 ).getPatchCallback()
        );
        assertEquals(
            "resource1 has wrong ipatch callback",
            callback1,
            registry.getResource( uri1 ).getIpatchCallback()
        );

        assertEquals( "resource2 has wrong get callback", null, registry.getResource( uri2 ).getGetCallback() );
        assertEquals( "resource2 has wrong post callback", null, registry.getResource( uri2 ).getPostCallback() );
        assertEquals( "resource2 has wrong put callback", callback2, registry.getResource( uri2 ).getPutCallback() ); //note callback2
        assertEquals( "resource2 has wrong delete callback", null, registry.getResource( uri2 ).getDeleteCallback() );
        assertEquals( "resource1 has wrong fetch callback", null, registry.getResource( uri2 ).getFetchCallback() );
        assertEquals(
            "resource1 has wrong patch callback",
            callback2,
            registry.getResource( uri2 ).getPatchCallback()
        );
        assertEquals( "resource1 has wrong ipatch callback", null, registry.getResource( uri2 ).getIpatchCallback() );

        assertEquals( "resource3 has wrong get callback", callback3, registry.getResource( uri3 ).getGetCallback() );
        assertEquals( "resource3 has wrong post callback", callback3, registry.getResource( uri3 ).getPostCallback() );
        assertEquals( "resource3 has wrong put callback", callback2, registry.getResource( uri3 ).getPutCallback() ); //note callback2
        assertEquals(
            "resource3 has wrong delete callback",
            callback3,
            registry.getResource( uri3 ).getDeleteCallback()
        );
        assertEquals(
            "resource1 has wrong fetch callback",
            callback3,
            registry.getResource( uri3 ).getFetchCallback()
        );
        assertEquals(
            "resource1 has wrong patch callback",
            callback2,
            registry.getResource( uri3 ).getPatchCallback()
        );
        assertEquals(
            "resource1 has wrong ipatch callback",
            callback3,
            registry.getResource( uri3 ).getIpatchCallback()
        );

        assertEquals( "resource4 has wrong get callback", callback4, registry.getResource( uri4 ).getGetCallback() );
        assertEquals( "resource4 has wrong post callback", callback4, registry.getResource( uri4 ).getPostCallback() );
        assertEquals( "resource4 has wrong put callback", callback4, registry.getResource( uri4 ).getPutCallback() );
        assertEquals(
            "resource4 has wrong delete callback",
            callback4,
            registry.getResource( uri4 ).getDeleteCallback()
        );
        assertEquals(
            "resource1 has wrong fetch callback",
            callback4,
            registry.getResource( uri4 ).getFetchCallback()
        );
        assertEquals(
            "resource1 has wrong patch callback",
            callback4,
            registry.getResource( uri4 ).getPatchCallback()
        );
        assertEquals(
            "resource1 has wrong ipatch callback",
            callback4,
            registry.getResource( uri4 ).getIpatchCallback()
        );

    }

    @Test
    public void testGetResourceNonexistent() throws InternalResourceUriException, InternalResourceRegistryException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
        ResourceConfig resourceConfig;
        String name1= "resource1";
        String uri1= "/resource1";
        String name2= "resource2";
        String uri2= "/resource1/resource2";
        String name3= "resource3";
        String uri4= "/resource1/resource4";

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name1 );
        registry.add( null, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        registry.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        registry.add( uri2, resourceConfig );

        InternalResourceUriException e= assertThrows( InternalResourceUriException.class, () -> {
            registry.getResource( uri4 ).getURI();
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
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
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
        registry.add( null, resourceConfig );
        assertNotNull( registry );
        assertEquals( "registry does not contain resource1", uri1, registry.getResource( uri1 ).getURI() );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        resourceConfig.setNewSubResource( new NewSubResourceConfig( true, false ));
        registry.add( uri1, resourceConfig );
        assertNotNull( registry );
        assertEquals( "registry does not contain resource2", uri2, registry.getResource( uri2 ).getURI() );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        registry.add( uri2, resourceConfig );
        assertNotNull( registry );
        assertEquals( "registry does not contain resource3", uri3, registry.getResource( uri3 ).getURI() );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        registry.add( uri1, resourceConfig );
        assertNotNull( registry );
        assertEquals( "registry does not contain resource4", uri4, registry.getResource( uri4 ).getURI() );
    }

    /**
     * Test the findResource method
     * @throws InternalResourceUriException when uri is invalid
     * @throws InternalResourceRegistryException 
     */
    @Test
    public void testFindResources() throws InternalResourceUriException, InternalResourceRegistryException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
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
        registry.add( null, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        resourceConfig.setNewSubResource( new NewSubResourceConfig( true, false ));
        registry.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        registry.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        registry.add( uri1, resourceConfig );

        List< ServedResource > resources;

        resources= registry.findResources( uri1 );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 1, resources.size() );
        assertEquals( "wrong resources found", 0x1, resourcesPresent( resources ) );

        resources= registry.findResources( uri2 );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 1, resources.size() );
        assertEquals( "wrong resources found", 0x2, resourcesPresent( resources ) );

        resources= registry.findResources( uri3 );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 1, resources.size() );
        assertEquals( "wrong resources found", 0x4, resourcesPresent( resources ) );

        resources= registry.findResources( uri4 );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 1, resources.size() );
        assertEquals( "wrong resources found", 0x8, resourcesPresent( resources ) );

        resources= registry.findResources( "/*" );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 4, resources.size() );
        assertEquals( "wrong resources found", 0x1 | 0x2 | 0x4 | 0x8, resourcesPresent( resources ) );

        resources= registry.findResources( "/resource1/*" );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 3, resources.size() );
        assertEquals( "wrong resources found", 0x2 | 0x4 | 0x8, resourcesPresent( resources ) );

        resources= registry.findResources( "/resource1/resource2/*" );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 1, resources.size() );
        assertEquals( "wrong resources found", 0x4, resourcesPresent( resources ) );

        resources= registry.findResources( "/resource1/resource2/resource3/*" );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 0, resources.size() );
        assertEquals( "wrong resources found", 0x0, resourcesPresent( resources ) );

        resources= registry.findResources( "/resource1/resource4/*" );
        assertNotNull( resources );
        assertEquals( "wrong resource count", 0, resources.size() );
        assertEquals( "wrong resources found", 0x0, resourcesPresent( resources ) );
    }

    /**
     * Test the findResource method using config.
     * @throws InternalResourceUriException when uri is invalid
     * @throws InternalResourceRegistryException 
     */
    @Test
    public void testFindResource1() throws InternalResourceUriException, InternalResourceRegistryException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
        ResourceConfig resourceConfig;
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
        registry.add( null, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name2 );
        resourceConfig.setNewSubResource( new NewSubResourceConfig( true, false ));
        registry.add( uri1, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name3 );
        registry.add( uri2, resourceConfig );

        resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name4 );
        registry.add( uri1, resourceConfig );

        Resource resource;

        resource= registry.findResource( list1, false );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri1, resource.getURI() );

        resource= registry.findResource( list2, false );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri2, resource.getURI() );

        resource= registry.findResource( list3, false );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri3, resource.getURI() );

        resource= registry.findResource( list4, false );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri4, resource.getURI() );

        resource= registry.findResource( list1, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri1, resource.getURI() );

        resource= registry.findResource( list2, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri2, resource.getURI() );

        resource= registry.findResource( list3, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri3, resource.getURI() );

        resource= registry.findResource( list4, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri4, resource.getURI() );

        resource= registry.findResource( list5, false );
        assertNull( resource );

        resource= registry.findResource( list6, false );
        assertNull( resource );

        resource= registry.findResource( list7, false );
        assertNull( resource );

        resource= registry.findResource( list5, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri5, resource.getURI() );

        resource= registry.findResource( list6, true );
        assertNull( resource );

        resource= registry.findResource( list7, true );
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
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
        ResourceParams resourceParams;
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

        resourceParams= new ResourceParams();
        resourceParams.setResourcePath( uri1 );
        registry.add( resourceParams );

        resourceParams= new ResourceParams();
        resourceParams.setResourcePath( uri2 );
        resourceParams.setNewSubResource( new NewSubResourceParams( true, false ));
        registry.add( resourceParams );

        resourceParams= new ResourceParams();
        resourceParams.setResourcePath( uri3 );
        registry.add( resourceParams );

        resourceParams= new ResourceParams();
        resourceParams.setResourcePath( uri4 );
        registry.add( resourceParams );

        Resource resource;

        resource= registry.findResource( list1, false );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri1, resource.getURI() );

        resource= registry.findResource( list2, false );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri2, resource.getURI() );

        resource= registry.findResource( list3, false );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri3, resource.getURI() );

        resource= registry.findResource( list4, false );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri4, resource.getURI() );

        resource= registry.findResource( list1, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri1, resource.getURI() );

        resource= registry.findResource( list2, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri2, resource.getURI() );

        resource= registry.findResource( list3, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri3, resource.getURI() );

        resource= registry.findResource( list4, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri4, resource.getURI() );

        resource= registry.findResource( list5, false );
        assertNull( resource );

        resource= registry.findResource( list6, false );
        assertNull( resource );

        resource= registry.findResource( list7, false );
        assertNull( resource );

        resource= registry.findResource( list5, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri5, resource.getURI() );

        resource= registry.findResource( list6, true );
        assertNull( resource );

        resource= registry.findResource( list7, true );
        assertNotNull( resource );
        assertEquals( "wrong resource found", uri7, resource.getURI() );
    }

    /**
     * Check the existence of resources.
     * @param resources the list of resousources to check
     * @return bitflags of the resoures that are present in the list
     */
    int resourcesPresent( List< ServedResource > resources )
    {
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
