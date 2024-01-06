/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2023 (teslanet.nl) Rogier Cobben
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
 * Configuration items that are DTLS client specific.
 *
 */
public class DtlsClientParams implements VisitableConfig
{
    /**
     * Default handshake mode.
     */
    public enum DefaultHandshakeMode
    {
        /**
         * Don't start a handshake, even, if no session is available.
         */
        NONE,
        /**
         * Start a handshake, if no session is available.
         */
        AUTO,
    }

    /**
     * DTLS_TRUNCATE_CLIENT_CERTIFICATE_PATH
     * Truncate certificate paths for client's certificate message.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Summary( value= "Truncate certificate paths for client's certificate message." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean truncateClientCertificatePaths= true;

    /**
     * Verify the certificate subject of the remote server.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Summary( value= "Verify the certificate subject of the remote server." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean verifyServerCertificateSubject= true;

    /**
     * DTLS default handshake mode.
     */
    @Parameter
    @Optional( defaultValue= "AUTO" )
    @Summary( value= "DTLS default handshake mode." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public DefaultHandshakeMode defaultHandshakeMode= DefaultHandshakeMode.AUTO;

    /**
     * Accept visitor.
     */
    @Override
    public void accept( ConfigVisitor visitor ) throws ConfigException
    {
        visitor.visit( this );
    }

}
