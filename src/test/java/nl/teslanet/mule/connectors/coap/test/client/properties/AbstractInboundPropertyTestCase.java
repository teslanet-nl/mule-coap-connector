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


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.message.Message;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.api.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultEntityTag;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractClientTestCase;


/**
 * Abstract class for testing inbound properties
 *
 */
@RunnerDelegateTo( Parameterized.class )
public abstract class AbstractInboundPropertyTestCase extends AbstractClientTestCase
{
    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters( name= "request= {0}  " )
    public static Collection< Object[] > data()
    {
        return Arrays.asList(
            new Object [] []
            {
                { Code.GET, "/property/setoption", ResponseCode.CONTENT },
                { Code.PUT, "/property/setoption", ResponseCode.CHANGED },
                { Code.POST, "/property/setoption", ResponseCode.CHANGED },
                { Code.DELETE, "/property/setoption", ResponseCode.DELETED } }
        );
    }

    /**
     * Request code to test
     */
    @Parameter( 0 )
    public Code requestCode;

    /**
     * The path of the resource to call.
     */
    @Parameter( 1 )
    public String path;

    /**
     * The expected response code.
     */
    @Parameter( 2 )
    public ResponseCode expectedResponseCode;

    /* (non-Javadoc)
     * @see org.mule.munit.runner.functional.FunctionalMunitSuite#getConfigResources()
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-client-config/properties/testclient1.xml";
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
     * Implement this method to specify the property to test.
     * @return the property name
     */
    protected abstract String getPropertyName();

    /**
     * The property value that is expected to receive in inbound test
     * @return the value to expect
     * @throws Exception 
     */
    protected Object getExpectedInboundPropertyValue() throws Exception
    {
        return new String( getPropertyName() + "_test_value" );
    }

    /**
     * Implement this method to specify the strategy the coap test server has to use
     * in the test.
     * @return the Options strategy to use
     * @throws Exception 
     */
    protected abstract OptionStrategy getStrategy() throws Exception;

    /**
     * Override this method when a specific flow has to be used. 
     * @return the flow name extension, that will be added to the base flow name
     */
    protected String getFlowNameExtension()
    {
        //default is no extension
        return "";
    }

    /**
     * Override this method when a specific coap resource has to be used. 
     * @return the path extension, that will be added to the base path
     */
    protected String getPathExtension()
    {
        //default is no extension
        return "";
    }

    /**
     * The assertion needs to know what to expect
     * @return the type of the property expected
     */
    protected PropertyType getPropertyType()
    {
        // default type is object
        return PropertyType.Object;
    }

    /**
     * Fetches the property value from response attributes. 
     * Default implementation fetches the Option using getPropertyName()
     * Needs to be overridden when other properties than an option is to be retrieved
     * @param attributes the attributes to fetch the property from
     * @return the property value
     */
    abstract protected Object fetchInboundProperty( CoapResponseAttributes attributes );

    /**
     * Test inbound property
     * @throws Exception should not happen in this test
     */
    @Test
    public void testInboundProperty() throws Exception
    {
        Event result= flowRunner( "do_request" ).withPayload( "nothing_important" ).withVariable( "code", requestCode.name() ).withVariable( "host", "127.0.0.1" ).withVariable(
            "port",
            null
        ).withVariable( "path", path + getPathExtension() ).keepStreamsOpen().run();
        Message response= result.getMessage();
        CoapResponseAttributes attributes= null;;

        switch ( getPropertyType() )
        {
            case NoResponse:
            {
                break;
            }
            default:
                assertTrue( "wrong attributes class", response.getAttributes().getValue() instanceof CoapResponseAttributes );

                attributes= (CoapResponseAttributes) response.getAttributes().getValue();
                assertEquals( "wrong response code", expectedResponseCode.name(), attributes.getResponseCode() );
        }
        switch ( getPropertyType() )
        {
            case CollectionOfByteArray:
            {
                @SuppressWarnings( "unchecked" )
                Collection< byte[] > property= (Collection< byte[] >) fetchInboundProperty( attributes );
                assertNotNull( "property is not found in inbound scope", property );

                @SuppressWarnings( "unchecked" )
                Collection< byte[] > expected= (Collection< byte[] >) getExpectedInboundPropertyValue();
                assertEquals( "option value list length differ", expected.size(), property.size() );

                Iterator< byte[] > propertyIt= property.iterator();
                Iterator< byte[] > expectedIt= expected.iterator();
                while ( propertyIt.hasNext() && expectedIt.hasNext() )
                {
                    byte[] optionValue= propertyIt.next();
                    byte[] expectedValue= expectedIt.next();
                    assertArrayEquals( "value in collection not equal", expectedValue, optionValue );
                } ;
            }
                break;

            case CollectionOfObject:
            {
                @SuppressWarnings( "unchecked" )
                Collection< Object > property= (Collection< Object >) fetchInboundProperty( attributes );
                assertNotNull( "property is not found in inbound scope", property );

                @SuppressWarnings( "unchecked" )
                Collection< Object > expected= (Collection< Object >) getExpectedInboundPropertyValue();
                assertEquals( "option value list length differ", expected.size(), property.size() );

                Iterator< Object > propertyIt= property.iterator();
                Iterator< Object > expectedIt= expected.iterator();
                while ( propertyIt.hasNext() && expectedIt.hasNext() )
                {
                    Object optionValue= propertyIt.next();
                    Object expectedValue= expectedIt.next();
                    assertEquals( "value in collection not equal", expectedValue, optionValue );
                } ;
            }
                break;

            case CollectionOfETag:
            {
                @SuppressWarnings( "unchecked" )
                Collection< DefaultEntityTag > property= (Collection< DefaultEntityTag >) fetchInboundProperty( attributes );
                assertNotNull( "property is not found in inbound scope", property );

                @SuppressWarnings( "unchecked" )
                Collection< DefaultEntityTag > expected= (Collection< DefaultEntityTag >) getExpectedInboundPropertyValue();
                assertEquals( "option value list length differ", expected.size(), property.size() );

                Iterator< DefaultEntityTag > propertyIt= property.iterator();
                Iterator< DefaultEntityTag > expectedIt= expected.iterator();
                while ( propertyIt.hasNext() && expectedIt.hasNext() )
                {
                    DefaultEntityTag optionValue= propertyIt.next();
                    DefaultEntityTag expectedValue= expectedIt.next();
                    assertEquals( "value in collection not equal", expectedValue, optionValue );
                } ;
            }
                break;

            case ByteArray:
                assertArrayEquals( "wrong inbound property value", (byte[]) getExpectedInboundPropertyValue(), (byte[]) fetchInboundProperty( attributes ) );
                break;

            case ETag:
                assertEquals( "wrong inbound property value", (DefaultEntityTag) getExpectedInboundPropertyValue(), (DefaultEntityTag) fetchInboundProperty( attributes ) );
                break;

            case NoResponse:
                assertEquals( "received unexpected response", "NO_RESPONSE", getPayloadAsString( response ) );
                break;

            default:
                assertEquals( "wrong inbound property value", getExpectedInboundPropertyValue(), fetchInboundProperty( attributes ) );
                break;
        }
    }
}
