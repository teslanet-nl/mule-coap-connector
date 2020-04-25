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
package nl.teslanet.mule.connectors.coap.api;


import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import nl.teslanet.mule.connectors.coap.api.options.OptionAttributes;


public class ReceivedRequestAttributes
{
    /**
     * The CoAP request code that was issued.
     */
    private String requestCode= null;

    /**
     * When true, the request issued was confirmable.
     */
    private Boolean confirmable= null;

    /**
     * The server address the request was issued on. 
     */
    private String localAddress= null;

    /**
     * The uri of the resource the request was issued on. 
     */
    private String requestUri= null;

    /**
     * The key of the observe relation when the request is an observe request or notification. Null otherwise. 
     */
    private String relation= null;

    /**
     * The address of the client that issued the request.
     */
    private String remoteAddress= null;

    /**
     * The port that was used by the client ti issue the request.
     */
    private Integer remotePort= null;

    /**
     * The CoAP options that accompanied the request.
     */
    private OptionAttributes options= new OptionAttributes();

    /**
     * @return the requestCode
     */
    public String getRequestCode()
    {
        return requestCode;
    }

    /**
     * @param requestCode the requestCode to set
     */
    public void setRequestCode( String requestCode )
    {
        this.requestCode= requestCode;
    }

    /**
     * @return the confirmable
     */
    public Boolean getConfirmable()
    {
        return confirmable;
    }

    /**
     * @param confirmable the confirmable to set
     */
    public void setConfirmable( Boolean confirmable )
    {
        this.confirmable= confirmable;
    }

    /**
     * @return the localAddress
     */
    public String getLocalAddress()
    {
        return localAddress;
    }

    /**
     * @param localAddress the localAddress to set
     */
    public void setLocalAddress( String localAddress )
    {
        this.localAddress= localAddress;
    }

    /**
     * @return the requestUri
     */
    public String getRequestUri()
    {
        return requestUri;
    }

    /**
     * @param requestUri the requestUri to set
     */
    public void setRequestUri( String requestUri )
    {
        this.requestUri= requestUri;
    }

    /**
     * @return the relation
     */
    public String getRelation()
    {
        return relation;
    }

    /**
     * @param relation the relation to set
     */
    public void setRelation( String relation )
    {
        this.relation= relation;
    }

    /**
     * @return the remoteAddress
     */
    public String getRemoteAddress()
    {
        return remoteAddress;
    }

    /**
     * @param remoteAddress the remoteAddress to set
     */
    public void setRemoteAddress( String remoteAddress )
    {
        this.remoteAddress= remoteAddress;
    }

    /**
     * @return the remotePort
     */
    public Integer getRemotePort()
    {
        return remotePort;
    }

    /**
     * @param remotePort the remotePort to set
     */
    public void setRemotePort( Integer remotePort )
    {
        this.remotePort= remotePort;
    }

    /**
     * @return the options
     */
    public OptionAttributes getOptions()
    {
        return options;
    }

    /**
     * @param options the options to set
     */
    public void setOptions( OptionAttributes options )
    {
        this.options= options;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString( this, MULTI_LINE_STYLE );
    }
}
