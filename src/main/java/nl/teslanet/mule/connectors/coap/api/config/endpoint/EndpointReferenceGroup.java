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
package nl.teslanet.mule.connectors.coap.api.config.endpoint;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.ExclusiveOptionals;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Parametergroup for reference to an endpoint configuration.
 */
@ExclusiveOptionals(isOneRequired= true)
public class EndpointReferenceGroup
{
    /**
     * Reference to UDP endpoint configuration.
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowInlineDefinition= false, allowReferences= true)
    @Placement(order= 1, tab= "Endpoint")
    @Summary(value= "Reference to UDP endpoint configuration.")
    public UDPEndpoint udp;

    /**
     * Reference to multicast UDP endpoint configuration
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= true, allowInlineDefinition= false)
    @Placement(order= 1, tab= "Endpoint")
    @Summary(value= "Reference to multicast UDP endpoint configuration.")
    public MulticastUDPEndpoint multicastUdp;

    /**
     * Reference to DTLS endpoint configuration.
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowInlineDefinition= false, allowReferences= true)
    @Placement(order= 1, tab= "Endpoint")
    @Summary(value= "Reference to DTLS endpoint configuration.")
    public DTLSEndpoint dtls;

    /**
     * Reference to TCP server endpoint configuration.
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowInlineDefinition= false, allowReferences= true)
    @Placement(order= 1, tab= "Endpoint")
    @Summary(value= "Reference to TCP server endpoint configuration.")
    public TCPServerEndpoint tcpServer;

    /**
     * Reference to TCP client endpoint configuration.
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowInlineDefinition= false, allowReferences= true)
    @Placement(order= 1, tab= "Endpoint")
    @Summary(value= "Reference to TCP client endpoint configuration.")
    public TCPClientEndpoint tcpClient;

    /**
     * Reference to TCP server endpoint configuration.
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowInlineDefinition= false, allowReferences= true)
    @Placement(order= 1, tab= "Endpoint")
    @Summary(value= "Reference to TLS server endpoint configuration.")
    public TLSServerEndpoint tlsServer;

    /**
     * Reference to TLS client endpoint configuration.
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowInlineDefinition= false, allowReferences= true)
    @Placement(order= 1, tab= "Endpoint")
    @Summary(value= "Reference to TLS client endpoint configuration.")
    public TLSClientEndpoint tlsClient;

}
