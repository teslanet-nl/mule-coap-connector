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
package nl.teslanet.mule.connectors.coap.api.options;


import java.util.List;

import nl.teslanet.mule.connectors.coap.api.binary.BytesValue;
import nl.teslanet.mule.connectors.coap.api.query.QueryParamAttribute;


/**
 * The option parameters of a CoAP response.
 *
 */
public class ResponseOptionsAttributes
{
    /**
     * The request size that is acceptable for the server ac
     */
    protected Integer acceptableRequestSize= null;

    /**
     * The content format of the response payload.
     */
    protected Integer contentFormat= null;

    /**
     * The entity tag of the response payload content.
     */
    protected BytesValue etag= null;

    /**
     * The location path of the resource that is created.
     */
    protected List< String > locationPath= null;

    /**
     * The location query of the resource that is created.
     */
    protected List< QueryParamAttribute > locationQuery= null;

    /**
     * The max age of an observable resource.
     */
    protected Long maxAge= null;

    /**
     * The number of the notification of an observed resource.number
     */
    protected Integer observe= null;

    /**
     * The other response options.
     */
    protected List< OtherOptionAttribute > other= null;

    /**
     * The response payload size indication [bytes].
     */
    protected Integer responseSize= null;

    /**
     * @return The etag option.
     */
    public BytesValue getEtag()
    {
        return etag;
    }

    /**
     * @return the locationPath
     */
    public List< String > getLocationPath()
    {
        return locationPath;
    }

    /**
     * @return the contentFormat
     */
    public Integer getContentFormat()
    {
        return contentFormat;
    }

    /**
     * @return The maxAge option if present, otherwise null.
     */
    public Long getMaxAge()
    {
        return maxAge;
    }

    /**
     * Get the location query options as list.
     */
    public List< QueryParamAttribute > getLocationQuery()
    {
        return locationQuery;
    }

    /**
     * @return The size2 if present, otherwise null.
     */
    public Integer getResponseSize()
    {
        return responseSize;
    }

    /**
     * @return The size1 option if present, otherwise null.
     */
    public Integer getAcceptableRequestSize()
    {
        return acceptableRequestSize;
    }

    /**
     * @return The observe option if present, otherwise null.
     */
    public Integer getObserve()
    {
        return observe;
    }

    /**
     * Get the other response options.
     */
    public List< OtherOptionAttribute > getOther()
    {
        return other;
    }
}
