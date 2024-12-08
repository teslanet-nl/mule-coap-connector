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
package nl.teslanet.mule.connectors.coap.test.config;


import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.elements.config.Configuration;

import nl.teslanet.mule.connectors.coap.api.config.VisitableConfig;
import nl.teslanet.mule.connectors.coap.internal.config.ConfigurationVisitor;


/**
 * The Configuration parameters to test
 *
 */
public class ConfigParams
{
    /**
     * Get actual value of the parameter
     * @param param Name of the parameter.
     * @param config The configuration containe the parameter.
     * @return The parameter value.
     * @throws Exception When an error occurs.
     */
    public static String getValue( ConfigParam param, VisitableConfig config ) throws Exception
    {
        GetValueVisitor visitor= new GetValueVisitor( param );
        config.accept( visitor );
        return visitor.getValue();
    }

    /**
     * Get the test value from parameter description
     * @param param the name of the parameter
     * @return the value to use in the test
     * @throws Exception when parameter name is not valid
     */
    public static String getCustomValue( ConfigParam param ) throws Exception
    {
        return getConfigDescription( param ).getCustomValue();
    }

    /**
     * Get the expected test value from parameter description
     * @param param the name of the parameter
     * @return the expected value to use in the test
     * @throws Exception when parameter name is not valid
     */
    public static String getExpectedCustomNetworkValue( ConfigParam param ) throws Exception
    {
        return getConfigDescription( param ).getExpectedCustomNetworkValue();
    }

    /**
     * Get the expected default network value from parameter description
     * @param param the name of the parameter
     * @return the expected default network value to use in the test
     * @throws Exception when parameter name is not valid
     */
    public static String getExpectedDefaultNetworkValue( ConfigParam param ) throws Exception
    {
        return getConfigDescription( param ).getExpectedDefaultNetworkValue();
    }

    /**
     * Get the expected default value from parameter description
     * @param param the name of the parameter
     * @return the expected default value to use in the test
     * @throws Exception when parameter name is not valid
     */
    public static String getExpectedDefaultValue( ConfigParam param ) throws Exception
    {
        return getConfigDescription( param ).getExpectedDefaultValue();
    }

    /**
     * @param config 
     * 
     * @param Configparam
     *            the parameter name
     * @throws Exception
     *             when invalid name
     */
    /**
     * Establish whether the parameter is a Configuration parameter
     * @param param The name of the parameter.
     * @return True when parameter is network configuration.
     * @throws Exception
     */
    static public boolean isNetworkConfig( ConfigParam param ) throws Exception
    {
        return( param.getCfConfigDef() != null );
    }

    /**
     * Get the value of a Configuration parameter
     * 
     * @return the actual value when it is a Configuration parameter, otherwise null
     * @throws Exception
     *             when name is unknown
     */
    static public String getNetworkConfigValue( ConfigParam param, VisitableConfig config ) throws Exception
    {
        String result= null;

        if ( param.getCfConfigDef() != null )
        {
            ConfigurationVisitor visitor= new ConfigurationVisitor();
            config.accept( visitor );
            Configuration configuration= visitor.getConfiguration();
            try
            {
                result= configuration.getAsText( param.getCfConfigDef() );
            }
            catch ( NullPointerException e )
            {
                return result;
            }
        }
        return result;
    }

    /**
     * Set value for the parameter
     * 
     * @param config
     *            configuration to set the parameter on
     * @param value
     *            to set
     * @throws Exception
     *             when parameter is invalid
     */
    static public void setValue( ConfigParam param, VisitableConfig config, String value ) throws Exception
    {
        SetValueVisitor visitor= new SetValueVisitor( param, value );
        config.accept( visitor );
    }

    /**
     * Get the test description of the parameter.
     * @param param ID of the configuration item.
     * @return description of the parameter test
     * @throws Exception when the name is unknown
     */
    static ConfigDescription getConfigDescription( ConfigParam param ) throws Exception
    {
        final int CORES= Runtime.getRuntime().availableProcessors();
        final int THREADS= CORES > 3 ? 2 : 1;
        //set values for parameters: param, expectedDefaultValue, expectedDefaultNetworkValue, customValue, expectedCustomNetworkValue
        switch ( param )
        {
            //from EndpointConfig
            case ENDPOINT_LOGTRAFFIC:
                return new ConfigDescription( param, "false", "false", "true", null, ConfigDescription.TEST_ALL );
            case ENDPOINT_BINDTOHOST:
                return new ConfigDescription( param, null, null, "somehost.org", "somehost.org", ConfigDescription.TEST_ALL );
            case ENDPOINT_BINDTOPORT:
                return new ConfigDescription( param, null, "5683", "9983", "9983", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case ENDPOINT_BINDTOSECUREPORT:
                return new ConfigDescription( param, null, "5683", "9983", "9983", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case ENDPOINT_REUSEADDRESS:
                return new ConfigDescription( param, "false", "false", "true", "true", ConfigDescription.TEST_ALL );
            case maxActivePeers:
                return new ConfigDescription( param, null, "150000", "25", "25", ConfigDescription.TEST_ALL );
            case maxPeerInactivityPeriod:
                return new ConfigDescription( param, null, "10[min]", "333s", "333[s]", ConfigDescription.TEST_ALL );
            case pskHost:
                return new ConfigDescription( param, null, null, "[host1,host2,host3]", null, ConfigDescription.TEST_DTLS );
            case pskPort:
                return new ConfigDescription( param, null, null, "[5984,5685,5686]", null, ConfigDescription.TEST_DTLS );
            case pskIdentity:
                return new ConfigDescription( param, null, null, "[pskId1,pskId2,pskId3]", null, ConfigDescription.TEST_DTLS );
            case pskKey:
                return new ConfigDescription( param, null, null, "[pskKey1,pskKey2,pskKey3]", null, ConfigDescription.TEST_DTLS );
            case pskKeyFileLocation:
                return new ConfigDescription( param, null, null, "/tmp/test1", null, ConfigDescription.TEST_DTLS );
            case pskKeyFilePassword:
                return new ConfigDescription( param, null, null, "secret1", null, ConfigDescription.TEST_DTLS );
            case keyStoreLocation:
                return new ConfigDescription( param, null, null, "/tmp/test1", null, ConfigDescription.TEST_DTLS );
            case keyStorePassword:
                return new ConfigDescription( param, null, null, "secret1", null, ConfigDescription.TEST_DTLS );
            case trustStoreLocation:
                return new ConfigDescription( param, null, null, "/tmp/test2", null, ConfigDescription.TEST_DTLS );
            case trustStorePassword:
                return new ConfigDescription( param, null, null, "secret1", null, ConfigDescription.TEST_DTLS );
            case privateKeyAlias:
                return new ConfigDescription( param, null, null, "secretKey", null, ConfigDescription.TEST_DTLS );
            case privateKeyPassword:
                return new ConfigDescription( param, null, null, "secret_keypassword", null, ConfigDescription.TEST_DTLS );
            case trustedRootCertificateAlias:
                return new ConfigDescription( param, null, null, "certificate2", null, ConfigDescription.TEST_DTLS );
            case ackTimeout:
                return new ConfigDescription( param, null, "2[s]", "22s", "22[s]", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case maxAckTimeout:
                return new ConfigDescription( param, null, "1[min]", "12s", "12[s]", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case ackRandomFactor:
                return new ConfigDescription( param, null, "1.5", "3.56", "3.56", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case ackTimeoutScale:
                return new ConfigDescription( param, null, "2.0", "7.364", "7.364", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case maxRetransmit:
                return new ConfigDescription( param, null, "4", "44", "44", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case exchangeLifetime:
                return new ConfigDescription( param, null, "247[s]", "50m", "50[min]", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case nonLifetime:
                return new ConfigDescription( param, null, "145[s]", "755s", "755[s]", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case maxLatency:
                return new ConfigDescription( param, null, "100[s]", "321s", "321[s]", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case maxServerResponseDelay:
                return new ConfigDescription( param, null, "250[s]", "555s", "555[s]", ConfigDescription.TEST_MULTICAST | ConfigDescription.TEST_DTLS );
            case maxTransmitWait:
                return new ConfigDescription( param, null, "93[s]", "158s", "158[s]", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case nstart:
                return new ConfigDescription( param, null, "1", "145", "145", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case leisure:
                return new ConfigDescription( param, null, "5[s]", "9s", "9[s]", ConfigDescription.TEST_MULTICAST );
            case probingRate:
                return new ConfigDescription( param, null, "1.0", "3.15", "3.15", ConfigDescription.TEST_NONE );
            case useRandomMidStart:
                return new ConfigDescription( param, "true", "true", "false", "false", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case midTracker:
                return new ConfigDescription( param, "GroupedMidTracker", "GROUPED", "MapBasedMidTracker", "MAPBASED", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case midTrackerGroups:
                return new ConfigDescription( param, null, "16", "27", "27", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case tokenSizeLimit:
                return new ConfigDescription( param, null, "8", "15", "15", ConfigDescription.TEST_ALL );
            case preferredBlockSize:
                return new ConfigDescription( param, null, "512", "1024", "1024", ConfigDescription.TEST_ALL );
            case maxMessageSize:
                return new ConfigDescription( param, null, "1024", "4156", "4156", ConfigDescription.TEST_ALL );
            case maxResourceBodySize:
                return new ConfigDescription( param, null, "8192", "16000", "16000", ConfigDescription.TEST_ALL );
            case blockwiseStatusLifetime:
                return new ConfigDescription( param, null, "5[min]", "150s", "150[s]", ConfigDescription.TEST_ALL );
            case blockwiseStatusInterval:
                return new ConfigDescription( param, null, "5[s]", "87s", "87[s]", ConfigDescription.TEST_ALL );
            case blockwiseStrictBlock1Option:
                return new ConfigDescription( param, "false", "false", "true", "true", ConfigDescription.TEST_ALL );
            case blockwiseStrictBlock2Option:
                return new ConfigDescription( param, "false", "false", "true", "true", ConfigDescription.TEST_ALL );
            case blockwiseEntityTooLargeAutoFailover:
                return new ConfigDescription( param, "true", "true", "false", "false", ConfigDescription.TEST_ALL );
            case NOTIFICATION_CHECK_INTERVAL_TIME:
                return new ConfigDescription( param, null, "2[min]", "91100001 ms", "91100001[ms]", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case NOTIFICATION_CHECK_INTERVAL_COUNT:
                return new ConfigDescription( param, null, "100", "95", "95", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case NOTIFICATION_REREGISTRATION_BACKOFF:
                return new ConfigDescription( param, null, "2[s]", "9s", "9[s]", ConfigDescription.TEST_ALL );
            case MAX_SERVER_OBSERVES:
                return new ConfigDescription( param, null, "50000", "11002", "11002", ConfigDescription.TEST_ALL );
            case congestionControlAlgorithm:
                return new ConfigDescription( param, null, "NULL", "PeakhopperRto", "PEAKHOPPER_RTO", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case UDP_RECEIVER_THREAD_COUNT:
                return new ConfigDescription( param, null, String.valueOf( THREADS ), "12", "12", ConfigDescription.TEST_UDP );
            case UDP_SENDER_THREAD_COUNT:
                return new ConfigDescription( param, null, String.valueOf( THREADS ), "18", "18", ConfigDescription.TEST_UDP );
            case UDP_DATAGRAM_SIZE:
                return new ConfigDescription( param, null, "2048", "4096", "4096", ConfigDescription.TEST_UDP );
            case UDP_RECEIVE_BUFFER_SIZE:
                return new ConfigDescription( param, null, null, "1000", "1000", ConfigDescription.TEST_UDP );
            case UDP_SEND_BUFFER_SIZE:
                return new ConfigDescription( param, null, null, "500", "500", ConfigDescription.TEST_UDP );
            case UDP_CONNECTOR_OUT_CAPACITY:
                return new ConfigDescription( param, null, "2147483647", "1007483647", "1007483647", ConfigDescription.TEST_UDP );
            case multicastMidBase:
                return new ConfigDescription( param, null, "65000", "55000", "55000", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case responseMatching:
                return new ConfigDescription( param, "STRICT", "STRICT", "RELAXED", "RELAXED", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case deduplicator:
                return new ConfigDescription(
                    param,
                    null,
                    CoapConfig.DEDUPLICATOR_MARK_AND_SWEEP,
                    "CropRotation",
                    CoapConfig.DEDUPLICATOR_CROP_ROTATION,
                    ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS
                );
            case deduplicationAutoReplace:
                return new ConfigDescription( param, null, "true", "false", "false", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case markAndSweepInterval:
                return new ConfigDescription( param, null, "10[s]", "22s", "22[s]", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case peersMarkAndSweepMessages:
                return new ConfigDescription( param, null, "64", "129", "129", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case cropRotationPeriod:
                return new ConfigDescription( param, null, "247[s]", "78s", "78[s]", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case logHealthStatus:
                return new ConfigDescription( param, "false", null, "true", null, ConfigDescription.TEST_ALL );
            case HEALTH_STATUS_INTERVAL:
                return new ConfigDescription( param, null, "0[ms]", "100s", "100[s]", ConfigDescription.TEST_ALL );
            case multicastGroups:
                return new ConfigDescription( param, "", "", "224.0.1.187;test,eth0;", "224.0.1.187;test,eth0;", ConfigDescription.TEST_MULTICAST );
            case ENDPOINT_OTHEROPTION_ALIAS:
                return new ConfigDescription( param, null, "[]", "[65001, 65002, 65003]", "[65001, 65002, 65003]", ConfigDescription.TEST_ALL );
            case ENDPOINT_OTHEROPTION_NUMBER:
                return new ConfigDescription( param, "[]", "[]", "[65001, 65002, 65003]", "[65001, 65002, 65003]", ConfigDescription.TEST_NONE );
            case ENDPOINT_OTHEROPTION_TYPE:
                return new ConfigDescription( param, "[]", "[]", "[OPAQUE, INTEGER, STRING]", "[OPAQUE, INTEGER, STRING]", ConfigDescription.TEST_NONE );
            case ENDPOINT_OTHEROPTION_SINGLEVALUE:
                return new ConfigDescription( param, "[]", "[]", "[true, false, true]", "[true, false, true]", ConfigDescription.TEST_NONE );
            case ENDPOINT_OTHEROPTION_MINBYTES:
                return new ConfigDescription( param, "[]", "[]", "[0, 2, 8]", "[0, 2, 8]", ConfigDescription.TEST_NONE );
            case ENDPOINT_OTHEROPTION_MAXBYTES:
                return new ConfigDescription( param, "[]", "[]", "[0, 2, 8]", "[0, 2, 8]", ConfigDescription.TEST_NONE );
            case strictEmptyMessageFormat:
                return new ConfigDescription( param, "true", "true", "false", "false", ConfigDescription.TEST_UDP | ConfigDescription.TEST_DTLS );
            case DTLS_AUTO_HANDSHAKE_TIMEOUT:
                return new ConfigDescription( param, null, null, "31s", "31[s]", ConfigDescription.TEST_DTLS );
            case DTLS_RETRANSMISSION_TIMEOUT:
                return new ConfigDescription( param, null, "2[s]", "3s", "3[s]", ConfigDescription.TEST_DTLS );
            case DTLS_MAX_RETRANSMISSION_TIMEOUT:
                return new ConfigDescription( param, null, "1[min]", "75s", "75[s]", ConfigDescription.TEST_DTLS );
            case DTLS_ADDITIONAL_ECC_TIMEOUT:
                return new ConfigDescription( param, null, "0[ms]", "153ms", "153[ms]", ConfigDescription.TEST_DTLS );
            case DTLS_MAX_RETRANSMISSIONS:
                return new ConfigDescription( param, null, "4", "7", "7", ConfigDescription.TEST_DTLS );
            case DTLS_RETRANSMISSION_INIT_RANDOM:
                return new ConfigDescription( param, null, "1.0", "1.3", "1.3", ConfigDescription.TEST_DTLS );
            case DTLS_RETRANSMISSION_TIMEOUT_SCALE:
                return new ConfigDescription( param, null, "2.0", "3.3", "3.3", ConfigDescription.TEST_DTLS );
            case DTLS_RETRANSMISSION_BACKOFF:
                return new ConfigDescription( param, null, null, "3", "3", ConfigDescription.TEST_DTLS );
            case DTLS_SUPPORT_CONNECTION_ID:
                return new ConfigDescription( param, null, null, "true", "0", ConfigDescription.TEST_DTLS );
            case DTLS_CONNECTION_ID_LENGTH:
                return new ConfigDescription( param, null, null, "5", "5", ConfigDescription.TEST_DTLS );
            case DTLS_CONNECTION_ID_NODE_ID:
                return new ConfigDescription( param, "true", "true", "false", "false", ConfigDescription.TEST_DTLSC );
            case DTLS_SERVER_USE_SESSION_ID:
                return new ConfigDescription( param, "true", "true", "false", "false", ConfigDescription.TEST_DTLS );
            case DTLS_USE_EARLY_STOP_RETRANSMISSION:
                return new ConfigDescription( param, "true", "true", "false", "false", ConfigDescription.TEST_DTLS );
            case DTLS_RECORD_SIZE_LIMIT:
                return new ConfigDescription( param, null, null, "128", "128", ConfigDescription.TEST_DTLS );
            case DTLS_MAX_FRAGMENT_LENGTH:
                return new ConfigDescription( param, null, null, "BYTES_2048", "BYTES_2048", ConfigDescription.TEST_DTLS );
            case DTLS_MAX_FRAGMENTED_HANDSHAKE_MESSAGE_LENGTH:
                return new ConfigDescription( param, "8192", "8192", "6036", "6036", ConfigDescription.TEST_DTLS );
            case DTLS_USE_MULTI_RECORD_MESSAGES:
                return new ConfigDescription( param, "UNDEFINED", null, "YES", "true", ConfigDescription.TEST_DTLS );
            case DTLS_USE_MULTI_HANDSHAKE_MESSAGE_RECORDS:
                return new ConfigDescription( param, "UNDEFINED", null, "YES", "true", ConfigDescription.TEST_DTLS );
            case DTLS_CLIENT_AUTHENTICATION_MODE:
                return new ConfigDescription( param, null, "NEEDED", "OPTIONAL", "WANTED", ConfigDescription.TEST_DTLS );
            case DTLS_VERIFY_SERVER_CERTIFICATES_SUBJECT:
                return new ConfigDescription( param, "true", "true", "false", "false", ConfigDescription.TEST_DTLS );
            case DTLS_ROLE:
                return new ConfigDescription( param, "BOTH", "BOTH", "CLIENT_ONLY", "CLIENT_ONLY", ConfigDescription.TEST_DTLS );
            case DTLS_MAX_TRANSMISSION_UNIT:
                return new ConfigDescription( param, null, null, "2100", "2100", ConfigDescription.TEST_DTLS );
            case DTLS_MAX_TRANSMISSION_UNIT_LIMIT:
                return new ConfigDescription( param, "1500", "1500", "2100", "2100", ConfigDescription.TEST_DTLS );
            case DTLS_DEFAULT_HANDSHAKE_MODE:
                return new ConfigDescription( param, "AUTO", "auto", "NONE", "none", ConfigDescription.TEST_DTLS );
            case DTLS_MAX_CONNECTIONS:
                return new ConfigDescription( param, null, "150000", "300", "300", ConfigDescription.TEST_DTLS );
            case DTLS_STALE_CONNECTION_THRESHOLD:
                return new ConfigDescription( param, null, "30[min]", "5m", "5[min]", ConfigDescription.TEST_DTLS );
            case DTLS_MAX_PENDING_OUTBOUND_JOBS:
                return new ConfigDescription( param, null, "50000", "555", "555", ConfigDescription.TEST_DTLS );
            case DTLS_MAX_PENDING_INBOUND_JOBS:
                return new ConfigDescription( param, null, "50000", "353", "353", ConfigDescription.TEST_DTLS );
            case DTLS_MAX_PENDING_HANDSHAKE_RESULT_JOBS:
                return new ConfigDescription( param, null, "5000", "3655", "3655", ConfigDescription.TEST_DTLS );
            case DTLS_MAX_DEFERRED_OUTBOUND_APPLICATION_MESSAGES:
                return new ConfigDescription( param, null, "10", "21", "21", ConfigDescription.TEST_DTLS );
            case DTLS_MAX_DEFERRED_INBOUND_RECORDS_SIZE:
                return new ConfigDescription( param, null, "8192", "751", "751", ConfigDescription.TEST_DTLS );
            case DTLS_RECEIVER_THREAD_COUNT:
                return new ConfigDescription( param, null, String.valueOf( THREADS ), "7", "7", ConfigDescription.TEST_DTLS );
            case DTLS_CONNECTOR_THREAD_COUNT:
                return new ConfigDescription( param, null, String.valueOf( CORES ), "17", "17", ConfigDescription.TEST_DTLS );
            case DTLS_RECEIVE_BUFFER_SIZE:
                return new ConfigDescription( param, null, null, "1011", "1011", ConfigDescription.TEST_DTLS );
            case DTLS_SEND_BUFFER_SIZE:
                return new ConfigDescription( param, null, null, "1022", "1022", ConfigDescription.TEST_DTLS );
            case DTLS_USE_SERVER_NAME_INDICATION:
                return new ConfigDescription( param, "false", "false", "true", "true", ConfigDescription.TEST_DTLS );
            case DTLS_EXTENDED_MASTER_SECRET_MODE:
                return new ConfigDescription( param, "ENABLED", "ENABLED", "REQUIRED", "REQUIRED", ConfigDescription.TEST_DTLS );
            case DTLS_USE_HELLO_VERIFY_REQUEST:
                return new ConfigDescription( param, "true", "true", "false", "false", ConfigDescription.TEST_DTLS );
            case DTLS_USE_ANTI_REPLAY_FILTER:
                return new ConfigDescription( param, "DEFAULT", "true", "NO", "false", ConfigDescription.TEST_DTLS );
            case DTLS_USE_DISABLED_WINDOW_FOR_ANTI_REPLAY_FILTER:
                return new ConfigDescription( param, "0", "0", "36", "36", ConfigDescription.TEST_DTLS );
            case DTLS_UPDATE_ADDRESS_USING_CID_ON_NEWER_RECORDS:
                return new ConfigDescription( param, "true", "true", "false", "false", ConfigDescription.TEST_DTLS );
            case DTLS_TRUNCATE_CLIENT_CERTIFICATE_PATH:
                return new ConfigDescription( param, "true", "true", "false", "false", ConfigDescription.TEST_DTLS );
            case DTLS_TRUNCATE_CERTIFICATE_PATH_FOR_VALIDATION:
                return new ConfigDescription( param, "true", "true", "false", "false", ConfigDescription.TEST_DTLS );
            case DTLS_RECOMMENDED_CIPHER_SUITES_ONLY:
                return new ConfigDescription( param, "true", "true", "false", "false", ConfigDescription.TEST_DTLS );
            case DTLS_RECOMMENDED_CURVES_ONLY:
                return new ConfigDescription( param, "true", "true", "false", "false", ConfigDescription.TEST_DTLS );
            case DTLS_RECOMMENDED_SIGNATURE_AND_HASH_ALGORITHMS_ONLY:
                return new ConfigDescription( param, "true", "true", "false", "false", ConfigDescription.TEST_DTLS );
            case DTLS_PRESELECTED_CIPHER_SUITES:
                return new ConfigDescription(
                    param,
                    null,
                    null,
                    "[TLS_PSK_WITH_AES_256_GCM_SHA384, TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA]",
                    "TLS_PSK_WITH_AES_256_GCM_SHA378, TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
                    ConfigDescription.TEST_DTLS
                );
            case DTLS_CIPHER_SUITES:
                return new ConfigDescription(
                    param,
                    null,
                    null,
                    "[TLS_PSK_WITH_AES_128_GCM_SHA256, TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA]",
                    "TLS_PSK_WITH_AES_128_GCM_SHA256, TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
                    ConfigDescription.TEST_DTLS
                );
            case DTLS_CURVES:
                return new ConfigDescription( param, null, null, "[secp521r1, secp384r1]", "secp521r1, secp384r1", ConfigDescription.TEST_DTLS );
            case DTLS_SIGNATURE_AND_HASH_ALGORITHMS:
                return new ConfigDescription( param, null, null, "[SHA512withRSA, SHA256withRSA]", "SHA512withRSA, SHA256withRSA", ConfigDescription.TEST_DTLS );
            case DTLS_CERTIFICATE_KEY_ALGORITHMS:
                return new ConfigDescription( param, null, null, "[RSA]", "RSA", ConfigDescription.TEST_DTLS );
            case DTLS_USE_DEFAULT_RECORD_FILTER:
                return new ConfigDescription( param, "true", "true", "false", "false", ConfigDescription.TEST_DTLSC );
            case DTLS_REMOVE_STALE_DOUBLE_PRINCIPALS:
                return new ConfigDescription( param, "false", "false", "true", "true", ConfigDescription.TEST_DTLS );
            case DTLS_MAC_ERROR_FILTER_QUIET_TIME:
                return new ConfigDescription( param, "RECORD_FILTER", "true", "false", "false", ConfigDescription.TEST_DTLSC );
            case DTLS_MAC_ERROR_FILTER_THRESHOLD:
                return new ConfigDescription( param, "RECORD_FILTER", "true", "false", "false", ConfigDescription.TEST_DTLSC );
            case protocolStageThreadCount:
                return new ConfigDescription( param, null, Integer.toString( Runtime.getRuntime().availableProcessors() ), "12", "12", ConfigDescription.TEST_NONE );
            default:
                throw new Exception( "cannot create parameter description: unknown" );
        }
    }

}
