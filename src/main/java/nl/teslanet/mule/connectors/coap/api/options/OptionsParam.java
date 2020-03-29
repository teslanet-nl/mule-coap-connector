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


import java.util.List;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * The option parameters of a CoAP request or response.
 *
 */
public class OptionsParam implements Options
{
    /**
     * RFC 7252: The Uri-Host Option specifies the Internet host of the resource
      being requested.
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7252#section-5.10.1">IETF RFC 7252 - 5.10.1. Uri-Host, Uri-Port, Uri-Path, and Uri-Query</a>
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Uri-Host Option specifies the Internet host of the resource being requested.")
    private String uriHost= null;

    /**
     * RFC 7252: The Uri-Port Option specifies the transport-layer port number of
     * the resource.
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7252#section-5.10.1">IETF RFC 7252 - 5.10.1. Uri-Host, Uri-Port, Uri-Path, and Uri-Query</a>
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Uri-Port Option specifies the transport-layer port number of the resource being requested.")
    private Integer uriPort= null;

    /**
     * RFC 7252: Each Uri-Path Option specifies one segment of the absolute path to
     * the resource.
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7252#section-5.10.1">IETF RFC 7252 - 5.10.1. Uri-Host, Uri-Port, Uri-Path, and Uri-Query</a>
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Uri-Path Options specify the absolute path to the resource being requested.")
    //private String uriPath= null;
    private List< String > uriPathList= null;

    /**
     * RFC 7252: Each each Uri-Query Option specifies one argument parameterizing the
     * resource.
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7252#section-5.10.1">IETF RFC 7252 - 5.10.1. Uri-Host, Uri-Port, Uri-Path, and Uri-Query</a>
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Uri-Query Options specify arguments parameterizing the request.")
    //private String uriQuery= null;
    private List< String > uriQueryList= null;

    /**
     * RFC 7252: The Proxy-Uri Option is used to make a request to a forward-proxy. 
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7252#section-5.10.2">IETF RFC 7252 - 5.10.2. Proxy-Uri and Proxy-Scheme</a>
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Proxy-Uri Option is used to make a request to a forward-proxy.")
    private String proxyUri= null;

    /**
     * RFC 7252: When a Proxy-Scheme Option is present, the absolute-URI is constructed 
     * as follows: a CoAP URI is constructed from the Uri-* options as defined in Section 6.5.
     * In the resulting URI, the initial scheme up to, but not including, the following colon 
     * is then replaced by the content of the Proxy-Scheme Option. 
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7252#section-5.10.2">IETF RFC 7252 - 5.10.2. Proxy-Uri and Proxy-Scheme</a>
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Proxy-Scheme Option is used to make a request to a forward-proxy.")
    private String proxyScheme= null;

    /**
     * RFC 7252: The Content-Format Option indicates the representation format of the
     * message payload. The representation format is given as a numeric Content-Format 
     * identifier that is defined in the "CoAP Content-Formats" registry (Section 12.3).
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7252#section-5.10.3">IETF RFC 7252 - 5.10.3. Content-Format</a>
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Content-Format Option indicates the representation format of the message payload.")
    private Integer contentFormat= null;

    /**
     * RFC 7252: The CoAP Accept option can be used to indicate which Content-Format
     * is acceptable to the client. The representation format is given as a
     * numeric Content-Format identifier that is defined in the "CoAP Content-Formats" 
     * registry (Section 12.3). 
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7252#section-5.10.4">IETF RFC 7252 - 5.10.4. Accept</a>
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The CoAP Accept option can be used to indicate which Content-Format is acceptable to the client.")
    private Integer accept= null;

    /**
     * RFC 7252: The Max-Age Option indicates the maximum time a response may be
     * cached before it is considered not fresh (see Section 5.6.1).
     * 
     * The option value is an integer number of seconds between 0 and
     * {@code 2**32-1 } inclusive (about 136.1 years). A default value of 60 seconds
     * is assumed in the absence of the option in a response.
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7252#section-5.10.5">IETF RFC 7252 - 5.10.5. Max-Age</a>
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Max-Age Option indicates the maximum time a response may be cached before it is considered not fresh.")
    private Long maxAge= null;

    /**
     * RFC 7252: An entity-tag is intended for use as a resource-local identifier 
     * for differentiating between representations of the same resource that vary 
     * over time. It is generated by the server providing the resource, which may 
     * generate it in any number of ways including a version, checksum, hash, or time.  
     * An endpoint receiving an entity-tag MUST treat it as opaque and 
     * make no assumptions about its content or structure.
     * 
     * The ETag Option in a response provides the current value (i.e., after
     * the request was processed) of the entity-tag for the "tagged representation".
     * 
     * In a GET request, an endpoint that has one or more representations
     * previously obtained from the resource, and has obtained ETag response
     * options with these, can specify an instance of the ETag Option for
     * one or more of these stored responses. 
     * A server can issue a 2.03 Valid response (Section 5.9.1.3) in place
     * of a 2.05 Content response if one of the ETags given is the entity-tag 
     * for the current representation, i.e., is valid; the 2.03 Valid
     * response then echoes this specific ETag in a response option.
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7252#section-5.10.6">IETF RFC 7252 - 5.10.6. ETag</a>
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("An entity-tag is intended for use as a resource-local identifier for differentiating between representations of the same resource.")
    private List< ETag > etagList= null;

    /**
     * RFC 7252: The Location-Path and Location-Query Options together indicate a
     * relative URI that consists either of an absolute path, a query string,
     * or both.  A combination of these options is included in a 2.01 (Created)
     * response to indicate the location of the resource created as the result 
     * of a POST request (see Section 5.8.2).  The location is resolved relative to the request URI.
     * 
     * Each Location-Path Option specifies one segment of the absolute path
     * to the resource. 
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7252#section-5.10.7">IETF RFC 7252 - 5.10.7. Location-Path and Location-Query</a>
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Location-Path Options specify the absolute path to a resource that is created.")
    //private String locationPath= null;
    private List< String > locationPathList= null;

    /**
     * RFC 7252: The Location-Path and Location-Query Options together indicate a
     * relative URI that consists either of an absolute path, a query string,
     * or both.  A combination of these options is included in a 2.01 (Created)
     * response to indicate the location of the resource created as the result 
     * of a POST request (see Section 5.8.2).  The location is resolved relative to the request URI.
     * 
     * Each each Location-Query Option specifies one argument parameterizing the resource.
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7252#section-5.10.7">IETF RFC 7252 - 5.10.7. Location-Path and Location-Query</a>
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Location-Query Options specify the arguments parameterizing the resource that is created.")
    //private String locationQuery= null;
    private List< String > locationQueryList= null;

    /**
     * RFC 7252: The If-Match Option MAY be used to make a request conditional
     * on the current existence or value of an ETag for one or more 
     * representations of the target resource. If-Match is generally useful for 
     * resource update requests, such as PUT requests, as a means for protecting
     * against accidental overwrites when multiple clients are acting in
     * parallel on the same resource (i.e., the "lost update" problem).
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7252#section-5.10.8.1">IETF RFC 7252 - 5.10.8.1. If-Match</a>
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The If-Match Option makes a request conditional on the resources ETag.")
    private List< ETag > ifMatchList= null;

    /**
     * RFC 7252: The If-None-Match Option MAY be used to make a request conditional on
     * the nonexistence of the target resource. If-None-Match is useful for
     * resource creation requests, such as PUT requests, as a means for
     * protecting against accidental overwrites when multiple clients are
     * acting in parallel on the same resource. The If-None-Match Option
     * carries no value. If the target resource does exist, then the condition is 
     * not fulfilled.
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7252#section-5.10.8.2">IETF RFC 7252 - 5.10.8.2. If-None-Match</a>
     */
    @Parameter
    @Optional(defaultValue= "false")
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The If-None-Match Option MAY be used to make a request conditional on the nonexistence of the target resource.")
    private boolean ifNoneMatch= false;

    /**
     * RFC 7959: In a request with a request payload (e.g., PUT or POST), 
     * the Block1 Option refers to the payload in the request (descriptive usage).
     * 
     * In response to a request with a payload (e.g., a PUT or POST
     * transfer), the block size given in the Block1 Option indicates the
     * block size preference of the server for this resource (control usage). 
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7959#section-2.5">IETF RFC 7959 - 2.5. Using the Block1 Option</a>
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Block1 describes request payload blocks in Block-Wise transfers.")
    private BlockValue block1= null;

    /**
     * RFC 7959: When a request is answered with a response carrying a Block2 Option
     * with the M bit set, the requester may retrieve additional blocks of
     * the resource representation by sending further requests with the same
     * options as the initial request and a Block2 Option giving the block
     * number and block size desired.  In a request, the client MUST set the
     * M bit of a Block2 Option to zero and the server MUST ignore it on
     * reception.
     * 
     * To influence the block size used in a response, the requester MAY
     * also use the Block2 Option on the initial request, giving the desired
     * size, a block number of zero and an M bit of zero. 
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7959#section-2.4">IETF RFC 7959 - 2.4. Using the Block2 Option</a>
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Block2 describes response payload blocks in Block-Wise transfers.")
    private BlockValue block2= null;

    /**
     * RFC 7959: The Size1 Option may be used for two purposes:
     * 
     * o  In a request carrying a Block1 Option, to indicate the current
     * estimate the client has of the total size of the resource
     * representation, measured in bytes ("size indication").
     * 
     * o  In a 4.13 response, to indicate the maximum size that would have
     * been acceptable [RFC7252], measured in bytes.
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7959#section-4">IETF RFC 7959 - 4. The Size2 and Size1 Options</a>
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Size1 indicates the total request payload size that will be sent by a client or is acceptable for a server.")
    private Integer size1= null;

    /**
     * RFC 7959: The Size2 Option may be used for two purposes:
     * 
     * o  In a request, to ask the server to provide a size estimate along
     * with the usual response ("size request").  For this usage, the
     * value MUST be set to 0.
    
     * o  In a response carrying a Block2 Option, to indicate the current
     * estimate the server has of the total size of the resource
     * representation, measured in bytes ("size indication").
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7959#section-4">IETF RFC 7959 - 4. The Size2 and Size1 Options</a>
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Size1 indicates total response payload size that is requested by a client (value=0) or that will be returned by a server (value is size of payload).")
    private Integer size2= null;

    /**
     * RFC 7641: When included in a GET request, the Observe Option extends the GET
     * method so it does not only retrieve a current representation of the
     * target resource, but also requests the server to add or remove an
     * entry in the list of observers of the resource depending on the
     * option value.  The list entry consists of the client endpoint and the
     * token specified by the client in the request.  Possible values are:
     * 
     * 0 (register) adds the entry to the list, if not present;
     * 
     * 1 (deregister) removes the entry from the list, if present.
     * 
     * When included in a response, the Observe Option identifies the
     * message as a notification.  This implies that a matching entry exists
     * in the list of observers and that the server will notify the client
     * of changes to the resource state.  The option value is a sequence
     * number for reordering detection (see Sections 3.4 and 4.4).
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7641#section-2">IETF RFC 7641 - 2. The Observe Option</a>
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Size1 indicates total response payload size that is requested by a client (value=0) or that will be returned by a server (value is size of payload).")
    private Integer observe= null;

    /**
     * RFC 8613: The OSCORE option indicates that the CoAP message
     * is an OSCORE message and that it contains a compressed COSE object. 
     * 
     * The OSCORE option includes the OSCORE flag bits.
     * @see <a target="_blank" href="https://tools.ietf.org/html/rfc8613#section-2">IETF RFC 8613 - 2. The OSCORE Option</a>
     */
    //TODO
//    @Parameter
//    @Optional
//    @Expression(ExpressionSupport.SUPPORTED)
//    @Summary("The OSCORE option includes the OSCORE flag bits.")
//    private String oscore= null;

    /**
     * The CoAP options to send with the request.
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The CoAP options to send with the request.")
    @Placement(tab= "Advanced")
    @DisplayName("Other Options")
    private MultiMap< String, Object > otherOptions;
    //Mule does not seem to convert multimap key to Integer

    @Override
    public List< ETag > getIfMatchList()
    {
        return ifMatchList;
    }

    @Override
    public Boolean getIfNoneMatch()
    {
        return ifNoneMatch;
    }

    @Override
    public String getUriHost()
    {
        return uriHost;
    }

    @Override
    public List< ETag > getEtagList()
    {
        return etagList;
    }

    @Override
    public Integer getUriPort()
    {
        return uriPort;
    }

    @Override
    public String getLocationPath()
    {
        return null;
    }

    @Override
    public List< String > getLocationPathList()
    {
        return locationPathList;
    }

    @Override
    public String getUriPath()
    {
        return null;
    }

    @Override
    public List< String > getUriPathList()
    {
        return uriPathList;
    }

    @Override
    public Integer getContentFormat()
    {
        return contentFormat;
    }

    @Override
    public Long getMaxAge()
    {
        return maxAge;
    }

    @Override
    public String getUriQuery()
    {
        return null;
    }

    @Override
    public List< String > getUriQueryList()
    {
        return uriQueryList;
    }

    @Override
    public Integer getAccept()
    {
        return accept;
    }

    @Override
    public String getLocationQuery()
    {
        return null;
    }

    @Override
    public List< String > getLocationQueryList()
    {
        return locationQueryList;
    }

    @Override
    public String getProxyUri()
    {
        return proxyUri;
    }

    @Override
    public String getProxyScheme()
    {
        return proxyScheme;
    }

    @Override
    public BlockValue getBlock1()
    {
        return block1;
    }

    @Override
    public BlockValue getBlock2()
    {
        return block2;
    }

    @Override
    public Integer getSize1()
    {
        return size1;
    }

    @Override
    public Integer getSize2()
    {
        return size2;
    }

    @Override
    public Integer getObserve()
    {
        return observe;
    }

    @Override
    public MultiMap< String, Object > getOtherOptions()
    {
        return otherOptions;
    }

    /**
     * @param ifMatchList the ifMatchList to set
     */
    public void setIfMatchList( List< ETag > ifMatchList )
    {
        this.ifMatchList= ifMatchList;
    }

    /**
     * @param uriHost the uriHost to set
     */
    public void setUriHost( String uriHost )
    {
        this.uriHost= uriHost;
    }

    /**
     * @param etagList the etagList to set
     */
    public void setEtagList( List< ETag > etagList )
    {
        this.etagList= etagList;
    }

    /**
     * @param ifNoneMatch the ifNoneMatch to set
     */
    public void setIfNoneMatch( boolean ifNoneMatch )
    {
        this.ifNoneMatch= ifNoneMatch;
    }

    /**
     * @param uriPort the uriPort to set
     */
    public void setUriPort( Integer uriPort )
    {
        this.uriPort= uriPort;
    }

    /**
     * @param locationPath the locationPath to set
     */
    //    public void setLocationPath( String locationPath )
    //    {
    //        this.locationPath= locationPath;
    //    }

    /**
     * @param locationPathList the locationPathList to set
     */
    public void setLocationPathList( List< String > locationPathList )
    {
        this.locationPathList= locationPathList;
    }

    /**
     * @param uriPath the uriPath to set
     */
    //    public void setUriPath( String uriPath )
    //    {
    //        this.uriPath= uriPath;
    //    }

    /**
     * @param uriPathList the uriPathList to set
     */
    public void setUriPathList( List< String > uriPathList )
    {
        this.uriPathList= uriPathList;
    }

    /**
     * @param contentFormat the contentFormat to set
     */
    public void setContentFormat( Integer contentFormat )
    {
        this.contentFormat= contentFormat;
    }

    /**
     * @param maxAge the maxAge to set
     */
    public void setMaxAge( Long maxAge )
    {
        this.maxAge= maxAge;
    }

    /**
     * @param uriQuery the uriQuery to set
     */
    //    public void setUriQuery( String uriQuery )
    //    {
    //        this.uriQuery= uriQuery;
    //    }

    /**
     * @param uriQueryList the uriQueryList to set
     */
    public void setUriQueryList( List< String > uriQueryList )
    {
        this.uriQueryList= uriQueryList;
    }

    /**
     * @param accept the accept to set
     */
    public void setAccept( Integer accept )
    {
        this.accept= accept;
    }

    /**
     * @param locationQuery the locationQuery to set
     */
    //    public void setLocationQuery( String locationQuery )
    //    {
    //        this.locationQuery= locationQuery;
    //    }

    /**
     * @param locationQueryList the locationQueryList to set
     */
    public void setLocationQueryList( List< String > locationQueryList )
    {
        this.locationQueryList= locationQueryList;
    }

    /**
     * @param proxyUri the proxyUri to set
     */
    public void setProxyUri( String proxyUri )
    {
        this.proxyUri= proxyUri;
    }

    /**
     * @param proxyScheme the proxyScheme to set
     */
    public void setProxyScheme( String proxyScheme )
    {
        this.proxyScheme= proxyScheme;
    }

    /**
     * @param block1 the block1 to set
     */
    public void setBlock1( BlockValue block1 )
    {
        this.block1= block1;
    }

    /**
     * @param block2 the block2 to set
     */
    public void setBlock2( BlockValue block2 )
    {
        this.block2= block2;
    }

    /**
     * @param size1 the size1 to set
     */
    public void setSize1( Integer size1 )
    {
        this.size1= size1;
    }

    /**
     * @param size2 the size2 to set
     */
    public void setSize2( Integer size2 )
    {
        this.size2= size2;
    }

    /**
     * @param observe the observe to set
     */
    public void setObserve( Integer observe )
    {
        this.observe= observe;
    }

    /**
     * @param otherOptions the otherOptions to set
     */
    public void setOtherOptions( MultiMap< String, Object > otherOptions )
    {
        this.otherOptions= otherOptions;
    }
}
