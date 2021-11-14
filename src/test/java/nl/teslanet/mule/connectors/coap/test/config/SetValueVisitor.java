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


import java.util.concurrent.CopyOnWriteArrayList;

import nl.teslanet.mule.connectors.coap.api.MulticastGroupConfig;
import nl.teslanet.mule.connectors.coap.api.config.BlockwiseParams;
import nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor;
import nl.teslanet.mule.connectors.coap.api.config.SecurityParams;
import nl.teslanet.mule.connectors.coap.api.config.ExchangeParams;
import nl.teslanet.mule.connectors.coap.api.config.LogHealthStatus;
import nl.teslanet.mule.connectors.coap.api.config.MulticastParams;
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
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.GroupedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.MapBasedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.NullMidTracker;


/**
 * Configuration visitor that sets a configuration parameter value
 *
 */
public class SetValueVisitor implements ConfigVisitor
{
    private ConfigParamName configParamName;

    private String value= null;

    public SetValueVisitor( ConfigParamName configParamName, String value )
    {
        this.configParamName= configParamName;
        this.value= value;
    }

    //TODO client and server config support 

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.endpoint.Endpoint)
     */
    @Override
    public void visit( AbstractEndpoint toVisit )
    {
        switch ( configParamName )
        {
            case logCoapMessages:
                toVisit.logCoapMessages= new Boolean( value );
                break;
            case useCongestionControl:
                toVisit.congestionControl= ( Boolean.parseBoolean( value ) ? new Cocoa() : null );
                break;
            case congestionControlAlgorithm:
                if ( value == null )
                {
                    toVisit.congestionControl= null;
                }
                else
                {
                    switch ( value )
                    {
                        case "BasicRto":
                            toVisit.congestionControl= new BasicRto();
                            break;
                        case "Cocoa":
                            toVisit.congestionControl= new Cocoa();
                            break;
                        case "CocoaStrong":
                            toVisit.congestionControl= new CocoaStrong();
                            break;
                        case "LinuxRto":
                            toVisit.congestionControl= new LinuxRto();
                            break;
                        case "PeakhopperRto":
                            toVisit.congestionControl= new PeakhopperRto();
                            break;
                        default:
                            break;
                    }
                    break;
                }
            case logHealthStatus:
                toVisit.logHealthStatus= ( Boolean.parseBoolean( value ) ? new LogHealthStatus() : null );
                break;
            case healthStatusInterval:
                if ( toVisit.logHealthStatus == null )
                {
                    toVisit.logHealthStatus= new LogHealthStatus();
                }
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
                toVisit.preferredBlockSize= new Integer( value );
                break;
            case maxMessageSize:
                toVisit.maxMessageSize= new Integer( value );
                break;
            case maxResourceBodySize:
                toVisit.maxResourceBodySize= new Integer( value );
                break;
            case blockwiseStatusLifetime:
                toVisit.blockwiseStatusLifetime= new Integer( value );
                break;
            default:
                break;
        }
    }


    /**
     * Visit security configuration.
     */
    @Override
    public void visit( SecurityParams toVisit )
    {
        //            case keyStoreLocation:
        //                result= config.getKeyStoreLocation();
        //                break;
        //            case keyStorePassword:
        //                result= config.getKeyStorePassword();
        //                break;
        //            case trustStoreLocation:
        //                result= config.getTrustStoreLocation();
        //                break;
        //            case trustStorePassword:
        //                result= config.getTrustStorePassword;
        //                break;
        //            case privateKeyAlias:
        //                result= config.getPrivateKeyAlias;
        //                break;
        //            case privateKeyPassword:
        //                result= config.getPrivateKeyPassword;
        //                break;
        //            case trustedRootCertificateAlias:
        //                result= config.getTrustedRootCertificateAlias;
        //                break;
        //            case secureSessionTimeout:
        //                result= ( config.getSecureSessionTimeout != null ? config.getSecureSessionTimeout.toString() : null );
        //                break;
        //            case dtlsAutoResumeTimeout:
        //                result= ( config.dtlsAutoResumeTimeout() != null ? config.dtlsAutoResumeTimeout.toString() : null );
        //                break;
        //            case responseMatching:
        //                result= ( config.getResponseMatching != null ? config.getResponseMatching().name() : null );
        //                break;
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
                toVisit.maxActivePeers= new Integer( value );
                break;
            case maxPeerInactivityPeriod:
                toVisit.maxPeerInactivityPeriod= new Integer( value );
                break;
            case ackTimeout:
                toVisit.ackTimeout= new Integer( value );
                break;
            case ackRandomFactor:
                toVisit.ackRandomFactor= new Float( value );
                break;
            case ackTimeoutScale:
                toVisit.ackTimeoutScale= new Float( value );
                break;
            case maxRetransmit:
                toVisit.maxRetransmit= new Integer( value );
                break;
            case exchangeLifetime:
                toVisit.exchangeLifetime= new Long( value );
                break;
            case nonLifetime:
                toVisit.nonLifetime= new Long( value );
                break;
            case nstart:
                toVisit.nstart= new Integer( value );
                break;
            case tokenSizeLimit:
                toVisit.tokenSizeLimit= new Integer( value );
                break;
            case deduplicator:
                if ( value == null )
                {
                    toVisit.deduplicator= null;
                }
                else
                {
                    switch ( value )
                    {
                        case "CropRotation":
                            toVisit.deduplicator= new CropRotation();
                            break;
                        case "MarkAndSweep":
                            toVisit.deduplicator= new MarkAndSweep();
                            break;
                        default:
                            toVisit.deduplicator= null;
                            break;
                    }
                }
                break;
            case cropRotationPeriod:
                if ( toVisit.deduplicator == null )
                {
                    toVisit.deduplicator= new CropRotation();
                }
                break;
            case markAndSweepInterval:
                if ( toVisit.deduplicator == null )
                {
                    toVisit.deduplicator= new MarkAndSweep();
                }
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
                //noop
                break;
            case midTrackerGroups:
                toVisit.midTrackerGroups= new Integer( value );
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
                //noop
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
                //noop
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
                //noop
                break;
            case cropRotationPeriod:
                toVisit.cropRotationPeriod= new Long( value );
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
                //noop
                break;
            case markAndSweepInterval:
                toVisit.markAndSweepInterval= new Long( value );
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
                toVisit.healthStatusInterval= new Integer( value );
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
                //noop
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
                //noop
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
                //noop
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
                //noop
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
                //noop
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
            case useRandomMidStart:
                toVisit.useRandomMidStart= new Boolean( value );
                break;
            case networkStageReceiverThreadCount:
                toVisit.networkStageReceiverThreadCount= new Integer( value );
                break;
            case networkStageSenderThreadCount:
                toVisit.networkStageSenderThreadCount= new Integer( value );
                break;
            case udpConnectorDatagramSize:
                toVisit.udpConnectorDatagramSize= new Integer( value );
                break;
            case udpConnectorReceiveBuffer:
                toVisit.udpConnectorReceiveBuffer= new Integer( value );
                break;
            case udpConnectorSendBuffer:
                toVisit.udpConnectorSendBuffer= new Integer( value );
                break;
            case midTracker:
                if ( value == null )
                {
                    toVisit.midTracker= null;
                }
                else
                {
                    switch ( value )
                    {
                        case "GroupedMidTracker":
                            toVisit.midTracker= new GroupedMidTracker();
                            break;
                        case "MapBasedMidTracker":
                            toVisit.midTracker= new MapBasedMidTracker();
                            break;
                        case "NullMidTracker":
                            toVisit.midTracker= new NullMidTracker();
                            break;
                        default:
                            toVisit.midTracker= null;
                            break;
                    }
                }
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
                toVisit.bindToHost= value;
                break;
            case bindToPort:
                toVisit.bindToPort= new Integer( value );
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
                toVisit.notificationCheckIntervalTime= new Long( value );
                break;
            case notificationCheckIntervalCount:
                toVisit.notificationCheckIntervalCount= new Integer( value );
                break;
            case notificationReregistrationBackoff:
                toVisit.notificationReregistrationBackoff= new Long( value );
                break;
            default:
                break;
        }
    }

    @Override
    public void visit( MulticastParams toVisit )
    {
        switch ( configParamName )
        {
            case multicastGroups:
                if ( toVisit.join == null )
                {
                    toVisit.join= new CopyOnWriteArrayList< MulticastGroupConfig >();
                }
                String[] values= value.split( "[\\[\\]\\s,]+" );
                for ( int i= 1; i < values.length; i++ )
                {
                    String[] fields= values[i].split( "|" );
                    toVisit.join.add( new MulticastGroupConfig( fields[0], ( fields.length > 0 ? fields[1] : null ) ) );
                }
                break;
            default:
                break;
        }
    }
}
