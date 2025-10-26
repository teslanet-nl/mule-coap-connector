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


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Request parameters to express interest or disinterest in a response or
 * category of responses. The server MUST send back responses of the classes for
 * which the client has not expressed any disinterest. This way explicit
 * interest in responses is expressed for which no flag has been set. In
 * multicast scenarios, the parameter can be used to make a server respond where
 * it would otherwise remain silent.
 * 
 * @see <a href= "https://datatracker.ietf.org/doc/html/rfc7967">IETF RFC 7967\n
 *      - Constrained Application Protocol (CoAP) Option for No Server
 *      Response</a>
 */
public class RequireResponse
{
    /**
     * If true, the client has no interest in Success responses.
     */
    @Parameter
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "Indicates interest (true) or disinterest (false) in Success responses." )
    private boolean success= false;

    /**
     * If true, the client has no interest in Client-Error responses.
     */
    @Parameter
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "Indicates interest (true) or disinterest (false) in Client-Error responses." )
    private boolean clientError= false;

    /**
     * If true, the client has no interest in Server-Error responses.
     */
    @Parameter
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "Indicates interest (true) or disinterest (false) in Server Error responses." )
    private boolean serverError= false;

    /**
     * @return the success
     */
    public boolean isSuccess()
    {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess( boolean success )
    {
        this.success= success;
    }

    /**
     * @return the clientError
     */
    public boolean isClientError()
    {
        return clientError;
    }

    /**
     * @param clientError the clientError to set
     */
    public void setClientError( boolean clientError )
    {
        this.clientError= clientError;
    }

    /**
     * @return the serverError
     */
    public boolean isServerError()
    {
        return serverError;
    }

    /**
     * @param serverError the serverError to set
     */
    public void setServerError( boolean serverError )
    {
        this.serverError= serverError;
    }
}
