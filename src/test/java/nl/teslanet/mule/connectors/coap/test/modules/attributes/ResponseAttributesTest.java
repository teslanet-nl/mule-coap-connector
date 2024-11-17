/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2024 (teslanet.nl) Rogier Cobben
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.option.IntegerOptionDefinition;
import org.junit.Before;
import org.junit.Test;

import nl.teslanet.mule.connectors.coap.api.attributes.Result;
import nl.teslanet.mule.connectors.coap.api.binary.BytesValue;
import nl.teslanet.mule.connectors.coap.api.options.OptionValueException;
import nl.teslanet.mule.connectors.coap.api.options.ResponseOptionsAttributes;
import nl.teslanet.mule.connectors.coap.internal.attributes.CoapRequestOptionsAttributesImpl;
import nl.teslanet.mule.connectors.coap.internal.attributes.CoapResponseAttributesImpl;
import nl.teslanet.mule.connectors.coap.internal.attributes.CoapResponseOptionsAttributesImpl;
import nl.teslanet.mule.connectors.coap.internal.exceptions.InternalInvalidOptionValueException;
import nl.teslanet.mule.connectors.coap.internal.options.DefaultEntityTag;


/**
 * Test the RequestCodeFlags class.
 */
public class ResponseAttributesTest
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
     * The CoAP request options to test.
     */
    protected CoapRequestOptionsAttributesImpl requestOptions= null;

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

    /**
     * Response location uri.
     */
    private String locationUri= "/the/new/resource?is=there";

    /**
     * The response is a notification.
     */
    private boolean notification= true;

    /**
     * The response type.
     */
    private String responseType= "NON_CONFIRMABLE";

    /**
     * The response code.
     */
    private String responseCode= "CONTENT";

    /**
     * The CoAP response options to test.
     */
    private ResponseOptionsAttributes responseOptions;

    /**
     * Response options to test.
     */
    protected OptionSet responseOptionSet= new OptionSet();

    /**
     * The response result indicator.
     */
    private Result result= Result.SUCCESS;

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

        responseOptionSet.setSize1( 569 );
        responseOptionSet.setContentFormat( 43 );
        responseOptionSet.addETag( DefaultEntityTag.valueOf( 569 ).getValue() );
        responseOptionSet.setLocationPath( "/the/path" );
        responseOptionSet.setLocationQuery( "f=6&g=7" );
        responseOptionSet.setMaxAge( 61 );
        responseOptionSet.addOtherOption( new Option( otherOptionXDef, 501 ) );
        responseOptionSet.addOtherOption( new Option( otherOptionYDef, 502 ) );
        responseOptionSet.setSize2( 965 );
        responseOptions= new CoapResponseOptionsAttributesImpl( responseOptionSet );
    }

    @Test
    public void defaultConstructorTest()
    {
        CoapResponseAttributesImpl attributes= new CoapResponseAttributesImpl();

        assertNull( "RequestType has wrong value", attributes.getRequestType() );
        assertNull( "RequestCode has wrong value", attributes.getRequestCode() );
        assertNull( "RequestScheme has wrong value", attributes.getRequestScheme() );
        assertNull( "RequestHost has wrong value", attributes.getRequestHost() );
        assertEquals( "RequestPort has wrong value", -1, attributes.getRequestPort() );
        assertEquals( "RequestPath has wrong value", "", attributes.getRequestPath() );
        assertEquals( "RequestQuery has wrong value", "", attributes.getRequestQuery() );
        assertEquals( "RequestUri has wrong value", "", attributes.getRequestUri() );
        assertNull( "RequestOptions has wrong value", attributes.getRequestOptions() );

        assertNull( "LocalAddress has wrong value", attributes.getLocalAddress() );
        assertNull( "RemoteAddress has wrong value", attributes.getRemoteAddress() );

        assertNull( "LocationUri has wrong value", attributes.getLocationUri() );
        assertFalse( "LocationUri has wrong value", attributes.isNotification() );
        assertNull( "ResponseType has wrong value", attributes.getResponseType() );
        assertNull( "ResponseCode has wrong value", attributes.getResponseCode() );
        assertNull( "ResponseOptions has wrong value", attributes.getResponseOptions() );
        assertFalse( "Success has wrong value", attributes.isSuccess() );
    }

    @Test
    public void setGetTest()
    {
        CoapResponseAttributesImpl attributes= new CoapResponseAttributesImpl();
        attributes.setRequestType( requestType );
        attributes.setRequestCode( requestCode );
        attributes.setRequestUri( requestUri );
        attributes.setRequestOptions( requestOptions );
        attributes.setLocalAddress( localAddress );
        attributes.setRemoteAddress( remoteAddress );

        attributes.setLocationUri( locationUri );
        attributes.setNotification( notification );
        attributes.setResponseType( responseType );
        attributes.setResponseCode( responseCode );
        attributes.setResponseOptions( responseOptions );
        attributes.setResult( result );

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

        assertEquals( "LocationUri has wrong value", locationUri, attributes.getLocationUri() );
        assertEquals( "LocationUri has wrong value", notification, attributes.isNotification() );
        assertEquals( "ResponseType has wrong value", responseType, attributes.getResponseType() );
        assertEquals( "ResponseCode has wrong value", responseCode, attributes.getResponseCode() );
        assertEquals( "ResponseOptions has wrong value", responseOptions, attributes.getResponseOptions() );
        assertEquals( "Result has wrong value", result, attributes.getResult() );
        assertEquals( "No Response has wrong value", result == Result.NO_RESPONSE, attributes.isNoResponse() );
        assertEquals( "Client Error has wrong value", result == Result.CLIENT_ERROR, attributes.isClientError() );
        assertEquals( "Server Error has wrong value", result == Result.SERVER_ERROR, attributes.isServerError() );
        assertEquals( "Success has wrong value", result == Result.SUCCESS, attributes.isSuccess() );
    }

    @Test
    public void requestOptionsToStringTest()
    {
        String stringResult= requestOptions.toString();

        assertEquals(
            "response options string has wrong value",
            "CoapRequestOptionsAttributesImpl\n" + "{\n" + "   accept=0,\n" + "   contentFormat=41,\n" + "   etags=[\n"
                + "      BytesValue{ 02 },\n" + "      BytesValue{ 03 }\n" + "   ],\n" + "   ifMatch=[\n"
                + "      BytesValue{ 04 },\n" + "      BytesValue{ 05 }\n" + "   ],\n" + "   observe=1,\n"
                + "   other=[\n" + "      Option{ alias=X, number=65001, valueAsNumber=401 },\n"
                + "      Option{ alias=Y, number=65002, valueAsNumber=402 }\n" + "   ],\n"
                + "   provideResponseSize=true,\n" + "   proxyScheme=http,\n"
                + "   proxyUri=http://test:8080/path?x=3,\n" + "   uriHost=myhost,\n" + "   uriPort=6354,\n"
                + "   uriPath=[\n" + "      parent,\n" + "      child\n" + "   ],\n" + "   uriQuery=[\n"
                + "      a=1,\n" + "      b=2\n" + "   ]\n" + "}",
            stringResult
        );
    }

    @Test
    public void responseOptionsToStringTest()
    {
        String stringResult= responseOptions.toString();

        assertEquals(
            "response options string has wrong value",
            "CoapResponseOptionsAttributesImpl\n" + "{\n" + "   acceptableRequestSize=569,\n" + "   contentFormat=43,\n"
                + "   etag=BytesValue{ 0239 },\n" + "   locationPath=[\n" + "      the,\n" + "      path\n" + "   ],\n"
                + "   locationQuery=[\n" + "      f=6,\n" + "      g=7\n" + "   ],\n" + "   maxAge=61,\n"
                + "   other=[\n" + "      Option{ alias=X, number=65001, valueAsNumber=501 },\n"
                + "      Option{ alias=Y, number=65002, valueAsNumber=502 }\n" + "   ],\n" + "   responseSize=965\n"
                + "}",
            stringResult
        );
    }

    @Test
    public void toStringTest()
    {
        CoapResponseAttributesImpl attributes= new CoapResponseAttributesImpl();
        attributes.setRequestType( requestType );
        attributes.setRequestCode( requestCode );
        attributes.setRequestUri( requestUri );
        attributes.setRequestOptions( requestOptions );
        attributes.setLocalAddress( localAddress );
        attributes.setRemoteAddress( remoteAddress );

        attributes.setLocationUri( locationUri );
        attributes.setNotification( notification );
        attributes.setResponseType( responseType );
        attributes.setResponseCode( responseCode );
        attributes.setResponseOptions( responseOptions );
        attributes.setResult( result );

        String stringResult= attributes.toString();

        assertEquals(
            "Request attributes has wrong string value",
            "CoapResponseAttributesImpl\n" + "{\n" + "   localAddress=myhost,\n" + "   remoteAddress=192.168.0.26,\n"
                + "   requestType=CONFIRMABLE,\n" + "   requestCode=PUT,\n"
                + "   requestOptions=CoapRequestOptionsAttributesImpl\n" + "   {\n" + "      accept=0,\n"
                + "      contentFormat=41,\n" + "      etags=[\n" + "         BytesValue{ 02 },\n"
                + "         BytesValue{ 03 }\n" + "      ],\n" + "      ifMatch=[\n" + "         BytesValue{ 04 },\n"
                + "         BytesValue{ 05 }\n" + "      ],\n" + "      observe=1,\n" + "      other=[\n"
                + "         Option{ alias=X, number=65001, valueAsNumber=401 },\n"
                + "         Option{ alias=Y, number=65002, valueAsNumber=402 }\n" + "      ],\n"
                + "      provideResponseSize=true,\n" + "      proxyScheme=http,\n"
                + "      proxyUri=http://test:8080/path?x=3,\n" + "      uriHost=myhost,\n" + "      uriPort=6354,\n"
                + "      uriPath=[\n" + "         parent,\n" + "         child\n" + "      ],\n" + "      uriQuery=[\n"
                + "         a=1,\n" + "         b=2\n" + "      ]\n" + "   },\n"
                + "   requestUri=coaps+tcp://myhost:6354/parent/child?a=1&b=2,\n" + "   notification=true,\n"
                + "   responseType=NON_CONFIRMABLE,\n" + "   responseCode=CONTENT,\n"
                + "   locationUri=/the/new/resource?is=there,\n"
                + "   responseOptions=CoapResponseOptionsAttributesImpl\n" + "   {\n"
                + "      acceptableRequestSize=569,\n" + "      contentFormat=43,\n"
                + "      etag=BytesValue{ 0239 },\n" + "      locationPath=[\n" + "         the,\n" + "         path\n"
                + "      ],\n" + "      locationQuery=[\n" + "         f=6,\n" + "         g=7\n" + "      ],\n"
                + "      maxAge=61,\n" + "      other=[\n"
                + "         Option{ alias=X, number=65001, valueAsNumber=501 },\n"
                + "         Option{ alias=Y, number=65002, valueAsNumber=502 }\n" + "      ],\n"
                + "      responseSize=965\n" + "   },\n" + "   result=SUCCESS\n" + "}",
            stringResult
        );
    }

    @Test
    public void toStringTest2() throws InternalInvalidOptionValueException
    {
        CoapResponseAttributesImpl attributes= new CoapResponseAttributesImpl();

        attributes.setRequestType( requestType );
        attributes.setRequestCode( requestCode );
        attributes.setRequestUri( requestUri );
        attributes.setLocalAddress( localAddress );
        attributes.setRemoteAddress( remoteAddress );
        attributes.setRequestOptions( new CoapRequestOptionsAttributesImpl( new OptionSet() ) );
        //attributes.
        attributes.setResponseOptions( new CoapResponseOptionsAttributesImpl( new OptionSet() ) );
        attributes.setResult( Result.NO_RESPONSE );

        String stringResult= attributes.toString();

        assertEquals(
            "Request attributes has wrong string value",
            "CoapResponseAttributesImpl\n" + "{\n" + "   localAddress=myhost,\n" + "   remoteAddress=192.168.0.26,\n"
                + "   requestType=CONFIRMABLE,\n" + "   requestCode=PUT,\n"
                + "   requestOptions=CoapRequestOptionsAttributesImpl\n" + "   {\n" + "      \n" + "   },\n"
                + "   requestUri=coaps+tcp://myhost:6354/parent/child?a=1&b=2,\n"
                + "   responseOptions=CoapResponseOptionsAttributesImpl\n" + "   {\n" + "      \n" + "   },\n"
                + "   result=NO_RESPONSE\n" + "}",
            stringResult
        );
    }
}