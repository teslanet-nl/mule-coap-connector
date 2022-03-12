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
package nl.teslanet.mule.connectors.coap.test.client.secure;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.californium.core.CoapServer;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.message.Message;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.api.CoAPRequestCode;
import nl.teslanet.mule.connectors.coap.api.CoAPResponseAttributes;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;


@RunnerDelegateTo( Parameterized.class )
public class InSecureTest extends AbstractClientTestCase
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
                { "get_me", CoAPRequestCode.GET.name(), "coap://127.0.0.1:5684/secure/get_me", null, null },
                { "do_not_get_me", CoAPRequestCode.GET.name(), "coap://127.0.0.1:5684/secure/do_not_get_me", null, null },
                { "post_me", CoAPRequestCode.POST.name(), "coap://127.0.0.1:5684/secure/post_me", null, null },
                { "do_not_post_me", CoAPRequestCode.POST.name(), "coap://127.0.0.1:5684/secure/do_not_post_me", null, null },
                { "put_me", CoAPRequestCode.PUT.name(), "coap://127.0.0.1:5684/secure/put_me", null, null },
                { "do_not_put_me", CoAPRequestCode.PUT.name(), "coap://127.0.0.1:5684/secure/do_not_put_me", null, null },
                { "delete_me", CoAPRequestCode.DELETE.name(), "coap://127.0.0.1:5684/secure/delete_me", null, null },
                { "do_not_delete_me", CoAPRequestCode.DELETE.name(), "coap://127.0.0.1:5684/secure/do_not_delete_me", null, null } }
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
    public byte[] expectedPayload;

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-client-config/secure/testclient2.xml";
    };

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase#getTestServer()
     */
    @Override
    protected CoapServer getTestServer() throws Exception
    {
        return new SecureTestServer();
    }

    /**
     * Test CoAP request
     * @throws Exception should not happen in this test
     */
    @Test
    public void testRequest() throws Exception
    {
        Event result= flowRunner( flowName ).withPayload( "nothing_important" ).run();
        Message response= result.getMessage();

        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoAPResponseAttributes );
        CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();

        assertEquals( "wrong request code", expectedRequestCode, attributes.getRequestCode() );
        assertEquals( "wrong request uri", expectedRequestUri, attributes.getRequestUri() );
        assertEquals( "wrong response code", expectedResponseCode, attributes.getResponseCode() );
        byte[] payload= (byte[]) response.getPayload().getValue();
        assertEquals( "wrong response payload", expectedPayload, payload );
    }

}
