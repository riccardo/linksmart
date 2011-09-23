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
package eu.linksmart.limbo.backend.upnp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.extensions.schema.Schema;
import javax.xml.namespace.QName;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.osgi.service.component.ComponentContext;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import eu.linksmart.limbo.constants.LimboConstants;
import eu.linksmart.limbo.backend.Backend;
import eu.linksmart.limbo.frontend.ontology.OntologyHandler;
import eu.linksmart.limbo.repository.Repository;
import eu.linksmart.limbo.soap.BasicTypesHandling;
import eu.linksmart.limbo.soap.OperationType;
import eu.linksmart.limbo.soap.TypesHandler;
import eu.linksmart.limbo.library.OSGiResourceLoader;
import eu.linksmart.upnp.ActionArgument;
import eu.linksmart.upnp.UPnPActionImpl;
import eu.linksmart.upnp.UPnPDeviceImpl;
import eu.linksmart.upnp.UPnPServiceImpl;
import eu.linksmart.upnp.UPnPStateVariableImpl;
import com.ibm.wsdl.extensions.schema.SchemaImportImpl;

@Component(properties={
		"limbo.platform=.*",
		"limbo.language=jse",
		"limbo.generationtype=service;server;all",
		"limbo.generates=controller"	
})
public class UPnPBackend implements Backend {

	
	private VelocityEngine engine = new VelocityEngine();
	private TypesHandler typesHandler;
	private Repository repository;
	private String transportProtocol;
	private String webServiceName;
	private String outputDirectory;
	private String serverType;
	private File wsdlFile;
	private ComponentContext context;
	private static final Logger log = Logger.getLogger(UPnPBackend.class.getName());
	private List<UPnPServiceImpl> services;
	private UPnPDeviceImpl device;
	private String deviceDescriptionPath;
	private List<String> serviceDescriptionPaths;
	private Definition definition;
	private Map<String,String> map;
	private boolean handlingTypes;
    private HashMap<String, List<OperationType>> operations;

	
	
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
		repository = null;
	}
	
	
	public void generate() throws Exception {
		if(((String)repository.getParameter(LimboConstants.UPNP)).equals("true")) {
			engine.setProperty("resource.loader", "mine"); 
			engine.setProperty("mine.resource.loader.instance", new OSGiResourceLoader(this.context.getBundleContext().getBundle()));
			this.serverType = (String)this.repository.getParameter(LimboConstants.PLATFORM);
			this.transportProtocol = (String)this.repository.getParameter(LimboConstants.PROTOCOL);
			String wsdlFileName = this.repository.getWSDLFile().getName();
			StringTokenizer st = new StringTokenizer(wsdlFileName,".");
			this.webServiceName = st.nextToken();
			this.outputDirectory = (String)this.repository.getParameter(LimboConstants.OUTPUTDIRECTORY);
			//FIXME: get deviceID from repository, if null use local device ontology to retrieve info, else use the ontology manager to retrieve the device information.
			this.typesHandler = new TypesHandler(LimboConstants.JSE.toString());
			this.services = new LinkedList<UPnPServiceImpl>();
			this.serviceDescriptionPaths = new LinkedList<String>();
			OntologyHandler oh = null;
			URI ontURI = this.repository.getOntologyURI();
			this.operations = (HashMap<String,List<OperationType>>)this.repository.getParameter(LimboConstants.OPERATIONS);
			
			
			if(ontURI != null) {
				oh = new OntologyHandler(new URI("file:resources/Device.owl"));
				map = oh.getLinkSmartOnProperties(ontURI);
				this.buildDeviceInfo();
				this.parseWSDL();
				if(this.serverType.equals(LimboConstants.OSGI.toString()))
					generateOSGiResources();
				else
					if(this.serverType.equals(LimboConstants.STANDALONE.toString()))
						generateStandaloneResources();
			}
		}
		
	}	
	
	private void generateOSGiResources() {
		generateActivatorResource(this.webServiceName);
		for(UPnPServiceImpl service : this.services) {
			generateOSGiService(service);
		}
		generateDeviceResource(this.device, this.services);
	}
	
	private void generateOSGiService(UPnPServiceImpl service) {
		for(UPnPStateVariableImpl statVar : service.getStateVariablesImpl())
			generateStateVariableResource(statVar);
		for(UPnPActionImpl action : service.getActionsImpl())
			generateActionResource(action,service,this.operations.get(service.getServiceName()));
		generateServiceResource(service, this.operations.get(service.getServiceName()));
	}
	
	private void generateServiceResource(UPnPServiceImpl service, List<OperationType> operations) {
		try {
			engine.init();
			Template t;
			t = engine.getTemplate( "resources/osgi/UPnPService.vm" );
			VelocityContext context = new VelocityContext();
			context.put("methods", this.generateOperations(operations));
			context.put("service", service);
			String importTypes = "";
			if(this.handlingTypes)
				importTypes = "import eu.linksmart.limbo.types.*;\n";
			context.put("importTypes", importTypes);
			context.put("webServiceName", this.webServiceName);
			context.put("nStateVars", service.getStateVariablesImpl().length);
			context.put("class", service.getServiceName()+"UPnPService");
			context.put("device", this.webServiceName+"Device");
			context.put("webServiceName",this.webServiceName);
			context.put("package", "eu.linksmart.limbo.upnp");
			String directory = outputDirectory+"/"+this.webServiceName+"Server/src/eu/linksmart/limbo/upnp";
			new File(directory).mkdirs();
			String filePath = directory + "/"+context.get("class")+".java";
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(filePath));
			t.merge(context, bWriter);
			bWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generateActionResource(UPnPActionImpl action, UPnPServiceImpl service,List<OperationType> operations) {
		try {
			engine.init();
			Template t;
			t = engine.getTemplate( "resources/osgi/UPnPAction.vm" );
			VelocityContext context = new VelocityContext();
			OperationType operation = null;
			for(OperationType op : operations)
				if(op.getOpName().equals(action.getName()))
					operation = op;
			context.put("operation",operation);
			context.put("basicTypesHandling", new BasicTypesHandling());
			context.put("action", action);
			if(this.handlingTypes)
				context.put("importTypes", "import eu.linksmart.limbo.types.*;\n");
			else
				context.put("importTypes", "");
			context.put("class", action.getName());
			context.put("package", "eu.linksmart.limbo.upnp");
			context.put("service",service.getServiceName()+"UPnPService");
			String directory = outputDirectory + "/" + this.webServiceName+"Server/src/eu/linksmart/limbo/upnp";
			new File(directory).mkdirs();
			String filePath = directory + "/"+action.getName()+"Action.java";
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(filePath));
			t.merge(context, bWriter);
			bWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generateStateVariableResource(UPnPStateVariableImpl stateVariable) {
		try {
			engine.init();
			Template t;
			t = engine.getTemplate( "resources/osgi/UPnPStateVariable.vm" );
			VelocityContext context = new VelocityContext();
			context.put("statevariable", stateVariable);
			context.put("class", stateVariable.getName()+"StateVariable");
			context.put("defaultValues", UPnPConstants.statVarDefaultValue);
			
			context.put("package", "eu.linksmart.limbo.upnp");
			String directory = outputDirectory+ "/" +this.webServiceName+"Server/src/eu/linksmart/limbo/upnp";
			new File(directory).mkdirs();
			String filePath = directory + "/"+stateVariable.getName()+"StateVariable.java";
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(filePath));
			t.merge(context, bWriter);
			bWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generateStandaloneResources() {
		generateUPnPDeviceDescription();
		for(UPnPServiceImpl upnpService : this.services) {
			generateUPnPServiceDescription(upnpService);
			generateUPnPServiceOpsImpl(upnpService);
		}
		generateStandaloneServerResource();
		generateMulticastThreadResource();
		
	}
	
	private void generateUPnPServiceOpsImpl(UPnPServiceImpl service) {
		try {
			engine.init();
			Template t;
			t = engine.getTemplate( "resources/standalone/UPnPServiceOpsImplTemplate.vm" );
			VelocityContext context = new VelocityContext();
			context.put("service", service);
			context.put("deviceWSDL", this.webServiceName);
			context.put("operations", service.getActionsImpl());
			context.put("statevariables", service.getStateVariablesImpl());
			context.put("comma", ",");
			context.put("i", 0);
			String directory = outputDirectory + "/" + this.webServiceName+"Server/src/eu/linksmart/limbo/upnp";
			new File(directory).mkdirs();
			String serviceOpsImplPath = directory + "/"+service.getServiceName()+"UPnPOpsImpl.java";
			context.put("class", service.getServiceName()+"UPnPOpsImpl");
			context.put("serviceOpsImpl", service.getServiceName()+"OpsImpl");
			context.put("serviceWS", service.getServiceName());
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(serviceOpsImplPath));
			t.merge(context, bWriter);
			bWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generateMulticastThreadResource() {	
		try {
			engine.init();
			Template t;
			t = engine.getTemplate( "resources/standalone/MulticastThread.vm" );
			VelocityContext context = new VelocityContext();
			String directory = outputDirectory + "/" +this.webServiceName+"Server/src/eu/linksmart/limbo/upnp";
			new File(directory).mkdirs();
			String serviceDescriptionPath = directory + "/UPnPMulticastThread.java";
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(serviceDescriptionPath));
			t.merge(context, bWriter);
			bWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generateStandaloneServerResource() {
		try {
			Template t;
			t = engine.getTemplate( "resources/standalone/FileHttpServer.vm" );
			VelocityContext context = new VelocityContext();
			context.put("device", device);
			context.put("deviceFilePath", this.deviceDescriptionPath);
			context.put("deviceWSDL", this.repository.getWSDLFile().getName().substring(0, this.repository.getWSDLFile().getName().indexOf(".")));
			context.put("nServices", device.getServicesImpl().length);
			context.put("protocol", this.transportProtocol+"Protocol");
			context.put("services", device.getServicesImpl());
			context.put("webServiceName", this.webServiceName);
			context.put("basicTypes",new eu.linksmart.limbo.soap.BasicTypesHandling());
			String directory = outputDirectory + "/" +this.webServiceName+"Server/src/eu/linksmart/limbo/upnp";
			new File(directory).mkdirs();
			String serviceDescriptionPath = directory + "/UPnPServer.java";
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(serviceDescriptionPath));
			t.merge(context, bWriter);
			bWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generateUPnPDeviceDescription() {

		try {
			Template t;
			VelocityContext context = new VelocityContext();
			t = engine.getTemplate( "resources/standalone/UPnPDeviceTemplate.vm" );
			context.put("services", this.services);
			context.put("device", this.device);
			String directory = outputDirectory + "/" +this.webServiceName+"Server/resources";
			new File(directory).mkdirs();
			
			this.deviceDescriptionPath = "resources/"+device.getFriendlyName()+".xml";
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(directory+"/"+device.getFriendlyName()+".xml"));
			t.merge(context, bWriter);
			bWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generateUPnPServiceDescription(UPnPServiceImpl service) {
		try {
			Template t;
			VelocityContext context = new VelocityContext();
			t = engine.getTemplate( "resources/standalone/UPnPServiceTemplate.vm" );
			context.put("service", service);
			String directory = outputDirectory + "/" +this.webServiceName+"Server/resources";
			new File(directory).mkdirs();
			this.serviceDescriptionPaths.add("resources/"+service.getServiceName()+".xml");
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(directory+"/"+service.getServiceName()+"_scpd.xml"));
			t.merge(context, bWriter);
			bWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
private String generateOperations(List<OperationType> operations) {
		
		Iterator<OperationType> it = operations.iterator();
		String headers = "";
		while (it.hasNext()){
			OperationType ot = it.next();
			String outType = ot.getOutputType();
			if(ot.getInput() != null){
				headers = headers.concat("\tpublic "+outType+" "+ot.getOpName()+"(");
				for(int i=0;i<ot.getInputParts().size();i++){
					String inputName = ot.getInputParts().get(i).getName();
					String inputType = ot.getInputParts().get(i).getType();
					if(i==ot.getInputParts().size()-1)
						headers = headers.concat(inputType+" "+inputName+" ");
					else
						headers = headers.concat(inputType+" "+inputName+", ");
				}
				headers = headers.concat(")");
				headers = headers.concat( "{"+ot.getResultLine()+"}\n\n");
				
					
			}
			else {
				headers = headers.concat("\tpublic "+outType+" "+ot.getOpName()+"( )");
				headers = headers.concat("{}\n\n");
			}
		}
		return headers;
	}
	
	
	@SuppressWarnings("unchecked")
	private void parseWSDL() throws Exception {
		
		QName serviceQName = null;
		this.definition = this.repository.getWSDL();
		parseComplexType(definition);
		for(QName port: (Set<QName>)this.definition.getPortTypes().keySet()){
			UPnPServiceImpl service = new UPnPServiceImpl();
			for (QName bindingName: (Set<QName>)this.definition.getBindings().keySet()) {
				
				Binding binding = this.definition.getBinding(bindingName);
				if(binding.getPortType().getQName().equals(port)) {
					for (BindingOperation operation: (List<BindingOperation>)binding.getBindingOperations()) {
						try {
							parseBindingOperation(serviceQName, port,this.definition, operation, service);
						} catch (Exception e) {
							log.log(Level.SEVERE, "Error generating for binding operation");
							e.printStackTrace();
						}
					}
				}
			}
			service.setType("urn:schemas-upnp-org:service:"+port.getLocalPart()+":1");
			service.setId("urn:upnp-org:serviceId:"+port.getLocalPart());
			service.setSCPDURL("_urn:upnp-org:serviceId:"+port.getLocalPart()+"_scpd.xml");
			service.setControlUrl("_urn:upnp-org:serviceId:"+port.getLocalPart()+"_control");
			service.setEventSubUrl("_urn:upnp-org:serviceId:"+port.getLocalPart()+"_event");
			service.setServiceName(port.getLocalPart());
			this.services.add(service);
			this.device.addService(service);
		}
		
			
		
	}
	
	@SuppressWarnings("unchecked")
	private void parseBindingOperation(QName serviceQName, QName port, Definition def, BindingOperation bo, UPnPServiceImpl service) {
			
			PortType portType = definition.getPortType(port); 
			for (Operation operation: (List<Operation>)portType.getOperations()) {
				if ((operation.getName().equals(bo.getName())) && (operation.getInput().getName().equals(bo.getBindingInput().getName())) && (operation.getOutput().getName().equals(bo.getBindingOutput().getName()))) {
					 UPnPActionImpl action = new UPnPActionImpl();
					 action.setActionName(bo.getName()); 					 
					 for(Part input : (List<Part>)operation.getInput().getMessage().getOrderedParts(null)) {
						 ActionArgument argIn = new ActionArgument(input.getName(), ActionArgument.ARG_IN,input.getName());
						 String type = input.getTypeName().getLocalPart();
						 Class javaDataType = UPnPConstants.toClass(type);
						 if(javaDataType != null) {
							 UPnPStateVariableImpl statVar = new UPnPStateVariableImpl(input.getName(), javaDataType, null, null, null,null, false);
							 statVar.setTypeConstant(UPnPConstants.toString(javaDataType));
							 service.addStateVariable(statVar);
							 argIn.setRelatedStateVariable(statVar);
							 action.addArgument(argIn);
							 boolean contains = false;
							 for(UPnPStateVariableImpl sv : service.getStateVariablesImpl()) {
								 if(sv.getName().equals(statVar.getName()))
									 contains = true;
							 }
							 if(!contains)
								 service.addStateVariable(statVar);
								 
						 }
						 else {
							 eu.linksmart.limbo.soap.Types argType = this.typesHandler.getTypes().get(type);
							 generateForType(argType, service, action, ActionArgument.ARG_IN); 
						 }
					
					 }	 
					 for(Part output : (List<Part>)operation.getOutput().getMessage().getOrderedParts(null)) {
						 ActionArgument argOut = new ActionArgument(output.getName(), ActionArgument.ARG_OUT,output.getName());
						 String type = output.getTypeName().getLocalPart();
						 Class javaDataType = UPnPConstants.toClass(type);
						 if(javaDataType != null) {
							 UPnPStateVariableImpl statVar = new UPnPStateVariableImpl(output.getName(), javaDataType, null, null, null,null, false);
							 statVar.setTypeConstant(UPnPConstants.toString(javaDataType));
							 service.addStateVariable(statVar);
							 argOut.setRelatedStateVariable(statVar);
							 action.addArgument(argOut);
							 boolean contains = false;
							 for(UPnPStateVariableImpl sv : service.getStateVariablesImpl()) {
								 if(sv.getName().equals(statVar.getName()))
									 contains = true;
							 }
							 if(!contains)
								 service.addStateVariable(statVar);
						 }
						 else {
							 //ComplexType
							 eu.linksmart.limbo.soap.Types argType = this.typesHandler.getTypes().get(type);
							 generateForType(argType, service, action, ActionArgument.ARG_OUT); 
						 }
					 }
					 if(!service.getActionsImplHash().containsKey(action.getName()))
						 service.addAction(action);
				}	
				
			}
	}
	
	@SuppressWarnings("unchecked")
	private void generateForType(eu.linksmart.limbo.soap.Types type, UPnPServiceImpl service, UPnPActionImpl action, String direction) {		
		
		for(eu.linksmart.limbo.soap.Types subType : type.getSequence()) {
			if(subType.isBasicType()) {
				ActionArgument argument = new ActionArgument(subType.getTypeName(), direction, subType.getTypeName());
				String argType = type.getType();
				Class javaDataType = UPnPConstants.toClass(argType);
				if(javaDataType != null) {
					UPnPStateVariableImpl statVar = new UPnPStateVariableImpl(subType.getTypeName(), javaDataType, null, null, null,null, false);
					statVar.setTypeConstant(UPnPConstants.toString(javaDataType));
					service.addStateVariable(statVar);
					argument.setRelatedStateVariable(statVar);
					action.addArgument(argument);
					boolean contains = false;
					for(UPnPStateVariableImpl sv : service.getStateVariablesImpl()) {
						 if(sv.getName().equals(statVar.getName()))
							 contains = true;
					 }
					 if(!contains)
						 service.addStateVariable(statVar);
				}
				else {
					//Non-supported by UPnP as String
					UPnPStateVariableImpl statVar = new UPnPStateVariableImpl(subType.getTypeName(), String.class, null, null, null,null, false);
					statVar.setTypeConstant(UPnPConstants.toString(String.class));
					service.addStateVariable(statVar);
					argument.setRelatedStateVariable(statVar);
					action.addArgument(argument);
					boolean contains = false;
					 for(UPnPStateVariableImpl sv : service.getStateVariablesImpl()) {
						 if(sv.getName().equals(statVar.getName()))
							 contains = true;
					 }
					 if(!contains)
						 service.addStateVariable(statVar);
				}
			}
			else 
				generateForType(subType, service, action, direction);
		}
		service.addAction(action);
	}
	
	@SuppressWarnings("unchecked")
	private void parseComplexType(Definition definition) {
		
		if((com.ibm.wsdl.TypesImpl)definition.getTypes()!=null){
			for (Schema schema: (List<Schema>)definition.getTypes().getExtensibilityElements()) {
				for (String importName: (Set<String>)schema.getImports().keySet()) {
					for (SchemaImportImpl schemaImport: (Vector<SchemaImportImpl>)schema.getImports().get(importName)) {
						NodeList childNodes = schemaImport.getReferencedSchema().getElement().getChildNodes(); 
						for (int i = 0; i < childNodes.getLength(); i++) {
							if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
								if ("complexType".equals(childNodes.item(i).getLocalName())) {
									try {
										generateForComplexType(childNodes.item(i));
									} catch (Exception e) {
										log.log(Level.SEVERE, "Error parsing complex type");
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void generateForComplexType(Node node) {
		String complexTypeName = node.getAttributes().getNamedItem("name").getNodeValue();
		eu.linksmart.limbo.soap.Types type = new eu.linksmart.limbo.soap.Types(null, complexTypeName, false,false);
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				if ("sequence".equals(childNodes.item(i).getLocalName()))
					generateForSequence(childNodes.item(i), type);
			}
		}
	}
			
	private void generateForSequence(Node node, eu.linksmart.limbo.soap.Types type) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				if ("element".equals(childNodes.item(i).getLocalName()))
					generateForElement(childNodes.item(i),type);
			}
		}
	}
	
	private void generateForElement(Node node, eu.linksmart.limbo.soap.Types type) {
		String elementName = node.getAttributes().getNamedItem("name").getNodeValue();
		String elementType = node.getAttributes().getNamedItem("type").getNodeValue();
		StringTokenizer st = new StringTokenizer(elementType, ":");
		st.nextToken();
		elementType = st.nextToken();
		boolean isArray = false;
		Node maxOccursNode = node.getAttributes().getNamedItem("maxOccurs");
		if(maxOccursNode != null) {
			if(maxOccursNode.getNodeValue().equals("unbounded"))
				isArray = true;
		}
		boolean optional = false; 
		Node minOccursNode = node.getAttributes().getNamedItem("minOccurs");
		if(minOccursNode != null) {
			if(minOccursNode.getNodeValue().equals("0"))
				optional = true;
		}
		type.addTypeToSequence(new eu.linksmart.limbo.soap.Types(elementName, this.typesHandler.getCorrectType(LimboConstants.JSE.toString(), elementType), isArray, optional));
		this.typesHandler.addNewType(type);	
		this.handlingTypes = true;
	}
	
	
	//Currently local information is retrieved, EXTEND to Ontology Manager
	private void buildDeviceInfo() throws Exception {
		
		
		this.device = new UPnPDeviceImpl();
		String deviceFriendlyName = new String();
		String deviceManufacturer = new String();
		String deviceManufacturerURL = new String();
		String deviceModelDescription = new String();
		String deviceModelName = new String();
		String deviceModelNumber = new String();
		for (Iterator<Map.Entry<String,String>> it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String,String> entry = it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			if(key.toString().endsWith("_friendlyName"))
				deviceFriendlyName = value.toString();
			if(key.toString().endsWith("_manufacturer"))
				deviceManufacturer = value.toString();
			if(key.toString().endsWith("_manufacturerURL"))
				deviceManufacturerURL = value.toString();
			if(key.toString().endsWith("_modelDescription"))
				deviceModelDescription = value.toString();
			if(key.toString().endsWith("_modelName"))
				deviceModelName = value.toString();	
			if(key.toString().endsWith("_modelNumber"))
				deviceModelNumber = value.toString();		
		}
		
		StringTokenizer token = new StringTokenizer(repository.getOntologyURI().toString());
		String deviceType = null;
		while(token.hasMoreTokens())
			deviceType = token.nextToken("#");
		
		this.device.setDeviceType(deviceType);
		this.device.setFriendlyName(deviceFriendlyName);
		this.device.setManufacturer(deviceManufacturer);
		this.device.setManufacturerURL(deviceManufacturerURL);
		this.device.setModelDescription(deviceModelDescription);
		this.device.setModelName(deviceModelName);
		this.device.setModelNumber(deviceModelNumber);
		String uuid = java.util.UUID.randomUUID().toString();
		this.device.setUDN("uuid:"+uuid);
		
	}
	
	private void generateDeviceResource(UPnPDeviceImpl device, List<UPnPServiceImpl> services) {
		try {
			Template t;
			t = engine.getTemplate("resources/osgi/UPnPDevice.vm" );
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo.upnp");
			context.put("class", this.webServiceName+"Device");
			context.put("device", device);
			context.put("webServiceName", this.webServiceName);
			context.put("services", services);
			String directory = outputDirectory + "/"+this.webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class") + ".java"));
			t.merge(context, writer);
			writer.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating UPnPDevice resource!");
			e.printStackTrace();
		}
		
	}
	
	private void generateActivatorResource(String deviceName) {
		try {
			Template t;
			t = engine.getTemplate("resources/osgi/UPnPActivator.vm" );
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo.upnp");
			context.put("class", this.webServiceName+"UPnPActivator");
			context.put("imports", "import java.util.Dictionary;\n\nimport org.osgi.framework.BundleActivator;\n" +
					"import org.osgi.framework.BundleContext;\nimport org.osgi.framework.ServiceRegistration;\n" +
					"import org.osgi.service.upnp.UPnPDevice;");
			context.put("device", deviceName+"Device");
			context.put("portTypes", this.definition.getPortTypes().values());
			context.put("webServiceName", this.webServiceName);
			String directory = outputDirectory + "/"+this.webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class") + ".java"));
			t.merge(context, writer);
			writer.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating UPnPActivator resource!");
			e.printStackTrace();
		}
		
	}
	
}
