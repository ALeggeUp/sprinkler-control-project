/**
 * DefaultServerProperties.java
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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephen Legge
 */
public class DefaultServerProperties implements ServerProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultServerProperties.class);

    private static final String SEPARATOR = ".";

    public DefaultServerProperties() {
    }

    @Override
    public ServerEnvironment serverEnvironment() {
        return ServerEnvironment.LOCAL;
    }

    @Override
    public PersistentStorageMode persistentStorageMode() {
        return PersistentStorageMode.CLOUD;
    }

    @Override
    public String collectionPrefix() {
        final ServerEnvironment serverEnvironment = serverEnvironment();

        String identifier = "";
        if (ServerEnvironment.LOCAL.equals(serverEnvironment)
                || ServerEnvironment.INTEGRATION.equals(serverEnvironment)) {
            try {
                identifier = SEPARATOR + InetAddress.getLocalHost().getHostName();
            } catch (final UnknownHostException e) {
                LOGGER.warn("Unable to determine hostname", e);
            }
        }
        return serverEnvironment().toString().toLowerCase() + identifier + SEPARATOR;
    }
}
