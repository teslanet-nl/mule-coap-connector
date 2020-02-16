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


import java.util.Map;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * The attributes os a CoAP response that is received or that will be returned.
 *
 */
public class ResponseAttributes
{
    /**
     * The CoAP response code to be set in the CoAP response.
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The CoAP response code of the response.")
    private CoAPResponseCode responseCode;

    /**
     * The CoAP options to be set in the CoAP response.
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The CoAP options of the response.")
    Map< String, Object > options;

    /**
     * @return the responseCode
     */
    public CoAPResponseCode getResponseCode()
    {
        return responseCode;
    }

    /**
     * @param responseCode the responseCode to set
     */
    public void setResponseCode( CoAPResponseCode responseCode )
    {
        this.responseCode= responseCode;
    }

    /**
     * @return the options
     */
    public Map< String, Object > getOptions()
    {
        return options;
    }

    /**
     * @param options the options to set
     */
    public void setOptions( Map< String, Object > options )
    {
        this.options= options;
    }
}
