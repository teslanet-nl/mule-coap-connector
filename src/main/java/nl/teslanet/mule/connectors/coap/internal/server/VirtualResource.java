/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2025 (teslanet.nl) Rogier Cobben
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


import java.io.InputStream;

import org.eclipse.californium.core.server.resources.CoapExchange;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;

import nl.teslanet.mule.connectors.coap.api.CoapResponseCode;
import nl.teslanet.mule.connectors.coap.api.attributes.CoapRequestAttributes;


/**
 * The VirtualResource class represents a CoAP resource placeholder for creating resources on the server.
 * A ServedResource object handles all requests from clients on the CoAP resource.
 */
public class VirtualResource extends AbstractResource
{
    /**
     * The callback of the messagesource for Put requests.
     * It is used to hand messages over to the Mule flow that should process the request.
     */
    private SourceCallback< InputStream, CoapRequestAttributes > putCallback= null;

    /**
     * Constructor that creates a VirtualResource object.
     * @param earlyAck Flag indicating that an early acknowledgement must be issued on resource creation.
     */
    public VirtualResource( boolean earlyAck )
    {
        super( "", false );
        this.earlyAck= earlyAck;
    }

    /**
     * Override default handler of Cf.
     */
    @Override
    public void handlePUT( CoapExchange exchange )
    {
        handleRequest( putCallback, exchange, CoapResponseCode.CREATED );
    }

    /**
     * set the Mule callback for this resource for Put requests.
     */
    @Override
    public void setPutCallback( SourceCallback< InputStream, CoapRequestAttributes > sourceCallback )
    {
        putCallback= sourceCallback;
    }

    /**
     * Get the Mule MessageSource callback for Put requests.
     * @return the callback
     */
    @Override
    public SourceCallback< InputStream, CoapRequestAttributes > getPutCallback()
    {
        return putCallback;
    }

}
