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
package nl.teslanet.mule.connectors.coap.test.server.modules;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.teslanet.mule.connectors.coap.internal.server.RequestCodeFlags;


/**
 * Test the RequestCodeFlags class.
 */
public class RequestCodeFlagsTest
{
    @Test
    public void defaultConstructorTest()
    {
        RequestCodeFlags flags= new RequestCodeFlags();

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );
    }

    @Test
    public void constructor1Test()
    {
        RequestCodeFlags flags= new RequestCodeFlags( false, false, false, false, false, false, false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );
    }

    @Test
    public void constructor2Test()
    {
        RequestCodeFlags flags= new RequestCodeFlags( true, false, false, false, false, false, false );

        assertTrue( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );
    }

    @Test
    public void constructor3Test()
    {
        RequestCodeFlags flags= new RequestCodeFlags( false, true, false, false, false, false, false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertTrue( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );
    }

    @Test
    public void constructor4Test()
    {
        RequestCodeFlags flags= new RequestCodeFlags( false, false, true, false, false, false, false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertTrue( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );
    }

    @Test
    public void constructor5Test()
    {
        RequestCodeFlags flags= new RequestCodeFlags( false, false, false, true, false, false, false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertTrue( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );
    }

    @Test
    public void constructor6Test()
    {
        RequestCodeFlags flags= new RequestCodeFlags( false, false, false, false, true, false, false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertTrue( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );
    }

    @Test
    public void constructor7Test()
    {
        RequestCodeFlags flags= new RequestCodeFlags( false, false, false, false, false, true, false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertTrue( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );
    }

    @Test
    public void constructor8Test()
    {
        RequestCodeFlags flags= new RequestCodeFlags( false, false, false, false, false, false, true );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertTrue( "iPatch flag has wrong value", flags.isIpatch() );
    }

    @Test
    public void constructor9Test()
    {
        RequestCodeFlags flags= new RequestCodeFlags( true, true, true, true, true, true, true );

        assertTrue( "Get flag has wrong value", flags.isGet() );
        assertTrue( "Post flag has wrong value", flags.isPost() );
        assertTrue( "Put flag has wrong value", flags.isPut() );
        assertTrue( "Delete flag has wrong value", flags.isDelete() );
        assertTrue( "Fetch flag has wrong value", flags.isFetch() );
        assertTrue( "Patch flag has wrong value", flags.isPatch() );
        assertTrue( "iPatch flag has wrong value", flags.isIpatch() );
    }

    @Test
    public void getSetterTest()
    {
        RequestCodeFlags flags= new RequestCodeFlags( false, false, false, false, false, false, false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );

        flags.setGet( true );

        assertTrue( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );

        flags.setGet( false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );
    }

    @Test
    public void postSetterTest()
    {
        RequestCodeFlags flags= new RequestCodeFlags( false, false, false, false, false, false, false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );

        flags.setPost( true );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertTrue( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );

        flags.setPost( false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );
    }

    @Test
    public void putSetterTest()
    {
        RequestCodeFlags flags= new RequestCodeFlags( false, false, false, false, false, false, false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );

        flags.setPut( true );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertTrue( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );

        flags.setPut( false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );
    }

    @Test
    public void deleteSetterTest()
    {
        RequestCodeFlags flags= new RequestCodeFlags( false, false, false, false, false, false, false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );

        flags.setDelete( true );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertTrue( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );

        flags.setDelete( false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );
    }

    @Test
    public void fetchSetterTest()
    {
        RequestCodeFlags flags= new RequestCodeFlags( false, false, false, false, false, false, false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );

        flags.setFetch( true );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertTrue( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );

        flags.setFetch( false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );
    }

    @Test
    public void patchSetterTest()
    {
        RequestCodeFlags flags= new RequestCodeFlags( false, false, false, false, false, false, false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );

        flags.setPatch( true );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertTrue( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );

        flags.setPatch( false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );
    }

    @Test
    public void ipatchSetterTest()
    {
        RequestCodeFlags flags= new RequestCodeFlags( false, false, false, false, false, false, false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );

        flags.setIpatch( true );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertTrue( "iPatch flag has wrong value", flags.isIpatch() );

        flags.setIpatch( false );

        assertFalse( "Get flag has wrong value", flags.isGet() );
        assertFalse( "Post flag has wrong value", flags.isPost() );
        assertFalse( "Put flag has wrong value", flags.isPut() );
        assertFalse( "Delete flag has wrong value", flags.isDelete() );
        assertFalse( "Fetch flag has wrong value", flags.isFetch() );
        assertFalse( "Patch flag has wrong value", flags.isPatch() );
        assertFalse( "iPatch flag has wrong value", flags.isIpatch() );
    }

    @Test
    public void equalsTest()
    {
        RequestCodeFlags flags1= new RequestCodeFlags( false, true, true, false, true, true, false );
        RequestCodeFlags flags2= new RequestCodeFlags( false, true, false, false, true, true, false );
        RequestCodeFlags flags3= new RequestCodeFlags( false, true, true, false, true, true, false );

        assertTrue( "flag 1 equals flag 1 returns wrong result", flags1.equals( flags1 ) );
        assertFalse( "flag 1 equals null returns wrong result", flags1.equals( null ) );
        assertFalse( "flag 1 equals flag 2 returns wrong result", flags1.equals( flags2 ) );
        assertFalse( "flag 2 equals flag 1 returns wrong result", flags2.equals( flags1 ) );
        assertTrue( "flag 1 equals flag 3 returns wrong result", flags1.equals( flags3 ) );
        assertTrue( "flag 3 equals flag 1 returns wrong result", flags3.equals( flags1 ) );
    }

    @Test
    public void compareTest()
    {
        RequestCodeFlags flags1= new RequestCodeFlags( false, true, true, false, true, true, false );
        RequestCodeFlags flags2= new RequestCodeFlags( false, true, false, false, true, true, false );
        RequestCodeFlags flags3= new RequestCodeFlags( false, true, true, false, true, true, false );

        assertEquals( "flag 1 equals flag 1 returns wrong result", 0, flags1.compareTo( flags1 ) );
        assertEquals( "flag 1 equals null returns wrong result", 1, flags1.compareTo( null ) );
        assertEquals( "flag 1 equals flag 2 returns wrong result", 1, flags1.compareTo( flags2 ) );
        assertEquals( "flag 2 equals flag 1 returns wrong result", -1, flags2.compareTo( flags1 ) );
        assertEquals( "flag 1 equals flag 3 returns wrong result", 0, flags1.compareTo( flags3 ) );
        assertEquals( "flag 3 equals flag 1 returns wrong result", 0, flags3.compareTo( flags1 ) );
    }

    @Test
    public void hashCodeTest()
    {
        RequestCodeFlags flags1= new RequestCodeFlags( false, true, true, false, true, true, false );
        RequestCodeFlags flags2= new RequestCodeFlags( false, true, false, false, true, true, false );
        RequestCodeFlags flags3= new RequestCodeFlags( false, true, true, false, true, true, false );

        assertEquals( "hashcode flag 1 has wrong value", 0x36, flags1.hashCode() );
        assertEquals( "hashcode flag 2 has wrong value", 0x32, flags2.hashCode() );
        assertEquals( "hashcode flag 3 has wrong value", 0x36, flags3.hashCode() );
    }
}
