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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The {@link SecurityContext} represents the active mechanisms and
 * configurations for LinkSmart Security within the LinkSmart-defined Security Boundary.
 * 
 * @author Michael Crouch
 * 
 */
public class SecurityContext extends MemberedContext {

	/** HID of the {@link NetworkManagerApplication} */
	private String nmHid;

	/** Description of the Network Manager */
	private String nmDescription;

	/** URL of the Trust Manager used by the Network Manager */
	private String trustManagerUrl;

	/** URL of the Crypto Manager used by the Network Manager */
	private String cryptoManagerUrl;

	/**
	 * Constructor
	 */
	public SecurityContext() {
		super("securitycontext:network");
	}

	@Override
	public void encode(PrintStream out) {
		Map<String, String> ctxAttrs = new LinkedHashMap<String, String>();
		ctxAttrs.put("nmHid", nmHid);
		ctxAttrs.put("nmDescription", nmDescription);
		ctxAttrs.put("trustManagerUrl", trustManagerUrl);
		ctxAttrs.put("cryptoManagerUrl", cryptoManagerUrl);

		Set<String> additions = new HashSet<String>();
		additions.add(encodeMembers());

		this.encode(null, ctxAttrs, additions, out);
	}

	/**
	 * Gets the nmHid
	 * 
	 * @return the nmHid
	 */
	public String getNmHid() {
		return nmHid;
	}

	/**
	 * Sets the nmHid
	 * 
	 * @param nmHid
	 *            the nmHid to set
	 */
	public void setNmHid(String nmHid) {
		this.nmHid = nmHid;
	}

	/**
	 * Gets the nmDescription
	 * 
	 * @return the nmDescription
	 */
	public String getNmDescription() {
		return nmDescription;
	}

	/**
	 * Sets the nmDescription
	 * 
	 * @param nmDescription
	 *            the nmDescription to set
	 */
	public void setNmDescription(String nmDescription) {
		this.nmDescription = nmDescription;
	}

	/**
	 * Gets the trustManagerUrl
	 * 
	 * @return the trustManagerUrl
	 */
	public String getTrustManagerUrl() {
		return trustManagerUrl;
	}

	/**
	 * Sets the trustManagerUrl
	 * 
	 * @param trustManagerUrl
	 *            the trustManagerUrl to set
	 */
	public void setTrustManagerUrl(String trustManagerUrl) {
		this.trustManagerUrl = trustManagerUrl;
	}

	/**
	 * Gets the cryptoManagerUrl
	 * 
	 * @return the cryptoManagerUrl
	 */
	public String getCryptoManagerUrl() {
		return cryptoManagerUrl;
	}

	/**
	 * Sets the cryptoManagerUrl
	 * 
	 * @param cryptoManagerUrl
	 *            the cryptoManagerUrl to set
	 */
	public void setCryptoManagerUrl(String cryptoManagerUrl) {
		this.cryptoManagerUrl = cryptoManagerUrl;
	}

}
