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
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import eu.linksmart.caf.cm.engine.Encodeable;
import eu.linksmart.caf.cm.engine.TimeService;
import eu.linksmart.caf.cm.engine.members.ContextMember;

/**
 * Abstract base class for all types of Context. <p> Has a container (
 * {@link Set}) of {@link Location} contexts that are represent associated
 * locations of the context. All base contexts also have an associated HID, that
 * may be null.
 * 
 * @author Michael Crouch
 * 
 */
public abstract class BaseContext implements Encodeable {

	/** {@link Set} of {@link Location}s of the context */
	private Set<Location> hasLocations;

	/** the contextId */
	private String contextId;

	/** the name */
	private String name;

	/** the hid */
	private String hid;

	/** the timestamp */
	private String timestamp;

	/**
	 * Constructor passing the given name. As no unique contextId is provided,
	 * it is generated as a random UUID.
	 */
	public BaseContext(String name) {
		this(UUID.randomUUID().toString(), name);
	}

	/**
	 * Constructor with contextId and name
	 * 
	 * @param contextId
	 *            the contextId
	 * @param name
	 *            the name
	 */
	public BaseContext(String contextId, String name) {
		this.contextId = contextId;
		this.name = name;
		this.timestamp = TimeService.getInstance().getCurrentTimestamp();
		hasLocations = new HashSet<Location>();
	}

	/**
	 * Gets the contextId of the Context
	 * 
	 * @return the contextId
	 */
	public String getContextId() {
		return contextId;
	}

	/**
	 * Sets the contextId of the Context
	 * 
	 * @param contextId
	 *            the contextId to set
	 */
	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	/**
	 * Gets the Time Stamp representing when this Context was instantiated
	 * 
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * Gets the {@link Set} of {@link Location}s of the context
	 * 
	 * @return the {@link Set} of {@link Location}s
	 */
	public Set<Location> getHasLocations() {
		return hasLocations;
	}

	/**
	 * Sets the {@link Set} of {@link Location}s of the context
	 * 
	 * @param hasLocations
	 *            the {@link Set} of {@link Location}s
	 */
	public void setHasLocations(Set<Location> hasLocations) {
		this.hasLocations = hasLocations;
	}

	/**
	 * Gets the context name
	 * 
	 * @return the context name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the context name
	 * 
	 * @param name
	 *            the context name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the HID represented by the context, if one exists
	 * 
	 * @return the HID
	 */
	public String getHid() {
		return hid;
	}

	/**
	 * Sets the HID represented by the context
	 * 
	 * @param hid
	 *            the HID
	 */
	public void setHid(String hid) {
		this.hid = hid;
	}

	@Override
	public abstract void encode(PrintStream out);

	/**
	 * Helper method for all implemented BaseContext subtypes. Encodes the
	 * {@link ContextProperty}s and {@link ContextMember}s as XML
	 * 
	 * @param rootAttrs
	 *            {@link Map} of attributes to add to the root node of the XML
	 * @param ctxAttrs
	 *            {@link Map} of elements to add (element name | value) under
	 *            the root node of the XML
	 * @param additions
	 *            {@link Set} of Strings to also encode
	 * @param out
	 *            the PrintStream to write to
	 */
	protected void encode(Map<String, String> rootAttrs,
			Map<String, String> ctxAttrs, Set<String> additions, PrintStream out) {
		String contextType = this.getClass().getSimpleName();
		String root = "<" + contextType + " name=\"" + name + "\""; // >");
		if ((hid != null) && (!"".equals(hid)))
			root = root + " hid=\"" + hid + "\"";
		if (rootAttrs != null) {
			Iterator<String> it1 = rootAttrs.keySet().iterator();
			while (it1.hasNext()) {
				String key = it1.next();
				String value = rootAttrs.get(key);
				root = root + " " + key + "=\"" + value + "\"";
			}
		}
		root = root + ">";
		out.println(root);

		out.println("<hasLocations>");
		Iterator<Location> locIt = hasLocations.iterator();
		while (locIt.hasNext()) {
			Location loc = locIt.next();
			out.println(loc.getXmlReferenceElement());
		}
		out.println("</hasLocations>");

		if (ctxAttrs != null) {
			Iterator<String> it2 = ctxAttrs.keySet().iterator();
			while (it2.hasNext()) {
				String key = it2.next();
				String value = ctxAttrs.get(key);
				out.println("<" + key + ">" + value + "</" + key + ">");
			}
		}

		if (additions != null) {
			Iterator<String> addIt = additions.iterator();
			while (addIt.hasNext()) {
				out.println(addIt.next());
			}
		}
		out.println("</" + contextType + ">");
	}

	/**
	 * Encodes the Context as XML, and returns as a String
	 * 
	 * @return the encoded Context as XML String
	 */
	@Override
	public String toString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		this.encode(out);
		return baos.toString();
	}

}
