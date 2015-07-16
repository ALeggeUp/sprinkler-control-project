/**
 * JobDetailWrapper.java
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

package com.aleggeup.automation.schedule.quartz.model;

import org.quartz.JobDetail;
import org.quartz.JobKey;

import com.aleggeup.automation.persist.Persistable;

/**
 * @author Stephen Legge
 */
public class JobDetailWrapper implements Persistable {

    private static final long serialVersionUID = -873885339021611367L;

    private String id;

    private JobKey jobKey;

    private JobDetail jobDetail;

    public JobDetailWrapper() {
    }

    public JobDetailWrapper(final JobDetail jobDetail) {
        setJobKey(jobDetail.getKey());
        setJobDetail(jobDetail);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(final String id) {
        this.id = id;
    }

    public JobKey getJobKey() {
        return jobKey;
    }

    public final void setJobKey(final JobKey jobKey) {
        this.jobKey = jobKey;
    }

    public JobDetail getJobDetail() {
        return jobDetail;
    }

    public final void setJobDetail(final JobDetail jobDetail) {
        this.jobDetail = jobDetail;
    }
}
