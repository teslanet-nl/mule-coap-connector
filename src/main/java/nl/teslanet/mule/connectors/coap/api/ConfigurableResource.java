/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2024 - 2025 (teslanet.nl) Rogier Cobben
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


/**
 * Interface of a configurable resource.
 */
public interface ConfigurableResource
{
    /**
     * @return The get.
     */
    public boolean isGet();

    /**
     * @return The post.
     */
    public boolean isPost();

    /**
     * @return The put.
     */
    public boolean isPut();

    /**
     * @return The delete.
     */
    public boolean isDelete();

    /**
     * @return The fetch.
     */
    public boolean isFetch();

    /**
     * @return The patch.
     */
    public boolean isPatch();

    /**
     * @return The ipatch.
     */
    public boolean isIpatch();

    /**
     * @return The observable.
     */
    public boolean isObservable();

    /**
     * @return The earlyAck.
     */
    public boolean isEarlyAck();

    /**
     * @return The configured CoRE info.
     */
    public CoreInfo getCoreInfo();
    
    /**
     * @return The new-sub-resource configuration.
     */
    public NewSubResource getNewSubResource();


}
