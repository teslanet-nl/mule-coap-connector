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
package nl.teslanet.mule.connectors.coap.internal.utils;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.scandium.dtls.ExtendedMasterSecretMode;
import org.eclipse.californium.scandium.dtls.SignatureAndHashAlgorithm;
import org.eclipse.californium.scandium.dtls.SignatureAndHashAlgorithm.HashAlgorithm;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite.CertificateKeyAlgorithm;
import org.eclipse.californium.scandium.dtls.cipher.XECDHECryptography.SupportedGroup;

import nl.teslanet.mule.connectors.coap.api.config.security.CertificateKeyAlgorithmName;
import nl.teslanet.mule.connectors.coap.api.config.security.CipherSuiteName;
import nl.teslanet.mule.connectors.coap.api.config.security.Curve;
import nl.teslanet.mule.connectors.coap.api.config.security.ExtendedMasterSecretModeName;
import nl.teslanet.mule.connectors.coap.api.config.security.HashAlgorithmName;
import nl.teslanet.mule.connectors.coap.api.config.security.SignatureAlgorithm;
import nl.teslanet.mule.connectors.coap.api.config.security.SignatureAlgorithmName;
import nl.teslanet.mule.connectors.coap.api.config.security.SupportedGroupName;


/**
 * Utilities for security classes.
 *
 */
public class SecurityUtils
{
    /**
     * Do not create objects.
     */
    private SecurityUtils()
    {
        //NOOP
    }

    /**
     * Convert Cipher suite name to Cf CipherSuite.
     * @param name The name of the Cipher suite to convert.
     * @return The Cf CipherSuite object, or {@code null} when not found.
     */
    public static org.eclipse.californium.scandium.dtls.cipher.CipherSuite toCfCipherSuite( CipherSuiteName name )
    {
        if ( name != null )
        {
            switch ( name )
            {
                case TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256:
                    return CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256;
                case TLS_ECDHE_ECDSA_WITH_AES_128_CCM:
                    return CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CCM;
                case TLS_ECDHE_ECDSA_WITH_AES_128_CCM_8:
                    return CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CCM_8;
                case TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256:
                    return CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256;
                case TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA:
                    return CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA;
                case TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384:
                    return CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384;
                case TLS_ECDHE_ECDSA_WITH_AES_256_CCM:
                    return CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CCM;
                case TLS_ECDHE_ECDSA_WITH_AES_256_CCM_8:
                    return CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CCM_8;
                case TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384:
                    return CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384;
                case TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA256:
                    return CipherSuite.TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA256;
                case TLS_ECDHE_PSK_WITH_AES_128_CCM_8_SHA256:
                    return CipherSuite.TLS_ECDHE_PSK_WITH_AES_128_CCM_8_SHA256;
                case TLS_ECDHE_PSK_WITH_AES_128_CCM_SHA256:
                    return CipherSuite.TLS_ECDHE_PSK_WITH_AES_128_CCM_SHA256;
                case TLS_ECDHE_PSK_WITH_AES_128_GCM_SHA256:
                    return CipherSuite.TLS_ECDHE_PSK_WITH_AES_128_GCM_SHA256;
                case TLS_ECDHE_PSK_WITH_AES_256_GCM_SHA384:
                    return CipherSuite.TLS_ECDHE_PSK_WITH_AES_256_GCM_SHA378;
                case TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256:
                    return CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256;
                case TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256:
                    return CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256;
                case TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA:
                    return CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA;
                case TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384:
                    return CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384;
                case TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384:
                    return CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384;
                case TLS_EMPTY_RENEGOTIATION_INFO_SCSV:
                    return CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV;
                case TLS_PSK_WITH_AES_128_CBC_SHA256:
                    return CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA256;
                case TLS_PSK_WITH_AES_128_CCM:
                    return CipherSuite.TLS_PSK_WITH_AES_128_CCM;
                case TLS_PSK_WITH_AES_128_CCM_8:
                    return CipherSuite.TLS_PSK_WITH_AES_128_CCM_8;
                case TLS_PSK_WITH_AES_128_GCM_SHA256:
                    return CipherSuite.TLS_PSK_WITH_AES_128_GCM_SHA256;
                case TLS_PSK_WITH_AES_256_CCM:
                    return CipherSuite.TLS_PSK_WITH_AES_256_CCM;
                case TLS_PSK_WITH_AES_256_CCM_8:
                    return CipherSuite.TLS_PSK_WITH_AES_256_CCM_8;
                case TLS_PSK_WITH_AES_256_GCM_SHA384:
                    return CipherSuite.TLS_PSK_WITH_AES_256_GCM_SHA378;
                default:
                    break;
            }
        }
        return null;
    }

    /**
     * Convert Cipher suite list to Cf CipherSuites.
     * @param suites The list of Cipher suites to convert.
     * @return The list of Cf CipherSuite objects.
     */
    public static List< CipherSuite > toCfCipherSuites( List< nl.teslanet.mule.connectors.coap.api.config.security.CipherSuite > suites )
    {
        ArrayList< org.eclipse.californium.scandium.dtls.cipher.CipherSuite > cfSuites= new ArrayList<>();
        for ( nl.teslanet.mule.connectors.coap.api.config.security.CipherSuite suite : suites )
        {
            CipherSuite cfSuite= toCfCipherSuite( suite.getCipherSuiteName() );
            if ( cfSuite != null ) cfSuites.add( cfSuite );
        }
        return cfSuites;
    }

    /**
     * Convert curve name to Cf SupportedGroup.
     * @param name The name of the curve to convert.
     * @return The Cf SupportedGroup object, or {@code null} when not found.
     */
    public static SupportedGroup toCfCurve( SupportedGroupName name )
    {
        if ( name != null )
        {
            switch ( name )
            {
                case X25519:
                    return SupportedGroup.X25519;
                case X448:
                    return SupportedGroup.X448;
                case arbitrary_explicit_char2_curves:
                    return SupportedGroup.arbitrary_explicit_char2_curves;
                case arbitrary_explicit_prime_curves:
                    return SupportedGroup.arbitrary_explicit_prime_curves;
                case brainpoolP256r1:
                    return SupportedGroup.brainpoolP256r1;
                case brainpoolP384r1:
                    return SupportedGroup.brainpoolP384r1;
                case brainpoolP512r1:
                    return SupportedGroup.brainpoolP512r1;
                case ffdhe2048:
                    return SupportedGroup.ffdhe2048;
                case ffdhe3072:
                    return SupportedGroup.ffdhe3072;
                case ffdhe4096:
                    return SupportedGroup.ffdhe4096;
                case ffdhe6144:
                    return SupportedGroup.ffdhe6144;
                case ffdhe8192:
                    return SupportedGroup.ffdhe8192;
                case secp160k1:
                    return SupportedGroup.secp160k1;
                case secp160r1:
                    return SupportedGroup.secp160r1;
                case secp160r2:
                    return SupportedGroup.secp160r2;
                case secp192k1:
                    return SupportedGroup.secp192k1;
                case secp192r1:
                    return SupportedGroup.secp192r1;
                case secp224k1:
                    return SupportedGroup.secp224k1;
                case secp224r1:
                    return SupportedGroup.secp224r1;
                case secp256k1:
                    return SupportedGroup.secp256k1;
                case secp256r1:
                    return SupportedGroup.secp256r1;
                case secp384r1:
                    return SupportedGroup.secp384r1;
                case secp521r1:
                    return SupportedGroup.secp521r1;
                case sect163k1:
                    return SupportedGroup.sect163k1;
                case sect163r1:
                    return SupportedGroup.sect163r1;
                case sect163r2:
                    return SupportedGroup.sect163r2;
                case sect193r1:
                    return SupportedGroup.sect193r1;
                case sect193r2:
                    return SupportedGroup.sect193r2;
                case sect233k1:
                    return SupportedGroup.sect233k1;
                case sect233r1:
                    return SupportedGroup.sect233r1;
                case sect239k1:
                    return SupportedGroup.sect239k1;
                case sect283k1:
                    return SupportedGroup.sect283k1;
                case sect283r1:
                    return SupportedGroup.sect283r1;
                case sect409k1:
                    return SupportedGroup.sect409k1;
                case sect409r1:
                    return SupportedGroup.sect409r1;
                case sect571k1:
                    return SupportedGroup.sect571k1;
                case sect571r1:
                    return SupportedGroup.sect571r1;
                default:
                    break;
            }
        }
        return null;
    }

    /**
     * Convert Curves list to Cf Curves.
     * @param curves The list of curves to convert.
     * @return The list of Cf Curve objects.
     */
    public static List< SupportedGroup > toCfCurves( List< Curve > curves )
    {
        ArrayList< SupportedGroup > cfCurves= new ArrayList<>();
        for ( Curve curve : curves )
        {
            SupportedGroup cfCurve= toCfCurve( curve.getCurveName() );
            if ( cfCurve != null ) cfCurves.add( cfCurve );
        }
        return cfCurves;
    }

    /**
     * Convert signature and hash algorithm to Cf algorithm
     * @param hashAlgorithm The hash algoritms
     * @param signatureAlgorithm
     * @return The Cf Algorithm.
     */
    public static SignatureAndHashAlgorithm toCfSignatureAndHashAlgoritm( HashAlgorithmName hashAlgorithm, SignatureAlgorithmName signatureAlgorithm )
    {
        HashAlgorithm cfHhashAlgorithm;
        SignatureAndHashAlgorithm.SignatureAlgorithm cfSignatureAlgorithm;
        switch ( hashAlgorithm )
        {
            case MD5:
                cfHhashAlgorithm= HashAlgorithm.MD5;
                break;
            case NONE:
                cfHhashAlgorithm= HashAlgorithm.NONE;
                break;
            case SHA1:
                cfHhashAlgorithm= HashAlgorithm.SHA1;
                break;
            case SHA224:
                cfHhashAlgorithm= HashAlgorithm.SHA224;
                break;
            case SHA256:
                cfHhashAlgorithm= HashAlgorithm.SHA256;
                break;
            case SHA384:
                cfHhashAlgorithm= HashAlgorithm.SHA384;
                break;
            case SHA512:
                cfHhashAlgorithm= HashAlgorithm.SHA512;
                break;
            case INTRINSIC:
            default:
                cfHhashAlgorithm= HashAlgorithm.INTRINSIC;
                break;
        }
        switch ( signatureAlgorithm )
        {
            case DSA:
                cfSignatureAlgorithm= SignatureAndHashAlgorithm.SignatureAlgorithm.DSA;
                break;
            case ECDSA:
                cfSignatureAlgorithm= SignatureAndHashAlgorithm.SignatureAlgorithm.ECDSA;
                break;
            case ED25519:
                cfSignatureAlgorithm= SignatureAndHashAlgorithm.SignatureAlgorithm.ED25519;
                break;
            case ED448:
                cfSignatureAlgorithm= SignatureAndHashAlgorithm.SignatureAlgorithm.ED448;
                break;
            case RSA:
                cfSignatureAlgorithm= SignatureAndHashAlgorithm.SignatureAlgorithm.RSA;
                break;
            case ANONYMOUS:
            default:
                cfSignatureAlgorithm= SignatureAndHashAlgorithm.SignatureAlgorithm.ANONYMOUS;
                break;
        }
        return new SignatureAndHashAlgorithm( cfHhashAlgorithm, cfSignatureAlgorithm );
    }

    /**
     * Convert signature and hash algorithms to Cf algorithms
     * @param signatureAlgorithms The list of algoritms to convert.
     * @return The converted list.
     */
    public static List< SignatureAndHashAlgorithm > toCfSignatureAndHashAlgoritms( List< SignatureAlgorithm > signatureAlgorithms )
    {
        ArrayList< SignatureAndHashAlgorithm > cfAlgorithms= new ArrayList<>();
        for ( SignatureAlgorithm algortithm : signatureAlgorithms )
        {
            SignatureAndHashAlgorithm cfAlgoritm= toCfSignatureAndHashAlgoritm( algortithm.hashAlgorithm, algortithm.algorithm );
            cfAlgorithms.add( cfAlgoritm );
        }
        return cfAlgorithms;
    }

    /**
     * Convert CertificateKeyAlgorithmName name to Cf CertificateKeyAlgorithm.
     * @param name The name of the algorithm to convert.
     * @return The Cf CertificateKeyAlgorithm object, or {@code null} when not found.
     */
    public static CertificateKeyAlgorithm toCfCertificateKeyAlgorithm( CertificateKeyAlgorithmName name )
    {
        if ( name != null )
        {
            switch ( name )
            {
                case EC:
                    return CertificateKeyAlgorithm.EC;
                case RSA:
                    return CertificateKeyAlgorithm.RSA;
                default:
                    break;
            }
        }
        return null;
    }

    /**
     * Convert CertificateKeyAlgorithmNames list to Cf CertificateKeyAlgorithms.
     * @param certificateKeyAlgorithms The list of algorithmNames to convert.
     * @return The list of Cf Algorithm objects.
     */
    public static List< CertificateKeyAlgorithm > toCfCertificateKeyAlgorithms(
        List< nl.teslanet.mule.connectors.coap.api.config.security.CertificateKeyAlgorithm > certificateKeyAlgorithms
    )
    {
        ArrayList< CertificateKeyAlgorithm > cfAlgorithms= new ArrayList<>();
        for ( nl.teslanet.mule.connectors.coap.api.config.security.CertificateKeyAlgorithm algorithm : certificateKeyAlgorithms )
        {
            CertificateKeyAlgorithm cfAlgorithm= toCfCertificateKeyAlgorithm( algorithm.getAlgorithm() );
            if ( cfAlgorithm != null ) cfAlgorithms.add( cfAlgorithm );
        }
        return cfAlgorithms;
    }

    /**
     * Convert ExtendedMasterSecretModeName to Cf ExtendedMasterSecretMode
     * @param extendedMasterSecretMode The mode to convert.
     * @return The converted mode.
     */
    public static ExtendedMasterSecretMode toCfExtendedMasterSecretMode( ExtendedMasterSecretModeName extendedMasterSecretMode )
    {
        if ( extendedMasterSecretMode != null )
        {
            switch ( extendedMasterSecretMode )
            {
                case ENABLED:
                    return ExtendedMasterSecretMode.ENABLED;
                case NONE:
                    return ExtendedMasterSecretMode.NONE;
                case OPTIONAL:
                    return ExtendedMasterSecretMode.OPTIONAL;
                case REQUIRED:
                    return ExtendedMasterSecretMode.REQUIRED;
                default:
                    break;
            }
        }
        return null;
    }
}
