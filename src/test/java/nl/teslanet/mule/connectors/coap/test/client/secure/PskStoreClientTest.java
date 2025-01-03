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
package nl.teslanet.mule.connectors.coap.test.client.secure;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.message.Message;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.api.CoapRequestCode;
import nl.teslanet.mule.connectors.coap.api.attributes.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractTestCase;


@Ignore( "Psk Store intented for servers only." )
@RunnerDelegateTo( Parameterized.class )
public class PskStoreClientTest extends AbstractTestCase
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
            { { "get_me", CoapRequestCode.GET, "coaps://127.0.0.1/secure/get_me", "CONTENT",
                "GET called on: /secure/get_me" },
                { "do_not_get_me", CoapRequestCode.GET, "coaps://127.0.0.1/secure/do_not_get_me", "METHOD_NOT_ALLOWED",
                    "" },
                { "post_me", CoapRequestCode.POST, "coaps://127.0.0.1/secure/post_me", "CREATED",
                    "POST called on: /secure/post_me" },
                { "do_not_post_me", CoapRequestCode.POST, "coaps://127.0.0.1/secure/do_not_post_me",
                    "METHOD_NOT_ALLOWED", "" },
                { "put_me", CoapRequestCode.PUT, "coaps://127.0.0.1/secure/put_me", "CHANGED",
                    "PUT called on: /secure/put_me" },
                { "do_not_put_me", CoapRequestCode.PUT, "coaps://127.0.0.1/secure/do_not_put_me", "METHOD_NOT_ALLOWED",
                    "" },
                { "delete_me", CoapRequestCode.DELETE, "coaps://127.0.0.1/secure/delete_me", "DELETED",
                    "DELETE called on: /secure/delete_me" },
                { "do_not_delete_me", CoapRequestCode.DELETE, "coaps://127.0.0.1/secure/do_not_delete_me",
                    "METHOD_NOT_ALLOWED", "" } } );
    }

    /**
     * Server to test against
     */
    protected static PskTestServer server= null;

    /**
     * Start the server
     * @throws Exception when server cannot start
     */
    @BeforeClass
    public static void setUpServer() throws Exception
    {
        server= new PskTestServer();
        server.start();
    }

    /**
     * Stop the server
     * @throws Exception when the server cannot stop
     */
    @AfterClass
    public static void tearDownServer() throws Exception
    {
        if ( server != null )
        {
            server.stop();
            server.destroy();
            server= null;
        }
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
    public CoapRequestCode expectedRequestCode;

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
        return "mule-client-config/secure/testclient-pskfile.xml";
    };

    /**
     * Test CoAP request
     * @throws Exception should not happen in this test
     */
    @Test
    public void testRequest() throws Exception
    {
        Event result= flowRunner( flowName ).withPayload( "nothing_important" ).keepStreamsOpen().run();
        Message response= result.getMessage();
        result.getVariables().get( "saved_payload" );

        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoapResponseAttributes );
        CoapResponseAttributes attributes= (CoapResponseAttributes) response.getAttributes().getValue();

        assertEquals( "wrong request code", expectedRequestCode.name(), attributes.getRequestCode() );
        assertEquals( "wrong request uri", expectedRequestUri, attributes.getRequestUri() );
        assertEquals( "wrong response code", expectedResponseCode, attributes.getResponseCode() );

        String payload= getPayloadAsString( response );
        assertEquals( "wrong response payload", expectedPayload, payload );
    }
}
