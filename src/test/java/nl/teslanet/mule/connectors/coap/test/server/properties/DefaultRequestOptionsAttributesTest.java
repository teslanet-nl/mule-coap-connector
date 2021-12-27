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
package nl.teslanet.mule.connectors.coap.test.server.properties;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;
import nl.teslanet.mule.connectors.coap.api.options.ETag;
import nl.teslanet.mule.connectors.coap.api.options.OtherOptionAttribute;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultOtherOptionAttribute;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultRequestOptionsAttributes;


/**
 * Test option classes
 *
 */
public class DefaultRequestOptionsAttributesTest
{
    @Test
    public void testOptionSetifExists() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        byte[] etagValue1= {};
        set.addIfMatch( etagValue1.clone() );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        List< ETag > list= attributes.getIfMatch();
        boolean ifExists= attributes.isIfExists();

        assertNull( list );
        assertTrue( "coap.opt.if_match.any: wrong value", ifExists );
    }

    @Test
    public void testOptionSetifExistsMultiple() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        byte[] etagValue1= {};
        byte[] etagValue2= { (byte) 0x11, (byte) 0xFF };
        byte[] etagValue3= {};

        set.addIfMatch( etagValue1.clone() );
        set.addIfMatch( etagValue2.clone() );
        set.addIfMatch( etagValue3.clone() );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        List< ETag > list= attributes.getIfMatch();
        boolean ifExists= attributes.isIfExists();

        assertNotNull( list );
        assertTrue( "coap.opt.if_match.any: wrong value", ifExists );
        assertEquals( "coap.opt.if_match.etags: wrong number of etags", 1, list.size() );
        assertFalse( "coap.opt.if_match.etags: missing etag", list.contains( new ETag( etagValue1 ) ) );
        assertTrue( "coap.opt.if_match.etags: missing etag", list.contains( new ETag( etagValue2 ) ) );
        assertFalse( "coap.opt.if_match.etags: etag not expected", list.contains( new ETag( etagValue3 ) ) );
    }

    @Test
    public void testOptionSetIfMatch() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        byte[] etagValue1= { (byte) 0x00, (byte) 0xFF };
        byte[] etagValue2= { (byte) 0x11, (byte) 0xFF };
        set.addIfMatch( etagValue1.clone() );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        List< ETag > list= attributes.getIfMatch();

        assertNotNull( list );
        assertEquals( "coap.opt.if_match.etags: wrong number of etags", 1, list.size() );
        assertTrue( "coap.opt.if_match.etags: missing etag", list.contains( new ETag( etagValue1 ) ) );
        assertFalse( "coap.opt.if_match.etags: etag not expected", list.contains( new ETag( etagValue2 ) ) );
    }

    @Test
    public void testOptionSetIfMatchMultiple() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        byte[] etagValue1= { (byte) 0x00, (byte) 0xFF };
        byte[] etagValue2= { (byte) 0x11, (byte) 0xFF };
        byte[] etagValue3= { (byte) 0x22, (byte) 0xFF };

        set.addIfMatch( etagValue1.clone() );
        set.addIfMatch( etagValue2.clone() );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        List< ETag > list= attributes.getIfMatch();

        assertNotNull( list );
        assertEquals( "coap.opt.if_match.etags: wrong number of etags", 2, list.size() );
        assertTrue( "coap.opt.if_match.etags: missing etag", list.contains( new ETag( etagValue1 ) ) );
        assertTrue( "coap.opt.if_match.etags: missing etag", list.contains( new ETag( etagValue2 ) ) );
        assertFalse( "coap.opt.if_match.etags: etag not expected", list.contains( new ETag( etagValue3 ) ) );
    }

    @Test
    public void testOptionUrihost() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String host= "testhost";
        set.setUriHost( host );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        String attr= attributes.getUriHost();

        assertEquals( "coap.opt.uri_host: wrong value", host, attr );
    }

    @Test
    public void testOptionSetETagMultiple() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        byte[] etagValue1= { (byte) 0x00, (byte) 0xFF };
        byte[] etagValue2= { (byte) 0x11, (byte) 0xFF };
        byte[] etagValue3= { (byte) 0x22, (byte) 0xFF };

        set.addETag( etagValue1.clone() );
        set.addETag( etagValue2.clone() );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        List< ETag > list= attributes.getEtags();

        assertNotNull( list );
        assertEquals( "coap.opt.etag.list: wrong number of etags", 2, list.size() );
        assertTrue( "coap.opt.etag.list: missing etag", list.contains( new ETag( etagValue1 ) ) );
        assertTrue( "coap.opt.etag.list: missing etag", list.contains( new ETag( etagValue2 ) ) );
        assertFalse( "coap.opt.etag.list: etag not expected", list.contains( new ETag( etagValue3 ) ) );
    }

    @Test
    public void testOptionIfNoneMatch() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        set.setIfNoneMatch( new Boolean( true ) );
        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        boolean attr= attributes.isIfNoneMatch();

        assertTrue( "coap.opt.if_none_match: wrong value", attr );

        set.setIfNoneMatch( new Boolean( false ) );
        attributes= new DefaultRequestOptionsAttributes( set );

        attr= attributes.isIfNoneMatch();

        assertFalse( "coap.opt.if_none_match: wrong value", attr );
    }

    @Test
    public void testOptionUriPort() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer port= 5536;
        set.setUriPort( port );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        Integer attr= attributes.getUriPort();

        assertEquals( "coap.opt.uri_port: wrong value", port, attr );
    }

    @Test
    public void testOptionSetUriPath() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String value1= "this";
        String value2= "is";
        String value3= "some path";
        //String total= "this/is";

        set.addUriPath( value1 );
        set.addUriPath( value2 );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        List< String > list= attributes.getUriPath();

        assertNotNull( list );
        assertEquals( "coap.opt.uri_path.list: wrong number of path segment", 2, list.size() );
        assertEquals( "coap.opt.uri_path.list: missing path segment", value1, list.get( 0 ) );
        assertEquals( "coap.opt.uri_path.list: missing path segment", value2, list.get( 1 ) );
        assertFalse( "coap.opt.uri_path.list: path segment not expected", list.contains( value3 ) );
        //TODO assertEquals( "coap.opt.uri_path: wrong path", total, props.get( "coap.opt.uri_path" ) );
    }

    @Test
    public void testOptionContentFormat() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer format= 41;
        set.setContentFormat( format );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        Integer attr= attributes.getContentFormat();

        assertEquals( "coap.opt.content_format: wrong value", format, attr );
    }

//    @Test
//    public void testOptionSetUriQuery() throws InvalidETagException, InternalInvalidOptionValueException
//    {
//        //TODO RC add multiple values
//        OptionSet set= new OptionSet();
//        String[] values= { "this", "is", "some=locationquery" };
//        ArrayList< DefaultQueryParamAttribute > expected= new ArrayList<>();
//        for ( String param : values )
//        {
//            expected.add( new DefaultQueryParamAttribute( param ) );
//            set.addUriQuery( param );
//        }
//
//        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );
//
//        MultiMap< String, String > query= attributes.getUriQuery();
//
//        int index= 0;
//        for ( DefaultQueryParamAttribute expect : expected )
//        {
//            assertTrue( "coap.opt.location_query.list: wrong query", query.containsKey( expect.getKey() ));
//            assertEquals( "coap.opt.location_query.list: wrong value", expect.getValue(), query.get( expected.get( index ).getValue()  ));
//            index++;
//        }
//    }

    @Test
    public void testOptionAccept() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer format= new Integer( 41 );
        set.setAccept( format );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        Integer attr= attributes.getAccept();

        assertEquals( "coap.opt.accept: wrong value", format, attr );
    }

    @Test
    public void testOptionProxyUri() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String uri= "testproxyuri";
        set.setProxyUri( uri );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        String attr= attributes.getProxyUri();

        assertEquals( "coap.opt.proxy_uri: wrong value", uri, attr );
    }

    @Test
    public void testOptionProxyScheme() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String scheme= "testproxyscheme";
        set.setProxyScheme( scheme );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        String attr= attributes.getProxyScheme();

        assertEquals( "coap.opt.proxy_scheme: wrong value", scheme, attr );
    }

    @Test
    public void testOptionSize1() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer size= new Integer( 120 );
        set.setSize1( size );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        Integer attr= attributes.getSize1();

        assertEquals( "coap.opt.size1: wrong value", size, attr );
    }

    @Test
    public void testOptionSize2() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer size= new Integer( 0 );
        set.setSize2( size );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        boolean attr= attributes.isRequestSize2();

        assertTrue( "coap.opt.size2: wrong value", attr );
    }

    @Test
    public void testOptionObserve() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer seqnum= new Integer( 120 );
        set.setObserve( seqnum );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        Integer attr= attributes.getObserve();

        assertEquals( "coap.opt.observe: wrong value", seqnum, attr );
    }

    @Test
    public void testOptionOther() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        byte[][] values= {
            { (byte) 0x01, (byte) 0x02, (byte) 0x03 },
            { (byte) 0x01, (byte) 0x02, (byte) 0x04 },
            { (byte) 0x01, (byte) 0x02, (byte) 0x05 },
            { (byte) 0xff, (byte) 0x02, (byte) 0x05 } };

        Integer[] optionNrs= { 65000 | 0x01, 65000 | 0x02, 65000 | 0x1c, 65000 | 0x1d };

        ArrayList< DefaultOtherOptionAttribute > expected= new ArrayList<>();
        for ( int i= 0; i < 4; i++ )
        {
            expected.add( new DefaultOtherOptionAttribute( optionNrs[i], values[i].clone() ) );
            set.addOption( new Option( optionNrs[i], values[i].clone() ) );
        }

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );
        List< ? extends OtherOptionAttribute > options= attributes.getOtherOptions();

        assertEquals( "coap.opt.other has wrong length", expected.size(), options.size() );
        for ( int i= 0; i < 4; i++ )
        {
            assertEquals( "coap.opt.other has wrong number", expected.get( i ).getOptionNumber(), options.get( i ).getOptionNumber() );
            assertArrayEquals( "coap.opt.other has wrong value", expected.get( i ).getOptionValue(), options.get( i ).getOptionValue() );
        }
    }
}
