/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2025 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.test.client.basic;


import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.runtime.api.message.Message;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.api.attributes.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.attributes.Result;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;
import nl.teslanet.mule.connectors.coap.test.utils.Timing;


@RunnerDelegateTo( Parameterized.class )
public class AsyncNoResponseTest extends AbstractClientTestCase
{
    static boolean bools[]= { true, false };

    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters( name= "code= {0}, responseCode= {1}, requireSuccess= {2}, requireClientError {3},  requireServerError {4}" )
    public static Collection< Object[] > getTests()
    {
        ArrayList< Object[] > tests= new ArrayList< Object[] >();
        for ( ResponseCode code : ResponseCode.values() )
        {
            if ( code != ResponseCode._UNKNOWN_SUCCESS_CODE )
            {
                for ( boolean successValue : bools )
                {
                    for ( boolean clientErrorValue : bools )
                    {
                        for ( boolean serverErrorValue : bools )
                        {
                            tests.add( new Object []{ "POST", code, successValue, clientErrorValue, serverErrorValue } );
                        }
                    }
                }
            }
        }
        return tests;
    }

    /**
     * The request code to use.
     */
    @Parameter( 0 )
    public String requestCode;

    /**
     * The responseCode to test.
     */
    @Parameter( 1 )
    public ResponseCode responseCode;

    /**
     * Required success reponse to test .
     */
    @Parameter( 2 )
    public boolean requireSuccess;

    /**
     * Required client error reponse to test .
     */
    @Parameter( 3 )
    public boolean requireClientError;

    /**
     * Required server error reponse to test .
     */
    @Parameter( 4 )
    public boolean requireServerError;

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-client-config/basic/testclient-noresponse.xml";
    };

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase#getTestServer()
     */
    @Override
    protected CoapServer getTestServer() throws SocketException
    {
        return new ResponseTestServer();
    }

    /**
     * Test CoAP request that receives no response.
     * @throws Exception should not happen in this test
     */
    @Test
    public void testNoResponse() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "handler_spy" );
        spy.clear();

        flowRunner( "do_request_async" )
            .withPayload( "nothing_important" )
            .withVariable( "code", requestCode )
            .withVariable( "host", "127.0.0.1" )
            .withVariable( "port", "5683" )
            .withVariable( "path", "/response/" + responseCode.name() )
            .withVariable( "requireSuccess", requireSuccess )
            .withVariable( "requireClientError", requireClientError )
            .withVariable( "requireServerError", requireServerError )
            .run();
        if ( !( requireSuccess && responseCode.isSuccess() || requireClientError && responseCode.isClientError() || requireServerError && responseCode.isServerError() ) )
        {
            //let handler do its asynchronous work
            Timing.pauze();
            assertEquals( "should not receive response", 0, spy.getEvents().size() );
        }
        else
        {
            //let handler do its asynchronous work
            await().atMost( 10, TimeUnit.SECONDS ).until( () -> {
                return spy.getEvents().size() == 1;
            } );
            assertEquals( "spy has not been called once", 1, spy.getEvents().size() );
            Message response= (Message) spy.getEvents().get( 0 ).getContent();
            assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoapResponseAttributes );
            CoapResponseAttributes attributes= (CoapResponseAttributes) response.getAttributes().getValue();
            boolean responseReceived= attributes.getResult() != Result.NO_RESPONSE;
            if ( responseCode.isSuccess() )
            {
                assertEquals( "wrong succes response", requireSuccess, responseReceived );
            }
            else if ( responseCode.isClientError() )
            {
                assertEquals( "wrong client error response", requireClientError, responseReceived );
            }
            else if ( responseCode.isServerError() )
            {
                assertEquals( "wrong server error response", requireServerError, responseReceived );
            }
        }
    }
}
