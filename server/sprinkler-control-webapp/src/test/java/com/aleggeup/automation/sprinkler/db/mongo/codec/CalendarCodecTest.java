/**
 * CalendarCodecTest.java
 *
 * Copyright 2014 [A Legge Up Consulting]
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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.aleggeup.automation.sprinkler.schedule.quartz.model.CalendarWrapper;

/**
 * @author Stephen Legge
 */
public class CalendarCodecTest {

    private static final String FIELD_ID = "_id";
    private static final String FIELD_CALENDAR_NAME = "calendarName";
    private static final String FIELD_SERIALIZED = "serialized";
    private static final String NAME_OF_TEST_CALENDAR = "nameOfCalendar";
    private static final String ID_OF_TEST_CALENDAR = "228fc7352faa53582d7a8e91";
    private static final ObjectId OBJECT_ID_OF_TEST_CALENDAR = new ObjectId(ID_OF_TEST_CALENDAR);
    private static final String SERIALIZED_CALENDAR =
            "rO0ABXNyACdvcmcucXVhcnR6LmltcGwuY2FsZW5kYXIuV2Vla2x5Q2FsZW5kYXKhgILMnyitHgIAAloACmV"
            + "4Y2x1ZGVBbGxbAAtleGNsdWRlRGF5c3QAAltaeHIAJW9yZy5xdWFydHouaW1wbC5jYWxlbmRhci5CYXNl"
            + "Q2FsZW5kYXIrHPGG498Y7wIAA0wADGJhc2VDYWxlbmRhcnQAFUxvcmcvcXVhcnR6L0NhbGVuZGFyO0wAC"
            + "2Rlc2NyaXB0aW9udAASTGphdmEvbGFuZy9TdHJpbmc7TAAIdGltZVpvbmV0ABRMamF2YS91dGlsL1RpbW"
            + "Vab25lO3hwcHBwAHVyAAJbWlePIDkUuF3iAgAAeHAAAAAIAAEAAAAAAAE=";

    @Mock
    private BsonWriter mockBsonWriter;

    @Mock
    private BsonReader mockBsonReader;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void verifyMocks() {
        Mockito.verifyNoMoreInteractions(mockBsonWriter, mockBsonReader);
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link com.aleggeup.automation.sprinkler.db.mongo.codec.CalendarCodec#encode(org.bson.BsonWriter,
     * com.aleggeup.automation.sprinkler.schedule.quartz.model.CalendarWrapper, org.bson.codecs.EncoderContext)}.
     */
    @Test
    public final void testEncodeWithNoId() {
        final CalendarCodec codec = new CalendarCodec();
        final CalendarWrapper wrapper = createTestCalendarWrapperWithNoId();

        codec.encode(mockBsonWriter, wrapper, null);

        final InOrder order = Mockito.inOrder(mockBsonWriter);

        order.verify(mockBsonWriter).writeStartDocument();
        order.verify(mockBsonWriter).writeObjectId(anyString(), any(ObjectId.class));
        order.verify(mockBsonWriter).writeString(FIELD_CALENDAR_NAME, NAME_OF_TEST_CALENDAR);
        order.verify(mockBsonWriter).writeString(anyString(), anyString());
        order.verify(mockBsonWriter).writeEndDocument();
    }

    /**
     * Test method for
     * {@link com.aleggeup.automation.sprinkler.db.mongo.codec.CalendarCodec#encode(org.bson.BsonWriter,
     * com.aleggeup.automation.sprinkler.schedule.quartz.model.CalendarWrapper, org.bson.codecs.EncoderContext)}.
     */
    @Test
    public final void testEncodeWithId() {
        final CalendarCodec codec = new CalendarCodec();
        final CalendarWrapper wrapper = createTestCalendarWrapperWithId();

        codec.encode(mockBsonWriter, wrapper, null);

        final InOrder order = Mockito.inOrder(mockBsonWriter);

        order.verify(mockBsonWriter).writeStartDocument();
        order.verify(mockBsonWriter).writeObjectId(FIELD_ID, OBJECT_ID_OF_TEST_CALENDAR);
        order.verify(mockBsonWriter).writeString(FIELD_CALENDAR_NAME, NAME_OF_TEST_CALENDAR);
        order.verify(mockBsonWriter).writeString(anyString(), anyString());
        order.verify(mockBsonWriter).writeEndDocument();
    }

    /**
     * Test method for {@link com.aleggeup.automation.sprinkler.db.mongo.codec.CalendarCodec#getEncoderClass()}.
     */
    @Test
    public final void testGetEncoderClass() {
        final CalendarCodec codec = new CalendarCodec();
        assertTrue(codec.getEncoderClass().equals(CalendarWrapper.class));
    }

    /**
     * Test method for {@link com.aleggeup.automation.sprinkler.db.mongo.codec.CalendarCodec#decode(
     * org.bson.BsonReader, org.bson.codecs.DecoderContext)}.
     */
    @Test
    public final void testDecode() {
        final CalendarCodec codec = new CalendarCodec();

        when(mockBsonReader.readObjectId(FIELD_ID)).thenReturn(OBJECT_ID_OF_TEST_CALENDAR);
        when(mockBsonReader.readString(FIELD_CALENDAR_NAME)).thenReturn(NAME_OF_TEST_CALENDAR);
        when(mockBsonReader.readString(FIELD_SERIALIZED)).thenReturn(SERIALIZED_CALENDAR);

        codec.decode(mockBsonReader, null);

        final InOrder order = Mockito.inOrder(mockBsonReader);

        order.verify(mockBsonReader).readStartDocument();
        order.verify(mockBsonReader).readObjectId(FIELD_ID);
        order.verify(mockBsonReader).readString(FIELD_CALENDAR_NAME);
        order.verify(mockBsonReader).readString(anyString());
        order.verify(mockBsonReader).readEndDocument();
    }

    private CalendarWrapper createTestCalendarWrapperWithId() {
        final CalendarWrapper wrapper = new CalendarWrapper();
        wrapper.setId(ID_OF_TEST_CALENDAR);
        wrapper.setCalendarName(NAME_OF_TEST_CALENDAR);

        return wrapper;
    }

    private CalendarWrapper createTestCalendarWrapperWithNoId() {
        final CalendarWrapper wrapper = new CalendarWrapper();
        wrapper.setCalendarName(NAME_OF_TEST_CALENDAR);

        return wrapper;
    }
}
