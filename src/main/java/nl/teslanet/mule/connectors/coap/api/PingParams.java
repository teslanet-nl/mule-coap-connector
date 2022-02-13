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
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

/**
 * The parameters of a CoAP ping.
 */
public class PingParams
{
    /**
     * The address of the remote endpoint to ping.
     * When empty the client request defaults are used.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    private PingAddress pingAddress= null;

    /**
     * @return The pingAddress.
     */
    public PingAddress getPingAddress()
    {
        return pingAddress;
    }

    /**
     * @param pingAddress The pingAddress to set.
     */
    public void setPingAddress( PingAddress pingAddress )
    {
        this.pingAddress= pingAddress;
    }
}
