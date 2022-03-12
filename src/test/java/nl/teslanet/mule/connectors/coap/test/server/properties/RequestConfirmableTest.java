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
package nl.teslanet.mule.connectors.coap.test.server.properties;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractServerTestCase;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;


@RunnerDelegateTo(Parameterized.class)
public class RequestConfirmableTest extends AbstractServerTestCase
{
    /**
     * @return the test parameters
     */
    @Parameters(name= "Request= {0}, path= {1}")
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []{
                //default maxResourceBodySize on server
                { Code.GET, "/service/get_me" },
                { Code.PUT, "/service/put_me" },
                { Code.POST, "/service/post_me" },
                { Code.DELETE, "/service/delete_me" }, } );
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

    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/properties/testserver-RequestConfirmable.xml";
    };

    @Test
    public void testCON() throws ConnectorException, IOException
    {
        String expected= "true";
        setClientUri( resourcePath );
        Request request= new Request( requestCode, Type.CON );
        CoapResponse response= client.advanced( request );

        assertNotNull( "get gave no response", response );
        assertTrue( "response indicates failure", response.isSuccess() );
        assertEquals( "echoed request confirmable has wrong value", expected, response.getResponseText() );
    }

    @Test
    public void testNon() throws ConnectorException, IOException
    {
        String expected= "false";
        setClientUri( resourcePath );
        Request request= new Request( requestCode, Type.NON );
        CoapResponse response= client.advanced( request );

        assertNotNull( "get gave no response", response );
        assertTrue( "response indicates failure", response.isSuccess() );
        assertEquals( "echoed request confirmable has wrong value", expected, response.getResponseText() );
    }
}
