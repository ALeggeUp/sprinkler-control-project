/**
 * MongoDatabaseModule.java
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

package com.aleggeup.automation.sprinkler.guice;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;

import com.aleggeup.automation.sprinkler.db.mongo.MongoDatastoreCollectionRegistry;
import com.aleggeup.automation.sprinkler.db.mongo.codec.CalendarCodec;
import com.aleggeup.automation.sprinkler.db.mongo.codec.JobDetailCodec;
import com.aleggeup.automation.sprinkler.db.mongo.codec.TriggerCodec;
import com.aleggeup.automation.sprinkler.db.mongo.codec.ZoneCodec;
import com.aleggeup.automation.sprinkler.db.mongo.collection.provider.CalendarMongoCollectionProvider;
import com.aleggeup.automation.sprinkler.db.mongo.collection.provider.JobDetailMongoCollectionProvider;
import com.aleggeup.automation.sprinkler.db.mongo.collection.provider.OperableTriggerMongoCollectionProvider;
import com.aleggeup.automation.sprinkler.db.mongo.collection.provider.ZoneMongoCollectionProvider;
import com.aleggeup.automation.sprinkler.db.mongo.provider.MongoClientOptionsProvider;
import com.aleggeup.automation.sprinkler.db.mongo.provider.MongoClientProvider;
import com.aleggeup.automation.sprinkler.db.mongo.provider.MongoCodecProviderProvider;
import com.aleggeup.automation.sprinkler.db.mongo.provider.MongoDatabaseProvider;
import com.aleggeup.automation.sprinkler.model.Zone;
import com.aleggeup.automation.sprinkler.odm.DatastoreCollectionRegistry;
import com.aleggeup.automation.sprinkler.schedule.quartz.model.CalendarWrapper;
import com.aleggeup.automation.sprinkler.schedule.quartz.model.JobDetailWrapper;
import com.aleggeup.automation.sprinkler.schedule.quartz.model.OperableTriggerWrapper;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * @author Stephen Legge
 */
public class MongoDatabaseModule extends AbstractModule {

    /* (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        bind(MongoClientOptions.class).toProvider(MongoClientOptionsProvider.class);
        bind(MongoClient.class).toProvider(MongoClientProvider.class);
        bind(MongoDatabase.class).toProvider(MongoDatabaseProvider.class);
        bind(CodecProvider.class).toProvider(MongoCodecProviderProvider.class);

        bind(new TypeLiteral<Codec<CalendarWrapper>>() {
        }).to(CalendarCodec.class);
        bind(new TypeLiteral<Codec<JobDetailWrapper>>() {
        }).to(JobDetailCodec.class);
        bind(new TypeLiteral<Codec<OperableTriggerWrapper>>() {
        }).to(TriggerCodec.class);
        bind(new TypeLiteral<Codec<Zone>>() {
        }).to(ZoneCodec.class);

        bind(new TypeLiteral<MongoCollection<CalendarWrapper>>() {
        }).toProvider(CalendarMongoCollectionProvider.class);
        bind(new TypeLiteral<MongoCollection<JobDetailWrapper>>() {
        }).toProvider(JobDetailMongoCollectionProvider.class);
        bind(new TypeLiteral<MongoCollection<OperableTriggerWrapper>>() {
        }).toProvider(OperableTriggerMongoCollectionProvider.class);
        bind(new TypeLiteral<MongoCollection<Zone>>() {
        }).toProvider(ZoneMongoCollectionProvider.class);

        bind(DatastoreCollectionRegistry.class).to(MongoDatastoreCollectionRegistry.class).in(Singleton.class);
    }
}
