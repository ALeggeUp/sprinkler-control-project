/**
 * GpioControllerProvider.java
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

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

/**
 * @author Stephen Legge
 */
public class GpioControllerProvider implements Provider<GpioController> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GpioControllerProvider.class);

    private static final byte[] RASPBERRY_PI_MAC_PREFIX = new byte[] {(byte) 0xb8, 0x27, (byte) 0xeb};

    /* (non-Javadoc)
     * @see com.google.inject.Provider#get()
     */
    @Override
    public GpioController get() {

        if (!hasPiNetworkInterface()) {
            LOGGER.info("Does not appear to be running on a Raspberry Pi -- Using virtual GPIO");
            GpioFactory.setDefaultProvider(new VirtualGpioProvider());
            GpioFactory.getExecutorServiceFactory();
        }

        return GpioFactory.getInstance();
    }

    public boolean hasPiNetworkInterface() {
        boolean result = false;

        try {
            for (final Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();) {
                final NetworkInterface networkInterface = e.nextElement();
                final byte[] macBytes = networkInterface.getHardwareAddress();
                if (macBytes != null
                        && (macBytes[0] == RASPBERRY_PI_MAC_PREFIX[0])
                        && (macBytes[1] == RASPBERRY_PI_MAC_PREFIX[1])
                        && (macBytes[2] == RASPBERRY_PI_MAC_PREFIX[2])) {
                    result = true;
                }
            }
        } catch (final SocketException e) {
            result = false;
        }

        return result;
    }

}
