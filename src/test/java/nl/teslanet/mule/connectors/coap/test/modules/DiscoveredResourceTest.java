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
package nl.teslanet.mule.connectors.coap.test.modules;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.teslanet.mule.connectors.coap.api.DiscoveredResource;
import nl.teslanet.mule.connectors.coap.api.ResourceInfoConfig;
import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;


/**
 * Test the implementation of the DiscoveredResource class.
 *
 */
public class DiscoveredResourceTest
{
    @Test
    public void testConstructorAndGetters1()
    {
        String path= "/path";
        boolean obs= true;
        String title= "title";
        String ifdesc= "if1,if2";
        String rt= "rt1,rt2";
        String sz= "3409";
        String ct= "0,41";

        DiscoveredResource resource= new DiscoveredResource( path, obs, title, ifdesc, rt, sz, ct );

        assertNotNull( "no object constructed", resource );

        assertEquals( "wrong path", path, resource.getPath() );
        assertEquals( "wrong obs", obs, resource.isObs() );
        assertEquals( "wrong Obs", obs, resource.getObs() );
        assertEquals( "wrong title", title, resource.getTitle() );
        assertEquals( "wrong if", ifdesc, resource.getIf() );
        assertEquals( "wrong rt", rt, resource.getRt() );
        assertEquals( "wrong sz", sz, resource.getSz() );
        assertEquals( "wrong ct", ct, resource.getCt() );
    }

    @Test
    public void testConstructorAndGetters2()
    {
        String path= "/path";
        boolean obs= true;
        String title= "title";
        String ifdesc= "if1,if2";
        String rt= "rt1,rt2";
        String sz= "3409";
        String ct= "0,41";

        DiscoveredResource resource= new DiscoveredResource( path, obs, new ResourceInfoConfig( title, ifdesc, rt, sz, ct ) );

        assertNotNull( "no object constructed", resource );

        assertEquals( "wrong path", path, resource.getPath() );
        assertEquals( "wrong obs", obs, resource.isObs() );
        assertEquals( "wrong Obs", obs, resource.getObs() );
        assertEquals( "wrong title", title, resource.getTitle() );
        assertEquals( "wrong if", ifdesc, resource.getIf() );
        assertEquals( "wrong rt", rt, resource.getRt() );
        assertEquals( "wrong sz", sz, resource.getSz() );
        assertEquals( "wrong ct", ct, resource.getCt() );
    }

    @Test
    public void testToString()
    {
        String path= "/path";
        boolean obs= true;
        String title= "title";
        String ifdesc= "if1,if2";
        String rt= "rt1,rt2";
        String sz= "3409";
        String ct= "0,41";

        DiscoveredResource resource= new DiscoveredResource( path, obs, new ResourceInfoConfig( title, ifdesc, rt, sz, ct ) );

        assertTrue( "toString shows wrong path value", resource.toString().contains( path ) );
        assertTrue( "toString shows wrong obs value", resource.toString().contains( Boolean.toString( obs ) ) );
        assertTrue( "toString shows wrong title value", resource.toString().contains( title ) );
        assertTrue( "toString shows wrong if value", resource.toString().contains( ifdesc ) );
        assertTrue( "toString shows wrong rt value", resource.toString().contains( rt ) );
        assertTrue( "toString shows wrong sz value", resource.toString().contains( sz ) );
        assertTrue( "toString shows wrong ct value", resource.toString().contains( ct ) );
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEqualsToWrongClass()
    {
        String path= "/path";
        boolean obs= true;
        String title= "title";
        String ifdesc= "if1,if2";
        String rt= "rt1,rt2";
        String sz= "3409";
        String ct= "0,41";

        DiscoveredResource resource= new DiscoveredResource( path, obs, title, ifdesc, rt, sz, ct );
        assertFalse( "resource wrongfully equals other class", resource.equals( Boolean.TRUE ) );
    }

    @Test
    public void testEqualsNull()
    {
        String path= "/path";
        boolean obs= true;
        String title= "title";
        String ifdesc= "if1,if2";
        String rt= "rt1,rt2";
        String sz= "3409";
        String ct= "0,41";

        DiscoveredResource resource= new DiscoveredResource( path, obs, title, ifdesc, rt, sz, ct );
        assertFalse( "resource wrongfully equals other class", resource.equals( null ) );
    }

    @Test
    public void testEquals()
    {
        String path1= "/path";
        boolean obs1= true;
        String title1= "title";
        String ifdesc1= "if1,if2";
        String rt1= "rt1,rt2";
        String sz1= "3409";
        String ct1= "0,41";
        String path2= "/path2";
        boolean obs2= false;
        String title2= "title2";
        String ifdesc2= "if1,if2,if3";
        String rt2= "rt1,rt2,rt3";
        String sz2= "340909";
        String ct2= "0,41,40";

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
        String path1= "/path";
        boolean obs1= true;
        String title1= "title";
        String ifdesc1= "if1,if2";
        String rt1= "rt1,rt2";
        String sz1= "3409";
        String ct1= "0,41";
        String path2= "/path2";
        boolean obs2= false;
        String title2= null;
        String ifdesc2= null;
        String rt2= null;
        String sz2= null;
        String ct2= null;

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
    public void testCompareTo() throws InvalidETagException
    {
        String path1= "/path";
        boolean obs1= true;
        String title1= "title";
        String ifdesc1= "if1,if2";
        String rt1= "rt1,rt2";
        String sz1= "3409";
        String ct1= "0,41";
        String path2= "/path2";
        boolean obs2= false;
        String title2= "title2";
        String ifdesc2= "if1,if2,if3";
        String rt2= "rt1,rt2,rt3";
        String sz2= "340909";
        String ct2= "0,41,40";

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
    public void testCompareTo2() throws InvalidETagException
    {
        String path1= "/path";
        boolean obs1= true;
        String title1= "title";
        String ifdesc1= "if1,if2";
        String rt1= "rt1,rt2";
        String sz1= "3409";
        String ct1= "0,41";
        String path2= "/path2";
        boolean obs2= false;
        String title2= null;
        String ifdesc2= null;
        String rt2= null;
        String sz2= null;
        String ct2= null;

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

        assertTrue( "null wrongfully compares resource2", 0 < resource2.compareTo( null ) );
    }

    @Test
    public void testHashCode() throws InvalidETagException
    {
        String path1= "/path";
        boolean obs1= true;
        String title1= "title";
        String ifdesc1= "if1,if2";
        String rt1= "rt1,rt2";
        String sz1= "3409";
        String ct1= "0,41";
        String path2= "/path2";
        boolean obs2= false;
        String title2= "title2";
        String ifdesc2= "if1,if2,if3";
        String rt2= "rt1,rt2,rt3";
        String sz2= "340909";
        String ct2= "0,41,40";

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
    public void testHashCode2() throws InvalidETagException
    {
        String path1= "/path";
        boolean obs1= true;
        String title1= "title";
        String ifdesc1= "if1,if2";
        String rt1= "rt1,rt2";
        String sz1= "3409";
        String ct1= "0,41";
        String path2= "/path2";
        boolean obs2= false;
        String title2= null;
        String ifdesc2= null;
        String rt2= null;
        String sz2= null;
        String ct2= null;

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
}
