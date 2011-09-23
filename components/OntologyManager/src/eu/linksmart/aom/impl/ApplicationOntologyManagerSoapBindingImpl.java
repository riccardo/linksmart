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
/**
 * Copyright (C) 2006-2010 Technical University of Kosice
 *                         the HYDRA consortium, EU project IST-2005-034891
 *
 * This file is part of LinkSmart.
 *
 * LinkSmart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * version 3 as published by the Free Software Foundation.
 *
 * LinkSmart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with LinkSmart.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.linksmart.aom.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openrdf.model.vocabulary.XMLSchema;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.EventAdmin;

import eu.linksmart.aom.ApplicationOntologyManager;
import eu.linksmart.aom.discovery.DeviceDiscovery;
import eu.linksmart.aom.discovery.SemanticDeviceDiscovery;
import eu.linksmart.aom.generator.ConfigurationGenerator;
import eu.linksmart.aom.generator.ModelGenerator;
import eu.linksmart.aom.generator.QueryResponseGenerator;
import eu.linksmart.aom.generator.SCPDGenerator;
import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.processor.ConfigurationProcessor;
import eu.linksmart.aom.processor.DiscoveryProcessor;
import eu.linksmart.aom.processor.EnergyProfileProcessor;
import eu.linksmart.aom.processor.EventProcessor;
import eu.linksmart.aom.processor.FormDataProcessor;
import eu.linksmart.aom.processor.SCPDProcessor;
import eu.linksmart.aom.repository.AOMRepository;
import eu.linksmart.aom.repository.PIDRuleResolver;
import eu.linksmart.aom.repository.RepositoryFactory;
import eu.linksmart.aom.repository.Serializer;
import eu.linksmart.clients.RemoteWSClientProvider;
import eu.linksmart.network.CryptoHIDResult;
import eu.linksmart.network.NetworkManagerApplication;

/**
 * Facade class which serves as the access point to all ontology related
 * functionality.
 * 
 * @author Peter Kostelnik, Peter Smatana
 * 
 */
public class ApplicationOntologyManagerSoapBindingImpl implements
		ApplicationOntologyManager {

	private final static Logger LOG = Logger
			.getLogger(ApplicationOntologyManagerSoapBindingImpl.class
					.getName());
	private static final String DEFAULT_REPO_LOCATION = "AOM_Repository";
	public static final String ONTOLOGY_MANAGER_PATH = "/axis/services/ApplicationOntologyManager";
	public static final String NETWORK_MANAGER_PATH = "/axis/services/NetworkManagerApplication";

	private AOMRepository repository = null;
	private EventAdmin ea;
	private NetworkManagerApplication nm;
	private boolean useNetworkManager;
	private boolean createdHID;
	private String ontologyManagerHID;
	private OntologyManagerConfigurator configurator;
	private boolean nmOsgi;
	private RemoteWSClientProvider clientProvider;
	private boolean activated;

	public ApplicationOntologyManagerSoapBindingImpl() {
		if (repository == null) {
			repository = RepositoryFactory.local(DEFAULT_REPO_LOCATION);
		}
	}

	public ApplicationOntologyManagerSoapBindingImpl(AOMRepository repo) {
		if (repository != null) {
			repository.close();
		}
		repository = repo;
	}

	/**
	 * Activate method
	 * 
	 * @param context
	 *            the bundle's execution context
	 */
	protected void activate(ComponentContext context) {
		LOG.info("OntologyManager activating...");
		// set up configurator for ontology manager

		if (repository == null) {
			repository = RepositoryFactory.local(DEFAULT_REPO_LOCATION);
		}

		configurator = new OntologyManagerConfigurator(this, context
				.getBundleContext());
		configurator.registerConfiguration();

		createHIDForOntologyManager(false);

		this.activated = true;

		LOG.info("OntologyManager activated...");
	}

	/**
	 * Deactivate method
	 * 
	 * @param context
	 *            the bundle's context
	 */
	protected void deactivate(ComponentContext context) {
		if (repository != null) {
			repository.close();
			repository = null;
		}
	}

	protected void bindNM(NetworkManagerApplication nm) {
		this.nm = nm;
		nmOsgi = true;
		if (activated) {
			createHIDForOntologyManager(false);
		}
	}

	protected void unbindNM(NetworkManagerApplication nm) {
		if (activated) {
			removeOntologyManagerHID();
			createdHID = false;
		}
		this.nm = null;
		nmOsgi = false;

	}

	protected void bindConfigurationAdmin(ConfigurationAdmin ca) {
		if (configurator != null) {
			configurator.bindConfigurationAdmin(ca);
			if (activated == true) {
				configurator.registerConfiguration();
			}
		}
	}

	protected void unbindConfigurationAdmin(ConfigurationAdmin ca) {
		configurator.unbindConfigurationAdmin(ca);
	}

	protected void bindWSProvider(RemoteWSClientProvider clientProvider) {
		LOG.debug("RemoteWSClientProvider bound in OntologyManager");
		this.clientProvider = clientProvider;
		if (activated) {
			createHIDForOntologyManager(false);
		}
	}

	protected void unbindWSProvider(RemoteWSClientProvider clientProvider) {
		removeOntologyManagerHID();
		this.clientProvider = null;
		if (!nmOsgi) {
			removeOntologyManagerHID();
			createdHID = false;
			this.nm = null;
		}
		LOG.debug("RemoteWSClientProvider unbound from OntologyManager");
	}

	private void createHIDForOntologyManager(boolean renewCert) {

		if (ontologyManagerHID != null)
			return; // Only do this once

		boolean withNetworkManager = Boolean.parseBoolean(configurator
				.get(OntologyManagerConfigurator.USE_NETWORK_MANAGER));

		LOG.debug("OntologyManager with NetworkManager: " + withNetworkManager);

		if (withNetworkManager) {
			String nmAddress = (String) configurator
					.get(OntologyManagerConfigurator.NETWORK_MANAGER_ADDRESS);
			if (nmAddress != null && nmAddress.equalsIgnoreCase("local")) {
				// Communicate directly with the Network Manager using OSGi
				if (this.nmOsgi) {
					try {
						// ontologymanager has no certificate yet or needs new
						// then
						// create it
						if ((configurator
								.get(OntologyManagerConfigurator.CERTIFICATE_REF) == null)
								|| renewCert == true) {
							this.ontologyManagerHID = createCertificate();
						} else {
							this.ontologyManagerHID = nm
									.createCryptoHIDfromReference(
											configurator
													.get(OntologyManagerConfigurator.CERTIFICATE_REF),
											"http://localhost:"
													+ System
															.getProperty("org.osgi.service.http.port")
													+ ONTOLOGY_MANAGER_PATH);
							if (this.ontologyManagerHID == null) {
								// Certificate ref is not valid...
								this.ontologyManagerHID = createCertificate();
							}
						}
						LOG.info("OntologyManager HID: " + ontologyManagerHID);
					} catch (Exception e) {
						LOG.error(
								"Error while creating HID for OntologyManager: "
										+ e.getMessage(), e);
					}
				}
			} else if (!nmOsgi) {
				// Load the WS client and try to communicated with NM
				if (clientProvider != null) {
					if (nmAddress != null) {
						try {

							if (nm == null) {
								try {
									this.nm = (NetworkManagerApplication) clientProvider
											.getRemoteWSClient(
													NetworkManagerApplication.class
															.getName(),
													(String) configurator
															.get(OntologyManagerConfigurator.NETWORK_MANAGER_ADDRESS),
													false);
								} catch (Exception e1) {
									LOG.error(
											"Error while creating client to NetworkManager: "
													+ e1.getMessage(), e1);
								}
							}
							if (configurator
									.get(OntologyManagerConfigurator.CERTIFICATE_REF) != null) {
								this.ontologyManagerHID = nm
										.createCryptoHIDfromReference(
												configurator
														.get(OntologyManagerConfigurator.CERTIFICATE_REF),
												"http://localhost:"
														+ System
																.getProperty("org.osgi.service.http.port")
														+ ONTOLOGY_MANAGER_PATH);
								if (this.ontologyManagerHID == null) {
									// Certificate ref is not valid...
									this.ontologyManagerHID = createCertificate();
								}
							} else {
								this.ontologyManagerHID = createCertificate();
							}
							LOG.info("OntologyManager HID: "
									+ ontologyManagerHID);
						} catch (Exception e) {
							LOG.error(e.getMessage(), e);
						}
					} else {
						LOG
								.info("init - No Network Manager URL specfied. Will not be possible to create HID");
					}
				}
			} else {
				LOG
						.error("Cannot use remote Network Manager when local is running!");
			}

		}
	}

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

	private String createCertificate() throws IOException {
		String pid = configurator.get(OntologyManagerConfigurator.PID);
		// if no PID set use local IP as identifier
		if ((pid == null) || (pid.equals(""))) {
			pid = "OntologyManager:" + InetAddress.getLocalHost().getHostName();
		}
		String xmlAttributes = getXMLAttributeProperties(pid,
				"OntologyManager", pid);
		CryptoHIDResult result = nm.createCryptoHID(xmlAttributes,
				"http://localhost:"
						+ System.getProperty("org.osgi.service.http.port")
						+ ONTOLOGY_MANAGER_PATH);
		configurator.setConfiguration(
				OntologyManagerConfigurator.CERTIFICATE_REF, result
						.getCertRef());
		return result.getHID();
	}

	private void removeOntologyManagerHID() {
		if (ontologyManagerHID == null)
			return; // Only do this once
		if (nm != null) {
			try {
				nm.removeHID(ontologyManagerHID);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ontologyManagerHID = null;
		}
	}

	public void applyConfigurations(Hashtable updates) {
		LOG.debug("Applying configurations in OntologyManager");

		if (updates.containsKey(OntologyManagerConfigurator.PID)) {
			removeOntologyManagerHID();
			createHIDForOntologyManager(true);
			createdHID = true;
		}
		if (updates
				.containsKey(OntologyManagerConfigurator.USE_NETWORK_MANAGER)
				|| updates
						.containsKey(OntologyManagerConfigurator.NETWORK_MANAGER_ADDRESS)) {
			boolean useNetworkManager = Boolean.parseBoolean((String) updates
					.get(OntologyManagerConfigurator.USE_NETWORK_MANAGER));
			if (useNetworkManager == true) {
				if (createdHID == false) {
					createHIDForOntologyManager(false);
					createdHID = true;
				}
			} else {
				removeOntologyManagerHID();
				createdHID = false;
			}
		}
		// else is handled by OSGI ConfigAdmin

	}

	private AOMRepository getRepository() {
		if (repository == null) {
			repository = RepositoryFactory.local("developement");
		}
		return repository;
	}

	/**
	 * Broadcasts the Graph of the new device to OSGi environment so other
	 * components can react on it.
	 * 
	 * @param device
	 */
	private void announceNewDevice(Graph device) {
		if (ea != null) {
			try {
				HashMap<String, String> rdf = new HashMap<String, String>();
				rdf.put("rdf-xml", Serializer.serialize(device));
				ea.postEvent(new org.osgi.service.event.Event(
						"com/hydra/semantic/addDevice", rdf));
			} catch (Exception e) {
				/*
				 * If you arrive here, you should play the lottery. EventAdmin
				 * has been unregistered during the last three lines. Nothing to
				 * do here.
				 */
			}
		}
	}

	/**
	 * Broadcasts the URI of the removed device to OSGi environment so other
	 * components can react on it.
	 * 
	 * @param device
	 */
	private void announceRemovedDevice(Graph device) {
		if (ea != null) {
			try {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("deviceURI", device.getBaseURI());
				ea.postEvent(new org.osgi.service.event.Event(
						"com/hydra/semantic/removeDevice", map));
			} catch (Exception e) {
				// Nothing to do here.
			}
		}
	}

	/**
	 * Called by OSGi framework when an EventAdmin service is available.
	 * 
	 * @param ea
	 */
	protected void bindEventAdmin(EventAdmin ea) {
		this.ea = ea;
	}

	/**
	 * Called by OSGi framework when EventAdmin has disappeared.
	 * 
	 * @param ea
	 */
	protected void unbindEventAdmin(EventAdmin ea) {
		this.ea = null;
	}

	/**
	 * Creates a new ontology template for specific device model. The basic
	 * device model will contain manufacturer information and the models of
	 * device services. Discovery, configuration and energy profiles are added
	 * separately.
	 * 
	 * @param scpd
	 *            The XML representation of new device template created in DDK.
	 * @return Ontology URI of newly created device template.
	 */
	public String createDeviceTemplate(String scpd)
			throws java.rmi.RemoteException {
		SCPDProcessor sp = new SCPDProcessor(repository);
		Graph device = sp.process(scpd);
		getRepository().store(device);
		return device.getBaseURI();
	}

	/**
	 * Assigns the PID of the newly created device. If device PID exists, it is
	 * replaced with the new value. After the PID and HID are assigned, the
	 * semantic devices has to be resolved to take into account the new runtime
	 * ontology population. The result of new enabled semantic devices
	 * representation is returned in the form of SCPD docs, one for each enabled
	 * semantic device.
	 * 
	 * @param pid
	 *            PID value.
	 * @return The list of SCPD documents.
	 */
	public String assignPID(String deviceURI, String pid)
			throws java.rmi.RemoteException {
		try {
			AOMRepository repo = getRepository();

			Graph device = repo.getResource(deviceURI);
			String dPID = device.value(Device.PID);
			if (dPID != null) {
				repo.remove(device.getBaseURI(), Device.PID);
			}

			Graph gPID = new Graph(deviceURI);
			ResourceUtil.addStatement(deviceURI, Device.PID, pid,
					XMLSchema.STRING, repo.getValueFactory(), gPID);
			repo.store(gPID);

			announceNewDevice(device);

			PIDRuleResolver resolver = new PIDRuleResolver(repo);
			resolver.resolve(pid);

			SemanticDeviceDiscovery disco = new SemanticDeviceDiscovery(repo);
			Set<Graph> devices = disco.resolveDevices();

			SCPDGenerator g = new SCPDGenerator();
			return g.toString(g.process(devices));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Removes device graph with specified ontology URI. After the device is
	 * removed, the semantic devices has to be resolved to take into account the
	 * new runtime ontology population. The result of new enabled semantic
	 * devices representation is returned in the form of SCPD docs, one for each
	 * enabled semantic device.
	 * <p/>
	 * When device is removed, the full ontology structure related to the device
	 * URI is removed also (except the static resources, such as rdfs classes or
	 * static taxonomy instances (units or security related instances).
	 * 
	 * @param deviceURI
	 *            Ontology instance URI.
	 * @return The list of SCPD documents.
	 */
	public String removeDevice(String deviceURI)
			throws java.rmi.RemoteException {
		AOMRepository repo = getRepository();
		Graph device = repo.getResource(deviceURI);
		if (device != null && device.getStmts().size() > 0) {
			repo.remove(device);

			announceRemovedDevice(device);

			SemanticDeviceDiscovery disco = new SemanticDeviceDiscovery(repo);
			Set<Graph> devices = disco.resolveDevices();

			SCPDGenerator g = new SCPDGenerator();
			return g.toString(g.process(devices));
		}
		return null;
	}

	/**
	 * Semantic resolution of device. When device enters the network, its
	 * low-level discovery information is compared to discovery informations
	 * attached to device templates. When one best matching template is found,
	 * device is cloned from this template and stored into ontology as the new
	 * runtime instance.
	 * 
	 * @param discoXML
	 *            Low-level discovery information.
	 * @return SCPD XML of newly created device. If there was no or there were
	 *         more matching templates, the error message is returned.
	 */
	public String resolveDevice(String discoXML)
			throws java.rmi.RemoteException {
		AOMRepository repo = getRepository();
		DeviceDiscovery disco = new DeviceDiscovery(repo);
		Graph template = disco.resolveDevice(discoXML);
		if (template.value(Device.errorMessage) == null) {
			Graph device = template.clone(repo, template.getBaseURI());
			repo.store(device);

			SCPDGenerator g = new SCPDGenerator();
			return g.toString(g.process(device));
		} else {
			SCPDGenerator g = new SCPDGenerator();
			return g.toString(g.error(template));
		}
	}

	/**
	 * Creates testing device instance and simulates the run-time process of
	 * device discovery. Used only for testing of application behaviour in IDE.
	 * 
	 * @param templateURI
	 *            Ontology template URI to be cloned.
	 * @param pid
	 *            PID to be assigned to testing device instance.
	 * @return Notification on operation success.
	 */
	public boolean createTestingRuntimeClone(String templateURI, String pid)
			throws java.rmi.RemoteException {
		AOMRepository repo = getRepository();
		Graph template = repo.getResource(templateURI);
		Graph runtime = template.clone(repo, templateURI);
		ResourceUtil.addStatement(runtime.getBaseURI(), Device.isTesting,
				"true", XMLSchema.BOOLEAN, repo.getValueFactory(), runtime);
		repo.store(runtime);
		assignPID(runtime.getBaseURI(), pid);
		return true;
	}

	/**
	 * Assigns the discovery model to the device instance. If the discovery info
	 * exists, it is replaced with the new information.
	 * 
	 * @param deviceURI
	 *            Ontology URI of device.
	 * @param discovery
	 *            The XML representation of device discovery information
	 *            retrieved from some of low-level discovery managers.
	 * @return Notification on operation success.
	 */
	public boolean assignDiscoveryInfo(String deviceURI, String discovery)
			throws java.rmi.RemoteException {
		try {
			AOMRepository repo = getRepository();

			Graph device = repo.getResource(deviceURI);
			Graph disco = device.subGraph(Device.hasDiscoveryInfo);
			if (disco != null) {
				repo.remove(disco);
			}

			DiscoveryProcessor dp = new DiscoveryProcessor(repository);
			Graph graph = dp.process(deviceURI, discovery);
			repo.store(graph);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Assigns the model of events to the device instance. If the event model
	 * exists, it is replaced with the new one.
	 * 
	 * @param deviceURI
	 *            Ontology URI of device.
	 * @param eventModel
	 *            The XML representation of device event model.
	 * @return Notification on operation success.
	 */
	public boolean assignEventModel(String deviceURI, String eventModel)
			throws java.rmi.RemoteException {
		try {
			AOMRepository repo = getRepository();

			Graph device = repo.getResource(deviceURI);
			Set<Graph> events = device.subGraphs(Device.hasEvent);
			Iterator<Graph> i = events.iterator();
			while (i.hasNext()) {
				repo.remove(i.next());
			}

			EventProcessor ep = new EventProcessor(repository);
			Graph graph = ep.process(deviceURI, eventModel);
			repo.store(graph);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Assigns the energy profile to the device instance. If the energy profile
	 * exists, it is replaced with the new information.
	 * 
	 * @param deviceURI
	 *            Ontology URI of device.
	 * @param energyProfile
	 *            The XML representation of device energy profile.
	 * @return Notification on operation success.
	 */
	public boolean assignEnergyProfile(String deviceURI, String energyProfile)
			throws java.rmi.RemoteException {
		try {
			AOMRepository repo = getRepository();

			Graph device = repo.getResource(deviceURI);
			Graph energy = device.subGraph(Device.hasEnergyProfile);
			if (energy != null) {
				repo.remove(energy);
			}

			EnergyProfileProcessor ep = new EnergyProfileProcessor(repository);
			Graph graph = ep.process(deviceURI, energyProfile);
			repo.store(graph);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Assigns the DDK configuration to the device instance. If the
	 * configuration profile exists, it is replaced with the new information.
	 * 
	 * @param deviceURI
	 *            Ontology URI of device.
	 * @param configuration
	 *            The XML representation of DDK configuration.
	 * @return Notification on operation success.
	 */
	public boolean assignConfiguration(String deviceURI, String configuration)
			throws java.rmi.RemoteException {
		try {
			AOMRepository repo = getRepository();

			Graph device = repo.getResource(deviceURI);
			Graph config = device.subGraph(Device.hasConfiguration);
			if (config != null) {
				repo.remove(config);
			}

			ConfigurationProcessor cp = new ConfigurationProcessor(repository);
			Graph graph = cp.process(deviceURI, configuration);
			repo.store(graph);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Used for DDK support. Retrieves all DDK configurations grouped by the
	 * device type. For each device type, the list of existing configurations is
	 * attached.
	 * 
	 * @return XML containing DDK configurations grouped by device type.
	 */
	public String getConfigurations() throws java.rmi.RemoteException {
		try {
			AOMRepository repo = getRepository();

			String query = "device:hasConfiguration";
			Set<Graph> devices = repo.getDevices(query);

			Map<String, HashSet<Graph>> typeMap = new HashMap<String, HashSet<Graph>>();
			for (Graph device : devices) {
				String rdfType = device.value(Rdf.rdfType);
				if (typeMap.containsKey(rdfType)) {
					typeMap.get(rdfType).add(device);
				} else {
					HashSet<Graph> newType = new HashSet<Graph>();
					newType.add(device);
					typeMap.put(rdfType, newType);
				}
			}

			ConfigurationGenerator cg = new ConfigurationGenerator();
			return cg.toString(cg.getConfigurations(typeMap));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Used for DDK support. Retrieves all device types from taxonomy, so DDK
	 * can assign the device type to new template.
	 * 
	 * @return XML device types.
	 */
	public String getDeviceTypes() throws java.rmi.RemoteException {
		try {
			ModelGenerator g = new ModelGenerator(getRepository());
			return g.getDeviceTypes();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Executes the query searching primarily for services and retrieves the XML
	 * containing the list of devices having matched services including the
	 * device and service information.
	 * 
	 * @param serviceQuery
	 *            Query specifying the expectations on services to be matched.
	 * @param deviceQuery
	 *            Query specifying the further expectations on devices having
	 *            matched services.
	 * @param deviceRequirements
	 *            Query specifying the parameters to be returned for devices.
	 * @param serviceRequirements
	 *            Query specifying the parameters to be returned for services.
	 * @return XML containing the required description of devices and the
	 *         matched services.
	 */
	public String getDevicesWithServices(String serviceQuery,
			String deviceQuery, String deviceRequirements,
			String serviceRequirements) throws java.rmi.RemoteException {
		QueryResponseGenerator g = new QueryResponseGenerator();
		try {
			AOMRepository repo = getRepository();
			return g.toString(g.devicesWithServicesResult(repo
					.getDevicesWithServices(serviceQuery, deviceQuery),
					deviceRequirements, serviceRequirements));
		} catch (Exception e) {
			e.printStackTrace();
			return g.toString(g.error(e));
		}
	}

	/**
	 * Executes the query searching for devices and retrieves the XML containing
	 * the list of matched devices.
	 * 
	 * @param deviceQuery
	 *            Query specifying the expectations on devices.
	 * @param deviceRequirements
	 *            Query specifying the parameters to be returned for devices.
	 * @return XML containing the required description of matched devices.
	 */
	public String getDevices(String deviceQuery, String deviceRequirements)
			throws java.rmi.RemoteException {
		QueryResponseGenerator g = new QueryResponseGenerator();
		try {
			deviceQuery = "device:isDeviceTemplate;\"false\"^^xsd:boolean\n, "
					+ deviceQuery;
			AOMRepository repo = getRepository();
			return g.toString(g.devicesResult(repo.getDevices(deviceQuery),
					deviceRequirements));
		} catch (Exception e) {
			e.printStackTrace();
			return g.toString(g.error(e));
		}
	}

	public boolean updateValue(String deviceURI, String path)
			throws java.rmi.RemoteException {
		AOMRepository repo = getRepository();
		return repo.updateValue(deviceURI, path);
	}

	// ==================================================
	// IDE SUPPORT
	// ==================================================

	/**
	 * Removes all run-time device instances including testing device and
	 * semantic device instances.
	 * 
	 * @return Notification on operation success.
	 */
	public boolean removeRunTimeDevices() throws java.rmi.RemoteException {
		getRepository().removeRunTimeDevices();
		return true;
	}

	public String addValue(String sURI, String pURI, String value,
			String dataType, boolean append) throws java.rmi.RemoteException {
		AOMRepository repo = getRepository();
		return repo.add(sURI, pURI, value, dataType, append);
	}

	public String addValue(String sURI, String pURI, String oURI, boolean append)
			throws java.rmi.RemoteException {
		AOMRepository repo = getRepository();
		return repo.add(sURI, pURI, oURI, append);
	}

	public String addFormData(String xml, boolean append)
			throws RemoteException {
		FormDataProcessor fdp = new FormDataProcessor(getRepository());
		return fdp.store(xml, append);
	}

	public String remove(String sURI, String pURI, String oURI)
			throws java.rmi.RemoteException {
		AOMRepository repo = getRepository();
		return repo.remove(sURI, pURI, oURI);
	}

	public String remove(String sURI, String pURI, String value, String dataType)
			throws java.rmi.RemoteException {
		AOMRepository repo = getRepository();
		return repo.remove(sURI, pURI, value, dataType);
	}

	/**
	 * Used for IDE support. Returns the full device taxonomy including the
	 * hierarchy of classes and the device instances information.
	 * 
	 * @return XML containing the full taxonomy tree.
	 */
	public String getDeviceTree() throws java.rmi.RemoteException {
		return new ModelGenerator(getRepository()).getDeviceTree();
	}

	/**
	 * Used for IDE support. Returns the full device taxonomy including the
	 * hierarchy of classes and the device instances information for specified
	 * ontology class URI.
	 * 
	 * @param classURI
	 *            Ontology URI of class for which the tree is generated.
	 * @param includeInstances
	 *            Flag indicating if the information on instances has to be
	 *            included.
	 * @return XML containing the full taxonomy tree.
	 */
	public String getTree(String classURI, boolean includeInstances)
			throws java.rmi.RemoteException {
		return new ModelGenerator(getRepository()).getTree(classURI,
				includeInstances);
	}

	/**
	 * Used for IDE support. Returns the full tree of instance specified as
	 * instance ontolgy URI. Tree is recursively generated using the instance
	 * properties.
	 * 
	 * @param instanceURI
	 *            Ontology URI of instance for which the tree is generated.
	 * @param includeInstances
	 *            Flag indicating if the information on instances has to be
	 *            included.
	 * @return XML containing the full instance tree.
	 */
	public String getInstanceTree(String instanceURI)
			throws java.rmi.RemoteException {
		return new ModelGenerator(getRepository()).getInstanceTree(instanceURI);
	}

	/**
	 * Used for IDE support. Returns the description of literal properties of
	 * ontology class having this class defined as the domain in metamodel.
	 * 
	 * @param classURI
	 *            Ontology URI of class for which the information is generated.
	 * @return XML containing description of class literals.
	 */
	public String getClassLiterals(String classURI)
			throws java.rmi.RemoteException {
		return new ModelGenerator(getRepository()).getClassLiterals(classURI);
	}

	/**
	 * Used for IDE support. Generates the description of properties, which are
	 * used for driving the IDE behavior when extending the instance
	 * information. The property information is added to the description if
	 * property is instance of:
	 * <ul>
	 * <li>model:AnnotationProperty</li>
	 * <li>model:FormProperty or</li>
	 * <li>model:FormFieldProperty</li>
	 * </ul>
	 * 
	 * @return XML containing the property annotation model.
	 */
	public String getPropertyAnnotationModel() throws java.rmi.RemoteException {
		Set<Graph> properties = getRepository().getPropertyAnnotationModels();
		ModelGenerator g = new ModelGenerator(null);
		return g.toString(g.getPropertyAnnotationModel(properties));
	}

	/**
	 * Used for IDE support. Evaluates the results returned by service query
	 * actually edited in service query builder.
	 * 
	 * @param serviceQuery
	 *            Query specifying the services to be matched.
	 * @return XML containing the list of devices with matched services.
	 */
	public String getDevicesWithServices(String serviceQuery)
			throws java.rmi.RemoteException {
		QueryResponseGenerator rg = new QueryResponseGenerator();
		ModelGenerator g = new ModelGenerator(getRepository());
		if (serviceQuery == null || serviceQuery.trim().equals(""))
			return g.getResults(new HashSet<Graph>());
		try {
			AOMRepository repo = getRepository();
			return g.getResults(repo.getDevicesWithServices(serviceQuery, ""));
		} catch (Exception e) {
			e.printStackTrace();
			return g.toString(rg.error(e));
		}
	}

	/**
	 * Used for IDE support. Evaluates the results returned by device query
	 * actually edited in device query builder.
	 * 
	 * @param deviceQuery
	 *            Query specifying the device to be matched.
	 * @return XML containing the list of matched devices.
	 */
	public String getDevices(String deviceQuery)
			throws java.rmi.RemoteException {
		QueryResponseGenerator rg = new QueryResponseGenerator();
		ModelGenerator g = new ModelGenerator(getRepository());
		if (deviceQuery == null || deviceQuery.trim().equals(""))
			return g.getResults(new HashSet<Graph>());
		try {
			deviceQuery = "device:isDeviceTemplate;\"false\"^^xsd:boolean\n, "
					+ deviceQuery;
			AOMRepository repo = getRepository();
			return g.getResults(repo.getDevices(deviceQuery));
		} catch (Exception e) {
			e.printStackTrace();
			return g.toString(rg.error(e));
		}
	}

	/**
	 * Used for IDE support. Returns the full SCPD document of specified device.
	 * 
	 * @param deviceURI
	 *            Ontology URI of device.
	 * @return The XML SPCD document of device.
	 */
	public String getSCPD(String deviceURI) throws java.rmi.RemoteException {
		Graph device = repository.getResource(deviceURI);
		if (device != null) {
			SCPDGenerator g = new SCPDGenerator();
			return g.toString(g.process(device));
		}
		return null;
	}

	@Override
	/**
	 * Used for IDE support.
	 * Dumps the full repository to be stored locally by IDE.
	 * @return The RDF/XML of actual repository content.
	 */
	public String dump() throws java.rmi.RemoteException {
		return getRepository().serialize();
	}

	@Override
	/**
	 * Used for IDE support.
	 * Cleans the repository content.
	 * @return Notification on operation success.
	 */
	public boolean clean() throws java.rmi.RemoteException {
		AOMRepository repo = getRepository();
		return repo.clear();
	}

	@Override
	/**
	 * Used for IDE support.
	 * Updates the repository content with new information.
	 * @param RDF/XML to be stored in repository.
	 * @return Notification on operation success.
	 */
	public boolean update(String xml) throws java.rmi.RemoteException {
		AOMRepository repo = getRepository();
		return repo.update(xml);
	}
}
