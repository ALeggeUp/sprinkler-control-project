/**
 * SavedServerProperties.java
 *
 * Copyright 2016 [A Legge Up Consulting]
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

package com.aleggeup.automation.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephen Legge
 */
public class SavedServerProperties extends DefaultServerProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(SavedServerProperties.class);

    private static final String APP_CONFIG_FILE = "app.properties";

    private final ServerEnvironment serverEnvironmentProperty;
    private final PersistentStorageMode persistentStorageModeProperty;

    public SavedServerProperties() {
        final Properties properties = this.getProperties();
        ServerEnvironment serverEnvironment = null;
        PersistentStorageMode persistentStorageMode = null;
        try {
            serverEnvironment = ServerEnvironment.valueOf(properties.getProperty(KEY_ENVIRONMENT_PROPERTY));
            persistentStorageMode = PersistentStorageMode.valueOf(properties.getProperty(KEY_MODE_PROPERTY));
        } catch (final IllegalArgumentException | NullPointerException e) {
            LOGGER.info("Unable to parse values from saved properties, will use defaults");
        }

        this.serverEnvironmentProperty = serverEnvironment;
        this.persistentStorageModeProperty = persistentStorageMode;
    }

    /* (non-Javadoc)
     * @see com.aleggeup.automation.config.DefaultServerProperties#serverEnvironment()
     */
    @Override
    public ServerEnvironment serverEnvironment() {
        ServerEnvironment serverEnvironment = null;

        if (this.serverEnvironmentProperty != null) {
            serverEnvironment = this.serverEnvironmentProperty;
        } else {
            serverEnvironment = super.serverEnvironment();
        }

        return serverEnvironment;
    }

    /* (non-Javadoc)
     * @see com.aleggeup.automation.config.DefaultServerProperties#persistentStorageMode()
     */
    @Override
    public PersistentStorageMode persistentStorageMode() {
        PersistentStorageMode persistentStorageMode = null;
        if (this.persistentStorageModeProperty != null) {
            persistentStorageMode = this.persistentStorageModeProperty;
        } else {
            persistentStorageMode = super.persistentStorageMode();
        }

        return persistentStorageMode;
    }

    public Properties getProperties() {
        return this.getProperties(APP_CONFIG_FILE);
    }

    public Properties getProperties(final String configFile) {

        final Properties props = new Properties();

        InputStream stream;
        stream = this.getInputStreamFromDisk(configFile);
        if (stream == null) {
            stream = this.getInputStreamFromResource(configFile);
        }

        try {
            if (stream == null) {
                throw new IOException(String.format("Unable to find %s on disk or classpath", configFile));
            }
            props.load(stream);
        } catch (final IOException e) {
            LOGGER.error(String.format("Failed to load %s", configFile));
            // Runtime.getRuntime().halt(1);
        }

        return props;
    }

    protected InputStream getInputStreamFromDisk(final String configFile) {

        final String configFilePath = System.getProperty("user.dir") + System.getProperty("file.separator")
                + configFile;

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(configFilePath);
        } catch (final IOException e) {
            return null;
        }

        return inputStream;
    }

    protected InputStream getInputStreamFromResource(final String configFile) {
        return this.getClass().getResourceAsStream(configFile);
    }
}
