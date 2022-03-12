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
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;


/**
 * The uri parameters for a CoAP request.
 *
 */
public abstract class AbstractResourceRequestParams extends AbstractResourceParams
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
    private CoAPRequestType type= CoAPRequestType.DEFAULT;

    /**
     * @return the confirmable
     */
    public CoAPRequestType getType()
    {
        return type;
    }

    /**
     * @param type the message type to set
     */
    public void setType( CoAPRequestType type )
    {
        this.type= type;
    }
}
