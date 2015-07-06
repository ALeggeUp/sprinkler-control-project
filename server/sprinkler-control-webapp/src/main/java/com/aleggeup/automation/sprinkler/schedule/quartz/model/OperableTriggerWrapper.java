/**
 * OperableTriggerWrapper.java
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

import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.quartz.spi.OperableTrigger;

import com.aleggeup.automation.sprinkler.persister.Persistable;

/**
 * @author Stephen Legge
 */
public class OperableTriggerWrapper implements Persistable {

    private static final long serialVersionUID = -774726843747502452L;

    public enum OperableTriggerState {
        WAITING,
        ACQUIRED,
        EXECUTING,
        COMPLETE,
        PAUSED,
        BLOCKED,
        PAUSED_BLOCKED,
        ERROR
    };

    private String id;

    private TriggerKey triggerKey;

    private OperableTriggerState operableTriggerState;

    private JobKey jobKey;

    private OperableTrigger operableTrigger;

    public OperableTriggerWrapper() {
    }

    public TriggerKey getTriggerKey() {
        return triggerKey;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(final String id) {
        this.id = id;
    }

    public void setTriggerKey(final TriggerKey triggerKey) {
        this.triggerKey = triggerKey;
    }

    public OperableTriggerState getOperableTriggerState() {
        return operableTriggerState;
    }

    public void setOperableTriggerState(final OperableTriggerState operableTriggerState) {
        this.operableTriggerState = operableTriggerState;
    }

    public JobKey getJobKey() {
        return jobKey;
    }

    public void setJobKey(final JobKey jobKey) {
        this.jobKey = jobKey;
    }

    public OperableTrigger getOperableTrigger() {
        return operableTrigger;
    }

    public void setOperableTrigger(final OperableTrigger operableTrigger) {
        this.operableTrigger = operableTrigger;
    }
}
