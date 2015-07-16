/**
 * ParseConverter.java
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

package com.aleggeup.automation.persist.parse;

import org.parse4j.ParseObject;

import com.aleggeup.automation.persist.Persistable;

/**
 * @author Stephen Legge
 */
public interface ParseConverter<T extends Persistable, P extends ParseObject> {

    Iterable<P> encodeList(Iterable<T> toConvert);

    P encode(T toConvert);

    Iterable<T> decodeList(Iterable<P> toConvert);

    T decode(P toConvert);
}
