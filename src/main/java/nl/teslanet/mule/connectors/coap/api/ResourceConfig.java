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
package nl.teslanet.mule.connectors.coap.api;


import java.util.List;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Configuration of a CoAP resource.
 * The configuration is the resource description used to construct resources on a CoAP server.
 */
@Alias("resource")
public class ResourceConfig
{
    /**
    * The name of the resource that will be used to identify it in CoAP uri's.
    */
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Summary("The name of the resource that will be used to identify it in CoAP uri's.")
    @Example("my_resource")
    private String resourceName;

    /**
     * When true, Get requests are allowed on the resource.
     */
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Summary("When true, GET requests are allowed on the resource.")
    private boolean get= false;

    /**
     * When true, Post requests are allowed on the resource.
     */
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Summary("When true, POST requests are allowed on the resource.")
    private boolean post= false;

    /**
     * When true, Put requests are allowed on the resource.
     */
    @Parameter
    //@ParameterGroup(name= "Methods")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Summary("When true, PUT requests are allowed on the resource.")
    private boolean put= false;

    /**
     * When true, Delete requests are allowed on the resource.
     */
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Summary("When true, DELETE requests are allowed on the resource")
    private boolean delete= false;

    /**
     * When true, the resource can be observed by clients.
     */
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Summary("When true, the resource can be observed by clients")
    private boolean observable= false;

    /**
     * When true an acknowledgement is immediately sent to the client, before processing the request and returning the response.
     * Use this when processing takes longer than the acknowledgment-timeout of the client.  
     */
    @Parameter
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Summary("When true an acknowledgement is immediately sent to the client, before processing the request.")
    private boolean earlyAck= false;

    @Parameter
    @Optional
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Summary("The CoRE information describing the contrained resource for discovery.")
    @DisplayName("Discovery CoRE Info")
    private ResourceInfoConfig coreInfoConfig;

    /**
     * The subordinate resources of the resource..
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Summary(value= "The subordinate resources of the resource.")
    private List< ResourceConfig > subResources;

    /**
    * @return the resourceName
    */
    public String getResourceName()
    {
        return resourceName;
    }

    /**
     * @param resourceName the name to set
     */
    public void setResourceName( String resourceName )
    {
        this.resourceName= resourceName;
    }

    /**
     * @return the get
     */
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
     * @return the post
     */
    public boolean isPost()
    {
        return post;
    }

    /**
     * @param post the post to set
     */
    public void setPost( boolean post )
    {
        this.post= post;
    }

    /**
     * @return the put
     */
    public boolean isPut()
    {
        return put;
    }

    /**
     * @param put the put to set
     */
    public void setPut( boolean put )
    {
        this.put= put;
    }

    /**
     * @return the delete
     */
    public boolean isDelete()
    {
        return delete;
    }

    /**
     * @param delete the delete to set
     */
    public void setDelete( boolean delete )
    {
        this.delete= delete;
    }

    /**
     * @return the observable
     */
    public boolean isObservable()
    {
        return observable;
    }

    /**
     * @param observable the observable to set.
     */
    public void setObservable( boolean observable )
    {
        this.observable= observable;
    }

    /**
     * @return the earlyAck.
     */
    public boolean isEarlyAck()
    {
        return earlyAck;
    }

    /**
     * @param earlyAck the earlyAck to set.
     */
    public void setEarlyAck( boolean earlyAck )
    {
        this.earlyAck= earlyAck;
    }

    /**
     * @return the configured CoRE info.
     */
    public ResourceInfoConfig getCoreInfoConfig()
    {
        return coreInfoConfig;
    }

    /**
     * @param coreInfo the CoRE info configuration to set.
     */
    public void setCoreInfoConfig( ResourceInfoConfig coreInfo )
    {
        this.coreInfoConfig= coreInfo;
    }

    /**
     * @return the subordinate resources.
     */
    public List< ResourceConfig > getSubResources()
    {
        return subResources;
    }

    /**
     * @param resources the subordinate resources to set on this resource.
     */
    public void setResources( List< ResourceConfig > resources )
    {
        this.subResources= resources;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        String printName= ( resourceName == null ? "nullName" : resourceName );
        String printResources= ( subResources == null ? "leaf" : subResources.toString() );
        return printName + " : " + printResources;
    }
}
