/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2023 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.api.config.dtls;


import org.mule.runtime.extension.api.annotation.param.ParameterGroup;

import nl.teslanet.mule.connectors.coap.api.config.ConfigException;
import nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor;


/**
 * Configuration items that are DTLS specific.
 *
 */
public class DtlsServerRole implements DtlsEndpointRole
{
    /**
     * DTLS server parameters.
     */
    @ParameterGroup( name= "DTLS server params " )
    private DtlsServerParams dtlsServerParams;

    /**
     * Default constructor.
     */
    public DtlsServerRole()
    {
        super();
        this.dtlsServerParams= new DtlsServerParams();
    }

    /**
     * Constructor.
     * @param dtlsServerParams The DTLS server parameters.
     */
    public DtlsServerRole( DtlsServerParams dtlsServerParams )
    {
        super();
        this.dtlsServerParams= dtlsServerParams;
    }

    /**
     * Accept visitor.
     */
    @Override
    public void accept( ConfigVisitor visitor ) throws ConfigException
    {
        visitor.visit( this );
        dtlsServerParams.accept( visitor );
    }
}
