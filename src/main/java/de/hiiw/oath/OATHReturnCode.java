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

import jnr.ffi.util.EnumMapper;

/**
 * Mapping of the oath return codes to an enum
 * Positive values can be mapped to OATH_OK and another intValue
 */
public enum OATHReturnCode implements EnumMapper.IntegerEnum {
    OATH_OK(0),
    OATH_CRYPTO_ERROR(-1),
    OATH_INVALID_DIGITS(-2),
    OATH_PRINTF_ERROR(-3),
    OATH_INVALID_HEX ( -4),
    OATH_TOO_SMALL_BUFFER ( -5),
    OATH_INVALID_OTP ( -6),
    OATH_REPLAYED_OTP ( -7),
    OATH_BAD_PASSWORD ( -8),
    OATH_INVALID_COUNTER ( -9),
    OATH_INVALID_TIMESTAMP ( -10),
    OATH_NO_SUCH_FILE ( -11),
    OATH_UNKNOWN_USER ( -12),
    OATH_FILE_SEEK_ERROR ( -13),
    OATH_FILE_CREATE_ERROR ( -14),
    OATH_FILE_LOCK_ERROR ( -15),
    OATH_FILE_RENAME_ERROR ( -16),
    OATH_FILE_UNLINK_ERROR ( -17),
    OATH_TIME_ERROR ( -18),
    OATH_STRCMP_ERROR ( -19),
    OATH_INVALID_BASE32 ( -20),
    OATH_BASE32_OVERFLOW ( -21),
    OATH_MALLOC_ERROR ( -22),
    OATH_FILE_FLUSH_ERROR ( -23),
    OATH_FILE_SYNC_ERROR ( -24),
    OATH_FILE_CLOSE_ERROR ( -25);

    /**
     * Holds the intValue of this error code
     */
    int intValue;

    /**
     * Constructs a new error code with a specific int value
     * @param intValue the intValue
     */
    OATHReturnCode(int intValue){
        this.intValue = intValue;
    }

    /**
     * Returns the intValue
     * @return the intValue
     */
    @Override
    public int intValue() {
        return intValue;
    }

    /**
     * Sets the intValue, only used for positive codes, with an eror code of OATH_OK
     * @param intValue new intValue
     */
    protected void setIntValue(int intValue) {
        if(this != OATH_OK){
            throw new RuntimeException("Int Value of an error code cannot be changed");
        }

        this.intValue = intValue;
    }

    /**
     * @return the error as string
     */
    public String getError(){
        return intValue > 0 ? "" : OATH.strError(this);
    }

    /**
     * @return the error name
     */
    public String getErrorName(){
        return OATH.strErrorName(intValue > 0 ? OATH_OK : this);
    }

    @Override
    public String toString() {
        return getErrorName() + " (" + intValue() + ")" + (intValue <= 0 ? " " + getError() : "");
    }
}
