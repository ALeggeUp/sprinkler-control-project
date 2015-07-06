/**
 * CalendarWrapper.java
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

package com.aleggeup.automation.sprinkler.schedule.quartz.model;

import org.quartz.Calendar;

import com.aleggeup.automation.sprinkler.persister.Persistable;

/**
 * @author Stephen Legge
 */
public class CalendarWrapper implements Persistable {

    private static final long serialVersionUID = 8906182312411257643L;

    private String id;

    private String calendarName;

    private Calendar calendar;

    public CalendarWrapper() {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(final String id) {
        this.id = id;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(final String calendarName) {
        this.calendarName = calendarName;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(final Calendar calendar) {
        this.calendar = calendar;
    }
}
