/**
 * ParseDatastoreCollection.java
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.parse4j.ParseException;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleggeup.automation.odm.DatastoreCollection;
import com.aleggeup.automation.persist.Persistable;
import com.aleggeup.automation.persist.parse.ParseConverter;

/**
 * @author Stephen Legge
 */
public class ParseDatastoreCollection<T extends Persistable, P extends ParseObject> implements DatastoreCollection<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParseDatastoreCollection.class);

    private final Class<T> type;

    private final String collectionName;

    private final ParseConverter<T, P> converter;

    public ParseDatastoreCollection(final Class<T> type, final String collectionName,
            final ParseConverter<T, P> converter) {
        this.type = type;
        this.collectionName = collectionName;
        this.converter = converter;
    }

    @Override
    public Iterable<T> findAll() {
        final ParseQuery<P> query = ParseQuery.getQuery(collectionName);

        Iterable<T> converted = null;
        try {
            final List<P> list = query.find();
            if (list != null) {
                converted = converter.decodeList(list);
            }
        } catch (final ParseException e) {
            e.printStackTrace();
        } finally {
            if (converted == null) {
                converted = new ArrayList<T>();
            }
        }

        return converted;
    }

    @Override
    public T find(final Map<String, String> matcher) {
        final ParseQuery<P> query = ParseQuery.getQuery(collectionName);

        query.limit(1);

        for (final Entry<String, String> entry : matcher.entrySet()) {
            query.whereEqualTo(entry.getKey(), entry.getValue());
        }

        T found = null;
        try {
            found = converter.decode(query.find().get(0));
        } catch (final ParseException e) {
        }

        return found;
    }

    @Override
    public T update(final T toUpdate) {
        LOGGER.info("update()");
        save(toUpdate);

        return toUpdate;
    }

    @Override
    public void save(final T toSave) {
        try {
            final P p = converter.encode(toSave);
            p.setUpdatedAt(new Date());
            p.save();
        } catch (final ParseException e) {
            LOGGER.warn("Exception while saving/updating", e);
        }
    }

    @Override
    public void save(final Iterable<T> toSave) {
        for (final T each : toSave) {
            save(each);
        }
    }

    @Override
    public boolean delete(final T toDelete) {
        try {
            converter.encode(toDelete).delete();
        } catch (final ParseException e) {
            LOGGER.warn("Exception while deleting", e);
        }

        return true;
    }

    @Override
    public Class<T> classForCollection() {
        return type;
    }
}
