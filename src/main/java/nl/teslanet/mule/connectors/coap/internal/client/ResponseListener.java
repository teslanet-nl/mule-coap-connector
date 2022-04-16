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

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.teslanet.mule.connectors.coap.api.CoAPResponseAttributes;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidHandlerException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.StartException;


/**
 * The Response Handler message source receives responses on asynchronous CoAP client requests and observe notifications.
 * The received CoAP messages are delivered to the handlers mule-flow.
 */
@MediaType(value= MediaType.APPLICATION_OCTET_STREAM, strict= false)
public class ResponseListener extends Source< InputStream, CoAPResponseAttributes >
{

    /**
     * The logger of this class.
     */
    private static final Logger logger= LoggerFactory.getLogger( ResponseListener.class.getCanonicalName() );

    //TODO change reference to response handler
    /**
     * The client that owns the handler.
     */
    @Config
    private Client client;

    /**
     * The handler that will deliver responses to process of observers and async requests.
     */
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    private String responseHandler;

    /* (non-Javadoc)
     * @see org.mule.runtime.extension.api.runtime.source.Source#onStart(org.mule.runtime.extension.api.runtime.source.SourceCallback)
     */
    @Override
    public void onStart( SourceCallback< InputStream, CoAPResponseAttributes > sourceCallback ) throws MuleException
    {
        try
        {
            client.addHandler( responseHandler, sourceCallback );
        }
        catch ( InternalInvalidHandlerException e )
        {
            throw new StartException( this + " failed to start, invalid handler name.", e );
        }
        logger.info( this + " started." );
    }

    /* (non-Javadoc)
     * @see org.mule.runtime.extension.api.runtime.source.Source#onStop()
     */
    @Override
    public void onStop()
    {
        client.removeHandler( responseHandler );
        logger.info( this + " stopped." );

    }

    /**
     * Get String representation.
     */
    @Override
    public String toString()
    {
        return "CoAP ResponseHandler { " + client.getClientName() + "::" + responseHandler + " }";
    }
}