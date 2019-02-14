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
 * Exceptionclass for mapping negative return codes from oath to an exception
 */
public class OATHException extends RuntimeException {
    /**
     * The return code hold by this exception
     */
    private OATHReturnCode errorCode;

    /**
     * Constructs a new OATHException with a specific error code
     * @param errorCode the error code
     */
    public OATHException(OATHReturnCode errorCode) {
        super(errorCode.toString());
        this.errorCode = errorCode;
    }

    /**
     * Returns the return code hold by this exception
     * @return return code
     */
    public OATHReturnCode getErrorCode() {
        return errorCode;
    }
}
