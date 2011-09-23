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
 * The axis handler that applies Core LinkSmart security to incoming and 
 * outgoing messages.
 * 
 * @author Julian Schuette
 */
public class CoreSecurityRequestHandler extends BasicHandler {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = 
		Logger.getLogger(CoreSecurityRequestHandler.class.getName());
	
	public static SecurityLibrary securityLib;
	public static final String COREPROTECTION_STRING = "coreProtection";
	public static final String YES_STRING = "yes";
	public static final String UTF8_STRING = "UTF-8";
	
	// Pre-load the security library
	static {
		try {
			logger.debug("Initialising Core Security Request Handler");
			if (Activator.configuration!=null) 
				securityLib = new SecurityLibraryImpl(
					Short.parseShort((String) Activator.configuration.get(
						WSProviderConfigurator.CORE_SECURITY_CONFIG)));
			else {
				securityLib = new SecurityLibraryImpl(SecurityLibrary.CONF_ENC);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Constructor
	 */
	public CoreSecurityRequestHandler() {
		super();
	}

	/**
	 * Invoke
	 * @param msgContext the context message
	 */
	public void invoke(MessageContext msgContext) throws AxisFault {
		try {
			// Remember this call
			msgContext.setMaintainSession(true);
			logger.debug("===== Start trace message in request handler =====");

			// Workaround for the "empty targetService" problem in Axis:
			msgContext = AxisWorkarounds.fixTargetServiceSpec(msgContext);

			// Get the message content to protect
			Message msg = msgContext.getCurrentMessage();

			// Get the name of the invoked service
			String serviceName = msgContext.getTargetService();

			if (msgContext.isClient()) {
				if (securityLib.getConfig() != securityLib.CONF_NULL) {
					String protectedBody = 
						securityLib.protectCoreMessage(msg.getSOAPBody().toString());

					// Convert string result to XML document
					javax.xml.parsers.DocumentBuilderFactory dbf = 
						javax.xml.parsers.DocumentBuilderFactory.newInstance();
					dbf.setNamespaceAware(securityLib.getConfig() 
						== SecurityLibrary.CONF_ENC_SIG);
					javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
					ByteArrayInputStream bis = new ByteArrayInputStream(
						protectedBody.getBytes());
					logger.debug("Protecting the outgoing core request: "
						+ msg.getSOAPEnvelope().getAsString());
					Document document = db.parse(bis);
					msg.getSOAPBody().removeContents();
					msg.getSOAPBody().addDocument(document);
					msgContext.setCurrentMessage(msg);
					msgContext.setProperty(COREPROTECTION_STRING, YES_STRING);
					logger.info("Protected the outgoing core request to " + serviceName);
				}
				
				logger.debug("Outgoing message body is " 
					+ msgContext.getCurrentMessage().getSOAPEnvelope().getBody() + "\n");
			}
			else {
				logger.debug("Server request handler for call to service " 
					+ msgContext.getTargetService());

				String textContent = msg.getSOAPBody().getFirstChild().toString();
				if (securityLib.isValidCoreMessage(textContent) 
						|| securityLib.isValidCoreSigMessage(textContent)) {
					
					// Retrieve plain text from the message
					String unprotectedContent = 
						securityLib.unprotectCoreMessage(textContent);
					javax.xml.parsers.DocumentBuilderFactory dbf = 
						javax.xml.parsers.DocumentBuilderFactory.newInstance();
					dbf.setNamespaceAware(securityLib.getConfig()== SecurityLibrary.CONF_ENC);
					javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
					ByteArrayInputStream bis = new ByteArrayInputStream(
						unprotectedContent.getBytes(UTF8_STRING));
					Document document = db.parse(bis);

					// Remove old body
					msgContext.getCurrentMessage().getSOAPEnvelope().getBody().detachNode();
					SOAPBody newBody = msgContext.getCurrentMessage().getSOAPEnvelope().addBody();

					Document xmlDocument2 = db.newDocument();
					xmlDocument2.appendChild(xmlDocument2.importNode(
						document.getDocumentElement().getElementsByTagName("*").item(0), true));

					newBody.addNamespaceDeclaration("soapenv", 
						"http://schemas.xmlsoap.org/soap/envelope/");
					newBody.addDocument(xmlDocument2);

					// Remember that the requester is able to speak "Core LinkSmart security"
					msgContext.setProperty(COREPROTECTION_STRING, YES_STRING);

					// getSOAPPartAsString has to be called here to avoid an
					// Exception. This is an Axis bug.
					logger.info("Decrypted incoming request message: " 
						+ msgContext.getCurrentMessage().getSOAPPartAsString());
				}
				else {
					// The request was in plain text, so remember to answer in
					// plain text as well.
					msgContext.setProperty(COREPROTECTION_STRING, "no");
					logger.debug("Is not a valid protected request message. I'll leave it as it is.");
				}
			}

			logger.debug("===== Stop trace message in request handler =====");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Undo
	 * @param msgContext the message context
	 */
	public void undo(MessageContext msgContext) { }
	
}
