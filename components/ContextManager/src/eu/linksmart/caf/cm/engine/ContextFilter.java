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

import org.apache.log4j.Logger;
import org.drools.runtime.ObjectFilter;

import eu.linksmart.caf.cm.engine.contexts.BaseContext;
import eu.linksmart.caf.cm.managers.RuleEngine;

/**
 * {@link ObjectFilter} for extracting contexts from the {@link RuleEngine}.<p>
 * 
 * Can filter against the contextId, name and hid.
 * 
 * @author Michael Crouch
 * 
 */
public class ContextFilter implements ObjectFilter {

	/** ContextId filter type */
	public static final int CONTEXT_ID_FILTER = 0;

	/** Context name filter type */
	public static final int CONTEXT_NAME_FILTER = 1;

	/** Context HID filter type */
	public static final int CONTEXT_HID_FILTER = 2;

	/** the {@link Logger} */
	private static final Logger logger = Logger.getLogger(ContextFilter.class);

	/** The string to match against */
	private final String toMatch;

	/** The type of the filter */
	private final int filterType;

	/** The context class to match against (minimum is {@link BaseContext} */
	private final Class clazz;

	/**
	 * Constructor
	 * 
	 * @param clazz
	 *            the class to match against
	 * @param filterType
	 *            the type of the filter to match with
	 * @param toMatch
	 *            the variable to match
	 */
	public ContextFilter(Class clazz, int filterType, String toMatch) {
		this.clazz = clazz;
		this.toMatch = toMatch;
		this.filterType = filterType;
	}

	/**
	 * Matches the given object, to check that it an instance of the defined
	 * class, and the toMatch value matches the value of the context specified
	 * by the filter type.
	 */
	@Override
	public boolean accept(Object obj) {
		if (obj instanceof BaseContext) {
			BaseContext ctx = (BaseContext) obj;
			
			if (!clazz.isInstance(ctx))
				return false;

			switch (filterType) {
				case CONTEXT_HID_FILTER: {	
					if (toMatch.equals(ctx.getHid()))
						return true;
					break;
				}

				case CONTEXT_ID_FILTER: {					
					if (toMatch.equals(ctx.getContextId()))
						return true;
					break;
				}

				case CONTEXT_NAME_FILTER: {
					if (toMatch.equals(ctx.getName()))
						return true;
					break;
				}

				default: {
					logger.error("Unrecognised ContextFilter - " + filterType);
					return false;
				}
			}
		}
		return false;
	}
}
