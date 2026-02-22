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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.api.ResourceConfig;
import nl.teslanet.mule.connectors.coap.api.error.InvalidResourceUriException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUriPatternException;
import nl.teslanet.mule.connectors.coap.internal.server.AbstractResource;
import nl.teslanet.mule.connectors.coap.internal.server.OperationalServer;
import nl.teslanet.mule.connectors.coap.internal.server.ServedResource;
import nl.teslanet.mule.connectors.coap.internal.server.VirtualResource;


/**
 * Test the static methods of the resource registry.
 */
public class ResourceRegistryStaticMethodsTest
{
    String uri0= "";

    AbstractResource resource0;

    String uri00= "/";

    AbstractResource resource00;

    String uri1= "/resource1";

    AbstractResource resource1;

    String uri2= "/resource1/resource2";

    AbstractResource resource2;

    String uri3= "/resource1/resource2/resource3";

    AbstractResource resource3;

    String uri4= "/resource1/resource4";

    AbstractResource resource4;

    String uri40= "/resource1/resource4/";

    AbstractResource resource40;

    String name0= "";

    String name00= "";

    String name1= "resource1";

    String name2= "resource2";

    String name3= "resource3";

    String name4= "resource4";

    String name44= "";

    String parenturi0= "";

    String parenturi00= "";

    String parenturi1= "";

    String parenturi2= "/resource1";

    String parenturi3= "/resource1/resource2";

    String parenturi4= "/resource1";

    String parenturi40= "/resource1";

    String uripath0= "";

    String uripath00= "";

    String uripath1= "/";

    String uripath2= "/resource1/";

    String uripath3= "/resource1/resource2/";

    String uripath4= "/resource1/";

    String uripath40= "/resource1/";

    String pattern0= "*";

    String pattern1= "/*";

    String pattern2= "/resource1/*";

    String pattern3= "/resource1/resource2/*";

    String pattern4= "/resource1/*";

    //actually not supported:
    String pattern5= "/*/resource4";

    String pattern6= "/resource1/resource*";

    @Before
    public void setup()
    {
        ResourceConfig config= new ResourceConfig();

        config.setResourceName( name0 );
        resource0= new ServedResource( config );

        config.setResourceName( name00 );
        resource00= new VirtualResource( false );
        resource0.add( resource00 );

        config.setResourceName( name1 );
        resource1= new ServedResource( config );
        resource0.add( resource1 );

        config.setResourceName( name2 );
        resource2= new ServedResource( config );
        resource1.add( resource2 );

        config.setResourceName( name3 );
        resource3= new ServedResource( config );
        resource2.add( resource3 );

        config.setResourceName( name4 );
        resource4= new ServedResource( config );
        resource1.add( resource4 );

        resource40= new VirtualResource( false );
        resource4.add( resource40 );

    }

    @Test
    public void testMaxMatchUri() throws InvalidResourceUriException, InternalUriPatternException
    {
        assertEquals( "wrong match against pattern: " + uri0 + " on uri: " + uri0, Integer.MAX_VALUE, OperationalServer.matchUri( uri0, resource0 ) );
        assertEquals( "wrong match against pattern: " + uri0 + " on uri: " + uri0, -1, OperationalServer.matchUri( uri00, resource0 ) );
        assertEquals( "wrong match against pattern: " + uri0 + " on uri: " + uri00, -1, OperationalServer.matchUri( uri0, resource00 ) );
        assertEquals( "wrong match against pattern: " + uri00 + " on uri: " + uri00, Integer.MAX_VALUE, OperationalServer.matchUri( uri00, resource00 ) );
        assertEquals( "wrong match against pattern: " + uri1 + " on uri: " + uri1, Integer.MAX_VALUE, OperationalServer.matchUri( uri1, resource1 ) );
        assertEquals( "wrong match against pattern: " + uri2 + " on uri: " + uri2, Integer.MAX_VALUE, OperationalServer.matchUri( uri2, resource2 ) );
        assertEquals( "wrong match against pattern: " + uri3 + " on uri: " + uri3, Integer.MAX_VALUE, OperationalServer.matchUri( uri3, resource3 ) );
        assertEquals( "wrong match against pattern: " + uri4 + " on uri: " + uri4, Integer.MAX_VALUE, OperationalServer.matchUri( uri4, resource4 ) );
        assertEquals( "wrong match against pattern: " + uri40 + " on uri: " + uri40, Integer.MAX_VALUE, OperationalServer.matchUri( uri40, resource40 ) );
        assertEquals( "wrong match against pattern: " + uri40 + " on uri: " + uri4, -1, OperationalServer.matchUri( uri40, resource4 ) );
        assertEquals( "wrong match against pattern: " + uri4 + " on uri: " + uri40, -1, OperationalServer.matchUri( uri4, resource40 ) );
    }

    @Test
    public void testMatchUriPattern() throws InvalidResourceUriException, InternalUriPatternException
    {
        assertEquals( "wrong match against pattern: " + pattern0 + " on uri: " + uri0, 0, OperationalServer.matchUri( pattern0, resource0 ) );
        assertEquals( "wrong match against pattern: " + pattern0 + " on uri: " + uri00, 0, OperationalServer.matchUri( pattern0, resource00 ) );
        assertEquals( "wrong match against pattern: " + pattern0 + " on uri: " + uri1, 0, OperationalServer.matchUri( pattern0, resource1 ) );
        assertEquals( "wrong match against pattern: " + pattern0 + " on uri: " + uri2, 0, OperationalServer.matchUri( pattern0, resource2 ) );
        assertEquals( "wrong match against pattern: " + pattern0 + " on uri: " + uri3, 0, OperationalServer.matchUri( pattern0, resource3 ) );
        assertEquals( "wrong match against pattern: " + pattern0 + " on uri: " + uri4, 0, OperationalServer.matchUri( pattern0, resource4 ) );

        assertEquals( "wrong match against pattern: " + pattern1 + " on uri: " + uri0, -1, OperationalServer.matchUri( pattern1, resource0 ) );
        assertEquals( "wrong match against pattern: " + pattern1 + " on uri: " + uri00, 1, OperationalServer.matchUri( pattern1, resource00 ) );
        assertEquals( "wrong match against pattern: " + pattern1 + " on uri: " + uri1, 1, OperationalServer.matchUri( pattern1, resource1 ) );
        assertEquals( "wrong match against pattern: " + pattern1 + " on uri: " + uri1, 1, OperationalServer.matchUri( pattern1, resource1 ) );
        assertEquals( "wrong match against pattern: " + pattern1 + " on uri: " + uri2, 1, OperationalServer.matchUri( pattern1, resource2 ) );
        assertEquals( "wrong match against pattern: " + pattern1 + " on uri: " + uri3, 1, OperationalServer.matchUri( pattern1, resource3 ) );
        assertEquals( "wrong match against pattern: " + pattern1 + " on uri: " + uri4, 1, OperationalServer.matchUri( pattern1, resource4 ) );

        assertEquals( "wrong match against pattern: " + pattern2 + " on uri: " + uri0, -1, OperationalServer.matchUri( pattern2, resource0 ) );
        assertEquals( "wrong match against pattern: " + pattern2 + " on uri: " + uri00, -1, OperationalServer.matchUri( pattern2, resource00 ) );
        assertEquals( "wrong match against pattern: " + pattern2 + " on uri: " + uri1, -1, OperationalServer.matchUri( pattern2, resource1 ) );
        assertEquals( "wrong match against pattern: " + pattern2 + " on uri: " + uri2, 2, OperationalServer.matchUri( pattern2, resource2 ) );
        assertEquals( "wrong match against pattern: " + pattern2 + " on uri: " + uri3, 2, OperationalServer.matchUri( pattern2, resource3 ) );
        assertEquals( "wrong match against pattern: " + pattern2 + " on uri: " + uri4, 2, OperationalServer.matchUri( pattern2, resource4 ) );

        assertEquals( "wrong match against pattern: " + pattern3 + " on uri: " + uri0, -1, OperationalServer.matchUri( pattern3, resource0 ) );
        assertEquals( "wrong match against pattern: " + pattern3 + " on uri: " + uri00, -1, OperationalServer.matchUri( pattern3, resource00 ) );
        assertEquals( "wrong match against pattern: " + pattern3 + " on uri: " + uri1, -1, OperationalServer.matchUri( pattern3, resource1 ) );
        assertEquals( "wrong match against pattern: " + pattern3 + " on uri: " + uri2, -1, OperationalServer.matchUri( pattern3, resource2 ) );
        assertEquals( "wrong match against pattern: " + pattern3 + " on uri: " + uri3, 3, OperationalServer.matchUri( pattern3, resource3 ) );
        assertEquals( "wrong match against pattern: " + pattern3 + " on uri: " + uri4, -1, OperationalServer.matchUri( pattern3, resource4 ) );

        assertEquals( "wrong match against pattern: " + pattern4 + " on uri: " + uri0, -1, OperationalServer.matchUri( pattern4, resource0 ) );
        assertEquals( "wrong match against pattern: " + pattern4 + " on uri: " + uri00, -1, OperationalServer.matchUri( pattern4, resource00 ) );
        assertEquals( "wrong match against pattern: " + pattern4 + " on uri: " + uri1, -1, OperationalServer.matchUri( pattern4, resource1 ) );
        assertEquals( "wrong match against pattern: " + pattern4 + " on uri: " + uri2, 2, OperationalServer.matchUri( pattern4, resource2 ) );
        assertEquals( "wrong match against pattern: " + pattern4 + " on uri: " + uri3, 2, OperationalServer.matchUri( pattern4, resource3 ) );
        assertEquals( "wrong match against pattern: " + pattern4 + " on uri: " + uri4, 2, OperationalServer.matchUri( pattern4, resource4 ) );
    }

    @Test
    public void testMatchUriPatternException() throws InvalidResourceUriException, InternalUriPatternException
    {

        InternalUriPatternException e1= assertThrows(
            "No exception from pattern: " + pattern5,
            InternalUriPatternException.class,
            () -> OperationalServer.matchUri( pattern5, resource4 )
        );

        InternalUriPatternException e2= assertThrows(
            "No exception from pattern: " + pattern6,
            InternalUriPatternException.class,
            () -> OperationalServer.matchUri( pattern6, resource4 )
        );
        assertEquals( "Wrong exceptionmessage from pattern: " + pattern5, "Wildcard is only allowed on pattern ending. { /*/resource4 }", e1.getMessage() );
        assertEquals( "Wrong exceptionmessage from pattern: " + pattern6, "Wildcard is only allowed on whole resource name. { /resource1/resource* }", e2.getMessage() );
    }

    @Test
    public void testGetUriDepth() throws InvalidResourceUriException
    {
        assertEquals( "got wrong depth from uri: " + uri0, 0, OperationalServer.getUriDepth( uri0 ) );
        assertEquals( "got wrong depth from uri: " + uri00, 0, OperationalServer.getUriDepth( uri00 ) );
        assertEquals( "got wrong depth from uri: " + uri1, 1, OperationalServer.getUriDepth( uri1 ) );
        assertEquals( "got wrong depth from uri: " + uri2, 2, OperationalServer.getUriDepth( uri2 ) );
        assertEquals( "got wrong depth from uri: " + uri3, 3, OperationalServer.getUriDepth( uri3 ) );
        assertEquals( "got wrong depth from uri: " + uri4, 2, OperationalServer.getUriDepth( uri4 ) );
        assertEquals( "got wrong depth from pattern: " + pattern0, 0, OperationalServer.getUriDepth( pattern0 ) );
        assertEquals( "got wrong depth from pattern: " + pattern1, 1, OperationalServer.getUriDepth( pattern1 ) );
        assertEquals( "got wrong depth from pattern: " + pattern2, 2, OperationalServer.getUriDepth( pattern2 ) );
        assertEquals( "got wrong depth from pattern: " + pattern3, 3, OperationalServer.getUriDepth( pattern3 ) );
        assertEquals( "got wrong depth from pattern: " + pattern4, 2, OperationalServer.getUriDepth( pattern4 ) );
    }

    @Test
    public void testGetUriPath() throws InvalidResourceUriException
    {
        assertEquals( "got wrong path from uri: " + uri0, uripath0, OperationalServer.getUriPath( uri0 ) );
        assertEquals( "got wrong path from uri: " + uri00, uripath00, OperationalServer.getUriPath( uri00 ) );
        assertEquals( "got wrong path from uri: " + uri1, uripath1, OperationalServer.getUriPath( uri1 ) );
        assertEquals( "got wrong path from uri: " + uri2, uripath2, OperationalServer.getUriPath( uri2 ) );
        assertEquals( "got wrong path from uri: " + uri3, uripath3, OperationalServer.getUriPath( uri3 ) );
        assertEquals( "got wrong path from uri: " + uri4, uripath4, OperationalServer.getUriPath( uri4 ) );
        assertEquals( "got wrong path from pattern: " + pattern0, uripath0, OperationalServer.getUriPath( pattern0 ) );
        assertEquals( "got wrong path from pattern: " + pattern1, uripath1, OperationalServer.getUriPath( pattern1 ) );
        assertEquals( "got wrong path from pattern: " + pattern2, uripath2, OperationalServer.getUriPath( pattern2 ) );
        assertEquals( "got wrong path from pattern: " + pattern3, uripath3, OperationalServer.getUriPath( pattern3 ) );
        assertEquals( "got wrong path from pattern: " + pattern4, uripath4, OperationalServer.getUriPath( pattern4 ) );
    }

    @Test
    public void testGetUriResourceName() throws InvalidResourceUriException
    {
        assertEquals( "got wrong resourcename from uri: " + uri0, name0, OperationalServer.getUriResourceName( uri0 ) );
        assertEquals( "got wrong resourcename from uri: " + uri0, name0, OperationalServer.getUriResourceName( uri00 ) );
        assertEquals( "got wrong resourcename from uri: " + uri1, name1, OperationalServer.getUriResourceName( uri1 ) );
        assertEquals( "got wrong resourcename from uri: " + uri2, name2, OperationalServer.getUriResourceName( uri2 ) );
        assertEquals( "got wrong resourcename from uri: " + uri3, name3, OperationalServer.getUriResourceName( uri3 ) );
        assertEquals( "got wrong resourcename from uri: " + uri4, name4, OperationalServer.getUriResourceName( uri4 ) );
    }

    @Test
    public void testUriHasWildcard() throws InvalidResourceUriException, InternalUriPatternException
    {
        assertTrue( "got wrong wildcard flag from pattern: " + pattern0, OperationalServer.uriHasWildcard( pattern0 ) );
        assertTrue( "got wrong wildcard flag from pattern: " + pattern1, OperationalServer.uriHasWildcard( pattern1 ) );
        assertTrue( "got wrong wildcard flag from pattern: " + pattern2, OperationalServer.uriHasWildcard( pattern2 ) );
        assertTrue( "got wrong wildcard flag from pattern: " + pattern3, OperationalServer.uriHasWildcard( pattern3 ) );
        assertTrue( "got wrong wildcard flag from pattern: " + pattern4, OperationalServer.uriHasWildcard( pattern4 ) );

        InternalUriPatternException e1= assertThrows( "No exception from pattern: " + pattern5, InternalUriPatternException.class, () -> {
            OperationalServer.uriHasWildcard( pattern5 );
        } );
        assertEquals( "Wrong exceptionmessage from uri: " + pattern5, "Wildcard is only allowed on pattern ending. { /*/resource4 }", e1.getMessage() );

        InternalUriPatternException e2= assertThrows( "No exception from pattern: " + pattern6, InternalUriPatternException.class, () -> {
            OperationalServer.uriHasWildcard( pattern6 );
        } );
        assertEquals( "Wrong exceptionmessage from uri: " + pattern5, "Wildcard is only allowed on whole resource name. { /resource1/resource* }", e2.getMessage() );

        assertFalse( "got wrong wildcard flag from uri: " + uri0, OperationalServer.uriHasWildcard( uri0 ) );
        assertFalse( "got wrong wildcard flag from uri: " + uri0, OperationalServer.uriHasWildcard( uri00 ) );
        assertFalse( "got wrong wildcard flag from uri: " + uri1, OperationalServer.uriHasWildcard( uri1 ) );
        assertFalse( "got wrong wildcard flag from uri: " + uri2, OperationalServer.uriHasWildcard( uri2 ) );
        assertFalse( "got wrong wildcard flag from uri: " + uri3, OperationalServer.uriHasWildcard( uri3 ) );
        assertFalse( "got wrong wildcard flag from uri: " + uri4, OperationalServer.uriHasWildcard( uri4 ) );
    }

    @Test
    public void testGetParentUri() throws InvalidResourceUriException
    {
        assertEquals( "got wrong parent from uri: " + uri0, parenturi0, OperationalServer.getParentUri( uri0 ) );
        assertEquals( "got wrong parent from uri: " + uri0, parenturi00, OperationalServer.getParentUri( uri00 ) );
        assertEquals( "got wrong parent from uri: " + uri1, parenturi1, OperationalServer.getParentUri( uri1 ) );
        assertEquals( "got wrong parent from uri: " + uri2, parenturi2, OperationalServer.getParentUri( uri2 ) );
        assertEquals( "got wrong parent from uri: " + uri3, parenturi3, OperationalServer.getParentUri( uri3 ) );
        assertEquals( "got wrong parent from uri: " + uri4, parenturi4, OperationalServer.getParentUri( uri4 ) );
    }
}
