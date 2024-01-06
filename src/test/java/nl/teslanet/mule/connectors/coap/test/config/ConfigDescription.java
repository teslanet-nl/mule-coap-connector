/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2023 (teslanet.nl) Rogier Cobben
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


/**
 * Description of a configuration parameter to test
 *
 */
public class ConfigDescription
{
    /**
     * Flag indicating no relevance for any test case.
     */
    public static final int TEST_NONE= 0x0;

    /**
     * Flag indicating relevance for UDP test case.
     */
    public static final int TEST_UDP= 0x1;

    /**
     * Flag indicating relevance for MULTICAST test case.
     */
    public static final int TEST_MULTICAST= 0x2;

    /**
     * Flag indicating relevance for DTLS test case.
     */
    public static final int TEST_DTLS= 0x4;

    /**
     * Flag indicating relevance for DTLS Cluster test case.
     */
    public static final int TEST_DTLSC= 0x8;

    /**
     * Flag indicating relevance future DTLS test case.
     */
    public static final int TEST_DTLSX= 0x10;

    /**
     * Flag indicating relevance for TCP test case.
     */
    public static final int TEST_TCP= 0x20;

    /**
     * Flag indicating relevance for ALL test cases.
     */
    public static final int TEST_ALL= TEST_UDP | TEST_MULTICAST | TEST_DTLS | TEST_DTLSC | TEST_DTLSX | TEST_TCP;

    /**
     * Flags indicating relevant test-cases.
     */
    public final int testCases;

    /**
     * Name of the attribute
     */
    private final ConfigParam param;

    /**
     * Expected default value of the attribute 
     */
    private final String expectedDefaultValue;

    /**
     * Expected default Californium Configuration value
     */
    private final String expectedDefaultNetworkValue;

    /**
     * Custom value to test
     */
    private final String customValue;

    /**
     *  Expected Custom Californium Configuration value
     */
    private final String expectedCustomNetworkValue;

    /**
     * Constructor
     * @param param Name of the parameter to test.
     * @param expectedDefaultValue The default value to expect.
     * @param expectedDefaultNetworkValue The default network value to expect.
     * @param customValue The custom value to use in the test.
     * @param expectedCustomNetworkValue The custom network value to expect.
     * @param testCases Test cases that are relevant.
     */
    public ConfigDescription(
        ConfigParam param,
        String expectedDefaultValue,
        String expectedDefaultNetworkValue,
        String customValue,
        String expectedCustomNetworkValue,
        int testCases
    )
    {
        this.testCases= testCases;
        this.param= param;
        this.expectedDefaultValue= expectedDefaultValue;
        this.expectedDefaultNetworkValue= expectedDefaultNetworkValue;
        this.customValue= customValue;
        this.expectedCustomNetworkValue= expectedCustomNetworkValue;
    }

    /**
     * Constructor
     * @param param Name of the parameter to test.
     * @param networkConfigName Name of the network configuration.
     * @param expectedDefaultValue The default value to expect.
     * @param expectedDefaultNetworkValue The default network value to expect.
     * @param customValue The custom value to use in the test.
     * @param expectedCustomNetworkValue The custom network value to expect.
     */
    public ConfigDescription( ConfigParam param, String expectedDefaultValue, String expectedDefaultNetworkValue, String customValue, String expectedCustomNetworkValue )
    {
        this( param, expectedDefaultValue, expectedDefaultNetworkValue, customValue, expectedCustomNetworkValue, TEST_ALL );
    }

    public boolean isRelevant( int testCases )
    {
        return ( this.testCases & testCases ) > 0;
    }

    /**
     * @return the param
     */
    public ConfigParam getConfigparam()
    {
        return param;
    }

    /**
     * @return the expectedDefaultValue
     */
    public String getExpectedDefaultValue()
    {
        return expectedDefaultValue;
    }

    /**
     * @return the expectedDefaultNetworkValue
     */
    public String getExpectedDefaultNetworkValue()
    {
        return expectedDefaultNetworkValue;
    }

    /**
     * @return the expectedCustomNetworkValue
     */
    public String getExpectedCustomNetworkValue()
    {
        return expectedCustomNetworkValue;
    }

    /**
     * @return the customValue
     */
    public String getCustomValue()
    {
        return customValue;
    }
}
