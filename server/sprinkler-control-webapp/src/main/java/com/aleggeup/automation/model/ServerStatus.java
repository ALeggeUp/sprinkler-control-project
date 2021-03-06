/**
 * ServerStatus.java
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

package com.aleggeup.automation.model;

import java.io.Serializable;
import java.util.Date;

import com.aleggeup.automation.config.ServerProperties;
import com.aleggeup.automation.config.ServerProperties.PersistentStorageMode;
import com.aleggeup.automation.config.ServerProperties.ServerEnvironment;
import com.aleggeup.automation.identity.ServerIdentification;
import com.google.inject.Inject;

/**
 * @author Stephen Legge
 */
public class ServerStatus implements Serializable {

    private static final long serialVersionUID = -3220995295233288678L;

    private final ServerIdentification serverIdentification;

    private final ServerProperties serverProperties;

    @Inject
    public ServerStatus(final ServerIdentification serverIdentification, final ServerProperties serverProperties) {
        this.serverIdentification = serverIdentification;
        this.serverProperties = serverProperties;
    }

    public ServerEnvironment getServerEnvironment() {
        return serverProperties.serverEnvironment();
    }

    public PersistentStorageMode getServerMode() {
        return serverProperties.persistentStorageMode();
    }

    public ServerIdentification getServerIdentification() {
        return serverIdentification;
    }

    public long getServerTime() {
        return new Date().getTime();
    }
}
