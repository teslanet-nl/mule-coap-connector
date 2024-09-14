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
package nl.teslanet.mule.connectors.coap.api.config.options;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Configuration of an other option to accept.
 *
 */
public class AcceptOtherOption
{
    /**
     * The alias of the other option to accept.
     * This other option must be configured in a {@code coap:config} element.
     */
    @Parameter
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @Summary(
        "The alias of the other option to accept.\nThis other option must be configured in a coap:config element."
    )
    @Example( "my_option_alias" )
    private String alias= null;

    /**
     * Default constructor
     */
    public AcceptOtherOption()
    {
        //NOOP
    }

    /**
     * Constructor with alias.
     * @param alias The alias of the other option to accept.
     */
    public AcceptOtherOption( String alias )
    {
        this.alias= alias;
    }

    /**
     * @return The alias.
     */
    public String getAlias()
    {
        return alias;
    }

    /**
     * @param alias The alias to set.
     */
    public void setAlias( String alias )
    {
        this.alias= alias;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj )
    {
        if ( obj == null )
        {
            return false;
        }
        if ( obj == this )
        {
            return true;
        }
        if ( obj.getClass() != getClass() )
        {
            return false;
        }
        AcceptOtherOption rhs= (AcceptOtherOption) obj;
        return new EqualsBuilder().append( alias, rhs.alias ).isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 25, 45 ).append( alias ).toHashCode();
    }
}
