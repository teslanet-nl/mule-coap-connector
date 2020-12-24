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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.api.error.InvalidResourceUriException;
import nl.teslanet.mule.connectors.coap.internal.server.ResourceRegistry;


/**
 * Test the static methods of the resource registry.
 */
public class ResourceRegistryStaticMethodsTest
{
    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {

    }

    @Test
    public void testMatchUri() throws InvalidResourceUriException
    {
        String uri1= "/resource1";
        String uri2= "/resource1/resource2";
        String uri3= "/resource1/resource2/resource3";
        String uri4= "/resource1/resource4";

        String pattern1= "/*";
        String pattern2= "/resource1/*";
        String pattern3= "/resource1/resource2/*";
        String pattern4= "/resource1/resource4/*";

        assertEquals( "wrong match against pattern: " + uri1 + " on uri: " + uri1, Integer.MAX_VALUE, ResourceRegistry.matchUri( uri1, uri1 ) );
        assertEquals( "wrong match against pattern: " + uri2 + " on uri: " + uri2, Integer.MAX_VALUE, ResourceRegistry.matchUri( uri2, uri2 ) );
        assertEquals( "wrong match against pattern: " + uri3 + " on uri: " + uri3, Integer.MAX_VALUE, ResourceRegistry.matchUri( uri3, uri3 ) );
        assertEquals( "wrong match against pattern: " + uri4 + " on uri: " + uri4, Integer.MAX_VALUE, ResourceRegistry.matchUri( uri4, uri4 ) );

        assertEquals( "wrong match against pattern: " + pattern1 + " on uri: " + uri1, 1, ResourceRegistry.matchUri( pattern1, uri1 ) );
        assertEquals( "wrong match against pattern: " + pattern1 + " on uri: " + uri2, 1, ResourceRegistry.matchUri( pattern1, uri2 ) );
        assertEquals( "wrong match against pattern: " + pattern1 + " on uri: " + uri3, 1, ResourceRegistry.matchUri( pattern1, uri3 ) );
        assertEquals( "wrong match against pattern: " + pattern1 + " on uri: " + uri4, 1, ResourceRegistry.matchUri( pattern1, uri4 ) );

        assertEquals( "wrong match against pattern: " + pattern2 + " on uri: " + uri1, 0, ResourceRegistry.matchUri( pattern2, uri1 ) );
        assertEquals( "wrong match against pattern: " + pattern2 + " on uri: " + uri2, 2, ResourceRegistry.matchUri( pattern2, uri2 ) );
        assertEquals( "wrong match against pattern: " + pattern2 + " on uri: " + uri3, 2, ResourceRegistry.matchUri( pattern2, uri3 ) );
        assertEquals( "wrong match against pattern: " + pattern2 + " on uri: " + uri4, 2, ResourceRegistry.matchUri( pattern2, uri4 ) );

        assertEquals( "wrong match against pattern: " + pattern3 + " on uri: " + uri1, 0, ResourceRegistry.matchUri( pattern3, uri1 ) );
        assertEquals( "wrong match against pattern: " + pattern3 + " on uri: " + uri2, 0, ResourceRegistry.matchUri( pattern3, uri2 ) );
        assertEquals( "wrong match against pattern: " + pattern3 + " on uri: " + uri3, 3, ResourceRegistry.matchUri( pattern3, uri3 ) );
        assertEquals( "wrong match against pattern: " + pattern3 + " on uri: " + uri4, 0, ResourceRegistry.matchUri( pattern3, uri4 ) );

        assertEquals( "wrong match against pattern: " + pattern4 + " on uri: " + uri1, 0, ResourceRegistry.matchUri( pattern4, uri1 ) );
        assertEquals( "wrong match against pattern: " + pattern4 + " on uri: " + uri2, 0, ResourceRegistry.matchUri( pattern4, uri2 ) );
        assertEquals( "wrong match against pattern: " + pattern4 + " on uri: " + uri3, 0, ResourceRegistry.matchUri( pattern4, uri3 ) );
        assertEquals( "wrong match against pattern: " + pattern4 + " on uri: " + uri4, 0, ResourceRegistry.matchUri( pattern4, uri4 ) );
    }

    @Test
    public void testGetUriDepth() throws InvalidResourceUriException
    {
        String uri1= "/resource1";
        String uri2= "/resource1/resource2";
        String uri3= "/resource1/resource2/resource3";
        String uri4= "/resource1/resource4";

        assertEquals( "got wrong depth from uri: " + uri1, 1, ResourceRegistry.getUriDepth( uri1 ) );
        assertEquals( "got wrong depth from uri: " + uri2, 2, ResourceRegistry.getUriDepth( uri2 ) );
        assertEquals( "got wrong depth from uri: " + uri3, 3, ResourceRegistry.getUriDepth( uri3 ) );
        assertEquals( "got wrong depth from uri: " + uri4, 2, ResourceRegistry.getUriDepth( uri4 ) );
    }

    @Test
    public void testGetUriPath() throws InvalidResourceUriException
    {
        String uri1= "/resource1";
        String uri2= "/resource1/resource2";
        String uri3= "/resource1/resource2/resource3";
        String uri4= "/resource1/resource4";
        String uripath1= "/";
        String uripath2= "/resource1/";
        String uripath3= "/resource1/resource2/";
        String uripath4= "/resource1/";

        assertEquals( "got wrong path from uri: " + uri1, uripath1, ResourceRegistry.getUriPath( uri1 ) );
        assertEquals( "got wrong path from uri: " + uri2, uripath2, ResourceRegistry.getUriPath( uri2 ) );
        assertEquals( "got wrong path from uri: " + uri3, uripath3, ResourceRegistry.getUriPath( uri3 ) );
        assertEquals( "got wrong path from uri: " + uri4, uripath4, ResourceRegistry.getUriPath( uri4 ) );
    }

    @Test
    public void testGetUriResourceName() throws InvalidResourceUriException
    {
        String uri1= "/resource1";
        String uri2= "/resource1/resource2";
        String uri3= "/resource1/resource2/resource3";
        String uri4= "/resource1/resource4";
        String name1= "resource1";
        String name2= "resource2";
        String name3= "resource3";
        String name4= "resource4";

        assertEquals( "got wrong resourcename from uri: " + uri1, name1, ResourceRegistry.getUriResourceName( uri1 ) );
        assertEquals( "got wrong resourcename from uri: " + uri2, name2, ResourceRegistry.getUriResourceName( uri2 ) );
        assertEquals( "got wrong resourcename from uri: " + uri3, name3, ResourceRegistry.getUriResourceName( uri3 ) );
        assertEquals( "got wrong resourcename from uri: " + uri4, name4, ResourceRegistry.getUriResourceName( uri4 ) );
    }

    @Test
    public void testUriHasWildcard() throws InvalidResourceUriException
    {
        String uri1= "/*";
        String uri2= "/resource1/*";
        String uri3= "/resource1/resource2/*";
        //actually not supported:
        String uri4= "/*/resource4";
        String uri5= "/resource1";
        String uri6= "/resource1/resource2";
        String uri7= "/resource1/resource2/resource3";
        String uri8= "/resource1/resource4";

        assertTrue( "got wrong wildcard flag from uri: " + uri1, ResourceRegistry.uriHasWildcard( uri1 ) );
        assertTrue( "got wrong wildcard flag from uri: " + uri2, ResourceRegistry.uriHasWildcard( uri2 ) );
        assertTrue( "got wrong wildcard flag from uri: " + uri3, ResourceRegistry.uriHasWildcard( uri3 ) );
        assertFalse( "got wrong wildcard flag from uri: " + uri4, ResourceRegistry.uriHasWildcard( uri4 ) );

        assertFalse( "got wrong wildcard flag from uri: " + uri5, ResourceRegistry.uriHasWildcard( uri5 ) );
        assertFalse( "got wrong wildcard flag from uri: " + uri6, ResourceRegistry.uriHasWildcard( uri6 ) );
        assertFalse( "got wrong wildcard flag from uri: " + uri7, ResourceRegistry.uriHasWildcard( uri7 ) );
        assertFalse( "got wrong wildcard flag from uri: " + uri8, ResourceRegistry.uriHasWildcard( uri8 ) );
    }

    @Test
    public void testGetParentUri() throws InvalidResourceUriException
    {
        String uri1= "/resource1";
        String uri2= "/resource1/resource2";
        String uri3= "/resource1/resource2/resource3";
        String uri4= "/resource1/resource4";
        String parenturi1= "";
        String parenturi2= "/resource1";
        String parenturi3= "/resource1/resource2";
        String parenturi4= "/resource1";

        assertEquals( "got wrong parent from uri: " + uri1, parenturi1, ResourceRegistry.getParentUri( uri1 ) );
        assertEquals( "got wrong parent from uri: " + uri2, parenturi2, ResourceRegistry.getParentUri( uri2 ) );
        assertEquals( "got wrong parent from uri: " + uri3, parenturi3, ResourceRegistry.getParentUri( uri3 ) );
        assertEquals( "got wrong parent from uri: " + uri4, parenturi4, ResourceRegistry.getParentUri( uri4 ) );
    }
}
