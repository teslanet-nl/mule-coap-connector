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
package nl.teslanet.mule.connectors.coap.test.server.modules;


import java.io.InputStream;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;

import nl.teslanet.mule.connectors.coap.api.CoAPRequestAttributes;


/**
 * Test class for testing connector modules that use SourceCallBack instances.
 *
 */
public class TestSourceCallBack implements SourceCallback< InputStream, CoAPRequestAttributes >
{

    /* (non-Javadoc)
     * @see org.mule.runtime.extension.api.runtime.source.SourceCallback#handle(org.mule.runtime.extension.api.runtime.operation.Result)
     */
    @Override
    public void handle( Result< InputStream, CoAPRequestAttributes > result )
    {
        // not needed for test
    }

    /* (non-Javadoc)
     * @see org.mule.runtime.extension.api.runtime.source.SourceCallback#handle(org.mule.runtime.extension.api.runtime.operation.Result, org.mule.runtime.extension.api.runtime.source.SourceCallbackContext)
     */
    @Override
    public void handle( Result< InputStream, CoAPRequestAttributes > result, SourceCallbackContext context )
    {
        // not needed for test         
    }

    /* (non-Javadoc)
     * @see org.mule.runtime.extension.api.runtime.source.SourceCallback#onConnectionException(org.mule.runtime.api.connection.ConnectionException)
     */
    @Override
    public void onConnectionException( ConnectionException e )
    {
        // not needed for test         
    }

    /* (non-Javadoc)
     * @see org.mule.runtime.extension.api.runtime.source.SourceCallback#createContext()
     */
    @Override
    public SourceCallbackContext createContext()
    {
        // not needed for test
        return null;
    }
}
