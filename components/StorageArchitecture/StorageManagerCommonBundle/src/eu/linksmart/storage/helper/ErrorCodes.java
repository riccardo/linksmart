/*
 * In case of German law being applicable to this license agreement, the following warranty and liability terms shall apply:
 *
 * 1. Licensor shall be liable for any damages caused by wilful intent or malicious concealment of defects.
 * 2. Licensor's liability for gross negligence is limited to foreseeable, contractually typical damages.
 * 3. Licensor shall not be liable for damages caused by slight negligence, except in cases 
 *    of violation of essential contractual obligations (cardinal obligations). Licensee's claims for 
 *    such damages shall be statute barred within 12 months subsequent to the delivery of the software.
 * 4. As the Software is licensed on a royalty free basis, any liability of the Licensor for indirect damages 
 *    and consequential damages - except in cases of intent - is excluded.
 *
 * This limitation of liability shall also apply if this license agreement shall be subject to law 
 * stipulating liability clauses corresponding to German law.
 */
/**
 * Copyright (C) 2006-2010 [University of Paderborn]
 *                         the HYDRA consortium, EU project IST-2005-034891
 *
 * This file is part of LinkSmart.
 *
 * LinkSmart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * version 3 as published by the Free Software Foundation.
 *
 * LinkSmart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with LinkSmart.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.linksmart.storage.helper;

public class ErrorCodes {
	
	public static final int EC_NO_ERROR = 0;
	
	// User Errors
	public static final int EC_FILE_NOT_FOUND = 1;
	public static final int EC_PATH_EXISTS = 2;
	public static final int EC_UNKNOWN_ENCODING = 3;
	public static final int EC_IS_NO_DIRECTORY = 4;
	public static final int EC_IS_NO_FILE = 5;
	public static final int EC_REQUIRED_ARG_MISSING = 6;
	public static final int EC_DIR_NOT_EMPTY = 7;
	public static final int EC_ARG_ERROR = 8;
	public static final int EC_USER_RIGHTS = 9;
	public static final int EC_FILESIZE_EXCEEDED = 10;
	public static final int EC_KEY_NOT_FOUND = 11;
	public static final int EC_LOCK_NOT_SET = 12;
	public static final int EC_LOCK_ALLREADY_SET_BY_SENDER = 13;
	
	// System Error not broken
	public static final int EC_NO_CREATION = 1025;
	public static final int EC_SYSTEM_RIGHTS = 1026;
	public static final int EC_NOT_AVAILABLE = 1027;
	
	// System error broken
	public static final int EC_IO_EXEPTION = 2049;
	public static final int EC_JDOM_EXCEPTION = 2050;
	public static final int EC_NO_PROPERTIES = 2051;
	public static final int EC_MD_IO_EXCEPTION = 2052;
	public static final int EC_MD_NO_CREATION = 2053;
	public static final int EC_NO_REMOVE = 2054;
	public static final int EC_INCOMPLETE_WRITE_ACCESS = 2055;
	
	public static boolean isError(int errorCode) {
		return errorCode != EC_NO_ERROR;
	}
	
	public static boolean isUserError(int errorCode) {
		return isError(errorCode) && errorCode < 1024;
	}
	
	public static boolean isSystemError(int errorCode) {
		return isError(errorCode) && errorCode >= 1024;
	}
	
	public static boolean isBroken(int errorCode) {
		return isError(errorCode) && errorCode >= 2048;
	}
}
