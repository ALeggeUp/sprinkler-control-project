/**
 * OperableTriggerWrapperParseConverter.java
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

package com.aleggeup.automation.sprinkler.db.parse.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.parse4j.ParseObject;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.quartz.spi.OperableTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleggeup.automation.persist.mongo.codec.TriggerCodec;
import com.aleggeup.automation.schedule.quartz.model.OperableTriggerWrapper;
import com.aleggeup.automation.schedule.quartz.model.OperableTriggerWrapper.OperableTriggerState;
import com.sun.jersey.core.util.Base64;

/**
 * @author Stephen Legge
 *
 */
public class OperableTriggerWrapperParseConverter extends AbstractParseConverter<OperableTriggerWrapper, ParseObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperableTriggerWrapperParseConverter.class);

    @Override
    public OperableTriggerWrapper decode(final ParseObject toConvert) {
        final OperableTriggerWrapper trigger = new OperableTriggerWrapper();

        trigger.setId(toConvert.getObjectId());
        trigger.setTriggerKey(new TriggerKey(toConvert.getString(TriggerCodec.FIELD_TRIGGER_NAME), toConvert
                .getString(TriggerCodec.FIELD_TRIGGER_GROUP)));
        trigger.setJobKey(new JobKey(toConvert.getString(TriggerCodec.FIELD_JOB_NAME), toConvert
                .getString(TriggerCodec.FIELD_JOB_GROUP)));
        trigger.setOperableTriggerState(OperableTriggerState.valueOf(toConvert
                .getString(TriggerCodec.FIELD_TRIGGER_STATE)));

        final String serialized = toConvert.getString(TriggerCodec.FIELD_SERIALIZED);
        final byte[] bytes = Base64.decode(serialized);
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try (final ObjectInputStream stream = new ObjectInputStream(bais)) {
            final OperableTrigger deserialized = (OperableTrigger) stream.readObject();
            if (deserialized != null) {
                trigger.setOperableTrigger(deserialized);
            }
        } catch (final IOException | ClassNotFoundException e) {
            LOGGER.error("Unable to deserialize calendar data", e);
        }

        return trigger;
    }

    @Override
    public ParseObject encode(final OperableTriggerWrapper toConvert) {
        final ParseObject parseObject = new ParseObject("trigger");

        if (StringUtils.isNotBlank(toConvert.getId())) {
            parseObject.setObjectId(toConvert.getId());
        }

        parseObject.put(TriggerCodec.FIELD_TRIGGER_NAME, toConvert.getTriggerKey().getName());
        parseObject.put(TriggerCodec.FIELD_TRIGGER_GROUP, toConvert.getTriggerKey().getGroup());
        parseObject.put(TriggerCodec.FIELD_TRIGGER_STATE, toConvert.getOperableTriggerState().toString());
        parseObject.put(TriggerCodec.FIELD_JOB_NAME, toConvert.getJobKey().getName());
        parseObject.put(TriggerCodec.FIELD_JOB_GROUP, toConvert.getJobKey().getGroup());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final ObjectOutputStream stream = new ObjectOutputStream(baos)) {
            stream.writeObject(toConvert.getOperableTrigger());
            stream.flush();
            parseObject.put(TriggerCodec.FIELD_SERIALIZED, new String(Base64.encode(baos.toByteArray())));
        } catch (final IOException e) {
            LOGGER.error("Unable to serialize calendar data", e);
        }

        return parseObject;
    }
}
