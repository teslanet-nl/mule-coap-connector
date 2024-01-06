/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2023 (teslanet.nl) Rogier Cobben
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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import nl.teslanet.mule.connectors.coap.api.error.InvalidEntityTagException;
import nl.teslanet.mule.connectors.coap.internal.server.RequestCodeFlags;


/**
 * Test the implementation of the RequestCodeFlags class.
 *
 */
@RunWith( Parameterized.class )
public class RequestCodeFlagsTest
{
    /**
     * Set get flag.
     */
    @Parameter( 0 )
    public boolean get;

    /**
     * Set post flag.
     */
    @Parameter( 1 )
    public boolean post;

    /**
     * Set put flag.
     */
    @Parameter( 2 )
    public boolean put;

    /**
     * Set delete flag.
     */
    @Parameter( 3 )
    public boolean delete;

    /**
     * Set fetch flag.
     */
    @Parameter( 4 )
    public boolean fetch;

    /**
     * Set patch flag.
     */
    @Parameter( 5 )
    public boolean patch;

    /**
     * Set ipatch flag.
     */
    @Parameter( 6 )
    public boolean ipatch;

    /**
     * @return the collection of test parameters.
     */
    @Parameters( name= "get= {0} ; post= {1} ; put= {2} ; delete= {3} ; fetch= {4} ; patch= {5} ; ipatch= {6}" )
    public static Collection< Object[] > data()
    {
        boolean[] values= { false, true };
        ArrayList< Object[] > parameterCollection= new ArrayList< Object[] >();
        for ( boolean get : values )
        {
            for ( boolean post : values )
            {
                for ( boolean put : values )
                {
                    for ( boolean delete : values )
                    {
                        for ( boolean fetch : values )
                        {
                            for ( boolean patch : values )
                            {
                                for ( boolean ipatch : values )
                                {
                                    parameterCollection.add( new Boolean []{ get, post, put, delete, fetch, patch, ipatch } );
                                }
                            }
                        }
                    }
                }
            }
        }
        return (Collection< Object[] >) parameterCollection;
    }

    @Test
    public void testConstructorAndGetters() throws InvalidEntityTagException
    {
        RequestCodeFlags flags= new RequestCodeFlags( get, post, put, delete, fetch, patch, ipatch );
        assertNotNull( "no object constructed", flags );

        assertEquals( "wrong get flag", get, flags.isGet() );
        assertEquals( "wrong post flag", post, flags.isPost() );
        assertEquals( "wrong put flag", put, flags.isPut() );
        assertEquals( "wrong delete flag", delete, flags.isDelete() );
        assertEquals( "wrong fetch flag", fetch, flags.isFetch() );
        assertEquals( "wrong patch flag", patch, flags.isPatch() );
        assertEquals( "wrong ipatch flag", ipatch, flags.isIpatch() );

        assertNotEquals( "wrong not get flag", get, flags.isNotGet() );
        assertNotEquals( "wrong not post flag", post, flags.isNotPost() );
        assertNotEquals( "wrong not put flag", put, flags.isNotPut() );
        assertNotEquals( "wrong not delete flag", delete, flags.isNotDelete() );
        assertNotEquals( "wrong not fetch flag", fetch, flags.isNotFetch() );
        assertNotEquals( "wrong not patch flag", patch, flags.isNotPatch() );
        assertNotEquals( "wrong not ipatch flag", ipatch, flags.isNotIpatch() );
    }

    @Test
    public void testCopyConstructor() throws InvalidEntityTagException
    {
        RequestCodeFlags originalFlags= new RequestCodeFlags( get, post, put, delete, fetch, patch, ipatch );
        assertNotNull( "no object constructed", originalFlags );

        RequestCodeFlags flags= new RequestCodeFlags( originalFlags );
        assertNotNull( "no object constructed", flags );

        assertEquals( "wrong get flag", get, flags.isGet() );
        assertEquals( "wrong post flag", post, flags.isPost() );
        assertEquals( "wrong put flag", put, flags.isPut() );
        assertEquals( "wrong delete flag", delete, flags.isDelete() );
        assertEquals( "wrong fetch flag", fetch, flags.isFetch() );
        assertEquals( "wrong patch flag", patch, flags.isPatch() );
        assertEquals( "wrong ipatch flag", ipatch, flags.isIpatch() );

        flags.setGet( !get );
        flags.setPost( !post );
        flags.setPut( !put );
        flags.setDelete( !delete );
        flags.setFetch( !fetch );
        flags.setPatch( !patch );
        flags.setIpatch( !ipatch );

        assertNotEquals( "wrongly changed get flag", get, flags.isGet() );
        assertNotEquals( "wrongly changed post flag", post, flags.isPost() );
        assertNotEquals( "wrongly changed put flag", put, flags.isPut() );
        assertNotEquals( "wrongly changed delete flag", delete, flags.isDelete() );
        assertNotEquals( "wrongly changed fetch flag", fetch, flags.isFetch() );
        assertNotEquals( "wrongly changed patch flag", patch, flags.isPatch() );
        assertNotEquals( "wrongly changed ipatch flag", ipatch, flags.isIpatch() );

        assertEquals( "wrongly changed get originalFlag", get, originalFlags.isGet() );
        assertEquals( "wrongly changed post originalFlag", post, originalFlags.isPost() );
        assertEquals( "wrongly changed put originalFlag", put, originalFlags.isPut() );
        assertEquals( "wrongly changed delete originalFlag", delete, originalFlags.isDelete() );
        assertEquals( "wrongly changed fetch originalFlag", fetch, originalFlags.isFetch() );
        assertEquals( "wrongly changed patch originalFlag", patch, originalFlags.isPatch() );
        assertEquals( "wrongly changed ipatch originalFlag", ipatch, originalFlags.isIpatch() );
    }

    @Test
    public void testCopyConstructorNull() throws InvalidEntityTagException
    {
        RequestCodeFlags originalFlags= null;
        assertNull( "should no object be constructed", originalFlags );

        RequestCodeFlags flags= new RequestCodeFlags( originalFlags );
        assertNotNull( "no object constructed", flags );

        assertEquals( "wrong get flag", false, flags.isGet() );
        assertEquals( "wrong post flag", false, flags.isPost() );
        assertEquals( "wrong put flag", false, flags.isPut() );
        assertEquals( "wrong delete flag", false, flags.isDelete() );
        assertEquals( "wrong fetch flag", false, flags.isFetch() );
        assertEquals( "wrong patch flag", false, flags.isPatch() );
        assertEquals( "wrong ipatch flag", false, flags.isIpatch() );
    }

    @Test
    public void testSetterFromDefault1() throws InvalidEntityTagException
    {
        RequestCodeFlags flags= new RequestCodeFlags();
        assertNotNull( "no object constructed", flags );

        assertEquals( "wrong get flag", false, flags.isGet() );
        assertEquals( "wrong post flag", false, flags.isPost() );
        assertEquals( "wrong put flag", false, flags.isPut() );
        assertEquals( "wrong delete flag", false, flags.isDelete() );
        assertEquals( "wrong fetch flag", false, flags.isFetch() );
        assertEquals( "wrong patch flag", false, flags.isPatch() );
        assertEquals( "wrong ipatch flag", false, flags.isIpatch() );

        if ( get ) flags.setGet( get );
        if ( post ) flags.setPost( post );
        if ( put ) flags.setPut( put );
        if ( delete ) flags.setDelete( delete );
        if ( fetch ) flags.setFetch( fetch );
        if ( patch ) flags.setPatch( patch );
        if ( ipatch ) flags.setIpatch( ipatch );

        assertEquals( "wrong get flag", get, flags.isGet() );
        assertEquals( "wrong post flag", post, flags.isPost() );
        assertEquals( "wrong put flag", put, flags.isPut() );
        assertEquals( "wrong delete flag", delete, flags.isDelete() );
        assertEquals( "wrong fetch flag", fetch, flags.isFetch() );
        assertEquals( "wrong patch flag", patch, flags.isPatch() );
        assertEquals( "wrong ipatch flag", ipatch, flags.isIpatch() );
    }

    @Test
    public void testSetterFromDefault2() throws InvalidEntityTagException
    {
        RequestCodeFlags flags= new RequestCodeFlags();
        assertNotNull( "no object constructed", flags );

        assertEquals( "wrong get flag", false, flags.isGet() );
        assertEquals( "wrong post flag", false, flags.isPost() );
        assertEquals( "wrong put flag", false, flags.isPut() );
        assertEquals( "wrong delete flag", false, flags.isDelete() );
        assertEquals( "wrong fetch flag", false, flags.isFetch() );
        assertEquals( "wrong patch flag", false, flags.isPatch() );
        assertEquals( "wrong ipatch flag", false, flags.isIpatch() );

        flags.setGet( get );
        flags.setPost( post );
        flags.setPut( put );
        flags.setDelete( delete );
        flags.setFetch( fetch );
        flags.setPatch( patch );
        flags.setIpatch( ipatch );

        assertEquals( "wrong get flag", get, flags.isGet() );
        assertEquals( "wrong post flag", post, flags.isPost() );
        assertEquals( "wrong put flag", put, flags.isPut() );
        assertEquals( "wrong delete flag", delete, flags.isDelete() );
        assertEquals( "wrong fetch flag", fetch, flags.isFetch() );
        assertEquals( "wrong patch flag", patch, flags.isPatch() );
        assertEquals( "wrong ipatch flag", ipatch, flags.isIpatch() );
    }

    @Test
    public void testSetter1() throws InvalidEntityTagException
    {
        RequestCodeFlags flags= new RequestCodeFlags( get, post, put, delete, fetch, patch, ipatch );
        assertNotNull( "no object constructed", flags );

        if ( get ) flags.setGet( get );
        if ( post ) flags.setPost( post );
        if ( put ) flags.setPut( put );
        if ( delete ) flags.setDelete( delete );
        if ( fetch ) flags.setFetch( fetch );
        if ( patch ) flags.setPatch( patch );
        if ( ipatch ) flags.setIpatch( ipatch );

        assertEquals( "wrong get flag", get, flags.isGet() );
        assertEquals( "wrong post flag", post, flags.isPost() );
        assertEquals( "wrong put flag", put, flags.isPut() );
        assertEquals( "wrong delete flag", delete, flags.isDelete() );
        assertEquals( "wrong fetch flag", fetch, flags.isFetch() );
        assertEquals( "wrong patch flag", patch, flags.isPatch() );
        assertEquals( "wrong ipatch flag", ipatch, flags.isIpatch() );
    }

    @Test
    public void testSetter2() throws InvalidEntityTagException
    {
        RequestCodeFlags flags= new RequestCodeFlags( get, post, put, delete, fetch, patch, ipatch );
        assertNotNull( "no object constructed", flags );

        flags.setGet( get );
        flags.setPost( post );
        flags.setPut( put );
        flags.setDelete( delete );
        flags.setFetch( fetch );
        flags.setPatch( patch );
        flags.setIpatch( ipatch );

        assertEquals( "wrong get flag", get, flags.isGet() );
        assertEquals( "wrong post flag", post, flags.isPost() );
        assertEquals( "wrong put flag", put, flags.isPut() );
        assertEquals( "wrong delete flag", delete, flags.isDelete() );
        assertEquals( "wrong fetch flag", fetch, flags.isFetch() );
        assertEquals( "wrong patch flag", patch, flags.isPatch() );
        assertEquals( "wrong ipatch flag", ipatch, flags.isIpatch() );
    }

    @Test
    public void testToString() throws InvalidEntityTagException
    {
        RequestCodeFlags flags= new RequestCodeFlags( get, post, put, delete, fetch, patch, ipatch );

        assertTrue( "flags.toString shows wrong get value", flags.toString().contains( get ? "GET" : "get" ) );
        assertTrue( "flags.toString shows wrong post value", flags.toString().contains( post ? "POST" : "post" ) );
        assertTrue( "flags.toString shows wrong put value", flags.toString().contains( put ? "PUT" : "put" ) );
        assertTrue( "flags.toString shows wrong delete value", flags.toString().contains( delete ? "DELETE" : "delete" ) );
        assertTrue( "flags.toString shows wrong fetch value", flags.toString().contains( fetch ? "FETCH" : "fetch" ) );
        assertTrue( "flags.toString shows wrong patch value", flags.toString().contains( patch ? "PATCH" : "patch" ) );
        assertTrue( "flags.toString shows wrong ipatch value", flags.toString().contains( ipatch ? "IPATCH" : "ipatch" ) );
    }

    @Test
    public void testEquals() throws InvalidEntityTagException
    {
        RequestCodeFlags flags= new RequestCodeFlags( get, post, put, delete, fetch, patch, ipatch );
        RequestCodeFlags flags0= new RequestCodeFlags( get, post, put, delete, fetch, patch, ipatch );
        RequestCodeFlags flags1= new RequestCodeFlags( !get, post, put, delete, fetch, patch, ipatch );
        RequestCodeFlags flags2= new RequestCodeFlags( get, !post, put, delete, fetch, patch, ipatch );
        RequestCodeFlags flags3= new RequestCodeFlags( get, post, !put, delete, fetch, patch, ipatch );
        RequestCodeFlags flags4= new RequestCodeFlags( get, post, put, !delete, fetch, patch, ipatch );
        RequestCodeFlags flags5= new RequestCodeFlags( get, post, put, delete, !fetch, patch, ipatch );
        RequestCodeFlags flags6= new RequestCodeFlags( get, post, put, delete, fetch, !patch, ipatch );
        RequestCodeFlags flags7= new RequestCodeFlags( get, post, put, delete, fetch, patch, !ipatch );

        assertEquals( "flags wrongfully unequal to flags0", flags, flags0 );

        assertNotEquals( "flags wrongfully equal to flags1", flags, flags1 );
        assertNotEquals( "flags wrongfully equal to flags2", flags, flags2 );
        assertNotEquals( "flags wrongfully equal to flags3", flags, flags3 );
        assertNotEquals( "flags wrongfully equal to flags4", flags, flags4 );
        assertNotEquals( "flags wrongfully equal to flags5", flags, flags5 );
        assertNotEquals( "flags wrongfully equal to flags6", flags, flags6 );
        assertNotEquals( "flags wrongfully equal to flags7", flags, flags7 );

        assertNotEquals( "flags wrongfully equal to null", flags, null );
    }

    @Test
    public void testEqualsToWrongClass() throws InvalidEntityTagException
    {
        RequestCodeFlags flags= new RequestCodeFlags( get, post, put, delete, fetch, patch, ipatch );
        assertNotEquals( "flags wrongfully equals other class", flags, Boolean.TRUE );
    }

    @Test
    public void testCompareTo() throws InvalidEntityTagException
    {
        RequestCodeFlags flags= new RequestCodeFlags( get, post, put, delete, fetch, patch, ipatch );
        RequestCodeFlags flags0= new RequestCodeFlags( get, post, put, delete, fetch, patch, ipatch );
        RequestCodeFlags flags1= new RequestCodeFlags( !get, post, put, delete, fetch, patch, ipatch );
        RequestCodeFlags flags2= new RequestCodeFlags( get, !post, put, delete, fetch, patch, ipatch );
        RequestCodeFlags flags3= new RequestCodeFlags( get, post, !put, delete, fetch, patch, ipatch );
        RequestCodeFlags flags4= new RequestCodeFlags( get, post, put, !delete, fetch, patch, ipatch );
        RequestCodeFlags flags5= new RequestCodeFlags( get, post, put, delete, !fetch, patch, ipatch );
        RequestCodeFlags flags6= new RequestCodeFlags( get, post, put, delete, fetch, !patch, ipatch );
        RequestCodeFlags flags7= new RequestCodeFlags( get, post, put, delete, fetch, patch, !ipatch );

        assertEquals( "flags wrongfully unequal to flags0", 0, flags.compareTo( flags0 ) );

        assertNotEquals( "flags wrongfully equal to flags1", 0, flags.compareTo( flags1 ) );
        assertNotEquals( "flags wrongfully equal to flags2", 0, flags.compareTo( flags2 ) );
        assertNotEquals( "flags wrongfully equal to flags3", 0, flags.compareTo( flags3 ) );
        assertNotEquals( "flags wrongfully equal to flags4", 0, flags.compareTo( flags4 ) );
        assertNotEquals( "flags wrongfully equal to flags5", 0, flags.compareTo( flags5 ) );
        assertNotEquals( "flags wrongfully equal to flags6", 0, flags.compareTo( flags6 ) );
        assertNotEquals( "flags wrongfully equal to flags7", 0, flags.compareTo( flags7 ) );

        assertEquals( "flags wrongfully equal to null", 1, flags.compareTo( null ) );
    }

    @Test
    public void testHashCode() throws InvalidEntityTagException
    {
        RequestCodeFlags flags= new RequestCodeFlags( get, post, put, delete, fetch, patch, ipatch );
        RequestCodeFlags flags0= new RequestCodeFlags( get, post, put, delete, fetch, patch, ipatch );
        RequestCodeFlags flags1= new RequestCodeFlags( !get, post, put, delete, fetch, patch, ipatch );
        RequestCodeFlags flags2= new RequestCodeFlags( get, !post, put, delete, fetch, patch, ipatch );
        RequestCodeFlags flags3= new RequestCodeFlags( get, post, !put, delete, fetch, patch, ipatch );
        RequestCodeFlags flags4= new RequestCodeFlags( get, post, put, !delete, fetch, patch, ipatch );
        RequestCodeFlags flags5= new RequestCodeFlags( get, post, put, delete, !fetch, patch, ipatch );
        RequestCodeFlags flags6= new RequestCodeFlags( get, post, put, delete, fetch, !patch, ipatch );
        RequestCodeFlags flags7= new RequestCodeFlags( get, post, put, delete, fetch, patch, !ipatch );

        assertEquals( "flags hashcode wrongfully unequal to flags0", flags.hashCode(), flags0.hashCode() );

        assertNotEquals( "flags hashcode wrongfully equal to flags1", flags.hashCode(), flags1.hashCode() );
        assertNotEquals( "flags hashcode wrongfully equal to flags2", flags.hashCode(), flags2.hashCode() );
        assertNotEquals( "flags hashcode wrongfully equal to flags3", flags.hashCode(), flags3.hashCode() );
        assertNotEquals( "flags hashcode wrongfully equal to flags4", flags.hashCode(), flags4.hashCode() );
        assertNotEquals( "flags hashcode wrongfully equal to flags5", flags.hashCode(), flags5.hashCode() );
        assertNotEquals( "flags hashcode wrongfully equal to flags6", flags.hashCode(), flags6.hashCode() );
        assertNotEquals( "flags hashcode wrongfully equal to flags7", flags.hashCode(), flags7.hashCode() );

        assertNotEquals( "flags hashcode wrongfully equal to null", flags.hashCode(), null );
    }
}
