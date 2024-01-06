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


import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CopyOnWriteArrayList;

import nl.teslanet.mule.connectors.coap.api.MulticastGroupConfig;
import nl.teslanet.mule.connectors.coap.api.binary.FromNumberConfig;
import nl.teslanet.mule.connectors.coap.api.binary.FromStringConfig;
import nl.teslanet.mule.connectors.coap.api.config.BlockwiseParams;
import nl.teslanet.mule.connectors.coap.api.config.ConfigException;
import nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor;
import nl.teslanet.mule.connectors.coap.api.config.ExchangeParams;
import nl.teslanet.mule.connectors.coap.api.config.LogHealthStatus;
import nl.teslanet.mule.connectors.coap.api.config.MulticastParams;
import nl.teslanet.mule.connectors.coap.api.config.NotificationParams;
import nl.teslanet.mule.connectors.coap.api.config.OptionParams;
import nl.teslanet.mule.connectors.coap.api.config.SocketParams;
import nl.teslanet.mule.connectors.coap.api.config.TriState;
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
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsClientParams.DefaultHandshakeMode;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsClientRole;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsMessageParams;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsMessageParams.FragmentSize;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsParams;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsResponseMatching;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsRetransmissionParams;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsServerParams;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsServerParams.AuthenticationMode;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsServerRole;
import nl.teslanet.mule.connectors.coap.api.config.dtls.ExtendedReplayFilter;
import nl.teslanet.mule.connectors.coap.api.config.dtls.NoReplayFilter;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.DTLSEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.UDPEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.GroupedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.MapBasedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.NullMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.options.OptionType;
import nl.teslanet.mule.connectors.coap.api.config.options.OtherOptionConfig;
import nl.teslanet.mule.connectors.coap.api.config.security.CertificateKeyAlgorithm;
import nl.teslanet.mule.connectors.coap.api.config.security.CertificateKeyAlgorithmName;
import nl.teslanet.mule.connectors.coap.api.config.security.CipherSuite;
import nl.teslanet.mule.connectors.coap.api.config.security.CipherSuiteName;
import nl.teslanet.mule.connectors.coap.api.config.security.ConnectionId;
import nl.teslanet.mule.connectors.coap.api.config.security.Curve;
import nl.teslanet.mule.connectors.coap.api.config.security.ExtendedMasterSecretModeName;
import nl.teslanet.mule.connectors.coap.api.config.security.HashAlgorithmName;
import nl.teslanet.mule.connectors.coap.api.config.security.KeyStore;
import nl.teslanet.mule.connectors.coap.api.config.security.PreSharedKey;
import nl.teslanet.mule.connectors.coap.api.config.security.SecurityParams;
import nl.teslanet.mule.connectors.coap.api.config.security.SignatureAlgorithm;
import nl.teslanet.mule.connectors.coap.api.config.security.SignatureAlgorithmName;
import nl.teslanet.mule.connectors.coap.api.config.security.SupportedGroupName;
import nl.teslanet.mule.connectors.coap.api.config.security.TrustStore;


/**
 * Configuration visitor that sets a configuration parameter value
 *
 */
public class SetValueVisitor implements ConfigVisitor
{
    private ConfigParam param;

    private String value= null;

    public SetValueVisitor( ConfigParam param, String value )
    {
        this.param= param;
        this.value= value;
    }

    //TODO client and server config support 

    /**
     * Visit configuration.
     */
    @Override
    public void visit( UDPEndpoint toVisit )
    {
        switch ( param )
        {
            case responseMatching:
                toVisit.strictResponseMatching= DtlsResponseMatching.STRICT.name().equals( value );
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( DTLSEndpoint toVisit )
    {
        switch ( param )
        {
            case responseMatching:
                toVisit.responseMatching= DtlsResponseMatching.valueOf( value );
                break;
            case DTLS_ROLE:
                switch ( value )
                {
                    case "CLIENT_ONLY":
                        toVisit.dtlsRole= new DtlsClientRole();
                        break;
                    case "SERVER_ONLY":
                        toVisit.dtlsRole= new DtlsServerRole();
                        break;
                    case "BOTH":
                    default:
                        toVisit.dtlsRole= new DtlsClientAndServerRole();
                        break;
                }
                break;
            case DTLS_USE_ANTI_REPLAY_FILTER:
                switch ( value )
                {
                    case "NO":
                        toVisit.replayFilter= new NoReplayFilter();
                        break;
                    case "DEFAULT":
                        toVisit.replayFilter= new DefaultReplayFilter();
                        break;
                    case "EXTENDED":
                        toVisit.replayFilter= new ExtendedReplayFilter();
                        break;
                    default:
                        break;
                }
                break;
            case DTLS_USE_DISABLED_WINDOW_FOR_ANTI_REPLAY_FILTER:
                switch ( value )
                {
                    case "0":
                        toVisit.replayFilter= new DefaultReplayFilter();
                        break;
                    default:
                        toVisit.replayFilter= new ExtendedReplayFilter();
                        break;
                }
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( AbstractEndpoint toVisit )
    {
        switch ( param )
        {
            case logCoapMessages:
                toVisit.logCoapMessages= Boolean.valueOf( value );
                break;
            case logHealthStatus:
                toVisit.logHealthStatus= ( Boolean.parseBoolean( value ) ? new LogHealthStatus() : null );
                break;
            case HEALTH_STATUS_INTERVAL:
                if ( toVisit.logHealthStatus == null )
                {
                    toVisit.logHealthStatus= new LogHealthStatus();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Visit security configuration.
     */
    @Override
    public void visit( BlockwiseParams toVisit )
    {
        switch ( param )
        {
            case preferredBlockSize:
                toVisit.preferredBlockSize= Integer.valueOf( value );
                break;
            case maxMessageSize:
                toVisit.maxMessageSize= Integer.valueOf( value );
                break;
            case maxResourceBodySize:
                toVisit.maxResourceBodySize= Integer.valueOf( value );
                break;
            case blockwiseStatusLifetime:
                toVisit.statusLifetime= value;
                break;
            case blockwiseStatusInterval:
                toVisit.statusInterval= value;
                break;
            case blockwiseStrictBlock1Option:
                toVisit.strictBlock1Option= Boolean.valueOf( value );
                break;
            case blockwiseStrictBlock2Option:
                toVisit.strictBlock2Option= Boolean.valueOf( value );
                break;
            case blockwiseEntityTooLargeAutoFailover:
                toVisit.entityTooLargeFailover= Boolean.valueOf( value );
                break;
            default:
                break;
        }
    }

    /**
     * Visit security configuration.
     */
    @Override
    public void visit( OptionParams toVisit )
    {
        int nr= 65001;
        switch ( param )
        {
            case ENDPOINT_OTHEROPTION_ALIAS:
                toVisit.otherOptionConfigs= new ArrayList< OtherOptionConfig >();
                for ( String item : value.split( "," ) )
                {
                    String alias= item.replaceAll( "[\\[\\]\\s]+", "" );
                    toVisit.otherOptionConfigs.add( new OtherOptionConfig( alias, Integer.valueOf( alias ) ) );
                }
                break;

            case ENDPOINT_OTHEROPTION_NUMBER:
                toVisit.otherOptionConfigs= new ArrayList< OtherOptionConfig >();
                for ( String item : value.split( "," ) )
                {
                    int val= Integer.valueOf( item.replaceAll( "[\\[\\]\\s]+", "" ) );
                    toVisit.otherOptionConfigs.add( new OtherOptionConfig( String.valueOf( val ), val ) );
                }
                break;

            case ENDPOINT_OTHEROPTION_MAXBYTES:
                toVisit.otherOptionConfigs= new ArrayList< OtherOptionConfig >();
                for ( String item : value.split( "," ) )
                {
                    nr++;
                    int val= Integer.valueOf( item.replaceAll( "[\\[\\]\\s]+", "" ) );
                    toVisit.otherOptionConfigs.add( new OtherOptionConfig( String.valueOf( nr ), nr, OptionType.OPAQUE, true, 1, val ) );
                }
                break;
            case ENDPOINT_OTHEROPTION_MINBYTES:
                toVisit.otherOptionConfigs= new ArrayList< OtherOptionConfig >();
                for ( String item : value.split( "," ) )
                {
                    nr++;
                    int val= Integer.valueOf( item.replaceAll( "[\\[\\]\\s]+", "" ) );
                    toVisit.otherOptionConfigs.add( new OtherOptionConfig( String.valueOf( nr ), nr, OptionType.OPAQUE, true, val, Integer.MAX_VALUE ) );
                }
                break;
            case ENDPOINT_OTHEROPTION_SINGLEVALUE:
                toVisit.otherOptionConfigs= new ArrayList< OtherOptionConfig >();
                for ( String item : value.split( "," ) )
                {
                    nr++;
                    boolean val= Boolean.valueOf( item.replaceAll( "[\\[\\]\\s]+", "" ) );
                    toVisit.otherOptionConfigs.add( new OtherOptionConfig( String.valueOf( nr ), nr, OptionType.OPAQUE, val, 0, Integer.MAX_VALUE ) );
                }
                break;
            case ENDPOINT_OTHEROPTION_TYPE:
                toVisit.otherOptionConfigs= new ArrayList< OtherOptionConfig >();
                for ( String item : value.split( "," ) )
                {
                    nr++;
                    OptionType val= OptionType.valueOf( item.replaceAll( "[\\[\\]\\s]+", "" ) );
                    toVisit.otherOptionConfigs.add( new OtherOptionConfig( String.valueOf( nr ), nr, val, true, 0, Integer.MAX_VALUE ) );
                }
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( ExchangeParams toVisit )
    {
        switch ( param )
        {
            case maxActivePeers:
                toVisit.maxActivePeers= Integer.valueOf( value );
                break;
            case maxPeerInactivityPeriod:
                toVisit.maxPeerInactivityPeriod= value;
                break;
            case ackTimeout:
                toVisit.ackTimeout= value;
                break;
            case maxAckTimeout:
                toVisit.maxAckTimeout= value;
                break;
            case ackRandomFactor:
                toVisit.ackRandomFactor= Float.valueOf( value );
                break;
            case ackTimeoutScale:
                toVisit.ackTimeoutScale= Float.valueOf( value );
                break;
            case maxRetransmit:
                toVisit.maxRetransmit= Integer.valueOf( value );
                break;
            case exchangeLifetime:
                toVisit.exchangeLifetime= value;
                break;
            case nonLifetime:
                toVisit.nonLifetime= value;
                break;
            case maxLatency:
                toVisit.maxLatency= value;
                break;
            case maxTransmitWait:
                toVisit.maxTransmitWait= value;
                break;
            case maxServerResponseDelay:
                toVisit.maxResponseDelay= value;
                break;
            case nstart:
                toVisit.nstart= Integer.valueOf( value );
                break;
            case tokenSizeLimit:
                toVisit.tokenSizeLimit= Integer.valueOf( value );
                break;
            case multicastMidBase:
                toVisit.multicastMidBase= Integer.valueOf( value );
                break;
            case leisure:
                toVisit.leisure= value;
                break;
            case strictEmptyMessageFormat:
                toVisit.strictEmptyMessageFormat= Boolean.valueOf( value );
                break;
            case useRandomMidStart:
                toVisit.useRandomMidStart= Boolean.valueOf( value );
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
                        case "PeersMarkAndSweep":
                            toVisit.deduplicator= new PeersMarkAndSweep();
                            break;
                        default:
                            toVisit.deduplicator= null;
                            break;
                    }
                }
                break;
            case deduplicationAutoReplace:
                if ( toVisit.deduplicator == null )
                {
                    toVisit.deduplicator= new PeersMarkAndSweep();
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
            case peersMarkAndSweepMessages:
                if ( toVisit.deduplicator == null )
                {
                    toVisit.deduplicator= new PeersMarkAndSweep();
                }
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
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( GroupedMidTracker toVisit )
    {
        switch ( param )
        {
            case midTracker:
                //noop
                break;
            case midTrackerGroups:
                toVisit.midTrackerGroups= Integer.valueOf( value );
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( MapBasedMidTracker toVisit )
    {
        switch ( param )
        {
            case midTracker:
                //noop
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( NullMidTracker toVisit )
    {
        switch ( param )
        {
            case midTracker:
                //noop
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( Deduplicator toVisit )
    {
        switch ( param )
        {
            case deduplicationAutoReplace:
                toVisit.autoReplace= Boolean.valueOf( value );
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( CropRotation toVisit )
    {
        switch ( param )
        {
            case deduplicator:
                //noop
                break;
            case cropRotationPeriod:
                toVisit.cropRotationPeriod= value;
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( MarkAndSweep toVisit )
    {
        switch ( param )
        {
            case deduplicator:
                //noop
                break;
            case markAndSweepInterval:
                toVisit.markAndSweepInterval= value;
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( PeersMarkAndSweep toVisit )
    {
        switch ( param )
        {
            case deduplicator:
                //noop
                break;
            case peersMarkAndSweepMessages:
                toVisit.maxMessagesPerPeer= Integer.valueOf( value );
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( LogHealthStatus toVisit )
    {
        switch ( param )
        {
            case HEALTH_STATUS_INTERVAL:
                toVisit.healthStatusInterval= value;
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( BasicRto toVisit )
    {
        switch ( param )
        {
            case congestionControlAlgorithm:
                //noop
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( Cocoa toVisit )
    {
        switch ( param )
        {
            case congestionControlAlgorithm:
                //noop
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( CocoaStrong toVisit )
    {
        switch ( param )
        {
            case congestionControlAlgorithm:
                //noop
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( LinuxRto toVisit )
    {
        switch ( param )
        {
            case congestionControlAlgorithm:
                //noop
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( PeakhopperRto toVisit )
    {
        switch ( param )
        {
            case congestionControlAlgorithm:
                //noop
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( UdpParams toVisit )
    {
        switch ( param )
        {
            case UDP_RECEIVER_THREAD_COUNT:
                toVisit.receiverThreadCount= Integer.valueOf( value );
                break;
            case UDP_SENDER_THREAD_COUNT:
                toVisit.senderThreadCount= Integer.valueOf( value );
                break;
            case UDP_DATAGRAM_SIZE:
                toVisit.datagramSize= Integer.valueOf( value );
                break;
            case UDP_CONNECTOR_OUT_CAPACITY:
                toVisit.outCapacity= Integer.valueOf( value );
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( KeyStore toVisit )
    {
        switch ( param )
        {
            case keyStoreLocation:
                toVisit.path= value;
                break;
            case keyStorePassword:
                toVisit.password= value;
                break;
            case privateKeyAlias:
                toVisit.privateKeyAlias= value;
                break;
            case privateKeyPassword:
                toVisit.privateKeyPassword= value;
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( TrustStore toVisit )
    {
        switch ( param )
        {
            case trustStoreLocation:
                toVisit.path= value;
            case trustStorePassword:
                toVisit.password= value;
                break;
            case trustedRootCertificateAlias:
                toVisit.rootCertificateAlias= value;
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( SecurityParams toVisit )
    {
        switch ( param )
        {
            case pskHost:
                toVisit.preSharedKeys= new CopyOnWriteArraySet<>();
                for ( String item : value.split( "," ) )
                {
                    String host= item.replaceAll( "[\\[\\]\\s]+", "" );
                    toVisit.preSharedKeys.add( new PreSharedKey( host + "identity", new FromStringConfig( host ), host, 5684 ) );
                }
                break;
            case pskPort:
                toVisit.preSharedKeys= new CopyOnWriteArraySet<>();
                for ( String item : value.split( "," ) )
                {
                    Integer port= Integer.valueOf( item.replaceAll( "[\\[\\]\\s]+", "" ) );
                    toVisit.preSharedKeys.add( new PreSharedKey( "identity" + port, new FromNumberConfig( Long.valueOf( port ) ), "host" + port, port ) );
                }
                break;
            case pskIdentity:
                toVisit.preSharedKeys= new CopyOnWriteArraySet<>();
                for ( String item : value.split( "," ) )
                {
                    String identity= item.replaceAll( "[\\[\\]\\s]+", "" );
                    toVisit.preSharedKeys.add( new PreSharedKey( identity, new FromStringConfig( identity ) ) );
                }
                break;
            case pskKey:
                toVisit.preSharedKeys= new CopyOnWriteArraySet<>();
                for ( String item : value.split( "," ) )
                {
                    String key= item.replaceAll( "[\\[\\]\\s]+", "" );
                    toVisit.preSharedKeys.add( new PreSharedKey( key + "identity", new FromStringConfig( key ) ) );
                }
                break;
            case keyStoreLocation:
            case keyStorePassword:
            case privateKeyAlias:
            case privateKeyPassword:
                toVisit.keyStore= new KeyStore();
                break;
            case trustStoreLocation:
            case trustStorePassword:
            case trustedRootCertificateAlias:
                toVisit.trustStore= new TrustStore();
                break;
            case DTLS_RECOMMENDED_CIPHER_SUITES_ONLY:
                toVisit.recommendedCipherSuitesOnly= Boolean.valueOf( value );
                break;
            case DTLS_RECOMMENDED_CURVES_ONLY:
                toVisit.recommendedCurvesOnly= Boolean.valueOf( value );
                break;
            case DTLS_RECOMMENDED_SIGNATURE_AND_HASH_ALGORITHMS_ONLY:
                toVisit.recommendedSignatureAndHashAlgorithmsOnly= Boolean.valueOf( value );
                break;
            case DTLS_PRESELECTED_CIPHER_SUITES:
                toVisit.preselectedCipherSuites= new ArrayList<>();
                for ( String item : value.split( "," ) )
                {
                    toVisit.preselectedCipherSuites.add( new CipherSuite( CipherSuiteName.valueOf( item.replaceAll( "[\\[\\]\\s]+", "" ) ) ) );
                }
                break;
            case DTLS_CIPHER_SUITES:
                toVisit.cipherSuites= new ArrayList<>();
                for ( String item : value.split( "," ) )
                {
                    toVisit.cipherSuites.add( new CipherSuite( CipherSuiteName.valueOf( item.replaceAll( "[\\[\\]\\s]+", "" ) ) ) );
                }
                break;
            case DTLS_CURVES:
                toVisit.curves= new ArrayList<>();
                for ( String item : value.split( "," ) )
                {
                    toVisit.curves.add( new Curve( SupportedGroupName.valueOf( item.replaceAll( "[\\[\\]\\s]+", "" ) ) ) );
                }
                break;
            case DTLS_SIGNATURE_AND_HASH_ALGORITHMS:
                toVisit.signatureAlgorithms= new ArrayList<>();
                for ( String item : value.split( "," ) )
                {
                    String algorithmConfig= item.replaceAll( "[\\[\\]\\s]+", "" );
                    int index= algorithmConfig.indexOf( "with" );
                    if ( index > 0 )
                    {
                        String hash= algorithmConfig.substring( 0, index );
                        String signature= algorithmConfig.substring( index + 4 );

                        toVisit.signatureAlgorithms.add( new SignatureAlgorithm( HashAlgorithmName.valueOf( hash ), SignatureAlgorithmName.valueOf( signature ) ) );
                    }
                }
                break;
            case DTLS_CERTIFICATE_KEY_ALGORITHMS:
                toVisit.certificateKeyAlgorithms= new ArrayList<>();
                for ( String item : value.split( "," ) )
                {
                    toVisit.certificateKeyAlgorithms.add( new CertificateKeyAlgorithm( CertificateKeyAlgorithmName.valueOf( item.replaceAll( "[\\[\\]\\s]+", "" ) ) ) );
                }
                break;
            case DTLS_EXTENDED_MASTER_SECRET_MODE:
                toVisit.extendedMasterSecretMode= ExtendedMasterSecretModeName.valueOf( value );
                break;
            case SUPPORT_CONNECTION_ID:
                if ( Boolean.valueOf( value ) ) toVisit.supportConnectionId= new ConnectionId();
                break;
            case DTLS_CONNECTION_ID_LENGTH:
            case DTLS_UPDATE_ADDRESS_USING_CID_ON_NEWER_RECORDS:
                toVisit.supportConnectionId= new ConnectionId();
                break;
            case DTLS_TRUNCATE_CERTIFICATE_PATH_FOR_VALIDATION:
                toVisit.truncateCertificatePathForValidation= Boolean.valueOf( value );
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( ConnectionId toVisit )
    {
        switch ( param )
        {
            case SUPPORT_CONNECTION_ID:
                toVisit.connectionIdLength= null;
                break;
            case DTLS_CONNECTION_ID_LENGTH:
                toVisit.connectionIdLength= Integer.valueOf( value );
                break;
            case DTLS_UPDATE_ADDRESS_USING_CID_ON_NEWER_RECORDS:
                toVisit.updateAddressOnNewerRecords= Boolean.valueOf( value );
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( DtlsServerParams toVisit )
    {
        switch ( param )
        {
            case DTLS_CLIENT_AUTHENTICATION_MODE:
                toVisit.clientAuthentication= AuthenticationMode.valueOf( value );
                break;
            case DTLS_SERVER_USE_SESSION_ID:
                toVisit.serverUseSessionId= Boolean.valueOf( value );
                break;
            case DTLS_USE_SERVER_NAME_INDICATION:
                toVisit.serverNameIndication= Boolean.valueOf( value );
                break;
            case DTLS_USE_HELLO_VERIFY_REQUEST:
                toVisit.helloVerifyRequest= Boolean.valueOf( value );
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( DtlsClientParams toVisit )
    {
        switch ( param )
        {
            case DTLS_VERIFY_SERVER_CERTIFICATES_SUBJECT:
                toVisit.verifyServerCertificateSubject= Boolean.valueOf( value );
                break;
            case DTLS_DEFAULT_HANDSHAKE_MODE:
                toVisit.defaultHandshakeMode= DefaultHandshakeMode.valueOf( value );
                break;
            case DTLS_TRUNCATE_CLIENT_CERTIFICATE_PATH:
                toVisit.truncateClientCertificatePaths= Boolean.valueOf( value );
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( DtlsParams toVisit )
    {
        switch ( param )
        {
            case DTLS_MAX_CONNECTIONS:
                toVisit.maxConnections= Integer.valueOf( value );
                break;
            case DTLS_STALE_CONNECTION_THRESHOLD:
                toVisit.staleConnectionThreshold= value;
                break;
            case DTLS_UPDATE_ADDRESS_USING_CID_ON_NEWER_RECORDS:
                toVisit.updateAddressOnNewerCidRecords= Boolean.valueOf( value );
                break;
            case DTLS_REMOVE_STALE_DOUBLE_PRINCIPALS:
                toVisit.removeStaleDoublePrincipals= Boolean.valueOf( value );
                break;
            case DTLS_MAX_PENDING_OUTBOUND_JOBS:
                toVisit.outboundMsgCapacity= Integer.valueOf( value );
                break;
            case DTLS_MAX_PENDING_INBOUND_JOBS:
                toVisit.inboundMsgCapacity= Integer.valueOf( value );
                break;
            case DTLS_RECEIVER_THREAD_COUNT:
                toVisit.dtlsReceiverThreadCount= Integer.valueOf( value );
                break;
            case DTLS_CONNECTOR_THREAD_COUNT:
                toVisit.dtlsConnectorThreadCount= Integer.valueOf( value );
                break;
            case DTLS_MAX_PENDING_HANDSHAKE_RESULT_JOBS:
                toVisit.handshakeCapacity= Integer.valueOf( value );
                break;
            case DTLS_MAX_DEFERRED_INBOUND_RECORDS_SIZE:
                toVisit.handshakeRecordBufferSize= Integer.valueOf( value );
                break;
            case DTLS_MAX_DEFERRED_OUTBOUND_APPLICATION_MESSAGES:
                toVisit.deferredMsgCapacity= Integer.valueOf( value );
                break;
            case DTLS_AUTO_HANDSHAKE_TIMEOUT:
                toVisit.autoHandshakeTimeout= value;
                break;
            case DTLS_USE_ANTI_REPLAY_FILTER:
                toVisit.useAntiReplyFilter= Boolean.valueOf( value );
                break;
            case DTLS_USE_DISABLED_WINDOW_FOR_ANTI_REPLAY_FILTER:
                int intValue= Integer.valueOf( value );
                toVisit.useExtendedAntiReplayFilterWindow= Boolean.valueOf( intValue != 0 );
                toVisit.antiReplyFilterWindowExtension= intValue == -1 ? null : intValue;
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( DtlsMessageParams toVisit )
    {
        switch ( param )
        {
            case DTLS_RECORD_SIZE_LIMIT:
                toVisit.recordSizeLimit= Integer.valueOf( value );
                break;
            case DTLS_MAX_FRAGMENT_LENGTH:
                toVisit.maxFragmentLength= FragmentSize.valueOf( value );
                break;
            case DTLS_MAX_FRAGMENTED_HANDSHAKE_MESSAGE_LENGTH:
                toVisit.maxFragmentedHandshakeMsgLength= Integer.valueOf( value );
                break;
            case DTLS_USE_MULTI_RECORD_MESSAGES:
                toVisit.multiRecords= TriState.valueOf( value );
                break;
            case DTLS_USE_MULTI_HANDSHAKE_MESSAGE_RECORDS:
                toVisit.multiHandshakeMsgRecords= TriState.valueOf( value );
                break;
            case DTLS_MAX_TRANSMISSION_UNIT:
                toVisit.mtu= Integer.valueOf( value );
                break;
            case DTLS_MAX_TRANSMISSION_UNIT_LIMIT:
                toVisit.mtuLimit= Integer.valueOf( value );
                break;
            default:
                break;
        }
    }

    /**
    * Visit configuration.
    */
    @Override
    public void visit( DtlsRetransmissionParams toVisit )
    {
        switch ( param )
        {
            case DTLS_RETRANSMISSION_TIMEOUT:
                toVisit.initialTimeout= value;
                break;
            case DTLS_MAX_RETRANSMISSION_TIMEOUT:
                toVisit.maxTimeout= value;
                break;
            case DTLS_RETRANSMISSION_INIT_RANDOM:
                toVisit.timeoutRandomFactor= Float.valueOf( value );
                break;
            case DTLS_RETRANSMISSION_TIMEOUT_SCALE:
                toVisit.timeoutScaleFactor= Float.valueOf( value );
                break;
            case DTLS_ADDITIONAL_ECC_TIMEOUT:
                toVisit.additionalEccTimeout= value;
                break;
            case DTLS_MAX_RETRANSMISSIONS:
                toVisit.maxRetransmissions= Integer.valueOf( value );
                break;
            case DTLS_RETRANSMISSION_BACKOFF:
                toVisit.backoffThreshold= Integer.valueOf( value );
                break;
            case DTLS_USE_EARLY_STOP_RETRANSMISSION:
                toVisit.earlyStop= Boolean.valueOf( value );
                break;
            default:
                break;
        }
    }

    /**
    * Visit configuration.
    */
    @Override
    public void visit( DatagramFilter toVisit )
    {
        switch ( param )
        {
            case DTLS_MAC_ERROR_FILTER_THRESHOLD:
                toVisit.macErrorThreshold= Integer.valueOf( value );
                break;
            case DTLS_MAC_ERROR_FILTER_QUIET_TIME:
                toVisit.quitTime= value;
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    public void visit( NoReplayFilter toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit configuration.
     */
    public void visit( DefaultReplayFilter toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit configuration.
     */
    public void visit( ExtendedReplayFilter toVisit ) throws ConfigException
    {
        switch ( param )
        {
            case DTLS_USE_DISABLED_WINDOW_FOR_ANTI_REPLAY_FILTER:
                toVisit.extendedfilterWindow= Integer.valueOf( value );
                break;
            default:
                break;
        }
    }

    /**
    * Visit configuration.
    */
    @Override
    public void visit( SocketParams toVisit )
    {
        switch ( param )
        {
            case bindToHost:
                toVisit.bindToHost= value;
                break;
            case bindToPort:
                toVisit.bindToPort= Integer.valueOf( value );
                break;
            case bindToSecurePort:
                toVisit.bindToPort= Integer.valueOf( value );
                break;
            case UDP_RECEIVE_BUFFER_SIZE:
            case DTLS_RECEIVE_BUFFER_SIZE:
                toVisit.receiveBuffer= Integer.valueOf( value );
                break;
            case UDP_SEND_BUFFER_SIZE:
            case DTLS_SEND_BUFFER_SIZE:
                toVisit.sendBuffer= Integer.valueOf( value );
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( NotificationParams toVisit )
    {
        switch ( param )
        {
            case MAX_SERVER_OBSERVES:
                toVisit.maxObserves= Integer.valueOf( value );
                break;
            case NOTIFICATION_CHECK_INTERVAL_TIME:
                toVisit.checkIntervalTime= value;
                break;
            case NOTIFICATION_CHECK_INTERVAL_COUNT:
                toVisit.checkIntervalCount= Integer.valueOf( value );
                break;
            case NOTIFICATION_REREGISTRATION_BACKOFF:
                toVisit.reregistrationBackoff= value;
                break;
            default:
                break;
        }
    }

    @Override
    public void visit( MulticastParams toVisit )
    {
        switch ( param )
        {
            case multicastGroups:
                if ( toVisit.join == null )
                {
                    toVisit.join= new CopyOnWriteArrayList< MulticastGroupConfig >();
                }
                String[] values= value.split( ";" );
                for ( int i= 0; i < values.length; i++ )
                {
                    String[] fields= values[i].split( "," );
                    String group= fields[0];
                    String networkIf= ( fields.length > 1 ? fields[1] : null );
                    toVisit.join.add( new MulticastGroupConfig( group, networkIf ) );
                }
                break;
            default:
                break;
        }
    }
}
