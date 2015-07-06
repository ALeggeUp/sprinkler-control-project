/**
 * ZoneCodec.java
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

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.aleggeup.automation.sprinkler.model.Zone;
import com.aleggeup.automation.sprinkler.model.Zone.ZoneState;

/**
 * @author Stephen Legge
 */
public class ZoneCodec implements Codec<Zone> {

    public static final String FIELD_ID = "_id";
    public static final String FIELD_NUMBER = "number";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_STATE = "state";
    public static final String FIELD_DURATION_IN_SECONDS = "durationInSeconds";
    public static final String FIELD_LAST_START = "lastStart";
    public static final String FIELD_LAST_END = "lastEnd";
    public static final String FIELD_GPIO_OUTPUT = "gpioOutput";

    public ZoneCodec() {
    }

    @Override
    public void encode(final BsonWriter writer, final Zone value, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        if (StringUtils.isNotBlank(value.getId())) {
            writer.writeObjectId(FIELD_ID, new ObjectId(value.getId()));
        } else {
            writer.writeObjectId(FIELD_ID, new ObjectId());
        }
        writer.writeInt32(FIELD_NUMBER, value.getNumber());
        writer.writeString(FIELD_DESCRIPTION, value.getDescription());
        writer.writeString(FIELD_STATE, value.getState().toString());
        writer.writeInt32(FIELD_DURATION_IN_SECONDS, value.getDurationInSeconds());
        writer.writeInt64(FIELD_LAST_START, value.getLastStart().getMillis());
        writer.writeInt64(FIELD_LAST_END, value.getLastEnd().getMillis());
        writer.writeString(FIELD_GPIO_OUTPUT, value.getGpioOutput());
        writer.writeEndDocument();
        writer.flush();
    }

    @Override
    public Class<Zone> getEncoderClass() {
        return Zone.class;
    }

    @Override
    public Zone decode(final BsonReader reader, final DecoderContext decoderContext) {
        final Zone decoded = new Zone();

        reader.readStartDocument();
        decoded.setId(reader.readObjectId(FIELD_ID).toHexString());
        decoded.setNumber(reader.readInt32(FIELD_NUMBER));
        decoded.setDescription(reader.readString(FIELD_DESCRIPTION));
        decoded.setState(ZoneState.valueOf(reader.readString(FIELD_STATE)));
        decoded.setDurationInSeconds(reader.readInt32(FIELD_DURATION_IN_SECONDS));
        decoded.setLastStart(new DateTime(reader.readInt64(FIELD_LAST_START)));
        decoded.setLastEnd(new DateTime(reader.readInt64(FIELD_LAST_END)));
        decoded.setGpioOutput(reader.readString(FIELD_GPIO_OUTPUT));
        reader.readEndDocument();

        return decoded;
    }
}
