package eu.linksmart.network.tunnel;

import java.io.BufferedReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.Registration;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.utils.Part;

public class TunnelServlet extends HttpServlet{

	private static final long serialVersionUID = 5473388358983564206L;
	private Tunnel tunnel;
	private static final Logger logger = Logger
			.getLogger(TunnelServlet.class.getName());
	private static final String NO_SERVICE = "Did not find matching service";

	protected TunnelServlet(Tunnel tunnel) {
		this.tunnel = tunnel;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		processDatalessRequest(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		//path without query
		String path = request.getPathInfo();
		//get sender address and check if path contained default switch
		VirtualAddress senderVirtualAddress = null;
		if (path.startsWith("/0/") || path.equals("/0")) {
			senderVirtualAddress = tunnel.getNM().getService();
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
		
		//get service
		Registration registration = null;
		try {
			registration = getSearchedService(request.getQueryString());
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, e.getMessage());
			return;
		}

		if(registration != null) {
			StringBuilder requestBuilder = new StringBuilder();
			VirtualAddress receiverVirtualAddress = registration.getVirtualAddress();

			// build request
			// append request line
			requestBuilder.append(request.getMethod()).append(" /");
			//append path
			requestBuilder.append((path.startsWith("/")? path.substring(1) : path));
			requestBuilder.append(" ").append(request.getProtocol()).append("\r\n");
			// append headers and blank line for end of headers
			requestBuilder.append(buildHeaders(request));
			requestBuilder.append("\r\n");

			// append content
			if ((request.getContentLength() > 0)) {
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
			logger.debug("Sending soap request through tunnel");
		} else {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, NO_SERVICE);
			return;
		}
	}

	private void processDatalessRequest(HttpServletRequest request, HttpServletResponse response)
			throws IOException{		
		//path without query
		String path = request.getPathInfo();
		//get sender address and check if path contained default switch
		VirtualAddress senderVirtualAddress = null;
		if (path.startsWith("/0/") || path.equals("/0")) {
			senderVirtualAddress = tunnel.getNM().getService();
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

		//get service
		Registration registration = null;
		try {
			registration = getSearchedService(request.getQueryString());
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, e.getMessage());
			return;
		}

		if(registration != null) {
			boolean isWsdl = false;		
			if (path.endsWith("wsdl")) {
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
			if(isWsdl) {
				requestBuilder.append("?wsdl");
			}
			requestBuilder.append(" ").append(request.getProtocol()).append("\r\n");

			// append headers - no body because this is a GET request
			requestBuilder.append(buildHeaders(request));


			// send request to NetworkManagerCore
			sendRequest(senderVirtualAddress, registration.getVirtualAddress(), requestBuilder.toString(), response);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, NO_SERVICE);
			return;
		}
	}

	private Registration getSearchedService(String query) throws MultipleMatchException, Exception {
		ArrayList<Part> attributes = new ArrayList<Part>();
		boolean getDefault = false;
		if(query.contains("#default")) {
			//cut off default switch from end as it is not part of the query
			query = query.substring(0, query.indexOf("#default"));
			getDefault = true;
		}
		
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
			registrations = tunnel.getNM().
					getServiceByAttributes(attributes.toArray(new Part[]{}));
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
		NMResponse r = tunnel.getNM().sendData(senderVirtualAddress, receiverVirtualAddress, requestString.getBytes(), true);
		String body = new String();
		//check response status and if error response BAD_GATEWAY else parse response for client
		if (!r.getMessage().startsWith("HTTP/1.1 200 OK")) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			//set whole response data as body
			body = r.getMessage();
		} else {		
			//take headers from data and add them to response
			String[] headers = r.getMessage().split("(?<=\r\n)");
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
			//concat remaining elements of 'headers' array (the real data) into response body
			for(i++;i < headers.length;i++) {
				body = body.concat(headers[i]);
			}
		}
		//write body data
		response.setContentLength(body.getBytes().length);
		response.getWriter().write(body);
	}

	class MultipleMatchException extends Exception {

		public MultipleMatchException(String message) {
			super(message);
		}

		private static final long serialVersionUID = 1028769408212148548L;

	}
}
