/**
 * HardwareModule.java
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

import com.aleggeup.automation.identity.SimpleServerIdentification;
import com.aleggeup.automation.identity.ServerIdentification;
import com.aleggeup.automation.sprinkler.hw.ProvisionedPins;
import com.aleggeup.automation.sprinkler.hw.ZoneProvisionedGpioPins;
import com.aleggeup.automation.sprinkler.hw.provider.GpioControllerProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.pi4j.io.gpio.GpioController;

/**
 * @author Stephen Legge
 */
public class HardwareModule extends AbstractModule {

    /* (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        bind(ServerIdentification.class).to(SimpleServerIdentification.class);
        bind(GpioController.class).toProvider(GpioControllerProvider.class).in(Singleton.class);
        bind(ProvisionedPins.class).to(ZoneProvisionedGpioPins.class);
    }
}
