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
package nl.teslanet.mule.connectors.coap.api;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.options.ResponseOptionsParams;


/**
 * The parameters of a CoAP response that will be returned.
 *
 */
public class ResponseParams
{
    /**
     * The CoAP response code to be set in the CoAP response.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @Placement( tab= "Response", order= 1 )
    @Summary( "The CoAP response code of the response." )
    @Example( "CONTENT" )
    private CoapResponseCode responseCode;

    /**
     * The payload of the CoAP response message. 
     */
    @Parameter
    @Content( primary= true )
    @Placement( tab= "Response", order= 2 )
    @Summary( "The payload of the CoAP response." )
    private TypedValue< Object > responsePayload;

    /**
     * The CoAP options to send with the response.
     */
    @ParameterGroup( name= "Response options" )
    @Placement( tab= "Options", order= 49 )
    private ResponseOptionsParams responseOptionsParams= new ResponseOptionsParams();

    /**
     * @return the responseCode
     */
    public CoapResponseCode getResponseCode()
    {
        return responseCode;
    }

    /**
     * @param responseCode the responseCode to set
     */
    public void setResponseCode( CoapResponseCode responseCode )
    {
        this.responseCode= responseCode;
    }

    /**
     * @return the responsePayload
     */
    public TypedValue< Object > getResponsePayload()
    {
        return responsePayload;
    }

    /**
     * @param responsePayload the responsePayload to set
     */
    public void setResponsePayload( TypedValue< Object > responsePayload )
    {
        this.responsePayload= responsePayload;
    }

    /**
     * @return the responseOptionsParams
     */
    public ResponseOptionsParams getResponseOptionsParams()
    {
        return responseOptionsParams;
    }

    /**
     * @param responseOptionsParams the responseOptionsParams to set
     */
    public void setResponseOptionsParams( ResponseOptionsParams responseOptionsParams )
    {
        this.responseOptionsParams= responseOptionsParams;
    }
}
