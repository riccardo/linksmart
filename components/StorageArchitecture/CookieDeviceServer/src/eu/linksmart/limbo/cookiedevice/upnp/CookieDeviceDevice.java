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
 * Copyright (C) 2006-2010 [University of Paderborn]
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

package eu.linksmart.limbo.cookiedevice.upnp;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;

import org.apache.felix.upnp.extra.util.UPnPEventNotifier;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPEventListener;
import org.osgi.service.upnp.UPnPIcon;
import org.osgi.service.upnp.UPnPService;

import eu.linksmart.limbo.cookiedevice.CookieDeviceActivator;
import eu.linksmart.limbo.cookiedevice.backend.CookieContainer;

public class CookieDeviceDevice implements UPnPDevice, UPnPEventListener,
		ServiceListener {

	final private String DEVICE_ID = "uuid:CookieDeviceDevice+"
			+ Integer.toHexString(new Random(System.currentTimeMillis())
					.nextInt());
	private final static String COOKIEDEVICE_DEVICE_TYPE = "urn:schemas-upnp-org:linksmartdevice:Laptop_1:1";
	private final static String COOKIE_TYPE = "urn:schemas-upnp-org:service:Cookie:1";
	private final static String LINKSMARTSERVICEPORT_TYPE = "urn:schemas-upnp-org:service:LinkSmartServicePort:1";
	private final String devicesFilter = "(&" + "(" + Constants.OBJECTCLASS
			+ "=" + UPnPDevice.class.getName() + "))";

	private BundleContext context;
	private CookieUPnPService Cookie;
	private UPnPEventNotifier CookieNotifier;
	private LinkSmartServicePortUPnPService LinkSmartServicePort;
	private UPnPEventNotifier LinkSmartServicePortNotifier;

	private UPnPService[] services;
	private Dictionary dictionary;

	private regularExpressionStateVariable regularExpressionState;
	private resultStateVariable resultState;
	private cookieValueStateVariable cookieValueState;
	private cookieNameStateVariable cookieNameState;
	private PropertyValueStateVariable PropertyValueState;
	private ErrorMessageStateVariable ErrorMessageState;
	private LinkSmartWSEndpointStateVariable LinkSmartWSEndpointState;
	private DiscoveryInfoStateVariable DiscoveryInfoState;
	private wsdlStateVariable wsdlState;
	private LinkSmartIDStateVariable LinkSmartIDState;
	private WSEndpointStateVariable WSEndpointState;
	private HasErrorStateVariable HasErrorState;
	private ValueStateVariable ValueState;
	private PropertyStateVariable PropertyState;
	private DACEndpointStateVariable DACEndpointState;
	private StatusStateVariable StatusState;
	private ServiceRegistration serviceRegistration;
	private CookieDeviceActivator deviceActivator;

	private Dictionary properties;
	private CookieContainer container;

	public CookieDeviceDevice(BundleContext context, Dictionary properties,
			CookieContainer container) {
		this.context = context;
		this.properties = properties;
		this.container = container;
		Cookie = new CookieUPnPService(this);
		LinkSmartServicePort = new LinkSmartServicePortUPnPService(this);
		services = new UPnPService[] { Cookie, LinkSmartServicePort };
		regularExpressionState = (regularExpressionStateVariable) Cookie
				.getStateVariable("regularExpression");
		resultState = (resultStateVariable) Cookie.getStateVariable("result");
		cookieValueState = (cookieValueStateVariable) Cookie
				.getStateVariable("cookieValue");
		cookieNameState = (cookieNameStateVariable) Cookie
				.getStateVariable("cookieName");
		PropertyValueState = (PropertyValueStateVariable) LinkSmartServicePort
				.getStateVariable("PropertyValue");
		ErrorMessageState = (ErrorMessageStateVariable) LinkSmartServicePort
				.getStateVariable("ErrorMessage");
		LinkSmartWSEndpointState = (LinkSmartWSEndpointStateVariable) LinkSmartServicePort
				.getStateVariable("LinkSmartWSEndpoint");
		DiscoveryInfoState = (DiscoveryInfoStateVariable) LinkSmartServicePort
				.getStateVariable("DiscoveryInfo");
		wsdlState = (wsdlStateVariable) LinkSmartServicePort
				.getStateVariable("wsdl");
		LinkSmartIDState = (LinkSmartIDStateVariable) LinkSmartServicePort
				.getStateVariable("LinkSmartID");
		WSEndpointState = (WSEndpointStateVariable) LinkSmartServicePort
				.getStateVariable("WSEndpoint");
		HasErrorState = (HasErrorStateVariable) LinkSmartServicePort
				.getStateVariable("HasError");
		ValueState = (ValueStateVariable) LinkSmartServicePort
				.getStateVariable("Value");
		PropertyState = (PropertyStateVariable) LinkSmartServicePort
				.getStateVariable("Property");
		DACEndpointState = (DACEndpointStateVariable) LinkSmartServicePort
				.getStateVariable("DACEndpoint");
		StatusState = (StatusStateVariable) LinkSmartServicePort
				.getStateVariable("Status");
		setupDeviceProperties();
		buildEventNotifyer();
		try {
			CookieDeviceUPnPActivator.context.addServiceListener(this,
					devicesFilter);
		} catch (InvalidSyntaxException e) {
			System.out.println(e);
		}
	}

	public CookieContainer getContainer() {
		return container;
	}

	private void buildEventNotifyer() {
		CookieNotifier = new UPnPEventNotifier(
				CookieDeviceUPnPActivator.context, this, Cookie);
		regularExpressionState.setNotifier(CookieNotifier);
		resultState.setNotifier(CookieNotifier);
		cookieValueState.setNotifier(CookieNotifier);
		cookieNameState.setNotifier(CookieNotifier);
		LinkSmartServicePortNotifier = new UPnPEventNotifier(
				CookieDeviceUPnPActivator.context, this, LinkSmartServicePort);
		PropertyValueState.setNotifier(LinkSmartServicePortNotifier);
		ErrorMessageState.setNotifier(LinkSmartServicePortNotifier);
		LinkSmartWSEndpointState.setNotifier(LinkSmartServicePortNotifier);
		DiscoveryInfoState.setNotifier(LinkSmartServicePortNotifier);
		wsdlState.setNotifier(LinkSmartServicePortNotifier);
		LinkSmartIDState.setNotifier(LinkSmartServicePortNotifier);
		WSEndpointState.setNotifier(LinkSmartServicePortNotifier);
		HasErrorState.setNotifier(LinkSmartServicePortNotifier);
		ValueState.setNotifier(LinkSmartServicePortNotifier);
		PropertyState.setNotifier(LinkSmartServicePortNotifier);
		DACEndpointState.setNotifier(LinkSmartServicePortNotifier);
		StatusState.setNotifier(LinkSmartServicePortNotifier);
	}

	private void setupDeviceProperties() {
		dictionary = new Properties();
		dictionary.put(UPnPDevice.UPNP_EXPORT, "");
		dictionary.put(org.osgi.service.device.Constants.DEVICE_CATEGORY,
				new String[] { UPnPDevice.DEVICE_CATEGORY });
		dictionary.put(
				UPnPDevice.FRIENDLY_NAME,
				"CookieDevice::" + container.getName() + "::"
						+ container.getId() + ":");
		dictionary.put(UPnPDevice.MANUFACTURER, "Dell");
		dictionary.put(UPnPDevice.MANUFACTURER_URL, "www.dell.com");
		dictionary.put(UPnPDevice.MODEL_DESCRIPTION, "Dell Laptop 830 Series");
		dictionary.put(UPnPDevice.MODEL_NAME, "830");
		dictionary.put(UPnPDevice.MODEL_NUMBER, "830-N");
		dictionary.put(UPnPDevice.TYPE, COOKIEDEVICE_DEVICE_TYPE);
		dictionary.put(UPnPDevice.UDN,
				"uuid:LinkSmartStorageDevice" + container.getId());
		dictionary.put(UPnPDevice.UPC, "1213456789");

		HashSet types = new HashSet(services.length);
		String[] ids = new String[services.length];
		for (int i = 0; i < services.length; i++) {
			ids[i] = services[i].getId();
			types.add(services[i].getType());
		}

		dictionary.put(UPnPService.TYPE, types.toArray(new String[] {}));
		dictionary.put(UPnPService.ID, ids);
	}

	public Dictionary getDescriptions(String locale) {
		return dictionary;
	}

	public UPnPIcon[] getIcons(String locale) {
		return null;
	}

	public UPnPService getService(String serviceId) {
		if (serviceId.equals(Cookie.getId()))
			return Cookie;
		if (serviceId.equals(LinkSmartServicePort.getId()))
			return LinkSmartServicePort;
		return null;
	}

	public UPnPService[] getServices() {
		return services;
	}

	public void notifyUPnPEvent(String deviceId, String serviceId,
			Dictionary events) {
		System.out.println("Received event" + events.toString());

	}

	public void serviceChanged(ServiceEvent event) {
		switch (event.getType()) {
		case ServiceEvent.REGISTERED: {
		}
			;
			break;

		case ServiceEvent.MODIFIED: {
		}
			;
			break;

		case ServiceEvent.UNREGISTERING: {
		}
			;
			break;
		}

	}

	public void changeProperty(String key, String value) {
		dictionary.put(key, value);
		serviceRegistration.setProperties(dictionary);
	}

	public void doServiceRegistration() {

		serviceRegistration = context.registerService(
				UPnPDevice.class.getName(), this, this.getDescriptions(null));

		try {

			this.deviceActivator = new CookieDeviceActivator(this);
			this.deviceActivator.start(context);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void doServiceUnregistration() {
		serviceRegistration.unregister();
		try {
			this.deviceActivator.stop(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Dictionary getProperties() {
		return properties;
	}
}
