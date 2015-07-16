/**
 * ParseDatabaseModule.java
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

import com.aleggeup.automation.odm.Datastore;
import com.aleggeup.automation.odm.DatastoreCollectionRegistry;
import com.aleggeup.automation.sprinkler.db.parse.ParseDatastore;
import com.aleggeup.automation.sprinkler.db.parse.collection.ParseDatastoreCollectionRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * @author Stephen Legge
 *
 */
public class ParseDatabaseModule extends AbstractModule {

    public ParseDatabaseModule() {
    }

    /* (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        bind(Datastore.class).to(ParseDatastore.class).in(Singleton.class);
        bind(DatastoreCollectionRegistry.class).to(ParseDatastoreCollectionRegistry.class).in(Singleton.class);
    }
}
