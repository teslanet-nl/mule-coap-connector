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
 */
public class ResourceParams
{
    /**
    * The absolute path of the resource that will be used to identify it in CoAP uri's.
    */
    @Parameter
    @Expression(ExpressionSupport.SUPPORTED)
    @ParameterDsl(allowReferences= true)
    @Summary("The absolute path of the resource to create.")
    @Example("/my_parent_resource/my_resource")
    private String resourcePath;

    /**
     * When true, Get requests are allowed on the resource.
     */
    @Parameter
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("When true, GET requests are allowed on the resource.")
    private boolean get;

    /**
     * When true, Post requests are allowed on the resource.
     */
    @Parameter
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("When true, POST requests are allowed on the resource.")
    private boolean post= false;

    /**
     * When true, Put requests are allowed on the resource.
     */
    @Parameter
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("When true, PUT requests are allowed on the resource.")
    private boolean put= false;

    /**
     * When true, Delete requests are allowed on the resource.
     */
    @Parameter
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("When true, DELETE requests are allowed on the resource")
    private boolean delete= false;

    /**
     * When true, the resource can be observed by clients.
     */
    @Parameter
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("When true, the resource can be observed by clients")
    private boolean observable= false;

    /**
     * When true an acknowledgement is immediately sent to the client, before processing the request and returning the response.
     * Use this when processing takes longer than the acknowledgment-timeout of the client.  
     */
    @Parameter
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("When true an acknowledgement is immediately sent to the client, before processing the request.")
    private boolean earlyAck= false;

    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @ParameterDsl(allowReferences= true)
    @Summary("The CoRE information describing the contrained resource for discovery.")
    @DisplayName("Discovery CoRE Info")
    private ResourceInfoParams coreInfo;

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
     * @param observable the observable to set
     */
    public void setObservable( boolean observable )
    {
        this.observable= observable;
    }

    /**
     * @return the earlyAck
     */
    public boolean isEarlyAck()
    {
        return earlyAck;
    }

    /**
     * @param earlyAck the earlyAck to set
     */
    public void setEarlyAck( boolean earlyAck )
    {
        this.earlyAck= earlyAck;
    }

    /**
     * @return the discovery coreInfo of the resource
     */
    public ResourceInfoParams getInfo()
    {
        return coreInfo;
    }
    
    /**
     * @param coreInfo The discovery coreInfo to add to the resource
     */
    public void setAddInfo( ResourceInfoParams coreInfo )
    {
        this.coreInfo= coreInfo;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "resource { " + resourcePath + " }";
    }

}
