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
package nl.teslanet.mule.connectors.coap.internal.client;


import java.net.URI;

import org.eclipse.californium.core.coap.Request;

import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidRequestCodeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalRequestException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUriException;


/**
 * Builder for constructing requests on services that can be virtual or reside behind a proxy.
 * In latter cases the eindpoint address will differ from the service address.
 */
public interface CoapRequestBuilder
{
    /**
     * Build the URI of the resource to request.
     * @return The resource URI.
     * @throws InternalUriException When URI components cannot be assembled to a valid URI.
     */
    public URI buildResourceUri() throws InternalUriException;

    /**
     * Build the URI of the endpoint to address the request.
     * @return The endpoint URI.
     * @throws InternalUriException When URI components cannot be assembled to a valid URI.
     */
    public URI buildEndpointUri() throws InternalUriException;

    /**
     * Build the CoAP request based on given parameters.
     * @return The CoAP request.
     * @throws InternalInvalidRequestCodeException When given requestCode is invalid.
     * @throws InternalUriException When parameters do not assemble to valid URI.
     * @throws InternalRequestException When given payload could not be added the request.
     */
    public Request build() throws InternalInvalidRequestCodeException, InternalUriException, InternalRequestException;
}
