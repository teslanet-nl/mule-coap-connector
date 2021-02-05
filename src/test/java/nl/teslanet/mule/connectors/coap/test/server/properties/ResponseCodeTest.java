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
package nl.teslanet.mule.connectors.coap.test.server.properties;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractServerTestCase;
import nl.teslanet.shaded.org.eclipse.californium.core.CoapResponse;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.CoAP.Code;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.CoAP.ResponseCode;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.Request;
import nl.teslanet.shaded.org.eclipse.californium.elements.exception.ConnectorException;


@RunnerDelegateTo(Parameterized.class)
public class ResponseCodeTest extends AbstractServerTestCase
{
    /**
     * @return the test parameters
     */
    @Parameters(name= "Request= {0}, path= {1}, responseCode= {2}")
    public static Collection< Object[] > data()
    {
        ArrayList< Object[] > parameters= new ArrayList< Object[] >();
        //Code code;
        //code= Code.GET;
        for ( Code code : new Code []{ Code.GET, Code.PUT, Code.POST, Code.DELETE } )
        {
            for ( ResponseCode rspCode : ResponseCode.values() )
            {
                if ( !rspCode.name().startsWith( "_" ) )
                {
                    parameters.add( new Object []{ code, "/responsecode/always_" + rspCode.name(), rspCode } );
                }
            }
        }
        return parameters;
    }

    /**
     * Request code to test
     */
    @Parameter(0)
    public Code requestCode;

    /**
    * Test resource to call
    */
    @Parameter(1)
    public String resourcePath;

    /**
    * ResponseCode to test
    */
    @Parameter(2)
    public ResponseCode responseCode;

    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/properties/testserver-ResponseCode.xml";
    };

    @Test
    public void testResponseCode() throws ConnectorException, IOException
    {
        setClientPath( resourcePath );
        Request request= new Request( requestCode );
        CoapResponse response= client.advanced( request );

        assertNotNull( "request gave no response", response );
        assertEquals( "server didn't return response code: " + responseCode, responseCode, response.getCode() );
    }
}
