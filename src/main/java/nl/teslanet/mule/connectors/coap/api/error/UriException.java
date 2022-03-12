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
package nl.teslanet.mule.connectors.coap.api.error;


import org.mule.runtime.extension.api.exception.ModuleException;


/**
 * UriException is thrown when a CoAP uri is invalid, 
 * cannot be constructed or does not resolve.
 */
public class UriException extends ModuleException
{
    /**
     * Serial version ID.
     */
    private static final long serialVersionUID= 1L;

    public UriException( String message )
    {
        super( message, Errors.INVALID_URI );
    }

    public UriException( Throwable cause )
    {
        super( Errors.INVALID_URI, cause );
    }

    public UriException( String message, Throwable cause )
    {
        super( message, Errors.INVALID_URI, cause );
    }
}
