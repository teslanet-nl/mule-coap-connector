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
package nl.teslanet.mule.connectors.coap.test.utils;


import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.EndpointManager;


/**
 * Get method basic tests
 *
 */
public abstract class AbstractClientTestCase extends AbstractTestCase
{
    /**
     * The CoAP servers for testing Mule CoAP client.
     */
    private ArrayList< CoapServer > servers= null;

    /**
     * Setup test server
     * @throws Exception
     */
    @Before
    public void setUpServers() throws Exception
    {
        servers= new ArrayList< CoapServer >();
        CoapServer server= getTestServer();
        if ( server != null )
        {
            servers.add( server );
            server.start();
        }
        CoapServer[] serversToAdd= getTestServers();
        if ( serversToAdd != null )
        {
            for ( CoapServer serverToAdd : serversToAdd )
            {
                servers.add( serverToAdd );
                serverToAdd.start();
            }
        }

    }

    /**
     * @return the server to use in tests
     * @throws Exception when server can not be delivered
     */
    protected CoapServer getTestServer() throws Exception
    {
        return null;
    };

    /**
     * @return the server to use in tests
     * @throws Exception when servers can not be delivered
     */
    protected CoapServer[] getTestServers() throws Exception
    {
        return null;
    };

    /**
     * Destroy test-server and default endpoints.
     */
    @After
    public void tearDownServers()
    {
        if ( servers != null )
        {
            for ( CoapServer toStop : servers )
            {
                toStop.stop();
                toStop.destroy();
            }
            servers.clear();
            servers= null;
        }
        EndpointManager.reset();
    }
}
