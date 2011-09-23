/*
 * In case of German law being applicable to this license agreement, the following warranty and liability terms shall apply:
 *
 * 1. Licensor shall be liable for any damages caused by wilful intent or malicious concealment of defects.
 * 2. Licensor's liability for gross negligence is limited to foreseeable, contractually typical damages.
 * 3. Licensor shall not be liable for damages caused by slight negligence, except in cases 
 *    of violation of essential contractual obligations (cardinal obligations). Licensee's claims for 
 *    such damages shall be statute barred within 12 months subsequent to the delivery of the software.
 * 4. As the Software is licensed on a royalty free basis, any liability of the Licensor for indirect damages 
 *    and consequential damages - except in cases of intent - is excluded.
 *
 * This limitation of liability shall also apply if this license agreement shall be subject to law 
 * stipulating liability clauses corresponding to German law.
 */
package eu.linksmart.qos.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.aom.ApplicationOntologyManager;
import eu.linksmart.clients.RemoteWSClientProvider;
import eu.linksmart.network.CryptoHIDResult;
import eu.linksmart.network.NetworkManagerApplication;
import eu.linksmart.qos.QoSManager;
import eu.linksmart.qos.computation.Computation;

/**
 * 
 * Implementation of QoSManager and entry point for the OSGi DS component
 * 
 * See the documentation of QoSManager for further details
 * 
 *@author Amro Al-Akkad
 */
public class QoSManagerImpl implements QoSManager {

	/**
	 * Constant to refer to localhost.
	 */
	private static final String HTTP_LOCALHOST = "http://localhost:";

	/**
	 * Logger.
	 */
	private final static Logger LOG =
			Logger.getLogger(QoSManagerImpl.class.getName());

	/**
	 * Constant referring to the Web Service Path of the Network Manager.
	 */
	public static final String NETWORK_MANAGER_PATH =
			"/axis/services/NetworkManagerApplication";

	/**
	 * Constant to refer to the OSGi http port.
	 */
	private static final String ORG_OSGI_SERVICE_HTTP_PORT =
			"org.osgi.service.http.port";

	/**
	 * Constant for referring to persistent identifier of Quality-of-Service
	 * Manager.
	 */
	public static final String QM_SERVICE_PID =
			QoSManagerConfigurator.QM_PID + ".QoSManager";

	/**
	 * Constant to refer to the QoSManager port.
	 */
	private static final String QOS_MANAGER_PORT = "QoSManagerPort";

	/**
	 * Constant to refer to the QoSManager: prefix.
	 */
	private static final String QOS_MANAGER = "QoSManager:";

	/**
	 * Constant referring to the Web Service Path of the Quality-of-Service
	 * Manager.
	 */
	public static final String QOS_MANAGER_PATH =
			"/axis/services/" + QOS_MANAGER_PORT;

	/**
	 * RemoteWSClientProvider to retrieve via LinkSmart relvant WS Client
	 * managers.
	 */
	private RemoteWSClientProvider clientProvider;

	/**
	 * Dedicated configurator for QoS Manager.
	 */
	private QoSManagerConfigurator configurator;

	/**
	 * Field to point to Network Manager as OSGi bundle
	 */
	private NetworkManagerApplication nmOSGi;

	/**
	 * Field to point to Network Manager WS Client.
	 */
	private NetworkManagerApplication nmWSClient;

	/**
	 * Flag to indicated if the bundle is activated.
	 */
	private boolean activated = false;

	/**
	 * Field to refer to HID of QoS Manager
	 */
	private String qosManagerHID;

	/**
	 * Field to refer to Ontology Manager OSGi bundle.
	 */
	private ApplicationOntologyManager ontologyOSGi;

	/**
	 * Field to refer LinkSmart Ontology Manager.
	 */
	@SuppressWarnings("unused")
	private ApplicationOntologyManager ontologyWSClient;

	/**
	 * OSGi Declarative service method to deploy bundle.
	 * 
	 * @param context
	 *            Reference to ComponentContext.
	 */
	protected void activate(ComponentContext context) {

		LOG.debug("QoSManager starting...");

		configurator = new QoSManagerConfigurator(context, this);

		init();

		configurator.registerConfiguration();

		this.activated = true;
		LOG.info("QoSManager started");
	}

	/**
	 * Updates the configuration for QoS Manager on the fly.
	 * 
	 * @param updates
	 *            Requested parts to update.
	 */
	@SuppressWarnings("unchecked")
	public void applyConfigurations(Hashtable updates) {
		LOG.info("Applying configurations");
		boolean createdHID = false;
		if (updates.containsKey(QoSManagerConfigurator.PID)) {
			LOG.info("Applying configurationssssss");
			removeQoSManagerHID();
			createHIDForQoSManager(true);
			createdHID = true;
		}

		else if (updates
				.containsKey(QoSManagerConfigurator.USE_NETWORK_MANAGER)) {
			boolean useNetworkManager =
					Boolean.parseBoolean((String) updates
							.get(QoSManagerConfigurator.USE_NETWORK_MANAGER));
			if (useNetworkManager) {
				if (!createdHID)
					createHIDForQoSManager(false);
			} else {
				removeQoSManagerHID();
			}
		}

		// else is handled by OSGI ConfigAdmin
	}

	/**
	 * 
	 * @param xmlMessage
	 *            XML based request string.
	 * @return Returns a XML based QoS response.
	 */
	public String getBestSuitableService(String xmlMessage) {
		LOG.info(xmlMessage);

		Computation qosComputation = null;

		if (this.ontologyOSGi == null && this.nmOSGi == null) {
			LOG.info("QoS Manager OSGi and Network Manager OSGi are NULL.");
		} else {
			qosComputation = new Computation(ontologyOSGi);
		}
		// else if(this.ontologyWSClient!=null && this.nmWSClient!=null)
		// {
		// qosComputation = new Computation(ontologyWSClient);
		// }
		if (this.ontologyOSGi != null && this.nmOSGi == null) {
			qosComputation = new Computation(ontologyOSGi);
		}

		String result = null;

		try {
			result = qosComputation.performAlgorithm(xmlMessage, true);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * 
	 * @param messageInput
	 *            XML based request string.
	 * @return Returns a XML based QoS response.
	 */
	public String getQoSProperties(String messageInput) {
		LOG.info(messageInput);

		Computation qosComputation = null;

		if (this.ontologyOSGi == null && this.nmOSGi == null) {
			LOG.info("QoS Manager OSGi and Network Manager OSGi are NULL.");
		} else {
			qosComputation = new Computation(ontologyOSGi);
		}
		// else if(this.ontologyWSClient!=null && this.nmWSClient!=null)
		// {
		// qosComputation = new Computation(ontologyWSClient);
		// }
		if (this.ontologyOSGi != null && this.nmOSGi == null) {
			qosComputation = new Computation(ontologyOSGi);
		}

		String result = null;

		try {
			result = qosComputation.performGetQoSProperties(messageInput);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * @param xmlMessage
	 *            XML based request string.
	 * @return Returns a XML based QoS response.
	 */
	public String getRankingList(String xmlMessage) {

		LOG.info(xmlMessage);

		Computation qosComputation = null;

		if (this.ontologyOSGi == null && this.nmOSGi == null) {
			LOG.info("QoS Manager OSGi and Network Manager OSGi are NULL.");
		} else {
			qosComputation = new Computation(ontologyOSGi);
		}
		// else if(this.ontologyWSClient!=null && this.nmWSClient!=null)
		// {
		// qosComputation = new Computation(ontologyWSClient);
		// }
		if (this.ontologyOSGi != null && this.nmOSGi == null) {
			qosComputation = new Computation(ontologyOSGi);
		}

		String result = null;

		try {
			result = qosComputation.performAlgorithm(xmlMessage, false);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * Binds dynamically remote WS client provider.
	 * 
	 * @param clientProvider
	 *            Reference to client provider.
	 */
	protected void bindClientProvider(RemoteWSClientProvider clientProvider) {
		this.clientProvider = clientProvider;
		LOG.info("RemoteWSClientProvider bound for Component.");
		if (activated) {
			try {
				this.nmWSClient =
						(NetworkManagerApplication) clientProvider
								.getRemoteWSClient(
										NetworkManagerApplication.class
												.getName(),
										(String) configurator
												.get(QoSManagerConfigurator.NM_ADDRESS),
										false);

				// Deactivate this as no ontology WS client was able when this
				// code was finalized
				// this.ontologyWSClient = (ApplicationOntologyManager)
				// clientProvider.
				// getRemoteWSClient(ApplicationOntologyManager.class.getName(),
				// (String)configurator.get(QoSManagerConfigurator.NM_ADDRESS),
				// false);

			} catch (Exception e1) {
				LOG.error(e1.getMessage(), e1);
			}

			createHIDForQoSManager(false);
		}
	}

	/**
	 * Binds dynamically the configuration administrator bundle.
	 * 
	 * @param cm
	 *            Configuration Administrator reference.
	 */
	protected void bindConfiguration(ConfigurationAdmin cm) {
		if (configurator != null) {
			configurator.bindConfigurationAdmin(cm);
			if (activated) {
				configurator.registerConfiguration();
			}
		}
	}

	/**
	 * Binds dynamically the Network Manager bundle.
	 * 
	 * @param networkManagerApplication
	 *            Reference to Network Manager.
	 */
	protected void bindNM(NetworkManagerApplication networkManagerApplication) {
		this.nmOSGi = networkManagerApplication;
		if (activated) {
			createHIDForQoSManager(false);
		}
	}

	/**
	 * Binds dynamically the Ontology Manager bundle.
	 * 
	 * @param ontolgyManager
	 *            Reference to ontology manager.
	 */
	protected void bindOntology(ApplicationOntologyManager ontolgyManager) {
		this.ontologyOSGi = ontolgyManager;

	}

	/**
	 * Unbinds dynamically the remote WS client provider.
	 * 
	 * @param clientProvider
	 *            Reference to client provider bundle.
	 */
	protected void unbindClientProvider(RemoteWSClientProvider clientProvider) {
		removeQoSManagerHID();
		this.clientProvider = null;
		this.nmWSClient = null;
		LOG.info("RemoteWSClientProvider unbound");
	}

	/**
	 * Unbinds dynamically the configuration Administrator bundle.
	 * 
	 * @param cm
	 *            Reference to configuration Administrator bundle.
	 */
	protected void unbindConfiguration(ConfigurationAdmin cm) {
		configurator.unbindConfigurationAdmin(cm);
	}

	/**
	 * Unbinds dynamically the ontology manager.
	 * 
	 * @param ontolgyManager
	 *            Reference to ontology manager.
	 */
	protected void unbindOntology(ApplicationOntologyManager ontolgyManager) {
		// this.ontologyWSClient = null;
		this.ontologyOSGi = null;

	}

	/**
	 * Creates an HID for QoS Manager via LinkSmart.
	 * 
	 * @param renewCert
	 *            Flag indicating if the certificate needs to be renewed.
	 */
	private void createHIDForQoSManager(boolean renewCert) {
		if (qosManagerHID != null)
			return; // Only do this once

		boolean withNetworkManager =
				Boolean.parseBoolean(configurator
						.get(QoSManagerConfigurator.USE_NETWORK_MANAGER));

		LOG.info("withNetworkManager: " + withNetworkManager);

		if (withNetworkManager) {
			boolean osgi =
					Boolean
							.parseBoolean(configurator
									.get(QoSManagerConfigurator.USE_NETWORK_MANAGER_OSGI));
			if (osgi) {
				// Communicate directly with the Network Manager using OSGi
				if (nmOSGi != null) {
					createHIDViaOSGi(renewCert);
				}

			} else {
				// Load the WS client and try to communicated with NM
				if (clientProvider != null) {
					if (configurator.get(QoSManagerConfigurator.NM_ADDRESS) == null) {
						LOG
								.info("init - No Network Manager URL specfied. Will not be possible to create HID");
					} else {
						createHIDViaRemoteWSClientProvider();
					}
				}

			}

		}

	}

	/**
	 * Creates an HID for QoS Manager via OSGi.
	 * 
	 * @param renewCert
	 *            Flag indicating if the certificate needs to be renewed.
	 */
	private void createHIDViaOSGi(boolean renewCert) {
		try {

			if ((configurator.get(QoSManagerConfigurator.CERT_REF) == null)
					|| renewCert) {
				String pid = configurator.get(QoSManagerConfigurator.PID);
				if ((pid == null) || (pid.equals(""))) {
					pid =
							QOS_MANAGER
									+ InetAddress.getLocalHost().getHostName();
				}
				String xmlAttributes =
						getXMLAttributeProperties(pid, QOS_MANAGER_PORT, pid);
				CryptoHIDResult result =
						nmOSGi
								.createCryptoHID(
										xmlAttributes,
										HTTP_LOCALHOST
												+ System
														.getProperty(ORG_OSGI_SERVICE_HTTP_PORT)
												+ QOS_MANAGER_PATH);
				this.qosManagerHID = result.getHID();
				configurator.setConfiguration(QoSManagerConfigurator.CERT_REF,
						result.getCertRef());

			} else {
				this.qosManagerHID =
						nmOSGi
								.createCryptoHIDfromReference(
										configurator
												.get(QoSManagerConfigurator.CERT_REF),
										HTTP_LOCALHOST
												+ System
														.getProperty(ORG_OSGI_SERVICE_HTTP_PORT)
												+ QOS_MANAGER_PATH);
				if (this.qosManagerHID == null) {
					// Certificate ref is not valid...
					String pid = configurator.get(QoSManagerConfigurator.PID);
					if ((pid == null) || (pid.equals(""))) {
						pid =
								QOS_MANAGER
										+ InetAddress.getLocalHost()
												.getHostName();
					}
					String xmlAttributes =
							getXMLAttributeProperties(pid, QOS_MANAGER_PORT,
									pid);
					CryptoHIDResult result =
							nmOSGi
									.createCryptoHID(
											xmlAttributes,
											HTTP_LOCALHOST
													+ System
															.getProperty(ORG_OSGI_SERVICE_HTTP_PORT)
													+ QOS_MANAGER_PATH);
					this.qosManagerHID = result.getHID();
					configurator.setConfiguration(
							QoSManagerConfigurator.CERT_REF, result
									.getCertRef());
				}
			}

			LOG.info("QoSManager HID: " + qosManagerHID);
		} catch (RemoteException e) {
			LOG.error(e.getMessage(), e);
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * Creates an HID via the remote WS client provider.
	 */
	private void createHIDViaRemoteWSClientProvider() {
		try {

			if (nmWSClient == null) {
				try {
					this.nmWSClient =
							(NetworkManagerApplication) clientProvider
									.getRemoteWSClient(
											NetworkManagerApplication.class
													.getName(),
											(String) configurator
													.get(QoSManagerConfigurator.NM_ADDRESS),
											false);
				} catch (Exception e1) {
					LOG.error(e1.getMessage(), e1);
				}
			}
			if (configurator.get(QoSManagerConfigurator.CERT_REF) == null) {
				String pid = configurator.get(QoSManagerConfigurator.PID);
				if ((pid == null) || (pid.equals(""))) {
					pid =
							QOS_MANAGER
									+ InetAddress.getLocalHost().getHostName();
				}
				String xmlAttributes =
						getXMLAttributeProperties(pid, QOS_MANAGER_PORT, pid);
				CryptoHIDResult result =
						nmWSClient
								.createCryptoHID(
										xmlAttributes,
										HTTP_LOCALHOST
												+ System
														.getProperty(ORG_OSGI_SERVICE_HTTP_PORT)
												+ QOS_MANAGER_PATH);
				this.qosManagerHID = result.getHID();
				configurator.setConfiguration(QoSManagerConfigurator.CERT_REF,
						result.getCertRef());
			} else {

				this.qosManagerHID =
						nmWSClient
								.createCryptoHIDfromReference(
										configurator
												.get(QoSManagerConfigurator.CERT_REF),
										HTTP_LOCALHOST
												+ System
														.getProperty(ORG_OSGI_SERVICE_HTTP_PORT)
												+ QOS_MANAGER_PATH);
				if (this.qosManagerHID == null) {
					// Certificate ref is not valid...
					String pid = configurator.get(QoSManagerConfigurator.PID);
					if ((pid == null) || (pid.equals(""))) {
						pid =
								QOS_MANAGER
										+ InetAddress.getLocalHost()
												.getHostName();
					}
					String xmlAttributes =
							getXMLAttributeProperties(pid, QOS_MANAGER_PORT,
									pid);
					CryptoHIDResult result =
							nmWSClient
									.createCryptoHID(
											xmlAttributes,
											HTTP_LOCALHOST
													+ System
															.getProperty(ORG_OSGI_SERVICE_HTTP_PORT)
													+ QOS_MANAGER_PATH);
					this.qosManagerHID = result.getHID();
					configurator.setConfiguration(
							QoSManagerConfigurator.CERT_REF, result
									.getCertRef());
				}

			}

			LOG.info("QoSManager HID: " + qosManagerHID);

		} catch (RemoteException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param pid
	 *            Persistent identifier.
	 * @param sid
	 *            SID.
	 * @param desc
	 *            Description
	 * @return Returns the XML attribute properties of a given LinkSmart entity.
	 * @throws IOException
	 */
	private String getXMLAttributeProperties(String pid, String sid, String desc)
			throws IOException {
		Properties descProps = new Properties();
		descProps.setProperty(NetworkManagerApplication.PID, pid);
		descProps.setProperty(NetworkManagerApplication.DESCRIPTION, desc);
		descProps.setProperty(NetworkManagerApplication.SID, sid);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		descProps.storeToXML(bos, "");
		return bos.toString();
	}

	/**
	 * Removes the QoS Manager HID.
	 */
	private void removeQoSManagerHID() {
		if (qosManagerHID == null)
			return; // Only do this once

		boolean withNetworkManager =
				Boolean.parseBoolean(configurator
						.get(QoSManagerConfigurator.USE_NETWORK_MANAGER));

		LOG.info("withNetworkManager: " + withNetworkManager);

		boolean osgi =
				Boolean.parseBoolean(configurator
						.get(QoSManagerConfigurator.USE_NETWORK_MANAGER_OSGI));
		if (osgi) {
			// Communicate directly with the Network Manager using OSGi
			if (nmOSGi != null) {
				try {
					this.nmOSGi.removeHID(qosManagerHID);
					this.qosManagerHID = null;
					LOG.info("Removed QoSManager HID");
				} catch (RemoteException e) {
					LOG.error(e.getMessage(), e);
				}
			}

		} else {
			// Load the WS client and try to communicated with NM
			if (clientProvider != null) {
				if (configurator.get(QoSManagerConfigurator.NM_ADDRESS) == null) {

					LOG
							.info("init - No Network Manager URL specfied. Will not be possible to create HID");
				} else {
					try {
						if (nmWSClient == null) {
							try {
								this.nmWSClient =
										(NetworkManagerApplication) clientProvider
												.getRemoteWSClient(
														NetworkManagerApplication.class
																.getName(),
														(String) configurator
																.get(QoSManagerConfigurator.NM_ADDRESS),
														false);
							} catch (Exception e1) {
								LOG.error(e1.getMessage(), e1);
							}
						}
						nmWSClient.removeHID(qosManagerHID);
						this.qosManagerHID = null;
						LOG.info("Removed QoSManager HID");

					} catch (RemoteException e) {
						LOG.error(e.getMessage(), e);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
				}
			}

		}

	}

	/**
	 * Unbinds dynamically the network manager.
	 * 
	 * @param networkManagerApplication
	 *            Reference to network manager.
	 */
	@SuppressWarnings("deprecation")
	protected void unbindNM(NetworkManagerApplication networkManagerApplication) {
		try {
			this.nmOSGi.startNM();
		} catch (RemoteException e) {
			LOG.error(e.getMessage());
		}
		if (activated) {
			removeQoSManagerHID();
		}
		this.nmOSGi = null;
	}

	/**
	 * OSGi Declarative service method to undeploy bundle.
	 * 
	 * @param context
	 *            Reference to ComponentContext
	 */
	protected void deactivate(ComponentContext context) {
		removeQoSManagerHID();

		activated = false;

		LOG.debug("QoSManager deactivated");
	}

	/**
	 * Initializes QoS Manager.
	 */
	private void init() {

		createHIDForQoSManager(false);

		LOG.info("QoSManager initialized");

	}

}
