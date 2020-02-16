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
package nl.teslanet.mule.connectors.coap.api.error;


import org.mule.runtime.extension.api.exception.ModuleException;


/**
 * MalformedUriException is thrown when a CoAP uri is invalid or cannot be constructed
 * from schema, host, port. parth and query parameters to form a valid uri.
 *
 */
public class MalformedUriException extends ModuleException
{
    /**
     * 
     */
    private static final long serialVersionUID= 1L;

    public MalformedUriException( String message )
    {
        super( message, Errors.MALFORMED_URI );
    }

    public MalformedUriException( Throwable cause )
    {
        super( Errors.MALFORMED_URI, cause );
    }

    public MalformedUriException( String message, Throwable cause )
    {
        super( message, Errors.MALFORMED_URI, cause );
    }
}
