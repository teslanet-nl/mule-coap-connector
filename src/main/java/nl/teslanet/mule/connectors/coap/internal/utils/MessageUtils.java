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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.californium.core.coap.BlockOption;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.streaming.bytes.CursorStreamProvider;
import org.mule.runtime.api.transformation.TransformationService;
import org.mule.runtime.core.api.message.OutputHandler;
import org.mule.runtime.core.api.util.IOUtils;

import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;
import nl.teslanet.mule.connectors.coap.api.error.InvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.api.options.BlockValue;
import nl.teslanet.mule.connectors.coap.api.options.ETag;
import nl.teslanet.mule.connectors.coap.api.options.OtherOption;
import nl.teslanet.mule.connectors.coap.api.options.RequestOptions;
import nl.teslanet.mule.connectors.coap.api.options.ResponseOptions;
import nl.teslanet.mule.connectors.coap.internal.Defs;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidByteArrayValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;


/**
 * Utilities for handling message content.
 *
 */
public class MessageUtils
{
    /**
     * Mule transformation service.
     */
    @Inject
    private static TransformationService transformationService;

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
    public static void copyOptions( RequestOptions requestOptions, OptionSet optionSet ) throws InternalInvalidOptionValueException
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
                etags= MessageUtils.toEtagList( requestOptions.getIfMatch() );
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
                etags= MessageUtils.toEtagList( requestOptions.getEtags() );
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
        if ( requestOptions.isRequestSize2() )
        {
            optionSet.setSize2( 0 );
        }
        if ( requestOptions.getProxyUri() != null )
        {
            optionSet.setProxyUri( requestOptions.getProxyUri() );
        }
        if ( requestOptions.getSize1() != null )
        {
            optionSet.setSize1( requestOptions.getSize1() );
        }
        if ( requestOptions.getProxyScheme() != null )
        {
            optionSet.setProxyScheme( requestOptions.getProxyScheme() );
        }
        for ( OtherOption otherOption : requestOptions.getOtherRequestOptions() )
        {
            try
            {
                optionSet.addOption( new Option( otherOption.getNumber(), optionToBytes( otherOption.getValue() ) ) );
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
    public static void copyOptions( ResponseOptions responseOptions, OptionSet optionSet ) throws InternalInvalidOptionValueException, IOException, InvalidETagException
    {
        if ( responseOptions.getEtag() != null )
        {
            ETag etag= MessageUtils.toETag( responseOptions.getEtag() );
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
            optionSet.setLocationQuery( responseOptions.getLocationQuery() );
        }
        for ( OtherOption otherOption : responseOptions.getOtherResponseOptions() )
        {
            try
            {
                optionSet.addOption( new Option( otherOption.getNumber(), optionToBytes( otherOption.getValue() ) ) );
            }
            catch ( InternalInvalidByteArrayValueException e )
            {
                throw new InternalInvalidOptionValueException( "Other-Option", "Number { " + otherOption.getNumber() + " }", e );
            }
        }
    }

    //TODO make one toBytes method
    /**
     * Convert option object to bytes.
     * @param optionObject to convert.
     * @return Converted object as bytes.
     * @throws InternalInvalidByteArrayValueException when object cannot be converted to bytes.
     */
    private static byte[] optionToBytes( Object optionObject ) throws InternalInvalidByteArrayValueException
    {
        if ( Object.class.isInstance( optionObject ) )
        {
            if ( InputStream.class.isInstance( optionObject ) )
            {
                byte[] bytes= null;
                try
                {
                    bytes= IOUtils.toByteArray( (InputStream) optionObject );
                }
                catch ( RuntimeException e )
                {
                    throw new InternalInvalidByteArrayValueException( "Cannot convert object to byte[]", e );
                }
                finally
                {
                    IOUtils.closeQuietly( (InputStream) optionObject );
                }
                return bytes;
            }
            else if ( Byte[].class.isInstance( optionObject ) )
            {
                return (byte[]) optionObject;
            }
            else if ( byte[].class.isInstance( optionObject ) )
            {
                return (byte[]) optionObject;
            }
            else if ( ETag.class.isInstance( optionObject ) )
            {
                return ( (ETag) optionObject ).getBytes();
            }
            else
            {
                return optionObject.toString().getBytes( CoAP.UTF8_CHARSET );
            }
        }
        return null;
    }

    /**
     * Convert a payload value to byte array.
     * @param payload is the payload to convert.
     * @return converted payload as bytes
     * @throws IOException when the payload is an outputhandler that cannot write.
     */
    public static byte[] payloadToByteArray( TypedValue< Object > payload ) throws IOException
    {
        Object object= TypedValue.unwrap( payload );

        if ( object == null )
        {
            return null;
        }
        else if ( object instanceof String )
        {
            return ( (String) object ).getBytes( Defs.COAP_CHARSET );
        }
        if ( object instanceof CursorStreamProvider )
        {
            return IOUtils.toByteArray( (CursorStreamProvider) object );
        }
        else if ( object instanceof InputStream )
        {
            return IOUtils.toByteArray( (InputStream) object );
        }
        else if ( object instanceof byte[] )
        {
            return (byte[]) object;
        }
        else if ( object instanceof Byte[] )
        {
            return toPrimitives( (Byte[]) object );
        }
        else if ( object instanceof OutputHandler )
        {
            ByteArrayOutputStream output= new ByteArrayOutputStream();
            ( (OutputHandler) object ).write( null, output );
            return output.toByteArray();
        }
        else //do transform using Mule's transformers.
        {
            return (byte[]) transformationService.transform( Message.builder().payload( payload ).build(), BYTE_ARRAY ).getPayload().getValue();
        }
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
    private static ETag toETag( TypedValue< Object > typedValue ) throws IOException, InvalidETagException
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
        if ( object instanceof CursorStreamProvider )
        {
            return new ETag( IOUtils.toByteArray( (CursorStreamProvider) object ) );
        }
        else if ( object instanceof InputStream )
        {
            return new ETag( IOUtils.toByteArray( (InputStream) object ) );
        }
        else if ( object instanceof byte[] )
        {
            return new ETag( (byte[]) object );
        }
        else if ( object instanceof Byte[] )
        {
            return new ETag( toPrimitives( (Byte[]) object ) );
        }
        else if ( object instanceof OutputHandler )
        {
            ByteArrayOutputStream output= new ByteArrayOutputStream();
            ( (OutputHandler) object ).write( null, output );
            return new ETag( output.toByteArray() );
        }
        else //do transform using Mule's transformers.
        {
            return new ETag( (byte[]) transformationService.transform( Message.builder().payload( typedValue ).build(), BYTE_ARRAY ).getPayload().getValue() );
        }
    }

    /**
     * Create a list of ETags
     * @param typedValue is the types value to create a list of etags from.
     * @return List of ETags.
     * @throws InvalidETagException 
     * @throws IOException 
     */
    private static List< ETag > toEtagList( TypedValue< Object > typedValue ) throws IOException, InvalidETagException
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
                list.add( toETag( new TypedValue<>( item, null ) ) );
            }
        }
        else
        {
            list.add( toETag( typedValue ) );
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
}
