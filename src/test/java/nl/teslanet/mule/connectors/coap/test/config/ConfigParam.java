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
import org.eclipse.californium.elements.config.DocumentedDefinition;
import org.eclipse.californium.elements.config.SystemConfig;
import org.eclipse.californium.elements.config.UdpConfig;
import org.eclipse.californium.scandium.config.DtlsConfig;


/**
 * The configuration parameters
 *
 */
public enum ConfigParam
{
    //endpoint
    ENDPOINT_LOGCOAPMESSAGES(null),
    ENDPOINT_BINDTOHOST(null),
    ENDPOINT_BINDTOPORT(null),
    ENDPOINT_BINDTOSECUREPORT(null),
    ENDPOINT_REUSEADDRESS(null),
    ENDPOINT_OTHEROPTION_ALIAS(null),
    ENDPOINT_OTHEROPTION_NUMBER(null),
    ENDPOINT_OTHEROPTION_TYPE(null),
    ENDPOINT_OTHEROPTION_SINGLEVALUE(null),
    ENDPOINT_OTHEROPTION_MINBYTES(null),
    ENDPOINT_OTHEROPTION_MAXBYTES(null),

    // module COAP
    maxActivePeers(CoapConfig.MAX_ACTIVE_PEERS),
    maxPeerInactivityPeriod(CoapConfig.MAX_PEER_INACTIVITY_PERIOD),
    ackTimeout(CoapConfig.ACK_TIMEOUT),
    maxAckTimeout(CoapConfig.MAX_ACK_TIMEOUT), //Not RFC7252 compliant.
    ackRandomFactor(CoapConfig.ACK_INIT_RANDOM),
    ackTimeoutScale(CoapConfig.ACK_TIMEOUT_SCALE),
    maxRetransmit(CoapConfig.MAX_RETRANSMIT),
    exchangeLifetime(CoapConfig.EXCHANGE_LIFETIME),
    nonLifetime(CoapConfig.NON_LIFETIME),
    maxLatency(CoapConfig.MAX_LATENCY),
    maxTransmitWait(CoapConfig.MAX_TRANSMIT_WAIT),
    maxServerResponseDelay(CoapConfig.MAX_SERVER_RESPONSE_DELAY),
    nstart(CoapConfig.NSTART),
    leisure(CoapConfig.LEISURE),
    probingRate(CoapConfig.PROBING_RATE),
    //TODO useMessageOffloading

    useRandomMidStart(CoapConfig.USE_RANDOM_MID_START),
    midTracker(CoapConfig.MID_TRACKER),
    midTrackerGroups(CoapConfig.MID_TRACKER_GROUPS),
    multicastMidBase(CoapConfig.MULTICAST_BASE_MID),
    tokenSizeLimit(CoapConfig.TOKEN_SIZE_LIMIT),

    preferredBlockSize(CoapConfig.PREFERRED_BLOCK_SIZE),
    maxMessageSize(CoapConfig.MAX_MESSAGE_SIZE),
    maxResourceBodySize(CoapConfig.MAX_RESOURCE_BODY_SIZE),
    blockwiseStatusLifetime(CoapConfig.BLOCKWISE_STATUS_LIFETIME),
    blockwiseStatusInterval(CoapConfig.BLOCKWISE_STATUS_INTERVAL),
    //TODO tcpNumberOfBulkBlocks
    blockwiseStrictBlock1Option(CoapConfig.BLOCKWISE_STRICT_BLOCK1_OPTION),
    blockwiseStrictBlock2Option(CoapConfig.BLOCKWISE_STRICT_BLOCK2_OPTION),
    blockwiseEntityTooLargeAutoFailover(CoapConfig.BLOCKWISE_ENTITY_TOO_LARGE_AUTO_FAILOVER),

    NOTIFICATION_CHECK_INTERVAL_TIME(CoapConfig.NOTIFICATION_CHECK_INTERVAL_TIME),
    NOTIFICATION_CHECK_INTERVAL_COUNT(CoapConfig.NOTIFICATION_CHECK_INTERVAL_COUNT),
    NOTIFICATION_REREGISTRATION_BACKOFF(CoapConfig.NOTIFICATION_REREGISTRATION_BACKOFF),
    MAX_SERVER_OBSERVES(CoapConfig.MAX_SERVER_OBSERVES),

    congestionControlAlgorithm(CoapConfig.CONGESTION_CONTROL_ALGORITHM),

    deduplicator(CoapConfig.DEDUPLICATOR),
    // deduplicatorMarkAndSweep( CoapConfig ),
    markAndSweepInterval(CoapConfig.MARK_AND_SWEEP_INTERVAL),
    // deduplicatorPeersMarkAndSweep( CoapConfig ),
    peersMarkAndSweepMessages(CoapConfig.PEERS_MARK_AND_SWEEP_MESSAGES),
    // deduplicatorCropRotation( CoapConfig ),
    cropRotationPeriod(CoapConfig.CROP_ROTATION_PERIOD),
    // noDeduplicator( CoapConfig ),
    // all deduplicators
    deduplicationAutoReplace(CoapConfig.DEDUPLICATOR_AUTO_REPLACE),

    //coap on endpoint level
    responseMatching(CoapConfig.RESPONSE_MATCHING),
    strictEmptyMessageFormat(CoapConfig.STRICT_EMPTY_MESSAGE_FORMAT),

    //dtls params

    //DTLS_SESSION_TIMEOUT(DtlsConfig.DTLS_SESSION_TIMEOUT), //not supported by cf
    DTLS_AUTO_HANDSHAKE_TIMEOUT(DtlsConfig.DTLS_AUTO_HANDSHAKE_TIMEOUT),
    DTLS_RETRANSMISSION_TIMEOUT(DtlsConfig.DTLS_RETRANSMISSION_TIMEOUT),
    DTLS_MAX_RETRANSMISSION_TIMEOUT(DtlsConfig.DTLS_MAX_RETRANSMISSION_TIMEOUT),
    DTLS_ADDITIONAL_ECC_TIMEOUT(DtlsConfig.DTLS_ADDITIONAL_ECC_TIMEOUT),
    DTLS_MAX_RETRANSMISSIONS(DtlsConfig.DTLS_MAX_RETRANSMISSIONS),
    DTLS_RETRANSMISSION_INIT_RANDOM(DtlsConfig.DTLS_RETRANSMISSION_INIT_RANDOM),
    DTLS_RETRANSMISSION_TIMEOUT_SCALE(DtlsConfig.DTLS_RETRANSMISSION_TIMEOUT_SCALE),
    DTLS_RETRANSMISSION_BACKOFF(DtlsConfig.DTLS_RETRANSMISSION_BACKOFF),
    DTLS_SUPPORT_CONNECTION_ID(DtlsConfig.DTLS_CONNECTION_ID_LENGTH),
    DTLS_SERVER_USE_SESSION_ID(DtlsConfig.DTLS_SERVER_USE_SESSION_ID),
    DTLS_USE_EARLY_STOP_RETRANSMISSION(DtlsConfig.DTLS_USE_EARLY_STOP_RETRANSMISSION),
    DTLS_RECORD_SIZE_LIMIT(DtlsConfig.DTLS_RECORD_SIZE_LIMIT),
    DTLS_MAX_FRAGMENT_LENGTH(DtlsConfig.DTLS_MAX_FRAGMENT_LENGTH),
    DTLS_MAX_FRAGMENTED_HANDSHAKE_MESSAGE_LENGTH(DtlsConfig.DTLS_MAX_FRAGMENTED_HANDSHAKE_MESSAGE_LENGTH),
    DTLS_USE_MULTI_RECORD_MESSAGES(DtlsConfig.DTLS_USE_MULTI_RECORD_MESSAGES),
    DTLS_USE_MULTI_HANDSHAKE_MESSAGE_RECORDS(DtlsConfig.DTLS_USE_MULTI_HANDSHAKE_MESSAGE_RECORDS),
    DTLS_CLIENT_AUTHENTICATION_MODE(DtlsConfig.DTLS_CLIENT_AUTHENTICATION_MODE),
    DTLS_VERIFY_SERVER_CERTIFICATES_SUBJECT(DtlsConfig.DTLS_VERIFY_SERVER_CERTIFICATES_SUBJECT),
    DTLS_ROLE(DtlsConfig.DTLS_ROLE),
    DTLS_MAX_TRANSMISSION_UNIT(DtlsConfig.DTLS_MAX_TRANSMISSION_UNIT),
    DTLS_MAX_TRANSMISSION_UNIT_LIMIT(DtlsConfig.DTLS_MAX_TRANSMISSION_UNIT_LIMIT),
    DTLS_DEFAULT_HANDSHAKE_MODE(DtlsConfig.DTLS_DEFAULT_HANDSHAKE_MODE),
    DTLS_MAX_CONNECTIONS(DtlsConfig.DTLS_MAX_CONNECTIONS),
    DTLS_STALE_CONNECTION_THRESHOLD(DtlsConfig.DTLS_STALE_CONNECTION_THRESHOLD),
    DTLS_MAX_PENDING_OUTBOUND_JOBS(DtlsConfig.DTLS_MAX_PENDING_OUTBOUND_JOBS),
    DTLS_MAX_PENDING_INBOUND_JOBS(DtlsConfig.DTLS_MAX_PENDING_INBOUND_JOBS),
    DTLS_MAX_PENDING_HANDSHAKE_RESULT_JOBS(DtlsConfig.DTLS_MAX_PENDING_HANDSHAKE_RESULT_JOBS),
    DTLS_MAX_DEFERRED_OUTBOUND_APPLICATION_MESSAGES(DtlsConfig.DTLS_MAX_DEFERRED_OUTBOUND_APPLICATION_MESSAGES),
    DTLS_MAX_DEFERRED_INBOUND_RECORDS_SIZE(DtlsConfig.DTLS_MAX_DEFERRED_INBOUND_RECORDS_SIZE),
    DTLS_RECEIVER_THREAD_COUNT(DtlsConfig.DTLS_RECEIVER_THREAD_COUNT),
    DTLS_CONNECTOR_THREAD_COUNT(DtlsConfig.DTLS_CONNECTOR_THREAD_COUNT),
    DTLS_RECEIVE_BUFFER_SIZE(DtlsConfig.DTLS_RECEIVE_BUFFER_SIZE),
    DTLS_SEND_BUFFER_SIZE(DtlsConfig.DTLS_SEND_BUFFER_SIZE),
    DTLS_USE_SERVER_NAME_INDICATION(DtlsConfig.DTLS_USE_SERVER_NAME_INDICATION),
    DTLS_EXTENDED_MASTER_SECRET_MODE(DtlsConfig.DTLS_EXTENDED_MASTER_SECRET_MODE),
    DTLS_USE_HELLO_VERIFY_REQUEST(DtlsConfig.DTLS_USE_HELLO_VERIFY_REQUEST),
    DTLS_USE_ANTI_REPLAY_FILTER(DtlsConfig.DTLS_USE_ANTI_REPLAY_FILTER),
    DTLS_USE_DISABLED_WINDOW_FOR_ANTI_REPLAY_FILTER(DtlsConfig.DTLS_USE_DISABLED_WINDOW_FOR_ANTI_REPLAY_FILTER),
    DTLS_UPDATE_ADDRESS_USING_CID_ON_NEWER_RECORDS(DtlsConfig.DTLS_UPDATE_ADDRESS_USING_CID_ON_NEWER_RECORDS),
    DTLS_TRUNCATE_CLIENT_CERTIFICATE_PATH(DtlsConfig.DTLS_TRUNCATE_CLIENT_CERTIFICATE_PATH),
    DTLS_TRUNCATE_CERTIFICATE_PATH_FOR_VALIDATION(DtlsConfig.DTLS_TRUNCATE_CERTIFICATE_PATH_FOR_VALIDATION),
    DTLS_RECOMMENDED_CIPHER_SUITES_ONLY(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY),
    DTLS_RECOMMENDED_CURVES_ONLY(DtlsConfig.DTLS_RECOMMENDED_CURVES_ONLY),
    DTLS_RECOMMENDED_SIGNATURE_AND_HASH_ALGORITHMS_ONLY(DtlsConfig.DTLS_RECOMMENDED_SIGNATURE_AND_HASH_ALGORITHMS_ONLY),
    DTLS_PRESELECTED_CIPHER_SUITES(DtlsConfig.DTLS_PRESELECTED_CIPHER_SUITES),
    DTLS_CIPHER_SUITES(DtlsConfig.DTLS_CIPHER_SUITES),
    DTLS_CURVES(DtlsConfig.DTLS_CURVES),
    DTLS_SIGNATURE_AND_HASH_ALGORITHMS(DtlsConfig.DTLS_SIGNATURE_AND_HASH_ALGORITHMS),
    DTLS_CERTIFICATE_KEY_ALGORITHMS(DtlsConfig.DTLS_CERTIFICATE_KEY_ALGORITHMS),
    DTLS_REMOVE_STALE_DOUBLE_PRINCIPALS(DtlsConfig.DTLS_REMOVE_STALE_DOUBLE_PRINCIPALS),

    //cluster features
    DTLS_CONNECTION_ID_LENGTH(DtlsConfig.DTLS_CONNECTION_ID_LENGTH),
    DTLS_CONNECTION_ID_NODE_ID(DtlsConfig.DTLS_CONNECTION_ID_NODE_ID),
    DTLS_USE_DEFAULT_RECORD_FILTER(DtlsConfig.DTLS_USE_DEFAULT_RECORD_FILTER),
    DTLS_MAC_ERROR_FILTER_QUIET_TIME(DtlsConfig.DTLS_MAC_ERROR_FILTER_QUIET_TIME),
    DTLS_MAC_ERROR_FILTER_THRESHOLD(DtlsConfig.DTLS_MAC_ERROR_FILTER_THRESHOLD),

    //secure
    pskHost(null),
    pskPort(null),
    pskIdentity(null),
    pskKey(null),
    keyStoreLocation(null),
    keyStorePassword(null),
    privateKeyAlias(null),
    privateKeyPassword(null),
    trustStoreLocation(null),
    trustStorePassword(null),
    trustedRootCertificateAlias(null),

    //server
    protocolStageThreadCount(CoapConfig.PROTOCOL_STAGE_THREAD_COUNT),

    //module udp
    UDP_RECEIVER_THREAD_COUNT(UdpConfig.UDP_RECEIVER_THREAD_COUNT),
    UDP_SENDER_THREAD_COUNT(UdpConfig.UDP_SENDER_THREAD_COUNT),
    UDP_DATAGRAM_SIZE(UdpConfig.UDP_DATAGRAM_SIZE),
    UDP_RECEIVE_BUFFER_SIZE(UdpConfig.UDP_RECEIVE_BUFFER_SIZE),
    UDP_SEND_BUFFER_SIZE(UdpConfig.UDP_SEND_BUFFER_SIZE),
    UDP_CONNECTOR_OUT_CAPACITY(UdpConfig.UDP_CONNECTOR_OUT_CAPACITY),

    multicastGroups(null),

    //module sys
    logHealthStatus(null),
    HEALTH_STATUS_INTERVAL(SystemConfig.HEALTH_STATUS_INTERVAL);

    //TODO module tcp
    /*
    TCP_WORKER_THREADS(TcpConfig.TCP_WORKER_THREADS),
    TCP_CONNECTION_IDLE_TIMEOUT(TcpConfig.TCP_CONNECTION_IDLE_TIMEOUT),
    TCP_CONNECT_TIMEOUT(TcpConfig.TCP_CONNECT_TIMEOUT),
    TLS_HANDSHAKE_TIMEOUT(TcpConfig.TLS_HANDSHAKE_TIMEOUT),
    TLS_SESSION_TIMEOUT(TcpConfig.TLS_SESSION_TIMEOUT),
    TLS_CLIENT_AUTHENTICATION_MODE(TcpConfig.TLS_CLIENT_AUTHENTICATION_MODE),
    TLS_VERIFY_SERVER_CERTIFICATES_SUBJECT(TcpConfig.TLS_VERIFY_SERVER_CERTIFICATES_SUBJECT),
    */

    /**
     * Californium configuration definition.
     */
    private final DocumentedDefinition< ? > cfConfigDef;

    /**
     * Constructor.
     * @param cfConfigDef The Californium configuration definition.
     */
    ConfigParam( DocumentedDefinition< ? > cfConfigDef )
    {
        this.cfConfigDef= cfConfigDef;
    }

    /**
     * @return The Californium configuration definition.
     */
    public DocumentedDefinition< ? > getCfConfigDef()
    {
        return cfConfigDef;
    }
}
