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
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.elements.exception.ConnectorException;


/**
 * Get method basic tests
 *
 */
public class ImplicitServerEndpointTest extends AbstractServerTestCase
{
    String baseUri= "coap://localhost";

    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/basic/testserver-implicit-endpoint.xml";
    };

    @Test
    public void testGet() throws ConnectorException, IOException
    {
        String path= "/implicit_endpoint/get_me";
        setClientUri( path );

        CoapResponse response= client.get();

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CONTENT", ResponseCode.CONTENT, response.getCode() );
        assertEquals( "response payload has wrong value", baseUri + path, response.getResponseText() );

    }

    @Test
    public void testPost() throws ConnectorException, IOException
    {
        String path= "/implicit_endpoint/post_me";
        setClientUri( path );

        CoapResponse response= client.post( path, 0 );

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CHANGED", ResponseCode.CHANGED, response.getCode() );
        assertEquals( "response payload has wrong value", baseUri + path + path, response.getResponseText() );
    }

    @Test
    public void testPut() throws ConnectorException, IOException
    {
        String path= "/implicit_endpoint/put_me";
        setClientUri( path );

        CoapResponse response= client.put( path, 0 );

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CHANGED", ResponseCode.CHANGED, response.getCode() );
        assertEquals( "response payload has wrong value", baseUri + path + path, response.getResponseText() );
    }

    @Test
    public void testDelete() throws ConnectorException, IOException
    {
        String path= "/implicit_endpoint/delete_me";
        setClientUri( path );

        CoapResponse response= client.delete();

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be DELETED", ResponseCode.DELETED, response.getCode() );
        assertEquals( "response payload has wrong value", baseUri + path, response.getResponseText() );
    }
}
