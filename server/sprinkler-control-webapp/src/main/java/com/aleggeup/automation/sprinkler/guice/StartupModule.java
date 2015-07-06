/**
 * StartupModule.java
 *
 * Copyright 2014-2015 [A Legge Up Consulting]
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

package com.aleggeup.automation.sprinkler.guice;

import com.aleggeup.automation.sprinkler.config.ServerProperties;
import com.aleggeup.automation.sprinkler.startup.Initialization;
import com.aleggeup.automation.sprinkler.startup.Startup;
import com.google.inject.AbstractModule;

/**
 * @author Stephen Legge
 */
public class StartupModule extends AbstractModule {

    private final ServerProperties serverProperties;

    public StartupModule(final ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    /* (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        bind(ServerProperties.class).toInstance(serverProperties);

        bind(Initialization.class);
        bind(Startup.class).asEagerSingleton();
    }
}
