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
package nl.teslanet.mule.connectors.coap.internal.config;


import java.util.concurrent.TimeUnit;

import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.config.CoapConfig.CongestionControlMode;
import org.eclipse.californium.core.config.CoapConfig.MatcherMode;
import org.eclipse.californium.core.config.CoapConfig.TrackerMode;
import org.eclipse.californium.elements.DtlsEndpointContext;
import org.eclipse.californium.elements.config.CertificateAuthenticationMode;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.SystemConfig;
import org.eclipse.californium.elements.config.TcpConfig;
import org.eclipse.californium.elements.config.UdpConfig;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.californium.scandium.config.DtlsConfig.DtlsRole;
import org.eclipse.californium.scandium.dtls.MaxFragmentLengthExtension.Length;

import nl.teslanet.mule.connectors.coap.api.config.BlockwiseParams;
import nl.teslanet.mule.connectors.coap.api.config.ConfigException;
import nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor;
import nl.teslanet.mule.connectors.coap.api.config.ExchangeParams;
import nl.teslanet.mule.connectors.coap.api.config.LogHealthStatus;
import nl.teslanet.mule.connectors.coap.api.config.NotificationParams;
import nl.teslanet.mule.connectors.coap.api.config.SocketParams;
import nl.teslanet.mule.connectors.coap.api.config.TcpParams;
import nl.teslanet.mule.connectors.coap.api.config.TlsParams;
import nl.teslanet.mule.connectors.coap.api.config.UdpParams;
import nl.teslanet.mule.connectors.coap.api.config.congestion.BasicRto;
import nl.teslanet.mule.connectors.coap.api.config.congestion.Cocoa;
import nl.teslanet.mule.connectors.coap.api.config.congestion.CocoaStrong;
import nl.teslanet.mule.connectors.coap.api.config.congestion.LinuxRto;
import nl.teslanet.mule.connectors.coap.api.config.congestion.PeakhopperRto;
import nl.teslanet.mule.connectors.coap.api.config.deduplication.CropRotation;
import nl.teslanet.mule.connectors.coap.api.config.deduplication.Deduplicator;
import nl.teslanet.mule.connectors.coap.api.config.deduplication.MarkAndSweep;
import nl.teslanet.mule.connectors.coap.api.config.deduplication.PeersMarkAndSweep;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DatagramFilter;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DefaultReplayFilter;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsClientAndServerRole;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsClientParams;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsClientRole;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsMessageParams;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsParams;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsRetransmissionParams;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsServerParams;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsServerRole;
import nl.teslanet.mule.connectors.coap.api.config.dtls.ExtendedReplayFilter;
import nl.teslanet.mule.connectors.coap.api.config.dtls.NoReplayFilter;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractUDPEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.DTLSEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.GroupedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.MapBasedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.NullMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.security.ConnectionId;
import nl.teslanet.mule.connectors.coap.api.config.security.SecurityParams;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidDurationException;
import nl.teslanet.mule.connectors.coap.internal.utils.SecurityUtils;
import nl.teslanet.mule.connectors.coap.internal.utils.TimeUtils;


/**
 * Configuration visitor that collects collects configuration parameters
 * to put into a Californium Configuration.
 *
 */
public class ConfigurationVisitor implements ConfigVisitor
{
    private Configuration config= Configuration.createStandardWithoutFile();

    /**
     * Visit configuration.
     */
    @Override
    public void visit( AbstractUDPEndpoint toVisit )
    {
        if ( toVisit.strictResponseMatching )
        {
            config.set( CoapConfig.RESPONSE_MATCHING, MatcherMode.STRICT );
        }
        else
        {
            config.set( CoapConfig.RESPONSE_MATCHING, MatcherMode.RELAXED );

        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( DTLSEndpoint toVisit )
    {
        if ( toVisit.responseMatching != null )
        {
            switch ( toVisit.responseMatching )
            {
                case PRINCIPAL:
                    config.set( CoapConfig.RESPONSE_MATCHING, MatcherMode.PRINCIPAL );
                    break;
                case PRINCIPAL_IDENTITY:
                    config.set( CoapConfig.RESPONSE_MATCHING, MatcherMode.PRINCIPAL_IDENTITY );
                    break;
                case RELAXED:
                    config.set( CoapConfig.RESPONSE_MATCHING, MatcherMode.RELAXED );
                    break;
                case STRICT:
                    config.set( CoapConfig.RESPONSE_MATCHING, MatcherMode.STRICT );
                    break;
            }
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( DtlsServerRole toVisit )
    {
        config.set( DtlsConfig.DTLS_ROLE, DtlsRole.SERVER_ONLY );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( DtlsClientRole toVisit )
    {
        config.set( DtlsConfig.DTLS_ROLE, DtlsRole.CLIENT_ONLY );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( DtlsClientAndServerRole toVisit )
    {
        config.set( DtlsConfig.DTLS_ROLE, DtlsRole.BOTH );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( NoReplayFilter toVisit )
    {
        config.set( DtlsConfig.DTLS_USE_ANTI_REPLAY_FILTER, false );
        config.set( DtlsConfig.DTLS_USE_DISABLED_WINDOW_FOR_ANTI_REPLAY_FILTER, 0 );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( DefaultReplayFilter toVisit )
    {
        config.set( DtlsConfig.DTLS_USE_ANTI_REPLAY_FILTER, true );
        config.set( DtlsConfig.DTLS_USE_DISABLED_WINDOW_FOR_ANTI_REPLAY_FILTER, 0 );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( ExtendedReplayFilter toVisit )
    {
        config.set( DtlsConfig.DTLS_USE_ANTI_REPLAY_FILTER, true );
        if ( toVisit.extendedfilterWindow == null )
        {
            config.set( DtlsConfig.DTLS_USE_DISABLED_WINDOW_FOR_ANTI_REPLAY_FILTER, -1 );
        }
        else
        {
            config
                .set(
                    DtlsConfig.DTLS_USE_DISABLED_WINDOW_FOR_ANTI_REPLAY_FILTER,
                    Math.max( 0, toVisit.extendedfilterWindow )
                );
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( BlockwiseParams toVisit ) throws ConfigException
    {
        try
        {
            if ( toVisit.preferredBlockSize != null ) config
                .set( CoapConfig.PREFERRED_BLOCK_SIZE, toVisit.preferredBlockSize );
            if ( toVisit.maxMessageSize != null ) config.set( CoapConfig.MAX_MESSAGE_SIZE, toVisit.maxMessageSize );
            // TODO: only transparent blockwise is supported: maxResourceBodySize > 0
            if ( toVisit.maxResourceBodySize != null && toVisit.maxResourceBodySize > 0 ) config
                .set( CoapConfig.MAX_RESOURCE_BODY_SIZE, toVisit.maxResourceBodySize );
            if ( toVisit.statusLifetime != null ) config
                .set(
                    CoapConfig.BLOCKWISE_STATUS_LIFETIME,
                    TimeUtils.toNanos( toVisit.statusLifetime ),
                    TimeUnit.NANOSECONDS
                );
            if ( toVisit.statusInterval != null ) config
                .set(
                    CoapConfig.BLOCKWISE_STATUS_INTERVAL,
                    TimeUtils.toNanos( toVisit.statusInterval ),
                    TimeUnit.NANOSECONDS
                );
            config.set( CoapConfig.BLOCKWISE_STRICT_BLOCK1_OPTION, toVisit.strictBlock1Option );
            config.set( CoapConfig.BLOCKWISE_STRICT_BLOCK2_OPTION, toVisit.strictBlock2Option );
            config.set( CoapConfig.BLOCKWISE_ENTITY_TOO_LARGE_AUTO_FAILOVER, toVisit.entityTooLargeFailover );
        }
        catch ( InternalInvalidDurationException e )
        {
            throw new ConfigException( "blockwise-params configuration error", e );
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( ExchangeParams toVisit ) throws ConfigException
    {
        try
        {
            if ( toVisit.maxActivePeers != null )
            {
                config.set( CoapConfig.MAX_ACTIVE_PEERS, toVisit.maxActivePeers );
            }
            if ( toVisit.maxPeerInactivityPeriod != null )
            {
                config
                    .set(
                        CoapConfig.MAX_PEER_INACTIVITY_PERIOD,
                        TimeUtils.toNanos( toVisit.maxPeerInactivityPeriod ),
                        TimeUnit.NANOSECONDS
                    );
            }
            if ( toVisit.ackTimeout != null )
            {
                config.set( CoapConfig.ACK_TIMEOUT, TimeUtils.toNanos( toVisit.ackTimeout ), TimeUnit.NANOSECONDS );
            }
            if ( toVisit.maxAckTimeout != null )
            {
                config
                    .set(
                        CoapConfig.MAX_ACK_TIMEOUT,
                        TimeUtils.toNanos( toVisit.maxAckTimeout ),
                        TimeUnit.NANOSECONDS
                    );
            }
            if ( toVisit.ackRandomFactor != null )
            {
                config.set( CoapConfig.ACK_INIT_RANDOM, toVisit.ackRandomFactor );
            }
            if ( toVisit.ackTimeoutScale != null )
            {
                config.set( CoapConfig.ACK_TIMEOUT_SCALE, toVisit.ackTimeoutScale );
            }
            if ( toVisit.maxRetransmit != null )
            {
                config.set( CoapConfig.MAX_RETRANSMIT, toVisit.maxRetransmit );
            }
            if ( toVisit.exchangeLifetime != null )
            {
                config
                    .set(
                        CoapConfig.EXCHANGE_LIFETIME,
                        TimeUtils.toNanos( toVisit.exchangeLifetime ),
                        TimeUnit.NANOSECONDS
                    );
            }
            if ( toVisit.nonLifetime != null )
            {
                config.set( CoapConfig.NON_LIFETIME, TimeUtils.toNanos( toVisit.nonLifetime ), TimeUnit.NANOSECONDS );
            }
            if ( toVisit.maxLatency != null )
            {
                config.set( CoapConfig.MAX_LATENCY, TimeUtils.toNanos( toVisit.maxLatency ), TimeUnit.NANOSECONDS );
            }
            if ( toVisit.maxTransmitWait != null )
            {
                config
                    .set(
                        CoapConfig.MAX_TRANSMIT_WAIT,
                        TimeUtils.toNanos( toVisit.maxTransmitWait ),
                        TimeUnit.NANOSECONDS
                    );
            }
            if ( toVisit.maxResponseDelay != null )
            {
                config
                    .set(
                        CoapConfig.MAX_SERVER_RESPONSE_DELAY,
                        TimeUtils.toNanos( toVisit.maxResponseDelay ),
                        TimeUnit.NANOSECONDS
                    );
            }
            if ( toVisit.nstart != null )
            {
                config.set( CoapConfig.NSTART, toVisit.nstart );
            }
            if ( toVisit.tokenSizeLimit != null )
            {
                config.set( CoapConfig.TOKEN_SIZE_LIMIT, toVisit.tokenSizeLimit );
            }
            if ( toVisit.multicastMidBase != null )
            {
                config.set( CoapConfig.MULTICAST_BASE_MID, toVisit.multicastMidBase );
            }
            if ( toVisit.leisure != null )
            {
                config.set( CoapConfig.LEISURE, TimeUtils.toNanos( toVisit.leisure ), TimeUnit.NANOSECONDS );
            }
            config.set( CoapConfig.STRICT_EMPTY_MESSAGE_FORMAT, toVisit.strictEmptyMessageFormat );
            config.set( CoapConfig.USE_RANDOM_MID_START, toVisit.useRandomMidStart );
        }
        catch ( InternalInvalidDurationException e )
        {
            throw new ConfigException( "exchange-params configuration error", e );
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( GroupedMidTracker toVisit )
    {
        config.set( CoapConfig.MID_TRACKER, TrackerMode.GROUPED );
        if ( toVisit.midTrackerGroups != null ) config.set( CoapConfig.MID_TRACKER_GROUPS, toVisit.midTrackerGroups );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( MapBasedMidTracker toVisit )
    {
        config.set( CoapConfig.MID_TRACKER, TrackerMode.MAPBASED );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( NullMidTracker toVisit )
    {
        config.set( CoapConfig.MID_TRACKER, TrackerMode.NULL );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( Deduplicator toVisit )
    {
        config.set( CoapConfig.DEDUPLICATOR_AUTO_REPLACE, toVisit.autoReplace );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( CropRotation toVisit ) throws ConfigException
    {
        try
        {
            config.set( CoapConfig.DEDUPLICATOR, CoapConfig.DEDUPLICATOR_CROP_ROTATION );
            if ( toVisit.cropRotationPeriod != null ) config
                .set(
                    CoapConfig.CROP_ROTATION_PERIOD,
                    TimeUtils.toNanos( toVisit.cropRotationPeriod ),
                    TimeUnit.NANOSECONDS
                );
        }
        catch ( InternalInvalidDurationException e )
        {
            throw new ConfigException( "crop-rotation configuration error", e );
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( MarkAndSweep toVisit ) throws ConfigException
    {
        try
        {
            config.set( CoapConfig.DEDUPLICATOR, CoapConfig.DEDUPLICATOR_MARK_AND_SWEEP );
            if ( toVisit.markAndSweepInterval != null ) config
                .set(
                    CoapConfig.MARK_AND_SWEEP_INTERVAL,
                    TimeUtils.toNanos( toVisit.markAndSweepInterval ),
                    TimeUnit.NANOSECONDS
                );
        }
        catch ( InternalInvalidDurationException e )
        {
            throw new ConfigException( "mark-and-sweep configuration error", e );
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( PeersMarkAndSweep toVisit )
    {
        config.set( CoapConfig.DEDUPLICATOR, CoapConfig.DEDUPLICATOR_PEERS_MARK_AND_SWEEP );
        if ( toVisit.maxMessagesPerPeer != null ) config
            .set( CoapConfig.PEERS_MARK_AND_SWEEP_MESSAGES, toVisit.maxMessagesPerPeer );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( LogHealthStatus toVisit ) throws ConfigException
    {
        try
        {
            if ( toVisit.healthStatusInterval != null )
            {
                config
                    .set(
                        SystemConfig.HEALTH_STATUS_INTERVAL,
                        TimeUtils.toNanos( toVisit.healthStatusInterval ),
                        TimeUnit.NANOSECONDS
                    );
            }
            else
            {
                config.set( SystemConfig.HEALTH_STATUS_INTERVAL, 600, TimeUnit.SECONDS );
            }
        }
        catch ( InternalInvalidDurationException e )
        {
            throw new ConfigException( "health status configuration error", e );
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( BasicRto basicRto )
    {
        config.set( CoapConfig.CONGESTION_CONTROL_ALGORITHM, CongestionControlMode.BASIC_RTO );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( Cocoa cocoa )
    {
        config.set( CoapConfig.CONGESTION_CONTROL_ALGORITHM, CongestionControlMode.COCOA );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( CocoaStrong cocoaStrong )
    {
        config.set( CoapConfig.CONGESTION_CONTROL_ALGORITHM, CongestionControlMode.COCOA_STRONG );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( LinuxRto linuxRto )
    {
        config.set( CoapConfig.CONGESTION_CONTROL_ALGORITHM, CongestionControlMode.LINUX_RTO );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( PeakhopperRto peakhopperRto )
    {
        config.set( CoapConfig.CONGESTION_CONTROL_ALGORITHM, CongestionControlMode.PEAKHOPPER_RTO );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( UdpParams toVisit )
    {
        if ( toVisit.receiverThreadCount != null ) config
            .set( UdpConfig.UDP_RECEIVER_THREAD_COUNT, toVisit.receiverThreadCount );
        if ( toVisit.senderThreadCount != null ) config
            .set( UdpConfig.UDP_SENDER_THREAD_COUNT, toVisit.senderThreadCount );
        if ( toVisit.datagramSize != null ) config.set( UdpConfig.UDP_DATAGRAM_SIZE, toVisit.datagramSize );
        if ( toVisit.outCapacity != null ) config.set( UdpConfig.UDP_CONNECTOR_OUT_CAPACITY, toVisit.outCapacity );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( TcpParams toVisit )
    {
        if ( toVisit.tcpConnectTimeout != null ) config
            .set( TcpConfig.TCP_CONNECT_TIMEOUT, toVisit.tcpConnectTimeout, TimeUnit.MILLISECONDS );
        if ( toVisit.tcpWorkerThreads != null ) config.set( TcpConfig.TCP_WORKER_THREADS, toVisit.tcpWorkerThreads );
        if ( toVisit.tcpConnectionIdleTimeout != null ) config
            .set( TcpConfig.TCP_CONNECTION_IDLE_TIMEOUT, toVisit.tcpConnectTimeout, TimeUnit.MILLISECONDS );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( TlsParams toVisit )
    {
        if ( toVisit.tlsHandshakeTimeout != null ) config
            .set( TcpConfig.TLS_HANDSHAKE_TIMEOUT, toVisit.tlsHandshakeTimeout, TimeUnit.MILLISECONDS );
        if ( toVisit.secureSessionTimeout != null ) config
            .set( TcpConfig.TLS_SESSION_TIMEOUT, toVisit.secureSessionTimeout, TimeUnit.MILLISECONDS );
        //TODO complete params, update default
    }

    /**
     * Visit configuration.
     * @throws ConfigException 
     */
    @Override
    public void visit( SecurityParams toVisit ) throws ConfigException
    {
        config.set( DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, toVisit.recommendedCipherSuitesOnly );
        config.set( DtlsConfig.DTLS_RECOMMENDED_CURVES_ONLY, toVisit.recommendedCurvesOnly );
        config
            .set(
                DtlsConfig.DTLS_RECOMMENDED_SIGNATURE_AND_HASH_ALGORITHMS_ONLY,
                toVisit.recommendedSignatureAndHashAlgorithmsOnly
            );
        if ( toVisit.preselectedCipherSuites != null )
        {
            config
                .set(
                    DtlsConfig.DTLS_PRESELECTED_CIPHER_SUITES,
                    SecurityUtils.toCfCipherSuites( toVisit.preselectedCipherSuites )
                );
        }
        if ( toVisit.cipherSuites != null && !toVisit.cipherSuites.isEmpty() )
        {
            config.set( DtlsConfig.DTLS_CIPHER_SUITES, SecurityUtils.toCfCipherSuites( toVisit.cipherSuites ) );
        }
        if ( toVisit.curves != null && !toVisit.curves.isEmpty() )
        {
            config.set( DtlsConfig.DTLS_CURVES, SecurityUtils.toCfCurves( toVisit.curves ) );
        }
        if ( toVisit.signatureAlgorithms != null && !toVisit.signatureAlgorithms.isEmpty() )
        {
            config
                .set(
                    DtlsConfig.DTLS_SIGNATURE_AND_HASH_ALGORITHMS,
                    SecurityUtils.toCfSignatureAndHashAlgoritms( toVisit.signatureAlgorithms )
                );
        }
        if ( toVisit.certificateKeyAlgorithms != null && !toVisit.certificateKeyAlgorithms.isEmpty() )
        {
            config
                .set(
                    DtlsConfig.DTLS_CERTIFICATE_KEY_ALGORITHMS,
                    SecurityUtils.toCfCertificateKeyAlgorithms( toVisit.certificateKeyAlgorithms )
                );
        }
        if ( toVisit.extendedMasterSecretMode != null )
        {
            config
                .set(
                    DtlsConfig.DTLS_EXTENDED_MASTER_SECRET_MODE,
                    SecurityUtils.toCfExtendedMasterSecretMode( toVisit.extendedMasterSecretMode )
                );
        }
        config
            .set(
                DtlsConfig.DTLS_TRUNCATE_CERTIFICATE_PATH_FOR_VALIDATION,
                toVisit.truncateCertificatePathForValidation
            );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( ConnectionId toVisit ) throws ConfigException
    {
        if ( toVisit.connectionIdLength != null && toVisit.connectionIdLength > 0 )
        {
            config.set( DtlsConfig.DTLS_CONNECTION_ID_LENGTH, toVisit.connectionIdLength );
        }
        else
        {
            config.set( DtlsConfig.DTLS_CONNECTION_ID_LENGTH, 0 );
        }
        config.set( DtlsConfig.DTLS_UPDATE_ADDRESS_USING_CID_ON_NEWER_RECORDS, toVisit.updateAddressOnNewerRecords );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( DtlsServerParams toVisit ) throws ConfigException
    {
        config.set( DtlsConfig.DTLS_SERVER_USE_SESSION_ID, toVisit.serverUseSessionId );
        if ( toVisit.clientAuthentication != null )
        {
            switch ( toVisit.clientAuthentication )
            {
                case NONE:
                    config.set( DtlsConfig.DTLS_CLIENT_AUTHENTICATION_MODE, CertificateAuthenticationMode.NONE );
                    break;
                case OPTIONAL:
                    config.set( DtlsConfig.DTLS_CLIENT_AUTHENTICATION_MODE, CertificateAuthenticationMode.WANTED );
                    break;
                case MANDATORY:
                    config.set( DtlsConfig.DTLS_CLIENT_AUTHENTICATION_MODE, CertificateAuthenticationMode.NEEDED );
                    break;
            }
        }
        config.set( DtlsConfig.DTLS_SERVER_USE_SESSION_ID, toVisit.serverUseSessionId );
        config.set( DtlsConfig.DTLS_USE_SERVER_NAME_INDICATION, toVisit.serverNameIndication );
        config.set( DtlsConfig.DTLS_USE_HELLO_VERIFY_REQUEST, toVisit.helloVerifyRequest );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( DtlsClientParams toVisit ) throws ConfigException
    {
        config.set( DtlsConfig.DTLS_VERIFY_SERVER_CERTIFICATES_SUBJECT, toVisit.verifyServerCertificateSubject );
        switch ( toVisit.defaultHandshakeMode )
        {
            case NONE:
                config.set( DtlsConfig.DTLS_DEFAULT_HANDSHAKE_MODE, DtlsEndpointContext.HANDSHAKE_MODE_NONE );
                break;
            case AUTO:
                config.set( DtlsConfig.DTLS_DEFAULT_HANDSHAKE_MODE, DtlsEndpointContext.HANDSHAKE_MODE_AUTO );
                break;
        }
        config.set( DtlsConfig.DTLS_TRUNCATE_CLIENT_CERTIFICATE_PATH, toVisit.truncateClientCertificatePaths );
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( DtlsParams toVisit ) throws ConfigException
    {
        try
        {
            if ( toVisit.maxConnections != null )
            {
                config.set( DtlsConfig.DTLS_MAX_CONNECTIONS, toVisit.maxConnections );
            }
            if ( toVisit.staleConnectionThreshold != null )
            {
                config
                    .set(
                        DtlsConfig.DTLS_STALE_CONNECTION_THRESHOLD,
                        TimeUtils.toNanos( toVisit.staleConnectionThreshold ),
                        TimeUnit.NANOSECONDS
                    );
            }
            config.set( DtlsConfig.DTLS_REMOVE_STALE_DOUBLE_PRINCIPALS, toVisit.removeStaleDoublePrincipals );
            if ( toVisit.outboundMsgCapacity != null )
            {
                config.set( DtlsConfig.DTLS_MAX_PENDING_OUTBOUND_JOBS, toVisit.outboundMsgCapacity );
            }
            if ( toVisit.inboundMsgCapacity != null )
            {
                config.set( DtlsConfig.DTLS_MAX_PENDING_INBOUND_JOBS, toVisit.inboundMsgCapacity );
            }
            if ( toVisit.dtlsReceiverThreadCount != null )
            {
                config.set( DtlsConfig.DTLS_RECEIVER_THREAD_COUNT, toVisit.dtlsReceiverThreadCount );
            }
            if ( toVisit.dtlsConnectorThreadCount != null )
            {
                config.set( DtlsConfig.DTLS_CONNECTOR_THREAD_COUNT, toVisit.dtlsConnectorThreadCount );
            }
            if ( toVisit.handshakeCapacity != null )
            {
                config.set( DtlsConfig.DTLS_MAX_PENDING_HANDSHAKE_RESULT_JOBS, toVisit.handshakeCapacity );
            }
            if ( toVisit.handshakeRecordBufferSize != null )
            {
                config.set( DtlsConfig.DTLS_MAX_DEFERRED_INBOUND_RECORDS_SIZE, toVisit.handshakeRecordBufferSize );
            }
            if ( toVisit.deferredMsgCapacity != null )
            {
                config.set( DtlsConfig.DTLS_MAX_DEFERRED_OUTBOUND_APPLICATION_MESSAGES, toVisit.deferredMsgCapacity );
            }
            if ( toVisit.autoHandshakeTimeout != null )
            {
                config
                    .set(
                        DtlsConfig.DTLS_AUTO_HANDSHAKE_TIMEOUT,
                        TimeUtils.toNanos( toVisit.autoHandshakeTimeout ),
                        TimeUnit.NANOSECONDS
                    );
            }
            config.set( DtlsConfig.DTLS_USE_ANTI_REPLAY_FILTER, toVisit.useAntiReplayFilter );
            if ( toVisit.useExtendedAntiReplayFilterWindow )
            {
                if ( toVisit.antiReplayFilterWindowExtension == null )
                {
                    config.set( DtlsConfig.DTLS_USE_DISABLED_WINDOW_FOR_ANTI_REPLAY_FILTER, -1 );
                }
                else
                {
                    config
                        .set(
                            DtlsConfig.DTLS_USE_DISABLED_WINDOW_FOR_ANTI_REPLAY_FILTER,
                            Math.max( 0, toVisit.antiReplayFilterWindowExtension )
                        );
                }
            }
            else
            {
                config.set( DtlsConfig.DTLS_USE_DISABLED_WINDOW_FOR_ANTI_REPLAY_FILTER, 0 );
            }

        }
        catch ( InternalInvalidDurationException e )
        {
            throw new ConfigException( "dtls-params configuration error", e );
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( DtlsMessageParams toVisit ) throws ConfigException
    {
        if ( toVisit.recordSizeLimit != null )
        {
            config.set( DtlsConfig.DTLS_RECORD_SIZE_LIMIT, toVisit.recordSizeLimit );
        }
        if ( toVisit.maxFragmentLength != null )
        {
            switch ( toVisit.maxFragmentLength )
            {
                case BYTES_512:
                    config.set( DtlsConfig.DTLS_MAX_FRAGMENT_LENGTH, Length.BYTES_512 );
                    break;
                case BYTES_1024:
                    config.set( DtlsConfig.DTLS_MAX_FRAGMENT_LENGTH, Length.BYTES_1024 );
                    break;
                case BYTES_2048:
                    config.set( DtlsConfig.DTLS_MAX_FRAGMENT_LENGTH, Length.BYTES_2048 );
                    break;
                case BYTES_4096:
                    config.set( DtlsConfig.DTLS_MAX_FRAGMENT_LENGTH, Length.BYTES_4096 );
                    break;
                default:
                    break;
            }
        }
        if ( toVisit.maxFragmentedHandshakeMsgLength != null )
        {
            config
                .set(
                    DtlsConfig.DTLS_MAX_FRAGMENTED_HANDSHAKE_MESSAGE_LENGTH,
                    toVisit.maxFragmentedHandshakeMsgLength
                );
        }
        if ( toVisit.multiRecords != null )
        {
            switch ( toVisit.multiRecords )
            {
                case NO:
                    config.set( DtlsConfig.DTLS_USE_MULTI_RECORD_MESSAGES, false );
                    break;
                case YES:
                    config.set( DtlsConfig.DTLS_USE_MULTI_RECORD_MESSAGES, true );
                    break;
                case UNDEFINED:
                default:
                    break;
            }
        }
        if ( toVisit.multiHandshakeMsgRecords != null )
        {
            switch ( toVisit.multiHandshakeMsgRecords )
            {
                case NO:
                    config.set( DtlsConfig.DTLS_USE_MULTI_HANDSHAKE_MESSAGE_RECORDS, false );
                    break;
                case YES:
                    config.set( DtlsConfig.DTLS_USE_MULTI_HANDSHAKE_MESSAGE_RECORDS, true );
                    break;
                case UNDEFINED:
                default:
                    break;
            }
        }
        if ( toVisit.mtu != null )
        {
            config.set( DtlsConfig.DTLS_MAX_TRANSMISSION_UNIT, toVisit.mtu );
        }
        if ( toVisit.mtuLimit != null )
        {
            config.set( DtlsConfig.DTLS_MAX_TRANSMISSION_UNIT_LIMIT, toVisit.mtuLimit );
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( DtlsRetransmissionParams toVisit ) throws ConfigException
    {
        try
        {
            if ( toVisit.initialTimeout != null )
            {
                config
                    .set(
                        DtlsConfig.DTLS_RETRANSMISSION_TIMEOUT,
                        TimeUtils.toNanos( toVisit.initialTimeout ),
                        TimeUnit.NANOSECONDS
                    );
            }
            if ( toVisit.maxTimeout != null )
            {
                config
                    .set(
                        DtlsConfig.DTLS_MAX_RETRANSMISSION_TIMEOUT,
                        TimeUtils.toNanos( toVisit.maxTimeout ),
                        TimeUnit.NANOSECONDS
                    );
            }
            if ( toVisit.timeoutRandomFactor != null )
            {
                config.set( DtlsConfig.DTLS_RETRANSMISSION_INIT_RANDOM, toVisit.timeoutRandomFactor );
            }
            if ( toVisit.timeoutScaleFactor != null )
            {
                config.set( DtlsConfig.DTLS_RETRANSMISSION_TIMEOUT_SCALE, toVisit.timeoutScaleFactor );
            }
            if ( toVisit.additionalEccTimeout != null )
            {
                config
                    .set(
                        DtlsConfig.DTLS_ADDITIONAL_ECC_TIMEOUT,
                        TimeUtils.toNanos( toVisit.additionalEccTimeout ),
                        TimeUnit.NANOSECONDS
                    );
            }
            if ( toVisit.maxRetransmissions != null )
            {
                config.set( DtlsConfig.DTLS_MAX_RETRANSMISSIONS, toVisit.maxRetransmissions );
            }
            if ( toVisit.backoffThreshold != null )
            {
                config.set( DtlsConfig.DTLS_RETRANSMISSION_BACKOFF, toVisit.backoffThreshold );
            }
            config.set( DtlsConfig.DTLS_USE_EARLY_STOP_RETRANSMISSION, toVisit.earlyStop );
        }
        catch ( InternalInvalidDurationException e )
        {
            throw new ConfigException( "dtls-retransmission-params configuration error", e );
        }
    }

    /**
     * Visit configuration.
     * @throws ConfigException 
     */
    @Override
    public void visit( DatagramFilter toVisit ) throws ConfigException
    {
        if ( toVisit.macErrorThreshold <= 0 ) throw new ConfigException(
            "mac-error-filter configuration error: invalid threshold { " + toVisit.macErrorThreshold + " }"
        );
        config.set( DtlsConfig.DTLS_MAC_ERROR_FILTER_THRESHOLD, toVisit.macErrorThreshold );
        try
        {
            Long quitTime= TimeUtils.toNanos( toVisit.quitTime );
            if ( quitTime <= 0 ) throw new ConfigException(
                "mac-error-filter configuration error: invalid quit time { " + quitTime + " }"
            );
            config.set( DtlsConfig.DTLS_MAC_ERROR_FILTER_QUIET_TIME, quitTime, TimeUnit.NANOSECONDS );
        }
        catch ( InternalInvalidDurationException e )
        {
            throw new ConfigException( "mac-error-filter configuration error", e );
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( SocketParams toVisit )
    {
        //reuse address, host and port are configured on endpoint
        if ( toVisit.receiveBuffer != null )
        {
            config.set( UdpConfig.UDP_RECEIVE_BUFFER_SIZE, toVisit.receiveBuffer );
            config.set( DtlsConfig.DTLS_RECEIVE_BUFFER_SIZE, toVisit.receiveBuffer );
        }
        if ( toVisit.sendBuffer != null )
        {
            config.set( UdpConfig.UDP_SEND_BUFFER_SIZE, toVisit.sendBuffer );
            config.set( DtlsConfig.DTLS_SEND_BUFFER_SIZE, toVisit.sendBuffer );
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( NotificationParams toVisit ) throws ConfigException
    {
        try
        {
            //TODO add test
            if ( toVisit.maxObserves != null )
            {
                if ( toVisit.maxObserves > 0 )
                {
                    config.set( CoapConfig.MAX_SERVER_OBSERVES, toVisit.maxObserves );
                }
                else if ( toVisit.maxObserves < 0 )
                {
                    config.set( CoapConfig.MAX_SERVER_OBSERVES, 0 );
                }
                else
                {
                    config.set( CoapConfig.MAX_SERVER_OBSERVES, -1 );
                }
            }
            if ( toVisit.checkIntervalTime != null ) config
                .set(
                    CoapConfig.NOTIFICATION_CHECK_INTERVAL_TIME,
                    TimeUtils.toNanos( toVisit.checkIntervalTime ),
                    TimeUnit.NANOSECONDS
                );
            if ( toVisit.checkIntervalCount != null ) config
                .set( CoapConfig.NOTIFICATION_CHECK_INTERVAL_COUNT, toVisit.checkIntervalCount );
            if ( toVisit.reregistrationBackoff != null ) config
                .set(
                    CoapConfig.NOTIFICATION_REREGISTRATION_BACKOFF,
                    TimeUtils.toNanos( toVisit.reregistrationBackoff ),
                    TimeUnit.NANOSECONDS
                );
        }
        catch ( InternalInvalidDurationException e )
        {
            throw new ConfigException( "notification-params configuration error", e );
        }
    }

    /**
     * Get the Californium configuration that has been collected.
     */
    public Configuration getConfiguration()
    {
        return config;
    }
}
