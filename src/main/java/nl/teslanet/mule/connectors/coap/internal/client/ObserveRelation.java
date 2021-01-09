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
package nl.teslanet.mule.connectors.coap.internal.client;


import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResponseException;


/**
 * The ObserverRelation processes notifications and manages re-registration when needed.
 */
public class ObserveRelation implements CoapHandler
{
    /**
     * The logger.
     */
    private static final Logger LOGGER= LoggerFactory.getLogger( ObserveRelation.class.getCanonicalName() );

    /**
     * 
     * The name of the observer.
     */
    private final String observerName;

    /**
     * The client owning the observer.
     */
    private final CoapClient coapClient;

    /**
     * The uri of the resource that is observed.
     */
    private final String uri;

    /**
     * The uri of the resource that is observed.
     */
    private final boolean confirmable;

    /**
     * Failure logmessage.
     */
    private final String logMessageFailed;

    /**
     * Failure logmessage.
     */
    private final String logMessageRecreated;

    /**
     * Failure logmessage.
     */
    private final String logMessageRecreatedFailed;

    /**
     * Failure logmessage.
     */
    private final String logMessageReregistered;

    /**
     * Reregistration failure logmessage
     */
    private final String logMessageReregisterFailed;

    /**
     * Error processing logmessage.
     */
    private final String logMessageErrorProcessingFailed;

    /**
     * Processing failure logmessage.
     */
    private final String logMessageProcessingFailed;

    /**
     * processor that will process notifications.
     */
    private final ResponseProcessingStrategy processor;

    /**
     * The CoAP relation that has been established.
     */
    private CoapObserveRelation coapRelation= null;

    /**
     * Constructor
     * @param sourceCallback
     */
    ObserveRelation( String observerName, CoapClient coapClient, boolean confirmable, String uri, ResponseProcessingStrategy processor )
    {
        super();
        this.observerName= observerName;
        this.coapClient= coapClient;
        this.uri= uri;
        this.confirmable= confirmable;
        this.processor= processor;
        logMessageFailed= toString() + " failed to observe, trying to restore relation with server...";
        logMessageRecreated= toString() + " relation with server recreated.";
        logMessageRecreatedFailed= toString() + " failed to recreate relation with server.";
        logMessageReregistered= toString() + " reregistered on server.";
        logMessageReregisterFailed= toString() + " failed to reregister on server.";
        logMessageErrorProcessingFailed= toString() + " error processing failed.";
        logMessageProcessingFailed= toString() + " notification processing failed.";
    }

    /**
     * Start observing.
     */
    public void start()
    {
        coapRelation= doObserveRequest();
        LOGGER.info( this + " started." );
    }

    /**
     * Stop observing
     */
    public void stop()
    {
        coapRelation.proactiveCancel();
        coapRelation= null;
        LOGGER.info( this + " stopped." );
    }

    /**
    * Callback for error processing.
    */
    @Override
    public void onError()
    {
        LOGGER.warn( logMessageFailed );
        if ( coapRelation != null )
        {
            //TODO wait time?
            if ( coapRelation.isCanceled() )
            {
                coapRelation= doObserveRequest();
                if ( coapRelation != null )
                {
                    LOGGER.info( logMessageRecreated );
                }
                else
                {
                    LOGGER.error( logMessageRecreatedFailed );
                }
            }
            else
            {
                if ( coapRelation.reregister() )
                {
                    LOGGER.info( logMessageReregistered );
                }
                else
                {
                    LOGGER.error( logMessageReregisterFailed );

                }
            }
        }
        try
        {
            processor.process( uri, Code.GET, null );
        }
        catch ( InternalResponseException e )
        {
            LOGGER.error( logMessageErrorProcessingFailed, e );
        }
    }

    /**
     * Callback for processing notifications.
     */
    @Override
    public void onLoad( CoapResponse response )
    {
        try
        {
            processor.process( uri, Code.GET, response );
        }
        catch ( InternalResponseException e )
        {
            LOGGER.error( logMessageProcessingFailed, e );
        }
    }

    /**
     * Send an observe request for a resource with given uri
     * @param confirmable when true the request will be sent confirmable
     * @param uri the uri of the resource to observe
     * @param handler the handler that will receive notifications from the observed resource
     * @return the established relation with the resource when the request succeeded, otherwise null 
     */
    private CoapObserveRelation doObserveRequest()
    {
        Request request= new Request( Code.GET, ( confirmable ? Type.CON : Type.NON ) );
        request.setURI( uri );
        //TODO add (other) options
        request.setObserve();
        return coapClient.observe( request, this );
    }

    /**
     * Get String representation.
     */
    @Override
    public String toString()
    {
        return observerName;
    }
}
