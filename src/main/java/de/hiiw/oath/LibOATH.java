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

import jnr.ffi.Runtime;
import jnr.ffi.TypeAlias;
import jnr.ffi.annotations.In;
import jnr.ffi.annotations.Out;
import jnr.ffi.byref.IntByReference;
import jnr.ffi.byref.PointerByReference;
import jnr.ffi.types.size_t;
import jnr.ffi.types.time_t;
import jnr.ffi.types.u_int32_t;
import jnr.ffi.types.u_int64_t;

/**
 * JNI abstractions, for documentation look at the official liboath docs.
 * https://www.nongnu.org/oath-toolkit/liboath-api/liboath-oath.html
 */
public interface LibOATH {
    //Header defines
    String OATH_VERSION = "2.6.2";
    long OATH_VERSION_NUMBER = 0x02060200;
    long OATH_HOTP_DYNAMIC_TRUNCATION = Runtime.getSystemRuntime().findType(TypeAlias.size_t).size();
    long OATH_TOTP_DEFAULT_TIME_STEP_SIZE = 30;
    long OATH_TOTP_DEFAULT_START_TIME = 0;

    //Init and deinit
    OATHReturnCode oath_init();
    OATHReturnCode oath_done();

    //Error handling
    String oath_strerror(@In OATHReturnCode err);
    String oath_strerror_name(@In OATHReturnCode err);

    //Version handling
    String oath_check_version(@In String req_version);

    //HOTP Length
    default int OATH_HOTP_LENGTH(int digits, boolean checksum) {
        return digits + (checksum ? 1 : 0);
    }

    //Generate HOTP
    OATHReturnCode oath_hotp_generate(@In byte[] secret,
                                       @In @size_t int secret_length,
                                       @In @u_int64_t long moving_factor,
                                       @In @u_int32_t int digits,
                                       @In boolean add_checksum,
                                       @In @size_t long truncation_offset,
                                       @Out byte[] output_otp);

    //Validate HOTP
    int oath_hotp_validate(@In byte[] secret,
                            @In @size_t long secret_length,
                            @In @u_int64_t long start_moving_factor,
                            @In @size_t long window,
                            @In String otp);

    //Generate time based otp tokens
    OATHReturnCode oath_totp_generate(@In byte[] secret,
                                      @In @size_t long secret_length,
                                      @In @time_t long now,
                                      @In @u_int32_t int time_step_size,
                                      @In @time_t long start_offset,
                                      @In @u_int32_t int digits,
                                      @Out byte[] output_otp);

    OATHReturnCode oath_totp_generate2(@In byte[] secret,
                                       @In @size_t long secret_length,
                                       @In @time_t long now,
                                       @In @u_int32_t int time_step_size,
                                       @In @time_t long start_offset,
                                       @In @u_int32_t int digits,
                                       @In OATHTotpFlag flags,
                                       @Out byte[] output_otp);

    //Validate time based otp tokens
    int oath_totp_validate(@In byte[] secret,
                           @In @size_t long secret_length,
                           @In @time_t long now,
                           @In @u_int32_t int time_step_size,
                           @In @time_t long start_offset,
                           @In @size_t long window,
                           @In String otp);

    int oath_totp_validate2(@In byte[] secret,
                            @In @size_t long secret_length,
                            @In @time_t long now,
                            @In @u_int32_t int time_step_size,
                            @In @time_t long start_offset,
                            @In @size_t long window,
                            @Out IntByReference otp_pos,
                            @In String otp);

    int oath_totp_validate3(@In byte[] secret,
                            @In @size_t long secret_length,
                            @In @time_t long now,
                            @In @u_int32_t int time_step_size,
                            @In @time_t long start_offset,
                            @In @size_t long window,
                            @Out IntByReference otp_pos,
                            @Out IntByReference otp_counter,
                            @In String otp);

    int oath_totp_validate4(@In byte[] secret,
                            @In @size_t long secret_length,
                            @In @time_t long now,
                            @In @u_int32_t int time_step_size,
                            @In @time_t long start_offset,
                            @In @size_t long window,
                            @Out IntByReference otp_pos,
                            @Out IntByReference otp_counter,
                            @In OATHTotpFlag flag,
                            @In String otp);

    //Base32 Encode/Decode
    OATHReturnCode oath_base32_encode(@In byte[] in, @In @size_t long inlen, @Out PointerByReference out, @Out IntByReference outlen);
    OATHReturnCode oath_base32_decode(@In byte[] in, @In @size_t long inlen, @Out PointerByReference out, @Out IntByReference outlen);

    //Hex Encode/Decode
    OATHReturnCode oath_hex2bin(@In byte[] hexstr, @Out byte[] binstr, @Out IntByReference binlen);
    void oath_bin2hex(@In byte[] binstr, @In @size_t long binlen, @Out byte[] hexstr);
}