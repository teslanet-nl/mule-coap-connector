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
 * Enum with TLS supported groups.
 */
public enum SupportedGroupName
{
    sect163k1,
    sect163r1,
    sect163r2,
    sect193r1,
    sect193r2,
    sect233k1,
    sect233r1,
    sect239k1,
    sect283k1,
    sect283r1,
    sect409k1,
    sect409r1,
    sect571k1,
    sect571r1,
    secp160k1,
    secp160r1,
    secp160r2,
    secp192k1,
    secp192r1,
    secp224k1,
    secp224r1,
    secp256k1,
    secp256r1,
    secp384r1,
    secp521r1,
    brainpoolP256r1,
    brainpoolP384r1,
    brainpoolP512r1,
    X25519,
    X448,
    ffdhe2048,
    ffdhe3072,
    ffdhe4096,
    ffdhe6144,
    ffdhe8192,
    arbitrary_explicit_prime_curves,
    arbitrary_explicit_char2_curves;
}