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
package nl.teslanet.mule.connectors.coap.api.config.security;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;


/**
 * Key constructed from a number config.
 *
 */
public class KeyFromNumber implements KeyConfig
{
    /**
     * The long value from which the key is formed.
     * String type is needed for Password annotation.
     */
    @Parameter
    @Password
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( value= "The long value from which the key is formed." )
    private String numberValue= "0";

    /**
     * Default constructor.
     */
    public KeyFromNumber()
    {
        super();
    }

    /**
     * Constructor.
     * @param numberValue
     */
    public KeyFromNumber( Long numberValue )
    {
        super();
        this.numberValue= numberValue.toString();
    }

    /**
     * @return the numberValue
     */
    public Long getNumberValue()
    {
        return Long.valueOf( numberValue );
    }

    /**
     * @param numberValue the numberValue to set
     */
    public void setNumberValue( Long numberValue )
    {
        this.numberValue= numberValue.toString();
    }

    /**
     * @return the value as byte array.
     */
    @Override
    public byte[] getByteArray()
    {
        return OptionUtils.toBytes( Long.valueOf( numberValue ) );
    }
}
