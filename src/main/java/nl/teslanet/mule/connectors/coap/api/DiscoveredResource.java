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


import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Resource of a coap-server that has been discovered. The class holds 
 * information given by the server such as the resource uri and attributes.
 * The class is immutable.
 */
public final class DiscoveredResource implements Comparable< DiscoveredResource >, Serializable
{
    /**
     * Version ID.
     */
    private static final long serialVersionUID= 1L;

    /**
     * The uri-path of the resource.
     */
    private final String path;

    /**
     * When true, the resource can be observed by clients.
     */
    private final boolean obs;

    /**
     * Human readable title of the resource. 
     */
    private String title;

    /**
     * List of interface descriptions that apply to the resource.
     */
    private LinkedList< String > ifdesc= new LinkedList<>();

    /**
     * List of resource types that apply to the resource.
     */
    private LinkedList< String > rt= new LinkedList<>();

    /**
     * Maximum size estimate of the resource. [bytes]
     */
    private String sz;

    /**
     * List of content types that are available on the resource.
     * The types are specified by an integer as defined by CoAP.
     */
    private LinkedList< String > ct= new LinkedList<>();

    /**
     * HashCode, that will be set to value unequal to zero when needed.
     */
    private final transient AtomicInteger hashCode= new AtomicInteger();

    /**
     * The Comparator of lists.
     */
    public static final Comparator< List< String > > LISTCOMPARATOR= ( o1, o2 ) -> {

        if ( o1 == o2 ) return 0;
        if ( o1.size() != o2.size() ) return( o1.size() < o2.size() ? -1 : 1 );
        Iterator< String > it1= o1.iterator();
        Iterator< String > it2= o2.iterator();
        int result= 0;
        while ( result == 0 && it1.hasNext() && it2.hasNext() )
        {
            result= Objects.compare( it1.next(), it2.next(), Comparator.nullsLast( Comparator.naturalOrder() ) );
        }
        return result;
    };

    /**
     * The Comparator.
     */
    public static final Comparator< DiscoveredResource > COMPARATOR= Comparator.comparing( DiscoveredResource::getPath, Comparator.naturalOrder() ).thenComparing(
        DiscoveredResource::getObs,
        Comparator.nullsLast( Comparator.naturalOrder() )
    ).thenComparing( DiscoveredResource::getTitle, Comparator.nullsLast( Comparator.naturalOrder() ) ).thenComparing( DiscoveredResource::getIf, LISTCOMPARATOR ).thenComparing(
        DiscoveredResource::getRt,
        LISTCOMPARATOR
    ).thenComparing( DiscoveredResource::getSz, Comparator.nullsLast( Comparator.naturalOrder() ) ).thenComparing( DiscoveredResource::getCt, LISTCOMPARATOR );

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
    public DiscoveredResource( String path, boolean obs, String title, List< String > ifdesc, List< String > rt, String sz, List< String > ct )
    {
        if ( path == null ) throw new NullPointerException( "DiscoveredResource uri-path of null is not allowed" );
        this.path= path;
        this.obs= obs;
        this.title= title;
        this.ifdesc.addAll( ifdesc );
        this.rt.addAll( rt );
        this.sz= sz;
        this.ct.addAll( ct );
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
        return title;
    }

    /**
     * Get resource if values.
     * @return Unmodifiable list of if values.
     */
    public List< String > getIf()
    {
        return Collections.unmodifiableList( ifdesc );
    }

    /**
     * Get resource rt values.
     * @return Unmodifiable list of if values.
     */
    public List< String > getRt()
    {
        return Collections.unmodifiableList( rt );
    }

    /**
     * Get resource sz.
     * @return the sz of the resource
     */
    public String getSz()
    {
        return sz;
    }

    /**
     * Get resource ct values.
     * @return Unmodifiable list of if values.
     */
    public List< String > getCt()
    {
        return Collections.unmodifiableList( ct );
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
        if ( !DiscoveredResource.class.isInstance( other ) ) return false;
        return 0 == COMPARATOR.compare( this, (DiscoveredResource) other );
    }

    /**
     * Calculate the hashcode of this object.
     * @return The hashCode of the object
     */
    protected int calculateHashCode()
    {
        int result= 113;
        result= 31 * result + Objects.hashCode( this.path );
        result= 31 * result + Objects.hashCode( this.obs );
        result= 31 * result + Objects.hashCode( this.title );
        for ( String value : this.ifdesc )
        {
            result= 31 * result + Objects.hashCode( value );
        }
        for ( String value : this.rt )
        {
            result= 31 * result + Objects.hashCode( value );
        }
        result= 31 * result + Objects.hashCode( sz );
        for ( String value : this.ct )
        {
            result= 31 * result + Objects.hashCode( value );
        }
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
        StringBuilder builder= new StringBuilder( "CoRE info {" );
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
