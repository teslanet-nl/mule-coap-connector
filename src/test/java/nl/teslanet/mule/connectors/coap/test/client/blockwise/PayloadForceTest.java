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
package nl.teslanet.mule.connectors.coap.test.client.blockwise;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.californium.core.CoapServer;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.runtime.api.message.Message;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.api.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.Data;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;


@RunnerDelegateTo( Parameterized.class )
public class PayloadForceTest extends AbstractClientTestCase
{
    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters( name= "flowName= {0}, request= {1}, resourcePath= {2}, requestPayloadSize= {3}, expectedResponseCode= {4}" )
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []
            {
                { "do_request", "GET", "/blockwise/rq0", -1, "CONTENT", 2, false },
                { "do_request", "POST", "/blockwise/rq0", -1, "CREATED", 2, false },
                { "do_request", "PUT", "/blockwise/rq0", -1, "CHANGED", 2, false },
                { "do_request", "DELETE", "/blockwise/rq0", -1, "DELETED", 2, false },
                { "do_request", "FETCH", "/blockwise/rq0", -1, "CONTENT", 2, false },
                { "do_request", "PATCH", "/blockwise/rq0", -1, "CHANGED", 2, false },
                { "do_request", "IPATCH", "/blockwise/rq0", -1, "CHANGED", 2, false },

                { "do_request", "GET", "/blockwise/rq0", 0, "CONTENT", 2, false },
                { "do_request", "POST", "/blockwise/rq0", 0, "CREATED", 2, false },
                { "do_request", "PUT", "/blockwise/rq0", 0, "CHANGED", 2, false },
                { "do_request", "DELETE", "/blockwise/rq0", 0, "DELETED", 2, false },
                { "do_request", "FETCH", "/blockwise/rq0", 0, "CONTENT", 2, false },
                { "do_request", "PATCH", "/blockwise/rq0", 0, "CHANGED", 2, false },
                { "do_request", "IPATCH", "/blockwise/rq0", 0, "CHANGED", 2, false },

                { "do_request", "GET", "/blockwise/rsp0", 2, "CONTENT", -1, false },
                { "do_request", "POST", "/blockwise/rsp0", 2, "CREATED", -1, false },
                { "do_request", "PUT", "/blockwise/rsp0", 2, "CHANGED", -1, false },
                { "do_request", "DELETE", "/blockwise/rsp0", 2, "DELETED", -1, false },
                { "do_request", "FETCH", "/blockwise/rsp0", 2, "CONTENT", -1, false },
                { "do_request", "PATCH", "/blockwise/rsp0", 2, "CHANGED", -1, false },
                { "do_request", "IPATCH", "/blockwise/rsp0", 2, "CHANGED", -1, false },

                { "do_request", "GET", "/blockwise/rq10", 10, "CONTENT", 2, false },
                { "do_request", "GET", "/blockwise/rsp10", 2, "CONTENT", 10, false },
                { "do_request", "POST", "/blockwise/rq10", 10, "CREATED", 2, false },
                { "do_request", "POST", "/blockwise/rsp10", 2, "CREATED", 10, false },
                { "do_request", "PUT", "/blockwise/rq10", 10, "CHANGED", 2, false },
                { "do_request", "PUT", "/blockwise/rsp10", 2, "CHANGED", 10, false },
                { "do_request", "DELETE", "/blockwise/rq10", 10, "DELETED", 2, false },
                { "do_request", "DELETE", "/blockwise/rsp10", 2, "DELETED", 10, false },
                { "do_request", "FETCH", "/blockwise/rq10", 10, "CONTENT", 2, false },
                { "do_request", "FETCH", "/blockwise/rsp10", 2, "CONTENT", 10, false },
                { "do_request", "PATCH", "/blockwise/rq10", 10, "CHANGED", 2, false },
                { "do_request", "PATCH", "/blockwise/rsp10", 2, "CHANGED", 10, false },
                { "do_request", "IPATCH", "/blockwise/rq10", 10, "CHANGED", 2, false },
                { "do_request", "IPATCH", "/blockwise/rsp10", 2, "CHANGED", 10, false },

                { "do_request", "GET", "/blockwise/rq8192", 8192, "CONTENT", 2, false },
                { "do_request", "GET", "/blockwise/rsp8192", 2, "CONTENT", 8192, false },
                { "do_request", "POST", "/blockwise/rq8192", 8192, "CREATED", 2, false },
                { "do_request", "POST", "/blockwise/rsp8192", 2, "CREATED", 8192, false },
                { "do_request", "PUT", "/blockwise/rq8192", 8192, "CHANGED", 2, false },
                { "do_request", "PUT", "/blockwise/rsp8192", 2, "CHANGED", 8192, false },
                { "do_request", "DELETE", "/blockwise/rq8192", 8192, "DELETED", 2, false },
                { "do_request", "DELETE", "/blockwise/rsp8192", 2, "DELETED", 8192, false },
                { "do_request", "FETCH", "/blockwise/rq8192", 8192, "CONTENT", 2, false },
                { "do_request", "FETCH", "/blockwise/rsp8192", 2, "CONTENT", 8192, false },
                { "do_request", "PATCH", "/blockwise/rq8192", 8192, "CHANGED", 2, false },
                { "do_request", "PATCH", "/blockwise/rsp8192", 2, "CHANGED", 8192, false },
                { "do_request", "IPATCH", "/blockwise/rq8192", 8192, "CHANGED", 2, false },
                { "do_request", "IPATCH", "/blockwise/rsp8192", 2, "CHANGED", 8192, false },

                { "do_request", "GET", "/blockwise/rq16000", 16000, "REQUEST_ENTITY_TOO_LARGE", -1, true },
                { "do_request", "GET", "/blockwise/rsp16000", 2, null, -1, true },
                { "do_request", "POST", "/blockwise/rq16000", 16000, "REQUEST_ENTITY_TOO_LARGE", -1, true },
                { "do_request", "POST", "/blockwise/rsp16000", 2, null, -1, true },
                { "do_request", "PUT", "/blockwise/rq16000", 16000, "REQUEST_ENTITY_TOO_LARGE", -1, true },
                { "do_request", "PUT", "/blockwise/rsp16000", 2, null, -1, true },
                { "do_request", "DELETE", "/blockwise/rq16000", 16000, "REQUEST_ENTITY_TOO_LARGE", -1, true },
                { "do_request", "DELETE", "/blockwise/rsp16000", 2, null, -1, true },
                { "do_request", "FETCH", "/blockwise/rq16000", 16000, "REQUEST_ENTITY_TOO_LARGE", -1, true },
                { "do_request", "FETCH", "/blockwise/rsp16000", 2, null, -1, true },
                { "do_request", "PATCH", "/blockwise/rq16000", 16000, "REQUEST_ENTITY_TOO_LARGE", -1, true },
                { "do_request", "PATCH", "/blockwise/rsp16000", 2, null, -1, true },
                { "do_request", "IPATCH", "/blockwise/rq16000", 16000, "REQUEST_ENTITY_TOO_LARGE", -1, true },
                { "do_request", "IPATCH", "/blockwise/rsp16000", 2, null, -1, true },

                { "do_request2", "GET", "/blockwise/rq16000", 16000, "CONTENT", 2, false },
                { "do_request2", "GET", "/blockwise/rsp16000", 2, "CONTENT", 16000, false },
                { "do_request2", "POST", "/blockwise/rq16000", 16000, "CREATED", 2, false },
                { "do_request2", "POST", "/blockwise/rsp16000", 2, "CREATED", 16000, false },
                { "do_request2", "PUT", "/blockwise/rq16000", 16000, "CHANGED", 2, false },
                { "do_request2", "PUT", "/blockwise/rsp16000", 2, "CHANGED", 16000, false },
                { "do_request2", "DELETE", "/blockwise/rq16000", 16000, "DELETED", 2, false },
                { "do_request2", "DELETE", "/blockwise/rsp16000", 2, "DELETED", 16000, false },
                { "do_request2", "FETCH", "/blockwise/rq16000", 16000, "CONTENT", 2, false },
                { "do_request2", "FETCH", "/blockwise/rsp16000", 2, "CONTENT", 16000, false },
                { "do_request2", "PATCH", "/blockwise/rq16000", 16000, "CHANGED", 2, false },
                { "do_request2", "PATCH", "/blockwise/rsp16000", 2, "CHANGED", 16000, false },
                { "do_request2", "IPATCH", "/blockwise/rq16000", 16000, "CHANGED", 2, false },
                { "do_request2", "IPATCH", "/blockwise/rsp16000", 2, "CHANGED", 16000, false },

                { "do_request2", "GET", "/blockwise/rq16001", 16001, "REQUEST_ENTITY_TOO_LARGE", -1, true },
                { "do_request2", "GET", "/blockwise/rsp16001", 2, null, -1, true },
                { "do_request2", "POST", "/blockwise/rq16001", 16001, "REQUEST_ENTITY_TOO_LARGE", -1, true },
                { "do_request2", "POST", "/blockwise/rsp16001", 2, null, -1, true },
                { "do_request2", "PUT", "/blockwise/rq16001", 16001, "REQUEST_ENTITY_TOO_LARGE", -1, true },
                { "do_request2", "PUT", "/blockwise/rsp16001", 2, null, -1, true },
                { "do_request2", "DELETE", "/blockwise/rq16001", 16001, "REQUEST_ENTITY_TOO_LARGE", -1, true },
                { "do_request2", "DELETE", "/blockwise/rsp16001", 2, null, -1, true },
                { "do_request2", "FETCH", "/blockwise/rq16001", 16001, "REQUEST_ENTITY_TOO_LARGE", -1, true },
                { "do_request2", "FETCH", "/blockwise/rsp16001", 2, null, -1, true },
                { "do_request2", "PATCH", "/blockwise/rq16001", 16001, "REQUEST_ENTITY_TOO_LARGE", -1, true },
                { "do_request2", "PATCH", "/blockwise/rsp16001", 2, null, -1, true },
                { "do_request2", "IPATCH", "/blockwise/rq16001", 16001, "REQUEST_ENTITY_TOO_LARGE", -1, true },
                { "do_request2", "IPATCH", "/blockwise/rsp16001", 2, null, -1, true } }
        );
    }

    /**
     * The mule flow to call.
     */
    @Parameter( 0 )
    public String flowName;

    /**
     * The request.
     */
    @Parameter( 1 )
    public String request;

    /**
     * The path of the resource to call.
     */
    @Parameter( 2 )
    public String resourcePath;

    /**
     * The request payload size to test.
     */
    @Parameter( 3 )
    public Integer requestPayloadSize;

    /**
     * The response code that is expected.
     */
    @Parameter( 4 )
    public String expectedResponseCode;

    /**
     * The response payload size to test.
     */
    @Parameter( 5 )
    public Integer expectedResponsePayloadSize;

    /**
     * True when response is Too Large to process
     */
    @Parameter( 6 )
    public boolean expectTooLarge;

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-client-config/blockwise/testclient2.xml";
    };

    /**
     * Create test-servers
     * @throws Exception when servers cannot be created
     */
    @Override
    protected CoapServer[] getTestServers() throws Exception
    {
        return new CoapServer []{ new TestServer(), new TestServer( 5685, 16000 ) };
    }

    //TODO BlockwiseLayer calls responseExceedsMaxBodySize(response) but does debug logging only
    //this way reason of canceling is not explicit (should log error)
    //TODO add check for request isCancelled
    /** 
     * Test CoAP request with request or response payload
     * @throws Exception should not happen in this test
     */
    @Test( timeout= 100000 )
    public void testPayload() throws Exception
    {
        String spyName= flowName + request + resourcePath;
        MuleEventSpy spy= new MuleEventSpy( spyName );
        spy.clear();

        flowRunner( flowName ).withVariable( "requestCode", request ).withVariable( "path", resourcePath ).withVariable( "spyName", spyName ).withVariable(
            "forcePayload",
            "true"
        ).withPayload( Data.getContent( requestPayloadSize ) ).run();

        assertEquals( "spy has wrong number of events", 1, spy.getEvents().size() );

        Message response= (Message) spy.getEvents().get( 0 ).getContent();
        byte[] payload= (byte[]) response.getPayload().getValue();

        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoapResponseAttributes );

        CoapResponseAttributes attributes= (CoapResponseAttributes) response.getAttributes().getValue();
        if ( expectTooLarge )
        {
            assertFalse( "request should fail", attributes.isSuccess() );
            assertEquals( "wrong response code", expectedResponseCode, attributes.getResponseCode() );
        }
        else
        {
            assertTrue( "request failed", attributes.isSuccess() );
            assertEquals( "wrong response code", expectedResponseCode, attributes.getResponseCode() );
            assertEquals( "wrong response size", expectedResponsePayloadSize.intValue(), ( payload == null ? -1 : payload.length ) );
            assertTrue( "wrong response payload contents", Data.validateContent( payload, expectedResponsePayloadSize ) );
        }
    }
}
