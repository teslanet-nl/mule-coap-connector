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
package nl.teslanet.mule.connectors.coap.internal.client;


import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.network.Endpoint;


/**
 * Coap Client for use in this connector. 
 * It adds features and specific behaviors that are needed in the connector.
 */
public class ConnectorCoapClient extends CoapClient
{
    /** The endpoint. */
    private Endpoint endpoint;

    /**
     * Sets the endpoint this client is supposed to use.
     * 
     * The endpoint maybe shared among clients. Therefore {@link #shutdown()}
     * doesn't close nor destroy it.
     *
     * @param endpoint The endpoint
     * @return the CoAP client
     */
    @Override
    public CoapClient setEndpoint( Endpoint endpoint )
    {
        synchronized ( this )
        {
            this.endpoint= endpoint;
        }
        return this;
    }

    /**
     * Gets the endpoint this client uses.
     *
     * @return the endpoint
     */
    @Override
    public synchronized Endpoint getEndpoint()
    {
        return endpoint;
    }
}
