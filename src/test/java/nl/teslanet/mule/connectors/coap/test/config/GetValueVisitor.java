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


import nl.teslanet.mule.connectors.coap.api.config.BlockwiseParams;
import nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor;
import nl.teslanet.mule.connectors.coap.api.config.ExchangeParams;
import nl.teslanet.mule.connectors.coap.api.config.LogHealthStatus;
import nl.teslanet.mule.connectors.coap.api.config.NotificationParams;
import nl.teslanet.mule.connectors.coap.api.config.SocketParams;
import nl.teslanet.mule.connectors.coap.api.config.UdpParams;
import nl.teslanet.mule.connectors.coap.api.config.congestion.BasicRto;
import nl.teslanet.mule.connectors.coap.api.config.congestion.Cocoa;
import nl.teslanet.mule.connectors.coap.api.config.congestion.CocoaStrong;
import nl.teslanet.mule.connectors.coap.api.config.congestion.LinuxRto;
import nl.teslanet.mule.connectors.coap.api.config.congestion.PeakhopperRto;
import nl.teslanet.mule.connectors.coap.api.config.deduplication.CropRotation;
import nl.teslanet.mule.connectors.coap.api.config.deduplication.MarkAndSweep;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.Endpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.MulticastUDPEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.GroupedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.MapBasedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.NullMidTracker;


/**
 * Configuration visitor that gets a collects configuration parameter value
 *
 */
public class GetValueVisitor implements ConfigVisitor
{
    private ConfigParamName configParamName;

    private String result= null;

    public GetValueVisitor( ConfigParamName configParamName )
    {
        this.configParamName= configParamName;
    }

    //TODO client and server config support 

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.endpoint.Endpoint)
     */
    @Override
    public void visit( Endpoint toVisit )
    {
        switch ( configParamName )
        {
            case logCoapMessages:
                result= Boolean.toString( toVisit.logCoapMessages );
                break;
            case useCongestionControl:
                result= Boolean.toString( toVisit.congestionControl != null );
                break;
            case logHealthStatus:
                result= Boolean.toString( toVisit.logHealthStatus != null );
                break;
            default:
                break;
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.BlockwiseParams)
     */
    @Override
    public void visit( BlockwiseParams toVisit )
    {
        switch ( configParamName )
        {
            case preferredBlockSize:
                result= ( toVisit.preferredBlockSize != null ? toVisit.preferredBlockSize.toString() : null );
                break;
            case maxMessageSize:
                result= ( toVisit.maxMessageSize != null ? toVisit.maxMessageSize.toString() : null );
                break;
            case maxResourceBodySize:
                result= ( toVisit.maxResourceBodySize != null ? toVisit.maxResourceBodySize.toString() : null );
                break;
            case blockwiseStatusLifetime:
                result= ( toVisit.blockwiseStatusLifetime != null ? toVisit.blockwiseStatusLifetime.toString() : null );
                break;
            default:
                break;
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.ExchangeParams)
     */
    @Override
    public void visit( ExchangeParams toVisit )
    {
        switch ( configParamName )
        {
            case maxActivePeers:
                result= ( toVisit.maxActivePeers != null ? toVisit.maxActivePeers.toString() : null );
                break;
            case maxPeerInactivityPeriod:
                result= ( toVisit.maxPeerInactivityPeriod != null ? toVisit.maxPeerInactivityPeriod.toString() : null );
                break;
            case ackTimeout:
                result= ( toVisit.ackTimeout != null ? toVisit.ackTimeout.toString() : null );
                break;
            case ackRandomFactor:
                result= ( toVisit.ackRandomFactor != null ? toVisit.ackRandomFactor.toString() : null );
                break;
            case ackTimeoutScale:
                result= ( toVisit.ackTimeoutScale != null ? toVisit.ackTimeoutScale.toString() : null );
                break;
            case maxRetransmit:
                result= ( toVisit.maxRetransmit != null ? toVisit.maxRetransmit.toString() : null );
                break;
            case exchangeLifetime:
                result= ( toVisit.exchangeLifetime != null ? toVisit.exchangeLifetime.toString() : null );
                break;
            case nonLifetime:
                result= ( toVisit.nonLifetime != null ? toVisit.nonLifetime.toString() : null );
                break;
            case nstart:
                result= ( toVisit.nstart != null ? toVisit.nstart.toString() : null );
                break;
            case useRandomMidStart:
                result= ( toVisit.useRandomMidStart != null ? toVisit.useRandomMidStart.toString() : null );
                break;
            case tokenSizeLimit:
                result= ( toVisit.tokenSizeLimit != null ? toVisit.tokenSizeLimit.toString() : null );
                break;
            default:
                break;
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.midtracker.GroupedMidTracker)
     */
    @Override
    public void visit( GroupedMidTracker toVisit )
    {
        switch ( configParamName )
        {
            case midTracker:
                result= toVisit.name();
                break;
            case midTrackerGroups:
                result= ( toVisit.midTrackerGroups != null ? toVisit.midTrackerGroups.toString() : null );
                break;
            default:
                break;
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.midtracker.MapBasedMidTracker)
     */
    @Override
    public void visit( MapBasedMidTracker toVisit )
    {
        switch ( configParamName )
        {
            case midTracker:
                result= toVisit.name();
                break;
            default:
                break;
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.midtracker.NullMidTracker)
     */
    @Override
    public void visit( NullMidTracker toVisit )
    {
        switch ( configParamName )
        {
            case midTracker:
                result= toVisit.name();
                break;
            default:
                break;
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.deduplication.CropRotation)
     */
    @Override
    public void visit( CropRotation toVisit )
    {
        switch ( configParamName )
        {
            case deduplicator:
                result= toVisit.name();
                break;
            case cropRotationPeriod:
                result= ( toVisit.cropRotationPeriod != null ? toVisit.cropRotationPeriod.toString() : null );
                break;
            default:
                break;
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.deduplication.MarkAndSweep)
     */
    @Override
    public void visit( MarkAndSweep toVisit )
    {
        switch ( configParamName )
        {
            case deduplicator:
                result= toVisit.name();
                break;
            case markAndSweepInterval:
                result= toVisit.markAndSweepInterval.toString();
                break;
            default:
                break;
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.LogHealthStatus)
     */
    @Override
    public void visit( LogHealthStatus toVisit )
    {
        switch ( configParamName )
        {
            case healthStatusInterval:
                result= ( toVisit.healthStatusInterval != null ? toVisit.healthStatusInterval.toString() : null );
                break;
            default:
                break;
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.BasicRto)
     */
    @Override
    public void visit( BasicRto toVisit )
    {
        switch ( configParamName )
        {
            case congestionControlAlgorithm:
                result= toVisit.name();
                break;
            default:
                break;
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.Cocoa)
     */
    @Override
    public void visit( Cocoa toVisit )
    {
        switch ( configParamName )
        {
            case congestionControlAlgorithm:
                result= toVisit.name();
                break;
            default:
                break;
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.CocoaStrong)
     */
    @Override
    public void visit( CocoaStrong toVisit )
    {
        switch ( configParamName )
        {
            case congestionControlAlgorithm:
                result= toVisit.name();
                break;
            default:
                break;
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.LinuxRto)
     */
    @Override
    public void visit( LinuxRto toVisit )
    {
        switch ( configParamName )
        {
            case congestionControlAlgorithm:
                result= toVisit.name();
                break;
            default:
                break;
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.PeakhopperRto)
     */
    @Override
    public void visit( PeakhopperRto toVisit )
    {
        switch ( configParamName )
        {
            case congestionControlAlgorithm:
                result= toVisit.name();
                break;
            default:
                break;
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.UdpParams)
     */
    @Override
    public void visit( UdpParams toVisit )
    {
        switch ( configParamName )
        {
            case networkStageReceiverThreadCount:
                result= ( toVisit.networkStageReceiverThreadCount != null ? toVisit.networkStageReceiverThreadCount.toString() : null );
                break;
            case networkStageSenderThreadCount:
                result= ( toVisit.networkStageSenderThreadCount != null ? toVisit.networkStageSenderThreadCount.toString() : null );
                break;
            case udpConnectorDatagramSize:
                result= ( toVisit.udpConnectorDatagramSize != null ? toVisit.udpConnectorDatagramSize.toString() : null );
                break;
            case udpConnectorReceiveBuffer:
                result= ( toVisit.udpConnectorReceiveBuffer != null ? toVisit.udpConnectorReceiveBuffer.toString() : null );
                break;
            case udpConnectorSendBuffer:
                result= ( toVisit.udpConnectorSendBuffer != null ? toVisit.udpConnectorSendBuffer.toString() : null );
                break;
            default:
                break;
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.SocketParams)
     */
    @Override
    public void visit( SocketParams toVisit )
    {
        switch ( configParamName )
        {
            case bindToHost:
                result= toVisit.bindToHost;
                break;
            case bindToPort:
                result= ( toVisit.bindToPort != null ? toVisit.bindToPort.toString() : null );
                break;
            default:
                break;
        }
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.NotificationParams)
     */
    @Override
    public void visit( NotificationParams toVisit )
    {
        switch ( configParamName )
        {
            case notificationCheckIntervalTime:
                result= ( toVisit.notificationCheckIntervalTime != null ? toVisit.notificationCheckIntervalTime.toString() : null );
                break;
            case notificationCheckIntervalCount:
                result= ( toVisit.notificationCheckIntervalCount != null ? toVisit.notificationCheckIntervalCount.toString() : null );
                break;
            case notificationReregistrationBackoff:
                result= ( toVisit.notificationReregistrationBackoff != null ? toVisit.notificationReregistrationBackoff.toString() : null );
                break;
            default:
                break;
        }
    }

    @Override
    public void visit( MulticastUDPEndpoint toVisit )
    {
        switch ( configParamName )
        {
            case multicastGroups:
                result= ( toVisit.multicastGroups != null ? toVisit.multicastGroups.toString() : null );
                break;
            default:
                break;
        }
    }

    /**
     * Get the value of the configuration parameter.
     */
    public String getValue()
    {
        return result;

    }

}
