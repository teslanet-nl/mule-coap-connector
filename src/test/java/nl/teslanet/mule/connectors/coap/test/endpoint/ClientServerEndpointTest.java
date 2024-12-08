/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2024 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.test.endpoint;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.streaming.bytes.CursorStreamProvider;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.util.IOUtils;

import nl.teslanet.mule.connectors.coap.api.Defs;
import nl.teslanet.mule.connectors.coap.api.attributes.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractTestCase;


/**
 * Test mutual client and server use of endpoints.
 */
public class ClientServerEndpointTest extends AbstractTestCase
{
    /**
     * Determines if the test case should perform graceful shutdown or not. Default is false so that tests run more quickly.
     */
    @Override
    protected boolean isGracefulShutdown()
    {
        return true;
    }

    /**
     * Mule test application.
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-config/endpoint/test-endpoints1.xml";
    };

    /**
     * Test CoAP request
     * @throws Exception should not happen in this test
     */
    @Test
    public void testEndpointRoute1() throws Exception
    {
        CoreEvent result= flowRunner( "client1-flow" ).keepStreamsOpen().run();
        Message response= result.getMessage();

        assertNotNull( "no mule event", response );
        CursorStreamProvider responsePayload= (CursorStreamProvider) TypedValue.unwrap( response.getPayload() );
        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoapResponseAttributes );
        CoapResponseAttributes attributes= (CoapResponseAttributes) response.getAttributes().getValue();
        assertEquals( "wrong message type", "ACKNOWLEDGEMENT", attributes.getResponseType() );
        assertEquals( "wrong request code", "CHANGED", attributes.getResponseCode() );
        assertEquals(
            "wrong response payload",
            "server2 response",
            new String( IOUtils.toByteArray( responsePayload.openCursor() ), Defs.COAP_CHARSET )
        );
    }

    /**
     * Test CoAP request
     * @throws Exception should not happen in this test
     */
    @Test
    public void testEndpointRoute2() throws Exception
    {
        CoreEvent result= flowRunner( "client2-flow" ).keepStreamsOpen().run();
        Message response= result.getMessage();

        assertNotNull( "no mule event", response );
        CursorStreamProvider responsePayload= (CursorStreamProvider) TypedValue.unwrap( response.getPayload() );
        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoapResponseAttributes );
        CoapResponseAttributes attributes= (CoapResponseAttributes) response.getAttributes().getValue();
        assertEquals( "wrong message type", "ACKNOWLEDGEMENT", attributes.getResponseType() );
        assertEquals( "wrong request code", "CHANGED", attributes.getResponseCode() );
        assertEquals(
            "wrong response payload",
            "server1 response",
            new String( IOUtils.toByteArray( responsePayload.openCursor() ), Defs.COAP_CHARSET )
        );
    }
}