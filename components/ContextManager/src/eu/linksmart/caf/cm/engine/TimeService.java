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
package eu.linksmart.caf.cm.engine;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

import eu.linksmart.caf.cm.managers.RuleEngine;

/**
 * Provide Time-related functionalities to the Context Manager, including
 * timestamping. Installed as a global in the {@link RuleEngine}
 * 
 * @author Michael Crouch
 * 
 */
public final class TimeService {

	/** the {@link Logger} */
	private static final Logger logger = Logger.getLogger(TimeService.class);

	/** Default timestamp format */
	public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

	/** Singleton instance */
	private static TimeService instance;

	/** The {@link SimpleDateFormat} for the timestamp */
	private final SimpleDateFormat sdf;

	/**
	 * Singleton Constructor
	 * 
	 * @param format
	 *            DateTime format
	 */
	private TimeService(String format) {
		sdf = new SimpleDateFormat(format, Locale.ENGLISH);
	}

	/**
	 * Gets the TimeService instance. If not already created, it is created with
	 * the default formated: <code>"yyyy-MM-dd HH:mm:ss Z"</code>
	 * 
	 * @return the {@link TimeService}
	 */
	public static synchronized TimeService getInstance() {
		if (instance == null) {
			instance = new TimeService(DEFAULT_FORMAT);
		}
		return instance;
	}

	/**
	 * Gets an instance of the {@link TimeService} with the given format.<p> If
	 * the {@link TimeService} has already initialised with a different format,
	 * a warning is logged, and the previously initialised {@link TimeService}
	 * is returned.
	 * 
	 * @param format
	 *            the format to initialise with
	 * @return the {@link TimeService}
	 */
	public static synchronized TimeService getInstance(String format) {
		if (instance == null) {
			instance = new TimeService(format);
		} else if (!instance.getFormat().equals(format)) {
			logger.warn("TimeService already exists with different format. "
					+ "[" + instance.getFormat() + "].");
		}

		return instance;
	}

	/**
	 * Gets a String timestamp representing the current time
	 * 
	 * @return the timestamp
	 */
	public String getCurrentTimestamp() {

		Date now = new Date();
		return sdf.format(now);
	}

	/**
	 * Returns the Current Time represented as a {@link Date}
	 * 
	 * @return the {@link Date}
	 */
	public Date getCurrentTime() {
		return new Date();
	}

	/**
	 * Gets a {@link Date} object from the given timestamp
	 * 
	 * @param timestamp
	 *            the timestamp
	 * @return the associated {@link Date}
	 * @throws ParseException
	 */
	public Date getDate(String timestamp) throws ParseException {
		return sdf.parse(timestamp);
	}

	/**
	 * Gets the format used by the current {@link TimeService}
	 * 
	 * @return the format
	 */
	public String getFormat() {
		return sdf.toPattern();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

}
