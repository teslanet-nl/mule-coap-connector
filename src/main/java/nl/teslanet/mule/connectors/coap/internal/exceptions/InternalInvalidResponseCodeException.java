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
package nl.teslanet.mule.connectors.coap.internal.exceptions;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;

import nl.teslanet.mule.connectors.coap.api.ResponseBuilder.CoAPResponseCode;

/**
 *  Exception that is thrown when an invalid response code is used.
 */
public class InternalInvalidResponseCodeException extends Exception
{

    /**
     * serial version id
     */
    private static final long serialVersionUID= 1L;

    /**
     * Construct exception with given 
     * @param responseCodeString 
     */
    public InternalInvalidResponseCodeException( String responseCodeString )
    {
        super( "Invalid response code { " + responseCodeString + " }" );
    }
    
    /**
     * Construct exception with given 
     * @param responseCode
     */
    public InternalInvalidResponseCodeException( ResponseCode responseCode )
    {
        super( "Invalid response code { " + responseCode.name() + " / " + responseCode.text + " }" );
    }

    /**
     * Construct exception with given 
     * @param reponseCodeAttribute
     */
    public InternalInvalidResponseCodeException( CoAPResponseCode reponseCodeAttribute )
    {
        super( "Invalid response code { " + reponseCodeAttribute.name() + " }" );
    }
}
