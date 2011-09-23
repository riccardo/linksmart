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
package eu.linksmart.limbo.backend.linksmartstandard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.wsdl.PortType;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import eu.linksmart.limbo.constants.LimboConstants;
import eu.linksmart.limbo.backend.Backend;
import eu.linksmart.limbo.generator.Generator;
import eu.linksmart.limbo.repository.Repository;
import eu.linksmart.limbo.library.OSGiResourceLoader;

@Component(properties={
		"limbo.platform=.*",
		"limbo.language=jse;jme",
	  	 "limbo.generates=controller",
	  	 "limbo.generationtype=service;server;all"
	})
public class LinkSmartStandardBackend implements Backend {
	private Repository repository;
	private String webServiceName;
	private String generationType;
	private String communicationProtocol;
	private String outputDirectory; 
	private VelocityEngine engine = new VelocityEngine();
	private String language;
	private static final Logger log = Logger.getLogger(LinkSmartStandardBackend.class.getName());
	private ComponentContext context;
	private Generator generator;
	private String serverType;
	private String withUPnP;
	
	protected void activate(ComponentContext ctxt)  {
		this.context = ctxt;
	}
	
	protected void deactivate(ComponentContext ctxt) {	
	}
	
	@Reference
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	
	public void unsetRepository(Repository repository) {
		this.repository = null;
	}
	
	//Generates LinkSmartStandardOpsImpl resources and calls Limbo to generate Web Service code for this service
	
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
		this.serverType = (String)this.repository.getParameter(LimboConstants.PLATFORM);
		this.withUPnP = (String)this.repository.getParameter(LimboConstants.UPNP);
		if(!((this.serverType.equals(LimboConstants.OSGI.toString()))&& ((this.withUPnP.equals("true")))))
			generateLinkSmartOpsImplResource();
		String directory = outputDirectory + "/"+this.webServiceName+"Server/resources";
		new File(directory).mkdirs();
		File f = this.repository.getWSDLFile();
		if(f.exists()) {
			File dst = new File(directory+"/"+f.getName());
    	
			if(!dst.exists()) {
				dst.createNewFile();
				InputStream in = new FileInputStream(f);
				OutputStream out = new FileOutputStream(dst);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
		}
	
	}
	
	private void generateLinkSmartOpsImplResource() {
		try{
			Template t;
			t = engine.getTemplate( "resources/LinkSmartWSOpsImpl.vm" );
			VelocityContext context = new VelocityContext();
			context.put("protocol", this.communicationProtocol+"Protocol");
			context.put("deviceWSDLFile", "resources/"+this.webServiceName+".wsdl");
			context.put("class", "LinkSmartServicePortOpsImpl");
			context.put("package", "eu.linksmart.limbo");
			if(this.serverType.equals(LimboConstants.OSGI.toString()))
				context.put("server", "Servlet");
			else
				if(this.serverType.equals(LimboConstants.STANDALONE.toString()))
					context.put("server", "LimboServer");
			String serviceName = "";
			context.put("serverType", this.serverType);
			for(PortType pt : (Collection<PortType>)this.repository.getWSDL().getPortTypes().values()){
				if(!pt.getQName().getLocalPart().equals("LinkSmartServicePort")) {
					serviceName = pt.getQName().getLocalPart();
				}
			}
			context.put("serviceName", serviceName);
			String directory = outputDirectory + "/"+webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(directory + "/" + context.get("class") + ".java"));
			//BufferedWriter bWriter = new BufferedWriter(new FileWriter(directory+context.get("class")));
			t.merge(context, bWriter);
			bWriter.close();
		
		
		
		} catch (Exception e) {e.printStackTrace();}
	}

}
