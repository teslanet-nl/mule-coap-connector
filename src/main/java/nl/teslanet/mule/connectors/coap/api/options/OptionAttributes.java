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
package nl.teslanet.mule.connectors.coap.api.options;


import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.mule.runtime.api.util.MultiMap;


/**
 * The option parameters of a received request or response.
 *
 */
public class OptionAttributes
{
    /*
     * Options defined by the CoAP protocol
     */
    private List< ETag > ifMatchList= null;

    private String uriHost= null;

    private List< ETag > etagList= null;

    private boolean ifNoneMatch= false;

    private Integer uriPort= null;

    private String locationPath= null;

    private List< String > locationPathList= null;

    private String uriPath= null;

    private List< String > uriPathList= null;

    private Integer contentFormat= null;

    private Long maxAge= null;

    private String uriQuery= null;

    private List< String > uriQueryList= null;

    private Integer accept= null;

    private String locationQuery= null;

    private List< String > locationQueryList= null;

    private String proxyUri= null;

    private String proxyScheme= null;

    private BlockValue block1= null;

    private BlockValue block2= null;

    private Integer size1= null;

    private Integer size2= null;

    private Integer observe= null;

    //TODO
    //private byte[] oscore= null;

    /**
     * The other CoAP options received.
     */
    private MultiMap< String, Object > otherOptions= new MultiMap< String, Object >();

    /**
     * @return the ifMatchList
     */
    public List< ETag > getIfMatchList()
    {
        return ifMatchList;
    }

    /**
     * @param ifMatchList the ifMatchList to set
     */
    public void setIfMatchList( List< ETag > ifMatchList )
    {
        this.ifMatchList= ifMatchList;
    }

    /**
     * @return the uriHost
     */
    public String getUriHost()
    {
        return uriHost;
    }

    /**
     * @param uriHost the uriHost to set
     */
    public void setUriHost( String uriHost )
    {
        this.uriHost= uriHost;
    }

    /**
     * @return the etagList
     */
    public List< ETag > getEtagList()
    {
        return etagList;
    }

    /**
     * @param etagList the etagList to set
     */
    public void setEtagList( List< ETag > etagList )
    {
        this.etagList= etagList;
    }

    /**
     * @return the ifNoneMatch
     */
    public boolean isIfNoneMatch()
    {
        return ifNoneMatch;
    }

    /**
     * @param ifNoneMatch the ifNoneMatch to set
     */
    public void setIfNoneMatch( boolean ifNoneMatch )
    {
        this.ifNoneMatch= ifNoneMatch;
    }

    /**
     * @return the uriPort
     */
    public Integer getUriPort()
    {
        return uriPort;
    }

    /**
     * @param uriPort the uriPort to set
     */
    public void setUriPort( Integer uriPort )
    {
        this.uriPort= uriPort;
    }

    /**
     * @return the locationPath
     */
    public String getLocationPath()
    {
        return locationPath;
    }

    /**
     * @param locationPath the locationPath to set
     */
    public void setLocationPath( String locationPath )
    {
        this.locationPath= locationPath;
    }

    /**
     * @return the locationPathList
     */
    public List< String > getLocationPathList()
    {
        return locationPathList;
    }

    /**
     * @param locationPathList the locationPathList to set
     */
    public void setLocationPathList( List< String > locationPathList )
    {
        this.locationPathList= locationPathList;
    }

    /**
     * @return the uriPath
     */
    public String getUriPath()
    {
        return uriPath;
    }

    /**
     * @param uriPath the uriPath to set
     */
    public void setUriPath( String uriPath )
    {
        this.uriPath= uriPath;
    }

    /**
     * @return the uriPathList
     */
    public List< String > getUriPathList()
    {
        return uriPathList;
    }

    /**
     * @param uriPathList the uriPathList to set
     */
    public void setUriPathList( List< String > uriPathList )
    {
        this.uriPathList= uriPathList;
    }

    /**
     * @return the contentFormat
     */
    public Integer getContentFormat()
    {
        return contentFormat;
    }

    /**
     * @param contentFormat the contentFormat to set
     */
    public void setContentFormat( Integer contentFormat )
    {
        this.contentFormat= contentFormat;
    }

    /**
     * @return the maxAge
     */
    public Long getMaxAge()
    {
        return maxAge;
    }

    /**
     * @param maxAge the maxAge to set
     */
    public void setMaxAge( Long maxAge )
    {
        this.maxAge= maxAge;
    }

    /**
     * @return the uriQuery
     */
    public String getUriQuery()
    {
        return uriQuery;
    }

    /**
     * @param uriQuery the uriQuery to set
     */
    public void setUriQuery( String uriQuery )
    {
        this.uriQuery= uriQuery;
    }

    /**
     * @return the uriQueryList
     */
    public List< String > getUriQueryList()
    {
        return uriQueryList;
    }

    /**
     * @param uriQueryList the uriQueryList to set
     */
    public void setUriQueryList( List< String > uriQueryList )
    {
        this.uriQueryList= uriQueryList;
    }

    /**
     * @return the accept
     */
    public Integer getAccept()
    {
        return accept;
    }

    /**
     * @param accept the accept to set
     */
    public void setAccept( Integer accept )
    {
        this.accept= accept;
    }

    /**
     * @return the locationQuery
     */
    public String getLocationQuery()
    {
        return locationQuery;
    }

    /**
     * @param locationQuery the locationQuery to set
     */
    public void setLocationQuery( String locationQuery )
    {
        this.locationQuery= locationQuery;
    }

    /**
     * @return the locationQueryList
     */
    public List< String > getLocationQueryList()
    {
        return locationQueryList;
    }

    /**
     * @param locationQueryList the locationQueryList to set
     */
    public void setLocationQueryList( List< String > locationQueryList )
    {
        this.locationQueryList= locationQueryList;
    }

    /**
     * @return the proxyUri
     */
    public String getProxyUri()
    {
        return proxyUri;
    }

    /**
     * @param proxyUri the proxyUri to set
     */
    public void setProxyUri( String proxyUri )
    {
        this.proxyUri= proxyUri;
    }

    /**
     * @return the proxyScheme
     */
    public String getProxyScheme()
    {
        return proxyScheme;
    }

    /**
     * @param proxyScheme the proxyScheme to set
     */
    public void setProxyScheme( String proxyScheme )
    {
        this.proxyScheme= proxyScheme;
    }

    /**
     * @return the block1
     */
    public BlockValue getBlock1()
    {
        return block1;
    }

    /**
     * @param block1 the block1 to set
     */
    public void setBlock1( BlockValue block1 )
    {
        this.block1= block1;
    }

    /**
     * @return the block2
     */
    public BlockValue getBlock2()
    {
        return block2;
    }

    /**
     * @param block2 the block2 to set
     */
    public void setBlock2( BlockValue block2 )
    {
        this.block2= block2;
    }

    /**
     * @return the size1
     */
    public Integer getSize1()
    {
        return size1;
    }

    /**
     * @param size1 the size1 to set
     */
    public void setSize1( Integer size1 )
    {
        this.size1= size1;
    }

    /**
     * @return the size2
     */
    public Integer getSize2()
    {
        return size2;
    }

    /**
     * @param size2 the size2 to set
     */
    public void setSize2( Integer size2 )
    {
        this.size2= size2;
    }

    /**
     * @return the observe
     */
    public Integer getObserve()
    {
        return observe;
    }

    /**
     * @param observe the observe to set
     */
    public void setObserve( Integer observe )
    {
        this.observe= observe;
    }

    /**
     * @return other options map.
     */
    public MultiMap< String, Object > getOtherOptions()
    {
        return otherOptions;
    }

    /**
     * @param otherOptions the other options to set
     */
    public void setOtherOptions( MultiMap< String, Object > otherOptions )
    {
        this.otherOptions= otherOptions;
    }

    /**
     * @param otherOptions the other options to set
     */
    /**
     * Add other option to attributes.
     * @param optionNr the option number.
     * @param value the option value.
     */
    public void addOtherOption( String optionNr, Object value )
    {
        this.otherOptions.put( optionNr, value );
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString( this, MULTI_LINE_STYLE );
    }
}
