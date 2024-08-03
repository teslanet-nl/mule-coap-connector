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
package nl.teslanet.mule.connectors.coap.api.config;


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
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractTCPEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractTLSEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.DTLSEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.MulticastUDPEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPClientEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPServerEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TLSClientEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TLSServerEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.UDPEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.GroupedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.MapBasedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.NullMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.options.OptionParams;
import nl.teslanet.mule.connectors.coap.api.config.security.ConnectionId;
import nl.teslanet.mule.connectors.coap.api.config.security.KeyStore;
import nl.teslanet.mule.connectors.coap.api.config.security.PreSharedKeyGroup;
import nl.teslanet.mule.connectors.coap.api.config.security.PreSharedKeyStore;
import nl.teslanet.mule.connectors.coap.api.config.security.PreSharedKeyParams;
import nl.teslanet.mule.connectors.coap.api.config.security.SecurityParams;
import nl.teslanet.mule.connectors.coap.api.config.security.TrustStore;


/**
 * Visitor for Configurations. The visitor interface in api package allows making
 * the concrete visitor internal. 
 */
public interface ConfigVisitor
{
    /**
     * Visit BlockwiseParams configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful. 
     */
    public default void visit( BlockwiseParams toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit UdpParams configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( UdpParams toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit SocketParams configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( SocketParams toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit security configuration.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( SecurityParams toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit key store configuration.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( KeyStore toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit trust store configuration.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( TrustStore toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit ExchangeParams configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
         */
    public default void visit( ExchangeParams toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit GroupedMidTracker configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( GroupedMidTracker toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit MapBasedMidTracker configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( MapBasedMidTracker toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit NullMidTracker configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( NullMidTracker toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit Deduplicator configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( Deduplicator toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit CropRotation configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( CropRotation toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit MarkAndSweep configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( MarkAndSweep toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit PeersMarkAndSweep configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( PeersMarkAndSweep toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit LogHealthStatus configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( LogHealthStatus toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit NotificationParams configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( NotificationParams toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit BasicRto configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( BasicRto toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit Cocoa configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( Cocoa toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit CocoaStrong configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( CocoaStrong toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit LinuxRto configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( LinuxRto toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit PeakhopperRto configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( PeakhopperRto toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit Endpoint configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( AbstractEndpoint toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit UDPEndpoint configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( UDPEndpoint toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit MulticastUDPEndpoint configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( MulticastUDPEndpoint toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit DTLSEndpoint configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( DTLSEndpoint toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit TCPEndpoint configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( AbstractTCPEndpoint toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit TcpParams configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( TcpParams toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit TlsParams configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( TlsParams toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit TCPClientEndpoint configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( TCPClientEndpoint toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit TCPServerEndpoint configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( TCPServerEndpoint toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit TLSEndpoint configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( AbstractTLSEndpoint toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit TLSClientEndpoint configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
    */
    public default void visit( TLSClientEndpoint toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit TLSServerEndpoint configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( TLSServerEndpoint toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit DtlsParams configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( DtlsParams toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit DtlsMessageParams configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( DtlsMessageParams toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit DtlsServerRole configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( DtlsServerRole toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit DtlsClientRole configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( DtlsClientRole toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit DtlsReplayFilter configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( NoReplayFilter toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit DtlsReplayFilter configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( DefaultReplayFilter toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit DtlsReplayFilter configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( ExtendedReplayFilter toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit DtlsClientAndServerRole configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( DtlsClientAndServerRole toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit MulticastParams configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( MulticastParams toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit OptionParams configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( OptionParams toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit DtlsRetransmissionParams configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( DtlsRetransmissionParams toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit DtlsClientParams configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( DtlsClientParams toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit DtlsServerParams configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( DtlsServerParams toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit MacErrorFilter configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( DatagramFilter toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit ConnectionId configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( ConnectionId toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit PreSharedKeyGroup configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( PreSharedKeyParams toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit PreSharedKeyFile configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( PreSharedKeyStore toVisit ) throws ConfigException
    {
        //NOOP
    }

    /**
     * Visit PreSharedKeytGroup configuration object.
     * @param toVisit The object to visit.
     * @throws ConfigException When the visit is not successful.
     */
    public default void visit( PreSharedKeyGroup preSharedKeyGroup ) throws ConfigException
    {
        //NOOP
    }
}
