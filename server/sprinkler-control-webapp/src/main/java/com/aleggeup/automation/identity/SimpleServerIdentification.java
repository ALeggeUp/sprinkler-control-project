/**
 * SimpleServerIdentification.java
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

package com.aleggeup.automation.identity;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author Stephen Legge
 */
public class SimpleServerIdentification implements ServerIdentification {

    private static final byte[] RASPBERRY_PI_MAC_PREFIX = new byte[] {(byte) 0xb8, 0x27, (byte) 0xeb};
    private static final int MAC_BYTES_LENGTH = 6;

    private static final String[] PREFERRED_PREFIXES = new String[] {"em", "en", "eth", "wlan", "wl"};
    private static final int NUM_PREFERRED = 3;
    private static final int OB_MASK = 0xa5;
    private static final int MASK = 0xff;
    private static final int CHECK_START = 0x31;
    private static final String HEX_FORMAT = "%02X";

    private final byte[] identifierBytes;

    public SimpleServerIdentification() throws UnidentifiableServerException {
        identifierBytes = preferredInterface();
    }

    /* (non-Javadoc)
     * @see com.aleggeup.automation.identity.ServerIdentification#isPi()
     */
    @Override
    public boolean isPi() {
        return isPiMac(identifierBytes);
    }

    /* (non-Javadoc)
     * @see com.aleggeup.automation.identity.ServerIdentification#identity()
     */
    @Override
    public String getIdentity() throws UnidentifiableServerException {
        final StringBuilder stringBuilder = new StringBuilder();
        int check = CHECK_START;
        for (final byte b : identifierBytes) {
            check ^= b & MASK;
            stringBuilder.append(String.format(HEX_FORMAT, (b ^ OB_MASK) & MASK));
        }
        stringBuilder.append(String.format(HEX_FORMAT, check));

        return stringBuilder.toString();
    }

    private byte[] preferredInterface() throws UnidentifiableServerException {
        byte[] macBytes = null;
        try {
            if (NetworkInterface.getNetworkInterfaces() == null) {
                throw new UnidentifiableServerException();
            }

            for (final String ePrefix : PREFERRED_PREFIXES) {
                for (int i = 0; i < NUM_PREFERRED; ++i) {
                    final String name = ePrefix + i;
                    final NetworkInterface networkInterface = NetworkInterface.getByName(name);
                    if (networkInterface != null) {
                        if (networkInterface.isVirtual() || !networkInterface.isUp()
                                || networkInterface.isPointToPoint()) {
                            continue;
                        }
                        final byte[] tempBytes = networkInterface.getHardwareAddress();
                        if (isPiMac(tempBytes)) {
                            macBytes = tempBytes;
                            break;
                        } else if (macBytes == null) {
                            macBytes = tempBytes;
                        }
                    }
                }
                if (macBytes != null && isPiMac(macBytes)) {
                    break;
                }
            }

        } catch (final SocketException e) {
            throw new UnidentifiableServerException(e);
        }

        if (macBytes == null) {
            macBytes = firstIdentifierInterface();
        }

        return macBytes;
    }

    private byte[] firstIdentifierInterface() throws UnidentifiableServerException {
        byte[] macBytes = null;

        try {
            if (NetworkInterface.getNetworkInterfaces() == null) {
                throw new UnidentifiableServerException();
            }
            for (final Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();) {
                final NetworkInterface networkInterface = e.nextElement();
                if (networkInterface.isVirtual() || !networkInterface.isUp() || networkInterface.isPointToPoint()) {
                    continue;
                }
                final byte[] tempBytes = networkInterface.getHardwareAddress();
                if (isPiMac(tempBytes)) {
                    macBytes = tempBytes;
                    break;
                } else if (macBytes == null) {
                    macBytes = tempBytes;
                }
            }
        } catch (final SocketException e) {
        } finally {
            if (macBytes == null) {
                throw new UnidentifiableServerException();
            }
        }

        return macBytes;
    }

    private boolean isPiMac(final byte[] macBytes) {
        return macBytes != null && macBytes.length == MAC_BYTES_LENGTH
                && (macBytes[0] == RASPBERRY_PI_MAC_PREFIX[0])
                && (macBytes[1] == RASPBERRY_PI_MAC_PREFIX[1])
                && (macBytes[2] == RASPBERRY_PI_MAC_PREFIX[2]);
    }
}
