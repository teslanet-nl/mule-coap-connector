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
package nl.teslanet.mule.connectors.coap.test.utils;


import java.io.IOException;
import java.io.InputStream;

import org.mule.runtime.core.api.util.IOUtils;


public class Data
{
    /**
     * Read resource as string.
     *
     * @param resourcePath the resource path
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static String readResourceAsString( String resourcePath ) throws java.io.IOException
    {
        return IOUtils.getResourceAsString( resourcePath, Data.class );
    }

    /**
     * Read resource as inputstream.
     *
     * @param resourcePath the resource path
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static InputStream readResourceAsStream( String resourcePath ) throws java.io.IOException
    {
        return IOUtils.getResourceAsStream( resourcePath, Data.class, true, true );
    }

    /**
     * Create test content of given size 
     * @param size the content size, when -1 then null will be returned
     * @return the test content
     */
    public static byte[] getContent( int size )
    {
        if ( size == -1 ) return null;
        byte[] content= new byte [size];
        for ( int i= 0; i < content.length; i++ )
        {
            content[i]= (byte) ( i % ( Byte.MAX_VALUE + 1 ) );
        }
        return content;
    }

    /**
     * Validates the test content of standard size
     * @param content to validate as String
     * @param size the content size, when -1 content should be null
     * @return true when the content is as expected, otherwise false
     */
    public static boolean validateContent( String content, int size )
    {
        return validateContent( content.getBytes(), size );
    }

    /**
     * Validates the test content of standard size
     * @param content to validate
     * @param size the content size, when -1 content should be null
     * @return true when the content is as expected, otherwise false
     */
    public static boolean validateContent( byte[] content, int size )
    {
        //check if stream should be null.
        if ( size == -1 ) return content == null;
        //stream should not be null.
        if ( content == null ) return false;
        //check length
        if ( content.length != size ) return false;
        //check content
        for ( int i= 0; i < content.length; i++ )
        {
            if ( content[i] != (byte) ( i % ( Byte.MAX_VALUE + 1 ) ) )
            {
                return false;
            } ;
        }
        return true;
    }

    /**
     * Validates the test content of standard size
     * @param content to validate
     * @param size the content size, when -1 content should be null
     * @return true when the content is as expected, otherwise false
     */
    public static boolean validateContent( InputStream content, int size )
    {
        //check if stream should be null.
        if ( size == -1 ) return content == null;
        //stream should not be null.
        if ( content == null ) return false;
        //check content
        try
        {
            for ( int i= 0; i < size; i++ )
            {
                int value;
                value= content.read();
                if ( value != ( i % ( Byte.MAX_VALUE + 1 ) ) )
                {
                    return false;
                } ;
            }
            //check if end of stream is reached.
            return content.read() == -1;
        }
        catch ( IOException e )
        {
            //when stream is not readable it is not valid.
            return false;
        }
    }
}
