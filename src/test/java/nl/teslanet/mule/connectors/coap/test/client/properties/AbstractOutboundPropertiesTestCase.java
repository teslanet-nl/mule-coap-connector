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
package nl.teslanet.mule.connectors.coap.test.client.properties;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.api.ReceivedResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.RequestBuilder;
import nl.teslanet.mule.connectors.coap.api.RequestBuilder.CoAPRequestCode;
import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;


@RunnerDelegateTo(Parameterized.class)
public abstract class AbstractOutboundPropertiesTestCase extends AbstractClientTestCase
{
    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters(name= "requestCode= {0}")
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []{
                { CoAPRequestCode.GET, ResponseCode.CONTENT },
                { CoAPRequestCode.POST, ResponseCode.CHANGED },
                { CoAPRequestCode.PUT, ResponseCode.CHANGED },
                { CoAPRequestCode.DELETE, ResponseCode.DELETED } } );
    }

    /**
     * Request code to test
     */
    @Parameter(0)
    public CoAPRequestCode requestCode;

    /**
     * The expected response code.
     */
    @Parameter(1)
    public ResponseCode expectedResponseCode;

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-client-config/properties/testclient2.xml";
    };

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase#getTestServer()
     */
    @Override
    protected CoapServer getTestServer() throws Exception
    {
        return new PropertiesTestServer( getStrategy() );
    }

    /**
     * The path to use on outbound test
     * @return the path
     */
    protected String getResourcePath()
    {
        return "/property/validate";
    }

    /**
     * Implement this method to specify the property to test.
     * @return the property name
     */
    protected abstract String getPropertyName();

    /**
     * The property value to set on outbound test
     * @return the value to set
     * @throws InvalidETagException 
     */
    protected Object getOutboundPropertyValue() throws InvalidETagException
    {
        return new String( getPropertyName() + "_test_value" );
    }

    /**
     * Inserts the option value into options map. 
     * Default implementation inserts the Option using getOutboundPropertyValue()
     * Needs to be overridden when other properties than an option is to be inserted
     * @param options the attributes to insert the property in
     * @throws InvalidETagException 
     */
    @Deprecated
    protected void insertOutboundProperty( HashMap< String, Object > options ) throws InvalidETagException
    {
        options.put( getPropertyName(), getOutboundPropertyValue() );
    }

    /**
     * Override to set the expected exception if any.
     * @return The expected exception.
     */
    protected Exception getExpectedException()
    {
        return null;
    }

    /**
     * The property value that is expected to receive in inbound test
     * @return the value to expect
     */
    protected Object getExpectedInboundPropertyValue()
    {
        return new String( getPropertyName() + "_test_value" );
    }

    /**
     * Implement this method to specify the strategy the coap test server has to use
     * in the test.
     * @return the Options strategy to use
     * @throws InvalidETagException 
     */
    protected abstract OptionStrategy getStrategy() throws InvalidETagException;

    /**
     * Override this method when a specific flow has to be used. 
     * @return the flow name extension, that will be added to the base flow name
     */
    protected String getFlowNameExtension()
    {
        return getPropertyName();
    }

    /**
     * Test outbound property
     * @throws Exception should not happen in this test
     */
    @Test
    public void testOutboundProperty() throws Exception
    {
        RequestBuilder requestAttibutes= new RequestBuilder();
        requestAttibutes.setRequestCode( requestCode );
        requestAttibutes.setHost( "127.0.0.1" );
        requestAttibutes.setPath( getResourcePath() );
        Event result;

        if ( getExpectedException() != null )
        {
            Exception e= assertThrows( Exception.class, () -> {
                runFlow();
            } );
            assertTrue( "wrong exception message", e.getMessage().contains( getExpectedException().getMessage() ) );
            assertEquals( "wrong exception cause", getExpectedException().getClass(), e.getCause().getClass() );
        }
        else
        {
            result= runFlow();
            Message response= result.getMessage();
            assertEquals(
                "wrong attributes class",
                new TypedValue< ReceivedResponseAttributes >( new ReceivedResponseAttributes(), null ).getClass(),
                response.getAttributes().getClass() );
            ReceivedResponseAttributes attributes= (ReceivedResponseAttributes) response.getAttributes().getValue();
            assertEquals( "wrong response code", expectedResponseCode.name(), attributes.getResponseCode() );
        }
    }

    private Event runFlow() throws InvalidETagException, Exception
    {
        return flowRunner( "do_request-" + getFlowNameExtension() ).withPayload( "nothing_important" ).withVariable( "requestCode", requestCode ).withVariable(
            "host",
            "127.0.0.1" ).withVariable( "path", getResourcePath() ).withVariable( "option", getOutboundPropertyValue() ).run();
    }
}
