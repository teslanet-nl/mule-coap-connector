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


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

import nl.teslanet.mule.connectors.coap.api.config.BlockwiseParams;
import nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor;
import nl.teslanet.mule.connectors.coap.api.config.DtlsParams;
import nl.teslanet.mule.connectors.coap.api.config.EncryptionParams;
import nl.teslanet.mule.connectors.coap.api.config.ExchangeParams;
import nl.teslanet.mule.connectors.coap.api.config.IsItUsed;
import nl.teslanet.mule.connectors.coap.api.config.LogHealthStatus;
import nl.teslanet.mule.connectors.coap.api.config.MulticastParams;
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
 * Configuration visitor that collects multi-cast configuration
 *
 */
public class MulticastVisitor implements ConfigVisitor
{
    /**
     * The interface address retrieved from configuration.
     */
    private String interfaceAddress= null;

    /**
     * Multicast groups retrieved from configuration.
     */
    private Set< String > multicastGroups= null;

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.endpoint.Endpoint)
     */
    @Override
    public void visit( Endpoint toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.endpoint.Endpoint)
     */
    @Override
    public void visit( UDPEndpoint toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.endpoint.MulticastUDPEndpoint)
     */
    @Override
    public void visit( MulticastUDPEndpoint multicastUDPEndpoint )
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.endpoint.DTLSEndpoint)
     */
    @Override
    public void visit( DTLSEndpoint toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPEndpoint)
     */
    @Override
    public void visit( TCPEndpoint toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPEndpoint)
     */
    @Override
    public void visit( TCPClientEndpoint toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPEndpoint)
     */
    @Override
    public void visit( TCPServerEndpoint toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPEndpoint)
     */
    @Override
    public void visit( TLSEndpoint toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPEndpoint)
     */
    @Override
    public void visit( TLSClientEndpoint toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPEndpoint)
     */
    @Override
    public void visit( TLSServerEndpoint toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.BlockwiseParams)
     */
    @Override
    public void visit( BlockwiseParams toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.EncryptionParams)
     */
    @Override
    public void visit( EncryptionParams toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.ExchangeParams)
     */
    @Override
    public void visit( ExchangeParams toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.midtracker.GroupedMidTracker)
     */
    @Override
    public void visit( GroupedMidTracker toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.midtracker.MapBasedMidTracker)
     */
    @Override
    public void visit( MapBasedMidTracker toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.midtracker.NullMidTracker)
     */
    @Override
    public void visit( NullMidTracker toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.deduplication.CropRotation)
     */
    @Override
    public void visit( CropRotation toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.deduplication.MarkAndSweep)
     */
    @Override
    public void visit( MarkAndSweep toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.IsItUsed)
     */
    @Override
    public void visit( IsItUsed toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.LogHealthStatus)
     */
    @Override
    public void visit( LogHealthStatus toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.BasicRto)
     */
    @Override
    public void visit( BasicRto basicRto )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.Cocoa)
     */
    @Override
    public void visit( Cocoa cocoa )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.CocoaStrong)
     */
    @Override
    public void visit( CocoaStrong cocoaStrong )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.LinuxRto)
     */
    @Override
    public void visit( LinuxRto linuxRto )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.congestion.PeakhopperRto)
     */
    @Override
    public void visit( PeakhopperRto peakhopperRto )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.UdpParams)
     */
    @Override
    public void visit( UdpParams toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.UdpParams)
     */
    @Override
    public void visit( TcpParams toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.UdpParams)
     */
    @Override
    public void visit( TlsParams toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.DtlsParams)
     */
    @Override
    public void visit( DtlsParams toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.SocketParams)
     */
    @Override
    public void visit( SocketParams toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.NotificationParams)
     */
    @Override
    public void visit( NotificationParams toVisit )
    {
        //noop
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.NotificationParams)
     */
    @Override
    public void visit( MulticastParams toVisit )
    {
        interfaceAddress= toVisit.interfaceAddress;
        multicastGroups= toVisit.multicastGroups;
    }

    /**
     *  Is multicast configured?
     * @return true when Multicast is configured, otherwise false
     */
    public boolean isMulticast()
    {
        return multicastGroups.size() > 0;
    }

    /**
     * Get the multi-cast groups that the visitor collected from the configuration.
     * @return array containing the multi-cast addresses to subscribe to.
     */
    public InetAddress[] getMulticastGroups()
    {
        if ( multicastGroups == null ) return new InetAddress [0];
        InetAddress[] result= new InetAddress [multicastGroups.size()];
        int i= 0;
        for ( String group : multicastGroups )
        {
            try
            {
                result[i++]= InetAddress.getByName( group );
            }
            catch ( UnknownHostException e )
            {
                // TODO improve exception handling
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Get the interface address that the visitor collected from the configuration.
     * @return the interface address.
     */
    public InetAddress getInterfaceAddress()
    {
        if ( interfaceAddress == null )
        {
            return null;
        }
        try
        {
            return InetAddress.getByName( interfaceAddress );
        }
        catch ( UnknownHostException e )
        {
            // TODO improve exception handling
            e.printStackTrace();
            return null;
        }
    }
}
