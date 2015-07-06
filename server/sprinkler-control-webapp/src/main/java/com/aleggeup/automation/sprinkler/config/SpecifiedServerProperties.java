/**
 * SpecifiedServerProperties.java
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

package com.aleggeup.automation.sprinkler.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Stephen Legge
 */
public class SpecifiedServerProperties extends DefaultServerProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpecifiedServerProperties.class);

    private static final String KEY_ENVIRONMENT_PROPERTY = "server-environment";
    private static final String KEY_MODE_PROPERTY = "storage-mode";

    @Inject
    public SpecifiedServerProperties() {
    }

    @Override
    public ServerEnvironment serverEnvironment() {
        return getProperty(ServerEnvironment.class, KEY_ENVIRONMENT_PROPERTY, super.serverEnvironment());
    }

    @Override
    public PersistentStorageMode persistentStorageMode() {
        return getProperty(PersistentStorageMode.class, KEY_MODE_PROPERTY, super.persistentStorageMode());
    }

    private <E extends Enum<E>> E getProperty(final Class<E> enumClass, final String propertyName, final E defaultValue) {
        final String propertyValue = System.getProperty(propertyName);

        E retEnum = null;
        if (propertyValue != null) {
            try {
                retEnum = Enum.valueOf(enumClass, propertyValue.toUpperCase());
            } catch (final IllegalArgumentException e) {
                LOGGER.warn("Unable to set enum {} to {}", enumClass, propertyValue);
            }
        }

        if (retEnum == null) {
            LOGGER.warn("Using default value for property {} (default:{})", propertyName, defaultValue);
            retEnum = defaultValue;
        }

        return retEnum;
    }
}
