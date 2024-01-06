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


import java.io.InputStream;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.util.IOUtils;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;


/**
 * Bytes constructed from a binary value.
 *
 */
public class FromBinary implements BytesValue
{
    /**
     * The binary value.
     */
    @Parameter
    @Expression( ExpressionSupport.SUPPORTED )
    @ParameterDsl( allowReferences= true )
    private InputStream binaryValue= null;

    /**
     * Default constructor.
     */
    public FromBinary()
    {
        super();
    }

    /**
     * Constructor.
     * @return the binaryValue
     */
    public InputStream getBinaryValue()
    {
        return binaryValue;
    }

    /**
     * @param binaryValue the bytesValue to set
     */
    public void setBinaryValue( InputStream binaryValue )
    {
        this.binaryValue= binaryValue;
    }

    /**
     * @return the value as byte array.
     */
    @Override
    public byte[] getByteArray()
    {
        return IOUtils.toByteArray( binaryValue );
    }

    /**
     * @param binaryValue
     */
    public FromBinary( InputStream binaryValue )
    {
        super();
        this.binaryValue= binaryValue;
    }
}
