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
package nl.teslanet.mule.connectors.coap.api.config.options;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Configuration of other options. 
 */
public class OtherOptionConfig
{
    /**
     * The name of the other option.
     */
    @Parameter
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The number of the other option." )
    @Example( "myCustomOption" )
    protected String alias;

    /**
     * The number of the other option.
     */
    @Parameter
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The number of the other option." )
    @Example( "65001" )
    protected int number;

    /**
     * The type of the other option.
     */
    @Parameter
    @Optional( defaultValue= "OPAQUE" )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The type of the other option." )
    @Example( "INTEGER" )
    protected OptionType optionType= OptionType.OPAQUE;

    /**
     * The multiplicity of the other option.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The type of the other option." )
    @Example( "INTEGER" )
    protected boolean singleValue= true;

    /**
     * The value of the other option.
     */
    @Parameter
    @Optional( defaultValue= "0" )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The minimum number of bytes of the option." )
    @Example( "0" )
    protected Integer minBytes= 0;

    /**
     * The value of the other option.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The maximum number of bytes of the option." )
    @Example( "4" )
    protected Integer maxBytes= Integer.MAX_VALUE;

    /**
     * @return The alias.
     */
    public String getAlias()
    {
        return alias;
    }

    /**
     * @param alias The alias to set.
     */
    public void setAlias( String alias )
    {
        this.alias= alias;
    }

    /**
     * @return The number.
     */
    public int getNumber()
    {
        return number;
    }

    /**
     * @param number The number to set.
     */
    public void setNumber( int number )
    {
        this.number= number;
    }

    /**
     * @return The optionType.
     */
    public OptionType getOptionType()
    {
        return optionType;
    }

    /**
     * @param optionType The optionType to set.
     */
    public void setOptionType( OptionType optionType )
    {
        this.optionType= optionType;
    }

    /**
     * @return The singleValue.
     */
    public boolean isSingleValue()
    {
        return singleValue;
    }

    /**
     * @param singleValue The singleValue to set.
     */
    public void setSingleValue( boolean singleValue )
    {
        this.singleValue= singleValue;
    }

    /**
     * @return The minBytes.
     */
    public Integer getMinBytes()
    {
        return minBytes;
    }

    /**
     * @param minBytes The minBytes to set.
     */
    public void setMinBytes( Integer minBytes )
    {
        this.minBytes= minBytes;
    }

    /**
     * @return The maxBytes.
     */
    public Integer getMaxBytes()
    {
        return maxBytes;
    }

    /**
     * @param maxBytes The maxBytes to set.
     */
    public void setMaxBytes( Integer maxBytes )
    {
        this.maxBytes= maxBytes;
    }

    /**
     * Default constructor.
     */
    public OtherOptionConfig()
    {
        super();
    }

    /**
     * Constructor
     * @param alias
     * @param number
     */
    public OtherOptionConfig( String alias, int number )
    {
        super();
        this.alias= alias;
        this.number= number;
    }

    /**
     * Constructor with options.
     * @param alias
     * @param number
     * @param optionType
     * @param singleValue
     * @param minBytes
     * @param maxBytes
     */
    public OtherOptionConfig( String alias, int number, OptionType optionType, boolean singleValue, Integer minBytes, Integer maxBytes )
    {
        super();
        this.alias= alias;
        this.number= number;
        this.optionType= optionType;
        this.singleValue= singleValue;
        this.minBytes= minBytes;
        this.maxBytes= maxBytes;
    }
}
