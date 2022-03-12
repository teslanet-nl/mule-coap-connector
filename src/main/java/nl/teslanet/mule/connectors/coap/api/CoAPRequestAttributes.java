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
package nl.teslanet.mule.connectors.coap.api;


import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import nl.teslanet.mule.connectors.coap.api.options.RequestOptionsAttributes;


/**
* The attributes of a CoAP request that was received from a client.
*
*/
public class CoAPRequestAttributes
{
    /**
     * The CoAP request code that was issued.
     */
    protected String requestCode= null;

    /**
     * When true, the request issued was confirmable.
     */
    protected Boolean confirmable= null;

    /**
     * The server address the request was issued on. 
     */
    protected String localAddress= null;

    /**
     * The uri of the request. 
     */
    protected String requestUri= null;

    /**
     * The address of the client that issued the request.
     */
    protected String remoteAddress= null;

    /**
     * The key of the observe relation when the request is an observe request or notification. Null otherwise. 
     */
    protected String relation= null;

    /**
     * The CoAP options that accompanied the request.
     */
    protected RequestOptionsAttributes options= null;

    /**
     * @return The requestCode.
     */
    public String getRequestCode()
    {
        return requestCode;
    }

    /**
     * @return The confirmable.
     */
    public Boolean getConfirmable()
    {
        return confirmable;
    }

    /**
     * @return The localAddress.
     */
    public String getLocalAddress()
    {
        return localAddress;
    }

    /**
     * @return The request uri.
     */
    public String getRequestUri()
    {
        return requestUri;
    }

    /**
     * @return The relation
     */
    public String getRelation()
    {
        return relation;
    }

    /**
     * @return The remoteHost.
     */
    public String getRemoteAddress()
    {
        return remoteAddress;
    }

    /**
     * @return The options.
     */
    public RequestOptionsAttributes getOptions()
    {
        return options;
    }

    /**
     * Get the string representation.
     */
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString( this, MULTI_LINE_STYLE );
    }
}
