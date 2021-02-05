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


@RunnerDelegateTo(Parameterized.class)
public class RemoveResourceTest extends AbstractServerTestCase
{
    /**
     * @return the test parameters
     */
    @Parameters(name= "Request= {0}")
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []{
                //default maxResourceBodySize on server
                { Code.GET },
                { Code.PUT },
                { Code.POST },
                { Code.DELETE } } );
    }

    /**
     * Request code to test
     */
    @Parameter(0)
    public Code requestCode;

    /* (non-Javadoc)
     * @see org.mule.functional.junit4.FunctionalTestCase#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/resources/testserver1.xml";
    };

    @Test(timeout= 20000L)
    public void testRemoveResource() throws Exception
    {
        setClientPath( "/service/resource-to-remove" );
        CoapResponse response= client.put( "some content", 0 );
        assertNotNull( "put resource gave no response", response );
        assertEquals( "put gave wrong response", ResponseCode.CHANGED, response.getCode() );

        response= client.delete();
        assertNotNull( "got no response on delete", response );
        assertEquals( "wrong response on delete", ResponseCode.DELETED, response.getCode() );

        Request request= new Request( requestCode );
        response= client.advanced( request );
        assertNotNull( "got no response", response );
        assertEquals( "wrong response code", ResponseCode.NOT_FOUND, response.getCode() );
    }

    @Test(timeout= 20000L)
    public void testRemoveAddedResource() throws Exception
    {
        setClientPath( "/service/resource1" );
        CoapResponse response= client.get();
        assertNotNull( "get resoure1 gave no response", response );
        assertTrue( "response get resoure1 indicates failure", response.isSuccess() );
        assertEquals( "get gave wrong response", ResponseCode.CONTENT, response.getCode() );

        setClientPath( "/service/resource-to-remove2" );
        response= client.get();
        assertNotNull( "get resoure2 gave no response", response );
        assertEquals( "get gave wrong response", ResponseCode.NOT_FOUND, response.getCode() );

        setClientPath( "/service/add_resource/all_methods" );
        Request request= new Request( Code.POST );
        request.getOptions().addLocationPath( "service" ).addLocationPath( "resource-to-remove2" );
        response= client.advanced( request );
        assertNotNull( "post gave no response", response );
        assertTrue( "post response indicates failure", response.isSuccess() );
        assertEquals( "post gave wrong response", ResponseCode.CREATED, response.getCode() );

        setClientPath( "/service/resource-to-remove2" );
        response= client.delete();
        assertNotNull( "got no response on delete", response );
        assertEquals( "wrong response on delete", ResponseCode.DELETED, response.getCode() );

        setClientPath( "/service/resource-to-remove2" );
        request= new Request( requestCode );
        response= client.advanced( request );
        assertNotNull( "got no response", response );
        assertEquals( "wrong responsecode", ResponseCode.NOT_FOUND, response.getCode() );
    }
}
