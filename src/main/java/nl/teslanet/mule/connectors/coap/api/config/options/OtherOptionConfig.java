/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2023 - 2024 (teslanet.nl) Rogier Cobben
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
     * The format of the other option.
     */
    @Parameter
    @Optional( defaultValue= "OPAQUE" )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The format of the other option." )
    @Example( "INTEGER" )
    protected OptionFormat format= OptionFormat.OPAQUE;

    /**
     * The multiplicity of the other option.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The type of the other option." )
    protected boolean singleValue= true;

    /**
     * The value of the other option.
     */
    @Parameter
    @Optional( defaultValue= "0" )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The minimum number of bytes of the option." )
    @Example( "1" )
    protected int minBytes= 0;

    /**
     * The value of the other option.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The maximum number of bytes of the option." )
    @Example( "4" )
    protected int maxBytes= Integer.MAX_VALUE;

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
     * @return The option format.
     */
    public OptionFormat getFormat()
    {
        return format;
    }

    /**
     * @param format The option format to set.
     */
    public void setFormat( OptionFormat format )
    {
        this.format= format;
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
    public void setMinBytes( int minBytes )
    {
        this.minBytes= minBytes;
    }

    /**
     * @return The maxBytes.
     */
    public int getMaxBytes()
    {
        return maxBytes;
    }

    /**
     * @param maxBytes The maxBytes to set.
     */
    public void setMaxBytes( int maxBytes )
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
     * Constructor with parameters.
     * @param alias The option alias.
     * @param number The option number.
     * @param format The option format.
     * @param singleValue The option alias.
     * @param minBytes The minimum length in bytes, may be null.
     * @param maxBytes The maximum length in bytes, may be null.
     */
    public OtherOptionConfig( String alias, int number, OptionFormat format, boolean singleValue, int minBytes, int maxBytes )
    {
        super();
        this.alias= alias;
        this.number= number;
        this.format= format;
        this.singleValue= singleValue;
        this.minBytes= minBytes;
        this.maxBytes= maxBytes;
    }
}
