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
package nl.teslanet.mule.connectors.coap.internal.exceptions;


/**
 * InternalResourceRegistryException is thrown when an error on a servers resource registry occurred.
 */
public class InternalResourceRegistryException extends Exception
{
    /**
     * Serial version ID
     */
    private static final long serialVersionUID= 1L;

    /**
     * Construct exception with given 
     * @param message
     */
    public InternalResourceRegistryException( String message )
    {
        super( message );
    }

    /**
     * Construct exception with given 
     * @param message
     * @param e The cause of the exception
     */
    public InternalResourceRegistryException( String message, Throwable e )
    {
        super( message, e );
    }

    /**
     * Construct exception with given 
     * @param e The cause of the exception
     */
    public InternalResourceRegistryException( Throwable e )
    {
        super( e );
    }

}
