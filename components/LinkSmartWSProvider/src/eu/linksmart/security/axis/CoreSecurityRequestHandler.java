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
import java.util.Iterator;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.Constants;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import eu.linksmart.security.communication.VerificationFailureException;
import eu.linksmart.security.communication.core.SecurityLibrary;
import eu.linksmart.security.communication.core.impl.SecurityLibraryImpl;
import eu.linksmart.security.communication.utils.CookieProvider;
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

	public static SecurityLibrary securityLib = null;
	public static final String COREPROTECTION_STRING = "coreProtection";
	public static final String YES_STRING = "yes";
	public static final String UTF8_STRING = "UTF-8";

	// Pre-load the security library
	static {
		logger.debug("Initialising Core Security Request Handler");
		securityLib = SecurityLibraryImpl.getInstance();

		if (Activator.configuration!=null) {
			securityLib.setConfiguration(Short.parseShort((String) Activator.configuration.get(
					WSProviderConfigurator.CORE_SECURITY_CONFIG)));	}
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
					if(AxisWorkarounds.isLocalCall(msgContext, true)){
						//create cookie header field
						Name name = msg.getSOAPEnvelope().createName(
								CookieProvider.COOKIE_PROPERTY_NAME, 
								CookieProvider.COOKIE_PREFIX, 
								SecurityLibrary.CORE_SECURITY_NAMESPACE);
						SOAPHeaderElement cookieElement = msg.getSOAPHeader().addHeaderElement(name);
						cookieElement.addTextNode(securityLib.getCookie());
					}else{
						String protectedBody = 
							securityLib.protectCoreMessage(msg.getSOAPBody().toString());

						// Convert string result to XML document
						javax.xml.parsers.DocumentBuilderFactory dbf = 
							javax.xml.parsers.DocumentBuilderFactory.newInstance();
						/*when not using core security there are no elements which are from other namespace
						 * when using core security with signatures than namespaces would destroy the digest value
						 */
						dbf.setNamespaceAware(securityLib.getConfig()== SecurityLibrary.CONF_ENC);
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
						logger.debug("Protected the outgoing core request to " + serviceName);
					}
				}

				logger.debug("Outgoing message body is " 
						+ msgContext.getCurrentMessage().getSOAPEnvelope().getBody() + "\n");
			}
			else {
				logger.debug("Server request handler for call to service " 
						+ msgContext.getTargetService());

				String textContent = msg.getSOAPBody().getFirstChild().toString();

				//check wether message has to be opened or dropped
				boolean msgEnc = securityLib.isValidCoreMessage(textContent);
				boolean msgMac = securityLib.isValidCoreMacMessage(textContent);
				short secConfig = securityLib.getConfig();
				/* accept if no security required but present
				 * or message has the required amount of security
				 */
				if ( secConfig == SecurityLibrary.CONF_NULL && (msgEnc || msgMac)
						|| secConfig == SecurityLibrary.CONF_ENC && (msgEnc || msgMac)
						|| (secConfig == SecurityLibrary.CONF_ENC_SIG || secConfig == SecurityLibrary.CONF_ENC_SIG_SPORADIC)
						&& msgMac) {

					// Retrieve plain text from the message
					String unprotectedContent = 
						securityLib.unprotectCoreMessage(textContent);
					javax.xml.parsers.DocumentBuilderFactory dbf = 
						javax.xml.parsers.DocumentBuilderFactory.newInstance();
					/*when not using core security there are no elements which are from other namespace
					 * when using core security with signatures than namespaces would destroy the digest value
					 */
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
					logger.debug("Decrypted incoming request message: " 
							+ msgContext.getCurrentMessage().getSOAPPartAsString());
				}
				else if(secConfig != SecurityLibrary.CONF_NULL && AxisWorkarounds.isLocalCall(msgContext, false)){

					SOAPHeader header = msg.getSOAPHeader();
					Iterator<SOAPHeaderElement> hit = (Iterator<SOAPHeaderElement>)header.examineAllHeaderElements();
					boolean valid = false;
					while(hit.hasNext()){
						SOAPHeaderElement el = hit.next();
						if(el.getNodeName().equals(CookieProvider.COOKIE_PREFIX + ":" + CookieProvider.COOKIE_PROPERTY_NAME)){
							if(securityLib.checkCookie(el.getFirstChild().getNodeValue())){
								valid = true;
							}
							break;
						}
					}
					if(!valid){
						//drop message because shows to be localhost but does not have valid cookie
						logger.warn("Received local message with invalid cookie!");
						throw new VerificationFailureException("Not valid message.");
					}
				}
				else if(secConfig != SecurityLibrary.CONF_NULL){
					//drop message because security is required but not provided
					logger.warn("Received not protected message from: " + msgContext.getProperty(Constants.MC_REMOTE_ADDR));
					throw new VerificationFailureException("Not valid message.");
				}
			}

			logger.debug("===== Stop trace message in request handler =====");
		} catch (Exception e) {
			throw AxisFault.makeFault(e);
		}
	}

	/**
	 * Undo
	 * @param msgContext the message context
	 */
	public void undo(MessageContext msgContext) { }

}
