/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2023 - 2024 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.api.config.dtls;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.config.ConfigException;
import nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor;
import nl.teslanet.mule.connectors.coap.api.config.VisitableConfig;


/**
 * Configuration items that are DTLS server specific.
 *
 */
public class DtlsServerParams implements VisitableConfig
{
    /**
     * Client Authentication mode.
     *
     */
    public enum AuthenticationMode
    {
        /**
         * Don't use a certificate for authentication.
         */
        NONE,
        /**
         * Use a certificate for optional authentication.
         */
        OPTIONAL,
        /**
         * Use a certificate for authentication.
         */
        MANDATORY;
    }

    /**
     * The initial DTLS retransmission timeout.
     */
    @Parameter
    @Optional( defaultValue= "MANDATORY" )
    @Summary( value= "DTLS client authentication mode for certificate based cipher suites." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public AuthenticationMode clientAuthentication;

    /**
     * Enable server to use a session ID in order to support session resumption.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Summary( value= "Enable server to use a session ID in order to support session resumption." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean serverUseSessionId= true;

    /**
     * Use server name indication.
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @Summary( value= "Use server name indication." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean serverNameIndication= false;

    /**
     * Use a HELLO_VERIFY_REQUEST to protect against spoofing.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Summary( value= "Use a HELLO_VERIFY_REQUEST to protect against spoofing." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean helloVerifyRequest= true;

    /**
     * Accept visitor.
     */
    @Override
    public void accept( ConfigVisitor visitor ) throws ConfigException
    {
        visitor.visit( this );
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj )
    {
        if ( obj == null )
        {
            return false;
        }
        if ( obj == this )
        {
            return true;
        }
        if ( obj.getClass() != getClass() )
        {
            return false;
        }
        DtlsServerParams rhs= (DtlsServerParams) obj;
        return new EqualsBuilder()
            .append( clientAuthentication, rhs.clientAuthentication )
            .append( serverUseSessionId, rhs.serverUseSessionId )
            .append( serverNameIndication, rhs.serverNameIndication )
            .append( helloVerifyRequest, rhs.helloVerifyRequest )
            .isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 15, 35 )
            .append( clientAuthentication )
            .append( serverUseSessionId )
            .append( serverNameIndication )
            .append( helloVerifyRequest )
            .toHashCode();
    }
}
