package eu.linksmart.network.backbone.impl.jxta;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.jxta.impl.protocol.RdvAdv;
import net.jxta.logging.Logging;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.rendezvous.RendezVousService;
import net.jxta.rendezvous.RendezvousEvent;
import net.jxta.rendezvous.RendezvousListener;
import net.jxta.socket.JxtaMulticastSocket;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import eu.linksmart.network.HID;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.security.communication.SecurityProperty;

/**
 * The class BackboneJXTAImpl implements the LinkSmart backbone that uses JXTA.
 * 
 * The class implements the methods defined in the respective interface class
 * {@Backbone}.
 * 
 * The goal is that the communication for LinkSmart applications is transparent
 * to the underlying protocol. Therefore BackboneJXTA manages everything related
 * to JXTA communication and uses standardized interface methods. The
 * BackboneJXTA does not know about the content of data exchanged.
 * 
 * On the other hand in order to transport important information (such as the
 * senderHID) additional information will be added to the payload sent over the
 * JXTA network. On the receiving side this information is extracted and the
 * original payload is given to the Backbone manager component which in turn is
 * responsible for further processing of the received data package.
 * 
 * JXTA is an implementation of the peer-to-peer (P2P) communication style. You
 * can use JXTA to implement a P2P communication with Java.
 * 
 * Currently v2.5 of the JXTA java library is in use. One can find further
 * information on the following pages:
 * 
 * http://java.sun.com/othertech/jxta/index.jsp or
 * http://download.java.net/jxta/
 * 
 * On http://jxta.kenai.com/ one can find further information together with
 * newer versions of the library. The development has been pushed by volunteers.
 * 
 * Oracle as the owner of SUN und Java has withdrawn from the JXTA project and
 * currently no further development takes place.
 * 
 * 
 * @author Schneider
 * 
 */
public class BackboneJXTAImpl implements Backbone, RendezvousListener,
		DiscoveryListener {

	private static String BACKBONE_JXTA = BackboneJXTAImpl.class
			.getSimpleName();

	private Logger logger = Logger.getLogger(BackboneJXTAImpl.class.getName());
	protected ComponentContext context;
	private BackboneJXTAConfigurator configurator;

	protected static String JXTA_HOME_DIR = "Backbone/.jxta/";

	private String jxtaHome;

	private BackboneRouter bbRouter;

	private HttpService http;
	private String httpPort;
	public PipeSyncHandler pipeSyncHandler;
	public SocketHandler socketHandler;
	public ConfigMode jxtaMode;
	private String synched;
	private MulticastSocket msocket;

	private static String BackboneJXTAStatusServletName = "/BackboneJXTAStatus";

	/**
	 * A map of HID to JXTA Peer ID.
	 */
	protected Hashtable<HID, String> listOfRemoteEndpoints;

	protected void activate(ComponentContext context) {
		logger.info("**** Activating JXTA backbone!");

		this.context = context;

		this.bbRouter = (BackboneRouter) context
				.locateService(BackboneRouter.class.getSimpleName());

		try {
			this.configurator = new BackboneJXTAConfigurator(this, context
					.getBundleContext());
			configurator.registerConfiguration();
		} catch (NullPointerException e) {
			logger.fatal("Configurator could not be initialized "
					+ e.toString());
		}

		this.name = (String) configurator
				.get(BackboneJXTAConfigurator.PEER_NAME);
		this.jxtaHome = JXTA_HOME_DIR + this.name;

		boolean logs = Boolean.parseBoolean((String) configurator
				.getConfiguration().get(BackboneJXTAConfigurator.JXTA_LOGS));

		if (!logs) {
			System.setProperty(Logging.JXTA_LOGGING_PROPERTY, Level.OFF
					.toString());
		} else if (logs) {
			System.setProperty(Logging.JXTA_LOGGING_PROPERTY, Level.ALL
					.toString());
		}

		this.announcementValidityTime = Long.parseLong((String) configurator
				.get(BackboneJXTAConfigurator.ANNOUNCE_VALIDITY));

		this.factor = Long.parseLong((String) configurator
				.get(BackboneJXTAConfigurator.FACTOR));

		this.waitForRdvTime = Long.parseLong((String) configurator
				.get(BackboneJXTAConfigurator.WAIT_FOR_RDV_TIME));

		this.synched = (String) configurator
				.get(BackboneJXTAConfigurator.SYNCHRONIZED);

		String jxtaConfMode = ((String) configurator
				.get(BackboneJXTAConfigurator.MODE));
		/*
		 * Depending on the mode of configuration the node can act as a
		 * RDV(SuperNode) or as EDGE(Node)
		 */
		if (jxtaConfMode.equals(BackboneJXTAConfigurator.MODE_SUPERNODE)) {
			this.jxtaMode = NetworkManager.ConfigMode.SUPER;
			logger.info("LinkSmart Network Manager configured as Super Node");
		} else {
			// if it not a super node it must be an edge
			this.jxtaMode = NetworkManager.ConfigMode.EDGE;
			logger.info("LinkSmart Network Manager configured as Node");
		}

		listOfRemoteEndpoints = new Hashtable<HID, String>();

		startJXTA();

		logger.info("**** JXTA Backbone started succesfully");
	}

	public void deactivate(ComponentContext context) {
		// Unregister servlets
		http.unregister(BackboneJXTAStatusServletName);

		// stop JXTA traffic
		configurator.stop();
		pipeSyncHandler.stopPipes();
		socketHandler.stopSockets();
		stopJXTA();

		// remove objects
		bbRouter = null;
		socketHandler = null;
		pipeSyncHandler = null;
		listOfRemoteEndpoints = null;

		logger.info("JXTA Backbone stopped succesfully");
	}

	/*
	 * Now follow the methods from the interface
	 */

	/**
	 * Sends a message for one specific recipient over the JXTA communication
	 * channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param message
	 */
	public NMResponse sendData(HID senderHID, HID receiverHID, byte[] data) {
		NMResponse response = new NMResponse();

		// add senderHID to data
		byte[] payload = BackboneJXTAUtils.AddHIDToData(senderHID, data);
		logger.debug("Sending data over pipe to HID= " + receiverHID);
		
		String receiverJxtaAddress = listOfRemoteEndpoints.get(receiverHID);
		URI receiverJxtaURI;
		PeerID receiverPeerID = null;
		try {
			receiverJxtaURI = new URI(receiverJxtaAddress);
			receiverPeerID = (PeerID)IDFactory.fromURI(receiverJxtaURI);
		} catch (URISyntaxException e) {
			logger.warn("Wrong syntax in URI " + receiverJxtaAddress, e);
		}
		
		response = pipeSyncHandler.sendData(senderHID.toString(), receiverHID
				.toString(), payload, receiverPeerID);

		logger.debug("sendData Response: " + response.toString());

		return response;
	}

	/**
	 * Receives a message over the JXTA communication channel. In case of a
	 * broadcast message the receiverHID is null (since the sender does not
	 * specify who should receive this message).
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param message
	 */
	public NMResponse receiveData(HID senderHID, HID receiverHID,
			byte[] receivedData) {
		NMResponse response = new NMResponse();

		// give message to BBRouter for further processing
		try {
			response = bbRouter.receiveData(senderHID, receiverHID,
					receivedData,
					(Backbone) this);
			
			logger.debug("receiveData Response: " + response.toString());
		} catch (Exception e) {
			logger
					.error(
							"BBJXTA could not give received message for processing to bbRouter",
							e);
		}

		return response;
	}

	/**
	 * Broadcasts a message over the specific communication channel.
	 * 
	 * @param senderHID
	 * @param data
	 * @return
	 */
	public NMResponse broadcastData(HID senderHID, byte[] data) {
		NMResponse response = new NMResponse();

		logger.debug("BBJXTA - broadcastData: "
				+ BackboneJXTAUtils.ConvertByteArrayToString(data));
		byte[] payload = BackboneJXTAUtils.AddHIDToData(senderHID, data);

		logger.debug("BBJXTA broadcastData - senderHID: "
				+ senderHID.toString());
		logger.debug("BBJXTA - broadcastData with HID: "
				+ BackboneJXTAUtils.ConvertByteArrayToString(payload));

		// send message as multicast message
		synchronized (msocket) {
			msocket.sendData(multicastSocket, new DatagramPacket(payload,
					payload.length));
		}
		response.setStatus(NMResponse.STATUS_SUCCESS);
		response.setMessage("Broadcast successful");

		logger.debug("broadcastData Response: " + response.toString());

		return response;
	}

	/**
	 * Return the destination JXTA address as string that will be used for
	 * display purposes.
	 * 
	 * 
	 * @param hid
	 * @return the backbone address represented by the HID, else null.
	 */
	public String getEndpoint(HID hid) {
		String remoteEndpoint = listOfRemoteEndpoints.get(hid);
		return remoteEndpoint;
	}

	/*
	 * Now follow methods that are not part of the interface
	 */

	@SuppressWarnings("rawtypes")
	@Override
	/**
	 * Apply the configuration changes
	 * 
	 * @param updates the configuration changes
	 */
	public void applyConfigurations(Hashtable updates) {
		if (updates.containsKey(BackboneJXTAConfigurator.JXTA_LOGS)) {
			if (updates.get(BackboneJXTAConfigurator.JXTA_LOGS).equals("OFF")) {
				System.setProperty(Logging.JXTA_LOGGING_PROPERTY, Level.OFF
						.toString());
			} else if (updates.get(BackboneJXTAConfigurator.JXTA_LOGS).equals(
					"ON")) {
				System.setProperty(Logging.JXTA_LOGGING_PROPERTY, Level.ALL
						.toString());
			}
		}

		if (updates.containsKey(BackboneJXTAConfigurator.ANNOUNCE_VALIDITY)) {
			this.announcementValidityTime = Long.parseLong((String) updates
					.get(BackboneJXTAConfigurator.ANNOUNCE_VALIDITY));
		}
		if (updates.containsKey(BackboneJXTAConfigurator.FACTOR)) {

			this.factor = Long.parseLong((String) updates
					.get(BackboneJXTAConfigurator.FACTOR));
		}
		if (updates.containsKey(BackboneJXTAConfigurator.WAIT_FOR_RDV_TIME)) {
			this.waitForRdvTime = Long.parseLong((String) updates
					.get(BackboneJXTAConfigurator.WAIT_FOR_RDV_TIME));
		}
		if (updates.containsKey(BackboneJXTAConfigurator.SYNCHRONIZED)) {
			this.synched = (String) updates
					.get(BackboneJXTAConfigurator.SYNCHRONIZED);
		}
		if (updates.containsKey(BackboneJXTAConfigurator.MODE)) {

			String jxtaConfMode = ((String) updates
					.get(BackboneJXTAConfigurator.MODE));
			/*
			 * Depending on the mode of configuration the node can act as a
			 * RDV(SuperNode) or as EDGE(Node)
			 */
			if (jxtaConfMode.equals(BackboneJXTAConfigurator.MODE_SUPERNODE)) {
				this.jxtaMode = NetworkManager.ConfigMode.SUPER;
				logger
						.info("LinkSmart Network Manager configured as SuperNode");
			} else {
				// if it is not a supernode it must be a node
				this.jxtaMode = NetworkManager.ConfigMode.EDGE;
				logger.info("LinkSmart Network Manager configured as Node");
			}
		}

	}

	/**
	 * Returns the dictionary of configuration parameters
	 * 
	 * @return Dictionary
	 */
	public Dictionary getConfiguration() {
		return configurator.getConfiguration();
	}

	// ***************************************************************************************
	// here follows the JXTA specific stuff coming from the old NetworkManager
	// Backbone.
	// ***************************************************************************************

	public String name;
	public PeerGroup netPeerGroup;
	private DiscoveryService netPGDiscoveryService;
	private RendezVousService myPGRdvService;

	private String rdvlock = new String("rocknroll");
	private boolean connected = false;
	private long waitForRdvTime;

	private PeerID peerID;
	// private ConfigMode mode;
	private NetworkManager jxtaNetworkManager;
	private NetworkConfigurator jxtaNetworkConfigurator;

	public long announcementValidityTime, factor;

	private JxtaMulticastSocket multicastSocket;
	private MulticastSocketListener listener;

	/**
	 * Starts JXTA manager. This method performs different tasks: - configures
	 * the JXTA platform - creates and joins the LinkSmart peer
	 * groupLinkSmartrently password protected peer group)
	 */
	private void startJXTA() {
		clearCache(new File(jxtaHome, "cm"));
		AdvertisementFactory.registerAdvertisementInstance(
				NMadvertisement.getAdvertisementType(), new NMadvertisement.Instantiator());

		/* JXTA platform configuration. */
		try {
			jxtaNetworkManager = new NetworkManager(jxtaMode, name, new File(
					jxtaHome).toURI());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		/* Set the JXTA configuration parameters. */
		try {
			jxtaNetworkConfigurator = jxtaNetworkManager.getConfigurator();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// URI uri = new File(JXTA_HOME_DIR + "config/seeds.txt").toURI();
		// logger.debug(uri.toString());
		//
		// jxtaNetworkConfigurator.addRdvSeedingURI(uri);

		URI superNodeTCPURI = null;
		try {
			String uriString = (String) configurator.getConfiguration().get(
					BackboneJXTAConfigurator.SUPERNODE_TCP_URI);
			superNodeTCPURI = new URI(uriString);
			jxtaNetworkConfigurator.addSeedRendezvous(superNodeTCPURI);
			logger.debug("Added SuperNode-TCP-URI: "
					+ superNodeTCPURI.toString());

			URI superNodeHTTPURI = new URI((String) configurator
					.getConfiguration().get(
							BackboneJXTAConfigurator.SUPERNODE_HTTP_URI));
			jxtaNetworkConfigurator.addSeedRendezvous(superNodeHTTPURI);
			logger.debug("Added SuperNode-H-URI: "
					+ superNodeHTTPURI.toString());

			if (Boolean.parseBoolean((String) configurator.getConfiguration()
					.get(BackboneJXTAConfigurator.RELAYED))) {
				// jxtaNetworkConfigurator.addRelaySeedingURI(uri);
				jxtaNetworkConfigurator.addRelaySeedingURI(superNodeTCPURI);
				jxtaNetworkConfigurator.addRelaySeedingURI(superNodeHTTPURI);

			} else if ((String) configurator.getConfiguration().get(
					BackboneJXTAConfigurator.EXT_TCP_ADDR) != null) {
				jxtaNetworkConfigurator.setTcpPublicAddress(
						(String) configurator.getConfiguration().get(
								BackboneJXTAConfigurator.EXT_TCP_ADDR), true);
				jxtaNetworkConfigurator.setTcpStartPort(-1);
				jxtaNetworkConfigurator.setTcpEndPort(-1);
			}

		} catch (Exception e) {
			logger.error("Could not add URI for SuperNode. URI: "
					+ superNodeTCPURI, e);
		}

		if (Boolean.parseBoolean((String) configurator.getConfiguration().get(
				BackboneJXTAConfigurator.MULTICAST))) {
			jxtaNetworkConfigurator.setUseMulticast(true);
		} else {
			jxtaNetworkConfigurator.setUseMulticast(false);
		}

		logger.debug("JXTA TCP-Port: "
				+ (String) configurator.getConfiguration().get(
						BackboneJXTAConfigurator.JXTA_TCP_PORT));
		jxtaNetworkConfigurator.setTcpPort(Integer
				.valueOf((String) configurator.getConfiguration().get(
						BackboneJXTAConfigurator.JXTA_TCP_PORT)));
		jxtaNetworkConfigurator.setHttpPort(Integer
				.valueOf((String) configurator.getConfiguration().get(
						BackboneJXTAConfigurator.JXTA_HTTP_PORT)));
		jxtaNetworkConfigurator.setPeerID(IDFactory
				.newPeerID(PeerGroupID.defaultNetPeerGroupID));

		/* Start JXTA network. */
		try {
			jxtaNetworkManager.startNetwork();
		} catch (PeerGroupException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		netPeerGroup = jxtaNetworkManager.getNetPeerGroup();

		/* Add the listeners for the services in the netpeergroup. */
		netPGDiscoveryService = netPeerGroup.getDiscoveryService();
		netPGDiscoveryService.addDiscoveryListener(this);
		peerID = netPeerGroup.getPeerID();

		msocket = new MulticastSocket();
		multicastSocket = msocket.createMulticastSocket(netPeerGroup);

		listener = new MulticastSocketListener(multicastSocket, this);
		listener.start();

		/*
		 * Add the listeners for the services in the LinkSmart group (discovery
		 * and Rdv)
		 */
		myPGRdvService = netPeerGroup.getRendezVousService();
		myPGRdvService.addListener(this);

		pipeSyncHandler = new PipeSyncHandler(this);
		socketHandler = new SocketHandler(this);

		if (((String) configurator.getConfiguration().get(
				BackboneJXTAConfigurator.MODE))
				.equals(BackboneJXTAConfigurator.MODE_SUPERNODE)) {
			myPGRdvService.startRendezVous();
		}
		if (((String) configurator.getConfiguration().get(
				BackboneJXTAConfigurator.MODE))
				.equals(BackboneJXTAConfigurator.MODE_NODE)) {
			waitForRdv();
			if (!myPGRdvService.isConnectedToRendezVous()) {
				logger
						.info("Could not connect to LinkSmart Super Node. Will "
								+ "start without it (only local network communications)");
			} else {
				logger.info("Connected to LinkSmart Super Node succesfully");
			}
		}

	}

	/**
	 * It waits for a RDV connection. This method blocks until there is a
	 * RDVconnect event or expires the time parameter.
	 */
	private void waitForRdv() {
		long startTime = System.currentTimeMillis();
		synchronized (rdvlock) {
			while ((!myPGRdvService.isConnectedToRendezVous())
					&& (System.currentTimeMillis() - startTime <= waitForRdvTime)) {

				logger.info("Awaiting rendezvous conx...");
				try {
					if (!myPGRdvService.isConnectedToRendezVous()) {
						rdvlock.wait(waitForRdvTime);
					}
				} catch (InterruptedException e) {
					logger.error("Interrupted exception in waitForRdv...");
				}
			}
		}
	}

	/**
	 * Listens for RDV events in a peer. This method is synchronized with
	 * waitForRdv(time) and unlocks it when a RDVCONNECT event arrives
	 * 
	 * @param event
	 *            a rendezvous event
	 */
	public void rendezvousEvent(RendezvousEvent event) {
		String eventDescription;

		int eventType;
		eventType = event.getType();
		switch (eventType) {
		case RendezvousEvent.RDVCONNECT:
			eventDescription = "RDVCONNECT";
			connected = true;
			break;
		case RendezvousEvent.RDVRECONNECT:
			eventDescription = "RDVRECONNECT";
			connected = true;
			break;
		case RendezvousEvent.RDVDISCONNECT:
			eventDescription = "RDVDISCONNECT";
			break;
		case RendezvousEvent.RDVFAILED:
			eventDescription = "RDVFAILED";
			break;
		case RendezvousEvent.CLIENTCONNECT:
			eventDescription = "CLIENTCONNECT";
			break;
		case RendezvousEvent.CLIENTRECONNECT:
			eventDescription = "CLIENTRECONNECT";
			break;
		case RendezvousEvent.CLIENTDISCONNECT:
			eventDescription = "CLIENTDISCONNECT";
			break;
		case RendezvousEvent.CLIENTFAILED:
			eventDescription = "CLIENTFAILED";
			break;
		case RendezvousEvent.BECAMERDV:
			eventDescription = "BECAMERDV";
			connected = true;
			break;
		case RendezvousEvent.BECAMEEDGE:
			eventDescription = "BECAMEEDGE";
			break;
		default:
			eventDescription = "UNKNOWN RENDEZVOUS EVENT";
		}

		logger.info("RendezvousEvent: Event = " + eventDescription
				+ " from peer = " + event.getPeer());
		synchronized (rdvlock) {
			if (connected) {
				rdvlock.notify();
			}
		}
	}

	/**
	 * This method gets any responses to the getRemoteAdvertisements method, NM
	 * advertisements, and processes them. Extracts the useful information from
	 * them, such as updating the idTable
	 * 
	 * @param event
	 *            the discovery event
	 */
	public void discoveryEvent(DiscoveryEvent event) {
		DiscoveryResponseMsg response = event.getResponse();
		logger.info("Got a discovery response with "
				+ response.getResponseCount() + " elements from peer: "
				+ event.getSource() + "\n");
		NMadvertisement adv;
		Enumeration<Advertisement> en = response.getAdvertisements();
		Long time;

		if (en != null) {
			while (en.hasMoreElements()) {
				/* Store received NM advertisement. */
				Advertisement adv1 = en.nextElement();

				if (adv1.getAdvType().equals(RdvAdv.getAdvertisementType())) {
					RdvAdv rdvAdv = (RdvAdv) adv1;
					rdvAdv.getPeerID();
					logger.info("LinkSmart Super Node discovered "
							+ rdvAdv.getPeerID());
				}

				if (adv1.getAdvType().equals(
						NMadvertisement.getAdvertisementType())) {
					adv = (NMadvertisement) adv1;

					logger.debug(adv.getName() + " from " + adv.getEndpoint());
				}

				if (adv1.getAdvType().equals(DiscoveryService.GROUP)) {
					System.out.println("Grupo: \n "
							+ adv1.getDocument(MimeMediaType.TEXTUTF8));
				}
			}
		}
	}

	/**
	 * Clears the cache
	 * 
	 * @param rootDir
	 *            the root directory
	 */
	private void clearCache(final File rootDir) {
		try {
			if (rootDir.exists()) {
				File[] list = rootDir.listFiles();
				for (File aList : list) {
					if (aList.isDirectory()) {
						clearCache(aList);
					} else {
						aList.delete();
					}
				}
			}
			rootDir.delete();
			logger.info("Cache component " + rootDir.toString() + " cleared.");
		} catch (Throwable t) {
			logger.error("Unable to clear " + rootDir.toString());
			t.printStackTrace();
		}
	}

	/**
	 * Stops JXTA
	 */
	private void stopJXTA() {
		listener.stopThread();
		multicastSocket.disconnect();
		multicastSocket.close();

		jxtaNetworkManager = null;
		// netPeerGroup.globalRegistry.unRegisterInstance(
		// netPeerGroup.getPeerGroupID(), netPeerGroup);
		netPeerGroup.stopApp();
		// netPeerGroup.unref();
		netPeerGroup = null;

		listener = null;
		msocket = null;
		multicastSocket = null;

		System.gc();
		System.runFinalization();
		System.gc();
	}

	/**
	 * Multicast Socket Listener
	 */
	public class MulticastSocketListener extends Thread {
		JxtaMulticastSocket m;
		boolean running;
		BackboneJXTAImpl bbJXTA;

		/**
		 * Constructor
		 * 
		 * @param m
		 *            the JXTA multicast socket
		 */
		public MulticastSocketListener(JxtaMulticastSocket m,
				BackboneJXTAImpl bbJXTA) {
			this.running = true;
			this.m = m;
			this.bbJXTA = bbJXTA;
		}

		/**
		 * Starts the thread
		 */
		public void run() {
			setName("MulticastListener");
			byte[] buffer;
			DatagramPacket receivedData;
			while (running) {
				buffer = new byte[64000];
				receivedData = new DatagramPacket(buffer, buffer.length);

				try {
					m.receive(receivedData);
				} catch (IOException e2) {
					logger.error(getName() + ": Unable to receive data in "
							+ m.toString(), e2);
					e2.printStackTrace();
				}

				// give message to BBRouter for further processing
				// receiverHID is null because this is a broadcast message
				try {
					String msgAsString = BackboneJXTAUtils
							.ConvertByteArrayToString(receivedData.getData())
							.trim();

					if (msgAsString.length() > 0) {
						// do not process empty messages
						
						// FIXME
						// this is temporary code until the complete move to LS 2.0; it is
						// there just
						// to ignore LS1.1 messages from supernodes and other 1.1 NM
						if (receivedData.getData()[0] == 'N' && receivedData.getData()[1] == 'M') {
							logger.debug("Received LS1.1 Message: " + 
									((receivedData.getLength() > 10)?
											new String(receivedData.getData()).substring(0, 10): new String(receivedData.getData()))
									+ ".... Throwing it away.");
							continue;
						}
						// end temporary code
						
						String senderJXTAID = receivedData.getAddress()
								.getHostName();
						
						HID senderHID = BackboneJXTAUtils
								.GetHIDFromData(receivedData.getData());
						
						
						logger.debug("BBJXTA receive multicast message: "
								+ BackboneJXTAUtils
										.ConvertByteArrayToString(receivedData
												.getData()));
						logger
								.debug("BBJXTA receive multicast message: senderJXTAID: "
										+ senderJXTAID
										+ " | senderHID: "
										+ senderHID);

						// add info to table of HID-Endpoints
						//TODO Mark this check is a workaround for a parsing bug (HID String can have random text)
						//Should be removed when parsing bug is fixed
						if (senderHID.getContextID1() == 0){
							listOfRemoteEndpoints.put(senderHID, senderJXTAID);
						}
						
						bbJXTA.receiveData(senderHID,
								null,
								BackboneJXTAUtils.RemoveHIDFromData(receivedData.getData()));
						
					}
				} catch (Exception e) {
					logger
							.error(
									"BBJXTA could not process received multicast message.",
									e);
				}

			}
		}

		/**
		 * Stops the thread
		 */
		public void stopThread() {
			m.close();
			m = null;
			this.running = false;
		}
	}

	@Override
	public boolean addEndpoint(HID hid, String endpoint) {
		try {
			listOfRemoteEndpoints.put(hid, endpoint);
			return true;
		} catch (Exception e) {
			logger.error("Unable to addEndpoint: " + endpoint.toString()
					+ " for HID: " + hid, e);
		}
		return false;
	}

	@Override
	public boolean removeEndpoint(HID hid) {
		try {
			listOfRemoteEndpoints.remove(hid);
			return true;
		} catch (Exception e) {
			logger.error("Unable to remove endpoint for HID: " + hid, e);
		}
		return false;
	}

	@Override
	/**
	 * returns security types available by using this backbone implementation. 
	 * The security types are configured via the LS configuration interface.
	 * See resources/BBJXTA.properties for details on configuration
	 * @return a list of security types available
	 */
	public List<SecurityProperty> getSecurityTypesAvailable() {
		String configuredSecurity = this.configurator
				.get(BackboneJXTAConfigurator.SECURITY_PARAMETERS);
		String[] securityTypes = configuredSecurity.split("|");
		SecurityProperty oneProperty;
		List<SecurityProperty> answer = new ArrayList<SecurityProperty>();
		for (String s : securityTypes) {
			try {
				oneProperty = SecurityProperty.valueOf(s);
				answer.add(oneProperty);
			} catch (Exception e) {
				logger
						.error("Security property value from configuration is not recognized: "
								+ s + ": " + e);
			}
		}
		return answer;
	}

	@Override
	public String getName() {
		return BackboneJXTAImpl.class.getName();
	}

	@Override
	public void addEndpointForRemoteHID(HID senderHID, HID remoteHID) {
		
		String endpoint = listOfRemoteEndpoints.get(senderHID);
		
		if(endpoint != null){			
			listOfRemoteEndpoints.put(remoteHID, endpoint);
		}else{			
			logger.error("Network Manager endpoint of HID " + senderHID + " cannot be found");
		}
		
	}

}
