/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2023 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.test.entitytag;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import nl.teslanet.mule.connectors.coap.test.utils.AbstractTestCase;


@RunnerDelegateTo( Parameterized.class )
public class EntityTagOperationTest extends AbstractTestCase
{
    /**
     * input and output data types to test.
     */
    private enum DataType
    {

        EMPTY(byte[].class), BINARY(byte[].class), HEX(String.class), NUMBER(Long.class), STRING(String.class);

        /**
         * The class of the datatype.
         */
        private final Class< ? > clazz;

        /**
         * Constructor.
         * @param clazz The class of the datatype.
         */
        DataType( Class< ? > clazz )
        {
            this.clazz= clazz;
        }

        /**
         * @return The class of the datatype.
         */
        public Class< ? > getClazz()
        {
            return clazz;
        }
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
    @Parameters( name= "in= {0}, out= {2}" )
    public static Collection< Object[] > getTests()
    {
        //tests to run
        ArrayList< Object[] > tests= new ArrayList< Object[] >();
        //build list
        for ( DataType inType : DataType.values() )
        {
            for ( DataType outType : DataType.values() )
            {
                if ( outType != DataType.EMPTY )
                {
                    tests.add( new Object []{ inType, getInPayload( inType ), outType, getOutPayload( inType, outType) } );
                }
            }
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
     * The output payload type that is expected.
     */
    @Parameter( 2 )
    public DataType outPayloadType;

    /**
     * The output payload that is expected.
     */
    @Parameter( 3 )
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
    private static Object getOutPayload( DataType inType, DataType outType )
    {
        if ( inType == DataType.EMPTY )
        {
            switch ( outType )
            {
                case BINARY:
                    return OptionUtils.EMPTY_BYTES;
                case HEX:
                    return OptionUtils.EMPTY_STRING;
                case NUMBER:
                    return 0L;
                case STRING:
                    return OptionUtils.EMPTY_STRING;
                case EMPTY:
                default:
                    return OptionUtils.EMPTY_BYTES;
            }
        }
        else
        {
            switch ( outType )
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
        }
    };

    /**
     * Mule test application.
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-config/entitytag/testflow1.xml";
    };

    /**
     * Test CoAP request
     * @throws Exception should not happen in this test
     */
    @Test
    public void testConRequest() throws Exception
    {
        CoreEvent result= flowRunner( "testflow" ).withPayload( inPayload ).withVariable( "in", inPayloadType.name() ).withVariable( "out", outPayloadType.name() ).run();
        Message response= result.getMessage();

        assertNotNull( "no mule event", response );
        Object responsePayload= TypedValue.unwrap( response.getPayload() );
        assertEquals( "wrong response payload class", outPayloadType.getClazz(), responsePayload.getClass() );
        if ( outPayloadType == DataType.BINARY )
        {
            assertArrayEquals( (byte[]) outPayload, (byte[]) responsePayload );
        }
        else
        {
            assertEquals( "wrong response payload", outPayload, responsePayload );
        }
    }
}
