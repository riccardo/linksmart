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
 * It provides a sendData interface in order to send data from a source VirtualAddress 
 * to a destine VirtualAddress:
 * 	sendDataOverPipe(String sessionID, String senderVirtualAddress, String receiverVirtualAddress, String data)
 * 
 * @see eu.linksmart.network.backbone.BackboneManagerApplication
 */

package eu.linksmart.network.backbone.impl.jxta;

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
import net.jxta.endpoint.ByteArrayMessageElement;
import net.jxta.endpoint.Message;
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

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.VirtualAddress;

/**
 * PipeSyncHandler
 */
public class PipeSyncHandler extends Thread implements PipeMsgListener {

	private static Logger logger = Logger.getLogger(PipeSyncHandler.class
			.getName());

	/**
	 * Number of times to try to search for input pipe.
	 */
	private static final int MAXNUMRETRIES = 15;
	/**
	 * Time to wait for advertisement to find pipe.
	 */
	private static final long OUTPUTPIPE_CREATION_TIMEOUT = 3000;
	/**
	 * The identifier for the LinkSmart input pipes.
	 */
	private static final String PIPE_ID = "urn:jxta:uuid-79B6A084D3264DF8B641867D926C48D9F3C29CB8709F4D7EB39D92335B61D4CE04";

	private static final String MESSAGE_ELEMENT_NAME_DATA = "Data";
	private static final String MESSAGE_ELEMENT_NAME_SOURCE = "Source";
	private static final String MESSAGE_ELEMENT_NAME_DESTINATION = "Dest";
	private static final String MESSAGE_ELEMENT_NAME_TYPE = "Type";
	private static final String MESSAGE_ELEMENT_TYPE_REQUEST = "REQUEST2";
	private static final String MESSAGE_ELEMENT_TYPE_RESPONSE = "RESPONSE2";	
	private static final String MESSAGE_ELEMENT_NAME_REQUESTID = "RequestId";
	private static final String MESSAGE_ELEMENT_NAME_SYNCH = "Synch";
	private static final String MESSAGE_ELEMENT_SENDER_PEER_ID = "PeerID";
	private static final String TRUE = "true";
	private static final String FALSE = "false";

	private PipeService pipeService;
	private PipeAdvertisement pipeAdv;
	private InputPipe inputPipe;
	private ConcurrentHashMap<ID, PipeInfo> pipeTable;
	private PipeTableUpdater pipeTableUpdater;
	public NMResponse response;
	private Hashtable<Integer, PipeSender> requestIdPipeSenderTable;
	Integer i = 0;

	private int MAXTIMERESPONSE = 60000;

	private BackboneJXTAImpl bbjxta;

	/**
	 * Constructor
	 * 
	 * @param bbjxta
	 *            the Network Manager application
	 */
	public PipeSyncHandler(BackboneJXTAImpl bbjxta) {
		this.bbjxta = bbjxta;

		this.pipeAdv = createPipeAdv();
		this.pipeService = bbjxta.netPeerGroup.getPipeService();
		this.pipeTable = new ConcurrentHashMap<ID, PipeInfo>();
		this.requestIdPipeSenderTable = new Hashtable<Integer, PipeSender>();

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
	 * Creates a Pipe Advertisement.
	 * 
	 * @return a pipe advertisement
	 */
	private PipeAdvertisement createPipeAdv() {
		PipeID pipeID = IDFactory.newPipeID(bbjxta.netPeerGroup
				.getPeerGroupID());
		PipeAdvertisement pipeAdv = (PipeAdvertisement) AdvertisementFactory
				.newAdvertisement(PipeAdvertisement.getAdvertisementType());
		String ppid = PIPE_ID;

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
	 * 
	 * @return the counter value
	 */
	private synchronized Integer count() {
		i = i + 1;
		return i;
	}

	/**
	 * Creates a request message
	 * 
	 * @param sessionID
	 *            the session ID
	 * @param source
	 *            the source VirtualAddress
	 * @param dest
	 *            the destination VirtualAddress
	 * @param data
	 *            the data
	 * @param requestID
	 *            the index
	 * @return the request message
	 */
	private Message createRequestMessage(VirtualAddress source, VirtualAddress dest, byte[] data, String requestID, PeerID senderID, boolean synch) {

		Message msg = new Message();
		msg.addMessageElement(new ByteArrayMessageElement(
				MESSAGE_ELEMENT_NAME_SYNCH, null, (synch)?TRUE.getBytes():FALSE.getBytes(), null));
		msg.addMessageElement(new ByteArrayMessageElement(
				MESSAGE_ELEMENT_NAME_DATA, null, data, null));
		msg.addMessageElement(new ByteArrayMessageElement(
				MESSAGE_ELEMENT_NAME_DESTINATION, null, dest.getBytes(), null));
		msg.addMessageElement(new ByteArrayMessageElement(
				MESSAGE_ELEMENT_NAME_SOURCE, null, source.getBytes(), null));
		msg.addMessageElement(new StringMessageElement(
				MESSAGE_ELEMENT_NAME_TYPE, MESSAGE_ELEMENT_TYPE_REQUEST, null));
		msg.addMessageElement(new StringMessageElement(MESSAGE_ELEMENT_NAME_REQUESTID, requestID, null));
		msg.addMessageElement(new StringMessageElement(
				MESSAGE_ELEMENT_SENDER_PEER_ID, senderID.getURL().toString(), null));
		return msg;
	}

	/**
	 * Sends data from a source VirtualAddress to a destine VirtualAddress
	 * 
	 * @param sessionID
	 *            the session ID
	 * @param s
	 *            the source LinkSmart ID
	 * @param d
	 *            the destine LinkSmart ID
	 * @param data
	 *            the data to send
	 * @return the response
	 */
	public NMResponse sendData(String s, String d, byte[] data, PeerID pID, boolean synch) {
		Integer i = count();
		PipeSender pipeSender = new PipeSender(i);
		long time = System.currentTimeMillis();
		//store requestid to identify response
		if(synch) {
			requestIdPipeSenderTable.put(i, pipeSender);
		}
		NMResponse res = pipeSender.sendDataOverPipe(s, d, data, pID, synch);

		//block until response comes and then remove id
		if(synch) {
			// TODO: check if res has any content.
			res = pipeSender.waitForResponseFromPeer();

			requestIdPipeSenderTable.remove(i);
			logger.debug("Closing " + i + " the size is " + requestIdPipeSenderTable.size() + " Time: "
					+ (System.currentTimeMillis() - time));
		}
		return res;
	}

	/**
	 * PipeSender class
	 */
	public class PipeSender {
		private NMResponse resp;
		private Integer requestID;
		private boolean responseReceived = false;

		/**
		 * Constructor
		 * 
		 * @param i
		 *            unique integer
		 */
		public PipeSender(int requestID) {
			this.requestID = requestID;
		}

		/**
		 * @param sessionId
		 *            the session ID
		 * @param data
		 *            the data to send
		 */
		public void notification(byte[] data) {
			responseReceived = true;
			// TODO: Does data also include a status? Then, resp.setStatus()
			// must also be called

			logger.debug("Received response in " + requestID);
			resp.setBytesPrimary(true);
			resp.setMessageBytes(data);
			synchronized (requestID) {
				requestID.notify();
			}
		}

		/**
		 * Gets the response
		 * 
		 * @return the response
		 */
		public NMResponse waitForResponseFromPeer() {
			synchronized (requestID) {
				try {
					requestID.wait(MAXTIMERESPONSE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(!responseReceived) {
				resp = new NMResponse(NMResponse.STATUS_ERROR);
				resp.setMessage("Request timed out.");
			}
			return resp;
		}

		/**
		 * Sends data over a pipe from a source VirtualAddress to a destine VirtualAddress
		 * 
		 * @param sessionID
		 *            the session ID
		 * @param s
		 *            the source LinkSmart ID
		 * @param d
		 *            the destine LinkSmart ID
		 * @param data
		 *            the data to send
		 * @param synch
		 * 			  send message synchronously
		 * @return the response
		 */
		public NMResponse sendDataOverPipe(String s, String d, byte[] data,
				PeerID pID, boolean synch) {

			VirtualAddress dest = new VirtualAddress(d);
			VirtualAddress source = new VirtualAddress(s);
			int numRetries = 0;
			OutputPipe outPipe = null;
			resp = new NMResponse();
			resp.setStatus(NMResponse.STATUS_ERROR);
			resp.setMessage("<Response>Error in SenderPipeSyncHandler</Response>");

			while (numRetries < MAXNUMRETRIES) {
				try {
					pID = bbjxta.getPeerID(dest);
					if (pID != null) {
						if (pipeTable.containsKey(pID))
							outPipe = pipeTable.get(pID).getOutputPipe();
						break;
					}
					numRetries++;
				} catch (NullPointerException e) {
					logger.debug("Could not find destination VirtualAddress. Backing off");
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
					if(outPipe == null) {
						resp.setStatus(NMResponse.STATUS_ERROR);
						resp.setMessage("Could not reach destination VirtualAddress "
								+ dest.toString());
						return resp;
					}
					logger.debug("The pipe to " + pID.toString()
							+ " was closed or never created before");
					Message message = createRequestMessage(source, dest, data, i.toString(), bbjxta.getPeerID(), synch);

					try {
						if(outPipe.send(message)){
							resp.setStatus(NMResponse.STATUS_SUCCESS);
							resp.setMessage("<Response>Success sending data to VirtualAddress = "
									+ dest.toString() + "</Response>");
						}
					} catch (IOException e) {
						logger.error("<Response>Error sending data to VirtualAddress = "
								+ dest.toString()+ "</Response>");
						resp.setStatus(NMResponse.STATUS_ERROR);
						resp.setMessage("Error sending data to VirtualAddress = "
								+ dest.toString());
					}

					return resp;
				} else {
					logger.error("Sender: Could not find destination VirtualAddress "
							+ dest.toString() + ". Please try later...");
					resp.setStatus(NMResponse.STATUS_ERROR);
					resp.setMessage("<Response>Could not find destination VirtualAddress "
							+ dest.toString() + ". Please try later...</Response>");
					return resp;
				}
			} else {
				Message message = createRequestMessage(source, dest, data, i.toString(), bbjxta.getPeerID(), synch);

				try {

					if(outPipe.send(message)){
						resp.setStatus(NMResponse.STATUS_SUCCESS);
						resp.setMessage("<Response>Success sending data to VirtualAddress = "
								+ dest.toString()+"</Response>");
					}
				} catch (IOException e) {
					logger.error("Error sending data to VirtualAddress = "
							+ dest.toString());
					resp.setStatus(NMResponse.STATUS_ERROR);
					resp.setMessage("<Response>Error sending data to VirtualAddress = "
							+ dest.toString() + "</Response>");
					//there was an error with the pipe so we remove it
					//this will enforce the recreation at next invocation
					outPipe.close();
					pipeTable.remove(pID);
				}

				pipeTable.get(pID).setTime(System.currentTimeMillis());
				return resp;
			}
		}

	}

	/**
	 * Creates an output pipe
	 * 
	 * @param pID
	 *            the peer ID
	 * @return the output pipe
	 */
	private OutputPipe createOutputPipe(PeerID pID) {
		OutputPipe outputPipe = null;
		pipeAdv = createPipeAdv();
		Set<PeerID> peersID = new HashSet<PeerID>();
		peersID.add(pID);

		// the outputpipe is often only created by second or third try so the
		// loop is a workaround
		int i = 0;
		while (i < MAXNUMRETRIES) {
			try {
				outputPipe = pipeService.createOutputPipe(pipeAdv, peersID,
						OUTPUTPIPE_CREATION_TIMEOUT);
				// only returns if pipe has been created so we can exit loop
				break;
			} catch (IOException e) {
				logger.debug("Error when creating pipe: Timeout");
				i++;
			}
		}
		if (outputPipe != null) {
			pipeTable.put(pID, new PipeInfo(outputPipe));
		} else {
			logger.error("Cannot create outputpipe to destination: " + pID);
			return null;
		}
		return outputPipe;
	}

	/**
	 * Listener where arrive the messages from a pipe. Two different types of
	 * message are received: Request and Response
	 * 
	 * @param event
	 *            a pipe message event
	 */
	public void pipeMsgEvent(PipeMsgEvent event) {
		new EventProcessor(event).start();
	}

	/**
	 * Receives data
	 * 
	 * @param sessionID
	 *            the session ID
	 * @param senderVirtualAddress
	 *            the sender VirtualAddress
	 * @param receiverVirtualAddress
	 *            the receiver VirtualAddress
	 * @param data
	 *            the data
	 * @return the NMResponse
	 * @throws java.rmi.RemoteException
	 */
	private NMResponse receiveData(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress,
			byte[] data, boolean synch) throws java.rmi.RemoteException {
		if(synch) {
			return bbjxta.receiveDataSynch(senderVirtualAddress, receiverVirtualAddress,
					data);
		} else {
			return bbjxta.receiveDataAsynch(senderVirtualAddress, receiverVirtualAddress,
					data);
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
	 * Event Processor class
	 */
	public class EventProcessor extends Thread {
		PipeMsgEvent event;

		/**
		 * Constructor
		 * 
		 * @param event
		 *            a pipe message event
		 */
		public EventProcessor(PipeMsgEvent event) {
			this.event = event;
		}

		/**
		 * Starts the thread
		 */
		public void run() {
			Message msg = event.getMessage();
			String type = msg.getMessageElement(MESSAGE_ELEMENT_NAME_TYPE).toString();

			ByteArrayMessageElement data = (ByteArrayMessageElement) msg.getMessageElement(MESSAGE_ELEMENT_NAME_DATA);
			ByteArrayMessageElement dest = (ByteArrayMessageElement) msg.getMessageElement(MESSAGE_ELEMENT_NAME_DESTINATION);
			ByteArrayMessageElement source = (ByteArrayMessageElement) msg.getMessageElement(MESSAGE_ELEMENT_NAME_SOURCE);			
			String requestId = msg.getMessageElement(MESSAGE_ELEMENT_NAME_REQUESTID).toString();

			if(type.equals(MESSAGE_ELEMENT_TYPE_REQUEST)){
				/*
				 * MESSAGE request arrived. Call the NMSoapImp.receiveData(). Once
				 * it has received the status send it as a response using
				 * sendMessageResponse(sessionID,source, dest, data)
				 */
				logger.info("PipeSyncHandler - Receiving message.");
				NMResponse r = new NMResponse();

				VirtualAddress sourceVirtualAddress = new VirtualAddress(source.getBytes());
				VirtualAddress destVirtualAddress = new VirtualAddress(dest.getBytes());

				//
				// adding into map the sender jxta peerId
				//
				if(msg.getMessageElement(MESSAGE_ELEMENT_SENDER_PEER_ID) != null) {
					String senderPeerID = msg.getMessageElement(MESSAGE_ELEMENT_SENDER_PEER_ID).toString();
					bbjxta.addEndpoint(sourceVirtualAddress, senderPeerID);
				}

				ByteArrayMessageElement synchBytes = (ByteArrayMessageElement) msg.getMessageElement(MESSAGE_ELEMENT_NAME_SYNCH);
				boolean synch = new String(synchBytes.getBytes()).contentEquals(TRUE);
				try {
					r = receiveData(sourceVirtualAddress, 
							destVirtualAddress, data.getBytes(), synch);
				} catch (RemoteException e) {
					logger.error("Error calling receiveData " + e.getMessage());
				}

				logger.debug("Received data : " + data.toString() + " from VirtualAddress="
						+ sourceVirtualAddress.toString() + " to VirtualAddress=" + destVirtualAddress.toString());
				if(synch) {
					// reverse source and destination because we (dest) send response back to source
					if(r.getMessage() == null) r.setMessage("");
					boolean success = sendMessageResponse(destVirtualAddress, sourceVirtualAddress, r.getMessageBytes(), requestId);
					if(!success) {
						logger.info("Unable to send response to message from " + sourceVirtualAddress.toString());
					}
				}

			}else if (type.toString().equalsIgnoreCase(MESSAGE_ELEMENT_TYPE_RESPONSE)) {
				/*
				 * RESPONSE MESSAGE. NOTIFY the lock (in sendDataOverPipe). 
				 * Save the parameter for the RM
				 */
				logger.debug("Received response message: " + data.toString());
				PipeSender p = requestIdPipeSenderTable.get(Integer.valueOf(requestId));

				if (p != null) {
					p.notification(data.getBytes());
				}
			} else {
				logger.debug("Received incompatible JXTA message with type " + type);
			}

		}

		/**
		 * Sends a response message
		 * @param sessionID the session ID
		 * @param source the source VirtualAddress
		 * @param destination the destination VirtualAddress
		 * @param data the data
		 * @param i the index
		 * @return boolean depending on the result
		 */
		private boolean sendMessageResponse(/* String sessionID, */ VirtualAddress source, VirtualAddress destination, 
				byte[] data, String i) {

			//			VirtualAddress dest = new VirtualAddress(destination);
			//			VirtualAddress source = new VirtualAddress(sourceVirtualAddress);
			PeerID pID = null;
			int numRetries = 0;
			OutputPipe outPipe = null;

			while (numRetries < MAXNUMRETRIES) {
				try {
					pID = bbjxta.getPeerID(destination);
					if (pID != null) {
						if (pipeTable.containsKey(pID)) outPipe = 
								pipeTable.get(pID).getOutputPipe();
						break;
					}
					numRetries++;
				} catch (NullPointerException e) {
					logger.debug("Could not find destination VirtualAddress. Backing off");
					numRetries++;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}

			if ((outPipe == null)) {
				if ((pID != null)) {
					outPipe = createOutputPipe(pID);
					if(outPipe == null) {
						return false;
					}
					logger.debug("The pipe to " + pID.toString()
							+ " was closed or never created before");
					Message message = createResponseMessage(source, destination, data, i);

					try {
						outPipe.send(message);
					} catch (IOException e) {
						logger.error("Error sending data to VirtualAddress = " + destination.toString());
					}

					return true;
				} else {
					logger.error("Resp. Could not find destination VirtualAddress "
							+ destination.toString() + ". Please try later...");
					return false;
				}
			}
			else {
				Message message = createResponseMessage(source, destination, data, i);

				try {
					outPipe.send(message);
				} catch (IOException e) {
					logger.error("Error sending data to VirtualAddress = " + destination.toString());
				}

				pipeTable.get(pID).setTime(System.currentTimeMillis());
				return true;	
			}
		}

	}

	/**
	 * Creates a response message
	 * @param sessionID the session ID
	 * @param source the source VirtualAddress
	 * @param dest the destination VirtualAddress
	 * @param data the data
	 * @param i the index
	 * @return the response message
	 */
	private Message createResponseMessage(VirtualAddress source, VirtualAddress dest, 
			byte[] data, String i) {

		Message msg = new Message();
		msg.addMessageElement(new ByteArrayMessageElement(
				MESSAGE_ELEMENT_NAME_DATA, null, data, null));
		msg.addMessageElement(new ByteArrayMessageElement(
				MESSAGE_ELEMENT_NAME_DESTINATION, null, dest.getBytes(), null));
		msg.addMessageElement(new ByteArrayMessageElement(
				MESSAGE_ELEMENT_NAME_SOURCE, null, source.getBytes(), null));
		msg.addMessageElement(new StringMessageElement(
				MESSAGE_ELEMENT_NAME_TYPE, MESSAGE_ELEMENT_TYPE_RESPONSE, null));
		msg.addMessageElement(new StringMessageElement(
				MESSAGE_ELEMENT_NAME_REQUESTID, i , null));
		return msg;
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
					if ((currentTime - pipeTable.get(id).getTime()) > Long
							.parseLong((String) bbjxta.getConfiguration().get(
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
