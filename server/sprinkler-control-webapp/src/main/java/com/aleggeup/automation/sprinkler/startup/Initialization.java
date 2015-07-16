/**
 * Initialization.java
 *
 * Copyright 2015 [A Legge Up Consulting]
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

package com.aleggeup.automation.sprinkler.startup;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleggeup.automation.odm.DatastoreCollection;
import com.aleggeup.automation.odm.DatastoreCollectionRegistry;
import com.aleggeup.automation.sprinkler.model.Zone;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

/**
 * @author Stephen Legge
 */
public class Initialization {

    private static final Logger LOGGER = LoggerFactory.getLogger(Initialization.class);

    private final DatastoreCollection<Zone> zoneDatastoreCollection;

    private final ObjectMapper objectMapper;

    private final Scheduler scheduler;

    @Inject
    public Initialization(final ObjectMapper objectMapper, final DatastoreCollectionRegistry collectionRegistry,
            final Scheduler scheduler) {
        this.objectMapper = objectMapper;
        this.zoneDatastoreCollection = collectionRegistry.get(Zone.class);
        this.scheduler = scheduler;
    }

    public void initializeZones() {
        try {
            final Iterable<Zone> readZones = zoneDatastoreCollection.findAll();
            if (!readZones.iterator().hasNext()) {
                final InputStream inputStream = Initialization.class.getResourceAsStream("/zones.json");
                final List<Zone> zones = objectMapper.readValue(inputStream, new TypeReference<List<Zone>>() {
                });
                zoneDatastoreCollection.save(zones);
            }
        } catch (final IOException e) {
            LOGGER.error("Error initializing zones", e);
        }
    }

    public void initializeScheduler() {
        try {
            /*
            scheduler.addCalendar("AnnualCalendar", new AnnualCalendar(), true, false);
            scheduler.addCalendar("HolidayCalendar", new HolidayCalendar(), true, false);
            scheduler.addCalendar("MonthlyCalendar", new MonthlyCalendar(), true, false);
            scheduler.addCalendar("WeeklyCalendar", new WeeklyCalendar(), true, false);
            */

            scheduler.start();
        } catch (final SchedulerException e) {
            LOGGER.error("Error starting scheduler", e);
        }
    }
}
