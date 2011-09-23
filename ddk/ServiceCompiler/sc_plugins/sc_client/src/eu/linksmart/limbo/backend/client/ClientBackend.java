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
package eu.linksmart.limbo.backend.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.Definition;
import javax.wsdl.PortType;

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

@Component(properties={
		"limbo.platform=.*",
		"limbo.language=jse;jme",
		"limbo.generates=controller",
		"limbo.generationtype=client;all"})
public class ClientBackend implements Backend {
	
	private Repository repository;
	private String webServiceName;
	private String communicationProtocol;
	private String language;
	private String outputDirectory;
	private VelocityEngine engine = new VelocityEngine();
	private Logger log = Logger.getLogger(ClientBackend.class.getName());
	private ComponentContext context;
	private Definition definition;
	
	protected void activate(ComponentContext ctxt)  {
		this.context = ctxt;
	}

	@Reference
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	
	public void unsetRepository(Repository repository) {
		this.repository = null;
	}

	public void generate() throws Exception {

		engine.setProperty("resource.loader", "mine"); 
		engine.setProperty("mine.resource.loader.instance", new OSGiResourceLoader(this.context.getBundleContext().getBundle()));
		String wsdlFileName = this.repository.getWSDLFile().getName();
		StringTokenizer st = new StringTokenizer(wsdlFileName,".");
		this.webServiceName = st.nextToken();
		this.outputDirectory = (String)this.repository.getParameter(LimboConstants.OUTPUTDIRECTORY);
		this.communicationProtocol = (String)this.repository.getParameter(LimboConstants.PROTOCOL);
		this.language = (String)this.repository.getParameter(LimboConstants.LANGUAGE);
		this.definition = this.repository.getWSDL();
		generateClientHeaderParserResource();
		Collection<PortType> portTypes = this.definition.getPortTypes().values();
		for(PortType pt : portTypes)
			generateLimboClientResource(pt);
		generateStringTokenizerResource();
	}
	
	private void generateClientHeaderParserResource() {
		try {
			Template t = null;
			VelocityContext context = new VelocityContext();
			t = engine.getTemplate("resources/Parsers/HeaderParser.vm" );
			context.put("package", "eu.linksmart.limbo.client");
			context.put("class", "LimboClientHeaderParser");
			context.put("ifCondition", "this.header.startsWith("+'"'+"HTTP"+'"'+")");
			String directory = outputDirectory + "/"+webServiceName+"Client/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class") + ".java"));
			t.merge( context, writer );
			writer.close();
		}catch(Exception e) {
			log.log(Level.SEVERE, "Error generating ClientHeaderParserResource");
			e.printStackTrace();
		}
		
	}
	
	private void generateLimboClientResource(PortType portType) {
		if(this.language.equals(LimboConstants.JSE.toString())) {
			generateJSEClientResource(portType);
		}
		if(this.language.equals(LimboConstants.JME.toString())) {
			generateJMEClientResource(portType);
		}
			
	}
	
	private void generateJSEClientResource(PortType portType) {

		Template t;
		VelocityContext context;
		BufferedWriter writer;
		String directory;
		try {
       	    context = new VelocityContext();
			t = null;
			if(!this.communicationProtocol.equals(LimboConstants.BLUETOOTH.toString())) {
				t = engine.getTemplate( "resources/Client/LimboClient.vm" );
				context.put("class", portType.getQName().getLocalPart()+"LimboClient");
				context.put("LimboPortImpl", portType.getQName().getLocalPart()+"LimboClientPortImpl");
				context.put("service", portType.getQName().getLocalPart());
			}
			else {
				t = engine.getTemplate( "resources/Client/LimboBTClientJSE.vm");
				context.put("imports", "import java.awt.BorderLayout;\nimport java.awt.Component;\nimport java.awt.Dimension;\n" +
							"import java.awt.event.ItemEvent;\nimport java.awt.event.ItemListener;\nimport java.io.IOException;\n" +
							"import java.util.Vector;\nimport javax.bluetooth.BluetoothStateException;\nimport javax.bluetooth.DataElement;\n" +
							"import javax.bluetooth.DeviceClass;\nimport javax.bluetooth.DiscoveryAgent;\nimport javax.bluetooth.DiscoveryListener;\n" +
							"import javax.bluetooth.LocalDevice;\nimport javax.bluetooth.RemoteDevice;\nimport javax.bluetooth.ServiceRecord;\n" +
							"import javax.bluetooth.UUID;\nimport javax.swing.BorderFactory;\nimport javax.swing.DefaultListCellRenderer;\n" +
							"import javax.swing.JComboBox;\nimport javax.swing.JFrame;\nimport javax.swing.JList;\nimport javax.swing.JPanel;\n" +
							"import javax.swing.JScrollPane;\nimport javax.swing.JTextArea;\nimport javax.swing.SwingUtilities;");
				context.put("portTypes", this.definition.getPortTypes().values());
				context.put("class", "LimboClient");
			}
			String _protocol=this.communicationProtocol;
			if (_protocol.equalsIgnoreCase("all")) {
				
				context.put("protocol", "TCPProtocol");
			}
			else 
				{context.put("protocol", this.communicationProtocol+"Protocol");
				}
			context.put("package", "eu.linksmart.limbo.client");
			
		    directory = outputDirectory + "/"+this.webServiceName+"Client/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class") + ".java"));
		    t.merge( context, writer );
			writer.close();
		}catch(Exception e) {
			log.log(Level.SEVERE, "Error generating JSEClientResource");
			e.printStackTrace();
		}
	}
	
	private void generateJMEClientResource(PortType portType) {
	
		Template t;
		VelocityContext context;
		BufferedWriter writer;
		String directory;
		try {
			context = new VelocityContext();
			t = null;
			if(!this.communicationProtocol.equals(LimboConstants.BLUETOOTH.toString())){
				t = engine.getTemplate( "resources/Client/LimboClientJME.vm" );
				context.put("class", portType.getQName().getLocalPart()+"LimboClient");
				context.put("LimboPortImpl", portType.getQName().getLocalPart()+"LimboClientPortImpl");
				context.put("service", portType.getQName().getLocalPart());
			}
			else {
				t = engine.getTemplate( "resources/Client/LimboBTClientJME.vm");
				context.put("transport_imports", "import java.io.IOException;\nimport java.util.Vector;\nimport javax.bluetooth.BluetoothStateException;\n" +
						"import javax.bluetooth.DataElement;\nimport javax.bluetooth.DeviceClass;\nimport javax.bluetooth.DiscoveryAgent;\n" +
						"import javax.bluetooth.DiscoveryListener;\nimport javax.bluetooth.LocalDevice;\nimport javax.bluetooth.RemoteDevice;\n" +
						"import javax.bluetooth.ServiceRecord;\nimport javax.bluetooth.UUID;\nimport javax.microedition.lcdui.Command;\n" +
						"import javax.microedition.lcdui.CommandListener;\nimport javax.microedition.lcdui.Display;\nimport javax.microedition.lcdui.Displayable;\n" +
						"import javax.microedition.lcdui.Form;\nimport javax.microedition.lcdui.List;\nimport javax.microedition.lcdui.TextBox;\n" +
						"import javax.microedition.midlet.MIDlet;\nimport javax.microedition.midlet.MIDletStateChangeException;");
				context.put("portTypes", this.definition.getPortTypes().values());
				context.put("class", "LimboClient");
			}
			context.put("protocol", this.communicationProtocol+"Protocol");
			context.put("package", "eu.linksmart.limbo.client");
			
		    directory = outputDirectory + "/"+this.webServiceName+"Client/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class") + ".java"));
		    t.merge( context, writer );
			writer.close();
		}catch(Exception e) {
			log.log(Level.SEVERE, "Error generating JMEClientResource");
			e.printStackTrace();
		}
	}
	
	private void generateStringTokenizerResource() {
		try {
			Template t;
			t = engine.getTemplate( "resources/StringTokenizer.vm" );
			String directory ="";
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo.client");
			directory = outputDirectory + "/"+webServiceName+"Client/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/StringTokenizer.java"));
			t.merge( context, writer );
			writer.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating StringTokenizerResource");
			e.printStackTrace();
		}
	}
}
