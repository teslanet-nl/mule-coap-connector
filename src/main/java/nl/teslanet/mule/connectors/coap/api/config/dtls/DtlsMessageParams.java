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
import nl.teslanet.mule.connectors.coap.api.config.TriState;
import nl.teslanet.mule.connectors.coap.api.config.VisitableConfig;


/**
 * DTLS message parameters.
 *
 */
public class DtlsMessageParams implements VisitableConfig
{
    /**
     * Fragment length used by Hello extension.
     *
     */
    public enum FragmentSize
    {
        BYTES_512, BYTES_1024, BYTES_2048, BYTES_4096;
    }

    /**
     * The DTLS record size limit.
     * 
     * See <a href="https://tools.ietf.org/html/rfc8449" target="_blank">RFC 8449</a>.
     */
    @Parameter
    @Optional
    @Summary( value= "DTLS record size limit (RFC 8449) [BYTES]. The value must be between 64 and 16K." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer recordSizeLimit= null;

    /**
     * The maximum fragment length used by Hello extension.
     * 
     * @see <a href="https://tools.ietf.org/html/rfc6066#section-4" target="_blank">RFC 6066, Section 4</a>
     */
    @Parameter
    @Optional
    @Summary( value= "Maximum fragment length (RFC 6066)." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public FragmentSize maxFragmentLength= null;

    /**
     * The maximum length of reassembled fragmented handshake messages.
     */
    @Parameter
    @Optional( defaultValue= "8192" )
    @Summary( value= "Maximum length [BYTES] of reassembled fragmented handshake message.\nMust be large enough for used certificates." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer maxFragmentedHandshakeMsgLength= 8192;

    /**
     * Enable to use multiple DTLS records in UDP messages.
     */
    @Parameter
    @Optional( defaultValue= "UNDEFINED" )
    @Summary( value= "Use multiple DTLS records in UDP messages." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public TriState multiRecords= TriState.UNDEFINED;

    /**
     * Enable to use multiple handshake messages in DTLS records.
     */
    @Parameter
    @Optional( defaultValue= "UNDEFINED" )
    @Summary( value= "Use multiple handshake messages in DTLS records.\nNot all libraries may have implemented this." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public TriState multiHandshakeMsgRecords= TriState.UNDEFINED;

    /**
     * The MTU (Maximum Transmission Unit) override [BYTES].
     */
    @Parameter
    @Optional
    @Summary( value= "MTU (Maximum Transmission Unit) [BYTES].\nMust be used, if the MTU of the local network doesn't apply, e.g. if ip-tunnels are used." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer mtu= null;

    /**
     * The MTU (Maximum Transmission Unit) limit [BYTES]for (link local) auto detection.
     */
    @Parameter
    @Optional( defaultValue= "1500" )
    @Summary( value= "MTU (Maximum Transmission Unit) limit [BYTES] for local auto detection." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer mtuLimit= 1500;

    /**
     * Accept visitor.
     */
    @Override
    public void accept( ConfigVisitor visitor ) throws ConfigException
    {
        visitor.visit( this );
    }
}
