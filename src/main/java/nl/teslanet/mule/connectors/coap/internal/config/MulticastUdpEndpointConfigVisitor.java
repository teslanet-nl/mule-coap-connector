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
package nl.teslanet.mule.connectors.coap.internal.config;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.UdpMulticastConnector;

import nl.teslanet.mule.connectors.coap.api.MulticastGroupConfig;
import nl.teslanet.mule.connectors.coap.api.config.ConfigException;
import nl.teslanet.mule.connectors.coap.api.config.MulticastParams;
import nl.teslanet.mule.connectors.coap.internal.exceptions.EndpointConstructionException;


/**
 * Configuration visitor that collects multi-cast UDP Endpoint configuration
 *
 */
public class MulticastUdpEndpointConfigVisitor extends EndpointConfigVisitor
{
    /**
     * Error message format.
     */
    private static final String EXCEPTION_FORMAT= "CoAP Endpoint { %s } construction failed. %s: { %s }";

    /**
     * The UDP MulticasConnetor Builder that collects relevant configuration and will be used to build the connector.
     */
    private UdpMulticastConnector.Builder connectorBuilder= new UdpMulticastConnector.Builder();

    /**
     * Network interface to use for multicast.
     */
    private String multiCastNetworkInterfaceConfig= null;

    /**
     * The configured multicast groups.
     */
    private List< MulticastGroupConfig > joinMulticastGroups= null;

    /**
     * The configured disable loopback flag, if any.
     */
    private boolean disableLoopback= false;

    /**
     * The configured interface for outgoing multicast traffic, if any.
     */
    private String outgoingInterface= null;

    /**
     * The configured address indicating the interface for outgoing multicast traffic, if any.
     */
    private String outgoingAddress= null;

    /**
     * Visit the configuration object.
     */
    @Override
    public void visit( MulticastParams toVisit ) throws ConfigException
    {
        super.visit( toVisit );
        joinMulticastGroups= toVisit.join;
        disableLoopback= toVisit.disableLoopback;
        outgoingInterface= toVisit.outgoingMulticastConfig.outgoingInterface;
        outgoingAddress= toVisit.outgoingMulticastConfig.outgoingAddress;
    }

    /**
     * Build the endpoint using the configuration this visitor has collected.
     * @return The Endpoint.
     * @throws EndpointConstructionException When the configuration cannot be used to create an endpoint.
     */
    @Override
    public CoapEndpoint getEndpoint() throws EndpointConstructionException
    {
        connectorBuilder.setLocalAddress( getLocalAddress() );
        if ( outgoingInterface != null )
        {
            try
            {
                connectorBuilder.setOutgoingMulticastInterface( NetworkInterface.getByName( outgoingInterface ) );
            }
            catch ( SocketException e )
            {
                throw new EndpointConstructionException( String.format( EXCEPTION_FORMAT, getEndpointName(), "Outgoing network interface is invalid", outgoingInterface ), e );
            }
        }
        if ( outgoingAddress != null )
        {
            try
            {
                connectorBuilder.setOutgoingMulticastInterface( InetAddress.getByName( outgoingAddress ) );
            }
            catch ( UnknownHostException e )
            {
                throw new EndpointConstructionException( String.format( EXCEPTION_FORMAT, getEndpointName(), "Outgoing network address is invalid", outgoingAddress ), e );
            }
        }
        if ( joinMulticastGroups != null )
        {
            for ( MulticastGroupConfig groupConfig : joinMulticastGroups )
            {
                NetworkInterface networkInterface;
                if ( groupConfig.networkInterface == null )
                {
                    networkInterface= null;
                }
                else
                {
                    try
                    {
                        networkInterface= NetworkInterface.getByName( groupConfig.networkInterface );
                    }
                    catch ( SocketException e )
                    {
                        throw new EndpointConstructionException(
                            String.format( EXCEPTION_FORMAT, getEndpointName(), "Network interface is invalid", multiCastNetworkInterfaceConfig ),
                            e
                        );
                    }
                }
                InetAddress groupAddress;
                try
                {
                    groupAddress= InetAddress.getByName( groupConfig.group );
                }
                catch ( UnknownHostException e )
                {
                    throw new EndpointConstructionException( String.format( EXCEPTION_FORMAT, getEndpointName(), "Multicast group", groupConfig ), e );
                }
                connectorBuilder.addMulticastGroup( groupAddress, networkInterface );
            }
        }
        endpointBuilder.setConfiguration( this.getConfiguration() );
        UdpMulticastConnector connector= connectorBuilder.build();
        connector.setLoopbackMode( disableLoopback );
        endpointBuilder.setConnector( connector );
        return endpointBuilder.build();
    }
}
