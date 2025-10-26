/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2025 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.api.options;


/**
 * Attributes that expresses client interest or disinterest in a response or category of responses.
 * The server MUST send back responses of the classes for which the client has not expressed any disinterest.
 * This way explicit interest in responses is expressed for which no flag has been set. 
 * In multicast scenarios, the parameter is used to make a server respond where it would otherwise remain silent.
 * 
 * @see <a href=
 *      "https://datatracker.ietf.org/doc/html/rfc7967">IETF RFC 7967\n - Constrained Application Protocol (CoAP) Option for No Server Response</a>
 *
 * The class is in fact an interface, but Mule needs a concrete class.
 */
public class RequireResponseAttribute
{
    /**
     * @return the success
     */
    public boolean isSuccess()
    {
        return false;
    }

    /**
     * @return the clientError
     */
    public boolean isClientError()
    {
        return false;
    }

    /**
     * @return the serverError
     */
    public boolean isServerError()
    {
        return false;
    }

    /**
     * @return True if client has no interest in a response at all. 
     */
    public boolean isNone()
    {
        return !( isSuccess() || isClientError() || isServerError() );
    }

    /**
     * @return True if client has interest in all kinds of responses. 
     */
    public boolean isAll()
    {
        return isSuccess() && isClientError() && isServerError();
    }
}
