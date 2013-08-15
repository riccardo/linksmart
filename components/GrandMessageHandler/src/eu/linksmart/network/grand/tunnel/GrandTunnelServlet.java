package eu.linksmart.network.grand.tunnel;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.Registration;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.grand.backbone.BackboneGrandImpl;
import eu.linksmart.network.grand.impl.GrandMessageHandlerImpl;
import eu.linksmart.utils.Part;

public class GrandTunnelServlet extends HttpServlet{

	private static final long serialVersionUID = 5473388358983564206L;
	private GrandMessageHandlerImpl tunnel;
	private static final Logger logger = Logger
			.getLogger(GrandTunnelServlet.class.getName());
	private static final String NO_SERVICE = "Did not find matching service";
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
		processRequest(request, response, false);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		processRequest(request, response, true);
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response, boolean hasData)
			throws IOException{		
		//path without query
		String path = request.getPathInfo();
		//get sender address and check if path contained default switch
		VirtualAddress senderVirtualAddress = null;
		if (path.startsWith("/0/") || path.equals("/0")) {
			senderVirtualAddress = tunnel.getGrandHandlerVAD();
			//remove sender virtual address part from path
			path = path.substring(2);
		} else {
			Pattern pat = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+");
			Matcher matcher = pat.matcher(path);
			if(matcher.find()) {
				senderVirtualAddress = new VirtualAddress(matcher.group());
				//remove sender virtual address part from path
				path = path.substring(matcher.group().length() + 1);
			} else {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Bad format of sender VirtualAddress");
				return;
			}
		}
		//check if a receiver address is provided
		Pattern pat = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+");
		Matcher matcher = pat.matcher(path);
		VirtualAddress receiverVirtualAddress = null;
		if(matcher.find()) {
			receiverVirtualAddress = new VirtualAddress(matcher.group());
			//remove sender virtual address part from path
			path = path.substring(matcher.group().length() + 1);
		} else {
			boolean getDefault = path.startsWith("/default");
			if(getDefault) {
				//remove default switch similarly to sender
				path = path.substring(8);
			}
			//get service
			Registration registration = null;
			try {
				registration = getSearchedService(request.getQueryString(), getDefault);
			} catch (Exception e) {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, e.getMessage());
				return;
			}
			if(registration != null) {
				receiverVirtualAddress = registration.getVirtualAddress();
			}
		}

		if(receiverVirtualAddress != null) {
			boolean isWsdl = false;		
			if (!hasData && path.endsWith("wsdl")) {
				isWsdl = true;
				//remove wsdl from path
				path = path.substring(0, path.length() - 5);
			}

			// build request
			StringBuilder requestBuilder = new StringBuilder();
			// append request line
			requestBuilder.append(request.getMethod()).append(" /");
			//append path
			requestBuilder.append((path.startsWith("/")? path.substring(1) : path));
			if(!hasData && isWsdl) {
				requestBuilder.append("?wsdl");
			}
			requestBuilder.append(" ").append(request.getProtocol()).append("\r\n");

			// append headers
			requestBuilder.append(buildHeaders(request));
			if(hasData) {
				requestBuilder.append("\r\n");
			}

			// append content if necessary
			if (hasData && request.getContentLength() > 0) {
				try {
					BufferedReader reader = request.getReader();
					for (String line = null; (line = reader.readLine()) != null;)
						requestBuilder.append(line);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// send request to NetworkManagerCore
			sendRequest(senderVirtualAddress, receiverVirtualAddress, requestBuilder.toString(), response);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, NO_SERVICE);
			return;
		}
	}

	private Registration getSearchedService(String query, boolean getDefault) throws MultipleMatchException, Exception {
		ArrayList<Part> attributes = new ArrayList<Part>();		
		//divide query into individual attributes
		String[] queryAttrs = query.split("&");

		for(String queryAttr : queryAttrs) {
			int separatorIndex = queryAttr.indexOf("=");
			String attributeName = queryAttr.substring(0, separatorIndex).toUpperCase();
			//FIXME should we have this check?
			//check if the searched service attribute exists
			//			ServiceAttribute attributeNameCheck = null;
			//			try {
			//				attributeNameCheck = ServiceAttribute.valueOf(attributeName);
			//			} catch (Exception e) {
			//				throw new Exception("Unknown service attribute key");
			//			}
			//			if(attributeNameCheck == null) {
			//				throw new Exception("Unknown service attribute key");
			//			}

			String attributeValue = queryAttr.substring(separatorIndex + 1);
			if(attributeValue.startsWith("\"") && attributeValue.endsWith("\"")) {
				//cut off quotation symbols
				attributeValue = attributeValue.substring(1, attributeValue.length() - 1);
			} else if(attributeValue.startsWith("%22") && attributeValue.endsWith("%22")) {
				//cut off quotation symbols
				attributeValue = attributeValue.substring(3, attributeValue.length() - 3);
			} else {
				throw new Exception("False format of service query");
			}
			attributes.add(new Part(attributeName, attributeValue));
		}

		//find service matching attributes
		Registration[] registrations = null;
		try {
			if(getDefault) {
				registrations = tunnel.getNM().
						getServiceByAttributes(attributes.toArray(new Part[]{}), SERVICE_DISCOVERY_TIMEOUT, true, false);
			} else {
				registrations = tunnel.getNM().
						getServiceByAttributes(attributes.toArray(new Part[]{}));
			}
		} catch (RemoteException e) {
			//local invocation
		}

		if (registrations.length > 1 && !getDefault) {
			throw new MultipleMatchException("No default switch provided although the number of found services was " + registrations.length);
		} else if (registrations != null && registrations.length != 0) {
			//there is only one element or the default has been required
			return registrations[0];
		} else {
			return null;
		}
	}

	/**
	 * Builds the string that represents the headers of a HttpServletRequest
	 * @param request the HttpServletRequest of which to extract the headers
	 * @return the String representing the headers
	 */
	public String buildHeaders(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder();
		Enumeration<?> headerNames = request.getHeaderNames();

		while (headerNames.hasMoreElements()) {
			String header = (String) headerNames.nextElement();
			String value = request.getHeader(header);
			builder.append(header + ": " + value + "\r\n");
		}

		return builder.toString();
	}

	/**
	 * Sends a request via NetworkManagerCore and adds the response to the HttpServletResponse
	 * @param senderVirtualAddress the sender VirtualAddress
	 * @param receiverVirtualAddress the receiver VirtualAddress
	 * @param requestString the request message, as a String
	 * @param response the servlet response to add the response message to
	 * @throws IOException
	 */
	private void sendRequest(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, String requestString,
			HttpServletResponse response) throws IOException {
		//open session for receiving data blocks
		String uuid = openSession();
		//put grand header at the beginning of message
		String grandHeader = BackboneGrandImpl.GRAND_TUNNEL_HEADER + uuid + ";";
		requestString = grandHeader + requestString;
		//make invocation which will block until data is ready
		NMResponse r = this.tunnel.getNM().sendData(senderVirtualAddress, receiverVirtualAddress, requestString.getBytes(), true);
		//if response is success look into data
		if(r.getStatus() == NMResponse.STATUS_SUCCESS) {
			int largestIndex = Integer.parseInt(r.getMessage());
			byte[] byteData = getSessionData(uuid, largestIndex);
			closeSession(uuid);
			int bodyStartIndex = 0;
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
				//take headers from data and add them to response
				byte[] headerEnd = new String("\r\n\r\n").getBytes();
				//find end of header
				for(;bodyStartIndex < byteData.length; bodyStartIndex++) {
					if(bodyStartIndex + headerEnd.length < byteData.length) {
						if(Arrays.equals(
								Arrays.copyOfRange(byteData, bodyStartIndex, bodyStartIndex + headerEnd.length),
								headerEnd)) {
							bodyStartIndex = bodyStartIndex + headerEnd.length;
							break;
						}
					} else {
						bodyStartIndex = byteData.length;
						break;
					}
				}
				byte[] headersBytes = Arrays.copyOf(byteData, bodyStartIndex);
				String[] headers = new String(headersBytes).split("(?<=\r\n)");
				//use it to get index of data element
				int i = 0;
				//go through headers and put them to response until empty line is reached
				for (String header : headers) {	
					if(header.contentEquals("\r\n")) {
						break;
					}
					if(!header.toLowerCase().startsWith("http")) {
						header = header.replace("\r\n", "");
						String[] headerParts = header.split(":");
						if(headerParts.length == 2) {
							response.setHeader(headerParts[0], headerParts[1].trim());
						}
					}
					i++;
				}
				body = Arrays.copyOfRange(byteData, bodyStartIndex, byteData.length);
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
