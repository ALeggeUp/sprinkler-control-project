/**
 * ZoneMongoCollectionProvider.java
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

import com.aleggeup.automation.config.ServerProperties;
import com.aleggeup.automation.sprinkler.model.Zone;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

/**
 * @author Stephen Legge
 */
public class ZoneMongoCollectionProvider implements Provider<MongoCollection<Zone>> {

    private final MongoDatabase mongoDatabase;

    private final ServerProperties serverProperties;

    @Inject
    public ZoneMongoCollectionProvider(final MongoDatabase mongoDatabase, final ServerProperties serverProperties) {
        this.mongoDatabase = mongoDatabase;
        this.serverProperties = serverProperties;
    }

    @Override
    public MongoCollection<Zone> get() {
        final MongoCollection<Zone> collection =
                mongoDatabase.getCollection(serverProperties.collectionPrefix() + "zones", Zone.class);
        collection.createIndex(new BasicDBObject("number", 1), new IndexOptions().unique(true).name("ZoneIndex"));
        return collection;
    }
}
