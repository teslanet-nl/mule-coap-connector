/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2024 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.api;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Parameters of a CoAP resource.
 * The parameters are used to construct resources on a CoAP server.
 */
public class ResourceParams implements ConfigurableResource
{
    /**
    * The absolute path of the resource that will be used to identify it in CoAP uri's.
    */
    @Parameter
    @Expression( ExpressionSupport.SUPPORTED )
    @ParameterDsl( allowReferences= true )
    @Summary( "The absolute path of the resource to create." )
    @Example( "/my_parent_resource/my_resource" )
    private String resourcePath;

    /**
     * When true, Get requests are allowed on the resource.
     */
    @Parameter
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "When true, GET requests are allowed on the resource." )
    private boolean get= false;

    /**
     * When true, Post requests are allowed on the resource.
     */
    @Parameter
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "When true, POST requests are allowed on the resource." )
    private boolean post= false;

    /**
     * When true, Put requests are allowed on the resource.
     */
    @Parameter
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "When true, PUT requests are allowed on the resource." )
    private boolean put= false;

    /**
     * When true, Delete requests are allowed on the resource.
     */
    @Parameter
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "When true, DELETE requests are allowed on the resource" )
    private boolean delete= false;

    /**
     * When true, Fetch requests are allowed on the resource.
     */
    @Parameter
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "When true, FETCH requests are allowed on the resource" )
    private boolean fetch= false;

    /**
     * When true, Patch requests are allowed on the resource.
     */
    @Parameter
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "When true, PATCH requests are allowed on the resource" )
    private boolean patch= false;

    /**
     * When true, iPatch requests are allowed on the resource.
     */
    @Parameter
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "When true, iPatch requests are allowed on the resource" )
    private boolean ipatch= false;

    /**
     * When true, the resource can be observed by clients.
     */
    @Parameter
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "When true, the resource can be observed by clients" )
    private boolean observable= false;

    /**
     * When true an acknowledgement is immediately sent to the client, before processing the request and returning the response.
     * Use this when processing takes longer than the acknowledgment-timeout of the client.  
     */
    @Parameter
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "When true an acknowledgement is immediately sent to the client, before processing the request." )
    private boolean earlyAck= false;

    /**
     * The CoRE information describing the contrained resource for discovery.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @ParameterDsl( allowReferences= true )
    @Summary( "The CoRE information describing the contrained resource for discovery." )
    @DisplayName( "Discovery CoRE Info" )
    private ResourceInfoParams coreInfo= null;

    /**
     * The creation of subordinate resources configuration.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @ParameterDsl( allowReferences= true )
    @Summary( "The creation of subordinate resources parameters." )
    @DisplayName( "New sub-resource" )
    private NewSubResourceParams newSubResource= null;

    /**
    * @return the absolute path of the resource.
    */
    public String getResourcePath()
    {
        return resourcePath;
    }

    /**
     * @param resourcePath the absolute path of the resource to set.
     */
    public void setResourcePath( String resourcePath )
    {
        this.resourcePath= resourcePath;
    }

    /**
     * @return The get.
     */
    @Override
    public boolean isGet()
    {
        return get;
    }

    /**
     * @param get the get to set
     */
    public void setGet( boolean get )
    {
        this.get= get;
    }

    /**
     * @return The post.
     */
    @Override
    public boolean isPost()
    {
        return post;
    }

    /**
     * @param post The post to set.
     */
    public void setPost( boolean post )
    {
        this.post= post;
    }

    /**
     * @return The put.
     */
    @Override
    public boolean isPut()
    {
        return put;
    }

    /**
     * @param put The put to set.
     */
    public void setPut( boolean put )
    {
        this.put= put;
    }

    /**
     * @return The delete.
     */
    @Override
    public boolean isDelete()
    {
        return delete;
    }

    /**
     * @param delete The delete to set.
     */
    public void setDelete( boolean delete )
    {
        this.delete= delete;
    }

    /**
     * @return The fetch.
     */
    @Override
    public boolean isFetch()
    {
        return fetch;
    }

    /**
     * @param fetch The fetch to set.
     */
    public void setFetch( boolean fetch )
    {
        this.fetch= fetch;
    }

    /**
     * @return The patch.
     */
    @Override
    public boolean isPatch()
    {
        return patch;
    }

    /**
     * @param patch The patch to set.
     */
    public void setPatch( boolean patch )
    {
        this.patch= patch;
    }

    /**
     * @return The ipatch.
     */
    @Override
    public boolean isIpatch()
    {
        return ipatch;
    }

    /**
     * @param ipatch The ipatch to set.
     */
    public void setIpatch( boolean ipatch )
    {
        this.ipatch= ipatch;
    }

    /**
     * @return The observable flag.
     */
    @Override
    public boolean isObservable()
    {
        return observable;
    }

    /**
     * @param observable The observable flag to set.
     */
    public void setObservable( boolean observable )
    {
        this.observable= observable;
    }

    /**
     * @return The earlyAck.
     */
    @Override
    public boolean isEarlyAck()
    {
        return earlyAck;
    }

    /**
     * @param earlyAck The earlyAck to set.
     */
    public void setEarlyAck( boolean earlyAck )
    {
        this.earlyAck= earlyAck;
    }

    /**
     * @return The configured CoRE info.
     */
    @Override
    public CoreInfo getCoreInfo()
    {
        return coreInfo;
    }

    /**
     * @param coreInfo The CoRE info parameters to set.
     */
    public void setCoreInfo( ResourceInfoParams coreInfo )
    {
        this.coreInfo= coreInfo;
    }

    /**
     * @return The new-sub-resource parameters.
     */
    @Override
    public NewSubResource getNewSubResource()
    {
        return newSubResource;
    }

    /**
     * @param newSubresource The new-sub-resource parameters to set.
     */
    public void setNewSubResource( NewSubResource newSubresource )
    {
        this.newSubResource= new NewSubResourceParams( newSubresource.isPut(), newSubresource.isEarlyAck() );
    }

    /**
     * The string representation.
     */
    @Override
    public String toString()
    {
        return "CoAP Resource params { " + resourcePath + " }";
    }
}
