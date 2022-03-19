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
package nl.teslanet.mule.connectors.coap.test.server.resources;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractServerTestCase;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Request;


public class ResourceListTest extends AbstractServerTestCase
{
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/resources/testserver1.xml";
    };

    @Test( timeout= 20000L )
    public void testResourcelistAll() throws Exception
    {
        setClientUri( "/list" );
        String path1= "/*";
        String resourceList= "/add_resource,/add_resource/all_methods,/add_resource/delete_only,/add_resource/get_only,/add_resource/post_only,/add_resource/put_only,/exists,/list,/service,/service/resource-to-remove,/service/resource1,/service/resource1/resource2,/service/resource1/resource2/resource3";
        Request request= new Request( Code.POST );
        request.setPayload( path1 );
        CoapResponse response= client.advanced( request );
        assertNotNull( "get list gave no response", response );
        assertTrue( "response get list indicates failure", response.isSuccess() );
        assertEquals( "get list gave wrong response", ResponseCode.CONTENT, response.getCode() );
        assertEquals( "get list gave wrong payload", resourceList, response.getResponseText() );
    }

    @Test( timeout= 20000L )
    public void testResourcelist() throws Exception
    {
        setClientUri( "/list" );
        String path1= "/service/resource-to-remove";
        Request request= new Request( Code.POST );
        request.setPayload( path1 );
        CoapResponse response= client.advanced( request );
        assertNotNull( "get list gave no response", response );
        assertTrue( "response get list indicates failure", response.isSuccess() );
        assertEquals( "get list gave wrong response", ResponseCode.CONTENT, response.getCode() );
        assertEquals( "get list gave wrong payload", path1, response.getResponseText() );

        setClientUri( "/service/resource-to-remove" );
        response= client.delete();
        assertNotNull( "got no response on delete", response );
        assertEquals( "wrong response on delete", ResponseCode.DELETED, response.getCode() );

        setClientUri( "/list" );
        request= new Request( Code.POST );
        request.setPayload( path1 );
        response= client.advanced( request );
        assertNotNull( "get list gave no response", response );
        assertTrue( "response get list indicates failure", response.isSuccess() );
        assertEquals( "get list gave wrong response", ResponseCode.CONTENT, response.getCode() );
        assertEquals( "get list gave wrong payload", "", response.getResponseText() );
    }

    @Test( timeout= 20000L )
    public void testAddedResourcelist() throws Exception
    {
        setClientUri( "/list" );
        String path1= "/service/temporary-resource";
        Request request= new Request( Code.POST );
        request.setPayload( path1 );
        CoapResponse response= client.advanced( request );
        assertNotNull( "get list gave no response", response );
        assertTrue( "response get list indicates failure", response.isSuccess() );
        assertEquals( "get list gave wrong response", ResponseCode.CONTENT, response.getCode() );
        assertEquals( "get list gave wrong payload", "", response.getResponseText() );

        setClientUri( "/add_resource/all_methods" );
        request= new Request( Code.POST );
        request.setPayload( path1 );
        response= client.advanced( request );
        assertNotNull( "post gave no response", response );
        assertTrue( "post response indicates failure", response.isSuccess() );
        assertEquals( "post gave wrong response", ResponseCode.CREATED, response.getCode() );

        setClientUri( "/list" );
        request= new Request( Code.POST );
        request.setPayload( path1 );
        response= client.advanced( request );
        assertNotNull( "get list gave no response", response );
        assertTrue( "response get list indicates failure", response.isSuccess() );
        assertEquals( "get list gave wrong response", ResponseCode.CONTENT, response.getCode() );
        assertEquals( "get list gave wrong payload", path1, response.getResponseText() );

        setClientUri( "/service/temporary-resource" );
        response= client.delete();
        assertNotNull( "got no response on delete", response );
        assertEquals( "wrong response on delete", ResponseCode.DELETED, response.getCode() );

        setClientUri( "/list" );
        request= new Request( Code.POST );
        request.setPayload( path1 );
        response= client.advanced( request );
        assertNotNull( "get list gave no response", response );
        assertTrue( "response get list indicates failure", response.isSuccess() );
        assertEquals( "get list gave wrong response", ResponseCode.CONTENT, response.getCode() );
        assertEquals( "get list gave wrong payload", "", response.getResponseText() );
    }
}
