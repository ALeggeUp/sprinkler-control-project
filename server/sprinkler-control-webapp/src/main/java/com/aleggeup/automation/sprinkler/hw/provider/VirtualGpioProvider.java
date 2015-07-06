/**
 * VirtualGpioProvider.java
 *
 * Copyright 2014-2015 [A Legge Up Consulting]
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

package com.aleggeup.automation.sprinkler.hw.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.GpioProviderBase;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import com.pi4j.wiringpi.GpioInterruptEvent;
import com.pi4j.wiringpi.GpioInterruptListener;

/**
 * @author Stephen Legge
 */
public class VirtualGpioProvider extends GpioProviderBase implements GpioProvider, GpioInterruptListener {

    public static final String NAME = "Virtual GPIO Provider";

    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualGpioProvider.class);

    public VirtualGpioProvider() {
    }

    /* (non-Javadoc)
     * @see com.pi4j.io.gpio.GpioProviderBase#getName()
     */
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean hasPin(final Pin pin) {
        return true;
    }

    /* (non-Javadoc)
     * @see com.pi4j.wiringpi.GpioInterruptListener#pinStateChange(com.pi4j.wiringpi.GpioInterruptEvent)
     */
    @Override
    public void pinStateChange(final GpioInterruptEvent event) {
        LOGGER.info("pinStateChange " + event.getPin());
        // TODO Auto-generated method stub
    }

    @Override
    public void export(final Pin pin, final PinMode mode) {
        LOGGER.info("export({}, {})", pin.getName(), mode);
        super.export(pin, mode);
    }

    @Override
    public void setState(final Pin pin, final PinState state) {
        LOGGER.info("setState({}, {})", pin.getName(), state);
        super.setState(pin, state);
    }

}
