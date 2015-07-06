/**
 * DataMappingModule.java
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

import com.aleggeup.automation.sprinkler.odm.provider.ObjectMapperProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;

/**
 * @author Stephen Legge
 */
public class DataMappingModule extends AbstractModule {

    /* (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class);
    }

}
