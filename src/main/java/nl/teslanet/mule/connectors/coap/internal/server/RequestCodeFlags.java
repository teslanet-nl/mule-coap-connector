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
package nl.teslanet.mule.connectors.coap.internal.server;


/**
 * The RequestCodeFlags mantains a set of flags representing the CoAP request
 * codes.
 */
public class RequestCodeFlags implements Comparable< RequestCodeFlags >
{
    private static final int GET_FLAG= 0x1;

    private static final int POST_FLAG= 0x2;

    private static final int PUT_FLAG= 0x4;

    private static final int DELETE_FLAG= 0x8;

    private static final int FETCH_FLAG= 0x10;

    private static final int PATCH_FLAG= 0x20;

    private static final int IPATCH_FLAG= 0x40;

    /**
     * Bitflags indicating which request codes are active.
     */
    private int flags= 0;

    /**
     * Default constructor
     */
    public RequestCodeFlags()
    {
        super();
    }

    /**
     * Copy constructor
     */
    public RequestCodeFlags( RequestCodeFlags original )
    {
        super();
        if ( original != null )
        {
            setGet( original.isGet() );
            setPost( original.isPost() );
            setPut( original.isPut() );
            setDelete( original.isDelete() );
            setFetch( original.isFetch() );
            setPatch( original.isPatch() );
            setIpatch( original.isIpatch() );
        }
    }

    /**
     * Constructor setting the flags.
     * @param getFlag the state of the get flag to set
     * @param postFlag the state of the post flag to set
     * @param putFlag the state of the get put to set
     * @param deleteFlag the state of the delete flag to set
     * @param fetchFlag the state of the fetch flag to set
     * @param patchFlag the state of the patch flag to set
     * @param ipatchFlag the state of the ipatch flag to set
     */
    public RequestCodeFlags( boolean getFlag, boolean postFlag, boolean putFlag, boolean deleteFlag, boolean fetchFlag, boolean patchFlag, boolean ipatchFlag )
    {
        super();
        setGet( getFlag );
        setPost( postFlag );
        setPut( putFlag );
        setDelete( deleteFlag );
        setFetch( fetchFlag );
        setPatch( patchFlag );
        setIpatch( ipatchFlag );
    }

    /**
     * Set the Get flag
     * @param flag the state of the flag to set
     */
    public void setGet( boolean flag )
    {
        if ( flag )
        {
            flags|= GET_FLAG;
        }
        else
        {
            flags&= ~GET_FLAG;
        }
    }

    /**
     * @return the state of the get flag
     */
    public boolean isGet()
    {
        return ( flags & GET_FLAG ) != 0;
    }

    /**
     * @return the inverse state of the get flag
     */
    public boolean isNotGet()
    {
        return ( flags & GET_FLAG ) == 0;
    }

    /**
     * Set the post flag.
     * @param flag the state of the flag
     */
    public void setPost( boolean flag )
    {
        if ( flag )
        {
            flags|= POST_FLAG;
        }
        else
        {
            flags&= ~POST_FLAG;
        }
    }

    /**
     * @return the state of the post flag
     */
    public boolean isPost()
    {
        return ( flags & POST_FLAG ) != 0;
    }

    /**
     * @return the inverse state of the post flag
     */
    public boolean isNotPost()
    {
        return ( flags & POST_FLAG ) == 0;
    }

    /**
     * Set the put flag.
     * @param flag the state of the flag
     */
    public void setPut( boolean flag )
    {
        if ( flag )
        {
            flags|= PUT_FLAG;
        }
        else
        {
            flags&= ~PUT_FLAG;
        }
    }

    /**
     * @return the state of the put flag
     */
    public boolean isPut()
    {
        return ( flags & PUT_FLAG ) != 0;
    }

    /**
     * @return the inverse state of the put flag
     */
    public boolean isNotPut()
    {
        return ( flags & PUT_FLAG ) == 0;
    }
    
    /**
     * Set the delete flag.
     * @param flag the state of the flag
     */
    public void setDelete( boolean flag )
    {
        if ( flag )
        {
            flags|= DELETE_FLAG;
        }
        else
        {
            flags&= ~DELETE_FLAG;
        }
    }

    /**
     * @return the state of the delete flag
     */
    public boolean isDelete()
    {
        return ( flags & DELETE_FLAG ) != 0;
    }

    /**
     * @return the inverse state of the delete flag
     */
    public boolean isNotDelete()
    {
        return ( flags & DELETE_FLAG ) == 0;
    }
    
    /**
     * Set the fetch flag.
     * @param flag the state of the flag
     */
    public void setFetch( boolean flag )
    {
        if ( flag )
        {
            flags|= FETCH_FLAG;
        }
        else
        {
            flags&= ~FETCH_FLAG;
        }
    }

    /**
     * @return the state of the fetch flag
     */
    public boolean isFetch()
    {
        return ( flags & FETCH_FLAG ) != 0;
    }

    /**
     * @return the inverse state of the fetch flag
     */
    public boolean isNotFetch()
    {
        return ( flags & FETCH_FLAG ) == 0;
    }
    
    /**
     * Set the patch flag.
     * @param flag the state of the flag
     */
    public void setPatch( boolean flag )
    {
        if ( flag )
        {
            flags|= PATCH_FLAG;
        }
        else
        {
            flags&= ~PATCH_FLAG;
        }
    }

    /**
     * @return the state of the patch flag
     */
    public boolean isPatch()
    {
        return ( flags & PATCH_FLAG ) != 0;
    }

    /**
     * @return the inverse state of the patch flag
     */
    public boolean isNotPatch()
    {
        return ( flags & PATCH_FLAG ) == 0;
    }
    
    /**
     * Set the ipatch flag.
     * @param flag the state of the flag
     */
    public void setIpatch( boolean flag )
    {
        if ( flag )
        {
            flags|= IPATCH_FLAG;
        }
        else
        {
            flags&= ~IPATCH_FLAG;
        }
    }

    /**
     * @return the state of the ipatch flag
     */
    public boolean isIpatch()
    {
        return ( flags & IPATCH_FLAG ) != 0;
    }

    /**
     * @return the inverse state of the ipatch flag
     */
    public boolean isNotIpatch()
    {
        return ( flags & IPATCH_FLAG ) == 0;
    }

    /**
     * Check on equality to another object. 
     * These are equal when the flags have equal values.  
     * @param other The other object to test for equality
     * @return true when flags are equal
     */
    @Override
    public boolean equals( Object other )
    {
        if ( other == null ) return false;
        if ( !( other instanceof RequestCodeFlags ) )
        {
            return false;
        }
        return( this.flags == ( (RequestCodeFlags) other ).flags );
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( RequestCodeFlags other )
    {
        if ( this == other ) return 0;
        if ( null == other ) return 1;
        if ( this.flags < other.flags ) return -1;
        if ( this.flags > other.flags ) return 1;
        return 0;
    }

    /**
     * Get hashCode
     */
    @Override
    public int hashCode()
    {
        return flags;
    }

    /**
     * Get String representation of the requestCodeFlags.
     * When a flag is set the requestCode is shown in uppercase, otherwise lowercase.
     */
    @Override
    public String toString()
    {
        StringBuilder  bfr= new StringBuilder ();
        bfr.append( "RequestCodeFlags[ " );
        bfr.append( isGet() ? "GET" : "get" );
        bfr.append( " " );
        bfr.append( isPost() ? "POST" : "post" );
        bfr.append( " " );
        bfr.append( isPut() ? "PUT" : "put" );
        bfr.append( " " );
        bfr.append( isDelete() ? "DELETE" : "delete" );
        bfr.append( " " );
        bfr.append( isFetch() ? "FETCH" : "fetch" );
        bfr.append( " " );
        bfr.append( isPatch() ? "PATCH" : "patch" );
        bfr.append( " " );
        bfr.append( isIpatch() ? "IPATCH" : "ipatch" );
        bfr.append( " ]" );
        return bfr.toString();
    }
}
