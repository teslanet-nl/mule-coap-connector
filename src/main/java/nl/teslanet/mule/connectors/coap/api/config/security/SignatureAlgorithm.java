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
package nl.teslanet.mule.connectors.coap.api.config.security;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Configuration of the signature algorithm with the hash algorithm to use. 
 */
public class SignatureAlgorithm
{
    /**
     * The signature algorithm to use.
     */
    @Parameter
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The signature algorithm to use." )
    public SignatureAlgorithmName algorithm= null;

    /**
     * The hash algorithm to use.
     */
    @Parameter
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The hash algorithm to use." )
    public HashAlgorithmName hashAlgorithm= null;

    /**
     * Default constructor.
     */
    public SignatureAlgorithm()
    {
        super();
    }

    /**
     * Constructor.
     * @param hashAlgorithm The hash algorithm to use.
     * @param signatureAlgorithm The signature algorithm to use.
     */
    public SignatureAlgorithm( HashAlgorithmName hashAlgorithm, SignatureAlgorithmName algorithm )
    {
        super();
        this.hashAlgorithm= hashAlgorithm;
        this.algorithm= algorithm;
    }

    /**
     * String value of this algorithm configuration.
     */
    @Override
    public String toString()
    {
        StringBuilder builder= new StringBuilder();
        builder.append( hashAlgorithm == null ? String.valueOf( hashAlgorithm ) : hashAlgorithm.name() );
        builder.append( "with" );
        builder.append( algorithm == null ? String.valueOf( algorithm ) : algorithm.name() );
        return builder.toString();
    }
}
