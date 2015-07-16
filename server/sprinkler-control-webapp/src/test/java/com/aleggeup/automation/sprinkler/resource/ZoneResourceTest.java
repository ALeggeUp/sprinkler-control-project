/**
 * ZoneResourceTest.java
 *
 * Copyright 2014 [A Legge Up Consulting]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aleggeup.automation.sprinkler.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.Scheduler;

import com.aleggeup.automation.odm.DatastoreCollection;
import com.aleggeup.automation.odm.DatastoreCollectionRegistry;
import com.aleggeup.automation.sprinkler.model.Zone;

/**
 * @author Stephen Legge
 */
public class ZoneResourceTest {

    private static final String ID_1 = "id1";
    private static final String ID_1_NUM = "1";
    private static final String ID_2 = "id2";
    private static final String ID_2_NUM = "2";
    private static final String ID_3_NUM = "3";
    private static final int ID_3_VAL = 3;

    @Mock
    private Scheduler mockQuartzScheduler;

    @Mock
    private DatastoreCollectionRegistry mockDatastoreCollectionRegistry;

    @Mock
    private DatastoreCollection<Zone> mockZoneDatastoreCollection;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link com.aleggeup.automation.sprinkler.resource.ZoneResource#ZoneResource(Scheduler, Persister)}.
     */
    @Test
    public final void testZoneResource() {
        when(mockDatastoreCollectionRegistry.get(Zone.class)).thenReturn(mockZoneDatastoreCollection);
        final ZoneResource zoneResource = new ZoneResource(mockQuartzScheduler, mockDatastoreCollectionRegistry);
        assertNotNull(zoneResource);
    }

    /**
     * Test method for {@link com.aleggeup.automation.sprinkler.resource.ZoneResource#getZones()}.
     */
    @Test
    public final void testGetZones() {
        final Iterable<Zone> iterable = new ArrayList<Zone>();
        when(mockZoneDatastoreCollection.findAll()).thenReturn(iterable);
        when(mockDatastoreCollectionRegistry.get(Zone.class)).thenReturn(mockZoneDatastoreCollection);
        final ZoneResource zoneResource = new ZoneResource(mockQuartzScheduler, mockDatastoreCollectionRegistry);
        zoneResource.getZones();

        verify(mockZoneDatastoreCollection).findAll();
    }

    /**
     * Test method for {@link com.aleggeup.automation.sprinkler.resource.ZoneResource#getZone(java.lang.String)}.
     */
    @Test
    public final void testGetZoneById() {
        final List<Zone> zones = new ArrayList<Zone>();
        final Zone zone1 = new Zone();
        zone1.setId(ID_1);
        final Zone zone2 = new Zone();
        zone2.setId(ID_2);
        zones.add(zone2);
        when(mockZoneDatastoreCollection.findAll()).thenReturn(zones);
        when(mockDatastoreCollectionRegistry.get(Zone.class)).thenReturn(mockZoneDatastoreCollection);
        final ZoneResource zoneResource = new ZoneResource(mockQuartzScheduler, mockDatastoreCollectionRegistry);
        final Zone getZone = zoneResource.getZone(ID_2);

        assertNotNull(getZone);
        assertEquals(zone2, getZone);

        verify(mockZoneDatastoreCollection).findAll();
    }

    /**
     * Test method for {@link com.aleggeup.automation.sprinkler.resource.ZoneResource#getZone(java.lang.String)}.
     */
    @Test
    public final void testGetZoneByNumber() {
        final List<Zone> zones = new ArrayList<Zone>();
        final Zone zone1 = new Zone();
        zone1.setId(ID_1);
        zone1.setNumber(1);
        final Zone zone2 = new Zone();
        zone2.setId(ID_2);
        zone2.setNumber(2);
        zones.add(zone2);
        final Zone zone3 = new Zone();
        zone3.setId(ID_3_NUM);
        zone3.setNumber(ID_3_VAL);
        zones.add(zone3);
        when(mockZoneDatastoreCollection.findAll()).thenReturn(zones);
        when(mockDatastoreCollectionRegistry.get(Zone.class)).thenReturn(mockZoneDatastoreCollection);
        final ZoneResource zoneResource = new ZoneResource(mockQuartzScheduler, mockDatastoreCollectionRegistry);
        final Zone getZone2 = zoneResource.getZone(ID_2_NUM);
        final Zone getZone3 = zoneResource.getZone(ID_3_NUM);
        final Zone getZone4 = zoneResource.getZone(String.valueOf(Integer.MIN_VALUE));

        assertNotNull(getZone2);
        assertNotNull(getZone3);
        assertNull(getZone4);
        assertEquals(zone2, getZone2);
        assertEquals(zone3, getZone3);

        verify(mockZoneDatastoreCollection, times(2 + 1)).findAll();
    }

    /**
     * Test method for {@link com.aleggeup.automation.sprinkler.resource.ZoneResource#getZone(java.lang.String)}.
     */
    @Test
    public final void testGetZoneNotFound() {
        final List<Zone> zones = new ArrayList<Zone>();
        when(mockZoneDatastoreCollection.findAll()).thenReturn(zones);
        when(mockDatastoreCollectionRegistry.get(Zone.class)).thenReturn(mockZoneDatastoreCollection);
        final ZoneResource zoneResource = new ZoneResource(mockQuartzScheduler, mockDatastoreCollectionRegistry);
        final Zone getZone = zoneResource.getZone(ID_1_NUM);

        assertNull(getZone);

        verify(mockZoneDatastoreCollection).findAll();
    }

    /**
     * Test method for {@link com.aleggeup.automation.sprinkler.resource.ZoneResource#updateZone(Zone)}.
     */
    @Test
    public final void testUpdateZone() {
        final Zone zone = new Zone();
        when(mockDatastoreCollectionRegistry.get(Zone.class)).thenReturn(mockZoneDatastoreCollection);
        final ZoneResource zoneResource = new ZoneResource(mockQuartzScheduler, mockDatastoreCollectionRegistry);
        final Response response = zoneResource.updateZone(zone);

        assertNotNull(response);

        verify(mockZoneDatastoreCollection).update(zone);
    }

    /**
     * Test method for {@link com.aleggeup.automation.sprinkler.resource.ZoneResource#startZone(Zone)}.
     */
    @Test
    public final void testStartZone() {
    }

}
