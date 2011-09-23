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
package eu.linksmart.caf.cm.impl.util;

/**
 * Class providing helper input for whether a defined data type is numeric or
 * not
 * 
 * @author Michael Crouch
 * 
 */
public final class NumericHelper {

	/**
	 * Singleton Constructor
	 */
	private NumericHelper() {

	}

	/**
	 * Evaluates whether the data type passed is numeric.
	 * 
	 * @param dataType
	 *            the data type
	 * @return whether it is numeric
	 */
	public static boolean isNumericType(String dataType) {

		String[] numericTypes =
				{ "int", "integer", "double", "long", "float", "short" };
		String[] nunmericUpnpTypes =
				{ "i1", "i2", "i4", "r4", "r8", "ui1", "ui2", "ui4" };

		for (String str : numericTypes) {
			if (dataType.endsWith(str))
				return true;
		}

		for (String str : nunmericUpnpTypes) {
			if (dataType.endsWith(str))
				return true;
		}

		return false;
	}
	
	/**
	 * Evaluates whether the value given can be represented numerically. <p>
	 * Attempts to parse the String as a Double.
	 * @param value the value to test
	 * @return the result
	 */
	public static boolean isNumericValue(String value){
		try{
			double converted = Double.parseDouble(value);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

}
