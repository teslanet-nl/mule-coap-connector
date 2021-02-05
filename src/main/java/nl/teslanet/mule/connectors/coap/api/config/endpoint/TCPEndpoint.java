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
package nl.teslanet.mule.connectors.coap.api.config.endpoint;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

import nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor;
import nl.teslanet.mule.connectors.coap.api.config.TcpParams;


/**
 * Abstract TCP coap endpoint
 *
 */
abstract public class TCPEndpoint extends Endpoint
{
    /**
     * The tcpParams to use.
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Placement(order= 1)
    public TcpParams tcpParams;

    /**
     * Default Constructor used by Mule. 
     * Mandatory and Nullsafe params are set by Mule.
     */
    public TCPEndpoint()
    {
        super();
    }

    /**
     * Constructor for manually constructing the endpoint.
     * (Mule uses default constructor and sets Nullsafe params.)
     * @param name the manually set name of the endpoint
     */
    public TCPEndpoint( String name )
    {
        super( name );
        tcpParams= new TcpParams();
    }

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.VisitableConfig#accept(nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor)
     */
    @Override
    public void accept( ConfigVisitor visitor )
    {
        super.accept( visitor );
        visitor.visit( this );
        tcpParams.accept( visitor );
    }
}
