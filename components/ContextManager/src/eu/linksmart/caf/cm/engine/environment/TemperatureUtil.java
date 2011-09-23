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
package eu.linksmart.caf.cm.engine.environment;

import eu.linksmart.caf.cm.engine.members.ContextMember;

/**
 * Utility class to resolve Temperature units from {@link ContextMember}s.<p>
 * The {@link ContextMember} must be annotated with the unit metadata.
 * 
 * @author Michael Crouch
 * 
 */
public class TemperatureUtil {

	/** Fahrenheit identifier */
	public static final String FAHRENHEIT = "fahrenheit";

	/** Celsius identifier */
	public static final String CELSIUS = "degree_celsius";

	/** Kelvin identifier */
	public static final String KELVIN = "kelvin";

	/** the RegEx */
	private static String regex;

	static {
		regex = "\\b(";
		regex = regex + FAHRENHEIT + "|";
		regex = regex + CELSIUS + "|";
		regex = regex + KELVIN;
		regex = regex + ")\\b";
	}

	/**
	 * Converts the numValue of the given {@link ContextMember}, to Kelvin, and
	 * returns
	 * 
	 * @param member
	 *            the {@link ContextMember} representing a Temperature
	 * @return the temperature in Kelvin
	 */
	public double asKelvin(ContextMember member) {
		if (!isTemp(member))
			return 0;

		if (member.getInstanceOf().equals(KELVIN))
			return member.getNumValue();

		if (member.getInstanceOf().equals(CELSIUS))
			return member.getNumValue() + 273.15;

		if (member.getInstanceOf().equals(FAHRENHEIT))
			return (member.getNumValue() + 459.67) * (5 / 9);

		return 0;
	}

	/**
	 * Converts the numValue of the given {@link ContextMember}, to Celsius, and
	 * returns
	 * 
	 * @param member
	 *            the {@link ContextMember} representing a Temperature
	 * @return the temperature in Celsius
	 */
	public double asCelsius(ContextMember member) {
		if (!isTemp(member))
			return 0;

		if (member.getInstanceOf().equals(KELVIN))
			return member.getNumValue() - 273.15;

		if (member.getInstanceOf().equals(CELSIUS))
			return member.getNumValue();

		if (member.getInstanceOf().equals(FAHRENHEIT))
			return (member.getNumValue() - 32) * (5 / 9);

		return 0;
	}

	/**
	 * Converts the numValue of the given {@link ContextMember}, to Fahrenheit,
	 * and returns
	 * 
	 * @param member
	 *            the {@link ContextMember} representing a Temperature
	 * @return the temperature in Fahrenheit
	 */
	public double asFahrenheit(ContextMember member) {
		if (!isTemp(member))
			return 0;

		if (member.getInstanceOf().equals(KELVIN))
			return (member.getNumValue() * (9 / 5)) - 459.67;

		if (member.getInstanceOf().equals(CELSIUS))
			return (member.getNumValue() * (9 / 5)) + 32;

		if (member.getInstanceOf().equals(FAHRENHEIT))
			return member.getNumValue();

		return 0;
	}

	/**
	 * Performs a check that the {@link ContextMember} represents a Temperature
	 * 
	 * @param member
	 *            the {@link ContextMember}
	 * @return whether it is a Temperature or not
	 */
	private boolean isTemp(ContextMember member) {
		if (!member.isNumeric())
			return false;
		return member.getInstanceOf().matches(regex);
	}
}
