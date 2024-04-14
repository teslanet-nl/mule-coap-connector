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
package nl.teslanet.mule.connectors.coap.api.config.security;


import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import nl.teslanet.mule.connectors.coap.api.binary.BytesConfig;


/**
 * PSK config. 
 */
public class PreSharedKey
{
    /**
     * The hostname of the peer that uses the key.
     * Needed for (client) endpoints that connect to multiple servers.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The hostname of the peer that uses the key. \nNeeded for (client) endpoints that connect to multiple servers." )
    private String host= null;

    /**
     * The virtual host to use the key for.
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The virtual host to use the key for." )
    private String virtualHost= null;

    /**
     * The port of the peer that uses the key.
     * Needed for  (client) endpoints that connect to multiple servers."
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The port of the peer that uses the key. \nNeeded for  (client) endpoints that connect to multiple servers." )
    private Integer port= null;

    /**
     * The identity of the key.
     */
    @Parameter
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The identity of the key." )
    private String identity= null;

    /**
     * The key configuration.
     */
    @Parameter
    @Expression( ExpressionSupport.NOT_SUPPORTED )
    @ParameterDsl( allowReferences= false )
    @Summary( "The key configuration." )
    private BytesConfig key= null;

    /**
     * Default constructor.
     */
    public PreSharedKey()
    {
        super();
        this.identity= null;
        this.key= null;
    }

    /**
     * Constructor using identity.
     * @param identity The identity of the key.
     * @param key The key in hexadecimal notation.
     */
    public PreSharedKey( String identity, BytesConfig key )
    {
        this( identity, key, null, null );
    }

    /**
     * Constructor using address and identity.
     * @param identity The identity of the key.
     * @param key The key in hexadecimal notation.
     * @param host The hostname of the server using the key.
     * @param port The port of the server using the key.
     */
    public PreSharedKey( String identity, BytesConfig key, String host, Integer port )
    {
        super();
        Validate.notEmpty( identity );
        Objects.requireNonNull( key );
        this.host= host;
        this.port= port;
        this.identity= identity;
        this.key= key;
    }

    /**
     * @return the host
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost( String host )
    {
        this.host= host;
    }

    /**
     * @return the virtualHost
     */
    public String getVirtualHost()
    {
        return virtualHost;
    }

    /**
     * @param virtualHost the virtualHost to set
     */
    public void setVirtualHost( String virtualHost )
    {
        this.virtualHost= virtualHost;
    }

    /**
     * @return the port
     */
    public Integer getPort()
    {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort( Integer port )
    {
        this.port= port;
    }

    /**
     * @return the identity
     */
    public String getIdentity()
    {
        return identity;
    }

    /**
     * @return the key
     */
    public BytesConfig getKey()
    {
        return key;
    }

    /**
     * Returns a hash code value for the object.
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 17, 37 ).append( host ).append( port ).append( identity ).toHashCode();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     */
    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( !( obj instanceof PreSharedKey ) )
        {
            return false;
        }
        PreSharedKey other= (PreSharedKey) obj;
        //check host
        if ( this.host == null )
        {
            if ( other.host != null ) return false;
        }
        else
        {
            if ( !this.host.equals( other.host ) ) return false;
        }
        //check port
        if ( this.port == null )
        {
            if ( other.port != null ) return false;
        }
        else
        {
            if ( !this.port.equals( other.port ) ) return false;
        }
        //check identity
        return this.identity == ( (PreSharedKey) obj ).identity;
    }
}
