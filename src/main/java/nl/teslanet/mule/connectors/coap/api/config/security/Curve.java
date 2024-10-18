/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2023 - 2024 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.api.config.security;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Curve config. 
 */
public class Curve
{
    /**
     * The curve (supported group).
     */
    @Parameter
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The curve (supported group)." )
    private SupportedGroupName curveName= null;

    /**
     * Default constructor.
     */
    public Curve()
    {
        super();
        this.curveName= null;
    }

    /**
     * Constructor.
     * @param curveName The curve to identify by this object.
     */
    public Curve( SupportedGroupName curveName )
    {
        super();
        this.curveName= curveName;
    }

    /**
     * @return the curveName.
     */
    public SupportedGroupName getCurveName()
    {
        return curveName;
    }

    /**
     * @param curveName the curveName to set
     */
    public void setCurveName( SupportedGroupName curveName )
    {
        this.curveName= curveName;
    }

    /**
     * Returns a hash code value for the object.
     */
    @Override
    public int hashCode()
    {
        if ( this.curveName == null )
        {
            return 0;
        }
        else
        {
            return this.curveName.hashCode();
        }
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     */
    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( !( obj instanceof Curve ) )
        {
            return false;
        }
        return this.curveName.equals( ( (Curve) obj ).curveName );
    }

    /**
     * String value of this enum.
     */
    @Override
    public String toString()
    {
        return curveName == null ? String.valueOf( curveName ) : curveName.name();
    }
}
