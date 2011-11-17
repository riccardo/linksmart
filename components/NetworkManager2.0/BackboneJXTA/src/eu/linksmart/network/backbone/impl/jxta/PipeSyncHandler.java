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
 * Copyright (C) 2006-2010 [Telefonica I+D]
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

/**
 * The PipeSyncHandler class provides the methods to establish a pipe 
 * communication between Network Managers. The communication is done 
 * through JXTA unidirectional pipes, whereas the NM pipe communication 
 * is synchronous. For this purpose, the PipeSyncHandler class uses a 
 * request/response protocol to establish the bidirectional communication 
 * between NMs.
 * It provides a sendData interface in order to send data from a source HID 
 * to a destine HID:
 * 	sendDataOverPipe(String sessionID, String senderHID, String receiverHID, String data)
 * 
 * @see eu.linksmart.network.backbone.BackboneManagerApplication
 */

package eu.linksmart.network.backbone.impl.jxta;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

import org.apache.log4j.Logger;

import eu.linksmart.network.HID;
import eu.linksmart.network.NMResponse;
//import eu.linksmart.network.identity.HIDManagerApplication;
//import eu.linksmart.network.impl.NetworkManagerApplicationSoapBindingImpl;
//import eu.linksmart.network.impl.NetworkManagerConfigurator;
//import eu.linksmart.network.routing.RouteManagerApplication;
//import eu.linksmart.network.session.Session;
//import eu.linksmart.network.session.SessionManagerApplication;
//import eu.linksmart.security.protocols.net.Consts;
//import eu.linksmart.security.protocols.net.SecureSessionController;
//import eu.linksmart.security.protocols.net.impl.SecureSessionControllerImpl;
//import eu.linksmart.security.protocols.net.transport.Command;
//import eu.linksmart.types.HID;

/**
 * PipeSyncHandler
 */
public class PipeSyncHandler extends Thread implements PipeMsgListener {

	private static Logger logger = Logger.getLogger(PipeSyncHandler.class.getName());

	private static final int MAXNUMRETRIES = 15;
	private static final long OUTPUTPIPE_CREATION_TIMEOUT = 2000;
	public static final String HID_ATTRIBUTE_QUERY_RESPONSE = "1";

//	private NetworkManagerApplicationSoapBindingImpl nm;
//	BackboneManagerApplication backboneMgr;
//	HIDManagerApplication hidMgr;
//	SessionManagerApplication sessionMgr;
//	RouteManagerApplication routeMgr;

	private PipeService pipeService;
	private PipeAdvertisement pipeAdv;
	private InputPipe inputPipe;
	private ConcurrentHashMap<ID, PipeInfo> pipeTable;
	private PipeTableUpdater pipeTableUpdater;
	private String connected = "-1"; 
	public NMResponse response;
	private int count = 0;
	private int ok = 0;
	private Hashtable<Integer, PipeSender> h;
	Integer i = 0;

	private int MAXTIMERESPONSE = 200000;
	private boolean firstStart = true;
	
	private BackboneJXTAImpl bbjxta;

	/**
	 * Constructor
	 * 
	 * @param nm the Network Manager application
	 */
	public PipeSyncHandler(BackboneJXTAImpl bbjxta) {
		this.bbjxta = bbjxta;
//		this.nm = nm;
//
//		backboneMgr = nm.backboneMgr;
//		hidMgr = nm.hidMgr;
//		sessionMgr = nm.sessionMgr;
//		routeMgr = nm.routeMgr;

		this.pipeAdv = createPipeAdv();
		this.pipeService = bbjxta.netPeerGroup.getPipeService();
		this.pipeTable = new ConcurrentHashMap<ID, PipeInfo>();
		this.h = new Hashtable<Integer, PipeSender>();

		try {
			this.inputPipe = pipeService.createInputPipe(pipeAdv, this);
			logger.debug("SyncInputPipe created: " + inputPipe.getPipeID());
			pipeTableUpdater = new PipeTableUpdater();
			pipeTableUpdater.start();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}	
	}

	/**
	 * Creates a Pipe Advertisement
	 * 
	 * @return a pipe advertisement
	 */
	private PipeAdvertisement createPipeAdv() {
		PipeID pipeID = IDFactory.newPipeID(bbjxta.netPeerGroup.getPeerGroupID());
		PipeAdvertisement pipeAdv = (PipeAdvertisement)
		AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
		System.err.println(pipeID.toString());
		String ppid = 
			"urn:jxta:uuid-79B6A084D3264DF8B641867D926C48D9F3C29CB8709F4D7EB39D92335B61D4CE04";

		try {
			pipeID = (PipeID) IDFactory.fromURI(new URI(ppid));
		} catch (URISyntaxException e) {
			logger.error("Imposible to create PipeID for the pipe advertisement");
		}

		pipeAdv.setPipeID(pipeID);
		pipeAdv.setName("Input Pipe Advertisement for Network Manager");
		pipeAdv.setType(PipeService.UnicastType);
		logger.debug("Pipe created: " + pipeID);

		return pipeAdv;
	}

	/**
	 * Private count
	 * @return the counter value
	 */
	private synchronized Integer count() {
		i = i + 1;
		return i;
	}

	/**
	 * Sends a response message
	 * @param sessionID the session ID
	 * @param s the source HID
	 * @param d the destination HID
	 * @param data the data
	 * @param i the index
	 * @return boolean depending on the result
	 */
//	private boolean sendMessageResponse(String sessionID, String s, String d, 
//			String data, String i) {
//
//		HID dest = new HID(d);
//		HID source = new HID(s);
//		PeerID pID = null;
//		int numRetries = 0;
//		OutputPipe outPipe = null;
//
//		while (numRetries < MAXNUMRETRIES) {
//			try {
//				pID = hidMgr.getIDfromHID(dest);
//				if (pID != null) {
//					if (pipeTable.containsKey(pID)) outPipe = 
//						pipeTable.get(pID).getOutputPipe();
//					break;
//				}
//				numRetries++;
//			} catch (NullPointerException e) {
//				logger.debug("Could not find destination HID. Backing off");
//				numRetries++;
//			}
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				logger.error(e.getMessage(), e);
//			}
//		}
//
//		if ((outPipe == null)) {
//			if ((pID != null)) {
//				outPipe = createOutputPipe(pID);
//				logger.debug("The pipe to " + pID.toString()
//						+ " was closed or never created before");
//				Message message = createResponseMessage(sessionID, source, dest, data, i);
//
//				try {
//					outPipe.send(message);
//				} catch (IOException e) {
//					logger.error("Error sending data to HID = " + dest.toString());
//				}
//
//				return true;
//			}
//			else {
//				logger.error("Resp. Could not find destination HID "
//						+ dest.toString() + ". Please try later...");
//				return false;
//			}
//		}
//		else {
//			Message message = createResponseMessage(sessionID, source, dest, data, i);
//
//			try {
//				outPipe.send(message);
//			} catch (IOException e) {
//				logger.error("Error sending data to HID = " + dest.toString());
//			}
//
//			pipeTable.get(pID).setTime(System.currentTimeMillis());
//			return true;	
//		}
//	}

	/**
	 * Sends an asynchronous message
	 * 
	 * @param senderHID the sender LinkSmart ID
	 * @param receiverHID the receiver LinkSmart ID
	 * @param data the data to send
	 * @param messageType the type of the message
	 * @param i unique integer
	 */
	public void sendAsyncMessage(String senderHID, String receiverHID, 
			String data, String messageType, Integer i) {

		HID source = new HID(senderHID);
		HID dest = new HID(receiverHID);
		PeerID pID = null;
		int numRetries = 0;
		OutputPipe outPipe = null;

		while (numRetries < MAXNUMRETRIES) {
			try {
				// TODO: check what this was supposed to be
//				pID = hidMgr.getIDfromHID(dest);

				if (pID != null) {
					if (pipeTable.containsKey(pID)) outPipe = 
						pipeTable.get(pID).getOutputPipe();
					break;
				}
				numRetries++;
			} catch (NullPointerException e) {
				logger.debug("Could not find destination HID. Backing off");
				numRetries++;
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if ((outPipe == null)) {
			if ((pID != null)) {
				outPipe = createOutputPipe(pID);
				logger.debug("The pipe to " + pID.toString()
						+ " was closed or never created before");
				Message message = 
					createAsyncMessage(source, dest, data, i.toString(), messageType);

				try {
					outPipe.send(message);
				} catch (IOException e) {
					logger.error("Error sending async message to HID = " + dest.toString());
				} 
			}
			else {
				logger.error("Async: Could not find destination HID "
						+ dest.toString() +". Please try later...");
			}
		}
		else  {
			Message message = 
				createAsyncMessage(source, dest, data, i.toString(), messageType);

			try {
				outPipe.send(message);
			} catch (IOException e) {
				logger.error("Error sending async message to HID = " + dest.toString());
			}

			pipeTable.get(pID).setTime(System.currentTimeMillis());
		}
	}

	/**
	 * Sends data from a source HID to a destine HID
	 *  
	 * @param sessionID the session ID
	 * @param s the source LinkSmart ID
	 * @param d the destine LinkSmart ID
	 * @param data the data to send
	 * @return the response
	 */
	public NMResponse sendData(String sessionID, String s, String d, String data) {
		Integer i = count();
		PipeSender pipeSender = new PipeSender(i);
		h.put(i, pipeSender);
		long time = System.currentTimeMillis();
		NMResponse res = pipeSender.sendDataOverPipe(sessionID, s, d, data);

//		if (res.getSessionID().equals("0")) {
//			res = pipeSender.getResponse();
//			if (res.getSessionID().equals("0")) {
//				res.setSessionID("-1");
//				res.setData("Max expiration time reached. No response was received"); 
//			}
//		}

		h.remove(i);
		logger.info("Closing " + i + " the size is " + h.size()
				+ " Time: " + (System.currentTimeMillis() - time));
		return res;
	}



	/**
	 * PipeSender class
	 */
	public class PipeSender {
		private NMResponse resp;
		private Integer i;
		private String response;

		/**
		 * Constructor
		 * 
		 * @param i unique integer
		 */
		public PipeSender(Integer i) {
			this.i = i;
			response = new String("-1");
		}

		/**
		 * @param sessionId the session ID
		 * @param data the data to send
		 */
		public void notification(String sessionId, String data) {
			resp.setData(data);
//			resp.setSessionID(sessionId);
			synchronized (response) {
				response.notify();	
			}
		}

		/**
		 * Gets the response
		 * 
		 * @return the response
		 */
		public NMResponse getResponse() {
			synchronized (response) {
				try {
					response.wait(MAXTIMERESPONSE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			logger.debug("Received response in " + i);
			return resp;
		}

		/**
		 * Sends data over a pipe from a source HID to a destine HID
		 *  
		 * @param sessionID the session ID
		 * @param s the source LinkSmart ID
		 * @param d the destine LinkSmart ID
		 * @param data the data to send
		 * @return the response
		 */
		public NMResponse sendDataOverPipe(String sessionID, String s, 
				String d, String data) {

			HID dest = new HID(d);
			HID source = new HID(s);
			PeerID pID = null;
			int numRetries = 0;
			OutputPipe outPipe = null;
			resp = new NMResponse();
			resp.setData("Error in SenderPipeSyncHandler");
//			resp.setSessionID("-1");

			while (numRetries < MAXNUMRETRIES) {
				try {
					// TODO: fix this
//					pID = hidMgr.getIDfromHID(dest);

					if (pID != null) {
						if (pipeTable.containsKey(pID)) outPipe = 
							pipeTable.get(pID).getOutputPipe();
						break;
					}
					numRetries++;
				} catch (NullPointerException e) {
					logger.debug("Could not find destination HID. Backing off");
					numRetries++;
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if ((outPipe == null)) {
				if ((pID != null)) {
					outPipe = createOutputPipe(pID);
					logger.debug("The pipe to " + pID.toString() 
							+ " was closed or never created before");
					Message message = 
						createRequestMessage(sessionID, source, dest, data, i.toString());

					try {
						outPipe.send(message);
//						resp.setSessionID("0");
					} catch (IOException e) {
						logger.error("Error sending data to HID = " + dest.toString());
						resp.setData("Error sending data to HID = "+ dest.toString());
					}

					return resp;
				}
				else {
					logger.error("Sender: Could not find destination HID " 
							+ dest.toString() + ". Please try later...");
					resp.setData("Could not find destination HID " 
							+ dest.toString() + ". Please try later...");
					return resp;
				}
			}
			else  {
				Message message = 
					createRequestMessage(sessionID, source, dest, data, i.toString());

				try {
					outPipe.send(message);
//					resp.setSessionID("0");
				} catch (IOException e) {
					logger.error("Error sending data to HID = " + dest.toString());
					resp.setData("Error sending data to HID = "+ dest.toString());
				}

				pipeTable.get(pID).setTime(System.currentTimeMillis());				
				return resp;
			}
		}

	}



	/**
	 * Creates a request message
	 * @param sessionID the session ID
	 * @param source the source HID
	 * @param dest the destination HID
	 * @param data the data
	 * @param i the index
	 * @return the request message
	 */
	private Message createRequestMessage(String sessionID, HID source, 
			HID dest, String data, String i) {

		Message msg = new Message();
		MessageElement elem = new StringMessageElement("Data", data, null);
		msg.addMessageElement(null, elem);
		elem = new StringMessageElement("Dest", dest.toString(), null);
		msg.addMessageElement(null, elem);
		elem = new StringMessageElement("Source", source.toString(), null);
		msg.addMessageElement(null, elem);
		elem = new StringMessageElement("SessionID", sessionID, null);
		msg.addMessageElement(null, elem);
		elem = new StringMessageElement("Type", "REQUEST", null);
		msg.addMessageElement(null, elem);
		elem = new StringMessageElement("Count", i, null);
		msg.addMessageElement(null, elem);
		return msg;
	}

	/**
	 * Creates an asynchronous message
	 * @param source the source HID
	 * @param dest the destination HID
	 * @param data the data
	 * @param i the index
	 * @param messageType the type of the message
	 * @return the asynchronous message
	 */
	private Message createAsyncMessage(HID source, HID dest, String data, 
			String i, String messageType) {

		Message msg = new Message();
		MessageElement elem = new StringMessageElement("Data", data, null);
		msg.addMessageElement(null, elem);
		elem = new StringMessageElement("Dest", dest.toString(), null);
		msg.addMessageElement(null, elem);
		elem = new StringMessageElement("Source", source.toString(), null);
		msg.addMessageElement(null, elem);
		elem = new StringMessageElement("Type", messageType, null);
		msg.addMessageElement(null, elem);
		elem = new StringMessageElement("SessionID", "", null);
		msg.addMessageElement(null, elem);
		elem = new StringMessageElement("Count", i, null);
		msg.addMessageElement(null, elem);
		return msg;
	}

	/**
	 * Creates a response message
	 * @param sessionID the session ID
	 * @param source the source HID
	 * @param dest the destination HID
	 * @param data the data
	 * @param i the index
	 * @return the response message
	 */
	private Message createResponseMessage(String sessionID, HID source, HID dest, 
			String data, String i) {

		Message msg = new Message();
		MessageElement elem = new StringMessageElement("Dest", dest.toString(), null);
		msg.addMessageElement(null, elem);
		elem = new StringMessageElement("Source", source.toString(), null);
		msg.addMessageElement(null, elem);
		elem = new StringMessageElement("SessionID", sessionID, null);
		msg.addMessageElement(null, elem);
		elem = new StringMessageElement("Type", "RESPONSE", null);
		msg.addMessageElement(null, elem);
		elem = new StringMessageElement("Data", data, null);
		msg.addMessageElement(null, elem);
		elem = new StringMessageElement("Count", i, null);
		msg.addMessageElement(null, elem);
		return msg;
	}

	/**
	 * Creates an output pipe
	 * @param pID the peer ID
	 * @return the output pipe
	 */
	private OutputPipe createOutputPipe(PeerID pID) {
		OutputPipe outputPipe = null;
		pipeAdv = createPipeAdv();
		Set<PeerID> peersID = new HashSet<PeerID>();
		peersID.add(pID);

		//the outputpipe is often only created by second or third try so the loop is a workaround
		int i=0;
		while(i < MAXNUMRETRIES){
			try {
				outputPipe = pipeService.createOutputPipe(pipeAdv, peersID, OUTPUTPIPE_CREATION_TIMEOUT);
				//only returns if pipe has been created so we can exit loop
				break;
			} catch (IOException e) {
				logger.debug("Error when creating pipe: Timeout");
				i++;
			}
		}
		if(outputPipe != null){
		pipeTable.put(pID, new PipeInfo(outputPipe));
		}else{
			logger.error("Cannot create outputpipe to destination: " + pID);
		}
		return outputPipe;
	}	



	/**
	 * Listener where arrive the messages from a pipe. Two different types of 
	 * message are received: Request and Response
	 * 
	 * @param event a pipe message event
	 */
	public void pipeMsgEvent(PipeMsgEvent event) {
		new EventProcessor(event).start();
	}


	/**
	 * Receives data
	 * 
	 * @param sessionID the session ID
	 * @param senderHID the sender HID
	 * @param receiverHID the receiver HID
	 * @param data the data
	 * @return the NMResponse
	 * @throws java.rmi.RemoteException
	 */
	private NMResponse receiveData(String sessionID, String senderHID, 
			String receiverHID, String data) throws java.rmi.RemoteException {

		NMResponse response = new NMResponse();

//		if (sessionID.contains("@")) {
//			int firstAt = sessionID.indexOf('@');
//			int secondAt = sessionID.indexOf("@", firstAt + 1);
//			String oldStyleSessionId = sessionID.substring(firstAt + 1, secondAt);
//			String protocolId = sessionID.substring(secondAt + 1);

//			if (protocolId.equals("SecureSession")) {				
//				SecureSessionController controller = 
//					SecureSessionControllerImpl.getInstance();
//				Command command = new Command();
//
//				try {
//					command.loadFromXML(new ByteArrayInputStream(data.getBytes()));
//				} catch (IOException e) {
//					logger.error("Could not convert data to Command");
//					e.printStackTrace();
//				}
//
//				String role = Consts.SERVER;
//				String server = command.getProperty("server");
//
//				if (!receiverHID.equals(server)) {
//					role = Consts.CLIENT;
//				}
//
//				controller.handleCommand(command, role);
//			}
//			sessionID = oldStyleSessionId;
//		}

		/* SessionID checking. */
//		if(sessionID.equals("0")) {
//			/* A new session will be created. */
//			Session mySession = sessionMgr.createSession(senderHID, receiverHID);
//
//			if(mySession != null) {
//				sessionID = mySession.getSessionID();
//			}
//			else {
//				/* Maximum number of sessions reached. */
//				logger.error("Error - Cannot create session, Maximum number "
//						+ "of sessions reached");
//				response.setSessionID("-1");
//				response.setData("Error - Session creation");
//				return response;
//			}
//
//			/* Saving session. */
//			mySession.saveSession();
//		}
//		else if(sessionID.equals("-9")) {
//			response.setData("OK");
//			response.setSessionID("-9");
//			return response;
//		}
//		else if (!sessionID.equals("-3")) {
//			/* We check if the sessionID is known on the server. */
//			boolean sessionLoaded = sessionMgr.loadSession(sessionID);
//
//			/* Session ID is not on sessionServerList nor on disk. */
//			if(!sessionMgr.isSessionIDvalidServer(sessionID) && !sessionLoaded) {
//				logger.error("Error - Invalid SessionID");
//				response.setSessionID("-1");
//				response.setData("Error - Invalid SessionID");
//				return response;
//			}			
//			else {
//				logger.debug("Session " + sessionID + "already exist !");
//			}
//		}

		/* We call receiveData with the right sessionID. */
//		response = routeMgr.receiveData(sessionID, senderHID, receiverHID, data);
//
//		if(response.getSessionID().equals("-1")) {
//			logger.error("Error with Routing Manager - receiveData()");
//			return response;
//		}
//		else {
//			return response; 
//		}
		
		return response; 

	}

	/**
	 * Stops the pipes
	 */
	public void stopPipes() {
		pipeTableUpdater.stopThread();
		inputPipe.close();
		Enumeration<PipeInfo> en = pipeTable.elements();
		while (en.hasMoreElements()) {
			en.nextElement().getOutputPipe().close();
		}
	}



	/**
	 * Event Processor class
	 */
	public class EventProcessor extends Thread {
		PipeMsgEvent event;

		/**
		 * Constructor
		 * 
		 * @param event a pipe message event
		 */
		public EventProcessor(PipeMsgEvent event) {
			this.event = event;
		}

		/**
		 * Starts the thread
		 */
		public void run() {
			Message msg = event.getMessage(); 
			MessageElement data = msg.getMessageElement("Data");
			MessageElement type = msg.getMessageElement("Type");
			MessageElement dest = msg.getMessageElement("Dest");
			MessageElement source = msg.getMessageElement("Source");
			MessageElement sessionID = msg.getMessageElement("SessionID");
			MessageElement i = msg.getMessageElement("Count");

			if (type.toString().equalsIgnoreCase("REQUEST")) {
				/* 
				 * MESSAGE request arrived. Call the NMSoapImp.receiveData().
				 * Once it has received the status send it as a response using 
				 * sendMessageResponse(sessionID,source, dest, data)
				 */
				logger.debug("Receiving message. Creating response");
				NMResponse r = new NMResponse();

				try {
					r = receiveData(sessionID.toString(), source.toString(), 
							dest.toString(), data.toString());
				} catch (RemoteException e) {
					logger.error("Error calling receiveData " + e.getMessage());
				}

				logger.debug("Received data : " + data.toString() + " from HID="
						+ source.toString() + " to HID=" + dest.toString());
				// TODO: fix this
//				sendMessageResponse(r.getSessionID(), dest.toString(), 
//						source.toString(), r.getData(), i.toString());
			}

			if (type.toString().equalsIgnoreCase("RESPONSE")) {
				/*
				 * RESPONSE MESSAGE. NOTIFY the lock (in sendDataOverPipe). 
				 * Save the parameter for the RM
				 */
				logger.debug("Received response message: " + data.toString());
				PipeSender p = h.get(Integer.valueOf(i.toString()));

				if (p != null) {
					p.notification(sessionID.toString(), data.toString());
				}
			}

			if (type.toString().equalsIgnoreCase(HID_ATTRIBUTE_QUERY_RESPONSE)) {
				logger.debug("Received async message: " + data.toString());
				// TODO: fix this
//				backboneMgr.receivedQueryResponse(data.toString(), 
//						Integer.parseInt(i.toString()));
			}
		}
	}



	/**
	 * PipeTableUpdater class
	 */
	public class PipeTableUpdater extends Thread {
		private boolean running;
		final static long UPDATERTIME = 30000;

		/**
		 * Starts the thread
		 */
		public void run() {
			setName(PipeTableUpdater.class.getName());
			running = true;
			while (running) {
				Enumeration<ID> en = pipeTable.keys();
				Long currentTime = System.currentTimeMillis();
				while (en.hasMoreElements()) {
					ID id = en.nextElement();
					Long time = pipeTable.get(id).getTime() - currentTime;
					time = pipeTable.get(id).getTime();

					if ((currentTime - pipeTable.get(id).getTime()) 
							> Long.parseLong((String) bbjxta.getConfiguration().get(
									BackboneJXTAConfigurator.PIPE_LIFETIME))) {
						pipeTable.get(id).getOutputPipe().close();
						pipeTable.remove(id);
						logger.debug("Closed pipe to peer " + id + ". Max "
								+ "time without need reached");
					}
				}

				try {
					Thread.sleep(UPDATERTIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * Stops the thread
		 */
		public void stopThread() {
			running = false;
		}
	}

}
