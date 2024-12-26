/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2024 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.test.server.resources;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Request;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractServerTestCase;


/**
 * Test adding a resource 
 *
 */
public class PutSubResourceTest extends AbstractServerTestCase
{

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/resources/testserver-putsubresource.xml";
    };

    /**
     * Test adding a resource on the server
     * @throws Exception
     */
    @Test
    public void testPutResource1() throws Exception
    {
        String resourcePath1= "/parent1/newSub1";
        String resourcePath2= "/parent1/newSub2";

        setClientUri( resourcePath1 );
        Request request1= new Request( Code.PUT );
        request1.setPayload( "content1" );
        CoapResponse response1= client.advanced( request1 );
        assertNotNull( "request gave no response", response1 );
        assertEquals( "request gave wrong response", ResponseCode.CREATED, response1.getCode() );

        setClientUri( resourcePath1 );
        Request request2= new Request( Code.PUT );
        request1.setPayload( "content2" );
        CoapResponse response2= client.advanced( request2 );
        assertNotNull( "request gave no response", response2 );
        assertEquals( "request gave wrong response", ResponseCode.CHANGED, response2.getCode() );

        setClientUri( resourcePath2 );
        Request request3= new Request( Code.PUT );
        request1.setPayload( "content3" );
        CoapResponse response3= client.advanced( request3 );
        assertNotNull( "request gave no response", response3 );
        assertEquals( "request gave wrong response", ResponseCode.CREATED, response3.getCode() );

        setClientUri( resourcePath1 );
        Request request4= new Request( Code.PUT );
        request1.setPayload( "content4" );
        CoapResponse response4= client.advanced( request4 );
        assertNotNull( "request gave no response", response4 );
        assertEquals( "request gave wrong response", ResponseCode.CHANGED, response4.getCode() );
    }
    
    /**
     * Test adding a resource on the server
     * @throws Exception
     */
    @Test
    public void testPutResource2() throws Exception
    {
        String resourcePath1= "/parent2/newSub1";
        String resourcePath2= "/parent2/newSub2";

        setClientUri( resourcePath1 );
        Request request1= new Request( Code.PUT );
        request1.setPayload( "content1" );
        CoapResponse response1= client.advanced( request1 );
        assertNotNull( "request gave no response", response1 );
        assertEquals( "request gave wrong response", ResponseCode.CREATED, response1.getCode() );

        setClientUri( resourcePath1 );
        Request request2= new Request( Code.PUT );
        request1.setPayload( "content2" );
        CoapResponse response2= client.advanced( request2 );
        assertNotNull( "request gave no response", response2 );
        assertEquals( "request gave wrong response", ResponseCode.CHANGED, response2.getCode() );

        setClientUri( resourcePath2 );
        Request request3= new Request( Code.PUT );
        request1.setPayload( "content3" );
        CoapResponse response3= client.advanced( request3 );
        assertNotNull( "request gave no response", response3 );
        assertEquals( "request gave wrong response", ResponseCode.CREATED, response3.getCode() );

        setClientUri( resourcePath1 );
        Request request4= new Request( Code.PUT );
        request1.setPayload( "content4" );
        CoapResponse response4= client.advanced( request4 );
        assertNotNull( "request gave no response", response4 );
        assertEquals( "request gave wrong response", ResponseCode.CHANGED, response4.getCode() );
    }

}
