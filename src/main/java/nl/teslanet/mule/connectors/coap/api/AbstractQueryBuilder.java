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
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * The parameters for a CoAP query request.
 *
 */
public abstract class AbstractQueryBuilder extends AbstractAddressBuilder
{
    /**
     * The query parameters of the request.
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression( ExpressionSupport.SUPPORTED )
    @Placement(order = 61)
    @DisplayName( "Query Parameters" )
    @Summary( "The query parameters of the request." )
    private MultiMap< String, String> queryParams= null;

    /**
     * @return the queryParameters
     */
    public MultiMap< String, String> getQueryParams()
    {
        return queryParams;
    }

    /**
     * @param queryParameters the queryParameters to set
     */
    public void setQueryParams( MultiMap< String, String> queryParameters )
    {
        this.queryParams= queryParameters;
    }
}
