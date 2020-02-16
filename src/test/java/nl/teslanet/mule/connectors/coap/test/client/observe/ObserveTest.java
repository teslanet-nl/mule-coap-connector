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
package nl.teslanet.mule.connectors.coap.test.client.observe;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;

import nl.teslanet.mule.connectors.coap.api.ReceivedResponseAttributes;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;
import nl.teslanet.shaded.org.eclipse.californium.core.CoapServer;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.CoAP.ResponseCode;


/**
 * Test observe
 *
 */
//TODO test exceptions in handler
//TODO test configuration faults (no host)
public class ObserveTest extends AbstractClientTestCase
{
    static final long PAUZE= 2000L;

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
        contents.clear();
        contents= null;
    }

    /**
     * Test permanent observe 
     * @throws Exception should not happen in this test
     */
    @Test(timeout= 100000L)
    public void testPermanentObserve() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( "permanent" );
        spy.clear();

        //let asynchronous work happen
        Thread.sleep( PAUZE );

        for ( int i= 1; i < contents.size(); i++ )
        {
            Thread.sleep( PAUZE );
            Event result= flowRunner( "do_put_permanent" ).withPayload( contents.get( i ) ).run();
            Message response= result.getMessage();
            assertEquals(
                "wrong attributes class",
                new TypedValue< ReceivedResponseAttributes >( new ReceivedResponseAttributes(), null ).getClass(),
                response.getAttributes().getClass() );
            ReceivedResponseAttributes attributes= (ReceivedResponseAttributes) response.getAttributes().getValue();
            assertEquals( "put nr: " + i + " gave wrong response", ResponseCode.CHANGED.name(), attributes.getResponseCode() );
        }

        Thread.sleep( PAUZE );
        assertEquals( "wrong count of observations", contents.size(), spy.getEvents().size() );

        int obsOffset= 0;
        for ( int i= 0; i < spy.getEvents().size(); i++ )
        {
            Message response= (Message) spy.getEvents().get( i ).getContent();
            assertEquals(
                "wrong attributes class",
                new TypedValue< ReceivedResponseAttributes >( new ReceivedResponseAttributes(), null ).getClass(),
                response.getAttributes().getClass() );
            ReceivedResponseAttributes attributes= (ReceivedResponseAttributes) response.getAttributes().getValue();
            if ( i == 0 ) obsOffset= ( (Integer) attributes.getOptions().get( "coap.opt.observe" ) ).intValue();
            assertNotEquals( "observation nr: " + i + " is empty", null, response.getPayload() );
            assertTrue( "observation nr: " + i + " indicates failure", attributes.isSuccess() );
            assertEquals( "observation nr: " + i + " has wrong content", contents.get( i ), new String( (byte[]) response.getPayload().getValue() ) );
            assertEquals( "observation nr: " + i + " has wrong observe option", obsOffset + i, ( (Integer) attributes.getOptions().get( "coap.opt.observe" ) ).intValue() );
        }

    }

    /**
     * Test temporary observe 
     * @throws Exception should not happen in this test
     */
    @Test
    public void testTemporaryObserve() throws Exception
    {
        Event result;
        Message response;

        MuleEventSpy spy= new MuleEventSpy( "temporary" );
        spy.clear();

        //let asynchronous work happen
        Thread.sleep( PAUZE );

        assertEquals( "unexpected obsevation on start test", 0, spy.getEvents().size() );

        for ( int i= 1; i < contents.size(); i++ )
        {
            Thread.sleep( PAUZE );
            result= flowRunner( "do_put_temporary" ).withPayload( contents.get( i ) ).run();
            response= result.getMessage();
            assertEquals(
                "wrong attributes class",
                new TypedValue< ReceivedResponseAttributes >( new ReceivedResponseAttributes(), null ).getClass(),
                response.getAttributes().getClass() );
            ReceivedResponseAttributes attributes= (ReceivedResponseAttributes) response.getAttributes().getValue();
            assertEquals( "1st series put nr: " + i + " gave wrong response", ResponseCode.CHANGED.name(), attributes.getResponseCode() );
        }

        //let asynchronous work happen
        Thread.sleep( PAUZE );
        assertEquals( "unexpected obsevation after 1st test series", 0, spy.getEvents().size() );

        result= flowRunner( "start_temporary" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        Thread.sleep( PAUZE );
        assertEquals( "missing observation start response", 1, spy.getEvents().size() );

        for ( int i= 1; i < contents.size(); i++ )
        {
            Thread.sleep( PAUZE );
            result= flowRunner( "do_put_temporary" ).withPayload( contents.get( i ) ).run();
            response= result.getMessage();
            assertEquals(
                "wrong attributes class",
                new TypedValue< ReceivedResponseAttributes >( new ReceivedResponseAttributes(), null ).getClass(),
                response.getAttributes().getClass() );
            ReceivedResponseAttributes attributes= (ReceivedResponseAttributes) response.getAttributes().getValue();
            assertEquals( "2nd series put nr: " + i + " gave wrong response", ResponseCode.CHANGED.name(), attributes.getResponseCode() );
        }
        Thread.sleep( PAUZE );
        assertEquals( "unexpected number of obsevation after 2nd test series", contents.size(), spy.getEvents().size() );

        result= flowRunner( "stop_temporary" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        Thread.sleep( PAUZE );
        assertEquals( "missing observation stop response", contents.size() + 1, spy.getEvents().size() );

        for ( int i= 1; i < contents.size(); i++ )
        {
            Thread.sleep( PAUZE );
            result= flowRunner( "do_put_temporary" ).withPayload( contents.get( i ) ).run();
            response= result.getMessage();
            assertEquals(
                "wrong attributes class",
                new TypedValue< ReceivedResponseAttributes >( new ReceivedResponseAttributes(), null ).getClass(),
                response.getAttributes().getClass() );
            ReceivedResponseAttributes attributes= (ReceivedResponseAttributes) response.getAttributes().getValue();
            assertEquals( "3rd series put nr: " + i + " gave wrong response", ResponseCode.CHANGED.name(), attributes.getResponseCode() );
        }
        Thread.sleep( PAUZE );
        assertEquals( "unexpected number of obsevation after 3rd test series", contents.size() + 1, spy.getEvents().size() );

        int obsOffset= 0;
        for ( int i= 1; i < contents.size(); i++ )
        {
            response= (Message) spy.getEvents().get( i ).getContent();
            assertEquals(
                "wrong attributes class",
                new TypedValue< ReceivedResponseAttributes >( new ReceivedResponseAttributes(), null ).getClass(),
                response.getAttributes().getClass() );
            ReceivedResponseAttributes attributes= (ReceivedResponseAttributes) response.getAttributes().getValue();
            if ( i == 1 ) obsOffset= ( (Integer) attributes.getOptions().get( "coap.opt.observe" ) ).intValue() - 1;
            assertNotEquals( "observation nr: " + i + " is empty", null, response.getPayload().getValue() );
            assertTrue( "observation nr: " + i + " indicates failure", attributes.isSuccess() );
            assertEquals( "observation nr: " + i + " has wrong content", contents.get( i ), new String( (byte[]) response.getPayload().getValue() ) );
            assertEquals( "observation nr: " + i + " has wrong observe option", obsOffset + i, ( (Integer) attributes.getOptions().get( "coap.opt.observe" ) ).intValue() );
        }
    }

    //TODO warn when not existing observe is stopped

    /**
     * Test observe notifications at max_age 
     * @throws Exception should not happen in this test
     */
    @Test
    public void testObserveNotifications() throws Exception
    {
        @SuppressWarnings("unused")
        Event result;

        MuleEventSpy spy= new MuleEventSpy( "maxage1" );
        spy.clear();

        //let asynchronous work happen
        Thread.sleep( PAUZE );

        assertEquals( "unexpected obsevation on start test", 0, spy.getEvents().size() );

        result= flowRunner( "start_maxage1" ).withPayload( "nothing_important" ).run();
        Thread.sleep( 5500 );
        // GET observe=0 response + notifications= 1+5
        assertEquals( "wrong number of notifications received", 1 + 5, spy.getEvents().size() );

        result= flowRunner( "stop_maxage1" ).withPayload( "nothing_important" ).run();
        Thread.sleep( 5500 );
        // GET observe=0 response + notifications= 1+5
        //stop result is also handled -> 1+5+1 times
        assertEquals( "unexpected notifications received", 1 + 5 + 1, spy.getEvents().size() );
    }

    /**
     * Test observe re-registration after max_age 
     * @throws Exception should not happen in this test
     */
    //TODO cf issue #917, notificationReregistrationBackoff not implemented
    @Test
    public void testObserveReregistration1() throws Exception
    {
        @SuppressWarnings("unused")
        Event result;

        MuleEventSpy spy= new MuleEventSpy( "maxage1_nonotify" );
        spy.clear();

        //let asynchronous work happen
        Thread.sleep( PAUZE );

        //check clean start
        assertEquals( "unexpected obsevation on start test", 0, spy.getEvents().size() );

        result= flowRunner( "start_maxage1_nonotify" ).withPayload( "nothing_important" ).run();
        //five notifications expected, period= max_age + notificationReregistrationBackoff per notification, plus margin
        Thread.sleep( 5000 + 500 + 100 );
        // GET observe=0 response + notification= 1 + 5
        assertEquals( "wrong number of notifications received", 1 + 5, spy.getEvents().size() );

        result= flowRunner( "stop_maxage1_nonotify" ).withPayload( "nothing_important" ).run();
        //wait some more
        Thread.sleep( 5000 + 500 + 100 );
        //stop result is also handled -> 1+5+1 times
        assertEquals( "unexpected notifications received", 1 + 5 + 1, spy.getEvents().size() );
    }

    /**
     * Test observe re-registration after max_age 
     * @throws Exception should not happen in this test
     */
    @Test
    public void testObserveReregistration4() throws Exception
    {
        @SuppressWarnings("unused")
        Event result;

        MuleEventSpy spy= new MuleEventSpy( "maxage4_nonotify" );
        spy.clear();

        //let asynchronous work happen
        Thread.sleep( PAUZE );

        //check clean start
        assertEquals( "unexpected obsevation on start test", 0, spy.getEvents().size() );

        result= flowRunner( "start_maxage4_nonotify" ).withPayload( "nothing_important" ).run();
        //five notifications expected, period= max_age + notificationReregistrationBackoff per notification, plus margin
        Thread.sleep( 5 * ( 4 * 1000 + 100 ) + 500 );
        // GET observe=0 response + notification= 1 + 5
        assertEquals( "wrong number of notifications received", 1 + 5, spy.getEvents().size() );

        result= flowRunner( "stop_maxage4_nonotify" ).withPayload( "nothing_important" ).run();
        //five notifications expected, period= max_age + notificationReregistrationBackoff per notification, plus margin
        Thread.sleep( 5 * ( 4 * 1000 + 100 ) + 500 );
        // GET observe=0 response + notification= 1 + 5
        assertEquals( "unexpected notifications received", 1 + 5 + 1, spy.getEvents().size() );

    }

    /**
     * Test list observe relations
     * @throws Exception should not happen in this test
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testListObserve() throws Exception
    {
        Event result;
        Message response;

        Thread.sleep( PAUZE );
        //get observe list
        result= flowRunner( "observer_list" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong number of observers", 0, ( (List< String >) response.getPayload().getValue() ).size() );

        //first observe
        result= flowRunner( "start_temporary1" ).withPayload( "nothing_important" ).run();
        Thread.sleep( PAUZE );

        //get observe list
        result= flowRunner( "observer_list" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong number of observers", 1, ( (List< String >) response.getPayload().getValue() ).size() );
        assertEquals( "wrong observer uri", "coap://127.0.0.1:5683/observe/temporary1", ( (List< String >) response.getPayload().getValue() ).get( 0 ) );
        Thread.sleep( PAUZE );

        //second observe
        result= flowRunner( "start_temporary2" ).withPayload( "nothing_important" ).run();
        Thread.sleep( PAUZE );

        //get observe list
        result= flowRunner( "observer_list" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong number of observers", 2, ( (List< String >) response.getPayload().getValue() ).size() );
        assertEquals( "wrong observer uri", "coap://127.0.0.1:5683/observe/temporary1", ( (List< String >) response.getPayload().getValue() ).get( 0 ) );
        assertEquals( "wrong observer uri", "coap://127.0.0.1:5683/observe/temporary2", ( (List< String >) response.getPayload().getValue() ).get( 1 ) );
        Thread.sleep( PAUZE );

        //remove second observe
        result= flowRunner( "stop_temporary2" ).withPayload( "nothing_important" ).run();
        Thread.sleep( PAUZE );

        //get observe list
        result= flowRunner( "observer_list" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong number of observers", 1, ( (List< String >) response.getPayload().getValue() ).size() );
        assertEquals( "wrong observer uri", "coap://127.0.0.1:5683/observe/temporary1", ( (List< String >) response.getPayload().getValue() ).get( 0 ) );
        Thread.sleep( PAUZE );

        //remove first observe
        result= flowRunner( "stop_temporary1" ).withPayload( "nothing_important" ).run();
        Thread.sleep( PAUZE );

        //get observe list
        result= flowRunner( "observer_list" ).withPayload( "nothing_important" ).run();
        response= result.getMessage();
        assertEquals( "wrong number of observer", 0, ( (List< String >) response.getPayload().getValue() ).size() );

    }
}
