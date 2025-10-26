/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2025 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.test.server.noresponse;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.coap.NoResponseOption;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.runtime.api.message.Message;
import org.mule.test.runner.RunnerDelegateTo;

import nl.teslanet.mule.connectors.coap.api.attributes.CoapRequestAttributes;
import nl.teslanet.mule.connectors.coap.test.utils.AbstractServerTestCase;
import nl.teslanet.mule.connectors.coap.test.utils.MuleEventSpy;


/**
 * Server no response test
 *
 */
@RunnerDelegateTo( Parameterized.class )
public class NoResponseTest extends AbstractServerTestCase
{
    static boolean bools[]= { true, false };

    /**
     * The list of tests with their parameters
     * @return Test parameters.
     */
    @Parameters( name= "code= {0}, responseCode= {1}, requireSuccess= {2}, requireClientError {3},  requireServerError {4}" )
    public static Collection< Object[] > getTests()
    {
        ArrayList< Object[] > tests= new ArrayList< Object[] >();
        for ( ResponseCode rspCode : ResponseCode.values() )
        {
            if ( rspCode != ResponseCode._UNKNOWN_SUCCESS_CODE )
            {
                for ( boolean successValue : bools )
                {
                    for ( boolean clientErrorValue : bools )
                    {
                        for ( boolean serverErrorValue : bools )
                        {
                            tests.add( new Object []{ Code.POST, rspCode, successValue, clientErrorValue, serverErrorValue } );
                        }
                    }
                }
            }
        }
        return tests;
    }

    /**
     * The request code to use.
     */
    @Parameter( 0 )
    public Code requestCode;

    /**
     * The responseCode to test.
     */
    @Parameter( 1 )
    public ResponseCode responseCode;

    /**
     * Required success reponse to test .
     */
    @Parameter( 2 )
    public boolean requireSuccess;

    /**
     * Required client error reponse to test .
     */
    @Parameter( 3 )
    public boolean requireClientError;

    /**
     * Required server error reponse to test .
     */
    @Parameter( 4 )
    public boolean requireServerError;

    @Override
    protected String getConfigResources()
    {
        return "mule-server-config/noresponse/testserver1.xml";
    };

    /**
     * Create Coap Configuration
     */
    @Override
    protected Configuration getCoapConfiguration()
    {
        return super.getCoapConfiguration().set( CoapConfig.EXCHANGE_LIFETIME, 2, TimeUnit.SECONDS );
    }

    @Test
    public void testSuccess() throws ConnectorException, IOException
    {
        MuleEventSpy spy= new MuleEventSpy( "do_request" );
        spy.clear();

        String uri= "coap://localhost/do_request";
        setClientUri( uri );

        Request request= new Request( Code.POST, Type.NON );
        request
            .getOptions()
            .setNoResponse(
                ( !requireSuccess ? NoResponseOption.SUPPRESS_SUCCESS : 0 ) + ( !requireClientError ? NoResponseOption.SUPPRESS_CLIENT_ERROR : 0 ) + ( !requireServerError
                    ? NoResponseOption.SUPPRESS_SERVER_ERROR : 0 )
            );
        request.setPayload( responseCode.name() );
        CoapResponse response= client.advanced( request );

        assertEquals( "spy has not been called once", 1, spy.getEvents().size() );
        Message spied= (Message) spy.getEvents().get( 0 ).getContent();
        assertTrue( "wrong attributes class", spied.getAttributes().getValue() instanceof CoapRequestAttributes );
        CoapRequestAttributes attributes= (CoapRequestAttributes) spied.getAttributes().getValue();

        boolean responseReceived= response != null;
        if ( responseCode.isSuccess() )
        {
            assertEquals( "wrong succes response", requireSuccess, responseReceived );
        }
        else if ( responseCode.isClientError() )
        {
            assertEquals( "wrong client error response", requireClientError, responseReceived );
        }
        else if ( responseCode.isServerError() )
        {
            assertEquals( "wrong server error response", requireServerError, responseReceived );
        }
        assertEquals( "wrong requireSuccess attribute", requireSuccess, attributes.getRequestOptions().getRequireResponse().isSuccess() );
        assertEquals( "wrong requireClientError attribute", requireClientError, attributes.getRequestOptions().getRequireResponse().isClientError() );
        assertEquals( "wrong requireServerError attribute", requireServerError, attributes.getRequestOptions().getRequireResponse().isServerError() );
        assertEquals( "wrong require all attribute", requireSuccess && requireClientError && requireServerError, attributes.getRequestOptions().getRequireResponse().isAll() );
        assertEquals(
            "wrong require none attribute",
            !( requireSuccess || requireClientError || requireServerError ),
            attributes.getRequestOptions().getRequireResponse().isNone()
        );
    }
}
