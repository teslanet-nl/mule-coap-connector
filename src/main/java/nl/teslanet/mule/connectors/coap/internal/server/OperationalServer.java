/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2026 (teslanet.nl) Rogier Cobben
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

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.elements.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.teslanet.mule.connectors.coap.api.Defs;
import nl.teslanet.mule.connectors.coap.api.ResourceConfig;
import nl.teslanet.mule.connectors.coap.api.ResourceParams;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResourceRegistryException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalResourceUriException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUriPatternException;


/**
 * The CoAP server that manages resources, listeners and maintains
 * consistency of the callback relations they have.
 */
public class OperationalServer extends CoapServer
{
    /**
     * The Logger of this class.
     */
    private static final Logger LOGGER= LoggerFactory.getLogger( OperationalServer.class );

    /**
     * The hosted resources.
     */
    private ConcurrentHashMap< String, AbstractResource > servedResources= new ConcurrentHashMap<>();

    /**
     * The listeners that are active on the resources.
     */
    private CopyOnWriteArrayList< OperationalListener > listeners= new CopyOnWriteArrayList<>();

    /**
     * Construct a server  using given configuration.
     * @param rootResource 
     * @param config The coap configuration.
     */
    public OperationalServer( Configuration config )
    {
        super( config );
        setMessageDeliverer( new ServedResourceMessageDeliverer( this, config ) );
        servedResources.put( Defs.COAP_URI_EMPTY, (AbstractResource) this.getRoot() );
    }

    /**
     * Creates a root for this server.
     *
     * @return the resource
     */
    @Override
    protected Resource createRoot()
    {
        return new ServedResource();
    }

    /**
     * Add a new resource to the registry based on given resource configuration. The
     * resource will be added as a child of resource with given parentUri. When
     * parentUri is null the resource will be added to the root.
     * 
     * @param parentUri          the uri of the parent of the new resource.
     * @param resourceDescription the definition of the resource to create
     * @throws InternalResourceUriException the parent uri does not resolve to an
     *                                      existing resource or resource parameters are invalid.
     */
    public void add( String parentUri, ResourceConfig resourceDescription ) throws InternalResourceUriException
    {
        if ( resourceDescription.getResourceName() == null || resourceDescription.getResourceName().length() == 0 )
        {
            throw new InternalResourceUriException( "An empty resource name is invalid." );
        }
        ServedResource parent= getResource( parentUri );
        if ( parent == null )
        {
            throw new InternalResourceUriException( "Parent resource not found: " + parentUri );
        }
        ServedResource resource;
        try
        {
            resource= new ServedResource( resourceDescription );
        }
        catch ( Exception e )
        {
            throw new InternalResourceUriException( "Resource parameters are invalid.", e );
        }
        register( parent, resource );
    }

    /**
     * Add a new resource to the registry based on given resource description.
     * 
     * @param resourceDescription The definition of the resource to create.
     * @throws InternalResourceUriException when the uri is invalid.
     * @throws InternalResourceRegistryException When the resource cannot be added.
     */
    public void add( ResourceParams resourceDescription ) throws InternalResourceUriException, InternalResourceRegistryException
    {
        if (
            resourceDescription.getResourcePath() == null || resourceDescription.getResourcePath().length() == 0 || getUriResourceName( resourceDescription.getResourcePath() )
                .length() == 0
        )
        {
            throw new InternalResourceUriException( "An empty resource path is invalid." );
        }
        String parentUri= OperationalServer.getParentUri( resourceDescription.getResourcePath() );
        AbstractResource parent= getResource( parentUri );
        if ( parent == null )
        {
            throw new InternalResourceRegistryException( "Parent resource not found: " + parentUri );
        }
        ServedResource resource;
        try
        {
            resource= new ServedResource( resourceDescription );
        }
        catch ( Exception e )
        {
            //also thrown when resource name is empty
            throw new InternalResourceRegistryException( "Resource parameters are invalid.", e );
        }
        register( parent, resource );
    }

    /**
     * Register resource and its children.
     * 
     * @param resource to be registered
     */
    synchronized void register( AbstractResource parent, AbstractResource resource )
    {
        parent.add( resource );
        servedResources.put( resource.getURI(), resource );
        setResourceCallBack( resource );
        // also register children recursively
        for ( Resource child : resource.getChildren() )
        {
            register( resource, (AbstractResource) child );
        }
    }

    /**
     * Remove all resources from the registry.
     */
    public void removeAll()
    {
        unRegister( servedResources.get( Defs.COAP_ROOTRESOURCE ) );
    }

    /**
     * Remove a resource from the registry
     * 
     * @param uriPattern The uri pattern defining the resources to remove
     * @throws InternalUriPatternException If an attempt is made to remove root resource.
     * @throws InternalResourceRegistryException 
     */
    public void remove( String uriPattern ) throws InternalUriPatternException
    {
        for ( AbstractResource resource : findResources( uriPattern, false ) )
        {
            unRegister( resource );
        }
    }

    /**
     * Unregister resource and its children.
     * 
     * @param resource to be registered
     */
    synchronized void unRegister( AbstractResource resource )
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
        int maxGetMatchlevel= -1;
        int maxPostMatchlevel= -1;
        int maxPutMatchlevel= -1;
        int maxDeleteMatchlevel= -1;
        int maxFetchMatchlevel= -1;
        int maxPatchMatchlevel= -1;
        int maxIpatchMatchlevel= -1;
        for ( OperationalListener listener : listeners )
        {
            int matchLevel;
            try
            {
                matchLevel= matchUri( listener.getUriPattern(), resource );
            }
            catch ( InternalUriPatternException e )
            {
                // listeners uriPattern is invalid. Should not occur.
                LOGGER.error( e.getMessage() );
                matchLevel= -1;
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
     * Get the resources from the registry with given uri. The 
     * string "/" is interpreted as the root uri.
     * 
     * @param uri The uri of the resource to get.
     * @return The served resource that has given uri, or null when the root uri is
     *         given.
     * @throws InternalResourceUriException The resource does not exist.
     */
    public ServedResource getResource( String uri ) throws InternalResourceUriException
    {
        if ( uri == null )
        {
            throw new InternalResourceUriException( "An empty resource uri is invalid." );
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
        //TODO make a not found exception
        throw new InternalResourceUriException( "Resource { " + uri + " } does not exist." );
    }

    /**
     * Find all served resources that have matching uri's.
     * Virtual resources are ignored and omitted from the returned result.
     * 
     * @param uriPattern the pattern to match to. A null or empty string value will match the root resource.
     * @param includeRoot If <code>true</code> finding the root resource is considered an error and will throw an <code>InternalUriPatternException</code>.
     * @return A list containing found resources. The list is not thread safe.
     * @throws InternalUriPatternException If the pattern is invalid.
     */
    public List< ServedResource > findResources( String uriPattern, boolean includeRoot ) throws InternalUriPatternException
    {
        // TODO regex support
        ArrayList< ServedResource > found= new ArrayList<>();
        if ( uriPattern == null )
        {
            throw new InternalUriPatternException( "Resource path pattern value is not allowed.", null );
        }
        else if ( Defs.COAP_URI_ROOTRESOURCE.equals( uriPattern ) )
        {
            if ( includeRoot )
            {
                // find root resource
                found.add( (ServedResource) servedResources.get( Defs.COAP_ROOTRESOURCE ) );
            }
            else
            {
                throw new InternalUriPatternException( "Reference to root resource is not allowed.", uriPattern );
            }
        }
        else
        {
            for ( Entry< String, AbstractResource > entry : servedResources.entrySet() )
            {
                AbstractResource resource= entry.getValue();
                if ( matchUri( uriPattern, resource ) >= 0 && resource instanceof ServedResource )
                {
                    if ( Defs.COAP_ROOTRESOURCE.equals( entry.getKey() ) && !includeRoot )
                    {
                        throw new InternalUriPatternException( "Including root resource is not allowed.", uriPattern );
                    }
                    else
                    {
                        found.add( (ServedResource) entry.getValue() );
                    }
                }
            }
        }
        return found;
    }

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
        Resource current= this.getRoot();
        for ( String name : path )
        {
            parent= current;
            current= parent.getChild( name );
            if ( current == null )
            {
                //Consider parent allowing put create request.
                Resource virtual= parent.getChild( "" );
                if ( virtual != null && isPut )
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
    public static int matchUri( String uriPattern, AbstractResource resource ) throws InternalUriPatternException
    {
        if ( uriHasWildcard( uriPattern ) )
        {
            String patternPath= getUriPath( uriPattern );
            //String resourcePath= getUriPath( resourceUri );
            if ( resource.getPath().startsWith( patternPath ) )
            {
                return getUriDepth( uriPattern );
            }
        }
        else if ( uriPattern.equals( resource.getURI() ) )
        {
            return Integer.MAX_VALUE;
        }
        return -1;
    }

    /**
     * Get the depth of resource hierarchy in an uri.
     * The root resource has depth 0.
     * The sub-resources have depth 1 and higher depending on the nesting level.
     * An absolute uri path is assumed ( starting with '/' ).
     * 
     * @param uri the resource uri
     * @return the depth of the uri
     */
    public static int getUriDepth( String uri )
    {
        int count;
        int index;
        if ( Defs.COAP_URI_ROOTRESOURCE.equals( uri ) ) return 0;
        for ( count= 0, index= uri.indexOf( Defs.COAP_URI_PATHSEP, 0 ); index >= 0; index= uri.indexOf( Defs.COAP_URI_PATHSEP, index + 1 ), count++ );
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
        if ( Defs.COAP_URI_ROOTRESOURCE.equals( uri ) ) return Defs.COAP_URI_EMPTY;
        int lastPathSep= uri.lastIndexOf( Defs.COAP_URI_PATHSEP );
        if ( lastPathSep >= 0 )
        {
            return uri.substring( 0, lastPathSep + 1 );
        }
        else
        {
            return Defs.COAP_URI_EMPTY;
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
        if ( Defs.COAP_URI_ROOTRESOURCE.equals( uri ) ) return Defs.COAP_ROOTRESOURCE_NAME;
        int lastPathSep= uri.lastIndexOf( Defs.COAP_URI_PATHSEP );
        if ( lastPathSep >= 0 )
        {
            return uri.substring( lastPathSep + 1 );
        }
        else
        {
            return Defs.COAP_ROOTRESOURCE_NAME;
        }
    }

    /**
     * Establish whether the uri pattern has a wildcard and is in fact a pattern.
     * 
     * @param uri The uri of a resource.
     * @return true when a wildcard is found, otherwise false.
     * @throws InternalUriPatternException
     */
    public static boolean uriHasWildcard( String uri ) throws InternalUriPatternException
    {
        int index= uri.indexOf( Defs.COAP_URI_WILDCARD );
        if ( index == -1 ) return false;
        if ( index < ( uri.length() - 1 ) )
        {
            throw new InternalUriPatternException( "Wildcard is only allowed on pattern ending.", uri );
        }
        if ( index > 0 && uri.charAt( index - 1 ) != Defs.COAP_URI_PATHSEP )
        {
            throw new InternalUriPatternException( "Wildcard is only allowed on whole resource name.", uri );
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
        if ( lastPathSep >= 0 )
        {
            return uri.substring( 0, lastPathSep );
        }
        else
        {
            return Defs.COAP_ROOTRESOURCE;
        }
    }
}
