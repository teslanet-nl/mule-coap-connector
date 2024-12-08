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
package nl.teslanet.mule.connectors.coap.internal.exceptions;


/**
 *  Exception that is thrown when an attempt is made to construct an option using an invalid value.
 */
public class InternalInvalidOptionValueException extends Exception
{

    /**
     * serial version id
     */
    private static final long serialVersionUID= 1L;

    /**
     * Error message when alias is given.
     */
    private static final String MSG_FORMAT_WITH_ALIAS= "Option{ %s } has invalid value: %s";

    /**
     * Construct exception for an option with given message.
     * @param alias The name of the option the exception occurred on
     * @param message description of the exception
     */
    public InternalInvalidOptionValueException( String alias, String message )
    {
        super( String.format( MSG_FORMAT_WITH_ALIAS, alias, message ) );
    }

    /**
     * Construct exception for an option with given message.
     * @param alias The name of the option the exception occurred on
     * @param message description of the exception
     * @param cause underlying cause
     */
    public InternalInvalidOptionValueException( String alias, String message, Throwable cause )
    {
        super( String.format( MSG_FORMAT_WITH_ALIAS, alias, message ), cause );
    }

}
