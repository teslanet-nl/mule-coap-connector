/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2024 (teslanet.nl) Rogier Cobben
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


import java.util.List;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.config.ConfigException;
import nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor;
import nl.teslanet.mule.connectors.coap.api.config.VisitableConfig;


/**
 * Configuration of security parameters.
 *
 */
public class SecurityParams implements VisitableConfig
{    
    /**
     * The pre shared key configuration.
     */
    @Parameter
    @Optional
    @Summary( value= "The pre shared key configuration." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public PreSharedKeyParams preSharedKeyParams=null;
    
    /**
     * The keystore configuration.
     */
    @Parameter
    @Optional
    @Summary( value= "The keystore configuration." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public KeyStore keyStore= null;

    /**
     * The truststore configuration.
     */
    @Parameter
    @Optional
    @Summary( value= "The truststore configuration." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public TrustStore trustStore= null;

    /**
     * Truncate certificate path according the available trusted certificates before validation.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Summary( value= "Truncate certificate path according the available trusted certificates before validation." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean truncateCertificatePathForValidation= true;

    /**
     * Use recommended cipher suites only.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Summary( value= "Use recommended cipher suites only." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean recommendedCipherSuitesOnly= true;

    /**
     * Use recommended ECC curves/groups only.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Summary( value= "Use recommended ECC curves/groups only." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean recommendedCurvesOnly= true;

    /**
     * Use recommended signature- and hash-algorithms only.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Summary( value= "Use recommended signature- and hash-algorithms only." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean recommendedSignatureAndHashAlgorithmsOnly= true;

    /**
     * List of preselected cipher suites.
     */
    @Parameter
    @Optional
    @NullSafe
    @Summary( value= "List cipher suites that are preselected." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public List< CipherSuite > preselectedCipherSuites= null;

    /**
     * List of preselected cipher suites.
     */
    @Parameter
    @Optional
    @NullSafe
    @Summary( value= "Cipher suite(s) to use. Leave empty when preselection list is given." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public List< CipherSuite > cipherSuites= null;

    /**
     * List of DTLS curves (supported groups).\nDefaults to all supported curves of the JCE at runtime.
     */
    @Parameter
    @Optional
    @NullSafe
    @Summary( value= "List of DTLS curves (supported groups).\nDefaults to all supported curves of the JCE at runtime." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public List< Curve > curves= null;

    /**
     * List of supported signature and hash algorithms with hash algorithms.\nDefaults to SHA256 with ECDSA and SHA256 with RSA.
     */
    @Parameter
    @Optional
    @NullSafe
    @Summary( value= "List of supported signature and hash algorithms with hash algorithms.\nDefaults to SHA256 with ECDSA and SHA256 with RSA." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public List< SignatureAlgorithm > signatureAlgorithms= null;

    /**
     * List of supported certificate key algorithms.
     */
    @Parameter
    @Optional
    @NullSafe
    @Summary( value= "List of supported certificate key algorithms." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public List< CertificateKeyAlgorithm > certificateKeyAlgorithms= null;

    /**
     * Extended master secret mode.
     */
    @Parameter
    @Optional( defaultValue= "ENABLED" )
    @Summary( value= "The extended master secret mode to use." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public ExtendedMasterSecretModeName extendedMasterSecretMode= ExtendedMasterSecretModeName.ENABLED;

    /**
     * Support use of Connection ID's. (rfc9146)
     */
    @Parameter
    @Optional
    @Summary( value= "Support use of Connection ID's. (rfc9146)" )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public ConnectionId supportConnectionId= null;

    /**
     * Accept visitor.
     */
    @Override
    public void accept( ConfigVisitor visitor ) throws ConfigException
    {
        visitor.visit( this );
        if ( preSharedKeyParams != null ) preSharedKeyParams.accept( visitor );
        if ( keyStore != null ) keyStore.accept( visitor );
        if ( trustStore != null ) trustStore.accept( visitor );
        if ( supportConnectionId != null ) supportConnectionId.accept( visitor );
    }
}
