/**
 * QuartzSchedulerResourcesProvider.java
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

import org.quartz.SchedulerConfigException;
import org.quartz.core.QuartzSchedulerResources;
import org.quartz.impl.DefaultThreadExecutor;
import org.quartz.impl.StdJobRunShellFactory;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.spi.JobStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Stephen Legge
 */
public class QuartzSchedulerResourcesProvider implements Provider<QuartzSchedulerResources> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzSchedulerResourcesProvider.class);

    private static final String SCHEDULER_NAME = "CustomQuartzScheduler";

    private static final String SCHEDULER_INSTANCE_ID = "NON_CLUSTERED";

    private static final String SCHEDULER_THREAD_NAME = "QuartzSchedulerThread";

    private final JobStore jobStore;

    @Inject
    public QuartzSchedulerResourcesProvider(final JobStore jobStore) {
        this.jobStore = jobStore;
    }

    @Override
    public QuartzSchedulerResources get() {

        final SimpleThreadPool simpleThreadPool = new SimpleThreadPool(10, Thread.NORM_PRIORITY);
        try {
            simpleThreadPool.initialize();
            simpleThreadPool.setMakeThreadsDaemons(true);
        } catch (final SchedulerConfigException e) {
            LOGGER.warn("Unable to initialize Scheduler ThreadPool", e);
        }

        final QuartzSchedulerResources quartzSchedulerResources = new QuartzSchedulerResources();
        quartzSchedulerResources.setName(SCHEDULER_NAME);
        quartzSchedulerResources.setInstanceId(SCHEDULER_INSTANCE_ID);
        quartzSchedulerResources.setJobStore(jobStore);
        quartzSchedulerResources.setMakeSchedulerThreadDaemon(true);
        quartzSchedulerResources.setRunUpdateCheck(false);
        quartzSchedulerResources.setJMXExport(false);
        quartzSchedulerResources.setJobRunShellFactory(new StdJobRunShellFactory());

        quartzSchedulerResources.setThreadName(SCHEDULER_THREAD_NAME);
        quartzSchedulerResources.setThreadExecutor(new DefaultThreadExecutor());
        quartzSchedulerResources.setThreadPool(simpleThreadPool);
        quartzSchedulerResources.setThreadsInheritInitializersClassLoadContext(true);

        return quartzSchedulerResources;
    }
}
