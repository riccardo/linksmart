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
package eu.linksmart.caf.cm.rules.drl;

import java.util.HashSet;
import java.util.Set;

import eu.linksmart.caf.cm.action.ActionManager;
import eu.linksmart.caf.cm.engine.Encodeable;
import eu.linksmart.caf.cm.engine.contexts.BaseContext;
import eu.linksmart.caf.cm.engine.environment.TemperatureUtil;
import eu.linksmart.caf.cm.engine.event.BaseEvent;
import eu.linksmart.caf.cm.engine.members.ContextMember;
import eu.linksmart.caf.cm.managers.CmInternalManager;

/**
 * Final class defining the qualified class names to be imported to all DRL
 * files. Provides {@link Set} of imports as String
 * 
 * @author Michael Crouch
 * 
 */
public final class BaseImports {

	/** the {@link Set} of imports */
	private static final Set<String> BASE_IMPORTS;

	/**
	 * Initialises the set of imports to add to all processed DRLs
	 */
	static {
		BASE_IMPORTS = new HashSet<String>();

		BASE_IMPORTS.add(ActionManager.class.getPackage().getName() + ".*");
		BASE_IMPORTS.add(Encodeable.class.getPackage().getName() + ".*");
		BASE_IMPORTS.add(BaseContext.class.getPackage().getName() + ".*");
		BASE_IMPORTS.add(TemperatureUtil.class.getPackage().getName() + ".*");
		BASE_IMPORTS.add(BaseEvent.class.getPackage().getName() + ".*");
		BASE_IMPORTS.add(ContextMember.class.getPackage().getName() + ".*");
		BASE_IMPORTS.add(CmInternalManager.class.getPackage().getName() + ".*");
		BASE_IMPORTS.add("java.util.*");

		
	}

	/**
	 * Singleton Constructor
	 */
	private BaseImports() {
	};

	/**
	 * Gets the {@link Set} of imports
	 * 
	 * @return the imports
	 */
	public static Set<String> getImports() {
		return BASE_IMPORTS;
	}
}
