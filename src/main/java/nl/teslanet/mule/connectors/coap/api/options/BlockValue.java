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
package nl.teslanet.mule.connectors.coap.api.options;


/**
 * Implementation of a block option value for convenience.
 * It eases the handling of CoAP block option values in Mule flows. 
 * A BlockValue object is immutable and comparable to other BlockValue objects.
 */
public final class BlockValue implements Comparable< BlockValue >
{
    /**
     * The block number.
     */
    private final int num;

    /**
     * The block szx indicating block size. 
     * The block size is {@code 1 << (4 + szx) }. 
     * Valid values are 0..7.
     */
    private final int szx;

    /**
     * The flag indicating that more blocks follow ( value {@code true} ), it is the last (value = {@code false} ).
     */
    private final boolean m;

    /**
     * The hashCode is lazily initialized.
     */
    private Integer hashCode= null;

    /**
     * The constructor.
     * @param num is the number of the block
     * @param szx is the block szx indicating block size. The block size is {@code 1 << (4 + szx) }. Valid values are 0..7.
     * @param m is the flag indicating that more blocks will follow (when {@code true} 
     */
    public BlockValue( int num, int szx, boolean m )
    {
        this.num= num;
        this.szx= szx;
        this.m= m;
    }

    /**
     * Gets the etag value as string containing the hexadecimal representation.
     * Hexadecimal values a-f will be lower case.
     * @return The string containing the hexadecimal representation.
     */
    @Override
    public String toString()
    {
        return String.format( "block { num=%d, szx=%d/%d, m=%b }", num, szx, getSize(), m );
    }

    /**
     * Static method to create a BlockValue object.
     * @param num is the number of the block
     * @param szx is the block szx indicating block size. The block size is {@code 1 << (4 + szx) }. Valid values are 0..7.
     * @param m is the flag indicating that more blocks will follow (when {@code true} 
     * @return the BlockValue object.
     */
    public static BlockValue create( int num, int szx, boolean m )
    {
        return new BlockValue( num, szx, m );
    }

    /**
     * Gets the offset of the block in the total resource content.
     * @return The offset that is defined by {@code num * size }.
     */
    public int getOffset()
    {
        return num * getSize();
    }

    /**
     * @return the num
     */
    public int getNum()
    {
        return num;
    }

    /**
     * @return the szx
     */
    public int getSzx()
    {
        return szx;
    }

    /**
     * Gets the block size. Size is defined as {@code size == 1 << (4 + szx)}.
     *
     * @return the size
     */
    public int getSize()
    {
        return 1 << ( 4 + szx );
    }

    /**
    * @return the m
    */
    public boolean isM()
    {
        return m;
    }

    /**
     * Check etag on equality to another etag. 
     * Etags are equal when there byte arrays contain the same sequence of bytes. 
     * @param o The etag object to test for equality
     * @return True 
     */
    @Override
    public boolean equals( Object o )
    {
        if ( !( o instanceof BlockValue ) )
        {
            return false;
        }
        BlockValue other= (BlockValue) o;
        return( num == other.num && szx == other.szx && m == other.m );
    }

    /**
     * Compare to other blockvalue object.
     */
    @Override
    public int compareTo( BlockValue other )
    {
        if ( this == other ) return 0;
        if ( null == other ) return 1;
        int compared= Integer.compare( num, other.num );
        if ( compared == 0 )
        {
            compared= Integer.compare( szx, other.szx );
            if ( compared == 0 )
            {
                compared= Boolean.compare( m, other.m );
            }
        }
        return compared;
    }

    /**
     * Return the hash code of this object.
     */
    @Override
    public int hashCode()
    {
        if ( this.hashCode == null )
        {
            int result= szx;
            result= 31 * result + ( m ? 0 : 1 );
            result= 31 * result + num;
            this.hashCode= Integer.valueOf( result );
        }
        return this.hashCode;
    }
}
