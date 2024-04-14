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
package nl.teslanet.mule.connectors.coap.test.server.properties;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map.Entry;

import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.option.OpaqueOptionDefinition;
import org.junit.Test;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.core.api.util.IOUtils;

import nl.teslanet.mule.connectors.coap.api.entity.EntityTag;
import nl.teslanet.mule.connectors.coap.api.entity.EntityTagException;
import nl.teslanet.mule.connectors.coap.api.options.OtherOptionAttribute;
import nl.teslanet.mule.connectors.coap.api.query.QueryParamAttribute;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultRequestOptionsAttributes;
import nl.teslanet.mule.connectors.coap.test.utils.TestOptions;


/**
 * Test option classes
 *
 */
public class DefaultRequestOptionsAttributesTest
{
    @Test
    public void testOptionSetifExists() throws InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        byte[] etagValue1= {};
        set.addIfMatch( etagValue1.clone() );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        List< EntityTag > list= attributes.getIfMatch();
        boolean ifExists= attributes.isIfExists();

        assertNull( list );
        assertTrue( "coap.opt.if_match.any: wrong value", ifExists );
    }

    @Test
    public void testOptionSetifExistsMultiple() throws InternalInvalidOptionValueException, EntityTagException
    {
        OptionSet set= new OptionSet();
        byte[] etagValue1= {};
        byte[] etagValue2= { (byte) 0x11, (byte) 0xFF };
        byte[] etagValue3= {};

        set.addIfMatch( etagValue1.clone() );
        set.addIfMatch( etagValue2.clone() );
        set.addIfMatch( etagValue3.clone() );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        List< EntityTag > list= attributes.getIfMatch();
        boolean ifExists= attributes.isIfExists();

        assertNotNull( list );
        assertTrue( "coap.opt.if_match.any: wrong value", ifExists );
        assertEquals( "coap.opt.if_match.etags: wrong number of etags", 1, list.size() );
        assertFalse( "coap.opt.if_match.etags: missing etag", list.contains( new EntityTag( etagValue1 ) ) );
        assertTrue( "coap.opt.if_match.etags: missing etag", list.contains( new EntityTag( etagValue2 ) ) );
        assertFalse( "coap.opt.if_match.etags: etag not expected", list.contains( new EntityTag( etagValue3 ) ) );
    }

    @Test
    public void testOptionSetIfMatch() throws InternalInvalidOptionValueException, EntityTagException
    {
        OptionSet set= new OptionSet();
        byte[] etagValue1= { (byte) 0x00, (byte) 0xFF };
        byte[] etagValue2= { (byte) 0x11, (byte) 0xFF };
        set.addIfMatch( etagValue1.clone() );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        List< EntityTag > list= attributes.getIfMatch();

        assertNotNull( list );
        assertEquals( "coap.opt.if_match.etags: wrong number of etags", 1, list.size() );
        assertTrue( "coap.opt.if_match.etags: missing etag", list.contains( new EntityTag( etagValue1 ) ) );
        assertFalse( "coap.opt.if_match.etags: etag not expected", list.contains( new EntityTag( etagValue2 ) ) );
    }

    @Test
    public void testOptionSetIfMatchMultiple() throws InternalInvalidOptionValueException, EntityTagException
    {
        OptionSet set= new OptionSet();
        byte[] etagValue1= { (byte) 0x00, (byte) 0xFF };
        byte[] etagValue2= { (byte) 0x11, (byte) 0xFF };
        byte[] etagValue3= { (byte) 0x22, (byte) 0xFF };

        set.addIfMatch( etagValue1.clone() );
        set.addIfMatch( etagValue2.clone() );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        List< EntityTag > list= attributes.getIfMatch();

        assertNotNull( list );
        assertEquals( "coap.opt.if_match.etags: wrong number of etags", 2, list.size() );
        assertTrue( "coap.opt.if_match.etags: missing etag", list.contains( new EntityTag( etagValue1 ) ) );
        assertTrue( "coap.opt.if_match.etags: missing etag", list.contains( new EntityTag( etagValue2 ) ) );
        assertFalse( "coap.opt.if_match.etags: etag not expected", list.contains( new EntityTag( etagValue3 ) ) );
    }

    @Test
    public void testOptionUrihost() throws InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String host= "testhost";
        set.setUriHost( host );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        String attr= attributes.getUriHost();

        assertEquals( "coap.opt.uri_host: wrong value", host, attr );
    }

    @Test
    public void testOptionETagMultiple() throws InternalInvalidOptionValueException, EntityTagException
    {
        OptionSet set= new OptionSet();
        byte[] etagValue1= { (byte) 0x00, (byte) 0xFF };
        byte[] etagValue2= { (byte) 0x11, (byte) 0xFF };
        byte[] etagValue3= { (byte) 0x22, (byte) 0xFF };

        set.addETag( etagValue1.clone() );
        set.addETag( etagValue2.clone() );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        List< EntityTag > list= attributes.getEtags();

        assertNotNull( list );
        assertEquals( "coap.opt.etag.list: wrong number of etags", 2, list.size() );
        assertTrue( "coap.opt.etag.list: missing etag", list.contains( new EntityTag( etagValue1 ) ) );
        assertTrue( "coap.opt.etag.list: missing etag", list.contains( new EntityTag( etagValue2 ) ) );
        assertFalse( "coap.opt.etag.list: etag not expected", list.contains( new EntityTag( etagValue3 ) ) );
    }

    @Test
    public void testOptionIfNoneMatch() throws InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        set.setIfNoneMatch( Boolean.TRUE );
        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        boolean attr= attributes.isIfNoneMatch();

        assertTrue( "coap.opt.if_none_match: wrong value", attr );

        set.setIfNoneMatch( Boolean.FALSE );
        attributes= new DefaultRequestOptionsAttributes( set );

        attr= attributes.isIfNoneMatch();

        assertFalse( "coap.opt.if_none_match: wrong value", attr );
    }

    @Test
    public void testOptionUriPort() throws InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer port= 5536;
        set.setUriPort( port );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        Integer attr= attributes.getUriPort();

        assertEquals( "coap.opt.uri_port: wrong value", port, attr );
    }

    @Test
    public void testOptionSetUriPath() throws InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String value1= "this";
        String value2= "is";
        String value3= "some path";

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
    public void testOptionContentFormat() throws InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer format= 41;
        set.setContentFormat( format );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        Integer attr= attributes.getContentFormat();

        assertEquals( "coap.opt.content_format: wrong value", format, attr );
    }

    @Test
    public void testOptionSetUriQuery() throws InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String[] keys= { "this", "is", "some", "some" };
        String[] values= { null, "really", "locationquery", "also" };
        for ( int i= 0; i < keys.length; i++ )
        {
            set.addUriQuery( keys[i] + ( values[i] == null ? "" : "=" + values[i] ) );
        }

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        List< QueryParamAttribute > query= attributes.getUriQuery();

        int index= 0;
        assertEquals( "coap.opt.uri_query.list: wrong size", keys.length, query.size() );
        for ( QueryParamAttribute param : query )
        {
            assertEquals( "coap.opt.uri_query.list: wrong key", keys[index], param.getKey() );
            assertEquals( "coap.opt.uri_query.list: wrong key", values[index], param.getValue() );
            index++;
        }
    }

    @Test
    public void testOptionAccept() throws InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer format= Integer.valueOf( 41 );
        set.setAccept( format );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        Integer attr= attributes.getAccept();

        assertEquals( "coap.opt.accept: wrong value", format, attr );
    }

    @Test
    public void testOptionSize2() throws InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer size= Integer.valueOf( 0 );
        set.setSize2( size );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        boolean attr= attributes.isProvideResponseSize();

        assertTrue( "coap.opt.size2: wrong value", attr );
    }

    @Test
    public void testOptionProxyUri() throws InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String uri= "testproxyuri";
        set.setProxyUri( uri );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        String attr= attributes.getProxyUri();

        assertEquals( "coap.opt.proxy_uri: wrong value", uri, attr );
    }

    @Test
    public void testOptionProxyScheme() throws InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String scheme= "testproxyscheme";
        set.setProxyScheme( scheme );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        String attr= attributes.getProxyScheme();

        assertEquals( "coap.opt.proxy_scheme: wrong value", scheme, attr );
    }

    @Test
    public void testOptionSize1() throws InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer size= Integer.valueOf( 120 );
        set.setSize1( size );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        Integer attr= attributes.getRquestSize();

        assertEquals( "coap.opt.size1: wrong value", size, attr );
    }

    @Test
    public void testOptionObserve() throws InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer seqnum= Integer.valueOf( 120 );
        set.setObserve( seqnum );

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );

        Integer attr= attributes.getObserve();

        assertEquals( "coap.opt.observe: wrong value", seqnum, attr );
    }

    @Test
    public void testOptionOther() throws InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        byte[][] values= {
            { (byte) 0x01, (byte) 0x02, (byte) 0x03 },
            { (byte) 0x01, (byte) 0x02, (byte) 0x04 },
            { (byte) 0x01, (byte) 0x02, (byte) 0x05 },
            { (byte) 0xff, (byte) 0x02, (byte) 0x05 } };

        OpaqueOptionDefinition[] optionsDefs= { TestOptions.OTHER_OPTION_65001, TestOptions.OTHER_OPTION_65002, TestOptions.OTHER_OPTION_65028, TestOptions.OTHER_OPTION_65029 };

        for ( int i= 0; i < 4; i++ )
        {
            set.addOption( new Option( optionsDefs[i], values[i].clone() ) );
        }

        DefaultRequestOptionsAttributes attributes= new DefaultRequestOptionsAttributes( set );
        MultiMap< String, OtherOptionAttribute > options= attributes.getOther();

        assertEquals( "coap.opt.other has wrong length", 4, options.size() );
        int i= 0;
        for ( Entry< String, OtherOptionAttribute > entry : options.entryList() )
        {
            assertEquals( "coap.opt.other has wrong number", optionsDefs[i].getNumber(), entry.getValue().getNumber() );
            assertArrayEquals( "coap.opt.other has wrong value", values[i], IOUtils.toByteArray( entry.getValue().getValue() ) );
            i++;
        }
    }
}
