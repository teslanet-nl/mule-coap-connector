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
package nl.teslanet.mule.connectors.coap.test.client.virtual;


import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.OptionSet;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.api.CoAPResponseAttributes;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;


/**
 * Abstract class for testing inbound properties
 *
 */
@RunnerDelegateTo( Parameterized.class )
public class VirtualHostTestCase extends AbstractClientTestCase
{
    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters( name= "request= {0}  " )
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []
            {
                { Code.GET, "californium.eclipseprojects.io", 33, "127.0.0.1", 6768, "/echo", ResponseCode.CONTENT },
                { Code.PUT, "californium.eclipseprojects.io", 33, "127.0.0.1", 6768, "/echo", ResponseCode.CHANGED },
                { Code.POST, "californium.eclipseprojects.io", 33, "127.0.0.1", 6768, "/echo", ResponseCode.CHANGED },
                { Code.DELETE, "californium.eclipseprojects.io", 33, "127.0.0.1", 6768, "/echo", ResponseCode.DELETED } }
        );
    }

    /**
     * Request code to test
     */
    @Parameter( 0 )
    public Code requestCode;

    /**
     * The path of the resource to call.
     */
    @Parameter( 1 )
    public String host;

    /**
     * The path of the resource to call.
     */
    @Parameter( 2 )
    public Integer port;

    /**
     * The path of the resource to call.
     */
    @Parameter( 3 )
    public String endpointHost;

    /**
     * The path of the resource to call.
     */
    @Parameter( 4 )
    public Integer endpointPort;

    /**
     * The path of the resource to call.
     */
    @Parameter( 5 )
    public String path;

    /**
     * The expected response code.
     */
    @Parameter( 6 )
    public ResponseCode expectedResponseCode;

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-client-config/virtual/testclient1.xml";
    };

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase#getTestServer()
     */
    @Override
    protected CoapServer[] getTestServers() throws Exception
    {
        CoapServer[] servers= { new VirtualHostTestServer( 6767 ), new VirtualHostTestServer( 6768 ) };
        return servers;
    }

    /**
     * Test inbound property
     * @throws Exception should not happen in this test
     */
    @Test
    public void testVirtualHostRequest() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "6768" );
        spy.clear();
        Event result= flowRunner( "do_request" ).withPayload( "nothing_important" ).withVariable( "code", requestCode.name() ).withVariable( "host", host ).withVariable(
            "port",
            port
        ).withVariable( "endpointHost", endpointHost ).withVariable( "endpointPort", endpointPort ).withVariable( "path", path ).run();
        Message response= result.getMessage();
        assertEquals( "wrong attributes class", new TypedValue< CoAPResponseAttributes >( new CoAPResponseAttributes(), null ).getClass(), response.getAttributes().getClass() );
        CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
        assertEquals( "wrong response code", expectedResponseCode.name(), attributes.getResponseCode() );

        MuleEventSpy.Event event= spy.getEvents().get( 0 );
        assertEquals( "wrong options class", OptionSet.class, event.getContent().getClass() );
        OptionSet options= (OptionSet) event.getContent();
        assertEquals( "wrong uri host", host, options.getUriHost() );
        assertEquals( "wrong uri port", port, options.getUriPort() );
    }

    /**
     * Test inbound property
     * @throws Exception should not happen in this test
     */
    @Test
    public void testDefaultVirtualHostRequest() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "6767" );
        spy.clear();
        Event result= flowRunner( "do_request_to_virtual_host" ).withPayload( "nothing_important" ).withVariable( "code", requestCode.name() ).withVariable(
            "host",
            host
        ).withVariable( "port", port ).withVariable( "endpointHost", endpointHost ).withVariable( "endpointPort", endpointPort ).withVariable( "path", path ).run();
        Message response= result.getMessage();
        assertEquals( "wrong attributes class", new TypedValue< CoAPResponseAttributes >( new CoAPResponseAttributes(), null ).getClass(), response.getAttributes().getClass() );
        CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
        assertEquals( "wrong response code", expectedResponseCode.name(), attributes.getResponseCode() );

        MuleEventSpy.Event event= spy.getEvents().get( 0 );
        assertEquals( "wrong options class", OptionSet.class, event.getContent().getClass() );
        OptionSet options= (OptionSet) event.getContent();
        assertEquals( "wrong uri host", host, options.getUriHost() );
        assertEquals( "wrong uri port", port, options.getUriPort() );
    }
    /**
     * Test inbound property
     * @throws Exception should not happen in this test
     */
    @Test
    public void testDefaultVirtualHostOverrideRequest() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "6768" );
        spy.clear();
        Event result= flowRunner( "do_request_to_virtual_host_override" ).withPayload( "nothing_important" ).withVariable( "code", requestCode.name() ).withVariable(
            "host",
            host
        ).withVariable( "port", port ).withVariable( "endpointHost", endpointHost ).withVariable( "endpointPort", endpointPort ).withVariable( "path", path ).run();
        Message response= result.getMessage();
        assertEquals( "wrong attributes class", new TypedValue< CoAPResponseAttributes >( new CoAPResponseAttributes(), null ).getClass(), response.getAttributes().getClass() );
        CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
        assertEquals( "wrong response code", expectedResponseCode.name(), attributes.getResponseCode() );

        MuleEventSpy.Event event= spy.getEvents().get( 0 );
        assertEquals( "wrong options class", OptionSet.class, event.getContent().getClass() );
        OptionSet options= (OptionSet) event.getContent();
        assertEquals( "wrong uri host", host, options.getUriHost() );
        assertEquals( "wrong uri port", port, options.getUriPort() );
    }
}
