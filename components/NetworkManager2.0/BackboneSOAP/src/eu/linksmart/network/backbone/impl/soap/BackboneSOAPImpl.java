package eu.linksmart.network.backbone.impl.soap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.HID;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.backbone.Backbone;

/*
 * TODO #NM refactoring
 */
public class BackboneSOAPImpl implements Backbone {

	private static String BACKBONE_SOAP = BackboneSOAPImpl.class
			.getSimpleName();
	private Map<HID, URL> hidUrlMap;
	private static Logger LOG = Logger.getLogger(BackboneSOAPImpl.class
			.getName());
	private static final int MAXNUMRETRIES = 15;
	private static final long SLEEPTIME = 20;
	private static final int BUFFSIZE = 16384;

	protected void activate(ComponentContext context) {
		LOG.info(BACKBONE_SOAP + "started");
	}

	protected void deactivate(ComponentContext context) {
		LOG.info(BACKBONE_SOAP + "stopped");
	}

	/**
	 * Sends a message over the specific communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param data
	 * @return
	 */
	public NMResponse sendData(HID senderHID, HID receiverHID, byte[] rawData) {
		URL urlEndpoint = hidUrlMap.get(receiverHID);
		if (urlEndpoint == null) {
			throw new IllegalArgumentException("Cannot send data to HID "
					+ receiverHID.toString() + ", unknown endpoint");
		}
		//TODO how can we know how to convert data to String?
		String data = new String(rawData);
		LOG.debug("SOAPTunnel received this message " + data);
		NMResponse resp = new NMResponse();
		resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope "
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
				+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
				+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
				+ "<SendDataResult>"
				+ "Error in SOAP tunneling receiveData"
				+ "</SendDataResult></SendDataResponse></soap:Body></soap:Envelope>");
		// resp.setSessionID("");

		if (data.startsWith("GET")) {
			// It is a GET request
			try {
				// URL urlEndpoint = new URL(endpoint);
				StringTokenizer token = new StringTokenizer(data, "\r\n");
				String header = "", aux = "";
				String dataproc = "";
				String url = "";
				while (token.hasMoreElements()) {
					aux = token.nextToken();
					if (aux.toLowerCase().contains("get")) {
						String parts[] = aux.toLowerCase().split(" ");
						urlEndpoint = new URL(urlEndpoint.toString()
								+ parts[1].replace("/", ""));
						header = aux.replace(parts[1], urlEndpoint.getFile())
								+ "\r\n";
					} else if (aux.toLowerCase().contains("host")) {
						header = "Host: " + urlEndpoint.getHost() + ":"
								+ urlEndpoint.getPort() + "\r\n";
					} else if (aux.toLowerCase().startsWith("connection")) {
						header = "Connection: close\r\n";
					} else if (aux.toLowerCase().startsWith("keep-alive")) {
						header = "";
					} else {
						header = "\r\n";
					}
					dataproc = dataproc + header;
				}
				dataproc = dataproc + "\r\n";

				LOG.debug("Received GET request at the end of the tunnel:\n"
						+ data);
				Socket clientSocket = new Socket(urlEndpoint.getHost(),
						urlEndpoint.getPort());
				OutputStream cos = clientSocket.getOutputStream();
				cos.write(dataproc.getBytes());
				cos.flush();
				InputStream cis = clientSocket.getInputStream();
				String response = "";
				byte[] buffer = new byte[BUFFSIZE];

				int bytesRead = 0;
				int total = 0;
				int numRetries = 0;
				do {
					if (cis.available() == 0) {
						if (numRetries >= MAXNUMRETRIES) {
							LOG.debug("Error delivering the data to destination:\n"
									+ "Data not available on service. Max Number of "
									+ "retries reached: " + MAXNUMRETRIES);
							resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
									+ "<soap:Envelope "
									+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
									+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
									+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
									+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
									+ "<SendDataResult>"
									+ "Data not available on service. Max Number "
									+ "of retries reached: "
									+ MAXNUMRETRIES
									+ "</SendDataResult>"
									+ "</SendDataResponse></soap:Body></soap:Envelope>");
							break;
						}

						try {
							Thread.currentThread().sleep(SLEEPTIME, 0);
						} catch (InterruptedException e) {
							LOG.debug("Error delivering the data to destination:\n"
									+ e.getMessage());
							resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
									+ "<soap:Envelope "
									+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
									+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
									+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
									+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
									+ "<SendDataResult>"
									+ e.getMessage()
									+ "</SendDataResult></SendDataResponse>"
									+ "</soap:Body></soap:Envelope>");
						}
						++numRetries;
					}
					bytesRead = cis.read(buffer);
					if (bytesRead > 0) {
						numRetries = 0;
						response = response.concat(new String(buffer, 0,
								bytesRead));
						total += bytesRead;
					}
				} while (bytesRead != -1);

				String[] s = response.split("\r\n\r\n");
				if (s.length > 0) {
					response = s[1];
				} else {
					// In case the SOAP response from the service is empty.
					response = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope "
							+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
							+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
							+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
							+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
							+ "<SendDataResult>"
							+ "No SOAP response from the service"
							+ "</SendDataResult></SendDataResponse></soap:Body></soap:Envelope>";
				}

				LOG.debug("Response:\n" + response);
				resp.setData(response);
				cos.close();
				cis.close();
			} catch (MalformedURLException e) {
				LOG.debug("Error delivering the data to destination:\n"
						+ e.getMessage());
				resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope "
						+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
						+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
						+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
						+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
						+ "<SendDataResult>"
						+ e.getMessage()
						+ "</SendDataResult>"
						+ "</SendDataResponse></soap:Body></soap:Envelope>");
			} catch (UnknownHostException e) {
				LOG.debug("Error delivering the data to destination:\n"
						+ e.getMessage());
				resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope "
						+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
						+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
						+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
						+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
						+ "<SendDataResult>"
						+ e.getMessage()
						+ "</SendDataResult>"
						+ "</SendDataResponse></soap:Body></soap:Envelope>");
			} catch (IOException e) {
				LOG.debug("Error delivering the data to destination:\n"
						+ e.getMessage());
				resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope "
						+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
						+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
						+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
						+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
						+ "<SendDataResult>"
						+ e.getMessage()
						+ "</SendDataResult>"
						+ "</SendDataResponse></soap:Body></soap:Envelope>");
			}
		} else {
			// It is a POST request.
			try {
				// URL urlEndpoint = new URL(endpoint);
				String post;
				if (urlEndpoint.getQuery() != null) {
					post = "POST " + urlEndpoint.getPath() + "?"
							+ urlEndpoint.getQuery() + " HTTP/1.0\r\n";
				} else {
					post = "POST " + urlEndpoint.getPath() + " HTTP/1.0\r\n";
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
						header = "Content-Length: " + headers[1].length()
								+ "\r\n";
					} else if (aux.contains("content-length")) {
						header = "content-length: " + headers[1].length()
								+ "\r\n";
					} else if (aux.toLowerCase().contains("host")) {
						header = "Host: " + urlEndpoint.getHost() + ":"
								+ urlEndpoint.getPort() + "\r\n";
					} else {
						header = aux + "\r\n";
					}

					if ((aux.contains("Connection:"))
							|| (aux.contains("connection:"))) {
						header = "";
					}
					post = post + header;
				}

				String SOAPMessage = post + "\r\n" + headers[1];
				LOG.debug("Received SOAP message at the end of the tunnel:\n"
						+ SOAPMessage);
				Socket clientSocket = new Socket(urlEndpoint.getHost(),
						urlEndpoint.getPort());
				OutputStream cos = clientSocket.getOutputStream();
				cos.write(SOAPMessage.getBytes());
				InputStream cis = clientSocket.getInputStream();
				String response = "";
				byte[] buffer = new byte[BUFFSIZE];

				int bytesRead = 0;
				int total = 0;
				int numRetries = 0;
				do {
					if (cis.available() == 0) {
						if (numRetries >= MAXNUMRETRIES) {
							LOG.debug("Error delivering the data to destination:\n"
									+ "Data not available on service. Max Number of retries "
									+ "reached: " + MAXNUMRETRIES);
							resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
									+ "<soap:Envelope "
									+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
									+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
									+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
									+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
									+ "<SendDataResult>"
									+ "Data not available on service. Max Number "
									+ "of retries reached: "
									+ MAXNUMRETRIES
									+ "</SendDataResult>"
									+ "</SendDataResponse></soap:Body></soap:Envelope>");
							break;
						}

						try {
							Thread.currentThread().sleep(SLEEPTIME, 0);
						} catch (InterruptedException e) {
							LOG.debug("Error delivering the data to destination:\n"
									+ e.getMessage());
							resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
									+ "<soap:Envelope "
									+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
									+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
									+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
									+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
									+ "<SendDataResult>"
									+ e.getMessage()
									+ "</SendDataResult>"
									+ "</SendDataResponse></soap:Body></soap:Envelope>");
						}
						++numRetries;
					}

					bytesRead = cis.read(buffer);
					if (bytesRead > 0) {
						numRetries = 0;
						response = response.concat(new String(buffer, 0,
								bytesRead));
						total += bytesRead;
					}
				} while (bytesRead != -1);

				String[] s = response.split("\r\n\r\n");
				if (s.length > 1) {
					response = s[1];
				} else {
					// In case the SOAP response from the service is empty.
					response = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
							+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
							+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
							+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
							+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
							+ "<SendDataResult>"
							+ "No SOAP response from the service"
							+ "</SendDataResult></SendDataResponse></soap:Body></soap:Envelope>";
				}

				LOG.debug("Response:\n" + response);
				resp.setData(response);
				cos.close();
				cis.close();
			} catch (MalformedURLException e) {
				LOG.debug("Error delivering the data to destination:\n"
						+ e.getMessage());
				resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
						+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
						+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
						+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
						+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
						+ "<SendDataResult>" + e.getMessage()
						+ "</SendDataResult>"
						+ "</SendDataResponse></soap:Body></soap:Envelope>");
			} catch (UnknownHostException e) {
				LOG.debug("Error delivering the data to destination:\n"
						+ e.getMessage());
				resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
						+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
						+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
						+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
						+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
						+ "<SendDataResult>" + e.getMessage()
						+ "</SendDataResult>"
						+ "</SendDataResponse></soap:Body></soap:Envelope>");
			} catch (IOException e) {
				LOG.debug("Error delivering the data to destination:\n"
						+ e.getMessage());
				resp.setData("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
						+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
						+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
						+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
						+ "<soap:Body><SendDataResponse xmlns=\"http://se.cnet.hydra/\">"
						+ "<SendDataResult>" + e.getMessage()
						+ "</SendDataResult>"
						+ "</SendDataResponse></soap:Body></soap:Envelope>");
			}
		}
		return resp;

	}

	/**
	 * Receives a message over the specific communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param data
	 * @return
	 */
	public NMResponse receiveData(HID senderHID, HID receiverHID, byte[] data) {
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
//		throw new UnsupportedOperationException(this.getClass().getName()
//				+ " does not support broadcasting messages");
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
		// TODO Auto-generated method stub

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
			LOG.debug(
					"Unable to add endpoint " + endpoint + " for HID "
							+ hid.toString(), e);
		}
		return false;
	}

	@Override
	public boolean removeEndpoint(HID hid) {
		return this.hidUrlMap.remove(hid) != null;
	}

}
