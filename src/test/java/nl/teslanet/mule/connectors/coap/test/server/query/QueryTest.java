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
package nl.teslanet.mule.connectors.coap.test.server.query;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.Request;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.runtime.api.message.Message;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.api.CoAPRequestAttributes;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractServerTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;


@RunnerDelegateTo( Parameterized.class )
public class QueryTest extends AbstractServerTestCase
{
    @Parameters( name= "Request= {0}, uri= {1}" )
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []
            {
                { Code.GET, "coap://127.0.0.1/service/test?query0", "coap://localhost/service/test?query0", "query0" },
                { Code.PUT, "coap://127.0.0.1/service/test?query0", "coap://localhost/service/test?query0", "query0" },
                { Code.POST, "coap://127.0.0.1/service/test?query0", "coap://localhost/service/test?query0", "query0" },
                { Code.DELETE, "coap://127.0.0.1/service/test?query0", "coap://localhost/service/test?query0", "query0" },
                { Code.GET, "coap://127.0.0.1/service/test?query0=", "coap://localhost/service/test?query0=", "query0=" },
                { Code.PUT, "coap://127.0.0.1/service/test?query0=", "coap://localhost/service/test?query0=", "query0=" },
                { Code.POST, "coap://127.0.0.1/service/test?query0=", "coap://localhost/service/test?query0=", "query0=" },
                { Code.DELETE, "coap://127.0.0.1/service/test?query0=", "coap://localhost/service/test?query0=", "query0=" },
                { Code.GET, "coap://127.0.0.1/service/test?query1=one", "coap://localhost/service/test?query1=one", "query1=one" },
                { Code.PUT, "coap://127.0.0.1/service/test?query1=one", "coap://localhost/service/test?query1=one", "query1=one" },
                { Code.POST, "coap://127.0.0.1/service/test?query1=one", "coap://localhost/service/test?query1=one", "query1=one" },
                { Code.DELETE, "coap://127.0.0.1/service/test?query1=one", "coap://localhost/service/test?query1=one", "query1=one" },
                { Code.GET, "coap://127.0.0.1/service/test?query1=one&query2", "coap://localhost/service/test?query1=one&query2", "query1=one&query2" },
                { Code.PUT, "coap://127.0.0.1/service/test?query1=one&query2", "coap://localhost/service/test?query1=one&query2", "query1=one&query2" },
                { Code.POST, "coap://127.0.0.1/service/test?query1=one&query2", "coap://localhost/service/test?query1=one&query2", "query1=one&query2" },
                { Code.DELETE, "coap://127.0.0.1/service/test?query1=one&query2", "coap://localhost/service/test?query1=one&query2", "query1=one&query2" },
                { Code.GET, "coap://127.0.0.1/service/test?query1=one&query2=two", "coap://localhost/service/test?query1=one&query2=two", "query1=one&query2=two" },
                { Code.PUT, "coap://127.0.0.1/service/test?query1=one&query2=two", "coap://localhost/service/test?query1=one&query2=two", "query1=one&query2=two" },
                { Code.POST, "coap://127.0.0.1/service/test?query1=one&query2=two", "coap://localhost/service/test?query1=one&query2=two", "query1=one&query2=two" },
                { Code.DELETE, "coap://127.0.0.1/service/test?query1=one&query2=two", "coap://localhost/service/test?query1=one&query2=two", "query1=one&query2=two" },
                { Code.GET, "coap://127.0.0.1/service/test?query1=one&query1=two", "coap://localhost/service/test?query1=one&query1=two", "query1=one&query1=two" },
                { Code.PUT, "coap://127.0.0.1/service/test?query1=one&query1=two", "coap://localhost/service/test?query1=one&query1=two", "query1=one&query1=two" },
                { Code.POST, "coap://127.0.0.1/service/test?query1=one&query1=two", "coap://localhost/service/test?query1=one&query1=two", "query1=one&query1=two" },
                { Code.DELETE, "coap://127.0.0.1/service/test?query1=one&query1=two", "coap://localhost/service/test?query1=one&query1=two", "query1=one&query1=two" }, }
        );
    }

    /**
     * Request code to test
     */
    @Parameter( 0 )
    public Code requestCode;

    /**
     * Test uri to call
     */
    @Parameter( 1 )
    public String uri;

    /**
     * Expected uri
     */
    @Parameter( 2 )
    public String expectedUri;

    /**
     * Expected uri
     */
    @Parameter( 3 )
    public String expectedQuery;

    /**
     * Mule config to use in tests.
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/query/testserver1.xml";
    };

    @Test
    public void testQuery() throws Exception
    {
        //spyRequestMessage();
        MuleEventSpy spy= new MuleEventSpy( "query", null, "OK" );
        spy.clear();

        Request request= new Request( requestCode );
        request.setURI( uri );

        CoapResponse response= client.advanced( request );

        assertNotNull( "no response", response );
        assertTrue( "response indicates failure: " + response.getCode() + " msg: " + response.getResponseText(), response.isSuccess() );
        assertEquals( "wrong spy activation count", 1, spy.getEvents().size() );

        Message received= (Message) spy.getEvents().get( 0 ).getContent();
        assertTrue( "wrong attributes class", received.getAttributes().getValue() instanceof CoAPRequestAttributes );
        CoAPRequestAttributes attributes= (CoAPRequestAttributes) received.getAttributes().getValue();
        assertEquals( "wrong request code", requestCode.name(), attributes.getRequestCode() );
        assertEquals( "wrong request uri", expectedUri, attributes.getRequestUri() );
        assertEquals( "wrong query params", expectedQuery, queryString( attributes.getOptions().getUriQuery() ) );

    }
}
