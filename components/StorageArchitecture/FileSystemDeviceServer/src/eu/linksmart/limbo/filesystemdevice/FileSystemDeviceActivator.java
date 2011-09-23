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

package eu.linksmart.limbo.filesystemdevice;

import org.osgi.service.http.HttpService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import java.net.InetAddress;
import java.net.UnknownHostException;

import eu.linksmart.limbo.filesystemdevice.upnp.*;

import org.osgi.framework.ServiceRegistration;
public class FileSystemDeviceActivator implements BundleActivator {

	private BundleContext context;
	public FileSystemDeviceServlet fileSystemDeviceServlet;
	public LinkSmartServicePortServlet LinkSmartServicePortservlet;
	private ServiceTracker tracker;
	private HttpService http; 
	private FileSystemDeviceDevice device;
	private ServiceRegistration FileSystemDevicePortService;
	private ServiceRegistration LinkSmartServicePortService;
	private String servicePID = "";
	public FileSystemDeviceActivator(FileSystemDeviceDevice device) {
		this.device = device;
		this.servicePID = (String) device.properties.get("service.pid");
		
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
		this.fileSystemDeviceServlet = new FileSystemDeviceServlet(host, new Integer(System.getProperty("org.osgi.service.http.port").toString()), "/FileSystemDevice-"+servicePID, device);
		this.http.registerServlet("/FileSystemDevice-"+servicePID, this.fileSystemDeviceServlet, null, null);
		FileSystemDevicePortService = this.context.registerService(FileSystemDeviceServlet.class.getName(), this.fileSystemDeviceServlet, null);      
		this.LinkSmartServicePortservlet = new LinkSmartServicePortServlet(host, new Integer(System.getProperty("org.osgi.service.http.port").toString()), "/LinkSmart-FileSystemDevice-"+servicePID, device);
		this.http.registerServlet("/LinkSmart-FileSystemDevice-"+servicePID, this.LinkSmartServicePortservlet, null, null);
		LinkSmartServicePortService = this.context.registerService(LinkSmartServicePortServlet.class.getName(), this.LinkSmartServicePortservlet, null);      
	}
	
	
	public void stop(BundleContext context) throws Exception {
		FileSystemDevicePortService.unregister();
		//System.out.println("/LinkSmart-FileSystemDevice-"+ servicePID);
		this.http.unregister("/FileSystemDevice-"+servicePID);
		LinkSmartServicePortService.unregister();
		this.http.unregister("/LinkSmart-FileSystemDevice-"+servicePID);
		tracker.close();
		tracker = null;
	}
}
