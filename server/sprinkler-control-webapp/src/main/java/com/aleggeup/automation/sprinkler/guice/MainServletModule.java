/**
 * MainServletModule.java
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

import java.util.HashMap;
import java.util.Map;

import com.aleggeup.automation.sprinkler.resource.filter.CorsFilter;
import com.google.inject.Singleton;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * @author Stephen Legge
 */
public class MainServletModule extends JerseyServletModule {

    private static final String GZIP_FILTER =
            com.sun.jersey.api.container.filter.GZIPContentEncodingFilter.class.getName();
    private static final String ENTITY_LOGGING = "com.sun.jersey.config.feature.logging.DisableEntitylogging";

    private static final String PATH_SERVICES = "/services/*";

    /*
     * (non-Javadoc)
     *
     * @see com.google.inject.servlet.ServletModule#configureServlets()
     */
    @Override
    protected void configureServlets() {
        final Map<String, String> params = new HashMap<String, String>();

        params.put(JSONConfiguration.FEATURE_POJO_MAPPING, String.valueOf(true));

        params.put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, GZIP_FILTER);
        params.put(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, GZIP_FILTER);

        params.put(PackagesResourceConfig.PROPERTY_PACKAGES, "com.aleggeup.automation.sprinkler.resource");
        /*
        params.put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS,
                com.sun.jersey.api.container.filter.LoggingFilter.class.getName());

        params.put(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS,
                com.sun.jersey.api.container.filter.LoggingFilter.class.getName());
        */
        params.put(ENTITY_LOGGING, String.valueOf(true));

        bind(CorsFilter.class).in(Singleton.class);

        // explicitly bind GuiceContainer before binding Jersey resources
        // otherwise resource won't be available for GuiceContainer
        // when using two-phased injection
        this.bind(GuiceContainer.class);

        this.filter(PATH_SERVICES).through(CorsFilter.class);

        // Serve resources with Jerseys GuiceContainer
        this.serve(PATH_SERVICES).with(GuiceContainer.class, params);
    }
}
