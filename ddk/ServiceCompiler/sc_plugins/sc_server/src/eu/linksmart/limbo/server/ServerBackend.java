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
package eu.linksmart.limbo.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
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


@Component(properties = {
		"limbo.platform=.*",
		"limbo.language=jse;jme",
		"limbo.generates=controller",
		"limbo.generationtype=service;server;all"})
public class ServerBackend implements Backend {

	/**Generates standard classes for the Server: Handler, Handlers, HandlerService, StringTokenizer and OpsImpl**/


	private Repository repository;
	private String outputDirectory; 
	private String webServiceName;
	private String typeOfSystem;
	private VelocityEngine engine = new VelocityEngine();
	private boolean withLogHandler;
	//private boolean withProbeHandler; //FIXME: Probe handler 
	private String language;
	private Logger log = Logger.getLogger(ServerBackend.class.getName());
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


	@SuppressWarnings("unchecked")
	public void generate() throws Exception {
		engine.setProperty("resource.loader", "mine"); 
		engine.setProperty("mine.resource.loader.instance", new OSGiResourceLoader(this.context.getBundleContext().getBundle()));
		String wsdlFileName = this.repository.getWSDLFile().getName();
		StringTokenizer st = new StringTokenizer(wsdlFileName,".");
		this.webServiceName = st.nextToken();
		this.typeOfSystem = (String)this.repository.getParameter(LimboConstants.PLATFORM);
		this.outputDirectory = (String)this.repository.getParameter(LimboConstants.OUTPUTDIRECTORY);
		this.withLogHandler = Boolean.valueOf((String)this.repository.getParameter(LimboConstants.LOGHANDLER));
		this.language = (String)this.repository.getParameter(LimboConstants.LANGUAGE);

		HashMap<String, LinkedList<String>> operations = (HashMap<String, LinkedList<String>>)this.repository.getParameter(LimboConstants.OPERATIONSNAMES);

		LinkedList<String> handlers = (LinkedList<String>)this.repository.getParameter(LimboConstants.HANDLERSLIST);

		//must be last in order to know the final list of handlers in the queue!
		if(handlers.contains("ProbeHandler")) { //if has probe handler then there should be two probe handlers in the queue one in the first position and one in the last position
			handlers.remove("ProbeHandler"); //removes to be sure that it will be first or last in the list
			handlers.addFirst("ProbeHandler");
			handlers.addLast("ProbeHandler");
		}
		for(String portName : operations.keySet()) {
			LinkedList<String> opsNames = operations.get(portName);
			generateHandlerResource(portName);
			generateHandlerServiceResource(portName);

			generateServiceResource(portName, opsNames);
			generateEndPointResource(portName);

			if(this.withLogHandler)
				generateLogHandlerResource(portName);
			generateHandlersResource(portName, handlers);
		}
		generateStringTokenizerResource();
		generateHeaderParser();	

	}

	@SuppressWarnings("unchecked")
	private void generateServiceResource(String portName, LinkedList<String> operationsNames) {
		try {

			Template t;
			t = engine.getTemplate( "resources/Service.vm" );
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo");
			context.put("class", portName+"Service");
			context.put("OperationsImpl", portName+"OpsImpl");
			context.put("handlers", portName+"Handlers");
			context.put("Parser", portName+"Parser");
			context.put("endPoint", portName+"EndPoint");
			context.put("operations", operationsNames);
			context.put("nOperations", operationsNames.size());
			String directory = outputDirectory + "/"+this.webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class") + ".java"));
			t.merge(context, writer);
			writer.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating Service resource!");
			e.printStackTrace();
		}
	}

	private void generateEndPointResource(String portName) {
		try {

			Template t = engine.getTemplate( "resources/EndPoint.vm" );
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo");
			context.put("class", portName+"EndPoint");
			context.put("service", portName+"Service");
			String directory = outputDirectory + "/"+webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory+"/" + context.get("class") + ".java"));
			t.merge( context, writer );
			writer.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating EndPoint resource!");
			e.printStackTrace();
		}
	}

	private void generateHandlerServiceResource(String portName) {
		try {

			Template tHandler;
			tHandler = engine.getTemplate("/resources/Handlers/HandlerService.vm");
			VelocityContext ctxtHandler = new VelocityContext();
			String directory = "";
			ctxtHandler.put("package", "eu.linksmart.limbo.handler");
			directory = this.outputDirectory + "/"+this.webServiceName+"Server/src/" + ctxtHandler.get("package").toString().replace('.', '/');

			ctxtHandler.put("interface", portName+"HandlerService");
			if(this.typeOfSystem.equals("osgi"))
				ctxtHandler.put("service", portName+"Servlet");
			else
				ctxtHandler.put("service", portName+"Service");
			ctxtHandler.put("OperationsImpl", portName+"OpsImpl");

			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + ctxtHandler.get("interface") + ".java"));
			tHandler.merge(ctxtHandler, writer);
			writer.close();
		}catch(Exception ex) {ex.printStackTrace();}
	}

	private void generateStringTokenizerResource() {
		try {

			Template t;
			t = engine.getTemplate( "resources/StringTokenizer.vm" );
			String directory ="";
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo");
			directory = this.outputDirectory + "/"+this.webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');

			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/StringTokenizer.java"));
			t.merge( context, writer );
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateHeaderParser() {
		try {
			Template t = engine.getTemplate( "resources/HeaderParser.vm" );
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo");
			context.put("class", "HeaderParser");
			context.put("ifCondition", "(this.postLine.startsWith("+'"'+"POST"+'"'+")&&(this.postLine.endsWith("+'"'+" HTTP/1.0"+'"'+")))||(this.postLine.startsWith("+'"'+"POST"+'"'+")&&(this.postLine.endsWith("+'"'+" HTTP/1.1"+'"'+")))");
			String directory = outputDirectory + "/"+webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class") + ".java"));
			t.merge( context, writer );
			writer.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating HeaderParser resource!");
			e.printStackTrace();
		}
	}

	private void generateHandlerResource(String portName) {
		try {
			Template tHandler;
			tHandler = engine.getTemplate("/resources/Handlers/Handler.vm");
			VelocityContext ctxtHandler = new VelocityContext();
			String directory = "";
			ctxtHandler.put("package", "eu.linksmart.limbo.handler");
			directory = this.outputDirectory + "/"+webServiceName+"Server/src/" + ctxtHandler.get("package").toString().replace('.', '/');

			ctxtHandler.put("class", portName+"Handler");
			ctxtHandler.put("handlers", portName+"Handlers");
			if(this.typeOfSystem.equals("osgi"))
				ctxtHandler.put("service", portName+"Servlet");
			else
				if(this.typeOfSystem.equals("standalone"))
					ctxtHandler.put("service", portName+"Service");

			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + ctxtHandler.get("class") + ".java"));
			tHandler.merge(ctxtHandler, writer);
			writer.close();
		}catch(Exception ex) {ex.printStackTrace();}
	}

	@SuppressWarnings("unchecked")
	private void generateLogHandlerResource(String portName) {
		try {

			Template tHandler = null;
			if(this.language.equals(LimboConstants.JSE.toString()))
				tHandler = engine.getTemplate("/resources/Handlers/LogHandler.vm");
			else
				tHandler = engine.getTemplate("/resources/Handlers/LogHandlerJME.vm");
			VelocityContext ctxtHandler = new VelocityContext();
			ctxtHandler.put("package", "eu.linksmart.limbo.handler");
			ctxtHandler.put("OperationsImpl", portName+"OpsImpl");
			ctxtHandler.put("class", portName+"LogHandler");
			if(this.typeOfSystem.equals(LimboConstants.STANDALONE))
				ctxtHandler.put("service", portName+"Service");
			else
				ctxtHandler.put("service", portName+"Servlet");

			ctxtHandler.put("handler", portName+"Handler");
			ctxtHandler.put("handlers", portName+"Handlers");
			String directory = outputDirectory + "/"+webServiceName+"Server/src/" + ctxtHandler.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + ctxtHandler.get("class") + ".java"));
			tHandler.merge(ctxtHandler, writer);
			writer.close();
			((LinkedList<String>)this.repository.getParameter(LimboConstants.HANDLERSLIST)).add("LogHandler");
		}catch(Exception ex) {
			log.log(Level.SEVERE, "Error generating LogHandler Resource!");
			ex.printStackTrace();
		}


	}

	private void generateHandlersResource(String portName, LinkedList<String> handlers) {
		try {

			Template tHandler;
			tHandler = engine.getTemplate("/resources/Handlers/Handlers.vm");
			VelocityContext ctxtHandler = new VelocityContext();
			ctxtHandler.put("class", portName+"Handlers");
			ctxtHandler.put("fileName", portName); //CHECK
			ctxtHandler.put("parser", portName+"Parser");
			ctxtHandler.put("handler", portName+"Handler");

			LinkedList<String> handlersNames = new LinkedList<String>();
			int counter = 1;
			for(String handler : handlers) {
				if(handler.equals("ProbeHandler"))
					handlersNames.add(handler.toLowerCase()+counter++);
				else
					handlersNames.add(handler.toLowerCase());
			}
			ctxtHandler.put("handlers", handlers);
			ctxtHandler.put("handlersNames", handlersNames);

			ctxtHandler.put("import", "eu.linksmart.limbo.*;");
			ctxtHandler.put("package", "eu.linksmart.limbo.handler");
			ctxtHandler.put("handlerService", portName+"HandlerService");
			if(this.typeOfSystem.equals(LimboConstants.OSGI))
				ctxtHandler.put("service", portName+"Servlet");
			else
				ctxtHandler.put("service", portName+"Service");

			ctxtHandler.put("OperationsImpl", portName+"OpsImpl");
			String directory = outputDirectory + "/"+this.webServiceName+"Server/src/" + ctxtHandler.get("package").toString().replace('.', '/');

			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + ctxtHandler.get("class") + ".java"));
			tHandler.merge(ctxtHandler, writer);
			writer.close();
		}catch(Exception ex) {
			log.log(Level.SEVERE, "Error generating HandlersResource");
			ex.printStackTrace();}

	}

}
