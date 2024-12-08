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
package nl.teslanet.mule.connectors.coap.api.config.security;


/**
 * Enum with supported cipher suites.
 *
 */
public enum CipherSuiteName
{
    TLS_ECDHE_PSK_WITH_AES_128_GCM_SHA256,
    TLS_ECDHE_PSK_WITH_AES_256_GCM_SHA384,
    TLS_ECDHE_PSK_WITH_AES_128_CCM_8_SHA256,
    TLS_ECDHE_PSK_WITH_AES_128_CCM_SHA256,
    TLS_PSK_WITH_AES_128_GCM_SHA256,
    TLS_PSK_WITH_AES_256_GCM_SHA384,
    TLS_PSK_WITH_AES_128_CCM_8,
    TLS_PSK_WITH_AES_256_CCM_8,
    TLS_PSK_WITH_AES_128_CCM,
    TLS_PSK_WITH_AES_256_CCM,
    TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA256,
    TLS_PSK_WITH_AES_128_CBC_SHA256,
    TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
    TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,
    TLS_ECDHE_ECDSA_WITH_AES_128_CCM_8,
    TLS_ECDHE_ECDSA_WITH_AES_256_CCM_8,
    TLS_ECDHE_ECDSA_WITH_AES_128_CCM,
    TLS_ECDHE_ECDSA_WITH_AES_256_CCM,
    TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256,
    TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384,
    TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,

    // RSA Certificates
    TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
    TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
    TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,
    TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384,
    TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,

    /**
     * Cipher suite indicating client support for secure renegotiation.
     */
    TLS_EMPTY_RENEGOTIATION_INFO_SCSV;
}