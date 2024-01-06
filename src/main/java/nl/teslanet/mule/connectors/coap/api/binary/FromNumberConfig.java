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
package nl.teslanet.mule.connectors.coap.api.binary;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;


/**
 * Bytes constructed from a number config.
 *
 */
public class FromNumberConfig implements BytesConfig
{
    /**
     * The long value from which bytes are formed.
     */
    @Parameter
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    private Long numberValue= 0L;

    /**
     * Default constructor.
     */
    public FromNumberConfig()
    {
        super();
    }

    /**
     * Constructor.
     * @param numberValue
     */
    public FromNumberConfig( Long numberValue )
    {
        super();
        this.numberValue= numberValue;
    }

    /**
     * @return the numberValue
     */
    public Long getNumberValue()
    {
        return numberValue;
    }

    /**
     * @param numberValue the numberValue to set
     */
    public void setNumberValue( Long numberValue )
    {
        this.numberValue= numberValue;
    }

    /**
     * @return the value as byte array.
     */
    @Override
    public byte[] getByteArray()
    {
        return OptionUtils.toBytes( numberValue );
    }
}
