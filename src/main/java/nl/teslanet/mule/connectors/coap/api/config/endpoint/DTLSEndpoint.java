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
package nl.teslanet.mule.connectors.coap.api.config.endpoint;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.config.ConfigException;
import nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DefaultReplayFilter;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsClientAndServerRole;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsEndpointRole;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsMessageParams;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsParams;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsResponseMatching;
import nl.teslanet.mule.connectors.coap.api.config.dtls.DtlsRetransmissionParams;
import nl.teslanet.mule.connectors.coap.api.config.dtls.ReplayFilter;
import nl.teslanet.mule.connectors.coap.api.config.security.SecurityParams;


/**
 * DTLS coap endpoint
 *
 */
@TypeDsl( allowInlineDefinition= true, allowTopLevelDefinition= true )
public class DTLSEndpoint extends AbstractEndpoint
{
    /**
     * The DTLS role of the endpoint.
     */
    @Parameter
    @Optional
    @NullSafe( defaultImplementingType= DtlsClientAndServerRole.class )
    @Summary( value= "The DTLS role of the endpoint." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @DisplayName( "DTLS Endpoint role" )
    public DtlsEndpointRole dtlsRole= null;

    /**
     * The security parameters.
     */
    @Parameter
    @Summary( value= "The security parameters." )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @DisplayName( "Encryption config" )
    public SecurityParams securityParams= null;

    /**
     * The DTLS response matcher defines the algorithm used to correlate responses to requests.
     */
    @Parameter
    @Optional( defaultValue= "STRICT" )
    @Summary( value= "The DTLS response matcher defines the algorithm used to correlate responses to requests." )
    @Example( value= "RELAXED" )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public DtlsResponseMatching responseMatching= DtlsResponseMatching.STRICT;

    /**
     * DTLS parameters.
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public DtlsParams dtlsParams;

    /**
     * DTLS message parameters.
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public DtlsMessageParams dtlsMessageParams;

    /**
     * DTLS retransmissions parameters.
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public DtlsRetransmissionParams dtlsRetransmissionsParams;

    /**
     * Anti replay filter.
     */
    @Parameter
    @Optional
    @NullSafe( defaultImplementingType= DefaultReplayFilter.class )
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    public ReplayFilter replayFilter= null;

    /**
     * Default Constructor used by Mule. 
     * Mandatory and Nullsafe params are set by Mule.
     */
    public DTLSEndpoint()
    {
        super();
    }

    /**
     * Constructor for manually constructing the endpoint.
     * (Mule uses default constructor and sets Nullsafe params.)
     * @param name the manually set name of the endpoint
     */
    public DTLSEndpoint( String name )
    {
        super( name );
        securityParams= new SecurityParams();
        dtlsRole= new DtlsClientAndServerRole();
        dtlsParams= new DtlsParams();
        dtlsMessageParams= new DtlsMessageParams();
        dtlsRetransmissionsParams= new DtlsRetransmissionParams();
        replayFilter= new DefaultReplayFilter();
    }

    /**
     * Accept visitor.
     */
    @Override
    public void accept( ConfigVisitor visitor ) throws ConfigException
    {
        super.accept( visitor );
        visitor.visit( this );
        securityParams.accept( visitor );
        dtlsRole.accept( visitor );
        dtlsParams.accept( visitor );
        dtlsMessageParams.accept( visitor );
        dtlsRetransmissionsParams.accept( visitor );
        if ( replayFilter != null ) replayFilter.accept( visitor );
    }
}
