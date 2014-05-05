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
package eu.linksmart.policy.pep.response.impl;

import org.apache.log4j.Logger;

import com.sun.xacml.Obligation;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.ResponseCtx;

import eu.linksmart.policy.pep.cache.impl.PdpSessionCache;
import eu.linksmart.policy.pep.impl.PepXacmlConstants;
import eu.linksmart.policy.pep.request.impl.PepRequest;
import eu.linksmart.policy.pep.response.PepObligationObserver;
import eu.linksmart.policy.pep.response.impl.SessionCachePepObligationObserver;

/**
 * <p>Session cache {@link PepObligationObserver} implementation</p>
 * 
 * <p>Adds a {@link ResponseCtx} to the PEP response cache if an 
 * {@link Obligation} requiring response caching is given as argument.</p>.
 * 
 * <p>This {@link PepObligationObserver} is part of the default LinkSmart 
 * @{link PepApplication} and always enabled.</p>
 * 
 * @author Marco Tiemann
 *
 */
public class SessionCachePepObligationObserver 
		implements PepObligationObserver {

	/** logger */
	private static final Logger logger
			= Logger.getLogger(SessionCachePepObligationObserver.class);
	
	/** {@link PdpSessionCache} */
	private PdpSessionCache cache = null;
	
	/** flag indicating whether to use session caching */
	private boolean usePdpSessionCache = false;
	
	/**
	 * Constructor
	 * 
	 * @param theUsePdpSessionCache
	 * 				a flag indicating whether to use session caching
	 * @param theCache
	 * 				the {@link PdpSessionCache}
	 */
	public SessionCachePepObligationObserver(boolean theUsePdpSessionCache,
			PdpSessionCache theCache) {
		super();
		usePdpSessionCache = theUsePdpSessionCache;
		cache = theCache;
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.ObligationObserver#evaluate(
	 * 		com.sun.xacml.Obligation, eu.linksmart.policy.pep.PepRequest,
	 * 		com.sun.xacml.ctx.ResponseCtx)
	 */
	@Override
	public boolean evaluate(Obligation theObligation, PepRequest theRequest,
			ResponseCtx theResponse) {
		if (PepXacmlConstants.OBLIGATION_CACHE.getUrn()
				.equalsIgnoreCase(theObligation.getId().toString())) {
			/*
			 *  only evaluate if we actually want to do caching and can 
			 *  do it
			 */
			if ((usePdpSessionCache) && (cache != null)) {
				for (Object attrObj : theObligation.getAssignments()) {
					Attribute attr = (Attribute) attrObj;
					if (PepXacmlConstants.OBLIGATION_CACHE_LIFETIME
							.getUrn().toLowerCase().equals(
									attr.getId().toString())) {
						long time = 0L;
						try {
							String av = attr.getValue().toString();
							if (av.contains(":")) {
								av = av.split(":")[1].replaceAll("\"", "")
										.trim();
							}
							time = convertStringToTimestamp(av);
							cache.add(
									theRequest.getSessionId(),
									theRequest.getSndHid(),
									theRequest.getRecHid(),
									theRequest.getActionAttrString(),
									theResponse,
									System.currentTimeMillis(),
									time);
							return true;
						} catch (NumberFormatException nfe) {
							logger.info("Error converting obligation "
									+ "time to timestamp: "	
									+ nfe.getLocalizedMessage());
							if (logger.isDebugEnabled()) {
								logger.debug("Stack trace: ", nfe);
							}
							return false;
						}
					}
				}
			}
			/* ignore if caching has not been enabled */
			return true;
		}
		return false;
	}
	
	/**
	 * Converts time value with unit qualifier to millisecond time value
	 * 
	 * @param theTime
	 * 				<code>String</code> time value
	 * @return
	 * 				<code>long</code> millisecond time value
	 * @throws NumberFormatException
	 * 				if <code>theTime</code> cannot be converted
	 */
	private long convertStringToTimestamp(String theTime) 
			throws NumberFormatException {
		String strTime = theTime.toLowerCase();
		if (strTime.endsWith("d")) {
			return Long.parseLong(strTime.substring(0, strTime.length() - 1))
					* 24 * 60 * 60 * 1000;
		} else if (strTime.endsWith("h")) {
			return Long.parseLong(strTime.substring(0, strTime.length() - 1))
					* 60 * 60 * 1000;
		} else if (strTime.endsWith("m")) {
			return Long.parseLong(strTime.substring(0, strTime.length() - 1))
					* 60 * 1000;
		} else if (strTime.endsWith("ms")) {
			return Long.parseLong(strTime.substring(0, strTime.length() - 2));
		} else if (strTime.endsWith("s")) {
			return Long.parseLong(strTime.substring(0, strTime.length() - 1))
					* 1000;
		} else {
			return Long.parseLong(strTime);
		}
	}

}
