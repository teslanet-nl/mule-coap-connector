/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2021 (teslanet.nl) Rogier Cobben
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


import static org.mule.runtime.core.api.event.EventContextFactory.create;
import static org.mule.tck.MuleTestUtils.getTestFlow;

import javax.inject.Inject;

import org.mule.functional.junit4.ArtifactFunctionalTestCase;
import org.mule.runtime.api.component.location.ConfigurationComponentLocator;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.notification.NotificationListenerRegistry;
import org.mule.runtime.core.api.construct.FlowConstruct;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.event.EventContextService;
import org.mule.runtime.core.privileged.event.BaseEventContext;
import org.mule.test.runner.ArtifactClassLoaderRunnerConfig;


//TODO review
@ArtifactClassLoaderRunnerConfig(providedExclusions= { "org.mule.tests:*:*:*:*", "com.mulesoft.compatibility.tests:*:*:*:*" }, testExclusions= {
    "org.mule.runtime:*:*:*:*",
    "org.mule.modules*:*:*:*:*",
    "org.mule.transports:*:*:*:*",
    "org.mule.mvel:*:*:*:*",
    "org.mule.extensions:*:*:*:*",
    "org.mule.connectors:*:*:*:*",
    "org.mule.tests.plugin:*:*:*:*",
    "com.mulesoft.mule.runtime*:*:*:*:*",
    "com.mulesoft.licm:*:*:*:*" }, testInclusions= { "*:*:jar:tests:*", "*:*:test-jar:*:*" }, testRunnerExportedRuntimeLibs= {
        "org.mule.tests:mule-tests-functional" }, applicationSharedRuntimeLibs= { "org.eclipse.californium:demo-certs" })
public abstract class AbstractSecureTestCase extends ArtifactFunctionalTestCase
{

    @Inject
    protected ConfigurationComponentLocator locator;

    @Inject
    protected NotificationListenerRegistry notificationListenerRegistry;

    @Inject
    private EventContextService eventContextService;

    private CoreEvent testEvent;

    /* (non-Javadoc)
     * @see org.mule.tck.junit4.AbstractMuleTestCase#testEvent()
     */
    @Override
    protected CoreEvent testEvent() throws MuleException
    {
        if ( testEvent == null )
        {
            testEvent= baseEvent();
        }
        return testEvent;
    }

    private CoreEvent baseEvent() throws MuleException
    {
        FlowConstruct flowConstruct= getTestFlow( muleContext );
        return CoreEvent.builder( create( flowConstruct, TEST_CONNECTOR_LOCATION ) ).message( Message.of( TEST_PAYLOAD ) ).build();
    }

    @Override
    protected boolean doTestClassInjection()
    {
        return true;
    }

    @Override
    protected void doTearDown() throws Exception
    {
        if ( testEvent != null )
        {
            ( (BaseEventContext) testEvent.getContext() ).success();
        }
        super.doTearDown();

        if ( eventContextService != null )
        {
            eventContextService.getCurrentlyActiveFlowStacks();
        }
    }
}
