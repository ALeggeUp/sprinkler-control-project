/**
 * MongoDatastore.java
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

package com.aleggeup.automation.persist.mongo;

import com.aleggeup.automation.odm.AbstractDatastore;
import com.aleggeup.automation.odm.DatastoreCollection;
import com.aleggeup.automation.persist.Persistable;
import com.google.inject.Inject;

/**
 * @author Stephen Legge
 */
public class MongoDatastore extends AbstractDatastore {

    @Inject
    public MongoDatastore() {
    }

    @Override
    protected <T extends Persistable> DatastoreCollection<T> createCollection(final Class<T> clazz) {
        return null;
    }
}
