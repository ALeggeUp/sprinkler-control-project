/**
 * ZoneResourceIntegrationTest.java
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

package com.aleggeup.automation.sprinkler.resource;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aleggeup.automation.sprinkler.resource.config.WebResourceParameters;
import com.aleggeup.automation.sprinkler.util.TestHelperUtil;

public class ZoneResourceIntegrationTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testZonesEndpoint() throws ClientProtocolException, IOException {
        final CloseableHttpClient httpClient = HttpClients.createDefault();
        final HttpUriRequest request = new HttpGet(TestHelperUtil.getServerServicesUrl()
                + WebResourceParameters.SERVICE_NAME_ZONES);
        final HttpResponse httpResponse = httpClient.execute(request);

        Assert.assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
        Assert.assertNotNull(httpResponse.getEntity());
        Assert.assertNotNull(httpResponse.getEntity().getContentType());
        Assert.assertEquals(MediaType.APPLICATION_JSON, httpResponse.getEntity().getContentType().getValue());
    }
}