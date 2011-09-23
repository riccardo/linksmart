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
package eu.linksmart.policy.pdp.impl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>Policy handling utility</p>
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 * 
 */
public final class PolicyNormaliser {

	/** Private constructor to prevent instantiation */
	private PolicyNormaliser() {
		super();
	}
	
	/**
	 * Removes white space from policy text content
	 * 
	 * @param thePolicy
	 * 				the {@link Document}, modified during processing
	 */
	public static void normalisePolicy(Document thePolicy) {
		Element docEl = thePolicy.getDocumentElement();
		processNode(docEl);
	}
	
	/**
	 * Recursive white space remover
	 * 
	 * @param theNode
	 * 				the {@link Node}
	 */
	private static void processNode(Node theNode) {
		if ((theNode.getNodeType() == Node.TEXT_NODE) 
				|| (theNode.getNodeType() == Node.CDATA_SECTION_NODE)) {
			theNode.setTextContent(theNode.getTextContent().trim());
		}		
		NodeList children = theNode.getChildNodes();
		int cl = children.getLength();
		for (int i=0; i < cl; i++) {
			processNode(children.item(i));
		}
	}
	
}
