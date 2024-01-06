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
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;

import nl.teslanet.mule.connectors.coap.api.entity.EntityTag;
import nl.teslanet.mule.connectors.coap.api.entity.EntityTagException;
import nl.teslanet.mule.connectors.coap.api.entity.EntityTagParams;
import nl.teslanet.mule.connectors.coap.api.error.InvalidEntityTagException;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.EntityTagErrorProvider;


/**
 * This class is a container for operations independent of client or server.
 */
public class GlobalOperations
{
    /**
     * Operation that constructs an Entity Tag object.
     * @param entityTagParams The parameters for entity tag construction.
     * @return The entity tag containing given value.
     */
    @Throws( { EntityTagErrorProvider.class } )
    public EntityTag entityTag( @ParameterGroup( name= "Entity Tag" )
    EntityTagParams entityTagParams )
    {
        try
        {
            return EntityTag.valueOf( entityTagParams.getBytes().getByteArray() );
        }
        catch ( EntityTagException | OptionValueException e )
        {
            throw new InvalidEntityTagException("Invalid entity tag value.", e);
        }
    }
}
