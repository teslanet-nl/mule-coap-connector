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
package nl.teslanet.mule.connectors.coap.test.client.discovery;


//TODO query string
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Set;

import org.junit.Test;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.TypedValue;

import nl.teslanet.mule.connectors.coap.api.DiscoveredResource;
import nl.teslanet.mule.connectors.coap.api.ReceivedResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.error.UriException;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;


public class DiscoveryTest extends AbstractClientTestCase
{
    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-client-config/discovery/testclient1.xml";
    };

    /**
     * create the server
     * @throws Exception when server cannot be created
     */
    @Override
    protected CoapServer getTestServer() throws Exception
    {
        return new TestServer();
    }

    private HashMap< String, DiscoveredResource > linkMap( Set< DiscoveredResource > set )
    {
        HashMap< String, DiscoveredResource > map= new HashMap< String, DiscoveredResource >();
        for ( DiscoveredResource link : set )
        {
            map.put( link.getPath(), link );
        }
        return map;
    }

    /**
     * Test ping
     * @throws Exception should not happen in this test
     */
    @Test
    public void testPing() throws Exception
    {
        String flowName= "ping_ok";
        Boolean expectedPayload= Boolean.TRUE;

        Event result= flowRunner( flowName ).withPayload( "nothing_important" ).run();
        Message response= result.getMessage();
        assertTrue( "wrong response class", DataType.BOOLEAN.isCompatibleWith( response.getPayload().getDataType() ) );
        assertEquals( "wrong response payload", expectedPayload, (Boolean) response.getPayload().getValue() );
    }

    /**
     * Test ping on server not listening
     * @throws Exception should not happen in this test
     */
    @Test
    public void testPingNOK() throws Exception
    {
        String flowName= "ping_nok";
        Boolean expectedPayload= Boolean.FALSE;

        Event result= flowRunner( flowName ).withPayload( "nothing_important" ).run();
        Message response= result.getMessage();
        assertTrue( "wrong response class", DataType.BOOLEAN.isCompatibleWith( response.getPayload().getDataType() ) );
        assertEquals( "wrong response payload", expectedPayload, (Boolean) response.getPayload().getValue() );
    }

    /**
     * Test ping on server does not exist
     * @throws Exception should not happen in this test
     */
    @Test
    public void testPingNoResolvableHost() throws Exception
    {
        String flowName= "ping_notresolvable";

        Exception e= assertThrows(
            Exception.class,
            () -> flowRunner( flowName ).withPayload( "nothing_important" ).run() );
        assertTrue(
            "wrong exception message",
            e.getMessage().contains( "ping failed" ) );
        assertEquals( "wrong exception cause", e.getCause().getClass(), UriException.class );
    }

    /**
     * Test Ping on dynamicly set host and port
     * @throws Exception should not happen in this test
     */
    @Test
    public void testDynamicUriPing() throws Exception
    {
        String flowName= "ping_dynamic";
        String host= "127.0.0.1";
        String port= Integer.toString( CoAP.DEFAULT_COAP_PORT );
        String path= "/service";
        Boolean expectedPayload= Boolean.TRUE;

        Event result= flowRunner( flowName ).withVariable( "host", host ).withVariable( "port", port ).withVariable( "path", path ).withPayload( "nothing_important" ).run();
        Message response= result.getMessage();
        assertTrue( "wrong response class", DataType.BOOLEAN.isCompatibleWith( response.getPayload().getDataType() ) );
        assertEquals( "wrong response payload", expectedPayload, (Boolean) response.getPayload().getValue() );
    }

    /**
     * Test Ping on dynamicly set host and port of not listening server
     * @throws Exception should not happen in this test
     */
    @Test
    public void testDynamicUriPingNOK() throws Exception
    {
        String flowName= "ping_dynamic";
        String host= "127.0.0.1";
        String port= Integer.toString( 6767 );
        String path= "/service";
        Boolean expectedPayload= Boolean.FALSE;

        Event result= flowRunner( flowName ).withVariable( "host", host ).withVariable( "port", port ).withVariable( "path", path ).withPayload( "nothing_important" ).run();
        Message response= result.getMessage();
        assertTrue( "wrong response class", DataType.BOOLEAN.isCompatibleWith( response.getPayload().getDataType() ) );
        assertEquals( "wrong response payload", expectedPayload, (Boolean) response.getPayload().getValue() );
    }

    //TODO catch error in flow
    /**
     * Test Ping on dynamicly set host and port of not listening server
     * @throws Exception should not happen in this test
     */
    @Test
    public void testDynamicUriPingNoResolvableHost() throws Exception
    {
        String flowName= "ping_dynamic";
        String host= "dit_bestaat_niet.org";
        String port= Integer.toString( CoAP.DEFAULT_COAP_PORT );

        Exception e= assertThrows(
            Exception.class,
            () -> flowRunner( flowName ).withVariable( "host", host ).withVariable( "port", port ).withVariable( "path", port ).withPayload( "nothing_important" ).run() );
        assertTrue(
            "wrong exception message",
            e.getMessage().contains( "ping failed" ) );
        //assert( "COAP:MALFORMED_URI" );
        assertEquals( "wrong exception cause", e.getCause().getClass(), UriException.class );
    }

    @Test
    public void testWellKnownCore() throws Exception
    {
        String flowName= "discover";
        Event result= flowRunner( flowName ).withPayload( "nothing_important" ).run();
        Message response= result.getMessage();

        @SuppressWarnings("unchecked")
        HashMap< String, DiscoveredResource > links= linkMap( (Set< DiscoveredResource >) response.getPayload().getValue() );

        assertEquals( "wrong number of weblinks", 8, links.size() );
        DiscoveredResource link= links.get( "/.well-known/core" );
        assertNotNull( "/.well-known/core is missing", link );
    }

    @Test
    public void testCt() throws Exception
    {
        String flowName= "discover";
        Event result= flowRunner( flowName ).withPayload( "nothing_important" ).run();
        Message response= result.getMessage();

        @SuppressWarnings("unchecked")
        HashMap< String, DiscoveredResource > links= linkMap( (Set< DiscoveredResource >) response.getPayload().getValue() );
        DiscoveredResource link= links.get( "/service/resource_with_ct" );

        assertNotNull( "/service/resource_with_ct is missing", link );
        String[] ct= link.getCt().split( "\\s*,\\s*" );
        assertEquals( "wrong number ct", 2, ct.length );
        assertEquals( "ct does not contain 0", "0", ct[0] );
        assertEquals( "ct does not contain 41", "41", ct[1] );

        //check other attributes are not there
        String[] ifdesc= link.getIf().split( "\\s*,\\s*" );
        assertEquals( "if length unexpected", 1, ifdesc.length );
        assertEquals( "if unexpected", "", ifdesc[0] );
        boolean obs= link.isObs();
        assertFalse( "obs unexpected", obs );
        String[] rt= link.getRt().split( "\\s*,\\s*" );
        assertEquals( "rt length unexpected", 1, rt.length );
        assertEquals( "rt unexpected", "", rt[0] );
        String sz= link.getSz();
        assertEquals( "sz unexpected", "", sz );
        String title= link.getTitle();
        assertNull( "title unexpected", title );

    }

    @Test
    public void testIf() throws Exception
    {
        String flowName= "discover";
        Event result= flowRunner( flowName ).withPayload( "nothing_important" ).run();
        Message response= result.getMessage();

        @SuppressWarnings("unchecked")
        HashMap< String, DiscoveredResource > links= linkMap( (Set< DiscoveredResource >) response.getPayload().getValue() );

        DiscoveredResource link= links.get( "/service/resource_with_if" );
        assertNotNull( "/service/resource_with_if is missing", link );
        String[] ifdesc= link.getIf().split( "\\s*,\\s*" );
        assertEquals( "wrong number of ifdesc", 2, ifdesc.length );
        assertEquals( "ifdesc does not contain 0", "if1", ifdesc[0] );
        assertEquals( "ifdesc does not contain 41", "if2", ifdesc[1] );

        //check other attributes are not there
        String[] ct= link.getCt().split( "\\s*,\\s*" );
        assertEquals( "ct length unexpected", 1, ct.length );
        assertEquals( "ct unexpected", "", ct[0] );
        boolean obs= link.isObs();
        assertFalse( "obs unexpected", obs );
        String[] rt= link.getRt().split( "\\s*,\\s*" );
        assertEquals( "rt length unexpected", 1, rt.length );
        assertEquals( "rt unexpected", "", rt[0] );
        String sz= link.getSz();
        assertEquals( "sz unexpected", "", sz );
        String title= link.getTitle();
        assertNull( "title unexpected", title );
    }

    @Test
    public void testObs() throws Exception
    {
        String flowName= "discover";
        Event result= flowRunner( flowName ).withPayload( "nothing_important" ).run();
        Message response= result.getMessage();

        @SuppressWarnings("unchecked")
        HashMap< String, DiscoveredResource > links= linkMap( (Set< DiscoveredResource >) response.getPayload().getValue() );

        DiscoveredResource link= links.get( "/service/resource_with_obs" );
        assertNotNull( "/service/resource_with_obs is missing", link );
        boolean obs= link.isObs();
        assertTrue( "obs not true", obs );

        //check other attributes are not there
        String[] ct= link.getCt().split( "\\s*,\\s*" );
        assertEquals( "ct length unexpected", 1, ct.length );
        assertEquals( "ct unexpected", "", ct[0] );
        String[] ifdesc= link.getIf().split( "\\s*,\\s*" );
        assertEquals( "if length unexpected", 1, ifdesc.length );
        assertEquals( "if unexpected", "", ifdesc[0] );
        String[] rt= link.getRt().split( "\\s*,\\s*" );
        assertEquals( "rt length unexpected", 1, rt.length );
        assertEquals( "rt unexpected", "", rt[0] );
        String sz= link.getSz();
        assertEquals( "sz unexpected", "", sz );
        String title= link.getTitle();
        assertNull( "title unexpected", title );
    }

    @Test
    public void testRt() throws Exception
    {
        String flowName= "discover";
        Event result= flowRunner( flowName ).withPayload( "nothing_important" ).run();
        Message response= result.getMessage();

        @SuppressWarnings("unchecked")
        HashMap< String, DiscoveredResource > links= linkMap( (Set< DiscoveredResource >) response.getPayload().getValue() );

        DiscoveredResource link= links.get( "/service/resource_with_rt" );
        assertNotNull( "/service/resource_with_rt is missing", link );
        String[] rt= link.getRt().split( "\\s*,\\s*" );
        assertEquals( "wrong number of rt", 2, rt.length );
        assertEquals( "rt does not contain rt1", "rt1", rt[0] );
        assertEquals( "rt does not contain rt2", "rt2", rt[1] );

        //check other attributes are not there
        String[] ct= link.getCt().split( "\\s*,\\s*" );
        assertEquals( "ct length unexpected", 1, ct.length );
        assertEquals( "ct unexpected", "", ct[0] );
        String[] ifdesc= link.getIf().split( "\\s*,\\s*" );
        assertEquals( "if length unexpected", 1, ifdesc.length );
        assertEquals( "if unexpected", "", ifdesc[0] );
        boolean obs= link.isObs();
        assertFalse( "obs unexpected", obs );
        String sz= link.getSz();
        assertEquals( "sz unexpected", "", sz );
        String title= link.getTitle();
        assertNull( "title unexpected", title );
    }

    @Test
    public void testSz() throws Exception
    {
        String flowName= "discover";
        Event result= flowRunner( flowName ).withPayload( "nothing_important" ).run();
        Message response= result.getMessage();

        @SuppressWarnings("unchecked")
        HashMap< String, DiscoveredResource > links= linkMap( (Set< DiscoveredResource >) response.getPayload().getValue() );

        DiscoveredResource link= links.get( "/service/resource_with_sz" );
        assertNotNull( "/service/resource_with_sz is missing", link );
        String sz= link.getSz();
        assertEquals( "sz has wrong value", "123456", sz );

        //check other attributes are not there
        String[] ct= link.getCt().split( "\\s*,\\s*" );
        assertEquals( "ct length unexpected", 1, ct.length );
        assertEquals( "ct unexpected", "", ct[0] );
        String[] ifdesc= link.getIf().split( "\\s*,\\s*" );
        assertEquals( "if length unexpected", 1, ifdesc.length );
        assertEquals( "if unexpected", "", ifdesc[0] );
        boolean obs= link.isObs();
        assertFalse( "obs unexpected", obs );
        String[] rt= link.getRt().split( "\\s*,\\s*" );
        assertEquals( "rt length unexpected", 1, rt.length );
        assertEquals( "rt unexpected", "", rt[0] );
        String title= link.getTitle();
        assertNull( "title unexpected", title );

    }

    @Test
    public void testTitle() throws Exception
    {
        String flowName= "discover";
        Event result= flowRunner( flowName ).withPayload( "nothing_important" ).run();
        Message response= result.getMessage();

        @SuppressWarnings("unchecked")
        HashMap< String, DiscoveredResource > links= linkMap( (Set< DiscoveredResource >) response.getPayload().getValue() );

        DiscoveredResource link= links.get( "/service/resource_with_title" );
        assertNotNull( "/service/resource_with_title is missing", link );
        String title= link.getTitle();
        assertEquals( "title has wrong value", "Title is resource_with_title", title );

        //check other attributes are not there
        String[] ct= link.getCt().split( "\\s*,\\s*" );
        assertEquals( "ct length unexpected", 1, ct.length );
        assertEquals( "ct unexpected", "", ct[0] );
        String[] ifdesc= link.getIf().split( "\\s*,\\s*" );
        assertEquals( "if length unexpected", 1, ifdesc.length );
        assertEquals( "if unexpected", "", ifdesc[0] );
        boolean obs= link.isObs();
        assertFalse( "obs unexpected", obs );
        String[] rt= link.getRt().split( "\\s*,\\s*" );
        assertEquals( "rt length unexpected", 1, rt.length );
        assertEquals( "rt unexpected", "", rt[0] );
        String sz= link.getSz();
        assertEquals( "sz unexpected", "", sz );

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDynamicResource() throws Exception
    {
        //check resource is not there
        String flowName= "discover";
        Event result= flowRunner( flowName ).withPayload( "nothing_important" ).run();
        Message response= result.getMessage();

        HashMap< String, DiscoveredResource > links= linkMap( (Set< DiscoveredResource >) response.getPayload().getValue() );
        DiscoveredResource link= links.get( "/service/dynamic_resource" );
        assertNull( "/service/dynamic_resource should not be there", link );

        //create resource
        flowName= "post";
        result= flowRunner( flowName ).withPayload( "dynamic_resource" ).run();
        response= result.getMessage();
        assertEquals(
            "wrong attributes class",
            new TypedValue< ReceivedResponseAttributes >( new ReceivedResponseAttributes(), null ).getClass(),
            response.getAttributes().getClass() );
        ReceivedResponseAttributes attributes= (ReceivedResponseAttributes) response.getAttributes().getValue();
        assertEquals( "could not create resource", "CREATED", attributes.getResponseCode() );

        //check resource is there
        flowName= "discover";
        result= flowRunner( flowName ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        links= linkMap( (Set< DiscoveredResource >) response.getPayload().getValue() );
        link= links.get( "/service/dynamic_resource" );
        assertNotNull( "/service/dynamic_resource should not be there", link );

        //delete resource
        flowName= "delete";
        result= flowRunner( flowName ).withVariable( "host", "127.0.0.1" ).withVariable( "port", "5683" ).withVariable( "path", "/service/dynamic_resource" ).run();
        response= result.getMessage();
        assertEquals(
            "wrong attributes class",
            new TypedValue< ReceivedResponseAttributes >( new ReceivedResponseAttributes(), null ).getClass(),
            response.getAttributes().getClass() );
        attributes= (ReceivedResponseAttributes) response.getAttributes().getValue();
        assertEquals( "could not delete resource", "DELETED", attributes.getResponseCode() );

        //check resource is not there
        flowName= "discover";
        result= flowRunner( flowName ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        links= linkMap( (Set< DiscoveredResource >) response.getPayload().getValue() );
        link= links.get( "/service/dynamic_resource" );
        assertNull( "/service/dynamic_resource should not be there", link );
    }
}
