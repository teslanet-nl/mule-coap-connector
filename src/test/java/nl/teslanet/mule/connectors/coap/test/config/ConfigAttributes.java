/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2020 (teslanet.nl) Rogier Cobben
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


import org.eclipse.californium.core.network.config.NetworkConfig;

import nl.teslanet.mule.connectors.coap.api.config.VisitableConfig;
import nl.teslanet.mule.connectors.coap.internal.config.CfNetworkConfigVisitor;


/**
 * The Configuration attributes to test
 *
 */
public class ConfigAttributes
{
    /**
     * Get actual value of the attribute
     * @param paramName Name of the parameter.
     * @param config The configuration containe the parameter.
     * @return The parameter value.
     * @throws Exception When an error occurs.
     */
    static public String getValue( ConfigParamName paramName, VisitableConfig config ) throws Exception
    {
        GetValueVisitor visitor= new GetValueVisitor( paramName );
        config.accept( visitor );
        return visitor.getValue();
    }

    /**
     * @return the NetworkConfig key when attribute is a NetworkConfig attribute,
     *         otherwise null is returned
     * @throws Exception
     *             when attribute is invalid
     */
    static public String getKey( ConfigParamName paramName ) throws Exception
    {
        return getAttributeDesc( paramName ).getNetworkConfigKey();
    }

    /**
     * @return the attribute name corresponding to NetworkConfig key
     * @throws Exception
     *             when key is not known
     */
    static public ConfigParamName getName( String key ) throws Exception
    {
        for ( ConfigParamName paramName : ConfigParamName.values() )
        {
            ConfigAttributeDesc paramDesc= getAttributeDesc( paramName );
            if ( paramDesc.getNetworkConfigKey() != null && paramDesc.getNetworkConfigKey().equals( key ) )
            {
                return paramName;
            }
        }
        return null;
    }

    /**
     * Get the test value from parameter description
     * @param paramName the name of the parameter
     * @return the value to use in the test
     * @throws Exception when parameter name is not valid
     */
    public static String getCustomValue( ConfigParamName paramName ) throws Exception
    {
        return getAttributeDesc( paramName ).getCustomValue();
    }

    /**
     * Get the expected test value from parameter description
     * @param paramName the name of the parameter
     * @return the expected value to use in the test
     * @throws Exception when parameter name is not valid
     */
    public static String getExpectedCustomNetworkValue( ConfigParamName paramName ) throws Exception
    {
        return getAttributeDesc( paramName ).getExpectedCustomNetworkValue();
    }

    /**
     * Get the expected default network value from parameter description
     * @param paramName the name of the parameter
     * @return the expected default network value to use in the test
     * @throws Exception when parameter name is not valid
     */
    public static String getExpectedDefaultNetworkValue( ConfigParamName paramName ) throws Exception
    {
        return getAttributeDesc( paramName ).getExpectedDefaultNetworkValue();
    }

    /**
     * Get the expected default value from parameter description
     * @param paramName the name of the parameter
     * @return the expected default value to use in the test
     * @throws Exception when parameter name is not valid
     */
    public static String getExpectedDefaultValue( ConfigParamName paramName ) throws Exception
    {
        return getAttributeDesc( paramName ).getExpectedDefaultValue();
    }

    /**
     * @param config 
     * 
     * @param ConfigParamName
     *            the attribute name
     * @throws Exception
     *             when invalid name
     */
    /**
     * Establish whether the attribute is a NetworkConfig attribute
     * @param paramName The name of the parameter.
     * @return True when parameter is network configuration.
     * @throws Exception
     */
    static public boolean isNetworkConfig( ConfigParamName paramName ) throws Exception
    {
        return( getKey( paramName ) != null );
    }

    /**
     * Get the value of a NetworkConfig attribute
     * 
     * @return the actual value when it is a NetworkConfig attribute, otherwise null
     * @throws Exception
     *             when name is unknown
     */
    static public String getNetworkConfigValue( ConfigParamName paramName, VisitableConfig config ) throws Exception
    {
        String key= getKey( paramName );
        String result= null;

        if ( key != null )
        {
            CfNetworkConfigVisitor visitor= new CfNetworkConfigVisitor();
            config.accept( visitor );
            result= visitor.getNetworkConfig().getString( key );
        }
        return result;
    }

    /**
     * Set value for the attribute
     * 
     * @param config
     *            configuration to set the attribute on
     * @param value
     *            to set
     * @throws Exception
     *             when attribute is invalid
     */
    static public void setValue( ConfigParamName paramName, VisitableConfig config, String value ) throws Exception
    {
        SetValueVisitor visitor= new SetValueVisitor( paramName, value );
        config.accept( visitor );
    }

    /**
     * Get the test description of the attribute.
     * @param name of the attribute
     * @return description of the attribute test
     * @throws Exception when the name is unknown
     */
    private static ConfigAttributeDesc getAttributeDesc( ConfigParamName paramName ) throws Exception
    {
        //set values for attributes: paramName, networkConfigName, expectedDefaultValue, expectedDefaultNetworkValue, customValue, expectedCustomNetworkValue
        switch ( paramName )
        {
            case logCoapMessages:
                return new ConfigAttributeDesc( ConfigParamName.logCoapMessages, null, "false", "false", "true", null );
            //from EndpointConfig
            case bindToHost:
                return new ConfigAttributeDesc( ConfigParamName.bindToHost, null, null, null, "somehost.org", "somehost.org" );
            case bindToPort:
                return new ConfigAttributeDesc( ConfigParamName.bindToPort, NetworkConfig.Keys.COAP_PORT, null, "5683", "9983", "9983" );
            case bindToSecurePort:
                return new ConfigAttributeDesc( ConfigParamName.bindToSecurePort, NetworkConfig.Keys.COAP_SECURE_PORT, null, "5684", "9984", "9984" );
            case maxActivePeers:
                return new ConfigAttributeDesc( ConfigParamName.maxActivePeers, NetworkConfig.Keys.MAX_ACTIVE_PEERS, null, "150000", "25", "25" );
            case maxPeerInactivityPeriod:
                return new ConfigAttributeDesc( ConfigParamName.maxPeerInactivityPeriod, NetworkConfig.Keys.MAX_PEER_INACTIVITY_PERIOD, null, "600", "333", "333" );
            case keyStoreLocation:
                return new ConfigAttributeDesc( ConfigParamName.keyStoreLocation, null, null, null, "/tmp/test1", null );
            case keyStorePassword:
                return new ConfigAttributeDesc( ConfigParamName.keyStorePassword, null, null, null, "secret1", null );
            case trustStoreLocation:
                return new ConfigAttributeDesc( ConfigParamName.trustStoreLocation, null, null, null, "/tmp/test2", null );
            case trustStorePassword:
                return new ConfigAttributeDesc( ConfigParamName.trustStorePassword, null, null, null, "secret1", null );
            case privateKeyAlias:
                return new ConfigAttributeDesc( ConfigParamName.privateKeyAlias, null, null, null, "secretKey", null );
            case privateKeyPassword:
                return new ConfigAttributeDesc( ConfigParamName.privateKeyPassword, null, null, null, "secret_keypassword", null );
            case trustedRootCertificateAlias:
                return new ConfigAttributeDesc( ConfigParamName.trustedRootCertificateAlias, null, null, null, "certificate2", null );
            case ackTimeout:
                return new ConfigAttributeDesc( ConfigParamName.ackTimeout, NetworkConfig.Keys.ACK_TIMEOUT, null, "2000", "22000", "22000" );
            case ackRandomFactor:
                return new ConfigAttributeDesc( ConfigParamName.ackRandomFactor, NetworkConfig.Keys.ACK_RANDOM_FACTOR, null, "1.5", "3.56", "3.56" );
            case ackTimeoutScale:
                return new ConfigAttributeDesc( ConfigParamName.ackTimeoutScale, NetworkConfig.Keys.ACK_TIMEOUT_SCALE, null, "2.0", "7.364", "7.364" );
            case maxRetransmit:
                return new ConfigAttributeDesc( ConfigParamName.maxRetransmit, NetworkConfig.Keys.MAX_RETRANSMIT, null, "4", "44", "44" );
            case exchangeLifetime:
                return new ConfigAttributeDesc( ConfigParamName.exchangeLifetime, NetworkConfig.Keys.EXCHANGE_LIFETIME, null, "247000", "3000000", "3000000" );
            case nonLifetime:
                return new ConfigAttributeDesc( ConfigParamName.nonLifetime, NetworkConfig.Keys.NON_LIFETIME, null, "145000", "755000", "755000" );
            case maxTransmitWait:
                return new ConfigAttributeDesc( ConfigParamName.maxTransmitWait, NetworkConfig.Keys.MAX_TRANSMIT_WAIT, null, "93000", "158000", "158000" );
            case nstart:
                return new ConfigAttributeDesc( ConfigParamName.nstart, NetworkConfig.Keys.NSTART, null, "1", "145", "145" );
            case leisure:
                return new ConfigAttributeDesc( ConfigParamName.leisure, NetworkConfig.Keys.LEISURE, null, "5000", "9000", "9000" );
            case probingRate:
                return new ConfigAttributeDesc( ConfigParamName.probingRate, NetworkConfig.Keys.PROBING_RATE, null, "1.0", "3.15", "3.15" );
            case useRandomMidStart:
                return new ConfigAttributeDesc( ConfigParamName.useRandomMidStart, NetworkConfig.Keys.USE_RANDOM_MID_START, null, "true", "false", "false" );
            case midTracker:
                return new ConfigAttributeDesc( ConfigParamName.midTracker, NetworkConfig.Keys.MID_TRACKER, "GroupedMidTracker", "GROUPED", "MapBasedMidTracker", "MAPBASED" );
            case midTrackerGroups:
                return new ConfigAttributeDesc( ConfigParamName.midTrackerGroups, NetworkConfig.Keys.MID_TRACKER_GROUPS, null, "16", "27", "27" );
            case tokenSizeLimit:
                return new ConfigAttributeDesc( ConfigParamName.tokenSizeLimit, NetworkConfig.Keys.TOKEN_SIZE_LIMIT, null, "8", "15", "15" );
            case preferredBlockSize:
                return new ConfigAttributeDesc( ConfigParamName.preferredBlockSize, NetworkConfig.Keys.PREFERRED_BLOCK_SIZE, null, "512", "1024", "1024" );
            case maxMessageSize:
                return new ConfigAttributeDesc( ConfigParamName.maxMessageSize, NetworkConfig.Keys.MAX_MESSAGE_SIZE, null, "1024", "4156", "4156" );
            case maxResourceBodySize:
                return new ConfigAttributeDesc( ConfigParamName.maxResourceBodySize, NetworkConfig.Keys.MAX_RESOURCE_BODY_SIZE, null, "8192", "16000", "16000" );
            case blockwiseStatusLifetime:
                return new ConfigAttributeDesc( ConfigParamName.blockwiseStatusLifetime, NetworkConfig.Keys.BLOCKWISE_STATUS_LIFETIME, null, "300000", "150000", "150000" );
            case notificationCheckIntervalTime:
                return new ConfigAttributeDesc(
                    ConfigParamName.notificationCheckIntervalTime,
                    NetworkConfig.Keys.NOTIFICATION_CHECK_INTERVAL_TIME,
                    null,
                    "86400000",
                    "91100001",
                    "91100001" );
            case notificationCheckIntervalCount:
                return new ConfigAttributeDesc( ConfigParamName.notificationCheckIntervalCount, NetworkConfig.Keys.NOTIFICATION_CHECK_INTERVAL_COUNT, null, "100", "95", "95" );
            case notificationReregistrationBackoff:
                return new ConfigAttributeDesc(
                    ConfigParamName.notificationReregistrationBackoff,
                    NetworkConfig.Keys.NOTIFICATION_REREGISTRATION_BACKOFF,
                    null,
                    "2000",
                    "5002",
                    "5002" );
            case useCongestionControl:
                return new ConfigAttributeDesc( ConfigParamName.useCongestionControl, NetworkConfig.Keys.USE_CONGESTION_CONTROL, "false", "false", "true", "true" );
            case congestionControlAlgorithm:
                return new ConfigAttributeDesc(
                    ConfigParamName.congestionControlAlgorithm,
                    NetworkConfig.Keys.CONGESTION_CONTROL_ALGORITHM,
                    null,
                    "Cocoa",
                    "PeakhopperRto",
                    "PeakhopperRto" );
            case protocolStageThreadCount:
                return new ConfigAttributeDesc(
                    ConfigParamName.protocolStageThreadCount,
                    NetworkConfig.Keys.PROTOCOL_STAGE_THREAD_COUNT,
                    null,
                    Integer.toString( Runtime.getRuntime().availableProcessors() ),
                    "12",
                    "12" );
            case networkStageReceiverThreadCount:
                return new ConfigAttributeDesc( ConfigParamName.networkStageReceiverThreadCount, NetworkConfig.Keys.NETWORK_STAGE_RECEIVER_THREAD_COUNT, null, "1", "12", "12" );
            case networkStageSenderThreadCount:
                return new ConfigAttributeDesc( ConfigParamName.networkStageSenderThreadCount, NetworkConfig.Keys.NETWORK_STAGE_SENDER_THREAD_COUNT, null, "1", "18", "18" );
            case udpConnectorDatagramSize:
                return new ConfigAttributeDesc( ConfigParamName.udpConnectorDatagramSize, NetworkConfig.Keys.UDP_CONNECTOR_DATAGRAM_SIZE, null, "2048", "4096", "4096" );
            case udpConnectorReceiveBuffer:
                return new ConfigAttributeDesc( ConfigParamName.udpConnectorReceiveBuffer, NetworkConfig.Keys.UDP_CONNECTOR_RECEIVE_BUFFER, null, "0", "1000", "1000" );
            case udpConnectorSendBuffer:
                return new ConfigAttributeDesc( ConfigParamName.udpConnectorSendBuffer, NetworkConfig.Keys.UDP_CONNECTOR_SEND_BUFFER, null, "0", "500", "500" );
            case udpConnectorOutCapacity:
                return new ConfigAttributeDesc(
                    ConfigParamName.udpConnectorOutCapacity,
                    NetworkConfig.Keys.UDP_CONNECTOR_OUT_CAPACITY,
                    null,
                    "2147483647",
                    "1007483647",
                    "1007483647" );
            case deduplicator:
                return new ConfigAttributeDesc(
                    ConfigParamName.deduplicator,
                    NetworkConfig.Keys.DEDUPLICATOR,
                    null,
                    NetworkConfig.Keys.DEDUPLICATOR_MARK_AND_SWEEP,
                    "CropRotation",
                    NetworkConfig.Keys.DEDUPLICATOR_CROP_ROTATION );
            case responseMatching:
                return new ConfigAttributeDesc( ConfigParamName.responseMatching, NetworkConfig.Keys.RESPONSE_MATCHING, null, "STRICT", "RELAXED", "RELAXED" );
            case markAndSweepInterval:
                return new ConfigAttributeDesc( ConfigParamName.markAndSweepInterval, NetworkConfig.Keys.MARK_AND_SWEEP_INTERVAL, null, "10000", "22000", "22000" );
            case cropRotationPeriod:
                return new ConfigAttributeDesc( ConfigParamName.cropRotationPeriod, NetworkConfig.Keys.CROP_ROTATION_PERIOD, null, "247000", "78000", "78000" );
            case logHealthStatus:
                return new ConfigAttributeDesc( ConfigParamName.logHealthStatus, null, "false", null, "true", null );
            case secureSessionTimeout:
                return new ConfigAttributeDesc( ConfigParamName.secureSessionTimeout, NetworkConfig.Keys.SECURE_SESSION_TIMEOUT, null, "86400", "15689", "15689" );
            case dtlsAutoResumeTimeout:
                return new ConfigAttributeDesc( ConfigParamName.dtlsAutoResumeTimeout, NetworkConfig.Keys.DTLS_AUTO_RESUME_TIMEOUT, null, "30000", "15123", "15123" );
            case healthStatusInterval:
                return new ConfigAttributeDesc( ConfigParamName.healthStatusInterval, NetworkConfig.Keys.HEALTH_STATUS_INTERVAL, null, "0", "100", "100" );
            case multicastGroups:
                return new ConfigAttributeDesc( ConfigParamName.multicastGroups, null, null, null, "[224.0.1.187, test|eth0]", "[224.0.1.187, test|eth0]" );
            default:
                throw new Exception( "cannot create AttributeDesc: name unknown" );
        }
    }

}
