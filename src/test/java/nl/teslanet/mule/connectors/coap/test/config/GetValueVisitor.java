/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2024 (teslanet.nl) Rogier Cobben
 * Contributors:
 *     (teslanet.nl) Rogier Cobben - initial creation
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */
package nl.teslanet.mule.connectors.coap.test.config;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.eclipse.californium.scandium.config.DtlsConfig.DtlsRole;

import nl.teslanet.mule.connectors.coap.api.MulticastGroupConfig;
import nl.teslanet.mule.connectors.coap.api.config.BlockwiseParams;
import nl.teslanet.mule.connectors.coap.api.config.ConfigException;
import nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor;
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
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsResponseMatching;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsRetransmissionParams;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsServerParams;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsServerRole;
import nl.teslanet.mule.connectors.coap.api.config.dtls.ExtendedReplayFilter;
import nl.teslanet.mule.connectors.coap.api.config.dtls.NoReplayFilter;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.DTLSEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.UDPEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.GroupedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.MapBasedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.NullMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.options.OptionParams;
import nl.teslanet.mule.connectors.coap.api.config.security.ConnectionId;
import nl.teslanet.mule.connectors.coap.api.config.security.KeyStore;
import nl.teslanet.mule.connectors.coap.api.config.security.PreSharedKey;
import nl.teslanet.mule.connectors.coap.api.config.security.PreSharedKeyGroup;
import nl.teslanet.mule.connectors.coap.api.config.security.PreSharedKeyStore;
import nl.teslanet.mule.connectors.coap.api.config.security.SecurityParams;
import nl.teslanet.mule.connectors.coap.api.config.security.TrustStore;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;


/**
 * Configuration visitor that gets a collects configuration parameter value
 */
public class GetValueVisitor implements ConfigVisitor
{
    private ConfigParam param;

    private String result= null;

    public GetValueVisitor( ConfigParam param )
    {
        this.param= param;
    }

    // TODO client and server config support

    /**
     * Visit configuration.
     */
    @Override
    public void visit( UDPEndpoint toVisit )
    {
        switch ( param )
        {
            case responseMatching:
                if ( toVisit.strictResponseMatching )
                {
                    result= DtlsResponseMatching.STRICT.name();
                }
                else
                {
                    result= DtlsResponseMatching.RELAXED.name();
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
    public void visit( DTLSEndpoint toVisit )
    {
        switch ( param )
        {
            case responseMatching:
                result= toVisit.responseMatching.name();
                break;
            default:
                break;
        }
    }

    /*
     * Visit configuration.
     */
    @Override
    public void visit( AbstractEndpoint toVisit )
    {
        switch ( param )
        {
            case ENDPOINT_LOGTRAFFIC:
                result= Boolean.toString( toVisit.logTraffic );
                break;
            case logHealthStatus:
                result= Boolean.toString( toVisit.logHealthStatus != null );
                break;
            default:
                break;
        }
    }

    /*
     * Visit configuration.
     */
    @Override
    public void visit( BlockwiseParams toVisit )
    {
        switch ( param )
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
                result= ( toVisit.statusLifetime != null ? toVisit.statusLifetime.toString() : null );
                break;
            case blockwiseStatusInterval:
                result= ( toVisit.statusInterval != null ? toVisit.statusInterval.toString() : null );
                break;
            case blockwiseStrictBlock1Option:
                result= String.valueOf( toVisit.strictBlock1Option );
                break;
            case blockwiseStrictBlock2Option:
                result= String.valueOf( toVisit.strictBlock2Option );
                break;
            case blockwiseEntityTooLargeAutoFailover:
                result= String.valueOf( toVisit.entityTooLargeFailover );
                break;
            default:
                break;
        }
    }

    @Override
    public void visit( OptionParams toVisit )
    {
        if ( toVisit != null )
        {
            switch ( param )
            {
                case ENDPOINT_OTHEROPTION_ALIAS:
                    result= Arrays.toString( toVisit.otherOptionConfigs.stream().map( c -> c.getAlias() ).toArray( String[]::new ) );
                    break;
                case ENDPOINT_OTHEROPTION_NUMBER:
                    result= Arrays.toString( toVisit.otherOptionConfigs.stream().map( c -> c.getNumber() ).map( Object::toString ).toArray( String[]::new ) );
                    break;
                case ENDPOINT_OTHEROPTION_MAXBYTES:
                    result= Arrays.toString( toVisit.otherOptionConfigs.stream().map( c -> c.getMaxBytes() ).map( Object::toString ).toArray( String[]::new ) );
                    break;
                case ENDPOINT_OTHEROPTION_MINBYTES:
                    result= Arrays.toString( toVisit.otherOptionConfigs.stream().map( c -> c.getMinBytes() ).map( Object::toString ).toArray( String[]::new ) );
                    break;
                case ENDPOINT_OTHEROPTION_SINGLEVALUE:
                    result= Arrays.toString( toVisit.otherOptionConfigs.stream().map( c -> c.isSingleValue() ).map( Object::toString ).toArray( String[]::new ) );
                    break;
                case ENDPOINT_OTHEROPTION_TYPE:
                    result= Arrays.toString( toVisit.otherOptionConfigs.stream().map( c -> c.getFormat() ).map( Object::toString ).toArray( String[]::new ) );
                    break;
                default:
                    break;

            }
        }
        else
        {
            result= null;
        }
    }

    @Override
    public void visit( ExchangeParams toVisit )
    {
        switch ( param )
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
            case maxAckTimeout:
                result= ( toVisit.maxAckTimeout != null ? toVisit.maxAckTimeout.toString() : null );
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
            case maxLatency:
                result= ( toVisit.maxLatency != null ? toVisit.maxLatency.toString() : null );
                break;
            case maxTransmitWait:
                result= ( toVisit.maxTransmitWait != null ? toVisit.maxTransmitWait.toString() : null );
                break;
            case maxServerResponseDelay:
                result= ( toVisit.maxResponseDelay != null ? toVisit.maxResponseDelay.toString() : null );
                break;
            case nstart:
                result= ( toVisit.nstart != null ? toVisit.nstart.toString() : null );
                break;
            case tokenSizeLimit:
                result= ( toVisit.tokenSizeLimit != null ? toVisit.tokenSizeLimit.toString() : null );
                break;
            case multicastMidBase:
                result= ( toVisit.multicastMidBase != null ? toVisit.multicastMidBase.toString() : null );
                break;
            case useRandomMidStart:
                result= String.valueOf( toVisit.useRandomMidStart );
                break;
            case leisure:
                result= ( toVisit.leisure != null ? toVisit.leisure.toString() : null );
                break;
            case strictEmptyMessageFormat:
                result= String.valueOf( toVisit.strictEmptyMessageFormat );
                break;
            default:
                break;
        }
    }

    /*
     * Visit configuration.
     */
    @Override
    public void visit( GroupedMidTracker toVisit )
    {
        switch ( param )
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

    /*
     * Visit configuration.
     */
    @Override
    public void visit( MapBasedMidTracker toVisit )
    {
        switch ( param )
        {
            case midTracker:
                result= toVisit.name();
                break;
            default:
                break;
        }
    }

    /*
     * Visit configuration.
     */
    @Override
    public void visit( NullMidTracker toVisit )
    {
        switch ( param )
        {
            case midTracker:
                result= toVisit.name();
                break;
            default:
                break;
        }
    }

    @Override
    public void visit( Deduplicator toVisit )
    {
        switch ( param )
        {
            case deduplicationAutoReplace:
                result= String.valueOf( toVisit.autoReplace );
                break;
            default:
                break;
        }
    }

    /*
     * Visit configuration.
     */
    @Override
    public void visit( CropRotation toVisit )
    {
        switch ( param )
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

    /*
     * Visit configuration.
     */
    @Override
    public void visit( MarkAndSweep toVisit )
    {
        switch ( param )
        {
            case deduplicator:
                result= toVisit.name();
                break;
            case markAndSweepInterval:
                result= ( toVisit.markAndSweepInterval != null ? toVisit.markAndSweepInterval.toString() : null );
                break;
            default:
                break;
        }
    }

    /*
     * Visit configuration.
     */
    @Override
    public void visit( PeersMarkAndSweep toVisit )
    {
        switch ( param )
        {
            case deduplicator:
                result= toVisit.name();
                break;
            case peersMarkAndSweepMessages:
                result= toVisit.maxMessagesPerPeer.toString();
                break;
            default:
                break;
        }
    }

    /*
     * Visit configuration.
     */
    @Override
    public void visit( LogHealthStatus toVisit )
    {
        switch ( param )
        {
            case HEALTH_STATUS_INTERVAL:
                result= ( toVisit.healthStatusInterval != null ? toVisit.healthStatusInterval.toString() : null );
                break;
            default:
                break;
        }
    }

    /*
     * Visit configuration.
     */
    @Override
    public void visit( BasicRto toVisit )
    {
        switch ( param )
        {
            case congestionControlAlgorithm:
                result= toVisit.name();
                break;
            default:
                break;
        }
    }

    /*
     * Visit configuration.
     */
    @Override
    public void visit( Cocoa toVisit )
    {
        switch ( param )
        {
            case congestionControlAlgorithm:
                result= toVisit.name();
                break;
            default:
                break;
        }
    }

    /*
     * Visit configuration.
     */
    @Override
    public void visit( CocoaStrong toVisit )
    {
        switch ( param )
        {
            case congestionControlAlgorithm:
                result= toVisit.name();
                break;
            default:
                break;
        }
    }

    /*
     * Visit configuration.
     */
    @Override
    public void visit( LinuxRto toVisit )
    {
        switch ( param )
        {
            case congestionControlAlgorithm:
                result= toVisit.name();
                break;
            default:
                break;
        }
    }

    /*
     * Visit configuration.
     */
    @Override
    public void visit( PeakhopperRto toVisit )
    {
        switch ( param )
        {
            case congestionControlAlgorithm:
                result= toVisit.name();
                break;
            default:
                break;
        }
    }

    /*
     * Visit configuration.
     */
    @Override
    public void visit( UdpParams toVisit )
    {
        switch ( param )
        {
            case UDP_RECEIVER_THREAD_COUNT:
                result= ( toVisit.receiverThreadCount != null ? toVisit.receiverThreadCount.toString() : null );
                break;
            case UDP_SENDER_THREAD_COUNT:
                result= ( toVisit.senderThreadCount != null ? toVisit.senderThreadCount.toString() : null );
                break;
            case UDP_DATAGRAM_SIZE:
                result= ( toVisit.datagramSize != null ? toVisit.datagramSize.toString() : null );
                break;
            case UDP_CONNECTOR_OUT_CAPACITY:
                result= ( toVisit.outCapacity != null ? toVisit.outCapacity.toString() : null );
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
                result= toVisit.path;
                break;
            case keyStorePassword:
                result= toVisit.password;
                break;
            case privateKeyAlias:
                result= toVisit.privateKeyAlias;
                break;
            case privateKeyPassword:
                result= toVisit.privateKeyPassword;
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
                result= toVisit.path;
            case trustStorePassword:
                result= toVisit.password;
                break;
            case trustedRootCertificateAlias:
                result= toVisit.rootCertificateAlias;
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
            case DTLS_RECOMMENDED_CIPHER_SUITES_ONLY:
                result= Boolean.toString( toVisit.recommendedCipherSuitesOnly );
                break;
            case DTLS_RECOMMENDED_CURVES_ONLY:
                result= Boolean.toString( toVisit.recommendedCurvesOnly );
                break;
            case DTLS_RECOMMENDED_SIGNATURE_AND_HASH_ALGORITHMS_ONLY:
                result= Boolean.toString( toVisit.recommendedSignatureAndHashAlgorithmsOnly );
                break;
            case DTLS_PRESELECTED_CIPHER_SUITES:
                result= ( toVisit.preselectedCipherSuites != null ? toVisit.preselectedCipherSuites.toString() : null );
                break;
            case DTLS_CIPHER_SUITES:
                result= ( toVisit.cipherSuites != null ? toVisit.cipherSuites.toString() : null );
                break;
            case DTLS_CURVES:
                result= ( toVisit.curves != null ? toVisit.curves.toString() : null );
                break;
            case DTLS_SIGNATURE_AND_HASH_ALGORITHMS:
                result= ( toVisit.signatureAlgorithms != null ? toVisit.signatureAlgorithms.toString() : null );
                break;
            case DTLS_CERTIFICATE_KEY_ALGORITHMS:
                result= ( toVisit.certificateKeyAlgorithms != null ? toVisit.certificateKeyAlgorithms.toString() : null );
                break;
            case DTLS_EXTENDED_MASTER_SECRET_MODE:
                result= ( toVisit.extendedMasterSecretMode != null ? toVisit.extendedMasterSecretMode.toString() : null );
                break;
            case DTLS_TRUNCATE_CERTIFICATE_PATH_FOR_VALIDATION:
                result= Boolean.toString( toVisit.truncateCertificatePathForValidation );
                break;
            default:
                break;
        }
    }

    /**
    * Visit configuration.
    */
    @Override
    public void visit( PreSharedKeyGroup toVisit )
    {
        switch ( param )
        {
            case pskHost:
                if ( toVisit.preSharedKeys != null )
                {
                    StringBuilder builder= new StringBuilder( "[" );
                    String prefix= "";
                    for ( PreSharedKey item : toVisit.preSharedKeys )
                    {
                        builder.append( prefix );
                        builder.append( item.getHost() );
                        prefix= ",";
                    }
                    builder.append( ']' );
                    result= builder.toString();
                }
                else
                {
                    result= null;
                }
                break;
            case pskPort:
                if ( toVisit.preSharedKeys != null )
                {
                    StringBuilder builder= new StringBuilder( "[" );
                    String prefix= "";
                    for ( PreSharedKey item : toVisit.preSharedKeys )
                    {
                        builder.append( prefix );
                        builder.append( item.getPort() );
                        prefix= ",";
                    }
                    builder.append( ']' );
                    result= builder.toString();
                }
                else
                {
                    result= null;
                }
                break;
            case pskIdentity:
                if ( toVisit.preSharedKeys != null )
                {
                    StringBuilder builder= new StringBuilder( "[" );
                    String prefix= "";
                    for ( PreSharedKey item : toVisit.preSharedKeys )
                    {
                        builder.append( prefix );
                        builder.append( item.getIdentity() );
                        prefix= ",";
                    }
                    builder.append( ']' );
                    result= builder.toString();
                }
                else
                {
                    result= null;
                }
                break;
            case pskKey:
                if ( toVisit.preSharedKeys != null )
                {
                    StringBuilder builder= new StringBuilder( "[" );
                    String prefix= "";
                    for ( PreSharedKey item : toVisit.preSharedKeys )
                    {
                        builder.append( prefix );
                        try
                        {
                            builder.append( new String( item.getKey().getByteArray(), StandardCharsets.UTF_8 ) );
                        }
                        catch ( OptionValueException e )
                        {
                            builder.append( "NOT_CONVERTABLE" );
                        }
                        prefix= ",";
                    }
                    builder.append( ']' );
                    result= builder.toString();
                }
                else
                {
                    result= null;
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
    public void visit( PreSharedKeyStore toVisit )
    {
        switch ( param )
        {
            case pskKeyFileLocation:
                result= toVisit.path;
                break;
            case pskKeyFilePassword:
                result= toVisit.password;
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
            case DTLS_SUPPORT_CONNECTION_ID:
                result= "true";
                break;
            case DTLS_CONNECTION_ID_LENGTH:
                result= ( toVisit.connectionIdLength != null ? toVisit.connectionIdLength.toString() : null );
                break;
            case DTLS_UPDATE_ADDRESS_USING_CID_ON_NEWER_RECORDS:
                result= String.valueOf( toVisit.updateAddressOnNewerRecords );
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( DtlsServerRole toVisit )
    {
        switch ( param )
        {
            case DTLS_ROLE:
                result= DtlsRole.SERVER_ONLY.name();
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( DtlsClientRole toVisit )
    {
        switch ( param )
        {
            case DTLS_ROLE:
                result= DtlsRole.CLIENT_ONLY.name();
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    @Override
    public void visit( DtlsClientAndServerRole toVisit )
    {
        switch ( param )
        {
            case DTLS_ROLE:
                result= DtlsRole.BOTH.name();
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
                result= ( toVisit.clientAuthentication != null ? toVisit.clientAuthentication.name() : null );
                break;
            case DTLS_SERVER_USE_SESSION_ID:
                result= Boolean.toString( toVisit.serverUseSessionId );
                break;
            case DTLS_USE_SERVER_NAME_INDICATION:
                result= Boolean.toString( toVisit.serverNameIndication );
                break;
            case DTLS_USE_HELLO_VERIFY_REQUEST:
                result= Boolean.toString( toVisit.helloVerifyRequest );
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
                result= Boolean.toString( toVisit.verifyServerCertificateSubject );
                break;
            case DTLS_DEFAULT_HANDSHAKE_MODE:
                result= ( toVisit.defaultHandshakeMode != null ? toVisit.defaultHandshakeMode.name() : null );
                break;
            case DTLS_TRUNCATE_CLIENT_CERTIFICATE_PATH:
                result= Boolean.toString( toVisit.truncateClientCertificatePaths );
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
                result= ( toVisit.maxConnections != null ? toVisit.maxConnections.toString() : null );
                break;
            case DTLS_STALE_CONNECTION_THRESHOLD:
                result= ( toVisit.staleConnectionThreshold != null ? toVisit.staleConnectionThreshold.toString() : null );
                break;
            case DTLS_UPDATE_ADDRESS_USING_CID_ON_NEWER_RECORDS:
                result= String.valueOf( toVisit.updateAddressOnNewerCidRecords );
                break;
            case DTLS_REMOVE_STALE_DOUBLE_PRINCIPALS:
                result= String.valueOf( toVisit.removeStaleDoublePrincipals );
                break;
            case DTLS_MAX_PENDING_OUTBOUND_JOBS:
                result= ( toVisit.outboundMsgCapacity != null ? toVisit.outboundMsgCapacity.toString() : null );
                break;
            case DTLS_MAX_PENDING_INBOUND_JOBS:
                result= ( toVisit.inboundMsgCapacity != null ? toVisit.inboundMsgCapacity.toString() : null );
                break;
            case DTLS_RECEIVER_THREAD_COUNT:
                result= ( toVisit.dtlsReceiverThreadCount != null ? toVisit.dtlsReceiverThreadCount.toString() : null );
                break;
            case DTLS_CONNECTOR_THREAD_COUNT:
                result= ( toVisit.dtlsConnectorThreadCount != null ? toVisit.dtlsConnectorThreadCount.toString() : null );
                break;
            case DTLS_MAX_PENDING_HANDSHAKE_RESULT_JOBS:
                result= ( toVisit.handshakeCapacity != null ? toVisit.handshakeCapacity.toString() : null );
                break;
            case DTLS_MAX_DEFERRED_INBOUND_RECORDS_SIZE:
                result= ( toVisit.handshakeRecordBufferSize != null ? toVisit.handshakeRecordBufferSize.toString() : null );
                break;
            case DTLS_MAX_DEFERRED_OUTBOUND_APPLICATION_MESSAGES:
                result= ( toVisit.deferredMsgCapacity != null ? toVisit.deferredMsgCapacity.toString() : null );
                break;
            case DTLS_AUTO_HANDSHAKE_TIMEOUT:
                result= ( toVisit.autoHandshakeTimeout != null ? toVisit.autoHandshakeTimeout.toString() : null );
                break;
            case DTLS_USE_ANTI_REPLAY_FILTER:
                result= String.valueOf( toVisit.useAntiReplyFilter );
                break;
            case DTLS_USE_DISABLED_WINDOW_FOR_ANTI_REPLAY_FILTER:
                if ( toVisit.useExtendedAntiReplayFilterWindow )
                {
                    result= ( toVisit.antiReplyFilterWindowExtension != null ? toVisit.antiReplyFilterWindowExtension.toString() : "-1" );
                }
                else
                {
                    result= "0";
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
    public void visit( DtlsMessageParams toVisit )
    {
        switch ( param )
        {
            case DTLS_RECORD_SIZE_LIMIT:
                result= ( toVisit.recordSizeLimit != null ? toVisit.recordSizeLimit.toString() : null );
                break;
            case DTLS_MAX_FRAGMENT_LENGTH:
                result= ( toVisit.maxFragmentLength != null ? toVisit.maxFragmentLength.name() : null );
                break;
            case DTLS_MAX_FRAGMENTED_HANDSHAKE_MESSAGE_LENGTH:
                result= ( toVisit.maxFragmentedHandshakeMsgLength != null ? toVisit.maxFragmentedHandshakeMsgLength.toString() : null );
                break;
            case DTLS_USE_MULTI_RECORD_MESSAGES:
                result= toVisit.multiRecords.name();
                break;
            case DTLS_USE_MULTI_HANDSHAKE_MESSAGE_RECORDS:
                result= toVisit.multiHandshakeMsgRecords.name();
                break;
            case DTLS_MAX_TRANSMISSION_UNIT:
                result= ( toVisit.mtu != null ? toVisit.mtu.toString() : null );
                break;
            case DTLS_MAX_TRANSMISSION_UNIT_LIMIT:
                result= ( toVisit.mtuLimit != null ? toVisit.mtuLimit.toString() : null );
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
                result= toVisit.initialTimeout;
                break;
            case DTLS_MAX_RETRANSMISSION_TIMEOUT:
                result= toVisit.maxTimeout;
                break;
            case DTLS_RETRANSMISSION_INIT_RANDOM:
                result= ( toVisit.timeoutRandomFactor != null ? toVisit.timeoutRandomFactor.toString() : null );
                break;
            case DTLS_RETRANSMISSION_TIMEOUT_SCALE:
                result= ( toVisit.timeoutScaleFactor != null ? toVisit.timeoutScaleFactor.toString() : null );
                break;
            case DTLS_ADDITIONAL_ECC_TIMEOUT:
                result= toVisit.additionalEccTimeout;
                break;
            case DTLS_MAX_RETRANSMISSIONS:
                result= ( toVisit.maxRetransmissions != null ? toVisit.maxRetransmissions.toString() : null );
                break;
            case DTLS_RETRANSMISSION_BACKOFF:
                result= ( toVisit.backoffThreshold != null ? toVisit.backoffThreshold.toString() : null );
                break;
            case DTLS_USE_EARLY_STOP_RETRANSMISSION:
                result= String.valueOf( toVisit.earlyStop );
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
                result= toVisit.macErrorThreshold.toString();
                break;
            case DTLS_MAC_ERROR_FILTER_QUIET_TIME:
                result= toVisit.quitTime;
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
        switch ( param )
        {
            case DTLS_USE_ANTI_REPLAY_FILTER:
                result= "NO";
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    public void visit( DefaultReplayFilter toVisit ) throws ConfigException
    {
        switch ( param )
        {
            case DTLS_USE_ANTI_REPLAY_FILTER:
                result= "DEFAULT";
                break;
            case DTLS_USE_DISABLED_WINDOW_FOR_ANTI_REPLAY_FILTER:
                result= "0";
                break;
            default:
                break;
        }
    }

    /**
     * Visit configuration.
     */
    public void visit( ExtendedReplayFilter toVisit ) throws ConfigException
    {
        switch ( param )
        {
            case DTLS_USE_ANTI_REPLAY_FILTER:
                result= "EXTENDED";
                break;
            case DTLS_USE_DISABLED_WINDOW_FOR_ANTI_REPLAY_FILTER:
                result= String.valueOf( toVisit.extendedfilterWindow );;
                break;
            default:
                break;
        }
    }

    /*
     * Visit configuration.
     */
    @Override
    public void visit( SocketParams toVisit )
    {
        switch ( param )
        {
            case ENDPOINT_BINDTOHOST:
                result= toVisit.bindToHost;
                break;
            case ENDPOINT_BINDTOPORT:
                result= ( toVisit.bindToPort != null ? toVisit.bindToPort.toString() : null );
                break;
            case ENDPOINT_BINDTOSECUREPORT:
                result= ( toVisit.bindToPort != null ? toVisit.bindToPort.toString() : null );
                break;
            case ENDPOINT_REUSEADDRESS:
                result= String.valueOf( toVisit.reuseAddress );
                break;
            case UDP_RECEIVE_BUFFER_SIZE:
            case DTLS_RECEIVE_BUFFER_SIZE:
                result= ( toVisit.receiveBuffer != null ? toVisit.receiveBuffer.toString() : null );
                break;
            case UDP_SEND_BUFFER_SIZE:
            case DTLS_SEND_BUFFER_SIZE:
                result= ( toVisit.sendBuffer != null ? toVisit.sendBuffer.toString() : null );
                break;
            default:
                break;
        }
    }

    /*
     * Visit configuration.
     */
    @Override
    public void visit( NotificationParams toVisit )
    {
        switch ( param )
        {
            case MAX_SERVER_OBSERVES:
                result= ( toVisit.maxObserves != null ? toVisit.maxObserves.toString() : null );
                break;
            case NOTIFICATION_CHECK_INTERVAL_TIME:
                result= ( toVisit.checkIntervalTime != null ? toVisit.checkIntervalTime.toString() : null );
                break;
            case NOTIFICATION_CHECK_INTERVAL_COUNT:
                result= ( toVisit.checkIntervalCount != null ? toVisit.checkIntervalCount.toString() : null );
                break;
            case NOTIFICATION_REREGISTRATION_BACKOFF:
                result= ( toVisit.reregistrationBackoff != null ? toVisit.reregistrationBackoff.toString() : null );
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
                StringBuilder builder= new StringBuilder();
                if ( toVisit.join != null )
                {
                    for ( MulticastGroupConfig join : toVisit.join )
                    {
                        builder.append( join.group );
                        if ( join.networkInterface != null )
                        {
                            builder.append( "," );
                            builder.append( join.networkInterface );
                        }
                        builder.append( ";" );
                    }
                }
                result= builder.toString();

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
