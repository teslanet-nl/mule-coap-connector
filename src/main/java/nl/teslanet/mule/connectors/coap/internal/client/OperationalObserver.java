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
package nl.teslanet.mule.connectors.coap.internal.client;


import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapObserveRelation;


/**
 * The Operational Observer holds operational observe information.
 *
 */
public class OperationalObserver
{
    /**
     * The CoAP client used by the observer.
     */
    private CoapClient coapClient= null;

    /**
     * The CoAP ObserveRelation established by the observer.
     */
    private CoapObserveRelation coapObserveRelation= null;

    /**
     * @return the coapClient
     */
    public CoapClient getCoapClient()
    {
        return coapClient;
    }

    /**
     * @param coapClient the coapClient to set
     */
    public void setCoapClient( CoapClient coapClient )
    {
        this.coapClient= coapClient;
    }

    /**
     * @return the coapObserveRelation
     */
    public CoapObserveRelation getCoapObserveRelation()
    {
        return coapObserveRelation;
    }

    /**
     * @param coapObserveRelation the coapObserveRelation to set
     */
    public void setCoapObserveRelation( CoapObserveRelation coapObserveRelation )
    {
        this.coapObserveRelation= coapObserveRelation;
    }
}
