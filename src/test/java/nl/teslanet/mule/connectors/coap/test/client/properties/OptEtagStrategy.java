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


import java.util.Arrays;
import java.util.List;

import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;

import nl.teslanet.mule.connectors.coap.api.options.ETag;


/**
 * The strategy to use by the testserver
 */
public class OptEtagStrategy implements OptionStrategy
{
    private ETag value;

    /**
     * Constructor using single etag
     * @param value the test value
     */
    public OptEtagStrategy( ETag value )
    {
        this.value= value;
    }

    @Override
    public void setOption( Response response )
    {
            response.getOptions().addETag( value.getBytes() );
        
    }

    @Override
    public boolean validateOption( Request request )
    {
        List< byte[] > etags= request.getOptions().getETags();
        if ( etags.size() != 1 ) return false;
        for ( int i= 0; i < etags.size(); i++ )
        {
            if ( !Arrays.equals( etags.get( i ), value.getBytes() ) )
            {
                return false;
            }
        }
        return true;
    }
}
