/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2021 (teslanet.nl) Rogier Cobben
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


import java.net.InetSocketAddress;

import org.eclipse.californium.core.network.CoapEndpoint;

import nl.teslanet.mule.connectors.coap.api.config.SocketParams;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractEndpoint;
import nl.teslanet.mule.connectors.coap.internal.exceptions.EndpointConstructionException;


/**
 * Configuration visitor that collects multi-cast configuration
 *
 */
public class EndpointConfigVisitor extends CfNetworkConfigVisitor
{
    /**
     * The name of the endpont 
     */
    private String endpointName;

    /**
     * The Endpoint Builder that is used to collect endpoint configuration.
     */
    CoapEndpoint.Builder endPointBuilder= new CoapEndpoint.Builder();

    /**
     * Visit Endpoint configuration object.
     * @param toVisit the object to visit.
     */
    @Override
    public void visit( AbstractEndpoint toVisit )
    {
        super.visit( toVisit );
        endpointName= toVisit.configName;
    }


    /**
     * Visit socket parameters.
     * @param toVisit The object to visit.
     */
    @Override
    public void visit( SocketParams toVisit )
    {
        super.visit( toVisit );
        int port= ( toVisit.bindToPort != null ? toVisit.bindToPort : 0 );

        if ( toVisit.bindToHost != null )
        {
            endPointBuilder.setInetSocketAddress( new InetSocketAddress( toVisit.bindToHost, port ) );
        }
        else
        {
            endPointBuilder.setInetSocketAddress( new InetSocketAddress( port ) );
        }
    }

    /**
     * @return The configured endpoint name.
     */
    public String getEndpointName()
    {
        return endpointName;
    }

    /**
     * Get the Builder that is ready to build the endpoint.
     * @return The Endpoint Builder.
     */
    public CoapEndpoint.Builder getEndpointBuilder() throws EndpointConstructionException
    {
        endPointBuilder.setNetworkConfig( this.getNetworkConfig() );
        return endPointBuilder;
    }
}
