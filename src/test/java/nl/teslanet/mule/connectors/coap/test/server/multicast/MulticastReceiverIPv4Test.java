/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2025 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.test.server.multicast;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.junit.runners.Parameterized.Parameters;

import nl.teslanet.mule.connectors.coap.test.utils.Data;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;


public class MulticastReceiverIPv4Test extends MulticastIPv4Test
{
    @Parameters( name= "Request= {0}, port= {1}, path= {2}, contentSize= {3}" )
    public static Collection< Object[] > data()
    {
        return Arrays
            .asList( new Object [] []
            {
                //default maxResourceBodySize on server
                { Code.GET, 9999, "/service/get_me", 10, true, false, "listen1" },
                { Code.PUT, 9999, "/service/put_me", 10, false, false, "listen1" },
                { Code.POST, 9999, "/service/post_me", 10, false, false, "listen1" },
                { Code.DELETE, 9999, "/service/delete_me", 10, true, false, "listen1" }, } );
    }

    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/multicast/testserverIPv4-with-receiver.xml";
    };

    @Override
    protected void validateInboundResponse( CoapResponse response, MuleEventSpy spy )
    {
        if ( !expectTooLarge )
        {
            assertNotNull( "no response", response );
            assertTrue(
                "response indicates failure: " + response.getCode() + " msg: " + response.getResponseText(),
                response.isSuccess()
            );
            assertEquals(
                "wrong remote host",
                "127.0.0.1",
                response.advanced().getSourceContext().getPeerAddress().getHostString()
            );
            assertEquals(
                "wrong remote port",
                5683,
                response.advanced().getSourceContext().getPeerAddress().getPort()
            );
            assertEquals( "wrong spy activation count", 1, spy.getEvents().size() );
            assertTrue(
                "wrong payload in requets",
                Data.validateContent( (byte[]) spy.getEvents().get( 0 ).getContent(), contentSize )
            );
        }
        else
        {
            assertNotNull( "no response", response );
            assertEquals(
                "response is not REQUEST_ENTITY_TOO_LARGE : " + response.getResponseText(),
                ResponseCode.REQUEST_ENTITY_TOO_LARGE,
                response.getCode()
            );
            assertEquals(
                "wrong remote host",
                "127.0.0.1",
                response.advanced().getSourceContext().getPeerAddress().getHostString()
            );
            assertEquals(
                "wrong remote port",
                5683,
                response.advanced().getSourceContext().getPeerAddress().getPort()
            );
        }
    }

    @Override
    protected void validateOutboundResponse( CoapResponse response, MuleEventSpy spy )
    {
        assertNotNull( "no response on: " + requestCode, response );
        assertTrue(
            "response indicates failure: " + response.getCode() + " msg: " + response.getResponseText(),
            response.isSuccess()
        );
        assertEquals( "wrong spy activation count", 1, spy.getEvents().size() );
        assertTrue( "wrong payload in response", Data.validateContent( response.getPayload(), contentSize ) );
    }
}
