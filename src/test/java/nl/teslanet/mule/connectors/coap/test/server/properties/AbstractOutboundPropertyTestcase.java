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
package nl.teslanet.mule.connectors.coap.test.server.properties;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractServerTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;


/**
 * Test for outbound properties
 *
 */
@RunnerDelegateTo(Parameterized.class)
public abstract class AbstractOutboundPropertyTestcase extends AbstractServerTestCase
{
    @Parameters(name= "Request= {0}, path= {2}")
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []{
                //default maxResourceBodySize on server
                { Code.GET, 5683, "/service/get_me", true },
                { Code.PUT, 5683, "/service/put_me", false },
                { Code.POST, 5683, "/service/post_me", false },
                { Code.DELETE, 5683, "/service/delete_me", true }, } );
    }

    /**
     * Request code to test
     */
    @Parameter(0)
    public Code requestCode;

    /**
     * Test server port
     */
    @Parameter(1)
    public int port;

    /**
    * Test resource to call
    */
    @Parameter(2)
    public String resourcePath;

    /**
     * True when request is not supposed to have a payload, but does
     */
    @Parameter(3)
    public boolean unintendedPayload;

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/properties/testserver2.xml";
    };

    /**
     * set the resource path
     */
    @Before
    public void additionalSetUp()
    {
        setClientPath( resourcePath );
    }

    /**
     * Implement to specify how option should be retrieved from optionset
     * @param options where to fetch the option from
     * @return
     */
    abstract protected Object fetchOption( OptionSet options );

    /**
     * Implement to specify the outbound property that should be set
     * @return the name of the outbound property to test
     */
    abstract protected String getPropertyName();

    /**
     * Implement to specify the outbound property value to set
     * @return the value of the outbound property to set
     */
    abstract protected Object getPropertyValue() throws Exception;

    /**
     * Implement to specify the option value to expect in the response
     * @return the expected value
     */
    abstract protected Object getExpectedOptionValue() throws Exception;

    /**
     * Override and return false to specify that the response is expected to indicate failure.
     * @return {@code true} when successfull response is expected
     */
    protected boolean getExpectedSuccess()
    {
        return true;
    }

    /**
     * Override to specify whether the option is an collection of ByteArray
     * @return {@code true} when option is a collection of ByteArray
     */
    protected boolean optionValueIsCollectionOfByteArray()
    {
        return false;
    }

    /**
     * Override to specify whether the option is an collection of objects of which string representations are to compare
     * @return {@code true} when option is a collection of ByteArray
     */
    protected boolean optionValueIsCollectionOfStringable()
    {
        return false;
    }

    /**
     * Override to specify whether the option is an ByteArray
     * @return {@code true} when option is a ByteArray
     */
    protected boolean optionValueIsByteArray()
    {
        return false;
    }

    /**
     * Mock that sets the outbound property in the Mule flow
     * @param propertyName name of the outbound property to set
     * @param propertyValue value to set on the property
     */
    private MuleEventSpy mockMessage( String propertyName, Object propertyValue )
    {
        MuleEventSpy spy= new MuleEventSpy( propertyName, propertyName, propertyValue );
        spy.clear();
        return spy;
    }

    /**
     * Test outbound property
     * @throws Exception
     */
    @Test
    public void testOutbound() throws Exception
    {
        mockMessage( getPropertyName(), getPropertyValue() );

        Request request= new Request( requestCode );
        if ( unintendedPayload ) request.setUnintendedPayload();
        request.setPayload( "nothing important" );

        CoapResponse response= client.advanced( request );

        assertNotNull( "get gave no response", response );
        assertEquals( "response succes/failure unexpected", getExpectedSuccess(), response.isSuccess() );

        if ( optionValueIsCollectionOfByteArray() )
        {
            @SuppressWarnings("unchecked")
            Collection< byte[] > option= (Collection< byte[] >) fetchOption( response.getOptions() );

            @SuppressWarnings("unchecked")
            Collection< byte[] > expected= (Collection< byte[] >) getExpectedOptionValue();
            assertEquals( "option value list length differ", expected.size(), option.size() );

            Iterator< byte[] > optionIt= option.iterator();
            Iterator< byte[] > expectedIt= expected.iterator();
            while ( optionIt.hasNext() && expectedIt.hasNext() )
            {
                byte[] optionValue= optionIt.next();
                byte[] expectedValue= expectedIt.next();
                assertArrayEquals( "value in collection not equal", expectedValue, optionValue );
            } ;
        }
        else if ( optionValueIsCollectionOfStringable() )
        {
            @SuppressWarnings("unchecked")
            Collection< Object > option= (Collection< Object >) fetchOption( response.getOptions() );

            @SuppressWarnings("unchecked")
            Collection< Object > expected= (Collection< Object >) getExpectedOptionValue();
            assertEquals( "option value list length differ", expected.size(), option.size() );

            Iterator< Object > optionIt= option.iterator();
            Iterator< Object > expectedIt= expected.iterator();
            while ( optionIt.hasNext() && expectedIt.hasNext() )
            {
                Object optionValue= optionIt.next();
                Object expectedValue= expectedIt.next();
                assertEquals( "value in collection not equal", expectedValue.toString(), optionValue.toString() );
            } ;
        }
       else if ( optionValueIsByteArray() )
        {
            assertArrayEquals( "option has wrong value", (byte[]) getExpectedOptionValue(), (byte[]) fetchOption( response.getOptions() ) );
        }
        else
        {
            assertEquals( "option has wrong value", getExpectedOptionValue(), fetchOption( response.getOptions() ) );
        }
    }
}
