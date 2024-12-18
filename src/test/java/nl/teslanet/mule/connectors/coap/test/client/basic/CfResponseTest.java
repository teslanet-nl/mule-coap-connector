/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2023 (teslanet.nl) Rogier Cobben
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


import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.network.interceptors.MessageTracer;
import org.eclipse.californium.elements.config.Configuration;


/**
 * Verify Californium behaviour.
 *
 */
public class CfResponseTest
{
    private static final Logger LOGGER= LoggerFactory.getLogger( CfResponseTest.class.getCanonicalName() );

    /**
     * Server to get test responses.
     */
    private static BasicTestServer server;

    /**
     * Do setup for test. Create server and start.
     * @throws Throwable when sever cannot start
     */
    @BeforeClass
    public static void setup() throws Throwable
    {
        server= new BasicTestServer();
        server.start();
    }

    /**
     * @throws Throwable when server cannot be stopped or destroyed
     */
    @AfterClass
    public static void tearDown() throws Throwable
    {
        server.stop();
        server.destroy();
    }

    /**
     * Test synchronous request when a response is returned.
     * @throws Exception should not happen in this test
     */
    @Test
    public void testResponse() throws Exception
    {
        CoapEndpoint.Builder builder= new CoapEndpoint.Builder();
        Configuration config= Configuration.createStandardWithoutFile();
        config.set( CoapConfig.MAX_RETRANSMIT, 1 );
        builder.setConfiguration( config );
        CoapEndpoint endpoint= builder.build();
        endpoint.addInterceptor( new MessageTracer() );

        CoapClient client= new CoapClient( "coap://127.0.0.1/basic/get_me" );
        client.setEndpoint( (Endpoint) endpoint );

        CoapResponse response= client.get();
        assertNotNull( "no response", response );
        assertTrue( "wrong response", response.isSuccess() );
        client.shutdown();
    }

    /**
     * Test synchronous request when an error response is returned.
     * @throws Exception should not happen in this test
     */
    @Test
    public void testErrorResponse() throws Exception
    {
        CoapEndpoint.Builder builder= new CoapEndpoint.Builder();
        Configuration config= Configuration.createStandardWithoutFile();
        config.set( CoapConfig.MAX_RETRANSMIT, 1 );
        builder.setConfiguration( config );
        CoapEndpoint endpoint= builder.build();
        endpoint.addInterceptor( new MessageTracer() );

        CoapClient client= new CoapClient( "coap://127.0.0.1/basic/do_not_get_me" );
        client.setEndpoint( (Endpoint) endpoint );

        CoapResponse response= client.get();
        assertNotNull( "no response", response );
        assertFalse( "wrong response", response.isSuccess() );
        client.shutdown();
    }

    /**
     * Test synchronous request when no response is returned.
     * @throws Exception should not happen in this test
    */
    @Test
    public void testNoResponse() throws Exception
    {
        CoapEndpoint.Builder builder= new CoapEndpoint.Builder();
        Configuration config= Configuration.createStandardWithoutFile();
        config.set( CoapConfig.MAX_RETRANSMIT, 1 );
        builder.setConfiguration( config );
        CoapEndpoint endpoint= builder.build();
        endpoint.addInterceptor( new MessageTracer() );

        CoapClient client= new CoapClient( "coap://localhost:999/basic/get_me" );
        client.setEndpoint( (Endpoint) endpoint );

        CoapResponse response= client.get();
        assertEquals( "wrong response", null, response );
        client.shutdown();
    }

    /**
     * Test asynchronous request when a response is returned.
     * @throws Exception should not happen in this test
     */
    @Test
    public void testAsyncResponse() throws Exception
    {

        AtomicBoolean onLoadCalled= new AtomicBoolean( false );
        AtomicBoolean onErrorCalled= new AtomicBoolean( false );
        CoapEndpoint.Builder builder= new CoapEndpoint.Builder();
        Configuration config= Configuration.createStandardWithoutFile();
        config.set( CoapConfig.MAX_RETRANSMIT, 1 );
        builder.setConfiguration( config );
        CoapEndpoint endpoint= builder.build();
        endpoint.addInterceptor( new MessageTracer() );

        CoapClient client= new CoapClient( "coap://127.0.0.1/basic/get_me" );
        client.setEndpoint( (Endpoint) endpoint );

        CoapHandler handler= new CoapHandler()
            {

                @Override
                public void onLoad( CoapResponse response )
                {
                    onLoadCalled.set( true );
                    LOGGER.info( "onLoad called" );
                }

                @Override
                public void onError()
                {
                    onErrorCalled.set( true );
                    LOGGER.info( "onLoad called" );
                }
            };

        client.get( handler );
        //let handler do its asynchronous work
        await().atMost( 10, TimeUnit.SECONDS ).untilTrue( onLoadCalled );
        assertTrue( "onLoad should be called", onLoadCalled.get() );
        assertFalse( "onError should not have been called", onErrorCalled.get() );
        client.shutdown();
    }

    /**
     * Test asynchronous request when an error response is returned.
     * @throws Exception should not happen in this test
     */
    @Test
    public void testAsyncErrorResponse() throws Exception
    {
        AtomicBoolean onLoadCalled= new AtomicBoolean( false );
        AtomicBoolean onErrorCalled= new AtomicBoolean( false );
        CoapEndpoint.Builder builder= new CoapEndpoint.Builder();
        Configuration config= Configuration.createStandardWithoutFile();
        config.set( CoapConfig.MAX_RETRANSMIT, 1 );
        builder.setConfiguration( config );
        CoapEndpoint endpoint= builder.build();
        endpoint.addInterceptor( new MessageTracer() );

        CoapClient client= new CoapClient( "coap://127.0.0.1/basic/do_not_get_me" );
        client.setEndpoint( (Endpoint) endpoint );

        CoapHandler handler= new CoapHandler()
            {

                @Override
                public void onLoad( CoapResponse response )
                {
                    onLoadCalled.set( true );
                    LOGGER.info( "onLoad called" );
                }

                @Override
                public void onError()
                {
                    onErrorCalled.set( true );
                    LOGGER.info( "onLoad called" );
                }
            };

        client.get( handler );
        await().atMost( 10, TimeUnit.SECONDS ).untilTrue( onLoadCalled );
        assertTrue( "onLoad should be called", onLoadCalled.get() );
        assertFalse( "onError should not have been called", onErrorCalled.get() );
        client.shutdown();
    }

    /**
     * Test asynchronous request when no response is returned.
     * @throws Exception should not happen in this test
     */
    @Test
    public void testAsyncNoResponse() throws Exception
    {
        final AtomicBoolean onLoadCalled= new AtomicBoolean( false );
        final AtomicBoolean onErrorCalled= new AtomicBoolean( false );
        CoapEndpoint.Builder builder= new CoapEndpoint.Builder();
        Configuration config= Configuration.createStandardWithoutFile();
        config.set( CoapConfig.MAX_RETRANSMIT, 1 );
        builder.setConfiguration( config );
        CoapEndpoint endpoint= builder.build();
        endpoint.addInterceptor( new MessageTracer() );

        CoapClient client= new CoapClient( "coap://localhost:999/basic/get_me" );
        client.setEndpoint( (Endpoint) endpoint );

        CoapHandler handler= new CoapHandler()
            {

                @Override
                public void onLoad( CoapResponse response )
                {
                    onLoadCalled.set( true );
                    LOGGER.info( "onLoad called" );
                }

                @Override
                public void onError()
                {
                    onErrorCalled.set( true );
                    LOGGER.info( "onError called" );
                }
            };

        client.setTimeout( 2000L );
        client.get( handler );
        //wait for async processes to complete
        await().atMost( 10, TimeUnit.SECONDS ).untilTrue( onErrorCalled );
        assertFalse( "onLoad should not be called", onLoadCalled.get() );
        client.shutdown();
    }

}
