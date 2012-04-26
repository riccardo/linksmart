package eu.linksmart.network.networkmanager.core.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import eu.linksmart.network.HID;
import eu.linksmart.network.HIDAttribute;
import eu.linksmart.network.HIDInfo;
import eu.linksmart.network.Message;
import eu.linksmart.network.MessageDistributor;
import eu.linksmart.network.MessageProcessor;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.connection.Connection;
import eu.linksmart.network.connection.ConnectionManager;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.communication.SecurityProperty;
import eu.linksmart.tools.GetNetworkManagerStatus;
import eu.linksmart.tools.NetworkManagerApplicationStatus;
import eu.linksmart.utils.Part;
import eu.linksmart.utils.PartConverter;

/*
 * Core implementation of NetworkManagerCore Interface
 */
public class NetworkManagerCoreImpl implements NetworkManagerCore,
		MessageDistributor {
	/** The used identity manager **/
	protected IdentityManager identityManager;
	/** The used backbone router **/
	protected BackboneRouter backboneRouter;
	/** The used connection manager **/
	protected ConnectionManager connectionManager = new ConnectionManager();
	/** The HID of this NetworkManager and IdentityManager **/
	protected HID myHID;

	protected String myDescription;

	/* Constants */
	private static String NETWORK_MGR_CORE = NetworkManagerCoreImpl.class
			.getSimpleName();
	private static final String STARTED_MESSAGE = "Started" + NETWORK_MGR_CORE;
	private static final String STARTING_MESSAGE = "Starting"
			+ NETWORK_MGR_CORE;
	public static String SUCCESSFUL_PROCESSING = "OK";
	public static String ERROR_PROCESSING = "ERROR";
	/** The name of the class implementing CryptoHID **/
	private static String CRYPTO_HID_IMPLEMENTATION = "IdentityManagerCryptoImpl";

	/*
	 * logger
	 */
	Logger LOG = Logger.getLogger(NetworkManagerCoreImpl.class.getName());

	/* fields */
	private NetworkManagerCoreConfigurator configurator;
	private Map<String, ArrayList<MessageProcessor>> msgObservers = new HashMap<String, ArrayList<MessageProcessor>>();

	protected void activate(ComponentContext context) {

		LOG.debug(STARTING_MESSAGE);

		init(context);

		LOG.debug(STARTED_MESSAGE);

	}

	private void init(ComponentContext context) {
		this.configurator = new NetworkManagerCoreConfigurator(this, context
				.getBundleContext());
		this.configurator.registerConfiguration();
		this.myDescription = this.configurator
				.get(NetworkManagerCoreConfigurator.NM_DESCRIPTION);
		Part[] attributes = { new Part(HIDAttribute.DESCRIPTION.name(),
				this.myDescription) };
		this.myHID = this.identityManager.createHIDForAttributes(attributes);

		// Init Servlets
		HttpService http = (HttpService) context.locateService("HttpService");
		try {
			http.registerServlet("/NetworkManagerStatus",
					new NetworkManagerApplicationStatus(context, this,
							identityManager, backboneRouter), null, null);
			http.registerServlet("/GetNetworkManagerStatus",
					new GetNetworkManagerStatus(this, identityManager,
							backboneRouter), null, null);
			http.registerResources("/files", "/resources", null);
		} catch (Exception e) {
			LOG.error("Error registering servlets", e);
		}
	}

	public HID getHID() {
		return this.myHID;
	}

	@Override
	public HID createHID(Part[] attributes, String endpoint, String backboneName)
			throws RemoteException {
		// check if backbone exist before creating the HID
		boolean backboneFound = false;
		for (String backbone : this.backboneRouter.getAvailableBackbones()) {
			if (backbone.equals(backboneName)) {
				backboneFound = true;
				break;
			}
		}
		if (!backboneFound) {
			throw new IllegalArgumentException(
					"Required backbone not available");
		}

		HID newHID = this.identityManager.createHIDForAttributes(attributes);
		List<SecurityProperty> properties = this.backboneRouter
				.getBackboneSecurityProperties(backboneName);
		// register HID with backbone policies in connection manager
		this.connectionManager.registerHIDPolicy(newHID, properties);
		// add route to selected backbone
		this.backboneRouter.addRouteToBackbone(newHID, backboneName, endpoint);
		return newHID;
	}

	public NMResponse sendData(HID sender, HID receiver, byte[] data)
			throws RemoteException {
		return this.sendMessage(new Message(Message.TOPIC_APPLICATION, sender,
				receiver, data));
	}

	public boolean removeHID(HID hid) throws RemoteException {
		Boolean hidRemoved = this.identityManager.removeHID(hid);
		this.connectionManager.deleteHIDPolicy(hid);

		return hidRemoved;
	}

	protected void deactivate(ComponentContext context) {
		System.out.println(NETWORK_MGR_CORE + "stopped");
	}

	protected void bindCommunicationSecurityManager(
			CommunicationSecurityManager commSecMgr) {
		this.connectionManager.setCommunicationSecurityManager(commSecMgr);
	}

	protected void unbindCommunicationSecurityManager(
			CommunicationSecurityManager commSecMgr) {
		this.connectionManager.removeCommunicationSecurityManager(commSecMgr);
	}

	protected void bindIdentityManager(IdentityManager identityManager) {
		this.identityManager = identityManager;
	}

	protected void unbindIdentityManager(IdentityManager identityMgr) {
		this.identityManager = null;

	}

	protected void bindBackboneRouter(BackboneRouter backboneRouter) {
		this.backboneRouter = backboneRouter;
	}

	protected void unbindBackboneRouter(BackboneRouter backboneRouter) {
		this.backboneRouter = null;
	}

	public NMResponse receiveData(HID senderHID, HID receiverHID, byte[] data) {
		// get connection belonging to HIDs
		Connection conn;
		try {
			if (receiverHID == null) {
				// broadcast message
				conn = connectionManager.getBroadcastConnection(senderHID);
			} else {
				conn = connectionManager.getConnection(receiverHID, senderHID);
			}
		} catch (Exception e) {
			LOG.warn("Error getting connection for HIDs: "
					+ senderHID.toString() + " " + receiverHID.toString(), e);
			NMResponse response = new NMResponse();
			response.setStatus(NMResponse.STATUS_ERROR);
			response.setMessage("Error getting connection for HIDs: "
					+ senderHID.toString() + " " + receiverHID.toString());
			return response;
		}

		Message msg = conn.processData(senderHID, receiverHID, data);
		String topic = msg.getTopic();
		// go through MsgObservers for additional processing
		List<MessageProcessor> observers = msgObservers.get(topic);
		if (observers != null) {
			for (MessageProcessor observer : observers) {
				msg = observer.processMessage(msg);
				if (msg == null || msg.getData() == null) {
					NMResponse nmresp = new NMResponse();
					nmresp.setStatus(NMResponse.STATUS_SUCCESS);
					return nmresp;
				}
			}
		}

		// if message is still existing it has to be forwarded
		if (msg != null && msg.getData() != null && msg.getData().length != 0) {
			/*
			 * check if message is not intended for host HID, if yes and it has
			 * not been processed drop it
			 */
			if (msg.getReceiverHID() == null
					|| msg.getReceiverHID().equals(this.myHID)) {
				LOG.warn("Received a message which has not been processed");
				NMResponse response = new NMResponse();
				response.setStatus(NMResponse.STATUS_ERROR);
				response
						.setMessage("Received a message which has not been processed");
				return response;
			}
			/*
			 * send message over sendMessage method of this and return response
			 * of it
			 */
			return sendMessage(msg, this.myHID, receiverHID);
		} else {
			NMResponse response = new NMResponse();
			response.setStatus(NMResponse.STATUS_SUCCESS);
			return response;
		}
	}

	/**
	 * Updates the description of this NetworkManager instance. This update
	 * request is also forwarded to the IdentityManager
	 * 
	 * @param description
	 */
	public void updateDescription(String description) {
		this.myDescription = description;

		Properties attributes = new Properties();
		attributes.setProperty(HIDAttribute.DESCRIPTION.name(), description);

		this.identityManager.updateHIDInfo(this.myHID, attributes);

	}

	public void subscribe(String topic, MessageProcessor observer) {
		// check if topic already exists
		if (msgObservers.containsKey(topic)) {
			// add new observer to topic
			if (msgObservers.get(topic).contains(observer)) {
				// observer is already in list
				return;
			} else {
				msgObservers.get(topic).add(observer);
			}
		} else {
			// create topic and add observer
			msgObservers.put(topic, new ArrayList<MessageProcessor>());
			msgObservers.get(topic).add(observer);
		}

	}

	public void unsubscribe(String topic, MessageProcessor observer) {
		if (msgObservers.containsKey(topic)) {
			msgObservers.get(topic).remove(observer);
		}
	}

	// public HID createHID(byte[] data) throws IOException {
	//
	// Properties attributes = this.connectionManager.getHIDAttributes(data);
	//
	// HID newHID = this.identityManager.createHIDForAttributes(attributes);
	//
	// return newHID;
	// }

	public NMResponse broadcastMessage(Message message) {
		HID senderHID = message.getSenderHID();
		byte[] data;
		try {
			data = this.connectionManager.getBroadcastConnection(senderHID)
					.processMessage(message);
		} catch (Exception e) {
			LOG.warn("Could not create packet from message from HID: "
					+ message.getSenderHID(), e);
			NMResponse response = new NMResponse();
			response.setStatus(NMResponse.STATUS_ERROR);
			response
					.setMessage("Could not create packet from message from HID: "
							+ message.getSenderHID());
			return response;
		}
		NMResponse response = this.backboneRouter
				.broadcastData(senderHID, data);

		return response;
	}

	public NMResponse sendMessage(Message message) {
		HID senderHID = message.getReceiverHID();
		HID receiverHID = message.getSenderHID();
		return sendMessage(message, senderHID, receiverHID);
	}

	/**
	 * Internal method for sending messages with more possible parameters. The
	 * message contains the original sender and the final receiver but the
	 * parameters determine which connection to use for sending.
	 * 
	 * @param message
	 *            Message to send
	 * @param senderHID
	 *            Sender endpoint of connection to open
	 * @param receiverHID
	 *            Receiver endpoint of connection to open
	 * @return
	 */
	private NMResponse sendMessage(Message message, HID senderHID,
			HID receiverHID) {
		byte[] data = null;
		try {
			Connection connection = this.connectionManager.getConnection(
					receiverHID, senderHID);
			data = connection.processMessage(message);
		} catch (Exception e) {
			LOG.warn("Could not create packet from message from HID: "
					+ message.getSenderHID());
			NMResponse response = new NMResponse();
			response.setStatus(NMResponse.STATUS_ERROR);
			response
					.setMessage("Could not create packet from message from HID: "
							+ message.getSenderHID());
			return response;
		}
		NMResponse response = this.backboneRouter.sendData(senderHID,
				receiverHID, data);

		return response;
	}

	/**
	 * Sets the number of minutes before a connection is closed.
	 * 
	 * @param timeout
	 */
	protected void setConnectionTimeout(int timeout) {
		this.connectionManager.setConnectionTimeout(timeout);
	}

	/**
	 * Operation to create an crypto HID providing the persistent attributes for
	 * this HID and the endpoint of the service behind it (for service
	 * invocation). The crypto HID is the enhanced version of HIDs, that allow
	 * to store persistent information on them (through certificates) and
	 * doesn't propagate the information stored on it. In order to exchange the
	 * stored information, the Session Domain Protocol is used. It returns a
	 * certificate reference that point to the certificate generated. The next
	 * time the HID needs to be created, using the same attributes, the
	 * certificate reference can be used.
	 * 
	 * @param xmlAttributes
	 *            The attributes (persistent) associated with this HID. This
	 *            attributes are stored inside the certificate and follow the
	 *            Java {@link java.util.Properties} xml schema.
	 * @param endpoint
	 *            The endpoint of the service (if there is a service behind).
	 * @return A {@link eu.linksmart.network.ws.CrypyoHIDResult} containing
	 *         {@link String} representation of the HID and the certificate
	 *         reference (UUID) Null if no CryptoHID implementation referenced
	 */
	public HIDInfo createCryptoHID(String xmlAttributes) {
		/*
		 * as the method is implementation specific we have to check whether the
		 * appropriate implementation class is referenced
		 */

		if (!identityManager.getIdentifier().contentEquals(
				CRYPTO_HID_IMPLEMENTATION)) {
			return null;
		}
		HID hid = null;
		Properties attributes = new Properties();
		try {
			attributes.loadFromXML(new ByteArrayInputStream(xmlAttributes
					.getBytes()));
		} catch (IOException e) {
			LOG.error("Cannot parse attributes!", e);
			return null;
		}
		Part[] newAttributes = PartConverter.fromProperties(attributes);
		hid = identityManager.createHIDForAttributes(newAttributes);
		return identityManager.getHIDInfo(hid);
	}

	/**
	 * Operation to create an crypto HID providing a certificate reference (from
	 * a previously created cryptoHID) and an endpoint The crypto HID is the
	 * enhanced version of HIDs, that allow to store persistent information on
	 * them (through certificates)
	 * 
	 * @param certRef
	 *            The certificate reference from a previously generated
	 *            cryptoHID.
	 * @param endpoint
	 *            The endpoint of the service (if there is a service behind).
	 * @return The {@link String} representation of the HID.
	 */
	public HIDInfo createCryptoHIDFromReference(String certRef) {
		/*
		 * as the method is implementation specific we have to check whether the
		 * appropriate implementation class is referenced
		 */
		if (!identityManager.getIdentifier().contentEquals(
				CRYPTO_HID_IMPLEMENTATION)) {
			return null;
		}
		Part[] attributes = { new Part(HIDAttribute.CERT_REF.name(), certRef) };
		HID hid = identityManager.createHIDForAttributes(attributes);
		return identityManager.getHIDInfo(hid);
	}

	public String[] getAvailableBackbones() {
		return (String[]) this.backboneRouter.getAvailableBackbones().toArray();
	}

	@Override
	public void addRemoteHID(HID senderHID, HID remoteHID) {
		this.backboneRouter.addRouteForRemoteHID(senderHID, remoteHID);
	}

}
