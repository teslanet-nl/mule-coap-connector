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
package nl.teslanet.mule.connectors.coap.internal.attributes;


import java.util.Collections;
import java.util.LinkedList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.californium.core.coap.OptionSet;

import nl.teslanet.mule.connectors.coap.api.error.InvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;
import nl.teslanet.mule.connectors.coap.api.options.ResponseOptionsAttributes;
import nl.teslanet.mule.connectors.coap.api.query.QueryParamAttribute;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultEntityTag;
import nl.teslanet.mule.connectors.coap.internal.utils.AppendableToString;
import nl.teslanet.mule.connectors.coap.internal.utils.AttributeUtils;
import nl.teslanet.mule.connectors.coap.internal.utils.AttributesStringBuilder;


/**
 * The option parameters of a CoAP response.
 *
 */
public class CoapResponseOptionsAttributesImpl extends ResponseOptionsAttributes implements AppendableToString
{
    /**
     * Constructor that uses options from given optionSet.
     * @param optionSet to copy from.
     * @throws InvalidOptionValueException when given option value could not be copied successfully.
     */
    public CoapResponseOptionsAttributesImpl( OptionSet optionSet ) throws InternalInvalidOptionValueException
    {
        super();
        String errorMsg= "cannot create attribute";

        if ( !optionSet.getETags().isEmpty() )
        {
            try
            {
                etag= new DefaultEntityTag( optionSet.getETags().get( 0 ) );
            }
            catch ( OptionValueException e )
            {
                throw new InternalInvalidOptionValueException( "ETags", errorMsg, e );
            }
        }
        if ( !optionSet.getLocationPath().isEmpty() )
        {
            locationPath= Collections.unmodifiableList( optionSet.getLocationPath() );
        }
        if ( optionSet.hasContentFormat() )
        {
            contentFormat= Integer.valueOf( optionSet.getContentFormat() );
        }
        if ( optionSet.hasMaxAge() )
        {
            maxAge= optionSet.getMaxAge();
        }
        if ( !optionSet.getLocationQuery().isEmpty() )
        {
            LinkedList< QueryParamAttribute > queryParams= new LinkedList<>();
            optionSet
                .getLocationQuery()
                .forEach( queryParamString -> AttributeUtils.addQueryParam( queryParams, queryParamString ) );
            locationQuery= Collections.unmodifiableList( queryParams );
        }
        if ( optionSet.hasSize1() )
        {
            acceptableRequestSize= optionSet.getSize1();
        }
        if ( optionSet.hasSize2() )
        {
            responseSize= optionSet.getSize2();
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
        AttributesStringBuilder builder= new AttributesStringBuilder( this );
        this.appendTo( builder );
        return builder.toString();
    }

    /**
     * Append this to string builder.
     * @param style The styla to apply.
     * @param buffer The string buffer to use.
     */
    @Override
    public void appendTo( ToStringStyle style, StringBuffer buffer )
    {
        AttributesStringBuilder builder= new AttributesStringBuilder( this, style, buffer );
        this.appendTo( builder );
    }

    /**
     * Append this to string builder.
     * @param builder The string builder to append to.
     */
    public void appendTo( ToStringBuilder builder )
    {
        builder
            .append( "acceptableRequestSize", acceptableRequestSize )
            .append( "contentFormat", contentFormat )
            .append( "etag", etag )
            .append( "locationPath", locationPath )
            .append( "locationQuery", locationQuery )
            .append( "maxAge", maxAge )
            .append( "observe", observe )
            .append( "other", other )
            .append( "responseSize", responseSize );
    }
}
