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
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Set;

import nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor;
import nl.teslanet.mule.connectors.coap.api.config.MulticastParams;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalMulticastGroupException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalNetworkInterfaceException;


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
     * @throws InternalMulticastGroupException 
     */
    public InetAddress[] getMulticastGroups() throws InternalMulticastGroupException
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
                throw new InternalMulticastGroupException( "Multicast group { " + group + " } is invalid.", e );
            }
        }
        return result;
    }

    //TODO rename parameter
    //TODO add feature to specify interface per multicast-group
    /**
     * Get the interface address that the visitor collected from the configuration.
     * @return the interface address.
     * @throws InternalNetworkInterfaceException 
     */
    public NetworkInterface getInterfaceAddress() throws InternalNetworkInterfaceException
    {
        if ( interfaceAddress == null )
        {
            return null;
        }
        try
        {
            return NetworkInterface.getByName( interfaceAddress );
        }
        catch ( SocketException e )
        {
            throw new InternalNetworkInterfaceException( "Network interface address { " + interfaceAddress + " } is invalid.", e );
        }
    }
}
