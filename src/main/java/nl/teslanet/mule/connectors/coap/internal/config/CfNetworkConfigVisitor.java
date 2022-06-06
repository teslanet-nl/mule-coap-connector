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
    private NetworkConfig networkConfig= NetworkConfig.createStandardWithoutFile();

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.BlockwiseParams)
     */
    @Override
    public void visit( BlockwiseParams toVisit )
    {
        if ( toVisit.preferredBlockSize != null ) networkConfig.setInt( NetworkConfig.Keys.PREFERRED_BLOCK_SIZE, toVisit.preferredBlockSize );
        if ( toVisit.maxMessageSize != null ) networkConfig.setInt( NetworkConfig.Keys.MAX_MESSAGE_SIZE, toVisit.maxMessageSize );
        // TODO: only transparent blockwise is supported: maxResourceBodySize > 0
        if ( toVisit.maxResourceBodySize != null && toVisit.maxResourceBodySize > 0 )
            networkConfig.setInt( NetworkConfig.Keys.MAX_RESOURCE_BODY_SIZE, toVisit.maxResourceBodySize );
        if ( toVisit.blockwiseStatusLifetime != null ) networkConfig.setInt( NetworkConfig.Keys.BLOCKWISE_STATUS_LIFETIME, toVisit.blockwiseStatusLifetime );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.ExchangeParams)
     */
    @Override
    public void visit( ExchangeParams toVisit )
    {
        if ( toVisit.maxActivePeers != null ) networkConfig.setInt( NetworkConfig.Keys.MAX_ACTIVE_PEERS, toVisit.maxActivePeers );
        if ( toVisit.maxPeerInactivityPeriod != null ) networkConfig.setInt( NetworkConfig.Keys.MAX_PEER_INACTIVITY_PERIOD, toVisit.maxPeerInactivityPeriod );
        if ( toVisit.ackTimeout != null ) networkConfig.setInt( NetworkConfig.Keys.ACK_TIMEOUT, toVisit.ackTimeout );
        if ( toVisit.ackRandomFactor != null ) networkConfig.setFloat( NetworkConfig.Keys.ACK_RANDOM_FACTOR, toVisit.ackRandomFactor );
        if ( toVisit.ackTimeoutScale != null ) networkConfig.setFloat( NetworkConfig.Keys.ACK_TIMEOUT_SCALE, toVisit.ackTimeoutScale );
        if ( toVisit.maxRetransmit != null ) networkConfig.setInt( NetworkConfig.Keys.MAX_RETRANSMIT, toVisit.maxRetransmit );
        if ( toVisit.exchangeLifetime != null ) networkConfig.setLong( NetworkConfig.Keys.EXCHANGE_LIFETIME, toVisit.exchangeLifetime );
        if ( toVisit.nonLifetime != null ) networkConfig.setLong( NetworkConfig.Keys.NON_LIFETIME, toVisit.nonLifetime );
        if ( toVisit.nstart != null ) networkConfig.setInt( NetworkConfig.Keys.NSTART, toVisit.nstart );
        if ( toVisit.tokenSizeLimit != null ) networkConfig.setInt( NetworkConfig.Keys.TOKEN_SIZE_LIMIT, toVisit.tokenSizeLimit );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.midtracker.GroupedMidTracker)
     */
    @Override
    public void visit( GroupedMidTracker toVisit )
    {
        networkConfig.setString( NetworkConfig.Keys.MID_TRACKER, "GROUPED" );
        if ( toVisit.midTrackerGroups != null ) networkConfig.setInt( NetworkConfig.Keys.MID_TRACKER_GROUPS, toVisit.midTrackerGroups );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.midtracker.MapBasedMidTracker)
     */
    @Override
    public void visit( MapBasedMidTracker toVisit )
    {
        networkConfig.setString( NetworkConfig.Keys.MID_TRACKER, "MAPBASED" );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.midtracker.NullMidTracker)
     */
    @Override
    public void visit( NullMidTracker toVisit )
    {
        networkConfig.setString( NetworkConfig.Keys.MID_TRACKER, "NULL" );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.deduplication.CropRotation)
     */
    @Override
    public void visit( CropRotation toVisit )
    {
        networkConfig.setString( NetworkConfig.Keys.DEDUPLICATOR, NetworkConfig.Keys.DEDUPLICATOR_CROP_ROTATION );
        if ( toVisit.cropRotationPeriod != null ) networkConfig.setLong( NetworkConfig.Keys.CROP_ROTATION_PERIOD, toVisit.cropRotationPeriod );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.deduplication.MarkAndSweep)
     */
    @Override
    public void visit( MarkAndSweep toVisit )
    {
        networkConfig.setString( NetworkConfig.Keys.DEDUPLICATOR, NetworkConfig.Keys.DEDUPLICATOR_MARK_AND_SWEEP );
        if ( toVisit.markAndSweepInterval != null ) networkConfig.setLong( NetworkConfig.Keys.MARK_AND_SWEEP_INTERVAL, toVisit.markAndSweepInterval );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.LogHealthStatus)
     */
    @Override
    public void visit( LogHealthStatus toVisit )
    {
        if ( toVisit.healthStatusInterval != null )
        {
            networkConfig.setInt( NetworkConfig.Keys.HEALTH_STATUS_INTERVAL, toVisit.healthStatusInterval );
        }
        else
        {
            networkConfig.setInt( NetworkConfig.Keys.HEALTH_STATUS_INTERVAL, 600 );
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.BasicRto)
     */
    @Override
    public void visit( BasicRto basicRto )
    {
        networkConfig.setBoolean( NetworkConfig.Keys.USE_CONGESTION_CONTROL, true );
        networkConfig.setString( NetworkConfig.Keys.CONGESTION_CONTROL_ALGORITHM, "BasicRto" );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.Cocoa)
     */
    @Override
    public void visit( Cocoa cocoa )
    {
        networkConfig.setBoolean( NetworkConfig.Keys.USE_CONGESTION_CONTROL, true );
        networkConfig.setString( NetworkConfig.Keys.CONGESTION_CONTROL_ALGORITHM, "Cocoa" );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.CocoaStrong)
     */
    @Override
    public void visit( CocoaStrong cocoaStrong )
    {
        networkConfig.setBoolean( NetworkConfig.Keys.USE_CONGESTION_CONTROL, true );
        networkConfig.setString( NetworkConfig.Keys.CONGESTION_CONTROL_ALGORITHM, "CocoaStrong" );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.LinuxRto)
     */
    @Override
    public void visit( LinuxRto linuxRto )
    {
        networkConfig.setBoolean( NetworkConfig.Keys.USE_CONGESTION_CONTROL, true );
        networkConfig.setString( NetworkConfig.Keys.CONGESTION_CONTROL_ALGORITHM, "LinuxRto" );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.PeakhopperRto)
     */
    @Override
    public void visit( PeakhopperRto peakhopperRto )
    {
        networkConfig.setBoolean( NetworkConfig.Keys.USE_CONGESTION_CONTROL, true );
        networkConfig.setString( NetworkConfig.Keys.CONGESTION_CONTROL_ALGORITHM, "PeakhopperRto" );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.UdpParams)
     */
    @Override
    public void visit( UdpParams toVisit )
    {
        networkConfig.setBoolean( NetworkConfig.Keys.USE_RANDOM_MID_START, toVisit.useRandomMidStart );
        if ( toVisit.receiverThreadCount != null )
            networkConfig.setInt( NetworkConfig.Keys.NETWORK_STAGE_RECEIVER_THREAD_COUNT, toVisit.receiverThreadCount );
        if ( toVisit.senderThreadCount != null ) networkConfig.setInt( NetworkConfig.Keys.NETWORK_STAGE_SENDER_THREAD_COUNT, toVisit.senderThreadCount );
        if ( toVisit.datagramSize != null ) networkConfig.setInt( NetworkConfig.Keys.UDP_CONNECTOR_DATAGRAM_SIZE, toVisit.datagramSize );
        if ( toVisit.receiveBuffer != null ) networkConfig.setInt( NetworkConfig.Keys.UDP_CONNECTOR_RECEIVE_BUFFER, toVisit.receiveBuffer );
        if ( toVisit.sendBuffer != null ) networkConfig.setInt( NetworkConfig.Keys.UDP_CONNECTOR_SEND_BUFFER, toVisit.sendBuffer );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.UdpParams)
     */
    @Override
    public void visit( TcpParams toVisit )
    {
        if ( toVisit.tcpConnectTimeout != null ) networkConfig.setInt( NetworkConfig.Keys.TCP_CONNECT_TIMEOUT, toVisit.tcpConnectTimeout );
        if ( toVisit.tcpWorkerThreads != null ) networkConfig.setInt( NetworkConfig.Keys.TCP_WORKER_THREADS, toVisit.tcpWorkerThreads );
        if ( toVisit.tcpConnectionIdleTimeout != null ) networkConfig.setInt( NetworkConfig.Keys.TCP_CONNECTION_IDLE_TIMEOUT, toVisit.tcpConnectTimeout );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.UdpParams)
     */
    @Override
    public void visit( TlsParams toVisit )
    {
        if ( toVisit.tlsHandshakeTimeout != null ) networkConfig.setInt( NetworkConfig.Keys.TLS_HANDSHAKE_TIMEOUT, toVisit.tlsHandshakeTimeout );
        //TODO report Cf bug where long is used
        if ( toVisit.secureSessionTimeout != null ) networkConfig.setInt( NetworkConfig.Keys.SECURE_SESSION_TIMEOUT, toVisit.secureSessionTimeout );
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.DtlsParams)
     */
    @Override
    public void visit( DtlsParams toVisit )
    {
        if ( toVisit.dtlsAutoResumeTimeout != null ) networkConfig.setInt( NetworkConfig.Keys.DTLS_AUTO_RESUME_TIMEOUT, toVisit.dtlsAutoResumeTimeout );
        if ( toVisit.responseMatching != null )
        {
            switch ( toVisit.responseMatching )
            {
                case STRICT:
                    networkConfig.setString( NetworkConfig.Keys.RESPONSE_MATCHING, "STRICT" );
                    break;
                case PRINCIPAL:
                    networkConfig.setString( NetworkConfig.Keys.RESPONSE_MATCHING, "PRINCIPAL" );
                    break;
                case RELAXED:
                    networkConfig.setString( NetworkConfig.Keys.RESPONSE_MATCHING, "RELAXED" );
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
        //TODO reuse address?
        //host and port are configured on endpoint itself
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.NotificationParams)
     */
    @Override
    public void visit( NotificationParams toVisit )
    {
        if ( toVisit.notificationCheckIntervalTime != null ) networkConfig.setLong( NetworkConfig.Keys.NOTIFICATION_CHECK_INTERVAL_TIME, toVisit.notificationCheckIntervalTime );
        if ( toVisit.notificationCheckIntervalCount != null ) networkConfig.setInt( NetworkConfig.Keys.NOTIFICATION_CHECK_INTERVAL_COUNT, toVisit.notificationCheckIntervalCount );
        if ( toVisit.notificationReregistrationBackoff != null )
            networkConfig.setLong( NetworkConfig.Keys.NOTIFICATION_REREGISTRATION_BACKOFF, toVisit.notificationReregistrationBackoff );
    }

    //not at endpoint level 
    //if ( toVisit.protocolStageThreadCount != null ) networkConfig.setInt( NetworkConfig.Keys.PROTOCOL_STAGE_THREAD_COUNT, toVisit.protocolStageThreadCount ); // CORES);

    /* HTTP networkConfig:
     * if ( toVisit.httpPort != null ) networkConfig.setInt(NetworkConfig.Keys.HTTP_PORT,
     * toVisit.httpPort ); // 8080); if ( toVisit.httpServerSocketTimeout != null )
     * networkConfig.setInt(NetworkConfig.Keys.HTTP_SERVER_SOCKET_TIMEOUT,
     * toVisit.httpServerSocketTimeout ); // 100000); if (
     * toVisit.httpServerSocketBufferSize != null )
     * networkConfig.setInt(NetworkConfig.Keys.HTTP_SERVER_SOCKET_BUFFER_SIZE,
     * toVisit.httpServerSocketBufferSize ); // 8192); if (
     * toVisit.httpCacheResponseMaxAge != null )
     * networkConfig.setInt(NetworkConfig.Keys.HTTP_CACHE_RESPONSE_MAX_AGE,
     * toVisit.httpCacheResponseMaxAge ); // 86400); if ( toVisit.httpCacheSize != null )
     * networkConfig.setInt(NetworkConfig.Keys.HTTP_CACHE_SIZE, toVisit.httpCacheSize ); //
     * 32); 
     */

    /**
     * Get the Californium Network configuration that has been collected.
     */
    public NetworkConfig getNetworkConfig()
    {
        return networkConfig;

    }
}
