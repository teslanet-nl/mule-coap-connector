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


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Placement;


/**
 * The parameters describing an address of an server to access.
 *
 */
public abstract class AbstractAddressParams
{
    /**
     * The hostname or ip of the server to reach. 
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @Placement( order= 41 )
    @Example("californium.eclipseprojects.io")
    private String host= null;

    /**
     * The port of the server to reach.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @Placement( order= 42 )
    @Example("5683")
    private Integer port= null;

    /**
     * The shared server or proxy that will forward requests.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @Placement( order= 111, tab= Placement.ADVANCED_TAB )
    private RemoteEndpoint remoteEndpoint= null;

    /**
     * @return the remoteEndpoint
     */
    public RemoteEndpoint getRemoteEndpoint()
    {
        return remoteEndpoint;
    }

    /**
     * @param remoteEndpoint the remoteEndpoint to set
     */
    public void setRemoteEndpoint( RemoteEndpoint remoteEndpoint )
    {
        this.remoteEndpoint= remoteEndpoint;
    }

    /**
     * @return the host
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost( String host )
    {
        this.host= host;
    }

    /**
     * @return the port
     */
    public Integer getPort()
    {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort( Integer port )
    {
        this.port= port;
    }
}
