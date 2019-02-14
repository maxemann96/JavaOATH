/*
 * JavaOATH - A Java JNI Binding for LibOATH
 * Copyright (C) 2019  Maximilian Hippler
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package de.hiiw.oath;

/**
 * A validate result, containing the (absolute and relative) position of the otp and a ok return code
 */
public class OATHTotpValidateResult {
    /**
     * The returncode
     */
    private OATHReturnCode returnCode;

    /**
     * The relative position of the otp
     */
    private int otpPos;

    /**
     * The absolute position of the otp
     */
    private int otpCounter;

    /**
     * Constructs a new OATHTotpValidateResult
     * @param returnCode The returncode
     * @param otpPos The relative position of the otp
     * @param otpCounter The absolute position of the otp
     */
    public OATHTotpValidateResult(OATHReturnCode returnCode, int otpPos, int otpCounter) {
        this.returnCode = returnCode;
        this.otpPos = otpPos;
        this.otpCounter = otpCounter;
    }

    /**
     * @return The returncode
     */
    public OATHReturnCode getReturnCode() {
        return returnCode;
    }

    /**
     * @return The relative position of the otp
     */
    public int getOtpPos() {
        return otpPos;
    }

    /**
     * @return The absolute position of the otp
     */
    public int getOtpCounter() {
        return otpCounter;
    }

    @Override
    public String toString() {
        return "OATHTotpValidateResult{" +
                "returnCode=" + returnCode +
                ", otpPos=" + otpPos +
                ", otpCounter=" + otpCounter +
                '}';
    }
}
