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


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.config.ConfigException;
import nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor;
import nl.teslanet.mule.connectors.coap.api.config.VisitableConfig;


/**
 * Support dtls connection id (CID). 
 * Normally the IP address and port of the peer are used to identify the connection.
 * In some cases, such as NAT rebinding, this is insufficient. 
 * When using CID each party sends the value in the "connection_id" extension it wants to 
 * receive as a CID in encrypted records to do a connection lookup independent of network address.
 * <a href= "https://www.rfc-editor.org/rfc/rfc9146.html" target
 * ="_blank">RFC 9146, Connection Identifier for DTLS 1.2</a>
 */
public class ConnectionId implements VisitableConfig
{
    /**
     * Local connection ID length in [Bytes]. 
     * When empty remote peers connection ID is supported only. 
     * When set it's recommended to have 100 time more values than peers. 
     * E.g. 65000 peers then choose at least 3 bytes.
     */
    @Parameter
    @Optional
    @Summary(
                    value= "Local connection ID length in [BYTES]. " + "\nWhen empty remote peers connection ID is used only. "
                        + "\nWhen set it's recommended to have 100 time more values than peers. " + "\nE.g. 65000 peers then choose at least 3 bytes."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer connectionIdLength= null;

    /**
     * Update the ip-address from CID records only for newer records.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Summary( value= "Update the ip-address from CID records only for newer records." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean updateAddressOnNewerRecords= true;

    /**
     * Accept visitor.
     */
    @Override
    public void accept( ConfigVisitor visitor ) throws ConfigException
    {
        visitor.visit( this );
    }

}
