/**
 * CorsFilter.java
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

package com.aleggeup.automation.sprinkler.resource.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Singleton;

/**
 * @author Stephen Legge
 */
@Singleton
public class CorsFilter implements Filter {

    private static final String HEADER_AC_PREFIX = "Access-Control-";
    private static final String HEADER_ALLOW_ORIGIN = HEADER_AC_PREFIX + "Allow-Origin";
    private static final String HEADER_ALLOW_METHODS = HEADER_AC_PREFIX + "Allow-Methods";
    private static final String HEADER_ALLOW_HEADERS = HEADER_AC_PREFIX + "Allow-Headers";
    private static final String HEADER_MAX_AGE = HEADER_AC_PREFIX + "Max-Age";

    private static final List<String> METHOD_LIST = Arrays.asList("POST", "GET", "OPTIONS", "PUT", "DELETE", "HEAD");
    private static final List<String> HEADER_LIST = Arrays.asList("X-PINGOTHER", "Origin", "X-Requested-With",
            "Content-Type", "Accept");
    private static final int ACCESS_CONTROL_MAX_AGE = 20 * 24 * 60 * 60;

    private static final String LIST_SEPARATOR = ", ";
    private static final String WILDCARD = "*";

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse,
     *      javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.addHeader(HEADER_ALLOW_ORIGIN, WILDCARD);
            httpServletResponse.addHeader(HEADER_ALLOW_METHODS, StringUtils.join(METHOD_LIST, LIST_SEPARATOR));
            httpServletResponse.addHeader(HEADER_ALLOW_HEADERS, StringUtils.join(HEADER_LIST, LIST_SEPARATOR));
            httpServletResponse.addHeader(HEADER_MAX_AGE, String.valueOf(ACCESS_CONTROL_MAX_AGE));
        }
        chain.doFilter(request, response);
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
    }
}
