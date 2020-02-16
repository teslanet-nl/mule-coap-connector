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
package nl.teslanet.mule.connectors.coap.test.server.discovery;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractServerTestCase;
import nl.teslanet.shaded.org.eclipse.californium.core.WebLink;


public class DynamicResourcesTest extends AbstractServerTestCase
{
    HashMap< String, WebLink > links= null;

    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/discovery/testserver1.xml";
    };

    @Before
    public void additionalSetUp()
    {
        setClientPath( "/service/add_resources" );
        try
        {
            //post to create resource on server
            client.post( "nothing important", 0 );
            Set< WebLink > response= client.discover();
            links= new HashMap< String, WebLink >();
            for ( WebLink link : response )
            {
                links.put( link.getURI(), link );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }

    }

    @After
    public void additionalTearDown()
    {
        links.clear();
        links= null;
    }

    @Test
    public void testWellKnownCore()
    {
        assertEquals( "wrong number of weblinks", 15, links.size() );
        WebLink link= links.get( "/.well-known/core" );
        assertNotNull( "/.well-known/core is missing", link );
    }

    @Test
    public void testCt()
    {
        WebLink link= links.get( "/service/resource2_with_ct" );
        assertNotNull( "/service/resource2_with_ct is missing", link );
        List< String > ct= link.getAttributes().getContentTypes();
        assertEquals( "wrong number ct", 2, ct.size() );
        assertTrue( "ct does not contain 0", ct.contains( "0" ) );
        assertTrue( "ct does not contain 41", ct.contains( "41" ) );
    }

    @Test
    public void testIf()
    {
        WebLink link= links.get( "/service/resource2_with_if" );
        assertNotNull( "/service/resource2_with_if is missing", link );
        List< String > ifdesc= link.getAttributes().getInterfaceDescriptions();
        assertEquals( "wrong number of ifdesc", 2, ifdesc.size() );
        assertTrue( "ifdesc does not contain 0", ifdesc.contains( "if1" ) );
        assertTrue( "ifdesc does not contain 41", ifdesc.contains( "if2" ) );
    }

    @Test
    public void testObs()
    {
        WebLink link= links.get( "/service/resource2_with_obs" );
        assertNotNull( "/service/resource2_with_obs is missing", link );
        boolean obs= link.getAttributes().hasObservable();
        assertTrue( "obs not true", obs );
    }

    @Test
    public void testRt()
    {
        WebLink link= links.get( "/service/resource2_with_rt" );
        assertNotNull( "/service/resource2_with_rt is missing", link );
        List< String > rt= link.getAttributes().getResourceTypes();
        assertEquals( "wrong number of rt", 2, rt.size() );
        assertTrue( "rt does not contain rt1", rt.contains( "rt1" ) );
        assertTrue( "rt does not contain rt1", rt.contains( "rt2" ) );
    }

    @Test
    public void testSz()
    {
        WebLink link= links.get( "/service/resource2_with_sz" );
        assertNotNull( "/service/resource2_with_sz is missing", link );
        String sz= link.getAttributes().getMaximumSizeEstimate();
        assertEquals( "sz has wrong value", "123456", sz );
    }

    @Test
    public void testTitle()
    {
        WebLink link= links.get( "/service/resource2_with_title" );
        assertNotNull( "/service/resource2_with_title is missing", link );
        String title= link.getAttributes().getTitle();
        assertEquals( "title has wrong value", "another resource with a title", title );
    }
}
