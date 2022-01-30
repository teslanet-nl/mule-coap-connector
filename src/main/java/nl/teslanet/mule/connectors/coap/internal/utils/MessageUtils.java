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
package nl.teslanet.mule.connectors.coap.internal.utils;


import static org.mule.runtime.api.metadata.DataType.BYTE_ARRAY;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.californium.core.coap.BlockOption;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.elements.util.Bytes;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.transformation.TransformationService;

import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;
import nl.teslanet.mule.connectors.coap.api.error.InvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.api.options.BlockValue;
import nl.teslanet.mule.connectors.coap.api.options.ETag;
import nl.teslanet.mule.connectors.coap.api.options.OptionUtils;
import nl.teslanet.mule.connectors.coap.api.options.OtherOption;
import nl.teslanet.mule.connectors.coap.api.options.RequestOptions;
import nl.teslanet.mule.connectors.coap.api.options.ResponseOptions;
import nl.teslanet.mule.connectors.coap.api.query.QueryParam;
import nl.teslanet.mule.connectors.coap.api.query.QueryParamAttribute;
import nl.teslanet.mule.connectors.coap.api.query.QueryParamConfig;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidByteArrayValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;


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
     * @throws InvalidOptionValueException when given option value could not be copied
     */
    public static void copyOptions( RequestOptions requestOptions, OptionSet optionSet, TransformationService transformationService ) throws InternalInvalidOptionValueException
    {
        if ( requestOptions.isIfExists() )
        {
            optionSet.addIfMatch( new byte [0] );
        }
        if ( requestOptions.getIfMatch() != null )
        {
            List< ETag > etags;
            try
            {
                etags= MessageUtils.toEtagList( requestOptions.getIfMatch(), transformationService );
                for ( ETag etag : etags )
                {
                    optionSet.addIfMatch( etag.getBytes() );
                }
            }
            catch ( IOException | InvalidETagException e )
            {
                throw new InternalInvalidOptionValueException( "If-Match", "", e );
            }
        }
        if ( requestOptions.getEtags() != null )
        {
            List< ETag > etags;
            try
            {
                etags= MessageUtils.toEtagList( requestOptions.getEtags(), transformationService );
                for ( ETag etag : etags )
                {
                    if ( etag.isEmpty() ) throw new InternalInvalidOptionValueException( "ETag", "empty etag is not valid" );
                    optionSet.addETag( etag.getBytes() );
                }
            }
            catch ( IOException | InvalidETagException e )
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
        if ( requestOptions.isProvideResponseSize() )
        {
            optionSet.setSize2( 0 );
        }
        if ( requestOptions.getUriHost() != null )
        {
            optionSet.setUriHost( requestOptions.getUriHost() );
        }
        if ( requestOptions.getUriPort() != null )
        {
            optionSet.setUriPort( requestOptions.getUriPort() );
        }
        if ( requestOptions.getProxyUri() != null )
        {
            optionSet.setProxyUri( requestOptions.getProxyUri() );
        }
        if ( requestOptions.getRequestSize() != null )
        {
            optionSet.setSize1( requestOptions.getRequestSize() );
        }
        if ( requestOptions.getProxyScheme() != null )
        {
            optionSet.setProxyScheme( requestOptions.getProxyScheme() );
        }
        for ( OtherOption otherOption : requestOptions.getOtherRequestOptions() )
        {
            try
            {
                optionSet.addOption( new Option( otherOption.getNumber(), toBytes( otherOption.getValue(), transformationService ) ) );
            }
            catch ( InternalInvalidByteArrayValueException e )
            {
                throw new InternalInvalidOptionValueException( "Other-Option", "Number { " + otherOption.getNumber() + " }", e );
            }
        }
    }

    /**
     * Copy options from {@link ResponseOptions} to {@link OptionSet}.
     * @param responseOptions to copy from
     * @param optionSet to copy to
     * @throws InternalInvalidOptionValueException 
     * @throws InvalidETagException when an option containes invalid ETag value
     * @throws IOException when an option stream throws an error.
     */
    public static void copyOptions( ResponseOptions responseOptions, OptionSet optionSet, TransformationService transformationService ) throws InternalInvalidOptionValueException,
        IOException,
        InvalidETagException
    {
        if ( responseOptions.getEtag() != null )
        {
            ETag etag= MessageUtils.toETag( responseOptions.getEtag(), transformationService );
            if ( etag.isEmpty() ) throw new InternalInvalidOptionValueException( "ETag", "empty etag is not valid" );
            optionSet.addETag( etag.getBytes() );
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
            for ( QueryParam param : responseOptions.getLocationQuery() )
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
        for ( OtherOption otherOption : responseOptions.getOtherResponseOptions() )
        {
            try
            {
                optionSet.addOption( new Option( otherOption.getNumber(), toBytes( otherOption.getValue(), transformationService ) ) );
            }
            catch ( InternalInvalidByteArrayValueException e )
            {
                throw new InternalInvalidOptionValueException( "Other-Option", "Number { " + otherOption.getNumber() + " }", e );
            }
        }
    }

    /**
     * Convert an object to byte array.
     * @param toConvert The object to convert.
     * @return Converted object as bytes, or null when the input was null.
     * @throws InternalInvalidByteArrayValueException When object cannot be converted to bytes.
     */
    public static byte[] toBytes( Object toConvert, TransformationService transformationService ) throws InternalInvalidByteArrayValueException
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
        else if ( object instanceof ETag )
        {
            return ( (ETag) object ).getBytes();
        }
        else if ( object instanceof Integer )
        {
            return OptionUtils.toBytes( (Integer) object );
        }
        else if ( object instanceof Long )
        {
            return OptionUtils.toBytes( (Long) object );
        }
        //        if ( object instanceof CursorStreamProvider )
        //        {
        //            return IOUtils.toByteArray( (CursorStreamProvider) object );
        //        }
        //        else if ( object instanceof InputStream )
        //        {
        //            byte[] bytes= null;
        //            try
        //            {
        //                bytes= IOUtils.toByteArray( (InputStream) object );
        //            }
        //            catch ( RuntimeException e )
        //            {
        //                throw new InternalInvalidByteArrayValueException( "Cannot convert InputStream instance to byte[]", e );
        //            }
        //            finally
        //            {
        //                IOUtils.closeQuietly( (InputStream) object );
        //            }
        //            return bytes;
        //        }
        //        else if ( object instanceof OutputHandler )
        //        {
        //            ByteArrayOutputStream output= new ByteArrayOutputStream();
        //            try
        //            {
        //                ( (OutputHandler) object ).write( null, output );
        //            }
        //            catch ( IOException e )
        //            {
        //                throw new InternalInvalidByteArrayValueException( "Cannot convert OutputHandler instance to byte[]", e );
        //            }
        //            return output.toByteArray();
        //        }
        //        else
        //        {

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
    * @param typedValue The value to construct an ETag from.
    * @return The ETag object that has been constructed.
    * @throws IOException When the value is a stream that could not be read.
    * @throws InvalidETagException When value cannot be converted to a valid ETag.
    */
    private static ETag toETag( TypedValue< Object > typedValue, TransformationService transformationService ) throws IOException, InvalidETagException
    {
        Object object= TypedValue.unwrap( typedValue );

        if ( object == null )
        {
            return new ETag();
        }
        else if ( object instanceof ETag )
        {
            return (ETag) object;
        }
        else if ( object instanceof String )
        {
            return new ETag( (String) object );
        }
        else if ( object instanceof byte[] )
        {
            return new ETag( (byte[]) object );
        }
        else if ( object instanceof Byte[] )
        {
            return new ETag( toPrimitives( (Byte[]) object ) );
        }
        else
        {
            //do transform using Mule's transformers.
            return new ETag( (byte[]) transformationService.transform( typedValue.getValue(), typedValue.getDataType(), BYTE_ARRAY ) );
        }
    }

    /**
     * Create a list of ETags
     * @param typedValue is the types value to create a list of etags from.
     * @return List of ETags.
     * @throws InvalidETagException 
     * @throws IOException 
     */
    private static List< ETag > toEtagList( TypedValue< Object > typedValue, TransformationService transformationService ) throws IOException, InvalidETagException
    {
        Object object= TypedValue.unwrap( typedValue );
        LinkedList< ETag > list= new LinkedList<>();

        if ( object == null )
        {
            //noop
        }
        else if ( object instanceof Collection< ? > )
        {
            for ( Object item : (Collection< ? >) object )
            {
                list.add( toETag( new TypedValue<>( item, null ), transformationService ) );
            }
        }
        else
        {
            list.add( toETag( typedValue, transformationService ) );
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
            return null;
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
    public static String queryString( List< QueryParamConfig > defaultQueryParams, List< QueryParam > queryParams )
    {
        if ( ( defaultQueryParams == null || defaultQueryParams.isEmpty() ) && ( queryParams == null || queryParams.isEmpty() ) ) return null;
        StringWriter writer= new StringWriter();
        boolean first= true;
        for ( QueryParamConfig param : defaultQueryParams )
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
        for ( QueryParam param : queryParams )
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
     * Construct string representation of query parameters, merged with default parameters.
     * The string conforms to URI format,
     * @param defaultQueryParams The default parameters.
     * @param queryParams The query parameters.
     * @return The string representation for usage in an URI.
     */
    public static String queryString2( List< QueryParamConfig > defaultQueryParams, List< QueryParamConfig > queryParams )
    {
        if ( ( defaultQueryParams == null || defaultQueryParams.isEmpty() ) && ( queryParams == null || queryParams.isEmpty() ) ) return null;
        StringWriter writer= new StringWriter();
        boolean first= true;
        for ( QueryParamConfig param : defaultQueryParams )
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
        for ( QueryParamConfig param : queryParams )
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
     * @param locationQuery The optional list of query parameters.
     * @return The Uri string.
     */
    public static String uriString( List< String > path, List< QueryParamAttribute > query )
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
            for ( QueryParamAttribute param : query )
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
}
