/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2024 (teslanet.nl) Rogier Cobben
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

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
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;


/**
 * Test for inbound properties
 *
 */
@RunnerDelegateTo( Parameterized.class )
public abstract class AbstractInboundPropertyTestcase extends AbstractServerTestCase
{
    /**
     * @return the test parameters
     */
    @Parameters( name= "Request= {0}, path= {2}" )
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []
            {
                //default maxResourceBodySize on server
                { Code.GET, 5683, "/service/get_me?first=1&second=2", true },
                { Code.PUT, 5683, "/service/put_me?first=1&second=2", false },
                { Code.POST, 5683, "/service/post_me?first=1&second=2", false },
                { Code.DELETE, 5683, "/service/delete_me?first=1&second=2", true }, }
        );
    }

    /**
     * Request code to test
     */
    @Parameter( 0 )
    public Code requestCode;

    /**
     * Test server port
     */
    @Parameter( 1 )
    public int port;

    /**
    * Test resource to call
    */
    @Parameter( 2 )
    public String resourcePath;

    /**
     * True when request is not supposed to have a payload, but does
     */
    @Parameter( 3 )
    public boolean unintendedPayload;

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/properties/testserver1.xml";
    };

    /**
     * Set the resource path
     */
    @Before
    public void additionalSetUp()
    {
        setClientUri( resourcePath );
    }

    /**
     * Implement to add the option to test to the optionset
     * @param options where to add the opton
     * @throws Exception when option could not be set
     */
    abstract protected void addOption( OptionSet options ) throws Exception;

    /**
     * Implement to specify the property that should be spied on
     * @return the name of the inbound property to test
     */
    abstract protected String getPropertyName();

    /**
     * Implement to specify the option value the test should deliver
     * @return the expected option value
     * @throws Exception 
     */
    abstract protected Object getExpectedPropertyValue() throws Exception;

    /**
     * Replacement option value needed for the test.
     * @return the replacement option value
     * @throws Exception 
     */
    protected Object getReplacementValue() throws Exception
    {
        return null;
    }

    /**
     * Override and return false to specify that the response is expected to indicate failure.
     * @return {@code true} when successfull response is expected
     */
    protected boolean getExpectedSuccess()
    {
        return true;
    }

    /**
     * Get the expected response code.
     * @return The expected response code.
     * @throws Exception 
     */
    protected ResponseCode getExpectedResponseCode( Code requestCode ) throws Exception
    {
        ResponseCode responseCode;
        switch ( requestCode )
        {
            case GET:
                responseCode= ResponseCode.CONTENT;
                break;
            case POST:
                responseCode= ResponseCode.CHANGED;
                break;
            case PUT:
                responseCode= ResponseCode.CHANGED;
                break;
            case DELETE:
            default:
                responseCode= ResponseCode.DELETED;
                break;
        }
        return responseCode;
    }

    /**
     * Override to specify whether the option value is a byte array
     * @return {@code true} when the option value is a byte array
     */
    protected Boolean propertyValueIsByteArray()
    {
        return Boolean.FALSE;
    }

    /**
     * Create spy to assert the inbound property 
     * @param propertyName name of the property to inspect
     * @throws Exception 
     */
    private MuleEventSpy spyMessage() throws Exception
    {
        MuleEventSpy spy= new MuleEventSpy( getPropertyName(), getPropertyName(), getReplacementValue() );
        spy.clear();
        return spy;
    }

    /**
     * Assert the spy has collected the expected object
     * @param spy the spy that should have collected the property
     * @param expected
     */
    void assertSpy( MuleEventSpy spy, final Object expected )
    {
        assertEquals( "Spy has collected wrong number of events", 1, spy.getEvents().size() );
        assertEquals( "property has wrong class", expected.getClass(), spy.getEvents().get( 0 ).getContent().getClass() );
        if ( propertyValueIsByteArray() )
        {
            assertArrayEquals( "property has wrong value", (byte[]) expected, (byte[]) spy.getEvents().get( 0 ).getContent() );
        }
        else
        {
            assertEquals( "property has wrong value", expected, spy.getEvents().get( 0 ).getContent() );
        }
    }

    /**
     * Test inbound property
     * @throws Exception 
     */
    @Test
    public void testInbound() throws Exception
    {
        MuleEventSpy spy= spyMessage();

        Request request= new Request( requestCode );
        //set URI explicitly otherwise uri-options could get overwritten
        request.setURI( client.getURI() );
        if ( unintendedPayload ) request.setUnintendedPayload();
        addOption( request.setPayload( "<nothing_important/>" ).getOptions() );

        CoapResponse response= client.advanced( request );

        assertNotNull( "no response", response );
        assertEquals( "wrong response code", getExpectedResponseCode( requestCode ), response.getCode() );
        if ( getExpectedSuccess() )
        {
            assertTrue( "response indicates failure", response.isSuccess() );
            assertSpy( spy, getExpectedPropertyValue() );
        }
        else
        {
            assertFalse( "response indicates success", response.isSuccess() );
        }
    }
}
