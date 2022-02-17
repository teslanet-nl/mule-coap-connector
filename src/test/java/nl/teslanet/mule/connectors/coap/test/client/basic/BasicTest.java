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

import java.net.SocketException;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.californium.core.CoapServer;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.api.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.RequestParams.CoAPRequestCode;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;


@RunnerDelegateTo(Parameterized.class)
public class BasicTest extends AbstractClientTestCase
{
    //TODO RC add query
    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters(name= "flowName= {0}")
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []
            {
                { "get_me", CoAPRequestCode.GET.name(), "coap://127.0.0.1/basic/get_me", "CONTENT", "GET called on: /basic/get_me".getBytes() },
                { "do_not_get_me", CoAPRequestCode.GET.name(), "coap://127.0.0.1/basic/do_not_get_me", "METHOD_NOT_ALLOWED", null },
                { "post_me", CoAPRequestCode.POST.name(), "coap://127.0.0.1/basic/post_me", "CREATED", "POST called on: /basic/post_me".getBytes() },
                { "do_not_post_me", CoAPRequestCode.POST.name(), "coap://127.0.0.1/basic/do_not_post_me", "METHOD_NOT_ALLOWED", null },
                { "put_me", CoAPRequestCode.PUT.name(), "coap://127.0.0.1/basic/put_me", "CHANGED", "PUT called on: /basic/put_me".getBytes() },
                { "do_not_put_me", CoAPRequestCode.PUT.name(), "coap://127.0.0.1/basic/do_not_put_me", "METHOD_NOT_ALLOWED", null },
                { "delete_me", CoAPRequestCode.DELETE.name(), "coap://127.0.0.1/basic/delete_me", "DELETED", "DELETE called on: /basic/delete_me".getBytes() },
                { "do_not_delete_me", CoAPRequestCode.DELETE.name(), "coap://127.0.0.1/basic/do_not_delete_me", "METHOD_NOT_ALLOWED", null } }
        );
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
    public String expectedRequestCode;

    /**
     * The request uri that is expected.
     */
    @Parameter(2)
    public String expectedRequestUri;

    /**
     * The response code that is expected.
     */
    @Parameter(3)
    public String expectedResponseCode;

    /**
     * The payload code that is expected.
     */
    @Parameter(4)
    public byte[] expectedPayload;

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-client-config/basic/testclient1.xml";
    };

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase#getTestServer()
     */
    @Override
    protected CoapServer getTestServer() throws SocketException
    {
        return new BasicTestServer();
    }

    /**
     * Test CoAP request
     * @throws Exception should not happen in this test
     */
    @Test( timeout=5000 )
    public void testRequest() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( flowName );
        spy.clear();
        
        flowRunner( flowName ).withPayload( "nothing_important" ).run();
        
        assertEquals( "spy has not been called once", 1, spy.getEvents().size() );
        Message response= (Message) spy.getEvents().get( 0 ).getContent();
        byte[] payload= (byte[]) response.getPayload().getValue();
        assertEquals(
            "wrong attributes class",
            new TypedValue< CoapResponseAttributes >( new CoapResponseAttributes(), null ).getClass(),
            response.getAttributes().getClass() );
        CoapResponseAttributes attributes= (CoapResponseAttributes) response.getAttributes().getValue();
        assertEquals( "wrong request code", expectedRequestCode, attributes.getRequestCode() );
        assertEquals( "wrong request uri", expectedRequestUri, attributes.getRequestUri() );
        assertEquals( "wrong response code", expectedResponseCode, attributes.getResponseCode() );
        assertArrayEquals( "wrong response payload", expectedPayload, payload );

    }

}
