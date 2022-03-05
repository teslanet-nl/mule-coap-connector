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
package nl.teslanet.mule.connectors.coap.test.client.query;


import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.californium.core.CoapServer;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.streaming.bytes.CursorStreamProvider;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.api.CoAPRequestCode;
import nl.teslanet.mule.connectors.coap.api.CoAPResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.Defs;
import nl.teslanet.mule.connectors.coap.api.query.AbstractQueryParam;
import nl.teslanet.mule.connectors.coap.api.query.QueryParam;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;


@RunnerDelegateTo( Parameterized.class )
public class QueryTest extends AbstractClientTestCase
{
    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters( name= "flowName= {0}" )
    public static Collection< Object[] > data()
    {
        ArrayList< Object[] > tests= new ArrayList< Object[] >();

        for ( CoAPRequestCode code : CoAPRequestCode.values() )
        {
            tests.add( new Object []{ code } );
        }
        return tests;
    }

    /**
     * The request code that is expected.
     */
    @Parameter( 0 )
    public CoAPRequestCode requestCode;

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-client-config/query/testclient-query.xml";
    };

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase#getTestServer()
     */
    @Override
    protected CoapServer getTestServer() throws Exception
    {
        return new QueryTestServer();
    }

    private String expectedRequestUri( List< ? extends AbstractQueryParam > params ) throws URISyntaxException
    {
        URI uri= new URI( "coap", null, "127.0.0.1", -1, "/query/test", queryString( params ), null );
        return uri.toString();
    }

    private String expectedPayload( CoAPRequestCode code, List< ? extends AbstractQueryParam > params ) throws URISyntaxException
    {
        URI uri= new URI( "coap", null, "localhost", -1, "/query/test", queryString( params ), null );
        return requestCode.toString() + " called on: " + uri.toString();
    }

    /**
     * Test Sync request with query parameters.
     * @throws Exception should not happen in this test
     */
    @Test
    public void testSyncRequest() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "query-handler" );
        spy.clear();

        LinkedList< QueryParam > params= new LinkedList<>();
        params.add( new QueryParam( "query1", "one" ) );
        params.add( new QueryParam( "query2", "two" ) );
        params.add( new QueryParam( "query3", null ) );

        Event result= flowRunner( "request-sync" ).keepStreamsOpen().withPayload( "nothing_important" ).withVariable( "code", requestCode.toString() ).withVariable(
            "query",
            params
        ).run();

        Message response= result.getMessage();

        // assertions...
        //assertTrue( "wrong response payload type", response.getPayload().getDataType().isCompatibleWith( DataType.BYTE_ARRAY ) );
        assertNotNull( "no mule event", response );
        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoAPResponseAttributes );
        CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
        assertTrue( "request failed", attributes.isSuccess() );
        assertEquals( "wrong request uri", expectedRequestUri( params ), attributes.getRequestUri() );

        CursorStreamProvider responsePayload= (CursorStreamProvider) TypedValue.unwrap( response.getPayload() );
        assertArrayEquals( "wrong response payload", expectedPayload( requestCode, params ).getBytes( Defs.COAP_CHARSET ), IOUtils.toByteArray( responsePayload.openCursor() ) );
    }

    /**
     * Test Async request with query parameters.
     * @throws Exception should not happen in this test
     */
    @Test
    public void testAsyncRequest() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "query-handler" );
        spy.clear();

        LinkedList< QueryParam > params= new LinkedList<>();
        params.add( new QueryParam( "query1", "one" ) );
        params.add( new QueryParam( "query2", "two" ) );
        params.add( new QueryParam( "query3", null ) );

        Event result= flowRunner( "request-async" ).withPayload( "nothing_important" ).withVariable( "code", requestCode.toString() ).withVariable( "query", params ).run();

        Message response= result.getMessage();

        assertTrue( "wrong response payload", response.getPayload().getDataType().isCompatibleWith( DataType.STRING ) );
        assertEquals( "wrong response payload", "nothing_important", (String) response.getPayload().getValue() );

        //let handler do its asynchronous work
        await().atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == 1;
        } );
        // assertions...
        response= (Message) spy.getEvents().get( 0 ).getContent();
        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoAPResponseAttributes );
        CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
        assertTrue( "error response", attributes.isSuccess() );
        assertEquals( "wrong request uri", expectedRequestUri( params ), attributes.getRequestUri() );
        assertArrayEquals( "wrong response payload", expectedPayload( requestCode, params ).getBytes( Defs.COAP_CHARSET ), (byte[]) response.getPayload().getValue() );
    }

    /**
     * Test Sync request with fixed query parameters.
     * @throws Exception should not happen in this test
     */
    @Test
    public void testFixedSyncRequest() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "query-handler" );
        spy.clear();

        LinkedList< QueryParam > params= new LinkedList<>();
        params.add( new QueryParam( "query1", "one" ) );
        params.add( new QueryParam( "query2", "two" ) );
        params.add( new QueryParam( "query3", null ) );

        LinkedList< QueryParam > expectedParams= new LinkedList<>();
        expectedParams.add( new QueryParam( "test3", "three" ) );
        expectedParams.add( new QueryParam( "test4", "four" ) );
        expectedParams.add( new QueryParam( "novalue2", null ) );

        Event result= flowRunner( "request-sync-fixed" ).keepStreamsOpen().withPayload( "nothing_important" ).withVariable( "code", requestCode.toString() ).withVariable(
            "query",
            params
        ).run();

        Message response= result.getMessage();

        // assertions...
        //assertTrue( "wrong response payload type", response.getPayload().getDataType().isCompatibleWith( DataType.BYTE_ARRAY ) );
        assertNotNull( "no mule event", response );
        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoAPResponseAttributes );
        CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
        assertTrue( "request failed", attributes.isSuccess() );
        assertEquals( "wrong request uri", expectedRequestUri( expectedParams ), attributes.getRequestUri() );

        CursorStreamProvider responsePayload= (CursorStreamProvider) TypedValue.unwrap( response.getPayload() );
        assertArrayEquals(
            "wrong response payload",
            expectedPayload( requestCode, expectedParams ).getBytes( Defs.COAP_CHARSET ),
            IOUtils.toByteArray( responsePayload.openCursor() )
        );
    }

    /**
     * Test Async request with fixed query parameters.
     * @throws Exception should not happen in this test
     */
    @Test
    public void testFixedAsyncRequest() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "query-handler" );
        spy.clear();

        LinkedList< QueryParam > params= new LinkedList<>();
        params.add( new QueryParam( "query1", "one" ) );
        params.add( new QueryParam( "query2", "two" ) );
        params.add( new QueryParam( "query3", null ) );

        LinkedList< QueryParam > expectedParams= new LinkedList<>();
        expectedParams.add( new QueryParam( "test3", "three" ) );
        expectedParams.add( new QueryParam( "test4", "four" ) );
        expectedParams.add( new QueryParam( "novalue2", null ) );

        Event result= flowRunner( "request-async-fixed" ).withPayload( "nothing_important" ).withVariable( "code", requestCode.toString() ).withVariable( "query", params ).run();

        Message response= result.getMessage();

        assertTrue( "wrong response payload", response.getPayload().getDataType().isCompatibleWith( DataType.STRING ) );
        assertEquals( "wrong response payload", "nothing_important", (String) response.getPayload().getValue() );

        //let handler do its asynchronous work
        await().atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == 1;
        } );
        // assertions...
        response= (Message) spy.getEvents().get( 0 ).getContent();
        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoAPResponseAttributes );
        CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
        assertTrue( "error response", attributes.isSuccess() );
        assertEquals( "wrong request uri", expectedRequestUri( expectedParams ), attributes.getRequestUri() );
        assertArrayEquals( "wrong response payload", expectedPayload( requestCode, expectedParams ).getBytes( Defs.COAP_CHARSET ), (byte[]) response.getPayload().getValue() );
    }

    /**
     * Test Sync request with default query parameters.
     * @throws Exception should not happen in this test
     */
    @Test
    public void testRequestSyncWithDefaults1() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "query-handler" );
        spy.clear();

        LinkedList< QueryParam > params= new LinkedList<>();
        params.add( new QueryParam( "query1", "one" ) );
        params.add( new QueryParam( "query2", "two" ) );
        params.add( new QueryParam( "query3", null ) );

        LinkedList< QueryParam > expectedParams= new LinkedList<>();
        expectedParams.add( new QueryParam( "test1", "one" ) );
        expectedParams.add( new QueryParam( "test2", "two" ) );
        expectedParams.add( new QueryParam( "novalue1", null ) );
        expectedParams.add( new QueryParam( "query1", "one" ) );
        expectedParams.add( new QueryParam( "query2", "two" ) );
        expectedParams.add( new QueryParam( "query3", null ) );

        Event result= flowRunner( "request-sync-with-defaults" ).keepStreamsOpen().withPayload( "nothing_important" ).withVariable( "code", requestCode.toString() ).withVariable(
            "query",
            params
        ).run();

        Message response= result.getMessage();

        // assertions...
        //assertTrue( "wrong response payload type", response.getPayload().getDataType().isCompatibleWith( DataType.BYTE_ARRAY ) );
        assertNotNull( "no mule event", response );
        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoAPResponseAttributes );
        CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
        assertTrue( "request failed", attributes.isSuccess() );
        assertEquals( "wrong request uri", expectedRequestUri( expectedParams ), attributes.getRequestUri() );

        CursorStreamProvider responsePayload= (CursorStreamProvider) TypedValue.unwrap( response.getPayload() );
        assertArrayEquals(
            "wrong response payload",
            expectedPayload( requestCode, expectedParams ).getBytes( Defs.COAP_CHARSET ),
            IOUtils.toByteArray( responsePayload.openCursor() )
        );
    }

    /**
     * Test Async request with default query parameters.
     * @throws Exception should not happen in this test
     */
    @Test
    public void testRequestAsyncWithDefaults1() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "query-handler" );
        spy.clear();

        LinkedList< QueryParam > params= new LinkedList<>();
        params.add( new QueryParam( "query1", "one" ) );
        params.add( new QueryParam( "query2", "two" ) );
        params.add( new QueryParam( "query3", null ) );

        LinkedList< QueryParam > expectedParams= new LinkedList<>();
        expectedParams.add( new QueryParam( "test1", "one" ) );
        expectedParams.add( new QueryParam( "test2", "two" ) );
        expectedParams.add( new QueryParam( "novalue1", null ) );
        expectedParams.add( new QueryParam( "query1", "one" ) );
        expectedParams.add( new QueryParam( "query2", "two" ) );
        expectedParams.add( new QueryParam( "query3", null ) );

        Event result= flowRunner( "request-async-with-defaults" ).withPayload( "nothing_important" ).withVariable( "code", requestCode.toString() ).withVariable(
            "query",
            params
        ).run();

        Message response= result.getMessage();

        assertTrue( "wrong response payload", response.getPayload().getDataType().isCompatibleWith( DataType.STRING ) );
        assertEquals( "wrong response payload", "nothing_important", (String) response.getPayload().getValue() );

        //let handler do its asynchronous work
        await().atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == 1;
        } );
        // assertions...
        response= (Message) spy.getEvents().get( 0 ).getContent();
        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoAPResponseAttributes );
        CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
        assertTrue( "error response", attributes.isSuccess() );
        assertEquals( "wrong request uri", expectedRequestUri( expectedParams ), attributes.getRequestUri() );
        assertArrayEquals( "wrong response payload", expectedPayload( requestCode, expectedParams ).getBytes( Defs.COAP_CHARSET ), (byte[]) response.getPayload().getValue() );
    }

    /**
     * Test Sync request with default query parameters.
     * @throws Exception should not happen in this test
     */
    @Test
    public void testRequestSyncWithDefaults2() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "query-handler" );
        spy.clear();

        LinkedList< QueryParam > params= new LinkedList<>();
        params.add( new QueryParam( "test1", "one" ) );
        params.add( new QueryParam( "test2", "two" ) );
        params.add( new QueryParam( "novalue1", null ) );

        LinkedList< QueryParam > expectedParams= new LinkedList<>();
        expectedParams.add( new QueryParam( "test1", "one" ) );
        expectedParams.add( new QueryParam( "test2", "two" ) );
        expectedParams.add( new QueryParam( "novalue1", null ) );
        expectedParams.add( new QueryParam( "test1", "one" ) );
        expectedParams.add( new QueryParam( "test2", "two" ) );
        expectedParams.add( new QueryParam( "novalue1", null ) );

        Event result= flowRunner( "request-sync-with-defaults" ).keepStreamsOpen().withPayload( "nothing_important" ).withVariable( "code", requestCode.toString() ).withVariable(
            "query",
            params
        ).run();

        Message response= result.getMessage();

        // assertions...
        //assertTrue( "wrong response payload type", response.getPayload().getDataType().isCompatibleWith( DataType.BYTE_ARRAY ) );
        assertNotNull( "no mule event", response );
        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoAPResponseAttributes );
        CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
        assertTrue( "request failed", attributes.isSuccess() );
        assertEquals( "wrong request uri", expectedRequestUri( expectedParams ), attributes.getRequestUri() );

        CursorStreamProvider responsePayload= (CursorStreamProvider) TypedValue.unwrap( response.getPayload() );
        assertArrayEquals(
            "wrong response payload",
            expectedPayload( requestCode, expectedParams ).getBytes( Defs.COAP_CHARSET ),
            IOUtils.toByteArray( responsePayload.openCursor() )
        );
    }

    /**
     * Test Async request with default query parameters.
     * @throws Exception should not happen in this test
     */
    @Test
    public void testRequestAsyncWithDefaults2() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "query-handler" );
        spy.clear();

        LinkedList< QueryParam > params= new LinkedList<>();
        params.add( new QueryParam( "test1", "one" ) );
        params.add( new QueryParam( "test2", "two" ) );
        params.add( new QueryParam( "novalue1", null ) );

        LinkedList< QueryParam > expectedParams= new LinkedList<>();
        expectedParams.add( new QueryParam( "test1", "one" ) );
        expectedParams.add( new QueryParam( "test2", "two" ) );
        expectedParams.add( new QueryParam( "novalue1", null ) );
        expectedParams.add( new QueryParam( "test1", "one" ) );
        expectedParams.add( new QueryParam( "test2", "two" ) );
        expectedParams.add( new QueryParam( "novalue1", null ) );

        Event result= flowRunner( "request-async-with-defaults" ).withPayload( "nothing_important" ).withVariable( "code", requestCode.toString() ).withVariable(
            "query",
            params
        ).run();

        Message response= result.getMessage();

        assertTrue( "wrong response payload", response.getPayload().getDataType().isCompatibleWith( DataType.STRING ) );
        assertEquals( "wrong response payload", "nothing_important", (String) response.getPayload().getValue() );

        //let handler do its asynchronous work
        await().atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == 1;
        } );
        // assertions...
        response= (Message) spy.getEvents().get( 0 ).getContent();
        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoAPResponseAttributes );
        CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
        assertTrue( "error response", attributes.isSuccess() );
        assertEquals( "wrong request uri", expectedRequestUri( expectedParams ), attributes.getRequestUri() );
        assertArrayEquals( "wrong response payload", expectedPayload( requestCode, expectedParams ).getBytes( Defs.COAP_CHARSET ), (byte[]) response.getPayload().getValue() );
    }

    /**
     * Test Sync request with fixed and default query parameters.
     * @throws Exception should not happen in this test
     */
    @Test
    public void testRequestSyncFixedWithDefaults() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "query-handler" );
        spy.clear();

        LinkedList< QueryParam > params= new LinkedList<>();
        params.add( new QueryParam( "query1", "one" ) );
        params.add( new QueryParam( "query2", "two" ) );
        params.add( new QueryParam( "query3", null ) );

        LinkedList< QueryParam > expectedParams= new LinkedList<>();
        expectedParams.add( new QueryParam( "test1", "one" ) );
        expectedParams.add( new QueryParam( "test2", "two" ) );
        expectedParams.add( new QueryParam( "novalue1", null ) );
        expectedParams.add( new QueryParam( "test3", "three" ) );
        expectedParams.add( new QueryParam( "test4", "four" ) );
        expectedParams.add( new QueryParam( "novalue2", null ) );

        Event result= flowRunner( "request-sync-fixed-with-defaults" ).keepStreamsOpen().withPayload( "nothing_important" ).withVariable(
            "code",
            requestCode.toString()
        ).withVariable( "query", params ).run();

        Message response= result.getMessage();

        // assertions...
        //assertTrue( "wrong response payload type", response.getPayload().getDataType().isCompatibleWith( DataType.BYTE_ARRAY ) );
        assertNotNull( "no mule event", response );
        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoAPResponseAttributes );
        CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
        assertTrue( "request failed", attributes.isSuccess() );
        assertEquals( "wrong request uri", expectedRequestUri( expectedParams ), attributes.getRequestUri() );

        CursorStreamProvider responsePayload= (CursorStreamProvider) TypedValue.unwrap( response.getPayload() );
        assertArrayEquals(
            "wrong response payload",
            expectedPayload( requestCode, expectedParams ).getBytes( Defs.COAP_CHARSET ),
            IOUtils.toByteArray( responsePayload.openCursor() )
        );
    }

    /**
     * Test Async request with fixed and default query parameters.
     * @throws Exception should not happen in this test
     */
    @Test
    public void testRequestAsyncFixedWithDefaults() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "query-handler" );
        spy.clear();

        LinkedList< QueryParam > params= new LinkedList<>();
        params.add( new QueryParam( "query1", "one" ) );
        params.add( new QueryParam( "query2", "two" ) );
        params.add( new QueryParam( "query3", null ) );

        LinkedList< QueryParam > expectedParams= new LinkedList<>();
        expectedParams.add( new QueryParam( "test1", "one" ) );
        expectedParams.add( new QueryParam( "test2", "two" ) );
        expectedParams.add( new QueryParam( "novalue1", null ) );
        expectedParams.add( new QueryParam( "test3", "three" ) );
        expectedParams.add( new QueryParam( "test4", "four" ) );
        expectedParams.add( new QueryParam( "novalue2", null ) );

        Event result= flowRunner( "request-async-fixed-with-defaults" ).withPayload( "nothing_important" ).withVariable( "code", requestCode.toString() ).withVariable(
            "query",
            params
        ).run();

        Message response= result.getMessage();

        assertTrue( "wrong response payload", response.getPayload().getDataType().isCompatibleWith( DataType.STRING ) );
        assertEquals( "wrong response payload", "nothing_important", (String) response.getPayload().getValue() );

        //let handler do its asynchronous work
        await().atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == 1;
        } );
        // assertions...
        response= (Message) spy.getEvents().get( 0 ).getContent();
        assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoAPResponseAttributes );
        CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
        assertTrue( "error response", attributes.isSuccess() );
        assertEquals( "wrong request uri", expectedRequestUri( expectedParams ), attributes.getRequestUri() );
        assertArrayEquals( "wrong response payload", expectedPayload( requestCode, expectedParams ).getBytes( Defs.COAP_CHARSET ), (byte[]) response.getPayload().getValue() );
    }
}
