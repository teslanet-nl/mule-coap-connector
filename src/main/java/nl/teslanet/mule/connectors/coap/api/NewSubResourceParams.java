/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2024 (teslanet.nl) Rogier Cobben
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
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * New-sub-resource parameters. 
 *
 */
public class NewSubResourceParams implements NewSubResource
{
    /**
     * If true, put requests are allowed to create sub-resources.
     */
    @Parameter
    @Expression( ExpressionSupport.SUPPORTED )
    @ParameterDsl( allowReferences= true )
    @Summary( "If true, put requests are allowed to create sub-resources." )
    private boolean put= false;

    /**
     * If true an acknowledgement is immediately sent to the client, before processing the create request.
     * Use this when creating the new sub-resource takes longer than the acknowledgment-timeout of the client.  
     */
    @Parameter
    @Expression( ExpressionSupport.SUPPORTED )
    @ParameterDsl( allowReferences= true )
    @Summary( "If true an acknowledgement is immediately sent to the client, before processing the create request." )
    private boolean earlyAck= false;

    /**
     * Default constructor of NewSubResourceParams.
     */
    public NewSubResourceParams()
    {
    }

    /**
     * Constructor of NewSubResourceParams.
     */
    public NewSubResourceParams( boolean put, boolean earlyAck )
    {
        this.put= put;
        this.earlyAck= earlyAck;
    }

    /**
     * Copy constructor of NewSubResourceParams.
     */
    public NewSubResourceParams( NewSubResourceParams config )
    {
        this.put= config.put;
        this.earlyAck= config.earlyAck;
    }

    /**
     * @return The put flag.
     */
    @Override
    public boolean isPut()
    {
        return put;
    }

    /**
     * @return The early ack flag.
     */
    @Override
    public boolean isEarlyAck()
    {
        return earlyAck;
    }
}
