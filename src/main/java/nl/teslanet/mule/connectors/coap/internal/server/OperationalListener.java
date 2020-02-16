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
package nl.teslanet.mule.connectors.coap.internal.server;


import org.mule.runtime.extension.api.runtime.source.SourceCallback;

import nl.teslanet.mule.connectors.coap.api.ReceivedRequestAttributes;
import nl.teslanet.mule.connectors.coap.api.error.InvalidResourceUriException;
import nl.teslanet.mule.connectors.coap.internal.Defs;


public class OperationalListener
{

    /**
     * The uri pattern descibing the resources of which requests will be processed.
     */
    String uriPattern;

    /**
     * The callback of the Mule flow that will process requests.
     */
    SourceCallback< byte[], ReceivedRequestAttributes > callback= null;

    /**
     * Constructor
     * @param uriPattern
     * @param callback
     * @throws InvalidResourceUriException
     */
    public OperationalListener( String uriPattern, SourceCallback< byte[], ReceivedRequestAttributes > callback ) throws InvalidResourceUriException
    {
        super();
        setUriPattern( uriPattern );
        setCallback( callback );
    }

    /**
     * Get the uri pattern of the listener
     * @return the uri pattern
     */
    public String getUriPattern()
    {
        return uriPattern;
    }

    /**
     * Set the uri pattern of the resources the listener should process requests for.
     * @param uriPattern the uri pattern to set
     * @throws InvalidResourceUriException when the uri pattern is not valid
     */
    //TODO: make URIpattern class
    public void setUriPattern( String uriPattern ) throws InvalidResourceUriException
    {
        //TODO assure no bad chars
        if ( uriPattern == null ) throw new InvalidResourceUriException( "null", ", null is not allowed." );
        this.uriPattern= uriPattern.trim();
        if ( !this.uriPattern.startsWith( Defs.COAP_URI_PATHSEP ) )
        {
            this.uriPattern= Defs.COAP_URI_PATHSEP + this.uriPattern;
        } ;
        int wildcardIndex= this.uriPattern.indexOf( Defs.COAP_URI_WILDCARD );
        if ( wildcardIndex >= 0 && wildcardIndex < this.uriPattern.length() - 1 ) throw new InvalidResourceUriException( uriPattern, ", wildcard needs to be last character." );
        if ( this.uriPattern.length() < 2 ) throw new InvalidResourceUriException( uriPattern, ", uri cannot be empty." );

    }

    /**
     * @return the callback
     */
    public SourceCallback< byte[], ReceivedRequestAttributes > getCallback()
    {
        return callback;
    }

    /**
     * @param callback the callback to set
     */
    public void setCallback( SourceCallback< byte[], ReceivedRequestAttributes > callback )
    {
        //TODO assure not null
        this.callback= callback;
    }
}
