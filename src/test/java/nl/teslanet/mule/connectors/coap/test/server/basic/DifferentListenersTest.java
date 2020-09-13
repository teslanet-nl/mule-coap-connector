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
package nl.teslanet.mule.connectors.coap.test.server.basic;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractServerTestCase;
import nl.teslanet.shaded.org.eclipse.californium.core.CoapResponse;
import nl.teslanet.shaded.org.eclipse.californium.core.coap.CoAP.ResponseCode;
import nl.teslanet.shaded.org.eclipse.californium.elements.exception.ConnectorException;


/**
 * Get method basic tests
 *
 */
public class DifferentListenersTest extends AbstractServerTestCase
{
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/basic/testserver2.xml";
    };

    @Test(timeout= 5000)
    public void testAll() throws ConnectorException, IOException
    {
        String path= "/different_listeners/all";
        setClientPath( path );

        CoapResponse response= client.get();

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CONTENT", ResponseCode.CONTENT, response.getCode() );
        assertEquals( "response payload has wrong value", path + "-get", response.getResponseText() );

        response= client.post( path, 0 );

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CHANGED", ResponseCode.CHANGED, response.getCode() );
        assertEquals( "response payload has wrong value", path + "-post", response.getResponseText() );

        response= client.put( path, 0 );

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CHANGED", ResponseCode.CHANGED, response.getCode() );
        assertEquals( "response payload has wrong value", path + "-put", response.getResponseText() );

        response= client.delete();

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be DELETED", ResponseCode.DELETED, response.getCode() );
        assertEquals( "response payload has wrong value", path + "-delete", response.getResponseText() );
    }

    @Test(timeout= 5000)
    public void testGetOnly() throws ConnectorException, IOException
    {
        String path= "/different_listeners/all";
        String path1= "/different_listeners/get_only";
        setClientPath( path1 );

        CoapResponse response= client.get();

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CONTENT", ResponseCode.CONTENT, response.getCode() );
        assertEquals( "response payload has wrong value", path1, response.getResponseText() );

        response= client.post( path, 0 );

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CHANGED", ResponseCode.CHANGED, response.getCode() );
        assertEquals( "response payload has wrong value", path + "-post", response.getResponseText() );

        response= client.put( path, 0 );

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CHANGED", ResponseCode.CHANGED, response.getCode() );
        assertEquals( "response payload has wrong value", path + "-put", response.getResponseText() );

        response= client.delete();

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be DELETED", ResponseCode.DELETED, response.getCode() );
        assertEquals( "response payload has wrong value", path + "-delete", response.getResponseText() );

    }

    @Test(timeout= 5000)
    public void testPostOnly() throws ConnectorException, IOException
    {
        String path= "/different_listeners/all";
        String path1= "/different_listeners/post_only";
        setClientPath( path1 );

        CoapResponse response= client.get();

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CONTENT", ResponseCode.CONTENT, response.getCode() );
        assertEquals( "response payload has wrong value", path + "-get", response.getResponseText() );

        response= client.post( path, 0 );

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CHANGED", ResponseCode.CHANGED, response.getCode() );
        assertEquals( "response payload has wrong value", path1, response.getResponseText() );

        response= client.put( path, 0 );

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CHANGED", ResponseCode.CHANGED, response.getCode() );
        assertEquals( "response payload has wrong value", path + "-put", response.getResponseText() );

        response= client.delete();

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be DELETED", ResponseCode.DELETED, response.getCode() );
        assertEquals( "response payload has wrong value", path + "-delete", response.getResponseText() );
    }

    @Test(timeout= 5000)
    public void testPutOnly() throws ConnectorException, IOException
    {
        String path= "/different_listeners/all";
        String path1= "/different_listeners/put_only";
        setClientPath( path1 );

        CoapResponse response= client.get();

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CONTENT", ResponseCode.CONTENT, response.getCode() );
        assertEquals( "response payload has wrong value", path + "-get", response.getResponseText() );

        response= client.post( path, 0 );

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CHANGED", ResponseCode.CHANGED, response.getCode() );
        assertEquals( "response payload has wrong value", path + "-post", response.getResponseText() );

        response= client.put( path, 0 );

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CHANGED", ResponseCode.CHANGED, response.getCode() );
        assertEquals( "response payload has wrong value", path1 , response.getResponseText() );

        response= client.delete();

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be DELETED", ResponseCode.DELETED, response.getCode() );
        assertEquals( "response payload has wrong value", path + "-delete", response.getResponseText() );
    }

    @Test(timeout= 5000)
    public void testDeleteOnly() throws ConnectorException, IOException
    {
        String path= "/different_listeners/all";
        String path1= "/different_listeners/delete_only";
        setClientPath( path1 );

        CoapResponse response= client.get();

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CONTENT", ResponseCode.CONTENT, response.getCode() );
        assertEquals( "response payload has wrong value", path + "-get", response.getResponseText() );

        response= client.post( path, 0 );

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CHANGED", ResponseCode.CHANGED, response.getCode() );
        assertEquals( "response payload has wrong value", path + "-post", response.getResponseText() );

        response= client.put( path, 0 );

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CHANGED", ResponseCode.CHANGED, response.getCode() );
        assertEquals( "response payload has wrong value", path + "-put", response.getResponseText() );

        response= client.delete();

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be DELETED", ResponseCode.DELETED, response.getCode() );
        assertEquals( "response payload has wrong value", path1, response.getResponseText() );
    }

    @Test
    public void testNoListeners() throws ConnectorException, IOException
    {
        String path= "/no_listeners";
        setClientPath( path );

        CoapResponse response= client.get();

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be INTERNAL_SERVER_ERROR", ResponseCode.INTERNAL_SERVER_ERROR, response.getCode() );
        assertEquals( "response payload has wrong value", "NO LISTENER", response.getResponseText() );

        response= client.post( path, 0 );

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be INTERNAL_SERVER_ERROR", ResponseCode.INTERNAL_SERVER_ERROR, response.getCode() );
        assertEquals( "response payload has wrong value", "NO LISTENER", response.getResponseText() );

        response= client.put( path, 0 );

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be INTERNAL_SERVER_ERROR", ResponseCode.INTERNAL_SERVER_ERROR, response.getCode() );
        assertEquals( "response payload has wrong value", "NO LISTENER", response.getResponseText() );

        response= client.delete();

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be INTERNAL_SERVER_ERROR", ResponseCode.INTERNAL_SERVER_ERROR, response.getCode() );
        assertEquals( "response payload has wrong value", "NO LISTENER", response.getResponseText() );
    }
}
