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
package nl.teslanet.mule.connectors.coap.api.options;


import java.io.InputStream;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.util.IOUtils;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.ExclusiveOptionals;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;


/**
 * Option value parameters.
 * The value is formed in accordance with Coap rules.
 * When no value is given, the result is an empty bytes value.
 */
@ExclusiveOptionals( isOneRequired= false )
public class OptionValueParams
{
    /**
     * The binary from which bytes are formed.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @ParameterDsl( allowReferences= true )
    private InputStream fromBinary= null;

    /**
     * The long value from which bytes are formed.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @ParameterDsl( allowReferences= true )
    private Long fromNumber= null;

    /**
     * The hexadecimal value from which bytes are formed.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @ParameterDsl( allowReferences= true )
    private String fromHex= null;

    /**
     * The string value from which utf8 bytes are formed.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @ParameterDsl( allowReferences= true )
    private String fromString= null;

    /**
     * @return the fromBinary
     */
    public InputStream getFromBinary()
    {
        return fromBinary;
    }

    /**
     * @return the fromNumber
     */
    public Long getFromNumber()
    {
        return fromNumber;
    }

    /**
     * @param fromNumber the fromNumber to set
     */
    public void setFromNumber( Long fromNumber )
    {
        this.fromNumber= fromNumber;
    }

    /**
     * @param fromBinary the fromBinary to set
     */
    public void setFromBinary( InputStream fromBinary )
    {
        this.fromBinary= fromBinary;
    }

    /**
     * @return the fromHex
     */
    public String getFromHex()
    {
        return fromHex;
    }

    /**
     * @param fromHex the fromHex to set
     */
    public void setFromHex( String fromHex )
    {
        this.fromHex= fromHex;
    }

    /**
     * @return the fromString
     */
    public String getFromString()
    {
        return fromString;
    }

    /**
     * @param fromString the fromString to set
     */
    public void setFromString( String fromString )
    {
        this.fromString= fromString;
    }

    /**
     * Get the bytes value.
     * @return The value as byte array.
     * @throws OptionValueException When conversion failed.
     */
    public byte[] getByteArray() throws OptionValueException
    {
        if ( fromBinary != null ) return IOUtils.toByteArray( fromBinary );
        if ( fromNumber != null ) return OptionUtils.toBytes( fromNumber );
        if ( fromHex != null ) return OptionUtils.toBytesFromHex( fromHex );
        if ( fromString != null ) return OptionUtils.toBytes( fromString );
        return OptionUtils.EMPTY_BYTES;
    }
}
