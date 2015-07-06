/**
 * ZoneProvisionedGpioPins.java
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

package com.aleggeup.automation.sprinkler.hw;

import com.aleggeup.automation.sprinkler.model.Zone;
import com.aleggeup.automation.sprinkler.odm.DatastoreCollection;
import com.aleggeup.automation.sprinkler.odm.DatastoreCollectionRegistry;
import com.google.inject.Inject;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * @author Stephen Legge
 */
public class ZoneProvisionedGpioPins implements ProvisionedPins {

    private final GpioController gpioController;

    private final DatastoreCollection<Zone> zoneDatastoreCollection;

    @Inject
    public ZoneProvisionedGpioPins(final GpioController gpioController,
            final DatastoreCollectionRegistry collectionRegistry) {
        this.gpioController = gpioController;
        this.zoneDatastoreCollection = collectionRegistry.get(Zone.class);
    }

    @Override
    public void initialize() {
        for (final Zone zone : zoneDatastoreCollection.findAll()) {
            if (zone.getGpioOutput() != null) {
                gpioController.provisionDigitalOutputPin(RaspiPin.getPinByName(zone.getGpioOutput()), PinState.LOW);
            }
        }
    }
}
