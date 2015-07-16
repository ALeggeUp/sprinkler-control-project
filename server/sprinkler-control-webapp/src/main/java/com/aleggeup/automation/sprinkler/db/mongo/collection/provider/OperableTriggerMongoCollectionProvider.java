/**
 * OperableTriggerMongoCollectionProvider.java
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

package com.aleggeup.automation.sprinkler.db.mongo.collection.provider;

import java.util.HashMap;
import java.util.Map;

import com.aleggeup.automation.config.ServerProperties;
import com.aleggeup.automation.schedule.quartz.model.OperableTriggerWrapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

/**
 * @author Stephen Legge
 *
 */
public class OperableTriggerMongoCollectionProvider implements Provider<MongoCollection<OperableTriggerWrapper>> {

    private final MongoDatabase mongoDatabase;

    private final ServerProperties serverProperties;

    @Inject
    public OperableTriggerMongoCollectionProvider(final MongoDatabase mongoDatabase,
            final ServerProperties serverProperties) {
        this.mongoDatabase = mongoDatabase;
        this.serverProperties = serverProperties;
    }

    @Override
    public MongoCollection<OperableTriggerWrapper> get() {
        final MongoCollection<OperableTriggerWrapper> collection = mongoDatabase.getCollection(
                serverProperties.collectionPrefix() + "triggers", OperableTriggerWrapper.class);
        final Map<String, Object> indexMap = new HashMap<String, Object>();
        indexMap.put("triggerName", new Integer(1));
        indexMap.put("triggerGroup", new Integer(1));
        collection.createIndex(new BasicDBObject(indexMap), new IndexOptions().unique(true).name("TriggerKey"));

        return collection;
    }
}
