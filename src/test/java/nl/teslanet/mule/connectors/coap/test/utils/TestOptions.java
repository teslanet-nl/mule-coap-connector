/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2023 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.test.utils;


import org.eclipse.californium.core.coap.option.OpaqueOptionDefinition;


/**
 * Options for testing.
 * 
 **/
public class TestOptions
{
    /**
     * Test option 65001.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65001= new OpaqueOptionDefinition( 65001, "Other-Option-65001" );

    /**
     * Test option 65002.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65002= new OpaqueOptionDefinition( 65002, "Other-Option-65002", false );

    /**
     * Test option 65008.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65008= new OpaqueOptionDefinition( 65008, "Other-Option-65008", false );

    /**
     * Test option 65009.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65009= new OpaqueOptionDefinition( 65009, "Other-Option-65009", false );

    /**
     * Test option 65012.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65010= new OpaqueOptionDefinition( 65010, "Other-Option-65010", false );

    /**
     * Test option 65012.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65012= new OpaqueOptionDefinition( 65012, "Other-Option-65012", false );

    /**
     * Test option 65013.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65013= new OpaqueOptionDefinition( 65013, "Other-Option-65013", false );

    /**
     * Test option 65015.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65015= new OpaqueOptionDefinition( 65015, "Other-Option-65015", false );

    /**
     * Test option 65016.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65016= new OpaqueOptionDefinition( 65016, "Other-Option-65016", false );

    /**
     * Test option 65018.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65018= new OpaqueOptionDefinition( 65018, "Other-Option-65018", false );

    /**
     * Test option 65020.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65020= new OpaqueOptionDefinition( 65020, "Other-Option-65020", false );

    /**
     * Test option 65028.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65028= new OpaqueOptionDefinition( 65028, "Other-Option-65028", true );

    /**
     * Test option 65029.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65029= new OpaqueOptionDefinition( 65029, "Other-Option-65029", true );

    /**
     * Test option 65304.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65304= new OpaqueOptionDefinition( 65304, "Other-Option-65304", true );

    /**
     * Test option 65308.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65308= new OpaqueOptionDefinition( 65308, "Other-Option-65308", true );

    /**
     * No object are instantiated.
     */
    private TestOptions()
    {
        //NOOP
    }
}
