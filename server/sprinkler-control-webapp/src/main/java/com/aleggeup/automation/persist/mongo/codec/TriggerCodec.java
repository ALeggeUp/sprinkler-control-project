/**
 * TriggerCodec.java
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

package com.aleggeup.automation.persist.mongo.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.quartz.spi.OperableTrigger;

import com.aleggeup.automation.schedule.quartz.model.OperableTriggerWrapper;
import com.aleggeup.automation.schedule.quartz.model.OperableTriggerWrapper.OperableTriggerState;
import com.sun.jersey.core.util.Base64;

/**
 * @author Stephen Legge
 */
public class TriggerCodec implements Codec<OperableTriggerWrapper> {

    public static final String FIELD_TRIGGER_NAME = "triggerName";
    public static final String FIELD_TRIGGER_GROUP = "triggerGroup";
    public static final String FIELD_TRIGGER_STATE = "triggerState";
    public static final String FIELD_JOB_NAME = "jobName";
    public static final String FIELD_JOB_GROUP = "jobGroup";
    public static final String FIELD_SERIALIZED = "serialized";
    private static final String FIELD_ID = "_id";

    @Override
    public void encode(final BsonWriter writer, final OperableTriggerWrapper value,
            final EncoderContext encoderContext) {
        writer.writeStartDocument();
        if (StringUtils.isNotBlank(value.getId())) {
            writer.writeObjectId(FIELD_ID, new ObjectId(value.getId()));
        } else {
            writer.writeObjectId(FIELD_ID, new ObjectId());
        }
        writer.writeString(FIELD_TRIGGER_NAME, value.getTriggerKey().getName());
        writer.writeString(FIELD_TRIGGER_GROUP, value.getTriggerKey().getGroup());
        writer.writeString(FIELD_TRIGGER_STATE, value.getOperableTriggerState().name());
        writer.writeString(FIELD_JOB_NAME, value.getJobKey().getName());
        writer.writeString(FIELD_JOB_GROUP, value.getJobKey().getGroup());
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            final ObjectOutputStream stream = new ObjectOutputStream(baos);
            stream.writeObject(value.getOperableTrigger());
            stream.flush();
            stream.close();
            writer.writeString(FIELD_SERIALIZED, new String(Base64.encode(baos.toByteArray())));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        writer.writeEndDocument();
    }

    @Override
    public Class<OperableTriggerWrapper> getEncoderClass() {
        return OperableTriggerWrapper.class;
    }

    @Override
    public OperableTriggerWrapper decode(final BsonReader reader, final DecoderContext decoderContext) {
        final OperableTriggerWrapper wrapper = new OperableTriggerWrapper();
        reader.readStartDocument();
        final String id = reader.readObjectId(FIELD_ID).toString();
        wrapper.setId(id);
        final String triggerName = reader.readString(FIELD_TRIGGER_NAME);
        final String triggerGroup = reader.readString(FIELD_TRIGGER_GROUP);
        wrapper.setTriggerKey(new TriggerKey(triggerName, triggerGroup));
        final String state = reader.readString(FIELD_TRIGGER_STATE);
        wrapper.setOperableTriggerState(OperableTriggerState.valueOf(state));
        final String jobName = reader.readString(FIELD_JOB_NAME);
        final String jobGroup = reader.readString(FIELD_JOB_GROUP);
        wrapper.setJobKey(new JobKey(jobName, jobGroup));
        final String serialized = reader.readString(FIELD_SERIALIZED);
        final byte[] bytes = Base64.decode(serialized);
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            final ObjectInputStream stream = new ObjectInputStream(bais);
            final OperableTrigger trigger = (OperableTrigger) stream.readObject();
            stream.close();
            wrapper.setOperableTrigger(trigger);
        } catch (final IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        reader.readEndDocument();

        return wrapper;
    }
}
