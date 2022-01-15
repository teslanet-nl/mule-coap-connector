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
package nl.teslanet.mule.connectors.coap.test.server.observe;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractServerTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.Timing;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;


public class ObserveConCheckTest extends AbstractServerTestCase
{
    private AtomicInteger handlerErrors;

    private CopyOnWriteArrayList< ReceivedNotification > observations;

    private ArrayList< String > contents;

    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/observe/testserver1.xml";
    };

    @Before
    public void additionalSetUp()
    {
        handlerErrors= new AtomicInteger();
        observations= new CopyOnWriteArrayList< ReceivedNotification >();
        contents= new ArrayList< String >();
        handlerErrors.set( 0 );
        observations.clear();
        contents.clear();
        for ( int i= 0; i < 16; ++i )
        {
            contents.add( Integer.toString( i ) );
        }
    }

    @After
    public void additionalTearDown()
    {
        handlerErrors= null;
        observations= null;
        contents= null;
    }

    private CoapHandler getHandler()
    {
        CoapHandler handler= new CoapHandler()
            {
                @Override
                public void onError()
                {
                    handlerErrors.incrementAndGet();
                }

                @Override
                public void onLoad( CoapResponse response )
                {
                    observations.add( new ReceivedNotification( response ) );
                }

            };
        return handler;
    }

    @Test(timeout= 60000L)
    public void testConCheckOnCount() throws Exception
    {
        setClientUri( "/service/observe_me" );
        CoapResponse response= client.put( contents.get( 0 ), 0 );
        assertNotNull( "put nr: 0 gave no response", response );
        assertTrue( "response nr: 0 indicates failure", response.isSuccess() );

        response= client.get();
        assertNotNull( "get gave no response", response );
        assertTrue( "get response indicates failure", response.isSuccess() );
        assertEquals( "get gave wrong content", contents.get( 0 ), response.getResponseText() );

        CoapObserveRelation relation= client.observe( getHandler() );

        for ( int i= 1; i < contents.size(); i++ )
        {
            Timing.pauze( 100 );
            response= client.put( contents.get( i ), 0 );
            assertNotNull( "put nr: " + i + " gave no response", response );
            assertTrue( "response nr: " + i + " indicates failure", response.isSuccess() );
        }

        Timing.pauze( 100 );
        assertEquals( "handler errors count ", 0, handlerErrors.get() );
        assertEquals( "wrong count of observations", contents.size(), observations.size() );

        for ( int i= 0; i < observations.size(); i++ )
        {
            response= observations.get( i ).response;
            assertNotNull( "observation nr: " + i + " is empty", response );
            assertTrue( "observation nr: " + i + " indicates failure", response.isSuccess() );
            assertEquals( "observation nr: " + i + " has wrong content", contents.get( i ), response.getResponseText() );
            assertEquals( "observation nr: " + i + " is unexpected confirmable", i != 0 && ( i + 1 ) % 5 == 0, response.advanced().isConfirmable() );

        }
        relation.reactiveCancel();
    }

    @Test(timeout= 60000L)
    public void testConCheckOnTime() throws Exception
    {

        setClientUri( "/service/observe_me" );
        CoapResponse response= client.put( contents.get( 0 ), 0 );
        assertNotNull( "put nr: 0 gave no response", response );
        assertTrue( "response nr: 0 indicates failure", response.isSuccess() );

        response= client.get();
        assertNotNull( "get gave no response", response );
        assertTrue( "get response indicates failure", response.isSuccess() );
        assertEquals( "get gave wrong content", contents.get( 0 ), response.getResponseText() );

        CoapObserveRelation relation= client.observe( getHandler() );

        for ( int i= 1; i < contents.size(); i++ )
        {
            Timing.pauze( 350 );
            response= client.put( contents.get( i ), 0 );
            assertNotNull( "put nr: " + i + " gave no response", response );
            assertTrue( "response nr: " + i + " indicates failure", response.isSuccess() );
        }

        Timing.pauze( 100 );
        assertEquals( "handler errors count ", 0, handlerErrors.get() );
        assertEquals( "wrong count of observations", contents.size(), observations.size() );
        long ts1= observations.get( 0 ).ts;
        long ts2= 0;
        for ( int i= 0; i < observations.size(); i++ )
        {
            response= observations.get( i ).response;
            ts2= observations.get( i ).ts;
            boolean expectConfirmable= ( i != 0 && ( ts2 - ts1 ) >= 650 );
            if ( expectConfirmable ) ts1= ts2;
            assertNotNull( "observation nr: " + i + " is empty", response );
            assertTrue( "observation nr: " + i + " indicates failure", response.isSuccess() );
            assertEquals( "observation nr: " + i + " has wrong content", contents.get( i ), response.getResponseText() );
            assertEquals( "observation nr: " + i + " is unexpected confirmable", expectConfirmable, response.advanced().isConfirmable() );
        }
        relation.reactiveCancel();
    }

    public class ReceivedNotification
    {
        public long ts;

        public CoapResponse response;

        public ReceivedNotification( CoapResponse response )
        {
            this.ts= System.currentTimeMillis();
            this.response= response;
        }

    }
}
