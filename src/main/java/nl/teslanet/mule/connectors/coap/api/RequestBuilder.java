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
package nl.teslanet.mule.connectors.coap.api;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * The parameters of a CoAP request.
 *
 */
public class RequestBuilder extends AbstractResourceRequestBuilder
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
    @Expression( ExpressionSupport.SUPPORTED )
    @Placement(order = 11)
    @Example( value= "GET" )
    @Summary( "The CoAP request code specifying the requested action on the resource on the server." )
    private CoAPRequestCode requestCode;

    @Parameter
    @Content( primary= true )
    @Placement(order = 12)
    @Summary( "The CoAP request payload. Note: when not appropriate for the CoAP message-type (i.e. GET, DELETE) the payload is ignored, unless 'Force Payload' is set." )
    private TypedValue< Object > requestPayload;

    /**
     * When true the payload will be added to the request, even when not appropriate for the CoAP message-type (i.e. GET, DELETE).
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @Expression( ExpressionSupport.SUPPORTED )
    @Placement( tab= Placement.ADVANCED_TAB )
    @Summary( "When true the payload will also be added to the request, when not appropriate for the CoAP message-type (i.e. GET, DELETE)." )
    private boolean forcePayload= false;

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
     * @return the forcePayload
     */
    public boolean isForcePayload()
    {
        return forcePayload;
    }

    /**
     * @param forcePayload the forcePayload to set
     */
    public void setForcePayload( boolean forcePayload )
    {
        this.forcePayload= forcePayload;
    }

}
