/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2025 (teslanet.nl) Rogier Cobben
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
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.californium.core.server.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.teslanet.mule.connectors.coap.api.ResourceConfig;
import nl.teslanet.mule.connectors.coap.api.Defs;
import nl.teslanet.mule.connectors.coap.api.ResourceParams;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResourceRegistryException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResourceUriException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUriPatternException;


/**
 * Registry of served resources and listeners on the resources. It maintains
 * consistency of the callback relations they have.
 */
public class ResourceRegistry
{
    /**
     * The Logger of this class.
     */
    private static final Logger LOGGER= LoggerFactory.getLogger( ResourceRegistry.class );

    /**
     * The hosted resources.
     */
    private ConcurrentHashMap< String, AbstractResource > servedResources;

    /**
     * The listeners that are active on the resources.
     */
    private CopyOnWriteArrayList< OperationalListener > listeners;

    /**
     * The root resource on the server.
     */
    Resource root= null;

    /**
     * Construct a registry. The constructor initializes served resources and
     * listener repositories.
     * 
     * @param root Mandatory root resource.
     * @throws InternalResourceRegistryException When the the root resource is null.
     */
    public ResourceRegistry( Resource root ) throws InternalResourceRegistryException
    {
        if ( root == null ) throw new InternalResourceRegistryException(
            "Cannot construct a ResourceRegistry without root resource."
        );
        this.root= root;

        servedResources= new ConcurrentHashMap<>();
        listeners= new CopyOnWriteArrayList<>();
    }

    /**
     * Add a new resource to the registry based on given resource configuration. The
     * resource will be added as a child of resource with given parentUri. When
     * parentUri is null the resource will be added to the root.
     * 
     * @param parentUri          the uri of the parent of the new resource.
     * @param resourceDesciption the definition of the resource to create
     * @throws InternalResourceUriException the parent uri does not resolve to an
     *                                      existing resource or resource parameters are invalid.
     */
    public void add( String parentUri, ResourceConfig resourceDesciption ) throws InternalResourceUriException
    {
        ServedResource parent= getResource( parentUri );
        ServedResource resource;
        try
        {
            resource= new ServedResource( resourceDesciption );
        }
        catch ( Exception e )
        {
            throw new InternalResourceUriException( "Resource parameters are invalid", e );
        }
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
     * Add a new resource to the registry based on given resource description.
     * 
     * @param resourceDesciption The definition of the resource to create.
     * @throws InternalResourceUriException when the uri is invalid.
     */
    public void add( ResourceParams resourceDesciption ) throws InternalResourceUriException
    {
        String parentUri= ResourceRegistry.getParentUri( resourceDesciption.getResourcePath() );
        AbstractResource parent= getResource( parentUri );
        AbstractResource resource= new ServedResource( resourceDesciption );
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
     * 
     * @param resource to be registered
     */
    private void register( AbstractResource resource )
    {
        servedResources.put( resource.getURI(), resource );
        setResourceCallBack( resource );
        // also register children recursively
        for ( Resource child : resource.getChildren() )
        {
            register( (AbstractResource) child );
        }
    }

    /**
     * Remove all resources from the registry
     */
    public void removeAll()
    {
        remove( "/*" );
    }

    /**
     * Remove a resource from the registry
     * 
     * @param uriPattern The uri pattern defining the resources to remove
     */
    public void remove( String uriPattern )
    {
        for ( AbstractResource resource : findResources( uriPattern ) )
        {
            unRegister( resource );
        }
    }

    /**
     * Unregister resource and its children.
     * 
     * @param resource to be registered
     */
    private void unRegister( AbstractResource resource )
    {
        servedResources.remove( resource.getURI() );
        // also unregister children recursively
        for ( Resource child : resource.getChildren() )
        {
            unRegister( (AbstractResource) child );
        }
        resource.delete();
    }

    /**
     * Add a listener to the registry.
     * 
     * @param operationalListener The listener to add
     * @throws InternalUriPatternException When the listeners uri pattern is
     *                                     invalid.
     */
    public void add( OperationalListener operationalListener ) throws InternalUriPatternException
    {
        // validate wildcard
        uriHasWildcard( operationalListener.getUriPattern() );
        listeners.add( operationalListener );
        updateResourceCallBack();
    }

    /**
     * Remove a listener from the registry.
     * 
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
        for ( Entry< String, AbstractResource > e : servedResources.entrySet() )
        {
            setResourceCallBack( e.getValue() );
        }
    }

    /**
     * Set the callback of the resource on the listener that matches best the
     * resources uri.
     * 
     * @param resource The served resource.
     */
    private void setResourceCallBack( AbstractResource resource )
    {
        OperationalListener bestGetListener= null;
        OperationalListener bestPostListener= null;
        OperationalListener bestPutListener= null;
        OperationalListener bestDeleteListener= null;
        OperationalListener bestFetchListener= null;
        OperationalListener bestPatchListener= null;
        OperationalListener bestIpatchListener= null;
        int maxGetMatchlevel= 0;
        int maxPostMatchlevel= 0;
        int maxPutMatchlevel= 0;
        int maxDeleteMatchlevel= 0;
        int maxFetchMatchlevel= 0;
        int maxPatchMatchlevel= 0;
        int maxIpatchMatchlevel= 0;
        for ( OperationalListener listener : listeners )
        {
            int matchLevel;
            try
            {
                matchLevel= matchUri( listener.getUriPattern(), resource.getURI() );
            }
            catch ( InternalUriPatternException e )
            {
                // listeners uriPattern is invalid. Should not occur.
                LOGGER.error( e.getMessage() );
                matchLevel= 0;
            }
            if ( matchLevel > maxGetMatchlevel && listener.requestCodeFlags.isGet() )
            {
                maxGetMatchlevel= matchLevel;
                bestGetListener= listener;
            }
            if ( matchLevel > maxPostMatchlevel && listener.requestCodeFlags.isPost() )
            {
                maxPostMatchlevel= matchLevel;
                bestPostListener= listener;
            }
            if ( matchLevel > maxPutMatchlevel && listener.requestCodeFlags.isPut() )
            {
                maxPutMatchlevel= matchLevel;
                bestPutListener= listener;
            }
            if ( matchLevel > maxDeleteMatchlevel && listener.requestCodeFlags.isDelete() )
            {
                maxDeleteMatchlevel= matchLevel;
                bestDeleteListener= listener;
            }
            if ( matchLevel > maxFetchMatchlevel && listener.requestCodeFlags.isFetch() )
            {
                maxFetchMatchlevel= matchLevel;
                bestFetchListener= listener;
            }
            if ( matchLevel > maxPatchMatchlevel && listener.requestCodeFlags.isPatch() )
            {
                maxPatchMatchlevel= matchLevel;
                bestPatchListener= listener;
            }
            if ( matchLevel > maxIpatchMatchlevel && listener.requestCodeFlags.isIpatch() )
            {
                maxIpatchMatchlevel= matchLevel;
                bestIpatchListener= listener;
            }
        }
        // set the Get callback to the best found listener
        if ( bestGetListener != null )
        {
            resource.setGetCallback( bestGetListener.getCallback() );
        }
        else
        {
            resource.setGetCallback( null );
        }
        // set the Post callback to the best found listener
        if ( bestPostListener != null )
        {
            resource.setPostCallback( bestPostListener.getCallback() );
        }
        else
        {
            resource.setPostCallback( null );
        }
        // set the Put callback to the best found listener
        if ( bestPutListener != null )
        {
            resource.setPutCallback( bestPutListener.getCallback() );
        }
        else
        {
            resource.setPutCallback( null );
        }
        // set the Delete callback to the best found listener
        if ( bestDeleteListener != null )
        {
            resource.setDeleteCallback( bestDeleteListener.getCallback() );
        }
        else
        {
            resource.setDeleteCallback( null );
        }
        // set the Fetch callback to the best found listener
        if ( bestFetchListener != null )
        {
            resource.setFetchCallback( bestFetchListener.getCallback() );
        }
        else
        {
            resource.setFetchCallback( null );
        }
        // set the Patch callback to the best found listener
        if ( bestPatchListener != null )
        {
            resource.setPatchCallback( bestPatchListener.getCallback() );
        }
        else
        {
            resource.setPatchCallback( null );
        }
        // set the iPatch callback to the best found listener
        if ( bestIpatchListener != null )
        {
            resource.setIpatchCallback( bestIpatchListener.getCallback() );
        }
        else
        {
            resource.setIpatchCallback( null );
        }
    }

    /**
     * Get the root resource.
     * 
     * @return The root resource.
     */
    public Resource getRoot()
    {
        return root;
    }

    /**
     * Get the resources from the registry with given uri. A null uri, an empty
     * string or "/" is interpreted as the root uri.
     * 
     * @param uri The uri of the resource to get.
     * @return The served resource that has given uri, or null when the root uri is
     *         given.
     * @throws InternalResourceUriException The resource does not exist.
     */
    public ServedResource getResource( String uri ) throws InternalResourceUriException
    {
        if ( uri == null || uri.length() == 0 || uri.equals( "/" ) )
        {
            // do not expose root resource
            return null;
        }
        else
        {
            for ( Entry< String, AbstractResource > entry : servedResources.entrySet() )
            {
                AbstractResource resource= entry.getValue();
                if ( uri.equals( entry.getKey() ) && resource instanceof ServedResource )
                {
                    return (ServedResource) entry.getValue();
                }
            }
        }
        throw new InternalResourceUriException( "resource { " + uri + " } does not exist." );
    }

    /**
     * Find all resources of that have matching uri's.
     * 
     * @param uriPattern the pattern to match to.
     * @return A list containing all served resource that match. The list is not
     *         thread safe.
     */
    public List< ServedResource > findResources( String uriPattern )
    {
        // TODO regex support
        ArrayList< ServedResource > found= new ArrayList<>();

        for ( Entry< String, AbstractResource > entry : servedResources.entrySet() )
        {
            try
            {
                AbstractResource resource= entry.getValue();
                if ( matchUri( uriPattern, entry.getKey() ) > 0 && resource instanceof ServedResource )
                {
                    found.add( (ServedResource) entry.getValue() );
                }
            }
            catch ( InternalUriPatternException e )
            {
                // uriPattern is invalid. Should not occur, is already validated.
                LOGGER.error( e.getMessage() );
                break;
            }
        }
        return found;
    }

    /**
     * @param path The path of the referenced resource.
     * @return
     */
    /**
     * Find the resource responsible for handling requests.
     * If the request concerned is a Put and the parent resource allows for child creation, 
     * a virtual resource is returned for creation.
     * @param path The path of the resource to be found.
     * @param isPut Flag indicating the request concerned is a put.
     * @return The resource found or null when the path doesn't match any resource.
     */
    public Resource findResource( List< String > path, boolean isPut )
    {
        Resource parent= null;
        Resource current= root;
        for ( String name : path )
        {
            parent= current;
            current= parent.getChild( name );
            if ( current == null )
            {
                //Consider parent allowing put create request.
                Resource virtual= parent.getChild( "" );
                if ( isPut && virtual != null )
                {
                    current= virtual;
                }
                break;
            }
        }
        return current;
    }

    /**
     * Establish the degree in which a resource uri matches an uriPattern. The
     * degree is an indication how good the pattern matches. It can be used to
     * compare the matching of a resource uri to different patterns. For example the
     * resource "/one/two/three" will match to both patterns "/one/*" and
     * "/one/two/*". The latter with an higher degree because it is more specific.
     * The degree value returned is 0 when there is no match. An integer > 0 when
     * there is a match to some degree. MAX_VALUE when there is a perfect match,
     * which means the pattern equals the uri.
     * 
     * @param uriPattern  The pattern to match to.
     * @param resourceUri The resource uri to do the matching on.
     * @return The degree of matching.
     * @throws InternalUriPatternException when the pattern given is invalid
     */
    public static int matchUri( String uriPattern, String resourceUri ) throws InternalUriPatternException
    {
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
     * Get the depth of resource hierarchy in an uri.
     * 
     * @param uri the resource uri
     * @return the depth of the uri
     */
    public static int getUriDepth( String uri )
    {
        int count;
        int index;
        for (
                        count= 0, index= uri.indexOf( Defs.COAP_URI_PATHSEP, 0 ); index >= 0; index= uri
                            .indexOf( Defs.COAP_URI_PATHSEP, index + 1 ), count++
        );
        return count;
    }

    /**
     * Get the path part of an uri. That is the uri without the resource name.
     * 
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
     * Get the resource name part of an uri. That is the uri without the path
     * preceding the resource name.
     * 
     * @param uri The uri of the resource.
     * @return The resource name.
     */
    public static String getUriResourceName( String uri )
    {
        int lastPathSep= uri.lastIndexOf( Defs.COAP_URI_PATHSEP );
        if ( lastPathSep >= 0 )
        {
            return uri.substring( lastPathSep + 1 );
        }
        else
        {
            return Defs.COAP_URI_ROOTRESOURCE_NAME;
        }
    }

    /**
     * Establish whether the uri has a wildcard and is in fact a pattern.
     * 
     * @param uri The uri of a resource.
     * @return true when a wildcard is found, otherwise false.
     * @throws InternalUriPatternException
     */
    public static boolean uriHasWildcard( String uri ) throws InternalUriPatternException
    {
        int index= uri.indexOf( Defs.COAP_URI_WILDCARD );
        if ( index == -1 ) return false;
        if ( index < ( uri.length() - Defs.COAP_URI_WILDCARD.length() ) )
        {
            throw new InternalUriPatternException( "Wildcard is only allowed on pattern ending", uri );
        }
        return true;
    }

    /**
     * Get the uri of the parent of a resource.
     * 
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
