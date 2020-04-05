/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2020 (teslanet.nl) Rogier Cobben
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


import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import org.eclipse.californium.core.coap.BlockOption;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.mule.runtime.core.api.util.IOUtils;
import org.slf4j.Logger;

import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;
import nl.teslanet.mule.connectors.coap.api.error.InvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.api.options.BlockValue;
import nl.teslanet.mule.connectors.coap.api.options.ETag;
import nl.teslanet.mule.connectors.coap.api.options.OptionAttributes;
import nl.teslanet.mule.connectors.coap.api.options.RequestOptions;
import nl.teslanet.mule.connectors.coap.api.options.ResponseOptions;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidByteArrayValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;


/**
 * {@code CoAPOptions} is a collection of all options of a CoAP request or a response.
 * {@code CoAPOptions} provides methods for converting Californium OptionSet to 
 * connector API and vice versa.
 * When constructed based on a existing OptionSet it keeps a reference to this 
 * set in stead of making a deep copy, for performance reasons.
 * Notice that Californium OptionSet is not entirely thread-safe: hasObserve =&gt; (int) getObserve()
 * @see Option
 */
public class CoAPOptions
{
    private OptionSet optionSet= null;

    /**
     * Default constructor
     */
    public CoAPOptions()
    {
        super();
        setOptionSet( new OptionSet() );
    }

    /**
     * Constructs Options based on existing OptionSet.
     * Note that is keeps a reference to the OptionSet in stead of a deep copy - handle with care.
     * @param optionSet Set of options that will be used as a reference.
     */
    public CoAPOptions( OptionSet optionSet )
    {
        super();
        this.optionSet= optionSet;
    }

    /**
     * Constructs CoAPOptions based on a property map.
     * @param props The map of properties. 
     * @throws InternalInvalidByteArrayValueException 
     */
    public CoAPOptions( Map< String, Object > props ) throws InternalInvalidByteArrayValueException
    {
        super();
        this.optionSet= new OptionSet();
        fillOptionSet( this.optionSet, props, false );

    }

    /**
     * Constructs @{code CoAPOptions}  based on {@Code Options}.
     * @param options The options. 
     */
//    public CoAPOptions( Options options )
//    {
//        super();
//        this.optionSet= new OptionSet();
//        copyOptions( options, this.optionSet, false );
//    }

    /**
     * Get the OptionSet.
     * @return the optionSet
     */
    public OptionSet getOptionSet()
    {
        return this.optionSet;
    }

    /**
     * Set the OptionSet reference.
     * @param optionSet the optionSet to use
     */
    public void setOptionSet( OptionSet optionSet )
    {
        this.optionSet= optionSet;
    }

    private static Long toLong( Object object )
    {
        if ( Long.class.isInstance( object ) )
        {
            return (Long) object;
        }
        else if ( Object.class.isInstance( object ) )
        {
            return Long.parseLong( object.toString() );
        }
        return null;
    }

    private static Integer toInteger( Object object )
    {
        if ( Integer.class.isInstance( object ) )
        {
            return (Integer) object;
        }
        else if ( Object.class.isInstance( object ) )
        {
            return Integer.parseInt( object.toString() );
        }
        return null;
    }

    private static Boolean toBoolean( Object object )
    {
        if ( Boolean.class.isInstance( object ) )
        {
            return (Boolean) object;
        }
        else if ( Object.class.isInstance( object ) )
        {
            return Boolean.parseBoolean( object.toString() );
        }
        return null;
    }

    private static byte[] toBytes( Object object ) throws InternalInvalidByteArrayValueException     {
        if ( Object.class.isInstance( object ) )
        {
            if ( InputStream.class.isInstance( object ) )
            {
            	byte[] bytes= null;
            	try {
            		bytes= IOUtils.toByteArray((InputStream) object);
            	}
            	catch ( RuntimeException e )
            	{
            		throw new InternalInvalidByteArrayValueException( "Cannot convert object to byte[]", e);
            	}
            	finally
            	{
            		IOUtils.closeQuietly((InputStream) object);
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
                return ( (ETag) object ).asBytes();
            }
            else
            {
                return object.toString().getBytes( CoAP.UTF8_CHARSET );
            }
        }
        return null;
    }

    /**
     * Copy options from {@link Options} to {@link OptionSet}.
     * @param options to copy from
     * @param optionSet to copy to
     * @param clear when {@code true } the optionSet will be cleared before copying. 
     */
//    public static void copyOptions( Options options, OptionSet optionSet, boolean clear )
//    {
//        //make sure Optionset is empty, if needed
//        if ( clear ) optionSet.clear();
//
//        /* if_match_list       = null; // new LinkedList<byte[]>();*/
//        if ( options.getIfMatchList() != null )
//        {
//            for ( Object etag : options.getIfMatchList() )
//            {
//                optionSet.addIfMatch( toBytes( etag ) );
//            }
//        }
//        /*uri_host            = null; // from sender */
//        if ( options.getUriHost() != null )
//        {
//            optionSet.setUriHost( options.getUriHost() );
//        }
//        /* etag_list           = null; // new LinkedList<byte[]>();*/
//        if ( options.getEtagList() != null )
//        {
//            for ( Object etag : options.getEtagList() )
//            {
//                optionSet.addETag( toBytes( etag ) );
//            }
//        }
//        /* if_none_match       = false; */
//        if ( options.getIfNoneMatch() != null )
//        {
//            optionSet.setIfNoneMatch( options.getIfNoneMatch() );
//        }
//        /* uri_port            = null; // from sender*/
//        if ( options.getUriPort() != null )
//        {
//            optionSet.setUriPort( options.getUriPort() );
//        }
//        /* location_path_list  = null; // new LinkedList<String>();*/
//        if ( options.getLocationPathList() != null )
//        {
//            for ( String path : options.getLocationPathList() )
//            {
//                optionSet.addLocationPath( path );
//            }
//        }
//        //            case PropertyNames.COAP_OPT_LOCATIONPATH:
//        //                //TODO prefix with "/" ?
//        //                if ( Object.class.isInstance( otherOption.getValue() ) )
//        //                {
//        //                    optionSet.setLocationPath( otherOption.getValue().toString() );
//        //                }
//        //                break;
//        /* uri_path_list       = null; // new LinkedList<String>();*/
//        if ( options.getUriPathList() != null )
//        {
//            for ( String path : options.getUriPathList() )
//            {
//                optionSet.addUriPath( path );
//            }
//        }
//        //            case PropertyNames.COAP_OPT_URIPATH:
//        //                if ( Object.class.isInstance( otherOption.getValue() ) )
//        //                {
//        //                    optionSet.setUriPath( otherOption.getValue().toString() );
//        //                }
//        //                break;
//        /* content_format      = null;*/
//        if ( options.getContentFormat() != null )
//        {
//            optionSet.setContentFormat( options.getContentFormat() );
//        }
//        /* max_age             = null;*/
//        if ( options.getMaxAge() != null )
//        {
//            optionSet.setMaxAge( options.getMaxAge() );
//        }
//        /* uri_query_list      = null; // new LinkedList<String>();*/
//        //TODO queryparam object
//        if ( options.getUriQueryList() != null )
//        {
//            for ( String query : options.getUriQueryList() )
//            {
//                optionSet.addUriQuery( query );
//            }
//        }
//        //            case PropertyNames.COAP_OPT_URIQUERY:
//        //
//        //                if ( Object.class.isInstance( otherOption.getValue() ) )
//        //                {
//        //                    optionSet.setUriQuery( otherOption.getValue().toString() );
//        //                }
//        //                break;
//        /* accept              = null;*/
//        if ( options.getAccept() != null )
//        {
//            optionSet.setAccept( options.getAccept() );
//        }
//        /* location_query_list = null; // new LinkedList<String>();*/
//        if ( options.getLocationQueryList() != null )
//        {
//            for ( String path : options.getLocationQueryList() )
//            {
//                optionSet.addLocationQuery( path );
//            }
//        }
//        //            case PropertyNames.COAP_OPT_LOCATIONQUERY:
//        //                if ( Object.class.isInstance( otherOption.getValue() ) )
//        //                {
//        //                    optionSet.setLocationQuery( otherOption.getValue().toString() );
//        //                }
//        //                break;
//        /* proxy_uri           = null;*/
//        if ( options.getProxyUri() != null )
//        {
//            optionSet.setProxyUri( options.getProxyUri() );
//        }
//        /* proxy_scheme        = null;*/
//        if ( options.getProxyScheme() != null )
//        {
//            optionSet.setProxyScheme( options.getProxyScheme() );
//        }
//        /* block1              = null;*/
//        if ( options.getBlock1() != null )
//        {
//            optionSet.setBlock1( options.getBlock1().getSzx(), options.getBlock1().isM(), options.getBlock1().getNum() );
//        }
//        /* block2              = null;*/
//        if ( options.getBlock2() != null )
//        {
//            optionSet.setBlock2( options.getBlock2().getSzx(), options.getBlock2().isM(), options.getBlock2().getNum() );
//        }
//        /* size1               = null;*/
//        if ( options.getSize1() != null )
//        {
//            optionSet.setSize1( options.getSize1() );
//        }
//        /* size2               = null;*/
//        if ( options.getSize2() != null )
//        {
//            optionSet.setSize2( options.getSize2() );
//        }
//        /* observe             = null;*/
//        if ( options.getObserve() != null )
//        {
//            optionSet.setObserve( options.getObserve() );
//        }
//        // process other options
//        for ( Entry< String, Object > otherOption : options.getOtherOptions().entryList() )
//        {
//            if ( !otherOption.getKey().isEmpty() )
//            {
//                int optionNr= Integer.parseInt( otherOption.getKey() );
//                optionSet.addOption( new Option( optionNr, toBytes( otherOption.getValue() ) ) );
//            }
//        }
//    }

    /**
     * Copy options from {@link ResponseOptions} to {@link OptionSet}.
     * @param options to copy from
     * @param optionSet to copy to
     * @param clear when {@code true } the optionSet will be cleared before copying. 
     * @throws InternalInvalidByteArrayValueException 
     */
    public static void copyOptions( ResponseOptions options, OptionSet optionSet, boolean clear ) throws InternalInvalidByteArrayValueException
    {
        //make sure Optionset is empty, if needed
        if ( clear ) optionSet.clear();

        /* etag_list           = null; // new LinkedList<byte[]>();*/
        if ( options.getEtag() != null )
        {
            optionSet.addETag( toBytes( options.getEtag() ) );
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
     * Copy options from {@link RequestOptions} to {@link OptionSet}.
     * @param options to copy from
     * @param optionSet to copy to
     * @param clear when {@code true } the optionSet will be cleared before copying. 
     * @throws InternalInvalidByteArrayValueException 
     */
    public static void copyOptions( RequestOptions options, OptionSet optionSet, boolean clear ) throws InternalInvalidByteArrayValueException
    {
        //make sure Optionset is empty, if needed
        if ( clear ) optionSet.clear();

        /* if_match_list       = null; // new LinkedList<byte[]>();*/
        if ( options.getIfMatchList() != null )
        {
            for ( Object etag : options.getIfMatchList() )
            {
                optionSet.addIfMatch( toBytes( etag ) );
            }
        }
        /* etag_list           = null; // new LinkedList<byte[]>();*/
        if ( options.getEtagList() != null )
        {
            for ( Object etag : options.getEtagList() )
            {
                optionSet.addETag( toBytes( etag ) );
            }
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
                optionSet.addOption( new Option( optionNr, toBytes( otherOption.getValue() ) ) );
            }
        }
    }

    //TODO drop
    @SuppressWarnings("unchecked")
    public static void fillOptionSet( OptionSet optionSet, Map< String, Object > props, boolean clear ) throws InternalInvalidByteArrayValueException
    {
        //make sure Optionset is empty, if needed
        if ( clear ) optionSet.clear();

        for ( Entry< String, Object > e : props.entrySet() )
        {
            /*OptionSet() */
            switch ( e.getKey() )
            {
                /* if_match_list       = null; // new LinkedList<byte[]>();*/
                case PropertyNames.COAP_OPT_IFMATCH_LIST:
                    if ( Collection.class.isInstance( e.getValue() ) )
                    {
                        for ( Object val : ( (Collection< Object >) e.getValue() ) )
                        {
                            optionSet.addIfMatch( toBytes( val ) );
                        }
                    }
                    else if ( Object.class.isInstance( e.getValue() ) )
                    {
                        optionSet.addIfMatch( toBytes( e.getValue() ) );
                    }
                    break;
                /*uri_host            = null; // from sender */
                case PropertyNames.COAP_OPT_URIHOST:
                    if ( Object.class.isInstance( e.getValue() ) )
                    {
                        //TODO support for comma separated values?
                        optionSet.setUriHost( e.getValue().toString() );
                    }
                    break;
                /*
                etag_list           = null; // new LinkedList<byte[]>();*/
                case PropertyNames.COAP_OPT_ETAG_LIST:
                    //TODO multiple values not valid in responses, add check here?
                    if ( Collection.class.isInstance( e.getValue() ) )
                    {
                        for ( Object val : ( (Collection< Object >) e.getValue() ) )
                        {
                            optionSet.addETag( toBytes( val ) );
                        }
                    }
                    else if ( Object.class.isInstance( e.getValue() ) )
                    {
                        //TODO support for comma separated values?
                        optionSet.addETag( toBytes( e.getValue() ) );
                    }
                    break;
                /*if_none_match       = false; */
                case PropertyNames.COAP_OPT_IFNONMATCH:
                    optionSet.setIfNoneMatch( toBoolean( e.getValue() ) );
                    break;

                /*
                uri_port            = null; // from sender*/
                case PropertyNames.COAP_OPT_URIPORT:
                    optionSet.setUriPort( toInteger( e.getValue() ) );
                    break;
                /*
                location_path_list  = null; // new LinkedList<String>();*/
                case PropertyNames.COAP_OPT_LOCATIONPATH_LIST:
                    //TODO check for duplication with LOCATIONPATH
                    if ( Collection.class.isInstance( e.getValue() ) )
                    {
                        for ( Object val : ( (Collection< Object >) e.getValue() ) )
                        {
                            optionSet.addLocationPath( val.toString() );
                        }
                    }
                    break;
                case PropertyNames.COAP_OPT_LOCATIONPATH:
                    //TODO prefix with "/" ?
                    if ( Object.class.isInstance( e.getValue() ) )
                    {
                        optionSet.setLocationPath( e.getValue().toString() );
                    }
                    break;
                /*
                uri_path_list       = null; // new LinkedList<String>();*/
                case PropertyNames.COAP_OPT_URIPATH_LIST:
                    //TODO check for duplication with COAP_OPT_URIPATH
                    if ( Collection.class.isInstance( e.getValue() ) )
                    {
                        for ( Object val : ( (Collection< Object >) e.getValue() ) )
                        {
                            optionSet.addUriPath( val.toString() );
                        }
                    }
                    break;
                case PropertyNames.COAP_OPT_URIPATH:
                    if ( Object.class.isInstance( e.getValue() ) )
                    {
                        optionSet.setUriPath( e.getValue().toString() );
                    }
                    break;
                /*
                content_format      = null;*/
                case PropertyNames.COAP_OPT_CONTENTFORMAT:
                    //TODO add support for Content-Format?
                    //TODO add support for mime-type?
                    //TODO support for format as mime-type string
                    //TODO org.eclipse.californium.core.coap.MediaTypeRegistry
                    optionSet.setContentFormat( toInteger( e.getValue() ) );
                    break;
                /*
                max_age             = null;*/
                case PropertyNames.COAP_OPT_MAXAGE:

                    optionSet.setMaxAge( toLong( e.getValue() ) );
                    break;
                /*
                uri_query_list      = null; // new LinkedList<String>();*/
                case PropertyNames.COAP_OPT_URIQUERY_LIST:
                    //TODO check for duplication with COAP_OPT_URIQUERY
                    if ( Collection.class.isInstance( e.getValue() ) )
                    {
                        for ( Object val : ( (Collection< Object >) e.getValue() ) )
                        {
                            optionSet.addUriQuery( val.toString() );
                        }
                    }
                    break;
                case PropertyNames.COAP_OPT_URIQUERY:

                    if ( Object.class.isInstance( e.getValue() ) )
                    {
                        optionSet.setUriQuery( e.getValue().toString() );
                    }
                    break;
                /*
                accept              = null;*/
                case PropertyNames.COAP_OPT_ACCEPT:
                    //TODO add support for Content-Format?
                    //TODO add support for mime-type?
                    //TODO support for format as string
                    optionSet.setAccept( toInteger( e.getValue() ) );
                    break;
                /*
                location_query_list = null; // new LinkedList<String>();*/
                case PropertyNames.COAP_OPT_LOCATIONQUERY_LIST:
                    if ( Collection.class.isInstance( e.getValue() ) )
                    {
                        for ( Object val : ( (Collection< Object >) e.getValue() ) )
                        {
                            optionSet.addLocationQuery( val.toString() );
                        }
                    }
                    break;
                case PropertyNames.COAP_OPT_LOCATIONQUERY:
                    if ( Object.class.isInstance( e.getValue() ) )
                    {
                        optionSet.setLocationQuery( e.getValue().toString() );
                    }
                    break;
                /*
                proxy_uri           = null;*/
                case PropertyNames.COAP_OPT_PROXYURI:
                    if ( Object.class.isInstance( e.getValue() ) )
                    {
                        optionSet.setProxyUri( e.getValue().toString() );
                    }
                    break;
                /*
                proxy_scheme        = null;*/
                case PropertyNames.COAP_OPT_PROXYSCHEME:
                    if ( Object.class.isInstance( e.getValue() ) )
                    {
                        optionSet.setProxyScheme( e.getValue().toString() );
                    }
                    break;
                /*
                block1              = null;*/
                case PropertyNames.COAP_OPT_BLOCK1_SIZE:
                    //TODO check for duplicate with szx
                    if ( Object.class.isInstance( e.getValue() ) )
                    {
                        int szx= BlockOption.size2Szx( toInteger( e.getValue() ) );
                        boolean m= false;
                        int num= 0;
                        if ( optionSet.hasBlock1() )
                        {
                            m= optionSet.getBlock1().isM();
                            num= optionSet.getBlock1().getNum();
                        }
                        optionSet.setBlock1( szx, m, num );
                    }
                    break;
                case PropertyNames.COAP_OPT_BLOCK1_SZX:
                    if ( Object.class.isInstance( e.getValue() ) )
                    {
                        int szx= toInteger( e.getValue() );
                        boolean m= false;
                        int num= 0;
                        if ( optionSet.hasBlock1() )
                        {
                            m= optionSet.getBlock1().isM();
                            num= optionSet.getBlock1().getNum();
                        }
                        optionSet.setBlock1( szx, m, num );
                    }
                    break;
                case PropertyNames.COAP_OPT_BLOCK1_NUM:
                    if ( Object.class.isInstance( e.getValue() ) )
                    {
                        int szx= 0;
                        boolean m= false;
                        int num= toInteger( e.getValue() );
                        if ( optionSet.hasBlock1() )
                        {
                            szx= optionSet.getBlock1().getSzx();
                            m= optionSet.getBlock1().isM();
                        }
                        optionSet.setBlock1( szx, m, num );
                    }
                    break;
                case PropertyNames.COAP_OPT_BLOCK1_M:
                    if ( Object.class.isInstance( e.getValue() ) )
                    {
                        int szx= 0;
                        boolean m= toBoolean( e.getValue() );
                        int num= 0;
                        if ( optionSet.hasBlock1() )
                        {
                            szx= optionSet.getBlock1().getSzx();
                            num= optionSet.getBlock1().getNum();
                        }
                        optionSet.setBlock1( szx, m, num );
                    }
                    break;
                /*
                block2              = null;*/
                case PropertyNames.COAP_OPT_BLOCK2_SIZE:
                    //TODO check for duplicate with szx
                    if ( Object.class.isInstance( e.getValue() ) )
                    {
                        int szx= BlockOption.size2Szx( toInteger( e.getValue() ) );
                        boolean m= false;
                        int num= 0;
                        if ( optionSet.hasBlock2() )
                        {
                            m= optionSet.getBlock2().isM();
                            num= optionSet.getBlock2().getNum();
                        }
                        optionSet.setBlock2( szx, m, num );
                    }
                    break;
                case PropertyNames.COAP_OPT_BLOCK2_SZX:
                    if ( Object.class.isInstance( e.getValue() ) )
                    {
                        int szx= toInteger( e.getValue() );
                        boolean m= false;
                        int num= 0;
                        if ( optionSet.hasBlock2() )
                        {
                            m= optionSet.getBlock2().isM();
                            num= optionSet.getBlock2().getNum();
                        }
                        optionSet.setBlock2( szx, m, num );
                    }
                    break;
                case PropertyNames.COAP_OPT_BLOCK2_NUM:
                    if ( Object.class.isInstance( e.getValue() ) )
                    {
                        int szx= 0;
                        boolean m= false;
                        int num= toInteger( e.getValue() );
                        if ( optionSet.hasBlock2() )
                        {
                            szx= optionSet.getBlock2().getSzx();
                            m= optionSet.getBlock2().isM();
                        }
                        optionSet.setBlock2( szx, m, num );
                    }
                    break;
                case PropertyNames.COAP_OPT_BLOCK2_M:
                    if ( Object.class.isInstance( e.getValue() ) )
                    {
                        int szx= 0;
                        boolean m= toBoolean( e.getValue() );
                        int num= 0;
                        if ( optionSet.hasBlock2() )
                        {
                            szx= optionSet.getBlock2().getSzx();
                            num= optionSet.getBlock2().getNum();
                        }
                        optionSet.setBlock2( szx, m, num );
                    }
                    break;
                /*
                 * size1               = null;*/
                case PropertyNames.COAP_OPT_SIZE1:
                    optionSet.setSize1( toInteger( e.getValue() ) );
                    break;
                /*
                size2               = null;*/
                case PropertyNames.COAP_OPT_SIZE2:
                    optionSet.setSize2( toInteger( e.getValue() ) );
                    break;
                /*
                observe             = null;*/
                case PropertyNames.COAP_OPT_OBSERVE:
                    optionSet.setObserve( toInteger( e.getValue() ) );
                    break;

                default:
                    /*               
                    others              = null; // new LinkedList<>();
                    */
                    Integer optionNr= optionNrfromPropertyName( e.getKey() );
                    if ( optionNr >= 0 && e.getValue() != null )
                    {
                        Object o= e.getValue();
                        Option option= new Option( optionNr );
                        option.setValue( toBytes( o ) );
                        optionSet.addOption( option );

                    }
            }
        }
    }

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
                handlePropertyError( PropertyNames.COAP_OPT_IFMATCH_LIST, e, logger, errorMsg );
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
                handlePropertyError( PropertyNames.COAP_OPT_ETAG_LIST, e, logger, errorMsg );
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
     * Creates a BlockValue from a Cf block option 
     * @param block1 block option to use the values from
     * @return a BlockValue that corresponds to the block option
     */
    private static BlockValue toBlockValue( BlockOption block )
    {
        return BlockValue.create( block.getNum(), block.getSzx(), block.isM() );
    }

    /**
     * Fill property map with properties contained in given optionSet.
     * Processing options stops when an exception occurs.
     * @param options source of the properties
     * @param props map to put properties in
     * @throws InvalidOptionValueException when option value could not be converted into a property
     */
    public static void fillPropertyMap( OptionSet options, Map< String, Object > props ) throws InternalInvalidOptionValueException
    {
        String errorMsg= "cannot create property";
        fillPropertyMapLoggingOrThrowingErrors( options, props, null, errorMsg );
    }

    /**
     * Fill property map with properties contained in given optionSet.
     * Processing options continues when an exception occurs, after logging an error message.
     * @param options source of the properties
     * @param props map to put properties in
     * @param logger uses for logging errors
     * @param errorMsg message to log on errors
     * @throws InternalInvalidOptionValueException 
     */
    public static void fillPropertyMap( OptionSet options, Map< String, Object > props, Logger logger, String errorMsg ) throws InternalInvalidOptionValueException
    {
        fillPropertyMapLoggingOrThrowingErrors( options, props, logger, errorMsg );
    }

    private static void fillPropertyMapLoggingOrThrowingErrors( OptionSet options, Map< String, Object > props, final Logger logger, String errorMsg )
        throws InternalInvalidOptionValueException
    {
        // List<byte[]> if_match_list;
        if ( !options.getIfMatch().isEmpty() )
        {
            String propertyName= PropertyNames.COAP_OPT_IFMATCH_LIST;
            try
            {
                props.put( propertyName, ETag.getList( options.getIfMatch() ) );
            }
            catch ( InvalidETagException e )
            {
                handlePropertyError( propertyName, e, logger, errorMsg );
            }
        }
        // String       uri_host;
        if ( options.hasUriHost() )
        {
            props.put( PropertyNames.COAP_OPT_URIHOST, options.getUriHost() );
        }
        // List<byte[]> etag_list;
        if ( !options.getETags().isEmpty() )
        {
            String propertyName= PropertyNames.COAP_OPT_ETAG_LIST;
            try
            {
                props.put( propertyName, ETag.getList( options.getETags() ) );
            }
            catch ( InvalidETagException e )
            {
                handlePropertyError( propertyName, e, logger, errorMsg );
            }
        }
        // boolean      if_none_match; // true if option is set
        props.put( PropertyNames.COAP_OPT_IFNONMATCH, Boolean.valueOf( options.hasIfNoneMatch() ) );

        // Integer      uri_port; // null if no port is explicitly defined
        if ( options.hasUriPort() )
        {
            props.put( PropertyNames.COAP_OPT_URIPORT, options.getUriPort() );
        }
        // List<String> location_path_list;
        if ( !options.getLocationPath().isEmpty() )
        {
            props.put( PropertyNames.COAP_OPT_LOCATIONPATH_LIST, options.getLocationPath() );
            props.put( PropertyNames.COAP_OPT_LOCATIONPATH, options.getLocationPathString() );
        }
        // List<String> uri_path_list;
        if ( !options.getUriPath().isEmpty() )
        {
            props.put( PropertyNames.COAP_OPT_URIPATH_LIST, options.getUriPath() );
            props.put( PropertyNames.COAP_OPT_URIPATH, options.getUriPathString() );
        }
        // Integer      content_format;
        if ( options.hasContentFormat() )
        {
            //TODO as text?
            props.put( PropertyNames.COAP_OPT_CONTENTFORMAT, Integer.valueOf( options.getContentFormat() ) );
        }
        // Long         max_age; // (0-4 bytes)
        if ( options.hasMaxAge() )
        {
            props.put( PropertyNames.COAP_OPT_MAXAGE, options.getMaxAge() );
        }
        // List<String> uri_query_list;
        if ( !options.getUriQuery().isEmpty() )
        {
            props.put( PropertyNames.COAP_OPT_URIQUERY_LIST, options.getUriQuery() );
            props.put( PropertyNames.COAP_OPT_URIQUERY, options.getUriQueryString() );
        }
        // Integer      accept;
        if ( options.hasAccept() )
        {
            props.put( PropertyNames.COAP_OPT_ACCEPT, Integer.valueOf( options.getAccept() ) );
        }
        // List<String> location_query_list;
        if ( !options.getLocationQuery().isEmpty() )
        {
            props.put( PropertyNames.COAP_OPT_LOCATIONQUERY_LIST, options.getLocationQuery() );
            props.put( PropertyNames.COAP_OPT_LOCATIONQUERY, options.getLocationQueryString() );
        }
        // String       proxy_uri;
        if ( options.hasProxyUri() )
        {
            props.put( PropertyNames.COAP_OPT_PROXYURI, options.getProxyUri() );
        }
        // String       proxy_scheme;
        if ( options.hasProxyScheme() )
        {
            props.put( PropertyNames.COAP_OPT_PROXYSCHEME, options.getProxyScheme() );
        }
        // BlockOption  block1;
        if ( options.hasBlock1() )
        {
            props.put( PropertyNames.COAP_OPT_BLOCK1_SZX, Integer.valueOf( options.getBlock1().getSzx() ) );
            props.put( PropertyNames.COAP_OPT_BLOCK1_SIZE, Integer.valueOf( options.getBlock1().getSize() ) );
            props.put( PropertyNames.COAP_OPT_BLOCK1_NUM, Integer.valueOf( options.getBlock1().getNum() ) );
            props.put( PropertyNames.COAP_OPT_BLOCK1_M, Boolean.valueOf( options.getBlock1().isM() ) );
        }
        // BlockOption  block2;
        if ( options.hasBlock2() )
        {
            props.put( PropertyNames.COAP_OPT_BLOCK2_SZX, Integer.valueOf( options.getBlock2().getSzx() ) );
            props.put( PropertyNames.COAP_OPT_BLOCK2_SIZE, Integer.valueOf( options.getBlock2().getSize() ) );
            props.put( PropertyNames.COAP_OPT_BLOCK2_NUM, Integer.valueOf( options.getBlock2().getNum() ) );
            props.put( PropertyNames.COAP_OPT_BLOCK2_M, Boolean.valueOf( options.getBlock2().isM() ) );
        }
        // Integer      size1;
        if ( options.hasSize1() )
        {
            props.put( PropertyNames.COAP_OPT_SIZE1, options.getSize1() );
        }
        // Integer      size2;
        if ( options.hasSize2() )
        {
            props.put( PropertyNames.COAP_OPT_SIZE2, options.getSize2() );
        }
        // Integer      observe;
        if ( options.hasObserve() )
        {
            props.put( PropertyNames.COAP_OPT_OBSERVE, options.getObserve() );
        }
        // Arbitrary options
        // List<Option> others;
        for ( Option other : options.getOthers() )
        {
            props.put( PropertyNames.PREFIX_COAP_OPT_OTHER + other.getNumber(), other.getValue() );
            props.put( PropertyNames.PREFIX_COAP_OPT_OTHER + other.getNumber() + PropertyNames.POSTFIX_CRITICAL, Boolean.valueOf( other.isCritical() ) );
            props.put( PropertyNames.PREFIX_COAP_OPT_OTHER + other.getNumber() + PropertyNames.POSTFIX_NOCACHEKEY, Boolean.valueOf( other.isNoCacheKey() ) );
            props.put( PropertyNames.PREFIX_COAP_OPT_OTHER + other.getNumber() + PropertyNames.POSTFIX_UNSAFE, Boolean.valueOf( other.isUnSafe() ) );
        }
    }

    private static int optionNrfromPropertyName( String propertyName )
    {
        Matcher matcher= PropertyNames.otherPattern.matcher( propertyName );

        // if an occurrence if a pattern was found in a given string...
        if ( matcher.find() )
        {
            return Integer.parseInt( matcher.group( 1 ) );
        }
        else
        {
            return -1;
        }
    }
}
