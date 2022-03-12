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
package nl.teslanet.mule.connectors.coap.api.options;


import java.util.List;

import nl.teslanet.mule.connectors.coap.api.query.QueryParamAttribute;


/**
 * The option parameters of a CoAP response.
 *
 */
public class ResponseOptionsAttributes
{
    protected Integer contentFormat= null;

    protected Long maxAge= null;

    protected ETag etag= null;

    protected List< String > locationPath= null;

    protected List< QueryParamAttribute > locationQuery= null;

    protected Integer responseSize= null;

    protected Integer acceptableRequestSize= null;

    protected Integer observe= null;

    protected List< OtherOptionAttribute > otherOptions= null;

    /**
     * @return The etag option.
     */
    public ETag getEtag()
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
     * Get the other options.
     */
    public List< OtherOptionAttribute > getOtherOptions()
    {
        return otherOptions;
    }
}
