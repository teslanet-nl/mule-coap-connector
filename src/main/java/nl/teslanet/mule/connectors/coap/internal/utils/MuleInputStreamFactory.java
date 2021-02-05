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
package nl.teslanet.mule.connectors.coap.internal.utils;


import java.io.IOException;
import java.io.InputStream;

import org.eclipse.californium.elements.util.SslContextUtil.InputStreamFactory;
import org.mule.runtime.core.api.util.IOUtils;


/**
 * InputStreamFactory that creates input streams using Mule's IOUtils.
 *
 */
public class MuleInputStreamFactory implements InputStreamFactory
{
    /**
     * The scheme used in resource Uri's that are loaded using Mule's IOUtils.
     */
    protected static final String MULE_RESOURCE_SCHEME= "mule://";

    /* (non-Javadoc)
     * @see org.eclipse.californium.elements.util.SslContextUtil.InputStreamFactory#create(java.lang.String)
     */
    @Override
    public InputStream create( String uri ) throws IOException
    {
        String resource= uri.substring( MULE_RESOURCE_SCHEME.length() );
        //TODO is allowing url safe?
        InputStream inStream= IOUtils.getResourceAsStream( resource, this.getClass(), true, true );
        if ( inStream == null ) throw new IOException( "resource not found: " + resource );
        return inStream;
    }

    public final String getScheme()
    {
        return MULE_RESOURCE_SCHEME;
    }
}
