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
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;


/**
 * Bytes constructed from a string config.
 *
 */
public class FromStringConfig implements BytesConfig
{
    /**
     * The string value from which utf8 bytes are formed.
     */
    @Parameter
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String stringValue= OptionUtils.EMPTY_STRING;

    /**
     * Default constructor.
     */
    public FromStringConfig()
    {
        super();
    }

    /**
     * Constructor.
     * @param stringValue The key value.
     */
    public FromStringConfig( String stringValue )
    {
        super();
        this.stringValue= stringValue;
    }

    /**
     * @return the stringValue
     */
    public String getStringValue()
    {
        return stringValue;
    }

    /**
     * @param stringValue the stringValue to set
     */
    public void setStringValue( String stringValue )
    {
        this.stringValue= stringValue;
    }

    /**
     * @return the value as byte array.
     * @throws OptionValueException When conversion failed.
     */
    @Override
    public byte[] getByteArray() throws OptionValueException
    {
        return OptionUtils.toBytes( stringValue );
    }
}
