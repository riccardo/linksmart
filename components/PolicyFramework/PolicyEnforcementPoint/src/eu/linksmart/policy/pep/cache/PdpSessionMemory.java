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
package eu.linksmart.policy.pep.cache;

import eu.linksmart.policy.pep.cache.impl.PdpSessionItem;

/**
 * <p>Manages {@link PdpSessionItem} storage and handles removal of expired 
 * data</p>
 * 
 * @author Marco Tiemann
 *
 */
public interface PdpSessionMemory {
	
	/**
	 * @return
	 * 				the session lifetime in milliseconds
	 */
	public long getLifetime();
	
	/**
	 * @param theLifetime
	 * 				the session lifetime in milliseconds
	 */
	public void setLifetime(long theLifetime);
	
	/**
	 * @param theLifetime
	 * 				the session lifetime i.e. time for which a session decision 
	 * 				is cached. Accepted formats are <code>Integer</code> or 
	 * 				<code>Long</code> values with optional suffixes indicating 
	 * 				the time unit used. Accepted	suffixes are:
	 * 				- "d" or "D" for days, 
	 * 				- "h" or "H" for hours,
	 * 				- "m" or "M" for minutes, 
	 * 				- "s" or "S" for seconds, 
	 * 				- "ms" or "MS" for milliseconds. 
	 * 				If no suffix is given, the value is assumed	to be 
	 * 				represented	as milliseconds.
	 */
	public void setLifetime(String theLifetime);

	/**
	 * @return
	 * 				a flag denoting whether session lifetimes are extended as 
	 * 				subsequent equivalent requests are received
	 */
	public boolean getKeepAlive();
	
	/**
	 * @param theKeepSessionsAlive
	 * 				a flag denoting whether session lifetimes are to be 
	 * 				extended as subsequent equivalent requests are received
	 */
	public void setKeepAlive(boolean theKeepSessionsAlive);
	
	/**
	 * @param theNtry
	 * 				the {@link PdpSessionItem}
	 */
	public void add(PdpSessionItem theNtry);
	
	/**
	 * Searches for a {@link PdpSessionItem} that matches the arguments 
	 * provided; returns the found <code>PdpSessionItem</code> or 
	 * <code>null</code> if no matching instance could be found
	 * 
	 * @param theSessionId
	 * 				the session ID
	 * @param theParameters
	 * 				the method call
	 * @param theTimestamp
	 * 				the timestamp
	 * @return
	 * 				the found {@link PdpSessionItem} or <code>null</code>
	 */
	public PdpSessionItem find(String theParameters, 
			long theTimestamp);
	
	/** Empties the session cache */
	public void flush();
	
}
