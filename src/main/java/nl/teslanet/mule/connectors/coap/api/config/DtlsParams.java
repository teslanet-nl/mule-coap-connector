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
package nl.teslanet.mule.connectors.coap.api.config;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Configuration of which it is not clear whether it is really used.
 *
 */
public class DtlsParams implements VisitableConfig
{
    /**
     * The DTLS response matcher defines the algorithm used to correlate responses to requests.
     */
    @Parameter
    @Optional //TODO does not work: @Optional(defaultValue= "STRICT")
    @Summary(value= "The DTLS response matcher defines the algorithm used to correlate responses to requests.")
    @Example(value= "STRICT")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public DtlsResponseMatchingName responseMatching= DtlsResponseMatchingName.STRICT;

    /**
     * DTLS auto resumption timeout in milliseconds [ms]. After that period without
     * exchanged messages, the session is forced to resume.
     */
    @Parameter
    @Optional(defaultValue= "30000")
    @Summary(value= "DTLS auto resumption timeout in milliseconds [ms]. After that period without exchanged messages, the session is forced to resume.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public Integer dtlsAutoResumeTimeout= null;

    /**
     * DTLS session timeout in seconds [s]. Default value is 60 * 60 * 24 = 86400 s (24 hours).
     */
    @Parameter
    @Optional(defaultValue= "86400")
    @Summary(value= "TLS session timeout in seconds [s]. Default value is 24 hours.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public Long secureSessionTimeout= null;

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.VisitableConfig#accept(nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor)
     */
    @Override
    public void accept( ConfigVisitor visitor )
    {
        visitor.visit( this );
    }

}
