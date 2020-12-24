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
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.api.ResourceConfig;
import nl.teslanet.mule.connectors.coap.api.error.InvalidResourceUriException;
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
    public void testConstructor() throws InvalidResourceUriException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry;

        registry= new ResourceRegistry( root );
        assertNotNull( registry );
        assertEquals( "register should not expose root resource", null, registry.getResource( "" ) );
    }

    @Test
    public void testConstructorWithoutRootResource() throws InvalidResourceUriException
    {
        NullPointerException e= assertThrows( NullPointerException.class, () -> {
            new ResourceRegistry( null );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Cannot construct a ResourceRegistry without root resource" ) );
    }

    @Test
    public void testAddResourceWithoutName() throws InvalidResourceUriException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
        ResourceConfig resourceConfig= new ResourceConfig();

        NullPointerException e= assertThrows( NullPointerException.class, () -> {
            registry.add( null, resourceConfig );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "Child must have a name" ) );
    }

    @Test
    public void testAddResource() throws InvalidResourceUriException
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
    public void testRemoveResource1() throws InvalidResourceUriException
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
    public void testRemoveResource2() throws InvalidResourceUriException
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
    public void testAddOperationalListener() throws InvalidResourceUriException
    {
        TestSourceCallBack callback= new TestSourceCallBack();
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
        OperationalListener listener;
        String uri1= "/resource1";
        String uri2= "/resource1/resource2";
        String uri3= "/resource1/resource2/resource3";
        String uri4= "/resource1/resource4";
        RequestCodeFlags flags1= new RequestCodeFlags( false, false, false, false );
        RequestCodeFlags flags2= new RequestCodeFlags( true, false, false, false );
        RequestCodeFlags flags3= new RequestCodeFlags( true, true, false, false );
        RequestCodeFlags flags4= new RequestCodeFlags( true, true, true, false );

        //see that no exceptions occur
        listener= new OperationalListener( uri1, flags1, callback );
        registry.add( listener );

        listener= new OperationalListener( uri2, flags2, callback );
        registry.add( listener );

        listener= new OperationalListener( uri3, flags3, callback );
        registry.add( listener );

        listener= new OperationalListener( uri4, flags4, callback );
        registry.add( listener );
    }

    @Test
    public void testCallBack() throws InvalidResourceUriException
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
        RequestCodeFlags flags1= new RequestCodeFlags( true, true, true, true );
        RequestCodeFlags flags2= new RequestCodeFlags( true, true, true, true );
        RequestCodeFlags flags3= new RequestCodeFlags( true, true, true, true );
        RequestCodeFlags flags4= new RequestCodeFlags( true, true, true, true );

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

        assertEquals( "resource2 has wrong callback", callback2, registry.getResource( uri2 ).getGetCallback() );
        assertEquals( "resource2 has wrong callback", callback2, registry.getResource( uri2 ).getPostCallback() );
        assertEquals( "resource2 has wrong callback", callback2, registry.getResource( uri2 ).getPutCallback() );
        assertEquals( "resource2 has wrong callback", callback2, registry.getResource( uri2 ).getDeleteCallback() );

        assertEquals( "resource3 has wrong callback", callback3, registry.getResource( uri3 ).getGetCallback() );
        assertEquals( "resource3 has wrong callback", callback3, registry.getResource( uri3 ).getPostCallback() );
        assertEquals( "resource3 has wrong callback", callback3, registry.getResource( uri3 ).getPutCallback() );
        assertEquals( "resource3 has wrong callback", callback3, registry.getResource( uri3 ).getDeleteCallback() );

        assertEquals( "resource4 has wrong callback", callback4, registry.getResource( uri4 ).getGetCallback() );
        assertEquals( "resource4 has wrong callback", callback4, registry.getResource( uri4 ).getPostCallback() );
        assertEquals( "resource4 has wrong callback", callback4, registry.getResource( uri4 ).getPutCallback() );
        assertEquals( "resource4 has wrong callback", callback4, registry.getResource( uri4 ).getDeleteCallback() );
    }

    @Test
    public void testCallBackWithRequestCodeFlags() throws InvalidResourceUriException
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
        RequestCodeFlags flags1= new RequestCodeFlags( true, false, false, false );
        RequestCodeFlags flags2= new RequestCodeFlags( false, true, false, false );
        RequestCodeFlags flags3= new RequestCodeFlags( false, false, true, false );
        RequestCodeFlags flags4= new RequestCodeFlags( false, false, false, true );

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

        assertEquals( "resource1 has wrong callback", callback1, registry.getResource( uri1 ).getGetCallback() );
        assertEquals( "resource1 has wrong callback", callback2, registry.getResource( uri1 ).getPostCallback() );
        assertEquals( "resource1 has wrong callback", callback3, registry.getResource( uri1 ).getPutCallback() );
        assertEquals( "resource1 has wrong callback", callback4, registry.getResource( uri1 ).getDeleteCallback() );
    }
    
    @Test
    public void testCallBackWithWildcard1() throws InvalidResourceUriException
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
        RequestCodeFlags flags1= new RequestCodeFlags( true, true, true, true );
        RequestCodeFlags flags2= new RequestCodeFlags( true, true, true, true );

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
        assertEquals( "resource1 has wrong delete callback", callback1, registry.getResource( uri1 ).getDeleteCallback() );

        assertEquals( "resource2 has wrong get callback", callback2, registry.getResource( uri2 ).getGetCallback() );
        assertEquals( "resource2 has wrong post callback", callback2, registry.getResource( uri2 ).getPostCallback() );
        assertEquals( "resource2 has wrong put callback", callback2, registry.getResource( uri2 ).getPutCallback() );
        assertEquals( "resource2 has wrong delete callback", callback2, registry.getResource( uri2 ).getDeleteCallback() );
        
        assertEquals( "resource3 has wrong get callback", callback1, registry.getResource( uri3 ).getGetCallback() );
        assertEquals( "resource3 has wrong post callback", callback1, registry.getResource( uri3 ).getPostCallback() );
        assertEquals( "resource3 has wrong put callback", callback1, registry.getResource( uri3 ).getPutCallback() );
        assertEquals( "resource3 has wrong delete callback", callback1, registry.getResource( uri3 ).getDeleteCallback() );
        
        assertEquals( "resource4 has wrong get callback", callback1, registry.getResource( uri4 ).getGetCallback() );
        assertEquals( "resource4 has wrong post callback", callback1, registry.getResource( uri4 ).getPostCallback() );
        assertEquals( "resource4 has wrong put callback", callback1, registry.getResource( uri4 ).getPutCallback() );
        assertEquals( "resource4 has wrong delete callback", callback1, registry.getResource( uri4 ).getDeleteCallback() );
    }

    @Test
    public void testCallBackWithWildcardAndRequestCodeFlags1() throws InvalidResourceUriException
    {
        CoapResource root= new CoapResource( "" );
        ResourceRegistry registry= new ResourceRegistry( root );
        TestSourceCallBack callback1= new TestSourceCallBack();
        TestSourceCallBack callback2= new TestSourceCallBack();
        TestSourceCallBack callback3= new TestSourceCallBack();
        TestSourceCallBack callback4= new TestSourceCallBack();
        TestSourceCallBack callback5= new TestSourceCallBack();
        ResourceConfig resourceConfig;
        String name1= "resource1";
        String uri1= "/resource1";
        String name2= "resource2";
        String uri2= "/resource1/resource2";
        String name3= "resource3";
        String uri3= "/resource1/resource2/resource3";
        String name4= "resource4";
        String uri4= "/resource1/resource4";
        RequestCodeFlags flags1= new RequestCodeFlags( true, true, true, true );
        RequestCodeFlags flags2= new RequestCodeFlags( true, false, false, false );
        RequestCodeFlags flags3= new RequestCodeFlags( false, true, false, false );
        RequestCodeFlags flags4= new RequestCodeFlags( false, false, true, false );
        RequestCodeFlags flags5= new RequestCodeFlags( false, false, false, true );

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
        
        assertEquals( "resource1 has wrong get callback", callback1, registry.getResource( uri1 ).getGetCallback() );
        assertEquals( "resource1 has wrong post callback", callback1, registry.getResource( uri1 ).getPostCallback() );
        assertEquals( "resource1 has wrong put callback", callback1, registry.getResource( uri1 ).getPutCallback() );
        assertEquals( "resource1 has wrong delete callback", callback1, registry.getResource( uri1 ).getDeleteCallback() );

        assertEquals( "resource2 has wrong get callback", callback2, registry.getResource( uri2 ).getGetCallback() );
        assertEquals( "resource2 has wrong post callback", callback3, registry.getResource( uri2 ).getPostCallback() );
        assertEquals( "resource2 has wrong put callback", callback4, registry.getResource( uri2 ).getPutCallback() );
        assertEquals( "resource2 has wrong delete callback", callback5, registry.getResource( uri2 ).getDeleteCallback() );
        
        assertEquals( "resource3 has wrong get callback", callback1, registry.getResource( uri3 ).getGetCallback() );
        assertEquals( "resource3 has wrong post callback", callback1, registry.getResource( uri3 ).getPostCallback() );
        assertEquals( "resource3 has wrong put callback", callback1, registry.getResource( uri3 ).getPutCallback() );
        assertEquals( "resource3 has wrong delete callback", callback1, registry.getResource( uri3 ).getDeleteCallback() );
        
        assertEquals( "resource4 has wrong get callback", callback1, registry.getResource( uri4 ).getGetCallback() );
        assertEquals( "resource4 has wrong post callback", callback1, registry.getResource( uri4 ).getPostCallback() );
        assertEquals( "resource4 has wrong put callback", callback1, registry.getResource( uri4 ).getPutCallback() );
        assertEquals( "resource4 has wrong delete callback", callback1, registry.getResource( uri4 ).getDeleteCallback() );
    }

    @Test
    public void testCallBackWithWildcard2() throws InvalidResourceUriException
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
        RequestCodeFlags flags1= new RequestCodeFlags( true, true, true, true );
        RequestCodeFlags flags2= new RequestCodeFlags( false, false, true, false );
        RequestCodeFlags flags3= new RequestCodeFlags( true, true, false, true );
        RequestCodeFlags flags4= new RequestCodeFlags( true, true, true, true );

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
        assertEquals( "resource1 has wrong delete callback", callback1, registry.getResource( uri1 ).getDeleteCallback() );

        assertEquals( "resource2 has wrong get callback", callback1, registry.getResource( uri2 ).getGetCallback() );
        assertEquals( "resource2 has wrong post callback", callback1, registry.getResource( uri2 ).getPostCallback() );
        assertEquals( "resource2 has wrong put callback", callback1, registry.getResource( uri2 ).getPutCallback() );
        assertEquals( "resource2 has wrong delete callback", callback1, registry.getResource( uri2 ).getDeleteCallback() );
        
        assertEquals( "resource3 has wrong get callback", callback3, registry.getResource( uri3 ).getGetCallback() );
        assertEquals( "resource3 has wrong post callback", callback3, registry.getResource( uri3 ).getPostCallback() );
        assertEquals( "resource3 has wrong put callback", callback2, registry.getResource( uri3 ).getPutCallback() ); //note callback2
        assertEquals( "resource3 has wrong delete callback", callback3, registry.getResource( uri3 ).getDeleteCallback() );
        
        assertEquals( "resource4 has wrong get callback", callback4, registry.getResource( uri4 ).getGetCallback() );
        assertEquals( "resource4 has wrong post callback", callback4, registry.getResource( uri4 ).getPostCallback() );
        assertEquals( "resource4 has wrong put callback", callback4, registry.getResource( uri4 ).getPutCallback() );
        assertEquals( "resource4 has wrong delete callback", callback4, registry.getResource( uri4 ).getDeleteCallback() );
}

    @Test
    public void testCallBackWithWildcard3() throws InvalidResourceUriException
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
        RequestCodeFlags flags1= new RequestCodeFlags( true, true, true, true );
        RequestCodeFlags flags2= new RequestCodeFlags( false, false, true, false );
        RequestCodeFlags flags3= new RequestCodeFlags( true, true, false, true );
        RequestCodeFlags flags4= new RequestCodeFlags( true, true, true, true );

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
        assertEquals( "resource1 has wrong delete callback", callback1, registry.getResource( uri1 ).getDeleteCallback() );

        assertEquals( "resource2 has wrong get callback", null, registry.getResource( uri2 ).getGetCallback() );
        assertEquals( "resource2 has wrong post callback", null, registry.getResource( uri2 ).getPostCallback() );
        assertEquals( "resource2 has wrong put callback", callback2, registry.getResource( uri2 ).getPutCallback() ); //note callback2
        assertEquals( "resource2 has wrong delete callback", null, registry.getResource( uri2 ).getDeleteCallback() );
        
        assertEquals( "resource3 has wrong get callback", callback3, registry.getResource( uri3 ).getGetCallback() );
        assertEquals( "resource3 has wrong post callback", callback3, registry.getResource( uri3 ).getPostCallback() );
        assertEquals( "resource3 has wrong put callback", callback2, registry.getResource( uri3 ).getPutCallback() ); //note callback2
        assertEquals( "resource3 has wrong delete callback", callback3, registry.getResource( uri3 ).getDeleteCallback() );
        
        assertEquals( "resource4 has wrong get callback", callback4, registry.getResource( uri4 ).getGetCallback() );
        assertEquals( "resource4 has wrong post callback", callback4, registry.getResource( uri4 ).getPostCallback() );
        assertEquals( "resource4 has wrong put callback", callback4, registry.getResource( uri4 ).getPutCallback() );
        assertEquals( "resource4 has wrong delete callback", callback4, registry.getResource( uri4 ).getDeleteCallback() );

    }

    @Test
    public void testGetResourceNonexistent() throws InvalidResourceUriException
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

        InvalidResourceUriException e= assertThrows( InvalidResourceUriException.class, () -> {
            registry.getResource( uri4 ).getURI();
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( uri4 ) );
        assertTrue( "exception has wrong message", e.getMessage().contains( "resource does not exist" ) );
    }

    /**
     * Test the getResource method
     * @throws InvalidResourceUriException when uri is invalid
     */
    @Test
    public void testGetResource() throws InvalidResourceUriException
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

    /**
     * Test the findResource method
     * @throws InvalidResourceUriException when uri is invalid
     */
    @Test
    public void testFindResources() throws InvalidResourceUriException
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
            }
        }
        return resourceFlags;
    }
}
