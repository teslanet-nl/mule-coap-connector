/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2023 - 2024 (teslanet.nl) Rogier Cobben
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


import java.util.HashMap;
import java.util.Map;

import org.eclipse.californium.core.coap.option.OpaqueOptionDefinition;

import nl.teslanet.mule.connectors.coap.api.config.options.OptionFormat;
import nl.teslanet.mule.connectors.coap.api.config.options.OtherOptionConfig;


/**
 * Options for testing.
 * 
 **/
public class TestOptions
{
    /**
     * Test option 65001.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65001= new OpaqueOptionDefinition( 65001, "option-65001", false, 0, 200 );

    /**
     * Test option 65002.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65002= new OpaqueOptionDefinition( 65002, "option-65002", false, 0, 200 );

    /**
     * Test option 65008.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65008= new OpaqueOptionDefinition( 65008, "option-65008", false, 0, 200 );

    /**
     * Test option 65009.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65009= new OpaqueOptionDefinition( 65009, "option-65009", false, 0, 200 );

    /**
     * Test option 65012.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65010= new OpaqueOptionDefinition( 65010, "option-65010", false, 0, 200 );

    /**
     * Test option 65012.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65012= new OpaqueOptionDefinition( 65012, "option-65012", false, 0, 200 );

    /**
     * Test option 65013.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65013= new OpaqueOptionDefinition( 65013, "option-65013", false, 0, 200 );

    /**
     * Test option 65015.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65015= new OpaqueOptionDefinition( 65015, "option-65015", false, 0, 200 );

    /**
     * Test option 65016.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65016= new OpaqueOptionDefinition( 65016, "option-65016", false, 0, 200 );

    /**
     * Test option 65018.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65018= new OpaqueOptionDefinition( 65018, "option-65018", false, 0, 200 );

    /**
     * Test option 65020.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65020= new OpaqueOptionDefinition( 65020, "option-65020", false, 0, 200 );

    /**
     * Test option 65028.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65028= new OpaqueOptionDefinition( 65028, "option-65028", true, 0, 200 );

    /**
     * Test option 65029.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65029= new OpaqueOptionDefinition( 65029, "option-65029", true, 0, 200 );

    /**
     * Test option 65304.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65304= new OpaqueOptionDefinition( 65304, "option-65304", true, 0, 200 );

    /**
     * Test option 65308.
     */
    public static final OpaqueOptionDefinition OTHER_OPTION_65308= new OpaqueOptionDefinition( 65308, "option-65308", true, 0, 200 );

    /**
     * Test option definitions array.
     */
    public static final OpaqueOptionDefinition[] OTHER_OPTION_DEFS= {
        OTHER_OPTION_65001,
        OTHER_OPTION_65002,
        OTHER_OPTION_65008,
        OTHER_OPTION_65009,
        OTHER_OPTION_65010,
        OTHER_OPTION_65012,
        OTHER_OPTION_65013,
        OTHER_OPTION_65015,
        OTHER_OPTION_65016,
        OTHER_OPTION_65018,
        OTHER_OPTION_65020,
        OTHER_OPTION_65028,
        OTHER_OPTION_65029,
        OTHER_OPTION_65304,
        OTHER_OPTION_65308 };

    /**
     * No object are instantiated.
     */
    private TestOptions()
    {
        //NOOP
    }

    public static Map< Integer, OtherOptionConfig > getOtherOptionconfigs()
    {

        HashMap< Integer, OtherOptionConfig > otherOptionConfigs= new HashMap<>();
        for ( OpaqueOptionDefinition definition : OTHER_OPTION_DEFS )
        {
            otherOptionConfigs.put(
                definition.getNumber(),
                new OtherOptionConfig(
                    definition.getName(),
                    definition.getNumber(),
                    OptionFormat.OPAQUE,
                    definition.isSingleValue(),
                    definition.getValueLengths()[0],
                    definition.getValueLengths()[1]
                )
            );
        }
        return otherOptionConfigs;
    }
}
