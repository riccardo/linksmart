package eu.linksmart.network.backbone.impl.soap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.HID;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.security.communication.SecurityProperty;

/*
 * TODO #NM refactoring
 */
public class BackboneSOAPImpl implements Backbone {

	private static String BACKBONE_SOAP = BackboneSOAPImpl.class
			.getSimpleName();
	private Map<HID, URL> hidUrlMap;
	private Logger LOG = Logger.getLogger(BackboneSOAPImpl.class.getName());
	private static final int MAXNUMRETRIES = 15;
	private static final long SLEEPTIME = 20;
	private static final int BUFFSIZE = 16384;

	private BackboneSOAPConfigurator configurator;

	protected void activate(ComponentContext context) {
		hidUrlMap = new HashMap<HID, URL>();

		try {
			this.configurator = new BackboneSOAPConfigurator(this, context
					.getBundleContext());
			configurator.registerConfiguration();
		} catch (NullPointerException e) {
			LOG.fatal("Configurator could not be initialized " + e.toString());
		}

		LOG.info(BACKBONE_SOAP + " started");
	}

	protected void deactivate(ComponentContext context) {
		LOG.info(BACKBONE_SOAP + " stopped");
	}

	/**
	 * Sends a message over the specific communication channel.
	 * 
	 * @param senderHID
	 *            HID of sender
	 * @param receiverHID
	 *            HID of receiver
	 * @param rawData
	 *            header data as String
	 * @return SOAP response
	 */
	public NMResponse sendDataSynch(HID senderHID, HID receiverHID, byte[] rawData) {
		URL urlEndpoint = hidUrlMap.get(receiverHID);
		if (urlEndpoint == null) {
			throw new IllegalArgumentException("Cannot send data to HID "
					+ receiverHID.toString() + ", unknown endpoint");
		}
		// We expect data to be a string
		String data = new String(rawData);
		LOG.debug("SOAPTunnel received this message " + data);
		NMResponse resp = new NMResponse();

		// Set error as default status
		String soapMsg = "Error in SOAP tunneling receiveData";
		resp.setStatus(NMResponse.STATUS_ERROR);
		resp.setMessage(generateSoapResponse(soapMsg));
//decode properties & Decode64
		if (data.startsWith("GET")) {
			// It is a GET request
			resp = processGetMessage(urlEndpoint, data, resp);
		} else {
			// It is a POST request.
			// TODO make the same as GET
			processPostMessage(urlEndpoint, data, resp);
		}
		return resp;
	}
	
	public NMResponse sendDataAsynch(HID senderHID, HID receiverHID, byte[] rawData) {
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
	private void processPostMessage(URL urlEndpoint, String data,
			NMResponse resp) {
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

		getResponse(urlEndpoint, resp, SOAPMessage);
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
	private NMResponse processGetMessage(URL urlEndpoint, String data, NMResponse resp) {
		StringTokenizer token = new StringTokenizer(data, "\r\n");
		String header = "", aux = "";
		StringBuilder dataproc = new StringBuilder();

		while (token.hasMoreElements()) {
			aux = token.nextToken();
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
			}
			dataproc.append(header);
		}
		dataproc.append("\r\n");
		LOG.debug("Received GET request at the end of the tunnel:\n" + data);

		return getResponse(urlEndpoint, resp, dataproc.toString());
	}

	/*
	 * Creates a socket connection to local axis service located at urlEndpoint. Returns 
	 * response from this call.
	 * Can be a call to a Web Service or just to an WSDL.
	 */
	private NMResponse getResponse(URL urlEndpoint, NMResponse resp, String dataproc) {
		try {
			// Create Socket to local axis service
			Socket clientSocket = new Socket(urlEndpoint.getHost(), urlEndpoint
					.getPort());

			flushMessage(dataproc, clientSocket);
			String response = parseResponse(resp, clientSocket);

			String[] s = response.split("\r\n\r\n");
			if (s.length > 1) {
				response = s[1];
			} else {
				// In case the SOAP response from the service is empty.
				response = generateSoapResponse("No SOAP response from the service");
			}

			LOG.debug("Response:\n" + response);
			resp.setStatus(NMResponse.STATUS_SUCCESS);
			resp.setMessage(response);
			return resp;
			
		} catch (UnknownHostException e) {
			String msg = "Error delivering the data to destination:\n"
					+ e.getMessage();
			LOG.debug(msg);
			resp.setStatus(NMResponse.STATUS_ERROR);
			resp.setMessage(generateSoapResponse(msg));
			return resp;
		} catch (IOException e) {
			String msg = "Error delivering the data to destination:\n"
					+ e.getMessage();
			LOG.debug(msg);
			resp.setStatus(NMResponse.STATUS_ERROR);
			resp.setMessage(generateSoapResponse(msg));
			return resp;
		}
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
		// cos.close();
	}

	/**
	 * @param resp
	 * @param cis
	 * @param response
	 * @param buffer
	 * @return
	 * @throws IOException
	 */
	private String parseResponse(NMResponse resp, Socket socket)
			throws IOException {
		byte[] buffer = new byte[BUFFSIZE];
		String response = "";
		InputStream cis = socket.getInputStream();
		String soapMsg;
		int bytesRead = 0;
		int numRetries = 0;
		do {
			if (cis.available() == 0) {
				if (numRetries >= MAXNUMRETRIES) {
					soapMsg = "Error delivering the data to destination:\n"
							+ "Data not available on service. Max Number of "
							+ "retries reached: " + MAXNUMRETRIES;
					LOG.debug(soapMsg);
					resp.setStatus(NMResponse.STATUS_ERROR);
					resp.setMessage(generateSoapResponse(soapMsg));
					break;
				}
				try {
					Thread.currentThread().sleep(SLEEPTIME, 0);
				} catch (InterruptedException e) {
					soapMsg = "Error delivering the data to destination:\n"
							+ e.getMessage();
					LOG.debug(soapMsg);
					resp.setStatus(NMResponse.STATUS_ERROR);
					resp.setMessage(generateSoapResponse(soapMsg));
				}
				++numRetries;
			}
			bytesRead = cis.read(buffer);
			if (bytesRead > 0) {
				numRetries = 0;
				response = response.concat(new String(buffer, 0, bytesRead));
			}
		} while (bytesRead != -1);
		cis.close();
		return response;
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
	 * @param senderHID
	 * @param receiverHID
	 * @param data
	 * @return
	 */
	public NMResponse receiveDataSynch(HID senderHID, HID receiverHID, byte[] data) {
		return null;
	}
	
	/**
	 * Receives a message over the specific communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param data
	 * @return
	 */
	public NMResponse receiveDataAsynch(HID senderHID, HID receiverHID, byte[] data) {
		return null;
	}

	/**
	 * Broadcasts a message over the specific communication channel.
	 * 
	 * @param senderHID
	 * @param data
	 * @return
	 */
	public NMResponse broadcastData(HID senderHID, byte[] data) {
		// throw new UnsupportedOperationException(this.getClass().getName()
		// + " does not support broadcasting messages");
		return null;
	}

	/**
	 * Return the destination address as string that will be used for display
	 * purposes.
	 * 
	 * @param hid
	 * @return the backbone address represented by the Hid
	 */
	public String getEndpoint(HID hid) {
		if (!hidUrlMap.containsKey(hid)) {
			return null;
		}
		return hidUrlMap.get(hid).toString();
	}

	@Override
	public void applyConfigurations(Hashtable updates) {
		// at this point there is nothing that is saved in the configurations
		// that needs to be updated when they change

	}

	@Override
	public boolean addEndpoint(HID hid, String endpoint) {
		if (this.hidUrlMap.containsKey(hid)) {
			return false;
		}
		try {
			URL url = new URL(endpoint);
			this.hidUrlMap.put(hid, url);
			return true;
		} catch (MalformedURLException e) {
			LOG.debug("Unable to add endpoint " + endpoint + " for HID "
					+ hid.toString(), e);
		}
		return false;
	}

	@Override
	public boolean removeEndpoint(HID hid) {
		return this.hidUrlMap.remove(hid) != null;
	}

	@Override
	public String getName() {
		return BackboneSOAPImpl.class.getName();
	}

	@Override
	/**
	 * returns security types required by using this backbone implementation. 
	 * The security types are configured via the LS configuration interface.
	 * See resources/BBJXTA.properties for details on configuration
	 * @return a list of security types available
	 */
	public List<SecurityProperty> getSecurityTypesRequired() {
		String configuredSecurity = this.configurator
				.get(BackboneSOAPConfigurator.SECURITY_PARAMETERS);
		String[] securityTypes = configuredSecurity.split("\\|");
		SecurityProperty oneProperty;
		List<SecurityProperty> answer = new ArrayList<SecurityProperty>();
		for (String s : securityTypes) {
			try {
				oneProperty = SecurityProperty.valueOf(s);
				answer.add(oneProperty);
			} catch (Exception e) {
				LOG
						.error("Security property value from configuration is not recognized: "
								+ s + ": " + e);
			}
		}
		return answer;
	}

	@Override
	public void addEndpointForRemoteHID(HID senderHID, HID remoteHID) {

		URL endpoint = hidUrlMap.get(senderHID);

		if (endpoint != null) {
			hidUrlMap.put(remoteHID, endpoint);
		} else {
			LOG.error("Network Manager endpoint of HID " + senderHID
					+ " cannot be found");
		}

	}

}
