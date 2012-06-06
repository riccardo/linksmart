package eu.linksmart.remote.service.store.impl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.clients.RemoteWSClientProvider;
import eu.linksmart.network.NetworkManagerApplication;
import eu.linksmart.remote.service.store.LinkSmartRemoteServiceStore;

public class LinkSmartRemoteServiceStoreImpl implements
		LinkSmartRemoteServiceStore {

	private Thread registerAtNetworkManagerThread;

	private class GetRemoteServiceByDescriptionHelper {

		private String serviceDescription;
		private String className;

		public GetRemoteServiceByDescriptionHelper(String serviceDescription,
				String className) {
			this.serviceDescription = serviceDescription;
			this.className = className;
		}

		public Object getService() {

			// Use flags for not finding the service
			boolean notFound = true;

			int counter = 0;

			Object service = null;

			while (notFound && counter < 2) {

				String serviceHID = getServiceHID(serviceDescription);
				LOG.debug("Service HID: " + serviceHID);

				if (StringUtils.isNotEmpty(serviceHID)) {

					String targetUrl4HydraService = "http://localhost:8082/SOAPTunneling/0"
							+ "/" + serviceHID + "/0/hola";
					LOG.debug("TargetURL for service with pid "
							+ serviceDescription + " : "
							+ targetUrl4HydraService);
					try {
						service = remoteWSClientProvider.getRemoteWSClient(
								className, targetUrl4HydraService, false);

						if (service != null) {

							remoteHydraServices
									.put(serviceDescription, service);

							notFound = false;
						}

					} catch (Exception e) {
						LOG.error("!!!Cannot find service with description: "
								+ serviceDescription + " !!!", e);
					}
				} else {

					LOG.debug("Service with pid " + serviceDescription
							+ " not found.");
				}

				counter++;

			}

			return service;
		}

		private String getServiceHID(String description) {

			@SuppressWarnings("rawtypes")
			Vector results = null;

			try {
				results = networkManager.getHIDsbyDescription(description);
				LOG.debug("Number of available " + serviceDescription
						+ " HIDs: " + results.size());
			} catch (IOException e) {
				LOG.error("Cannot find HID for " + serviceDescription
						+ " for query: " + description, e);
			}
			if (results == null || (results != null && results.size() == 0)) {
				return null;
			} else {
				String serviceId = (String) results.get(0);
				return serviceId;
			}
		}

	}

	private class GetRemoteServiceByPIDHelper {

		private String servicePID;
		private String className;

		public GetRemoteServiceByPIDHelper(String servicePID, String className) {
			this.servicePID = servicePID;
			this.className = className;
		}

		public Object getService() {

			// Use flags for not finding the service
			boolean notFound = true;

			int counter = 0;

			Object service = null;

			while (notFound && counter < 2) {

				String serviceHID = getServiceCryptoHID(myHID);
				LOG.debug("Service HID: " + serviceHID);

				if (StringUtils.isNotEmpty(serviceHID)) {

					String targetUrl4HydraService = "http://localhost:8082/SOAPTunneling/0"
							+ "/" + serviceHID + "/0/hola";
					LOG.debug("TargetURL for service with pid " + servicePID
							+ " : " + targetUrl4HydraService);
					try {
						service = remoteWSClientProvider.getRemoteWSClient(
								className, targetUrl4HydraService, false);

						if (service != null) {

							remoteHydraServices.put(servicePID, service);

							notFound = false;
						}

					} catch (Exception e) {
						LOG.error("!!!Cannot find service with PID: "
								+ servicePID + " !!!", e);
					}
				} else {

					LOG.debug("Service with pid " + servicePID + " not found.");
				}

				counter++;

			}

			return service;

		}

		// TODO Amro refactor this method is redundant, see getServiceCryptoHID
		// in upper class
		private String getServiceCryptoHID(String requesterHID) {
			String[] results = null;
			long maxTime = 100000;
			int maxResponses = 1;
			String query = "((PID==" + servicePID + "))";
			try {
				LOG.debug("Trying to get " + servicePID
						+ " with attributes: RequesterHID: " + requesterHID
						+ ", query: " + query);
				Thread.sleep(5000);
				results = networkManager.getHIDByAttributes(requesterHID, null,
						query, maxTime, maxResponses);
				LOG.debug("Number of available " + servicePID + " HIDs: "
						+ results.length);
			} catch (IOException e) {
				LOG.error("Cannot find HID for " + servicePID + " for query: "
						+ query, e);
			} catch (InterruptedException e) {
				LOG.error(
						"Error while waiting for NetworkManager to find HID of "
								+ servicePID, e);
			}
			if (results == null || (results != null && results.length == 0)) {
				return null;
			} else {
				return results[0];
			}
		}

	}

	private static final String HYDRA_REMOTE_SERVICE_STORE_PATH = LinkSmartRemoteServiceStore.class
			.getSimpleName();
	private static final String OSGI_SERVICE_HTTP_PORT = System
			.getProperty("org.osgi.service.http.port");
	private static final String AXIS_SERVICES_PATH = "http://localhost:"
			+ OSGI_SERVICE_HTTP_PORT + "/axis/services/";

	private RemoteWSClientProvider remoteWSClientProvider;
	private NetworkManagerApplication networkManager;
	private BundleContext bundleContext;
	private HashMap<String, Object> remoteHydraServices;
	private String myHID;
	private Logger LOG = Logger.getLogger(LinkSmartRemoteServiceStoreImpl.class
			.getName());

	@Override
	public synchronized Object getRemoteHydraServiceByPID(String pid,
			String className) throws Exception {

		if (StringUtils.isNotEmpty(pid) && StringUtils.isNotEmpty(className)) {

			if (remoteHydraServices.containsKey(pid)) {

				return remoteHydraServices.get(pid);

			} else {
				GetRemoteServiceByPIDHelper helper = new GetRemoteServiceByPIDHelper(
						pid, className);

				Object dedicatedService = helper.getService();

				return dedicatedService;
			}

		} else {

			throw new Exception(
					"Values for service's pid and class name cannot be empty.");

		}

	}

	@Override
	public synchronized Object getRemoteHydraServiceByDescription(
			String description, String className) throws Exception {

		if (StringUtils.isNotEmpty(description)) {

			if (remoteHydraServices.containsKey(description)
					&& StringUtils.isNotEmpty(className)) {

				return remoteHydraServices.get(description);

			} else {
				GetRemoteServiceByDescriptionHelper helper = new GetRemoteServiceByDescriptionHelper(
						description, className);

				Object dedicatedService = helper.getService();

				return dedicatedService;
			}

		} else {

			throw new Exception(
					"Values for service's description and class name cannot be empty.");

		}

	}

	private class RegisterAtNetworkManagerThread implements Runnable {

		@Override
		public void run() {
			while (myHID == null) {
				try {
					myHID = networkManager.createHIDwDesc(
							HYDRA_REMOTE_SERVICE_STORE_PATH, AXIS_SERVICES_PATH
									+ HYDRA_REMOTE_SERVICE_STORE_PATH);
				} catch (RemoteException e) {
					LOG
							.warn("Unable to regster at Network Manager. Trying again.");
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					LOG.error("Error while waiting.", e);
				}
			}

		}

	}

	

	protected void activate(ComponentContext context) {

		bundleContext = context.getBundleContext();

		remoteHydraServices = new HashMap<String, Object>();

		// get RemoteClientProvider
		remoteWSClientProvider = (RemoteWSClientProvider) bundleContext
				.getService(bundleContext
						.getServiceReference(RemoteWSClientProvider.class
								.getName()));
		try {
			networkManager = (NetworkManagerApplication) remoteWSClientProvider
					.getRemoteWSClient(
							NetworkManagerApplication.class.getName(),
							"http://localhost:8082/axis/services/NetworkManagerApplication",
							false);
		} catch (Exception e) {
			LOG.error("Unable to get NetworkManager through RemoteWSClientProvider", e);
		}
		
		Thread thread1 = new Thread(new RegisterAtNetworkManagerThread());
		thread1.start();
		

		LOG.debug("Started " + bundleContext.getBundle().getSymbolicName());

	}

	protected void deactivate(ComponentContext context) {

		try {
			networkManager.removeHID(myHID);
		} catch (RemoteException e) {
			LOG.error(e.getMessage(), e.getCause());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e.getCause());
		}

		remoteHydraServices = null;
		networkManager = null;
		remoteWSClientProvider = null;

		bundleContext = null;

		LOG.debug("Stopped " + bundleContext.getBundle().getSymbolicName());
	}

	private String checkAttributeValuesAndformulateQuery(
			Map<String, String> attributes) {

		char openBracket = '(';
		char closeBracket = ')';
		String logicalEquals = "==";
		String logicalAnd = "&&";

		Set<String> keySet = attributes.keySet();

		Iterator<String> keySetIterator = keySet.iterator();

		StringBuffer buf = new StringBuffer();

		while (keySetIterator.hasNext()) {
			String attributeKey = keySetIterator.next();

			if (attributeKey == null || StringUtils.isEmpty(attributeKey))
				throw new IllegalArgumentException(
						"Attribute keys cannot be NULL or empty.");

			String attributeVal = attributes.get(attributeKey);

			if (attributeVal == null || StringUtils.isEmpty(attributeVal))
				throw new IllegalArgumentException(
						"Attribute values cannot be NULL or empty.");

			buf.append(openBracket);
			buf.append(attributeKey);
			buf.append(logicalEquals);
			buf.append(attributeVal);
			buf.append(closeBracket);

			buf.append(logicalAnd);
		}

		// after loop finished remove last logicalAnd contribution
		buf.delete(buf.length() - 2, buf.length());

		String finalQuery = buf.toString();

		return finalQuery;

	}

	@Override
	public String getHydraServiceHIDByAttributes(Map<String, String> attributes) {

		String query = checkAttributeValuesAndformulateQuery(attributes);

		String requestedHID = this.getServiceCryptoHID(query);

		return requestedHID;
	}

	@Override
	public String getServiceCryptoHID(String query) {
		if (query == null || StringUtils.isEmpty(query)) {
			throw new IllegalArgumentException("Query cannot be NULL or empty");
		}

		String[] results = null;
		long maxTime = 50000;
		int maxResponses = 1;

		try {
			LOG.debug("Trying to get HID with attributes. RequesterHID: "
					+ myHID + ", query: " + query);
			Thread.sleep(5000);
			results = networkManager.getHIDByAttributes(myHID,
					HYDRA_REMOTE_SERVICE_STORE_PATH, query, maxTime,
					maxResponses);
			LOG.debug("Number of available HIDs: " + results.length);
		} catch (IOException e) {
			LOG.error("Cannot find HID for query: " + query, e);
		} catch (InterruptedException e) {
			LOG.error("Error while waiting for NetworkManager to find HID.", e);
		}
		if (results == null || (results != null && results.length == 0)) {
			return null;
		} else {
			return results[0];
		}
	}

}
