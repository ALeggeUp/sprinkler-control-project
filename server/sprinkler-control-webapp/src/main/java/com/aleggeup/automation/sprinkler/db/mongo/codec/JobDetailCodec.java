/**
 * JobDetailCodec.java
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

package com.aleggeup.automation.sprinkler.db.mongo.codec;

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
import org.quartz.JobDetail;
import org.quartz.JobKey;

import com.aleggeup.automation.sprinkler.schedule.quartz.model.JobDetailWrapper;
import com.sun.jersey.core.util.Base64;

/**
 * @author Stephen Legge
 */
public class JobDetailCodec implements Codec<JobDetailWrapper> {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_GROUP = "group";
    public static final String FIELD_SERIALIZED = "serialized";
    private static final String FIELD_ID = "_id";

    @Override
    public void encode(final BsonWriter writer, final JobDetailWrapper value, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        if (StringUtils.isNotBlank(value.getId())) {
            writer.writeObjectId(FIELD_ID, new ObjectId(value.getId()));
        } else {
            writer.writeObjectId(FIELD_ID, new ObjectId());
        }
        writer.writeString(FIELD_NAME, value.getJobKey().getName());
        writer.writeString(FIELD_GROUP, value.getJobKey().getGroup());
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            final ObjectOutputStream stream = new ObjectOutputStream(baos);
            stream.writeObject(value.getJobDetail());
            stream.flush();
            stream.close();
            writer.writeString(FIELD_SERIALIZED, new String(Base64.encode(baos.toByteArray())));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        writer.writeEndDocument();
        writer.flush();
    }

    @Override
    public Class<JobDetailWrapper> getEncoderClass() {
        return JobDetailWrapper.class;
    }

    @Override
    public JobDetailWrapper decode(final BsonReader reader, final DecoderContext decoderContext) {
        final JobDetailWrapper wrapper = new JobDetailWrapper();

        reader.readStartDocument();
        final String id = reader.readObjectId(FIELD_ID).toString();
        wrapper.setId(id);
        final JobKey jobKey = new JobKey(reader.readString(FIELD_NAME), reader.readString(FIELD_GROUP));
        wrapper.setJobKey(jobKey);
        final String serialized = reader.readString(FIELD_SERIALIZED);
        final byte[] bytes = Base64.decode(serialized);
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            final ObjectInputStream stream = new ObjectInputStream(bais);
            final JobDetail jobDetail = (JobDetail) stream.readObject();
            stream.close();
            wrapper.setJobDetail(jobDetail);
        } catch (final IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        reader.readEndDocument();

        return wrapper;
    }

}
