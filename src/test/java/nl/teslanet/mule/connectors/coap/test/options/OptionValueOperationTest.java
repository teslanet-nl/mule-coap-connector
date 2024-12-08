/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2024 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.test.options;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultBytesValue;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractTestCase;


@RunnerDelegateTo( Parameterized.class )
public class OptionValueOperationTest extends AbstractTestCase
{
    /**
     * input and output data types to test.
     */
    private enum DataType
    {

        EMPTY, BINARY, HEX, NUMBER, STRING;
    }

    //testdata
    private static String hexData= "4554";

    private static byte[] binData= new BigInteger( hexData, 16 ).toByteArray();;

    private static Long numberData= Long.valueOf( hexData, 16 );

    private static String stringData= new String( binData, StandardCharsets.UTF_8 );

    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters( name= "in= {0}" )
    public static Collection< Object[] > getTests()
    {
        //tests to run
        ArrayList< Object[] > tests= new ArrayList< Object[] >();
        //build list
        for ( DataType inType : DataType.values() )
        {
            tests.add( new Object []{ inType, getInPayload( inType ), getOutPayload( inType ) } );
        }
        return tests;
    }

    /**
     * The input payload type that is input.
     */
    @Parameter( 0 )
    public DataType inPayloadType;

    /**
     * The input payload.
     */
    @Parameter( 1 )
    public Object inPayload;

    /**
     * The output payload that is expected.
     */
    @Parameter( 2 )
    public Object outPayload;

    /**
     * Get input payload.
     */
    private static Object getInPayload( DataType inType )
    {
        switch ( inType )
        {
            case BINARY:
                return binData;
            case HEX:
                return hexData;
            case NUMBER:
                return numberData;
            case STRING:
                return stringData;
            case EMPTY:
            default:
                return OptionUtils.EMPTY_BYTES;
        }
    };

    /**
     * Get output payload.
     */
    private static Object getOutPayload( DataType inType )
    {
        switch ( inType )
        {
            case BINARY:
            case HEX:
            case NUMBER:
            case STRING:
                return binData;
            default:
                return OptionUtils.EMPTY_BYTES;
        }
    };

    /**
     * Mule test application.
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-config/optionvalue/testflow1.xml";
    };

    /**
     * Test CoAP request
     * @throws Exception should not happen in this test
     */
    @Test
    public void testConRequest() throws Exception
    {
        CoreEvent result= flowRunner( "testflow" )
            .withPayload( inPayload )
            .withVariable( "in", inPayloadType.name() )
            .run();
        Message response= result.getMessage();

        assertNotNull( "no mule event", response );
        Object responsePayload= TypedValue.unwrap( response.getPayload() );
        assertTrue( "wrong response payload class", responsePayload instanceof DefaultBytesValue );
        assertArrayEquals( (byte[]) outPayload, ( (DefaultBytesValue) responsePayload ).getValue() );
    }
}