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
package nl.teslanet.mule.connectors.coap.api.config;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.config.options.OtherOptionConfig;


/**
 * Configuration of multi-cast.
 *
 */
public class OptionParams implements VisitableConfig
{
    //TODO non critical options need to be understood as well. Not suitable for proxy.. 
    /**
    * The list of other options that the endpoint understands. 
    * Messages containing critical options that are not understood will be refused.
    * Elective options that are not understood will be ignored.
    */
    @Parameter
    @Summary(
                    value= "The other options that the endpoint understands. " + "\nMessages containing critical options that are not understood will be rejected. "
                        + "\nElective options that are not understood will be ignored."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowInlineDefinition= true, allowReferences= false )
    public List< OtherOptionConfig > otherOptionConfigs;

    /**
     * Default Constructor used by Mule. 
     * Mandatory and Nullsafe params are set by Mule.
     */
    public OptionParams()
    {
        otherOptionConfigs= new CopyOnWriteArrayList<>();
    }

    /**
     * Constructor for manually constructing the endpoint.
     * (Mule uses default constructor and sets Nullsafe params.)
     * @param otherOptionConfigs List of expected other options.
     */
    public OptionParams( List< OtherOptionConfig > otherOptionConfigs )
    {
        this.otherOptionConfigs= otherOptionConfigs;
    }

    /**
     * Accept visitor.
     */
    @Override
    public void accept( ConfigVisitor visitor ) throws ConfigException
    {
        visitor.visit( this );
    }
}
