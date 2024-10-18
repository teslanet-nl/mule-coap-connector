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
package nl.teslanet.mule.connectors.coap.api.config.dtls;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
 * Configuration items that are DTLS specific.
 *
 */
public class DtlsParams implements VisitableConfig
{
    /**
     * The maximum number of active connections.
     */
    @Parameter
    @Optional( defaultValue= "150000" )
    @Summary( value= "The maximum number of active connections." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer maxConnections= null;

    /**
     * Stale connections (no messages are exchanged within the threshold)
     * can get removed for new connections.
     */
    @Parameter
    @Optional( defaultValue= "30m" )
    @Summary(
                    value= "Stale connections (no messages are exchanged within the threshold)\n can get removed for new connections."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String staleConnectionThreshold= null;

    /**
     * Update the ip-address from DTLS 1.2 CID records only for newer records
     * based on epoch/sequence_number.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Summary(
                    value= "Update the ip-address from DTLS 1.2 CID records only for newer records\n"
                        + "based on epoch/sequence_number."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean updateAddressOnNewerCidRecords= true;

    /**
     * Remove stale connections, if the principal has also a newer connection. 
     * Intended to free heap earlier for dynamic shared systems,
     * Requires to have unique principals.
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @Summary(
                    value= "Remove stale connections, if the principal has also a newer connection. \nRequires to have unique principals."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean removeStaleDoublePrincipals= false;

    /**
     * Maximum number of jobs for outbound messages.
     * The value must be 64 or larger.
     */
    @Parameter
    @Optional( defaultValue= "50000" )
    @Summary( value= "Maximum number of jobs for outbound messages.\nThe value must be 64 or larger." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer outboundMsgCapacity= null;

    /**
     * Maximum number of jobs for inbound messages.
     * (Value must be 64 or larger).
     */
    @Parameter
    @Optional( defaultValue= "50000" )
    @Summary( value= "Maximum number of jobs for inbound messages.\nThe value must be 64 or larger." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer inboundMsgCapacity= null;

    /**
     * The number of receiver threads.
     * Default value is 2 if number of CORES > 3, otherwise 1.
     */
    @Parameter
    @Optional
    @Summary( value= "The number of receiver threads. \nDefault value is 2 if number of CORES > 3, otherwise 1." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer dtlsReceiverThreadCount= null;

    /**
     * The number of connector threads. 
     * These threads handle most cryptographic
     * functions for both incoming and outgoing messages.
     * Default value is the number of CORES.
     */
    @Parameter
    @Optional
    @Summary(
                    value= "The number of connector threads. \n" + "These threads handle most cryptographic \n"
                        + "functions for both incoming and outgoing messages. \nDefault value is the number of CORES."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer dtlsConnectorThreadCount= null;

    /**
     * Maximum number of jobs for handshake results.
     * (Value must be 64 or larger).
     */
    @Parameter
    @Optional( defaultValue= "5000" )
    @Summary( value= "Maximum number of jobs for handshake results.\nThe value must be 64 or larger." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer handshakeCapacity= null;

    /**
     * The size [Bytes] of the buffer for Handshake records with future handshake message sequence number or
     * records with future epochs.
     */
    @Parameter
    @Optional( defaultValue= "8192" )
    @Summary(
                    value= "The size [Bytes] of the buffer for Handshake records with future handshake message sequence number \nor records with future epochs."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer handshakeRecordBufferSize= null;

    /**
     * The maximum number of messages sent during a handshake that are processed deferred after the handshake.
     * Above this number messages are dropped.
     */
    @Parameter
    @Optional( defaultValue= "10" )
    @Summary(
                    value= "The maximum number of messages sent during a handshake that are processed deferred after the handshake.\nAbove this number messages are dropped."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer deferredMsgCapacity= null;

    /**
     * The period in milliseconds [ms] after which, without exchanged messages, 
     * new messages will initiate a handshake. Clients use {@code 30000ms} as 
     * a common value to compensate assumed NAT timeouts. Not used by servers.
     */
    @Parameter
    @Optional
    @Summary(
                    value= "The period after which, without exchanged messages, \n"
                        + "new messages will initiate a handshake. Clients use {@code 30[s]} as \n"
                        + "a common value to compensate assumed NAT timeouts. Not used by servers.\n"
                        + "Default no auto handshake is initiated"
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public String autoHandshakeTimeout= null;

    /**
     * The threshold defines the maximum percentage of handshakes without HELLO_VERIFY_REQUEST.
     * When 0 peers are verified on all resumptions.
     */
    @Parameter
    @Optional( defaultValue= "30" )
    @Summary(
                    value= "The threshold defines the maximum percentage of handshakes without HELLO_VERIFY_REQUEST.\nWhen 0 peers are verified on all resumptions."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer verifyPeersOnResumptionThreshold= null;

    /**
     * Use anti replay filter.
     * @see <a href= "https://tools.ietf.org/html/rfc6347#section-4.1.2.6"
     *      target= "_blank">RFC6347 4.1.2.6. Anti-Replay</a>
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Summary( value= "Use anti replay filter." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean useAntiReplyFilter= true;

    /**
     * Use extended anti replay filter window passing postponed records.
     * @see <a href= "https://tools.ietf.org/html/rfc6347#section-4.1.2.6"
     *      target= "_blank">RFC6347 4.1.2.6. Anti-Replay</a>
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @Summary( value= "Use anti replay filter." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean useExtendedAntiReplayFilterWindow= false;

    /**
     * Limit the anti replay filter window extension to given number of postponed records.
     */
    @Parameter
    @Optional
    @Summary( value= "Limit the anti replay filter window extension to given number of postponed records." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer antiReplyFilterWindowExtension= null;

    /**
     * Accept visitor.
     */
    @Override
    public void accept( ConfigVisitor visitor ) throws ConfigException
    {
        visitor.visit( this );
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj )
    {
        if ( obj == null )
        {
            return false;
        }
        if ( obj == this )
        {
            return true;
        }
        if ( obj.getClass() != getClass() )
        {
            return false;
        }
        DtlsParams rhs= (DtlsParams) obj;
        return new EqualsBuilder()
            .append( maxConnections, rhs.maxConnections )
            .append( staleConnectionThreshold, rhs.staleConnectionThreshold )
            .append( updateAddressOnNewerCidRecords, rhs.updateAddressOnNewerCidRecords )
            .append( removeStaleDoublePrincipals, rhs.removeStaleDoublePrincipals )
            .append( outboundMsgCapacity, rhs.outboundMsgCapacity )
            .append( inboundMsgCapacity, rhs.inboundMsgCapacity )
            .append( dtlsReceiverThreadCount, rhs.dtlsReceiverThreadCount )
            .append( dtlsConnectorThreadCount, rhs.dtlsConnectorThreadCount )
            .append( handshakeCapacity, rhs.handshakeCapacity )
            .append( handshakeRecordBufferSize, rhs.handshakeRecordBufferSize )
            .append( deferredMsgCapacity, rhs.deferredMsgCapacity )
            .append( autoHandshakeTimeout, rhs.autoHandshakeTimeout )
            .append( verifyPeersOnResumptionThreshold, rhs.verifyPeersOnResumptionThreshold )
            .append( useAntiReplyFilter, rhs.useAntiReplyFilter )
            .append( useExtendedAntiReplayFilterWindow, rhs.useExtendedAntiReplayFilterWindow )
            .append( antiReplyFilterWindowExtension, rhs.antiReplyFilterWindowExtension )
            .isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 15, 35 )
            .append( maxConnections )
            .append( staleConnectionThreshold )
            .append( updateAddressOnNewerCidRecords )
            .append( removeStaleDoublePrincipals )
            .append( outboundMsgCapacity )
            .append( inboundMsgCapacity )
            .append( dtlsReceiverThreadCount )
            .append( dtlsConnectorThreadCount )
            .append( handshakeCapacity )
            .append( handshakeRecordBufferSize )
            .append( deferredMsgCapacity )
            .append( autoHandshakeTimeout )
            .append( verifyPeersOnResumptionThreshold )
            .append( useAntiReplyFilter )
            .append( useExtendedAntiReplayFilterWindow )
            .append( antiReplyFilterWindowExtension )
            .toHashCode();
    }

}
