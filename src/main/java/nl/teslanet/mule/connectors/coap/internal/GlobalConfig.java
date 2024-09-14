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
package nl.teslanet.mule.connectors.coap.internal;


import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.californium.core.coap.option.OptionDefinition;
import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.config.options.OtherOptionConfig;
import nl.teslanet.mule.connectors.coap.internal.utils.MessageUtils;


/**
 *  Global connector configuration.
 */
@Configuration( name= "global-config" )
public class GlobalConfig implements Initialisable, Disposable
{
    //TODO non critical options need to be understood as well. Not suitable for proxy.. 

    /**
     * The hosted resources.
     */
    private static final ConcurrentHashMap< String, OptionDefinition > optionDefinitions= new ConcurrentHashMap<>();

    /**
     * Get the definition of an other option.
     * @param alias The alias of the other option.
     * @return The other option definition, or empty if not found.
     */
    public static Optional< OptionDefinition > getOtherOptionDefinition( String alias )
    {
        if ( alias == null ) return Optional.empty();
        return Optional.ofNullable( optionDefinitions.get( alias ) );
    }

    /**
    * The list of other options that the endpoint understands. 
    * Messages containing critical options that are not understood will be refused.
    * Elective options that are not understood will be ignored.
    */
    @Parameter
    @Summary( value= "The other option definitions. A definition is referenced by its alias" )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowInlineDefinition= true, allowReferences= false )
    private List< OtherOptionConfig > otherOptionConfigs= new CopyOnWriteArrayList<>();

    /**
     * Default Constructor used by Mule. 
     * Mandatory and Nullsafe params are set by Mule.
     */
    public GlobalConfig()
    {
        //NOOP
    }

    /**
     * Constructor for manually constructing the endpoint.
     * (Mule uses default constructor and sets Nullsafe params.)
     * @param otherOptionConfigs List of expected other options.
     */
    public GlobalConfig( List< OtherOptionConfig > otherOptionConfigs )
    {
        this.otherOptionConfigs= otherOptionConfigs;
    }

    /**
     * Initialize configuration.
     */
    @Override
    public void initialise() throws InitialisationException
    {
        otherOptionConfigs
            .forEach(
                otherOptionConfig -> optionDefinitions
                    .put( otherOptionConfig.getAlias(), MessageUtils.toCfOptionDefinition( otherOptionConfig ) )
            );
    }

    /**
     * Dispose configuration.
     */
    @Override
    public void dispose()
    {
        optionDefinitions.clear();
    }
}
