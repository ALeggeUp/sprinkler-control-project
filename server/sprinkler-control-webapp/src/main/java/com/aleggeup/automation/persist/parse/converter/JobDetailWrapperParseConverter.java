/**
 * JobDetailWrapperParseConverter.java
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

package com.aleggeup.automation.persist.parse.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.parse4j.ParseObject;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleggeup.automation.persist.mongo.codec.JobDetailCodec;
import com.aleggeup.automation.schedule.quartz.model.JobDetailWrapper;
import com.sun.jersey.core.util.Base64;

/**
 * @author Stephen Legge
 *
 */
public class JobDetailWrapperParseConverter extends AbstractParseConverter<JobDetailWrapper, ParseObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobDetailWrapperParseConverter.class);

    @Override
    public JobDetailWrapper decode(final ParseObject toConvert) {
        final JobDetailWrapper jobDetail = new JobDetailWrapper();

        jobDetail.setId(toConvert.getObjectId());
        jobDetail.setJobKey(new JobKey(toConvert.getString(JobDetailCodec.FIELD_NAME), toConvert
                .getString(JobDetailCodec.FIELD_GROUP)));

        final String serialized = toConvert.getString(JobDetailCodec.FIELD_SERIALIZED);
        final byte[] bytes = Base64.decode(serialized);
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try (final ObjectInputStream stream = new ObjectInputStream(bais)) {
            final JobDetail deserialized = (JobDetail) stream.readObject();
            if (deserialized != null) {
                jobDetail.setJobDetail(deserialized);
            }
        } catch (final IOException | ClassNotFoundException e) {
            LOGGER.error("Unable to deserialize calendar data", e);
        }

        return jobDetail;
    }

    @Override
    public ParseObject encode(final JobDetailWrapper toConvert) {
        final ParseObject parseObject = new ParseObject("job");

        if (StringUtils.isNotBlank(toConvert.getId())) {
            parseObject.setObjectId(toConvert.getId());
        }

        parseObject.put(JobDetailCodec.FIELD_NAME, toConvert.getJobKey().getName());
        parseObject.put(JobDetailCodec.FIELD_GROUP, toConvert.getJobKey().getGroup());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final ObjectOutputStream stream = new ObjectOutputStream(baos)) {
            stream.writeObject(toConvert.getJobDetail());
            stream.flush();
            parseObject.put(JobDetailCodec.FIELD_SERIALIZED, new String(Base64.encode(baos.toByteArray())));
        } catch (final IOException e) {
            LOGGER.error("Unable to serialize calendar data", e);
        }

        return parseObject;
    }
}
