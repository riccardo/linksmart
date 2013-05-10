package eu.linksmart.network.networkmanager.core.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import eu.linksmart.network.ErrorMessage;
import eu.linksmart.network.Message;
import eu.linksmart.network.MessageDistributor;
import eu.linksmart.network.MessageProcessor;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.Registration;
import eu.linksmart.network.ServiceAttribute;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.connection.Connection;
import eu.linksmart.network.connection.ConnectionManager;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.communication.SecurityProperty;
import eu.linksmart.tools.GetNetworkManagerStatus;
import eu.linksmart.utils.Part;

/*
 * Core implementation of NetworkManagerCore Interface
 */
public class NetworkManagerCoreImpl implements NetworkManagerCore, MessageDistributor {
	/** The used identity manager **/
	protected IdentityManager identityManager;
	/** The used backbone router **/
	protected BackboneRouter backboneRouter;
	/** The used connection manager **/
	protected ConnectionManager connectionManager = new ConnectionManager(this);
	/** The VirtualAddress of this NetworkManager and IdentityManager **/
	protected VirtualAddress myVirtualAddress;
	protected String myDescription;

	/* Constants */
	private static String NETWORK_MGR_CORE = NetworkManagerCoreImpl.class
			.getSimpleName();
	private static final String STARTED_MESSAGE = "Started" + NETWORK_MGR_CORE;
	private static final String STARTING_MESSAGE = "Starting"
			+ NETWORK_MGR_CORE;
	private static final String COMMUNICATION_PARAMETERS_ERROR = "Could not establish common communication parameters with remote endpoint";
	public static String SUCCESSFUL_PROCESSING = "OK";
	public static String ERROR_PROCESSING = "ERROR";
	private static String NETWORK_MGR_ENDPOINT = "http://localhost:9090/cxf/services/NetworkManager";

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

		LOG.info(STARTING_MESSAGE);

		init(context);

		LOG.info(STARTED_MESSAGE);

	}

	protected void deactivate(ComponentContext context) {
		LOG.info(NETWORK_MGR_CORE + "stopped");
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
		this.connectionManager.setIdentityManager(identityManager);
	}

	protected void unbindIdentityManager(IdentityManager identityMgr) {
		this.identityManager = null;
		this.connectionManager.setIdentityManager(null);
	}

	protected void bindBackboneRouter(BackboneRouter backboneRouter) {
		this.backboneRouter = backboneRouter;
	}

	protected void unbindBackboneRouter(BackboneRouter backboneRouter) {
		this.backboneRouter = null;
	}

	/**
	 * Initializes the component, i.e. creates own VirtualAddress, and registers the NM
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
		Part[] attributes = { new Part(ServiceAttribute.DESCRIPTION.name(),
				this.myDescription) };

		// Create a local VirtualAddress with SOAP Backbone for NetworkManager
		// TODO Make the Backbone a constant or enum somewhere. find another way
		// to tell the BackboneRouter that my local network manager's VirtualAddress has
		// BackboneSOAPImpl.
		try {
			this.myVirtualAddress = registerService(attributes, NETWORK_MGR_ENDPOINT,
					"eu.linksmart.network.backbone.impl.soap.BackboneSOAPImpl")
					.getVirtualAddress();
		} catch (RemoteException e) {
			LOG.error(
					"PANIC - RemoteException thrown on local access of own method",
					e);
		}

		// Init Servlets
		// TODO implement servlet registration with HttpService in Declarative
		// Services style
		HttpService http = (HttpService) context.locateService("HttpService");
		try {

			http.registerServlet("/GetNetworkManagerStatus",
					new GetNetworkManagerStatus(this, identityManager,
							backboneRouter), null, null);
			http.registerResources("/files", "/resources", null);
		} catch (Exception e) {
			LOG.error("Error registering servlets", e);
		}
	}

	@Override
	public VirtualAddress getService() {
		return this.myVirtualAddress;
	}

	@Override
	public Registration registerService(Part[] attributes, String endpoint,
			String backboneName) throws RemoteException {

		// PID should be unique, if the PID is already used, throw exception
		for (Part attribute : attributes) {
			if (attribute.getKey().equalsIgnoreCase("PID")) {
				Registration serviceInfo = getServiceByPID(attribute.getValue());
				if (serviceInfo != null) {
					throw new IllegalArgumentException(
							"PID already in use. Please choose a different one.");
				}
			}
		}

		Registration newRegistration = this.identityManager.createServiceByAttributes(attributes);
		if(newRegistration != null) {
			List<SecurityProperty> properties = this.backboneRouter
					.getBackboneSecurityProperties(backboneName);
			if(properties != null) {
				// register VirtualAddress with backbone policies in connection manager
				this.connectionManager.registerServicePolicy(newRegistration.getVirtualAddress(), properties, true);
			}
			// add route to selected backbone
			this.backboneRouter.addRouteToBackbone(newRegistration.getVirtualAddress(), backboneName,
					endpoint);
		}
		return newRegistration;
	}

	@Override
	public NMResponse sendData(VirtualAddress sender, VirtualAddress receiver, byte[] data,
			boolean synch) throws RemoteException {
		return this.sendMessage(new Message(Message.TOPIC_APPLICATION, sender,
				receiver, data), synch);
	}

	@Override
	public boolean removeService(VirtualAddress virtualAddress) throws RemoteException {
		Boolean virtualAddressRemoved = this.identityManager.removeService(virtualAddress);
		this.connectionManager.deleteServicePolicy(virtualAddress);
		this.backboneRouter.removeRoute(virtualAddress, null);
		return virtualAddressRemoved;
	}

	@Override
	public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress,
			byte[] data) {
		// open message only if it is for local entity or is broadcast
		Registration receiverRegistrationInfo = null;
		if (receiverVirtualAddress != null) {
			receiverRegistrationInfo = identityManager.getServiceInfo(receiverVirtualAddress);
		}
		if (receiverVirtualAddress == null
				|| (receiverRegistrationInfo != null && identityManager.getLocalServices()
				.contains(receiverRegistrationInfo))) {
			// get connection belonging to services
			Connection conn;
			try {
				if (receiverVirtualAddress == null) {
					// broadcast message
					conn = connectionManager.getBroadcastConnection(senderVirtualAddress);
				} else {
					// to get proper connection use my VirtualAddress
					conn = getConnection(myVirtualAddress, senderVirtualAddress, data);
					//no common connection parameters could be established with the other end
					if(conn == null) {
						if(this.connectionManager.isHandshakeMessage(
								data, senderVirtualAddress, receiverVirtualAddress)) {
							return this.connectionManager.getDeclineHandshakeMessage(
									senderVirtualAddress, getService());
						} else {
							NMResponse response = new NMResponse(NMResponse.STATUS_ERROR);
							response.setMessage(COMMUNICATION_PARAMETERS_ERROR);
							return response;
						}
					}
				}
			} catch (Exception e) {
				LOG.warn(
						"Error getting connection for services: "
								+ senderVirtualAddress.toString() + " " + myVirtualAddress.toString(),
								e);
				NMResponse response = new NMResponse();
				response.setStatus(NMResponse.STATUS_ERROR);
				response.setMessage("Error getting connection for services: "
						+ senderVirtualAddress.toString() + " " + myVirtualAddress.toString());
				return response;
			}

			Message msg = conn.processData(senderVirtualAddress, receiverVirtualAddress, data);

			//drop error messages from further processing
			if(msg instanceof ErrorMessage) {
				NMResponse response = new NMResponse(NMResponse.STATUS_ERROR);
				if(msg.getData() != null) {
					response.setMessage(new String(msg.getData()));
				}
				return response;
			}
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
				 * check if message is not intended for host VirtualAddress, if yes and it
				 * has not been processed drop it
				 */
				if (msg.getReceiverVirtualAddress() == null) {
					LOG.warn("Received a message which has not been processed");
					NMResponse response = new NMResponse();
					response.setStatus(NMResponse.STATUS_ERROR);
					response.setMessage("Received a message which has not been processed");
					return response;
				} else {
					// if this is not the response first forward it
					if (!msg.getReceiverVirtualAddress().equals(senderVirtualAddress)) {
						// forward over sendMessage method of this and return
						// response
						// here the response message should include a message
						// object
						msg = sendMessageSynch(msg, this.myVirtualAddress,
								msg.getReceiverVirtualAddress()).getMessageObject();
					}
					NMResponse nmresp = new NMResponse();
					if (msg != null && msg.getReceiverVirtualAddress().equals(senderVirtualAddress)) {
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
			return backboneRouter.sendDataSynch(senderVirtualAddress, receiverVirtualAddress, data);
		}
	}

	@Override
	public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress,
			byte[] data) {
		// open message only if it is for local entity or is broadcast
		Registration receiverRegistrationInfo = null;
		if (receiverVirtualAddress != null) {
			receiverRegistrationInfo = identityManager.getServiceInfo(receiverVirtualAddress);
		}
		if (receiverVirtualAddress == null
				|| (receiverRegistrationInfo != null && identityManager.getLocalServices()
				.contains(receiverRegistrationInfo))) {
			// get connection belonging to services
			Connection conn;
			try {
				if (receiverVirtualAddress == null) {
					// broadcast message
					conn = connectionManager.getBroadcastConnection(senderVirtualAddress);
				} else {
					conn = getConnection(myVirtualAddress, senderVirtualAddress, data);
					//no common connection parameters could be established with the other end
					if(conn == null) {
						if(this.connectionManager.isHandshakeMessage(
								data, senderVirtualAddress, receiverVirtualAddress)) {
							return this.connectionManager.getDeclineHandshakeMessage(
									senderVirtualAddress, getService());
						} else {
							NMResponse response = new NMResponse(NMResponse.STATUS_ERROR);
							response.setMessage(COMMUNICATION_PARAMETERS_ERROR);
							return response;
						}
					}
				}
			} catch (Exception e) {
				LOG.warn(
						"Error getting connection for services: "
								+ senderVirtualAddress.toString() + " "
								+ receiverVirtualAddress.toString(), e);
				NMResponse response = new NMResponse();
				response.setStatus(NMResponse.STATUS_ERROR);
				response.setMessage("Error getting connection for services: "
						+ senderVirtualAddress.toString() + " " + receiverVirtualAddress.toString());
				return response;
			}

			Message msg = conn.processData(senderVirtualAddress, receiverVirtualAddress, data);
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
				 * check if message is not intended for host VirtualAddress, if yes and it
				 * has not been processed drop it
				 */
				if (msg.getReceiverVirtualAddress() == null) {
					//TODO #NM Mark remove or fix
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
				return sendMessageAsynch(msg, this.myVirtualAddress, msg.getReceiverVirtualAddress());
			} else {
				NMResponse response = new NMResponse();
				response.setStatus(NMResponse.STATUS_SUCCESS);
				return response;
			}
		} else {
			return backboneRouter.sendDataAsynch(senderVirtualAddress, receiverVirtualAddress, data);
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
		attributes.setProperty(ServiceAttribute.DESCRIPTION.name(), description);

		this.identityManager.updateServiceInfo(this.myVirtualAddress, attributes);

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
		VirtualAddress senderVirtualAddress = message.getSenderVirtualAddress();
		byte[] data;
		try {
			data = this.connectionManager.getBroadcastConnection(senderVirtualAddress)
					.processMessage(message);
		} catch (Exception e) {
			LOG.warn("Could not create packet from message from VirtualAddress: "
					+ message.getSenderVirtualAddress(), e);
			NMResponse response = new NMResponse();
			response.setStatus(NMResponse.STATUS_ERROR);
			response.setMessage("Could not create packet from message from VirtualAddress: "
					+ message.getSenderVirtualAddress());
			return response;
		}
		NMResponse response = this.backboneRouter
				.broadcastData(senderVirtualAddress, data);

		return response;
	}

	@Override
	public NMResponse sendMessage(Message message, boolean synch) {
		VirtualAddress senderVirtualAddress = message.getSenderVirtualAddress();
		VirtualAddress receiverVirtualAddress = message.getReceiverVirtualAddress();
		if (synch)
			return sendMessageSynch(message, senderVirtualAddress, receiverVirtualAddress);
		else
			return sendMessageAsynch(message, senderVirtualAddress, receiverVirtualAddress);
	}

	/**
	 * Internal method for sending messages with more possible parameters. The
	 * message contains the original sender and the final receiver but the
	 * parameters determine which connection to use for sending.
	 * 
	 * @param message
	 *            Message to send
	 * @param senderVirtualAddress
	 *            Sender endpoint of connection to open
	 * @param receiverVirtualAddress
	 *            Receiver endpoint of connection to open
	 * @return
	 */
	private NMResponse sendMessageAsynch(Message message, VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress) {
		byte[] data = null;
		try {
			Connection connection = getConnection(
					receiverVirtualAddress, myVirtualAddress, message.getData());

			//no common connection parameters could be established with the other end
			if(connection == null) {
				if(this.connectionManager.isHandshakeMessage(
						message.getData(), senderVirtualAddress, receiverVirtualAddress)) {
					return this.connectionManager.getDeclineHandshakeMessage(
							this.getService(), receiverVirtualAddress);
				} else {
					NMResponse response = new NMResponse(NMResponse.STATUS_ERROR);
					response.setMessage(COMMUNICATION_PARAMETERS_ERROR);
					return response;
				}	
			}
			data = connection.processMessage(message);
		} catch (Exception e) {
			LOG.warn("Could not create packet from message from VirtualAddress: "
					+ message.getSenderVirtualAddress());
			NMResponse response = new NMResponse();
			response.setStatus(NMResponse.STATUS_ERROR);
			response.setMessage("Could not create packet from message from VirtualAddress: "
					+ message.getSenderVirtualAddress());
			return response;
		}
		NMResponse response = this.backboneRouter.sendDataAsynch(senderVirtualAddress,
				receiverVirtualAddress, data);

		return response;
	}

	/**
	 * Internal method for sending messages with more possible parameters. The
	 * message contains the original sender and the final receiver but the
	 * parameters determine which connection to use for sending.
	 * 
	 * @param message
	 *            Message to send
	 * @param senderVirtualAddress
	 *            Sender endpoint of connection to open
	 * @param receiverVirtualAddress
	 *            Receiver endpoint of connection to open
	 * @return
	 */
	private NMResponse sendMessageSynch(Message message, VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress) {
		byte[] data = null;
		NMResponse response = new NMResponse();
		Message tempMessage = message;

		try {
			Connection connection = getConnection(
					receiverVirtualAddress, myVirtualAddress, message.getData());

			//no common connection parameters could be established with the other end
			if(connection == null) {
				if(this.connectionManager.isHandshakeMessage(
						message.getData(), senderVirtualAddress, receiverVirtualAddress)) {
					return this.connectionManager.getDeclineHandshakeMessage(
							this.getService(), receiverVirtualAddress);
				} else {
					response.setStatus(NMResponse.STATUS_ERROR);
					response.setMessage(COMMUNICATION_PARAMETERS_ERROR);
					return response;
				}
			}
			// process outgoing message
			data = connection.processMessage(tempMessage);
			response = this.backboneRouter.sendDataSynch(senderVirtualAddress,
					receiverVirtualAddress, data);
			if(response.getStatus() == NMResponse.STATUS_SUCCESS) {
				// process response where message contains logical endpoints and
				// connection contains physical endpoints
				//turn around sender and receiver of the message as this is a response
				tempMessage = connection.processData(
						message.getReceiverVirtualAddress(),
						message.getSenderVirtualAddress(),
						(response.getMessage() != null)? response.getMessage().getBytes(): new byte[0]);
				// repeat sending and receiving until security protocol is over
				while (tempMessage != null
						&& tempMessage
						.getTopic()
						.contentEquals(
								CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC)) {
					response = this.backboneRouter.sendDataSynch(senderVirtualAddress,
							receiverVirtualAddress, connection.processMessage(tempMessage));
					if(response.getStatus() == NMResponse.STATUS_SUCCESS) {
						//turn around sender and receiver of the message as this is a response
						tempMessage = connection.processData(message.getReceiverVirtualAddress(),
								message.getSenderVirtualAddress(), response.getMessage()
								.getBytes());
					} else {
						return response;
					}
				}
			} else {
				return response;
			}
		} catch (Exception e) {
			LOG.warn("Error while sending message from VirtualAddress "
					+ message.getSenderVirtualAddress() + "to VirtualAddress: " + message.getReceiverVirtualAddress());
			response = new NMResponse();
			response.setStatus(NMResponse.STATUS_ERROR);
			response.setMessage("Error while sending message: " + e.getMessage());
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

	@Override
	public String[] getAvailableBackbones() {
		List<String> backbones = this.backboneRouter.getAvailableBackbones();
		String[] backboneNames = new String[backbones.size()];
		return backbones.toArray(backboneNames);
	}

	@Override
	public void addRemoteVirtualAddress(VirtualAddress senderVirtualAddress, VirtualAddress remoteVirtualAddress) {
		this.backboneRouter.addRouteForRemoteService(senderVirtualAddress, remoteVirtualAddress);
		//add the security properties of the backbone to the new virtual address
		String backbone = this.backboneRouter.getRouteBackbone(remoteVirtualAddress);
		List<SecurityProperty> secProps = this.backboneRouter.getBackboneSecurityProperties(backbone);
		if(secProps != null) {
			connectionManager.registerServicePolicy(remoteVirtualAddress, secProps, false);
		}
	}

	@Override
	public Registration[] getServiceByDescription(String description) {

		Part part_description = new Part(ServiceAttribute.DESCRIPTION.name(),
				description);

		return getServiceByAttributes(new Part[] { part_description });
	}

	@Override
	public Registration getServiceByPID(String PID) throws IllegalArgumentException {
		if (PID == null || PID.length() == 0) {
			throw new IllegalArgumentException("PID not specificed");
		}

		Part part_description = new Part(ServiceAttribute.PID.name(), PID);

		Registration[] registrations = getServiceByAttributes(new Part[] { part_description });

		if (registrations.length > 1) {
			throw new RuntimeException("More than one service registration found to passed PID");
		} else if (registrations.length == 1) {
			return registrations[0];
		} else
			return null;
	}

	@Override
	public Registration[] getServiceByAttributes(Part[] attributes) {
		return identityManager.getServiceByAttributes(
				attributes,
				IdentityManager.SERVICE_RESOLVE_TIMEOUT,
				false,
				false);
	}

	@Override
	public Registration[] getServiceByQuery(String query) {
		return identityManager.getServicesByAttributes(query);
	}

	@Override
	public Registration[] getServiceByAttributes(Part[] attributes, long timeOut,
			boolean returnFirst, boolean isStrictRequest) {
		return identityManager.getServiceByAttributes(
				attributes, timeOut, returnFirst, isStrictRequest);
	}

	@Override
	public void updateSecurityProperties(List<VirtualAddress> virtualAddressesToUpdate,
			List<SecurityProperty> properties) {
		for(VirtualAddress virtualAddress : virtualAddressesToUpdate) {
			// register VirtualAddress with backbone policies in connection manager
			this.connectionManager.registerServicePolicy(virtualAddress, properties, true);
		}
	}

	private Connection getConnection(
			VirtualAddress receiverVirtualAddress,
			VirtualAddress senderVirtualAddress,
			byte[] data) {
		//only allow one thread at a time to get a connection
		//this is necessary to avoid the creation of multiple handshake connections
		synchronized(connectionManager) {
			while(connectionManager.isBusy()) {
				try {
					connectionManager.wait(2500);
				} catch (InterruptedException e) {
					// nothing to handle
				}
			}
			connectionManager.setBusy();
		}
		Connection con = this.connectionManager.getConnection(receiverVirtualAddress, senderVirtualAddress);
		if(con == null) {
			try {
				con = this.connectionManager.createConnection(receiverVirtualAddress, senderVirtualAddress, data);
			} catch (Exception e) {
				LOG.error(
						"Error getting connection for entities " 
								+ receiverVirtualAddress + " " + senderVirtualAddress,
								e);
			}
		}
		return con;
	}
}
