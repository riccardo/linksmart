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
package eu.linksmart.policy.pep.request.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.ParsingException;
import org.wso2.balana.UnknownIdentifierException;
import org.wso2.balana.XACMLConstants;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.xacml3.Attributes;
import org.xml.sax.SAXException;

import eu.linksmart.policy.LinkSmartXacmlConstants;

/**
 * <p>Extracts {@link Attribute}s from a SOAP message with a DOM parser</p>
 * 
 * @author Marco Tiemann
 *
 */
public class DomSoapAttrParser extends AbstractSoapAttrParser {

	/** logger */
	private static final Logger logger 
			= Logger.getLogger(DomSoapAttrParser.class); 	
	
	/** {@link DocumentBuilder} */
	private DocumentBuilder xmlDocumentBuilder = null;
	
	/** No-args constructor */
	public DomSoapAttrParser() {
		super();
		try {
			xmlDocumentBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			logger.error("Could not instantiate XML doc builder: " 
					+ pce.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", pce);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.SoapAttrParser#extractActionsFromSoapMsg(
	 * 		java.lang.String)
	 */
	@Override
	public Attributes extractActionsFromSoapMsg(String theSoapMsg) {
		HashSet<Attribute> attrs = new HashSet<Attribute>();
		try {
			int xmlIdx = theSoapMsg.indexOf("<?xml");
			String xmlPart;
			if (xmlIdx >= 0) {
				xmlPart = theSoapMsg.substring(xmlIdx);
			} else {
				/* does not contain xml start tag */
				xmlPart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" 
						 + theSoapMsg.substring(theSoapMsg.indexOf("<"));
			}
			Document doc = xmlDocumentBuilder.parse(
					new ByteArrayInputStream(xmlPart.getBytes("UTF-8")));
			Node firstChild = null;
			if ((doc == null) || ((firstChild = doc.getFirstChild()) == null)) {
				return null;
			}
			/* skip through to first after body */
			// skip past envelope
			NodeList mother	= null;
			int firstChildNodeCount = firstChild.getChildNodes().getLength();
			if (firstChildNodeCount == 1) {
				mother = firstChild.getFirstChild().getChildNodes();
			} else if (firstChildNodeCount == 2) {
				mother = firstChild.getChildNodes().item(1).getChildNodes();
			} else {
				// do not process
				return null;
			}
			// extract method call
			int ml = mother.getLength();
			for (int i=0; i < ml; i++) {
				Node aName = mother.item(i);
				String name = aName.getLocalName();
				if (name == null) {
					name = aName.getNodeName();
					if (name.contains(":")) {
						name = name.split(":")[1];
					}
				}
				attrs.add(new Attribute(
						URI.create(LinkSmartXacmlConstants.ACTION_ACTION_ID.getUrn()), 
						null, new DateTimeAttribute(), 
						new StringAttribute(name),
						XACMLConstants.XACML_VERSION_2_0));
				// extract method arguments
				NodeList childNodes = aName.getChildNodes();
				int cArg = 0;
				int cl = childNodes.getLength();
				for (int j=0; j < cl; j++) {
					Node nd = childNodes.item(j);
					NamedNodeMap nnm = nd.getAttributes();
					String tp = null;
					int nnml = nnm.getLength();
					for (int k=0; k < nnml; k++) {
						Node an = nnm.item(k);
						// search for XML type keys
						int p = Arrays.binarySearch(xmlTypeKeys, 
								an.getNodeName());
						if (p >= 0) {
							tp = an.getNodeValue().split(":")[1];
						}
					}
					AttributeValue av = null;
					try {
						if (tp != null) {
							av = xacmlAttrFactory.createValue(
									new URI(tp), nd.getTextContent());
						}
					} catch (UnknownIdentifierException uie) {
						logger.error("Unknown identifier used: "
								+ uie.getLocalizedMessage());
						if (logger.isDebugEnabled()) {
							logger.debug("Stack trace: ", uie);
						}
					} catch (ParsingException pe) {
						logger.error("Parsing exception occured: "
								+ pe.getLocalizedMessage());
						if (logger.isDebugEnabled()) {
							logger.debug("Stack trace: ", pe);
						}
					} catch (URISyntaxException use) {
						logger.error("URI syntax exception occured: "
								+ use.getLocalizedMessage());
						if (logger.isDebugEnabled()) {
							logger.debug("Stack trace: ", use);
						}
					}
					String lName = nd.getLocalName();
					if (lName == null) {
						lName = nd.getNodeName();
						if (lName.contains(":")) {
							lName = lName.split(":")[1];
						}
					}
					lName = lName.toLowerCase();
					String oName = null;
					if (!lName.startsWith("arg")) {
						oName = new String(lName);
						lName = "arg" + cArg;
						cArg++;						
					}
					if (av == null) {
						String ct = null;
						for (int k=0; k < nd.getChildNodes().getLength(); k++) {
							Node cNd = nd.getChildNodes().item(k);
							if (cNd.getChildNodes().getLength() > 0) {
								ct = extrNodeToString(nd);
								if ((oName != null) 
										&& (ct.contains("<" + oName + ">")) 
										&& (ct.contains("</" + oName + ">"))) {
									ct = ct.replace("<" + oName + ">", 
											"<" + lName + ">");
									ct = ct.replace("</" + oName + ">", 
											"</" + lName + ">");
								}
								break;
							}
						}
						if (ct == null) {
							ct = nd.getTextContent();
						}
						av = new StringAttribute(ct);
					}
					attrs.add(new Attribute(URI.create(
							LinkSmartXacmlConstants.ACTION_LINK_SMART_PREFIX.getUrn() 
									+ lName),
									null, new DateTimeAttribute(), av,
									XACMLConstants.XACML_VERSION_2_0));
				}
			}
		} catch (UnsupportedEncodingException uee) {
			logger.error("Unsupported encoding: " + uee.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", uee);
			}
		} catch (IOException ioe) {
			logger.error("I/O exception occured: " + ioe.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", ioe);
			}
		} catch (SAXException sae) {
			logger.error("SAX parser exception: " + sae.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", sae);
			}
		}
		
		Attributes attrAction = new Attributes(URI.create(XACMLConstants.ACTION_CATEGORY), attrs);
		return attrAction;
	}
	
	/**
	 * @param theNode
	 * 				the {@link Node}
	 * @return
	 * 				the <code>String</code> representation of the {@link Node}
	 */
	private String extrNodeToString(Node theNode) {
		StringWriter writer = new StringWriter();
		try {
			Transformer transformer 
					= TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(
					OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(new DOMSource(theNode), 
					new StreamResult(writer));
		} catch (TransformerException te) {
			logger.error("Exception transforming node to string: "
					+ te.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", te);
			}
		}
		return "<![CDATA[" + writer.toString() + "]]>";
	}

}
