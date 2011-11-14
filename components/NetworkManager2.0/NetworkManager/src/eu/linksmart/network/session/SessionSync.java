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
 * communication between Network Managers. The communication is done through 
 * JXTA unidirectional pipes, whereas the NM pipe communication is synchronous.
 * For this purpose, the PipeSyncHandler class uses a request/response protocol 
 * to establish the bidirectional communication between NMs.
 * It provides a sendData interface in order to send data from a source HID to 
 * a destine HID:
 *     sendDataOverPipe(String sessionID, String senderHID, String receiverHID, String data)
 * 
 * @see eu.linksmart.network.backbone.BackboneManagerApplication
 */

package eu.linksmart.network.session;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

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


import eu.linksmart.network.backbone.BackboneManagerApplication;
import eu.linksmart.network.backbone.PipeInfo;
import eu.linksmart.network.identity.HIDManagerApplication;
import eu.linksmart.network.impl.NetworkManagerApplicationSoapBindingImpl;
import eu.linksmart.network.routing.RouteManagerApplication;
import eu.linksmart.types.HID;

/**
 * Class to store session sync information
 */
public class SessionSync extends Thread implements PipeMsgListener{
		
	public final static String sessionRequest = "SESSIONREQ";
	public final static String sessionResponse = "SESSIONRESP";
	public final static String sessionCreationServer = "SESSIONGENSERVER";
	public final static String sessionCreationClient = "SESSIONGENCLIENT";
	
	private static Logger logger = Logger.getLogger(SessionSync.class.getName());
	
	BackboneManagerApplication backboneMgr;
	HIDManagerApplication hidMgr;
	SessionManagerApplication sessionMgr;
	RouteManagerApplication routeMgr;
	
	private PipeService pipeService;
	private PipeAdvertisement pipeAdv;
	private InputPipe inputPipe;
	private Hashtable<ID, PipeInfo> pipeTable;
	private PipeTableUpdater pipeTableUpdater;
	private String sessioned = "-1"; 
	private String session = "-1";
	private NetworkManagerApplicationSoapBindingImpl nm;
	
	/**
	 * Constructor
	 * 
	 * @param nm the Network Manager application
	 */
	public SessionSync(NetworkManagerApplicationSoapBindingImpl nm) {
		this.nm = nm;
		backboneMgr = nm.backboneMgr;
		hidMgr = nm.hidMgr;
		sessionMgr = nm.sessionMgr;
		routeMgr = nm.routeMgr;
		
		this.pipeAdv = createPipeAdv();
		this.pipeService = backboneMgr.netPeerGroup.getPipeService();
		this.pipeTable = new Hashtable<ID, PipeInfo>();

		try {
			this.inputPipe = pipeService.createInputPipe(pipeAdv, this);
			logger.debug("Session input pipe created: " + inputPipe.getPipeID());
			pipeTableUpdater = new PipeTableUpdater();
			pipeTableUpdater.start();
		} catch (IOException e) {}	
	}
	
	/**
	 * Creates a pipe advertisement
	 * 
	 * @return the pipe advertisement
	 */
	private PipeAdvertisement createPipeAdv() {
		PipeID pipeID = IDFactory.newPipeID(backboneMgr.netPeerGroup.getPeerGroupID());
		PipeAdvertisement pipeAdv = (PipeAdvertisement) AdvertisementFactory
			.newAdvertisement(PipeAdvertisement.getAdvertisementType());
		System.err.println(pipeID.toString());
		String ppid = 
			"urn:jxta:uuid-59616261646162614E50472050325033886294A673C545EDA2B4B0275B82D12804";
		
		try {
			pipeID = (PipeID) IDFactory.fromURI(new URI(ppid));
		} catch (URISyntaxException e) {
			logger.error("Imposible to create PipeID for the pipe advertisement");
		}
		
		pipeAdv.setPipeID(pipeID);
		pipeAdv.setName("Input Pipe Advertisement for Session synchronization");
		pipeAdv.setType(PipeService.UnicastType);
		logger.debug("Pipe created: " + pipeID);
		
		return pipeAdv;			
	}

	/**
	 * This method allows to send data over pipe to other Network Managers. 
	 * In order to build a bidirectional communication path, it uses two 
	 * unidirectional pipes and two types of message: REQUEST and RESPONSE.
	 * The process is as follows:
	 * 1. The NM invokes this method
	 * 2. A message REQUEST is sent to the destination HID with the data
	 * 3. The receiver HID receives the data and sends a message RESPONSE 
	 *    with the status of the message
	 * 4. The sendDataOverPipe method returns the status of the communication
	 * In this way, the sendDataOverPipe tries to emulate the process of 
	 * sending data through a webService interface
	 * 
	 * @param s The HID of the sender of data
	 * @param d The HID of the receiver
	 * @return The ACK with the status of the communication 
	 */
	public synchronized String synchSessions(String s, String d) {
		HID dest = new HID(d);
		HID source = new HID(s);
		PeerID pID = null;
		
		OutputPipe outPipe = null;
		
		session = " ";
		sessioned = new String("-1");
		
		try {
			pID = hidMgr.getIDfromHID(dest);
			outPipe =pipeTable.get(pID).getOutputPipe();	
		} catch (NullPointerException e) {
			logger.debug("Sorry, peerID is null");
		}
		
		if ((outPipe == null)) {
			if ((pID != null)) {
				outPipe = createOutputPipe(pID);
				logger.debug("The pipe to " + pID.toString()
					+ " was closed or never created before");
				Message message = createMessage(source, dest, "", sessionRequest);
				
				try {
					outPipe.send(message);
					synchronized (sessioned) {
						try {
							sessioned.wait(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					logger.error("Error sending data to HID = " + dest.toString());
				}
				return session;
			}
			else {
				logger.error("Could not find destination HID. Please try later...");
				return session;
			}
		}
		else  {
			Message message = createMessage(source, dest, "", sessionRequest);
			try {
				outPipe.send(message);
				
				synchronized (sessioned) {						
					try {
						sessioned.wait(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				pipeTable.get(pID).setTime(System.currentTimeMillis());
			} catch (IOException e) {
				logger.error("Error synch sessions. HID = " + dest.toString());
			}
			return session;	
		}
	}


	/**
	 * @param s The HID of the sender of data
	 * @param d The HID of the receiver
	 * @param data The data to be sent, in a string representation 
	 * @return The ACK with the status of the communication
	 */
	private boolean synchSessionsResponse(String s, String d, String data) {
		HID dest = new HID(d);
		HID source = new HID(s);
		PeerID pID = null;

		OutputPipe outPipe = null;
		try {
			pID = hidMgr.getIDfromHID(dest);
			outPipe =pipeTable.get(pID).getOutputPipe();
		} catch (NullPointerException e) {
			logger.debug("Sorry, peerID is null");
		}
		
		if ((outPipe == null)) {
			if ((pID != null)) {
				outPipe = createOutputPipe(pID);
				logger.debug("The pipe to " + pID.toString() 
					+ " was closed or never created before");
				Message message = createMessage(source, dest, data, sessionResponse);
				
				try {
					outPipe.send(message);
				} catch (IOException e) {
					logger.error("Error sending data to HID = " + dest.toString());
				}
				return true;
			}
			else {
				logger.error("Could not find destination HID. Please try later...");
				return false;
			}
		}
		else  {
			Message message = createMessage(source, dest, data, sessionResponse);
			try {
				outPipe.send(message);
			} catch (IOException e) {
				logger.error("Error sending data to HID = " + dest.toString());
			}
			pipeTable.get(pID).setTime(System.currentTimeMillis());
			return true;	
		}
	}

	/**
	 * Creates a remote session server
	 * 
	 * @param data the data
	 * @param s the source LinkSmart ID
	 * @param d the destiny LinkSmart ID
	 * @return the remote session server
	 */
	public boolean createRemoteSessionServer(String data, String s, String d) {
		HID dest = new HID(d);
		HID source = new HID(s);
		PeerID pID = null;

		OutputPipe outPipe = null;
		try {
			pID = hidMgr.getIDfromHID(dest);
			outPipe =pipeTable.get(pID).getOutputPipe();
		} catch (NullPointerException e) {
			logger.error("Unknown HID: " + dest.toString());
		}
		
		if ((outPipe == null)) {
			if ((pID != null)) {
				outPipe = createOutputPipe(pID);
				logger.debug("The pipe to " + pID.toString()
					+ " was closed or never created before");
				Message message = createMessage(source, dest, data, 
					sessionCreationServer);
				try {
					outPipe.send(message);
				} catch (IOException e) {
					logger.error("Error sending data to HID = " + dest.toString());
				}
				return true;
			}
			else {
				logger.error("Could not find destination HID. Please try later...");
				return false;
			}
		}
		else  {
			Message message = createMessage(source, dest, data, sessionCreationServer);
			try {
				outPipe.send(message);
			} catch (IOException e) {
				logger.error("Error sending data to HID = " + dest.toString());
			}
			pipeTable.get(pID).setTime(System.currentTimeMillis());
			return true;	
		}
	}

	/**
	 * Creates a remote session client
	 * 
	 * @param data the data
	 * @param s the source LinkSmart ID
	 * @param d the destiny LinkSmart ID
	 * @return the remote session client
	 */
	public boolean createRemoteSessionClient(String data, String s, String d) {
		HID dest = new HID(d);
		HID source = new HID(s);
		PeerID pID = null;

		OutputPipe outPipe = null;
		try {
			pID = hidMgr.getIDfromHID(dest);
			outPipe =pipeTable.get(pID).getOutputPipe();
		} catch (NullPointerException e) {
			logger.error("Unknown HID: " + dest.toString());
		}
		
		if ((outPipe == null)) {
			if ((pID != null)) {
				outPipe = createOutputPipe(pID);
				logger.debug("The pipe to " + pID.toString()
					+ " was closed or never created before");
				Message message = createMessage(source, dest, data, 
					sessionCreationClient);
				try {
					outPipe.send(message);
				} catch (IOException e) {
					logger.error("Error sending data to HID = " + dest.toString());
				}
				return true;
			}
			else {
				logger.error("Could not find destination HID. Please try later...");
				return false;
			}
		}
		else  {
			Message message = createMessage(source, dest, data, sessionCreationClient);
			try {
				outPipe.send(message);
			} catch (IOException e) {
				logger.error("Error sending data to HID = " + dest.toString());
			}
			pipeTable.get(pID).setTime(System.currentTimeMillis());
			return true;	
		}
	}
	
	/**
	 * Creates a message
	 * 
	 * @param source the source LinkSmart ID
	 * @param dest the destiny LinkSmart ID
	 * @param data the data of the message
	 * @param type the type of the message
	 * @return the message created
	 */
	private Message createMessage(HID source, HID dest, String data, String type) {
		Message msg = new Message();
		MessageElement elem = new StringMessageElement("Data", data, null);
		msg.addMessageElement(null, elem);
		elem = new StringMessageElement("Dest", dest.toString(), null);
		msg.addMessageElement(null, elem);
		elem = new StringMessageElement("Source", source.toString(), null);
		msg.addMessageElement(null, elem);
		elem = new StringMessageElement("Type", type, null);
		msg.addMessageElement(null, elem);
		return msg;
	}
	
	/**
	 * Creates an output pipe
	 * 
	 * @param pID the peer ID
	 * @return the output pipe created
	 */
	private OutputPipe createOutputPipe(PeerID pID) {
		OutputPipe outputPipe = null;
		pipeAdv = createPipeAdv();
		Set<PeerID> peersID = new HashSet<PeerID>();
		peersID.add(pID);
		
		try {
			outputPipe = pipeService.createOutputPipe(pipeAdv, peersID, 0);
			pipeTable.put(pID, new PipeInfo(outputPipe));
		} catch (IOException e) {
			logger.error("Error when creating pipe: Timeout");
		} catch (NullPointerException e) {
			logger.error("Error updating pipeTable: peerID or outputPipe don't exist");
			e.printStackTrace();
		}

		return outputPipe;
	}	
	
	/**
	 * Listener where arrive the messages from a pipe. Two different types 
	 * of message are received: Request and Response
	 * 
	 * @param event a pipe message event
	 */ 
	public void pipeMsgEvent(PipeMsgEvent event) {
		Message msg = event.getMessage(); 
		MessageElement data = msg.getMessageElement("Data");
		MessageElement type = msg.getMessageElement("Type");
		MessageElement dest = msg.getMessageElement("Dest");
		MessageElement source = msg.getMessageElement("Source");
		
		if (type.toString().equalsIgnoreCase(sessionCreationServer)) {	
			logger.debug("Receiving session creation server request");
			sessionMgr.addSessionLocalClient(data.toString(), 
				source.toString(), dest.toString());	
		}
		else if (type.toString().equalsIgnoreCase(sessionCreationClient)) {	
			logger.debug("Receiving session creation client request");
			Session mySession = sessionMgr.createSession(data.toString(),
				source.toString(), dest.toString());
			if (mySession != null) {
				mySession.saveSession();	
			}
		}
		else if (type.toString().equalsIgnoreCase(sessionRequest)) {
			String r = sessionMgr.synchronizeSessionsListP2P(
				source.toString(), dest.toString());
			synchSessionsResponse(dest.toString(), source.toString(), r);
		}
		else if (type.toString().equalsIgnoreCase(sessionResponse)) {
			logger.debug("Received session response message.");
			synchronized (sessioned) {
				session = data.toString();
				sessioned.notifyAll();
			}				
		}
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
	 * PipeTableUpdater class
	 */
	public class PipeTableUpdater extends Thread {
	  	private boolean running;
	  	final static long MAXTIME = 600000;
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

					if ((currentTime - pipeTable.get(id).getTime()) > MAXTIME) {
						pipeTable.get(id).getOutputPipe().close();
						pipeTable.remove(id);
						logger.debug("Closed pipe to peer " + id 
							+ ". Max time without need reached");
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