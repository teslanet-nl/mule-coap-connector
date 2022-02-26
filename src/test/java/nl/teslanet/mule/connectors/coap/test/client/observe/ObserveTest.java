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
package nl.teslanet.mule.connectors.coap.test.client.observe;


import static nl.teslanet.mule.connectors.coap.test.utils.Timing.pauze;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;

import nl.teslanet.mule.connectors.coap.api.CoAPResponseAttributes;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;


/**
 * Test observe
 *
 */
//TODO test exceptions in handler
//TODO test configuration faults (no host)
public class ObserveTest extends AbstractClientTestCase
{
    /**
     * The contents to set on observable resource
     */
    private ArrayList< String > contents= null;

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-client-config/observe/testclient1.xml";
    };

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase#getTestServer()
     */
    @Override
    protected CoapServer getTestServer() throws Exception
    {
        return new ObserveTestServer();
    }

    /**
     * Clean start
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        contents= new ArrayList< String >();
        contents.add( "nothing_yet" );
        contents.add( "first" );
        contents.add( "second" );
        contents.add( "third" );
        contents.add( "fourth" );
        contents.add( "fifth" );
    }

    @After
    public void tearDown()
    {
        if ( contents != null )
        {
            contents.clear();
            contents= null;
        }
    }

    /**
     * Test permanent observe 
     * @throws Exception should not happen in this test
     */
    @Test( timeout= 100000L )
    public void testPermanentObserve() throws Exception
    {
        final MuleEventSpy spy= new MuleEventSpy( "permanent" );
        spy.clear();

        //let asynchronous work happen
        pauze();

        for ( int i= 1; i < contents.size(); i++ )
        {
            pauze();
            Event result= flowRunner( "do_put_permanent" ).withPayload( contents.get( i ) ).run();
            Message response= result.getMessage();
            assertEquals(
                "wrong attributes class",
                new TypedValue< CoAPResponseAttributes >( new CoAPResponseAttributes(), null ).getClass(),
                response.getAttributes().getClass()
            );
            CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
            assertEquals( "put nr: " + i + " gave wrong response", ResponseCode.CHANGED.name(), attributes.getResponseCode() );
        }
        await().atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == contents.size();
        } );
        int obsOffset= 0;
        for ( int i= 0; i < spy.getEvents().size(); i++ )
        {
            Message response= (Message) spy.getEvents().get( i ).getContent();
            assertEquals(
                "wrong attributes class",
                new TypedValue< CoAPResponseAttributes >( new CoAPResponseAttributes(), null ).getClass(),
                response.getAttributes().getClass()
            );
            CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
            if ( i == 0 ) obsOffset= attributes.getOptions().getObserve().intValue();
            assertNotEquals( "observation nr: " + i + " is empty", null, response.getPayload() );
            assertTrue( "observation nr: " + i + " indicates failure", attributes.isSuccess() );
            assertEquals( "observation nr: " + i + " has wrong content", contents.get( i ), new String( (byte[]) response.getPayload().getValue() ) );
            assertEquals( "observation nr: " + i + " has wrong observe option", obsOffset + i, attributes.getOptions().getObserve().intValue() );
        }

    }

    /**
     * Test temporary observe 
     * @throws Exception should not happen in this test
     */
    @Test( timeout= 100000L )
    public void testTemporaryObserve() throws Exception
    {
        Event result;
        Message response;

        MuleEventSpy spy= new MuleEventSpy( "temporary" );
        spy.clear();

        //let asynchronous work happen
        pauze();
        assertEquals( "unexpected observation on start test", 0, spy.getEvents().size() );

        for ( int i= 1; i < contents.size(); i++ )
        {
            pauze();
            result= flowRunner( "do_put_temporary" ).withPayload( contents.get( i ) ).run();
            response= result.getMessage();
            assertEquals(
                "wrong attributes class",
                new TypedValue< CoAPResponseAttributes >( new CoAPResponseAttributes(), null ).getClass(),
                response.getAttributes().getClass()
            );
            CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
            assertEquals( "1st series put nr: " + i + " gave wrong response", ResponseCode.CHANGED.name(), attributes.getResponseCode() );
        }
        assertEquals( "unexpected observation after 1st test series", 0, spy.getEvents().size() );

        result= flowRunner( "start_temporary" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        await( "missing observation start response" ).atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == 1;
        } );
        for ( int i= 1; i < contents.size(); i++ )
        {
            pauze();
            result= flowRunner( "do_put_temporary" ).withPayload( contents.get( i ) ).run();
            response= result.getMessage();
            assertEquals(
                "wrong attributes class",
                new TypedValue< CoAPResponseAttributes >( new CoAPResponseAttributes(), null ).getClass(),
                response.getAttributes().getClass()
            );
            CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
            assertEquals( "2nd series put nr: " + i + " gave wrong response", ResponseCode.CHANGED.name(), attributes.getResponseCode() );
        }
        await( "number of observation after 2nd test series" ).atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == contents.size();
        } );
        result= flowRunner( "stop_temporary" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        await( "observation stop response" ).atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == contents.size() + 1;
        } );
        for ( int i= 1; i < contents.size(); i++ )
        {
            pauze();
            result= flowRunner( "do_put_temporary" ).withPayload( contents.get( i ) ).run();
            response= result.getMessage();
            assertEquals(
                "wrong attributes class",
                new TypedValue< CoAPResponseAttributes >( new CoAPResponseAttributes(), null ).getClass(),
                response.getAttributes().getClass()
            );
            CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
            assertEquals( "3rd series put nr: " + i + " gave wrong response", ResponseCode.CHANGED.name(), attributes.getResponseCode() );
        }
        await( "number of observation after 3rd test series" ).atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == contents.size() + 1;
        } );

        int obsOffset= 0;
        for ( int i= 1; i < contents.size(); i++ )
        {
            response= (Message) spy.getEvents().get( i ).getContent();
            assertEquals(
                "wrong attributes class",
                new TypedValue< CoAPResponseAttributes >( new CoAPResponseAttributes(), null ).getClass(),
                response.getAttributes().getClass()
            );
            CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
            if ( i == 1 ) obsOffset= attributes.getOptions().getObserve().intValue() - 1;
            assertNotEquals( "observation nr: " + i + " is empty", null, response.getPayload().getValue() );
            assertTrue( "observation nr: " + i + " indicates failure", attributes.isSuccess() );
            assertEquals( "observation nr: " + i + " has wrong content", contents.get( i ), new String( (byte[]) response.getPayload().getValue() ) );
            assertEquals( "observation nr: " + i + " has wrong observe option", obsOffset + i, attributes.getOptions().getObserve().intValue() );
        }
    }

    /**
     * Test temporary observe using NON requests
     * @throws Exception should not happen in this test
     */
    @Test( timeout= 100000L )
    public void testTemporaryObserveNon() throws Exception
    {
        Event result;
        Message response;

        MuleEventSpy spy= new MuleEventSpy( "temporary" );
        spy.clear();

        //let asynchronous work happen
        pauze();
        assertEquals( "unexpected observation on start test", 0, spy.getEvents().size() );

        for ( int i= 1; i < contents.size(); i++ )
        {
            pauze();
            result= flowRunner( "do_put_temporary" ).withPayload( contents.get( i ) ).run();
            response= result.getMessage();
            assertEquals(
                "wrong attributes class",
                new TypedValue< CoAPResponseAttributes >( new CoAPResponseAttributes(), null ).getClass(),
                response.getAttributes().getClass()
            );
            CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
            assertEquals( "1st series put nr: " + i + " gave wrong response", ResponseCode.CHANGED.name(), attributes.getResponseCode() );
        }
        assertEquals( "unexpected observation after 1st test series", 0, spy.getEvents().size() );

        result= flowRunner( "start_temporary" ).withPayload( "nothing_important" ).withVariable( "msgType", "NON_CONFIRMABLE" ).run();
        response= result.getMessage();
        await( "missing observation start response" ).atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == 1;
        } );
        for ( int i= 1; i < contents.size(); i++ )
        {
            pauze();
            result= flowRunner( "do_put_temporary" ).withPayload( contents.get( i ) ).run();
            response= result.getMessage();
            assertEquals(
                "wrong attributes class",
                new TypedValue< CoAPResponseAttributes >( new CoAPResponseAttributes(), null ).getClass(),
                response.getAttributes().getClass()
            );
            CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
            assertEquals( "2nd series put nr: " + i + " gave wrong response", ResponseCode.CHANGED.name(), attributes.getResponseCode() );
        }
        await( "number of observation after 2nd test series" ).atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == contents.size();
        } );
        result= flowRunner( "stop_temporary" ).withPayload( "nothing_important" ).withVariable( "msgType", "NON_CONFIRMABLE" ).run();
        response= result.getMessage();
        await( "observation stop response" ).atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == contents.size() + 1;
        } );
        for ( int i= 1; i < contents.size(); i++ )
        {
            pauze();
            result= flowRunner( "do_put_temporary" ).withPayload( contents.get( i ) ).run();
            response= result.getMessage();
            assertEquals(
                "wrong attributes class",
                new TypedValue< CoAPResponseAttributes >( new CoAPResponseAttributes(), null ).getClass(),
                response.getAttributes().getClass()
            );
            CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
            assertEquals( "3rd series put nr: " + i + " gave wrong response", ResponseCode.CHANGED.name(), attributes.getResponseCode() );
        }
        await( "number of observation after 3rd test series" ).atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == contents.size() + 1;
        } );

        int obsOffset= 0;
        for ( int i= 1; i < contents.size(); i++ )
        {
            response= (Message) spy.getEvents().get( i ).getContent();
            assertEquals(
                "wrong attributes class",
                new TypedValue< CoAPResponseAttributes >( new CoAPResponseAttributes(), null ).getClass(),
                response.getAttributes().getClass()
            );
            CoAPResponseAttributes attributes= (CoAPResponseAttributes) response.getAttributes().getValue();
            if ( i == 1 ) obsOffset= attributes.getOptions().getObserve().intValue() - 1;
            assertNotEquals( "observation nr: " + i + " is empty", null, response.getPayload().getValue() );
            assertTrue( "observation nr: " + i + " indicates failure", attributes.isSuccess() );
            assertEquals( "observation nr: " + i + " has wrong content", contents.get( i ), new String( (byte[]) response.getPayload().getValue() ) );
            assertEquals( "observation nr: " + i + " has wrong observe option", obsOffset + i, attributes.getOptions().getObserve().intValue() );
        }
    }

    //TODO warn when not existing observe is stopped

    /**
     * Test observe notifications at max_age 
     * @throws Exception should not happen in this test
     */
    @Test( timeout= 100000L )
    public void testObserveNotifications() throws Exception
    {
        @SuppressWarnings( "unused" )
        Event result;

        MuleEventSpy spy= new MuleEventSpy( "maxage1" );
        spy.clear();

        //let asynchronous work happen
        pauze();

        assertEquals( "unexpected observation on start test", 0, spy.getEvents().size() );

        result= flowRunner( "start_maxage1" ).withPayload( "nothing_important" ).run();
        pauze( 5500 );
        result= flowRunner( "stop_maxage1" ).withPayload( "nothing_important" ).run();
        // GET observe=0 response + notifications= 1+5+1
        await( "number of notifications received" ).atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == 1 + 5 + 1;
        } );

        pauze( 5500 );
        // GET observe=0 response + notifications= 1+5
        //stop result is also handled -> 1+5+1 times
        await( "number of notifications received" ).atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == 1 + 5 + 1;
        } );
    }

    /**
     * Test observe re-registration after max_age 
     * @throws Exception should not happen in this test
     */
    @Test( timeout= 100000L )
    public void testObserveReregistration1() throws Exception
    {
        @SuppressWarnings( "unused" )
        Event result;

        MuleEventSpy spy= new MuleEventSpy( "maxage1_nonotify" );
        spy.clear();

        //let asynchronous work happen
        pauze();

        //check clean start
        assertEquals( "unexpected observation on start test", 0, spy.getEvents().size() );

        result= flowRunner( "start_maxage1_nonotify" ).withPayload( "nothing_important" ).run();
        //five notifications expected, period= max_age + notificationReregistrationBackoff per notification, plus margin
        pauze( 5000 + 500 + 100 );
        result= flowRunner( "stop_maxage1_nonotify" ).withPayload( "nothing_important" ).run();

        // GET observe=0 response + notification= 1 + 5
        await( "number of notifications received 1" ).atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == 1 + 5 + 1;
        } );
        //wait some more
        pauze( 5000 + 500 + 100 );
        //stop result is also handled -> 1+5+1 times
        await( "number of notifications received 2" ).atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == 1 + 5 + 1;
        } );
    }

    /**
     * Test observe re-registration after max_age 
     * @throws Exception should not happen in this test
     */
    @Test( timeout= 100000L )
    public void testObserveReregistration4() throws Exception
    {
        @SuppressWarnings( "unused" )
        Event result;

        MuleEventSpy spy= new MuleEventSpy( "maxage4_nonotify" );
        spy.clear();

        //let asynchronous work happen
        pauze();

        //check clean start
        assertEquals( "unexpected observation on start test", 0, spy.getEvents().size() );

        result= flowRunner( "start_maxage4_nonotify" ).withPayload( "nothing_important" ).run();
        //five notifications expected, period= max_age + notificationReregistrationBackoff per notification, plus margin
        pauze( 5 * ( 4 * 1000 + 100 ) + 500 );
        result= flowRunner( "stop_maxage4_nonotify" ).withPayload( "nothing_important" ).run();
        // GET observe=0 response + notification= 1 + 5
        await( "number of notifications received 1" ).atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == 1 + 5 + 1;
        } );
        //five notifications expected, period= max_age + notificationReregistrationBackoff per notification, plus margin
        pauze( 5 * ( 4 * 1000 + 100 ) + 500 );
        // GET observe=0 response + notification= 1 + 5
        await( "number of notifications received 2" ).atMost( 10, TimeUnit.SECONDS ).until( () -> {
            return spy.getEvents().size() == 1 + 5 + 1;
        } );
    }

    /**
     * Test list observe relations
     * @throws Exception should not happen in this test
     */
    @SuppressWarnings( "unchecked" )
    @Test( timeout= 100000L )
    public void testObserverList() throws Exception
    {
        Event result;
        Message response;

        pauze();
        //get observe list
        result= flowRunner( "observer_list" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong number of observers", 0, ( (List< String >) response.getPayload().getValue() ).size() );

        //first observe
        result= flowRunner( "start_temporary1" ).withPayload( "nothing_important" ).run();
        pauze();

        //get observe list
        result= flowRunner( "observer_list" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong number of observers", 1, ( (List< String >) response.getPayload().getValue() ).size() );
        assertTrue( "wrong observer uri", ( (List< String >) response.getPayload().getValue() ).contains( "coap://127.0.0.1/observe/temporary1" ) );
        pauze();

        //second observe
        result= flowRunner( "start_temporary2" ).withPayload( "nothing_important" ).run();
        pauze();

        //get observe list
        result= flowRunner( "observer_list" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong number of observers", 2, ( (List< String >) response.getPayload().getValue() ).size() );
        System.out.println( (List< String >) response.getPayload().getValue() );
        assertTrue( "wrong observer uri", ( (List< String >) response.getPayload().getValue() ).contains( "coap://127.0.0.1/observe/temporary1" ) );
        assertTrue( "wrong observer uri", ( (List< String >) response.getPayload().getValue() ).contains( "coap://127.0.0.1/observe/temporary2?test1=1&test2=2" ) );
        pauze();

        //remove second observe
        result= flowRunner( "stop_temporary2" ).withPayload( "nothing_important" ).run();
        pauze();

        //get observe list
        result= flowRunner( "observer_list" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong number of observers", 1, ( (List< String >) response.getPayload().getValue() ).size() );
        assertTrue( "wrong observer uri", ( (List< String >) response.getPayload().getValue() ).contains( "coap://127.0.0.1/observe/temporary1" ) );
        pauze();

        //remove first observe
        result= flowRunner( "stop_temporary1" ).withPayload( "nothing_important" ).run();
        pauze();

        //get observe list
        result= flowRunner( "observer_list" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong number of observer", 0, ( (List< String >) response.getPayload().getValue() ).size() );

    }

    /**
     * Test existence observe relations
     * @throws Exception should not happen in this test
     */
    @Test( timeout= 100000L )
    public void testObserverExists() throws Exception
    {
        Event result;
        Message response;

        //get observer 1 exists
        result= flowRunner( "exists_temporary1" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong exististence of observer1", Boolean.FALSE, response.getPayload().getValue() );
        //get observe 2 exists
        result= flowRunner( "exists_temporary2" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong exististence of observer2", Boolean.FALSE, response.getPayload().getValue() );

        //first observe
        result= flowRunner( "start_temporary1" ).withPayload( "nothing_important" ).run();

        //get observer 1 exists
        result= flowRunner( "exists_temporary1" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong exististence of observer1", Boolean.TRUE, response.getPayload().getValue() );
        //get observe 2 exists
        result= flowRunner( "exists_temporary2" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong exististence of observer2", Boolean.FALSE, response.getPayload().getValue() );

        //second observe
        result= flowRunner( "start_temporary2" ).withPayload( "nothing_important" ).run();

        //get observer 1 exists
        result= flowRunner( "exists_temporary1" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong exististence of observer1", Boolean.TRUE, response.getPayload().getValue() );
        //get observe 2 exists
        result= flowRunner( "exists_temporary2" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong exististence of observer2", Boolean.TRUE, response.getPayload().getValue() );
        //pauze();

        //remove second observe
        result= flowRunner( "stop_temporary2" ).withPayload( "nothing_important" ).run();

        //get observer 1 exists
        result= flowRunner( "exists_temporary1" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong exististence of observer1", Boolean.TRUE, response.getPayload().getValue() );
        //get observe 2 exists
        result= flowRunner( "exists_temporary2" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong exististence of observer2", Boolean.FALSE, response.getPayload().getValue() );

        //remove first observe
        result= flowRunner( "stop_temporary1" ).withPayload( "nothing_important" ).run();

        //get observer 1 exists
        result= flowRunner( "exists_temporary1" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong exististence of observer1", Boolean.FALSE, response.getPayload().getValue() );
        //get observe 2 exists
        result= flowRunner( "exists_temporary2" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong exististence of observer2", Boolean.FALSE, response.getPayload().getValue() );
    }

}
