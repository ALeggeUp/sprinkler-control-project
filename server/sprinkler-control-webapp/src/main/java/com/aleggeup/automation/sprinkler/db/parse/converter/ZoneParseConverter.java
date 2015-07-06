/**
 * ZoneParseConverter.java
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

package com.aleggeup.automation.sprinkler.db.parse.converter;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.parse4j.ParseObject;

import com.aleggeup.automation.sprinkler.db.mongo.codec.ZoneCodec;
import com.aleggeup.automation.sprinkler.model.Zone;
import com.aleggeup.automation.sprinkler.model.Zone.ZoneState;

/**
 * @author Stephen Legge
 *
 */
public class ZoneParseConverter extends AbstractParseConverter<Zone, ParseObject> {

    @Override
    public Zone decode(final ParseObject toConvert) {
        final Zone zone = new Zone();

        zone.setId(toConvert.getObjectId());
        zone.setNumber(toConvert.getInt(ZoneCodec.FIELD_NUMBER));
        zone.setState(ZoneState.valueOf(toConvert.getString(ZoneCodec.FIELD_STATE)));
        zone.setDescription(toConvert.getString(ZoneCodec.FIELD_DESCRIPTION));
        zone.setDurationInSeconds(toConvert.getInt(ZoneCodec.FIELD_DURATION_IN_SECONDS));
        zone.setGpioOutput(toConvert.getString(ZoneCodec.FIELD_GPIO_OUTPUT));
        zone.setLastStart(new DateTime(toConvert.getLong(ZoneCodec.FIELD_LAST_START)));
        zone.setLastEnd(new DateTime(toConvert.getLong(ZoneCodec.FIELD_LAST_END)));

        return zone;
    }

    @Override
    public ParseObject encode(final Zone toConvert) {
        final ParseObject parseObject = new ParseObject("zone");

        if (StringUtils.isNotBlank(toConvert.getId())) {
            parseObject.setObjectId(toConvert.getId());
        }

        parseObject.put(ZoneCodec.FIELD_NUMBER, toConvert.getNumber());
        parseObject.put(ZoneCodec.FIELD_STATE, toConvert.getState().toString());
        parseObject.put(ZoneCodec.FIELD_DESCRIPTION, toConvert.getDescription());
        parseObject.put(ZoneCodec.FIELD_DURATION_IN_SECONDS, toConvert.getDurationInSeconds());
        parseObject.put(ZoneCodec.FIELD_GPIO_OUTPUT, toConvert.getGpioOutput());
        parseObject.put(ZoneCodec.FIELD_LAST_START, toConvert.getLastStart().getMillis());
        parseObject.put(ZoneCodec.FIELD_LAST_END, toConvert.getLastEnd().getMillis());

        return parseObject;
    }
}
