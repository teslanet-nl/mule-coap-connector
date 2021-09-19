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
package nl.teslanet.mule.connectors.coap.test.client.payload;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.streaming.bytes.CursorStreamProvider;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.message.OutputHandler;
import org.mule.runtime.core.api.streaming.bytes.InMemoryCursorStreamConfig;
import org.mule.runtime.core.api.streaming.bytes.InMemoryCursorStreamProvider;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.tck.core.streaming.SimpleByteBufferManager;

import nl.teslanet.mule.connectors.coap.api.ReceivedResponseAttributes;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.Data;
import org.eclipse.californium.core.CoapServer;


public class PayloadTest extends AbstractClientTestCase
{
    private final int PAYLOAD_SIZE= 103;

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-client-config/payload/testclient1.xml";
    };

    /**
     * Create test-servers
     * @throws Exception when servers cannot be created
     */
    @Override
    protected CoapServer getTestServer() throws Exception
    {
        return new EchoTestServer();
    }

    /** 
     * Test CoAP request with null payload
     * @throws Exception should not happen in this test
     */
    @Test
    public void testNullPayload() throws Exception
    {
        Object requestPayload= null;
        CoreEvent response= (CoreEvent) flowRunner( "do_test" ).keepStreamsOpen().withPayload( (Object) requestPayload ).run();

        assertNotNull( "no mule event", response );
        ReceivedResponseAttributes attributes= (ReceivedResponseAttributes) response.getMessage().getAttributes().getValue();
        assertTrue( "request failed", attributes.isSuccess() );

        Object responsePayload= TypedValue.unwrap( response.getMessage().getPayload() );
        assertNull( "wrong response payload contents", responsePayload );
    }

    /** 
     * Test CoAP request with byte[] payload
     * @throws Exception should not happen in this test
     */
    @Test
    public void testByteArrayPayload1() throws Exception
    {
        Object requestPayload= Data.getContent( PAYLOAD_SIZE );
        CoreEvent response= (CoreEvent) flowRunner( "do_test" ).keepStreamsOpen().withPayload( (Object) requestPayload ).run();

        assertNotNull( "no mule event", response );
        ReceivedResponseAttributes attributes= (ReceivedResponseAttributes) response.getMessage().getAttributes().getValue();
        assertTrue( "request failed", attributes.isSuccess() );

        CursorStreamProvider responsePayload= (CursorStreamProvider) TypedValue.unwrap( response.getMessage().getPayload() );
        assertTrue( "wrong response payload contents", Data.validateContent( responsePayload.openCursor(), PAYLOAD_SIZE ) );
    }

    /** 
     * Test CoAP request with Byte[] payload
     * @throws Exception should not happen in this test
     */
    @Test
    public void testByteArrayPayload2() throws Exception
    {
        byte[] data= Data.getContent( PAYLOAD_SIZE );
        Byte[] requestPayload= new Byte [PAYLOAD_SIZE];
        for ( int i= 0; i < PAYLOAD_SIZE; i++ )
        {
            requestPayload[i]= data[i];
        } ;

        CoreEvent response= (CoreEvent) flowRunner( "do_test" ).keepStreamsOpen().withPayload( (Object) requestPayload ).run();

        assertNotNull( "no mule event", response );
        ReceivedResponseAttributes attributes= (ReceivedResponseAttributes) response.getMessage().getAttributes().getValue();
        assertTrue( "request failed", attributes.isSuccess() );

        CursorStreamProvider responsePayload= (CursorStreamProvider) TypedValue.unwrap( response.getMessage().getPayload() );
        assertTrue( "wrong response payload contents", Data.validateContent( responsePayload.openCursor(), PAYLOAD_SIZE ) );
    }

    /** 
     * Test CoAP request with String payload
     * @throws Exception should not happen in this test
     */
    @Test
    public void testStringPayload() throws Exception
    {
        String requestPayload= "test123€¤€½¼¾¥×";
        CoreEvent response= (CoreEvent) flowRunner( "do_test" ).keepStreamsOpen().withPayload( (Object) requestPayload ).run();

        assertNotNull( "no mule event", response );
        ReceivedResponseAttributes attributes= (ReceivedResponseAttributes) response.getMessage().getAttributes().getValue();
        assertTrue( "request failed", attributes.isSuccess() );

        CursorStreamProvider responsePayload= (CursorStreamProvider) TypedValue.unwrap( response.getMessage().getPayload() );
        String responseString= IOUtils.toString( responsePayload.openCursor(), StandardCharsets.UTF_8 );
        assertEquals( "wrong response payload contents", requestPayload, responseString );
    }

    /** 
     * Test CoAP request with CursorStreamProvider payload
     * @throws Exception should not happen in this test
     */
    @Test
    public void testCursorStreamProviderPayload() throws Exception
    {
        CursorStreamProvider requestPayload= new InMemoryCursorStreamProvider( new ByteArrayInputStream( Data.getContent( PAYLOAD_SIZE )), InMemoryCursorStreamConfig.getDefault(), new SimpleByteBufferManager() );
        CoreEvent response= (CoreEvent) flowRunner( "do_test" ).keepStreamsOpen().withPayload( (Object) requestPayload ).run();

        assertNotNull( "no mule event", response );
        ReceivedResponseAttributes attributes= (ReceivedResponseAttributes) response.getMessage().getAttributes().getValue();
        assertTrue( "request failed", attributes.isSuccess() );

        CursorStreamProvider responsePayload= (CursorStreamProvider) TypedValue.unwrap( response.getMessage().getPayload() );
        assertTrue( "wrong response payload contents", Data.validateContent( responsePayload.openCursor(), PAYLOAD_SIZE ) );
    }

    /** 
     * Test CoAP request with InputStream payload
     * @throws Exception should not happen in this test
     */
    @Test
    public void testInputStreamPayload() throws Exception
    {
        InputStream requestPayload= new ByteArrayInputStream( Data.getContent( PAYLOAD_SIZE ) );
        CoreEvent response= (CoreEvent) flowRunner( "do_test" ).keepStreamsOpen().withPayload( (Object) requestPayload ).run();

        assertNotNull( "no mule event", response );
        ReceivedResponseAttributes attributes= (ReceivedResponseAttributes) response.getMessage().getAttributes().getValue();
        assertTrue( "request failed", attributes.isSuccess() );

        CursorStreamProvider responsePayload= (CursorStreamProvider) TypedValue.unwrap( response.getMessage().getPayload() );
        assertTrue( "wrong response payload contents", Data.validateContent( responsePayload.openCursor(), PAYLOAD_SIZE ) );
    }

    /** 
     * Test CoAP request with OutputHandler payload
     * @throws Exception should not happen in this test
     */
    @Test
    public void testOutputHandlerPayload() throws Exception
    {
        OutputHandler requestPayload= new TestOutputHandler( Data.getContent( PAYLOAD_SIZE ) );
        CoreEvent response= (CoreEvent) flowRunner( "do_test" ).keepStreamsOpen().withPayload( (Object) requestPayload ).run();

        assertNotNull( "no mule event", response );
        ReceivedResponseAttributes attributes= (ReceivedResponseAttributes) response.getMessage().getAttributes().getValue();
        assertTrue( "request failed", attributes.isSuccess() );

        CursorStreamProvider responsePayload= (CursorStreamProvider) TypedValue.unwrap( response.getMessage().getPayload() );
        assertTrue( "wrong response payload contents", Data.validateContent( responsePayload.openCursor(), PAYLOAD_SIZE ) );
    }

    //TODO testTransformedPayload()

    /**
     * Outputhandler for testing.
     *
     */
    private class TestOutputHandler implements OutputHandler
    {
        private byte[] content;

        public TestOutputHandler( byte[] content )
        {
            this.content= content;
        }

        @Override
        public void write( CoreEvent event, OutputStream out ) throws IOException
        {
            out.write( content );
            out.flush();
        }

    }
}
