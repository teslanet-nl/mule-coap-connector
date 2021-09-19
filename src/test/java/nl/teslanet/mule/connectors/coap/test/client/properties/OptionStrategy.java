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
package nl.teslanet.mule.connectors.coap.test.client.properties;


import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;


/**
 * Interface of strategies to set and validate options
 *
 */
public interface OptionStrategy
{
    /**
     * Operation to set option on response message 
     * @param response to set option on
     */
    public void setOption( Response response );

    /**
     * Operation to validate option on request
     * @param request containing the option
     * @return true when option value is as expected, false otherwise
     */
    public boolean validateOption( Request request );
}
