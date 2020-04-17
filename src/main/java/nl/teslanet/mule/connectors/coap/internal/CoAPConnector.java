/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2020 (teslanet.nl) Rogier Cobben
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


import javax.inject.Inject;

import org.mule.runtime.api.scheduler.SchedulerConfig;
import org.mule.runtime.api.scheduler.SchedulerService;
import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Export;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.SubTypeMapping;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.teslanet.mule.connectors.coap.api.config.congestion.BasicRto;
import nl.teslanet.mule.connectors.coap.api.config.congestion.Cocoa;
import nl.teslanet.mule.connectors.coap.api.config.congestion.CocoaStrong;
import nl.teslanet.mule.connectors.coap.api.config.congestion.CongestionControl;
import nl.teslanet.mule.connectors.coap.api.config.congestion.LinuxRto;
import nl.teslanet.mule.connectors.coap.api.config.congestion.PeakhopperRto;
import nl.teslanet.mule.connectors.coap.api.config.deduplication.CropRotation;
import nl.teslanet.mule.connectors.coap.api.config.deduplication.Deduplicator;
import nl.teslanet.mule.connectors.coap.api.config.deduplication.MarkAndSweep;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.DTLSEndpoint;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.Endpoint;
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
import nl.teslanet.mule.connectors.coap.api.error.Errors;
import nl.teslanet.mule.connectors.coap.api.options.ETag;
import nl.teslanet.mule.connectors.coap.internal.client.Client;
import nl.teslanet.mule.connectors.coap.internal.server.Server;


/**
 * A Mule extension that adds CoAP functionality to create CoAP clients and CoAP servers.
 */
@Xml(prefix= "coap", namespace= "http://www.teslanet.nl/schema/mule/coap")
@Extension(name= "CoAP", vendor= "Teslanet.nl")
@SubTypeMapping(baseType= Endpoint.class, subTypes= {
    UDPEndpoint.class,
    MulticastUDPEndpoint.class,
    DTLSEndpoint.class,
    TCPServerEndpoint.class,
    TCPClientEndpoint.class,
    TLSServerEndpoint.class,
    TLSClientEndpoint.class })
@SubTypeMapping(baseType= MidTracker.class, subTypes= { NullMidTracker.class, GroupedMidTracker.class, MapBasedMidTracker.class })
@SubTypeMapping(baseType= CongestionControl.class, subTypes= { Cocoa.class, CocoaStrong.class, BasicRto.class, LinuxRto.class, PeakhopperRto.class })
@SubTypeMapping(baseType= Deduplicator.class, subTypes= { CropRotation.class, MarkAndSweep.class })
@Configurations({ Server.class, Client.class })
@Export(classes= { ETag.class })
@ErrorTypes(Errors.class)
public class CoAPConnector
{
    /**
     * The logger.
     */
    private static final Logger LOGGER= LoggerFactory.getLogger( CoAPConnector.class );

    /**
     * The Scheduler service.
     */
    private static SchedulerService schedulerService= null;

    /**
     * The scheduler configuration.
     */
    private static SchedulerConfig schedulerConfig= null;

    /**
     * The IO Light scheduler.
     */
    private static Scheduler ioScheduler= null;

    /**
     * The CPU Light scheduler.
     */
    private static Scheduler lightScheduler= null;

    /**
     * Set the IO scheduler supplied by Mule.
     * @return IO scheduler.
     */
    public static synchronized void setSchedulerService( SchedulerService schedulerServiceCandidate, SchedulerConfig schedulerConfigCandidate )
    {
        if ( schedulerService == null && schedulerServiceCandidate != null )
        {
            schedulerService= schedulerServiceCandidate;
            schedulerConfig= schedulerConfigCandidate;
            LOGGER.info( "CoAP schedulerService registered" );
        }
    }

    /**
     * Get the IO scheduler supplied by Mule.
     * @return IO scheduler.
     */
    public static synchronized Scheduler getIoScheduler()
    {
        if ( ioScheduler == null )
        {
            ioScheduler= schedulerService.ioScheduler( schedulerConfig.withName( "CoAP IO scheduler" ) );
            LOGGER.info( "CoAP IO scheduler is started" );
        }
        return ioScheduler;
    }

    /**
     * Get the Light scheduler supplied by Mule.
     * @return Light scheduler.
     */
    public static synchronized Scheduler getLightScheduler()
    {
        if ( lightScheduler == null )
        {
            lightScheduler= schedulerService.cpuLightScheduler( schedulerConfig.withName( "CoAP Light scheduler" ) );
            LOGGER.info( "CoAP CPU Light scheduler is started" );
        }
        return lightScheduler;
    }

    /**
     * Stop the IO scheduler.
     */
    public static synchronized void stopIoScheduler()
    {
        if ( ioScheduler != null )
        {
            ioScheduler.stop();
            ioScheduler= null;
            LOGGER.info( "CoAP IO scheduler is stopped" );
        }
    }

    /**
     * Stop the Light scheduler.
     */
    public static synchronized void stopLightScheduler()
    {
        if ( lightScheduler != null )
        {
            lightScheduler.stop();
            lightScheduler= null;
            LOGGER.info( "CoAP CPU Light scheduler is stopped" );
        }
    }
}
