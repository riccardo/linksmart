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

import java.util.Set;


import eu.linksmart.caf.cm.ContextManager;
import eu.linksmart.caf.cm.impl.CmManagerHub;
import eu.linksmart.eventmanager.Part;

/**
 * Interface for components that handle events received by the
 * {@link ContextManager}.
 * 
 * @author Michael Crouch
 * 
 */
public interface IEventHandler {

	/**
	 * Registers the Event Handler with the {@link CmManagerHub}.
	 * 
	 * @param managers
	 *            the {@link CmManagerHub}
	 */
	public void register(CmManagerHub managers);

	/**
	 * Called just before the handler is unregisted
	 */
	public void unregistering();

	/**
	 * Returns whether the {@link IEventHandler} can handle the given topic.<p>
	 * Can simply return whether it is contained in the {@link Set} of
	 * handledTopics (see getHandledTopics), but may also handle the use of
	 * Regular Expressions for the subscription for Events
	 * 
	 * @param topic
	 *            the topic
	 * @return whether it can
	 */
	public boolean canHandleEvent(String topic);

	/**
	 * Returns a {@link Set} of the topics that the {@link IEventHandler} can
	 * handle
	 * 
	 * @return {@link Set} of topics
	 */
	public Set<String> getHandledTopics();

	/**
	 * Handles the received Event
	 * 
	 * @param topic
	 *            the topic
	 * @param parts
	 *            array of {@link Part}s
	 */
	public void handleEvent(String topic, Part[] parts);

}
