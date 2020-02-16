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
package nl.teslanet.mule.connectors.coap.test.utils;


import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Utility to collect Mule events en optionally replace the message.
 *
 */
public class MuleEventSpy
{
    private static ConcurrentSkipListMap< String, SpyData > spyData= new ConcurrentSkipListMap< String, SpyData >();

    /**
     * Id identifies the spy. Multiple instances can share the same id, 
     * in which case events of these will be collected into one collection 
     * and the same replacement will occur.
     */
    private String id;

    /**
     * Default Constructor of a spy accesspoint.
     */
    public MuleEventSpy()
    {
        this( UUID.randomUUID().toString(), null, null );
    }

    /**
     * Constructor of a spy accesspoint. All objects using the same id have access to the same spyData.
     * @param spyId the id defining the spy 
     */
    public MuleEventSpy( String spyId )
    {
        this( spyId, null, null );
    }

    /**
     * @param spyId the id defining the spy 
     * @param testKey if not null the key defining the test case 
     * @param replacement if not null the object will be used to replace the Mule message payload
     */
    public MuleEventSpy( String spyId, String testKey, Object replacement )
    {
        this.id= spyId;
        //make sure the spyData is there
        if ( !spyData.containsKey( spyId ) )
        {
            spyData.put( spyId, new SpyData() );
        }
        if ( testKey != null )
        {
            spyData.get( spyId ).setTestKey( testKey );
        }
        if ( replacement != null )
        {
            spyData.get( spyId ).setReplacement( replacement );;
        }
    }

    /**
     * Get the key of the test case, if set.
     * @return the key of the test case, otherwise null
     */
    public String getTestKey()
    {
        SpyData spydata= spyData.get( id );
        return spydata.getTestKey();
    }

    /**
     * Handle occurred event
     * @param payload the payload to collect
     * @return the replacement payload
     * @throws InvalidETagException 
     */
    public Object event( Object msg )
    {
        SpyData spydata= spyData.get( id );
        spydata.getCollector().add( new Event( msg ) );
        Object replacement= spydata.getReplacement();
        if ( replacement != null )
        {
            return replacement;
        }
        else
        {
            return msg;
        }
    }

    /**
     * Retrieve the events that the spy has collected.
     * @return the list of events
     */
    public CopyOnWriteArrayList< Event > getEvents()
    {
        SpyData spydata= spyData.get( id );
        return spydata.getCollector();
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * Clear all collected events for this spy
     */
    public void clear()
    {
        spyData.get( id ).getCollector().clear();
    }

    /**
     * The data used for a Spy
     *
     */
    private class SpyData
    {
        /**
         * The spydata containing even collector message replacement.
         */
        private CopyOnWriteArrayList< Event > collector;

        /**
         * The optional key to distinguish test cases.
         */
        private String testKey;

        /**
         * The optional replacement of message payload.
         */
        private Object replacement;

        /**
         * SpyData constructor.
         */
        SpyData()
        {
            collector= new CopyOnWriteArrayList< Event >();
            testKey= null;
            replacement= null;
        }

        /**
         * @return the collector
         */
        public CopyOnWriteArrayList< Event > getCollector()
        {
            return collector;
        }

        /**
         * @return the testKey
         */
        public String getTestKey()
        {
            return testKey;
        }

        /**
         * @param testKey the testKey to set
         */
        public void setTestKey( String testKey )
        {
            this.testKey= testKey;
        }

        /**
         * Setter for the payload replacement. Threadsafe
         * @param payloadReplacement the replcament to set
         */
        synchronized void setReplacement( Object replacement )
        {
            this.replacement= replacement;
        }

        /**
         * @return the payloadReplacement
         */
        public Object getReplacement()
        {
            return replacement;
        }
    }

    /**
     * Event representing the contents of a Mule event
     *
     */
    public class Event
    {
        /**
         * The payoad registered
         */
        Object content= null;

        /**
         * Event constructor
        * @param payload the payload that the event contained
        */
        Event( Object content )
        {
            this.content= content;
        }

        /**
         * @return the content
         */
        public Object getContent()
        {
            return content;
        }
    }
}
