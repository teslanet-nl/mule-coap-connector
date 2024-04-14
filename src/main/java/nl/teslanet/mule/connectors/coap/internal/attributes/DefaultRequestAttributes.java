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
package nl.teslanet.mule.connectors.coap.internal.attributes;


import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import nl.teslanet.mule.connectors.coap.api.CoapRequestAttributes;
import nl.teslanet.mule.connectors.coap.api.options.RequestOptionsAttributes;
import nl.teslanet.mule.connectors.coap.internal.options.ToStringStyle;


/**
* The attributes of a CoAP request that was received from a client.
*
*/
public class DefaultRequestAttributes extends CoapRequestAttributes
{
    /**
     * @param requestType The request type to set.
     */
    public void setRequestType( String requestType )
    {
        this.requestType= requestType;
    }

    /**
     * @param requestCode The requestCode to set.
     */
    public void setRequestCode( String requestCode )
    {
        this.requestCode= requestCode;
    }

    /**
     * @param localAddress The localAddress to set.
     */
    public void setLocalAddress( String localAddress )
    {
        this.localAddress= localAddress;
    }

    /**
     * @param requestUri The request uri to set.
     */
    public void setRequestUri( String requestUri )
    {
        this.requestUri= requestUri;
    }

    /**
     * @param relation The relation to set.
     */
    public void setRelation( String relation )
    {
        this.relation= relation;
    }

    /**
     * @param remoteAddress The remoteHost to set.
     */
    public void setRemoteAddress( String remoteAddress )
    {
        this.remoteAddress= remoteAddress;
    }

    /**
    * @param requestOptionAttributes The options to set.
    */
    public void setRequestOptionAttributes( RequestOptionsAttributes requestOptionAttributes )
    {
        this.options= requestOptionAttributes;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString( this, new ToStringStyle() );
    }
}
