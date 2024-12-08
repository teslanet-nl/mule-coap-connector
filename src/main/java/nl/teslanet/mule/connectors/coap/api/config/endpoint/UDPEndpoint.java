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
package nl.teslanet.mule.connectors.coap.api.config.endpoint;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.config.ConfigException;
import nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor;
import nl.teslanet.mule.connectors.coap.api.config.UdpParams;


/**
 * UDP CoAP endpoint configuration
 *
 */
@TypeDsl( allowInlineDefinition= true, allowTopLevelDefinition= true )
public class UDPEndpoint extends AbstractEndpoint
{
    /**
     * UDP endpoint parameters.
     */
    @Parameter
    @Optional
    @NullSafe
    @Summary( value= "UDP parameters" )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public UdpParams udpParams= null;

    /**
     * When enabled peer response address is checked.
     */
    @Parameter
    @Optional( defaultValue= "true" )
    @Summary( value= "When enabled peer response address is checked." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public boolean strictResponseMatching= true;

    /**
     * Default Constructor used by Mule. 
     * Mandatory and Nullsafe params are set by Mule.
     */
    public UDPEndpoint()
    {
        super();
    }

    /**
     * Constructor for manually constructing the endpoint.
     * (Mule uses default constructor and sets Nullsafe params.)
     * @param name The manually set name of the endpoint
     */
    public UDPEndpoint( String name )
    {
        super( name );
        udpParams= new UdpParams();
    }

    /**
     * Constructor for manually constructing the endpoint.
     * (Mule uses default constructor and sets Nullsafe params.)
     * @param name The manually set name of the endpoint
     * @param port The manually set port to bind to.
     */
    public UDPEndpoint( String name, int port )
    {
        super( name, port );
        udpParams= new UdpParams();
    }

    /**
     * Accept a visitor and pass on.
     * @throws ConfigException 
     */
    @Override
    public void accept( ConfigVisitor visitor ) throws ConfigException
    {
        super.accept( visitor );
        udpParams.accept( visitor );
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
        UDPEndpoint rhs= (UDPEndpoint) obj;
        return new EqualsBuilder()
            .appendSuper( super.equals( obj ) )
            .append( strictResponseMatching, rhs.strictResponseMatching )
            .append( udpParams, rhs.udpParams )
            .isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 13, 33 )
            .appendSuper( super.hashCode() )
            .append( strictResponseMatching )
            .append( udpParams )
            .toHashCode();
    }
}
