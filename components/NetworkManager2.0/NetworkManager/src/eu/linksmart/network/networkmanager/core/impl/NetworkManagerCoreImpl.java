package eu.linksmart.network.networkmanager.core.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import eu.linksmart.network.ErrorMessage;
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
public class NetworkManagerCoreImpl implements NetworkManagerCore, MessageDistributor {
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
	private static String BACKBONE_SOAP = "BackboneSOAPImpl";
	private static String NETWORK_MGR_ENDPOINT = "http://localhost:8082/axis/services/NetworkManager";

	/**
	 * logger
	 */
	Logger LOG = Logger.getLogger(NetworkManagerCoreImpl.class.getName());

	/* fields */
	private NetworkManagerCoreConfigurator configurator;
	private Map<String, ArrayList<MessageProcessor>> msgObservers = new HashMap<String, ArrayList<MessageProcessor>>();

	/**
	 * Component activation method.
	 * @param context
	 */
	protected void activate(ComponentContext context) {

		LOG.debug(STARTING_MESSAGE);

		init(context);

		LOG.debug(STARTED_MESSAGE);

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

	/**
	 * Initializes the component, i.e. creates own HID, and registers the NM
	 * status servlets.
	 * 
	 * @param context
	 */
	private void init(ComponentContext context) {
		this.configurator = new NetworkManagerCoreConfigurator(this,
				context.getBundleContext());
		this.configurator.registerConfiguration();
		this.myDescription = this.configurator
				.get(NetworkManagerCoreConfigurator.NM_DESCRIPTION);
		Part[] attributes = { new Part(HIDAttribute.DESCRIPTION.name(),
				this.myDescription) };

		// Create a local HID with SOAP Backbone for NetworkManager
		// TODO Make the Backbone a constant or enum somewhere. find another way
		// to tell the BackboneRouter that my local network manager's HID has
		// BackboneSOAPImpl.
		try {
			this.myHID = createHID(attributes, NETWORK_MGR_ENDPOINT,
					"eu.linksmart.network.backbone.impl.soap.BackboneSOAPImpl")
					.getHID();
		} catch (RemoteException e) {
			LOG.error(
					"PANIC - RemoteException thrown on local access of own method",
					e);
		}
		connectionManager.setOwnerHID(myHID);

		// Init Servlets
		// TODO implement servlet registration with HttpService in Declarative
		// Services style
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

	@Override
	public HID getHID() {
		return this.myHID;
	}

	@Override
	public HIDInfo createHID(Part[] attributes, String endpoint,
			String backboneName) throws RemoteException {
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

		// PID should be unique, if the PID is already used, throw exception
		for (Part attribute : attributes) {
			if (attribute.getKey().equalsIgnoreCase("PID")) {
				HIDInfo hidInfo = getHIDByPID(attribute.getValue());
				if (hidInfo != null) {
					throw new IllegalArgumentException(
							"PID already in use. Please choose a different one.");
				}
			}
		}

		HIDInfo newHID = this.identityManager
				.createHIDForAttributes(attributes);
		List<SecurityProperty> properties = this.backboneRouter
				.getBackboneSecurityProperties(backboneName);
		// register HID with backbone policies in connection manager
		this.connectionManager.registerHIDPolicy(newHID.getHID(), properties);
		// add route to selected backbone
		this.backboneRouter.addRouteToBackbone(newHID.getHID(), backboneName,
				endpoint);
		return newHID;
	}

	@Override
	public NMResponse sendData(HID sender, HID receiver, byte[] data,
			boolean synch) throws RemoteException {
		return this.sendMessage(new Message(Message.TOPIC_APPLICATION, sender,
				receiver, data), synch);
	}

	@Override
	public boolean removeHID(HID hid) throws RemoteException {
		Boolean hidRemoved = this.identityManager.removeHID(hid);
		this.connectionManager.deleteHIDPolicy(hid);

		return hidRemoved;
	}

	@Override
	public NMResponse receiveDataSynch(HID senderHID, HID receiverHID,
			byte[] data) {
		// open message only if it is for local entity or is broadcast
		HIDInfo receiverHIDInfo = null;
		if (receiverHID != null) {
			receiverHIDInfo = identityManager.getHIDInfo(receiverHID);
		}
		if (receiverHID == null
				|| (receiverHIDInfo != null && identityManager.getLocalHIDs()
						.contains(receiverHIDInfo))) {
			// get connection belonging to HIDs
			Connection conn;
			try {
				if (receiverHID == null) {
					// broadcast message
					conn = connectionManager.getBroadcastConnection(senderHID);
				} else {
					// to get proper connection use my HID
					conn = connectionManager.getConnection(myHID, senderHID);
				}
			} catch (Exception e) {
				LOG.warn(
						"Error getting connection for HIDs: "
								+ senderHID.toString() + " " + myHID.toString(),
						e);
				NMResponse response = new NMResponse();
				response.setStatus(NMResponse.STATUS_ERROR);
				response.setMessage("Error getting connection for HIDs: "
						+ senderHID.toString() + " " + myHID.toString());
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

			if (msg != null && msg.getData() != null
					&& msg.getData().length != 0) {
				/*
				 * check if message is not intended for host HID, if yes and it
				 * has not been processed drop it
				 */
				if (msg.getReceiverHID() == null) {
					LOG.warn("Received a message which has not been processed");
					NMResponse response = new NMResponse();
					response.setStatus(NMResponse.STATUS_ERROR);
					response.setMessage("Received a message which has not been processed");
					return response;
				} else {
					// if this is not the response first forward it
					if (!msg.getReceiverHID().equals(senderHID)) {
						// forward over sendMessage method of this and return
						// response
						// here the response message should include a message
						// object
						msg = sendMessageSynch(msg, this.myHID,
								msg.getReceiverHID()).getMessageObject();
					}
					NMResponse nmresp = new NMResponse();
					if (msg != null && msg.getReceiverHID().equals(senderHID)) {
						// create response with connection and etc
						nmresp.setStatus(NMResponse.STATUS_SUCCESS);
						try {
							nmresp.setMessage(new String(conn
									.processMessage(msg)));
						} catch (Exception e) {
							nmresp.setStatus(NMResponse.STATUS_ERROR);
						}
					} else {

						nmresp.setStatus(NMResponse.STATUS_ERROR);
						nmresp.setMessage("Error in processing request");
					}
					return nmresp;
				}

			} else {
				NMResponse response = new NMResponse();
				response.setStatus(NMResponse.STATUS_SUCCESS);
				return response;
			}
		} else {
			return backboneRouter.sendDataSynch(senderHID, receiverHID, data);
		}
	}

	@Override
	public NMResponse receiveDataAsynch(HID senderHID, HID receiverHID,
			byte[] data) {
		// open message only if it is for local entity or is broadcast
		HIDInfo receiverHIDInfo = null;
		if (receiverHID != null) {
			receiverHIDInfo = identityManager.getHIDInfo(receiverHID);
		}
		if (receiverHID == null
				|| (receiverHIDInfo != null && identityManager.getLocalHIDs()
						.contains(receiverHIDInfo))) {
			// get connection belonging to HIDs
			Connection conn;
			try {
				if (receiverHID == null) {
					// broadcast message
					conn = connectionManager.getBroadcastConnection(senderHID);
				} else {
					conn = connectionManager.getConnection(receiverHID,
							senderHID);
				}
			} catch (Exception e) {
				LOG.warn(
						"Error getting connection for HIDs: "
								+ senderHID.toString() + " "
								+ receiverHID.toString(), e);
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
			if (msg != null && msg.getData() != null
					&& msg.getData().length != 0) {
				/*
				 * check if message is not intended for host HID, if yes and it
				 * has not been processed drop it
				 */
				if (msg.getReceiverHID() == null) {
					// || msg.getReceiverHID().equals(this.myHID)) {
					LOG.warn("Received a message which has not been processed");
					NMResponse response = new NMResponse();
					response.setStatus(NMResponse.STATUS_ERROR);
					response.setMessage("Received a message which has not been processed");
					return response;
				}
				/*
				 * send message over sendMessage method of this and return
				 * response of it
				 */
				return sendMessageAsynch(msg, this.myHID, msg.getReceiverHID());
			} else {
				NMResponse response = new NMResponse();
				response.setStatus(NMResponse.STATUS_SUCCESS);
				return response;
			}
		} else {
			return backboneRouter.sendDataAsynch(senderHID, receiverHID, data);
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

	@Override
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

	@Override
	public void unsubscribe(String topic, MessageProcessor observer) {
		if (msgObservers.containsKey(topic)) {
			msgObservers.get(topic).remove(observer);
		}
	}

	@Override
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
			response.setMessage("Could not create packet from message from HID: "
					+ message.getSenderHID());
			return response;
		}
		NMResponse response = this.backboneRouter
				.broadcastData(senderHID, data);

		return response;
	}

	@Override
	public NMResponse sendMessage(Message message, boolean synch) {
		HID senderHID = message.getSenderHID();
		HID receiverHID = message.getReceiverHID();
		if (synch)
			return sendMessageSynch(message, senderHID, receiverHID);
		else
			return sendMessageAsynch(message, senderHID, receiverHID);
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
	private NMResponse sendMessageAsynch(Message message, HID senderHID,
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
			response.setMessage("Could not create packet from message from HID: "
					+ message.getSenderHID());
			return response;
		}
		NMResponse response = this.backboneRouter.sendDataAsynch(senderHID,
				receiverHID, data);

		return response;
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
	private NMResponse sendMessageSynch(Message message, HID senderHID,
			HID receiverHID) {
		byte[] data = null;
		NMResponse response = new NMResponse();
		Message tempMessage = message;

		try {
			Connection connection = this.connectionManager.getConnection(
					receiverHID, senderHID);

			// process outgoing message
			data = connection.processMessage(tempMessage);
			response = this.backboneRouter.sendDataSynch(senderHID,
					receiverHID, data);
			// process response message with logical endpoints in connection
			// with physical endpoints
			tempMessage = connection.processData(message.getReceiverHID(),
					message.getSenderHID(), response.getMessage().getBytes());
			// repeat sending and receiving until security protocol is over
			while (tempMessage != null
					&& tempMessage
							.getTopic()
							.contentEquals(
									CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC)) {
				response = this.backboneRouter.sendDataSynch(senderHID,
						receiverHID, connection.processMessage(tempMessage));
				tempMessage = connection.processData(message.getReceiverHID(),
						message.getSenderHID(), response.getMessage()
								.getBytes());
			}
		} catch (Exception e) {
			LOG.warn("Could not create packet from message from HID: "
					+ message.getSenderHID());
			response = new NMResponse();
			response.setStatus(NMResponse.STATUS_ERROR);
			response.setMessage("Could not create packet from message from HID: "
					+ message.getSenderHID());
			return response;
		}

		if (tempMessage.getClass().equals(ErrorMessage.class)) {
			response.setStatus(NMResponse.STATUS_ERROR);
		} else {
			response.setStatus(NMResponse.STATUS_SUCCESS);
		}
		response.setMessageObject(tempMessage);
		response.setMessage(new String(tempMessage.getData()));
		return response;
	}

	/**
	 * Sets the number of minutes before a connection is closed.
	 * 
	 * @param timeout the timeout to set, in minutes.
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
	@Override
	public HIDInfo createCryptoHID(String xmlAttributes, String endpoint) {
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
		hid = identityManager.createHIDForAttributes(newAttributes).getHID();

		// add it to backbonesoap as this method is deprecated anyway
		List<SecurityProperty> properties = this.backboneRouter
				.getBackboneSecurityProperties(BACKBONE_SOAP);
		// register HID with backbone policies in connection manager
		this.connectionManager.registerHIDPolicy(hid, properties);
		// add route to selected backbone
		this.backboneRouter.addRouteToBackbone(hid, BACKBONE_SOAP, endpoint);

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
	@Override
	public HIDInfo createCryptoHIDFromReference(String certRef, String endpoint) {
		/*
		 * as the method is implementation specific we have to check whether the
		 * appropriate implementation class is referenced
		 */
		if (!identityManager.getIdentifier().contentEquals(
				CRYPTO_HID_IMPLEMENTATION)) {
			return null;
		}
		Part[] attributes = { new Part(HIDAttribute.CERT_REF.name(), certRef) };
		HID hid = identityManager.createHIDForAttributes(attributes).getHID();

		// add it to backbonesoap as this method is deprecated anyway
		List<SecurityProperty> properties = this.backboneRouter
				.getBackboneSecurityProperties(BACKBONE_SOAP);
		// register HID with backbone policies in connection manager
		this.connectionManager.registerHIDPolicy(hid, properties);
		// add route to selected backbone
		this.backboneRouter.addRouteToBackbone(hid, BACKBONE_SOAP, endpoint);

		return identityManager.getHIDInfo(hid);
	}

	@Override
	public String[] getAvailableBackbones() {
		List<String> backbones = this.backboneRouter.getAvailableBackbones();
		String[] backboneNames = new String[backbones.size()];
		return backbones.toArray(backboneNames);
	}

	@Override
	public void addRemoteHID(HID senderHID, HID remoteHID) {
		this.backboneRouter.addRouteForRemoteHID(senderHID, remoteHID);
	}

	@Override
	public HIDInfo[] getHIDByDescription(String description) {

		Part part_description = new Part(HIDAttribute.DESCRIPTION.name(),
				description);

		return getHIDByAttributes(new Part[] { part_description });
	}

	@Override
	public HIDInfo getHIDByPID(String PID) throws IllegalArgumentException {
		if (PID == null || PID.length() == 0) {
			throw new IllegalArgumentException("PID not specificed");
		}

		Part part_description = new Part(HIDAttribute.PID.name(), PID);

		HIDInfo[] hids = getHIDByAttributes(new Part[] { part_description });

		if (hids.length > 1) {
			throw new RuntimeException("More than one hid found to passed PID");
		} else if (hids.length == 1) {
			return hids[0];
		} else
			return null;
	}

	@Override
	public HIDInfo[] getHIDByAttributes(Part[] attributes) {
		return getHIDByAttributes(attributes, true);
	}

	@Override
	public HIDInfo[] getHIDByAttributes(Part[] attributes, boolean isConjunction) {
		String relation = (isConjunction) ? "&&" : "||";

		StringBuilder query = new StringBuilder();
		for (Part attribute : attributes) {
			query.append("(" + attribute.getKey() + "==" + attribute.getValue()
					+ ")" + relation);
		}
		query.delete(query.length() - 2, query.length());
		Set<HIDInfo> hids = identityManager.getHIDsByAttributes(query
				.toString());

		return hids.toArray(new HIDInfo[hids.size()]);
	}

	@Override
	public HIDInfo[] getHIDByQuery(String query) {

		Set<HIDInfo> hids = identityManager.getHIDsByAttributes(query);
		return hids.toArray(new HIDInfo[hids.size()]);
	}

}
