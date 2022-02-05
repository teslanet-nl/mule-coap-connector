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
package nl.teslanet.mule.connectors.coap.api;


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * General definitions.
 *
 */
public class Defs
{
    /**
     * CoAP defined character set.
     */
    public static final Charset COAP_CHARSET= StandardCharsets.UTF_8;

    public static final String COAP_URI_WILDCARD= "*";

    public static final String COAP_URI_PATHSEP= "/";

    public static final String COAP_URI_ROOTRESOURCE= COAP_URI_PATHSEP;

    public static final String COAP_URI_ROOTRESOURCE_NAME= "";

    /**
     * No need to instantiate.
     */
    private Defs()
    {
        //NOOP
    }
}
