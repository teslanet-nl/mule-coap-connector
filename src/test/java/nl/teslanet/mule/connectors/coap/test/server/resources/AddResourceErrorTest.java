/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2024 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.test.server.resources;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractServerTestCase;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Request;


@RunnerDelegateTo( Parameterized.class )
public class AddResourceErrorTest extends AbstractServerTestCase
{
    /**
     * @return the test parameters
     */
    @Parameters( name= "path= {0}" )
    public static Collection< Object[] > data()
    {
        return Arrays.asList( new Object [] []{ { "/" }, { "x/y" }, { "x/y/" }, { "" }, { " " }, { "  " } } );
    }

    /**
    * Test resource path
    */
    @Parameter( 0 )
    public String resourcePath;

    /**
     * Test config.
     */
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/resources/testserver1.xml";
    };

    @Test
    public void testResourceSlash1() throws Exception
    {
        setClientUri( "/add_resource/all_methods" );
        Request request= new Request( Code.POST );
        request.setPayload( resourcePath );
        CoapResponse response= client.advanced( request );
        assertNotNull( "add resource gave no response", response );
        assertFalse( "expected failure", response.isSuccess() );
        assertEquals( "request gave wrong response", ResponseCode.BAD_REQUEST, response.getCode() );
        assertEquals( "request gave wrong payload", "INVALID_RESOURCE_URI", response.getResponseText() );
    }
}
