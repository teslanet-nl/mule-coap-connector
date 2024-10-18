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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import org.mule.runtime.api.streaming.bytes.CursorStreamProvider;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.api.Defs;
import nl.teslanet.mule.connectors.coap.api.attributes.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;


@RunnerDelegateTo( Parameterized.class )
public class BasicTest extends AbstractClientTestCase
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
            { { "get_me", "GET", "coap://127.0.0.1/basic/get_me?test=sync", "CONTENT",
                "GET called on: coap://localhost/basic/get_me?test=sync" },
                { "do_not_get_me", "GET", "coap://127.0.0.1/basic/do_not_get_me?test=sync", "METHOD_NOT_ALLOWED", "" },
                { "post_me", "POST", "coap://127.0.0.1/basic/post_me?test=sync", "CREATED",
                    "POST called on: coap://localhost/basic/post_me?test=sync" },
                { "do_not_post_me", "POST", "coap://127.0.0.1/basic/do_not_post_me?test=sync", "METHOD_NOT_ALLOWED",
                    "" },
                { "put_me", "PUT", "coap://127.0.0.1/basic/put_me?test=sync", "CHANGED",
                    "PUT called on: coap://localhost/basic/put_me?test=sync" },
                { "do_not_put_me", "PUT", "coap://127.0.0.1/basic/do_not_put_me?test=sync", "METHOD_NOT_ALLOWED", "" },
                { "delete_me", "DELETE", "coap://127.0.0.1/basic/delete_me?test=sync", "DELETED",
                    "DELETE called on: coap://localhost/basic/delete_me?test=sync" },
                { "do_not_delete_me", "DELETE", "coap://127.0.0.1/basic/do_not_delete_me?test=sync",
                    "METHOD_NOT_ALLOWED", "" },
                { "fetch_me", "FETCH", "coap://127.0.0.1/basic/fetch_me?test=sync", "CONTENT",
                    "FETCH called on: coap://localhost/basic/fetch_me?test=sync" },
                { "do_not_fetch_me", "FETCH", "coap://127.0.0.1/basic/do_not_fetch_me?test=sync", "METHOD_NOT_ALLOWED",
                    "" },
                { "patch_me", "PATCH", "coap://127.0.0.1/basic/patch_me?test=sync", "CREATED",
                    "PATCH called on: coap://localhost/basic/patch_me?test=sync" },
                { "do_not_patch_me", "PATCH", "coap://127.0.0.1/basic/do_not_patch_me?test=sync", "METHOD_NOT_ALLOWED",
                    "" },
                { "ipatch_me", "IPATCH", "coap://127.0.0.1/basic/ipatch_me?test=sync", "CHANGED",
                    "IPATCH called on: coap://localhost/basic/ipatch_me?test=sync" },
                { "do_not_ipatch_me", "IPATCH", "coap://127.0.0.1/basic/do_not_ipatch_me?test=sync",
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
    public String expectedRequestCode;

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
    @Test
    public void testConRequest() throws Exception
    {
        CoreEvent result= flowRunner( flowName ).keepStreamsOpen().withPayload( "nothing_important" ).run();
        Message response= result.getMessage();

        assertNotNull( "no mule event", response );
        CursorStreamProvider responsePayload= (CursorStreamProvider) TypedValue.unwrap( response.getPayload() );
        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoapResponseAttributes );
        CoapResponseAttributes attributes= (CoapResponseAttributes) response.getAttributes().getValue();
        assertEquals( "wrong message type", "CONFIRMABLE", attributes.getRequestType() );
        assertEquals( "wrong request code", expectedRequestCode, attributes.getRequestCode() );
        assertEquals( "wrong request uri", expectedRequestUri, attributes.getRequestUri() );
        assertEquals( "wrong response type", "ACKNOWLEDGEMENT", attributes.getResponseType() );
        assertEquals( "wrong response code", expectedResponseCode, attributes.getResponseCode() );
        assertEquals(
            "wrong response payload",
            expectedPayload,
            responsePayload == null
                ? "" : new String( IOUtils.toByteArray( responsePayload.openCursor() ), Defs.COAP_CHARSET )
        );
    }

    /**
     * Test CoAP request
     * @throws Exception should not happen in this test
     */
    @Test
    public void testNonRequest() throws Exception
    {
        CoreEvent result= flowRunner( flowName + "_non" ).keepStreamsOpen().withPayload( "nothing_important" ).run();
        Message response= result.getMessage();

        assertNotNull( "no mule event", response );
        CursorStreamProvider responsePayload= (CursorStreamProvider) TypedValue.unwrap( response.getPayload() );
        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoapResponseAttributes );
        CoapResponseAttributes attributes= (CoapResponseAttributes) response.getAttributes().getValue();
        assertEquals( "wrong message type", "NON_CONFIRMABLE", attributes.getRequestType() );
        assertEquals( "wrong request code", expectedRequestCode, attributes.getRequestCode() );
        assertEquals( "wrong request uri", expectedRequestUri, attributes.getRequestUri() );
        assertEquals( "wrong response type", "NON_CONFIRMABLE", attributes.getResponseType() );
        assertEquals( "wrong response code", expectedResponseCode, attributes.getResponseCode() );
        assertEquals(
            "wrong response payload",
            expectedPayload,
            responsePayload == null
                ? "" : new String( IOUtils.toByteArray( responsePayload.openCursor() ), Defs.COAP_CHARSET )
        );
    }
}
