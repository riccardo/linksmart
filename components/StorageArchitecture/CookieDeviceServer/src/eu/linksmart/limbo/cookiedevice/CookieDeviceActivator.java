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

package eu.linksmart.limbo.cookiedevice;

import org.apache.log4j.Logger;
import org.osgi.service.http.HttpService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import java.net.InetAddress;
import java.net.UnknownHostException;

import eu.linksmart.limbo.cookiedevice.upnp.*;

import org.osgi.framework.ServiceRegistration;

public class CookieDeviceActivator implements BundleActivator {

	private static Logger logger = Logger.getLogger(CookieDeviceActivator.class.getName());
	
	private BundleContext context;
	private CookieServlet Cookieservlet;
	private LinkSmartServicePortServlet LinkSmartServicePortservlet;
	private ServiceTracker tracker;
	private HttpService http; 
	private ServiceRegistration CookieService;
	private ServiceRegistration LinkSmartServicePortService;
	private String servicePID = "";

	private static CookieDeviceDevice device;
	public CookieDeviceActivator(CookieDeviceDevice device) {
		CookieDeviceActivator.device = device;
		this.servicePID = (String) device.getProperties().get("service.pid");
	}
	
	public static CookieDeviceDevice getDevice() {
		return CookieDeviceActivator.device;
	}

	public void start(BundleContext context) throws Exception {
		tracker = new ServiceTracker(context, HttpService.class.getName(), null);
		tracker.open();
		this.http = (HttpService) tracker.getService();
		this.context = context;
		createInstance();
	}
	
	public void createInstance() throws Exception {
	 	String host = "";
		try {
			host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.Cookieservlet = new CookieServlet(host, new Integer(System.getProperty("org.osgi.service.http.port").toString()), "/services/CookieDevice-"+servicePID);
		this.http.registerServlet("/services/CookieDevice-"+servicePID, this.Cookieservlet, null, null);
		CookieService = this.context.registerService(CookieServlet.class.getName(), this.Cookieservlet, null);      
		this.LinkSmartServicePortservlet = new LinkSmartServicePortServlet(host, new Integer(System.getProperty("org.osgi.service.http.port").toString()), "/LinkSmart-CookieDevie-"+servicePID);
		this.http.registerServlet("/LinkSmart-CookieDevice-"+servicePID, this.LinkSmartServicePortservlet, null, null);
		LinkSmartServicePortService = this.context.registerService(LinkSmartServicePortServlet.class.getName(), this.LinkSmartServicePortservlet, null);
		logger.info("New LinkSmart Cookie Device available at /services/CookieDevice-"+servicePID);
		logger.info("New LinkSmart Cookie Device (Management) available at /LinkSmart-CookieDevice-"+servicePID);
	}
	
	
	public void stop(BundleContext context) throws Exception {
		CookieService.unregister();
		this.http.unregister("/services/CookieDevice-"+servicePID);
		LinkSmartServicePortService.unregister();
		this.http.unregister("/LinkSmart-CookieDevice-"+servicePID);
		tracker.close();
		tracker = null;
	}
}
