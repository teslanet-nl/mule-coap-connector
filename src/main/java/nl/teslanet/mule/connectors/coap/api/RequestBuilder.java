/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2020 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.api;


import java.util.List;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import nl.teslanet.mule.connectors.coap.api.query.QueryParam;


/**
 * The attributes of a CoAP request.
 *
 */
public class RequestBuilder
{
    // Mule seems to need this to be an inner enum.
    /**
     * Available request codes.
     */
    public enum CoAPRequestCode
    {
        GET, POST, PUT, DELETE
    }

    /**
     * The CoAP request code specifying the requested action on the resource on the server.
     */
    @Parameter
    @Expression(ExpressionSupport.SUPPORTED)
    //@OfValues(RequestCodeValueProvider.class)
    @Example(value= "GET")
    @Summary("The CoAP request code specifying the requested action on the resource on the server.")
    //private String requestCode;
    private CoAPRequestCode requestCode;

    @Parameter
    @Content(primary= true)
    @Summary("The CoAP request payload.")
    private TypedValue< Object > requestPayload;

    /**private
     * When true the server is expected to acknowledge reception of the request.
     */
    @Parameter
    @Optional(defaultValue= "true")
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("When true the server is expected to acknowledge reception of the request.")
    private boolean confirmable= true;

    /**
     * The hostname or ip of the server to address. 
     * This overrides client host configuration. 
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The hostname or ip of the server to address. This overrides client host configuration.")
    private String host= null;

    /**
     * The port the server is listening on.
     * This overrides client port configuration. 
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The port the server is listening on. This overrides client port configuration.")
    private Integer port= null;

    /**
    * The path of the resource.
    */
    @Parameter
    @Expression(ExpressionSupport.SUPPORTED)
    @Example(value= "/some/resource/path")
    @Summary("The path on the server of the resource the request is issued on.")
    private String path= null;

    /**
     * The query parameters to send with the request.
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The query parameters to send with the request.")
    @DisplayName("Query Parameters")
    private List< QueryParam > queryParams= null;

    /**
     * @return the requestCode
     */
    public CoAPRequestCode getRequestCode()
    {
        return requestCode;
    }

    /**
     * @param requestCode the requestCode to set
     */
    public void setRequestCode( CoAPRequestCode requestCode )
    {
        this.requestCode= requestCode;
    }

    /**
     * @return the requestPayload
     */
    public TypedValue< Object > getRequestPayload()
    {
        return requestPayload;
    }

    /**
     * @param requestPayload the requestPayload to set
     */
    public void setRequestPayload( TypedValue< Object > requestPayload )
    {
        this.requestPayload= requestPayload;
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
     * @return the queryParameters
     */
    public List< QueryParam > getQueryParams()
    {
        return queryParams;
    }

    /**
     * @param queryParameters the queryParameters to set
     */
    public void setQueryParams( List< QueryParam > queryParameters )
    {
        this.queryParams= queryParameters;
    }

    /**
     * @return the confirmable
     */
    public boolean isConfirmable()
    {
        return confirmable;
    }

    /**
     * @param confirmable the confirmable to set
     */
    public void setConfirmable( boolean confirmable )
    {
        this.confirmable= confirmable;
    }

}
