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
package nl.teslanet.mule.connectors.coap.test.client.properties;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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
import nl.teslanet.mule.connectors.coap.internal.options.DefaultResponseOptionsAttributes;


/**
 * Test option classes
 *
 */
public class DefaultResponseOptionsAttributesTest
{

    @Test
    public void testOptionSetETag() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        byte[] etagValue1= { (byte) 0x00, (byte) 0xFF };
        byte[] etagValue2= { (byte) 0x11, (byte) 0xFF };

        set.addETag( etagValue1.clone() );
        set.addETag( etagValue2.clone() );

        DefaultResponseOptionsAttributes attributes= new DefaultResponseOptionsAttributes( set );

        ETag etag= attributes.getEtag();

        assertNotNull( etag );
        assertEquals( "coap.opt.etag: wrong etag value", new ETag( etagValue1 ), etag );
    }

    @Test
    public void testOptionSetLocationPath() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String[] values= { "this", "is", "some location" };

        set.addLocationPath( values[0] );
        set.addLocationPath( values[1] );

        DefaultResponseOptionsAttributes attributes= new DefaultResponseOptionsAttributes( set );

        List< String > list= attributes.getLocationPath();

        assertNotNull( list );
        assertEquals( "coap.opt.location_path.list: wrong number of path segment", 2, list.size() );
        assertFalse( "coap.opt.location_path.list: path segment not expected", list.contains( values[2] ) );
        for ( int i= 0; i < 2; i++ )
        {
            assertEquals( "coap.opt.location_path: wrong path", values[i], list.get( i ) );
        }
    }

    @Test
    public void testOptionContentFormat() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer format= 41;
        set.setContentFormat( format );

        DefaultResponseOptionsAttributes attributes= new DefaultResponseOptionsAttributes( set );

        Integer attr= attributes.getContentFormat();

        assertEquals( "coap.opt.content_format: wrong value", format, attr );
    }

    @Test
    public void testOptionMaxAge() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Long maxage= new Long( 120 );
        set.setMaxAge( maxage );

        DefaultResponseOptionsAttributes attributes= new DefaultResponseOptionsAttributes( set );

        Long attr= attributes.getMaxAge();

        assertEquals( "coap.opt.max_age: wrong value", maxage, attr );
    }

    //TODO RC
//    @Test
//    public void testOptionLocationQuery() throws InvalidETagException, InternalInvalidOptionValueException
//    {
//        //TODO RC add multiple values
//        OptionSet set= new OptionSet();
//        String[] values= { "this", "is", "some=locationquery" };
//        ArrayList< DefaultQueryParamAttribute > expected= new ArrayList<>();
//        for ( String param : values )
//        {
//            expected.add( new DefaultQueryParamAttribute( param ) );
//            set.addLocationQuery( param );
//
//        }
//
//        DefaultResponseOptionsAttributes attributes= new DefaultResponseOptionsAttributes( set );
//
//        MultiMap< String, String > query= attributes.getLocationQuery();
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
    public void testOptionSize1() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer size= new Integer( 120 );
        set.setSize1( size );

        DefaultResponseOptionsAttributes attributes= new DefaultResponseOptionsAttributes( set );

        Integer attr= attributes.getSize1();

        assertEquals( "coap.opt.size1: wrong value", size, attr );
    }

    @Test
    public void testOptionSize2() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer size= new Integer( 120 );
        set.setSize2( size );

        DefaultResponseOptionsAttributes attributes= new DefaultResponseOptionsAttributes( set );

        Integer attr= attributes.getSize2();

        assertEquals( "coap.opt.size2: wrong value", size, attr );
    }

    @Test
    public void testOptionObserve() throws InvalidETagException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer seqnum= new Integer( 120 );
        set.setObserve( seqnum );

        DefaultResponseOptionsAttributes attributes= new DefaultResponseOptionsAttributes( set );

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

        DefaultResponseOptionsAttributes attributes= new DefaultResponseOptionsAttributes( set );
        List< OtherOptionAttribute > options= attributes.getOtherOptions();

        assertEquals( "coap.opt.other has wrong length", expected.size(), options.size() );
        for ( int i= 0; i < 4; i++ )
        {
            assertEquals( "coap.opt.other has wrong number", expected.get( i ).getOptionNumber(), options.get( i ).getOptionNumber() );
            assertArrayEquals( "coap.opt.other has wrong value", expected.get( i ).getOptionValue(), options.get( i ).getOptionValue() );
        }
    }
}
