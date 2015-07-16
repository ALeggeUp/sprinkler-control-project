/**
 * ZoneResource.java
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

package com.aleggeup.automation.sprinkler.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleggeup.automation.odm.DatastoreCollection;
import com.aleggeup.automation.odm.DatastoreCollectionRegistry;
import com.aleggeup.automation.sprinkler.model.Zone;
import com.aleggeup.automation.sprinkler.model.Zone.ZoneState;
import com.aleggeup.automation.sprinkler.resource.config.WebResourceParameters;
import com.aleggeup.automation.sprinkler.schedule.quartz.job.GpioPinControllerJob;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Stephen Legge
 */
@Singleton
@Path(WebResourceParameters.SERVICE_NAME_ZONES)
public class ZoneResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZoneResource.class);

    private static final String IDENTITY_MANUAL_START = "manual-start";

    private final Scheduler scheduler;

    private final DatastoreCollection<Zone> zoneDatastoreCollection;

    @Inject
    public ZoneResource(final Scheduler scheduler, final DatastoreCollectionRegistry collectionRegistry) {
        this.scheduler = scheduler;
        this.zoneDatastoreCollection = collectionRegistry.get(Zone.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Iterable<Zone> getZones() {
        return zoneDatastoreCollection.findAll();
    }

    @GET
    @Path("/{" + WebResourceParameters.SERVICE_PARAM_ZONE_ID + "}")
    @Produces(MediaType.APPLICATION_JSON)
    public Zone getZone(@PathParam(WebResourceParameters.SERVICE_PARAM_ZONE_ID) final String zoneId) {
        final Iterable<Zone> zones = zoneDatastoreCollection.findAll();
        int zoneNumber = 0;
        try {
            zoneNumber = Integer.parseInt(zoneId);
        } catch (final NumberFormatException e) {
            zoneNumber = Integer.MIN_VALUE;
        }
        for (final Zone zone : zones) {
            if (zone.getId().equals(zoneId) || (zoneNumber > 0 && zoneNumber == zone.getNumber())) {
                return zone;
            }
        }
        return null;
    }

    @PUT
    @Path("/{" + WebResourceParameters.SERVICE_PARAM_ZONE_ID + "}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateZone(final Zone zone) {
        zoneDatastoreCollection.update(zone);

        return Response.ok(zone).build();
    }

    @PUT
    @Path("/{" + WebResourceParameters.SERVICE_PARAM_ZONE_ID + "}/" + WebResourceParameters.SERVICE_PATH_ACTION_START)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response startZone(final Zone zone) {
        zone.setState(ZoneState.RUNNING);
        zone.setLastStart(DateTime.now());
        zoneDatastoreCollection.update(zone);

        try {
            final JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("turnOn", true);
            jobDataMap.put("zone", zone);
            final JobDetail jobDetail = JobBuilder.newJob(GpioPinControllerJob.class)
                    .setJobData(jobDataMap)
                    .withIdentity(zone.getId(), IDENTITY_MANUAL_START)
                    .build();
            final Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(zone.getId(), IDENTITY_MANUAL_START)
                    .startNow()
                    // .modifiedByCalendar("AnnualCalendar")
                    .withSchedule(SimpleScheduleBuilder.repeatSecondlyForTotalCount(2, zone.getDurationInSeconds()))
                    .build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (final SchedulerException e) {
            LOGGER.error("Error creating new scheduler", e);
        }

        return Response.ok(zone).build();
    }
}
