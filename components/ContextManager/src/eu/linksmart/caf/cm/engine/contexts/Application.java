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

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import eu.linksmart.caf.cm.engine.TimeService;

/**
 * Application Context Fact Type, used in the Rule Engine.
 * 
 * @author Michael Crouch
 * 
 */
public class Application extends MemberedContext {

	/** the author */
	private String author;

	/** the version */
	private String version;

	/** the uri ontology reference */
	private String applicationUri;

	/**
	 * Constructor
	 * 
	 * @param appId
	 *            application id (name)
	 * @param author
	 *            the author
	 * @param version
	 *            the version
	 */
	public Application(String appId, String author, String version) {
		super(appId);
		this.author = author;
		this.version = version;
	}

	/**
	 * Gets the Author of the {@link Application} context
	 * 
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Sets the Author of the Application context
	 * 
	 * @param author
	 *            the author
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Gets the application uri
	 * 
	 * @return the application uri
	 */
	public String getApplicationUri() {
		return applicationUri;
	}

	/**
	 * Sets the application uri
	 * 
	 * @param applicationUri
	 *            the application uri
	 */
	public void setApplicationUri(String applicationUri) {
		this.applicationUri = applicationUri;
	}

	/**
	 * Sets the version
	 * 
	 * @param version
	 *            the version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Gets the version of the {@link Application} context
	 * 
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	@Override
	public void encode(PrintStream out) {
		Map<String, String> ctxAttrs = new LinkedHashMap<String, String>();
		ctxAttrs.put("Author", author);
		ctxAttrs.put("Version", version);
		ctxAttrs.put("ApplicationURI", applicationUri);
		ctxAttrs.put("Timestamp", this.getTimestamp());

		Set<String> additions = new HashSet<String>();
		additions.add(encodeMembers());

		this.encode(null, ctxAttrs, additions, out);
	}

}
