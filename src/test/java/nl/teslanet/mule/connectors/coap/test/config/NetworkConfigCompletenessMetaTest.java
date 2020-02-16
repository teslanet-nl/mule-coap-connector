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
package nl.teslanet.mule.connectors.coap.test.config;


import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class NetworkConfigCompletenessMetaTest
{
    /**
     * @return List of attributes to test.
     */
    @Parameters(name= "key = {0}")
    public static Iterable< String > attributeKeysToTest()
    {
        //Alas the keys is not an enum, so...
        //copy from NetworkConfig.Keys
        // regex: (public static final String \w+ = )("\w+");
        // replace: /* \1 */ \2,
        return Arrays.asList(

            /**
             * The maximum number of active peers supported.
             * <p>
             * An active peer is a node with which we exchange CoAP messages. For
             * each active peer we need to maintain some state, e.g. we need to keep
             * track of MIDs and tokens in use with the peer. It therefore is
             * reasonable to limit the number of peers so that memory consumption
             * can be better predicted.
             * <p>
             * The default value of this property is
             * {@link NetworkConfigDefaults#DEFAULT_MAX_ACTIVE_PEERS}.
             * <p>
             * For clients this value can safely be set to a small one or two digit
             * number as most clients will only communicate with a small set of
             * peers (servers).
             */
            /* public static final String MAX_ACTIVE_PEERS =  */ "MAX_ACTIVE_PEERS",
            /**
             * The maximum number of seconds a peer may be inactive for before it is
             * considered stale and all state associated with it can be discarded.
             */
            /* public static final String MAX_PEER_INACTIVITY_PERIOD =  */ "MAX_PEER_INACTIVITY_PERIOD",

            /* public static final String COAP_PORT =  */ "COAP_PORT",
            /* public static final String COAP_SECURE_PORT =  */ "COAP_SECURE_PORT",
            /* public static final String ACK_TIMEOUT =  */ "ACK_TIMEOUT",
            /* public static final String ACK_RANDOM_FACTOR =  */ "ACK_RANDOM_FACTOR",
            /* public static final String ACK_TIMEOUT_SCALE =  */ "ACK_TIMEOUT_SCALE",
            /* public static final String MAX_RETRANSMIT =  */ "MAX_RETRANSMIT",
            /**
             * The EXCHANGE_LIFETIME as defined by the CoAP spec in MILLISECONDS.
             */
            /* public static final String EXCHANGE_LIFETIME =  */ "EXCHANGE_LIFETIME",
            /* public static final String NON_LIFETIME =  */ "NON_LIFETIME",
            /* public static final String MAX_TRANSMIT_WAIT =  */ "MAX_TRANSMIT_WAIT",
            //NOT IN SCOPE (yet)            /* public static final String MAX_LATENCY =  */ "MAX_LATENCY",
            //NOT IN SCOPE (yet)             /* public static final String MAX_SERVER_RESPONSE_DELAY =  */ "MAX_SERVER_RESPONSE_DELAY",
            /* public static final String NSTART =  */ "NSTART",
            /* public static final String LEISURE =  */ "LEISURE",
            /* public static final String PROBING_RATE =  */ "PROBING_RATE",

            /* public static final String USE_RANDOM_MID_START =  */ "USE_RANDOM_MID_START",
            /* public static final String MID_TRACKER =  */ "MID_TACKER",
            /* public static final String MID_TRACKER_GROUPS =  */ "MID_TRACKER_GROUPS",
            /**
             * Base MID for multicast MID range. All multicast requests use the same
             * MID provider, which generates MIDs in the range [base...65536).
             * None multicast request use the range [0...base).
             * 0 := disable multicast support.
             */
            //NOT IN SCOPE (yet)            /* public static final String MULTICAST_BASE_MID =  */ "MULTICAST_BASE_MID",
            /* public static final String TOKEN_SIZE_LIMIT =  */ "TOKEN_SIZE_LIMIT",

            /**
             * The block size (number of bytes) to use when doing a blockwise
             * transfer. This value serves as the upper limit for block size in
             * blockwise transfers.
             */
            /* public static final String PREFERRED_BLOCK_SIZE =  */ "PREFERRED_BLOCK_SIZE",
            /**
             * The maximum payload size (in bytes) that can be transferred in a
             * single message, i.e. without requiring a blockwise transfer.
             * 
             * NB: this value MUST be adapted to the maximum message size supported
             * by the transport layer. In particular, this value cannot exceed the
             * network's MTU if UDP is used as the transport protocol.
             */
            /* public static final String MAX_MESSAGE_SIZE =  */ "MAX_MESSAGE_SIZE",
            /**
             * The maximum size of a resource body (in bytes) that will be accepted
             * as the payload of a POST/PUT or the response to a GET request in a
             * <em>transparent</em> blockwise transfer.
             * <p>
             * This option serves as a safeguard against excessive memory
             * consumption when many resources contain large bodies that cannot be
             * transferred in a single CoAP message. This option has no impact on
             * *manually* managed blockwise transfers in which the blocks are
             * handled individually.
             * <p>
             * Note that this option does not prevent local clients or resource
             * implementations from sending large bodies as part of a request or
             * response to a peer.
             * <p>
             * The default value of this property is
             * {@link NetworkConfigDefaults#DEFAULT_MAX_RESOURCE_BODY_SIZE}.
             * <p>
             * A value of {@code 0} turns off transparent handling of blockwise
             * transfers altogether.
             */
            /* public static final String MAX_RESOURCE_BODY_SIZE =  */ "MAX_RESOURCE_BODY_SIZE",
            /**
             * The maximum amount of time (in milliseconds) allowed between
             * transfers of individual blocks in a blockwise transfer before the
             * blockwise transfer state is discarded.
             * <p>
             * The default value of this property is
             * {@link NetworkConfigDefaults#DEFAULT_BLOCKWISE_STATUS_LIFETIME}.
             */
            /* public static final String BLOCKWISE_STATUS_LIFETIME =  */ "BLOCKWISE_STATUS_LIFETIME",

            /**
             * Property to indicate if the response should always include the Block2 option when client request early blockwise negociation but the response can be sent on one packet.
             * <p>
             * The default value of this property is
             * {@link NetworkConfigDefaults#DEFAULT_BLOCKWISE_STRICT_BLOCK2_OPTION}.
             * <p>
             * A value of {@code false} indicate that the server will respond without block2 option if no further blocks are required.<br/>
             * A value of {@code true} indicate that the server will response with block2 option event if no further blocks are required.
             *  
             */
            //NOT IN SCOPE (yet)            /* public static final String BLOCKWISE_STRICT_BLOCK2_OPTION =  */ "BLOCKWISE_STRICT_BLOCK2_OPTION",

            /* public static final String NOTIFICATION_CHECK_INTERVAL_TIME =  */ "NOTIFICATION_CHECK_INTERVAL",
            /* public static final String NOTIFICATION_CHECK_INTERVAL_COUNT =  */ "NOTIFICATION_CHECK_INTERVAL_COUNT",
            /* public static final String NOTIFICATION_REREGISTRATION_BACKOFF =  */ "NOTIFICATION_REREGISTRATION_BACKOFF",

            /* public static final String USE_CONGESTION_CONTROL =  */ "USE_CONGESTION_CONTROL",
            /* public static final String CONGESTION_CONTROL_ALGORITHM =  */ "CONGESTION_CONTROL_ALGORITHM",

            /* public static final String PROTOCOL_STAGE_THREAD_COUNT =  */ "PROTOCOL_STAGE_THREAD_COUNT",
            /* public static final String NETWORK_STAGE_RECEIVER_THREAD_COUNT =  */ "NETWORK_STAGE_RECEIVER_THREAD_COUNT",
            /* public static final String NETWORK_STAGE_SENDER_THREAD_COUNT =  */ "NETWORK_STAGE_SENDER_THREAD_COUNT",

            /* public static final String UDP_CONNECTOR_DATAGRAM_SIZE =  */ "UDP_CONNECTOR_DATAGRAM_SIZE",
            /* public static final String UDP_CONNECTOR_RECEIVE_BUFFER =  */ "UDP_CONNECTOR_RECEIVE_BUFFER",
            /* public static final String UDP_CONNECTOR_SEND_BUFFER =  */ "UDP_CONNECTOR_SEND_BUFFER",
            /* public static final String UDP_CONNECTOR_OUT_CAPACITY =  */ "UDP_CONNECTOR_OUT_CAPACITY",

            /* public static final String DEDUPLICATOR =  */ "DEDUPLICATOR",
            //NOT AN ATTRIBUTE /* public static final String DEDUPLICATOR_MARK_AND_SWEEP =  */ "DEDUPLICATOR_MARK_AND_SWEEP",
            /**
             * The interval after which the next sweep run should occur (in
             * MILLISECONDS).
             */
            /* public static final String MARK_AND_SWEEP_INTERVAL =  */ "MARK_AND_SWEEP_INTERVAL",
            //NOT AN ATTRIBUTE /* public static final String DEDUPLICATOR_CROP_ROTATION =  */ "DEDUPLICATOR_CROP_ROTATION",
            /* public static final String CROP_ROTATION_PERIOD =  */ "CROP_ROTATION_PERIOD",
            //NOT AN ATTRIBUTE /* public static final String NO_DEDUPLICATOR =  */ "NO_DEDUPLICATOR",
            /* public static final String RESPONSE_MATCHING =  */ "RESPONSE_MATCHING",

            //NOT IN SCOPE (yet) /* public static final String HTTP_PORT =  */ "HTTP_PORT",
            //NOT IN SCOPE (yet) /* public static final String HTTP_SERVER_SOCKET_TIMEOUT =  */ "HTTP_SERVER_SOCKET_TIMEOUT",
            //NOT IN SCOPE (yet) /* public static final String HTTP_SERVER_SOCKET_BUFFER_SIZE =  */ "HTTP_SERVER_SOCKET_BUFFER_SIZE",
            //NOT IN SCOPE (yet) /* public static final String HTTP_CACHE_RESPONSE_MAX_AGE =  */ "HTTP_CACHE_RESPONSE_MAX_AGE",
            //NOT IN SCOPE (yet) /* public static final String HTTP_CACHE_SIZE =  */ "HTTP_CACHE_SIZE",

            /* public static final String HEALTH_STATUS_INTERVAL =  */ "HEALTH_STATUS_INTERVAL",

            /** Properties for TCP connector. */
            //NOT IN SCOPE (yet) /* public static final String TCP_CONNECTION_IDLE_TIMEOUT =  */ "TCP_CONNECTION_IDLE_TIMEOUT",
            //NOT IN SCOPE (yet) /* public static final String TCP_CONNECT_TIMEOUT =  */ "TCP_CONNECT_TIMEOUT",
            //NOT IN SCOPE (yet) /* public static final String TCP_WORKER_THREADS =  */ "TCP_WORKER_THREADS",
            //NOT IN SCOPE (yet) /* public static final String TLS_HANDSHAKE_TIMEOUT =  */ "TLS_HANDSHAKE_TIMEOUT",

            /** Properties for encryption */
            /**
             * (D)TLS session timeout in seconds.
             */
            /* public static final String SECURE_SESSION_TIMEOUT =  */ "SECURE_SESSION_TIMEOUT",
            /**
             * DTLS auto resumption timeout in milliseconds. After that period
             * without exchanged messages, the session is forced to resume.
             */
            /* public static final String DTLS_AUTO_RESUME_TIMEOUT =  */ "DTLS_AUTO_RESUME_TIMEOUT"

        );
    }

    /**
     * The attribute to test.
     */
    @Parameter
    public String attributeKey;

    /**
     * Default no exception should be thrown.
     */
    @Rule
    public ExpectedException exception= ExpectedException.none();

    /**
     * Test whether the attribute key is covered in tests.
     * @throws Exception
     */
    @Test
    public void testKeyIsCovered() throws Exception
    {
        ConfigParamName attributeName= ConfigAttributes.getName( attributeKey );
        assertNotNull( "could not get ConfigParamName", attributeName );
    }
}
