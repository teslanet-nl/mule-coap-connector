/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2023 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.test;


import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertFalse;
import static org.mule.runtime.module.extension.api.util.MuleExtensionUtils.loadExtension;

import java.io.IOException;
import java.util.HashSet;

import javax.xml.transform.Source;

import org.junit.Before;
import org.junit.Test;
import org.mule.runtime.api.dsl.DslResolvingContext;
import org.mule.runtime.api.meta.model.ExtensionModel;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.internal.dsl.DefaultDslResolvingContext;
import org.mule.runtime.module.extension.internal.capability.xml.schema.DefaultExtensionSchemaGenerator;
//import org.mule.runtime.module.extension.internal.capability.xml.schema.ClasspathBasedDslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import nl.teslanet.mule.connectors.coap.internal.CoapConnector;


public class ExtensionXsdTest
{
    private static final Logger LOGGER= LoggerFactory.getLogger( ExtensionXsdTest.class.getCanonicalName() );

    private static final String SCHEMA_PATH= "schemata/mule-coap.xsd";

    private ExtensionModel extensionModel;

    private DslResolvingContext dslContext;

    @Before
    public void before()
    {
        extensionModel= loadExtension( CoapConnector.class );
        HashSet< ExtensionModel > models= new HashSet< ExtensionModel >();
        models.add( extensionModel );
        dslContext= new DefaultDslResolvingContext( models );
        //dslContext= new ClasspathBasedDslContext( ExtensionXsdTest.class.getClassLoader());
    }

    @Test
    public void xsdTest() throws IOException
    {
        DefaultExtensionSchemaGenerator generator= new DefaultExtensionSchemaGenerator();
        String schema= generator.generate( extensionModel, dslContext );

        String expected= IOUtils.toString( currentThread().getContextClassLoader().getResource( SCHEMA_PATH ).openStream() );

        Source expectedSource= Input.from( expected ).build();
        Source contentSource= Input.from( schema ).build();
        Diff diff= DiffBuilder.compare( expectedSource ).withTest( contentSource ).checkForSimilar()
                        //.checkForIdentical() 
                        .ignoreComments().ignoreWhitespace().normalizeWhitespace()
                        //.withComparisonController(ComparisonController) 
                        //.withComparisonFormatter(comparisonFormatter)
                        //.withComparisonListeners(comparisonListeners) 
                        //.withDifferenceEvaluator(differenceEvaluator) 
                        //.withDifferenceListeners(comparisonListeners)
                        //.withNodeMatcher(nodeMatcher) 
                        //.withAttributeFilter(attributeFilter) 
                        //.withNodeFilter(nodeFilter) 
                        //.withNamespaceContext(map)
                        //.withDocumentBuilerFactory(factory)
                        .ignoreElementContentWhitespace().build();

        if ( diff.hasDifferences() )
        {
            LOGGER.error( "\n---\n" + schema + "\n---" );
        }
        assertFalse( diff.toString(), diff.hasDifferences() );

    }
}
