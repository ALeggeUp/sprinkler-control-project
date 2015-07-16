/**
 * AbstractParseConverter.java
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

package com.aleggeup.automation.sprinkler.db.parse.converter;

import java.util.ArrayList;
import java.util.List;

import org.parse4j.ParseObject;

import com.aleggeup.automation.persist.Persistable;
import com.aleggeup.automation.sprinkler.db.parse.ParseConverter;

/**
 * @author Stephen Legge
 *
 */
public abstract class AbstractParseConverter<T extends Persistable, P extends ParseObject> implements
        ParseConverter<T, P> {

    public abstract P encode(final T toConvert);

    public abstract T decode(final P toConvert);

    @Override
    public Iterable<P> encodeList(final Iterable<T> toConvert) {
        final List<P> encoded = new ArrayList<>();
        for (final T type : toConvert) {
            encoded.add(encode(type));
        }

        return encoded;
    }

    @Override
    public Iterable<T> decodeList(final Iterable<P> toConvert) {
        final List<T> decoded = new ArrayList<>();
        for (final P parseObject : toConvert) {
            decoded.add(decode(parseObject));
        }

        return decoded;
    }
}
