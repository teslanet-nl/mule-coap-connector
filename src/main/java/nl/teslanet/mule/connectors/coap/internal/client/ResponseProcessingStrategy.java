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

package nl.teslanet.mule.connectors.coap.internal.client;


import org.eclipse.californium.core.CoapResponse;

import nl.teslanet.mule.connectors.coap.api.CoAPMessageType;
import nl.teslanet.mule.connectors.coap.api.CoAPRequestCode;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResponseException;


/**
 * Interface of a processing strategy that processes a CoAP response. 
 */
interface ResponseProcessingStrategy
{
    /**
     * Process a CoAP response.
     * @param requestUri The uri of the originating request.
     * @param requestCode The CoAP code of the originating request.
     * @param requestType The CoAP type of the originating request.
     * @param response The response received, or null when no response was be received.
     * @throws InternalResponseException When the response could not be interpreted correctly. 
     */
    void process( String requestUri, CoAPMessageType requestType, CoAPRequestCode requestCode, CoapResponse response ) throws InternalResponseException;
}
