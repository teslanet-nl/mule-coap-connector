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
package nl.teslanet.mule.connectors.coap.api;


/**
 * Endpoint info.
 */
public class EndpointInfo
{
    /**
     * Protocol scheme.
     */
    private String scheme;

    /**
     * The host to which is bound.
     */
    private String boundToHost;

    /**
     * The port to which is bound.
     */
    private int boundToPort;

    /**
     * Constructor
     * @param scheme The protocol used by the endpoint.
     * @param boundToHost The host to which is bound.
     * @param boundToPort The port to which is bound.
     */
    public EndpointInfo( String scheme, String boundToHost, int boundToPort )
    {
        super();
        this.scheme= scheme;
        this.boundToHost= boundToHost;
        this.boundToPort= boundToPort;
    }

    /**
     * @return the scheme
     */
    public String getScheme()
    {
        return scheme;
    }

    /**
     * @return the boundToHost
     */
    public String getBoundToHost()
    {
        return boundToHost;
    }

    /**
     * @return the boundToPort
     */
    public int getBoundToPort()
    {
        return boundToPort;
    }
}
