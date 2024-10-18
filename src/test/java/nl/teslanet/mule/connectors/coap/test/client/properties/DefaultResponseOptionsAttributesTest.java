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
package nl.teslanet.mule.connectors.coap.test.client.properties;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.option.OpaqueOptionDefinition;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.api.binary.BytesValue;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;
import nl.teslanet.mule.connectors.coap.api.options.OtherOptionAttribute;
import nl.teslanet.mule.connectors.coap.api.query.QueryParamAttribute;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultOtherOptionAttribute;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultResponseOptionsAttributes;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultEntityTag;
import nl.teslanet.mule.connectors.coap.test.utils.TestOptions;


/**
 * Test option classes
 *
 */
public class DefaultResponseOptionsAttributesTest
{
    @Test
    public void testOptionContentFormat() throws OptionValueException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer format= 41;
        set.setContentFormat( format );

        DefaultResponseOptionsAttributes attributes= new DefaultResponseOptionsAttributes( set );

        Integer attr= attributes.getContentFormat();

        assertEquals( "coap.opt.content_format: wrong value", format, attr );
    }

    @Test
    public void testOptionMaxAge() throws OptionValueException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Long maxage= Long.valueOf( 120 );
        set.setMaxAge( maxage );

        DefaultResponseOptionsAttributes attributes= new DefaultResponseOptionsAttributes( set );

        Long attr= attributes.getMaxAge();

        assertEquals( "coap.opt.max_age: wrong value", maxage, attr );
    }

    @Test
    public void testOptionSetETag() throws OptionValueException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        byte[] etagValue1= { (byte) 0x00, (byte) 0xFF };
        byte[] etagValue2= { (byte) 0x11, (byte) 0xFF };

        set.addETag( etagValue1.clone() );
        set.addETag( etagValue2.clone() );

        DefaultResponseOptionsAttributes attributes= new DefaultResponseOptionsAttributes( set );

        BytesValue etag= attributes.getEtag();

        assertNotNull( etag );
        assertEquals( "coap.opt.etag: wrong etag value", new DefaultEntityTag( etagValue1 ), etag );
    }

    @Test
    public void testOptionSetLocationPath() throws OptionValueException, InternalInvalidOptionValueException
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
    public void testOptionLocationQuery() throws OptionValueException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        String[] keys= { "this", "is", "some", "some" };
        String[] values= { null, "really", "locationquery", "also" };
        for ( int i= 0; i < keys.length; i++ )
        {
            set.addLocationQuery( keys[i] + ( values[i] == null ? "" : "=" + values[i] ) );
        }

        DefaultResponseOptionsAttributes attributes= new DefaultResponseOptionsAttributes( set );

        List< QueryParamAttribute > query= attributes.getLocationQuery();

        int index= 0;
        assertEquals( "coap.opt.location_query.list: wrong size", keys.length, query.size() );
        for ( QueryParamAttribute param : query )
        {
            assertEquals( "coap.opt.location_query.list: wrong key", keys[index], param.getKey() );
            assertEquals( "coap.opt.location_query.list: wrong key", values[index], param.getValue() );
            index++;
        }
    }

    @Test
    public void testOptionSize2() throws OptionValueException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer size= Integer.valueOf( 120 );
        set.setSize2( size );

        DefaultResponseOptionsAttributes attributes= new DefaultResponseOptionsAttributes( set );

        Integer attr= attributes.getResponseSize();

        assertEquals( "coap.opt.size2: wrong value", size, attr );
    }

    @Test
    public void testOptionSize1() throws OptionValueException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer size= Integer.valueOf( 120 );
        set.setSize1( size );

        DefaultResponseOptionsAttributes attributes= new DefaultResponseOptionsAttributes( set );

        Integer attr= attributes.getAcceptableRequestSize();

        assertEquals( "coap.opt.size1: wrong value", size, attr );
    }

    @Test
    public void testOptionObserve() throws OptionValueException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        Integer seqnum= Integer.valueOf( 120 );
        set.setObserve( seqnum );

        DefaultResponseOptionsAttributes attributes= new DefaultResponseOptionsAttributes( set );

        Integer attr= attributes.getObserve();

        assertEquals( "coap.opt.observe: wrong value", seqnum, attr );
    }

    @Test
    public void testOptionOther() throws OptionValueException, InternalInvalidOptionValueException
    {
        OptionSet set= new OptionSet();
        byte[][] values= { { (byte) 0x01, (byte) 0x02, (byte) 0x03 }, { (byte) 0x01, (byte) 0x02, (byte) 0x04 }, {
            (byte) 0x01, (byte) 0x02, (byte) 0x05 },
            { (byte) 0xff, (byte) 0x02, (byte) 0x05 } };

        OpaqueOptionDefinition[] optionsDefs= { TestOptions.OTHER_OPTION_65001, TestOptions.OTHER_OPTION_65002,
            TestOptions.OTHER_OPTION_65028, TestOptions.OTHER_OPTION_65029 };

        ArrayList< DefaultOtherOptionAttribute > expected= new ArrayList<>();
        for ( int i= 0; i < 4; i++ )
        {
            expected.add( new DefaultOtherOptionAttribute( optionsDefs[i], values[i].clone() ) );
            set.addOption( new Option( optionsDefs[i], values[i].clone() ) );
        }

        DefaultResponseOptionsAttributes attributes= new DefaultResponseOptionsAttributes( set );
        List< OtherOptionAttribute > options= attributes.getOther();

        assertEquals( "coap.opt.other has wrong length", expected.size(), options.size() );
        int j= 0;
        for ( OtherOptionAttribute item : options )
        {
            assertEquals( "coap.opt.other has wrong number", expected.get( j ).getNumber(), item.getNumber() );
            assertEquals( "coap.opt.other has wrong value", expected.get( j ).getValueAsHex(), item.getValueAsHex() );
            j++;
        }
    }
}
