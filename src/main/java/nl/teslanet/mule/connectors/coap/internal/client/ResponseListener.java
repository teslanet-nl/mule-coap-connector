/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2023 (teslanet.nl) Rogier Cobben
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

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.teslanet.mule.connectors.coap.api.CoapResponseAttributes;
import nl.teslanet.mule.connectors.coap.api.ResponseHandler;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidHandlerException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.StartException;


/**
 * The Response Handler message source receives responses on asynchronous CoAP client requests and observe notifications.
 * The received CoAP messages are delivered to the handlers mule-flow.
 */
@MediaType( value= MediaType.APPLICATION_OCTET_STREAM, strict= false )
public class ResponseListener extends Source< InputStream, CoapResponseAttributes >
{
    /**
     * The logger of this class.
     */
    private static final Logger LOGGER= LoggerFactory.getLogger( ResponseListener.class.getCanonicalName() );

    /**
     * The handler that will deliver responses and notifications to process.
     */
    @Parameter
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    private ResponseHandler responseHandler;

    private SourceCallback< InputStream, CoapResponseAttributes > sourceCallback= null;

    /* (non-Javadoc)
     * @see org.mule.runtime.extension.api.runtime.source.Source#onStart(org.mule.runtime.extension.api.runtime.source.SourceCallback)
     */
    @Override
    public void onStart( SourceCallback< InputStream, CoapResponseAttributes > sourceCallback ) throws MuleException
    {
        this.sourceCallback= sourceCallback;
        try
        {
            ResponseProcessor processor= ResponseProcessor.getResponseProcessor( responseHandler.getHandlerName() );
            processor.addListener( this.sourceCallback );
        }
        catch ( InternalInvalidHandlerException e )
        {
            throw new StartException( this + " failed to start, invalid handler name.", e );
        }
        LOGGER.info( "{} started.", this );
    }

    /* (non-Javadoc)
     * @see org.mule.runtime.extension.api.runtime.source.Source#onStop()
     */
    @Override
    public void onStop()
    {
        try
        {
            ResponseProcessor processor= ResponseProcessor.getResponseProcessor( responseHandler.getHandlerName() );
            processor.removeListener( sourceCallback );
        }
        catch ( InternalInvalidHandlerException e )
        {
            LOGGER.error( "{} cannot remove listener", this );
        }
        sourceCallback= null;
        LOGGER.info( "{} stopped.", this );
    }

    /**
     * Get String representation.
     */
    @Override
    public String toString()
    {
        return "CoAP Response Listener on { " + responseHandler.getHandlerName() + " }";
    }
}
