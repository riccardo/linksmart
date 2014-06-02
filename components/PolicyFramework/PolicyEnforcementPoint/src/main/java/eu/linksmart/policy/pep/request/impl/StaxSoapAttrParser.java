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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.log4j.Logger;
import org.wso2.balana.ParsingException;
import org.wso2.balana.UnknownIdentifierException;
import org.wso2.balana.XACMLConstants;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.xacml3.Attributes;

import eu.linksmart.policy.LinkSmartXacmlConstants;

/**
 * <p>Extracts {@link Attribute}s from a SOAP message with a STAX parser</p>
 * 
 * @author Marco Tiemann
 *
 */
public class StaxSoapAttrParser extends AbstractSoapAttrParser {

	/** logger */
	private static final Logger logger 
	= Logger.getLogger(StaxSoapAttrParser.class); 

	/** SOAP body tag */
	private static final String SOAP_BODY = "body";

	/** STAX {@link XMLInputFactory} */
	private XMLInputFactory staxFactory = null;	

	/** No-args constructor */
	public StaxSoapAttrParser() {
		super();
		staxFactory = XMLInputFactory.newInstance();
		staxFactory.setProperty("javax.xml.stream.isValidating", 
				new Boolean(false));
		staxFactory.setProperty("javax.xml.stream.isNamespaceAware", 
				new Boolean(false));
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.SoapAttrParser#extractActionsFromSoapMsg(
	 * 		java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Attributes extractActionsFromSoapMsg(String theSoapMsg) {
		HashSet<Attribute> attrs = new HashSet<Attribute>();
		int xmlIdx = theSoapMsg.indexOf("<?xml");
		String xmlPart;
		if (xmlIdx >= 0) {
			xmlPart = theSoapMsg.substring(xmlIdx);
		} else {
			/* does not contain xml start tag */
			xmlPart = theSoapMsg.substring(theSoapMsg.indexOf("<"));
		}
		Pattern pattern = Pattern.compile(":");
		try {
			XMLEventReader staxReader = staxFactory.createXMLEventReader(
					new ByteArrayInputStream(xmlPart.getBytes("UTF-8")));
			// skip through to SOAP body contents
			while (staxReader.hasNext()) {
				XMLEvent ev = staxReader.nextEvent();
				if (ev.isStartElement()) {
					String[] stQn = pattern.split(ev.asStartElement().getName()
							.getLocalPart());
					// we only check for the first occurrence of the body tag
					if (SOAP_BODY.equalsIgnoreCase(stQn[1])) {
						break;
					}
				}
			}
			boolean mStartFound = false;
			QName mQName = null;
			boolean aStartFound = false;
			QName aQName = null;
			String aData = "";
			boolean aDataIsComplex = false;
			int cArgs = 0;
			String attrType = null;
			while (staxReader.hasNext()) {
				XMLEvent ev = staxReader.nextEvent();
				if (!mStartFound) {			
					if (ev.isStartElement()) {
						StartElement stEl = ev.asStartElement();
						QName qName = stEl.getName();
						String sQName = qName.getLocalPart();
						if (sQName.contains(":")) {
							sQName = pattern.split(sQName)[1];
						}
						mStartFound = true;
						mQName = qName;
						attrs.add(new Attribute(
								URI.create(LinkSmartXacmlConstants.ACTION_ACTION_ID
										.getUrn()), 
										null, 
										new DateTimeAttribute(), 
										new StringAttribute(sQName),
										true,
										XACMLConstants.XACML_VERSION_3_0));					
					}
					// else do nothing
				} else {
					if (ev.isStartElement()) {
						if (!aStartFound) {					
							StartElement stEl = ev.asStartElement();
							QName qName = stEl.getName();
							aStartFound = true;
							aQName = qName;
							cArgs++;
							Iterator<javax.xml.stream.events.Attribute> atIt 
							= stEl.getAttributes();
							while (atIt.hasNext()) {
								javax.xml.stream.events.Attribute attr 
								= atIt.next();
								int p = Arrays.binarySearch(xmlTypeKeys, 
										attr.toString().split("=")[0]);
								if (p >= 0) {
									attrType = pattern.split(attr.getValue())[1];
								}
							}					
						} else {
							aDataIsComplex = true;
							aData += ev.toString();
						}
					} else if (ev.isEndElement()) {
						EndElement enEl = ev.asEndElement();
						QName enQn = enEl.getName();
						String enLc = enQn.getLocalPart();
						if (enLc.contains(":")) {
							String[] stQn = pattern.split(enLc);
							/* 
							 * we only check for the first occurrence of the 
							 * body tag
							 */
							if (SOAP_BODY.equalsIgnoreCase(stQn[1])) {
								break;
							}
						}
						if (enQn.equals(mQName)) {
							mStartFound = false;
							mQName = null;
							aData = "";
						}
						if ((enQn.equals(aQName)) && (aQName != null)) {
							String aName = aQName.getLocalPart();
							if (aName.contains(":")) {
								aName = pattern.split(aName)[1];
							}
							if (!aName.toLowerCase().startsWith("arg")) {
								aName = "arg" + (cArgs - 1);
							}
							if (aDataIsComplex) {
								aData = "<![CDATA[<" + aName + ">" + aData 
										+ "</" + aName + ">]]>";
							}
							AttributeValue av = null;
							if (attrType != null) {
								try {
									av = xacmlAttrFactory.createValue(
											new URI(attrType), aData);
								} catch (UnknownIdentifierException uie) {
									logger.error("Unknown identifier: " 
											+ uie.getLocalizedMessage());
									if (logger.isDebugEnabled()) {
										logger.debug("Stack trace: ", uie);
									}
								} catch (ParsingException pe) {
									logger.error("Parsing exception: " 
											+ pe.getLocalizedMessage());
									if (logger.isDebugEnabled()) {
										logger.debug("Stack trace: ", pe);
									}
								} catch (URISyntaxException use) {
									logger.error("URI syntax exception: " 
											+ use.getLocalizedMessage());
									if (logger.isDebugEnabled()) {
										logger.debug("Stack trace: ", use);
									}
								}
							} else {
								av = new StringAttribute(aData);
							}
							attrs.add(new Attribute(URI.create(
									LinkSmartXacmlConstants.ACTION_LINK_SMART_PREFIX
									.getUrn() + aName),
									null, 
									new DateTimeAttribute(), 
									av,
									true,
									XACMLConstants.XACML_VERSION_3_0));
							aStartFound = false;
							aQName = null;
							aDataIsComplex = false;
							attrType = null;
							aData = "";
						} else {
							aData += ev.toString();
						}
					} else {
						aData += ev.toString();
					}
				}
			}
			staxReader.close();
		} catch (UnsupportedEncodingException uee) {
			logger.error("Unsupported XML encoding " 
					+ uee.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", uee);
			}
		} catch (XMLStreamException xse) {
			logger.error("XML stream exception: " 
					+ xse.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", xse);
			}
		}
		Attributes attrAction = new Attributes(URI.create(XACMLConstants.ACTION_CATEGORY), attrs);
		return attrAction;
	}

}
