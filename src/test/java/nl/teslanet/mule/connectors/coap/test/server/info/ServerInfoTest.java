/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2025 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.test.server.info;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.test.utils.AbstractServerTestCase;


/**
 * Server info operation tests
 *
 */
public class ServerInfoTest extends AbstractServerTestCase
{
    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/serverinfo/testserver1.xml";
    };

    @Test
    public void testServerInfo() throws ConnectorException, IOException
    {
        String uri= "coap://localhost/serverinfo";
        setClientUri( uri );

        CoapResponse response= client.get();

        assertNotNull( "get gave no response", response );
        assertEquals( "response code should be CONTENT", ResponseCode.CONTENT, response.getCode() );
        assertTrue(
            "response payload has wrong value",
            response
                .getResponseText()
                .matches( "^config:coap,0\\.0\\.0\\.0,5683;coaps,127\\.0\\.0\\.1,5699;coap,0\\.0\\.0\\.0,[1-9][0-9]{4,};$" )
        );
    }
}
