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
package nl.teslanet.mule.connectors.coap.api.config.options;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.config.ConfigException;
import nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor;
import nl.teslanet.mule.connectors.coap.api.config.VisitableConfig;


/**
 * Configuration option parameters.
 *
 */
public class OptionParams implements VisitableConfig
{
    //TODO non critical options need to be understood as well. Not suitable for proxy.. 

    /**
    * The aliases of other options that the endpoint understands. 
    * Messages containing critical options that are not understood will be refused.
    * Elective options that are not understood will be ignored.
    */
    @Parameter
    @Summary( value= "The aliases of other options the endpoint understands." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowInlineDefinition= true, allowReferences= false )
    private List< AcceptOtherOption > acceptOtherOptions= new CopyOnWriteArrayList<>();

    /**
     * @return the expected other options 
     */
    public List< AcceptOtherOption > getAcceptOtherOptions()
    {
        return acceptOtherOptions;
    }

    /**
     * @param acceptOtherOptions the expected other options to set
     */
    public void setAcceptOtherOptions( List< AcceptOtherOption > acceptOtherOptions )
    {
        this.acceptOtherOptions= acceptOtherOptions;
    }

    /**
     * Accept visitor.
     */
    @Override
    public void accept( ConfigVisitor visitor ) throws ConfigException
    {
        visitor.visit( this );
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
        OptionParams rhs= (OptionParams) obj;
        EqualsBuilder builder= new EqualsBuilder();
        acceptOtherOptions.forEach( other -> builder.append( other, rhs.acceptOtherOptions ) );
        return builder.isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        HashCodeBuilder builder= new HashCodeBuilder( 9, 29 ).append( acceptOtherOptions );
        acceptOtherOptions.forEach( builder::append );
        return builder.toHashCode();
    }
}
