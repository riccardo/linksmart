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
package eu.linksmart.policy.pep.response.bundle.impl.debug;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.policy.pep.response.impl.BundlePepObligationObserverWrapper;
import eu.linksmart.policy.pep.response.impl.DebugPepObligationObserver;

/**
 * <p>Bundle wrapper around the {@link DebugPepObligationObserver}</p>
 * 
 * @author Marco Tiemann
 *
 */
public class DebugPepObligationObserverWrapper 
		extends BundlePepObligationObserverWrapper {

	/** logger */
	private static final Logger logger 
			= Logger.getLogger(DebugPepObligationObserverWrapper.class);
	
	/** No-args constructor */
	public DebugPepObligationObserverWrapper() {
		super(new DebugPepObligationObserver());
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.response.impl
	 * 		.BundlePepObligationObserverWrapper#activate(
	 * 		org.osgi.service.component.ComponentContext)
	 */
	@Override
	protected void activate(ComponentContext theContext) {
		logger.debug("Activating");
		super.activate(theContext);
		logger.debug("Activated");
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.response.impl
	 * 		.BundlePepObligationObserverWrapper#deactivate(
	 * 		org.osgi.service.component.ComponentContext)
	 */
	@Override
	protected void deactivate(ComponentContext theContext) {
		logger.debug("Deactivating");
		// intentionally left blank
		logger.debug("Deactivated");
	}

}
