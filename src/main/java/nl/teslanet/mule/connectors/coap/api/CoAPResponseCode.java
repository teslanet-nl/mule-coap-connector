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
package nl.teslanet.mule.connectors.coap.api;


/**
 * Available response codes.
 *
 */
public enum CoAPResponseCode
{
    CONTENT,
    CREATED,
    CHANGED,
    DELETED,
    VALID,
    CONTINUE,
    BAD_REQUEST,
    UNAUTHORIZED,
    BAD_OPTION,
    FORBIDDEN,
    NOT_FOUND,
    METHOD_NOT_ALLOWED,
    NOT_ACCEPTABLE,
    REQUEST_ENTITY_INCOMPLETE,
    CONFLICT,
    PRECONDITION_FAILED,
    REQUEST_ENTITY_TOO_LARGE,
    UNSUPPORTED_CONTENT_FORMAT,
    UNPROCESSABLE_ENTITY,
    INTERNAL_SERVER_ERROR,
    NOT_IMPLEMENTED,
    BAD_GATEWAY,
    SERVICE_UNAVAILABLE,
    GATEWAY_TIMEOUT,
    PROXY_NOT_SUPPORTED,
    TOO_MANY_REQUESTS
}
