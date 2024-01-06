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
package nl.teslanet.mule.connectors.coap.test.config;


import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;


@RunWith( Parameterized.class )
public class ConfigCompletenessMetaTest
{
    /**
     * @return List of attributes to test.
     */
    @Parameters( name= "key = {0}" )
    public static Iterable< ConfigParam > attributeKeysToTest()
    {
        return Arrays.asList( ConfigParam.values() );
    }

    /**
     * The attribute to test.
     */
    @Parameter
    public ConfigParam attributeKey;

    /**
     * Test whether the attribute key is covered in tests.
     * @throws Exception
     */
    @Test
    public void testKeyIsCovered() throws Exception
    {
        ConfigDescription attribute= ConfigParams.getConfigDescription( attributeKey );
        assertNotNull( "could not get attribute description", attribute );
    }
}
