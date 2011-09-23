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
package eu.linksmart.policy.pep.response.bundle.impl;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Map.Entry;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.sun.xacml.Obligation;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.ResponseCtx;

import eu.linksmart.policy.pep.impl.PepXacmlConstants;
import eu.linksmart.policy.pep.request.impl.PepRequest;
import eu.linksmart.policy.pep.response.PepObligationObserver;

/**
 * <p>Mail sending {@link PepObligationObserver} implementation</p>
 * 
 * @author Marco Tiemann
 *
 */
public class SendMailPepObligationObserver implements PepObligationObserver {

	/** logger */
	static final Logger logger 
			= Logger.getLogger(SendMailPepObligationObserver.class);
	
	/** flag indicating whether to use debug mode */
	boolean debugMode = false;
	
	/** SMTP host */
	String smtpHost = "smtp.reading.ac.uk";

	/** port */
	String port = "25";
	
	/** flag indicating whether authentication is required */
	boolean requiresAuth = true;
	
	/** flag indicating whether TLS is required */
	boolean requiresTLS = true;
	
	/** flag indicating whether SSL is required */
	boolean requiresSSL = false;
	
	/** user name */
	String username = "userName";
		
	/** password */
	String password = "password";
	
	/** flag indicating whether to deliver asynchronously */
	boolean deliverAsynch = true;
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.response.PepObligationObserver#evaluate(
	 * 		com.sun.xacml.Obligation, 
	 * 		eu.linksmart.policy.pep.request.PepRequest, 
	 * 		com.sun.xacml.ctx.ResponseCtx)
	 */
	@Override
	public boolean evaluate(Obligation theObligation, PepRequest theRequest,
			ResponseCtx theResponse) {
		if (PepXacmlConstants.OBLIGATION_SEND_MESSAGE.getUrn()
				.equalsIgnoreCase(theObligation.getId().toString())) {
			String sender = null;
			String receiver = null;
			String subject = null;
			String message = null;		
			for (Object obj : theObligation.getAssignments()) {
				Attribute attr = (Attribute) obj;
				if (PepXacmlConstants.OBLIGATION_SEND_MESSAGE_FROM.getUrn()
						.equalsIgnoreCase(attr.getId().toString())) {
					logger.info("Mail from: " + attr.getValue().encode());
					sender = attr.getValue().encode();
				}
				if (PepXacmlConstants.OBLIGATION_SEND_MESSAGE_TO.getUrn()
						.equalsIgnoreCase(attr.getId().toString())) {
					logger.info("Mail to: " + attr.getValue().encode());
					receiver = attr.getValue().encode();
				}
				if (PepXacmlConstants.OBLIGATION_SEND_MESSAGE_SUBJECT.getUrn()
						.equalsIgnoreCase(attr.getId().toString())) {
					logger.info("Mail subject: " + attr.getValue().encode());
					subject = attr.getValue().encode();
				}
				if (PepXacmlConstants.OBLIGATION_SEND_MESSAGE_MESSAGE.getUrn()
						.equalsIgnoreCase(attr.getId().toString())) {
					logger.info("Mail message: " + attr.getValue().encode());
					message = attr.getValue().encode();
				}				
			}
			if ((sender != null) && (receiver != null) && (subject != null) 
					&& (message != null)) {
				if (deliverAsynch) {
					Thread thread = new Thread(new MailRunner(sender, receiver, 
							subject, message));
					thread.start();
				} else {
					return new MailTask(sender, receiver, subject, message)
							.deliver();
				}
			}
		}		
		return false;
	}

	/**
	 * @param theUpdates
	 * 				the configuration updates
	 */
	@SuppressWarnings("unchecked")
	public void applyConfigurations(Hashtable theUpdates) {
		for (Object obj : theUpdates.entrySet()) {
			Entry<String, String> ntr = (Entry) obj;
			String key = ntr.getKey();
			String val = ntr.getValue();
			if (SendMailPepObligationObserverConfigurator.DEBUG_MODE
					.equals(key)) {
				debugMode = Boolean.parseBoolean(val);
			} else if (SendMailPepObligationObserverConfigurator.SMTP_HOST
					.equals(key)) {
				smtpHost = val;
			} else if (SendMailPepObligationObserverConfigurator.SMTP_PORT
					.equals(key)) {
				port = val;
			} else if (SendMailPepObligationObserverConfigurator.AUTH_RQRD
					.equals(key)) {
				requiresAuth = Boolean.parseBoolean(val);
			} else if (SendMailPepObligationObserverConfigurator.TLS
					.equals(key)) {
				requiresTLS = Boolean.parseBoolean(val);
			} else if (SendMailPepObligationObserverConfigurator.USER_NAME
					.equals(key)) {
				username = val;
			} else if (SendMailPepObligationObserverConfigurator.USER_PASS
					.equals(key)) {
				password = val;
			} else if (SendMailPepObligationObserverConfigurator.DELIVER_ASYNCH
					.equals(key)) {
				deliverAsynch = Boolean.parseBoolean(val);
			} else if (SendMailPepObligationObserverConfigurator.SSL
					.equals(key)) {
				requiresSSL = Boolean.parseBoolean(val);
			}
		}
	}
	
	
	/**
	 * <p>Authenticator</p>
	 * 
	 * @author Marco Tiemann
	 *
	 */
	private class Authenticator extends javax.mail.Authenticator {
		
		/** {@link PasswordAuthentication} */
		private PasswordAuthentication auth = null;

		/** No-args constructor */
		public Authenticator() {
			auth = new PasswordAuthentication(username, password);
		}

		/* (non-Javadoc)
		 * @see javax.mail.Authenticator#getPasswordAuthentication()
		 */
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return auth;
		}
		
	}
	
	
	/**
	 * <p>Task that sends a mail</p>
	 * 
	 * @author Marco Tiemann
	 *
	 */
	class MailTask {
		
		/** sender email */
		private String sender = null;
		
		/** receiver email */
		private String receiver = null;
		
		/** subject */
		private String subject = null;
		
		/** message */
		private String message = null;
		
		/** result */
		private boolean result = false;
		
		/**
		 * Constructor
		 * 
		 * @param theSender
		 * 				the sender
		 * @param theReceiver
		 * 				the receiver
		 * @param theSubject
		 * 				the subject
		 * @param theMessage
		 * 				the message
		 */
		public MailTask(String theSender, String theReceiver, String theSubject, 
				String theMessage) {
			sender = theSender;
			receiver = theReceiver;
			subject = theSubject;
			message = theMessage;
		}
		
		/**
		 * Attempts to deliver the message
		 * 
		 * @return
		 * 				success flag
		 */
		public boolean deliver() {
			result = false;
		    Properties props = new Properties();
		    props.put("mail.transport.protocol", "smtp");
		    props.put("mail.smtp.host", smtpHost);
		    props.put("mail.smtp.auth", Boolean.toString(requiresAuth));
		    props.put("mail.smtp.starttls.enable", 
		    		Boolean.toString(requiresTLS));
	        props.put("mail.smtp.socketFactory.port", port);
	        if (requiresSSL) {
		        props.put("mail.smtp.socketFactory.class", 
		        		"javax.net.ssl.SSLSocketFactory");
		        props.put("mail.smtp.socketFactory.fallback", "false");
	        }
		    Authenticator auth = new Authenticator();
		    props.setProperty("mail.smtp.submitter", 
		    		auth.getPasswordAuthentication().getUserName());
		    Session session = Session.getDefaultInstance(props, auth);
		    session.setDebug(debugMode);
		    Message msg = new MimeMessage(session);
		    InternetAddress from;
			try {
				from = new InternetAddress(sender); 
				msg.setFrom(from);
				InternetAddress[] to = new InternetAddress[1]; 
				to[0] = new InternetAddress(receiver);
				msg.setRecipients(Message.RecipientType.TO, to);
				msg.setSubject(subject);
				msg.setContent(message, "text/plain");
				Transport.send(msg);
				result = true;
			} catch (Exception e) {
				logger.error("Exception: " + e.getLocalizedMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Stack trace: ", e);
				}
			}
			return result;
		}
		
		/**
		 * @return
		 * 				the result flag
		 */
		public boolean getResult() {
			return result;
		}
		
	}
	
	
	/**
	 * <p>Mail sending {@link Runnable}</p>
	 * 
	 * @author Marco Tiemann
	 *
	 */
	class MailRunner implements Runnable {

		/** {@link MailTask} */
		private MailTask task = null;
		
		/**
		 * Constructor
		 * 
		 * @param theSender
		 * 				the sender
		 * @param theReceiver
		 * 				the receiver
		 * @param theSubject
		 * 				the subject
		 * @param theMessage
		 * 				the message
		 */
		public MailRunner(String theSender, String theReceiver, 
				String theSubject, String theMessage) {
			task = new MailTask(theSender, theReceiver, theSubject, theMessage);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			task.deliver();
		}
		
	}

}
