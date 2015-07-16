/**
 * MongoCodecProviderProvider.java
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

package com.aleggeup.automation.sprinkler.db.mongo.provider;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;

import com.aleggeup.automation.schedule.quartz.model.CalendarWrapper;
import com.aleggeup.automation.schedule.quartz.model.JobDetailWrapper;
import com.aleggeup.automation.schedule.quartz.model.OperableTriggerWrapper;
import com.aleggeup.automation.sprinkler.db.mongo.codec.provider.SprinklerModelCodecProvider;
import com.aleggeup.automation.sprinkler.model.Zone;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Stephen Legge
 */
public class MongoCodecProviderProvider implements Provider<CodecProvider> {

    private final Codec<CalendarWrapper> calendarCodec;
    private final Codec<JobDetailWrapper> jobDetailCodec;
    private final Codec<OperableTriggerWrapper> operableTriggerCodec;
    private final Codec<Zone> zoneCodec;

    @Inject
    public MongoCodecProviderProvider(final Codec<CalendarWrapper> calendarCodec,
            final Codec<JobDetailWrapper> jobDetailCodec,
            final Codec<OperableTriggerWrapper> operableTriggerCodec,
            final Codec<Zone> zoneCodec) {
        this.calendarCodec = calendarCodec;
        this.jobDetailCodec = jobDetailCodec;
        this.operableTriggerCodec = operableTriggerCodec;
        this.zoneCodec = zoneCodec;
    }

    @Override
    public CodecProvider get() {
        final SprinklerModelCodecProvider provider = new SprinklerModelCodecProvider();

        provider.addCodec(calendarCodec);
        provider.addCodec(jobDetailCodec);
        provider.addCodec(operableTriggerCodec);
        provider.addCodec(zoneCodec);

        return provider;
    }
}
