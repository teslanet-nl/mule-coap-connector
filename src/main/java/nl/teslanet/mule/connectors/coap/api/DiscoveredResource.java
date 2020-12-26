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
package nl.teslanet.mule.connectors.coap.api;

/**
 * Resource of a coap-server that has been discovered. The class holds 
 * information given by the server such as the resource uri and attributes.
 * The class is immutable.
 */
public final class DiscoveredResource implements Comparable< DiscoveredResource >
{
    /**
     * The uri-path of the resource.
     */
    private final String path;

    /**
     * When true, the resource can be observed by clients.
     */
    private boolean obs= false;

    /**
     * The CoRE attribute information of the resource.
     */
    //TODO change to ResourceInfo or CoreInfo?
    private final ResourceInfoConfig info;

    /**
     * Construct DiscoveredResource from uri and ResourceInfo.
     * @param path Identifies the resource.
     * @param info The resource attributes.
     */
    public DiscoveredResource( String path, ResourceInfoConfig info )
    {
        if ( path == null ) throw new NullPointerException( "DiscoveredResource uri-path: null is not allowed" );
        this.path= path;
        this.info= new ResourceInfoConfig( info );
    }

    /**
     * Construct DiscoveredResource from uri-path and meta info.
     * @param path Identifies the resource.
     * @param obs Flag indicating the resource is observable ({@code True}), or not ({@code False})
     * @param title Human readable title of the resource.
     * @param ifdesc Interface designators.
     * @param rt Resource type designators.
     * @param sz Content size estimation of the resource.
     * @param ct Content formats of the resource.
     */
    public DiscoveredResource( String path, boolean obs, String title, String ifdesc, String rt, String sz, String ct )
    {
        if ( path == null ) throw new NullPointerException( "DiscoveredResource uri-path of null is not allowed" );
        this.path= path;
        this.obs= obs;
        this.info= new ResourceInfoConfig();
        this.info.setTitle( title );
        this.info.setIfdesc( ifdesc );
        this.info.setRt( rt );
        this.info.setSz( sz );
        this.info.setCt( ct );
    }

    /**
     * Get resource uri-path.
     * @return the uri-path of the resource
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Is resource observable.
     * @return true when resource is observable, otherwise false
     */
    public boolean isObs()
    {
        return obs;
    }

    /**
     * Get resource title.
     * @return the title of the resource
     */
    public String getTitle()
    {
        return info.getTitle();
    }

    /**
     * Get resource if.
     * @return the if of the resource
     */
    public String getIf()
    {
        return info.getIfdesc();
    }

    /**
     * Get resource rt.
     * @return the rt of the resource
     */
    public String getRt()
    {
        return info.getRt();
    }

    /**
     * Get resource sz.
     * @return the sz of the resource
     */
    public String getSz()
    {
        return info.getSz();
    }

    /**
     * Get resource ct.
     * @return the ct of the resource
     */
    public String getCt()
    {
        return info.getCt();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( DiscoveredResource other )
    {
        return this.path.compareTo( other.path );
    }

}
