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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractServerTestCase;
import nl.teslanet.shaded.org.eclipse.californium.core.CoapResponse;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.CoAP.Code;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.CoAP.ResponseCode;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.Request;


/**
 * Test adding a resource 
 *
 */
@RunnerDelegateTo(Parameterized.class)
public class AddResourceTest extends AbstractServerTestCase
{
    /**
     * @return the test parameters
     */
    @Parameters(name= "Request= {0}, path= {1}, addResource= {2}")
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []{
                //default maxResourceBodySize on server
                { Code.GET, "/service/new_get_resource1", "/service/add_resource/all_methods", ResponseCode.CONTENT },
                { Code.PUT, "/service/new_put_resource1", "/service/add_resource/all_methods", ResponseCode.CHANGED },
                { Code.POST, "/service/new_post_resource1", "/service/add_resource/all_methods", ResponseCode.CHANGED },
                { Code.DELETE, "/service/new_delete_resource1", "/service/add_resource/all_methods", ResponseCode.DELETED },
                { Code.GET, "/service/new_get_resource2", "/service/add_resource/get_only", ResponseCode.CONTENT },
                { Code.PUT, "/service/new_put_resource2", "/service/add_resource/get_only", ResponseCode.METHOD_NOT_ALLOWED },
                { Code.POST, "/service/new_post_resource2", "/service/add_resource/get_only", ResponseCode.METHOD_NOT_ALLOWED },
                { Code.DELETE, "/service/new_delete_resource2", "/service/add_resource/get_only", ResponseCode.METHOD_NOT_ALLOWED },
                { Code.GET, "/service/new_get_resource3", "/service/add_resource/post_only", ResponseCode.METHOD_NOT_ALLOWED },
                { Code.PUT, "/service/new_put_resource3", "/service/add_resource/post_only", ResponseCode.METHOD_NOT_ALLOWED },
                { Code.POST, "/service/new_post_resource3", "/service/add_resource/post_only", ResponseCode.CHANGED },
                { Code.DELETE, "/service/new_delete_resource3", "/service/add_resource/post_only", ResponseCode.METHOD_NOT_ALLOWED },
                { Code.GET, "/service/new_get_resource4", "/service/add_resource/put_only", ResponseCode.METHOD_NOT_ALLOWED },
                { Code.PUT, "/service/new_put_resource4", "/service/add_resource/put_only", ResponseCode.CHANGED },
                { Code.POST, "/service/new_post_resource4", "/service/add_resource/put_only", ResponseCode.METHOD_NOT_ALLOWED },
                { Code.DELETE, "/service/new_delete_resource4", "/service/add_resource/put_only", ResponseCode.METHOD_NOT_ALLOWED },
                { Code.GET, "/service/new_get_resource5", "/service/add_resource/delete_only", ResponseCode.METHOD_NOT_ALLOWED },
                { Code.PUT, "/service/new_put_resource5", "/service/add_resource/delete_only", ResponseCode.METHOD_NOT_ALLOWED },
                { Code.POST, "/service/new_post_resource5", "/service/add_resource/delete_only", ResponseCode.METHOD_NOT_ALLOWED },
                { Code.DELETE, "/service/new_delete_resource5", "/service/add_resource/delete_only", ResponseCode.DELETED } } );
    }

    /**
     * Request code to test
     */
    @Parameter(0)
    public Code requestCode;

    /**
    * Test resource to call
    */
    @Parameter(1)
    public String resourcePath;

    /**
    * Resource to call for adding
    */
    @Parameter(2)
    public String addResourcePath;

    /**
     * Expected response code to test
     */
    @Parameter(3)
    public ResponseCode expectedResponseCode;

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/resources/testserver1.xml";
    };

    /**
     * Test adding a resource on the server
     * @throws Exception
     */
    @Test(timeout= 20000L)
    public void testAddResource() throws Exception
    {
        setClientPath( resourcePath );
        Request request= new Request( requestCode );
        CoapResponse response1= client.advanced( request );
        assertNotNull( "request gave no response", response1 );
        assertEquals( "request gave wrong response", ResponseCode.NOT_FOUND, response1.getCode() );

        setClientPath( addResourcePath );
        Request request2= new Request( Code.POST );
        request2.setPayload( "some content" );
        request2.getOptions().setLocationPath( resourcePath );
        CoapResponse response2= client.advanced( request2 );
        assertNotNull( "post gave no response", response2 );
        assertTrue( "post response indicates failure", response2.isSuccess() );
        assertEquals( "post gave wrong response", ResponseCode.CREATED, response2.getCode() );

        setClientPath( resourcePath );
        Request request3= new Request( requestCode );
        CoapResponse response3= client.advanced( request3 );
        assertNotNull( "got no response", response3 );
        assertEquals( "wrong response", expectedResponseCode, response3.getCode() );
    }
}
