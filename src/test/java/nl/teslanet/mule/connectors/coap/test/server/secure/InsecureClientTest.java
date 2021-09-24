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
package nl.teslanet.mule.connectors.coap.test.server.secure;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.Request;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;


public class InsecureClientTest extends AbstractTestCase
{
    URI uri= null;

    CoapClient client= null;

    private static ArrayList< Code > inboundCalls;

    private static ArrayList< Code > outboundCalls;

    private static HashMap< Code, String > paths;

    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/secure/testserver1.xml";
    };

    @BeforeClass
    static public void setUpClass() throws Exception
    {
        inboundCalls= new ArrayList< Code >();
        inboundCalls.add( Code.GET );
        inboundCalls.add( Code.PUT );
        inboundCalls.add( Code.POST );
        inboundCalls.add( Code.DELETE );
        outboundCalls= new ArrayList< Code >();
        outboundCalls.add( Code.GET );
        outboundCalls.add( Code.PUT );
        outboundCalls.add( Code.POST );
        outboundCalls.add( Code.DELETE );

        paths= new HashMap< Code, String >();
        paths.put( Code.GET, "/service/get_me" );
        paths.put( Code.PUT, "/service/put_me" );
        paths.put( Code.POST, "/service/post_me" );
        paths.put( Code.DELETE, "/service/delete_me" );
    }

    @Before
    public void setUp() throws Exception
    {
        uri= new URI( "coap", null, "127.0.0.1", 5684, null, null, null );
    }

    @After
    public void tearDown() throws Exception
    {
        if ( client != null ) client.shutdown();
    }

    protected String getPath( Code call )
    {
        return paths.get( call );
    }

    private CoapClient getClient( String path )
    {
        CoapClient client= new CoapClient( uri.resolve( path ) );
        client.setTimeout( 3000L );
        return client;
    }

    /**
     * Create spy for requests.
     * @return the spy
     */
    private MuleEventSpy spyMessage()
    {
        return spyMessage( null );
    }

    /**
     * Create spy for requests with a message replacement
     * @return the spy
     */
    private MuleEventSpy spyMessage( byte[] replacement )
    {
        MuleEventSpy spy= new MuleEventSpy( "securityTest", null, replacement );
        spy.clear();
        return spy;
    }

    /**
     * Assert the spy has no collected events
     * @param spy the spy that should have collected the property
     */
    void assertSpy( MuleEventSpy spy )
    {
        assertEquals( "Spy has collected wrong number of events", 0, spy.getEvents().size() );
    }

    @Test(timeout= 15000)
    public void testInsecureClientGet() throws Exception
    {
        MuleEventSpy spy= spyMessage();

        Code call= Code.GET;
        CoapClient client= getClient( getPath( call ) );
        Request request= new Request( call );

        CoapResponse response= client.advanced( request );

        assertNull( "should not receive a response", response );
        assertSpy( spy );

        client.shutdown();
    }

    @Test(timeout= 15000)
    public void testInsecureClientPost() throws Exception
    {
        MuleEventSpy spy= spyMessage();

        Code call= Code.POST;
        CoapClient client= getClient( getPath( call ) );
        Request request= new Request( call );
        request.setPayload( "nothing important" );

        CoapResponse response= client.advanced( request );

        assertNull( "should not receive a response", response );
        assertSpy( spy );

        client.shutdown();

    }

    @Test()
    public void testInsecureClientPut() throws Exception
    {
        MuleEventSpy spy= spyMessage();

        Code call= Code.PUT;
        CoapClient client= getClient( getPath( call ) );
        Request request= new Request( call );
        request.setPayload( "nothing important" );

        CoapResponse response= client.advanced( request );

        assertNull( "should not receive a response", response );
        assertSpy( spy );

        client.shutdown();

    }

    @Test()
    public void testInsecureClientDelete() throws Exception
    {
        MuleEventSpy spy= spyMessage();

        Code call= Code.DELETE;
        CoapClient client= getClient( getPath( call ) );
        Request request= new Request( call );

        CoapResponse response= client.advanced( request );

        assertNull( "should not receive a response", response );
        assertSpy( spy );

        client.shutdown();

    }

}
