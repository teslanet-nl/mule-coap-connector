/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2024 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.internal.server;


import java.util.List;

import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.DelivererException;
import org.eclipse.californium.core.server.ServerMessageDeliverer;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.elements.config.Configuration;


/**
 * Message deliverer for served resources.
 * Deliverer is using the given registry to find resources to process CoAP requests.
 */
public class ServedResourceMessageDeliverer extends ServerMessageDeliverer
{
    private ResourceRegistry registry;

    /**
     * @param registry The resource registry.
     * @param config The server configuration.
     */
    public ServedResourceMessageDeliverer( ResourceRegistry registry, Configuration config )
    {
        super( registry.getRoot(), config );
        this.registry= registry;
    }

    /**
     * Searches the registry used to find the resource that should process the coap request.
     * Resources that process PUT requests to create sub-resources are considered.
     * 
     * @param exchange The exchange containing the inbound request including the
     *            path of resource names
     * @return the resource or {@code null}, if not found
     * @throws DelivererException if an other error is detected.
     */
    @Override
    protected Resource findResource( Exchange exchange ) throws DelivererException
    {
        return registry
            .findResource(
                exchange.getRequest().getOptions().getUriPath(),
                exchange.getRequest().getCode() == Code.PUT
            );
    }

    /**
     * This method must not be called. Always throws exception.
     * 
     * @param path the path as list of resource names
     * @return Not applicable.
     * @throws DelivererException Is always thrown.
     */
    @Override
    protected Resource findResource( final List< String > path ) throws DelivererException
    {
        throw new DelivererException( ResponseCode.INTERNAL_SERVER_ERROR, "Wrong attempt to find resource.", true );
    }
}
