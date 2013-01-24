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
 * Copyright (C) 2006-2010 []
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

package eu.linksmart.security.axis;

import java.io.ByteArrayInputStream;

import javax.xml.soap.SOAPBody;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import eu.linksmart.security.communication.core.SecurityLibrary;
import eu.linksmart.security.communication.core.impl.SecurityLibraryImpl;
import eu.linksmart.wsprovider.impl.Activator;
import eu.linksmart.wsprovider.impl.WSProviderConfigurator;

/**
 * Create an axis handler to output the contents of the message to the stdout
 * 
 * @author Julian Schuette
 */
public class CoreSecurityResponseHandler extends BasicHandler {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = 
		Logger.getLogger(CoreSecurityResponseHandler.class.getName());

	private static final String CORE_PROTECTION_LABEL = "coreProtection";
	
	public static SecurityLibrary securityLib;

	static {
		try {
			logger.debug("Initialising Core Security Response Handler");
			if (Activator.configuration!=null) { 
				securityLib = new SecurityLibraryImpl(
					Short.parseShort((String) Activator.configuration.get(
						WSProviderConfigurator.CORE_SECURITY_CONFIG)));
			}
			else {
				securityLib = new SecurityLibraryImpl(SecurityLibrary.CONF_ENC);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Invoke
	 * @param msgContext the context message
	 */
	public void invoke(MessageContext msgContext) throws AxisFault {
		try {
			logger.debug("===== Start trace in response handler message =====");
			msgContext = AxisWorkarounds.fixTargetServiceSpec(msgContext);
			Message msg = msgContext.getCurrentMessage();

			if (msgContext.isClient()) {
				logger.debug("Client response handler for call to service "
					+ msgContext.getTargetService());
				
				String textContent = msg.getSOAPBody().getFirstChild().toString();
				if (securityLib.isValidCoreMessage(textContent)
						|| securityLib.isValidCoreSigMessage(textContent)) {

					// Retrieve plain text from the message
					String unprotectedContent = 
						securityLib.unprotectCoreMessage(textContent);
					javax.xml.parsers.DocumentBuilderFactory dbf = 
						javax.xml.parsers.DocumentBuilderFactory.newInstance();
					dbf.setNamespaceAware(securityLib.getConfig() == SecurityLibrary.CONF_ENC);
					javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
					ByteArrayInputStream bis = new ByteArrayInputStream(
						unprotectedContent.getBytes("UTF-8"));
					Document document = db.parse(bis);
					
					// Remove old body
					msgContext.getCurrentMessage().getSOAPEnvelope().getBody().detachNode();
					SOAPBody newBody = msgContext.getCurrentMessage().getSOAPEnvelope().addBody();
					Document xmlDocument2 = db.newDocument();
					xmlDocument2.appendChild(xmlDocument2.importNode(
						document.getDocumentElement().getElementsByTagName("*").item(0), true));
					
					newBody.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
					newBody.addDocument(xmlDocument2);
					
					// getSOAPPartAsString has to be called here to avoid an 
					// Exception. This is an Axis bug.
					logger.debug("Decrypted incoming response message: "
						+ msgContext.getCurrentMessage().getSOAPPartAsString());
				}
				else {
					// The request was in plain text, so remember to answer in
					// plain text as well.
					msgContext.setProperty(CORE_PROTECTION_LABEL, "no");
					logger.debug("Response is not a valid Core message. I'll "
						+ "leave it as it is. Message body was " 
						+ msgContext.getCurrentMessage().getSOAPEnvelope().getBody());
				}
			}
			else {
				if ((msgContext.getStrProp(CORE_PROTECTION_LABEL) != null) 
						&& msgContext.getStrProp(CORE_PROTECTION_LABEL).equals("yes")) {
					
					String protectedBody = securityLib.protectCoreMessage(msg.getSOAPBody().toString());
					// Convert string result to XML document
					javax.xml.parsers.DocumentBuilderFactory dbf = 
						javax.xml.parsers.DocumentBuilderFactory.newInstance();
					dbf.setNamespaceAware(securityLib.getConfig() == SecurityLibrary.CONF_ENC);
					javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
					ByteArrayInputStream bis = new ByteArrayInputStream(
						protectedBody.getBytes());
					logger.debug("Message to protect: " 
						+ msg.getSOAPEnvelope().getAsString());
					Document document = db.parse(bis);
					msg.getSOAPBody().removeContents();
					msg.getSOAPBody().addDocument(document);
					msgContext.setCurrentMessage(msg);
					msgContext.setProperty(CORE_PROTECTION_LABEL, "yes");
					logger.debug("Protected the outgoing core response " 
						+ msgContext.getCurrentMessage().getSOAPEnvelope().toString());
				}
				else {
					logger.debug("Not protecting an outgoing core response as the request seemed not to be encrypted");
				}
			}
			logger.debug("===== Stop trace in response handler message =====");
		} catch (Exception e) {
			e.printStackTrace();
			throw AxisFault.makeFault(e);
		}
	}

	/**
	 * Undo
	 * @param msgContext the message context
	 */
	public void undo(MessageContext msgContext) { }
	
}
