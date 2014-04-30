package eu.linksmart.network.grand.backbone;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.security.communication.SecurityProperty;

/**
 * Implements the Backbone interface in sending HTTP messages to the provided endpoints.
 * Can handle both GET and POST messages.
 *
 */
public class BackboneGrandImpl implements Backbone {

	private static String BACKBONE_GRAND = BackboneGrandImpl.class
			.getSimpleName();
	private Map<VirtualAddress, URL> virtualAddressUrlMap;
	private Logger LOG = Logger.getLogger(BackboneGrandImpl.class.getName());
	private static final int MAXNUMRETRIES = 15;
	private static final long SLEEPTIME = 20;
	private static final int BUFFSIZE = 65450;
	public static final String GRAND_TUNNEL_HEADER = "GrandTunnelUUID ";

	/**
	 * Timeout to the web service endpoint in MS.
	 */
	private static final int TIMEOUT = 30000;

	private BackboneGrandConfigurator configurator;
	private BackboneRouter bbRouter;
	
	public BackboneGrandImpl(ComponentContext context, BackboneRouter bbRouter) {
		virtualAddressUrlMap = new HashMap<VirtualAddress, URL>();

		try {
			this.configurator = new BackboneGrandConfigurator(this, context
					.getBundleContext());
			configurator.registerConfiguration();
		} catch (NullPointerException e) {
			LOG.fatal("Configurator could not be initialized " + e.toString());
		}

		this.bbRouter = bbRouter;

		LOG.info(BACKBONE_GRAND + " started");
	}

	/**
	 * Sends a message over the specific communication channel.
	 * 
	 * @param senderVirtualAddress
	 *            VirtualAddress of sender
	 * @param receiverVirtualAddress
	 *            VirtualAddress of receiver
	 * @param rawData
	 *            header data as String
	 * @return SOAP response
	 */
	public NMResponse sendDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] rawData) {
		URL urlEndpoint = virtualAddressUrlMap.get(receiverVirtualAddress);
		if (urlEndpoint == null) {
			throw new IllegalArgumentException("Cannot send data to VirtualAddress "
					+ receiverVirtualAddress.toString() + ", unknown endpoint");
		}
		// We expect data to be a string
		String data = new String(rawData);
		LOG.debug("GrandTunnel received this message " + data);
		NMResponse resp = new NMResponse();

		// Set error as default status
		String soapMsg = "Error in GrandTunneling receiveData";
		resp.setStatus(NMResponse.STATUS_ERROR);
		resp.setMessage(generateSoapResponse(soapMsg));

		//remove uuid from message
		if(data.startsWith(GRAND_TUNNEL_HEADER)) {
			UUID uuid = UUID.fromString(data.substring(GRAND_TUNNEL_HEADER.length(), data.indexOf(";")));
			data = data.substring(data.indexOf(";") + 1);
			if (data.startsWith("GET")) {
				// It is a GET request
				resp = processGetMessage(urlEndpoint, data, resp, uuid, senderVirtualAddress, receiverVirtualAddress);
			} else {
				// It is a POST request.
				// TODO make the same as GET
				processPostMessage(urlEndpoint, data, resp, uuid, senderVirtualAddress, receiverVirtualAddress);
			}
			return resp;
		} else {
			resp.setMessage("Not properly formatted grand request");
			return resp;
		}
	}

	public NMResponse sendDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] rawData) {
		throw new RuntimeException("Asynchronous sending not supported by HTTP!");
	}

	/**
	 * Processes POST message
	 * 
	 * @param urlEndpoint
	 *            URL of endpoint
	 * @param data
	 *            header data as String
	 * @param resp
	 *            response from SOAP service
	 */
	private NMResponse processPostMessage(URL urlEndpoint, String data,
			NMResponse resp, UUID uuid, VirtualAddress clientVA, VirtualAddress serverVA) {
		StringBuilder dataproc;
		if (urlEndpoint.getQuery() != null) {
			dataproc = new StringBuilder("POST ").append(urlEndpoint.getPath())
					.append("?").append(urlEndpoint.getQuery()).append(
							" HTTP/1.0\r\n");
		} else {
			dataproc = new StringBuilder("POST ").append(urlEndpoint.getPath())
					.append(" HTTP/1.0\r\n");
		}

		if (data.contains("<ns1:SetAVTransportURI>")) {
			data = data
					.replace(
							"<ns1:SetAVTransportURI>",
							"<ns1:SetAVTransportURI "
									+ "xmlns:ns1=\"urn:schemas-upnp-org:service:AVTransport:1\">");
		}

		String[] headers = null;
		if (data.contains("\r\n\r\n")) {
			headers = data.split("\r\n\r\n");
		} else if (data.contains("\n\n")) {
			headers = data.split("\n\n");
		} else {
			LOG.error("Wrong headers!!!");
		}

		LOG.debug("Length of headers: " + headers.length);
		StringTokenizer token = new StringTokenizer(headers[0], "\r\n");
		String header = "", aux = "";

		while (token.hasMoreElements()) {
			aux = token.nextToken();
			if (aux.contains("Content-Length")) {
				header = new StringBuilder("Content-Length: ").append(
						headers[1].length()).append("\r\n").toString();
			} else if (aux.contains("content-length")) {
				header = new StringBuilder("content-length: ").append(
						headers[1].length()).append("\r\n").toString();
			} else if (aux.toLowerCase().contains("host")) {
				header = new StringBuilder("Host: ").append(
						urlEndpoint.getHost()).append(":").append(
								urlEndpoint.getPort()).append("\r\n").toString();
			} else {
				header = aux + "\r\n";
			}

			if ((aux.contains("Connection:")) || (aux.contains("connection:"))) {
				header = "";
			}
			dataproc.append(header);
		}

		String SOAPMessage = dataproc + "\r\n" + headers[1];
		LOG.debug("Received SOAP message at the end of the tunnel:\n"
				+ SOAPMessage);

		return getResponse(urlEndpoint, resp, SOAPMessage, uuid, clientVA, serverVA);
	}

	/**
	 * Processes GET message
	 * 
	 * @param urlEndpoint
	 *            URL of endpoint
	 * @param data
	 *            header data as String
	 * @param resp
	 *            response from SOAP service
	 */
	private NMResponse processGetMessage(URL urlEndpoint, String data, NMResponse resp, UUID uuid, VirtualAddress clientVA, VirtualAddress serverVA) {
		StringTokenizer token = new StringTokenizer(data, "\r\n");
		String aux = "";
		StringBuilder dataproc = new StringBuilder();

		while (token.hasMoreElements()) {
			aux = token.nextToken();
			String header = "";
			if (aux.toLowerCase().contains("get")) {
				String parts[] = aux.split(" ");
				if (parts[1].startsWith("/")) {
					parts[1] = parts[1].substring(1);
				}
				parts[1] = urlEndpoint.getFile() + parts[1];
				header = "";
				for (String part : parts) {
					header = header + " " + part;
				}
				header = header.substring(1) + "\r\n";
			} else if (aux.toLowerCase().contains("host")) {
				header = "Host: " + urlEndpoint.getHost() + ":"
						+ urlEndpoint.getPort() + "\r\n";
			} else if (aux.toLowerCase().startsWith("connection")) {
				header = "Connection: close\r\n";
			} else if (aux.toLowerCase().startsWith("keep-alive")) {
				header = "";
			} else if (aux.startsWith("Accept-Encoding")
					&& aux.contains("gzip")) {
				//skip header name
				String encodingsStr = aux.substring(15);
				//get listed encodings
				String[] encodings = encodingsStr.split(",");
				String newEncodingStr = new String("Accept-Encoding: ");

				//if the only encoding is not gzip than recreate encodings header but remove gzip
				if(encodings.length != 1) {
					int i = 0;
					for (String encoding : encodings) {
						if(!encoding.contains("gzip")) {
							//last encoding does not need comma at the end
							newEncodingStr = newEncodingStr.concat(encoding.trim()
									+ ((i+1 == encodings.length)? "" : ","));
						}
						i++;
					}
					header = newEncodingStr + "\r\n";
				}
			}
			dataproc.append(header);
		}
		dataproc.append("\r\n");
		LOG.debug("Received GET request at the end of the tunnel:\n" + data);

		return getResponse(urlEndpoint, resp, dataproc.toString(), uuid, clientVA, serverVA);
	}

	/*
	 * Creates a socket connection to local axis service located at urlEndpoint. Returns 
	 * response from this call.
	 * Can be a call to a Web Service or just to an WSDL.
	 */
	private NMResponse getResponse(URL urlEndpoint, NMResponse resp, String dataproc, UUID uuid, VirtualAddress clientVA, VirtualAddress serverVA) {
		Socket clientSocket = null;
		try {
			// Create Socket to local web service
			clientSocket = new Socket();
			clientSocket.connect(new InetSocketAddress(urlEndpoint.getHost(), urlEndpoint
					.getPort()), TIMEOUT);

			flushMessage(dataproc, clientSocket);
			int packetNr = parseResponse(resp, clientSocket, uuid, clientVA, serverVA);
			LOG.debug("Closed processing of GRAND request to " + urlEndpoint.toString());	

			if (packetNr < 0) {
				// In case the response from the service is empty.
				resp.setStatus(NMResponse.STATUS_ERROR);
				return resp;
			}

			resp.setStatus(NMResponse.STATUS_SUCCESS);
			resp.setMessage(Integer.toString(packetNr));
		} catch (IllegalArgumentException e) {
			LOG.debug("Error delivering the data to destination:\n"
					+ e.getMessage()); 
			resp.setStatus(NMResponse.STATUS_ERROR);
			resp.setMessage("HTTP/1.1 418 I'm a teapot\r\n" + e.getMessage());
		} catch (IOException e) {
			LOG.debug("Error delivering the data to destination:\n" 
					+ e.getMessage()); 
			resp.setStatus(NMResponse.STATUS_ERROR);
			resp.setMessage("HTTP/1.1 500 Internal Server Error\r\n" + e.getMessage());
		} finally {
			try {
				if(clientSocket != null)
					clientSocket.close();
			} catch(Exception e) {
				LOG.warn("Streams could not be closed!");
			}
		}
		return resp;
	}

	/**
	 * @param dataproc
	 * @param clientSocket
	 * @return
	 * @throws IOException
	 */
	private void flushMessage(String dataproc, Socket clientSocket)
			throws IOException {
		OutputStream cos = clientSocket.getOutputStream();
		cos.write(dataproc.getBytes());
		cos.flush();
		clientSocket.shutdownOutput();
	}

	/**
	 * @param resp
	 * @param cis
	 * @param response
	 * @param buffer
	 * @return
	 * @throws IOException
	 */
	private int parseResponse(NMResponse resp, Socket socket, UUID uuid, VirtualAddress clientVA, VirtualAddress serverVA)
			throws IOException {
		byte[] buffer = new byte[BUFFSIZE];
		InputStream cis = socket.getInputStream();
		String soapMsg;
		int bytesRead = 0;
		int numRetries = 0;
		int packetNr = 0;
		try {
			do {
				if (cis.available() == 0) {
					if (numRetries >= MAXNUMRETRIES) {
						soapMsg = "HTTP/1.1 204 No Content\r\n Error delivering the data to destination:\n"
								+ "Data not available on service. Max Number of "
								+ "retries reached: " + MAXNUMRETRIES;
						LOG.debug(soapMsg);
						resp.setStatus(NMResponse.STATUS_ERROR);
						resp.setMessage(generateSoapResponse(soapMsg));
						return 0;
					}
					try {
						Thread.currentThread().sleep(SLEEPTIME, 0);
					} catch (InterruptedException e) {
						soapMsg = "Error delivering the data to destination:\n"
								+ e.getMessage();
						LOG.debug(soapMsg);
						resp.setStatus(NMResponse.STATUS_ERROR);
						resp.setMessage(generateSoapResponse(soapMsg));
						return -1;
					}
					++numRetries;
				}
				bytesRead = cis.read(buffer);
				if (bytesRead > 0) {
					numRetries = 0;
					//send each block over NetworkManager
					//first provide metadata to packet
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bos.write(new String(uuid + ":" + packetNr + ";").getBytes());
					bos.write(buffer, 0, bytesRead);
					bbRouter.receiveDataAsynch(serverVA, clientVA, bos.toByteArray(), this);
					bos.close();
					packetNr++;
				}
			} while (bytesRead != -1);
		} finally {
			cis.close();
		}
		return packetNr - 1;
	}

	/**
	 * @return
	 */
	private String generateSoapResponse(String msg) {
		return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope "
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
				+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
				+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body><SendDataResponse xmlns=\"http://eu.linksmart/\">"
				+ "<SendDataResult>" + msg + "</SendDataResult>"
				+ "</SendDataResponse></soap:Body></soap:Envelope>";
	}

	/**
	 * Receives a message over the specific communication channel.
	 * 
	 * @param senderVirtualAddress
	 * @param receiverVirtualAddress
	 * @param data
	 * @return
	 */
	public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
		return null;
	}

	/**
	 * Receives a message over the specific communication channel.
	 * 
	 * @param senderVirtualAddress
	 * @param receiverVirtualAddress
	 * @param data
	 * @return
	 */
	public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
		return null;
	}

	/**
	 * Broadcasts a message over the specific communication channel.
	 * 
	 * @param senderVirtualAddress
	 * @param data
	 * @return
	 */
	public NMResponse broadcastData(VirtualAddress senderVirtualAddress, byte[] data) {
		// throw new UnsupportedOperationException(this.getClass().getName()
		// + " does not support broadcasting messages");
		return null;
	}

	/**
	 * Return the destination address as string that will be used for display
	 * purposes.
	 * 
	 * @param virtualAddress
	 * @return the backbone address represented by the virtual address
	 */
	public String getEndpoint(VirtualAddress virtualAddress) {
		if (!virtualAddressUrlMap.containsKey(virtualAddress)) {
			return null;
		}
		return virtualAddressUrlMap.get(virtualAddress).toString();
	}

	@Override
	public boolean addEndpoint(VirtualAddress virtualAddress, String endpoint) {
		if (this.virtualAddressUrlMap.containsKey(virtualAddress)) {
			return false;
		}
		try {
			URL url = new URL(endpoint);
			this.virtualAddressUrlMap.put(virtualAddress, url);
			return true;
		} catch (MalformedURLException e) {
			LOG.debug("Unable to add endpoint " + endpoint + " for VirtualAddress "
					+ virtualAddress.toString(), e);
		}
		return false;
	}

	@Override
	public boolean removeEndpoint(VirtualAddress virtualAddress) {
		return this.virtualAddressUrlMap.remove(virtualAddress) != null;
	}

	@Override
	public String getName() {
		return BackboneGrandImpl.class.getName();
	}

	@Override
	/**
	 * returns security types required by using this backbone implementation. 
	 * The security types are configured via the LS configuration interface.
	 * See resources/BBJXTA.properties for details on configuration
	 * @return a list of security types available
	 */
	public List<SecurityProperty> getSecurityTypesRequired() {
		List<SecurityProperty> secProps = new ArrayList<SecurityProperty>();
		secProps.add(SecurityProperty.NoEncoding);
		secProps.add(SecurityProperty.NoSecurity);
		return secProps;
//		String configuredSecurity = this.configurator
//				.get(BackboneGrandConfigurator.SECURITY_PARAMETERS);
//		String[] securityTypes = configuredSecurity.split("\\|");
//		SecurityProperty oneProperty;
//		List<SecurityProperty> answer = new ArrayList<SecurityProperty>();
//		for (String s : securityTypes) {
//			try {
//				oneProperty = SecurityProperty.valueOf(s);
//				answer.add(oneProperty);
//			} catch (Exception e) {
//				LOG
//				.error("Security property value from configuration is not recognized: "
//						+ s + ": " + e);
//			}
//		}
//		return answer;
	}

	@Override
	public void addEndpointForRemoteService(VirtualAddress senderVirtualAddress, VirtualAddress remoteService) {

		URL endpoint = virtualAddressUrlMap.get(senderVirtualAddress);

		if (endpoint != null) {
			virtualAddressUrlMap.put(remoteService, endpoint);
		} else {
			LOG.error("Network Manager endpoint of VirtualAddress " + senderVirtualAddress
					+ " cannot be found");
		}

	}

}
