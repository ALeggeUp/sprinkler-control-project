/**
 * Zone.java
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

package com.aleggeup.automation.sprinkler.model;

import org.joda.time.DateTime;

import com.aleggeup.automation.sprinkler.persister.Persistable;

/**
 * @author Stephen Legge
 */
public class Zone implements Persistable {

    private static final long serialVersionUID = 8092295422407370984L;

    public enum ZoneState {
        RUNNING,
        NOT_RUNNING,
        DISABLED
    }

    private String id;

    private int number;

    private String description;

    private ZoneState state;

    private int durationInSeconds;

    private DateTime lastStart;

    private DateTime lastEnd;

    private String gpioOutput;

    public Zone() {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(final String id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public ZoneState getState() {
        return state;
    }

    public void setState(final ZoneState state) {
        this.state = state;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(final int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public DateTime getLastStart() {
        return lastStart;
    }

    public void setLastStart(final DateTime lastStart) {
        this.lastStart = lastStart;
    }

    public DateTime getLastEnd() {
        return lastEnd;
    }

    public void setLastEnd(final DateTime lastEnd) {
        this.lastEnd = lastEnd;
    }

    public String getGpioOutput() {
        return gpioOutput;
    }

    public void setGpioOutput(final String gpioOutput) {
        this.gpioOutput = gpioOutput;
    }
}
