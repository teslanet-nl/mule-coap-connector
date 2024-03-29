/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2022 (teslanet.nl) Rogier Cobben
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

import nl.teslanet.mule.connectors.coap.api.config.endpoint.UDPEndpoint;


@RunWith(Parameterized.class)
public class UDPEndpointConfigTest
{
    /**
     * @return List of attributes to test.
     */
    @Parameters(name= "attributeName = {0}")
    public static ConfigParamName[] propertiesToTest()
    {
        return ConfigParamName.values();
    }

    /**
     * The attribute to test.
     */
    @Parameter
    public ConfigParamName attributeName;

    /**
     * Test getter and setter.
     * @throws Exception
     */
    @Test
    public void testGetterAndSetter() throws Exception
    {
        if ( doSkip( attributeName ) ) return;

        UDPEndpoint config= new UDPEndpoint( "testEndpoint" );
        assertNotNull( "constructor deliverd null", config );

        String input;
        String output;

        input= ConfigAttributes.getCustomValue( attributeName );
        ConfigAttributes.setValue( attributeName, config, input );
        output= ConfigAttributes.getValue( attributeName, config );
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

        UDPEndpoint config= new UDPEndpoint( "testEndpoint" );
        assertNotNull( "constructor deliverd null", config );

        String defaultValue;
        String output;

        defaultValue= ConfigAttributes.getExpectedDefaultValue( attributeName );
        output= ConfigAttributes.getValue( attributeName, config );
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

        UDPEndpoint config= new UDPEndpoint( "testEndpoint" );
        assertNotNull( "constructor deliverd null", config );

        if ( ConfigAttributes.isNetworkConfig( attributeName ) )
        {
            String input;
            String output;

            input= ConfigAttributes.getExpectedDefaultNetworkValue( attributeName );
            output= ConfigAttributes.getNetworkConfigValue( attributeName, config );
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

        UDPEndpoint config= new UDPEndpoint( "testEndpoint" );
        assertNotNull( "constructor deliverd null", config );

        if ( ConfigAttributes.isNetworkConfig( attributeName ) )
        {
            String input;
            String output;

            input= ConfigAttributes.getCustomValue( attributeName );
            ConfigAttributes.setValue( attributeName, config, input );
            String expected= ConfigAttributes.getExpectedCustomNetworkValue( attributeName );
            output= ConfigAttributes.getNetworkConfigValue( attributeName, config );
            assertEquals( "got wrong network value", expected, output );
        }

    }

    /**
     * Establish whether the parameter is irrelevant and should not be tested
     * @param paramName the parameter name
     * @return true when test should be skipped
     */
    private boolean doSkip( ConfigParamName paramName )
    {
        switch ( paramName )
        {
            case protocolStageThreadCount:

            case bindToPort:
            case bindToSecurePort:
            case dtlsAutoResumeTimeout:
            case keyStoreLocation:
            case keyStorePassword:
            case privateKeyAlias:
            case privateKeyPassword:
            case responseMatching:
            case secureSessionTimeout:
            case trustStoreLocation:
            case trustStorePassword:
            case trustedRootCertificateAlias:

            case udpConnectorOutCapacity:
            case leisure:
            case maxTransmitWait:
            case probingRate:

            case multicastGroups:
                return true;
            default:
                return false;
        }
    }

}
