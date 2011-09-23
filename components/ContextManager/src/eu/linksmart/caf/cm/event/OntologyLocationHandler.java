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
package eu.linksmart.caf.cm.event;

import java.util.HashSet;
import java.util.Set;


import eu.linksmart.caf.cm.impl.CmManagerHub;
import eu.linksmart.caf.cm.impl.ContextManagerApplication;
import eu.linksmart.caf.cm.managers.DeviceContextManager;
import eu.linksmart.eventmanager.Part;

public class OntologyLocationHandler implements IEventHandler {

	public static final String LOCATION_CHANGED = "DeviceLocationChanged";

	private static Set<String> handledTopics;

	private DeviceContextManager devMgr;
	private ContextManagerApplication app;

	static {
		handledTopics = new HashSet<String>();
		handledTopics.add(LOCATION_CHANGED);
	}

	@Override
	public boolean canHandleEvent(String topic) {
		return handledTopics.contains(topic);
	}

	@Override
	public Set<String> getHandledTopics() {
		return handledTopics;
	}

	@Override
	public void handleEvent(String topic, Part[] parts) {
		// TODO Auto-generated method stub

	}

	@Override
	public void register(CmManagerHub managers) {
		app = managers.getCmApp();
		devMgr =
				(DeviceContextManager) managers
						.getManager(DeviceContextManager.MANAGER_ID);

	}

	@Override
	public void unregistering() {
		// TODO Auto-generated method stub

	}

}
