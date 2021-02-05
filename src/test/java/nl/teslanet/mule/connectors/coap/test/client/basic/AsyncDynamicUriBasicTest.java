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
package nl.teslanet.mule.connectors.coap.test.client.basic;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.SocketException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.api.ReceivedResponseAttributes;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;
import nl.teslanet.shaded.org.eclipse.californium.core.CoapServer;


@RunnerDelegateTo(Parameterized.class)
public class AsyncDynamicUriBasicTest extends AbstractClientTestCase
{
    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters(name= "code= {1}, host= {2}, port= {3}, path= {4}")
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []{
                { "do_request", "GET", "127.0.0.1", "8976", "/basic/get_me", "CONTENT", "coap://127.0.0.1:8976/basic/get_me?", "GET called on: /basic/get_me".getBytes() },
                { "do_request", "GET", "127.0.0.1", "8976", "/basic/do_not_get_me", "METHOD_NOT_ALLOWED", "coap://127.0.0.1:8976/basic/do_not_get_me?", null },
                { "do_request", "POST", "127.0.0.1", "8976", "/basic/post_me", "CREATED", "coap://127.0.0.1:8976/basic/post_me?", "POST called on: /basic/post_me".getBytes() },
                { "do_request", "POST", "127.0.0.1", "8976", "/basic/do_not_post_me", "METHOD_NOT_ALLOWED", "coap://127.0.0.1:8976/basic/do_not_post_me?", null },
                { "do_request", "PUT", "127.0.0.1", "8976", "/basic/put_me", "CHANGED", "coap://127.0.0.1:8976/basic/put_me?", "PUT called on: /basic/put_me".getBytes() },
                { "do_request", "PUT", "127.0.0.1", "8976", "/basic/do_not_put_me", "METHOD_NOT_ALLOWED", "coap://127.0.0.1:8976/basic/do_not_put_me?", null },
                {
                    "do_request",
                    "DELETE",
                    "127.0.0.1",
                    "8976",
                    "/basic/delete_me",
                    "DELETED",
                    "coap://127.0.0.1:8976/basic/delete_me?",
                    "DELETE called on: /basic/delete_me".getBytes() },
                { "do_request", "DELETE", "127.0.0.1", "8976", "/basic/do_not_delete_me", "METHOD_NOT_ALLOWED", "coap://127.0.0.1:8976/basic/do_not_delete_me?", null } } );
    }

    /**
     * The mule flow to call.
     */
    @Parameter(0)
    public String flowName;

    /**
     * The request code that is expected.
     */
    @Parameter(1)
    public String requestCode;

    /**
     * The server host to call.
     */
    @Parameter(2)
    public String host;

    /**
     * The server port to call.
     */
    @Parameter(3)
    public String port;

    /**
     * The server path to call.
     */
    @Parameter(4)
    public String path;

    /**
     * The response code that is expected.
     */
    @Parameter(5)
    public String expectedResponseCode;

    /**
     * The request uri that is expected.
     */
    @Parameter(6)
    public String expectedRequestUri;

    /**
     * The payload code that is expected.
     */
    @Parameter(7)
    public byte[] expectedPayload;

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-client-config/basic/testclient4.xml";
    };

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase#getTestServer()
     */
    @Override
    protected CoapServer getTestServer() throws SocketException
    {
        return new BasicTestServer( 8976 );
    }

    /**
     * Test Async request
     * @throws Exception should not happen in this test
     */
    @Test(timeout= 15000L)
    public void testAsyncRequest() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "handler_spy" );
        spy.clear();

        Thread.sleep( 2000 );

        Event result= flowRunner( flowName ).withPayload( "nothing_important" ).withVariable( "code", requestCode ).withVariable( "host", host ).withVariable(
            "port",
            port ).withVariable( "path", path ).run();
        Message response= result.getMessage();

        assertTrue( "wrong response payload", response.getPayload().getDataType().isCompatibleWith( DataType.STRING ) );
        assertEquals( "wrong response payload", "nothing_important", (String) response.getPayload().getValue() );

        //let handler do its asynchronous work
        Thread.sleep( 10000L );

        assertEquals( "spy has not been called once", 1, spy.getEvents().size() );
        response= (Message) spy.getEvents().get( 0 ).getContent();
        assertEquals(
            "wrong attributes class",
            new TypedValue< ReceivedResponseAttributes >( new ReceivedResponseAttributes(), null ).getClass(),
            response.getAttributes().getClass() );
        ReceivedResponseAttributes attributes= (ReceivedResponseAttributes) response.getAttributes().getValue();
        byte[] payload= (byte[]) (response.getPayload().getValue());
        assertEquals( "wrong request code", requestCode, attributes.getRequestCode() );
        assertEquals( "wrong request uri", expectedRequestUri, attributes.getRequestUri() );
        assertEquals( "wrong response code", expectedResponseCode, attributes.getResponseCode() );
        assertArrayEquals( "wrong response payload", expectedPayload, payload );
    }

}
