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

import jnr.ffi.LibraryLoader;
import jnr.ffi.byref.IntByReference;
import jnr.ffi.byref.PointerByReference;
import jnr.ffi.util.BufferUtil;
import jnr.ffi.util.EnumMapper;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Abstraction Layer (or Wrapper) for the JNI-Abstraction Layer (jnr-jffi),
 * handles initialization and dispose of the native liboath
 */
public class OATH {
    /**
     * Holds the ingleton of this class
     */
    private static OATH instance;

    /**
     * The jni-abstraction interface
     */
    private static LibOATH libOATH;

    static {
        libOATH = LibraryLoader.create(LibOATH.class).load("oath");
    }

    /**
     * Initializes native lib and sets a shutdown hook for deinitialization,
     * also checks if the native installed lib is newer or the actual supported version
     * @throws OATHException if an exception occurred, see documentation of liboath (oath_init)
     * @throws UnsatisfiedLinkError if the lib is too old
     */
    private OATH(){
        if(libOATH.oath_check_version(LibOATH.OATH_VERSION) == null){
            throw new UnsatisfiedLinkError("OATH version too old, required version: "
                    + LibOATH.OATH_VERSION + ", found version " + libOATH.oath_check_version(null));
        }

        handleError(libOATH.oath_init());
        java.lang.Runtime.getRuntime().addShutdownHook(new Thread(() -> handleError(libOATH.oath_done())));
    }

    /**
     * Gets the oath instance, initialize it if not done before
     * @throws OATHException if an exception occurred, see documentation of liboath (oath_init)
     * @throws UnsatisfiedLinkError if the lib is too old
     * @return the oath wrapper
     */
    public static synchronized OATH getInstance(){
        if(instance == null){
            instance = new OATH();
        }

        return instance;
    }

    /**
     * Returns the underlying jnr-jffi interface
     * @return the jnr-jffi interface
     */
    public LibOATH getLibOATH(){
        return libOATH;
    }

    /**
     * Chcecks the version of the lib
     * @param requiredVersion minimum required version (can be null)
     * @return libversion of requiredVersion is greater or equal libversion or requiredVersion is null, else null
     */
    public static String checkVersion(String requiredVersion){
        return libOATH.oath_check_version(requiredVersion);
    }

    /**
     * Returns a string representation of the OATHReturnCode
     * @param errorCode The return code from liboath
     * @return string representation of OATHReturnCode
     */
    public static String strError(OATHReturnCode errorCode){
        return libOATH.oath_strerror(errorCode);
    }

    /**
     * Returns the name of the OATHReturnCode
     * @param errorCode The return code from liboath
     * @return name of the OATHReturnCode
     */
    public static String strErrorName(OATHReturnCode errorCode){
        return libOATH.oath_strerror_name(errorCode);
    }

    /**
     * Encodes the given byte array input to a base32 representation
     * @param input input byte array
     * @return Base32 encoded input
     * @throws OATHException if an exception occurred, see documentation of liboath (oath_base32_encode)
     */
    public String base32Encode(byte[] input){
        PointerByReference resultReference = new PointerByReference();
        IntByReference resultSizeReference = new IntByReference();

        handleError(libOATH.oath_base32_encode(input, input.length, resultReference, resultSizeReference));

        return resultReference.getValue().getString(0, resultSizeReference.getValue(), Charset.defaultCharset());
    }

    /**
     * Decodes a given base32 encoded string into a byte array
     * @param input base32 encoded string
     * @return result byte array
     * @throws OATHException if an exception occurred, see documentation of liboath (oath_base32_decode)
     */
    public byte[] base32Decode(String input){
        PointerByReference resultReference = new PointerByReference();
        IntByReference resultSizeReference = new IntByReference();
        byte[] stringBytes = input.getBytes();

        handleError(libOATH.oath_base32_decode(stringBytes, stringBytes.length, resultReference, resultSizeReference));

        byte[] buffer = new byte[resultSizeReference.getValue()];
        resultReference.getValue().get(0, buffer, 0, buffer.length);
        return buffer;
    }

    /**
     * Decodes a given hex encoded string into a byte array
     * @param hex hex encoded string
     * @return result byte array
     * @throws OATHException if an exception occurred, see documentation of liboath (oath_hex2bin)
     */
    public byte[] hex2Bin(String hex){
        IntByReference binLen = new IntByReference();
        byte[] hexBytes = hex.getBytes();

        handleError(libOATH.oath_hex2bin(hexBytes, new byte[0], binLen));

        byte[] buffer = new byte[binLen.getValue()];
        handleError(libOATH.oath_hex2bin(hexBytes, buffer, binLen));

        return buffer;
    }

    /**
     * Encodes the given byte array input to a hex representation
     * @param binary input byte array
     * @return hex encoded input
     * @throws OATHException if an exception occurred, see documentation of liboath (oath_bin2hex)
     */
    public String bin2Hex(byte[] binary){
        byte[] input = new byte[2 * binary.length + 1];
        libOATH.oath_bin2hex(binary, binary.length, input);
        return BufferUtil.getString(ByteBuffer.wrap(input), Charset.defaultCharset());
    }

    /**
     * Generates a time based one time token based on HMAC_SHA1
     * @param secret The secret
     * @param unixTimestamp The timestamp (usually now)
     * @param timeStepSize Size of timestep in seconds (usually 30)
     * @param startOffset Offset to start from
     * @param digits Digits of the otp (6-8 digits are now supported)
     * @return The otp as string
     * @throws OATHException if an exception occurred, see documentation of liboath (oath_totp_generate2)
     */
    public String totpGenerate(byte[] secret, long unixTimestamp, int timeStepSize, long startOffset, int digits){
        return totpGenerate(secret, unixTimestamp, timeStepSize, startOffset, digits, OATHTotpFlag.OATH_TOTP_HMAC_SHA1);
    }

    /**
     * Generates a time based one time token based on a hash algorithm specified by flag
     * @param secret The secret
     * @param unixTimestamp The timestamp (usually now)
     * @param timeStepSize Size of timestep in seconds (usually 30)
     * @param startOffset Offset to start from
     * @param digits Digits of the otp (6-8 digits are now supported)
     * @param flag Hash algorithm
     * @return The otp as string
     * @throws OATHException if an exception occurred, see documentation of liboath (oath_totp_generate2)
     */
    public String totpGenerate(byte[] secret, long unixTimestamp, int timeStepSize, long startOffset, int digits, OATHTotpFlag flag){
        byte[] buffer = new byte[digits + 1];
        handleError(libOATH.oath_totp_generate2(secret, secret.length, unixTimestamp,
                timeStepSize, startOffset, digits, flag, buffer));
        return BufferUtil.getString(ByteBuffer.wrap(buffer), Charset.defaultCharset());
    }

    /**
     * Validates an one time token against a secret
     * @param secret The secret
     * @param unixTimestamp The timestamp (usually now)
     * @param timeStepSize Size of timestep in seconds (usually 30)
     * @param startOffset Offset to start from
     * @param window The window (token offset) of accepted tokens
     * @param flag Hash algorithm
     * @param otp The otp to validate
     * @return a validate result, containing the (absolute and relative) position of the otp and a ok return code
     * @throws OATHException if the otp is invalid or an exception occurred, see documentation of liboath (oath_totp_validate4)
     */
    public OATHTotpValidateResult totpValidate(byte[] secret, long unixTimestamp, int timeStepSize, long startOffset, int window, OATHTotpFlag flag, String otp){
        OATHReturnCode code = OATHReturnCode.OATH_OK;
        IntByReference otpPos = new IntByReference();
        IntByReference otpCounter = new IntByReference();

        code.setIntValue(handleError(libOATH.oath_totp_validate4(secret, secret.length, unixTimestamp,
                timeStepSize, startOffset, window, otpPos, otpCounter, flag, otp)));

        return new OATHTotpValidateResult(code, otpPos.getValue(), otpCounter.getValue());
    }

    /**
     * Generates a HMAC-based one time token
     * @param secret The secret
     * @param movingFactor a counter indicating the current OTP to generate
     * @param digits Digits of the otp (6-8 digits are now supported)
     * @param truncationOffset use a specific truncation offset
     * @return The one time token
     * @throws OATHException if an exception occurred, see documentation of liboath (oath_hotp_generate)
     */
    public String hotpGenerate(byte[] secret, long movingFactor, int digits, long truncationOffset){
        byte[] buffer = new byte[digits + 1];
        handleError(libOATH.oath_hotp_generate(secret, secret.length, movingFactor, digits, false, truncationOffset, buffer));
        return BufferUtil.getString(ByteBuffer.wrap(buffer), Charset.defaultCharset());
    }

    /**
     * Validates an one time token against a secret
     * @param secret The secret
     * @param start_moving_factor start counter in OTP stream
     * @param window how many OTPs after start counter to test
     * @param otp The otp to validate
     * @return Returns position in OTP window (zero is first position)
     * @throws OATHException if the otp is invalid or an exception occurred, see documentation of liboath (oath_hotp_validate)
     */
    public OATHReturnCode hotpValidate(byte[] secret, long start_moving_factor, long window, String otp){
        int res = handleError(libOATH.oath_hotp_validate(secret, secret.length, start_moving_factor, window, otp));
        OATHReturnCode result = OATHReturnCode.OATH_OK;
        result.setIntValue(res);
        return result;
    }

    /**
     * Helper function to construct and throw an exception
     * @param oathReturnCode The return code
     * @return oathReturnCode
     * @throws OATHException if oathReturnCode < 0
     */
    private int handleError(int oathReturnCode){
        return oathReturnCode > 0 ? oathReturnCode :
                handleError((OATHReturnCode) EnumMapper.getInstance(OATHReturnCode.class).valueOf(oathReturnCode)).intValue();
    }

    /**
     * Helper function to construct and throw an exception
     * @param oathReturnCode The return code
     * @return oathReturnCode
     * @throws OATHException if oathReturnCode < 0
     */
    private OATHReturnCode handleError(OATHReturnCode oathReturnCode){
        if(oathReturnCode != OATHReturnCode.OATH_OK){
            throw new OATHException(oathReturnCode);
        }

        return oathReturnCode;
    }
}
