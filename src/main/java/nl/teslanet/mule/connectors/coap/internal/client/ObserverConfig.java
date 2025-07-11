/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2025 (teslanet.nl) Rogier Cobben
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


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.CoapRequestType;
import nl.teslanet.mule.connectors.coap.api.ObserveCancel;


/**
 * The configuration of an observer.
 *
 */
public class ObserverConfig extends AbstractRequestConfig
{
    /**
     * When the request type is Confirmable (CON) the server is expected to acknowledge reception of the request.
     * When Non-confirmable (NON) the client will not expect acknowledgement and will not be able to resend the message when needed.
     * When DEFAULT the client default is used.
     */
    @Parameter
    @Optional( defaultValue= "DEFAULT" )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @Summary(
        "When true a cancel message is sent actively. \nOtherwise a passive cancel is executed.\n When DEFAULT the client default is used"
    )
    @Placement( order= 1 )
    private CoapRequestType type= CoapRequestType.DEFAULT;

    /**
     * When true a cancel message is sent. Otherwise a passive cancel is executed.
     * When DEFAULT the client default is used.
     */
    @Parameter
    @Optional( defaultValue= "DEFAULT" )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @Summary(
        "When true a cancel message is sent. \nOtherwise a passive cancel is executed.\n When DEFAULT the client default is used"
    )
    @Placement( order= 71 )
    private ObserveCancel observeCancel= ObserveCancel.DEFAULT;

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
     * @return the observeCancel
     */
    public ObserveCancel getObserveCancel()
    {
        return observeCancel;
    }

    /**
     * @param observeCancel the observeCancel to set
     */
    public void setObserveCancel( ObserveCancel observeCancel )
    {
        this.observeCancel= observeCancel;
    }
}
