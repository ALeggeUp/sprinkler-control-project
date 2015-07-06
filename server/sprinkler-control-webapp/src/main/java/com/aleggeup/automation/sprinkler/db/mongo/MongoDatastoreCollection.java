/**
 * MongoDatastoreCollection.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleggeup.automation.sprinkler.odm.DatastoreCollection;
import com.aleggeup.automation.sprinkler.persister.Persistable;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

/**
 * @author Stephen Legge
 * @param <T>
 */
public class MongoDatastoreCollection<T extends Persistable> implements DatastoreCollection<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDatastoreCollection.class);

    private static final String FIELD_ID = "_id";

    private final Class<T> type;

    private final MongoCollection<T> collection;

    public MongoDatastoreCollection(final Class<T> type, final MongoCollection<T> collection) {
        LOGGER.info("Creating MongoDatastoreCollection({})", type);
        this.type = type;
        this.collection = collection;
    }

    @Override
    public Iterable<T> findAll() {
        return collection.find();
    }

    @Override
    public T find(final Map<String, String> matcher) {
        final List<Bson> filters = new ArrayList<Bson>();
        for (final Entry<String, String> entry : matcher.entrySet()) {
            filters.add(Filters.eq(entry.getKey(), entry.getValue()));
        }

        return collection.find(Filters.and(filters)).first();
    }

    @Override
    public void save(final T toSave) {
        try {
            this.collection.insertOne(toSave);
        } catch (final MongoWriteException e) {
            LOGGER.warn("Duplicate Alert!", e);
        }
    }

    @Override
    public void save(final Iterable<T> items) {
        for (final T item : items) {
            save(item);
        }
    }

    @Override
    public T update(final T toUpdate) {
        LOGGER.info("update()");
        return collection.findOneAndReplace(Filters.eq(FIELD_ID, new ObjectId(toUpdate.getId())), toUpdate);
    }

    @Override
    public boolean delete(final T toDelete) {
        LOGGER.info("delete()");
        return collection.deleteOne(Filters.eq(FIELD_ID, new ObjectId(toDelete.getId()))).wasAcknowledged();
    }

    @Override
    public Class<T> classForCollection() {
        return type;
    }
}
