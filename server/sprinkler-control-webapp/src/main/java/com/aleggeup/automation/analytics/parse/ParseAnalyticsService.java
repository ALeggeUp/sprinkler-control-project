/**
 * ParseAnalyticsService.java
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

package com.aleggeup.automation.analytics.parse;

import org.parse4j.Parse;
import org.parse4j.ParseAnalytics;

import com.aleggeup.automation.analytics.AnalyticsService;

/**
 * @author Stephen Legge
 */
public class ParseAnalyticsService implements AnalyticsService {

    private static final String APPLICATION_ID = "FkBNNyNpJ1Jg5XXjlByTsek57lEOY8t3NFfA8YKx";
    private static final String REST_API_KEY = "ldRqg12eb11ztpQQNiIqVB6K6FNkZB4Hv1K55Ns7";

    private static final String EVENT_APP_CLOSED = "AppClosed";

    public ParseAnalyticsService() {
        Parse.initialize(APPLICATION_ID, REST_API_KEY);
    }

    /* (non-Javadoc)
     * @see com.aleggeup.automation.analytics.AnalyticsService#start()
     */
    @Override
    public void start() {
        ParseAnalytics.trackAppOpened();
    }

    /* (non-Javadoc)
     * @see com.aleggeup.automation.analytics.AnalyticsService#end()
     */
    @Override
    public void end() {
        ParseAnalytics.trackEvent(EVENT_APP_CLOSED);
    }
}
