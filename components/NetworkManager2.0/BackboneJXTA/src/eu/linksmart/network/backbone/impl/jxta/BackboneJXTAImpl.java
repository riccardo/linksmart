package eu.linksmart.network.backbone.impl.jxta;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.MimeMediaType;
import net.jxta.exception.PeerGroupException;
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

/*
 * TODO #NM refactoring
 */
public class BackboneJXTAImpl implements Backbone, RendezvousListener,
		DiscoveryListener {

	private static String BACKBONE_JXTA = BackboneJXTAImpl.class
			.getSimpleName();

	private Logger logger = Logger.getLogger(BackboneJXTAImpl.class.getName());
	protected ComponentContext context;
	private BackboneJXTAConfigurator configurator;

	protected static String JXTA_HOME_DIR = "Backbone/.jxta/";

	private String jxtaHome = JXTA_HOME_DIR + this.name;

	private BackboneRouter bbRouter;

	private HttpService http;
	private String httpPort;
	public PipeSyncHandler pipeSyncHandler;
	public SocketHandler socketHandler;
	public ConfigMode jxtaMode;
	public static String MODE_NODE_AS_STRING = "Node";
	public static String MODE_SUPERNODE_AS_STRING = "SuperNode";
	private String synched;
	private MulticastSocket msocket;

	private static String BackboneJXTAStatusServletName = "/BackboneJXTAStatus";
	
	protected Hashtable<HID, RemoteEndpointInformation> listOfRemoteEndpoints;

	public void activate(ComponentContext context) {
		logger.info("**** Activating JXTA backbone!");

		this.context = context;

		this.bbRouter = (BackboneRouter) context
				.locateService(BackboneRouter.class.getSimpleName());

		configurator.registerConfiguration();

		try {
			this.configurator = new BackboneJXTAConfigurator(this,
					context.getBundleContext());
			configurator.registerConfiguration();
		} catch (NullPointerException e) {
			logger.fatal("Wrong configuration mode + " + mode.toString());
		}

		// Status Servlet Registration
		http = (HttpService) context.locateService("HttpService");
		httpPort = System.getProperty("org.osgi.service.http.port");
		try {
			http.registerServlet(BackboneJXTAStatusServletName,
					new BackboneJXTAStatus(context), null, null);
		} catch (Exception e) {
			logger.error("BackboneJXTA - Error registering servlets", e);
		}

		boolean logs = Boolean.parseBoolean((String) configurator
				.getConfiguration().get(BackboneJXTAConfigurator.JXTA_LOGS));

		if (!logs) {
			System.setProperty(Logging.JXTA_LOGGING_PROPERTY,
					Level.OFF.toString());
		} else if (logs) {
			System.setProperty(Logging.JXTA_LOGGING_PROPERTY,
					Level.ALL.toString());
		}

		this.announcementValidityTime = Long.parseLong((String) configurator
				.getConfiguration().get(
						BackboneJXTAConfigurator.ANNOUNCE_VALIDITY));
		this.factor = Long.parseLong((String) configurator.getConfiguration()
				.get(BackboneJXTAConfigurator.FACTOR));
		this.waitForRdvTime = Long.parseLong((String) configurator
				.getConfiguration().get(
						BackboneJXTAConfigurator.WAIT_FOR_RDV_TIME));
		this.synched = (String) configurator.getConfiguration().get(
				BackboneJXTAConfigurator.SYNCHRONIZED);

		String jxtaMode = ((String) configurator.getConfiguration().get(
				BackboneJXTAConfigurator.MODE));
		/*
		 * Depending on the mode of configuration the node can act as a
		 * RDV(SuperNode) or as EDGE(Node)
		 */
		if (mode.equals(MODE_NODE_AS_STRING)) {
			this.jxtaMode = NetworkManager.ConfigMode.EDGE;
			logger.info("LinkSmart Network Manager configured as Node");
		}
		if (mode.equals(MODE_SUPERNODE_AS_STRING)) {
			this.jxtaMode = NetworkManager.ConfigMode.SUPER;
			logger.info("LinkSmart Network Manager configured as Super Node");
		}
		if ((!mode.equals(MODE_NODE_AS_STRING))
				&& (!mode.equals(MODE_SUPERNODE_AS_STRING))) {
			logger.error("Wrong node mode format. Please choose between Node or SuperNode");
		}

		pipeSyncHandler = new PipeSyncHandler(this);
		socketHandler = new SocketHandler(this);
		
		listOfRemoteEndpoints = new Hashtable<HID, RemoteEndpointInformation>();

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
	 * Sends a message over the JXTA communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param message
	 */
	public NMResponse sendData(HID senderHID, HID receiverHID, byte[] data) {
		// TODO implement this

		NMResponse response = new NMResponse();

		logger.info("Sending data over pipe to HID= " + receiverHID);
		response = pipeSyncHandler.sendData(senderHID.toString(),
				receiverHID.toString(), data, peerID);

		return response;
	}

	/**
	 * Receives a message over the JXTA communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param message
	 */
	public NMResponse receiveData(HID senderHID, HID receiverHID, byte[] data) {
		// TODO implement this
		return new NMResponse();
	}

	/**
	 * Broadcasts a message over the specific communication channel.
	 * 
	 * @param senderHID
	 * @param data
	 * @return
	 */
	public NMResponse broadcastData(HID senderHID, byte[] data) {
		// TODO implement this
		NMResponse response = new NMResponse();

		// send message as multicast message
		synchronized (msocket) {
			msocket.sendData(multicastSocket, new DatagramPacket(data,
					data.length));
		}
		response.setData("Broadcast successful");

		return response;
	}

	/**
	 * Return the destination address as string that will be used for display
	 * purposes.
	 * 
	 * @param hid
	 * @return the backbone address represented by the Hid
	 */
	public String getDestinationAddressAsString(HID hid) {
		return "this is not a real address";
	}

	/*
	 * Now follow methods that are not part of the interface
	 */

	/**
	 * Apply the configuration changes
	 * 
	 * @param updates
	 *            the configuration changes
	 */
	public void applyConfigurations(Hashtable updates) {
		if (updates.containsKey(BackboneJXTAConfigurator.JXTA_LOGS)) {
			if (updates.get(BackboneJXTAConfigurator.JXTA_LOGS).equals("OFF")) {
				System.setProperty(Logging.JXTA_LOGGING_PROPERTY,
						Level.OFF.toString());
			} else if (updates.get(BackboneJXTAConfigurator.JXTA_LOGS).equals(
					"ON")) {
				System.setProperty(Logging.JXTA_LOGGING_PROPERTY,
						Level.ALL.toString());
			}
		}
	}

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

	public PeerID peerID;
	private ConfigMode mode;
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

		/* JXTA platform configuration. */
		try {
			jxtaNetworkManager = new NetworkManager(mode, name, new File(
					jxtaHome).toURI());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		/* Set the JXTA configuration parameters. */
		try {
			jxtaNetworkConfigurator = jxtaNetworkManager.getConfigurator();
		} catch (NullPointerException e) {
			logger.fatal("Wrong configuration mode + " + mode.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		URI uri = new File(JXTA_HOME_DIR + "config/seeds.txt").toURI();
		logger.debug(uri.toString());

		jxtaNetworkConfigurator.addRdvSeedingURI(uri);
		if (Boolean.parseBoolean((String) configurator.getConfiguration().get(
				BackboneJXTAConfigurator.RELAYED))) {
			jxtaNetworkConfigurator.addRelaySeedingURI(uri);
		} else if ((String) configurator.getConfiguration().get(
				BackboneJXTAConfigurator.EXT_TCP_ADDR) != null) {
			jxtaNetworkConfigurator.setTcpPublicAddress(
					(String) configurator.getConfiguration().get(
							BackboneJXTAConfigurator.EXT_TCP_ADDR), true);
			jxtaNetworkConfigurator.setTcpStartPort(-1);
			jxtaNetworkConfigurator.setTcpEndPort(-1);
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

		/*
		 * Add the listeners for the services in the LinkSmart group (discovery
		 * and Rdv)
		 */
		myPGRdvService = netPeerGroup.getRendezVousService();
		myPGRdvService.addListener(this);

		if (((String) configurator.getConfiguration().get(
				BackboneJXTAConfigurator.MODE)).equals("SuperNode")) {
			myPGRdvService.startRendezVous();
		}
		if (((String) configurator.getConfiguration().get(
				BackboneJXTAConfigurator.MODE)).equals("Node")) {
			waitForRdv();
			if (!myPGRdvService.isConnectedToRendezVous()) {
				logger.info("Could not connect to LinkSmart Super Node. Will "
						+ "start without it (only local network communications)");
			} else {
				logger.info("Connected to LinkSmart Super Node succesfully");
			}
		}

		msocket = new MulticastSocket();
		multicastSocket = msocket.createMulticastSocket(netPeerGroup);

		listener = new MulticastSocketListener(multicastSocket);
		listener.start();
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
		logger.debug("Got a discovery response with "
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
					logger.info("Cannot connect to it yet (Implement me!)");
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
		netPeerGroup.globalRegistry.unRegisterInstance(
				netPeerGroup.getPeerGroupID(), netPeerGroup);
		netPeerGroup.stopApp();
		netPeerGroup.unref();
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

		/**
		 * Constructor
		 * 
		 * @param m
		 *            the JXTA multicast socket
		 */
		public MulticastSocketListener(JxtaMulticastSocket m) {
			this.running = true;
			this.m = m;
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
					logger.error(
							getName() + ": Unable to receive data in "
									+ m.toString(), e2);
				}

				SocketAddress socketAddress = receivedData.getSocketAddress();
				InetAddress senderIP = receivedData.getAddress();
				int senderPort = receivedData.getPort();

				HID senderHID = GetHIDFromData(receivedData.getData());
				// give message to BBRouter for further processing
				bbRouter.receiveData(senderHID,
						null, RemoveHIDFromData(receivedData.getData()),
						(Backbone) this);
				// add info to table of HID-Endpoint
				RemoteEndpointInformation endpoint = new RemoteEndpointInformation(socketAddress,
						senderIP, senderPort);
				listOfRemoteEndpoints.put(senderHID, endpoint);
			}
		}

		/**
		 * Stops the thread
		 */
		public void stopThread() {
			m.close();
			this.running = false;
		}
	}

	private static final String HID_DELIMITER = "##";

	/**
	 * Adds an HID to the data package so that the next JXTA backbone can get it without the need to unpack the payload.
	 * 
	 * @param aHID This is the HID of the sender.
	 * @param origData
	 * @return
	 */
	private static byte[] AddHIDToData(HID aHID, byte[] origData) {
		String ret = aHID.toString() + HID_DELIMITER + origData.toString();
		return ret.getBytes();
	}

	/**
	 * Gets the HID of the sender of the data package.
	 * 
	 * @param origData
	 * @return The HID from the sender.
	 */
	private static HID GetHIDFromData(byte[] origData) {
		String dataAsString = origData.toString();
		int posHidDelimiter = dataAsString.indexOf(HID_DELIMITER);
		String hidAsString = dataAsString.substring(0, posHidDelimiter);
		return new HID(hidAsString);
	}

	/**
	 * Removes the HID from the data package so that the payload can be processed regularly e.g. unencrypted.
	 * 
	 * @param origData
	 * @return The original payload of the data package.
	 */
	private static byte[] RemoveHIDFromData(byte[] origData) {
		String dataAsString = origData.toString();
		int posHidDelimiter = dataAsString.indexOf(HID_DELIMITER);
		String dataWithoutHidAsString = dataAsString.substring(
				posHidDelimiter + 1, dataAsString.length());
		return dataWithoutHidAsString.getBytes();
	}
	
	
	private class RemoteEndpointInformation {
		protected SocketAddress socketAddress;
		protected InetAddress senderIP;
		protected int senderPort;
		
		
		public RemoteEndpointInformation(SocketAddress socketAddress,
				InetAddress senderIP, int senderPort) {
			super();
			this.socketAddress = socketAddress;
			this.senderIP = senderIP;
			this.senderPort = senderPort;
		}
		
		public SocketAddress getSocketAddress() {
			return socketAddress;
		}
		public void setSocketAddress(SocketAddress socketAddress) {
			this.socketAddress = socketAddress;
		}
		public InetAddress getSenderIP() {
			return senderIP;
		}
		public void setSenderIP(InetAddress senderIP) {
			this.senderIP = senderIP;
		}
		public int getSenderPort() {
			return senderPort;
		}
		public void setSenderPort(int senderPort) {
			this.senderPort = senderPort;
		}
 
	}

}
