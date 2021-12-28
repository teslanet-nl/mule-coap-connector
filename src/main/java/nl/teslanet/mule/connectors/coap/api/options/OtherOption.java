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
package nl.teslanet.mule.connectors.coap.api.options;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Query Parameter with expression support. 
 */
public class OtherOption
{
    /**
     * The number of the other option.
     */
    @Parameter
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "The number of the other option." )
    @Example( "65001" )
    protected int number;

    /**
     * The value of the other option.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "The value of the other option." )
    @Example( "#[ Binaries::fromHex('1122ff') ]" )
    protected TypedValue< Object > value= null;

    /**
     * @return The number of the option.
     */
    public int getNumber()
    {
        return number;
    }

    /**
     * @param optionNumber The optionNumber to set
     */
    public void setNumber( int optionNumber )
    {
        this.number= optionNumber;
    }

    /**
     * @return The value of the option.
     */
    public TypedValue< Object > getValue()
    {
        return value;
    }

    /**
     * @param optionValue The option value to set
     */
    public void setValue( TypedValue< Object > optionValue )
    {
        this.value= optionValue;
    }
}
