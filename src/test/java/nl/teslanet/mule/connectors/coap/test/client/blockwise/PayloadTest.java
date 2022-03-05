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

import nl.teslanet.mule.connectors.coap.api.CoAPResponseAttributes;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.Data;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;


@RunnerDelegateTo( Parameterized.class )
public class PayloadTest extends AbstractClientTestCase
{
    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters( name= "flowName= {0}, resourcePath= {1}, requestPayloadSize= {2}, expectedResponseCode= {3}, expectedResponsePayloadSize= {4}" )
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []
            {
                { "do_get", "/blockwise/rq0", -1, "CONTENT", 2, false },
                { "do_post", "/blockwise/rq0", -1, "CREATED", 2, false },
                { "do_put", "/blockwise/rq0", -1, "CHANGED", 2, false },
                { "do_delete", "/blockwise/rq0", -1, "DELETED", 2, false },

                { "do_get", "/blockwise/rq0", 0, "CONTENT", 2, false },
                { "do_post", "/blockwise/rq0", 0, "CREATED", 2, false },
                { "do_put", "/blockwise/rq0", 0, "CHANGED", 2, false },
                { "do_delete", "/blockwise/rq0", 0, "DELETED", 2, false },

                { "do_get", "/blockwise/rsp0", 2, "BAD_REQUEST", -1, true },
                { "do_post", "/blockwise/rsp0", 2, "CREATED", -1, false },
                { "do_put", "/blockwise/rsp0", 2, "CHANGED", -1, false },
                { "do_delete", "/blockwise/rsp0", 2, "BAD_REQUEST", -1, true },

                { "do_get", "/blockwise/rq10", 10, "BAD_REQUEST", 2, true },
                { "do_get", "/blockwise/rsp10", 2, "BAD_REQUEST", 10, true },
                { "do_post", "/blockwise/rq10", 10, "CREATED", 2, false },
                { "do_post", "/blockwise/rsp10", 2, "CREATED", 10, false },
                { "do_put", "/blockwise/rq10", 10, "CHANGED", 2, false },
                { "do_put", "/blockwise/rsp10", 2, "CHANGED", 10, false },
                { "do_delete", "/blockwise/rq10", 10, "BAD_REQUEST", 2, true },
                { "do_delete", "/blockwise/rsp10", 2, "BAD_REQUEST", 10, true },

                { "do_get", "/blockwise/rq8192", 8192, "BAD_REQUEST", 2, true },
                { "do_get", "/blockwise/rsp8192", 2, "BAD_REQUEST", 8192, true },
                { "do_post", "/blockwise/rq8192", 8192, "CREATED", 2, false },
                { "do_post", "/blockwise/rsp8192", 2, "CREATED", 8192, false },
                { "do_put", "/blockwise/rq8192", 8192, "CHANGED", 2, false },
                { "do_put", "/blockwise/rsp8192", 2, "CHANGED", 8192, false },
                { "do_delete", "/blockwise/rq8192", 8192, "BAD_REQUEST", 2, true },
                { "do_delete", "/blockwise/rsp8192", 2, "BAD_REQUEST", 8192, true },

                { "do_get", "/blockwise/rq16000", 16000, "BAD_REQUEST", -1, true },
                { "do_get", "/blockwise/rsp16000", 2, "BAD_REQUEST", -1, true },
                { "do_post", "/blockwise/rq16000", 16000, "REQUEST_ENTITY_TOO_LARGE", -1, true },
                { "do_post", "/blockwise/rsp16000", 2, null, -1, true },
                { "do_put", "/blockwise/rq16000", 16000, "REQUEST_ENTITY_TOO_LARGE", -1, true },
                { "do_put", "/blockwise/rsp16000", 2, null, -1, true },
                { "do_delete", "/blockwise/rq16000", 16000, "BAD_REQUEST", -1, true },
                { "do_delete", "/blockwise/rsp16000", 2, "BAD_REQUEST", -1, true },

                { "do_get2", "/blockwise/rq16000", 16000, "BAD_REQUEST", 2, true },
                { "do_get2", "/blockwise/rsp16000", 2, "BAD_REQUEST", 16000, true },
                { "do_post2", "/blockwise/rq16000", 16000, "CREATED", 2, false },
                { "do_post2", "/blockwise/rsp16000", 2, "CREATED", 16000, false },
                { "do_put2", "/blockwise/rq16000", 16000, "CHANGED", 2, false },
                { "do_put2", "/blockwise/rsp16000", 2, "CHANGED", 16000, false },
                { "do_delete2", "/blockwise/rq16000", 16000, "BAD_REQUEST", 2, true },
                { "do_delete2", "/blockwise/rsp16000", 2, "BAD_REQUEST", 16000, true },

                { "do_get2", "/blockwise/rq16001", 16001, "BAD_REQUEST", -1, true },
                { "do_get2", "/blockwise/rsp16001", 2, "BAD_REQUEST", -1, true },
                { "do_post2", "/blockwise/rq16001", 16001, "REQUEST_ENTITY_TOO_LARGE", -1, true },
                { "do_post2", "/blockwise/rsp16001", 2, null, -1, true },
                { "do_put2", "/blockwise/rq16001", 16001, "REQUEST_ENTITY_TOO_LARGE", -1, true },
                { "do_put2", "/blockwise/rsp16001", 2, null, -1, true },
                { "do_delete2", "/blockwise/rq16001", 16001, "BAD_REQUEST", -1, true },
                { "do_delete2", "/blockwise/rsp16001", 2, "BAD_REQUEST", -1, true } }
        );
    }

    /**
     * The mule flow to call.
     */
    @Parameter( 0 )
    public String flowName;

    /**
     * The path of the resource to call.
     */
    @Parameter( 1 )
    public String resourcePath;

    /**
     * The request payload size to test.
     */
    @Parameter( 2 )
    public Integer requestPayloadSize;

    /**
     * The response code that is expected.
     */
    @Parameter( 3 )
    public String expectedResponseCode;

    /**
     * The response payload size to test.
     */
    @Parameter( 4 )
    public Integer expectedResponsePayloadSize;

    /**
     * True when response is Too Large to process
     */
    @Parameter( 5 )
    public boolean expectFailure;

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-client-config/blockwise/testclient1.xml";
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
        MuleEventSpy spy= new MuleEventSpy( flowName );
        spy.clear();

        flowRunner( flowName ).withVariable( "path", resourcePath ).withPayload( Data.getContent( requestPayloadSize ) ).run();

        assertEquals( "spy has wrong number of events", 1, spy.getEvents().size() );

        Message response= (Message) spy.getEvents().get( 0 ).getContent();
        byte[] payload= (byte[]) response.getPayload().getValue();

        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoAPResponseAttributes );

        CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
        if ( expectFailure )
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
