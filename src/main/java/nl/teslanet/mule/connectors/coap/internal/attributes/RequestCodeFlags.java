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
package nl.teslanet.mule.connectors.coap.internal.attributes;

//TODO unit test
/**
 * The RequestCodeFlags mantains a set of flags representing the CoAP request
 * codes.
 */
public class RequestCodeFlags implements Comparable< RequestCodeFlags >
{
    private final int GETFLAG= 0x1;

    private final int POSTFLAG= 0x2;

    private final int PUTFLAG= 0x4;

    private final int DELETEFLAG= 0x8;

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
        setGet( original.isGet() );
        setPost( original.isPost() );
        setPut( original.isPut() );
        setDelete( original.isDelete() );
    }

    /**
     * Constructor setting the flags.
     * @param getFlag the state of the get flag to set
     * @param postFlag the state of the post flag to set
     * @param putFlag the state of the get put to set
     * @param deleteFlag the state of the delete flag to set
     */
    public RequestCodeFlags( boolean getFlag, boolean postFlag, boolean putFlag, boolean deleteFlag )
    {
        super();
        setGet( getFlag );
        setPost( postFlag );
        setPut( putFlag );
        setDelete( deleteFlag );
    }

    /**
     * Set the Get flag
     * @param flag the state of the flag to set
     */
    public void setGet( boolean flag )
    {
        if ( flag )
        {
            flags|= GETFLAG;
        }
        else
        {
            flags&= ~GETFLAG;
        }
    }

    /**
     * @return the state of the get flag
     */
    public boolean isGet()
    {
        return ( flags & GETFLAG ) > 0;
    }

    /**
     * Set the post flag.
     * @param flag the state of the flag
     */
    public void setPost( boolean flag )
    {
        if ( flag )
        {
            flags|= POSTFLAG;
        }
        else
        {
            flags&= ~POSTFLAG;
        }
    }

    /**
     * @return the state of the post flag
     */
    public boolean isPost()
    {
        return ( flags & POSTFLAG ) != 0;
    }

    /**
     * Set the put flag.
     * @param flag the state of the flag
     */
    public void setPut( boolean flag )
    {
        if ( flag )
        {
            flags|= PUTFLAG;
        }
        else
        {
            flags&= ~PUTFLAG;
        }
    }

    /**
     * @return the state of the put flag
     */
    public boolean isPut()
    {
        return ( flags & PUTFLAG ) != 0;
    }

    /**
     * Set the delete flag.
     * @param flag the state of the flag
     */
    public void setDelete( boolean flag )
    {
        if ( flag )
        {
            flags|= DELETEFLAG;
        }
        else
        {
            flags&= ~DELETEFLAG;
        }
    }

    /**
     * @return the state of the delete flag
     */
    public boolean isDelete()
    {
        return ( flags & DELETEFLAG ) != 0;
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

    @Override
    public int hashCode()
    {
        return flags;
    }
}
