/**
 * FixedParseUser.java
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

package org.parse4j;

import org.json.JSONException;
import org.json.JSONObject;
import org.parse4j.callback.LoginCallback;
import org.parse4j.callback.RequestPasswordResetCallback;
import org.parse4j.callback.SignUpCallback;
import org.parse4j.command.ParseCommand;
import org.parse4j.command.ParseDeleteCommand;
import org.parse4j.command.ParseGetCommand;
import org.parse4j.command.ParsePostCommand;
import org.parse4j.command.ParseResponse;
import org.parse4j.util.ParseRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephen Legge
 */
@ParseClassName("users")
public class FixedParseUser extends ParseObject {

    private static FixedParseUser currentUser;

    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    private static final Logger LOGGER = LoggerFactory.getLogger(FixedParseUser.class);

    private String password;
    private String sessionToken;

    public FixedParseUser() {
        super(ParseRegistry.getClassName(FixedParseUser.class));
        setEndPoint("users");
    }

    public static FixedParseUser getCurrentUser() {
        return currentUser;
    }

    private static void setCurrentUser(final FixedParseUser user) {
        currentUser = user;
    }

    @Override
    public void remove(final String key) {
        if (USERNAME.equals(key)) {
            LOGGER.error("Can't remove the username key. (1)");
            throw new IllegalArgumentException("Can't remove the username key.");
        }

        super.remove(key);
    }

    public void setSessionToken(final String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public void setUsername(final String username) {
        put(USERNAME, username);
    }

    public String getUsername() {
        return getString(USERNAME);
    }

    public void setPassword(final String password) {
        this.password = password;
        isDirty = true;
    }

    public void setEmail(final String email) {
        put(EMAIL, email);
    }

    public String getEmail() {
        return getString(EMAIL);
    }

    public String getSessionToken() {
        return sessionToken;

    }

    public static FixedParseUser logIn(final String username, final String password) throws ParseException {
        final FixedParseUser pu = new FixedParseUser();
        pu.setUsername(username);
        pu.setPassword(password);
        return pu;
    }

    public boolean isAuthenticated() {
        return (sessionToken != null) && (getObjectId() != null);
    }

    @Override
    void validateSave() {

        if (getObjectId() == null) {
            LOGGER.error("Cannot save a ParseUser until it has been signed up. Call signUp first. (1)");
            throw new IllegalArgumentException(
                    "Cannot save a ParseUser until it has been signed up. Call signUp first.");
        }

        if (isDirty && (!isAuthenticated()) && (getObjectId() != null)) {
            LOGGER.error("Cannot save a ParseUser that is not authenticated. (1)");
            throw new IllegalArgumentException(
                    "Cannot save a ParseUser that is not authenticated.");
        }

    }

    public void signUp() throws ParseException {

        if ((getUsername() == null) || (getUsername().length() == 0)) {
            LOGGER.error("Username cannot be missing or blank (1)");
            throw new IllegalArgumentException(
                    "Username cannot be missing or blank");
        }

        if (password == null) {
            LOGGER.error("Password cannot be missing or blank (2)");
            throw new IllegalArgumentException(
                    "Password cannot be missing or blank");
        }

        if (getObjectId() != null) {
            LOGGER.error("Cannot sign up a user that has already signed up. (1)");
            throw new IllegalArgumentException(
                    "Cannot sign up a user that has already signed up.");
        }

        final ParsePostCommand command = new ParsePostCommand(getClassName());
        final JSONObject parseData = getParseData();
        parseData.put(PASSWORD, password);
        command.setData(parseData);
        final ParseResponse response = command.perform();
        if (!response.isFailed()) {
            final JSONObject jsonResponse = response.getJsonObject();
            if (jsonResponse == null) {
                LOGGER.error("Empty response");
                throw response.getException();
            }
            try {
                setObjectId(jsonResponse.getString(ParseConstants.FIELD_OBJECT_ID));
                sessionToken = jsonResponse.getString(ParseConstants.FIELD_SESSION_TOKEN);
                final String createdAt = jsonResponse.getString(ParseConstants.FIELD_CREATED_AT);
                setCreatedAt(Parse.parseDate(createdAt));
                setUpdatedAt(Parse.parseDate(createdAt));
            } catch (final JSONException e) {
                LOGGER.error("Although Parse reports object successfully saved, the response was invalid. (1)");
                throw new ParseException(
                        ParseException.INVALID_JSON,
                        "Although Parse reports object successfully saved, the response was invalid. (2)",
                        e);
            }
        } else {
            LOGGER.error("Request failed. (1)");
            throw response.getException();
        }

    }

    public static FixedParseUser login(final String username, final String password) throws ParseException {

        setCurrentUser(null);
        final ParseGetCommand command = new ParseGetCommand("login");
        command.addJson(false);
        command.put(USERNAME, username);
        command.put(PASSWORD, password);
        final ParseResponse response = command.perform();
        if (!response.isFailed()) {
            final JSONObject jsonResponse = response.getJsonObject();
            if (jsonResponse == null) {
                LOGGER.error("Empty response. (1)");
                throw response.getException();
            }
            try {
                final FixedParseUser parseUser = new FixedParseUser();
                parseUser.setObjectId(jsonResponse.getString(ParseConstants.FIELD_OBJECT_ID));
                parseUser.setSessionToken(jsonResponse.getString(ParseConstants.FIELD_SESSION_TOKEN));
                setCurrentUser(parseUser);
                final String createdAt = jsonResponse.getString(ParseConstants.FIELD_CREATED_AT);
                final String updatedAt = jsonResponse.getString(ParseConstants.FIELD_UPDATED_AT);
                parseUser.setCreatedAt(Parse.parseDate(createdAt));
                parseUser.setUpdatedAt(Parse.parseDate(updatedAt));
                jsonResponse.remove(ParseConstants.FIELD_OBJECT_ID);
                jsonResponse.remove(ParseConstants.FIELD_CREATED_AT);
                jsonResponse.remove(ParseConstants.FIELD_UPDATED_AT);
                jsonResponse.remove(ParseConstants.FIELD_SESSION_TOKEN);
                parseUser.setData(jsonResponse, true);
                return parseUser;

            } catch (final JSONException e) {
                LOGGER.error("Although Parse reports object successfully saved, the response was invalid. (3)");
                throw new ParseException(
                        ParseException.INVALID_JSON,
                        "Although Parse reports object successfully saved, the response was invalid. (4)",
                        e);
            }
        } else {
            LOGGER.error("Request failed. (2)");
            throw response.getException();
        }

    }

    public static void requestPasswordReset(final String email) throws ParseException {

        final ParsePostCommand command = new ParsePostCommand("requestPasswordReset");
        final JSONObject data = new JSONObject();
        data.put(EMAIL, email);
        command.setData(data);
        final ParseResponse response = command.perform();
        if (!response.isFailed()) {
            final JSONObject jsonResponse = response.getJsonObject();
            if (jsonResponse == null) {
                LOGGER.error("Empty response.");
                throw response.getException();
            }
        } else {
            LOGGER.error("Request failed.");
            throw response.getException();
        }

    }

    @Override
    public void delete() throws ParseException {
        if (getObjectId() == null) {
            return;
        }

        final ParseCommand command = new ParseDeleteCommand(getEndPoint(), getObjectId());
        command.put(ParseConstants.FIELD_SESSION_TOKEN, getSessionToken());

        final ParseResponse response = command.perform();
        if (response.isFailed()) {
            throw response.getException();
        }

        setUpdatedAt(null);
        setCreatedAt(null);
        setObjectId(null);
        isDirty = false;
    }

    public void logout() throws ParseException {

        if (!isAuthenticated()) {
            return;
        }

    }

    public static void requestPasswordResetInBackground(final String email,
            final RequestPasswordResetCallback callback) {

    }

    public void signUpInBackground(final SignUpCallback callback) {

    }

    public static void loginInBackground(final String username, final String password,
            final LoginCallback callback) {

    }

}
