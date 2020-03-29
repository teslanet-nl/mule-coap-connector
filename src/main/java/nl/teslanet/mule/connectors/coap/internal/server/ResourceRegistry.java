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


import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.californium.core.server.resources.Resource;

import nl.teslanet.mule.connectors.coap.internal.Defs;
import nl.teslanet.mule.connectors.coap.api.ResourceConfig;
import nl.teslanet.mule.connectors.coap.api.error.InvalidResourceUriException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Registry of served resources and listeners on the resources.
 * It maintains consistency of the callback relations they have.
 */
public class ResourceRegistry
{
    //TODO maybe list, depending on validation of duplication of resourcenames
    private ConcurrentHashMap< String, ServedResource > servedResources;

    //TODO review concurrency
    private CopyOnWriteArrayList< OperationalListener > listeners;

    Resource root= null;

    /**
     * Construct a registry. The constructor initializes served resources 
     * and listener repositories. 
     * @param root mandatory root resource
     */
    public ResourceRegistry( Resource root )
    {
        if ( root == null ) throw new NullPointerException( "Cannot construct a ResourceRegistry without root resource." );
        this.root= root;

        servedResources= new ConcurrentHashMap< String, ServedResource >();
        listeners= new CopyOnWriteArrayList< OperationalListener >();
    }

    /**
     * Add a new resource to the registry based on given resource configuration. 
     * The resource will be added as a child of resource with given parentUri. 
     * When parentUri is null the resource will be added to the root. 
     * @param parentUri the uri of the parent of the new resource. 
     * @param config the definition of the resource to create
     * @throws InvalidResourceUriException the parent uri does not resolve to an existing resource
     */
    public void add( String parentUri, ResourceConfig config ) throws InvalidResourceUriException
    {
        ServedResource parent= getResource( parentUri );
        ServedResource resource= new ServedResource( config );
        if ( parent == null )
        {
            root.add( resource );
        }
        else
        {
            parent.add( resource );
        }
        register( resource );
    }

    /**
     * Register resource and its children.
     * @param resource to be registered
     */
    private void register( ServedResource resource )
    {
        servedResources.put( resource.getURI(), resource );
        setResourceCallBack( resource );
        //also register children recursively 
        for ( Resource child : resource.getChildren() )
        {
            register( (ServedResource) child );
        }
    }

    /**
     * Remove a resource from the registry
     * @param uriPattern The uri pattern defining the resources to remove
     */
    public void remove( String uriPattern )
    {
        for ( ServedResource resource : findResources( uriPattern ) )
        {
            unRegister( resource );
        }
    }

    /**
     * Unregister resource and its children. 
     * @param resource to be registered
     */
    private void unRegister( ServedResource resource )
    {
        servedResources.remove( resource.getURI() );
        //also unregister children recursively 
        for ( Resource child : resource.getChildren() )
        {
            unRegister( (ServedResource) child );
        }
        resource.delete();
    }

    /**
    * Add a listener to the registry.
    * @param operationalListener The listener to add
    */
    public void add( OperationalListener operationalListener )
    {
        listeners.add( operationalListener );
        updateResourceCallBack();
    }

    /**
    * Remove a listener from the registry.
    * @param listener The listener to add
    */
    public void remove( OperationalListener listener )
    {
        listeners.remove( listener );
        updateResourceCallBack();
    }

    /**
     * Updates callback of all served resources.
     */
    private void updateResourceCallBack()
    {
        for ( Entry< String, ServedResource > e : servedResources.entrySet() )
        {
            setResourceCallBack( e.getValue() );
        }
    }

    /**
     * Set the callback of the resource given to the listener that matches best
     * to the resources uri.
     * @param toServe the served resource of which the callback is set.
     */
    private void setResourceCallBack( ServedResource resource )
    {
        OperationalListener bestListener= null;
        int maxMatchlevel= 0;
        for ( OperationalListener listener : listeners )
        {
            int matchLevel= matchUri( listener.getUriPattern(), resource.getURI() );
            if ( matchLevel > maxMatchlevel )
            {
                maxMatchlevel= matchLevel;
                bestListener= listener;
            }
        }
        if ( bestListener != null )
        {
            resource.setCallback( bestListener.getCallback() );
        }
        else
        {
            resource.setCallback( null );
            //TODO log warning
        }
    }

    /**
     * Get the resources from the registry with given uri.
     * A null uri, an empty string or "/" is interpreted as the root uri. 
     * @param uri The uri of the resource to get.
     * @return The served resource that has given uri, or null when the root uri is given.
     * @throws InvalidResourceUriException The resource does not exist.
     */
    public ServedResource getResource( String uri ) throws InvalidResourceUriException
    {
        if ( uri == null || uri.length() == 0 || uri.equals( "/" ) )
        {
            //do not expose root resource
            return null;
        }
        else
        {
            for ( Entry< String, ServedResource > e : servedResources.entrySet() )
            {
                if ( uri.equals( e.getKey() ) )
                {
                    return e.getValue();
                }
            }
        }
        throw new InvalidResourceUriException( uri, ", resource does not exist." );
    }

    /**
     * Find all resources of that have matching uri's.
     * @param uriPattern the pattern to match to.
     * @return A list containing all served resource that match. The list is not thread safe.
     */
    public List< ServedResource > findResources( String uriPattern )
    {
        //TODO regex support
        //TODO concurrent?
        ArrayList< ServedResource > found= new ArrayList< ServedResource >();

        for ( Entry< String, ServedResource > e : servedResources.entrySet() )
        {
            if ( matchUri( uriPattern, e.getKey() ) > 0 )
            {
                found.add( e.getValue() );
            }
        }
        return found;
    }

    /**
     * Establish the degree in which a resource uri matches an uriPattern.
     * The degree is an indication how good the pattern matches.
     * It can be used to compare the matching of a resource uri to different patterns.
     * For example the resource "/one/two/three" will match to both patterns
     * "/one/*" and "/one/two/*". The latter with an higher degree because it is more 
     * specific. The degree value returned is 0 when there is no match. An integer > 0 
     * when there is a match to some degree. MAX_VALUE when there is a perfect match, which means the pattern equals the uri.
     * @param uriPattern The pattern to match to.
     * @param resourceUri The resource uri to do the matching on.
     * @return The degree of matching.
     */
    public static int matchUri( String uriPattern, String resourceUri )
    {
        //TODO assure wildcard only occurs at end
        if ( uriHasWildcard( uriPattern ) )
        {
            if ( resourceUri.startsWith( getUriPath( uriPattern ) ) )
            {
                return getUriDepth( uriPattern );
            }
        }
        else if ( uriPattern.equals( resourceUri ) )
        {
            return Integer.MAX_VALUE;
        }
        return 0;
    }

    /**
     * Get the depth of resource hierachy in an uri.
     * @param uri the resource uri
     * @return the depth of the uri
     */
    public static int getUriDepth( String uri )
    {
        int count;
        int index;
        for ( count= 0, index= uri.indexOf( Defs.COAP_URI_PATHSEP, 0 ); index >= 0; index= uri.indexOf( Defs.COAP_URI_PATHSEP, index + 1 ), count++ );
        return count;
    }

    /**
     * Get the path part of an uri. That is the uri without the resource name. 
     * @param uri The uri of the resouce.
     * @return The path from the uri.
     */
    public static String getUriPath( String uri )
    {
        int lastPathSep= uri.lastIndexOf( Defs.COAP_URI_PATHSEP );
        if ( lastPathSep >= 0 )
        {
            return uri.substring( 0, lastPathSep + 1 );
        }
        else
        {
            return Defs.COAP_URI_PATHSEP;
        }
    }

    /**
     * Get the resource name part of an uri. That is the uri without the path preceding the resource name. 
     * @param uri The uri of the resource.
     * @return The resource name.
     */
    public static String getUriResourceName( String uri )
    {
        int lastPathSep= uri.lastIndexOf( Defs.COAP_URI_PATHSEP );
        if ( lastPathSep >= 0 )
        {
            //TODO check uri format .*/x+
            return uri.substring( lastPathSep + 1 );
        }
        else
        {
            return Defs.COAP_URI_ROOTRESOURCE;
        }
    }

    /**
     * Establish whether the uri has a wildcard and is in fact a pattern.
     * @param uri The uri of a resource.
     * @return true when a wildcard is found, otherwise false.
     */
    public static boolean uriHasWildcard( String uri )
    {
        return uri.endsWith( Defs.COAP_URI_WILDCARD );
    }

    /**
     * Get the uri of the parent of a resource.
     * @param uri The uri of the resource to get the parent from.
     * @return the uri of the parent.
     */
    public static String getParentUri( String uri )
    {
        int lastPathSep= uri.lastIndexOf( Defs.COAP_URI_PATHSEP );
        if ( lastPathSep > 0 )
        {
            return uri.substring( 0, lastPathSep );
        }
        else
        {
            return Defs.COAP_URI_ROOTRESOURCE;
        }
    }

}
