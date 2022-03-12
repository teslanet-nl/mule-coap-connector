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


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.UdpMulticastConnector;

import nl.teslanet.mule.connectors.coap.api.MulticastGroupConfig;
import nl.teslanet.mule.connectors.coap.api.config.MulticastParams;
import nl.teslanet.mule.connectors.coap.api.config.SocketParams;
import nl.teslanet.mule.connectors.coap.internal.exceptions.EndpointConstructionException;


/**
 * Configuration visitor that collects multi-cast UDP Endpoint configuration
 *
 */
public class MulticastUdpEndpointConfigVisitor extends EndpointConfigVisitor
{
    /**
     * Error message prefix.
     */
    private static final String ENDPOINT_MSG_PREFIX= "CoAP Endpoint { ";

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

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor#visit(nl.teslanet.mule.connectors.coap.api.config.NotificationParams)
     */
    @Override
    public void visit( MulticastParams toVisit )
    {
        super.visit( toVisit );
        joinMulticastGroups= toVisit.join;
        disableLoopback= toVisit.outgoingMulticastConfig.disableLoopback;
        outgoingInterface= toVisit.outgoingMulticastConfig.outgoingInterface;
        outgoingAddress= toVisit.outgoingMulticastConfig.outgoingAddress;
    }

    /**
     * Visit socket parameters.
     * @param toVisit The object to visit.
     */
    @Override
    public void visit( SocketParams toVisit )
    {
        //Do not call super because UDP endpoint bind configuration is not allowed by Cf when connector is is set.  
        //super.visit( toVisit );
        int port= ( toVisit.bindToPort != null ? toVisit.bindToPort : 0 );

        if ( toVisit.bindToHost != null )
        {
            connectorBuilder.setLocalAddress( new InetSocketAddress( toVisit.bindToHost, port ) );
        }
        else
        {
            connectorBuilder.setLocalAddress( new InetSocketAddress( port ) );
        }
    }

    /**
     * Get the Builder that is ready to build the endpoint.
     * @return The Endpoint Builder.
     * @throws EndpointConstructionException  When the configuration cannot be create an endpoint builder.
     */
    @Override
    public CoapEndpoint.Builder getEndpointBuilder() throws EndpointConstructionException
    {
        if ( outgoingInterface != null )
        {
            try
            {
                connectorBuilder.setOutgoingMulticastInterface( NetworkInterface.getByName( outgoingInterface ) );
            }
            catch ( SocketException e )
            {
                throw new EndpointConstructionException(
                    ENDPOINT_MSG_PREFIX + getEndpointName() + " } construction failed. Outgoing network interface { " + outgoingInterface + " } is invalid.",
                    e
                );
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
                throw new EndpointConstructionException(
                    ENDPOINT_MSG_PREFIX + getEndpointName() + " } construction failed. Outgoing network address { " + outgoingAddress + " } is invalid.",
                    e
                );
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
                            ENDPOINT_MSG_PREFIX + getEndpointName() + " } construction failed. Network interface { " + multiCastNetworkInterfaceConfig + " } is invalid.",
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
                    throw new EndpointConstructionException(
                        ENDPOINT_MSG_PREFIX + getEndpointName() + " } construction failed. Multicast group { " + groupConfig + " } is invalid.",
                        e
                    );
                }
                connectorBuilder.addMulticastGroup( groupAddress, networkInterface );
            }
        }
        endPointBuilder.setNetworkConfig( this.getNetworkConfig() );
        UdpMulticastConnector connector= connectorBuilder.build();
        connector.setLoopbackMode( disableLoopback );
        endPointBuilder.setConnectorWithAutoConfiguration( connector );
        return endPointBuilder;
    }
}
