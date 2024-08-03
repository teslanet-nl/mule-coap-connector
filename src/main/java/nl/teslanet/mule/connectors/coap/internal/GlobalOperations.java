/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2023 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.internal;


import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;

import nl.teslanet.mule.connectors.coap.api.error.InvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueParams;
import nl.teslanet.mule.connectors.coap.internal.exceptions.OptionValueErrorProvider;


/**
 * This class is a container for operations independent of client or server.
 */
public class GlobalOperations
{
    /**
     * Operation that constructs an CoAP option byte value.
     * The resulting byte array is complient to CoAP specifications 
     * how integer and string values must be converted to bytes.
     * @param params The parameters for option value construction.
     * @return The byte array containing given value.
     */
    @Throws( { OptionValueErrorProvider.class } )
    @MediaType( strict= false, value= "application/octet-stream")
    public byte[] setOptionValue( @ParameterGroup( name= "Bytes value" )
    OptionValueParams params )
    {
        try
        {
            return params.getBytes().getByteArray();
        }
        catch ( OptionValueException e )
        {
            throw new InvalidOptionValueException("Invalid option value.", e);
        }
    }
}
