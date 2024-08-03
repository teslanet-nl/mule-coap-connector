/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2024 (teslanet.nl) Rogier Cobben
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


import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.option.MapBasedOptionRegistry;
import org.eclipse.californium.core.coap.option.OptionDefinition;
import org.eclipse.californium.core.coap.option.StandardOptionRegistry;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.CoapEndpoint.Builder;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.elements.config.Configuration;
import org.junit.After;
import org.junit.Before;


/**
 * Get method basic tests
 *
 */
public abstract class AbstractServerTestCase extends AbstractTestCase
{
    /**
     * The CoAP client for testing CoAP servers.
     */
    protected CoapClient client= null;

    /**
     * Setup test client
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        OptionDefinition[] critical= {
            TestOptions.OTHER_OPTION_65003,
            TestOptions.OTHER_OPTION_65009,
            TestOptions.OTHER_OPTION_65012,
            TestOptions.OTHER_OPTION_65013,
            TestOptions.OTHER_OPTION_65308 };

        URI uri= new URI( "coap", "127.0.0.1", null, null );
        client= new CoapClient( uri );
        client.setTimeout( 1000000L );
        Builder builder= CoapEndpoint.builder();
        builder.setConfiguration( Configuration.createStandardWithoutFile() );
        MapBasedOptionRegistry allOptions= new MapBasedOptionRegistry( StandardOptionRegistry.getDefaultOptionRegistry(), critical );
        builder.setOptionRegistry( allOptions );
        client.setEndpoint( builder.build() );
    }

    /**
     * Destroy test-client and default endpoints.
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception
    {
        if ( client != null )
        {
            client.getEndpoint().destroy();
            client.shutdown();
            client= null;
        }
        EndpointManager.reset();
    }

    /**
    * Set the default resource-path that the client uses.
    * @param uri to use in subsequent calls by client
    */
    protected void setClientUri( String uri )
    {
        URI clientUri;
        try
        {
            clientUri= new URI( client.getURI() );
        }
        catch ( URISyntaxException e )
        {
            throw new RuntimeException( e );
        }
        client.setURI( clientUri.resolve( uri ).toString() );
    }
}
