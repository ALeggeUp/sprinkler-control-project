/**
 * ScheduleModule.java
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

package com.aleggeup.automation.sprinkler.guice;

import org.quartz.Scheduler;
import org.quartz.core.QuartzSchedulerResources;
import org.quartz.spi.JobStore;

import com.aleggeup.automation.schedule.DatastoreJobStore;
import com.aleggeup.automation.schedule.GuiceJobFactory;
import com.aleggeup.automation.schedule.quartz.QuartzSchedulerProvider;
import com.aleggeup.automation.schedule.quartz.QuartzSchedulerResourcesProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * @author Stephen Legge
 */
public class SchedulerModule extends AbstractModule {

    /* (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        this.bind(GuiceJobFactory.class);
        this.bind(JobStore.class).to(DatastoreJobStore.class);
        // this.bind(JobStore.class).to(RAMJobStore.class);

        this.bind(QuartzSchedulerResources.class).toProvider(QuartzSchedulerResourcesProvider.class)
                .in(Singleton.class);
        this.bind(Scheduler.class).toProvider(QuartzSchedulerProvider.class).in(Singleton.class);
    }
}
