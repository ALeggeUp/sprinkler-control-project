/**
 * GuiceConfig.java
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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.core.QuartzSchedulerResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleggeup.automation.config.ServerProperties;
import com.aleggeup.automation.config.SpecifiedServerProperties;
import com.aleggeup.automation.config.ServerProperties.PersistentStorageMode;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * @author Stephen Legge
 */
public class GuiceConfig extends GuiceServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuiceConfig.class);

    private final ServerProperties serverProperties = new SpecifiedServerProperties();

    /* (non-Javadoc)
     * @see com.google.inject.servlet.GuiceServletContextListener#getInjector()
     */
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(pickModules(serverProperties));
    }

    protected Iterable<Module> pickModules(final ServerProperties serverProperties) {
        final List<Module> modules = new ArrayList<Module>();

        addConditionalModules(serverProperties.persistentStorageMode(), modules);

        modules.add(new MainServletModule());
        modules.add(new DataMappingModule());
        modules.add(new SchedulerModule());
        modules.add(new HardwareModule());
        modules.add(new StartupModule(serverProperties));

        return modules;
    }

    protected void addConditionalModules(final PersistentStorageMode storageMode, final List<Module> modules) {
        switch (storageMode) {
            case HOSTED:
                modules.add(new MongoDatabaseModule());
                break;
            case CLOUD:
            default:
                modules.add(new ParseDatabaseModule());
                break;
        }
    }

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        LOGGER.info("contextInitialized");
        super.contextInitialized(servletContextEvent);
    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        LOGGER.info("contextDestroyed");
        final ServletContext servletContext = servletContextEvent.getServletContext();
        final Injector injector = (Injector) servletContext.getAttribute(Injector.class.getName());
        final QuartzSchedulerResources qsr = injector.getInstance(QuartzSchedulerResources.class);
        qsr.getThreadPool().shutdown(true);
        final Scheduler scheduler = injector.getInstance(Scheduler.class);
        try {
            scheduler.shutdown(true);
        } catch (final SchedulerException e) {
            LOGGER.warn("Unable to shutdown scheduler", e);
        }
        super.contextDestroyed(servletContextEvent);
    }
}
