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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Testing generic stuff like constants
 */
public class GenericTest {
    private static OATH oath;

    @BeforeAll
    static void setup(){
        oath = OATH.getInstance();
    }

    @Test
    void testLibInterfaceNotNull(){
        Assertions.assertNotNull(oath.getLibOATH(), "OATHLib instance cannot be null");
    }

    @Test
    void testVersionChecker(){
        Assertions.assertNotNull(OATH.checkVersion(null), "Version should not be null");
    }

    @Test
    void testFinalInterfaceVariables(){
        Assertions.assertEquals("2.6.2", LibOATH.OATH_VERSION);
        Assertions.assertEquals(Runtime.getSystemRuntime().findType(TypeAlias.size_t).size(),
                LibOATH.OATH_HOTP_DYNAMIC_TRUNCATION);
        Assertions.assertEquals(0, LibOATH.OATH_TOTP_DEFAULT_START_TIME);
        Assertions.assertEquals(30, LibOATH.OATH_TOTP_DEFAULT_TIME_STEP_SIZE);
        Assertions.assertEquals(0x02060200, LibOATH.OATH_VERSION_NUMBER);
    }

    @Test
    void testHOTPSizeMakro(){
        Assertions.assertEquals(0, oath.getLibOATH().OATH_HOTP_LENGTH(0, false));
        Assertions.assertEquals(6, oath.getLibOATH().OATH_HOTP_LENGTH(6, false));

        Assertions.assertEquals(1, oath.getLibOATH().OATH_HOTP_LENGTH(0, true));
        Assertions.assertEquals(7, oath.getLibOATH().OATH_HOTP_LENGTH(6, true));
    }
}
