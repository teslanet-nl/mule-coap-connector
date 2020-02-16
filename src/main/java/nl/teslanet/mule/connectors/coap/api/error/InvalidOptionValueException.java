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
package nl.teslanet.mule.connectors.coap.api.error;


import org.mule.runtime.extension.api.exception.ModuleException;


/**
 *  Exception that is thrown when an attempt is made to construct an option using an invalid value.
 */
public class InvalidOptionValueException extends ModuleException
{

    /**
     * serial version id
     */
    private static final long serialVersionUID= 1L;

    /**
     * Construct exception with given message.
     * @param message Description of the exception
     */
    public InvalidOptionValueException( String message )
    {
        super( message, Errors.INVALID_OPTION_VALUE );
    }

    /**
     * Construct exception with given message.
     * @param message description of the exception
     * @param cause underlying cause
     */
    public InvalidOptionValueException( String message, Throwable cause )
    {
        super( message, Errors.INVALID_OPTION_VALUE, cause );
    }

    /**
     * Construct exception for an option with given message.
     * @param optionName name of the option the exception occurred on
     * @param message description of the exception
     */
    public InvalidOptionValueException( String optionName, String message )
    {
        super( "Value of option " + optionName + " is invalid, " + message, Errors.INVALID_OPTION_VALUE );
    }

    /**
     * Construct exception for an option with given message.
     * @param optionName name of the option the exception occurred on
     * @param message description of the exception
     * @param cause underlying cause
     */
    public InvalidOptionValueException( String optionName, String message, Throwable cause )
    {
        super( "Value of option " + optionName + " is invalid, " + message, Errors.INVALID_OPTION_VALUE, cause );
    }

}
