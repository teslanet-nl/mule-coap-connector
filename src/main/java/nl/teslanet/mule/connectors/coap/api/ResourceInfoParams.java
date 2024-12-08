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
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * CoRE Information of the resource 
 *
 */
public class ResourceInfoParams implements CoreInfo
{
    /**
     * Human readable title of the resource. 
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @ParameterDsl( allowReferences= true )
    @Summary( "Human readable title of the resource. " )
    private String title;

    /**
     * Comma separated list of interface descriptions that apply to the resource.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @ParameterDsl( allowReferences= true )
    @Summary( "Comma separated list of interface descriptions that apply to the resource." )
    @Alias( "if" )
    private String ifdesc;

    /**
     * Comma separated list of resource types that apply to the resource.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @ParameterDsl( allowReferences= true )
    @Summary( "Comma separated list of resource types that apply to the resource." )
    @DisplayName( "rt" )
    private String rt;

    /**
     * Maximum size estimate of the resource. [bytes]
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @ParameterDsl( allowReferences= true )
    @Summary( "Maximum size estimate of the resource. [bytes]" )
    @DisplayName( "sz" )
    private String sz;

    /**
     * Comma separated list of content types that are available on the resource.
     * The types are specified by an integer as defined by CoAP.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @ParameterDsl( allowReferences= true )
    @Summary( "Comma separated list of CoAP content types that are available on the resource." )
    @Example( "0,41" )
    @DisplayName( "ct" )
    private String ct;

    /**
     * Default constructor of ResourceInfo.
     */
    public ResourceInfoParams()
    {
    }

    /**
     * Copy constructor of ResourceInfo.
     */
    public ResourceInfoParams( ResourceInfoParams info )
    {
        this.title= info.getTitle();
        this.ifdesc= info.getIfdesc();
        this.rt= info.getRt();
        this.sz= info.getSz();
        this.ct= info.getCt();
    }

    /**
     * @return the title
     */
    @Override
    public String getTitle()
    {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle( String title )
    {
        this.title= title;
    }

    /**
     * @return the ifdesc
     */
    @Override
    public String getIfdesc()
    {
        return ifdesc;
    }

    /**
     * @param ifdesc the ifdesc to set
     */
    public void setIfdesc( String ifdesc )
    {
        this.ifdesc= ifdesc;
    }

    /**
     * @return the rt
     */
    @Override
    public String getRt()
    {
        return rt;
    }

    /**
     * @param rt the rt to set
     */
    public void setRt( String rt )
    {
        this.rt= rt;
    }

    /**
     * @return the sz
     */
    @Override
    public String getSz()
    {
        return sz;
    }

    /**
     * @param sz the sz to set
     */
    public void setSz( String sz )
    {
        this.sz= sz;
    }

    /**
     * @return the ct
     */
    @Override
    public String getCt()
    {
        return ct;
    }

    /**
     * @param ct the ct to set
     */
    public void setCt( String ct )
    {
        this.ct= ct;
    }
}
