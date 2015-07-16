/**
 * ParseDatastoreCollectionRegistry.java
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

package com.aleggeup.automation.sprinkler.db.parse.collection;

import java.util.HashMap;
import java.util.Map;

import org.parse4j.ParseObject;

import com.aleggeup.automation.config.ServerProperties;
import com.aleggeup.automation.odm.Datastore;
import com.aleggeup.automation.odm.DatastoreCollection;
import com.aleggeup.automation.odm.DatastoreCollectionRegistry;
import com.aleggeup.automation.persist.Persistable;
import com.aleggeup.automation.persist.parse.converter.CalendarWrapperParseConverter;
import com.aleggeup.automation.persist.parse.converter.JobDetailWrapperParseConverter;
import com.aleggeup.automation.persist.parse.converter.OperableTriggerWrapperParseConverter;
import com.aleggeup.automation.schedule.quartz.model.CalendarWrapper;
import com.aleggeup.automation.schedule.quartz.model.JobDetailWrapper;
import com.aleggeup.automation.schedule.quartz.model.OperableTriggerWrapper;
import com.aleggeup.automation.sprinkler.db.parse.converter.ZoneParseConverter;
import com.aleggeup.automation.sprinkler.model.Zone;
import com.google.inject.Inject;

/**
 * @author Stephen Legge
 */
public class ParseDatastoreCollectionRegistry implements DatastoreCollectionRegistry {

    private final Map<Class<? extends Persistable>, DatastoreCollection<? extends Persistable>> collections =
            new HashMap<>();

    @Inject
    public ParseDatastoreCollectionRegistry(final ServerProperties serverProperties, final Datastore datastore) {
        collections.put(Zone.class, new ParseDatastoreCollection<Zone, ParseObject>(Zone.class, "zone",
                new ZoneParseConverter()));
        collections.put(CalendarWrapper.class, new ParseDatastoreCollection<CalendarWrapper, ParseObject>(
                CalendarWrapper.class, "calendar", new CalendarWrapperParseConverter()));
        collections.put(JobDetailWrapper.class, new ParseDatastoreCollection<JobDetailWrapper, ParseObject>(
                JobDetailWrapper.class, "job", new JobDetailWrapperParseConverter()));
        collections.put(OperableTriggerWrapper.class,
                new ParseDatastoreCollection<OperableTriggerWrapper, ParseObject>(OperableTriggerWrapper.class,
                        "trigger", new OperableTriggerWrapperParseConverter()));
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
