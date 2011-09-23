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
package eu.linksmart.caf.cm.engine.event;

import java.text.ParseException;
import java.util.Date;

import eu.linksmart.caf.cm.engine.TimeService;
import eu.linksmart.caf.cm.engine.contexts.BaseContext;
import eu.linksmart.caf.cm.engine.members.ContextMember;
import eu.linksmart.caf.cm.managers.RuleEngine;

/**
 * Event fired in the {@link RuleEngine} when a {@link ContextMember} is
 * updated,<p> Contains the updated {@link ContextMember}, and a clone of the
 * {@link ContextMember} before it was updated.
 * 
 * @author Michael Crouch
 * 
 */
public class UpdatedMemberEvent {

	/** a clone of the {@link ContextMember} before it was updated */
	private ContextMember before;

	/** the current, updated, {@link ContextMember} */
	private ContextMember after;

	/** the parent {@link BaseContext} */
	private BaseContext parentContext;

	/** the time (in ms) since last update */
	private long updateInterval;

	/** the parent contextIs */
	private String contextId;

	/** the {@link ContextMember} key */
	private String key;

	/**
	 * Constructor
	 * 
	 * @param before
	 *            the clone of the {@link ContextMember} before it was updated
	 * @param after
	 *            the current, updated, {@link ContextMember}
	 */
	public UpdatedMemberEvent(ContextMember before, ContextMember after) {
		this.before = before;
		this.after = after;

		this.parentContext = after.getContext();
		this.contextId = parentContext.getContextId();
		this.key = after.getKey();

		try {
			Date timeBefore =
					TimeService.getInstance().getDate(before.getTimestamp());
			Date timeAfter =
					TimeService.getInstance().getDate(before.getTimestamp());
			updateInterval = timeAfter.getTime() - timeBefore.getTime();
		} catch (ParseException e) {
			updateInterval = 0;
		}
	}

	/**
	 * Gets the representation of the {@link ContextMember} before it was
	 * updated
	 * 
	 * @return the 'before' {@link ContextMember}
	 */
	public ContextMember getBefore() {
		return before;
	}

	/**
	 * Gets the representation of the {@link ContextMember} after it was updated
	 * 
	 * @return the 'after' {@link ContextMember}
	 */
	public ContextMember getAfter() {
		return after;
	}

	/**
	 * Gets the parent {@link BaseContext} of the updated {@link ContextMember}
	 * 
	 * @return the parent {@link BaseContext}
	 */
	public BaseContext getParentContext() {
		return parentContext;
	}

	/**
	 * Returns the time (in milliseconds, as {@link Long}), since the Member was
	 * last updated
	 * 
	 * @return the time since last updated
	 */
	public long getUpdateInterval() {
		return updateInterval;
	}

	/**
	 * Gets the contextId of the parent context
	 * 
	 * @return the contextId
	 */
	public String getContextId() {
		return contextId;
	}

	/**
	 * Gets the key of the updated {@link ContextMember}
	 * 
	 * @return the key;
	 */
	public String getKey() {
		return key;
	}
}
