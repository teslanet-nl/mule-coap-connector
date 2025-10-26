/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2024 - 2025 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.test.modules.attributes;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.option.IntegerOptionDefinition;
import org.junit.Before;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.api.binary.BytesValue;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;
import nl.teslanet.mule.connectors.coap.internal.attributes.CoapRequestAttributesImpl;
import nl.teslanet.mule.connectors.coap.internal.attributes.CoapRequestOptionsAttributesImpl;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultEntityTag;


/**
 * Test the RequestCodeFlags class.
 */
public class RequestAttributesTest
{
    /**
     * The CoAP request type to test.
     */
    protected String requestType= "CONFIRMABLE";

    /**
     * The CoAP request code to test.
     */
    protected String requestCode= "PUT";

    /**
     * The server address to test.
     */
    protected String localAddress= "myhost";

    /**
     * The uri of the request to test.
     */
    protected String requestUri= "coaps+tcp://myhost:6354/parent/child?a=1&b=2";

    /**
     * The scheme of the request to test.
     */
    protected String requestScheme= "coaps+tcp";

    /**
     * The host of the request to test.
     */
    protected String requestHost= "myhost";

    /**
     * The port of the request to test.
     */
    protected int requestPort= 6354;

    /**
     * The path of the request to test.
     */
    protected String requestPath= "/parent/child";

    /**
     * The query of the request to test.
     */
    protected String requestQuery= "a=1&b=2";

    /**
     * The address of the client to test.
     */
    protected String remoteAddress= "192.168.0.26";

    /**
     * The CoAP options to test.
     */
    protected CoapRequestOptionsAttributesImpl requestOptions= null;

    /**
     * Relation to test.
     */
    String relation= "192.168.0.26:4569/87asf";

    /**
     * Request options to test.
     */
    protected OptionSet requestOptionSet= new OptionSet();

    /**
     * Other option X definition.
     */
    protected IntegerOptionDefinition otherOptionXDef= new IntegerOptionDefinition( 65001, "X", false );

    /**
     * Other option Y definition.
     */
    protected IntegerOptionDefinition otherOptionYDef= new IntegerOptionDefinition( 65002, "Y", false );

    @Before
    public void setup() throws InternalInvalidOptionValueException, OptionValueException
    {
        List< BytesValue > etags= new ArrayList<>();
        List< BytesValue > ifMatch= new ArrayList<>();
        requestOptionSet.setAccept( 0 );
        requestOptionSet.setContentFormat( 41 );
        etags.add( DefaultEntityTag.valueOf( 2 ) );
        etags.add( DefaultEntityTag.valueOf( 3 ) );
        etags.forEach( ( etag ) -> requestOptionSet.addETag( etag.getValue() ) );
        ifMatch.add( DefaultEntityTag.valueOf( 4 ) );
        ifMatch.add( DefaultEntityTag.valueOf( 5 ) );
        ifMatch.forEach( ( etag ) -> requestOptionSet.addIfMatch( etag.getValue() ) );
        requestOptionSet.setObserve( 1 );
        requestOptionSet.addOtherOption( new Option( otherOptionXDef, 401 ) );
        requestOptionSet.addOtherOption( new Option( otherOptionYDef, 402 ) );
        requestOptionSet.setSize2( 0 );
        requestOptionSet.setProxyScheme( "http" );
        requestOptionSet.setProxyUri( "http://test:8080/path?x=3" );
        requestOptionSet.setUriHost( requestHost );
        requestOptionSet.setUriPort( requestPort );
        requestOptionSet.setUriPath( requestPath );
        requestOptionSet.setUriQuery( requestQuery );
        requestOptions= new CoapRequestOptionsAttributesImpl( requestOptionSet );
    }

    @Test
    public void defaultConstructorTest()
    {
        CoapRequestAttributesImpl attributes= new CoapRequestAttributesImpl();

        assertNull( "RequestType has wrong value", attributes.getRequestType() );
        assertNull( "RequestCode has wrong value", attributes.getRequestCode() );
        assertNull( "RequestScheme has wrong value", attributes.getRequestScheme() );
        assertNull( "RequestHost has wrong value", attributes.getRequestHost() );
        assertEquals( "RequestPort has wrong value", -1, attributes.getRequestPort() );
        assertEquals( "RequestPath has wrong value", "/", attributes.getRequestPath() );
        assertEquals( "RequestQuery has wrong value", "", attributes.getRequestQuery() );
        assertNull( "RequestUri has wrong value", attributes.getRequestUri() );
        assertNull( "RequestOptions has wrong value", attributes.getRequestOptions() );
        assertNull( "LocalAddress has wrong value", attributes.getLocalAddress() );
        assertNull( "RemoteAddress has wrong value", attributes.getRemoteAddress() );
        assertNull( "Relation has wrong value", attributes.getRelation() );
    }

    @Test
    public void setGetTest()
    {
        CoapRequestAttributesImpl attributes= new CoapRequestAttributesImpl();
        attributes.setRequestType( requestType );
        attributes.setRequestCode( requestCode );
        attributes.setRequestUri( requestUri );
        attributes.setRequestOptions( requestOptions );
        attributes.setLocalAddress( localAddress );
        attributes.setRemoteAddress( remoteAddress );
        attributes.setRelation( relation );

        assertEquals( "RequestType has wrong value", requestType, attributes.getRequestType() );
        assertEquals( "RequestCode has wrong value", requestCode, attributes.getRequestCode() );
        assertEquals( "RequestScheme has wrong value", requestScheme, attributes.getRequestScheme() );
        assertEquals( "RequestHost has wrong value", requestHost, attributes.getRequestHost() );
        assertEquals( "RequestPort has wrong value", requestPort, attributes.getRequestPort() );
        assertEquals( "RequestPath has wrong value", requestPath, attributes.getRequestPath() );
        assertEquals( "RequestQuery has wrong value", requestQuery, attributes.getRequestQuery() );
        assertEquals( "RequestUri has wrong value", requestUri, attributes.getRequestUri() );
        assertEquals( "RequestOptions has wrong value", requestOptions, attributes.getRequestOptions() );
        assertEquals( "LocalAddress has wrong value", localAddress, attributes.getLocalAddress() );
        assertEquals( "RemoteAddress has wrong value", remoteAddress, attributes.getRemoteAddress() );
        assertEquals( "Relation has wrong value", relation, attributes.getRelation() );
    }

    @Test
    public void optionsToStringTest()
    {
        String stringResult= requestOptions.toString();

        assertEquals(
            "response options string has wrong value",
            "CoapRequestOptionsAttributesImpl\n" + "{\n" + "   accept=0,\n" + "   contentFormat=41,\n" + "   etags=[\n"
                + "      BytesValue{ 02 },\n" + "      BytesValue{ 03 }\n" + "   ],\n" + "   ifMatch=[\n"
                + "      BytesValue{ 04 },\n" + "      BytesValue{ 05 }\n" + "   ],\n" + "   observe=1,\n"
                + "   other=[\n" + "      Option{ alias=X, number=65001, valueAsNumber=401 },\n"
                + "      Option{ alias=Y, number=65002, valueAsNumber=402 }\n" + "   ],\n"
                + "   requireResponseSize=true,\n" + "   proxyScheme=http,\n"
                + "   proxyUri=http://test:8080/path?x=3,\n" + "   uriHost=myhost,\n" + "   uriPort=6354,\n"
                + "   uriPath=[\n" + "      parent,\n" + "      child\n" + "   ],\n" + "   uriQuery=[\n"
                + "      a=1,\n" + "      b=2\n" + "   ]\n" + "}",
            stringResult
        );
    }

    @Test
    public void toStringTest()
    {
        CoapRequestAttributesImpl attributes= new CoapRequestAttributesImpl();

        attributes.setRequestType( requestType );
        attributes.setRequestCode( requestCode );
        attributes.setRequestUri( requestUri );
        attributes.setRequestOptions( requestOptions );
        attributes.setLocalAddress( localAddress );
        attributes.setRemoteAddress( remoteAddress );
        attributes.setRelation( relation );

        String stringResult= attributes.toString();

        assertEquals(
            "Request attributes has wrong string value",
            "CoapRequestAttributesImpl\n" + "{\n" + "   relation=192.168.0.26:4569/87asf,\n"
                + "   localAddress=myhost,\n" + "   remoteAddress=192.168.0.26,\n" + "   requestType=CONFIRMABLE,\n"
                + "   requestCode=PUT,\n" + "   requestOptions=CoapRequestOptionsAttributesImpl\n" + "   {\n"
                + "      accept=0,\n" + "      contentFormat=41,\n" + "      etags=[\n" + "         BytesValue{ 02 },\n"
                + "         BytesValue{ 03 }\n" + "      ],\n" + "      ifMatch=[\n" + "         BytesValue{ 04 },\n"
                + "         BytesValue{ 05 }\n" + "      ],\n" + "      observe=1,\n" + "      other=[\n"
                + "         Option{ alias=X, number=65001, valueAsNumber=401 },\n"
                + "         Option{ alias=Y, number=65002, valueAsNumber=402 }\n" + "      ],\n"
                + "      requireResponseSize=true,\n" + "      proxyScheme=http,\n"
                + "      proxyUri=http://test:8080/path?x=3,\n" + "      uriHost=myhost,\n" + "      uriPort=6354,\n"
                + "      uriPath=[\n" + "         parent,\n" + "         child\n" + "      ],\n" + "      uriQuery=[\n"
                + "         a=1,\n" + "         b=2\n" + "      ]\n" + "   },\n"
                + "   requestUri=coaps+tcp://myhost:6354/parent/child?a=1&b=2\n" + "}",
            stringResult
        );
    }

    @Test
    public void toStringTest2() throws InternalInvalidOptionValueException
    {
        CoapRequestAttributesImpl attributes= new CoapRequestAttributesImpl();

        attributes.setRequestType( requestType );
        attributes.setRequestCode( requestCode );
        attributes.setRequestUri( requestUri );
        attributes.setLocalAddress( localAddress );
        attributes.setRemoteAddress( remoteAddress );
        attributes.setRequestOptions( new CoapRequestOptionsAttributesImpl( new OptionSet() ) );

        String stringResult= attributes.toString();

        assertEquals(
            "Request attributes has wrong string value",
            "CoapRequestAttributesImpl\n" + "{\n" + "   localAddress=myhost,\n" + "   remoteAddress=192.168.0.26,\n"
                + "   requestType=CONFIRMABLE,\n" + "   requestCode=PUT,\n"
                + "   requestOptions=CoapRequestOptionsAttributesImpl\n" + "   {\n" + "      \n" + "   },\n"
                + "   requestUri=coaps+tcp://myhost:6354/parent/child?a=1&b=2\n" + "}",
            stringResult
        );
    }
}