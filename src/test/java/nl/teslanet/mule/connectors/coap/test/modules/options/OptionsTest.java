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
package nl.teslanet.mule.connectors.coap.test.modules.options;


import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.api.error.InvalidETagException;
import nl.teslanet.mule.connectors.coap.api.options.ETag;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidByteArrayValueException;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.options.CoAPOptions;

import org.eclipse.californium.core.coap.BlockOption;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;


/**
 * Test option classes
 *
 */
public class OptionsTest
{
    @Test
    public void testConstructorDefault()
    {
        CoAPOptions options= null;

        options= new CoAPOptions();

        assertNotNull( "Options default contruction failed", options );
    }

    @Test
    public void testConstructorOptionSet()
    {
        OptionSet optionSet= new OptionSet();

        CoAPOptions options= null;
        options= new CoAPOptions( optionSet );

        assertNotNull( "Options contruction with optionset failed", options );
        assertTrue( "Options contuction failed to deliver the same optionSet", options.getOptionSet() == optionSet );
    }

    @Test
    public void testConstructorEmptyProperties() throws InternalInvalidByteArrayValueException
    {
        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions options= null;
        options= new CoAPOptions( props );

        assertNotNull( "Options contruction with empty property map failed", options );
    }

    @Test
    public void testConstructorOneProperty() throws InternalInvalidByteArrayValueException
    {
        HashMap< String, Object > props= new HashMap< String, Object >();
        props.put( "coap.opt.observe", 123 );

        CoAPOptions options= null;
        options= new CoAPOptions( props );

        assertNotNull( "Options contruction with empty property map failed", options );
        assertEquals( "Options contruction with one property failed", new Integer( 123 ), options.getOptionSet().getObserve() );
    }

    @Test
    public void testSetGetOptionSet()
    {
        OptionSet optionSet= new OptionSet();

        CoAPOptions options= new CoAPOptions();
        options.setOptionSet( optionSet );

        assertNotNull( "Options contruction with optionset failed", options );
        assertTrue( "Options getter and setter failed to deliver the same optionSet", options.getOptionSet() == optionSet );
    }

    @Test
    public void testSetOptionSet()
    {
        OptionSet optionSet= new OptionSet();

        CoAPOptions options= new CoAPOptions();
        options.setOptionSet( optionSet );

        assertNotNull( "Options contruction with optionset failed", options );
        assertTrue( "Options getter and setter failed to deliver the same optionSet", options.getOptionSet() == optionSet );
    }

    @Test
    public void testLongProperty() throws InternalInvalidByteArrayValueException
    {
        HashMap< String, Object > props= new HashMap< String, Object >();
        Long value= new Long( 45 );
        props.put( "coap.opt.observe", value );

        CoAPOptions options= new CoAPOptions( props );
        options.getOptionSet().getObserve();

        assertEquals( "Options contruction with empty property map failed", new Integer( 45 ), options.getOptionSet().getObserve() );
    }

    @Test
    public void testIntegerProperty() throws InternalInvalidByteArrayValueException
    {
        HashMap< String, Object > props= new HashMap< String, Object >();
        Integer value= new Integer( 45 );
        props.put( "coap.opt.observe", value );

        CoAPOptions options= new CoAPOptions( props );
        options.getOptionSet().getObserve();

        assertEquals( "Options contruction with empty property map failed", new Integer( 45 ), options.getOptionSet().getObserve() );
    }

    @Test
    public void testStringProperty() throws InternalInvalidByteArrayValueException
    {
        HashMap< String, Object > props= new HashMap< String, Object >();
        String value= new String( "45" );
        props.put( "coap.opt.observe", value );

        CoAPOptions options= new CoAPOptions( props );
        options.getOptionSet().getObserve();

        assertEquals( "Options contruction with empty property map failed", new Integer( 45 ), options.getOptionSet().getObserve() );
    }

    @Test
    public void testOptionSetIfMatch() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        byte[] etagValue1= { (byte) 0x00, (byte) 0xFF };
        byte[] etagValue2= { (byte) 0x11, (byte) 0xFF };
        set.addIfMatch( etagValue1.clone() );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        @SuppressWarnings("unchecked")
        List< ETag > list= (List< ETag >) props.get( "coap.opt.if_match.list" );

        assertNotNull( list );
        assertEquals( "coap.opt.if_match.list: wrong number of etags", 1, list.size() );
        assertTrue( "coap.opt.if_match.list: missing etag", list.contains( new ETag( etagValue1 ) ) );
        assertFalse( "coap.opt.if_match.list: etag not expected", list.contains( new ETag( etagValue2 ) ) );
    }

    @Test
    public void testMapIfMatch() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        byte[] etagValue1= { (byte) 0x00, (byte) 0xFF };
        byte[] etagValue2= { (byte) 0x11, (byte) 0xFF };

        HashMap< String, Object > props= new HashMap< String, Object >();
        props.put( "coap.opt.if_match.list", new ETag( etagValue1 ) );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "if_match option has wrong count", 1, set.getIfMatchCount() );

        List< ETag > list= ETag.getList( set.getIfMatch() );
        assertEquals( "coap.opt.if_match.list: wrong number of etags", 1, list.size() );
        assertTrue( "coap.opt.if_match.list: missing etag", list.contains( new ETag( etagValue1 ) ) );
        assertFalse( "coap.opt.if_match.list: etag not expected", list.contains( new ETag( etagValue2 ) ) );

        props.clear();
        props.put( "coap.opt.if_match.list", etagValue1 );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "if_match option has wrong count", 1, set.getIfMatchCount() );

        list= ETag.getList( set.getIfMatch() );
        assertEquals( "coap.opt.if_match.list: wrong number of etags", 1, list.size() );
        assertTrue( "coap.opt.if_match.list: missing etag", list.contains( new ETag( etagValue1 ) ) );
        assertFalse( "coap.opt.if_match.list: etag not expected", list.contains( new ETag( etagValue2 ) ) );
    }

    @Test
    public void testMapIfMatchInvalid() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        byte[] etagValue1= { (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09 };

        HashMap< String, Object > props= new HashMap< String, Object >();
        props.put( "coap.opt.if_match.list", ( etagValue1 ) );
        OptionSet set= new OptionSet();

        IllegalArgumentException e= assertThrows( IllegalArgumentException.class, () -> {
            CoAPOptions.fillOptionSet( set, props, true );
        } );
        assertTrue( "exception has wrong message", e.getMessage().contains( "If-Match" ) );
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

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        @SuppressWarnings("unchecked")
        List< ETag > list= (List< ETag >) props.get( "coap.opt.if_match.list" );

        assertNotNull( list );
        assertEquals( "coap.opt.if_match.list: wrong number of etags", 2, list.size() );
        assertTrue( "coap.opt.if_match.list: missing etag", list.contains( new ETag( etagValue1 ) ) );
        assertTrue( "coap.opt.if_match.list: missing etag", list.contains( new ETag( etagValue2 ) ) );
        assertFalse( "coap.opt.if_match.list: etag not expected", list.contains( new ETag( etagValue3 ) ) );
    }

    @Test
    public void testMapIfMatchMultiple() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        byte[] etagValue1= { (byte) 0x00, (byte) 0xFF };
        byte[] etagValue2= { (byte) 0x11, (byte) 0xFF };
        byte[] etagValue3= { (byte) 0x22, (byte) 0xFF };

        HashMap< String, Object > props= new HashMap< String, Object >();
        LinkedList< ETag > list= new LinkedList< ETag >();
        list.add( new ETag( etagValue1 ) );
        list.add( new ETag( etagValue2 ) );
        props.put( "coap.opt.if_match.list", list );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "if_match option has wrong count", 2, set.getIfMatchCount() );

        List< ETag > etagslist= ETag.getList( set.getIfMatch() );
        assertTrue( "if_match option missing", etagslist.contains( new ETag( etagValue1 ) ) );
        assertTrue( "if_match option missing", etagslist.contains( new ETag( etagValue2 ) ) );

        props.clear();
        LinkedList< byte[] > bytelist= new LinkedList< byte[] >();
        bytelist.add( etagValue1 );
        bytelist.add( etagValue2 );
        props.put( "coap.opt.if_match.list", list );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "if_match option has wrong count", 2, set.getIfMatchCount() );

        etagslist= ETag.getList( set.getIfMatch() );
        assertEquals( "coap.opt.if_match.list: wrong number of etags", 2, etagslist.size() );
        assertTrue( "coap.opt.if_match.list: missing etag", etagslist.contains( new ETag( etagValue1 ) ) );
        assertTrue( "coap.opt.if_match.list: missing etag", etagslist.contains( new ETag( etagValue2 ) ) );
        assertFalse( "coap.opt.if_match.list: etag not expected", etagslist.contains( new ETag( etagValue3 ) ) );
    }

    @Test
    public void testOptionUrihost() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String host= "testhost";
        set.setUriHost( host );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        assertEquals( "coap.opt.uri_host: wrong value", host, props.get( "coap.opt.uri_host" ) );
    }

    @Test
    public void testMapUrihost() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        String host= "testhost";

        HashMap< String, Object > props= new HashMap< String, Object >();
        props.put( "coap.opt.uri_host", host );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.uri_host: wrong value", host, set.getUriHost() );

        props.clear();
        props.put( "coap.opt.uri_host", new StringWrapper( host ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.uri_host: wrong value", host, set.getUriHost() );
    }

    @Test
    public void testOptionSetETag() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        byte[] etagValue1= { (byte) 0x00, (byte) 0xFF };
        byte[] etagValue2= { (byte) 0x11, (byte) 0xFF };
        set.addETag( etagValue1.clone() );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        @SuppressWarnings("unchecked")
        List< ETag > list= (List< ETag >) props.get( "coap.opt.etag.list" );

        assertNotNull( list );
        assertEquals( "coap.opt.etag.list: wrong number of etags", 1, list.size() );
        assertTrue( "coap.opt.etag.list: missing etag", list.contains( new ETag( etagValue1 ) ) );
        assertFalse( "coap.opt.etag.list: etag not expected", list.contains( new ETag( etagValue2 ) ) );
    }

    @Test
    public void testMapETag() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        byte[] etagValue1= { (byte) 0x00, (byte) 0xFF };
        byte[] etagValue2= { (byte) 0x11, (byte) 0xFF };

        HashMap< String, Object > props= new HashMap< String, Object >();
        props.put( "coap.opt.etag.list", new ETag( etagValue1 ) );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "etag option has wrong count", set.getETagCount(), 1 );

        List< ETag > list= ETag.getList( set.getETags() );
        assertEquals( "coap.opt.etag.list: wrong number of etags", 1, list.size() );
        assertTrue( "coap.opt.etag.list: missing etag", list.contains( new ETag( etagValue1 ) ) );
        assertFalse( "coap.opt.etag.list: etag not expected", list.contains( new ETag( etagValue2 ) ) );

        props.clear();
        props.put( "coap.opt.etag.list", etagValue1.clone() );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "etag option has wrong count", 1, set.getETagCount() );

        list= ETag.getList( set.getETags() );
        assertEquals( "coap.opt.etag.list: wrong number of etags", 1, list.size() );
        assertTrue( "coap.opt.etag.list: missing etag", list.contains( new ETag( etagValue1 ) ) );
        assertFalse( "coap.opt.etag.list: etag not expected", list.contains( new ETag( etagValue2 ) ) );
    }

    @Test
    public void testMapETagInvalid() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        byte[] etagValue1= { (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09 };

        HashMap< String, Object > props= new HashMap< String, Object >();
        props.put( "coap.opt.etag.list", etagValue1 );

        // TODO: ProxyHttp uses ETags that are larger than 8 bytes (20).
        //exception.expect( IllegalArgumentException.class );
        //exception.expectMessage( "ETag" );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

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

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        @SuppressWarnings("unchecked")
        List< ETag > list= (List< ETag >) props.get( "coap.opt.etag.list" );

        assertNotNull( list );
        assertEquals( "coap.opt.etag.list: wrong number of etags", 2, list.size() );
        assertTrue( "coap.opt.etag.list: missing etag", list.contains( new ETag( etagValue1 ) ) );
        assertTrue( "coap.opt.etag.list: missing etag", list.contains( new ETag( etagValue2 ) ) );
        assertFalse( "coap.opt.etag.list: etag not expected", list.contains( new ETag( etagValue3 ) ) );
    }

    @Test
    public void testMapETagMultiple() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        byte[] etagValue1= { (byte) 0x00, (byte) 0xFF };
        byte[] etagValue2= { (byte) 0x11, (byte) 0xFF };
        byte[] etagValue3= { (byte) 0x22, (byte) 0xFF };

        HashMap< String, Object > props= new HashMap< String, Object >();
        LinkedList< ETag > list= new LinkedList< ETag >();
        list.add( new ETag( etagValue1 ) );
        list.add( new ETag( etagValue2 ) );
        props.put( "coap.opt.etag.list", list );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "etag option has wrong count", set.getETagCount(), 2 );

        List< ETag > etagslist= ETag.getList( set.getETags() );
        assertTrue( "etag option missing", etagslist.contains( new ETag( etagValue1 ) ) );
        assertTrue( "etag option missing", etagslist.contains( new ETag( etagValue2 ) ) );

        props.clear();
        LinkedList< byte[] > bytelist= new LinkedList< byte[] >();
        bytelist.add( etagValue1.clone() );
        bytelist.add( etagValue2.clone() );
        props.put( "coap.opt.etag.list", list );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "if_match option has wrong count", set.getETagCount(), 2 );

        etagslist= ETag.getList( set.getETags() );
        assertEquals( "coap.opt.etag.list: wrong number of etags", 2, etagslist.size() );
        assertTrue( "coap.opt.etag.list: missing etag", etagslist.contains( new ETag( etagValue1 ) ) );
        assertTrue( "coap.opt.etag.list: missing etag", etagslist.contains( new ETag( etagValue2 ) ) );
        assertFalse( "coap.opt.etag.list: etag not expected", etagslist.contains( new ETag( etagValue3 ) ) );
    }

    @Test
    public void testOptionIfNoneMatch() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        set.setIfNoneMatch( new Boolean( true ) );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        assertTrue( "coap.opt.if_none_match: wrong value", (Boolean) props.get( "coap.opt.if_none_match" ) );

        set.setIfNoneMatch( new Boolean( false ) );

        props.clear();

        CoAPOptions.fillPropertyMap( set, props );

        assertFalse( "coap.opt.if_none_match: wrong value", (Boolean) props.get( "coap.opt.if_none_match" ) );
    }

    @Test
    public void testMapIfNoneMatch() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        HashMap< String, Object > props= new HashMap< String, Object >();
        props.put( "coap.opt.if_none_match", new Boolean( true ) );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertTrue( "coap.opt.if_none_match: wrong value", set.hasIfNoneMatch() );

        props.clear();
        props.put( "coap.opt.if_none_match", new StringWrapper( "true" ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertTrue( "coap.opt.if_none_match: wrong value", set.hasIfNoneMatch() );

        props.clear();
        props.put( "coap.opt.if_none_match", new Boolean( false ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertFalse( "coap.opt.if_none_match: wrong value", set.hasIfNoneMatch() );

        props.clear();
        props.put( "coap.opt.if_none_match", new StringWrapper( "false" ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertFalse( "coap.opt.if_none_match: wrong value", set.hasIfNoneMatch() );
    }

    @Test
    public void testOptionUriPort() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer port= 5536;
        set.setUriPort( port );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        assertEquals( "coap.opt.uri_port: wrong value", port, (Integer) props.get( "coap.opt.uri_port" ) );
    }

    @Test
    public void testMapUriPort() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        HashMap< String, Object > props= new HashMap< String, Object >();
        Integer port= 5336;
        props.put( "coap.opt.uri_port", port );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.uri_port: wrong value", port, set.getUriPort() );

        props.clear();
        props.put( "coap.opt.uri_port", new StringWrapper( "5337" ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.uri_port: wrong value", new Integer( 5337 ), set.getUriPort() );

    }

    @Test
    public void testOptionSetLocationPath() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String value1= "this";
        String value2= "is";
        String value3= "some location";
        String total= "this/is";

        set.addLocationPath( value1 );
        set.addLocationPath( value2 );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        @SuppressWarnings("unchecked")
        List< String > list= (List< String >) props.get( "coap.opt.location_path.list" );

        assertNotNull( list );
        assertEquals( "coap.opt.location_path.list: wrong number of path segment", 2, list.size() );
        assertTrue( "coap.opt.location_path.list: missing path segment", list.contains( value1 ) );
        assertTrue( "coap.opt.location_path.list: missing path segment", list.contains( value2 ) );
        assertFalse( "coap.opt.location_path.list: path segment not expected", list.contains( value3 ) );
        assertEquals( "coap.opt.location_path: wrong path", total, props.get( "coap.opt.location_path" ) );

    }

    @Test
    public void testMapLocationPath() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        String value1= "this";
        String value2= "is";
        String value3= "some location";
        String total= "this/is";

        HashMap< String, Object > props= new HashMap< String, Object >();
        LinkedList< String > list= new LinkedList< String >();
        list.add( value1 );
        list.add( value2 );
        props.put( "coap.opt.location_path.list", list );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.location_path.list: wrong number of path segment", 2, set.getLocationPathCount() );
        List< String > locationlist= set.getLocationPath();
        assertEquals( "coap.opt.location_path.list: missing path segment", value1, locationlist.get( 0 ) );
        assertEquals( "coap.opt.location_path.list: missing path segment", value2, locationlist.get( 1 ) );
        assertFalse( "coap.opt.location_path.list: path segment not expected", locationlist.contains( value3 ) );
        assertEquals( "coap.opt.location_path: wrong path", total, set.getLocationPathString() );

        props.clear();
        props.put( "coap.opt.location_path", total );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.location_path.list: wrong number of path segment", 2, set.getLocationPathCount() );
        locationlist= set.getLocationPath();
        assertEquals( "coap.opt.location_path.list: missing path segment", value1, locationlist.get( 0 ) );
        assertEquals( "coap.opt.location_path.list: missing path segment", value2, locationlist.get( 1 ) );
        assertFalse( "coap.opt.location_path.list: path segment not expected", locationlist.contains( value3 ) );
        assertEquals( "coap.opt.location_path: wrong path", total, set.getLocationPathString() );
    }

    @Test
    public void testOptionSetUriPath() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String value1= "this";
        String value2= "is";
        String value3= "some path";
        String total= "this/is";

        set.addUriPath( value1 );
        set.addUriPath( value2 );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        @SuppressWarnings("unchecked")
        List< String > list= (List< String >) props.get( "coap.opt.uri_path.list" );

        assertNotNull( list );
        assertEquals( "coap.opt.uri_path.list: wrong number of path segment", 2, list.size() );
        assertEquals( "coap.opt.uri_path.list: missing path segment", value1, list.get( 0 ) );
        assertEquals( "coap.opt.uri_path.list: missing path segment", value2, list.get( 1 ) );
        assertFalse( "coap.opt.uri_path.list: path segment not expected", list.contains( value3 ) );
        assertEquals( "coap.opt.uri_path: wrong path", total, props.get( "coap.opt.uri_path" ) );

    }

    @Test
    public void testMapUriPath() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        String value1= "this";
        String value2= "is";
        String value3= "some path";
        String total= "this/is";

        HashMap< String, Object > props= new HashMap< String, Object >();
        LinkedList< String > list= new LinkedList< String >();
        list.add( value1 );
        list.add( value2 );
        props.put( "coap.opt.uri_path.list", list );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.uri_path.list: wrong number of path segment", 2, set.getURIPathCount() );
        List< String > uripathlist= set.getUriPath();
        assertEquals( "coap.opt.uri_path.list: missing path segment", value1, uripathlist.get( 0 ) );
        assertEquals( "coap.opt.uri_path.list: missing path segment", value2, uripathlist.get( 1 ) );
        assertFalse( "coap.opt.uri_path.list: path segment not expected", uripathlist.contains( value3 ) );
        assertEquals( "coap.opt.uri_path: wrong path", total, set.getUriPathString() );

        props.clear();
        props.put( "coap.opt.uri_path", total );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.uri_path.list: wrong number of path segment", 2, set.getURIPathCount() );
        uripathlist= set.getUriPath();
        assertEquals( "coap.opt.uri_path.list: missing path segment", value1, uripathlist.get( 0 ) );
        assertEquals( "coap.opt.uri_path.list: missing path segment", value2, uripathlist.get( 1 ) );
        assertFalse( "coap.opt.uri_path.list: path segment not expected", uripathlist.contains( value3 ) );
        assertEquals( "coap.opt.uri_path: wrong path", total, set.getUriPathString() );
    }

    @Test
    public void testOptionContentFormat() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer format= 41;
        set.setContentFormat( format );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        assertEquals( "coap.opt.content_format: wrong value", format, (Integer) props.get( "coap.opt.content_format" ) );
    }

    @Test
    public void testMapContentFormat() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        HashMap< String, Object > props= new HashMap< String, Object >();
        Integer format= 40;
        props.put( "coap.opt.content_format", format );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.content_format: wrong value", format, new Integer( set.getContentFormat() ) );

        props.clear();
        props.put( "coap.opt.content_format", new String( "40" ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.content_format: wrong value", format, new Integer( set.getContentFormat() ) );

        props.clear();
        props.put( "coap.opt.content_format", new StringWrapper( "40" ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.content_format: wrong value", format, new Integer( set.getContentFormat() ) );

    }

    @Test
    public void testOptionMaxAge() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Long maxage= new Long( 120 );
        set.setMaxAge( maxage );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        assertEquals( "coap.opt.max_age: wrong value", maxage, (Long) props.get( "coap.opt.max_age" ) );
    }

    @Test
    public void testMapMaxAge() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        HashMap< String, Object > props= new HashMap< String, Object >();
        Long maxage= new Long( 120 );
        props.put( "coap.opt.max_age", maxage );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.max_age: wrong value", maxage, set.getMaxAge() );

        props.clear();
        props.put( "coap.opt.max_age", new Integer( 120 ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.max_age: wrong value", maxage, set.getMaxAge() );

        props.clear();
        props.put( "coap.opt.max_age", new String( "120" ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.max_age: wrong value", maxage, set.getMaxAge() );

        props.clear();
        props.put( "coap.opt.max_age", new StringWrapper( "120" ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.max_age: wrong value", maxage, set.getMaxAge() );

    }

    @Test
    public void testOptionSetUriQuery() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String value1= "this";
        String value2= "is";
        String value3= "some=query";
        String total= "this&is";

        set.addUriQuery( value1 );
        set.addUriQuery( value2 );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        @SuppressWarnings("unchecked")
        List< String > list= (List< String >) props.get( "coap.opt.uri_query.list" );

        assertNotNull( list );
        assertEquals( "coap.opt.uri_query.list: wrong number of query segment", 2, list.size() );
        assertEquals( "coap.opt.uri_query.list: missing query segment", value1, list.get( 0 ) );
        assertEquals( "coap.opt.uri_query.list: missing query segment", value2, list.get( 1 ) );
        assertFalse( "coap.opt.uri_query.list: query segment not expected", list.contains( value3 ) );
        assertEquals( "coap.opt.uri_query: wrong query", total, props.get( "coap.opt.uri_query" ) );

    }

    @Test
    public void testMapUriQuery() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        String value1= "this";
        String value2= "is";
        String value3= "some=query";
        String total= "this&is";

        HashMap< String, Object > props= new HashMap< String, Object >();
        LinkedList< String > list= new LinkedList< String >();
        list.add( value1 );
        list.add( value2 );
        props.put( "coap.opt.uri_query.list", list );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.uri_query.list: wrong number of query segments", 2, set.getURIQueryCount() );
        List< String > uriquerylist= set.getUriQuery();
        assertEquals( "coap.opt.uri_query.list: missing query segment", value1, uriquerylist.get( 0 ) );
        assertEquals( "coap.opt.uri_query.list: missing query segment", value2, uriquerylist.get( 1 ) );
        assertFalse( "coap.opt.uri_query.list: query segment not expected", uriquerylist.contains( value3 ) );
        assertEquals( "coap.opt.uri_query: wrong query", total, set.getUriQueryString() );

        props.clear();
        props.put( "coap.opt.uri_query", total );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.uri_query.list: wrong number of query segments", 2, set.getURIQueryCount() );
        uriquerylist= set.getUriQuery();
        assertEquals( "coap.opt.uri_query.list: missing query segment", value1, uriquerylist.get( 0 ) );
        assertEquals( "coap.opt.uri_query.list: missing query segment", value2, uriquerylist.get( 1 ) );
        assertFalse( "coap.opt.uri_query.list: query segment not expected", uriquerylist.contains( value3 ) );
        assertEquals( "coap.opt.uri_query: wrong query", total, set.getUriQueryString() );
    }

    @Test
    public void testOptionAccept() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer format= new Integer( 41 );
        set.setAccept( format );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        assertEquals( "coap.opt.accept: wrong value", format, (Integer) props.get( "coap.opt.accept" ) );
    }

    @Test
    public void testMapAccept() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        HashMap< String, Object > props= new HashMap< String, Object >();
        Integer format= new Integer( 41 );
        props.put( "coap.opt.accept", format );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.accept: wrong value", format, (Integer) set.getAccept() );

        props.clear();
        props.put( "coap.opt.accept", new Integer( 41 ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.accept: wrong value", format, (Integer) set.getAccept() );

        props.clear();
        props.put( "coap.opt.accept", new String( "41" ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.accept: wrong value", format, (Integer) set.getAccept() );

        props.clear();
        props.put( "coap.opt.accept", new StringWrapper( "41" ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.accept: wrong value", format, (Integer) set.getAccept() );

    }

    @Test
    public void testOptionSetLocationQuery() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String value1= "this";
        String value2= "is";
        String value3= "some=locationquery";
        String total= "this&is";

        set.addLocationQuery( value1 );
        set.addLocationQuery( value2 );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        @SuppressWarnings("unchecked")
        List< String > list= (List< String >) props.get( "coap.opt.location_query.list" );

        assertNotNull( list );
        assertEquals( "coap.opt.location_query.list: wrong number of query segment", 2, list.size() );
        assertEquals( "coap.opt.location_query.list: missing query segment", value1, list.get( 0 ) );
        assertEquals( "coap.opt.location_query.list: missing query segment", value2, list.get( 1 ) );
        assertFalse( "coap.opt.location_query.list: query segment not expected", list.contains( value3 ) );
        assertEquals( "coap.opt.location_query: wrong query", total, props.get( "coap.opt.location_query" ) );

    }

    @Test
    public void testMapLocationQuery() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        String value1= "this";
        String value2= "is";
        String value3= "some=query";
        String total= "this&is";

        HashMap< String, Object > props= new HashMap< String, Object >();
        LinkedList< String > list= new LinkedList< String >();
        list.add( value1 );
        list.add( value2 );
        props.put( "coap.opt.location_query.list", list );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.location_query.list: wrong number of query segments", 2, set.getLocationQueryCount() );
        List< String > querylist= set.getLocationQuery();
        assertEquals( "coap.opt.location_query.list: missing query segment", value1, querylist.get( 0 ) );
        assertEquals( "coap.opt.location_query.list: missing query segment", value2, querylist.get( 1 ) );
        assertFalse( "coap.opt.location_query.list: query segment not expected", querylist.contains( value3 ) );
        assertEquals( "coap.opt.location_query: wrong query", total, set.getLocationQueryString() );

        props.clear();
        props.put( "coap.opt.location_query", total );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.location_query.list: wrong number of query segments", 2, set.getLocationQueryCount() );
        querylist= set.getLocationQuery();
        assertEquals( "coap.opt.location_query.list: missing query segment", value1, querylist.get( 0 ) );
        assertEquals( "coap.opt.location_query.list: missing query segment", value2, querylist.get( 1 ) );
        assertFalse( "coap.opt.location_query.list: query segment not expected", querylist.contains( value3 ) );
        assertEquals( "coap.opt.location_query: wrong query", total, set.getLocationQueryString() );
    }

    @Test
    public void testOptionProxyUri() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String uri= "testproxyuri";
        set.setProxyUri( uri );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        assertEquals( "coap.opt.proxy_uri: wrong value", uri, props.get( "coap.opt.proxy_uri" ) );
    }

    @Test
    public void testMapProxyUri() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        String uri= "testproxyuri";

        HashMap< String, Object > props= new HashMap< String, Object >();
        props.put( "coap.opt.proxy_uri", uri );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.proxy_uri: wrong value", uri, set.getProxyUri() );

        props.clear();
        props.put( "coap.opt.proxy_uri", new StringWrapper( uri ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.proxy_uri: wrong value", uri, set.getProxyUri() );
    }

    @Test
    public void testOptionProxyScheme() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String scheme= "testproxyscheme";
        set.setProxyScheme( scheme );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        assertEquals( "coap.opt.proxy_scheme: wrong value", scheme, props.get( "coap.opt.proxy_scheme" ) );
    }

    @Test
    public void testMapProxyScheme() throws InternalInvalidByteArrayValueException
    {
        String scheme= "testproxyscheme";

        HashMap< String, Object > props= new HashMap< String, Object >();
        props.put( "coap.opt.proxy_scheme", scheme );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.proxy_scheme: wrong value", scheme, set.getProxyScheme() );

        props.clear();
        props.put( "coap.opt.proxy_scheme", new StringWrapper( scheme ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.proxy_scheme: wrong value", scheme, set.getProxyScheme() );
    }

    @Test
    public void testOptionBlock1() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer szx= 3;
        Integer size= 128;
        Boolean m= true;
        Integer num= 3;

        set.setBlock1( szx, m, num );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        assertEquals( "coap.opt.block1.szx: wrong value", szx, props.get( "coap.opt.block1.szx" ) );
        assertEquals( "coap.opt.block1.size: wrong value", size, props.get( "coap.opt.block1.size" ) );
        assertEquals( "coap.opt.block1.num", num, props.get( "coap.opt.block1.num" ) );
        assertTrue( "coap.opt.block1.m: wrong value", (Boolean) props.get( "coap.opt.block1.m" ) );
    }

    @Test
    public void testMapBlock1() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        Integer szx= 3;
        Integer size= 128;
        Boolean m= true;
        Integer num= 3;
        BlockOption block= new BlockOption( num, m, num );

        HashMap< String, Object > props= new HashMap< String, Object >();
        props.put( "coap.opt.block1.szx", szx );
        props.put( "coap.opt.block1.m", m );
        props.put( "coap.opt.block1.num", num );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.block1: wrong value", set.getBlock1(), block );

        props.clear();
        props.put( "coap.opt.block1.szx", new StringWrapper( szx.toString() ) );
        props.put( "coap.opt.block1.m", new StringWrapper( m.toString() ) );
        props.put( "coap.opt.block1.num", new StringWrapper( num.toString() ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.block1: wrong value", set.getBlock1(), block );

        props.clear();
        props.put( "coap.opt.block1.size", size );
        props.put( "coap.opt.block1.m", m );
        props.put( "coap.opt.block1.num", num );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.block1: wrong value", set.getBlock1(), block );

        props.clear();
        props.put( "coap.opt.block1.size", new StringWrapper( size.toString() ) );
        props.put( "coap.opt.block1.m", new StringWrapper( m.toString() ) );
        props.put( "coap.opt.block1.num", new StringWrapper( num.toString() ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.block1: wrong value", set.getBlock1(), block );
    }

    @Test
    public void testOptionBlock2() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer szx= 3;
        Integer size= 128;
        Boolean m= true;
        Integer num= 3;

        set.setBlock2( szx, m, num );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        assertEquals( "coap.opt.block2.szx: wrong value", szx, props.get( "coap.opt.block2.szx" ) );
        assertEquals( "coap.opt.block2.size: wrong value", size, props.get( "coap.opt.block2.size" ) );
        assertEquals( "coap.opt.block2.num", num, props.get( "coap.opt.block2.num" ) );
        assertTrue( "coap.opt.block2.m: wrong value", (Boolean) props.get( "coap.opt.block2.m" ) );
    }

    @Test
    public void testMapBlock2() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        Integer szx= 3;
        Integer size= 128;
        Boolean m= true;
        Integer num= 3;
        BlockOption block= new BlockOption( num, m, num );

        HashMap< String, Object > props= new HashMap< String, Object >();
        props.put( "coap.opt.block2.szx", szx );
        props.put( "coap.opt.block2.m", m );
        props.put( "coap.opt.block2.num", num );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.block2: wrong value", set.getBlock2(), block );

        props.clear();
        props.put( "coap.opt.block2.szx", new StringWrapper( szx.toString() ) );
        props.put( "coap.opt.block2.m", new StringWrapper( m.toString() ) );
        props.put( "coap.opt.block2.num", new StringWrapper( num.toString() ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.block2: wrong value", set.getBlock2(), block );

        props.clear();
        props.put( "coap.opt.block2.size", size );
        props.put( "coap.opt.block2.m", m );
        props.put( "coap.opt.block2.num", num );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.block2: wrong value", set.getBlock2(), block );

        props.clear();
        props.put( "coap.opt.block2.size", new StringWrapper( size.toString() ) );
        props.put( "coap.opt.block2.m", new StringWrapper( m.toString() ) );
        props.put( "coap.opt.block2.num", new StringWrapper( num.toString() ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.block2: wrong value", set.getBlock2(), block );
    }

    @Test
    public void testOptionSize1() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer size= new Integer( 120 );
        set.setSize1( size );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        assertEquals( "coap.opt.size1: wrong value", size, props.get( "coap.opt.size1" ) );
    }

    @Test
    public void testMapSize1() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        HashMap< String, Object > props= new HashMap< String, Object >();
        Integer size= new Integer( 120 );
        props.put( "coap.opt.size1", size );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.size1: wrong value", size, set.getSize1() );

        props.clear();
        props.put( "coap.opt.size1", new Integer( 120 ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.size1: wrong value", size, set.getSize1() );

        props.clear();
        props.put( "coap.opt.size1", new String( "120" ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.size1: wrong value", size, set.getSize1() );

        props.clear();
        props.put( "coap.opt.size1", new StringWrapper( "120" ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.size1: wrong value", size, set.getSize1() );

    }

    @Test
    public void testOptionSize2() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer size= new Integer( 120 );
        set.setSize2( size );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        assertEquals( "coap.opt.size2: wrong value", size, props.get( "coap.opt.size2" ) );
    }

    @Test
    public void testMapSize2() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        HashMap< String, Object > props= new HashMap< String, Object >();
        Integer size= new Integer( 120 );
        props.put( "coap.opt.size2", size );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.size2: wrong value", size, set.getSize2() );

        props.clear();
        props.put( "coap.opt.size2", new Integer( 120 ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.size2: wrong value", size, set.getSize2() );

        props.clear();
        props.put( "coap.opt.size2", new String( "120" ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.size2: wrong value", size, set.getSize2() );

        props.clear();
        props.put( "coap.opt.size2", new StringWrapper( "120" ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.size2: wrong value", size, set.getSize2() );

    }

    @Test
    public void testOptionObserve() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer seqnum= new Integer( 120 );
        set.setObserve( seqnum );

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        assertEquals( "coap.opt.observe: wrong value", seqnum, props.get( "coap.opt.observe" ) );
    }

    @Test
    public void testMapObserve() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        HashMap< String, Object > props= new HashMap< String, Object >();
        Integer seqnum= new Integer( 120 );
        props.put( "coap.opt.observe", seqnum );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.observe: wrong value", seqnum, set.getObserve() );

        props.clear();
        props.put( "coap.opt.observe", new Integer( 120 ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.observe: wrong value", seqnum, set.getObserve() );

        props.clear();
        props.put( "coap.opt.observe", new String( "120" ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.observe: wrong value", seqnum, set.getObserve() );

        props.clear();
        props.put( "coap.opt.observe", new StringWrapper( "120" ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertEquals( "coap.opt.observe: wrong value", seqnum, set.getObserve() );

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

        HashMap< String, Object > props= new HashMap< String, Object >();

        CoAPOptions.fillPropertyMap( set, props );

        assertArrayEquals( "coap.opt.other." + optionNr1.toString() + ": wrong value", value1, (byte[]) props.get( "coap.opt.other." + optionNr1.toString() ) );
        assertEquals( "coap.opt.other." + optionNr1.toString() + ".critical: wrong value", true, props.get( "coap.opt.other." + optionNr1.toString() + ".critical" ) );
        assertEquals( "coap.opt.other." + optionNr1.toString() + ".unsafe: wrong value", false, props.get( "coap.opt.other." + optionNr1.toString() + ".unsafe" ) );
        assertEquals( "coap.opt.other." + optionNr1.toString() + ".no_cache_key: wrong value", false, props.get( "coap.opt.other." + optionNr1.toString() + ".no_cache_key" ) );

        assertArrayEquals( "coap.opt.other." + optionNr2.toString() + ": wrong value", value2, (byte[]) props.get( "coap.opt.other." + optionNr2.toString() ) );
        assertEquals( "coap.opt.other." + optionNr2.toString() + ".critical: wrong value", false, props.get( "coap.opt.other." + optionNr2.toString() + ".critical" ) );
        assertEquals( "coap.opt.other." + optionNr2.toString() + ".unsafe: wrong value", true, props.get( "coap.opt.other." + optionNr2.toString() + ".unsafe" ) );
        assertEquals( "coap.opt.other." + optionNr2.toString() + ".no_cache_key: wrong value", false, props.get( "coap.opt.other." + optionNr2.toString() + ".no_cache_key" ) );

        assertArrayEquals( "coap.opt.other." + optionNr3.toString() + ": wrong value", value3, (byte[]) props.get( "coap.opt.other." + optionNr3.toString() ) );
        assertEquals( "coap.opt.other." + optionNr3.toString() + ".critical: wrong value", false, props.get( "coap.opt.other." + optionNr3.toString() + ".critical" ) );
        assertEquals( "coap.opt.other." + optionNr3.toString() + ".unsafe: wrong value", false, props.get( "coap.opt.other." + optionNr3.toString() + ".unsafe" ) );
        assertEquals( "coap.opt.other." + optionNr3.toString() + ".no_cache_key: wrong value", true, props.get( "coap.opt.other." + optionNr3.toString() + ".no_cache_key" ) );

        assertArrayEquals( "coap.opt.other." + optionNr4.toString() + ": wrong value", value4, (byte[]) props.get( "coap.opt.other." + optionNr4.toString() ) );
        assertEquals( "coap.opt.other." + optionNr4.toString() + ".critical: wrong value", true, props.get( "coap.opt.other." + optionNr4.toString() + ".critical" ) );
        assertEquals( "coap.opt.other." + optionNr4.toString() + ".unsafe: wrong value", false, props.get( "coap.opt.other." + optionNr4.toString() + ".unsafe" ) );
        assertEquals( "coap.opt.other." + optionNr4.toString() + ".no_cache_key: wrong value", true, props.get( "coap.opt.other." + optionNr4.toString() + ".no_cache_key" ) );
    }

    @Test
    public void testMapOther() throws InvalidETagException, InternalInvalidByteArrayValueException
    {
        HashMap< String, Object > props= new HashMap< String, Object >();
        byte[] value1= { (byte) 0x31, (byte) 0x32, (byte) 0x30 };
        props.put( "coap.opt.other.65001", value1.clone() );

        OptionSet set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertTrue( "coap.opt.other.65001: option should exist", set.hasOption( 65001 ) );
        assertFalse( "coap.opt.other.65002: option should not exist", set.hasOption( 65002 ) );
        List< Option > others= set.getOthers();
        assertEquals( "coap.opt.other.65001: too many other options", 1, others.size() );
        assertEquals( "coap.opt.other.65001: wrong option number", 65001, others.get( 0 ).getNumber() );
        assertArrayEquals( "coap.opt.other.65001: wrong option value", value1, others.get( 0 ).getValue() );

        props.clear();
        props.put( "coap.opt.other.65001", new Integer( 120 ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertTrue( "coap.opt.other.65001: option should exist", set.hasOption( 65001 ) );
        assertFalse( "coap.opt.other.65002: option should not exist", set.hasOption( 65002 ) );
        others= set.getOthers();
        assertEquals( "coap.opt.other.65001: too many other options", 1, others.size() );
        assertEquals( "coap.opt.other.65001: wrong option number", 65001, others.get( 0 ).getNumber() );
        assertArrayEquals( "coap.opt.other.65001: wrong option value", value1, others.get( 0 ).getValue() );

        props.clear();
        props.put( "coap.opt.other.65001", new String( "120" ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertTrue( "coap.opt.other.65001: option should exist", set.hasOption( 65001 ) );
        assertFalse( "coap.opt.other.65002: option should not exist", set.hasOption( 65002 ) );
        others= set.getOthers();
        assertEquals( "coap.opt.other.65001: too many other options", 1, others.size() );
        assertEquals( "coap.opt.other.65001: wrong option number", 65001, others.get( 0 ).getNumber() );
        assertArrayEquals( "coap.opt.other.65001: wrong option value", value1, others.get( 0 ).getValue() );

        props.clear();
        props.put( "coap.opt.other.65001", new StringWrapper( "120" ) );

        set= new OptionSet();
        CoAPOptions.fillOptionSet( set, props, true );

        assertTrue( "coap.opt.other.65001: option should exist", set.hasOption( 65001 ) );
        assertFalse( "coap.opt.other.65002: option should not exist", set.hasOption( 65002 ) );
        others= set.getOthers();
        assertEquals( "coap.opt.other.65001: too many other options", 1, others.size() );
        assertEquals( "coap.opt.other.65001: wrong option number", 65001, others.get( 0 ).getNumber() );
        assertArrayEquals( "coap.opt.other.65001: wrong option value", value1, others.get( 0 ).getValue() );
    }

    private class StringWrapper
    {
        private String string;

        public StringWrapper( String string )
        {
            this.string= string;
        }

        @Override
        public String toString()
        {
            return string;
        }
    }
}
