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

/**
 * This class is designed to create special purpose Regular Expressions, which are sometimes used together with the Cookie Device.
 * 
 * @author fermat
 *
 */
public class RegExFactory {
	
	/**
	 * Regular Expression detecting integer numbers (of any length, positive and negative).
	 * 
	 * @return Regular Expression detecting integer numbers (of any length, positive and negative).
	 */
	public static String digit() {
		return "^[-+]?[0-9]+$";
	}
	
	/**
	 * Regular Expression detecting integer numbers (of any length, positive only).
	 * 
	 * @return Regular Expression detecting integer numbers (of any length, positive only).
	 */
	public static String posDigit() {
		return "^[+]?[0-9]+$";
	}
	
	/**
	 * Regular Expression detecting integer numbers (of any length, negative only).
	 * 
	 * @return Regular Expression detecting integer numbers (of any length, negative only).
	 */
	public static String negDigit() {
		return "^-[0-9]+$";
	}
	
	public static String digitPosRange(long lower, long upper) {
		if ((lower < 0) || (upper < 0))
			throw new NumberFormatException(
					"negative numbers are not supported");
		if (lower > upper)
			throw new NumberFormatException(
					"lower has to be lower or equal to upper");
		if (lower == upper)
			return "" + lower;
		StringBuffer sb = new StringBuffer("");
		String lowerS = "" + lower;
		String upperS = "" + upper;
		if (lowerS.length() == upperS.length()) {
			int equalLength = 0;
			while (lowerS.charAt(equalLength) == upperS.charAt(equalLength))
				equalLength++;
			
			if ((equalLength + 1) < lowerS.length()) {
				
				String equalS = lowerS.substring(0, equalLength);
				int lowerPosVal = Integer.parseInt(lowerS.substring(equalLength,
						equalLength + 1));
				int upperPosVal = Integer.parseInt(upperS.substring(equalLength,
						equalLength + 1));
				if ((upperPosVal - lowerPosVal) > 1) {
					sb.append("^(" + equalS);
					if ((upperPosVal - lowerPosVal) == 2) {
						sb.append("" + (lowerPosVal + 1));
					} else {
						sb.append("[" + (lowerPosVal + 1) + "-" + (upperPosVal - 1)
								+ "]");
					}
					sb.append("[0-9]{" + (lowerS.length() - 1 - equalLength)
							+ "})$|");
				}
				
				// Now the missing values in the lower area
				for (int i = (equalLength + 1); i < lowerS.length(); i++) {
					int posVal = Integer.parseInt(lowerS.substring(i, i + 1));
					if (posVal != 9) {
						if (i < (lowerS.length() - 1)) {
							sb.append("^(");
							sb.append(lowerS.substring(0, i));
							if (posVal == 8) {
								sb.append("" + 9);
							} else {
								sb.append("[" + (posVal + 1) + "-9]");
							}
							sb.append("[0-9]{" + (lowerS.length() - 1 - i)
									+ "})$|");
						} else {
							sb.append("^(");
							sb.append(lowerS.substring(0, i));
							sb.append("[" + (posVal) + "-9])$|");
						}
					} else {
						if (i == (lowerS.length() - 1)) {
							sb.append("^(" + lower + ")$|");
						}
					}
				}
				
				for (int i = (equalLength + 1); i < upperS.length(); i++) {
					int posVal = Integer.parseInt(upperS.substring(i, i + 1));
					if (posVal != 0) {
						if (i < (upperS.length() - 1)) {
							sb.append("^(");
							sb.append(upperS.substring(0, i));
							if (posVal == 1) {
								sb.append("" + 0);
							} else {
								sb.append("[0-" + (posVal - 1) + "]");
							}
							sb.append("[0-9]{" + (upperS.length() - 1 - i) + "})$|");
						} else {
							sb.append("^(");
							sb.append(upperS.substring(0, i));
							sb.append("[0-" + (posVal) + "])$|");
						}
					} else {
						if (i == (upperS.length() - 1)) {
							sb.append("^(" + upper + ")$|");
						}
					}
				}
			} else {
				String equalS = lowerS.substring(0, equalLength);
				int lowerPosVal = Integer.parseInt(lowerS.substring(equalLength,
						equalLength + 1));
				int upperPosVal = Integer.parseInt(upperS.substring(equalLength,
						equalLength + 1));
				sb.append("^(" + equalS + "[" + lowerPosVal + "-" + upperPosVal + "])$|");
			}
		} else {
			// At first the powers completly in range
			if (upperS.length() - lowerS.length() > 1) {
				for (int i = lowerS.length() + 1; i < upperS.length(); i++) {
					sb.append("^([0-9]{" + i + "})$|");
				}
			}

			// Now the missing values in the power of the lower value
			for (int i = 0; i < lowerS.length(); i++) {
				int posVal = Integer.parseInt(lowerS.substring(i, i + 1));
				if (posVal != 9) {
					if (i < (lowerS.length() - 1)) {
						sb.append("^(");
						sb.append(lowerS.substring(0, i));
						if (posVal == 8) {
							sb.append("" + 9);
						} else {
							sb.append("[" + (posVal + 1) + "-9]");
						}
						sb.append("[0-9]{" + (lowerS.length() - 1 - i) + "})$|");
					} else {
						sb.append("^(");
						sb.append(lowerS.substring(0, i));
						sb.append("[" + (posVal) + "-9])$|");
					}
				} else {
					if (i == (lowerS.length() - 1)) {
						sb.append("^(" + lower + ")$|");
					}
				}
			}

			// Now the missing values in the power of the upper value
			for (int i = 0; i < upperS.length(); i++) {
				int posVal = Integer.parseInt(upperS.substring(i, i + 1));
				if (posVal != 0) {
					if (i < (upperS.length() - 1)) {
						sb.append("^(");
						sb.append(upperS.substring(0, i));
						if (posVal == 1) {
							sb.append("" + 0);
						} else {
							sb.append("[0-" + (posVal - 1) + "]");
						}
						sb.append("[0-9]{" + (upperS.length() - 1 - i) + "})$|");
					} else {
						sb.append("^(");
						sb.append(upperS.substring(0, i));
						sb.append("[0-" + (posVal) + "])$|");
					}
				} else {
					if (i == (upperS.length() - 1)) {
						sb.append("^(" + upper + ")$|");
					}
				}
			}
		}
		return sb.substring(0, (sb.length() - 1));
	}
}
