/**
 * MongoClientOptionsProvider.java
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

package com.aleggeup.automation.sprinkler.db.mongo.provider;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

/**
 * @author Stephen Legge
 */
public class MongoClientOptionsProvider implements Provider<MongoClientOptions> {

    private final CodecProvider codecProvider;

    @Inject
    public MongoClientOptionsProvider(final CodecProvider codecProvider) {
        this.codecProvider = codecProvider;
    }

    @Override
    public MongoClientOptions get() {
        return MongoClientOptions.builder().codecRegistry(
                CodecRegistries.fromRegistries(
                        MongoClient.getDefaultCodecRegistry(),
                        CodecRegistries.fromProviders(this.codecProvider)))
                .build();
    }
}
