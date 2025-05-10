/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2024 - 2025 (teslanet.nl) Rogier Cobben
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
        String resourceUri1= "coap://127.0.0.1/parent1/newSub1";
        String resourceUri2= "coap://127.0.0.1/parent1/newSub2";
        String resourceUri3= "coap://127.0.0.1/parent2/newSub2/notAllowed";

        //setClientUri( resourceUri1 );
        Request requestGet1= new Request( Code.GET ).setURI( resourceUri1 );
        Request requestGet2= new Request( Code.GET ).setURI( resourceUri2 );
        Request requestPut1a= new Request( Code.PUT ).setURI( resourceUri1 ).setPayload( "content1a" );
        Request requestGet1a= new Request( Code.GET ).setURI( resourceUri1 );
        Request requestGet2a= new Request( Code.GET ).setURI( resourceUri2 );
        Request requestPut1b= new Request( Code.PUT ).setURI( resourceUri1 ).setPayload( "content1b" );
        Request requestGet1b= new Request( Code.GET ).setURI( resourceUri1 );
        Request requestGet2b= new Request( Code.GET ).setURI( resourceUri2 );
        Request requestPut2c= new Request( Code.PUT ).setURI( resourceUri2 ).setPayload( "content2c" );
        Request requestGet1c= new Request( Code.GET ).setURI( resourceUri1 );
        Request requestGet2c= new Request( Code.GET ).setURI( resourceUri2 );
        Request requestPut2d= new Request( Code.PUT ).setURI( resourceUri2 ).setPayload( "content2d" );
        Request requestGet1d= new Request( Code.GET ).setURI( resourceUri1 );
        Request requestGet2d= new Request( Code.GET ).setURI( resourceUri2 );
        Request requestPut3= new Request( Code.PUT ).setURI( resourceUri3 ).setPayload( "content3" );

        CoapResponse response= client.advanced( requestGet1 );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.NOT_FOUND, response.getCode() );
        response= client.advanced( requestGet2 );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.NOT_FOUND, response.getCode() );

        // create resource 1
        response= client.advanced( requestPut1a );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CREATED, response.getCode() );

        response= client.advanced( requestGet1a );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CONTENT, response.getCode() );
        response= client.advanced( requestGet2a );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.NOT_FOUND, response.getCode() );

        // update resource 1
        response= client.advanced( requestPut1b );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CHANGED, response.getCode() );

        response= client.advanced( requestGet1b );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CONTENT, response.getCode() );
        response= client.advanced( requestGet2b );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.NOT_FOUND, response.getCode() );

        // create resource 2
        response= client.advanced( requestPut2c );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CREATED, response.getCode() );

        response= client.advanced( requestGet1c );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CONTENT, response.getCode() );
        response= client.advanced( requestGet2c );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CONTENT, response.getCode() );

        // update resource 2
        response= client.advanced( requestPut2d );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CHANGED, response.getCode() );

        response= client.advanced( requestGet1d );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CONTENT, response.getCode() );
        response= client.advanced( requestGet2d );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CONTENT, response.getCode() );

        // update resource 3
        response= client.advanced( requestPut3 );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.BAD_GATEWAY, response.getCode() );

    }

    /**
     * Test adding nested resources on the server
     * @throws Exception
     */
    @Test
    public void testPutResource2() throws Exception
    {
        String resourceUri1= "coap://127.0.0.1/parent2/newSub1";
        String resourceUri2= "coap://127.0.0.1/parent2/newSub1/newSub2";
        String resourceUri3= "coap://127.0.0.1/parent2/newSub1/newSub2/newSub3";

        //setClientUri( resourceUri1 );
        Request requestGet1= new Request( Code.GET ).setURI( resourceUri1 );
        Request requestGet2= new Request( Code.GET ).setURI( resourceUri2 );
        Request requestGet3= new Request( Code.GET ).setURI( resourceUri3 );
        Request requestPut3a= new Request( Code.PUT ).setURI( resourceUri2 ).setPayload( "content3a" );
        Request requestPut2a= new Request( Code.PUT ).setURI( resourceUri2 ).setPayload( "content2a" );
        Request requestPut1a= new Request( Code.PUT ).setURI( resourceUri1 ).setPayload( "content1a" );
        Request requestGet1a= new Request( Code.GET ).setURI( resourceUri1 );
        Request requestGet2a= new Request( Code.GET ).setURI( resourceUri2 );
        Request requestGet3a= new Request( Code.GET ).setURI( resourceUri3 );
        Request requestPut1b= new Request( Code.PUT ).setURI( resourceUri1 ).setPayload( "content1b" );
        Request requestGet1b= new Request( Code.GET ).setURI( resourceUri1 );
        Request requestGet2b= new Request( Code.GET ).setURI( resourceUri2 );
        Request requestGet3b= new Request( Code.GET ).setURI( resourceUri3 );
        Request requestPut3c= new Request( Code.PUT ).setURI( resourceUri3 ).setPayload( "content3c" );
        Request requestPut2c= new Request( Code.PUT ).setURI( resourceUri2 ).setPayload( "content2c" );
        Request requestGet1c= new Request( Code.GET ).setURI( resourceUri1 );
        Request requestGet2c= new Request( Code.GET ).setURI( resourceUri2 );
        Request requestGet3c= new Request( Code.GET ).setURI( resourceUri3 );
        Request requestPut2d= new Request( Code.PUT ).setURI( resourceUri2 ).setPayload( "content2d" );
        Request requestGet1d= new Request( Code.GET ).setURI( resourceUri1 );
        Request requestGet2d= new Request( Code.GET ).setURI( resourceUri2 );
        Request requestGet3d= new Request( Code.GET ).setURI( resourceUri3 );
        Request requestPut3e= new Request( Code.PUT ).setURI( resourceUri3 ).setPayload( "content3e" );
        Request requestGet1e= new Request( Code.GET ).setURI( resourceUri1 );
        Request requestGet2e= new Request( Code.GET ).setURI( resourceUri2 );
        Request requestGet3e= new Request( Code.GET ).setURI( resourceUri3 );
        Request requestPut3f= new Request( Code.PUT ).setURI( resourceUri3 ).setPayload( "content3f" );
        Request requestGet1f= new Request( Code.GET ).setURI( resourceUri1 );
        Request requestGet2f= new Request( Code.GET ).setURI( resourceUri2 );
        Request requestGet3f= new Request( Code.GET ).setURI( resourceUri3 );

        CoapResponse response= client.advanced( requestGet1 );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.NOT_FOUND, response.getCode() );
        response= client.advanced( requestGet2 );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.NOT_FOUND, response.getCode() );
        response= client.advanced( requestGet3 );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.NOT_FOUND, response.getCode() );

        // try create resource 3
        response= client.advanced( requestPut3a );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.BAD_GATEWAY, response.getCode() );

        // try create resource 2
        response= client.advanced( requestPut2a );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.BAD_GATEWAY, response.getCode() );

        // create resource 1
        response= client.advanced( requestPut1a );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CREATED, response.getCode() );

        response= client.advanced( requestGet1a );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CONTENT, response.getCode() );
        response= client.advanced( requestGet2a );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.NOT_FOUND, response.getCode() );
        response= client.advanced( requestGet3a );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.NOT_FOUND, response.getCode() );

        // update resource 1
        response= client.advanced( requestPut1b );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CHANGED, response.getCode() );

        response= client.advanced( requestGet1b );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CONTENT, response.getCode() );
        response= client.advanced( requestGet2b );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.NOT_FOUND, response.getCode() );
        response= client.advanced( requestGet3b );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.NOT_FOUND, response.getCode() );

        // try create resource 3
        response= client.advanced( requestPut3c );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.BAD_GATEWAY, response.getCode() );

        // create resource 2
        response= client.advanced( requestPut2c );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CREATED, response.getCode() );

        response= client.advanced( requestGet1c );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CONTENT, response.getCode() );
        response= client.advanced( requestGet2c );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CONTENT, response.getCode() );
        response= client.advanced( requestGet3c );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.NOT_FOUND, response.getCode() );

        // update resource 2
        response= client.advanced( requestPut2d );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CHANGED, response.getCode() );

        response= client.advanced( requestGet1d );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CONTENT, response.getCode() );
        response= client.advanced( requestGet2d );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CONTENT, response.getCode() );
        response= client.advanced( requestGet3d );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.NOT_FOUND, response.getCode() );

        // create resource 3
        response= client.advanced( requestPut3e );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CREATED, response.getCode() );

        response= client.advanced( requestGet1e );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CONTENT, response.getCode() );
        response= client.advanced( requestGet2e );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CONTENT, response.getCode() );
        response= client.advanced( requestGet3e );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CONTENT, response.getCode() );

        // update resource 3
        response= client.advanced( requestPut3f );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CHANGED, response.getCode() );

        response= client.advanced( requestGet1f );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CONTENT, response.getCode() );
        response= client.advanced( requestGet2f );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CONTENT, response.getCode() );
        response= client.advanced( requestGet3f );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.CONTENT, response.getCode() );
    }

    /**
     * Test adding not allowed resources on the server
     * @throws Exception
     */
    @Test
    public void testPutResourceNotAllowed() throws Exception
    {
        String resourceUri1= "coap://127.0.0.1/parent3/newSub1";

        //setClientUri( resourceUri1 );
        Request requestGet1= new Request( Code.GET ).setURI( resourceUri1 );
        Request requestPut1a= new Request( Code.PUT ).setURI( resourceUri1 ).setPayload( "content1a" );
        Request requestGet1a= new Request( Code.GET ).setURI( resourceUri1 );

        CoapResponse response= client.advanced( requestGet1 );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.NOT_FOUND, response.getCode() );

        // try create resource 1
        response= client.advanced( requestPut1a );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.NOT_FOUND, response.getCode() );

        response= client.advanced( requestGet1a );
        assertNotNull( "request gave no response", response );
        assertEquals( "request gave wrong response", ResponseCode.NOT_FOUND, response.getCode() );
    }
}
