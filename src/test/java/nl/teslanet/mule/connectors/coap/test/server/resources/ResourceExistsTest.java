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
package nl.teslanet.mule.connectors.coap.test.server.resources;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractServerTestCase;
import nl.teslanet.shaded.org.eclipse.californium.core.CoapResponse;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.CoAP.Code;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.CoAP.ResponseCode;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.Request;


public class ResourceExistsTest extends AbstractServerTestCase
{
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/resources/testserver1.xml";
    };

    @Test(timeout= 20000L)
    public void testResourceExists() throws Exception
    {
        setClientPath( "/service/exists" );
        Request request= new Request( Code.GET );
        request.getOptions().addLocationPath( "service" );
        request.getOptions().addLocationPath( "resource-to-remove" );
        CoapResponse response= client.advanced( request );
        assertNotNull( "get exists gave no response", response );
        assertTrue( "response get exists indicates failure", response.isSuccess() );
        assertEquals( "get exists gave wrong response", ResponseCode.CONTENT, response.getCode() );
        assertEquals( "get exists gave wrong payload", Boolean.TRUE.toString(), response.getResponseText() );

        setClientPath( "/service/resource-to-remove" );
        response= client.delete();
        assertNotNull( "got no response on delete", response );
        assertEquals( "wrong response on delete", ResponseCode.DELETED, response.getCode() );

        setClientPath( "/service/exists" );
        request= new Request( Code.GET );
        request.getOptions().addLocationPath( "service" );
        request.getOptions().addLocationPath( "resource-to-remove" );
        response= client.advanced( request );
        assertNotNull( "get exists gave no response", response );
        assertTrue( "response get exists indicates failure", response.isSuccess() );
        assertEquals( "get exists gave wrong response", ResponseCode.CONTENT, response.getCode() );
        assertEquals( "get exists gave wrong payload", Boolean.FALSE.toString(), response.getResponseText() );
    }

    @Test(timeout= 20000L)
    public void testAddedResourceExists() throws Exception
    {
        setClientPath( "/service/exists" );
        Request request= new Request( Code.GET );
        request.getOptions().addLocationPath( "service" ).addLocationPath( "temporary-resource" );
        CoapResponse response= client.advanced( request );
        assertNotNull( "get exists gave no response", response );
        assertTrue( "response get exists indicates failure", response.isSuccess() );
        assertEquals( "get exists gave wrong response", ResponseCode.CONTENT, response.getCode() );
        assertEquals( "get exists gave wrong payload", Boolean.FALSE.toString(), response.getResponseText() );

        setClientPath( "/service/add_resource/all_methods" );
        request= new Request( Code.POST );
        request.setPayload( "some content" );
        request.getOptions().addLocationPath( "service" ).addLocationPath( "temporary-resource" );
        response= client.advanced( request );
        assertNotNull( "post gave no response", response );
        assertTrue( "post response indicates failure", response.isSuccess() );
        assertEquals( "post gave wrong response", ResponseCode.CREATED, response.getCode() );

        setClientPath( "/service/exists" );
        request= new Request( Code.GET );
        request.getOptions().addLocationPath( "service" ).addLocationPath( "temporary-resource" );
        response= client.advanced( request );
        assertNotNull( "get exists gave no response", response );
        assertTrue( "response get exists indicates failure", response.isSuccess() );
        assertEquals( "get exists gave wrong response", ResponseCode.CONTENT, response.getCode() );
        assertEquals( "get exists gave wrong payload", Boolean.TRUE.toString(), response.getResponseText() );

        setClientPath( "/service/temporary-resource" );
        response= client.delete();
        assertNotNull( "got no response on delete", response );
        assertEquals( "wrong response on delete", ResponseCode.DELETED, response.getCode() );

        setClientPath( "/service/exists" );
        request= new Request( Code.GET );
        request.getOptions().addLocationPath( "service" ).addLocationPath( "temporary-resource" );
        response= client.advanced( request );
        assertNotNull( "get exists gave no response", response );
        assertTrue( "response get exists indicates failure", response.isSuccess() );
        assertEquals( "get exists gave wrong response", ResponseCode.CONTENT, response.getCode() );
        assertEquals( "get exists gave wrong payload", Boolean.FALSE.toString(), response.getResponseText() );
    }
}
