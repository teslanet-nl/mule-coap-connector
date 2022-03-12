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
package nl.teslanet.mule.connectors.coap.api.config;


import nl.teslanet.mule.connectors.coap.api.config.congestion.BasicRto;
import nl.teslanet.mule.connectors.coap.api.config.congestion.Cocoa;
import nl.teslanet.mule.connectors.coap.api.config.congestion.CocoaStrong;
import nl.teslanet.mule.connectors.coap.api.config.congestion.LinuxRto;
import nl.teslanet.mule.connectors.coap.api.config.congestion.PeakhopperRto;
import nl.teslanet.mule.connectors.coap.api.config.deduplication.CropRotation;
import nl.teslanet.mule.connectors.coap.api.config.deduplication.MarkAndSweep;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.DTLSEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.MulticastUDPEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPClientEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractTCPEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPServerEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TLSClientEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractTLSEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TLSServerEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.UDPEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.GroupedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.MapBasedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.NullMidTracker;


/**
 * Visitor for Configurations. The visitor interface in api package allows making
 * the concrete visitor internal. 
 */
public interface ConfigVisitor
{
    /**
     * Visit BlockwiseParams configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( BlockwiseParams toVisit )
    {
        //NOOP
    }

    /**
     * Visit UdpParams configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( UdpParams toVisit )
    {
        //NOOP
    }

    /**
     * Visit SocketParams configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( SocketParams toVisit )
    {
        //NOOP
    }

    /**
     * Visit security configuration.
     * @param toVisit the object to visit.
     */
    public default void visit( SecurityParams toVisit )
    {
        //NOOP
    }

    /**
     * Visit ExchangeParams configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( ExchangeParams toVisit )
    {
        //NOOP
    }

    /**
     * Visit GroupedMidTracker configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( GroupedMidTracker toVisit )
    {
        //NOOP
    }

    /**
     * Visit MapBasedMidTracker configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( MapBasedMidTracker toVisit )
    {
        //NOOP
    }

    /**
     * Visit NullMidTracker configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( NullMidTracker toVisit )
    {
        //NOOP
    }

    /**
     * Visit CropRotation configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( CropRotation toVisit )
    {
        //NOOP
    }

    /**
     * Visit MarkAndSweep configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( MarkAndSweep toVisit )
    {
        //NOOP
    }

    /**
     * Visit LogHealthStatus configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( LogHealthStatus toVisit )
    {
        //NOOP
    }

    /**
     * Visit NotificationParams configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( NotificationParams toVisit )
    {
        //NOOP
    }

    /**
     * Visit BasicRto configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( BasicRto toVisit )
    {
        //NOOP
    }

    /**
     * Visit Cocoa configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( Cocoa toVisit )
    {
        //NOOP
    }

    /**
     * Visit CocoaStrong configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( CocoaStrong toVisit )
    {
        //NOOP
    }

    /**
     * Visit LinuxRto configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( LinuxRto toVisit )
    {
        //NOOP
    }

    /**
     * Visit PeakhopperRto configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( PeakhopperRto toVisit )
    {
        //NOOP
    }

    /**
     * Visit Endpoint configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( AbstractEndpoint toVisit )
    {
        //NOOP
    }

    /**
     * Visit UDPEndpoint configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( UDPEndpoint toVisit )
    {
        //NOOP
    }

    /**
     * Visit MulticastUDPEndpoint configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( MulticastUDPEndpoint toVisit )
    {
        //NOOP
    }

    /**
     * Visit DTLSEndpoint configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( DTLSEndpoint toVisit )
    {
        //NOOP
    }

    /**
     * Visit TCPEndpoint configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( AbstractTCPEndpoint toVisit )
    {
        //NOOP
    }

    /**
     * Visit TcpParams configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( TcpParams toVisit )
    {
        //NOOP
    }

    /**
     * Visit TlsParams configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( TlsParams toVisit )
    {
        //NOOP
    }

    /**
     * Visit TCPClientEndpoint configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( TCPClientEndpoint toVisit )
    {
        //NOOP
    }

    /**
     * Visit TCPServerEndpoint configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( TCPServerEndpoint toVisit )
    {
        //NOOP
    }

    /**
     * Visit TLSEndpoint configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( AbstractTLSEndpoint toVisit )
    {
        //NOOP
    }

    /**
     * Visit TLSClientEndpoint configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( TLSClientEndpoint toVisit )
    {
        //NOOP
    }

    /**
     * Visit TLSServerEndpoint configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( TLSServerEndpoint toVisit )
    {
        //NOOP
    }

    /**
     * Visit DtlsParams configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( DtlsParams toVisit )
    {
        //NOOP
    }
    
    /**
     * Visit MulticastParams configuration object.
     * @param toVisit the object to visit.
     */
    public default void visit( MulticastParams toVisit )
    {
        //NOOP
    }
}
