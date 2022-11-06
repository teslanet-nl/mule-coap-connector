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
package nl.teslanet.mule.connectors.coap.test.server.basic;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractServerTestCase;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;


/**
 * Patch method basic tests
 *
 */
public class PatchMethodTest extends AbstractServerTestCase
{
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/basic/testserver1.xml";
    };

    @Test
    public void testSuccess() throws ConnectorException, IOException
    {
        String uri= "coap://localhost/basic/patch_me";
        setClientUri( uri );

        CoapResponse response= client.advanced( new Request( Code.PATCH ) );

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CONTENT", ResponseCode.CHANGED, response.getCode() );
        assertEquals( "response payload has wrong value", uri, response.getResponseText() );
    }

    @Test
    public void testNoPatchAllowed() throws ConnectorException, IOException
    {
        String uri= "coap://localhost/basic/do_not_patch_me";

        setClientUri( uri );

        CoapResponse response= client.advanced( new Request( Code.PATCH ) );

        assertNotNull( "patch gave no response", response );
        assertEquals( "response code should be METHOD_NOT_ALLOWED", ResponseCode.METHOD_NOT_ALLOWED, response.getCode() );
        assertEquals( "response payload has wrong value", "", response.getResponseText() );
    }

    @Test
    public void testNoPatchAllowed2() throws ConnectorException, IOException
    {
        String uri= "coap://localhost/basic/do_not_patch_me2";

        setClientUri( uri );

        CoapResponse response= client.advanced( new Request( Code.PATCH ) );

        assertNotNull( "patch gave no response", response );
        assertEquals( "response code should be METHOD_NOT_ALLOWED", ResponseCode.METHOD_NOT_ALLOWED, response.getCode() );
        assertEquals( "response payload has wrong value", "", response.getResponseText() );
    }

    @Test
    public void testNoResource() throws ConnectorException, IOException
    {
        String uri= "coap://localhost/basic/do_not_patch_me3";

        setClientUri( uri );

        CoapResponse response= client.advanced( new Request( Code.PATCH ) );

        assertNotNull( "patch gave no response", response );
        assertEquals( "response code should be NOT_FOUND", ResponseCode.NOT_FOUND, response.getCode() );
        assertEquals( "response payload has wrong value", "", response.getResponseText() );
    }
}
