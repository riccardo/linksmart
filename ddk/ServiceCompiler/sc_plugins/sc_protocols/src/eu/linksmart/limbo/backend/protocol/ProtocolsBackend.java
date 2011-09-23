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
package eu.linksmart.limbo.backend.protocol;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

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
		"limbo.generationtype=.*"})
public class ProtocolsBackend implements Backend {

	private Repository repository;
	private String webServiceName;
	private String generationType;
	private String communicationProtocol;
	private String outputDirectory; 
	private VelocityEngine engine = new VelocityEngine();
	private String language;
	private static final Logger log = Logger.getLogger(ProtocolsBackend.class.getName());
	private ComponentContext context;
	
	
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
		this.generationType = (String)this.repository.getParameter(LimboConstants.GENERATIONTYPE);
		this.communicationProtocol = (String)this.repository.getParameter(LimboConstants.PROTOCOL);
		this.outputDirectory = (String)this.repository.getParameter(LimboConstants.OUTPUTDIRECTORY);
		this.language = (String)this.repository.getParameter(LimboConstants.LANGUAGE);
		generateProtocolResources();
	}
	
	private void generateProtocolResources() {
		if(this.language.equals(LimboConstants.JSE.toString())) {
			if((this.generationType.equals(LimboConstants.SERVER.toString())) || (this.generationType.equals(LimboConstants.ALL.toString()))) {
				generateJSEServerProtocolResources();
			}
			if((this.generationType.equals(LimboConstants.CLIENT.toString())) || (this.generationType.equals(LimboConstants.ALL.toString()))) {
				generateJSEClientProtocolResources();
			}
		}
		else if(this.language.equals(LimboConstants.JME.toString())) {
			if((this.generationType.equals(LimboConstants.SERVER.toString())) || (this.generationType.equals(LimboConstants.ALL.toString()))) {
				generateJMEServerProtocolResources();
			}
			if((this.generationType.equals(LimboConstants.CLIENT.toString())) || (this.generationType.equals(LimboConstants.ALL.toString()))) {
				generateJMEClientProtocolResources();
			}
		}
		
	}
	
	private void generateJSEServerProtocolResources() {
		generateServerProtocolInterface();
		generateJSEServerProtocolClasses();
	}
	
	private void generateJSEClientProtocolResources() {
		generateClientProtocolInterface();
		generateJSEClientProtocolClasses();
	}
	
	private void generateJSEServerProtocolClasses() {
		try {
		
			Template t = null;
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo");
			context.put("interface", "ServerProtocol");
			String directory = outputDirectory + "/"+webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			BufferedWriter writer =null;
			// Always generate UDP and TCP
			if(this.communicationProtocol.equals(LimboConstants.TCP.toString()) || this.communicationProtocol.equals(LimboConstants.UDP.toString())) {
				t = engine.getTemplate( "resources/Protocols/ServerTCPProtocol.vm" );
				context.put("imports", "import java.io.IOException;\nimport java.io.InputStream;\nimport java.io.OutputStream;\n" +
						"import java.net.ServerSocket;\nimport java.net.Socket;\nimport java.util.logging.Logger;\n" +
						"import java.net.InetAddress;\nimport java.net.UnknownHostException;");
				context.put("class", "TCPProtocol");
				new File(directory).mkdirs();
				writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
				t.merge(context, writer);
				writer.close();

				t = engine.getTemplate( "resources/Protocols/ServerUDPProtocol.vm" );
				context.put("class", "UDPProtocol");
				context.put("imports", "import java.io.IOException;\nimport java.net.DatagramPacket;\n" +
						"import java.net.DatagramSocket;\n" +
						"import java.net.InetAddress;\nimport java.net.SocketException;\n" +
						"import java.net.InetAddress;\nimport java.net.UnknownHostException;");
				new File(directory).mkdirs();
				writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
				t.merge(context, writer);
				writer.close();
			}
			else if(this.communicationProtocol.equals(LimboConstants.BLUETOOTH.toString())) {
				t = engine.getTemplate( "resources/Protocols/ServerBTProtocol.vm" );
				context.put("class", "BTProtocol");
				context.put("imports", "import java.io.IOException;\nimport java.io.InputStream;\n" +
						"import java.io.OutputStream;\n\nimport javax.bluetooth.BluetoothStateException;\n" +
						"import javax.bluetooth.DiscoveryAgent;\nimport javax.bluetooth.LocalDevice;\n" +
						"import javax.bluetooth.UUID;\nimport javax.microedition.io.Connector;\n" +
						"import javax.microedition.io.StreamConnection;\nimport javax.microedition.io.StreamConnectionNotifier;");
				context.put("serviceName", this.webServiceName);
				
				new File(directory).mkdirs();
				writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
				t.merge(context, writer);
				writer.close();
			}
			
			if(this.communicationProtocol.equalsIgnoreCase(LimboConstants.ALL.toString())) {
				t = engine.getTemplate( "resources/Protocols/ServerTCPProtocol.vm" );
				context.put("imports", "import java.io.IOException;\nimport java.io.InputStream;\nimport java.io.OutputStream;\n" +
						"import java.net.ServerSocket;\nimport java.net.Socket;\nimport java.util.logging.Logger;\n" +
						"import java.net.InetAddress;\nimport java.net.UnknownHostException;");
				context.put("class", "TCPProtocol");

				new File(directory).mkdirs();
				writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
				t.merge(context, writer);
				writer.close();
				t = engine.getTemplate( "resources/Protocols/ServerUDPProtocol.vm" );
				context.put("class", "UDPProtocol");
				context.put("imports", "import java.io.IOException;\nimport java.net.DatagramPacket;\n" +
						"import java.net.DatagramSocket;\n" +
						"import java.net.InetAddress;\nimport java.net.SocketException;\n" +
						"import java.net.InetAddress;\nimport java.net.UnknownHostException;");
				new File(directory).mkdirs();
				writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
				t.merge(context, writer);
				writer.close();
				
				t = engine.getTemplate( "resources/Protocols/ServerBTProtocol.vm" );
				context.put("class", "BTProtocol");
				context.put("imports", "import java.io.IOException;\nimport java.io.InputStream;\n" +
						"import java.io.OutputStream;\n\nimport javax.bluetooth.BluetoothStateException;\n" +
						"import javax.bluetooth.DiscoveryAgent;\nimport javax.bluetooth.LocalDevice;\n" +
						"import javax.bluetooth.UUID;\nimport javax.microedition.io.Connector;\n" +
						"import javax.microedition.io.StreamConnection;\nimport javax.microedition.io.StreamConnectionNotifier;");
				context.put("serviceName", this.webServiceName);
				new File(directory).mkdirs();
				writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
				t.merge(context, writer);
				writer.close();
			}
			
			
			
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating JSEServerProtocol resources");
			e.printStackTrace();
		}	
		
	}
	
	private void generateJMEServerProtocolResources() {
		generateServerProtocolInterface();
		generateJMEServerProtocolClasses();
	}
	
	
	//JME all protocol did not tested.
	private void generateJMEServerProtocolClasses() {
		try {
			
			Template t = null;
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo");
			context.put("interface", "ServerProtocol");
			String directory = outputDirectory + "/"+webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			BufferedWriter writer =null;
			if(this.communicationProtocol.equals(LimboConstants.TCP.toString())) {
				t = engine.getTemplate( "resources/Protocols/ServerJMETCPProtocol.vm" );
				context.put("imports", "import java.io.IOException;\nimport java.io.InputStream;\n" +
						"import java.io.OutputStream;\nimport javax.microedition.io.Connector;\n" +
						"import javax.microedition.io.ServerSocketConnection;\nimport javax.microedition.io.SocketConnection;");
				context.put("class", "TCPProtocol");
				

				new File(directory).mkdirs();
				writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
				t.merge(context, writer);
				writer.close();
				
			}
			else if(this.communicationProtocol.equals(LimboConstants.UDP.toString())) {
				t = engine.getTemplate( "resources/Protocols/ServerJMEUDPProtocol.vm" );
				context.put("class", "UDPProtocol");
				context.put("imports", "import java.io.IOException;\nimport javax.microedition.io.Connector;\nimport javax.microedition.io.Datagram;\n" +
						"import javax.microedition.io.DatagramConnection;");
				new File(directory).mkdirs();
				writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
				t.merge(context, writer);
				writer.close();
			}
			else if(this.communicationProtocol.equals(LimboConstants.BLUETOOTH.toString())) {
				t = engine.getTemplate( "resources/Protocols/ServerBTProtocol.vm" );
				context.put("class", "BTProtocol");
				context.put("imports", "import java.io.IOException;\nimport java.io.InputStream;\n" +
						"import java.io.OutputStream;\n\nimport javax.bluetooth.BluetoothStateException;\n" +
						"import javax.bluetooth.DiscoveryAgent;\nimport javax.bluetooth.LocalDevice;\n" +
						"import javax.bluetooth.UUID;\nimport javax.microedition.io.Connector;\n" +
						"import javax.microedition.io.StreamConnection;\n" +
						"import javax.microedition.io.StreamConnectionNotifier;");
				//context.put("serviceName", this.webServiceName);
				new File(directory).mkdirs();
				writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
				t.merge(context, writer);
				writer.close();
			}
			else if(this.communicationProtocol.equals(LimboConstants.ALL.toString())) {
				t = engine.getTemplate( "resources/Protocols/ServerJMETCPProtocol.vm" );
				context.put("imports", "import java.io.IOException;\nimport java.io.InputStream;\n" +
						"import java.io.OutputStream;\nimport javax.microedition.io.Connector;\n" +
						"import javax.microedition.io.ServerSocketConnection;\nimport javax.microedition.io.SocketConnection;");
				context.put("class", "TCPProtocol");
				

				new File(directory).mkdirs();
				writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
				t.merge(context, writer);
				writer.close();
				
				t = engine.getTemplate( "resources/Protocols/ServerJMEUDPProtocol.vm" );
				context.put("class", "UDPProtocol");
				context.put("imports", "import java.io.IOException;\nimport javax.microedition.io.Connector;\nimport javax.microedition.io.Datagram;\n" +
						"import javax.microedition.io.DatagramConnection;");
				new File(directory).mkdirs();
				writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
				t.merge(context, writer);
				writer.close();
	
				t = engine.getTemplate( "resources/Protocols/ServerBTProtocol.vm" );
				context.put("class", "BTProtocol");
				context.put("imports", "import java.io.IOException;\nimport java.io.InputStream;\n" +
						"import java.io.OutputStream;\n\nimport javax.bluetooth.BluetoothStateException;\n" +
						"import javax.bluetooth.DiscoveryAgent;\nimport javax.bluetooth.LocalDevice;\n" +
						"import javax.bluetooth.UUID;\nimport javax.microedition.io.Connector;\n" +
						"import javax.microedition.io.StreamConnection;\n" +
						"import javax.microedition.io.StreamConnectionNotifier;");
				//context.put("serviceName", this.webServiceName);
				new File(directory).mkdirs();
				writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
				t.merge(context, writer);
				writer.close();
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating JMEServerProtocol resources");
			e.printStackTrace();
		}
	}
	
	private void generateJMEClientProtocolResources() {
		generateClientProtocolInterface();
		generateJMEClientProtocolClasses();
	}
	
	private void generateJMEClientProtocolClasses() {
		try {
			Template t = null;
			VelocityContext context = new VelocityContext();
			if(this.communicationProtocol.equals(LimboConstants.TCP.toString())) {
				t = engine.getTemplate( "resources/Protocols/ClientJMETCPProtocol.vm" );
				context.put("imports", "import java.io.InputStream;\nimport java.io.OutputStream;\n" +
						"import javax.microedition.io.Connector;\nimport javax.microedition.io.SocketConnection;");
				context.put("class", "TCPProtocol");
			}
			else if(this.communicationProtocol.equals(LimboConstants.UDP.toString())) {
				t = engine.getTemplate( "resources/Protocols/ClientJMEUDPProtocol.vm" );
				context.put("class", "UDPProtocol");
				context.put("imports", "import javax.microedition.io.Connector;\n" +
						"import javax.microedition.io.Datagram;\nimport javax.microedition.io.DatagramConnection;");
			}
			else if(this.communicationProtocol.equals(LimboConstants.BLUETOOTH.toString())) {
				t = engine.getTemplate( "resources/Protocols/ClientBTProtocol.vm" );
				context.put("class", "BTProtocol");
				context.put("imports", "import java.io.IOException;\nimport java.io.InputStream;\n" +
						"import java.io.OutputStream;\n\nimport javax.microedition.io.Connector;\nimport javax.microedition.io.StreamConnection;");
			}
			
			context.put("package", "eu.linksmart.limbo.client");
			context.put("interface", "ClientProtocol");
			String directory = outputDirectory + "/"+webServiceName+"Client/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
			t.merge(context, writer);
			writer.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating JMEClientProtocol resources");
			e.printStackTrace();
		}
	}
	
	private void generateJSEClientProtocolClasses() {
		try {
	//		System.out.println("communicationProtocol"+communicationProtocol);
		
			Template t = null;
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo.client");
			context.put("interface", "ClientProtocol");
			String directory = outputDirectory + "/"+webServiceName+"Client/src/" + context.get("package").toString().replace('.', '/');
			BufferedWriter writer =null;
			// Always generate UDP and TCP
			if(this.communicationProtocol.equals(LimboConstants.TCP.toString()) || this.communicationProtocol.equals(LimboConstants.UDP.toString())) {
				t = engine.getTemplate( "resources/Protocols/ClientTCPProtocol.vm" );
				context.put("imports", "import java.io.IOException;\nimport java.io.InputStream;\n" +
						"import java.io.OutputStream;\nimport java.net.Socket;\nimport java.net.UnknownHostException;");
				context.put("class", "TCPProtocol");
				new File(directory).mkdirs();
				writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
				t.merge(context, writer);
				writer.close();

				t = engine.getTemplate( "resources/Protocols/ClientUDPProtocol.vm" );
				context.put("class", "UDPProtocol");
				context.put("imports", "import java.net.DatagramPacket;\nimport java.net.DatagramSocket;\n" +
						"import java.net.InetAddress;\nimport java.net.UnknownHostException;");
				new File(directory).mkdirs();
				writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
				t.merge(context, writer);
				writer.close();
			}
			else if(this.communicationProtocol.equals(LimboConstants.BLUETOOTH.toString())) {
				t = engine.getTemplate( "resources/Protocols/ClientBTProtocol.vm" );
				context.put("class", "BTProtocol");
				context.put("imports", "import java.io.IOException;\nimport java.io.InputStream;\nimport java.io.OutputStream;\n\n" +
						"import javax.microedition.io.Connector;import javax.microedition.io.StreamConnection;");
				new File(directory).mkdirs();
				writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
				t.merge(context, writer);
				writer.close();
			}
			
			else if(this.communicationProtocol.equalsIgnoreCase(LimboConstants.ALL.toString())) {
				t = engine.getTemplate( "resources/Protocols/ClientTCPProtocol.vm" );
				context.put("imports", "import java.io.IOException;\nimport java.io.InputStream;\n" +
						"import java.io.OutputStream;\nimport java.net.Socket;\nimport java.net.UnknownHostException;");
				context.put("class", "TCPProtocol");
				new File(directory).mkdirs();
				writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
				t.merge(context, writer);
				writer.close();
			 
				t = engine.getTemplate( "resources/Protocols/ClientUDPProtocol.vm" );
				context.put("class", "UDPProtocol");
				context.put("imports", "import java.net.DatagramPacket;\nimport java.net.DatagramSocket;\n" +
						"import java.net.InetAddress;\nimport java.net.UnknownHostException;");
				new File(directory).mkdirs();
				writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
				t.merge(context, writer);
				writer.close();
			
				t = engine.getTemplate( "resources/Protocols/ClientBTProtocol.vm" );
				context.put("class", "BTProtocol");
				context.put("imports", "import java.io.IOException;\nimport java.io.InputStream;\nimport java.io.OutputStream;\n\n" +
						"import javax.microedition.io.Connector;import javax.microedition.io.StreamConnection;");
				new File(directory).mkdirs();
				writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class")+".java"));
				t.merge(context, writer);
				writer.close();
			}


		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating JSEClientProtocol resources");
			e.printStackTrace();
		}
	}
	
	private void generateServerProtocolInterface() {
		try {
			
			Template t;
			t = engine.getTemplate( "resources/Protocols/ServerProtocolInterface.vm" );
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo");
			context.put("interface", "ServerProtocol");
			String directory = outputDirectory + "/"+webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + "ServerProtocol.java"));
			t.merge(context, writer);
			writer.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating ServerProtocol interface resource");
			e.printStackTrace();
		}
	}
	
	private void generateClientProtocolInterface() {
		try {
			Template t;
			t = engine.getTemplate( "resources/Protocols/ClientProtocolInterface.vm" );
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo.client");
			context.put("interface", "ClientProtocol");
			String directory = outputDirectory + "/"+webServiceName+"Client/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + "ClientProtocol.java"));
			t.merge(context, writer);
			writer.close();

			// Protocols protocol change classes
			t = engine.getTemplate( "resources/Protocols/Protocols.vm" );
			writer = new BufferedWriter(new FileWriter(directory + "/" + "Protocols.java"));
			t.merge(context, writer);
			writer.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating ClientProtocol interface resource");
			e.printStackTrace();
		}
	}
	
}
