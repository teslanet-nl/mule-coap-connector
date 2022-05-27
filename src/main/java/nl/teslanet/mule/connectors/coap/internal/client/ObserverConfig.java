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
package nl.teslanet.mule.connectors.coap.internal.client;


import java.util.List;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.CoapRequestType;
import nl.teslanet.mule.connectors.coap.api.RemoteEndpointConfig;
import nl.teslanet.mule.connectors.coap.api.query.QueryConfig;


/**
 * The configured defaults of a CoAP request.
 *
 */
public class ObserverConfig
{
    /**
     * When the request type is Confirmable (CON) the server is expected to acknowledge reception of the request.
     * When Non-confirmable (NON) the client will not expect acknowledgement and will not be able to resend the message when needed.
     * When DEFAULT the client default is used.
     */
    @Parameter
    @Optional( defaultValue= "DEFAULT" )
    @Expression( ExpressionSupport.SUPPORTED )
    @Placement( order= 1 )
    //@Summary( "When the request type is Confirmable (CON) the server is expected to acknowledge reception of the request.\nWhen Non-confirmable (NON) the client will not expect acknowledgement and will not be able to resend the message when needed.\nWhen DEFAULT the client default is used." )
    private CoapRequestType type= CoapRequestType.DEFAULT;

    /**
     * The hostname or ip of the server to access.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @Summary( "The hostname or ip of the server to access." )
    private String host= null;

    /**
     * "The port of the server to access."
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @Summary( "The port of the server to access." )
    private Integer port= null;

    /**
    * The path of the resource.
    */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @Example( value= "/some/resource/path" )
    @Summary( "The path of the resource on the server that the request is issued on." )
    private String path= null;

    /**
     * The default query parameters of requests.
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @Summary( "The query parameters of the request." )
    @DisplayName( "Query Parameters" )
    private List< QueryConfig > queryConfigs= null;

    /**
     * The shared server or proxy to address the request to.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false, allowInlineDefinition= true )
    @Placement( order= 1, tab= "Advanced" )
    private RemoteEndpointConfig remoteEndpointConfig= null;

    /**
     * @return the confirmable
     */
    public CoapRequestType getType()
    {
        return type;
    }

    /**
     * @param type the message type to set
     */
    public void setType( CoapRequestType type )
    {
        this.type= type;
    }

    /**
     * @return the host
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost( String host )
    {
        this.host= host;
    }

    /**
     * @return the port
     */
    public Integer getPort()
    {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort( Integer port )
    {
        this.port= port;
    }

    /**
     * @return the path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath( String path )
    {
        this.path= path;
    }

    /**
     * @return The queryParameters.
     */
    public List< QueryConfig > getQueryConfigs()
    {
        return queryConfigs;
    }

    /**
     * @param queryConfigs The query parameters to set.
     */
    public void setQueryConfigs( List< QueryConfig > queryConfigs )
    {
        this.queryConfigs= queryConfigs;
    }

    /**
     * @return the remoteEndpointConfig
     */
    public RemoteEndpointConfig getRemoteEndpointConfig()
    {
        return remoteEndpointConfig;
    }

    /**
     * @param remoteEndpointConfig the remoteEndpointConfig to set
     */
    public void setRemoteEndpointConfig( RemoteEndpointConfig remoteEndpointConfig )
    {
        this.remoteEndpointConfig= remoteEndpointConfig;
    }
}
