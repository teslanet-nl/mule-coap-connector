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
package nl.teslanet.mule.connectors.coap.api.config;


import nl.teslanet.mule.connectors.coap.api.config.congestion.BasicRto;
import nl.teslanet.mule.connectors.coap.api.config.congestion.Cocoa;
import nl.teslanet.mule.connectors.coap.api.config.congestion.CocoaStrong;
import nl.teslanet.mule.connectors.coap.api.config.congestion.LinuxRto;
import nl.teslanet.mule.connectors.coap.api.config.congestion.PeakhopperRto;
import nl.teslanet.mule.connectors.coap.api.config.deduplication.CropRotation;
import nl.teslanet.mule.connectors.coap.api.config.deduplication.MarkAndSweep;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.DTLSEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.Endpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.MulticastUDPEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPClientEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPServerEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TLSClientEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TLSEndpoint;
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
    default public void visit( BlockwiseParams toVisit )
    {
    };

    /**
     * Visit UdpParams configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( UdpParams toVisit )
    {
    };

    /**
     * Visit SocketParams configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( SocketParams toVisit )
    {
    };

    /**
     * Visit EncryptionParams configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( EncryptionParams toVisit )
    {
    };

    /**
     * Visit ExchangeParams configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( ExchangeParams toVisit )
    {
    };

    /**
     * Visit GroupedMidTracker configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( GroupedMidTracker toVisit )
    {
    };

    /**
     * Visit MapBasedMidTracker configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( MapBasedMidTracker toVisit )
    {
    };

    /**
     * Visit NullMidTracker configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( NullMidTracker toVisit )
    {
    };

    /**
     * Visit CropRotation configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( CropRotation toVisit )
    {
    };

    /**
     * Visit MarkAndSweep configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( MarkAndSweep toVisit )
    {
    };

    /**
     * Visit LogHealthStatus configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( LogHealthStatus toVisit )
    {
    };

    /**
     * Visit NotificationParams configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( NotificationParams toVisit )
    {
    };

    /**
     * Visit BasicRto configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( BasicRto toVisit )
    {
    };

    /**
     * Visit Cocoa configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( Cocoa toVisit )
    {
    };

    /**
     * Visit CocoaStrong configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( CocoaStrong toVisit )
    {
    };

    /**
     * Visit LinuxRto configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( LinuxRto toVisit )
    {
    };

    /**
     * Visit PeakhopperRto configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( PeakhopperRto toVisit )
    {
    };

    /**
     * Visit Endpoint configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( Endpoint toVisit )
    {
    };

    /**
     * Visit UDPEndpoint configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( UDPEndpoint toVisit )
    {
    };

    /**
     * Visit MulticastUDPEndpoint configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( MulticastUDPEndpoint toVisit )
    {
    };

    /**
     * Visit DTLSEndpoint configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( DTLSEndpoint toVisit )
    {
    };

    /**
     * Visit TCPEndpoint configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( TCPEndpoint toVisit )
    {
    };

    /**
     * Visit TcpParams configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( TcpParams toVisit )
    {
    };

    /**
     * Visit TlsParams configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( TlsParams toVisit )
    {
    };

    /**
     * Visit TCPClientEndpoint configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( TCPClientEndpoint toVisit )
    {
    };

    /**
     * Visit TCPServerEndpoint configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( TCPServerEndpoint toVisit )
    {
    };

    /**
     * Visit TLSEndpoint configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( TLSEndpoint toVisit )
    {
    };

    /**
     * Visit TLSClientEndpoint configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( TLSClientEndpoint toVisit )
    {
    };

    /**
     * Visit TLSServerEndpoint configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( TLSServerEndpoint toVisit )
    {
    };

    /**
     * Visit DtlsParams configuration object.
     * @param toVisit the object to visit.
     */
    default public void visit( DtlsParams toVisit )
    {
    };
}
