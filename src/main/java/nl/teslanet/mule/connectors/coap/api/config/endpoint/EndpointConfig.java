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
package nl.teslanet.mule.connectors.coap.api.config.endpoint;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.ExclusiveOptionals;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Parametergroup for endpoint configuration. Exactly one endpoint needs to be configured in the group.
 */
@ExclusiveOptionals( isOneRequired= true )
public class EndpointConfig
{
    /**
     * UDP endpoint configuration.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowInlineDefinition= true, allowReferences= true )
    @Summary( value= "UDP endpoint configuration. (configure one endpoint type only) " )
    public UDPEndpoint udpEndpoint;

    /**
     * Multicast UDP endpoint configuration.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowInlineDefinition= true, allowReferences= true )
    @Summary( value= "Multicast UDP endpoint configuration. (configure one endpoint type only) " )
    public MulticastUDPEndpoint multicastUdpEndpoint;

    /**
     * DTLS endpoint configuration.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowInlineDefinition= true, allowReferences= true )
    @Summary( value= "DTLS endpoint configuration." )
    public DTLSEndpoint dtlsEndpoint;

    //  TODO add tests
    //    /**
    //     * TCP server endpoint configuration.
    //     */
    //    @Parameter
    //    @Optional
    //    @Expression( ExpressionSupport.NOT_SUPPORTED )
    //    @ParameterDsl( allowInlineDefinition= true, allowReferences= true )
    //    @Summary( value= "TCP server endpoint configuration. (configure one endpoint type only) " )
    //    public TCPServerEndpoint tcpServerEndpoint;
    //
    //    /**
    //     * TCP client endpoint configuration.
    //     */
    //    @Parameter
    //    @Optional
    //    @Expression( ExpressionSupport.NOT_SUPPORTED )
    //    @ParameterDsl( allowInlineDefinition= true, allowReferences= true )
    //    @Summary( value= "TCP client endpoint configuration. (configure one endpoint type only) " )
    //    public TCPClientEndpoint tcpClientEndpoint;
    //
    //    /**
    //     * TCP server endpoint configuration.
    //     */
    //    @Parameter
    //    @Optional
    //    @Expression( ExpressionSupport.NOT_SUPPORTED )
    //    @ParameterDsl( allowInlineDefinition= true, allowReferences= true )
    //    @Summary( value= "TLS server endpoint configuration. (configure one endpoint type only) " )
    //    public TLSServerEndpoint tlsServerEndpoint;
    //
    //    /**
    //     * TLS client endpoint configuration.
    //     */
    //    @Parameter
    //    @Optional
    //    @Expression( ExpressionSupport.NOT_SUPPORTED )
    //    @ParameterDsl( allowInlineDefinition= true, allowReferences= true )
    //    @Summary( value= "TLS client endpoint configuration. (configure one endpoint type only) " )
    //    public TLSClientEndpoint tlsClientEndpoint;

    /**
     * @return the endpoint that is configured.
     */
    public AbstractEndpoint getEndpoint()
    {
        if ( udpEndpoint != null ) return udpEndpoint;
        if ( dtlsEndpoint != null ) return dtlsEndpoint;
        if ( multicastUdpEndpoint != null ) return multicastUdpEndpoint;
        //        if ( tcpClientEndpoint != null ) return tcpClientEndpoint;
        //        if ( tcpServerEndpoint != null ) return tcpServerEndpoint;
        //        if ( tlsClientEndpoint != null ) return tlsClientEndpoint;
        //        if ( tlsServerEndpoint != null ) return tlsServerEndpoint;
        return null;
    }

}
