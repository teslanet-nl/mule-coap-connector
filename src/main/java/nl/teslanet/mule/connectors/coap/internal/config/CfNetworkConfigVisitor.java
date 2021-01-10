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
package nl.teslanet.mule.connectors.coap.internal.config;


import org.eclipse.californium.core.network.config.NetworkConfig;

import nl.teslanet.mule.connectors.coap.api.config.BlockwiseParams;
import nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor;
import nl.teslanet.mule.connectors.coap.api.config.DtlsParams;
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
import nl.teslanet.mule.connectors.coap.api.config.deduplication.MarkAndSweep;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.GroupedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.MapBasedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.NullMidTracker;


/**
 * Configuration visitor that collects collects configuration parameters
 * to put into a Californium NetworkConfig.
 *
 */
public class CfNetworkConfigVisitor implements ConfigVisitor
{
    private NetworkConfig config= NetworkConfig.createStandardWithoutFile();

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.BlockwiseParams)
     */
    @Override
    public void visit( BlockwiseParams toVisit )
    {
        if ( toVisit.preferredBlockSize != null ) config.setInt( NetworkConfig.Keys.PREFERRED_BLOCK_SIZE, toVisit.preferredBlockSize ); // 512);
        if ( toVisit.maxMessageSize != null ) config.setInt( NetworkConfig.Keys.MAX_MESSAGE_SIZE, toVisit.maxMessageSize ); // 1024);
        // TODO: only transparent blockwise is supported: maxResourceBodySize > 0
        if ( toVisit.maxResourceBodySize != null && toVisit.maxResourceBodySize > 0 ) config.setInt( NetworkConfig.Keys.MAX_RESOURCE_BODY_SIZE, toVisit.maxResourceBodySize ); // 8192 bytes);
        if ( toVisit.blockwiseStatusLifetime != null ) config.setInt( NetworkConfig.Keys.BLOCKWISE_STATUS_LIFETIME, toVisit.blockwiseStatusLifetime );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.ExchangeParams)
     */
    @Override
    public void visit( ExchangeParams toVisit )
    {
        if ( toVisit.maxActivePeers != null ) config.setInt( NetworkConfig.Keys.MAX_ACTIVE_PEERS, toVisit.maxActivePeers );
        if ( toVisit.maxPeerInactivityPeriod != null ) config.setInt( NetworkConfig.Keys.MAX_PEER_INACTIVITY_PERIOD, toVisit.maxPeerInactivityPeriod );
        if ( toVisit.ackTimeout != null ) config.setInt( NetworkConfig.Keys.ACK_TIMEOUT, toVisit.ackTimeout ); // 2000);
        if ( toVisit.ackRandomFactor != null ) config.setFloat( NetworkConfig.Keys.ACK_RANDOM_FACTOR, toVisit.ackRandomFactor ); // 1.5f); Float.va
        if ( toVisit.ackTimeoutScale != null ) config.setFloat( NetworkConfig.Keys.ACK_TIMEOUT_SCALE, toVisit.ackTimeoutScale ); // 2f);
        if ( toVisit.maxRetransmit != null ) config.setInt( NetworkConfig.Keys.MAX_RETRANSMIT, toVisit.maxRetransmit ); // 4);
        if ( toVisit.exchangeLifetime != null ) config.setLong( NetworkConfig.Keys.EXCHANGE_LIFETIME, toVisit.exchangeLifetime ); // 247 * 1000); // ms
        if ( toVisit.nonLifetime != null ) config.setLong( NetworkConfig.Keys.NON_LIFETIME, toVisit.nonLifetime ); // 145 * 1000); // ms
        if ( toVisit.nstart != null ) config.setInt( NetworkConfig.Keys.NSTART, toVisit.nstart ); // 1);
        if ( toVisit.useRandomMidStart != null ) config.setBoolean( NetworkConfig.Keys.USE_RANDOM_MID_START, toVisit.useRandomMidStart ); // true);
        if ( toVisit.tokenSizeLimit != null ) config.setInt( NetworkConfig.Keys.TOKEN_SIZE_LIMIT, toVisit.tokenSizeLimit ); // 8);
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.midtracker.GroupedMidTracker)
     */
    @Override
    public void visit( GroupedMidTracker toVisit )
    {
        config.setString( NetworkConfig.Keys.MID_TRACKER, "GROUPED" );
        if ( toVisit.midTrackerGroups != null ) config.setInt( NetworkConfig.Keys.MID_TRACKER_GROUPS, toVisit.midTrackerGroups );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.midtracker.MapBasedMidTracker)
     */
    @Override
    public void visit( MapBasedMidTracker toVisit )
    {
        config.setString( NetworkConfig.Keys.MID_TRACKER, "MAPBASED" );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.midtracker.NullMidTracker)
     */
    @Override
    public void visit( NullMidTracker toVisit )
    {
        config.setString( NetworkConfig.Keys.MID_TRACKER, "NULL" );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.deduplication.CropRotation)
     */
    @Override
    public void visit( CropRotation toVisit )
    {
        config.setString( NetworkConfig.Keys.DEDUPLICATOR, NetworkConfig.Keys.DEDUPLICATOR_CROP_ROTATION );
        if ( toVisit.cropRotationPeriod != null ) config.setLong( NetworkConfig.Keys.CROP_ROTATION_PERIOD, toVisit.cropRotationPeriod ); // 2000);
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.deduplication.MarkAndSweep)
     */
    @Override
    public void visit( MarkAndSweep toVisit )
    {
        config.setString( NetworkConfig.Keys.DEDUPLICATOR, NetworkConfig.Keys.DEDUPLICATOR_MARK_AND_SWEEP );
        if ( toVisit.markAndSweepInterval != null ) config.setLong( NetworkConfig.Keys.MARK_AND_SWEEP_INTERVAL, toVisit.markAndSweepInterval ); // 10 * 1000);
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.LogHealthStatus)
     */
    @Override
    public void visit( LogHealthStatus toVisit )
    {
        if ( toVisit.healthStatusInterval != null )
        {
            config.setInt( NetworkConfig.Keys.HEALTH_STATUS_INTERVAL, toVisit.healthStatusInterval );
        }
        else
        {
            config.setInt( NetworkConfig.Keys.HEALTH_STATUS_INTERVAL, 600 );
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.BasicRto)
     */
    @Override
    public void visit( BasicRto basicRto )
    {
        config.setBoolean( NetworkConfig.Keys.USE_CONGESTION_CONTROL, true );
        config.setString( NetworkConfig.Keys.CONGESTION_CONTROL_ALGORITHM, "BasicRto" );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.Cocoa)
     */
    @Override
    public void visit( Cocoa cocoa )
    {
        config.setBoolean( NetworkConfig.Keys.USE_CONGESTION_CONTROL, true );
        config.setString( NetworkConfig.Keys.CONGESTION_CONTROL_ALGORITHM, "Cocoa" );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.CocoaStrong)
     */
    @Override
    public void visit( CocoaStrong cocoaStrong )
    {
        config.setBoolean( NetworkConfig.Keys.USE_CONGESTION_CONTROL, true );
        config.setString( NetworkConfig.Keys.CONGESTION_CONTROL_ALGORITHM, "CocoaStrong" );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.LinuxRto)
     */
    @Override
    public void visit( LinuxRto linuxRto )
    {
        config.setBoolean( NetworkConfig.Keys.USE_CONGESTION_CONTROL, true );
        config.setString( NetworkConfig.Keys.CONGESTION_CONTROL_ALGORITHM, "LinuxRto" );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.PeakhopperRto)
     */
    @Override
    public void visit( PeakhopperRto peakhopperRto )
    {
        config.setBoolean( NetworkConfig.Keys.USE_CONGESTION_CONTROL, true );
        config.setString( NetworkConfig.Keys.CONGESTION_CONTROL_ALGORITHM, "PeakhopperRto" );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.UdpParams)
     */
    @Override
    public void visit( UdpParams toVisit )
    {
        if ( toVisit.networkStageReceiverThreadCount != null ) config.setInt( NetworkConfig.Keys.NETWORK_STAGE_RECEIVER_THREAD_COUNT, toVisit.networkStageReceiverThreadCount ); // WINDOWS ? CORES :  1);
        if ( toVisit.networkStageSenderThreadCount != null ) config.setInt( NetworkConfig.Keys.NETWORK_STAGE_SENDER_THREAD_COUNT, toVisit.networkStageSenderThreadCount ); // WINDOWS ? CORES : 1);
        if ( toVisit.udpConnectorDatagramSize != null ) config.setInt( NetworkConfig.Keys.UDP_CONNECTOR_DATAGRAM_SIZE, toVisit.udpConnectorDatagramSize ); // 2048);
        if ( toVisit.udpConnectorReceiveBuffer != null ) config.setInt( NetworkConfig.Keys.UDP_CONNECTOR_RECEIVE_BUFFER, toVisit.udpConnectorReceiveBuffer ); // UDPConnector.UNDEFINED);
        if ( toVisit.udpConnectorSendBuffer != null ) config.setInt( NetworkConfig.Keys.UDP_CONNECTOR_SEND_BUFFER, toVisit.udpConnectorSendBuffer ); // UDPConnector.UNDEFINED);
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.UdpParams)
     */
    @Override
    public void visit( TcpParams toVisit )
    {
        if ( toVisit.tcpConnectTimeout != null ) config.setInt( NetworkConfig.Keys.TCP_CONNECT_TIMEOUT, toVisit.tcpConnectTimeout ); // 10000 ms
        if ( toVisit.tcpWorkerThreads != null ) config.setInt( NetworkConfig.Keys.TCP_WORKER_THREADS, toVisit.tcpWorkerThreads );
        if ( toVisit.tcpConnectionIdleTimeout != null ) config.setInt( NetworkConfig.Keys.TCP_CONNECTION_IDLE_TIMEOUT, toVisit.tcpConnectTimeout ); // 10 s
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.UdpParams)
     */
    @Override
    public void visit( TlsParams toVisit )
    {
        if ( toVisit.tlsHandshakeTimeout != null ) config.setInt( NetworkConfig.Keys.TLS_HANDSHAKE_TIMEOUT, toVisit.tlsHandshakeTimeout ); // 10000 ms
        //TODO report Cf bug where long is also used
        if ( toVisit.secureSessionTimeout != null ) config.setInt( NetworkConfig.Keys.SECURE_SESSION_TIMEOUT, toVisit.secureSessionTimeout ); //  60 * 60 * 24; // 24h [s]
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.DtlsParams)
     */
    @Override
    public void visit( DtlsParams toVisit )
    {
        if ( toVisit.dtlsAutoResumeTimeout != null ) config.setInt( NetworkConfig.Keys.DTLS_AUTO_RESUME_TIMEOUT, toVisit.dtlsAutoResumeTimeout );
        if ( toVisit.responseMatching != null )
        {
            switch ( toVisit.responseMatching )
            {
                case STRICT:
                    config.setString( NetworkConfig.Keys.RESPONSE_MATCHING, "STRICT" );
                    break;
                case PRINCIPAL:
                    config.setString( NetworkConfig.Keys.RESPONSE_MATCHING, "PRINCIPAL" );
                    break;
                case RELAXED:
                    config.setString( NetworkConfig.Keys.RESPONSE_MATCHING, "RELAXED" );
                    break;
                default:
                    break;
            }
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.SocketParams)
     */
    @Override
    public void visit( SocketParams toVisit )
    {
        //host is configured on endpoint itself
        if ( toVisit.bindToPort != null )
        {
            config.setInt( NetworkConfig.Keys.COAP_PORT, toVisit.bindToPort );
            config.setInt( NetworkConfig.Keys.COAP_SECURE_PORT, toVisit.bindToPort );
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.NotificationParams)
     */
    @Override
    public void visit( NotificationParams toVisit )
    {
        if ( toVisit.notificationCheckIntervalTime != null ) config.setLong( NetworkConfig.Keys.NOTIFICATION_CHECK_INTERVAL_TIME, toVisit.notificationCheckIntervalTime );
        if ( toVisit.notificationCheckIntervalCount != null ) config.setInt( NetworkConfig.Keys.NOTIFICATION_CHECK_INTERVAL_COUNT, toVisit.notificationCheckIntervalCount );
        if ( toVisit.notificationReregistrationBackoff != null )
            config.setLong( NetworkConfig.Keys.NOTIFICATION_REREGISTRATION_BACKOFF, toVisit.notificationReregistrationBackoff );
    }

    //not at endpoint level 
    //if ( toVisit.protocolStageThreadCount != null ) config.setInt( NetworkConfig.Keys.PROTOCOL_STAGE_THREAD_COUNT, toVisit.protocolStageThreadCount ); // CORES);

    /* HTTP config:
     * if ( toVisit.httpPort != null ) config.setInt(NetworkConfig.Keys.HTTP_PORT,
     * toVisit.httpPort ); // 8080); if ( toVisit.httpServerSocketTimeout != null )
     * config.setInt(NetworkConfig.Keys.HTTP_SERVER_SOCKET_TIMEOUT,
     * toVisit.httpServerSocketTimeout ); // 100000); if (
     * toVisit.httpServerSocketBufferSize != null )
     * config.setInt(NetworkConfig.Keys.HTTP_SERVER_SOCKET_BUFFER_SIZE,
     * toVisit.httpServerSocketBufferSize ); // 8192); if (
     * toVisit.httpCacheResponseMaxAge != null )
     * config.setInt(NetworkConfig.Keys.HTTP_CACHE_RESPONSE_MAX_AGE,
     * toVisit.httpCacheResponseMaxAge ); // 86400); if ( toVisit.httpCacheSize != null )
     * config.setInt(NetworkConfig.Keys.HTTP_CACHE_SIZE, toVisit.httpCacheSize ); //
     * 32); 
     */

    /**
     * Get the Californium Network configuration that has been collected.
     */
    public NetworkConfig getNetworkConfig()
    {
        return config;

    }
}
