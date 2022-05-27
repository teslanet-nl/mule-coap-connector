/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2022 (teslanet.nl) Rogier Cobben
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.api.DiscoveredResource;
import nl.teslanet.mule.connectors.coap.api.error.InvalidEntityTagException;


/**
 * Test the implementation of the DiscoveredResource class.
 *
 */
public class DiscoveredResourceTest
{
    private String path1;

    private boolean obs1;

    private String title1;

    private LinkedList< String > ifdesc1;

    private LinkedList< String > rt1;

    private String sz1;

    private LinkedList< String > ct1;

    private String path2;

    private boolean obs2;

    private String title2;

    private LinkedList< String > ifdesc2;

    private LinkedList< String > rt2;

    private String sz2= "340909";

    private LinkedList< String > ct2;

    @Before
    public void setup()
    {
        path1= "/path";
        obs1= true;
        title1= "title";
        ifdesc1= new LinkedList<>();
        ifdesc1.add( "if1" );
        ifdesc1.add( "if2" );
        rt1= new LinkedList<>();
        rt1.add( "rt1" );
        rt1.add( "rt2" );
        sz1= "3409";
        ct1= new LinkedList<>();
        ct1.add( "0" );
        ct1.add( "41" );

        path2= "/path2";
        obs2= false;
        title2= "title2";
        ifdesc2= new LinkedList<>();
        ifdesc2.add( "if1" );
        ifdesc2.add( "if2" );
        ifdesc2.add( "if3" );
        rt2= new LinkedList<>();
        rt2.add( "rt1" );
        rt2.add( "rt2" );
        rt2.add( "rt3" );
        sz2= "340909";
        ct2= new LinkedList<>();
        ct2.add( "0" );
        ct2.add( "41" );
        ct2.add( "40" );
    }

    @Test
    public void testConstructorAndGetters1()
    {
        DiscoveredResource resource= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct1 );

        assertNotNull( "no object constructed", resource );

        assertEquals( "wrong path", path1, resource.getPath() );
        assertEquals( "wrong obs", obs1, resource.isObs() );
        assertEquals( "wrong Obs", obs1, resource.getObs() );
        assertEquals( "wrong title", title1, resource.getTitle() );
        assertEquals( "wrong if", ifdesc1, resource.getIf() );
        assertEquals( "wrong rt", rt1, resource.getRt() );
        assertEquals( "wrong sz", sz1, resource.getSz() );
        assertEquals( "wrong ct", ct1, resource.getCt() );
    }

    @Test
    public void testToString()
    {
        DiscoveredResource resource= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct1 );

        assertTrue( "toString shows wrong path value", resource.toString().contains( path1 ) );
        assertTrue( "toString shows wrong obs value", resource.toString().contains( Boolean.toString( obs1 ) ) );
        assertTrue( "toString shows wrong title value", resource.toString().contains( title1 ) );
        assertTrue( "toString shows wrong if value", resource.toString().contains( ifdesc1.toString() ) );
        assertTrue( "toString shows wrong rt value", resource.toString().contains( rt1.toString() ) );
        assertTrue( "toString shows wrong sz value", resource.toString().contains( sz1.toString() ) );
        assertTrue( "toString shows wrong ct value", resource.toString().contains( ct1.toString() ) );
    }

    @SuppressWarnings( "unlikely-arg-type" )
    @Test
    public void testEqualsToWrongClass()
    {
        DiscoveredResource resource= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        assertFalse( "resource wrongfully equals other class", resource.equals( Boolean.TRUE ) );
    }

    @Test
    public void testEqualsNull()
    {
        DiscoveredResource resource= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        assertFalse( "resource wrongfully equals other class", resource.equals( null ) );
    }

    @Test
    public void testEquals()
    {
        DiscoveredResource resource1= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource2= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource3= new DiscoveredResource( path2, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource4= new DiscoveredResource( path1, obs2, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource5= new DiscoveredResource( path1, obs1, title2, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource6= new DiscoveredResource( path1, obs1, title1, ifdesc2, rt1, sz1, ct1 );
        DiscoveredResource resource7= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt2, sz1, ct1 );
        DiscoveredResource resource8= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz2, ct1 );
        DiscoveredResource resource9= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct2 );

        assertTrue( "resource2 wrongfully equals resource1", resource1.equals( resource2 ) );
        assertFalse( "resource3 wrongfully equals resource1", resource1.equals( resource3 ) );
        assertFalse( "resource4 wrongfully equals resource1", resource1.equals( resource4 ) );
        assertFalse( "resource5 wrongfully equals resource1", resource1.equals( resource5 ) );
        assertFalse( "resource6 wrongfully equals resource1", resource1.equals( resource6 ) );
        assertFalse( "resource7 wrongfully equals resource1", resource1.equals( resource7 ) );
        assertFalse( "resource8 wrongfully equals resource1", resource1.equals( resource8 ) );
        assertFalse( "resource9 wrongfully equals resource1", resource1.equals( resource9 ) );
    }

    @Test
    public void testEquals2()
    {
        DiscoveredResource resource1= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource2= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource3= new DiscoveredResource( path2, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource4= new DiscoveredResource( path1, false, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource5= new DiscoveredResource( path1, obs1, null, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource6= new DiscoveredResource( path1, obs1, title1, ifdesc2, rt1, sz1, ct1 );
        DiscoveredResource resource7= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt2, sz1, ct1 );
        DiscoveredResource resource8= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, null, ct1 );
        DiscoveredResource resource9= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct2 );

        assertTrue( "resource2 wrongfully equals resource1", resource1.equals( resource2 ) );
        assertFalse( "resource3 wrongfully equals resource1", resource1.equals( resource3 ) );
        assertFalse( "resource4 wrongfully equals resource1", resource1.equals( resource4 ) );
        assertFalse( "resource5 wrongfully equals resource1", resource1.equals( resource5 ) );
        assertFalse( "resource6 wrongfully equals resource1", resource1.equals( resource6 ) );
        assertFalse( "resource7 wrongfully equals resource1", resource1.equals( resource7 ) );
        assertFalse( "resource8 wrongfully equals resource1", resource1.equals( resource8 ) );
        assertFalse( "resource9 wrongfully equals resource1", resource1.equals( resource9 ) );
    }

    @Test
    public void testCompareTo() throws InvalidEntityTagException
    {
        DiscoveredResource resource1= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource2= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource3= new DiscoveredResource( path2, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource4= new DiscoveredResource( path1, obs2, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource5= new DiscoveredResource( path1, obs1, title2, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource6= new DiscoveredResource( path1, obs1, title1, ifdesc2, rt1, sz1, ct1 );
        DiscoveredResource resource7= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt2, sz1, ct1 );
        DiscoveredResource resource8= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz2, ct1 );
        DiscoveredResource resource9= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct2 );

        assertTrue( "resource2 wrongfully compares resource1", 0 == resource1.compareTo( resource2 ) );
        assertTrue( "resource3 wrongfully compares resource1", 0 > resource1.compareTo( resource3 ) );
        assertTrue( "resource4 wrongfully compares resource1", 0 < resource1.compareTo( resource4 ) );
        assertTrue( "resource5 wrongfully compares resource1", 0 > resource1.compareTo( resource5 ) );
        assertTrue( "resource6 wrongfully compares resource1", 0 > resource1.compareTo( resource6 ) );
        assertTrue( "resource7 wrongfully compares resource1", 0 > resource1.compareTo( resource7 ) );
        assertTrue( "resource8 wrongfully compares resource1", 0 > resource1.compareTo( resource8 ) );
        assertTrue( "resource9 wrongfully compares resource1", 0 > resource1.compareTo( resource9 ) );

        assertTrue( "resource2 wrongfully compares resource1", 0 == resource2.compareTo( resource1 ) );
        assertTrue( "resource3 wrongfully compares resource1", 0 < resource3.compareTo( resource1 ) );
        assertTrue( "resource4 wrongfully compares resource1", 0 > resource4.compareTo( resource1 ) );
        assertTrue( "resource5 wrongfully compares resource1", 0 < resource5.compareTo( resource1 ) );
        assertTrue( "resource6 wrongfully compares resource1", 0 < resource6.compareTo( resource1 ) );
        assertTrue( "resource7 wrongfully compares resource1", 0 < resource7.compareTo( resource1 ) );
        assertTrue( "resource8 wrongfully compares resource1", 0 < resource7.compareTo( resource1 ) );
        assertTrue( "resource9 wrongfully compares resource1", 0 < resource8.compareTo( resource1 ) );

        assertTrue( "null wrongfully compares resource1", 0 < resource1.compareTo( null ) );
    }

    @Test
    public void testCompareTo2() throws InvalidEntityTagException
    {
        DiscoveredResource resource1= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource2= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource3= new DiscoveredResource( path2, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource4= new DiscoveredResource( path1, false, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource5= new DiscoveredResource( path1, obs1, null, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource6= new DiscoveredResource( path1, obs1, title1, ifdesc2, rt1, sz1, ct1 );
        DiscoveredResource resource7= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt2, sz1, ct1 );
        DiscoveredResource resource8= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, null, ct1 );
        DiscoveredResource resource9= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct2 );

        assertTrue( "resource2 wrongfully compares resource1", 0 == resource1.compareTo( resource2 ) );
        assertTrue( "resource3 wrongfully compares resource1", 0 > resource1.compareTo( resource3 ) );
        assertTrue( "resource4 wrongfully compares resource1", 0 < resource1.compareTo( resource4 ) );
        assertTrue( "resource5 wrongfully compares resource1", 0 > resource1.compareTo( resource5 ) );
        assertTrue( "resource6 wrongfully compares resource1", 0 > resource1.compareTo( resource6 ) );
        assertTrue( "resource7 wrongfully compares resource1", 0 > resource1.compareTo( resource7 ) );
        assertTrue( "resource8 wrongfully compares resource1", 0 > resource1.compareTo( resource8 ) );
        assertTrue( "resource9 wrongfully compares resource1", 0 > resource1.compareTo( resource9 ) );

        assertTrue( "resource2 wrongfully compares resource1", 0 == resource2.compareTo( resource1 ) );
        assertTrue( "resource3 wrongfully compares resource1", 0 < resource3.compareTo( resource1 ) );
        assertTrue( "resource4 wrongfully compares resource1", 0 > resource4.compareTo( resource1 ) );
        assertTrue( "resource5 wrongfully compares resource1", 0 < resource5.compareTo( resource1 ) );
        assertTrue( "resource6 wrongfully compares resource1", 0 < resource6.compareTo( resource1 ) );
        assertTrue( "resource7 wrongfully compares resource1", 0 < resource7.compareTo( resource1 ) );
        assertTrue( "resource8 wrongfully compares resource1", 0 < resource7.compareTo( resource1 ) );
        assertTrue( "resource9 wrongfully compares resource1", 0 < resource8.compareTo( resource1 ) );

        assertTrue( "null wrongfully compares resource2", 0 < resource2.compareTo( null ) );
    }

    @Test
    public void testHashCode() throws InvalidEntityTagException
    {
        DiscoveredResource resource1= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource2= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource3= new DiscoveredResource( path2, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource4= new DiscoveredResource( path1, obs2, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource5= new DiscoveredResource( path1, obs1, title2, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource6= new DiscoveredResource( path1, obs1, title1, ifdesc2, rt1, sz1, ct1 );
        DiscoveredResource resource7= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt2, sz1, ct1 );
        DiscoveredResource resource8= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz2, ct1 );
        DiscoveredResource resource9= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct2 );

        assertEquals( "resource2 wrongfully compares resource1", resource1.hashCode(), resource2.hashCode() );
        assertNotEquals( "resource3 wrongfully compares resource1", resource1.hashCode(), resource3.hashCode() );
        assertNotEquals( "resource4 wrongfully compares resource1", resource1.hashCode(), resource4.hashCode() );
        assertNotEquals( "resource5 wrongfully compares resource1", resource1.hashCode(), resource5.hashCode() );
        assertNotEquals( "resource6 wrongfully compares resource1", resource1.hashCode(), resource6.hashCode() );
        assertNotEquals( "resource7 wrongfully compares resource1", resource1.hashCode(), resource7.hashCode() );
        assertNotEquals( "resource8 wrongfully compares resource1", resource1.hashCode(), resource8.hashCode() );
        assertNotEquals( "resource9 wrongfully compares resource1", resource1.hashCode(), resource9.hashCode() );
    }

    @Test
    public void testHashCode2() throws InvalidEntityTagException
    {
        DiscoveredResource resource1= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource2= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource3= new DiscoveredResource( path2, obs1, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource4= new DiscoveredResource( path1, false, title1, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource5= new DiscoveredResource( path1, obs1, null, ifdesc1, rt1, sz1, ct1 );
        DiscoveredResource resource6= new DiscoveredResource( path1, obs1, title1, ifdesc2, rt1, sz1, ct1 );
        DiscoveredResource resource7= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt2, sz1, ct1 );
        DiscoveredResource resource8= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, null, ct1 );
        DiscoveredResource resource9= new DiscoveredResource( path1, obs1, title1, ifdesc1, rt1, sz1, ct2 );

        assertEquals( "resource2 wrongfully compares resource1", resource1.hashCode(), resource2.hashCode() );
        assertNotEquals( "resource3 wrongfully compares resource1", resource1.hashCode(), resource3.hashCode() );
        assertNotEquals( "resource4 wrongfully compares resource1", resource1.hashCode(), resource4.hashCode() );
        assertNotEquals( "resource5 wrongfully compares resource1", resource1.hashCode(), resource5.hashCode() );
        assertNotEquals( "resource6 wrongfully compares resource1", resource1.hashCode(), resource6.hashCode() );
        assertNotEquals( "resource7 wrongfully compares resource1", resource1.hashCode(), resource7.hashCode() );
        assertNotEquals( "resource8 wrongfully compares resource1", resource1.hashCode(), resource8.hashCode() );
        assertNotEquals( "resource9 wrongfully compares resource1", resource1.hashCode(), resource9.hashCode() );
    }
}
