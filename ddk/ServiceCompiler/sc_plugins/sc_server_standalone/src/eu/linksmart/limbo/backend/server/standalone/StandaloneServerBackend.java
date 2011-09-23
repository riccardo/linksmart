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
package eu.linksmart.limbo.backend.server.standalone;

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

@Component(properties = {
		"limbo.platform=standalone",
		"limbo.language=jse;jme",
		"limbo.generates=controller",
		"limbo.generationtype=service;server;all"})
public class StandaloneServerBackend  implements Backend {
	private Repository repository;
	private String outputDirectory; 
	private String webServiceName;
	private String communicationProtocol;
	private VelocityEngine engine = new VelocityEngine();
	private String language;
	private Logger log = Logger.getLogger(StandaloneServerBackend.class.getName());
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
		this.definition = this.repository.getWSDL();
		
		this.outputDirectory = (String)this.repository.getParameter(LimboConstants.OUTPUTDIRECTORY);
		this.language = (String)this.repository.getParameter(LimboConstants.LANGUAGE);
		this.communicationProtocol = (String)this.repository.getParameter(LimboConstants.PROTOCOL);

	
		if(this.language.equals(LimboConstants.JSE.toString()))
			generateJSEServerResources();
		if(this.language.equals(LimboConstants.JME.toString()))
			generateJMEServerResources();
	
	}

	
	private void generateJSEServerResources( ) {
		try {
			Template t = engine.getTemplate( "resources/LimboJSEMain.vm" );
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo");
			context.put("class", "LimboMain");
			//by default it is TCP
			if (this.communicationProtocol.toString().equalsIgnoreCase("all"))
			context.put("protocol", "TCPProtocol");
			
			else context.put("protocol", this.communicationProtocol+"Protocol");
			context.put("portTypes",(Collection<PortType>)this.definition.getPortTypes().values());
			String directory = outputDirectory + "/"+webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class") + ".java"));
			t.merge( context, writer );
			writer.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating JSEServer Resources");
			e.printStackTrace();
		}
		for(PortType port : (Collection<PortType>)this.definition.getPortTypes().values()) {
			generateLimboServerResource(port.getQName().getLocalPart());
			generateJSEServerThreadResource(port.getQName().getLocalPart());
		}
		
	}
	
	private void generateLimboServerResource(String portName) {
		try {
			Template t = engine.getTemplate( "resources/LimboServer.vm" );
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo");
			context.put("class", portName+"LimboServer");
			context.put("protocol", this.communicationProtocol+"Protocol");
			context.put("serverThread", portName+"ServerThread");
			String directory = outputDirectory + "/"+webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class") + ".java"));
			t.merge( context, writer );
			writer.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating JSEServer Resources");
			e.printStackTrace();
		}
	}
	private void generateJSEServerThreadResource(String portName) {
		try {
			Template t = engine.getTemplate( "resources/LimboServerThreadJSE.vm" );
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo");
			context.put("endPoint", portName+"EndPoint");
			context.put("class", portName+"ServerThread");
			context.put("HeaderParser", "HeaderParser");
			String directory = outputDirectory + "/"+webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class") + ".java"));
			t.merge( context, writer );
			writer.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating JSEServerThread Resources");
			e.printStackTrace();
		}	
	
	}
	
	private void generateJMEServerResources() {
		try {
			
			Template t = engine.getTemplate( "resources/LimboJMEMain.vm" );
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo");
			context.put("class", "LimboServer");
			context.put("serverThreads", (Collection<PortType>)this.definition.getPortTypes().values());
			context.put("protocol", this.communicationProtocol+"Protocol");
			context.put("imports", "import javax.microedition.lcdui.Command;\nimport javax.microedition.lcdui.CommandListener;\n" +
					"import javax.microedition.lcdui.Display;\nimport javax.microedition.lcdui.Displayable;\n" +
					"import javax.microedition.lcdui.TextBox;\nimport javax.microedition.midlet.MIDlet;\nimport javax.microedition.midlet.MIDletStateChangeException;");
			String directory = outputDirectory + "/"+webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class") + ".java"));
			t.merge( context, writer );
			writer.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating JMEServer Resources");
			e.printStackTrace();
		}
		
		for(PortType port : (Collection<PortType>)this.definition.getPortTypes().values()) {
			generateLimboServerResource(port.getQName().getLocalPart());
			generateJMEServerThreadResource(port.getQName().getLocalPart());
		}

	}
	
	public void generateJMEServerThreadResource(String portName) {
		try {
			Template t = engine.getTemplate( "resources/LimboServerThreadJME.vm" );
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo");
			context.put("endPoint", portName+"EndPoint");
			context.put("class", portName+"ServerThread");
			context.put("HeaderParser", "HeaderParser");
			context.put("imports", "import javax.microedition.lcdui.TextBox;\n");
			String directory = outputDirectory + "/"+webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class") + ".java"));
			t.merge( context, writer );
			writer.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating JMEServerThread Resources");
			e.printStackTrace();
		}	
		
	}

}
