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
package nl.teslanet.mule.connectors.coap.test.modules.options;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;
import nl.teslanet.mule.connectors.coap.api.options.BlockValue;
import nl.teslanet.mule.connectors.coap.api.options.ETag;
import nl.teslanet.mule.connectors.coap.api.options.OptionAttributes;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.options.CoAPOptions;


/**
 * Test option classes
 *
 */
public class OptionSetToAttributesTest
{
    @Test
    public void testConstructorDefault()
    {
        OptionAttributes options= null;

        options= new OptionAttributes();

        assertNotNull( "Options default contruction failed", options );
    }

    @Test
    public void testOptionSetIfMatch() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        byte[] etagValue1= { (byte) 0x00, (byte) 0xFF };
        byte[] etagValue2= { (byte) 0x11, (byte) 0xFF };
        set.addIfMatch( etagValue1.clone() );

        OptionAttributes attributes= new OptionAttributes();

        CoAPOptions.copyOptions( set, attributes );

        List< ETag > list= attributes.getIfMatchList();

        assertNotNull( list );
        assertEquals( "coap.opt.if_match.list: wrong number of etags", 1, list.size() );
        assertTrue( "coap.opt.if_match.list: missing etag", list.contains( new ETag( etagValue1 ) ) );
        assertFalse( "coap.opt.if_match.list: etag not expected", list.contains( new ETag( etagValue2 ) ) );
    }

    @Test
    public void testOptionSetIfMatchMultiple() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        byte[] etagValue1= { (byte) 0x00, (byte) 0xFF };
        byte[] etagValue2= { (byte) 0x11, (byte) 0xFF };
        byte[] etagValue3= { (byte) 0x22, (byte) 0xFF };

        set.addIfMatch( new ETag( etagValue1 ).getBytes() );
        set.addIfMatch( new ETag( etagValue2 ).getBytes() );

        OptionAttributes attributes= new OptionAttributes();

        CoAPOptions.copyOptions( set, attributes );

        List< ETag > list= attributes.getIfMatchList();

        assertNotNull( list );
        assertEquals( "coap.opt.if_match.list: wrong number of etags", 2, list.size() );
        assertTrue( "coap.opt.if_match.list: missing etag", list.contains( new ETag( etagValue1 ) ) );
        assertTrue( "coap.opt.if_match.list: missing etag", list.contains( new ETag( etagValue2 ) ) );
        assertFalse( "coap.opt.if_match.list: etag not expected", list.contains( new ETag( etagValue3 ) ) );
    }

    @Test
    public void testOptionUrihost() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String host= "testhost";
        set.setUriHost( host );

        OptionAttributes attributes= new OptionAttributes();

        CoAPOptions.copyOptions( set, attributes );

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

        OptionAttributes attributes= new OptionAttributes();

        CoAPOptions.copyOptions( set, attributes );

        List< ETag > list= attributes.getEtagList();

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
        OptionAttributes attributes= new OptionAttributes();
        CoAPOptions.copyOptions( set, attributes );
        boolean attr= attributes.isIfNoneMatch();

        assertTrue( "coap.opt.if_none_match: wrong value", attr );

        set.setIfNoneMatch( new Boolean( false ) );
        attributes= new OptionAttributes();
        CoAPOptions.copyOptions( set, attributes );
        attr= attributes.isIfNoneMatch();

        assertFalse( "coap.opt.if_none_match: wrong value", attr );
    }

    @Test
    public void testOptionUriPort() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer port= 5536;
        set.setUriPort( port );

        OptionAttributes attributes= new OptionAttributes();
        CoAPOptions.copyOptions( set, attributes );
        Integer attr= attributes.getUriPort();

        assertEquals( "coap.opt.uri_port: wrong value", port, attr );
    }

    @Test
    public void testOptionSetLocationPath() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String value1= "this";
        String value2= "is";
        String value3= "some location";
        //String total= "this/is";

        set.addLocationPath( value1 );
        set.addLocationPath( value2 );

        OptionAttributes attributes= new OptionAttributes();
        CoAPOptions.copyOptions( set, attributes );
        List< String > list= attributes.getLocationPathList();

        assertNotNull( list );
        assertEquals( "coap.opt.location_path.list: wrong number of path segment", 2, list.size() );
        assertTrue( "coap.opt.location_path.list: missing path segment", list.contains( value1 ) );
        assertTrue( "coap.opt.location_path.list: missing path segment", list.contains( value2 ) );
        assertFalse( "coap.opt.location_path.list: path segment not expected", list.contains( value3 ) );
        //TODO assertEquals( "coap.opt.location_path: wrong path", total, props.get( "coap.opt.location_path" ) );

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

        OptionAttributes attributes= new OptionAttributes();
        CoAPOptions.copyOptions( set, attributes );
        List< String > list= attributes.getUriPathList();

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

        OptionAttributes attributes= new OptionAttributes();
        CoAPOptions.copyOptions( set, attributes );
        Integer attr= attributes.getContentFormat();

        assertEquals( "coap.opt.content_format: wrong value", format, attr );
    }

    @Test
    public void testOptionMaxAge() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Long maxage= new Long( 120 );
        set.setMaxAge( maxage );

        OptionAttributes attributes= new OptionAttributes();
        CoAPOptions.copyOptions( set, attributes );
        Long attr= attributes.getMaxAge();

        assertEquals( "coap.opt.max_age: wrong value", maxage, attr );
    }

    @Test
    public void testOptionSetUriQuery() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String value1= "this";
        String value2= "is";
        String value3= "some=query";
        //String total= "this&is";

        set.addUriQuery( value1 );
        set.addUriQuery( value2 );

        OptionAttributes attributes= new OptionAttributes();
        CoAPOptions.copyOptions( set, attributes );
        List< String > list= attributes.getUriQueryList();

        assertNotNull( list );
        assertEquals( "coap.opt.uri_query.list: wrong number of query segment", 2, list.size() );
        assertEquals( "coap.opt.uri_query.list: missing query segment", value1, list.get( 0 ) );
        assertEquals( "coap.opt.uri_query.list: missing query segment", value2, list.get( 1 ) );
        assertFalse( "coap.opt.uri_query.list: query segment not expected", list.contains( value3 ) );
        //TODO assertEquals( "coap.opt.uri_query: wrong query", total, props.get( "coap.opt.uri_query" ) );

    }

    @Test
    public void testOptionAccept() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer format= new Integer( 41 );
        set.setAccept( format );

        OptionAttributes attributes= new OptionAttributes();
        CoAPOptions.copyOptions( set, attributes );
        Integer attr= attributes.getAccept();

        assertEquals( "coap.opt.accept: wrong value", format, attr );
    }

    @Test
    public void testOptionLocationQuery() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String value1= "this";
        String value2= "is";
        String value3= "some=locationquery";
        //String total= "this&is";

        set.addLocationQuery( value1 );
        set.addLocationQuery( value2 );

        OptionAttributes attributes= new OptionAttributes();
        CoAPOptions.copyOptions( set, attributes );
        List< String > list= attributes.getLocationQueryList();

        assertNotNull( list );
        assertEquals( "coap.opt.location_query.list: wrong number of query segment", 2, list.size() );
        assertEquals( "coap.opt.location_query.list: missing query segment", value1, list.get( 0 ) );
        assertEquals( "coap.opt.location_query.list: missing query segment", value2, list.get( 1 ) );
        assertFalse( "coap.opt.location_query.list: query segment not expected", list.contains( value3 ) );
        //TODO assertEquals( "coap.opt.location_query: wrong query", total, props.get( "coap.opt.location_query" ) );
    }

    @Test
    public void testOptionProxyUri() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String uri= "testproxyuri";
        set.setProxyUri( uri );

        OptionAttributes attributes= new OptionAttributes();
        CoAPOptions.copyOptions( set, attributes );
        String attr= attributes.getProxyUri();

        assertEquals( "coap.opt.proxy_uri: wrong value", uri, attr );
    }

    @Test
    public void testOptionProxyScheme() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String scheme= "testproxyscheme";
        set.setProxyScheme( scheme );

        OptionAttributes attributes= new OptionAttributes();
        CoAPOptions.copyOptions( set, attributes );
        String attr= attributes.getProxyScheme();

        assertEquals( "coap.opt.proxy_scheme: wrong value", scheme, attr );
    }

    @Test
    public void testOptionBlock1() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        int szx= 3;
        int size= 128;
        boolean m= true;
        int num= 3;

        set.setBlock1( szx, m, num );

        OptionAttributes attributes= new OptionAttributes();
        CoAPOptions.copyOptions( set, attributes );
        BlockValue attr= attributes.getBlock1();

        assertEquals( "coap.opt.block1.szx: wrong value", szx, attr.getSzx() );
        assertEquals( "coap.opt.block1.size: wrong value", size, attr.getSize() );
        assertEquals( "coap.opt.block1.num", num, attr.getNum() );
        assertTrue( "coap.opt.block1.m: wrong value", attr.isM() );
    }

    @Test
    public void testOptionBlock2() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        int szx= 3;
        int size= 128;
        boolean m= true;
        int num= 3;

        set.setBlock2( szx, m, num );

        OptionAttributes attributes= new OptionAttributes();
        CoAPOptions.copyOptions( set, attributes );
        BlockValue attr= attributes.getBlock2();

        assertEquals( "coap.opt.block2.szx: wrong value", szx, attr.getSzx() );
        assertEquals( "coap.opt.block2.size: wrong value", size, attr.getSize() );
        assertEquals( "coap.opt.block2.num", num, attr.getNum() );
        assertTrue( "coap.opt.block2.m: wrong value", attr.isM() );
    }

    @Test
    public void testOptionSize1() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer size= new Integer( 120 );
        set.setSize1( size );

        OptionAttributes attributes= new OptionAttributes();
        CoAPOptions.copyOptions( set, attributes );
        Integer attr= attributes.getSize1();

        assertEquals( "coap.opt.size1: wrong value", size, attr );
    }

    @Test
    public void testOptionSize2() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer size= new Integer( 120 );
        set.setSize2( size );

        OptionAttributes attributes= new OptionAttributes();
        CoAPOptions.copyOptions( set, attributes );
        Integer attr= attributes.getSize2();

        assertEquals( "coap.opt.size2: wrong value", size, attr );
    }

    @Test
    public void testOptionObserve() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer seqnum= new Integer( 120 );
        set.setObserve( seqnum );

        OptionAttributes attributes= new OptionAttributes();
        CoAPOptions.copyOptions( set, attributes );
        Integer attr= attributes.getObserve();

        assertEquals( "coap.opt.observe: wrong value", seqnum, attr );
    }

    @Test
    public void testOptionOther() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        byte[] value1= { (byte) 0x01, (byte) 0x02, (byte) 0x03 };
        byte[] value2= { (byte) 0x01, (byte) 0x02, (byte) 0x04 };
        byte[] value3= { (byte) 0x01, (byte) 0x02, (byte) 0x05 };
        byte[] value4= { (byte) 0xff, (byte) 0x02, (byte) 0x05 };

        Integer optionNr1= 65000 | 0x01;
        Integer optionNr2= 65000 | 0x02;
        Integer optionNr3= 65000 | 0x1c;
        Integer optionNr4= 65000 | 0x1d;

        set.addOption( new Option( optionNr1, value1.clone() ) );
        set.addOption( new Option( optionNr2, value2.clone() ) );
        set.addOption( new Option( optionNr3, value3.clone() ) );
        set.addOption( new Option( optionNr4, value4.clone() ) );

        OptionAttributes attributes= new OptionAttributes();
        CoAPOptions.copyOptions( set, attributes );
        byte[] attr1= (byte[]) attributes.getOtherOptions().get( optionNr1.toString() );
        byte[] attr2= (byte[]) attributes.getOtherOptions().get( optionNr2.toString() );
        byte[] attr3= (byte[]) attributes.getOtherOptions().get( optionNr3.toString() );
        byte[] attr4= (byte[]) attributes.getOtherOptions().get( optionNr4.toString() );

        assertArrayEquals( "coap.opt.other." + optionNr1.toString() + ": wrong value", value1, attr1 );
        //        assertEquals( "coap.opt.other." + optionNr1.toString() + ".critical: wrong value", true, props.get( "coap.opt.other." + optionNr1.toString() + ".critical" ) );
        //        assertEquals( "coap.opt.other." + optionNr1.toString() + ".unsafe: wrong value", false, props.get( "coap.opt.other." + optionNr1.toString() + ".unsafe" ) );
        //        assertEquals( "coap.opt.other." + optionNr1.toString() + ".no_cache_key: wrong value", false, props.get( "coap.opt.other." + optionNr1.toString() + ".no_cache_key" ) );

        assertArrayEquals( "coap.opt.other." + optionNr2.toString() + ": wrong value", value2, attr2 );
        //        assertEquals( "coap.opt.other." + optionNr2.toString() + ".critical: wrong value", false, props.get( "coap.opt.other." + optionNr2.toString() + ".critical" ) );
        //        assertEquals( "coap.opt.other." + optionNr2.toString() + ".unsafe: wrong value", true, props.get( "coap.opt.other." + optionNr2.toString() + ".unsafe" ) );
        //        assertEquals( "coap.opt.other." + optionNr2.toString() + ".no_cache_key: wrong value", false, props.get( "coap.opt.other." + optionNr2.toString() + ".no_cache_key" ) );

        assertArrayEquals( "coap.opt.other." + optionNr3.toString() + ": wrong value", value3, attr3 );
        //        assertEquals( "coap.opt.other." + optionNr3.toString() + ".critical: wrong value", false, props.get( "coap.opt.other." + optionNr3.toString() + ".critical" ) );
        //        assertEquals( "coap.opt.other." + optionNr3.toString() + ".unsafe: wrong value", false, props.get( "coap.opt.other." + optionNr3.toString() + ".unsafe" ) );
        //        assertEquals( "coap.opt.other." + optionNr3.toString() + ".no_cache_key: wrong value", true, props.get( "coap.opt.other." + optionNr3.toString() + ".no_cache_key" ) );

        assertArrayEquals( "coap.opt.other." + optionNr4.toString() + ": wrong value", value4, attr4 );
        //        assertEquals( "coap.opt.other." + optionNr4.toString() + ".critical: wrong value", true, props.get( "coap.opt.other." + optionNr4.toString() + ".critical" ) );
        //        assertEquals( "coap.opt.other." + optionNr4.toString() + ".unsafe: wrong value", false, props.get( "coap.opt.other." + optionNr4.toString() + ".unsafe" ) );
        //        assertEquals( "coap.opt.other." + optionNr4.toString() + ".no_cache_key: wrong value", true, props.get( "coap.opt.other." + optionNr4.toString() + ".no_cache_key" ) );
    }
}
