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

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Request;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractServerTestCase;


/**
 * Test adding a resource 
 *
 */
@RunnerDelegateTo( Parameterized.class )
public class ResourceTest extends AbstractServerTestCase
{
    /**
     * @return the test parameters
     */
    @Parameters( name= "Request= {0}, path= {1}" )
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []
            {
                //default maxResourceBodySize on server
                { Code.GET, "/service/resource1", ResponseCode.CONTENT },
                { Code.PUT, "/service/resource1", ResponseCode.CHANGED },
                { Code.POST, "/service/resource1", ResponseCode.CHANGED },
                { Code.DELETE, "/service/resource1", ResponseCode.DELETED },
                { Code.GET, "/service/resource1/resource2", ResponseCode.CONTENT },
                { Code.PUT, "/service/resource1/resource2", ResponseCode.CHANGED },
                { Code.POST, "/service/resource1/resource2", ResponseCode.CHANGED },
                { Code.DELETE, "/service/resource1/resource2", ResponseCode.DELETED },
                { Code.GET, "/service/resource1/resource2/resource3", ResponseCode.CONTENT },
                { Code.PUT, "/service/resource1/resource2/resource3", ResponseCode.CHANGED },
                { Code.POST, "/service/resource1/resource2/resource3", ResponseCode.CHANGED },
                { Code.DELETE, "/service/resource1/resource2/resource3", ResponseCode.DELETED } }
        );
    }

    /**
     * Request code to test
     */
    @Parameter( 0 )
    public Code requestCode;

    /**
    * Test resource to call
    */
    @Parameter( 1 )
    public String resourcePath;

    /**
     * Expected response code to test
     */
    @Parameter( 2 )
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
    @Test
    public void testAddResource() throws Exception
    {
        setClientUri( resourcePath );
        Request request= new Request( requestCode );
        CoapResponse response1= client.advanced( request );
        assertNotNull( "request gave no response", response1 );
        assertEquals( "request gave wrong response", expectedResponseCode, response1.getCode() );
    }
}
