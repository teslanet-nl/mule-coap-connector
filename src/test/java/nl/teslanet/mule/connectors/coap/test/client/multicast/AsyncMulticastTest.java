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
package nl.teslanet.mule.connectors.coap.test.client.multicast;


import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
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

import nl.teslanet.mule.connectors.coap.api.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;


/**
 *  Multicast test.
 *  The system needs to allow multicast on the loopback interface:
 *  > sysctl -w net.ipv4.icmp_echo_ignore_broadcasts=0
 *  > ip -f inet maddr show dev lo
 */
@RunnerDelegateTo( Parameterized.class )
public class AsyncMulticastTest extends AbstractClientTestCase
{
    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters( name= "flowName= {0}" )
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []
            {
                { "get_me", Code.GET, "coap://224.0.1.187/basic/get_me", "CONTENT", "GET called on: /basic/get_me".getBytes() },
                { "do_not_get_me", Code.GET, "coap://224.0.1.187/basic/do_not_get_me", null, null },
                { "post_me", Code.POST, "coap://224.0.1.187/basic/post_me", "CREATED", "POST called on: /basic/post_me".getBytes() },
                { "do_not_post_me", Code.POST, "coap://224.0.1.187/basic/do_not_post_me", null, null },
                { "put_me", Code.PUT, "coap://224.0.1.187/basic/put_me", "CHANGED", "PUT called on: /basic/put_me".getBytes() },
                { "do_not_put_me", Code.PUT, "coap://224.0.1.187/basic/do_not_put_me", null, null },
                { "delete_me", Code.DELETE, "coap://224.0.1.187/basic/delete_me", "DELETED", "DELETE called on: /basic/delete_me".getBytes() },
                { "do_not_delete_me", Code.DELETE, "coap://224.0.1.187/basic/do_not_delete_me", null, null } }
        );
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
    public byte[] expectedPayload;

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-client-config/multicast/testclient2.xml";
    };

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase#getTestServer()
     */
    @Override
    protected CoapServer getTestServer() throws Exception
    {
        return new MulticastTestServer();
    }

    /**
     * Test Async request
     * @throws Exception should not happen in this test
     */
    @Test
    public void testAsyncRequest() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "async-handler" );
        spy.clear();

        Event result= flowRunner( flowName ).withPayload( "nothing_important" ).run();
        Message response= result.getMessage();

        assertTrue( "wrong response payload", response.getPayload().getDataType().isCompatibleWith( DataType.STRING ) );
        assertEquals( "wrong response payload", "nothing_important", (String) response.getPayload().getValue() );

        //let handler do its asynchronous work
        if ( expectedResponseCode == null )
        {
            Instant start= Instant.now();
            await().atMost( 20, TimeUnit.SECONDS ).until( () -> {
                return Instant.now().isAfter( start.plusSeconds( 15L ) );
            } );
            assertEquals( "should not receive response on multicast", 0, spy.getEvents().size() );
        }
        else
        {
            await().atMost( 15, TimeUnit.SECONDS ).until( () -> {
                return spy.getEvents().size() == 1;
            } );
            response= (Message) spy.getEvents().get( 0 ).getContent();
            assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoapResponseAttributes );

            CoapResponseAttributes attributes= (CoapResponseAttributes) response.getAttributes().getValue();
            assertEquals( "wrong request code", expectedRequestCode.name(), attributes.getRequestCode() );
            assertEquals( "wrong request uri", expectedRequestUri, attributes.getRequestUri() );
            assertEquals( "wrong response code", expectedResponseCode, attributes.getResponseCode() );
            assertArrayEquals( "wrong response payload", expectedPayload, (byte[]) response.getPayload().getValue() );
        }
    }

}
