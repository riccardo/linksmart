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
 * Copyright (C) 2006-2010 University of Reading,
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
package eu.linksmart.caf.daqc.util;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Helper factory for performing some functionality relating to types.<p>
 * 
 * @author Michael Crouch
 * 
 */
public final class TypeFactory {

	/**
	 * Private Constructor
	 */
	private TypeFactory(){};
	
	/**
	 * Returns the value given (as String), in the type specified
	 * 
	 * @param value
	 *            the value (as String) to be converted
	 * @param type
	 *            the type to convert the value to
	 * @return the converted Object
	 */
	public static Object getObjectAsType(String value, String type) {
		String coreType;
		// get core type
		if (type.contains("#")) {
			String[] typeSpl = type.split("#");
			coreType = typeSpl[1];
		} else {
			coreType = type;
		}

		if (coreType.equalsIgnoreCase("string"))
			return value;
		if (coreType.equalsIgnoreCase("boolean"))
			return new Boolean(value);
		if (coreType.equalsIgnoreCase("int"))
			return new Integer(value);
		if (coreType.equalsIgnoreCase("double"))
			return new Double(value);
		if (coreType.equalsIgnoreCase("integer"))
			return new BigInteger(value);
		if (coreType.equalsIgnoreCase("byte"))
			return new Byte(value);
		if (coreType.equalsIgnoreCase("long"))
			return new Long(value);
		if (coreType.equalsIgnoreCase("decimal"))
			return new BigDecimal(value);
		if (coreType.equalsIgnoreCase("float"))
			return new Float(value);
		if (coreType.equalsIgnoreCase("hexBinary"))
			return value.getBytes();
		if (coreType.equalsIgnoreCase("base64Binary"))
			return value.getBytes();
		if (coreType.equalsIgnoreCase("short"))
			return new Short(value);

		return value;
	}

	/**
	 * Returns the String represented value of the given object, as string
	 * 
	 * @param type
	 *            the type to convert from
	 * @param value
	 *            the value to convert
	 * @return the value as String
	 */
	public static String getValueAsString(String type, Object value) {

		String coreType;
		// get core type
		if (type.contains("#")) {
			String[] typeSpl = type.split("#");
			coreType = typeSpl[1];
		} else {
			coreType = type;
		}

		try {
			if (coreType.equalsIgnoreCase("string"))
				return (String) value;
			if (coreType.equalsIgnoreCase("boolean"))
				return Boolean.toString((Boolean) value);
			if (coreType.equalsIgnoreCase("int"))
				return Integer.toString((Integer) value);
			if (coreType.equalsIgnoreCase("double"))
				return Double.toString((Double) value);
			if (coreType.equalsIgnoreCase("integer"))
				return Integer.toString((Integer) value);
			if (coreType.equalsIgnoreCase("long"))
				return Long.toString((Long) value);
			if (coreType.equalsIgnoreCase("float"))
				return Float.toString((Float) value);
			if (coreType.equalsIgnoreCase("short"))
				return Short.toString((Short) value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
