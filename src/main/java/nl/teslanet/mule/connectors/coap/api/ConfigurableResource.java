/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2024 (teslanet.nl) Rogier Cobben
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
     * @return the get
     */
    public boolean isGet();

    /**
     * @return the post
     */
    public boolean isPost();

    /**
     * @return the put
     */
    public boolean isPut();

    /**
     * @return the delete
     */
    public boolean isDelete();

    /**
     * @return the fetch
     */
    public boolean isFetch();

    /**
     * @return the patch
     */
    public boolean isPatch();

    /**
     * @return the ipatch
     */
    public boolean isIpatch();

    /**
     * @return the observable
     */
    public boolean isObservable();

    /**
     * @return the earlyAck.
     */
    public boolean isEarlyAck();

    /**
     * @return the configured CoRE info.
     */
    public CoreInfo getCoreInfo();
}
