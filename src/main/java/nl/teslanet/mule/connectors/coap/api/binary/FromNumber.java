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
package nl.teslanet.mule.connectors.coap.api.binary;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;


/**
 * Bytes constructed from a number value.
 *
 */
public class FromNumber implements BytesParams
{
    /**
     * The long value from which bytes are formed.
     */
    @Parameter
    @Expression( ExpressionSupport.SUPPORTED )
    @ParameterDsl( allowReferences= true )
    private long numberValue= 0L;

    /**
     * Default constructor.
     */
    public FromNumber()
    {
        super();
    }

    /**
     * Constructor.
     * @return the numberValue
     */
    public long getNumberValue()
    {
        return numberValue;
    }

    /**
     * @param numberValue the numberValue to set
     */
    public void setNumberValue( long numberValue )
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

    /**
     * @param numberValue
     */
    public FromNumber( Long numberValue )
    {
        super();
        this.numberValue= numberValue;
    }
}
