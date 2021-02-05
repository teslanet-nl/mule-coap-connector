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
package nl.teslanet.mule.connectors.coap.api.config;


import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.meta.model.display.PathModel.Location;
import org.mule.runtime.api.meta.model.display.PathModel.Type;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Path;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * Configuration of Encryption parameters.
 *
 */
public class EncryptionParams implements VisitableConfig
{
    /**
     * The location of the keystore.
     */
    @Parameter
    @Summary(value= "The location of the keystore.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Path(type= Type.FILE, acceptsUrls= true, location= Location.ANY)
    public String keyStoreLocation= null;

    /**
     * The password to access the keystore.
     */
    @Parameter
    @Summary(value= "The password to access the keystore.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Password
    public String keyStorePassword= null;

    /**
     * Alias of the private key to use from the keystore.
     */
    @Parameter
    @Summary(value= "Alias of the private key to use from the keystore.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public String privateKeyAlias= null;

    /**
     * Password of the private key to use from the keystore.
     */
    @Parameter
    @Summary(value= "Password of the private key to use from the keystore.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Password
    public String privateKeyPassword= null;

    /**
     * The location of the truststore.
     */
    @Parameter
    @Summary(value= "The location of the truststore.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Path(type= Type.FILE, acceptsUrls= true, location= Location.ANY)
    public String trustStoreLocation= null;

    /**
     * The password to access the truststore.
     */
    @Parameter
    @Summary(value= "The password to access the truststore.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    @Password
    public String trustStorePassword= null;

    /**
     * The alias of the certificate to use from the truststore.
     */
    @Parameter
    @Summary(value= "The alias of the trusted certificate to use.")
    @Expression(ExpressionSupport.NOT_SUPPORTED)
    @ParameterDsl(allowReferences= false)
    public String trustedRootCertificateAlias= null;

    /* (non-Javadoc)
     * @see nl.teslanet.mule.connectors.coap.api.config.VisitableConfig#accept(nl.teslanet.mule.connectors.coap.api.config.ConfigVisitor)
     */
    @Override
    public void accept( ConfigVisitor visitor )
    {
        visitor.visit( this );
    }
}
