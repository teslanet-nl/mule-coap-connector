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
package nl.teslanet.mule.connectors.coap.internal.server;


import java.io.InputStream;

import org.mule.runtime.extension.api.runtime.source.SourceCallback;

import nl.teslanet.mule.connectors.coap.api.CoapRequestAttributes;
import nl.teslanet.mule.connectors.coap.internal.Defs;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResourceUriException;


/**
 * The Operationallistener holds the objects that are used in the listening
 * proces.
 */
public class OperationalListener
{

    /**
     * The uri pattern describing the resources of which requests will be processed.
     */
    String uriPattern;

    /**
     * The set of request codes the listener is listening out for.
     */
    RequestCodeFlags requestCodeFlags= null;

    /**
     * The callback of the Mule flow that will process requests.
     */
    SourceCallback< InputStream, CoapRequestAttributes > callback= null;

    /**
     * Constructor
     * 
     * @param uriPattern
     * @param callback
     * @throws InternalResourceUriException 
     */
    public OperationalListener( String uriPattern, RequestCodeFlags flags, SourceCallback< InputStream, CoapRequestAttributes > callback ) throws InternalResourceUriException
    {
        super();
        setUriPattern( uriPattern );
        setRequestCodeFlags( flags );
        setCallback( callback );
    }

    /**
     * Set the requestcode flags of the listener.
     * @param flags the flags to set
     */
    private void setRequestCodeFlags( RequestCodeFlags flags )
    {
        requestCodeFlags= flags;
    }

    /**
     * Get the uri pattern of the listener
     * 
     * @return the uri pattern
     */
    public String getUriPattern()
    {
        return uriPattern;
    }

    /**
     * Set the uri pattern of the resources the listener should process requests
     * for.
     * 
     * @param uriPattern the uri pattern to set
     * @throws InternalResourceUriException when the uri pattern is not valid
     */
    // TODO: make URIpattern class
    public void setUriPattern( String uriPattern ) throws InternalResourceUriException
    {
        // TODO assure no bad chars
        if ( uriPattern == null ) throw new InternalResourceUriException( "null value is not allowed." );
        this.uriPattern= uriPattern.trim();
        if ( !this.uriPattern.startsWith( Defs.COAP_URI_PATHSEP ) )
        {
            this.uriPattern= Defs.COAP_URI_PATHSEP + this.uriPattern;
        }
        int wildcardIndex= this.uriPattern.indexOf( Defs.COAP_URI_WILDCARD );
        if ( wildcardIndex >= 0 && wildcardIndex < this.uriPattern.length() - 1 )
            throw new InternalResourceUriException( "invalid uriPattern { " + uriPattern + " }, wildcard needs to be last character." );
        if ( this.uriPattern.length() < 2 ) throw new InternalResourceUriException( "invalid uriPattern { " + uriPattern + " }, uri cannot be empty." );

    }

    /**
     * @return the callback
     */
    public SourceCallback< InputStream, CoapRequestAttributes > getCallback()
    {
        return callback;
    }

    /**
     * @param callback the callback to set
     */
    public void setCallback( SourceCallback< InputStream, CoapRequestAttributes > callback )
    {
        this.callback= callback;
    }

    /**
     * @return the requestcode flags that indicate the kind of request the listener is receiving.
     */
    public RequestCodeFlags getRequestCodeFlags()
    {
        return requestCodeFlags;
    }
}
