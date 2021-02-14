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


import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


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
    private final boolean obs;

    /**
     * The CoRE attribute information of the resource.
     */
    private final ResourceInfoConfig coreInfo;

    /**
     * HashCode, that will be set to value unequal to zero when needed.
     */
    private final AtomicInteger hashCode= new AtomicInteger();

    public static final Comparator< DiscoveredResource > COMPARATOR= Comparator.comparing( DiscoveredResource::getPath, Comparator.naturalOrder() ).thenComparing(
        DiscoveredResource::getObs,
        Comparator.nullsLast( Comparator.naturalOrder() ) ).thenComparing( DiscoveredResource::getTitle, Comparator.nullsLast( Comparator.naturalOrder() ) ).thenComparing(
            DiscoveredResource::getIf,
            Comparator.nullsLast( Comparator.naturalOrder() ) ).thenComparing( DiscoveredResource::getRt, Comparator.nullsLast( Comparator.naturalOrder() ) ).thenComparing(
                DiscoveredResource::getSz,
                Comparator.nullsLast( Comparator.naturalOrder() ) ).thenComparing( DiscoveredResource::getCt, Comparator.nullsLast( Comparator.naturalOrder() ) );

    /**
     * Construct DiscoveredResource from uri and ResourceInfo.
     * @param path Identifies the resource.
     * @param obs Flag indicating the resource is observable ({@code True}), or not ({@code False})
     * @param coreInfo The CoRE resource attributes.
     */
    /**
     * @param path
     * @param coreInfo
     */
    public DiscoveredResource( String path, boolean obs, ResourceInfoConfig coreInfo )
    {
        if ( path == null ) throw new NullPointerException( "DiscoveredResource uri-path: null is not allowed" );
        this.path= path;
        this.obs= obs;
        if ( coreInfo == null )
        {
            this.coreInfo= new ResourceInfoConfig();
        }
        else
        {
            this.coreInfo= new ResourceInfoConfig( coreInfo );
        }
    }

    /**
     * Construct DiscoveredResource from uri-path and meta coreInfo.
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
        this.coreInfo= new ResourceInfoConfig();
        this.coreInfo.setTitle( title );
        this.coreInfo.setIfdesc( ifdesc );
        this.coreInfo.setRt( rt );
        this.coreInfo.setSz( sz );
        this.coreInfo.setCt( ct );
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
     * Get resource observable as Boolean.
     * @return true when resource is observable, otherwise false
     */
    public Boolean getObs()
    {
        return Boolean.valueOf( obs );
    }

    /**
     * Get resource title.
     * @return the title of the resource
     */
    public String getTitle()
    {
        return coreInfo.getTitle();
    }

    /**
     * Get resource if.
     * @return the if of the resource
     */
    public String getIf()
    {
        return coreInfo.getIfdesc();
    }

    /**
     * Get resource rt.
     * @return the rt of the resource
     */
    public String getRt()
    {
        return coreInfo.getRt();
    }

    /**
     * Get resource sz.
     * @return the sz of the resource
     */
    public String getSz()
    {
        return coreInfo.getSz();
    }

    /**
     * Get resource ct.
     * @return the ct of the resource
     */
    public String getCt()
    {
        return coreInfo.getCt();
    }

    /**
     * Compare to other DiscoveredResource object.
     * @param   other The reference DiscoveredResource object with which to compare.
     * @return  a negative integer, zero, or a positive integer as this DiscoveredResource object
     *          is less than, equal to, or greater than the other DiscoveredResource object.
     */
    @Override
    public int compareTo( DiscoveredResource other )
    {
        if ( other == null ) return 1;
        return COMPARATOR.compare( this, other );
    }

    /**
     * Indicates whether some other DiscoveredResource object is "equal to" this one.
     * @param   other The reference DiscoveredResource object with which to compare.
     * @return  a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object.
     */
    @Override
    public boolean equals( Object other )
    {
        if ( !DiscoveredResource.class.isInstance( other )) return false;
        return 0 == COMPARATOR.compare( this, (DiscoveredResource) other );
    }

    /**
     * Calculate the hashcode of this object.
     * @return 
     */
    protected int calculateHashCode()
    {
        int result= 113;
        result= 31 * result + Objects.hashCode( this.path );
        result= 31 * result + Objects.hashCode( this.obs );
        result= 31 * result + Objects.hashCode( this.coreInfo.getTitle() );
        result= 31 * result + Objects.hashCode( this.coreInfo.getIfdesc() );
        result= 31 * result + Objects.hashCode( this.coreInfo.getRt() );
        result= 31 * result + Objects.hashCode( this.coreInfo.getSz() );
        result= 31 * result + Objects.hashCode( this.coreInfo.getCt() );
        //never set hashCode to 0
        return( result == 0 ? 1 : result );
    }

    /**
     * Get the hashcode of this object.
     */
    @Override
    public int hashCode()
    {
        if ( hashCode.get() == 0 )
        {
            hashCode.compareAndSet( 0, calculateHashCode() );
        }
        return hashCode.get();
    }
    
    
    /**
     * Convert to string.
     */
    @Override
    public String toString()
    {
        StringBuilder builder= new StringBuilder( "CoRE info {");
        builder.append( " path= " );
        builder.append( getPath() );
        builder.append( ", obs= " );
        builder.append( isObs() );
        builder.append( ", title= " );
        builder.append( getTitle() );
        builder.append( ", if= " );
        builder.append( getIf() );
        builder.append( ", rt= " );
        builder.append( getRt() );
        builder.append( ", sz= " );
        builder.append( getSz() );
        builder.append( ", ct= " );
        builder.append( getCt() );
        builder.append( " }" );
        return builder.toString();
    }
}
