package eu.linksmart.network.tunnel.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.Registration;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.network.tunnel.BasicTunnelService;
import eu.linksmart.utils.Part;
import eu.linksmart.network.tunnel.MultipleMatchException;

public class BasicTunnelServiceImpl implements BasicTunnelService{

	private BasicTunnelServiceImpl tunnel;

	private static Logger LOG = Logger.getLogger(BasicTunnelServiceImpl.class.getName());
	protected NetworkManagerCore nmCore;

	protected NetworkManagerCore getNM() {
		return this.nmCore;
	}

	protected void activate(ComponentContext context) {
		LOG.debug("BasicTunnelServiceImpl activated");
	}

	protected void deactivate(ComponentContext context) {
	}

	protected void bindNetworkManager(NetworkManagerCore nmCore) {
		this.nmCore = nmCore;
	}

	protected void unbindNetworkManager(NetworkManagerCore nmCore) {
		this.nmCore = null;
	}

	@Override
	public VirtualAddress getSenderVirtualAddressFromPath(
			HttpServletRequest request,
			VirtualAddress defaultSender) {
		String path = request.getPathInfo();
		//get sender address and check if path contained default switch
		VirtualAddress senderVirtualAddress = null;
		if (path.startsWith("/0/") || path.equals("/0")) {
			return defaultSender;
		} else {
			Pattern pat = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+");
			Matcher matcher = pat.matcher(path);
			if(matcher.find()) {
				senderVirtualAddress = new VirtualAddress(matcher.group());
				//remove sender virtual address part from path
				path = path.substring(matcher.group().length() + 1);
				return senderVirtualAddress;
			} else {
				LOG.info(BasicTunnelService.INVALID_VIRTUAL_ADDRESS_FORMAT + " of sender:" + request.getPathInfo());
				return null;
			}
		}
	}

	@Override
	public VirtualAddress getReceiverVirtualAddressFromPath(
			HttpServletRequest request) throws MultipleMatchException, Exception {
		//path without query
		String path = request.getPathInfo();
		//remove sender part from path
		if (path.startsWith("/0/") || path.equals("/0")) {
			path = path.substring(2);
		} else {
			Pattern pat = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+");
			Matcher matcher = pat.matcher(path);
			if(matcher.find()) {
				//remove sender virtual address part from path
				path = path.substring(matcher.group().length() + 1);
			} else {
				throw new IllegalArgumentException(
						BasicTunnelService.INVALID_VIRTUAL_ADDRESS_FORMAT + " of sender");
			}
		}
		//check if a receiver address is provided
		Pattern pat = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+");
		Matcher matcher = pat.matcher(path);
		if(matcher.find()) {
			return new VirtualAddress(matcher.group());
		} else {
			boolean getDefault = path.startsWith("/default");
			if(getDefault) {
				//remove default switch similarly to sender
				path = path.substring(8);
			}
			//get service
			Registration registration = getSearchedService(
					request.getQueryString(), getDefault);
			if(registration != null) {
				return registration.getVirtualAddress();
			}
		}
		LOG.debug(BasicTunnelService.NO_SERVICE + " for " + request.getQueryString());
		return null;
	}

	//TODO Javadoc
	@Override
	public String processRequest(HttpServletRequest request, HttpServletResponse response, 
			boolean hasData) throws IOException {
		//get request path
		String path = request.getPathInfo();
		//remove sender
		if (path.startsWith("/0/") || path.equals("/0")) {
			path = path.substring(2);
		} else {
			Pattern pat = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+");
			Matcher matcher = pat.matcher(path);
			if(matcher.find()) {
				//remove sender virtual address part from path
				path = path.substring(matcher.group().length() + 1);
			}
		}
		//remove receiver if present
		Pattern pat = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+");
		Matcher matcher = pat.matcher(path);
		if(matcher.find()) {
			path = path.substring(matcher.group().length() + 1);
		} else {
			boolean getDefault = path.startsWith("/default");
			if(getDefault) {
				//remove default switch similarly to sender
				path = path.substring(8);
			}
		}
		
		//process relevant parts of path
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

		return requestBuilder.toString();
	}

	/**
	 * Sends a request via NetworkManagerCore and adds the response to the HttpServletResponse
	 * @param senderVirtualAddress the sender VirtualAddress
	 * @param receiverVirtualAddress the receiver VirtualAddress
	 * @param requestString the request message, as a String
	 * @param response the servlet response to add the response message to
	 * @throws IOException
	 */
	@Override
	public void sendRequest(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, String requestString,
			HttpServletResponse response) throws IOException {
		NMResponse r = this.tunnel.getNM().sendData(senderVirtualAddress, receiverVirtualAddress, requestString.getBytes(), true);

		byte[] byteData = null;
		byte[] body = null;
		//check response status and if error response BAD_GATEWAY else parse response for client
		if (!r.getMessage().startsWith("HTTP/1.1 200 OK")) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			//set whole response data as body
			body = r.getMessage().getBytes();
		} else {		
			//take headers from data and add them to response
			byteData = r.getMessageBytes();
		}
		//write body data	
		response.setContentLength(body.length);
		response.getOutputStream().write(body);
		response.getOutputStream().close();
	}

	//TODO Javadoc
	@Override
	public byte[] composeResponse(byte[] byteData, HttpServletResponse response) {
		int bodyStartIndex = 0;
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
		}
		return Arrays.copyOfRange(byteData, bodyStartIndex, byteData.length);
	}

	//TODO Javadoc
	private Registration getSearchedService(String query, boolean getDefault) throws MultipleMatchException, Exception {
		ArrayList<Part> attributes = new ArrayList<Part>();		
		//divide query into individual attributes
		String[] queryAttrs = query.split("&");

		for(String queryAttr : queryAttrs) {
			int separatorIndex = queryAttr.indexOf("=");
			String attributeName = queryAttr.substring(0, separatorIndex).toUpperCase();

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
				registrations = nmCore.
						getServiceByAttributes(attributes.toArray(new Part[]{}), SERVICE_DISCOVERY_TIMEOUT, true, false);
			} else {
				registrations = nmCore.
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
	private String buildHeaders(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder();
		Enumeration<?> headerNames = request.getHeaderNames();

		while (headerNames.hasMoreElements()) {
			String header = (String) headerNames.nextElement();
			String value = request.getHeader(header);
			builder.append(header + ": " + value + "\r\n");
		}

		return builder.toString();
	}

}
