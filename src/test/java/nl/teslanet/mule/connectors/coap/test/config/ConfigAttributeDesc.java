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
package nl.teslanet.mule.connectors.coap.test.config;


/**
 * Description of a configuration parameter to test
 *
 */
public class ConfigAttributeDesc
{

    /**
     * Name of the attribute
     */
    private ConfigParamName paramName;

    /**
     * Californium NetworkConfig key of the attribute
     */
    private String networkConfigKey;

    /**
     * Expected default value of the attribute 
     */
    private String expectedDefaultValue;

    /**
     * Expected default Californium NetworkConfig value
     */
    private String expectedDefaultNetworkValue;

    /**
     * Custom value to test
     */
    private String customValue;

    /**
     *  Expected Custom Californium NetworkConfig value
     */
    private String expectedCustomNetworkValue;

    /**
     * @param paramName
     * @param defaultValue
     * @param customValue
     */
    /**
     * Constructor
     * @param paramName Name of the parameter to test.
     * @param networkConfigName Name of the network configuration.
     * @param expectedDefaultValue The default value to expect.
     * @param expectedDefaultNetworkValue The default network value to expect.
     * @param customValue The custom value to use in the test.
     * @param expectedCustomNetworkValue The custom network value to expect.
     */
    public ConfigAttributeDesc(
        ConfigParamName paramName,
        String networkConfigName,
        String expectedDefaultValue,
        String expectedDefaultNetworkValue,
        String customValue,
        String expectedCustomNetworkValue )
    {
        this.paramName= paramName;
        this.networkConfigKey= networkConfigName;
        this.expectedDefaultValue= expectedDefaultValue;
        this.expectedDefaultNetworkValue= expectedDefaultNetworkValue;
        this.customValue= customValue;
        this.expectedCustomNetworkValue= expectedCustomNetworkValue;
    }

    /**
     * @return the paramName
     */
    public ConfigParamName getConfigParamName()
    {
        return paramName;
    }

    /**
     * @param paramName the paramName to set
     */
    public void setConfigParamName( ConfigParamName paramName )
    {
        this.paramName= paramName;
    }

    /**
     * @return the networkConfig key
     */
    public String getNetworkConfigKey()
    {
        return networkConfigKey;
    }

    /**
     * @param propertyNetworkKey The networkConfig key to set
     */
    public void setNetworkConfigKey( String propertyNetworkKey )
    {
        this.networkConfigKey= propertyNetworkKey;
    }

    /**
     * @return the expectedDefaultValue
     */
    public String getExpectedDefaultValue()
    {
        return expectedDefaultValue;
    }

    /**
     * @param expectedDefaultValue The expectedDefaultValue to set.
     */
    public void setExpectedDefaultValue( String expectedDefaultValue )
    {
        this.expectedDefaultValue= expectedDefaultValue;
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
     * @param expectedDefaultNetworkValue the expectedDefaultNetworkValue to set
     */
    public void setExpectedDefaultNetworkValue( String expectedDefaultNetworkValue )
    {
        this.expectedDefaultNetworkValue= expectedDefaultNetworkValue;
    }

    /**
     * @return the customValue
     */
    public String getCustomValue()
    {
        return customValue;
    }

    /**
     * @param customValue the customValue to set
     */
    public void setCustomValue( String customValue )
    {
        this.customValue= customValue;
    }

    /**
     * @param expectedCustomNetworkValue the expectedCustomNetworkValue to set
     */
    public void setExpectedCustomNetworkValue( String expectedCustomNetworkValue )
    {
        this.expectedCustomNetworkValue= expectedCustomNetworkValue;
    }
}
