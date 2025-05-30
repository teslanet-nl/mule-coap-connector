/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2024 - 2025 (teslanet.nl) Rogier Cobben
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
import org.eclipse.californium.elements.UDPConnector;
import org.eclipse.californium.elements.UdpMulticastConnector;
import org.eclipse.californium.elements.UdpMulticastConnector.Builder;

import nl.teslanet.mule.connectors.coap.api.MulticastReceiverConfig;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.UDPEndpoint;
import nl.teslanet.mule.connectors.coap.internal.exceptions.EndpointConstructionException;


/**
 * Configuration visitor that collects UDP Endpoint configuration
 *
 */
public class UdpEndpointConfigVisitor extends EndpointConfigVisitor
{
    /**
     * Error message format.
     */
    private static final String EXCEPTION_FORMAT= "CoAP Endpoint { %s } construction failed. %s: { %s }";

    /**
     *  Multi-cast receivers.
     */
    private List< MulticastReceiverConfig > multicastReceivers= null;

    /**
     * Visit UDPEndpoint configuration object.
     * @param toVisit The object to visit.
     */
    public void visit( UDPEndpoint toVisit )
    {
        multicastReceivers= toVisit.multicastReceivers;
    }

    /**
     * Build the endpoint.
     * @return The Endpoint.
     * @throws EndpointConstructionException When the configuration cannot be used to create an endpoint.
     */
    @Override
    public CoapEndpoint getEndpoint() throws EndpointConstructionException
    {
        UDPConnector connector= new UDPConnector( getLocalAddress(), getConfiguration() );
        connector.setReuseAddress( isReuseAddress() );
        if ( multicastReceivers != null )
        {
            for ( MulticastReceiverConfig receiver : multicastReceivers )
            {
                Builder receiverBuilder= new UdpMulticastConnector.Builder();
                receiverBuilder.setConfiguration( getConfiguration() );
                receiverBuilder.setMulticastReceiver( true );
                receiverBuilder.setLocalAddress( new InetSocketAddress( receiver.group, receiver.bindToPort ) );
                try
                {
                    receiverBuilder
                        .addMulticastGroup(
                            InetAddress.getByName( receiver.group ),
                            NetworkInterface.getByName( receiver.networkInterface )
                        );
                    connector.addMulticastReceiver( receiverBuilder.build() );
                }
                catch ( UnknownHostException | SocketException e )
                {
                    throw new EndpointConstructionException(
                        String.format( EXCEPTION_FORMAT, getEndpointName(), "Multicast receiver is invalid", receiver ),
                        e
                    );
                }
            }
        }
        endpointBuilder.setConnector( connector );
        return endpointBuilder.build();
    }
}
