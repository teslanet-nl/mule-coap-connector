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
package nl.teslanet.mule.connectors.coap.internal.client;


import java.net.URI;

import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;

import nl.teslanet.mule.connectors.coap.api.CoapMessageType;
import nl.teslanet.mule.connectors.coap.api.CoapRequestCode;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidRequestCodeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalRequestException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUnkownOptionException;
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
     * @throws InternalUnkownOptionException When an unknown other option alias is given.
     * @throws InternalInvalidOptionValueException When an invalid option value is given.
     */
    public Request build() throws InternalInvalidRequestCodeException,
        InternalUriException,
        InternalRequestException,
        InternalInvalidOptionValueException,
        InternalUnkownOptionException;

    /**
     * Build an empty CoAP request based on given parameters.
     * @return The CoAP request.
     * @throws InternalUriException When parameters do not assemble to valid URI.
     */
    public Request buildEmpty() throws InternalUriException;

    /**
     * Build the message type of the request.
     * @return The message type of the request to build.
     */
    public CoapMessageType buildMessageType();

    /**
     * Build the code of the request.
     * @return The requestCode of the request to build.
     */
    public CoapRequestCode buildRequestCode();

    /**
     * Build the options of the request.
     * @return The options of the request to build.
     */
    public OptionSet buildOptionSet();
}
