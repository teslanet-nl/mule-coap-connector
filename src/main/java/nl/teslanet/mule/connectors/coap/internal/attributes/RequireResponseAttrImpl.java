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
package nl.teslanet.mule.connectors.coap.internal.attributes;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.californium.core.coap.NoResponseOption;

import nl.teslanet.mule.connectors.coap.api.options.RequireResponseAttribute;
import nl.teslanet.mule.connectors.coap.internal.utils.AppendableToString;
import nl.teslanet.mule.connectors.coap.internal.utils.AttributesStringBuilder;


/**
 * Attributes implementation that expresses client interest or disinterest in a response or category of responses.
 * The server MUST send back responses of the classes for which the client has not expressed any disinterest.
 * This way explicit interest in responses is expressed for which no flag has been set. 
 * In multicast scenarios, the parameter is used to make a server respond where it would otherwise remain silent.
 * 
 * @see <a href=
 *      "https://datatracker.ietf.org/doc/html/rfc7967">IETF RFC 7967\n - Constrained Application Protocol (CoAP) Option for No Server Response</a>
 */
public class RequireResponseAttrImpl extends RequireResponseAttribute implements AppendableToString
{
    /**
     * The No Response option value.
     */
    private NoResponseOption option;

    /**
     * Constructor.
     * @param noResponse option to create attributes from. 
     */
    public RequireResponseAttrImpl( NoResponseOption noResponse )
    {
        this.option= noResponse;
    }

    /**
     * @return the success
     */
    @Override
    public boolean isSuccess()
    {
        return ( option.getMask() & NoResponseOption.SUPPRESS_SUCCESS ) == 0;
    }

    /**
     * @return the clientError
     */
    @Override
    public boolean isClientError()
    {
        return ( option.getMask() & NoResponseOption.SUPPRESS_CLIENT_ERROR ) == 0;
    }

    /**
     * @return the noServerError
     */
    @Override
    public boolean isServerError()
    {
        return ( option.getMask() & NoResponseOption.SUPPRESS_SERVER_ERROR ) == 0;
    }

    /**
     * @return True if client has no interest in a response at all. 
     */
    @Override
    public boolean isNone()
    {
        return option.getMask() == NoResponseOption.SUPPRESS_ALL;
    }

    /**
     * @return True if client has interest in all kinds of responses. 
     */
    @Override
    public boolean isAll()
    {
        return option.getMask() == 0;
    }

    /**
     * Get the string representation.
     */
    @Override
    public String toString()
    {
        AttributesStringBuilder builder= new AttributesStringBuilder( this );
        this.appendTo( builder );
        return builder.toString();
    }

    @Override
    public void appendTo( ToStringStyle style, StringBuffer buffer )
    {
        AttributesStringBuilder builder= new AttributesStringBuilder( this, style, buffer );
        this.appendTo( builder );
    }

    /**
     * Append this to string builder.
     * @param builder The string builder to append to.
     */
    public void appendTo( ToStringBuilder builder )
    {
        builder.append( "clientError", isClientError() ).append( "serverError", isServerError() ).append( "success", isSuccess() );
    }
}
