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
package nl.teslanet.mule.connectors.coap.api.error;


import org.mule.runtime.extension.api.exception.ModuleException;


/**
 * Exception thrown when CoAP response is received that indicates that the request is rejected by the server.
 */
public class ClientErrorResponseException extends ModuleException
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID= 1L;

    public ClientErrorResponseException( String message )
    {
        super( message, Errors.CLIENT_ERROR_RESPONSE );
    }

    public ClientErrorResponseException( Throwable cause )
    {
        super( Errors.CLIENT_ERROR_RESPONSE, cause );
    }

    public ClientErrorResponseException( String message, Throwable cause )
    {
        super( message, Errors.CLIENT_ERROR_RESPONSE, cause );
    }
}
