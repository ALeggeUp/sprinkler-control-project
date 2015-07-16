/**
 * GpioPinControllerJob.java
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

package com.aleggeup.automation.sprinkler.schedule.quartz.job;

import org.joda.time.DateTime;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.aleggeup.automation.odm.DatastoreCollection;
import com.aleggeup.automation.odm.DatastoreCollectionRegistry;
import com.aleggeup.automation.sprinkler.model.Zone;
import com.aleggeup.automation.sprinkler.model.Zone.ZoneState;
import com.google.inject.Inject;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

/**
 * @author Stephen Legge
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class GpioPinControllerJob implements Job {

    private static final String TURN_ON = "turnOn";

    private static final String ZONE = "zone";

    private final GpioController gpioController;

    private final DatastoreCollection<Zone> zoneDatastoreCollection;

    @Inject
    public GpioPinControllerJob(final GpioController gpioController,
            final DatastoreCollectionRegistry collectionRegistry) {
        this.gpioController = gpioController;
        this.zoneDatastoreCollection = collectionRegistry.get(Zone.class);
    }

    /* (non-Javadoc)
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        final JobDetail jobDetail = context.getJobDetail();
        final Zone zone = (Zone) jobDetail.getJobDataMap().get(ZONE);
        final boolean state = context.getJobDetail().getJobDataMap().getBoolean(TURN_ON);

        GpioPinDigitalOutput pin = null;
        for (final GpioPin gpioPin : gpioController.getProvisionedPins()) {
            if (gpioPin.getName().equals(zone.getGpioOutput())) {
                pin = (GpioPinDigitalOutput) gpioPin;
            }
        }

        if (pin == null) {
            throw new JobExecutionException("Unable to find provisioned pin " + zone.getGpioOutput());
        }

        if (state) {
            pin.high();
            jobDetail.getJobDataMap().put(TURN_ON, false);
        } else {
            pin.low();
            zone.setState(ZoneState.NOT_RUNNING);
            zone.setLastEnd(new DateTime());
            zoneDatastoreCollection.update(zone);
        }
    }
}
