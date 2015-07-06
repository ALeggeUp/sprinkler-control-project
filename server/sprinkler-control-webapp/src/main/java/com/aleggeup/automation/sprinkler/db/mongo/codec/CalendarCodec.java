/**
 * CalendarCodec.java
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
import org.quartz.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleggeup.automation.sprinkler.schedule.quartz.model.CalendarWrapper;
import com.sun.jersey.core.util.Base64;

/**
 * @author Stephen Legge
 */
public class CalendarCodec implements Codec<CalendarWrapper> {

    public static final String FIELD_CALENDAR_NAME = "calendarName";
    public static final String FIELD_SERIALIZED = "serialized";
    private static final String FIELD_ID = "_id";

    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarCodec.class);

    @Override
    public void encode(final BsonWriter writer, final CalendarWrapper value, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        if (StringUtils.isNotBlank(value.getId())) {
            writer.writeObjectId(FIELD_ID, new ObjectId(value.getId()));
        } else {
            writer.writeObjectId(FIELD_ID, new ObjectId());
        }
        writer.writeString(FIELD_CALENDAR_NAME, value.getCalendarName());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final ObjectOutputStream stream = new ObjectOutputStream(baos)) {
            stream.writeObject(value.getCalendar());
            stream.flush();
            writer.writeString(FIELD_SERIALIZED, new String(Base64.encode(baos.toByteArray())));
        } catch (final IOException e) {
            LOGGER.error("Unable to serialize calendar data", e);
        }
        writer.writeEndDocument();
    }

    @Override
    public Class<CalendarWrapper> getEncoderClass() {
        return CalendarWrapper.class;
    }

    @Override
    public CalendarWrapper decode(final BsonReader reader, final DecoderContext decoderContext) {
        final CalendarWrapper wrapper = new CalendarWrapper();
        reader.readStartDocument();
        final String id = reader.readObjectId(FIELD_ID).toString();
        wrapper.setId(id);
        wrapper.setCalendarName(reader.readString(FIELD_CALENDAR_NAME));

        final String serialized = reader.readString(FIELD_SERIALIZED);
        final byte[] bytes = Base64.decode(serialized);
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try (final ObjectInputStream stream = new ObjectInputStream(bais)) {
            final Calendar calendar = (Calendar) stream.readObject();
            if (calendar != null) {
                wrapper.setCalendar(calendar);
            }
        } catch (final IOException | ClassNotFoundException e) {
            LOGGER.error("Unable to deserialize calendar data", e);
        }
        reader.readEndDocument();

        return wrapper;
    }
}
