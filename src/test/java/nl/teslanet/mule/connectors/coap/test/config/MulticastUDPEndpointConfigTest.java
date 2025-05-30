/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2025 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.test.config;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import nl.teslanet.mule.connectors.coap.api.config.endpoint.MulticastUDPEndpoint;


@RunWith( Parameterized.class )
public class MulticastUDPEndpointConfigTest
{
    /**
     * @return List of attributes to test.
     */
    @Parameters( name= "attributeName = {0}" )
    public static ConfigParam[] propertiesToTest()
    {
        return ConfigParam.values();
    }

    /**
     * The attribute to test.
     */
    @Parameter
    public ConfigParam attributeName;

    /**
     * Test getter and setter.
     * @throws Exception
     */
    @Test
    public void testGetterAndSetter() throws Exception
    {
        if ( doSkip( attributeName ) ) return;

        MulticastUDPEndpoint config= new MulticastUDPEndpoint( "testEndpoint" );
        assertNotNull( "constructor deliverd null", config );

        String input;
        String output;

        input= ConfigParams.getCustomValue( attributeName );
        ConfigParams.setValue( attributeName, config, input );
        output= ConfigParams.getValue( attributeName, config );
        assertEquals( "get/set failed", input, output );
    }

    /**
     * Test the default value
     * @throws Exception
     */
    @Test
    public void testDefaults() throws Exception
    {
        if ( doSkip( attributeName ) ) return;

        MulticastUDPEndpoint config= new MulticastUDPEndpoint( "testEndpoint" );
        assertNotNull( "constructor deliverd null", config );

        String defaultValue;
        String output;

        defaultValue= ConfigParams.getExpectedDefaultValue( attributeName );
        output= ConfigParams.getValue( attributeName, config );
        assertEquals( "got wrong default value", defaultValue, output );
    }

    /**
     * Test the networkconfig value when a custom value is set on the attribute
     * @throws Exception
     */
    @Test
    public void testDefaultNetworkValues() throws Exception
    {
        if ( doSkip( attributeName ) ) return;

        MulticastUDPEndpoint config= new MulticastUDPEndpoint( "testEndpoint" );
        assertNotNull( "constructor deliverd null", config );

        if ( ConfigParams.isNetworkConfig( attributeName ) )
        {
            String input;
            String output;

            input= ConfigParams.getExpectedDefaultNetworkValue( attributeName );
            output= ConfigParams.getNetworkConfigValue( attributeName, config );
            assertEquals( "got wrong default network value", input, output );
        }
    }

    /**
     * Test a custom value set on the attribute
     * @throws Exception 
     */
    @Test
    public void testCustomNetworkValues() throws Exception
    {
        if ( doSkip( attributeName ) ) return;

        MulticastUDPEndpoint config= new MulticastUDPEndpoint( "testEndpoint" );
        assertNotNull( "constructor deliverd null", config );

        if ( ConfigParams.isNetworkConfig( attributeName ) )
        {
            String input;
            String output;

            input= ConfigParams.getCustomValue( attributeName );
            ConfigParams.setValue( attributeName, config, input );
            String expected= ConfigParams.getExpectedCustomNetworkValue( attributeName );
            output= ConfigParams.getNetworkConfigValue( attributeName, config );
            assertEquals( "got wrong network value", expected, output );
        }

    }

    /**
     * Establish whether the parameter is irrelevant and should not be tested
     * @param param the parameter name
     * @return true when test should be skipped
     * @throws Exception 
     */
    private boolean doSkip( ConfigParam param ) throws Exception
    {
        return !ConfigParams.getConfigDescription( param ).isRelevant( ConfigDescription.TEST_MULTICAST );
    }

}
