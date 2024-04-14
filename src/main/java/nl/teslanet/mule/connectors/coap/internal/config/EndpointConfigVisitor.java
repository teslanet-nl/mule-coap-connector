/*-
 * #%L
 * Mule CoAP Connector
 * %%
 * Copyright (C) 2019 - 2024 (teslanet.nl) Rogier Cobben
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
package nl.teslanet.mule.connectors.coap.internal.config;


import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.coap.option.OptionDefinition;
import org.eclipse.californium.core.network.CoapEndpoint;

import nl.teslanet.mule.connectors.coap.api.config.ConfigException;
import nl.teslanet.mule.connectors.coap.api.config.OptionParams;
import nl.teslanet.mule.connectors.coap.api.config.SocketParams;
import nl.teslanet.mule.connectors.coap.api.config.endpoint.AbstractEndpoint;
import nl.teslanet.mule.connectors.coap.internal.endpoint.EndpointOptionRegistry;
import nl.teslanet.mule.connectors.coap.internal.exceptions.EndpointConstructionException;
import nl.teslanet.mule.connectors.coap.internal.utils.MessageUtils;


/**
 * Endpoint configuration visitor.
 *
 */
public abstract class EndpointConfigVisitor extends ConfigurationVisitor
{
    /**
     * The name of the endpont 
     */
    private String endpointName;

    /**
     * Network interface to bind to.
     */
    private InetSocketAddress localAddress= null;

    /**
     * The Endpoint Builder that is used to collect endpoint configuration.
     */
    protected CoapEndpoint.Builder endpointBuilder= new CoapEndpoint.Builder();

    /**
     * Reuse address flag.
     */
    private boolean reuseAddress= false;

    /**
     * Other options that the endpoint expects.
     */
    private ArrayList< OptionDefinition > otherOptionDefs= new ArrayList<>();

    /**
     * Visit option parameters configuration object.
     * @param toVisit the object to visit.
     */
    @Override
    public void visit( OptionParams toVisit )
    {
        toVisit.otherOptionConfigs.forEach( otherOptionConfig -> otherOptionDefs.add( MessageUtils.toCfOptionDefinition( otherOptionConfig ) ) );
        endpointBuilder.setOptionRegistry( new EndpointOptionRegistry( otherOptionDefs.toArray( new OptionDefinition []{} ) ) );
    }

    /**
     * Visit Endpoint configuration object.
     * @param toVisit the object to visit.
     * @throws ConfigException When visit is not successful.
     */
    @Override
    public void visit( AbstractEndpoint toVisit ) throws ConfigException
    {
        super.visit( toVisit );
        endpointName= toVisit.configName;
    }

    /**
     * Visit socket parameters.
     * @param toVisit The object to visit.
     */
    @Override
    public void visit( SocketParams toVisit )
    {
        super.visit( toVisit );
        int port= ( toVisit.bindToPort != null ? toVisit.bindToPort : 0 );

        if ( toVisit.bindToHost != null )
        {
            localAddress= new InetSocketAddress( toVisit.bindToHost, port );
        }
        else
        {
            localAddress= new InetSocketAddress( port );
        }
        reuseAddress= toVisit.reuseAddress;
    }

    /**
     * @return The configured endpoint name.
     */
    public String getEndpointName()
    {
        return endpointName;
    }

    /**
     * @return the localAddress
     */
    public InetSocketAddress getLocalAddress()
    {
        return localAddress;
    }

    /**
     * @return {@code True} when to reuse address.
     */
    public boolean isReuseAddress()
    {
        return reuseAddress;
    }

    /**
     * @return The other option definitions.
     */
    public List< OptionDefinition > getOtherOptionDefs()
    {
        return otherOptionDefs;
    }

    /**
     * Build the endpoint using the configuration this visitor has collected.
     * @return The Endpoint.
     * @throws EndpointConstructionException When the configuration cannot be used to create an endpoint.
     */
    public abstract CoapEndpoint getEndpoint() throws EndpointConstructionException;
}
