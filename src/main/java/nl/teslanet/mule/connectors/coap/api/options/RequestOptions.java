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


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * The CoAP option parameters of a request.
 *
 */
public class RequestOptions
{

    /**
     * RFC 7252: The Proxy-Uri Option is used to make a request to a forward-proxy.
     * 
     * @see <a target="_blank" href=
     *      "https://tools.ietf.org/html/rfc7252#section-5.10.2">IETF RFC 7252 -
     *      5.10.2. Proxy-Uri and Proxy-Scheme</a>
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Proxy-Uri Option is used to make a request to a forward-proxy.")
    private String proxyUri= null;

    /**
     * RFC 7252: When a Proxy-Scheme Option is present, the absolute-URI is
     * constructed as follows: a CoAP URI is constructed from the Uri-* options as
     * defined in Section 6.5. In the resulting URI, the initial scheme up to, but
     * not including, the following colon is then replaced by the content of the
     * Proxy-Scheme Option.
     * 
     * @see <a target="_blank" href=
     *      "https://tools.ietf.org/html/rfc7252#section-5.10.2">IETF RFC 7252 -
     *      5.10.2. Proxy-Uri and Proxy-Scheme</a>
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Proxy-Scheme Option is used to make a request to a forward-proxy.")
    private String proxyScheme= null;

    /**
     * RFC 7252: The Content-Format Option indicates the representation format of
     * the message payload. The representation format is given as a numeric
     * Content-Format identifier that is defined in the "CoAP Content-Formats"
     * registry (Section 12.3).
     * 
     * @see <a target="_blank" href=
     *      "https://tools.ietf.org/html/rfc7252#section-5.10.3">IETF RFC 7252 -
     *      5.10.3. Content-Format</a>
     */
    @Parameter
    @Optional
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Content-Format Option indicates the representation format of the message payload.")
    private Integer contentFormat= null;

    /**
     * RFC 7252: The CoAP Accept option can be used to indicate which Content-Format
     * is acceptable to the client. The representation format is given as a numeric
     * Content-Format identifier that is defined in the "CoAP Content-Formats"
     * registry (Section 12.3).
     * 
     * @see <a target="_blank" href=
     *      "https://tools.ietf.org/html/rfc7252#section-5.10.4">IETF RFC 7252 -
     *      5.10.4. Accept</a>
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
     * 
     * @see <a target="_blank" href=
     *      "https://tools.ietf.org/html/rfc7252#section-5.10.5">IETF RFC 7252 -
     *      5.10.5. Max-Age</a>
     */
    @Parameter
    @Optional(defaultValue= "60")
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The Max-Age Option indicates the maximum time a response may be cached before it is considered not fresh.")
    private Long maxAge= null;

    /**
     * RFC 7252: An entity-tag is intended for use as a resource-local identifier
     * for differentiating between representations of the same resource that vary
     * over time. It is generated by the server providing the resource, which may
     * generate it in any number of ways including a version, checksum, hash, or
     * time. An endpoint receiving an entity-tag MUST treat it as opaque and make no
     * assumptions about its content or structure.
     * 
     * The ETag Option in a response provides the current value (i.e., after the
     * request was processed) of the entity-tag for the "tagged representation".
     * 
     * In a GET request, an endpoint that has one or more representations previously
     * obtained from the resource, and has obtained ETag response options with
     * these, can specify an instance of the ETag Option for one or more of these
     * stored responses. A server can issue a 2.03 Valid response (Section 5.9.1.3)
     * in place of a 2.05 Content response if one of the ETags given is the
     * entity-tag for the current representation, i.e., is valid; the 2.03 Valid
     * response then echoes this specific ETag in a response option.
     * 
     * @see <a target="_blank" href=
     *      "https://tools.ietf.org/html/rfc7252#section-5.10.6">IETF RFC 7252 -
     *      5.10.6. ETag</a>
     */
    @Parameter
    @Optional
    @Content(primary= false)
    @Summary("An entity-tag is intended for use as a resource-local identifier for differentiating between representations of the same resource.")
    private TypedValue< Object > etagList= null;

    /**
     * RFC 7252: The If-Match Option MAY be used to make a request conditional on
     * the current existence or value of an ETag for one or more representations of
     * the target resource. If-Match is generally useful for resource update
     * requests, such as PUT requests, as a means for protecting against accidental
     * overwrites when multiple clients are acting in parallel on the same resource
     * (i.e., the "lost update" problem).
     * 
     * @see <a target="_blank" href=
     *      "https://tools.ietf.org/html/rfc7252#section-5.10.8.1">IETF RFC 7252 -
     *      5.10.8.1. If-Match</a>
     */
    @Parameter
    @Optional
    @Content(primary= false)
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The If-Match Option makes a request conditional on the resources ETag.")
    private TypedValue< Object > ifMatchList= null;

    /**
     * RFC 7252: The If-None-Match Option MAY be used to make a request conditional
     * on the nonexistence of the target resource. If-None-Match is useful for
     * resource creation requests, such as PUT requests, as a means for protecting
     * against accidental overwrites when multiple clients are acting in parallel on
     * the same resource. The If-None-Match Option carries no value. If the target
     * resource does exist, then the condition is not fulfilled.
     * 
     * @see <a target="_blank" href=
     *      "https://tools.ietf.org/html/rfc7252#section-5.10.8.2">IETF RFC 7252 -
     *      5.10.8.2. If-None-Match</a>
     */
    @Parameter
    @Optional(defaultValue= "false")
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The If-None-Match Option MAY be used to make a request conditional on the nonexistence of the target resource.")
    private boolean ifNoneMatch= false;

    /**
     * RFC 8613: The OSCORE option indicates that the CoAP message is an OSCORE
     * message and that it contains a compressed COSE object.
     * 
     * The OSCORE option includes the OSCORE flag bits.
     * 
     * @see <a target="_blank" href=
     *      "https://tools.ietf.org/html/rfc8613#section-2">IETF RFC 8613 - 2. The
     *      OSCORE Option</a>
     */
    // TODO
    // @Parameter
    // @Optional
    // @Expression(ExpressionSupport.SUPPORTED)
    // @Summary("The OSCORE option includes the OSCORE flag bits.")
    // private String oscore= null;

    /**
     * The CoAP other options to send with the request.
     */
    @Parameter
    @Optional
    @NullSafe
    @Expression(ExpressionSupport.SUPPORTED)
    @Summary("The CoAP options to send with the request.")
    private MultiMap< String, Object > otherRequestOptions;
    // Mule does not seem to convert multimap key to Integer

    /**
     * @return the otherRequestOptions
     */
    public MultiMap< String, Object > getOtherRequestOptions()
    {
        return otherRequestOptions;
    }

    /**
     * @param otherRequestOptions the otherRequestOptions to set
     */
    public void setOtherRequestOptions( MultiMap< String, Object > otherRequestOptions )
    {
        this.otherRequestOptions= otherRequestOptions;
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
     * @return the etagList
     */
    public TypedValue< Object > getEtagList()
    {
        return etagList;
    }

    /**
     * @param etagList the etagList to set
     */
    public void setEtagList( TypedValue< Object > etagList )
    {
        this.etagList= etagList;
    }

    /**
     * @return the ifMatchList
     */
    public TypedValue< Object > getIfMatchList()
    {
        return ifMatchList;
    }

    /**
     * @param ifMatchList the ifMatchList to set
     */
    public void setIfMatchList( TypedValue< Object > ifMatchList )
    {
        this.ifMatchList= ifMatchList;
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
}
