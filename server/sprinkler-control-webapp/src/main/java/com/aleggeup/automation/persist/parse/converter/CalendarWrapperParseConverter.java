/**
 * CalendarWrapperParseConverter.java
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
import org.quartz.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleggeup.automation.persist.mongo.codec.CalendarCodec;
import com.aleggeup.automation.schedule.quartz.model.CalendarWrapper;
import com.sun.jersey.core.util.Base64;

/**
 * @author Stephen Legge
 *
 */
public class CalendarWrapperParseConverter extends AbstractParseConverter<CalendarWrapper, ParseObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarWrapperParseConverter.class);

    @Override
    public CalendarWrapper decode(final ParseObject toConvert) {
        final CalendarWrapper calendar = new CalendarWrapper();

        calendar.setId(toConvert.getObjectId());
        calendar.setCalendarName(toConvert.getString(CalendarCodec.FIELD_CALENDAR_NAME));

        final String serialized = toConvert.getString(CalendarCodec.FIELD_SERIALIZED);
        final byte[] bytes = Base64.decode(serialized);
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try (final ObjectInputStream stream = new ObjectInputStream(bais)) {
            final Calendar decodedCalendar = (Calendar) stream.readObject();
            if (decodedCalendar != null) {
                calendar.setCalendar(decodedCalendar);
            }
        } catch (final IOException | ClassNotFoundException e) {
            LOGGER.error("Unable to deserialize calendar data", e);
        }

        return calendar;
    }

    @Override
    public ParseObject encode(final CalendarWrapper toConvert) {
        final ParseObject parseObject = new ParseObject("calendar");

        if (StringUtils.isNotBlank(toConvert.getId())) {
            parseObject.setObjectId(toConvert.getId());
        }

        parseObject.put(CalendarCodec.FIELD_CALENDAR_NAME, toConvert.getCalendarName());
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final ObjectOutputStream stream = new ObjectOutputStream(baos)) {
            stream.writeObject(toConvert.getCalendar());
            stream.flush();
            parseObject.put(CalendarCodec.FIELD_SERIALIZED, new String(Base64.encode(baos.toByteArray())));
        } catch (final IOException e) {
            LOGGER.error("Unable to serialize calendar data", e);
        }

        return parseObject;
    }
}
