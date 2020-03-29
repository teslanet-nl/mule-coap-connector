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
package nl.teslanet.mule.connectors.coap.api.options;

import java.util.List;

import org.mule.runtime.api.util.MultiMap;

/**
 * Options interface.
 *
 */
public interface Options
{
    /**
     * @return the ifMatchList
     */
    public List< ETag > getIfMatchList();

    /**
     * @return the uriHost
     */
    public String getUriHost();

    /**
     * @return the etagList
     */
    public List< ETag > getEtagList();

    /**
     * @return the ifNoneMatch
     */
    public Boolean getIfNoneMatch();

    /**
     * @return the uriPort
     */
    public Integer getUriPort();

    /**
     * @return the locationPath
     */
    public String getLocationPath();

    /**
     * @return the locationPathList
     */
    public List< String > getLocationPathList();

    /**
     * @return the uriPath
     */
    public String getUriPath();

    /**
     * @return the uriPathList
     */
    public List< String > getUriPathList();

    /**
     * @return the contentFormat
     */
    public Integer getContentFormat();

    /**
     * @return the maxAge
     */
    public Long getMaxAge();

    /**
     * @return the uriQuery
     */
    public String getUriQuery();

    /**
     * @return the uriQueryList
     */
    public List< String > getUriQueryList();

    /**
     * @return the accept
     */
    public Integer getAccept();

    /**
     * @return the locationQuery
     */
    public String getLocationQuery();

    /**
     * @return the locationQueryList
     */
    public List< String > getLocationQueryList();

    /**
     * @return the proxyUri
     */
    public String getProxyUri();

    /**
     * @return the proxyScheme
     */
    public String getProxyScheme();

    /**
     * @return the block1
     */
    public BlockValue getBlock1();

    /**
     * @return the block2
     */
    public BlockValue getBlock2();

    /**
     * @return the size1
     */
    public Integer getSize1();

    /**
     * @return the size2
     */
    public Integer getSize2();

    /**
     * @return the observe
     */
    public Integer getObserve();

    /**
     * @return the oscore
     */
    //TODO
    //public String getOscore();

    /**
     * @return other options map.
     */
    public MultiMap< String, Object > getOtherOptions();

}
