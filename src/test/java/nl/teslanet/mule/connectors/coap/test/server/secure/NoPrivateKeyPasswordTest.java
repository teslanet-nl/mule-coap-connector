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
package nl.teslanet.mule.connectors.coap.test.server.secure;


import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isA;
import static org.junit.internal.matchers.ThrowableCauseMatcher.hasCause;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.lifecycle.LifecycleException;

import nl.teslanet.mule.connectors.coap.internal.exceptions.EndpointConstructionException;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractMuleStartTestCase;


public class NoPrivateKeyPasswordTest extends AbstractMuleStartTestCase
{
    @Override
    protected void expectException()
    {
        exception.expect( isA( LifecycleException.class ) );
        exception.expect( hasMessage( containsString( "nl.teslanet.mule.transport.coap.server" ) ) );
        exception.expect( hasCause( isA( ConnectionException.class ) ) );
        exception.expect( hasCause( hasMessage( containsString( "CoAP configuration error" ) ) ) );
        exception.expect( hasCause( hasCause( isA( EndpointConstructionException.class ) ) ) );
        exception.expect( hasCause( hasCause( hasMessage( containsString( "cannot construct secure endpoint" ) ) ) ) );
        exception.expect( hasCause( hasCause( hasCause( isA( NullPointerException.class ) ) ) ) );
        exception.expect( hasCause( hasCause( hasCause( hasMessage( containsString( "keyPassword must be provided" ) ) ) ) ) );
    }

    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/secure/testserver8.xml";
    };
}
