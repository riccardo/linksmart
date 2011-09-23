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
package eu.linksmart.limbo.soap.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.osgi.service.component.ComponentContext;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import eu.linksmart.limbo.constants.LimboConstants;
import eu.linksmart.limbo.backend.Backend;
import eu.linksmart.limbo.repository.Repository;
import eu.linksmart.limbo.library.*;
import com.ibm.wsdl.extensions.schema.SchemaImportImpl;
import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;

import eu.linksmart.limbo.soap.BasicTypesHandling;
import eu.linksmart.limbo.soap.LinkSmartKeyPair;
import eu.linksmart.limbo.soap.OperationType;
import eu.linksmart.limbo.soap.Parts;
import eu.linksmart.limbo.soap.Types;
import eu.linksmart.limbo.soap.TypesHandler;
import eu.linksmart.limbo.soap.TypesHandling;

@Component
public class SOAPBackend implements Backend {

	private Repository repository;
	private TypesHandler typesHandler;
	private String language;
	private String outputDirectory; 
	private String webServiceName;
	private BasicTypesHandling conversion;
	private HashMap<String, List<OperationType>> operations;
	private boolean handlingTypes = false;
	private String typeOfGeneration;
	private String protocol;
	private VelocityEngine engine = new VelocityEngine();
	private static final Logger log = Logger.getLogger(SOAPBackend.class.getName());
	private ComponentContext context;
	private String withProbe;
	private String serverType;
	private String withUPnP; 


	protected void activate(ComponentContext ctxt)  {
		this.context = ctxt;
	}

	@SuppressWarnings("unchecked")
	public void generate() throws Exception {
		engine.setProperty("resource.loader", "mine"); 
		engine.setProperty("mine.resource.loader.instance", new OSGiResourceLoader(this.context.getBundleContext().getBundle()));


		String isSOAP = (String)this.repository.getParameter(LimboConstants.SOAP);
		this.outputDirectory = (String)this.repository.getParameter(LimboConstants.OUTPUTDIRECTORY);
		if(isSOAP.equals("true")){
			this.typeOfGeneration = (String)this.repository.getParameter(LimboConstants.GENERATIONTYPE);
			Definition definition = null;
			QName serviceQName = null;
			definition = this.repository.getWSDL();
			this.language = ((String)this.repository.getParameter(LimboConstants.LANGUAGE)).toString().toUpperCase();
			String wsdlFileName = this.repository.getWSDLFile().getName();
			StringTokenizer st = new StringTokenizer(wsdlFileName,".");
			this.webServiceName = st.nextToken();
			this.protocol = (String)this.repository.getParameter(LimboConstants.PROTOCOL).toString();
			this.withProbe = (String)this.repository.getParameter(LimboConstants.PROBEHANDLER).toString();
			this.withUPnP = (String)this.repository.getParameter(LimboConstants.UPNP).toString();
			this.serverType = (String)this.repository.getParameter(LimboConstants.PLATFORM).toString();
			this.typesHandler = new TypesHandler(this.language);
			this.operations = new HashMap<String, List<OperationType>>();
			this.conversion = new BasicTypesHandling();

			generateForTypes(definition);
			for(QName port: (Set<QName>)definition.getPortTypes().keySet()){
				for (QName bindingName: (Set<QName>)definition.getBindings().keySet()) {

					Binding binding = definition.getBinding(bindingName);
					if(binding.getPortType().getQName().equals(port)) {
						for (BindingOperation operation: (List<BindingOperation>)binding.getBindingOperations()) {
							try {
								generateForBindingOperation(serviceQName, port,definition, operation);
							} catch (Exception e) {
								log.log(Level.SEVERE, "Error generating for binding operation");
								e.printStackTrace();
							}
						}
					}
				}
			}

			for(List<OperationType> operations : this.operations.values()) {
				if((this.typeOfGeneration.equals(LimboConstants.CLIENT.toString())) || (this.typeOfGeneration.equals(LimboConstants.ALL.toString()))) {
					generateClientParserResource(operations);
					generateClientPortResource(operations);
					generateClientPortImplResource(operations);
				}
				if((this.typeOfGeneration.equals(LimboConstants.SERVER.toString())) || (this.typeOfGeneration.equals(LimboConstants.ALL.toString()))) {
					generateParserResource(operations);
					generateSOAPHandlerResource(operations);
					generateOpsImplResource(operations);
				}
			}

			HashMap<String, LinkedList<String>> operationsNames = new HashMap<String,LinkedList<String>>();
			for(String portName : this.operations.keySet()) {
				LinkedList<String> operations = new LinkedList<String>();
				operationsNames.put(portName, operations);
			}

			for(List<OperationType> opList : this.operations.values())
				for(OperationType operation : opList)
					operationsNames.get(operation.getOperationPort()).add(operation.getSOAPAction());

			this.repository.addParameter(LimboConstants.OPERATIONSNAMES.toString(), operationsNames);
			this.repository.addParameter(LimboConstants.OPERATIONS.toString(), this.operations);


		}
	}

	@SuppressWarnings("unchecked")
	private void generateForTypes(Definition definition) {

		boolean hasTypes = (definition.getTypes() != null);
		if(hasTypes){
			Collection<Node> complexTypes = new LinkedList<Node>();
			for (Schema schema: (List<Schema>)definition.getTypes().getExtensibilityElements()) {
				NodeList childNodes;
				// Get embedded types
				childNodes = schema.getElement().getChildNodes(); 
				for (int i = 0; i < childNodes.getLength(); i++) {
					if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
						if ("complexType".equals(childNodes.item(i).getLocalName())) {
							complexTypes.add(childNodes.item(i));
						}
					}
				}
				// Get imported types
				for (String importName: (Set<String>)schema.getImports().keySet()) {
					for (SchemaImportImpl schemaImport: (Vector<SchemaImportImpl>)schema.getImports().get(importName)) {
						boolean hasImportedSchema = (schemaImport.getReferencedSchema() != null);
						this.handlingTypes = true;
						if (hasImportedSchema) {
							childNodes = schemaImport.getReferencedSchema().getElement().getChildNodes(); 
							for (int i = 0; i < childNodes.getLength(); i++) {
								if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
									if ("complexType".equals(childNodes.item(i).getLocalName())) {
										complexTypes.add(childNodes.item(i));
									}
								}
							}
						}
					}
				}
			}
			// Now generate for all types
			for (Node node: complexTypes) {
				try {
					generateForComplexType(node);
				} catch (Exception e) {
					log.log(Level.SEVERE, "Error generating for complex type");
					e.printStackTrace();
				}
			}
			if((this.typeOfGeneration.equals(LimboConstants.SERVER.toString())) || (this.typeOfGeneration.equals(LimboConstants.ALL.toString())))
				generateTypesResources(false);
			if((this.typeOfGeneration.equals(LimboConstants.CLIENT.toString())) || (this.typeOfGeneration.equals(LimboConstants.ALL.toString())))
				generateTypesResources(true);
		}

	}

	private void generateTypesResources(boolean forClient) {
		Template t;
		Iterator<String> it = this.typesHandler.getTypes().keySet().iterator();

		while(it.hasNext()){
			try {
				String key = it.next();
				t = engine.getTemplate( "resources/ComplexTypes/ComplexTypes.vm" );
				VelocityContext context = new VelocityContext();
				String directory ="";
				if(!forClient){
					context.put("package", "eu.linksmart.limbo.types");
					directory = outputDirectory + "/"+webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
				}
				else {
					context.put("package", "eu.linksmart.limbo.client.types");
					directory = outputDirectory + "/"+webServiceName+"Client/src/" + context.get("package").toString().replace('.', '/');
				}
				context.put("class", key);
				context.put("Types2", this.typesHandler.getTypes().get(key));
				context.put("language", this.language);
				context.put("isArray", new Boolean(this.typesHandler.getTypes().get(key).isArray()).toString());
				context.put("sequence", this.typesHandler.getTypes().get(key).getSequence());
				new File(directory).mkdirs();
				BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class") + ".java"));
				t.merge( context, writer );
				writer.close();
			} catch (ResourceNotFoundException e) {
				e.printStackTrace();
			} catch (ParseErrorException e) {
				e.printStackTrace();
			} catch (Exception e) {
				log.log(Level.SEVERE, "Error generating types resources");
				e.printStackTrace();
			}
		}
	}

	private void generateForComplexType(Node node) {
		String complexTypeName = node.getAttributes().getNamedItem("name").getNodeValue();
		Types type = new Types(null, complexTypeName, false,false);
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				if ("sequence".equals(childNodes.item(i).getLocalName()))
					generateForSequence(childNodes.item(i), type);
			}
		}
	}

	private void generateForSequence(Node node, Types type) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				if ("element".equals(childNodes.item(i).getLocalName()))
					generateForElement(childNodes.item(i),type);
			}
		}
	}

	private void generateForElement(Node node, Types type) {
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
		type.addTypeToSequence(new Types(elementName, this.typesHandler.getCorrectType(this.language, elementType), isArray, optional));
		this.typesHandler.addNewType(type);	
	}

	@SuppressWarnings("unchecked")
	private void generateForBindingOperation(QName service, QName port, Definition definition, BindingOperation bo) {

		//for (QName portTypeName: (Set<QName>)definition.getPortTypes().keySet()) {
		PortType portType = definition.getPortType(port);
		if(!this.operations.containsKey(portType.getQName().getLocalPart())) {
			LinkedList<OperationType> operations = new LinkedList<OperationType>();
			this.operations.put(portType.getQName().getLocalPart(), operations);
		}
		for (Operation operation: (List<Operation>)portType.getOperations()) {
			if ((operation.getName().equals(bo.getName())) && (operation.getInput().getName().equals(bo.getBindingInput().getName())) && (operation.getOutput().getName().equals(bo.getBindingOutput().getName()))) {
				OperationType ot = new OperationType(portType.getQName().getLocalPart(), operation.getName(), operation.getInput().getName(), operation.getOutput().getName());
				String soapAction ="";
				for (SOAPOperationImpl ee: (List<SOAPOperationImpl>)bo.getExtensibilityElements())
					soapAction = ee.getSoapActionURI();
				ot.setSOAPAction(soapAction);
				List<Part> inputParts = (List<Part>)operation.getInput().getMessage().getOrderedParts(null);
				LinkedList<Parts> operationInputParts = new LinkedList<Parts>();
				for(Part p : inputParts) {
					String s = this.conversion.getType(new LinkSmartKeyPair(this.language, p.getTypeName().getLocalPart()));
					if(s == null)
						operationInputParts.add(new Parts(p.getName(), p.getTypeName().getLocalPart()));
					else
						operationInputParts.add(new Parts(p.getName(), s));
				}
				ot.setInputParts(operationInputParts);
				ot.setInputArgumentsLine(operationInputParts);
				List<Part> outputParts = (List<Part>)operation.getOutput().getMessage().getOrderedParts(null);
				String body;
				if(outputParts.size() > 0) 
					body = '"'+"<soapenv:Body><"+ot.getOutput()+" xmlns="+'"'+"+'"+'"'+"'"+"+'"+'"'+"'+"+'"'+">"+"<"+outputParts.get(0).getName()+">"+'"'+"+operations."+ot.getOpName()+"(";
				else
					body = '"'+"<soapenv:Body><"+ot.getOutput()+" xmlns="+'"'+"+'"+'"'+"'"+"+'"+'"'+"'+"+'"'+">"+'"'+");\n\t\t\toperations."+ot.getOpName()+"(";
				for(int i=0; i<inputParts.size();i++){
					String inputType = inputParts.get(i).getTypeName().getLocalPart();
					if(i==inputParts.size()-1){
						body = body.concat(this.conversion.getMessagePart(inputType, i));
					}
					else{
						body = body.concat(this.conversion.getMessagePart(inputType, i)+", ");
					}
				}
				if(outputParts.size() > 0) {
					String outputAsString ="";
					if(this.typesHandler.getTypes().containsKey(outputParts.get(0).getTypeName().getLocalPart()))
						outputAsString = ".setSOAPMessageLine()";
					body = body.concat(")"+outputAsString+"+\""+"</"+outputParts.get(0).getName()+"></"+ot.getOutput()+"></soapenv:Body></soapenv:Envelope>"+'"');
				}
				else
					body = body.concat(");\n\t\t\t s=s.concat("+'"'+"</"+ot.getOutput()+"></soapenv:Body></soapenv:Envelope>"+'"');
				ot.setSOAPBody(body);
				if(outputParts.size() > 0) {
					String s = this.conversion.getType(new LinkSmartKeyPair(this.language, outputParts.get(0).getTypeName().getLocalPart()));
					ot.setOutputName(outputParts.get(0).getName());
					if(s == null) 
						ot.setOutputType(outputParts.get(0).getTypeName().getLocalPart());						
					else
						ot.setOutputType(s);
				}
				else {
					ot.setOutputType("void");
					ot.setOutputName(null);
				}
				TypesHandling th = new TypesHandling(ot.getOutputType());
				ot.setClientResultLines(th.getClientReturnLine(), th.getClientDefaultReturnLine());
				this.operations.get(ot.getOperationPort()).add(ot);
			}
		}
		//}
	}

	private void generateClientParserResource(List<OperationType> operations) {
		try {
			Template t;
			VelocityContext context = new VelocityContext();
			String portName = operations.get(0).getOperationPort();
			engine.init();
			t = engine.getTemplate("resources/Parsers/Parser.vm" );
			context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo.client");
			context.put("class", portName+"LimboClientParser");
			context.put("operations", operations);
			context.put("language", this.language);
			context.put("methods", this.generateParsingOperations(operations,false));
			String directory = outputDirectory + "/"+webServiceName+"Client/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class") + ".java"));
			t.merge( context, writer );
			writer.close();
		}catch(Exception e) {
			log.log(Level.SEVERE, "Error generating client parser resource");
			e.printStackTrace();
		}
	}

	private void generateClientPortResource(List<OperationType> operations) {
		try {
			Template t;
			String portName = operations.get(0).getOperationPort();
			t = engine.getTemplate("resources/Client/LimboClientPort.vm" );
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo.client");
			context.put("interface", portName+"LimboClientPort");
			context.put("operations", this.generateOperationsHeader(operations,true));
			if(this.handlingTypes)
				context.put("importTypes", "import eu.linksmart.limbo.client.types.*;");
			else
				context.put("importTypes", "");
			String directory = outputDirectory + "/"+webServiceName+"Client/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("interface") + ".java"));
			t.merge( context, writer );
			writer.close();
		}catch(Exception e) {
			log.log(Level.SEVERE, "Error generating client port resource");
			e.printStackTrace();
		}
	}

	private void generateClientPortImplResource(List<OperationType> operations) {
		try {
			Template t;
			String portName = operations.get(0).getOperationPort();
			t = engine.getTemplate("resources/Client/LimboClientStub.vm" );
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo.client");
			context.put("class", portName+"LimboClientPortImpl");
			context.put("interface", portName+"LimboClientPort");
			context.put("Parser", portName+"LimboClientParser");
			context.put("HeaderParser", "LimboClientHeaderParser");
			context.put("language", this.language);
			context.put("protocol", this.protocol);
			context.put("withProbe", this.withProbe);
			context.put("probe", portName+"ProbeHandler");
			context.put("operations", operations);
			if(this.language.toLowerCase().equals(LimboConstants.JSE.toString()))
				context.put("imports", "import java.net.MalformedURLException;\nimport java.net.URL;\n");
			else
				context.put("imports", "");
			if(this.handlingTypes)
				context.put("importTypes", "import eu.linksmart.limbo.client.types.*;");
			else
				context.put("importTypes", "");
			String directory = outputDirectory + "/"+webServiceName+"Client/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class") + ".java"));
			t.merge( context, writer );
			writer.close();
		}catch(Exception e) {
			log.log(Level.SEVERE, "Error generating clientportimpl resource");
			e.printStackTrace();
		}
	}



	private void generateOpsImplResource(List<OperationType> operations) {
		try {
			Template t;
			String portName = operations.get(0).getOperationPort();
			t = engine.getTemplate( "resources/LimboOpsImpl.vm" );
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo");
			context.put("class", portName+"OpsImpl");
			String imports = "";
			if(this.handlingTypes)
				imports = imports.concat("import eu.linksmart.limbo.types.*;\n");
			String directory = outputDirectory + "/"+webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class") + ".java"));
			boolean methodsFilledIn = false;
			if((this.serverType.equals(LimboConstants.OSGI.toString())) && (this.withUPnP.equals("true"))) { 
				imports = imports.concat("\nimport java.util.Hashtable;\n import java.util.Dictionary;\nimport eu.linksmart.limbo.upnp.*;");
				if(portName.equals("LinkSmartServicePort"))
					imports = imports.concat("\nimport java.io.BufferedReader;\nimport java.io.IOException;\nimport java.io.InputStream;\nimport java.io.InputStreamReader;\n");
				context.put("methods", this.generateOperations(operations));
				methodsFilledIn = true;
			}
			else
				context.put("methods", this.generateOperationsHeader(operations,false));
			context.put("importTypes", imports);
			context.put("operations", operations);
			context.put("portName",portName);

			context.put("webServiceName", this.webServiceName);
			context.put("methodsFilledIn", methodsFilledIn);
			t.merge( context, writer );
			writer.close();
		}  catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating OpsImpl resource");
			e.printStackTrace();
		}
	}

	private String generateReturnForComplexType(Types p) {
		String returnLine = "return new "+p.getType()+"(";
		int i = 0;
		for(Types subType : p.getSequence()) {
			if(!subType.isBasicType())
				returnLine = returnLine.concat(generateReturnForComplexType(subType));
			else
				returnLine = returnLine.concat(this.conversion.getConvertionOfType(this.typesHandler.getCorrectType(this.language, subType.getType()), "result.get(\""+subType.getTypeName()+"\")"));
			if(i == p.getSequence().size()-1)
				returnLine = returnLine.concat(");");
			else
				returnLine = returnLine.concat(",");
			i++;	
		}
		return returnLine;

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
				headers = headers.concat( "{\n\t\tHashtable args = new Hashtable();\n\t\tDictionary result = null;\n\t\t");
				for(Parts inputArg : ot.getInputParts())
					headers = headers.concat("args.put(\""+inputArg.getName()+"\","+inputArg.getName()+");\n\t\t");
				headers = headers.concat("try{\n\t\t\tresult = this."+ot.getOpName().toLowerCase()+".invoke(args);\n\t\t}catch(Exception e){\n\t\t\te.printStackTrace();\n\t\t}");
				String correctType = this.typesHandler.getCorrectType(this.language, ot.getOutputType());
				if(this.typesHandler.getTypes().containsKey(correctType)){//complextype
					Types type = this.typesHandler.getTypes().get(correctType);
					headers = headers.concat(generateReturnForComplexType(type));
					headers = headers.concat("");
				}
				else
					if(!(ot.getOutputType().equals("void")))
						headers = headers.concat("return "+this.conversion.getConvertionOfType(this.typesHandler.getCorrectType(this.language, ot.getOutputType()), "result.get(\""+ot.getOutputName()+"\")")+";");
				headers = headers.concat("\n\t}\n\n");


			}
			else {
				headers = headers.concat("\tpublic "+outType+" "+ot.getOpName()+"( )");
				headers = headers.concat("{\n\n");
				headers = headers.concat("Dictionary result = null;\n\t\t");
				headers = headers.concat("try{\n\t\t\tresult = this."+ot.getOpName().toLowerCase()+".invoke(args);\n\t\t}catch(Exception e){\n\t\t\te.printStackTrace();\n\t\t}");
				String correctType = this.typesHandler.getCorrectType(this.language, ot.getOutputType());
				if(this.typesHandler.getTypes().containsKey(correctType)){//complextype
					Types type = this.typesHandler.getTypes().get(correctType);
					headers = headers.concat(generateReturnForComplexType(type));
					headers = headers.concat("");
				}
				else
					if(!(ot.getOutputType().equals("void")))
						headers = headers.concat("return "+this.conversion.getConvertionOfType(this.typesHandler.getCorrectType(this.language, ot.getOutputType()), "result.get(\""+ot.getOutputName()+"\")")+";");
				headers = headers.concat("\n\t}\n\n");
			}
		}
		return headers;
	}

	private String generateOperationsHeader(List<OperationType> operations, boolean isInterface) {

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
				if(!isInterface)
					headers = headers.concat( "{"+ot.getResultLine()+"}\n\n");
				else 
					headers = headers.concat(";\n\n");

			}
			else {
				headers = headers.concat("\tpublic "+outType+" "+ot.getOpName()+"( )");
				if(!isInterface) 
					headers = headers.concat("{}\n\n");
				else 
					headers = headers.concat(";\n\n");
			}
		}
		return headers;
	}


	private void generateParserResource(List<OperationType> operations) {

		try {
			String portName = operations.get(0).getOperationPort();
			Template t;
			t = engine.getTemplate( "resources/Parsers/Parser.vm" );
			VelocityContext context = new VelocityContext();
			context.put("package", "eu.linksmart.limbo");
			context.put("class", portName+"Parser");
			context.put("operations", operations);
			context.put("language", this.language);
			context.put("methods", this.generateParsingOperations(operations,true));
			if(this.handlingTypes)
				context.put("importTypes", "import eu.linksmart.limbo.types.*;");
			else
				context.put("importTypes", "");
			String directory = outputDirectory + "/"+webServiceName+"Server/src/" + context.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + context.get("class") + ".java"));
			t.merge( context, writer );
			writer.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating parser resource");
			e.printStackTrace();
		}
	}

	private void generateSOAPHandlerResource(List<OperationType> operations) {

		try {
			String portName = operations.get(0).getOperationPort();
			Template tHandler;
			tHandler = engine.getTemplate("/resources/Handlers/SOAPHandler.vm");
			VelocityContext ctxtHandler = new VelocityContext();
			ctxtHandler.put("package", "eu.linksmart.limbo.handler");
			ctxtHandler.put("OperationsImpl", portName+"OpsImpl");
			ctxtHandler.put("class", portName+"SOAPHandler");
			ctxtHandler.put("service", portName+"Service");
			ctxtHandler.put("handlers", portName+"Handlers");
			ctxtHandler.put("handler", portName+"Handler");
			ctxtHandler.put("hasTypes", this.handlingTypes);
			ctxtHandler.put("operations", operations);
			ctxtHandler.put("nOperations",  operations.size());
			ctxtHandler.put("Parser", portName+"Parser");
			String directory = outputDirectory + "/"+webServiceName+"Server/src/" + ctxtHandler.get("package").toString().replace('.', '/');
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + ctxtHandler.get("class") + ".java"));
			tHandler.merge(ctxtHandler, writer);
			writer.close();
		}catch(Exception ex) {
			log.log(Level.SEVERE, "Error generating SOAPHandler resource");
			ex.printStackTrace();
		}

	}

	private String generateParsingOperations(List<OperationType> operations,boolean inputMessage){
		String parsingOperations = "";

		LinkedList<String> parsedOperations = new LinkedList<String>();
		Iterator<OperationType> it = operations.iterator();
		while(it.hasNext()){
			OperationType ot = it.next();
			if(!parsedOperations.contains(ot.getOpName())) {

				Iterator<OperationType> auxIterator = operations.iterator();
				LinkedList<OperationType> opsWithSameName = new LinkedList<OperationType>();
				while(auxIterator.hasNext()) {
					OperationType otAux = auxIterator.next();
					if(otAux.getOpName().equals(ot.getOpName())){
						if(opsWithSameName.size() == 0)
							opsWithSameName.add(otAux);
						else {
							boolean inserted = false;
							int i;
							for(i = 0; i < opsWithSameName.size(); i++) {
								if(opsWithSameName.get(i).getInputParts().size() < otAux.getInputParts().size()) {
									opsWithSameName.add(i, otAux);
									inserted = true;
									break;
								}
							}
							if(!inserted)
								opsWithSameName.add(i, otAux);

						}

					}
				}
				parsedOperations.add(ot.getOpName());

				Iterator<OperationType> parseOperations = opsWithSameName.iterator();
				parsingOperations = parsingOperations.concat("\tpublic Vector" + " " + ot.getOpName() + "(String subRequest){\n\t\t");
				parsingOperations = parsingOperations.concat("\n\t\tint nArgs;\n\t\tVector V;\n\t\tString result = null;\n\t\tString before;\n\t\tString after;\n\t\tint index = subRequest.lastIndexOf('<');\n\t\tStringTokenizer st;\n" +
				"\t\tsubRequest = subRequest.substring(0, index);");
				while(parseOperations.hasNext()) {
					ot = parseOperations.next();
					if(inputMessage) {
						parsingOperations = parsingOperations.concat("\n\t\tnArgs = "+ot.getInputParts().size()+";\n\t\tV = new Vector(nArgs);\n\t\t");
						parsingOperations = parsingOperations.concat("before ="+'"'+ ot.getOpName()+'"'+";\n\t\tafter = "+'"'+ot.getOpName()+">"+'"'+";\n");
						if(ot.getInputParts().size() == 0) {
							parsingOperations = parsingOperations.concat("\n\t\tif((subRequest.indexOf("+'"'+ot.getOpName()+'"'+")!=-1) && (subRequest.endsWith(\""+'/'+">\"))) {");
							parsingOperations = parsingOperations.concat("\n\t\t\tresult = \"\";");
							parsingOperations = parsingOperations.concat("\n\t\t\tthis.nrArgs= nArgs;");
							parsingOperations = parsingOperations.concat("\n\t\t\treturn V;\n\t\t}\n");
						}
					}
					else {
						int nArgs = 1;
						if(ot.getOutputType().equals("void"))
							nArgs = 0;
						parsingOperations = parsingOperations.concat("\n\t\tnArgs = "+nArgs+";\n\t\tV = new Vector(nArgs);\n\t\t");
						parsingOperations = parsingOperations.concat("before ="+'"'+ot.getOutput()+'"'+";\n\t\t");
						parsingOperations = parsingOperations.concat("after = "+'"'+ot.getOutput()+">"+'"'+";\n");
						if(ot.getOutputType() == "void") {
							parsingOperations = parsingOperations.concat("\n\t\tif((subRequest.indexOf("+'"'+ot.getOutput()+'"'+")!=-1) && (subRequest.endsWith(\""+'/'+">\"))) {");
							parsingOperations = parsingOperations.concat("\n\t\t\tresult = \"\";");
							parsingOperations = parsingOperations.concat("\n\t\t\tthis.nrArgs= nArgs;");
							parsingOperations = parsingOperations.concat("\n\t\t\treturn V;\n\t\t}");
						}
					}
					parsingOperations = parsingOperations.concat("\t\tif((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {");
					if(inputMessage) {
						List<Parts> vp = ot.getInputParts();
						if(vp.size() !=0) {
							parsingOperations = parsingOperations.concat("\n\t\tif((subRequest.indexOf(\"");
							int i = 0; 
							for(Parts part : vp) {
								if(i == vp.size()-1)
									parsingOperations = parsingOperations.concat("<"+part.getName()+"\",0)!=-1)) {");
								else {
									parsingOperations = parsingOperations.concat("<"+part.getName()+"\",0)!=-1) && (subRequest.indexOf(\"");
									i++;
								}
							}
						}
					}
					parsingOperations = parsingOperations.concat("\n\t\t\tst = new StringTokenizer(subRequest,"+'"'+">"+'"'+");\n" +
							"\t\t\tbefore = st.nextToken();\n\t\t\tbefore = before.concat("+'"'+">"+'"'+");\n" +
					"\t\t\tresult = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));");
					if(inputMessage){
						for(int p=0;p<ot.getInputParts().size();p++){
							if(p == ot.getInputParts().size()-1){
								parsingOperations = parsingOperations.concat("\n\t\t\tbefore = "+'"'+ "<"+ot.getInputParts().get(p).getName() +'"'+";\n"+
										"\t\t\tafter = "+'"'+"</"+ot.getInputParts().get(p).getName()+">"+'"'+";\n" +
										"\t\t\tresult = result.substring(before.length());\n\t\t\tindex = result.indexOf('>');"+
								"\n\t\t\tresult = result.substring(index+1);\n\t\t\tindex = result.indexOf(after);\n\t\t\tV.addElement(result.substring(0, index));");
							}
							else{

								parsingOperations = parsingOperations.concat("\n\t\t\tbefore = "+'"'+ "<"+ot.getInputParts().get(p).getName()+'"'+";\n"+
										"\t\t\tafter = "+'"'+"</"+ot.getInputParts().get(p).getName()+">"+'"'+";\n" +
										"\t\t\tresult = result.substring(before.length());\n\t\t\tindex = result.indexOf('>');"+
										"\n\t\t\tresult = result.substring(index+1);\n\t\t\tindex = result.indexOf(after);\n\t\t\tV.addElement(result.substring(0, index));"+
										"\n\t\t\tresult = result.substring(index);"+
								"\n\t\t\tresult = result.substring(after.length());");
							}

						}
					}else {
						if(!ot.getOutputType().equals("void"))
							parsingOperations = parsingOperations.concat("\n\t\t\tbefore = "+'"'+ "<"+ot.getOutputName() +'"'+";\n"+
									"\t\t\tafter = "+'"'+"</"+ot.getOutputName() +">"+'"'+";\n" +
									"\t\t\tresult = result.substring(before.length());\n\t\t\tindex = result.indexOf('>');"+
									"\n\t\t\tresult = result.substring(index+1);\n\t\t\tindex = result.lastIndexOf('<');\n\t\t\tV.addElement(result.substring(0, index));"+
									"\n\t\t\tresult = result.substring(index);"+
							"\n\t\t\tresult = result.substring(after.length());");
					}
					parsingOperations = parsingOperations.concat("\n\t\t\tthis.nrArgs = nArgs;\n\t\t\treturn V;\n\t\t}"); 
					if(inputMessage && ot.getInputParts().size()!=0) 
						parsingOperations = parsingOperations.concat("\n\t\t}");
				}
				parsingOperations = parsingOperations.concat("\n\t\treturn null;\n\t}\n");
			}
		}
		return parsingOperations;
	}

	@Reference
	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void unsetRepository(Repository repository) {
		this.repository = null;
	}
}
