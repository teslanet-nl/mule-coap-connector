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
package nl.teslanet.mule.connectors.coap.internal;


import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.Sources;
import org.mule.runtime.extension.api.annotation.SubTypeMapping;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.sdk.api.annotation.JavaVersionSupport;
import org.mule.sdk.api.annotation.MinMuleVersion;
import org.mule.sdk.api.meta.JavaVersion;

import nl.teslanet.mule.connectors.coap.api.Proxy;
import nl.teslanet.mule.connectors.coap.api.ProxyConfig;
import nl.teslanet.mule.connectors.coap.api.RemoteEndpoint;
import nl.teslanet.mule.connectors.coap.api.RemoteEndpointConfig;
import nl.teslanet.mule.connectors.coap.api.SharedServer;
import nl.teslanet.mule.connectors.coap.api.SharedServerConfig;
import nl.teslanet.mule.connectors.coap.api.config.congestion.BasicRto;
import nl.teslanet.mule.connectors.coap.api.config.congestion.Cocoa;
import nl.teslanet.mule.connectors.coap.api.config.congestion.CocoaStrong;
import nl.teslanet.mule.connectors.coap.api.config.congestion.CongestionControl;
import nl.teslanet.mule.connectors.coap.api.config.congestion.LinuxRto;
import nl.teslanet.mule.connectors.coap.api.config.congestion.PeakhopperRto;
import nl.teslanet.mule.connectors.coap.api.config.deduplication.CropRotation;
import nl.teslanet.mule.connectors.coap.api.config.deduplication.Deduplicator;
import nl.teslanet.mule.connectors.coap.api.config.deduplication.MarkAndSweep;
import nl.teslanet.mule.connectors.coap.api.config.deduplication.PeersMarkAndSweep;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DefaultReplayFilter;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsClientAndServerRole;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsClientRole;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsEndpointRole;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsServerRole;
import nl.teslanet.mule.connectors.coap.api.config.dtls.ExtendedReplayFilter;
import nl.teslanet.mule.connectors.coap.api.config.dtls.NoReplayFilter;
import nl.teslanet.mule.connectors.coap.api.config.dtls.ReplayFilter;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.DTLSEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.MulticastUDPEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPClientEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TCPServerEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TLSClientEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.TLSServerEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.UDPEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.GroupedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.MapBasedMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.MidTracker;
import nl.teslanet.mule.connectors.coap.api.config.midtracker.NullMidTracker;
import nl.teslanet.mule.connectors.coap.api.config.security.KeyConfig;
import nl.teslanet.mule.connectors.coap.api.config.security.KeyFromHex;
import nl.teslanet.mule.connectors.coap.api.config.security.KeyFromNumber;
import nl.teslanet.mule.connectors.coap.api.config.security.KeyFromString;
import nl.teslanet.mule.connectors.coap.api.error.Errors;
import nl.teslanet.mule.connectors.coap.internal.client.Client;
import nl.teslanet.mule.connectors.coap.internal.client.ResponseListener;
import nl.teslanet.mule.connectors.coap.internal.server.Server;


/**
 * Mule extension that adds the capability for Mule applications to act as CoAP client and/or CoAP server.
 */
@Xml( prefix= "coap", namespace= "http://www.teslanet.nl/schema/mule/coap" )
@Extension( name= "CoAP", vendor= "Teslanet.nl" )
@SubTypeMapping( baseType= AbstractEndpoint.class, subTypes=
{ UDPEndpoint.class, MulticastUDPEndpoint.class, DTLSEndpoint.class, TCPServerEndpoint.class, TCPClientEndpoint.class,
    TLSServerEndpoint.class, TLSClientEndpoint.class } )
@SubTypeMapping( baseType= DtlsEndpointRole.class, subTypes=
{ DtlsServerRole.class, DtlsClientRole.class, DtlsClientAndServerRole.class } )
@SubTypeMapping( baseType= ReplayFilter.class, subTypes=
{ NoReplayFilter.class, DefaultReplayFilter.class, ExtendedReplayFilter.class } )
@SubTypeMapping( baseType= MidTracker.class, subTypes=
{ NullMidTracker.class, GroupedMidTracker.class, MapBasedMidTracker.class } )
@SubTypeMapping( baseType= CongestionControl.class, subTypes=
{ Cocoa.class, CocoaStrong.class, BasicRto.class, LinuxRto.class, PeakhopperRto.class } )
@SubTypeMapping( baseType= Deduplicator.class, subTypes=
{ CropRotation.class, MarkAndSweep.class, PeersMarkAndSweep.class } )
@SubTypeMapping( baseType= RemoteEndpoint.class, subTypes=
{ SharedServer.class, Proxy.class } )
@SubTypeMapping( baseType= RemoteEndpointConfig.class, subTypes=
{ SharedServerConfig.class, ProxyConfig.class } )
@SubTypeMapping( baseType= KeyConfig.class, subTypes=
{ KeyFromHex.class, KeyFromNumber.class, KeyFromString.class } )
@Configurations(
    { Server.class, Client.class, GlobalConfig.class }
)
@Sources( value=
{ ResponseListener.class } )
@Operations( GlobalOperations.class )
@ErrorTypes( Errors.class )
@JavaVersionSupport(
    { JavaVersion.JAVA_8, JavaVersion.JAVA_11, JavaVersion.JAVA_17 }
)
@MinMuleVersion( value= "4.3" )
public class CoapConnector
{
    private CoapConnector()
    {
        //NOOP
    }
}
