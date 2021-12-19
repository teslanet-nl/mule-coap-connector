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
package nl.teslanet.mule.connectors.coap.test.client.exceptionhandling;


import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.awaitility.core.ConditionTimeoutException;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.api.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.error.InvalidHandlerNameException;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.Code;


@RunnerDelegateTo(Parameterized.class)
public class ExceptionHandlingTest extends AbstractClientTestCase
{
    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters(name= "flowName= {0}, host= {1}, port= {2}, path= {3}, expectedResponseCode= {4}, expectedPayload= {5}")
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []{
                { "do_request", Code.GET, "127.0.0.1", "8976", "/service/get_me", "CONTENT", "coap://127.0.0.1:8976/service/get_me?", "Response is: CONTENT".getBytes() },
                { "do_request", Code.POST, "127.0.0.1", "8976", "/service/post_me", "CREATED", "coap://127.0.0.1:8976/service/post_me?", "Response is: CREATED".getBytes() },
                { "do_request", Code.PUT, "127.0.0.1", "8976", "/service/put_me", "CHANGED", "coap://127.0.0.1:8976/service/put_me?", "Response is: CHANGED".getBytes() },
                {
                    "do_request",
                    Code.DELETE,
                    "127.0.0.1",
                    "8976",
                    "/service/delete_me",
                    "DELETED",
                    "coap://127.0.0.1:8976/service/delete_me?",
                    "Response is: DELETED".getBytes() } } );
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
    public Code expectedRequestCode;

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
        return "mule-client-config/exceptionhandling/testclient1.xml";
    };

    /**
     * create the server
     * @throws Exception when server cannot be created
     */
    @Override
    protected CoapServer getTestServer() throws Exception
    {
        return new ExceptionHandlingTestServer( 8976 );
    }

    /**
     * Test catching exception in handler
     * @throws Exception should not happen in this test
     */
    @Test
    public void testCatchedException() throws Exception
    {
        MuleEventSpy spy1= new MuleEventSpy( "spy-me1" );
        spy1.clear();
        MuleEventSpy spy2= new MuleEventSpy( "spy-me2" );
        spy2.clear();
        MuleEventSpy spy3= new MuleEventSpy( "spy-me3" );
        spy3.clear();

        Event result= flowRunner( flowName ).withPayload( "nothing_important" ).withVariable( "code", expectedRequestCode.name() ).withVariable( "host", host ).withVariable(
            "port",
            port ).withVariable( "path", path ).withVariable( "handler", "catching_handler" ).run();
        Message response= result.getMessage();

        //let handler do its asynchronous work
        await().atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy1.getEvents().size() == 1 && spy2.getEvents().size() == 1;
        } );
        response= (Message) spy1.getEvents().get( 0 ).getContent();
        assertEquals(
            "wrong attributes class",
            new TypedValue< CoapResponseAttributes >( new CoapResponseAttributes(), null ).getClass(),
            response.getAttributes().getClass() );
        CoapResponseAttributes attributes= (CoapResponseAttributes) response.getAttributes().getValue();
        assertEquals( "wrong request code", expectedRequestCode.name(), attributes.getRequestCode() );
        assertEquals( "wrong request uri", expectedRequestUri, attributes.getRequestUri() );
        assertEquals( "wrong response code", expectedResponseCode, attributes.getResponseCode() );
        assertArrayEquals( "wrong response payload", expectedPayload, (byte[]) response.getPayload().getValue() );

        response= (Message) spy2.getEvents().get( 0 ).getContent();
        //message containing message!!
        response= (Message) response.getPayload().getValue();
        assertEquals(
            "wrong attributes class",
            new TypedValue< CoapResponseAttributes >( new CoapResponseAttributes(), null ).getClass(),
            response.getAttributes().getClass() );
        attributes= (CoapResponseAttributes) response.getAttributes().getValue();
        assertEquals( "wrong request code", expectedRequestCode.name(), attributes.getRequestCode() );
        assertEquals( "wrong request uri", expectedRequestUri, attributes.getRequestUri() );
        assertEquals( "wrong response code", expectedResponseCode, attributes.getResponseCode() );
        assertArrayEquals( "wrong response payload", expectedPayload, (byte[]) response.getPayload().getValue() );
        //wait some more time and see that spy 3 is really not called
        try
        {
            await().atMost( 1, TimeUnit.SECONDS );
        }
        catch ( ConditionTimeoutException e )
        {
            //as expected
        }
        assertEquals( "spy 3 has wrongfully been called", 0, spy3.getEvents().size() );
    }

    /**
     * Test uncatched exception in handler
     * @throws Exception should not happen in this test
     */
    @Test
    public void testUnCatchedException() throws Exception
    {
        MuleEventSpy spy1= new MuleEventSpy( "spy-me1" );
        spy1.clear();
        MuleEventSpy spy2= new MuleEventSpy( "spy-me2" );
        spy2.clear();
        MuleEventSpy spy3= new MuleEventSpy( "spy-me3" );
        spy3.clear();

        Event result= flowRunner( flowName ).withPayload( "nothing_important" ).withVariable( "code", expectedRequestCode.name() ).withVariable( "host", host ).withVariable(
            "port",
            port ).withVariable( "path", path ).withVariable( "handler", "failing_handler" ).run();
        Message response= result.getMessage();

        //let handler do its asynchronous work
        try
        {
            await().atMost( 1, TimeUnit.SECONDS ).until( () -> {
                return false;
            } );
        }
        catch ( ConditionTimeoutException e )
        {
            //as expected
        }

        assertEquals( "spy 2 has wrongfully been called", 0, spy2.getEvents().size() );

        assertEquals( "spy has not been called once", 1, spy3.getEvents().size() );
        response= (Message) spy3.getEvents().get( 0 ).getContent();
        assertEquals(
            "wrong attributes class",
            new TypedValue< CoapResponseAttributes >( new CoapResponseAttributes(), null ).getClass(),
            response.getAttributes().getClass() );
        CoapResponseAttributes attributes= (CoapResponseAttributes) response.getAttributes().getValue();
        assertEquals( "wrong request code", expectedRequestCode.name(), attributes.getRequestCode() );
        assertEquals( "wrong request uri", expectedRequestUri, attributes.getRequestUri() );
        assertEquals( "wrong response code", expectedResponseCode, attributes.getResponseCode() );
        assertArrayEquals( "wrong response payload", expectedPayload, (byte[]) response.getPayload().getValue() );
    }

    /**
     * Test uncatched exception in handler
     * @throws Exception should not happen in this test
     */
    @Test
    public void testNonExistingHandler() throws Exception
    {
        //exception.expect( hasMessage( containsString( "referenced handler { nonexisting_handler } not found" ) ) );

        Exception e= assertThrows( Exception.class, () -> {
            flowRunner( flowName ).withPayload( "nothing_important" ).withVariable( "code", expectedRequestCode.name() ).withVariable( "host", host ).withVariable(
                "port",
                port ).withVariable( "path", path ).withVariable( "handler", "nonexisting_handler" ).run();
        } );
        assertTrue( "wrong exception message", e.getMessage().contains( "async request failed" ) );
        assertEquals( "wrong exception cause", InvalidHandlerNameException.class, e.getCause().getClass() );
    }

}
