/**
 * QuartzSchedulerProvider.java
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

package com.aleggeup.automation.schedule.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.core.QuartzScheduler;
import org.quartz.core.QuartzSchedulerResources;
import org.quartz.impl.StdScheduler;
import org.quartz.spi.JobStore;
import org.quartz.spi.SchedulerSignaler;

import com.aleggeup.automation.schedule.GuiceJobFactory;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Stephen Legge
 */
public class QuartzSchedulerProvider implements Provider<Scheduler> {

    private static final long IDLE_WAIT_TIME = 30000L;

    private final QuartzSchedulerResources quartzSchedulerResources;

    private final GuiceJobFactory guiceJobFactory;

    @Inject
    public QuartzSchedulerProvider(final QuartzSchedulerResources quartzSchedulerResources,
            final GuiceJobFactory guiceJobFactory) {
        this.quartzSchedulerResources = quartzSchedulerResources;
        this.guiceJobFactory = guiceJobFactory;
    }

    @Override
    public Scheduler get() {
        QuartzScheduler quartzScheduler = null;
        try {
            quartzScheduler = new QuartzScheduler(this.quartzSchedulerResources, IDLE_WAIT_TIME, Long.MIN_VALUE);
            final JobStore jobStore = quartzSchedulerResources.getJobStore();
            final SchedulerSignaler signaler = quartzScheduler.getSchedulerSignaler();
            jobStore.initialize(null, signaler);
            quartzScheduler.setJobFactory(guiceJobFactory);
            quartzScheduler.initialize();
        } catch (final SchedulerException e) {
            e.printStackTrace();
        }

        return new StdScheduler(quartzScheduler);
    }
}
