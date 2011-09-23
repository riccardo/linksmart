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
 * Copyright (C) 2006-2010
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
package eu.linksmart.limbo.osgi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import eu.linksmart.limbo.constants.LimboConstants;
import eu.linksmart.limbo.backend.Backend;
import eu.linksmart.limbo.repository.Repository;
import eu.linksmart.limbo.library.OSGiResourceLoader;
import com.ibm.wsdl.PortImpl;
import com.ibm.wsdl.ServiceImpl;
import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;

@Component(properties={
		"limbo.platform=osgi", 
		"limbo.language=jse",
		"limbo.generates=controller",
		"limbo.generationtype=service;server;all"})
public class OSGiBackend implements Backend{

	private Repository repository;
	private String webServiceName; 
	private String outputDirectory;
	private String transportProtocol;
	private HashMap<String,String> portAddresses;
	private VelocityEngine engine = new VelocityEngine();
	private static final Logger log = Logger.getLogger(OSGiBackend.class.getName());
	private ComponentContext context;
	private Definition definition;
	private String withUPnP;
	private Set<String> ports;


	protected void activate(ComponentContext ctxt)  {
		this.context = ctxt;
	}

	@SuppressWarnings("unchecked")
	public void generate() throws Exception {

		engine.setProperty("resource.loader", "mine"); 
		engine.setProperty("mine.resource.loader.instance", new OSGiResourceLoader(this.context.getBundleContext().getBundle()));
		this.transportProtocol = (String)this.repository.getParameter(LimboConstants.PROTOCOL);
		String wsdlFileName = this.repository.getWSDLFile().getName();
		StringTokenizer st = new StringTokenizer(wsdlFileName,".");
		this.webServiceName = st.nextToken();
		this.outputDirectory = (String)this.repository.getParameter(LimboConstants.OUTPUTDIRECTORY);
		this.withUPnP = (String)this.repository.getParameter(LimboConstants.UPNP);
		this.definition = this.repository.getWSDL();
		this.portAddresses = getPortAddresses();
		this.ports = portAddresses.keySet();
		for(PortType portType : (Collection<PortType>)definition.getPortTypes().values())  {
			generateServletResource(portType);
			List<PortType> portTypesOrdered = new LinkedList<PortType>();
			for(PortType p : (Collection<PortType>)definition.getPortTypes().values()) {
				if(p.getQName().getLocalPart().equals("LinkSmartServicePort"))
					portTypesOrdered.add(p);
				else
					portTypesOrdered.add(0, p);
			}
			generateActivatorResource(portTypesOrdered);
		}
	}

	public void generateServletResource(PortType portType) {
		try {
			Template t = null;
			if(this.transportProtocol.equals(LimboConstants.TCP.toString()))
				t = engine.getTemplate( "resources/LimboOSGiServlet.vm" );
			else
				if(this.transportProtocol.equals(LimboConstants.UDP.toString()) || (this.transportProtocol.equals(LimboConstants.BLUETOOTH.toString()))) {
					t = engine.getTemplate( "resources/LimboOSGiUDPBTServlet.vm" );
					generateServerThreadResource(portType);
				}
			VelocityContext context = new VelocityContext();
			context.put("transport", this.transportProtocol);
			context.put("package", "eu.linksmart.limbo");
			context.put("serverThread", portType.getQName().getLocalPart()+"ServerThread");
			context.put("servlet", portType.getQName().getLocalPart()+"Servlet");
			context.put("endPoint", portType.getQName().getLocalPart()+"EndPoint");
			context.put("OperationsImpl", portType.getQName().getLocalPart()+"OpsImpl");
			context.put("class", portType.getQName().getLocalPart()+"Servlet");
			String path = this.portAddresses.get(portType.getQName().getLocalPart());
			context.put("URI", path);
			context.put("service", portType.getQName().getLocalPart()+"Service");
			String directory = outputDirectory + "/"+this.webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + portType.getQName().getLocalPart()+"Servlet.java"));
			t.merge(context, writer);
			writer.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating Servlet resource");
			e.printStackTrace();
		}		
	}

	public void generateServerThreadResource(PortType portType) {
		try {
			Template t;
			t = engine.getTemplate( "resources/LimboServerThreadJSE.vm" );
			VelocityContext context = new VelocityContext();

			context.put("package", "eu.linksmart.limbo");

			context.put("class", portType.getQName().getLocalPart()+"ServerThread");
			context.put("endPoint", portType.getQName().getLocalPart()+"EndPoint");
			String directory = outputDirectory + "/"+webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + portType.getQName().getLocalPart()+"ServerThread.java"));
			t.merge(context, writer);
			writer.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating ServerThread resource");
			e.printStackTrace();
		}
	}

	public void generateActivatorResource(List<PortType> portTypes) {
		try {
			Template t;
			t = engine.getTemplate( "resources/Activator.vm" );
			VelocityContext context = new VelocityContext();
			context.put("transport", this.transportProtocol);
			context.put("package", "eu.linksmart.limbo");
			context.put("protocol", this.transportProtocol+"Protocol");
			context.put("servlets", this.portAddresses); //+Servlet
			context.put("ports", this.ports);
			context.put("portTypes", portTypes);
			context.put("withUPnP", this.withUPnP);
			context.put("device", this.webServiceName+"Device");

			context.put("class", this.webServiceName+"Activator");
			String directory = outputDirectory + "/"+webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + this.webServiceName+"Activator.java"));
			t.merge(context, writer);
			writer.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating Activator resource");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap<String,String> getPortAddresses() {
		HashMap<String,String> paths = new HashMap<String,String>();
		for (QName service: (Set<QName>)this.definition.getServices().keySet()) {
			Collection<String> portNames = this.definition.getService(service).getPorts().keySet();
			for(String portQName : portNames) {
				Port port = (Port)this.definition.getService(service).getPorts().get(portQName);
				String portTypeName = port.getBinding().getPortType().getQName().getLocalPart();
				if(!paths.containsKey(portTypeName)) {
					String address = getPortURL(this.definition, service);
					URL url = null;
					try {
						url = new URL(address);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
					paths.put(portTypeName, url.getPath());
				}
			}
		}
		return paths;
	}

	@SuppressWarnings("unchecked")
	private String getPortURL(Definition definition, QName serviceQName){
		ServiceImpl service = (ServiceImpl)definition.getService(serviceQName);
		Collection<PortImpl> ports = service.getPorts().values();
		for(PortImpl port : ports) {
			List eeList = port.getExtensibilityElements();
			for(Object ee : eeList) {
				if(ee instanceof SOAPAddressImpl) {
					SOAPAddressImpl soapAdd = (SOAPAddressImpl)ee;
					return soapAdd.getLocationURI(); 
				}
			}
		}
		return null;
	}

	@Reference
	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void unsetRepository(Repository repository) {
		this.repository = null;
	}
}
