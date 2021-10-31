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
package nl.teslanet.mule.connectors.coap.internal.options;


import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.mule.runtime.api.metadata.MediaType;


/**
 * Mediator between Californium and Mule MediaTypes.
 * Mule and Californium have a MediaType implementation that differ in some details.
 * This mediator bridges these differences.  
 */
public class MediaTypeMediator
{
    /**
     * No instances needed.
     */
    private MediaTypeMediator()
    {
        //NOOP
    }

    /**
     * Convert CoAP content format to Mule MediaType.
     * @param coapContentFormat the content format to convert
     * @return the converted MediaType, ANY when no conversion is found
     */
    public static MediaType toMediaType( int coapContentFormat )
    {
        MediaType result;
        if ( coapContentFormat == MediaTypeRegistry.UNDEFINED )
        {
            result= MediaType.ANY;
        }
        else
        {
            try
            {
                result= MediaType.parse( MediaTypeRegistry.toString( coapContentFormat ) );
            }
            catch ( Exception e )
            {
                result= MediaType.ANY;
            }
        }
        return result;
    }

    /**
     * Convert Mule MediaType to CoAP content format
     * @param mediaType the mediaType to convert
     * @return the converted CoAP content format, UNDEFINED when no conversion is found
     */
    public static int toContentFormat( MediaType mediaType )
    {
        int result;
        if ( mediaType.equals( MediaType.ANY ) )
        {
            result= MediaTypeRegistry.UNDEFINED;
        }
        else
        {
            try
            {
                result= MediaTypeRegistry.parse( mediaType.toRfcString() );
            }
            catch ( Exception e )
            {
                //does not parse when mediatype contains parameters (Mule uses parameters , Cf does not), UNDEFINED will be used 
                result= MediaTypeRegistry.UNDEFINED;
            }
        }
        return result;
    }

}
