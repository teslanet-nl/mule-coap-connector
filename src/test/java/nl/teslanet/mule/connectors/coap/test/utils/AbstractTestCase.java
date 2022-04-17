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
package nl.teslanet.mule.connectors.coap.test.utils;


import java.io.StringWriter;
import java.util.List;

import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.test.runner.ArtifactClassLoaderRunnerConfig;

import nl.teslanet.mule.connectors.coap.api.query.AbstractQueryParam;


@ArtifactClassLoaderRunnerConfig
(
    providedExclusions=
    { 
    },
    applicationRuntimeLibs= {
    }, 
    testRunnerExportedRuntimeLibs= { 
        "org.mule.tests:mule-tests-functional"
    },
    applicationSharedRuntimeLibs= {
        "org.eclipse.californium:californium-core",
        "org.eclipse.californium:element-connector",
        "org.eclipse.californium:scandium",
        "org.eclipse.californium:demo-certs"
    },
    exportPluginClasses= {
    }, 
    testExclusions= 
    {
        "org.mule.runtime:*:*:*:*",
        "org.mule.modules*:*:*:*:*",
        "org.mule.transports:*:*:*:*",
        "org.mule.mvel:*:*:*:*",
        "org.mule.extensions:*:*:*:*",
        "org.mule.connectors:*:*:*:*",
        "org.mule.tests.plugin:*:*:*:*",
        "com.mulesoft.mule.runtime*:*:*:*:*",
        "com.mulesoft.licm:*:*:*:*"
    }, 
    testInclusions= { 
        "*:*:jar:tests:*", 
        "*:*:test-jar:*:*"
    }, 
    extraPrivilegedArtifacts= {
    }
)
public abstract class AbstractTestCase extends MuleArtifactFunctionalTestCase
{
    /**
     * Create query part of an uri.
     * @param queryParams
     * @return The string representation of query parameters
     */
    protected static String queryString( List< ? extends AbstractQueryParam > queryParams )
    {
        if ( ( queryParams == null || queryParams.isEmpty() ) ) return "";
        StringWriter writer= new StringWriter();
        boolean first= true;
        for ( AbstractQueryParam param : queryParams )
        {
            if ( first )
            {
                first= false;
            }
            else
            {
                writer.append( "&" );
            }
            writer.append( param.toString() );
        }
        return writer.toString();
    }
}
