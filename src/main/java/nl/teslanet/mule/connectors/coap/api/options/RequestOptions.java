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
package nl.teslanet.mule.connectors.coap.api.options;


import java.util.List;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.metadata.TypedValue;
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
     * RFC 7252: The Content-Format Option indicates the representation format of
     * the message payload. The representation format is given as a numeric
     * Content-Format identifier that is defined in the "CoAP Content-Formats"
     * registry (Section 12.3).
     * 
     * @see <a href=
     *      "https://tools.ietf.org/html/rfc7252#section-5.10.3">IETF RFC 7252 -
     *      5.10.3. Content-Format</a>
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary(
        "The Content-Format Option indicates the representation format of the request payload. \nWhen empty the Mimetype of the payload is used. \nWhen none of these are set no content type option is set on the request."
    )
    private Integer contentFormat= null;

    /**
     * RFC 7252: The CoAP Accept option can be used to indicate which Content-Format
     * is acceptable to the client. The representation format is given as a numeric
     * Content-Format identifier that is defined in the "CoAP Content-Formats"
     * registry (Section 12.3).
     * 
     * @see <a href=
     *      "https://tools.ietf.org/html/rfc7252#section-5.10.4">IETF RFC 7252 -
     *      5.10.4. Accept</a>
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "The CoAP Accept option can be used to indicate which Content-Format is acceptable to the client." )
    private Integer accept= null;

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
     * @see <a href=
     *      "https://tools.ietf.org/html/rfc7252#section-5.10.6">IETF RFC 7252 -
     *      5.10.6. ETag</a>
     */
    @Parameter
    @Optional
    @Content( primary= false )
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary(
        "An entity-tag is intended for use as a resource-local identifier for differentiating between representations of the same resource. One or a collection of etag values can be set."
    )
    private TypedValue< Object > etags= null;

    /**
     * RFC 7252: The value of an If-Match option is either an ETag or the empty
     * string.  An If-Match option with an ETag matches a representation
     * with that exact ETag.  An If-Match option with an empty value matches
     * any existing representation (i.e., it places the precondition on the
     * existence of any current representation for the target resource).
     * The If-Match Option can occur multiple times. If any of the options
     * match, then the condition is fulfilled.
     * If there is one or more If-Match Options, but none of the options
     * match, then the condition is not fulfilled.
     * 
     * @see <a href=
     *      "https://tools.ietf.org/html/rfc7252#section-5.10.8.1">IETF RFC 7252 -
     *      5.10.8.1. If-Match</a>
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary(
        "Sets an empty If-Match Option which makes a request conditional on the existence of the resource only. When set If-MAtch options containing ETags must be ignored server-side."
    )
    private boolean ifExists= false;

    /**
     * RFC 7252: The If-Match Option MAY be used to make a request conditional on
     * the current existence or value of an ETag for one or more representations of
     * the target resource. If-Match is generally useful for resource update
     * requests, such as PUT requests, as a means for protecting against accidental
     * overwrites when multiple clients are acting in parallel on the same resource
     * (i.e., the "lost update" problem).
     * 
     * @see <a href=
     *      "https://tools.ietf.org/html/rfc7252#section-5.10.8.1">IETF RFC 7252 -
     *      5.10.8.1. If-Match</a>
     */
    @Parameter
    @Optional
    @Content( primary= false )
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "The If-Match Option makes a request conditional on the resources ETag." )
    private TypedValue< Object > ifMatch= null;

    /**
     * RFC 7252: The If-None-Match Option MAY be used to make a request conditional
     * on the nonexistence of the target resource. If-None-Match is useful for
     * resource creation requests, such as PUT requests, as a means for protecting
     * against accidental overwrites when multiple clients are acting in parallel on
     * the same resource. The If-None-Match Option carries no value. If the target
     * resource does exist, then the condition is not fulfilled.
     * 
     * @see <a href=
     *      "https://tools.ietf.org/html/rfc7252#section-5.10.8.2">IETF RFC 7252 -
     *      5.10.8.2. If-None-Match</a>
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "The If-None-Match Option MAY be used to make a request conditional on the nonexistence of the target resource." )
    private boolean ifNoneMatch= false;

    /**
     * RFC 7959: In a request carrying a Block1 Option, to indicate the current
      estimate the client has of the total size of the resource
      representation, measured in bytes ("size indication")
     * 
     * @see <a href=
     *      "https://datatracker.ietf.org/doc/html/rfc7959#section-4">IETF RFC 7959 - 4. The Size2 and Size1 Options</a>
     */
    @Parameter
    @Optional
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "Indication of the request payload size in [Bytes]." )
    private Integer requestSize= null;

    /**
     * RFC 7959: In a request, to ask the server to provide a size estimate along
      with the usual response ("size request").  For this usage, the
      value MUST be set to 0.
     * 
     * @see <a href=
     *      "https://datatracker.ietf.org/doc/html/rfc7959#section-4">IETF RFC 7959 - 4. The Size2 and Size1 Options</a>
     */
    @Parameter
    @Optional( defaultValue= "false" )
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "Ask server to provide for a Size2 option in the response,\nindicating the response payload size." )
    private boolean provideResponseSize= false;

    /**
     * RFC 8613: The OSCORE option indicates that the CoAP message is an OSCORE
     * message and that it contains a compressed COSE object.
     * 
     * The OSCORE option includes the OSCORE flag bits.
     * 
     * @see <a href=
     *      "https://tools.ietf.org/html/rfc8613#section-2">IETF RFC 8613 - 2. The
     *      OSCORE Option</a>
     */
    //TODO add oscore feature
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
    @Expression( ExpressionSupport.SUPPORTED )
    @Summary( "The CoAP options to send with the request." )
    private List< OtherOption > otherRequestOptions;
    // Mule does not seem to convert multimap key to Integer

    /**
     * @return The ifExists option.
     */
    public boolean isIfExists()
    {
        return ifExists;
    }

    /**
     * @param ifExists The ifExists option to set.
     */
    public void setIfExists( boolean ifExists )
    {
        this.ifExists= ifExists;
    }

    /**
     * The If-Match option contains an ETag value or a collection of ETag values.
     * @return The ifMatch options.
     */
    public TypedValue< Object > getIfMatch()
    {
        return ifMatch;
    }

    /**
     * @param ifMatch the ifMatch value to set.
     */
    public void setIfMatch( TypedValue< Object > ifMatch )
    {
        this.ifMatch= ifMatch;
    }

    /**
     * @return The etags value.
     */
    public TypedValue< Object > getEtags()
    {
        return etags;
    }

    /**
     * @param etags The etags to set.
     */
    public void setEtags( TypedValue< Object > etags )
    {
        this.etags= etags;
    }

    /**
     * @return the ifNoneMatch value
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
     * @return The contentFormat option.
     */
    public Integer getContentFormat()
    {
        return contentFormat;
    }

    /**
     * @param contentFormat The contentFormat option to set.
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
     * @return True when Size2 option is requested, otherwise false.
     */
    public boolean isProvideResponseSize()
    {
        return provideResponseSize;
    }

    /**
     * @param provideResponseSize The provideResponseSize option to set.
     */
    public void setProvideResponseSize( boolean provideResponseSize )
    {
        this.provideResponseSize= provideResponseSize;
    }

    /**
     * @return The Size1 option if present, otherwise null.
     */
    public Integer getRequestSize()
    {
        return requestSize;
    }

    /**
     * @param size1 The size1 option to set.
     */
    public void setRequestSize( Integer size1 )
    {
        this.requestSize= size1;
    }

    /**
    * @return the otherRequestOptions
    */
    public List< OtherOption > getOtherRequestOptions()
    {
        return otherRequestOptions;
    }

    /**
     * @param otherRequestOptions the otherRequestOptions to set
     */
    public void setOtherRequestOptions( List< OtherOption > otherRequestOptions )
    {
        this.otherRequestOptions= otherRequestOptions;
    }

}
