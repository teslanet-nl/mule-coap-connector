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
package nl.teslanet.mule.connectors.coap.internal.options;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.eclipse.californium.core.coap.OptionSet;

import nl.teslanet.mule.connectors.coap.api.entity.EntityTag;
import nl.teslanet.mule.connectors.coap.api.entity.EntityTagException;
import nl.teslanet.mule.connectors.coap.api.error.InvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.api.options.RequestOptionsAttributes;
import nl.teslanet.mule.connectors.coap.api.query.QueryParamAttribute;
import nl.teslanet.mule.connectors.coap.internal.attributes.AttributeUtils;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;


/**
 * The CoAP option parameters of a request.
 *
 */
public class DefaultRequestOptionsAttributes extends RequestOptionsAttributes
{
    /**
     * Constructor that uses options from given optionSet.
     * @param optionSet to copy from.
     * @throws InvalidOptionValueException when given option value could not be copied successfully.
     */
    public DefaultRequestOptionsAttributes( OptionSet optionSet ) throws InternalInvalidOptionValueException
    {
        super();
        String errorMsg= "cannot create attribute";

        if ( !optionSet.getIfMatch().isEmpty() )
        {
            try
            {
                List< EntityTag > tmpIfMatch= EntityTag.getList( optionSet.getIfMatch() );
                boolean emptyPresent= tmpIfMatch.removeIf( EntityTag::isEmpty );
                if ( emptyPresent )
                {
                    ifExists= true;
                }
                if ( !tmpIfMatch.isEmpty() )
                {
                    ifMatch= Collections.unmodifiableList( tmpIfMatch );
                }
            }
            catch ( EntityTagException e )
            {
                throw new InternalInvalidOptionValueException( "IfMatch", errorMsg, e );
            }
        }
        if ( optionSet.hasUriHost() )
        {
            uriHost= optionSet.getUriHost();
        }
        if ( !optionSet.getETags().isEmpty() )
        {
            try
            {
                etags= Collections.unmodifiableList( EntityTag.getList( optionSet.getETags() ) );
            }
            catch ( EntityTagException e )
            {
                throw new InternalInvalidOptionValueException( "ETags", errorMsg, e );
            }
        }
        ifNoneMatch= optionSet.hasIfNoneMatch();
        if ( optionSet.hasUriPort() )
        {
            uriPort= optionSet.getUriPort();
        }
        if ( !optionSet.getUriPath().isEmpty() )
        {
            uriPath= Collections.unmodifiableList( optionSet.getUriPath() );
            //attributes.setUriPath( optionSet.getUriPathString() );
        }
        if ( optionSet.hasContentFormat() )
        {
            contentFormat= Integer.valueOf( optionSet.getContentFormat() );
        }
        if ( !optionSet.getUriQuery().isEmpty() )
        {
            LinkedList< QueryParamAttribute > queryParams= new LinkedList<>();
            optionSet.getUriQuery().forEach( queryParamString -> AttributeUtils.addQueryParam( queryParams, queryParamString ) );
            uriQuery= Collections.unmodifiableList( queryParams );
        }
        if ( optionSet.hasAccept() )
        {
            accept= Integer.valueOf( optionSet.getAccept() );
        }
        if ( optionSet.hasProxyUri() )
        {
            proxyUri= optionSet.getProxyUri();
        }
        if ( optionSet.hasProxyScheme() )
        {
            proxyScheme= optionSet.getProxyScheme();
        }
        if ( optionSet.hasSize1() )
        {
            requestSize= optionSet.getSize1();
        }
        if ( optionSet.hasSize2() && optionSet.getSize2() == 0 )
        {
            provideResponseSize= true;
        }
        if ( optionSet.hasObserve() )
        {
            observe= optionSet.getObserve();
        }
        other= AttributeUtils.createOthers( optionSet.getOthers() );
    }

    /**
     * Get the string representation.
     */
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString( this, new ToStringStyle() );
    }
}
