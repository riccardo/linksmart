package eu.linksmart.network.facade.impl;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.NetworkManagerApplication;
import eu.linksmart.network.facade.NetworkManagerFacade;

public class NetworkManagerFacaceImpl implements NetworkManagerFacade {

	protected static final String OSGI_SERVICE_HTTP_PORT = System
			.getProperty("org.osgi.service.http.port");
	protected static final String AXIS_SERVICES_PATH = "http://localhost:"
			+ OSGI_SERVICE_HTTP_PORT + "/axis/services/";

	private Logger LOG = Logger.getLogger(NetworkManagerFacaceImpl.class);

	private NetworkManagerApplication networkManager;

	protected void activate(ComponentContext context) {
		LOG.info("Starting "
				+ context.getBundleContext().getBundle().getSymbolicName());
		networkManager = (NetworkManagerApplication) context
				.locateService(NetworkManagerApplication.class.getSimpleName());
	}

	protected void deactivate(ComponentContext context) {
		LOG.info("Stopping "
				+ context.getBundleContext().getBundle().getSymbolicName());
	}

	@Override
	public String createHIDForServiceID(String serviceID) throws RemoteException {
		String hid = networkManager.createHIDwDesc(serviceID,
				AXIS_SERVICES_PATH + serviceID);
		return hid;

	}

}
