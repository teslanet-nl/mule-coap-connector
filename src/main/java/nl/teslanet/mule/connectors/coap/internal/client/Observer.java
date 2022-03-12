/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2022 (teslanet.nl) Rogier Cobben
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


import java.io.InputStream;
import java.net.URI;

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.teslanet.mule.connectors.coap.api.CoAPResponseAttributes;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUriException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.StartException;


/**
 * The Observer message source observes a CoAP resource on a server. 
 * The CoAP notifications received from the server are delivered to the flow. 
 * The resource is observed during runtime of the flow. When 
 * an observe relation with the server could not be established or is lost, 
 * the flow is notified of the error and the observe request is retried continuously. 
 */
@Alias( "observer" )
@MediaType( value= MediaType.APPLICATION_OCTET_STREAM, strict= false )
public class Observer extends Source< InputStream, CoAPResponseAttributes >
{
    /**
     * The logger.
     */
    private static final Logger logger= LoggerFactory.getLogger( Observer.class.getCanonicalName() );

    /**
     * The client owning the observer.
     */
    @Config
    private Client client;

    @ParameterGroup( name= "Observe uri" )
    private ObserverConfig observerConfig;

    //TODO add options config?
    //TODO one relation namespace for dynamic and static observe, does coap allow for multiple uri observe from the same endooint?
    //TODO add refresh observe feature
    /**
     * The relation that has been established
     */
    private ObserveRelation relation= null;

    private URI uri= null;

    /* (non-Javadoc)
     * @see org.mule.runtime.extension.api.runtime.source.Source#onStart(org.mule.runtime.extension.api.runtime.source.SourceCallback)
     */
    @Override
    public void onStart( SourceCallback< InputStream, CoAPResponseAttributes > sourceCallback ) throws MuleException
    {
        CoapRequestBuilder requestBuilder= client.new CoapRequestBuilderImpl( observerConfig );;
        try
        {
            uri= requestBuilder.buildResourceUri();
        }
        catch ( InternalUriException e )
        {
            throw new StartException( this + " failed to start, invalid uri. ", e );
        }
        relation= new ObserveRelation( this.toString(), client.getCoapClient(), requestBuilder, ( requestUri, requestCode, response ) -> {
            client.processMuleFlow( requestUri, requestCode, response, sourceCallback );
        } );
        relation.start();
        logger.info( this + " started." );
    }

    /* (non-Javadoc)
     * @see org.mule.runtime.extension.api.runtime.source.Source#onStop()
     */
    @Override
    public void onStop()
    {
        if ( relation != null )
        {
            //TODO make type of canceling configurable
            relation.stop();
            relation= null;
        }
        logger.info( this + " stopped." );
    }

    /**
     * Get String representation.
     */
    @Override
    public String toString()
    {
        return "CoAP Observer { " + client.getClientName() + "::" + uri + "(static) }";
    }
}
