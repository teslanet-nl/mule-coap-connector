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
package nl.teslanet.mule.connectors.coap.api.query;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

//TODO make separate config possible?
/**
 * CoAP URI Query Parameter without expression support. 
 */
public class QueryParamConfig extends AbstractQueryParam
{
    /**
     * The key of the query parameter.
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @Summary("The key of query parameter. When empty the parameter is ignored.")
    @Example("some_key")
    private String key= null;

    /**
     * The value of the query parameter.
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @Summary("The value of the query parameter. When empty only the key will be added to request.")
    @Example("some_value_if_any")
    private String value= null;

    /**
     * @return the key of the query parameter.
     */
    @Override
    public String getKey()
    {
        return key;
    }

    /**
     * @return the value of the query parameter.
     */
    @Override
    public String getValue()
    {
        return value;
    }
}
