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
package eu.linksmart.caf.cm.engine.contexts;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.linksmart.caf.cm.engine.members.ContextMember;

/**
 * {@link MemberedContext} extends the {@link BaseContext}, adding a {@link Set}
 * of {@link ContextMember}s. <p> Abstract class extended by context types that
 * maintain a dynamic dataset - in {@link ContextMember}s.
 * 
 * @author Michael Crouch
 * 
 */
public abstract class MemberedContext extends BaseContext {
	
	/** {@link Set} of {@link ContextMember}s */
	private Set<ContextMember> members;

	/**
	 * Constructor with name
	 * 
	 * @param name
	 *            the name
	 */
	public MemberedContext(String name) {
		super(name);
		members = new HashSet<ContextMember>();
	}

	/**
	 * Constructor with contextId and name
	 * 
	 * @param contextId
	 *            the contextId
	 * @param name
	 *            the name
	 */
	public MemberedContext(String contextId, String name) {
		super(contextId, name);
		members = new HashSet<ContextMember>();
	}

	/**
	 * Adds a {@link ContextMember} to the Context
	 * 
	 * @param member
	 *            the {@link ContextMember} to add
	 */
	public void addMember(ContextMember member) {
		members.add(member);
	}

	/**
	 * Returns whether the Context contains a {@link ContextMember} with the
	 * given key
	 * 
	 * @param key
	 *            the key of the {@link ContextMember} to check for
	 * @return boolean declaring whether or not it exists
	 */
	public boolean hasMember(String key) {
		return hasMember(key, null);
	}

	/**
	 * Returns whether the Context contains a {@link ContextMember} with the
	 * given kry AND is an instance of the given instanceOf
	 * 
	 * @param key
	 *            the key of the {@link ContextMember} to check for
	 * @param instanceOf
	 *            the instanceOf of the {@link ContextMember} to check for
	 * @return boolean declaring whether or not it exists
	 */
	public boolean hasMember(String key, String instanceOf) {
		if (getMember(key, instanceOf) == null)
			return false;
		return true;
	}

	/**
	 * Gets the {@link ContextMember} with the given key
	 * 
	 * @param key
	 *            the key of the {@link ContextMember} to get
	 * @return the matching {@link ContextMember}, or null
	 */
	public ContextMember getMember(String key) {
		return getMember(key, null);
	}

	/**
	 * Gets the {@link ContextMember} with the given key, and the given
	 * instanceOf
	 * 
	 * @param key
	 *            the key of the {@link ContextMember} to get
	 * @param instanceOf
	 *            the instanceOf of the {@link ContextMember} to get
	 * @return the matching {@link ContextMember}, or null
	 */
	public ContextMember getMember(String key, String instanceOf) {
		Iterator<ContextMember> it = members.iterator();
		while (it.hasNext()) {
			ContextMember member = it.next();
			if (member.getKey().equals(key)) {
				if ((instanceOf != null) && (!"".equals(instanceOf))) {
					if (member.getInstanceOf().equals(instanceOf))
						return member;
				} else {
					return member;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the Set of {@link ContextMember}s
	 * 
	 * @return the Set of {@link ContextMember}s
	 */
	public Set<ContextMember> getMembers() {
		return members;
	}

	/**
	 * Sets the Set of {@link ContextMember}s
	 * 
	 * @param members
	 *            the Set of {@link ContextMember}s to set
	 */
	public void setMembers(Set<ContextMember> members) {
		this.members = members;
	}

	/**
	 * Removes the {@link ContextMember} with the given key from the context
	 * 
	 * @param key
	 *            the key
	 */
	public void removeMember(String key) {
		removeMember(getMember(key));
	}

	/**
	 * Removes the given {@link ContextMember} from the context
	 * 
	 * @param member
	 *            the {@link ContextMember}
	 */
	public void removeMember(ContextMember member) {
		if (member != null)
			members.remove(member);
	}

	/**
	 * Encodes the {@link Set} of {@link ContextMember}s to a String
	 * 
	 * @return the encoded String
	 */
	public String encodeMembers() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		out.println("<Members>");
		Iterator<ContextMember> it = members.iterator();
		while (it.hasNext()) {
			ContextMember member = it.next();
			//if (member.getStrValue() != null)
				out.println(member.toString());
		}
		out.print("</Members>");
		return baos.toString();
	}
}
