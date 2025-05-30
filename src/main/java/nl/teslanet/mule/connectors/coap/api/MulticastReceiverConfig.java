/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2025 (teslanet.nl) Rogier Cobben
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


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Configuration of a Multicast receiver.
 */
@Alias( "multicast-receiver" )
public class MulticastReceiverConfig extends MulticastGroupConfig
{
    /**
     * The port the multicast receiver binds to.  
     */
    @Parameter
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The port the multicast receiver binds to." )
    public Integer bindToPort= null;

    /**
     * Default constructor.
     */
    public MulticastReceiverConfig()
    {
        super();
    }

    /**
     * Constructor
     * @param bindToPort The port the multicast receiver binds to.
     * @param group The group The multicast group that is joined.
     * @param networkInterface The network interface on which to join the group.
     */
    public MulticastReceiverConfig( int bindToPort, String group, String networkInterface )
    {
        super( group, networkInterface );
        this.bindToPort= bindToPort;
    }

    /**
     * Convert to String representation.
     */
    @Override
    public String toString()
    {
        return "MulticastReceiverConfig { " + bindToPort + "|" + group + ( networkInterface != null
            ? "|" + networkInterface + " }" : " }" );
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
        MulticastReceiverConfig rhs= (MulticastReceiverConfig) obj;
        return new EqualsBuilder().append( bindToPort, rhs.bindToPort ).appendSuper( super.equals( rhs ) ).isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 19, 35 ).append( bindToPort ).appendSuper( super.hashCode() ).toHashCode();
    }
}
