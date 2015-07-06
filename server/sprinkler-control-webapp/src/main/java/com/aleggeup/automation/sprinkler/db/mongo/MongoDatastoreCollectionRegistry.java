/**
 * MongoDatastoreCollectionRegistry.java
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

package com.aleggeup.automation.sprinkler.db.mongo;

import java.util.HashMap;
import java.util.Map;

import com.aleggeup.automation.sprinkler.model.Zone;
import com.aleggeup.automation.sprinkler.odm.DatastoreCollection;
import com.aleggeup.automation.sprinkler.odm.DatastoreCollectionRegistry;
import com.aleggeup.automation.sprinkler.persister.Persistable;
import com.aleggeup.automation.sprinkler.schedule.quartz.model.CalendarWrapper;
import com.aleggeup.automation.sprinkler.schedule.quartz.model.JobDetailWrapper;
import com.aleggeup.automation.sprinkler.schedule.quartz.model.OperableTriggerWrapper;
import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;

/**
 * @author Stephen Legge
 */
public class MongoDatastoreCollectionRegistry implements DatastoreCollectionRegistry {

    private final Map<Class<? extends Persistable>, DatastoreCollection<? extends Persistable>> collections =
            new HashMap<>();

    @Inject
    public MongoDatastoreCollectionRegistry(final MongoCollection<CalendarWrapper> calendarMongoCollection,
            final MongoCollection<JobDetailWrapper> jobDetailMongoCollection,
            final MongoCollection<OperableTriggerWrapper> operableTriggerMongoCollection,
            final MongoCollection<Zone> zoneMongoCollection) {
        collections.put(CalendarWrapper.class, new MongoDatastoreCollection<CalendarWrapper>(CalendarWrapper.class,
                calendarMongoCollection));
        collections.put(JobDetailWrapper.class, new MongoDatastoreCollection<JobDetailWrapper>(JobDetailWrapper.class,
                jobDetailMongoCollection));
        collections.put(OperableTriggerWrapper.class, new MongoDatastoreCollection<OperableTriggerWrapper>(
                OperableTriggerWrapper.class, operableTriggerMongoCollection));
        collections.put(Zone.class, new MongoDatastoreCollection<Zone>(Zone.class, zoneMongoCollection));
    }

    /* (non-Javadoc)
     * @see DatastoreCollectionRegistry#get(java.lang.Class, com.mongodb.client.MongoCollection)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Persistable> DatastoreCollection<T> get(final Class<T> type) {
        return (DatastoreCollection<T>) collections.get(type);
    }
}
