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
package nl.teslanet.mule.connectors.coap.internal.utils;


import static org.mule.runtime.api.metadata.DataType.BYTE_ARRAY;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.eclipse.californium.core.coap.BlockOption;
import org.eclipse.californium.core.coap.CoAP;
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

import nl.teslanet.mule.connectors.coap.api.entity.EntityTag;
import nl.teslanet.mule.connectors.coap.api.entity.EntityTagAttribute;
import nl.teslanet.mule.connectors.coap.api.entity.EntityTagException;
import nl.teslanet.mule.connectors.coap.api.error.InvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.api.options.BlockValue;
import nl.teslanet.mule.connectors.coap.api.options.OptionFormat;
import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;
import nl.teslanet.mule.connectors.coap.api.options.OtherOption;
import nl.teslanet.mule.connectors.coap.api.options.RequestOptions;
import nl.teslanet.mule.connectors.coap.api.options.ResponseOptions;
import nl.teslanet.mule.connectors.coap.api.query.AbstractQueryParam;
import nl.teslanet.mule.connectors.coap.internal.GlobalConfig;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidByteArrayValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalUnkownOptionException;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultEntityTag;


/**
 * Utilities for handling message content.
 *
 */
public class MessageUtils
{
    /**
     * Do not create objects.
     */
    private MessageUtils()
    {
        //NOOP
    }

    /**
     * Copy options from {@link RequestOptions} to {@link OptionSet}.
     * @param requestOptions to copy from
     * @param optionSet to copy to
     * @param transformationService Mule transformation service.
     * @throws InvalidOptionValueException When given option value could not be copied
     */
    public static void copyOptions(
        RequestOptions requestOptions,
        OptionSet optionSet,
        TransformationService transformationService
    ) throws InternalInvalidOptionValueException
    {
        if ( requestOptions.isIfExists() )
        {
            optionSet.addIfMatch( new byte [0] );
        }
        if ( requestOptions.getIfMatchOptions() != null )
        {
            List< DefaultEntityTag > etags;
            try
            {
                etags= MessageUtils.toEtagList( requestOptions.getIfMatchOptions(), transformationService );
                for ( DefaultEntityTag etag : etags )
                {
                    optionSet.addIfMatch( etag.getValue() );
                }
            }
            catch ( EntityTagException e )
            {
                throw new InternalInvalidOptionValueException( "If-Match", e.getMessage(), e );
            }
        }
        if ( requestOptions.getEntityTagOptions() != null )
        {
            List< DefaultEntityTag > etags;
            try
            {
                etags= MessageUtils.toEtagList( requestOptions.getEntityTagOptions(), transformationService );
                for ( DefaultEntityTag etag : etags )
                {
                    optionSet.addETag( etag.getValue() );
                }
            }
            catch ( EntityTagException e )
            {
                throw new InternalInvalidOptionValueException( "ETag", e.getMessage(), e );
            }
        }
        if ( requestOptions.isIfNoneMatch() )
        {
            optionSet.setIfNoneMatch( true );
        }
        if ( requestOptions.getContentFormat() != null )
        {
            optionSet.setContentFormat( requestOptions.getContentFormat() );
        }
        if ( requestOptions.getAccept() != null )
        {
            optionSet.setAccept( requestOptions.getAccept() );
        }
        if ( requestOptions.isRequireResponseSize() )
        {
            optionSet.setSize2( 0 );
        }
        if ( requestOptions.getRequestSize() != null )
        {
            optionSet.setSize1( requestOptions.getRequestSize() );
        }
    }

    /**
    * Copy other options from {@link RequestOptions} to {@link OptionSet}.
    * @param requestOptions to copy from.
    * @param optionSet to copy to.
     * @param transformationService 
    * @throws InternalInvalidOptionValueException When given option value is not valid.
    * @throws InternalUnkownOptionException When given option alias was not defined. 
    */
    public static void copyOtherOptions(
        RequestOptions requestOptions,
        OptionSet optionSet,
        TransformationService transformationService
    ) throws InternalInvalidOptionValueException,
        InternalUnkownOptionException
    {
        for ( OtherOption otherOption : requestOptions.getOtherOptions() )
        {
            addOption( optionSet, otherOption, transformationService );
        }
    }

    /**
     * Copy options from {@link ResponseOptions} to {@link OptionSet}.
     * @param responseOptions The options to copy.
     * @param optionSet The set to copy to.
     * @param transformationService Mule transformation service.
     * @throws InternalInvalidOptionValueException When given option value is invalid. 
     */
    public static void copyOptions(
        ResponseOptions responseOptions,
        OptionSet optionSet,
        TransformationService transformationService
    ) throws InternalInvalidOptionValueException
    {
        if ( responseOptions.getEntityTagValue() != null )
        {
            DefaultEntityTag etag;
            try
            {
                etag= MessageUtils.toETag( responseOptions.getEntityTagValue(), transformationService );
            }
            catch ( EntityTagException e )
            {
                throw new InternalInvalidOptionValueException( "ETag", "Entity tag value is not valid" );
            }
            optionSet.addETag( etag.getValue() );
        }
        if ( responseOptions.getContentFormat() != null )
        {
            optionSet.setContentFormat( responseOptions.getContentFormat() );
        }
        if ( responseOptions.getMaxAge() != null )
        {
            optionSet.setMaxAge( responseOptions.getMaxAge() );
        }
        if ( responseOptions.getLocationPath() != null )
        {
            optionSet.setLocationPath( responseOptions.getLocationPath() );
        }
        if ( responseOptions.getLocationQuery() != null )
        {
            for ( AbstractQueryParam param : responseOptions.getLocationQuery() )
            {
                optionSet.addLocationQuery( param.toString() );
            }
        }
        if ( responseOptions.getResponseSize() != null )
        {
            optionSet.setSize2( responseOptions.getResponseSize() );
        }
        if ( responseOptions.getAcceptableRequestSize() != null )
        {
            optionSet.setSize1( responseOptions.getAcceptableRequestSize() );
        }
    }

    /**
    * Copy other options from {@link RequestOptions} to {@link OptionSet}.
    * @param responseOptions to copy from.
    * @param optionSet to copy to.
    * @param transformationService 
    * @throws InternalInvalidOptionValueException When given option value is not valid.
    * @throws InternalUnkownOptionException When given option alias was not defined. 
    */
    public static void copyOtherOptions(
        ResponseOptions responseOptions,
        OptionSet optionSet,
        TransformationService transformationService
    ) throws InternalInvalidOptionValueException,
        InternalUnkownOptionException
    {
        for ( OtherOption otherOption : responseOptions.getOtherOptions() )
        {
            addOption( optionSet, otherOption, transformationService );
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
    private static void addOption(
        OptionSet optionSet,
        OtherOption otherOption,
        TransformationService transformationService
    ) throws InternalInvalidOptionValueException,
        InternalUnkownOptionException
    {
        optionSet
            .addOption(
                toCfOtherOption(
                    otherOption,
                    GlobalConfig
                        .getOtherOptionDefinition( otherOption.getAlias() )
                        .orElseThrow(
                            () -> new InternalUnkownOptionException( "Unknown other option: " + otherOption.getAlias() )
                        ),
                    transformationService
                )
            );
    }

    /**
     * Convert other option configuration to Cf option definition.
     * @param otherOptionConfig The option config to create definition from.
     * @return The created definition.
     */
    public static OptionDefinition toCfOptionDefinition(
        nl.teslanet.mule.connectors.coap.api.config.options.OtherOptionConfig otherOptionConfig
    )
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
        return( left.getName().equals( right.getName() ) && left.getFormat() == right.getFormat()
            && left.getNumber() == right.getNumber() && left.isSingleValue() == right.isSingleValue()
            && Arrays.equals( left.getValueLengths(), right.getValueLengths() ) );
    }

    /**
     * Convert to Cf other option.
     * @param otherOption The other option to convert.
     * @param definition The definition of the option.
     * @param transformationService The service use for value transformation.
     * @return The Cf option.
     * @throws InternalInvalidOptionValueException When the value could not be converted.
     */
    private static Option toCfOtherOption(
        OtherOption otherOption,
        OptionDefinition definition,
        TransformationService transformationService
    ) throws InternalInvalidOptionValueException
    {
        Option option;
        try
        {
            option= new Option( definition, toBytes( otherOption.getValue(), transformationService ) );
        }
        catch ( Exception e )
        {
            throw new InternalInvalidOptionValueException( otherOption.getAlias(), e.getMessage(), e );
        }
        return option;
    }

    /**
     * Convert Cf format to option  format.
     * @param format The Cf option format.
     * @return The option format.
     */
    public static OptionFormat toOptionFormat(
        org.eclipse.californium.core.coap.OptionNumberRegistry.OptionFormat format
    )
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
        else if ( object instanceof EntityTagAttribute )
        {
            return ( (EntityTagAttribute) object ).getValue();
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
    * @throws EntityTagException When value cannot be converted to a valid ETag.
    */
    private static DefaultEntityTag toETag( EntityTag etag, TransformationService transformationService )
        throws EntityTagException
    {
        return toETag( etag.getValue(), transformationService );
    }

    /**
    * Convert a typed value to ETag.
    * @param etagValue The value to construct an ETag from.
    * @return The ETag object that has been constructed.
    * @throws EntityTagException When value cannot be converted to a valid ETag.
    */
    private static DefaultEntityTag toETag(
        TypedValue< Object > etagValue,
        TransformationService transformationService
    ) throws EntityTagException
    {
        Object object= TypedValue.unwrap( etagValue );

        if ( object == null )
        {
            throw new EntityTagException( "Cannot construct etag value of object { null }" );
        }
        else if ( object instanceof DefaultEntityTag )
        {
            return (DefaultEntityTag) object;
        }
        else if ( object instanceof Integer )
        {
            return new DefaultEntityTag( (Integer) object );
        }
        else if ( object instanceof Long )
        {
            return new DefaultEntityTag( (Long) object );
        }
        else if ( object instanceof String )
        {
            return new DefaultEntityTag( (String) object );
        }
        else if ( object instanceof byte[] )
        {
            return new DefaultEntityTag( (byte[]) object );
        }
        else if ( object instanceof Byte[] )
        {
            return new DefaultEntityTag( toPrimitives( (Byte[]) object ) );
        }
        else
        {
            //transform using Mule's transformers.
            return new DefaultEntityTag(
                (byte[]) transformationService.transform( etagValue.getValue(), etagValue.getDataType(), BYTE_ARRAY )
            );
        }
    }

    /**
     * Create a list of ETags
     * @param typedValue The value to create the list of etags from.
     * @param transformationService Transformation service for not recognized value types.
     * @return The list of entity tags.
     * @throws EntityTagException When given values are invalid.
     */
    private static List< DefaultEntityTag > toEtagList(
        List< EntityTag > etagValues,
        TransformationService transformationService
    ) throws EntityTagException
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
     * Creates a BlockValue from a Cf block option.
     * @param block The block option to use.
     * @return BlockValue that corresponds to the block option.
     */
    public static BlockValue toBlockValue( BlockOption block )
    {
        return BlockValue.create( block.getNum(), block.getSzx(), block.isM() );
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
    public static String queryString(
        List< ? extends AbstractQueryParam > defaultQueryParams,
        List< ? extends AbstractQueryParam > queryParams
    )
    {
        if (
            ( defaultQueryParams == null || defaultQueryParams.isEmpty() )
                && ( queryParams == null || queryParams.isEmpty() )
        ) return null;
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
     * @throws EntityTagException when the Entity-tag could not be created from bytes
     */
    public static Optional< List< DefaultEntityTag > > getList( List< byte[] > bytesList ) throws EntityTagException
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
