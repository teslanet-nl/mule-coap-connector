/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2024 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.internal.utils;


import java.util.Collection;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * The to string style for attributes.
 * Used for string building of attributes.
 */
public class AttributeToStringStyle extends ToStringStyle
{
    /**
     * Serial version.
     */
    private static final long serialVersionUID= 1L;

    /**
     * Constructor.
     */
    public AttributeToStringStyle()
    {
        super();
        this.setUseIdentityHashCode( false );
        this.setUseShortClassName( true );
        resetIndent();
    }

    /** Indenting of inner lines. */
    protected static final int INDENT_SIZE= 3;

    /** Current indenting. */
    private int spaces= INDENT_SIZE;

    @Override
    protected void appendDetail( final StringBuffer buffer, final String fieldName, final boolean[] array )
    {
        indent();
        super.appendDetail( buffer, fieldName, array );
        unIndent();
    }

    @Override
    protected void appendDetail( final StringBuffer buffer, final String fieldName, final byte[] array )
    {
        indent();
        super.appendDetail( buffer, fieldName, array );
        unIndent();
    }

    @Override
    protected void appendDetail( final StringBuffer buffer, final String fieldName, final char[] array )
    {
        indent();
        super.appendDetail( buffer, fieldName, array );
        unIndent();
    }

    @Override
    protected void appendDetail( final StringBuffer buffer, final String fieldName, final double[] array )
    {
        indent();
        super.appendDetail( buffer, fieldName, array );
        unIndent();
    }

    @Override
    protected void appendDetail( final StringBuffer buffer, final String fieldName, final float[] array )
    {
        indent();
        super.appendDetail( buffer, fieldName, array );
        unIndent();
    }

    @Override
    protected void appendDetail( final StringBuffer buffer, final String fieldName, final short[] array )
    {
        indent();
        super.appendDetail( buffer, fieldName, array );
        unIndent();
    }

    @Override
    protected void appendDetail( final StringBuffer buffer, final String fieldName, final int[] array )
    {
        indent();
        super.appendDetail( buffer, fieldName, array );
        unIndent();
    }

    @Override
    protected void appendDetail( final StringBuffer buffer, final String fieldName, final long[] array )
    {
        indent();
        super.appendDetail( buffer, fieldName, array );
        unIndent();
    }

    @Override
    public void appendDetail( final StringBuffer buffer, final String fieldName, final Object value )
    {
        if ( ClassUtils.isPrimitiveWrapper( value.getClass() ) || String.class.equals( value.getClass() ) )
        {
            super.appendDetail( buffer, fieldName, value );
        }
        else if ( value instanceof AppendableToString )
        {
            indent();
            ( (AppendableToString) value ).appendTo( this, buffer );
            appendEnd( buffer, value );
            unIndent();
        }
        else
        {
            indent();
            super.appendDetail( buffer, fieldName, value );
            unIndent();
        }
    }

    @Override
    protected void appendDetail( final StringBuffer buffer, final String fieldName, final Object[] array )
    {
        indent();
        super.appendDetail( buffer, fieldName, array );
        unIndent();
    }

    @Override
    protected void appendDetail( final StringBuffer buffer, final String fieldName, final Collection< ? > coll )
    {
        indent();
        super.appendDetail( buffer, fieldName, coll.toArray() );
        unIndent();
    }

    /**
     * Increment indentation.
     */
    private void indent()
    {
        spaces+= INDENT_SIZE;
        resetIndent();
    }

    /**
     * Decrement indentation.
     */
    private void unIndent()
    {
        spaces-= INDENT_SIZE;
        resetIndent();
    }

    /**
     * Resets the fields responsible for the line breaks and indenting.
     * Must be invoked after changing the {@link #spaces} value.
     */
    private void resetIndent()
    {
        setContentStart(
            System.lineSeparator() + spacer( spaces - INDENT_SIZE ) + "{" + System.lineSeparator() + spacer( spaces )
        );
        setArrayStart( "[" + System.lineSeparator() + spacer( spaces ) );
        setArraySeparator( "," + System.lineSeparator() + spacer( spaces ) );

        setContentEnd( System.lineSeparator() + spacer( spaces - INDENT_SIZE ) + "}" );
        setArrayEnd( System.lineSeparator() + spacer( spaces - INDENT_SIZE ) + "]" );
        setFieldSeparator( "," + System.lineSeparator() + spacer( spaces ) );
    }

    /**
     * Creates a StringBuilder responsible for the indenting.
     *
     * @param spaces how far to indent
     * @return a StringBuilder with {spaces} leading space characters.
     */
    private String spacer( final int spaces )
    {
        return StringUtils.repeat( ' ', spaces );
    }
}
