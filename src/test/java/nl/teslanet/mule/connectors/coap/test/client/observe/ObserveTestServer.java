/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2021 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.test.client.observe;


import java.net.SocketException;
import java.util.Date;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;


/**
 * Server used to test observing client 
 *
 */
/**
 * @author rogier
 *
 */
class ObserveTestServer extends CoapServer
{
    /**
     * Default Constructor for test server.
     */
    public ObserveTestServer() throws SocketException
    {
        this( CoAP.DEFAULT_COAP_PORT );
    }

    /**
     * Constructor for test server.
     */
    public ObserveTestServer( int port ) throws SocketException
    {
        super( NetworkConfig.createStandardWithoutFile(), port );
        addResources();
    }

    private void addResources()
    {
        // provide an instance of an observable resource
        add( new ObservableResource( "observe" ) );
        Resource parent= getRoot().getChild( "observe" );
        parent.add( new ObservableResource( "temporary" ) );
        parent.add( new ObservableResource( "temporary2" ) );
        parent.add( new ObservableResource( "permanent" ) );
        parent.add( new ObservableResource( "maxage1", 1, 1 ) );
        parent.add( new ObservableResource( "maxage1_nonotify", 1, 0 ) );
        parent.add( new ObservableResource( "maxage4_nonotify", 4, 0 ) );
    }

    /**
     * Resource that to test payloads
     */
    class ObservableResource extends CoapResource
    {
        /**
         * maxAge option to set when > 0
         */
        private long maxAge= 0;

        /**
         * notification period when > 0
         */
        private long notify= 0;

        /**
         * notify thread that triggers notifications, null when not needed
         */
        private NotifyThread notifyThread= null;

        /**
         * the resource content
         */
        private String content;

        /**
         * Last notification
         */
        private Date lastNotification= new Date();

        /**
         * @param responseCode the response to return
         */
        /**
         * Constructor
         * @param name The name of the resource.
         */
        public ObservableResource( String name )
        {
            this( name, 0, 0 );
        }

        /**
         * @param responseCode the response to return
         */
        /**
         * Constructor
         * @param name The name of the resource.
         * @param maxAge The max age option value to use in responses.
         * @param notify The actual notification period.
         */
        public ObservableResource( String name, long maxAge, long notify )
        {
            // set resource name
            super( name );

            // set display name
            getAttributes().setTitle( name );

            //set content
            content= "nothing_yet";

            //make observable
            setObservable( true );

            //set max age and notify
            this.maxAge= maxAge;
            this.notify= notify;

            //set notification period
            if ( notify > 0 )
            {
                notifyThread= new NotifyThread( this );
                notifyThread.start();
            }
        }

        /**
         * @return the lastNotification
         */
        public synchronized Date getLastNotification()
        {
            return lastNotification;
        }

        /**
         * @param lastNotification the lastNotification to set
         */
        public synchronized void setLastNotification( Date lastNotification )
        {
            this.lastNotification= lastNotification;
        }

        @Override
        public void handleGET( CoapExchange exchange )
        {
            Response response= new Response( ResponseCode.CONTENT );
            response.setPayload( content );
            if ( maxAge > 0 )
            {
                response.getOptions().setMaxAge( maxAge );
            }
            if ( exchange.advanced().getRelation() != null )
            {
                setLastNotification( new Date() );
            }
            exchange.respond( response );
        }

        @Override
        public void handlePOST( CoapExchange exchange )
        {
            content= exchange.getRequestText();
            exchange.respond( ResponseCode.CHANGED );
            changed();
        }

        @Override
        public void handlePUT( CoapExchange exchange )
        {
            content= exchange.getRequestText();
            exchange.respond( ResponseCode.CHANGED );
            changed();
        }

        @Override
        public void handleDELETE( CoapExchange exchange )
        {
            content= "nothing";
            exchange.respond( ResponseCode.DELETED );
            changed();
        }

        /**
         * Thread that triggers notifications
         *
         */
        private class NotifyThread extends Thread
        {
            private ObservableResource resource;

            public NotifyThread( ObservableResource resource )
            {
                super();
                this.resource= resource;
            }

            public void run()
            {
                boolean running= true;
                while ( running )
                {
                    try
                    {
                        Thread.sleep( 100 );
                        //this only works right when one observer  (sufficient for test)
                        if ( getLastNotification().getTime() + notify * 1000 < new Date().getTime() )
                        {
                            resource.changed();
                        }
                    }
                    catch ( InterruptedException e )
                    {
                        running= false;
                    }
                }
            }
        }
    }
}
