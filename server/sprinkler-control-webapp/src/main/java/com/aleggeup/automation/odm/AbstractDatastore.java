/**
 * AbstractDatastore.java
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

package com.aleggeup.automation.odm;

import java.util.HashMap;
import java.util.Map;

import com.aleggeup.automation.persist.Persistable;

/**
 * @author Stephen Legge
 */
public abstract class AbstractDatastore implements Datastore {

    private final Map<Class<? extends Persistable>, DatastoreCollection<? extends Persistable>> collections;

    public AbstractDatastore() {
        collections = new HashMap<>();
    }

    /* (non-Javadoc)
     * @see com.aleggeup.automation.sprinkler.odm.Datastore#connect()
     */
    @Override
    public void connect() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.aleggeup.automation.sprinkler.odm.Datastore#registerCollection(DatastoreCollection)
     */
    @Override
    public <T extends Persistable> void registerCollection(final DatastoreCollection<T> collection) {
        if (!collections.containsKey(collection.classForCollection())) {
            collections.put(collection.classForCollection(), collection);
        }
    }

    /* (non-Javadoc)
     * @see com.aleggeup.automation.sprinkler.odm.Datastore#getCollection(java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Persistable> DatastoreCollection<T> getCollection(final Class<T> clazz) {
        return (DatastoreCollection<T>) collections.get(clazz);
    }

    protected abstract <T extends Persistable> DatastoreCollection<T> createCollection(final Class<T> clazz);
}
