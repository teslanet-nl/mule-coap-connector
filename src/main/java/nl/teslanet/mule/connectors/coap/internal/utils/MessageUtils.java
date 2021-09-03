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
import java.util.Map.Entry;

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
import org.slf4j.Logger;

import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;
import nl.teslanet.mule.connectors.coap.api.error.InvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.api.options.BlockValue;
import nl.teslanet.mule.connectors.coap.api.options.ETag;
import nl.teslanet.mule.connectors.coap.api.options.OptionAttributes;
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
    static private TransformationService transformationService;

    /**
     * Do not create objects.
     */
    private MessageUtils()
    {
        //NOOP
    }

    //TODO move to AttibuteUtils
    //TODO rename
    /**
     * Copy options to option attributes from given optionSet.
     * Processing options stops when an exception occurs.
     * @param optionSet source of the properties
     * @param options Options to fill
     * @throws InvalidOptionValueException when option value could not be converted into a property
     */
    public static void copyOptions( OptionSet optionSet, OptionAttributes options ) throws InternalInvalidOptionValueException
    {
        String errorMsg= "cannot create property";
        copyOptionsLoggingOrThrowingErrors( optionSet, options, null, errorMsg );
    }

    //TODO move to AttibuteUtils
    /**
     * Copy options to option attributes from given optionSet.
     * Processing options continues when an exception occurs, after logging an error message.
     * @param optionSet source of the properties
     * @param options Options attributes to copy to
     * @param logger uses for logging errors
     * @param errorMsg message to log on errors
     * @throws InternalInvalidOptionValueException should not be thrown
     */
    public static void copyOptions( OptionSet optionSet, OptionAttributes options, Logger logger, String errorMsg ) throws InternalInvalidOptionValueException
    {
        copyOptionsLoggingOrThrowingErrors( optionSet, options, logger, errorMsg );
    }

    /**
     * Handle error that occurs during property processing
     * @param propertyName 
     * @param e cause exception
     * @param logger logger to use for logging when not null
     * @param errorMsg that will be logged or put into exception
     * @throws InternalInvalidOptionValueException when when logger is null in stead of logging
     */
    private static void handlePropertyError( String propertyName, Exception e, Logger logger, String errorMsg ) throws InternalInvalidOptionValueException
    {
        InternalInvalidOptionValueException exception= new InternalInvalidOptionValueException( propertyName, errorMsg, e );
        if ( logger == null )
        {
            throw exception;
        }
        else
        {
            logger.error( errorMsg + " { " + propertyName + " }", exception );
        }

    }

    //TODO move to AttibuteUtils
    /**
     * Copy options to option attributes from given optionSet.
     * Processing options continues when an exception occurs and logger is given.
     * @param optionSet source of the properties
     * @param options Options attributes to copy to
     * @param logger uses for logging errors
     * @param errorMsg message to log on errors
     * @throws InternalInvalidOptionValueException when an option cannot be converted.
     */
    private static void copyOptionsLoggingOrThrowingErrors( OptionSet optionSet, OptionAttributes options, final Logger logger, String errorMsg )
        throws InternalInvalidOptionValueException
    {
        // List<byte[]> if_match_list;
        if ( !optionSet.getIfMatch().isEmpty() )
        {
            try
            {
                options.setIfMatchList( ETag.getList( optionSet.getIfMatch() ) );
            }
            catch ( InvalidETagException e )
            {
                handlePropertyError( "IfMatch", e, logger, errorMsg );
            }
        }
        // String       uri_host;
        if ( optionSet.hasUriHost() )
        {
            options.setUriHost( optionSet.getUriHost() );
        }
        // List<byte[]> etag_list;
        if ( !optionSet.getETags().isEmpty() )
        {
            try
            {
                options.setEtagList( ETag.getList( optionSet.getETags() ) );
            }
            catch ( InvalidETagException e )
            {
                handlePropertyError( "ETags", e, logger, errorMsg );
            }
        }
        // boolean      if_none_match; // true if option is set
        options.setIfNoneMatch( optionSet.hasIfNoneMatch() );

        // Integer      uri_port; // null if no port is explicitly defined
        if ( optionSet.hasUriPort() )
        {
            options.setUriPort( optionSet.getUriPort() );
        }
        // List<String> location_path_list;
        if ( !optionSet.getLocationPath().isEmpty() )
        {
            options.setLocationPathList( Collections.unmodifiableList( optionSet.getLocationPath() ) );
            options.setLocationPath( optionSet.getLocationPathString() );
        }
        // List<String> uri_path_list;
        if ( !optionSet.getUriPath().isEmpty() )
        {
            options.setUriPathList( Collections.unmodifiableList( optionSet.getUriPath() ) );
            options.setUriPath( optionSet.getUriPathString() );
        }
        // Integer      content_format;
        if ( optionSet.hasContentFormat() )
        {
            options.setContentFormat( Integer.valueOf( optionSet.getContentFormat() ) );
        }
        // Long         max_age; // (0-4 bytes)
        if ( optionSet.hasMaxAge() )
        {
            options.setMaxAge( optionSet.getMaxAge() );
        }
        // List<String> uri_query_list;
        if ( !optionSet.getUriQuery().isEmpty() )
        {
            options.setUriQueryList( Collections.unmodifiableList( optionSet.getUriQuery() ) );
            options.setUriQuery( optionSet.getUriQueryString() );
        }
        // Integer      accept;
        if ( optionSet.hasAccept() )
        {
            options.setAccept( Integer.valueOf( optionSet.getAccept() ) );
        }
        // List<String> location_query_list;
        if ( !optionSet.getLocationQuery().isEmpty() )
        {
            options.setLocationQueryList( Collections.unmodifiableList( optionSet.getLocationQuery() ) );
            options.setLocationQuery( optionSet.getLocationQueryString() );
        }
        // String       proxy_uri;
        if ( optionSet.hasProxyUri() )
        {
            options.setProxyUri( optionSet.getProxyUri() );
        }
        // String       proxy_scheme;
        if ( optionSet.hasProxyScheme() )
        {
            options.setProxyScheme( optionSet.getProxyScheme() );
        }
        // BlockOption  block1;
        if ( optionSet.hasBlock1() )
        {
            options.setBlock1( toBlockValue( optionSet.getBlock1() ) );
        }
        // BlockOption  block2;
        if ( optionSet.hasBlock2() )
        {
            options.setBlock2( toBlockValue( optionSet.getBlock2() ) );
        }
        // Integer      size1;
        if ( optionSet.hasSize1() )
        {
            options.setSize1( optionSet.getSize1() );
        }
        // Integer      size2;
        if ( optionSet.hasSize2() )
        {
            options.setSize2( optionSet.getSize2() );
        }
        // Integer      observe;
        if ( optionSet.hasObserve() )
        {
            options.setObserve( optionSet.getObserve() );
        }
        // Arbitrary options
        // List<Option> others;
        for ( Option other : optionSet.getOthers() )
        {
            options.addOtherOption( String.valueOf( other.getNumber() ), other.getValue() );
        }
    }

    /**
     * Copy options from {@link RequestOptions} to {@link OptionSet}.
     * @param options to copy from
     * @param optionSet to copy to
     * @param clear when {@code true } the optionSet will be cleared before copying. 
     * @throws InternalInvalidOptionValueException 
    
     */
    public static void copyOptions( RequestOptions options, OptionSet optionSet, boolean clear ) throws InternalInvalidOptionValueException
    {
        //make sure Optionset is empty, if needed
        if ( clear ) optionSet.clear();

        /* if_none_match */
        if ( options.isIfNoneMatch() )
        {
            optionSet.setIfNoneMatch( true );
        }
        /* if_match_list       = null; // new LinkedList<byte[]>();*/
        if ( options.getIfMatchList() != null )
        {
            List< ETag > etags;
            try
            {
                etags= MessageUtils.toEtagList( options.getIfMatchList() );
                for ( ETag etag : etags )
                {
                    optionSet.addIfMatch( MessageUtils.toBytes( etag ) );
                }
            }
            catch ( IOException | InvalidETagException | InternalInvalidByteArrayValueException e )
            {
                throw new InternalInvalidOptionValueException( "If-Match", "", e );
            }
        }
        /* etag_list           = null; // new LinkedList<byte[]>();*/
        if ( options.getEtagList() != null )
        {
            List< ETag > etags;
            try
            {
                etags= MessageUtils.toEtagList( options.getEtagList() );
                for ( ETag etag : etags )
                {
                    optionSet.addETag( etag.getBytes() );
                }
            }
            catch ( IOException | InvalidETagException e )
            {
                throw new InternalInvalidOptionValueException( "ETag", "", e );
            }
        }
        /* content_format      = null;*/
        if ( options.getContentFormat() != null )
        {
            optionSet.setContentFormat( options.getContentFormat() );
        }
        /* accept              = null;*/
        if ( options.getAccept() != null )
        {
            optionSet.setAccept( options.getAccept() );
        }
        /* proxy_uri           = null;*/
        if ( options.getProxyUri() != null )
        {
            optionSet.setProxyUri( options.getProxyUri() );
        }
        /* proxy_scheme        = null;*/
        if ( options.getProxyScheme() != null )
        {
            optionSet.setProxyScheme( options.getProxyScheme() );
        }
        // process other options
        for ( Entry< String, Object > otherOption : options.getOtherRequestOptions().entryList() )
        {
            if ( !otherOption.getKey().isEmpty() )
            {
                int optionNr= Integer.parseInt( otherOption.getKey() );
                try
                {
                    optionSet.addOption( new Option( optionNr, toBytes( otherOption.getValue() ) ) );
                }
                catch ( InternalInvalidByteArrayValueException e )
                {
                    throw new InternalInvalidOptionValueException( "Other-Option", "Number { " + Integer.toString( optionNr ) + " }", e );
                }
            }
        }
    }

    /**
     * Copy options from {@link ResponseOptions} to {@link OptionSet}.
     * @param options to copy from
     * @param optionSet to copy to
     * @param clear when {@code true } the optionSet will be cleared before copying. 
     * @throws InternalInvalidByteArrayValueException 
     * @throws InvalidETagException when an option containes invalid ETag value
     * @throws IOException when an option stream throws an error.
     */
    public static void copyOptions( ResponseOptions options, OptionSet optionSet, boolean clear ) throws InternalInvalidByteArrayValueException, IOException, InvalidETagException
    {
        //make sure Optionset is empty, if needed
        if ( clear ) optionSet.clear();

        /* etag_list           = null; // new LinkedList<byte[]>();*/
        //TODO check for NothingType?
        if ( options.getEtag() != null )
        {
            optionSet.addETag( MessageUtils.toETag( options.getEtag() ).getBytes() );
        }
        /* content_format      = null;*/
        if ( options.getContentFormat() != null )
        {
            optionSet.setContentFormat( options.getContentFormat() );
        }
        /* max_age             = null;*/
        if ( options.getMaxAge() != null )
        {
            optionSet.setMaxAge( options.getMaxAge() );
        }
        //            case PropertyNames.COAP_OPT_URIQUERY:
        //
        //                if ( Object.class.isInstance( otherOption.getValue() ) )
        //                {
        //                    optionSet.setUriQuery( otherOption.getValue().toString() );
        //                }
        //                break;
        /* location_path */
        if ( options.getLocationPath() != null )
        {
            optionSet.setLocationPath( options.getLocationPath() );
        }
        /* location_query */
        if ( options.getLocationQuery() != null )
        {
            optionSet.setLocationQuery( options.getLocationQuery() );
        }

        //        if ( options.getLocationQueryList() != null )
        //        {
        //            for ( String path : options.getLocationQueryList() )
        //            {
        //                optionSet.addLocationQueryString( path );
        //            }
        //        }
        //            case PropertyNames.COAP_OPT_LOCATIONQUERY:
        //                if ( Object.class.isInstance( otherOption.getValue() ) )
        //                {
        //                    optionSet.setLocationQuery( otherOption.getValue().toString() );
        //                }
        //                break;
        // process other options
        for ( Entry< String, Object > otherOption : options.getOtherResponseOptions().entryList() )
        {
            if ( !otherOption.getKey().isEmpty() )
            {
                int optionNr= Integer.parseInt( otherOption.getKey() );
                optionSet.addOption( new Option( optionNr, toBytes( otherOption.getValue() ) ) );
            }
        }
    }

    /**
     * Convert Object to bytes.
     * @param object to convert
     * @return Converted object as bytes,
     * @throws InternalInvalidByteArrayValueException
     */
    public static byte[] toBytes( Object object ) throws InternalInvalidByteArrayValueException
    {
        if ( Object.class.isInstance( object ) )
        {
            if ( InputStream.class.isInstance( object ) )
            {
                byte[] bytes= null;
                try
                {
                    bytes= IOUtils.toByteArray( (InputStream) object );
                }
                catch ( RuntimeException e )
                {
                    throw new InternalInvalidByteArrayValueException( "Cannot convert object to byte[]", e );
                }
                finally
                {
                    IOUtils.closeQuietly( (InputStream) object );
                }
                return bytes;
            }
            else if ( Byte[].class.isInstance( object ) )
            {
                return (byte[]) object;
            }
            else if ( byte[].class.isInstance( object ) )
            {
                return (byte[]) object;
            }
            else if ( ETag.class.isInstance( object ) )
            {
                return ( (ETag) object ).getBytes();
            }
            else
            {
                return object.toString().getBytes( CoAP.UTF8_CHARSET );
            }
        }
        return null;
    }

    /**
     * Convert a typed value to byte array.
     * @param typedValueObject is the value to convert.
     * @return converted value as bytes
     * @throws IOException when the value is an outputhandler that cannot write.
     */
    public static byte[] toByteArray( TypedValue< Object > typedValueObject ) throws IOException
    {
        Object object= typedValueObject.getValue();

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
            return (byte[]) transformationService.transform( Message.builder().payload( typedValueObject ).build(), BYTE_ARRAY ).getPayload().getValue();
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
        Object object= typedValueObject.getValue();
    
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
    public static ETag toETag( TypedValue< Object > typedValue ) throws IOException, InvalidETagException
    {
        Object object= typedValue.getValue();

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
    public static List< ETag > toEtagList( TypedValue< Object > typedValue ) throws IOException, InvalidETagException
    {
        Object object= typedValue.getValue();
        LinkedList< ETag > list= new LinkedList< ETag >();

        if ( object == null )
        {
            //noop
        }
        else if ( object instanceof Collection< ? > )
        {
            for ( Object item : (Collection< ? >) object )
            {
                list.add( toETag( new TypedValue< Object >( item, null ) ) );
            }
        }
        else
        {
            list.add( toETag( typedValue ) );
        }
        return Collections.unmodifiableList( list );
    }

    /**
     * Creates a BlockValue from a Cf block option 
     * @param block1 block option to use the values from
     * @return a BlockValue that corresponds to the block option
     */
    private static BlockValue toBlockValue( BlockOption block )
    {
        return BlockValue.create( block.getNum(), block.getSzx(), block.isM() );
    }

    /**
     * Converts {@code Byte[]} to array of primitives.
     * @param bytes the array to convert.
     * @return the {@code byte[]} or  {@code null} when empty.
     */
    public static byte[] toPrimitives( Byte[] bytes )
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
