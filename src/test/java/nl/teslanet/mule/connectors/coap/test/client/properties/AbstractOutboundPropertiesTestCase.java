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
package nl.teslanet.mule.connectors.coap.test.client.properties;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.message.Message;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.api.CoapRequestCode;
import nl.teslanet.mule.connectors.coap.api.RequestParams;
import nl.teslanet.mule.connectors.coap.api.attributes.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;


@RunnerDelegateTo( Parameterized.class )
public abstract class AbstractOutboundPropertiesTestCase extends AbstractClientTestCase
{
    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters( name= "requestCode= {0}" )
    public static Collection< Object[] > data()
    {
        return Arrays
            .asList( new Object [] []
            { { CoapRequestCode.GET }, { CoapRequestCode.POST }, { CoapRequestCode.PUT }, { CoapRequestCode.DELETE } }
            );
    }

    /**
     * Request code to test
     */
    @Parameter( 0 )
    public CoapRequestCode requestCode;

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
     * The alias of the option to set on outbound test
     * @return the alias.
     */
    protected String getOutboundOptionAlias()
    {
        return null;
    }

    /**
     * The property value to set on outbound test
     * @return the value to set
     * @throws OptionValueException 
     */
    protected Object getOutboundPropertyValue() throws OptionValueException
    {
        return new String( getPropertyName() + "_test_value" );
    }

    /**
     * Inserts the option value into options map. 
     * Default implementation inserts the Option using getOutboundPropertyValue()
     * Needs to be overridden when other properties than an option is to be inserted
     * @param options the attributes to insert the property in
     * @throws OptionValueException 
     */
    @Deprecated
    protected void insertOutboundProperty( HashMap< String, Object > options ) throws OptionValueException
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
     * Override to set the expected responseCode.
     * @return The expected responseCode.
     */
    protected ResponseCode getExpectedResponseCode( CoapRequestCode requestCode )
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
     * @throws OptionValueException 
     */
    protected abstract OptionStrategy getStrategy() throws OptionValueException, OptionValueException;

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
        RequestParams requestAttibutes= new RequestParams();
        requestAttibutes.setRequestCode( requestCode );
        requestAttibutes.setHost( "127.0.0.1" );
        requestAttibutes.setPath( getResourcePath() );
        Event result;

        if ( getExpectedException() != null )
        {
            Exception e= assertThrows( Exception.class, () -> {
                runFlow();
            } );
            assertEquals( "wrong exception cause", getExpectedException().getClass(), e.getCause().getClass() );
            assertTrue(
                "wrong exception message content, expected frase: <" + getExpectedException().getMessage()
                    + "> but was: <" + e.getMessage() + ">",
                e.getMessage().contains( getExpectedException().getMessage() )
            );
        }
        else
        {
            result= runFlow();
            Message response= result.getMessage();
            assertTrue(
                "wrong attributes class",
                response.getAttributes().getValue() instanceof CoapResponseAttributes
            );

            CoapResponseAttributes attributes= (CoapResponseAttributes) response.getAttributes().getValue();
            assertEquals(
                "wrong response code",
                getExpectedResponseCode( requestCode ).name(),
                attributes.getResponseCode()
            );
        }
    }

    private Event runFlow() throws OptionValueException, Exception
    {
        return flowRunner( "do_request-" + getFlowNameExtension() )
            .withPayload( "nothing_important" )
            .withVariable( "requestCode", requestCode )
            .withVariable( "host", "127.0.0.1" )
            .withVariable( "path", getResourcePath() )
            .withVariable( "option", getOutboundPropertyValue() )
            .withVariable( "other", getOutboundOptionAlias() )
            .run();
    }
}
