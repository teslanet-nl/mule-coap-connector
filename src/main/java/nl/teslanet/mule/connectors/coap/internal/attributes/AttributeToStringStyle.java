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
package nl.teslanet.mule.connectors.coap.internal.attributes;


import org.apache.commons.lang3.builder.MultilineRecursiveToStringStyle;

import nl.teslanet.mule.connectors.coap.internal.options.DefaultEntityTag;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultOtherOptionAttribute;


/**
 * The to string style for attributes.
 *
 */
public class AttributeToStringStyle extends MultilineRecursiveToStringStyle
{
    /**
     * Serial version.
     */
    private static final long serialVersionUID= 1L;

    /**
     * Style instance.
     */
    private static final AttributeToStringStyle instance= new AttributeToStringStyle();

    /**
     * Constructor.
     */
    public AttributeToStringStyle()
    {
        this.setUseClassName( false );
        this.setUseIdentityHashCode( false );
    }

    /**
     * @return the instance
     */
    public static AttributeToStringStyle getInstance()
    {
        return instance;
    }

    /**
     * Do not accept classes that will use {@code toString()}
     */
    @Override
    protected boolean accept( final Class< ? > clazz )
    {
        return clazz != DefaultEntityTag.class && clazz != DefaultOtherOptionAttribute.class;
    }
}
