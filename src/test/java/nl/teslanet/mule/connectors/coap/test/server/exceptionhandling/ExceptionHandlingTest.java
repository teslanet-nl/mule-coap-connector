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
package nl.teslanet.mule.connectors.coap.test.server.exceptionhandling;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Request;


/**
 * Test for inbound properties
 *
 */
@RunnerDelegateTo(Parameterized.class)
public class ExceptionHandlingTest extends AbstractServerTestCase
{
    /**
     * The type of exceptionhandling on the server
     *
     */
    public enum ExceptionHandling
    {
        HANDLED, UNHANDLED, NO_HANDLER
    }

    /**
     * @return the test parameters
     */
    @Parameters(name= "Request= {0}, path= {2}, exceptionHandling= {5}")
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []{
                //default maxResourceBodySize on server
                { Code.GET, "/service/handled_exception", true, ResponseCode.CONTENT, ExceptionHandling.HANDLED },
                { Code.PUT, "/service/handled_exception", false, ResponseCode.CHANGED, ExceptionHandling.HANDLED },
                { Code.POST, "/service/handled_exception", false, ResponseCode.CHANGED, ExceptionHandling.HANDLED },
                { Code.DELETE, "/service/handled_exception", true, ResponseCode.DELETED, ExceptionHandling.HANDLED },
                { Code.GET, "/service/unhandled_exception", true, ResponseCode.CONTENT, ExceptionHandling.UNHANDLED },
                { Code.PUT, "/service/unhandled_exception", false, ResponseCode.CHANGED, ExceptionHandling.UNHANDLED },
                { Code.POST, "/service/unhandled_exception", false, ResponseCode.CHANGED, ExceptionHandling.UNHANDLED },
                { Code.DELETE, "/service/unhandled_exception", true, ResponseCode.DELETED, ExceptionHandling.UNHANDLED },
                { Code.GET, "/service/no_listener", true, ResponseCode.CONTENT, ExceptionHandling.NO_HANDLER },
                { Code.PUT, "/service/no_listener", false, ResponseCode.CHANGED, ExceptionHandling.NO_HANDLER },
                { Code.POST, "/service/no_listener", false, ResponseCode.CHANGED, ExceptionHandling.NO_HANDLER },
                { Code.DELETE, "/service/no_listener", true, ResponseCode.DELETED, ExceptionHandling.NO_HANDLER }, } );
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
     * True when request is not supposed to have a payload, but does
     */
    @Parameter(2)
    public boolean unintendedPayload;

    /**
     * Expected response code to test
     */
    @Parameter(3)
    public ResponseCode ExpectedResponseCode;

    /**
     * Exceptionhandling used in the test
     */
    @Parameter(4)
    public ExceptionHandling exceptionHandling;

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/exceptionhandling/testserver1.xml";
    };

    @Test(timeout= 8000L)
    public void testException() throws Exception
    {
        client.setTimeout( 5000L );
        setClientPath( resourcePath );
        Request request= new Request( requestCode );
        if ( unintendedPayload ) request.setUnintendedPayload();
        request.setPayload( "nothing important" );

        CoapResponse response= client.advanced( request );
        switch ( exceptionHandling )
        {
            case HANDLED:
                assertNotNull( "request gave no response", response );
                assertTrue( "response indicates failure", response.isSuccess() );
                assertEquals( "wrong responsecode", ExpectedResponseCode, response.getCode() );
                assertEquals( "wrong response payload", "exception catched", response.getResponseText() );
                break;
            case UNHANDLED:
                assertNotNull( "request gave no response", response );
                assertFalse( "response does not indicate failure", response.isSuccess() );
                assertEquals( "wrong responsecode", ResponseCode.INTERNAL_SERVER_ERROR, response.getCode() );
                assertEquals( "wrong response payload", "EXCEPTION IN PROCESSING FLOW", response.getResponseText() );
                break;
            case NO_HANDLER:
                assertNotNull( "request gave no response", response );
                assertFalse( "response does not indicate failure", response.isSuccess() );
                assertEquals( "wrong responsecode", ResponseCode.INTERNAL_SERVER_ERROR, response.getCode() );
                assertEquals( "wrong response payload", "NO LISTENER", response.getResponseText() );
                break;
            default:
                break;
        }
    }
}
