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


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.ResourceParams;
import nl.teslanet.mule.connectors.coap.api.error.InvalidResourceUriException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResourceUriException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.ServerOperationErrorProvider;


//TODO add error tests
/**
 * This class is a container for CoAP server operations.
 */
public class ServerOperations
{
    /**
     * The Notify processor initiates notifications to observing clients. 
     * Use this when the resource content has changed. 
     * For every observing client and resource an internal get-request
     * is issued on the listener concerned.
     * The response that the listener flow will be used as notication as is sent to the observing client.
     * @param server The server configuration name of which the resource(s) have changed content.
     * @param pathPattern The path pattern specifies the resource(s) that have changed content. Wildcards can be used, like "/*" or "/some/deeper/resources/*".
     * @throws InvalidResourceUriException Thrown when given uri pattern is invalid.
     */
    @Throws( { ServerOperationErrorProvider.class } )
    public void notify( @Config
    Server server,
        @Alias( "pathPattern" )
        @Summary( "For resources that apply to the path-pattern notifications are issued." )
        @Example( "/my_resources/*" )
        String pathPattern
    ) throws InvalidResourceUriException
    {
        if ( pathPattern == null )
        {
            throw new InvalidResourceUriException( server + ": notify operation failed, invalid uri.", "null" );
        }
        for ( ServedResource resource : server.getRegistry().findResources( pathPattern ) )
        {
            resource.changed();
        }
    }

    /**
     * The Resource Add processor dynamically adds a new resource to the CoAP server.
     * The uri needs to be a complete resource-path, including all parent resource(s). 
     * All parent resources in the path must exist already.
     * The resource is available to clients immediately, provided there is a listener configured which 
     * has an uri pattern that applies to it.
     * @param server The configuration name of the CoAP server to add the resource to.
     * @param resourceParams The builder that delivers the resource parameters.
     * @throws InvalidResourceUriException When the uri has invalid value.
     */
    @Throws( { ServerOperationErrorProvider.class } )
    public void resourceAdd( @Config
    Server server, @ParameterGroup( name= "Resource to add" )
    ResourceParams resourceParams ) throws InvalidResourceUriException
    {
        if ( resourceParams.getResourcePath() == null )
        {
            throw new InvalidResourceUriException( server + ": resource add operation failed,", "null" );
        }
        String parentUri= ResourceRegistry.getParentUri( resourceParams.getResourcePath() );
        String name= ResourceRegistry.getUriResourceName( resourceParams.getResourcePath() );
        if ( name.length() <= 0 ) throw new InvalidResourceUriException( server + ": resource add operation failed, empty resource name", resourceParams.getResourcePath() );

        try
        {
            server.getRegistry().add( parentUri, resourceParams );
        }
        catch ( InternalResourceUriException e )
        {
            throw new InvalidResourceUriException( server + ": resource add operation failed, ", resourceParams.getResourcePath(), e );

        }
    }

    //TODO add notification parameter
    /**
     * The  Resource Remove processor removes resources from the CoAP server.  
     * All resources that apply to the uri pattern will be removed.
     * Clients that observe a removed resource will be notified.
     * @param server The name of the CoAP server instance to use. 
     * @param pathPattern The uri pattern of the resource(s) that will be deleted. Wildcards can be used, like "/*" or "/some/deeper/resources/*". 
     * @throws InvalidResourceUriException Thrown when given uri pattern is not valid.
     */
    @Throws( { ServerOperationErrorProvider.class } )
    public void resourceRemove( @Config
    Server server,
        @Alias( "pathPattern" )
        @Summary( "Resources that apply to the path-pattern are removed." )
        @Example( "/resources/*" )
        String pathPattern
    ) throws InvalidResourceUriException
    {
        if ( pathPattern == null )
        {
            throw new InvalidResourceUriException( server + ": resource remove operation failed", "null" );
        }
        server.getRegistry().remove( pathPattern );
    }

    /**
     * The Resource Exists processor checks whether the CoAP server has one or more resources 
     * matching given uri pattern.
     * @param server The configuration name of the CoAP server instance to use. 
     * @param pathPattern The uri pattern of the resource(s) to be found. A wildcard can be used, e.g. "/tobefound/*". 
     * @return {@code True} when at least one resource is found to which the pattern applies, otherwise {@code False}. 
     * @throws InvalidResourceUriException When given resource uri is not valid
     */
    @Throws( { ServerOperationErrorProvider.class } )
    public Boolean resourceExists( @Config
    Server server,
        @Alias( "pathPattern" )
        @Summary( "If any resources that apply to the path-pattern exist true is retuned, otherwise false." )
        @Example( "/resources/*" )
        String pathPattern
    ) throws InvalidResourceUriException
    {
        if ( pathPattern == null )
        {
            throw new InvalidResourceUriException( server + ": resource exists operation failed", "null" );
        }
        List< ServedResource > found= server.getRegistry().findResources( pathPattern );
        return !found.isEmpty();
    }

    //TODO unit test, test performance
    /**
     * Returns a list of uri's of the resources, that match given uri pattern. 
     * @param server The server instance to list resources of.
     * @param pathPattern The uri pattern of the resource(s) to be found. A wildcard can be used, e.g. "/tobefound/*".
     * @return The list containing uri's of resources that apply to the parttern.
     * @throws InvalidResourceUriException Thrown when given uri pattern is not valid.
     */
    @Throws( { ServerOperationErrorProvider.class } )
    public List< String > resourceList( 
        @Config Server server,
        @Alias( "pathPattern" )
        @Summary( "Paths of the resources that apply to the path-pattern are listed and returned." )
        String pathPattern
    ) throws InvalidResourceUriException
    {
        if ( pathPattern == null )
        {
            throw new InvalidResourceUriException( server + ": resource list operation failed", "null" );
        }
        List< String > uriList= new ArrayList<>();
        for ( ServedResource found : server.getRegistry().findResources( pathPattern ) )
        {
            uriList.add( found.getURI() );
        }
        return new CopyOnWriteArrayList<>( uriList );
    }
}
