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
package nl.teslanet.mule.connectors.coap.internal.options;


import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.eclipse.californium.core.coap.OptionSet;

import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;
import nl.teslanet.mule.connectors.coap.api.options.ETag;
import nl.teslanet.mule.connectors.coap.api.options.OtherOptionAttribute;
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
    public DefaultRequestOptionsAttributes( OptionSet optionSet ) throws InternalInvalidOptionValueException
    {
        super();
        String errorMsg= "cannot create attribute";

        if ( !optionSet.getIfMatch().isEmpty() )
        {
            try
            {
                List< ETag > tmpIfMatch= ETag.getList( optionSet.getIfMatch() );
                boolean emptyPresent= tmpIfMatch.removeIf( etag -> etag.isEmpty() );
                if ( emptyPresent )
                {
                    ifExists= true;
                }
                if ( !tmpIfMatch.isEmpty() )
                {
                    ifMatch= Collections.unmodifiableList( tmpIfMatch );
                }
            }
            catch ( InvalidETagException e )
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
                etags= Collections.unmodifiableList( ETag.getList( optionSet.getETags() ) );
            }
            catch ( InvalidETagException e )
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
            optionSet.getUriQuery().forEach( queryParamString -> {
                AttributeUtils.addQueryParam( queryParams, queryParamString );
            } );
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
            size1= optionSet.getSize1();
        }
        if ( optionSet.hasSize2() && optionSet.getSize2() == 0 )
        {
            requestSize2= true;
        }
        if ( optionSet.hasObserve() )
        {
            observe= optionSet.getObserve();
        }
        ArrayList< OtherOptionAttribute > tmpOther= new ArrayList<>();
        optionSet.getOthers().forEach( option -> {
            tmpOther.add( new DefaultOtherOptionAttribute( option.getNumber(), option.getValue() ) );
        } );
        otherOptions= Collections.unmodifiableList( tmpOther );
    }

    /**
     * Get the string representation.
     */
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString( this, MULTI_LINE_STYLE );
    }
}
