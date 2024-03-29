/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2022 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.api.config;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Configuration defined by Cf but not really used.
 *
 */
public class Unused implements VisitableConfig
{
    // TODO review for removed and new config items in cf3
    /**
     * RFC 7252: MAX_TRANSMIT_WAIT is the maximum time from the first transmission
     * of a Confirmable message to the time when the sender gives up on
     * receiving an acknowledgement or reset.  Derived from:
     * {@code ACK_TIMEOUT * ((2 ** (MAX_RETRANSMIT + 1)) - 1) * ACK_RANDOM_FACTOR }  
     * @see <a href=
     *      "https://www.rfc-editor.org/rfc/rfc7252.html#section-4.8.2">IETF RFC 7252 -
     *      4.8.2 Time Values Derived from Transmission Parameters</a>
     */
    @Parameter
    @Optional( defaultValue= "93000" )
    @Summary(
                    value= "The maximum time (in milliseconds [ms]) between the first transmission of a Confirmable message \nand the sender giving up on receiving an acknowledgement or reset."
    )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Long maxTransmitWait= null;

    //TODO used in cf3
    /**
     * LEISURE is the period of time (in milliseconds [ms]) of the spreading of responses to a
     * multicast request, for network congestion prevention.
     * @see <a href=
     *      "https://www.rfc-editor.org/rfc/rfc7252.html#section-8.2">IETF RFC 7252 -
     *      8.2. Request/Response Layer</a>
     */
    @Parameter
    @Optional( defaultValue= "5000" )
    @Summary( value= "Period of time (in milliseconds [ms]) of the spreading of responses to a multicast request, \nfor network congestion prevention." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Integer leisure= null;

    /**
     * PROBING_RATE [byte/s] indicates the maximum average data rate of sending to another endpoint that does not respond.
     * @see <a href=
     *      "https://www.rfc-editor.org/rfc/rfc7252.html#section-4.7">IETF RFC 7252 - 4.7. Congestion Control</a>
     */
    @Parameter
    @Optional( defaultValue= "1.0" )
    @Summary( value= "Maximum average data rate (in [bytes/second]) of sending to another endpoint that does not respond. " )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public Float probingRate= null;

    //TODO used in cf3
    /**
     * Maximum number of pending outbound messages.
     */
    @Parameter
    @Optional( defaultValue= "1000" )
    @Summary( value= "Maximum number of pending outbound messages. Default" )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public int udpConnectorOutCapacity= 1000;

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.VisitableConfig#accept(nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor)
     */
    @Override
    public void accept( ConfigVisitor visitor )
    {
        //visitor.visit( this );
    }

}
