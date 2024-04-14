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
package nl.teslanet.mule.connectors.coap.internal.exceptions;


/**
 *  Exception that is thrown an option occurs that is not defined.
 */
public class InternalUnkownOptionException extends Exception
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID= 1L;

    /**
     * Error message when number is given.
     */
    private static final String MSG_FORMAT_WITH_NUMBER= "CoAP Option{ %d } is not defined on endpoint.";

    /**
     * Error message when alias is given.
     */
    private static final String MSG_FORMAT_WITH_ALIAS= "CoAP Option{ %s } is not defined on endpoint.";

    /**
     * Construct exception for an option with given message.
     * @param number The number of the unrecognized option.
     */
    public InternalUnkownOptionException( int number )
    {
        super( String.format( MSG_FORMAT_WITH_NUMBER, number ) );
    }

    /**
     * Construct exception for an option with given message.
     * @param number The number of the unrecognized option.
     * @param cause The underlying cause.
     */
    public InternalUnkownOptionException( int number, Throwable cause )
    {
        super( String.format( MSG_FORMAT_WITH_NUMBER, number ), cause );
    }

    /**
     * Construct exception for an option with given message.
     * @param alias The name of the option the exception occurred on.
     */
    public InternalUnkownOptionException( String alias )
    {
        super( String.format( MSG_FORMAT_WITH_ALIAS, alias ) );
    }

    /**
     * Construct exception for an option with given message.
     * @param alias The name of the option the exception occurred on.
     * @param cause The underlying cause.
     */
    public InternalUnkownOptionException( String alias, Throwable cause )
    {
        super( String.format( MSG_FORMAT_WITH_ALIAS, alias ), cause );
    }
}
