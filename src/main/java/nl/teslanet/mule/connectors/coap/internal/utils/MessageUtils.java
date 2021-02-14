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

import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.streaming.bytes.CursorStreamProvider;
import org.mule.runtime.api.transformation.TransformationService;
import org.mule.runtime.core.api.message.OutputHandler;
import org.mule.runtime.core.api.util.IOUtils;

import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;
import nl.teslanet.mule.connectors.coap.api.options.ETag;
import nl.teslanet.mule.connectors.coap.internal.Defs;


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

    /* TODO needed in later release
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
            return new ETag( toPrimitives( (Byte[]) object ));
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
