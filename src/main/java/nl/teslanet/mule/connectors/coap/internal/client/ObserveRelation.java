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
package nl.teslanet.mule.connectors.coap.internal.client;


import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidRequestCodeException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalRequestException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResponseException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUnkownOptionException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUriException;


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
     * The request builder.
     */
    private final CoapRequestBuilder requestBuilder;

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
    ObserveRelation(
        String observerName,
        CoapClient coapClient,
        CoapRequestBuilder requestBuilder,
        ResponseProcessingStrategy processor
    )
    {
        super();
        this.observerName= observerName;
        this.coapClient= coapClient;
        this.requestBuilder= requestBuilder;
        this.processor= processor;
    }

    /**
     * Start observing.
     */
    public synchronized void start()
    {
        try
        {
            coapRelation= sendObserveRequest();
            LOGGER.info( "{} has started.", this );
        }
        catch (
            InternalInvalidRequestCodeException | InternalUriException | InternalRequestException
            | InternalInvalidOptionValueException | InternalUnkownOptionException e
        )
        {
            LOGGER.error( String.format( "%s failed to recreate relation with server.", this ), e );
        }
    }

    /**
     * Stop observing
     */
    public synchronized void stop()
    {
        if ( coapRelation != null )
        {
            coapRelation.proactiveCancel();
            coapRelation= null;
            LOGGER.info( "{} has stopped.", this );
        }
    }

    /**
     * Stop observing
     */
    public synchronized void stop( boolean confirmable )
    {
        if ( coapRelation != null )
        {
            coapRelation.setConfirmable( confirmable );
            coapRelation.proactiveCancel();
            coapRelation= null;
            LOGGER.info( "{} has stopped.", this );
        }
    }

    /**
    * Callback for error processing.
    */
    @Override
    public void onError()
    {
        LOGGER.warn( "{} failed to observe, trying to restore relation with server...", this );
        if ( coapRelation != null )
        {
            //TODO wait time?
            if ( coapRelation.isCanceled() )
            {
                try
                {
                    coapRelation= sendObserveRequest();
                }
                catch (
                    InternalInvalidRequestCodeException | InternalUriException | InternalRequestException
                    | InternalInvalidOptionValueException | InternalUnkownOptionException e
                )
                {
                    LOGGER
                        .error(
                            String.format( "%s failed to observe, trying to restore relation with server...", this ),
                            e
                        );
                }
                if ( coapRelation != null )
                {
                    LOGGER.info( "{} relation with server recreated.", this );
                }
                else
                {
                    LOGGER.error( "{} failed to recreate relation with server.", this );
                }
            }
            else
            {
                if ( coapRelation.reregister() )
                {
                    LOGGER.info( "{} reregistered on server.", this );
                }
                else
                {
                    LOGGER.error( "{} failed to reregister on server.", this );

                }
            }
        }
        try
        {
            processor.process( requestBuilder, null );
        }
        catch ( InternalResponseException e )
        {
            LOGGER.error( String.format( "%s error processing failed.", this ), e );
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
            processor.process( requestBuilder, response );
        }
        catch ( InternalResponseException e )
        {
            LOGGER.error( String.format( "%s notification processing failed.", this ), e );
        }
    }

    /**
     * Send an observe request
     * @return the established CoAP relation with the resource when the request succeeded, otherwise null 
     * @throws InternalRequestException When the request cannot be constructed.
     * @throws InternalUriException When no valid uri could be constructed.
     * @throws InternalInvalidRequestCodeException When request code is invalid.
     * @throws InternalUnkownOptionException  When an other option is not accepted.
     * @throws InternalInvalidOptionValueException  When an option value is invalid.
     */
    private CoapObserveRelation sendObserveRequest() throws InternalInvalidRequestCodeException,
        InternalUriException,
        InternalRequestException,
        InternalInvalidOptionValueException,
        InternalUnkownOptionException
    {
        return coapClient.observe( requestBuilder.build(), this );
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
