/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2020 (teslanet.nl) Rogier Cobben
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
import static org.junit.Assert.assertNull;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.api.ReceivedResponseAttributes;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;
import nl.teslanet.shaded.org.eclipse.californium.core.CoapServer;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.CoAP.ResponseCode;


@RunnerDelegateTo(Parameterized.class)
public class AsyncResponseTest extends AbstractClientTestCase
{
    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters(name= "code= {0}, resourcePath= {1}, expectedResponseCode= {2}, expectedPayload= {3}")
    public static Collection< Object[] > getTests()
    {
        ArrayList< Object[] > tests= new ArrayList< Object[] >();

        for ( ResponseCode code : ResponseCode.values() )
        {
            tests.add( new Object []{ "GET", "/response/" + code.name(), code, "Response is: " + code.name() } );
            tests.add( new Object []{ "POST", "/response/" + code.name(), code, "Response is: " + code.name() } );
            tests.add( new Object []{ "PUT", "/response/" + code.name(), code, "Response is: " + code.name() } );
            tests.add( new Object []{ "DELETE", "/response/" + code.name(), code, "Response is: " + code.name() } );
        }
        return tests;
    }

    /**
     * The request code that is expected.
     */
    @Parameter(0)
    public String requestCode;

    /**
     * The path of the resource to call.
     */
    @Parameter(1)
    public String resourcePath;

    /**
     * The response code that is expected.
     */
    @Parameter(2)
    public ResponseCode expectedResponseCode;

    /**
     * The response payload that is expected.
     */
    @Parameter(3)
    public String expectedResponsePayload;

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
        return new ResponseTestServer();
    }

    /**
     * Test CoAP request
     * @throws Exception should not happen in this test
     */
    @Test
    public void testResponse() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "handler_spy" );
        spy.clear();

        flowRunner( "do_request" ).withPayload( "nothing_important" ).withVariable( "code", requestCode ).withVariable( "host", "127.0.0.1" ).withVariable(
            "port",
            "5683" ).withVariable( "path", resourcePath ).run();
        Thread.sleep( 5000 );
        assertEquals( "spy has not been called once", 1, spy.getEvents().size() );
        Message response= (Message) spy.getEvents().get( 0 ).getContent();
        assertEquals(
            "wrong attributes class",
            new TypedValue< ReceivedResponseAttributes >( new ReceivedResponseAttributes(), null ).getClass(),
            response.getAttributes().getClass() );
        ReceivedResponseAttributes attributes= (ReceivedResponseAttributes) response.getAttributes().getValue();
        byte[] payload= (byte[]) ( response.getPayload().getValue() );
        if ( expectedResponseCode.name().startsWith( "_" ) )
        {
            assertNull( "wrong response code", attributes.getResponseCode() );
            assertNull( "wrong response payload", payload );
            assertEquals( "wrong success flag", false, attributes.isSuccess() );
            
        }
        else
        {
        assertEquals( "wrong response code", expectedResponseCode.name(), attributes.getResponseCode() );
        assertEquals( "wrong response payload", expectedResponsePayload, new String( payload ) );
        assertEquals( "wrong success flag", ResponseCode.isSuccess( expectedResponseCode ), attributes.isSuccess() );
        }
    }
}
