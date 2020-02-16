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


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.Example;

import nl.teslanet.mule.connectors.coap.api.ResourceConfig;
import nl.teslanet.mule.connectors.coap.api.ResourceInfo;
import nl.teslanet.mule.connectors.coap.api.error.InvalidResourceUriException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.ServerOperationErrorProvider;


//TODO add error tests
/**
 * This class is a container for CoAP server operations.
 */
public class ServerOperations
{
    /**
     * The Resource Changed processor is used to indicates that resources content has changed. 
     * It initiates notifications to observing clients. 
     * For every observing client and resource an internal get-request
     * is issued on the listener of the resource concerned.
     * The listener flow then processes the get and responses will be sent to the observing client.
     * As a result every client will be notified of the changed resource content.
     * @param config The server configuration name of which the resource(s) have changed.
     * @param uriPattern The uri pattern specifies the resource(s) that have changed. Wildcards can be used, like "/*" or "/some/deeper/resources/*".
     * @throws InvalidResourceUriException Thrown when given uri pattern is invalid.
     */
    @Throws({ ServerOperationErrorProvider.class })
    public void resourceChanged( @Config Server config, @Example("/resources/*") String uriPattern ) throws InvalidResourceUriException
    {
        if ( uriPattern == null )
        {
            throw new InvalidResourceUriException( "null" );
        }
        for ( ServedResource resource : config.getRegistry().findResources( uriPattern ) )
        {
            resource.changed();
        }
    }

    /**
     * @param server The name of the CoAP server instance to use. 
     * @param uri The uri of the resource to add. 
     * @param get When true the resource accepts get-requests. 
     * @param put When true the resource accepts put-requests. 
     * @param post When true the resource accepts post-requests. 
     * @param delete When true the resource accepts delete-requests. 
     * @param observable When true the resource accepts observe-requests. 
     * @param earlyAck An immediate acknowledgement is sent to the client before processing the request and returning response. 
     * @param title The readable title of the resource. 
     * @param ifdesc The interface ( if ) indicates interfaces-name the resource implements. 
     * @param rt The defines the resource type. 
     * @param ct The type of the resources content, specified as CoAP type number. 
     * @throws InvalidResourceUriException thrown when resource uri is not valid
     */
    /**
     * The Add Resource processor dynamically adds a new resource to the CoAP server.
     * The uri needs to be a complete resource-path, including all parent resource(s). 
     * All parent resources in the path must exist already.
     * The resource is available to clients immediately, provided there is a listener configured which 
     * has an uri pattern that applies to it.
     * @param config The configuration name of the CoAP server to add the resource to.
     * @param uri The uri of the resource to add. 
     * @param get When true the resource accepts get-requests.
     * @param put When true the resource accepts put-requests.
     * @param post When true the resource accepts post-requests.
     * @param delete When true the resource accepts delete-requests.
     * @param observable When true the resource accepts observe-requests.
     * @param earlyAck When true an immediate acknowledgement is sent to the client before processing the request and returning response.
     * @param info Meta-information of the resource.
     * @throws InvalidResourceUriException When given resource uri is not valid
     */
    @Throws({ ServerOperationErrorProvider.class })
    public void addResource(
        @Config Server config,
        @Example("/new_resource") String uri,
        @Optional(defaultValue= "false") boolean get,
        @Optional(defaultValue= "false") boolean put,
        @Optional(defaultValue= "false") boolean post,
        @Optional(defaultValue= "false") boolean delete,
        @Optional(defaultValue= "false") boolean observable,
        @Optional(defaultValue= "false") boolean earlyAck,
        @Optional ResourceInfo info ) throws InvalidResourceUriException
    {
        if ( uri == null )
        {
            throw new InvalidResourceUriException( "null" );
        }
        String parentUri= ResourceRegistry.getParentUri( uri );
        String name= ResourceRegistry.getUriResourceName( uri );
        if ( name.length() <= 0 ) throw new InvalidResourceUriException( "empty string" );

        ResourceConfig resourceConfig= new ResourceConfig();
        resourceConfig.setResourceName( name );
        resourceConfig.setGet( get );
        resourceConfig.setPost( post );
        resourceConfig.setPut( put );
        resourceConfig.setDelete( delete );
        resourceConfig.setObservable( observable );
        resourceConfig.setEarlyAck( earlyAck );
        resourceConfig.setInfo( info );

        config.getRegistry().add( parentUri, resourceConfig );
    }

    //TODO add notification parameter
    /**
     * The Remove Resource processor removes resources from the CoAP server.  
     * All resources that apply to the uri pattern will be removed.
     * Clients that observe a removed resource will be notified.
     * @param config The name of the CoAP server instance to use. 
     * @param uriPattern The uri pattern of the resource(s) that will be deleted. Wildcards can be used, like "/*" or "/some/deeper/resources/*". 
     * @throws InvalidResourceUriException Thrown when given uri pattern is not valid.
     */
    @Throws({ ServerOperationErrorProvider.class })
    public void removeResource( @Config Server config, @Example("/resources/*") String uriPattern ) throws InvalidResourceUriException
    {
        if ( uriPattern == null )
        {
            throw new InvalidResourceUriException( "null" );
        }
        config.getRegistry().remove( uriPattern );
    }

    /**
     * The Resource Exists processor checks whether the CoAP server has one or more resources 
     * matching given uri pattern.
     * @param config The configuration name of the CoAP server instance to use. 
     * @param uriPattern The uri pattern of the resource(s) to be found. A wildcard can be used, e.g. "/tobefound/*". 
     * @return {@code True} when at least one resource is found to which the pattern applies, otherwise {@code False}. 
     * @throws InvalidResourceUriException When given resource uri is not valid
     */
    @Throws({ ServerOperationErrorProvider.class })
    public Boolean ResourceExists( @Config Server config, @Example("/resources/*") String uriPattern ) throws InvalidResourceUriException
    {
        if ( uriPattern == null )
        {
            throw new InvalidResourceUriException( "null" );
        }
        List< ServedResource > found= config.getRegistry().findResources( uriPattern );
        return !found.isEmpty();
    }

    //TODO add usermanual item, schema update, unit test
    //TODO check efficiency
    /**
     * The List Resources processor returns a list of uri's of 
     * the resources of the CoAP server that match given uri pattern. 
     * @param config The configuration name of the CoAP server instance to use.
     * @param uriPattern The uri pattern of the resource(s) to be found. A wildcard can be used, e.g. "/tobefound/*".
     * @return The list containing uri's of resources that apply to the parttern.
     * @throws InvalidResourceUriException Thrown when given uri pattern is not valid.
     */
    @Throws({ ServerOperationErrorProvider.class })
    public List< String > ListResources( @Config Server config, @Example("/resources/*") String uriPattern ) throws InvalidResourceUriException
    {
        if ( uriPattern == null )
        {
            throw new InvalidResourceUriException( "null" );
        }
        List< String > uriList= new ArrayList< String >();
        for ( ServedResource found : config.getRegistry().findResources( uriPattern ) )
        {
            uriList.add( found.getURI() );
        }
        return new CopyOnWriteArrayList< String >( uriList );
    }
}
