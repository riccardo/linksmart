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
package eu.linksmart.caf.daqc.protocol.push;

import eu.linksmart.caf.daqc.reporting.SubscriberStub;

/**
 * Class representing a subscriber for data from the {@link PushProtocol}. <p>
 * Includes the {@link SubscriberStub} for reporting data to, and the
 * subscriber-specific alias(es) for the data.
 * 
 * @author Michael Crouch
 * 
 */
public final class PushSubscriber {

	/** the {@link SubscriberStub} */
	private SubscriberStub subscriber;
	
	/** the aliases */
	private String[] aliases;

	/**
	 * Constructor
	 * 
	 * @param subscriber
	 *            the {@link SubscriberStub}
	 */
	public PushSubscriber(SubscriberStub subscriber) {
		this(subscriber, null);
	}

	/**
	 * Constructor
	 * 
	 * @param subscriber
	 *            the {@link SubscriberStub}
	 * @param alias
	 *            the alias for the data
	 */
	public PushSubscriber(SubscriberStub subscriber, String alias) {
		this.subscriber = subscriber;
		this.aliases = new String[0];
		if (alias != null)
			addAlias(alias);

	}

	/**
	 * Gets the {@link SubscriberStub}
	 * 
	 * @return the {@link SubscriberStub}
	 */
	public SubscriberStub getSubscriberStub() {
		return subscriber;
	}

	/**
	 * Gets the String array of data aliases
	 * 
	 * @return the String array of data aliases
	 */
	public String[] getAliases() {
		return aliases.clone();
	}

	/**
	 * Sets the String array of data aliases
	 * 
	 * @param aliases
	 *            the String array of data aliases
	 */
	public void setAliases(String[] aliases) {
		this.aliases = aliases.clone();
	}

	/**
	 * Returns whether {@link PushSubscriber} contains the give alias
	 * 
	 * @param alias
	 *            the alias for the data
	 * @return whether it contains it
	 */
	public boolean hasAlias(String alias) {
		for (String str : aliases) {
			if (str.equals(alias))
				return true;
		}
		return false;
	}

	/**
	 * Adds an alias for the data, to the {@link PushSubscriber}
	 * 
	 * @param alias
	 *            the alias to add
	 */
	public void addAlias(String alias) {
		if (!hasAlias(alias)) {
			int newLength = aliases.length + 1;
			String[] newArray = new String[newLength];
			System.arraycopy(aliases, 0, newArray, 0, aliases.length);
			newArray[newLength - 1] = alias;
			aliases = newArray;
		}
	}

	/**
	 * Removes the given alias
	 * 
	 * @param alias
	 *            the alias to remove
	 */
	public void removeAlias(String alias) {
		if (hasAlias(alias)) {
			int newLength = aliases.length - 1;
			String[] newArray = new String[newLength];
			boolean removed = false;
			for (int i = 0; i < aliases.length; i++) {
				String str = aliases[i];
				if (str.equals(alias)) {
					removed = true;
				} else {
					if (removed) {
						newArray[i - 1] = str;
					} else {
						newArray[i] = str;
					}
				}
			}
			aliases = newArray;
		}

	}
}
