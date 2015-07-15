/**
 * Startup.java
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

package com.aleggeup.automation.sprinkler.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleggeup.automation.analytics.AnalyticsService;
import com.aleggeup.automation.sprinkler.hw.ProvisionedPins;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Stephen Legge
 */
@Singleton
public class Startup {

    public static final Logger LOGGER = LoggerFactory.getLogger(Startup.class);

    private final AnalyticsService analyticsService;

    private final Initialization initialization;

    private final ProvisionedPins provisionedPins;

    @Inject
    public Startup(final AnalyticsService analyticsService, final Initialization initialization,
            final ProvisionedPins provisionedPins) {
        LOGGER.info("--- Application Startup ---");
        this.analyticsService = analyticsService;
        this.initialization = initialization;
        this.provisionedPins = provisionedPins;
        initialize();
    }

    public final void initialize() {
        analyticsService.start();
        initialization.initializeZones();
        initialization.initializeScheduler();
        provisionedPins.initialize();
    }
}
