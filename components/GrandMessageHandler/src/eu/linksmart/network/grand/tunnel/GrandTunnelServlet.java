package eu.linksmart.network.grand.tunnel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.grand.backbone.BackboneGrandImpl;
import eu.linksmart.network.grand.impl.GrandMessageHandlerImpl;
import eu.linksmart.network.tunnel.BasicTunnelService;

public class GrandTunnelServlet extends HttpServlet{

	private static final long serialVersionUID = 5473388358983564206L;
	private GrandMessageHandlerImpl tunnel;
	private static final Logger logger = Logger
			.getLogger(GrandTunnelServlet.class.getName());
	protected static final int SERVICE_DISCOVERY_TIMEOUT = 10*1000;
	protected static final int GRAND_MESSAGE_RETRIEVE_TIMEOUT = 10*1000;
	protected static final String SESSION_CLOSED_EXCEPTION = 
			"Session does not exist or has already been closed!";
	protected Map<String, Vector<byte[]>> sessionBuffers = new HashMap<String, Vector<byte[]>>();
	protected Map<String, LinkedList<Integer>> workingThreads = new HashMap<String, LinkedList<Integer>>();

	public GrandTunnelServlet(GrandMessageHandlerImpl tunnel) {
		this.tunnel = tunnel;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		logger.debug("GrandTunnel received GET request");
		processRequest(request, response, false);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		logger.debug("GrandTunnel received POST request");
		processRequest(request, response, true);
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response, boolean hasData)
			throws IOException{	
		//get sender and receiver from request path
		VirtualAddress senderVirtualAddress = 
				tunnel.getBasicTunnelService().
				getSenderVirtualAddressFromPath(
						request, tunnel.getGrandHandlerVAD());
		if(senderVirtualAddress == null) {
			response.sendError(
					HttpServletResponse.SC_BAD_REQUEST, BasicTunnelService.INVALID_VIRTUAL_ADDRESS_FORMAT);
			return;
		}
		VirtualAddress receiverVirtualAddress = null;
		try {
			receiverVirtualAddress = 
					tunnel.getBasicTunnelService().
					getReceiverVirtualAddressFromPath(request);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		}
		if(receiverVirtualAddress == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, BasicTunnelService.NO_SERVICE);
			return;
		}

		//compose request and headers
		String requestString = tunnel.getBasicTunnelService().processRequest(
				request,
				response,
				hasData);

		//open session for receiving data blocks
		String uuid = openSession();
		//put grand header at the beginning of message
		String grandHeader = BackboneGrandImpl.GRAND_TUNNEL_HEADER + uuid + ";";
		requestString = grandHeader + requestString;

		//send over LinkSmart
		NMResponse r = tunnel.getNM().sendData(
				senderVirtualAddress,
				receiverVirtualAddress,
				requestString.getBytes(),
				true);
		
		if(r.getStatus() == NMResponse.STATUS_SUCCESS) {
			int largestIndex = 0;
			try {
			largestIndex = Integer.parseInt(r.getMessage());
			} catch (NumberFormatException ne) {
				closeSession(uuid);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ne.getMessage());
			}
			byte[] byteData = getSessionData(uuid, largestIndex);
			closeSession(uuid);
			byte[] body = null;
			//check response status and if error response BAD_GATEWAY else parse response for client
			if (byteData == null || !new String(byteData).startsWith("HTTP/1.1 200 OK")) {
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				//set whole response data as body
				if(byteData == null) {
					body = "Error merging packets. Try again!".getBytes();
				} else {
					body = r.getMessage().getBytes();
				}
			} else {
				body = tunnel.getBasicTunnelService().composeResponse(byteData, response);
			}
			//write body data	
			response.setContentLength(body.length);
			response.getOutputStream().write(body);
			response.getOutputStream().close();
		} else {
			closeSession(uuid);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, r.getMessage());
		}
		
	}

	private boolean allMessagesAvailable(String uuid, int largestIndex) {
		Vector<byte[]> mergedPackets = sessionBuffers.get(uuid);
		if(mergedPackets.size() < largestIndex - 1 || mergedPackets.size() == 0) {
			return false;
		}
		for(int i=0; i <= largestIndex; i++) {
			if (mergedPackets.get(i) == null) {
				logger.debug("Packet nr. " + i + " missing for uuid: " + uuid);
				return false;
			}
		}
		return true;
	}

	private byte[] getSessionData(String uuid, int largestIndex) {
		if(!sessionBuffers.containsKey(uuid)) {
			throw new IllegalArgumentException(SESSION_CLOSED_EXCEPTION);
		}
		try {
			//wait for delayed packets for max timeout
			long startTime = Calendar.getInstance().getTimeInMillis();
			boolean allMsgsAvailable = false;
			while (Calendar.getInstance().getTimeInMillis() - startTime < GRAND_MESSAGE_RETRIEVE_TIMEOUT &&
					!(allMsgsAvailable = allMessagesAvailable(uuid, largestIndex))) {
				try {
					Object lock = workingThreads.get(uuid);
					synchronized(lock) {
						lock.wait(GRAND_MESSAGE_RETRIEVE_TIMEOUT);
					}
				} catch(InterruptedException e) {
					//nothing to handle
				}
			}

			if(!allMsgsAvailable) {
				logger.info("Not all packets arrived for session: " + uuid);
				return null;
			}
			//merge all packets together
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			for(byte[] packet : sessionBuffers.get(uuid)) {
				if(packet == null) return null;
				bos.write(packet);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}

	protected String openSession() {
		UUID uuid = UUID.randomUUID();
		sessionBuffers.put(uuid.toString(), new Vector<byte[]>());
		workingThreads.put(uuid.toString(), new LinkedList<Integer>());
		return uuid.toString();
	}

	protected void closeSession(String uuid) {
		if(sessionBuffers.containsKey(uuid)) {
			Object lock = workingThreads.get(uuid);
			synchronized(lock) {
				synchronized(this) {
					sessionBuffers.remove(uuid);
				}
				while(workingThreads.get(uuid).size() > 0) {
					try {
						lock.wait(GRAND_MESSAGE_RETRIEVE_TIMEOUT);
					} catch (InterruptedException e) {
						//nothing to handle
					}
				}
			}
			workingThreads.get(uuid).clear();
			workingThreads.remove(uuid);
		} else {
			throw new IllegalArgumentException(SESSION_CLOSED_EXCEPTION);
		}
	}

	public void receiveDataPacket(byte[] data) {
		//get index of the packet
		//take headers from data and add them to response
		byte[] headerEnd = new String(";").getBytes();
		int bodyStartIndex = 0;
		//find end of header
		for(;bodyStartIndex < data.length; bodyStartIndex++) {
			if(bodyStartIndex + headerEnd.length < data.length) {
				if(Arrays.equals(
						Arrays.copyOfRange(data, bodyStartIndex, bodyStartIndex + headerEnd.length),
						headerEnd)) {
					bodyStartIndex = bodyStartIndex + headerEnd.length;
					break;
				}
			} else {
				bodyStartIndex = data.length;
				break;
			}
		}
		byte[] headersBytes = Arrays.copyOf(data, bodyStartIndex);
		String header = new String(headersBytes);
		//take uuid and packet nr from header
		if(!header.contains(":")) return;
		String uuid = header.substring(0, header.indexOf(":"));
		header = header.substring(header.indexOf(":") + 1);
		int index = Integer.parseInt(header.replace(";", ""));

		//check if session has not been closed - after that no threads should be waiting
		Integer boxedIndex = new Integer(index);
		synchronized(this) {
			if(!sessionBuffers.containsKey(uuid)) return;
			workingThreads.get(uuid).add(boxedIndex);
		}
		//put data into session buffer
		Object lock = workingThreads.get(uuid);
		synchronized(lock) {
			if(sessionBuffers.containsKey(uuid)) {
				Vector<byte[]> mergingData = sessionBuffers.get(uuid);
				//increase array size if necessary
				if(mergingData.size() <= index) mergingData.setSize(index + 1);
				//set data
				mergingData.set(index, Arrays.copyOfRange(data, bodyStartIndex, data.length));
			}
			workingThreads.get(uuid).remove((Object)boxedIndex);
			//notify threads as they may wait for delayed packets
			lock.notify();
		}
	}

	class MultipleMatchException extends Exception {

		public MultipleMatchException(String message) {
			super(message);
		}

		private static final long serialVersionUID = 1028769408212148548L;
	}
}
