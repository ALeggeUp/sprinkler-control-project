/**
 * TestHelperUtil.java
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

package com.aleggeup.automation.sprinkler.util;

/**
 * @author Stephen Legge
 */
public final class TestHelperUtil {

    private static final String KEY_SERVER_ADDRESS = "server-address";
    private static final String KEY_SERVER_PORT = "tomcatIntegrationPort";
    private static final String DEFAULT_SERVER_ADDRESS = "localhost";
    private static final String DEFAULT_SERVER_PORT = "8080";

    private TestHelperUtil() {
    }

    public static String getServerServicesUrl() {
        final String address = System.getProperty(KEY_SERVER_ADDRESS, DEFAULT_SERVER_ADDRESS);
        final String port = System.getProperty(KEY_SERVER_PORT, DEFAULT_SERVER_PORT);

        return String.format("http://%s:%s/services/", address, port);
    }
}
