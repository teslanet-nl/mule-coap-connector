/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2025 (teslanet.nl) Rogier Cobben
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


import static org.mule.runtime.api.metadata.DataType.BYTE_ARRAY;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.NoResponseOption;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.option.EmptyOptionDefinition;
import org.eclipse.californium.core.coap.option.IntegerOptionDefinition;
import org.eclipse.californium.core.coap.option.OpaqueOptionDefinition;
import org.eclipse.californium.core.coap.option.OptionDefinition;
import org.eclipse.californium.core.coap.option.StringOptionDefinition;
import org.eclipse.californium.elements.util.Bytes;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.transformation.TransformationService;

import nl.teslanet.mule.connectors.coap.api.binary.BytesValue;
import nl.teslanet.mule.connectors.coap.api.options.EntityTag;
import nl.teslanet.mule.connectors.coap.api.options.OptionFormat;
import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;
import nl.teslanet.mule.connectors.coap.api.options.OtherOption;
import nl.teslanet.mule.connectors.coap.api.options.RequestOptionsParams;
import nl.teslanet.mule.connectors.coap.api.options.RequireResponse;
import nl.teslanet.mule.connectors.coap.api.options.ResponseOptionsParams;
import nl.teslanet.mule.connectors.coap.api.query.AbstractQueryParam;
import nl.teslanet.mule.connectors.coap.api.query.QueryParam;
import nl.teslanet.mule.connectors.coap.internal.GlobalConfig;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidByteArrayValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUnkownOptionException;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultBytesValue;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultEntityTag;


/**
 * Utilities for handling message content.
 *
 */
public class MessageUtils
{
    private static final String VALUE_INVALID= "value is not valid";

    /**
     * Do not create objects.
     */
    private MessageUtils()
    {
        //NOOP
    }

    /**
     * Copy options from {@link RequestOptionsParams} to {@link OptionSet}.
     * @param requestOptions to copy from
     * @param optionSet to copy to
     * @param transformationService Mule transformation service.
     * @throws InternalInvalidOptionValueException When given option value could not be copied
     */
    public static void copyOptions( RequestOptionsParams requestOptions, OptionSet optionSet, TransformationService transformationService )
        throws InternalInvalidOptionValueException
    {
        copyIfExists( requestOptions.isIfExists(), optionSet );
        copyIfMatch( requestOptions.getIfMatchOptions(), optionSet, transformationService );
        copyEntityTags( requestOptions.getEntityTagOptions(), optionSet, transformationService );
        copyIfNoneMatch( requestOptions.isIfNoneMatch(), optionSet );
        copyContentFormat( requestOptions.getContentFormat(), optionSet );
        copyAccept( requestOptions.getAccept(), optionSet );
        copyRequireResponseSize( requestOptions.isRequireResponseSize(), optionSet );
        copyRequireResponse( requestOptions.getRequireResponse(), optionSet );
        copyRequestSize( requestOptions.getRequestSize(), optionSet );
    }

    /**
     * Copy IfExists to {@link OptionSet}.
     * @param requestOptions to copy from
     * @param optionSet to copy to
     * @throws InternalInvalidOptionValueException When given option value could not be copied
     */
    private static void copyIfExists( boolean ifExists, OptionSet optionSet ) throws InternalInvalidOptionValueException
    {
        if ( ifExists )
        {
            try
            {
                optionSet.addIfMatch( OptionUtils.EMPTY_BYTES );
            }
            catch ( Exception e )
            {
                throw new InternalInvalidOptionValueException( "If-Exists", VALUE_INVALID, e );
            }
        }
    }

    /**
     * Copy IfMatch to {@link OptionSet}.
     * @param ifIfMatchOptions to copy from
     * @param optionSet to copy to
     * @param transformationService Mule transformation service.
     * @throws InternalInvalidOptionValueException When given option value could not be copied
     */
    private static void copyIfMatch( List< EntityTag > ifIfMatchOptions, OptionSet optionSet, TransformationService transformationService )
        throws InternalInvalidOptionValueException
    {
        if ( ifIfMatchOptions != null )
        {
            List< DefaultEntityTag > etags;
            try
            {
                etags= MessageUtils.toEtagList( ifIfMatchOptions, transformationService );
                for ( DefaultEntityTag etag : etags )
                {
                    optionSet.addIfMatch( etag.getValue() );
                }
            }
            catch ( Exception e )
            {
                throw new InternalInvalidOptionValueException( "If-Match", VALUE_INVALID, e );
            }
        }
    }

    /**
     * Copy EntityTags to {@link OptionSet}.
     * @param entityTagOptions to copy from
     * @param optionSet to copy to
     * @param transformationService Mule transformation service.
     * @throws InternalInvalidOptionValueException When given option value could not be copied
     */
    private static void copyEntityTags( List< EntityTag > entityTagOptions, OptionSet optionSet, TransformationService transformationService )
        throws InternalInvalidOptionValueException
    {
        if ( entityTagOptions != null )
        {
            List< DefaultEntityTag > etags;
            try
            {
                etags= MessageUtils.toEtagList( entityTagOptions, transformationService );
                for ( DefaultEntityTag etag : etags )
                {
                    optionSet.addETag( etag.getValue() );
                }
            }
            catch ( Exception e )
            {
                throw new InternalInvalidOptionValueException( "ETag", VALUE_INVALID, e );
            }
        }
    }

    /**
     * Copy IfNoneMatch to {@link OptionSet}.
     * @param requestOptions to copy from
     * @param optionSet to copy to
     * @throws InternalInvalidOptionValueException When given option value could not be copied
     */
    private static void copyIfNoneMatch( boolean ifNoneMatch, OptionSet optionSet ) throws InternalInvalidOptionValueException
    {
        if ( ifNoneMatch )
        {
            try
            {
                optionSet.setIfNoneMatch( true );
            }
            catch ( Exception e )
            {
                throw new InternalInvalidOptionValueException( "If-None-Match", VALUE_INVALID, e );
            }
        }
    }

    /**
     * Copy ContentFormat to {@link OptionSet}.
     * @param contentFormat to copy from
     * @param optionSet to copy to
     * @throws InternalInvalidOptionValueException When given option value could not be copied
     */
    private static void copyContentFormat( Integer contentFormat, OptionSet optionSet ) throws InternalInvalidOptionValueException
    {
        if ( contentFormat != null )
        {
            try
            {
                optionSet.setContentFormat( contentFormat );
            }
            catch ( Exception e )
            {
                throw new InternalInvalidOptionValueException( "Content-Format", VALUE_INVALID, e );
            }
        }
    }

    /**
     * Copy Accept to {@link OptionSet}.
     * @param contentFormat to copy from
     * @param optionSet to copy to
     * @throws InternalInvalidOptionValueException When given option value could not be copied
     */
    private static void copyAccept( Integer contentFormat, OptionSet optionSet ) throws InternalInvalidOptionValueException
    {
        if ( contentFormat != null )
        {
            try
            {
                optionSet.setAccept( contentFormat );
            }
            catch ( Exception e )
            {
                throw new InternalInvalidOptionValueException( "Accept", VALUE_INVALID, e );
            }
        }
    }

    /**
     * Copy requireResponseSize to {@link OptionSet}.
     * @param requireResponseSize to copy from
     * @param optionSet to copy to
     * @throws InternalInvalidOptionValueException When given option value could not be copied
     */
    private static void copyRequireResponseSize( boolean requireResponseSize, OptionSet optionSet ) throws InternalInvalidOptionValueException
    {
        if ( requireResponseSize )
        {
            try
            {
                optionSet.setSize2( 0 );
            }
            catch ( Exception e )
            {
                throw new InternalInvalidOptionValueException( "Require-Response-Size", VALUE_INVALID, e );
            }
        }
    }

    /**
     * Copy requireResponse to {@link OptionSet}.
     * @param requireResponse to copy from
     * @param optionSet to copy to
     * @throws InternalInvalidOptionValueException When given option value could not be copied
     */
    private static void copyRequireResponse( RequireResponse requireResponse, OptionSet optionSet ) throws InternalInvalidOptionValueException
    {
        if ( requireResponse != null )
        {
            int noResponse= 0;
            if ( !requireResponse.isSuccess() ) noResponse+= NoResponseOption.SUPPRESS_SUCCESS;
            if ( !requireResponse.isClientError() ) noResponse+= NoResponseOption.SUPPRESS_CLIENT_ERROR;
            if ( !requireResponse.isServerError() ) noResponse+= NoResponseOption.SUPPRESS_SERVER_ERROR;
            optionSet.setNoResponse( noResponse );
        }
    }

    /**
     * Copy IfExists to {@link OptionSet}.
     * @param requestSize to copy from
     * @param optionSet to copy to
     * @throws InternalInvalidOptionValueException When given option value could not be copied
     */
    private static void copyRequestSize( Integer requestSize, OptionSet optionSet ) throws InternalInvalidOptionValueException
    {
        if ( requestSize != null )
        {
            try
            {
                optionSet.setSize1( requestSize );
            }
            catch ( Exception e )
            {
                throw new InternalInvalidOptionValueException( "Request-Size", VALUE_INVALID, e );
            }
        }
    }

    /**
    * Copy other options from list of other options to {@link OptionSet}.
    * @param otherOptions to copy from.
    * @param optionSet to copy to.
    * @param transformationService Mules transformation service for unknown option value types.
    * @throws InternalInvalidOptionValueException When given option value is not valid.
    * @throws InternalUnkownOptionException When given option alias was not defined. 
    */
    public static void copyOptions( List< OtherOption > otherOptions, OptionSet optionSet, TransformationService transformationService ) throws InternalInvalidOptionValueException,
        InternalUnkownOptionException
    {
        for ( OtherOption otherOption : otherOptions )
        {
            addOption( optionSet, otherOption, transformationService );
        }
    }

    /**
     * Copy options from {@link ResponseOptionsParams} to {@link OptionSet}.
     * @param responseOptions The options to copy.
     * @param optionSet The set to copy to.
     * @param transformationService Mule transformation service.
     * @throws InternalInvalidOptionValueException When given option value is invalid. 
     */
    public static void copyOptions( ResponseOptionsParams responseOptions, OptionSet optionSet, TransformationService transformationService )
        throws InternalInvalidOptionValueException
    {
        copyEntityTagValue( responseOptions.getEntityTagValue(), optionSet, transformationService );
        copyContentFormat( responseOptions.getContentFormat(), optionSet );
        copyMaxAge( responseOptions.getMaxAge(), optionSet );
        copyLocationPath( responseOptions.getLocationPath(), optionSet );
        copyLocationQuery( responseOptions.getLocationQuery(), optionSet );
        copyResponseSize( responseOptions.getResponseSize(), optionSet );
        copyAcceptableRequestSize( responseOptions.getAcceptableRequestSize(), optionSet );
    }

    /**
     * Copy EntityTagValue to {@link OptionSet}.
     * @param entityTagValue The entityTagValue to copy.
     * @param optionSet The set to copy to.
     * @param transformationService Mule transformation service.
     * @throws InternalInvalidOptionValueException When given option value is invalid. 
     */
    public static void copyEntityTagValue( TypedValue< Object > entityTagValue, OptionSet optionSet, TransformationService transformationService )
        throws InternalInvalidOptionValueException
    {
        if ( entityTagValue != null )
        {
            Optional< DefaultEntityTag > optionalEtag;
            try
            {
                optionalEtag= toOptionalETag( entityTagValue, transformationService );
            }
            catch ( Exception e )
            {
                throw new InternalInvalidOptionValueException( "ETag", VALUE_INVALID, e );
            }
            optionalEtag.ifPresent( etag -> optionSet.addETag( etag.getValue() ) );
        }
    }

    /**
     * Copy MaxAge to {@link OptionSet}.
     * @param maxAge The options to copy.
     * @param optionSet The set to copy to.
     * @throws InternalInvalidOptionValueException When given option value is invalid. 
     */
    public static void copyMaxAge( Long maxAge, OptionSet optionSet ) throws InternalInvalidOptionValueException
    {
        if ( maxAge != null )
        {
            try
            {
                optionSet.setMaxAge( maxAge );
            }
            catch ( Exception e )
            {
                throw new InternalInvalidOptionValueException( "Max-Age", VALUE_INVALID, e );
            }
        }
    }

    /**
     * Copy LocationPath to {@link OptionSet}.
     * @param locationPath The options to copy.
     * @param optionSet The set to copy to.
     * @throws InternalInvalidOptionValueException When given option value is invalid. 
     */
    public static void copyLocationPath( String locationPath, OptionSet optionSet ) throws InternalInvalidOptionValueException
    {
        if ( locationPath != null )
        {
            try
            {
                optionSet.setLocationPath( locationPath );
            }
            catch ( Exception e )
            {
                throw new InternalInvalidOptionValueException( "Location-Path", VALUE_INVALID, e );
            }
        }
    }

    /**
     * Copy LocationQuery to {@link OptionSet}.
     * @param locationQuery The options to copy.
     * @param optionSet The set to copy to.
     * @throws InternalInvalidOptionValueException When given option value is invalid. 
     */
    public static void copyLocationQuery( List< QueryParam > locationQuery, OptionSet optionSet ) throws InternalInvalidOptionValueException
    {
        if ( locationQuery != null )
        {
            for ( QueryParam param : locationQuery )
            {
                try
                {
                    optionSet.addLocationQuery( param.toString() );
                }
                catch ( Exception e )
                {
                    throw new InternalInvalidOptionValueException( "Location-Query", VALUE_INVALID, e );
                }
            }
        }
    }

    /**
     * Copy ResponseSize to {@link OptionSet}.
     * @param responseSize The options to copy.
     * @param optionSet The set to copy to.
     * @throws InternalInvalidOptionValueException When given option value is invalid. 
     */
    public static void copyResponseSize( Integer responseSize, OptionSet optionSet ) throws InternalInvalidOptionValueException
    {
        if ( responseSize != null )
        {
            try
            {
                optionSet.setSize2( responseSize );
            }
            catch ( Exception e )
            {
                throw new InternalInvalidOptionValueException( "Response-Size", VALUE_INVALID, e );
            }
        }
    }

    /**
     * Copy AcceptableRequestSize to {@link OptionSet}.
     * @param acceptableRequestSize The options to copy.
     * @param optionSet The set to copy to.
     * @throws InternalInvalidOptionValueException When given option value is invalid. 
     */
    public static void copyAcceptableRequestSize( Integer acceptableRequestSize, OptionSet optionSet ) throws InternalInvalidOptionValueException
    {
        if ( acceptableRequestSize != null )
        {
            try
            {
                optionSet.setSize1( acceptableRequestSize );
            }
            catch ( Exception e )
            {
                throw new InternalInvalidOptionValueException( "Acceptable-Request-Size", VALUE_INVALID, e );
            }
        }
    }

    /**
     * Add other option to option set. 
     * @param optionSet The set to add to.
     * @param otherOption The other option to add.
     * @param transformationService Used when the value needs to be transformed.
     * @throws InternalInvalidOptionValueException When value is invalid.
     * @throws InternalUnkownOptionException When other option is not configured.
     */
    private static void addOption( OptionSet optionSet, OtherOption otherOption, TransformationService transformationService ) throws InternalInvalidOptionValueException,
        InternalUnkownOptionException
    {
        optionSet
            .addOption(
                toCfOtherOption(
                    otherOption,
                    GlobalConfig
                        .getOtherOptionDefinition( otherOption.getAlias() )
                        .orElseThrow( () -> new InternalUnkownOptionException( "Unknown other option: " + otherOption.getAlias() ) ),
                    transformationService
                )
            );
    }

    /**
     * Convert other option configuration to Cf option definition.
     * @param otherOptionConfig The option config to create definition from.
     * @return The created definition.
     */
    public static OptionDefinition toCfOptionDefinition( nl.teslanet.mule.connectors.coap.api.config.options.OtherOptionConfig otherOptionConfig )
    {
        OptionDefinition definition;
        switch ( otherOptionConfig.getFormat() )
        {
            case EMPTY:
                definition= new EmptyOptionDefinition( otherOptionConfig.getNumber(), otherOptionConfig.getAlias() );
                break;
            case INTEGER:
                definition= new IntegerOptionDefinition(
                    otherOptionConfig.getNumber(),
                    otherOptionConfig.getAlias(),
                    otherOptionConfig.isSingleValue(),
                    otherOptionConfig.getMinBytes(),
                    otherOptionConfig.getMaxBytes()
                );
                break;
            case STRING:
                definition= new StringOptionDefinition(
                    otherOptionConfig.getNumber(),
                    otherOptionConfig.getAlias(),
                    otherOptionConfig.isSingleValue(),
                    otherOptionConfig.getMinBytes(),
                    otherOptionConfig.getMaxBytes()
                );
                break;
            case OPAQUE:
            default:
                definition= new OpaqueOptionDefinition(
                    otherOptionConfig.getNumber(),
                    otherOptionConfig.getAlias(),
                    otherOptionConfig.isSingleValue(),
                    otherOptionConfig.getMinBytes(),
                    otherOptionConfig.getMaxBytes()
                );
                break;
        }
        return definition;
    }

    /**
     * Establish equality of two option definitions based on their properties.
     * @param left The first option definition.
     * @param right The second option definition.
     * @return {@code true} when the definition are equal otherwise {@code false}
     */
    //TODO cf need equals method.
    @SuppressWarnings( "deprecation" )
    public static boolean isEqual( OptionDefinition left, OptionDefinition right )
    {
        if ( left == right ) return true;
        if ( left == null || right == null ) return false;
        return( left.getName().equals( right.getName() ) && left.getFormat() == right.getFormat() && left.getNumber() == right.getNumber() && left.isSingleValue() == right
            .isSingleValue() && Arrays.equals( left.getValueLengths(), right.getValueLengths() ) );
    }

    /**
     * Convert to Cf other option.
     * @param otherOption The other option to convert.
     * @param definition The definition of the option.
     * @param transformationService The service use for value transformation.
     * @return The Cf option.
     * @throws InternalInvalidOptionValueException When the value could not be converted.
     */
    private static Option toCfOtherOption( OtherOption otherOption, OptionDefinition definition, TransformationService transformationService )
        throws InternalInvalidOptionValueException
    {
        Option option;
        try
        {
            option= new Option( definition, toBytes( otherOption.getValue(), transformationService ) );
        }
        catch ( Exception e )
        {
            throw new InternalInvalidOptionValueException( otherOption.getAlias(), VALUE_INVALID, e );
        }
        return option;
    }

    /**
     * Convert Cf format to option  format.
     * @param format The Cf option format.
     * @return The option format.
     */
    public static OptionFormat toOptionFormat( org.eclipse.californium.core.coap.OptionNumberRegistry.OptionFormat format )
    {
        switch ( format )
        {
            case EMPTY:
                return OptionFormat.EMPTY;
            case INTEGER:
                return OptionFormat.INTEGER;
            case STRING:
                return OptionFormat.STRING;
            case OPAQUE:
            case UNKNOWN:
            default:
                return OptionFormat.OPAQUE;
        }
    }

    /**
     * Convert an object to byte array.
     * @param toConvert The object to convert.
     * @return Converted object as bytes, or null when the input was null.
     * @throws InternalInvalidByteArrayValueException When object cannot be converted to bytes.
     */
    public static byte[] toBytes( Object toConvert, TransformationService transformationService )
    {
        Object object= TypedValue.unwrap( toConvert );

        if ( object == null )
        {
            return Bytes.EMPTY;
        }
        else if ( object instanceof byte[] )
        {
            return (byte[]) object;
        }
        else if ( object instanceof Byte[] )
        {
            return toPrimitives( (Byte[]) object );
        }
        else if ( object instanceof String )
        {
            return ( (String) object ).getBytes( CoAP.UTF8_CHARSET );
        }
        else if ( object instanceof BytesValue )
        {
            return ( (BytesValue) object ).getValue();
        }
        else if ( object instanceof Integer )
        {
            return OptionUtils.toBytes( (Integer) object );
        }
        else if ( object instanceof Long )
        {
            return OptionUtils.toBytes( (Long) object );
        }
        //do transform using Mule's transformers.
        TypedValue< Object > value= TypedValue.of( toConvert );
        return (byte[]) transformationService.transform( value.getValue(), value.getDataType(), BYTE_ARRAY );

    }

    /* TODO needed in future release
     * Convert a typed value to {@code InputStream}.
     * @param typedValueObject is the value to convert.
     * @return converted value as {@code InputStream}.
     * @throws IOException when the value is an outputhandler that cannot write.
     *
    public static InputStream toInputStream( TypedValue< Object > typedValueObject ) throws IOException
    {
        Object object= TypedValue.unwrap( payload );
    
        if ( object == null )
        {
            return null;
        }
        else if ( object instanceof String )
        {
            return new ByteArrayInputStream( ( (String) object ).getBytes( Defs.COAP_CHARSET ) );
        }
        if ( object instanceof CursorStreamProvider )
        {
            return ( (CursorStreamProvider) object ).openCursor();
        }
        else if ( object instanceof InputStream )
        {
            return (InputStream) object;
        }
        else if ( object instanceof byte[] )
        {
            return new ByteArrayInputStream( (byte[]) object );
        }
        else if ( object instanceof Byte[] )
        {
            return new ByteArrayInputStream( toPrimitives( (Byte[]) object ) );
        }
        else if ( object instanceof OutputHandler )
        {
            PipedOutputStream output= new PipedOutputStream();
            ( (OutputHandler) object ).write( null, output );
            return new PipedInputStream( output );
        }
        else //do transform using Mule's transformers.
        {
            return (InputStream) transformationService.transform( Message.builder().payload( typedValueObject ).build(), DataType.INPUT_STREAM ).getPayload().getValue();
        }
    }
    */

    /**
    * Convert a typed value to ETag.
    * @param etag The EntityTag to construct a DefaultEntityTag from.
    * @return The ETag object that has been constructed.
    * @throws OptionValueException When value cannot be converted to a valid ETag.
    */
    private static DefaultEntityTag toETag( EntityTag etag, TransformationService transformationService ) throws OptionValueException
    {
        return toETag( etag.getValue(), transformationService );
    }

    /**
    * Convert a typed value to ETag.
    * @param etagValue The value to construct an ETag from.
    * @return The ETag object that has been constructed.
    * @throws OptionValueException When value cannot be converted to a valid ETag.
    */
    private static DefaultEntityTag toETag( TypedValue< Object > etagValue, TransformationService transformationService ) throws OptionValueException
    {
        Object object= TypedValue.unwrap( etagValue );

        if ( object == null )
        {
            throw new OptionValueException( "Cannot construct etag value of object { null }" );
        }
        Optional< DefaultEntityTag > optionalEtag= convertETag( object );
        if ( optionalEtag.isPresent() )
        {
            return optionalEtag.get();
        }
        return transformETag( etagValue, transformationService );
    }

    /**
    * Convert a typed value to an optional ETag.
    * @param etagValue The value to construct an ETag from.
    * @return The Optional ETag object that may have been constructed.
    * @throws OptionValueException When value cannot be converted to a valid ETag.
    */
    private static Optional< DefaultEntityTag > toOptionalETag( TypedValue< Object > etagValue, TransformationService transformationService ) throws OptionValueException
    {
        Object object= TypedValue.unwrap( etagValue );

        if ( object == null )
        {
            return Optional.empty();
        }
        Optional< DefaultEntityTag > optionalEtag= convertETag( object );
        if ( optionalEtag.isPresent() )
        {
            return optionalEtag;
        }
        return Optional.of( transformETag( etagValue, transformationService ) );
    }

    /**
    * Convert an object to ETag when the value is of a known type.
    * @param object The value to construct an ETag from.
    * @return The Optional ETag object that has been converted.
    * @throws OptionValueException When value cannot be converted to a valid ETag.
    */
    private static Optional< DefaultEntityTag > convertETag( Object object ) throws OptionValueException
    {
        if ( object instanceof DefaultEntityTag )
        {
            return Optional.of( (DefaultEntityTag) object );
        }
        else if ( object instanceof DefaultBytesValue )
        {
            return Optional.of( new DefaultEntityTag( (DefaultBytesValue) object ) );
        }
        else if ( object instanceof BytesValue )
        {
            return Optional.of( new DefaultEntityTag( ( (BytesValue) object ).getValue() ) );
        }
        else if ( object instanceof Integer )
        {
            return Optional.of( new DefaultEntityTag( (Integer) object ) );
        }
        else if ( object instanceof Long )
        {
            return Optional.of( new DefaultEntityTag( (Long) object ) );
        }
        else if ( object instanceof String )
        {
            return Optional.of( new DefaultEntityTag( (String) object ) );
        }
        else if ( object instanceof byte[] )
        {
            return Optional.of( new DefaultEntityTag( (byte[]) object ) );
        }
        else if ( object instanceof Byte[] )
        {
            return Optional.of( new DefaultEntityTag( toPrimitives( (Byte[]) object ) ) );
        }
        else
        {
            return Optional.empty();
        }
    }

    /**
    * Transform a typed value to ETag.
    * @param etagValue The value to construct an ETag from.
    * @return The ETag object that has been constructed.
    * @throws OptionValueException When value cannot be converted to a valid ETag.
    */
    private static DefaultEntityTag transformETag( TypedValue< Object > etagValue, TransformationService transformationService ) throws OptionValueException
    {
        //transform using Mule's transformers.
        return new DefaultEntityTag( (byte[]) transformationService.transform( etagValue.getValue(), etagValue.getDataType(), BYTE_ARRAY ) );
    }

    /**
     * Create a list of ETags
     * @param typedValue The value to create the list of etags from.
     * @param transformationService Transformation service for not recognized value types.
     * @return The list of entity tags.
     * @throws OptionValueException When given values are invalid.
     */
    private static List< DefaultEntityTag > toEtagList( List< EntityTag > etagValues, TransformationService transformationService ) throws OptionValueException
    {
        LinkedList< DefaultEntityTag > list= new LinkedList<>();

        if ( etagValues == null )
        {
            //noop
        }
        else
        {
            for ( EntityTag etag : etagValues )
            {
                list.add( toETag( etag, transformationService ) );
            }
        }
        return Collections.unmodifiableList( list );
    }

    /**
     * Converts {@code Byte[]} to array of primitives.
     * @param bytes the array to convert.
     * @return the {@code byte[]} or  {@code null} when empty.
     */
    private static byte[] toPrimitives( Byte[] bytes )
    {
        if ( bytes == null || bytes.length == 0 )
        {
            return OptionUtils.EMPTY_BYTES;
        }
        final byte[] result= new byte [bytes.length];
        for ( int i= 0; i < bytes.length; i++ )
        {
            result[i]= bytes[i].byteValue();
        }
        return result;
    }

    /**
     * Construct string representation of query parameters, merged with default parameters.
     * The string conforms to URI format,
     * @param defaultQueryParams The default parameters.
     * @param queryParams The query parameters.
     * @return The string representation for usage in an URI.
     */
    public static String queryString( List< ? extends AbstractQueryParam > defaultQueryParams, List< ? extends AbstractQueryParam > queryParams )
    {
        if ( ( defaultQueryParams == null || defaultQueryParams.isEmpty() ) && ( queryParams == null || queryParams.isEmpty() ) ) return null;
        StringWriter writer= new StringWriter();
        boolean first= true;
        for ( AbstractQueryParam param : defaultQueryParams )
        {
            if ( first )
            {
                first= false;
            }
            else
            {
                writer.append( "&" );
            }
            writer.append( param.toString() );
        }
        for ( AbstractQueryParam param : queryParams )
        {
            if ( first )
            {
                first= false;
            }
            else
            {
                writer.append( "&" );
            }
            writer.append( param.toString() );
        }
        return writer.toString();
    }

    /**
     * Create UriString from path and query.
     * @param path The optional list of path segements.
     * @param query The optional list of query parameters.
     * @return The uri string.
     */
    public static String uriString( List< String > path, List< ? extends AbstractQueryParam > query )
    {
        if ( ( path == null || path.isEmpty() ) && ( query == null || query.isEmpty() ) ) return null;
        StringWriter writer= new StringWriter();
        boolean first= true;
        writer.append( "/" );
        if ( path != null )
        {
            for ( String pathsegment : path )
            {
                if ( first )
                {
                    first= false;
                }
                else
                {
                    writer.append( "/" );
                }
                writer.append( pathsegment );
            }
        }
        first= true;
        if ( query != null )
        {
            for ( AbstractQueryParam param : query )
            {
                if ( first )
                {
                    writer.append( "?" );
                    first= false;
                }
                else
                {
                    writer.append( "&" );
                }
                writer.append( param.toString() );
            }
        }
        return writer.toString();
    }

    /**
     * Convenience method to create a list of Entity-tags form a list of byte arrays.
     * To support ifMatch options no list is returned when one of the etags is empty,
     * in stead of throwing an exception.
     * @param bytesList The List of Byte arrays to make a list of Entity-tags from.
     * @return The optional list of Entity-tags. Empty when an empty etag occurs.
     * @throws OptionValueException when the Entity-tag could not be created from bytes
     */
    public static Optional< List< DefaultEntityTag > > getList( List< byte[] > bytesList ) throws OptionValueException
    {
        LinkedList< DefaultEntityTag > result= new LinkedList<>();
        for ( byte[] bytes : bytesList )
        {
            if ( bytes.length <= 0 ) return Optional.empty();
            result.add( new DefaultEntityTag( bytes ) );
        }
        return Optional.of( result );
    }

    /**
     * Check a collection of Entity-tags whether it contains the Entity-tag.
     * When given collection is null the Entity-tag is considered not found.
     * @param etag The Entity-tag to check.
     * @param etags The collection of Entity-tags to find the Entity-tag in.
     * @return True when the Entity-tag is found in the collection, otherwise false.
     */
    public static boolean isIn( DefaultEntityTag etag, Collection< DefaultEntityTag > etags )
    {
        if ( etags == null ) return false;
        return etags.contains( etag );
    }
}
