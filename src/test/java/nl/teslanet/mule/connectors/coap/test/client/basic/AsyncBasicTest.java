/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2024 (teslanet.nl) Rogier Cobben
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
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.DataType;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.api.Defs;
import nl.teslanet.mule.connectors.coap.api.attributes.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;


@RunnerDelegateTo( Parameterized.class )
public class AsyncBasicTest extends AbstractClientTestCase
{
    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters( name= "flowName= {0}" )
    public static Collection< Object[] > data()
    {
        return Arrays
            .asList( new Object [] []
            { { "get_me", Code.GET, "coap://127.0.0.1/basic/get_me?test=async", "CONTENT",
                "GET called on: coap://localhost/basic/get_me?test=async" },
                { "do_not_get_me", Code.GET, "coap://127.0.0.1/basic/do_not_get_me?test=async", "METHOD_NOT_ALLOWED",
                    "" },
                { "post_me", Code.POST, "coap://127.0.0.1/basic/post_me?test=async", "CREATED",
                    "POST called on: coap://localhost/basic/post_me?test=async" },
                { "do_not_post_me", Code.POST, "coap://127.0.0.1/basic/do_not_post_me?test=async", "METHOD_NOT_ALLOWED",
                    "" },
                { "put_me", Code.PUT, "coap://127.0.0.1/basic/put_me?test=async", "CHANGED",
                    "PUT called on: coap://localhost/basic/put_me?test=async" },
                { "do_not_put_me", Code.PUT, "coap://127.0.0.1/basic/do_not_put_me?test=async", "METHOD_NOT_ALLOWED",
                    "" },
                { "delete_me", Code.DELETE, "coap://127.0.0.1/basic/delete_me?test=async", "DELETED",
                    "DELETE called on: coap://localhost/basic/delete_me?test=async" },
                { "do_not_delete_me", Code.DELETE, "coap://127.0.0.1/basic/do_not_delete_me?test=async",
                    "METHOD_NOT_ALLOWED", "" } } );
    }

    /**
     * The mule flow to call.
     */
    @Parameter( 0 )
    public String flowName;

    /**
     * The request code that is expected.
     */
    @Parameter( 1 )
    public Code expectedRequestCode;

    /**
     * The request uri that is expected.
     */
    @Parameter( 2 )
    public String expectedRequestUri;

    /**
     * The response code that is expected.
     */
    @Parameter( 3 )
    public String expectedResponseCode;

    /**
     * The payload code that is expected.
     */
    @Parameter( 4 )
    public String expectedPayload;

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-client-config/basic/testclient2.xml";
    };

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase#getTestServer()
     */
    @Override
    protected CoapServer getTestServer() throws Exception
    {
        return new BasicTestServer();
    }

    /**
     * Test Async request
     * @throws Exception should not happen in this test
     */
    @Test( timeout= 20000L )
    public void testAsyncRequest() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "async-handler" );
        spy.clear();

        Event result= flowRunner( flowName ).withPayload( "nothing_important" ).run();
        Message response= result.getMessage();

        assertTrue( "wrong response payload", response.getPayload().getDataType().isCompatibleWith( DataType.STRING ) );
        assertEquals( "wrong response payload", "nothing_important", (String) response.getPayload().getValue() );

        //let handler do its asynchronous work
        await().atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == 1;
        } );
        // assertions...
        response= (Message) spy.getEvents().get( 0 ).getContent();
        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoapResponseAttributes );
        CoapResponseAttributes attributes= (CoapResponseAttributes) response.getAttributes().getValue();
        assertEquals( "wrong message type", "CONFIRMABLE", attributes.getRequestType() );
        assertEquals( "wrong request code", expectedRequestCode.name(), attributes.getRequestCode() );
        assertEquals( "wrong request uri", expectedRequestUri, attributes.getRequestUri() );
        assertEquals( "wrong response type", "ACKNOWLEDGEMENT", attributes.getResponseType() );
        assertEquals( "wrong response code", expectedResponseCode, attributes.getResponseCode() );
        assertArrayEquals(
            "wrong response payload",
            expectedPayload.getBytes( Defs.COAP_CHARSET ),
            (byte[]) response.getPayload().getValue()
        );
    }

}
