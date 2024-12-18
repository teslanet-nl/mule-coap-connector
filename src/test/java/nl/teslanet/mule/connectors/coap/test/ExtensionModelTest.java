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
/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package nl.teslanet.mule.connectors.coap.test;


import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mule.runtime.module.extension.api.util.MuleExtensionUtils.loadExtension;

import javax.xml.transform.Source;

import org.junit.Before;
import org.junit.Test;
import org.mule.runtime.api.meta.model.ExtensionModel;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.extension.api.resources.GeneratedResource;
import org.mule.runtime.extension.api.resources.spi.GeneratedResourceFactory;
import org.mule.runtime.module.extension.internal.resources.AbstractGeneratedResourceFactoryTestCase;
import org.mule.runtime.module.extension.internal.resources.documentation.ExtensionDocumentationResourceGenerator;
import org.mule.tck.size.SmallTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import nl.teslanet.mule.connectors.coap.internal.CoapConnector;


@SmallTest
public class ExtensionModelTest extends AbstractGeneratedResourceFactoryTestCase
{

    private static final Logger LOGGER= LoggerFactory.getLogger( ExtensionModelTest.class.getCanonicalName() );

    private static final String descriptionPath= "schemata/coap-extension-descriptions.xml";

    private ExtensionDocumentationResourceGenerator resourceFactory= new ExtensionDocumentationResourceGenerator();

    private ExtensionModel extensionModel;

    @Before
    public void before()
    {
        extensionModel= loadExtension( CoapConnector.class );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    protected Class< ? extends GeneratedResourceFactory >[] getResourceFactoryTypes()
    {
        return new Class []{ ExtensionDocumentationResourceGenerator.class };
    }

    @Test
    public void generate() throws Exception
    {
        GeneratedResource resource= resourceFactory.generateResource( extensionModel ).get();
        assertEquals( "unexpected model path", descriptionPath, "schemata/" + resource.getPath() );
        String expected= IOUtils.toString( currentThread().getContextClassLoader().getResource( descriptionPath ).openStream() );
        String content= new String( resource.getContent() );
        Source expectedSource= Input.from( expected ).build();
        Source contentSource= Input.from( content ).build();
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
            LOGGER.error( "\n---\n" + content + "\n---" );
        }
        assertFalse( diff.toString(), diff.hasDifferences() );

    }
}
