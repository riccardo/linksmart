/*
 * Copyright (c) 2003-2008, KNOPFLERFISH project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 *
 * - Neither the name of the KNOPFLERFISH project nor the names of its
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.linksmart.wsprovider.impl;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.XMLUtils;
import org.apache.log4j.Logger;


/**
 * An Axis SOAP service representation of an arbitrary object
 *
 * @author Lasse Helander (lars-erik.helander@home.se)
 */
public class ObjectSOAPService {

	private Logger log = Logger.getLogger(ObjectSOAPService.class.getName());
	private AxisServer axisServer = null;
	private String serviceName = null;
	private Object serviceObject = null;
	private boolean security = false;
	String allowedMethods;
	Hashtable typeset = new Hashtable();

	private String[] classNames;
	private static Set<String> baseClasses;
	private static Set<String> handledClasses;
	
	static {
		handledClasses = new HashSet<String>();
		baseClasses = new HashSet<String>();
		baseClasses.add("byte");
		baseClasses.add("boolean");
		baseClasses.add("double");
		baseClasses.add("float");
		baseClasses.add("int");
		baseClasses.add("long");
		baseClasses.add("short");
		baseClasses.add("void");
		baseClasses.add("java.util.Calendar");
		baseClasses.add("java.math.BigDecimal");
		baseClasses.add("java.math.BigInteger");
		baseClasses.add("javax.xml.namespace.QName");
		baseClasses.add("java.lang.String");
		baseClasses.add("java.lang.Object");
		baseClasses.add("java.lang.Class");
		baseClasses.add("java.util.Vector");
		baseClasses.add("java.security.PublicKey");
	}

	/**
	 * Constructor
	 * @param server the server
	 * @param serviceName the service name
	 * @param serviceObject the service object
	 * @param classNames the class names
	 * @param allowedMethods the allowed methods
	 * @param coreSecurity the core security
	 */
	public ObjectSOAPService(AxisServer server, String serviceName,
			Object serviceObject, String[] classNames, String allowedMethods,
			boolean coreSecurity) {
		
		this.serviceObject = serviceObject;
		this.serviceName = serviceName;
		this.axisServer = server;
		this.classNames = classNames;
		this.security = coreSecurity;
		
		if (serviceObject == null) {
			log.error("Unable to create SOAP Service for " + serviceName);
		}
		
		if(allowedMethods == null) {
			this.allowedMethods = getMethodNames(classNames);
		}
		else {
			this.allowedMethods = allowedMethods;
		}
	}
	
	/**
	 * Get all method names from a set of classes, except for the
	 * methods names in java.lang.Object.
	 *
	 * @param classNames array of class names. Each class object will
	 * be created using Class.forName
	 * @return the method names
	 */
	String getMethodNames(String[] classNames) {
		StringBuffer sb = new StringBuffer();
	
		try {
			Class objectClass = Object.class;
	
			for(int i = 0; i < classNames.length; i++) {
				Class clazz = Class.forName(classNames[i]);
				Method[] methods = clazz.getMethods();
	
				for(int j = 0; j < methods.length; j++) {
					boolean bInObject = false;
					try {
						objectClass.getMethod(methods[i].getName(),
							methods[i].getParameterTypes());
						bInObject = true;
					} catch (Exception ignored) { }
	
					if(!bInObject) {
						if(sb.length() > 0) {
							sb.append(" ");
						}
						sb.append(methods[j].getName());
					}
				}
			}
			return sb.toString();
		} catch (Exception e) {
			log.error("Failed to analyze methods in service object: "
				+ serviceObject.getClass().getName(), e);
		}
		return "*";
	}
		
	/**
	 * Get all objects used in the methods of the serviceObject that do not
	 * have a standard SOAP mapping.
	 *
	 * @return a HashSet containing all unmapped object classes
	 */
	private HashSet getMethodObjects() {
		HashSet classset = new HashSet();
		HashSet allowedMethodSet = new HashSet();
	
		StringTokenizer st = new StringTokenizer(allowedMethods);
		while (st.hasMoreTokens()) allowedMethodSet.add(st.nextToken());
	
		try {
			Class objectClass = Object.class;
			Class clazz = serviceObject.getClass();
	
			Method[] methods= clazz.getMethods();
	
			for(int i = 0; i < methods.length; i++) {
				if(allowedMethodSet.contains(methods[i].getName())) {
					recursiveGetMethod(methods[i], classset);
				}
			}
		} catch (Exception e) {
			log.error("Failed to analyze methods: " + e.toString(), e);
		}
		
		allowedMethodSet.clear();
		
		// Remove all standard mappings from the set because they already have
		// a (de)serializer.
		classset.remove("byte");
		classset.remove("boolean");
		classset.remove("double");
		classset.remove("float");
		classset.remove("int");
		classset.remove("long");
		classset.remove("short");
		classset.remove("void");
		classset.remove("java.util.Calendar");
		classset.remove("java.math.BigDecimal");
		classset.remove("java.math.BigInteger");
		classset.remove("javax.xml.namespace.QName");
		classset.remove("java.lang.String");
		classset.remove("java.util.Vector");
		classset.remove("java.security.PublicKey");
		return classset;
	}
	
	/**
	 * Recursively parse each class provided, and add all classes
	 * Added to indlude typeMappings for nested non-primitive types
	 * 
	 * @param clazz class
	 * @param classset classset
	 */
	public void recursiveGetClasses(Class clazz, HashSet classset){
		
		if ((isBaseType(clazz.getName()) || isHandled(clazz.getName()))) {
			return;
		}
		else {
			handledClasses.add(clazz.getName());
		}
		
		Method[] methods = clazz.getMethods();
		for(int i = 0; i < methods.length; i++) {
			recursiveGetMethod(methods[i], classset);
		}
		
		classset.add(clazz.getName());
	}
	
	/**
	 * Recursive method
	 * 
	 * @param method the method
	 * @param classset classset
	 */
	public void recursiveGetMethod(Method method, HashSet classset) {
		Class params[] = method.getParameterTypes();
	
		for (int j = 0; j < params.length; j++) {
			String paramname = params[j].getName();
			if (!isBaseType(paramname)) {
				recursiveGetClasses(params[j], classset);
				if (params[j].isArray()) {
					recursiveGetClasses(params[j].getComponentType(), classset);
				}
				classset.add(paramname);
				if (params[j].isArray()) {
					classset.add(params[j].getComponentType().getName());
				}
			}
		}
	
		Class returnClazz = method.getReturnType();
		String returnname = returnClazz.getName();
		if ((!isBaseType(returnname)) && (!isHandled(returnname))) {
			classset.add(returnname);
			if (method.getReturnType().isArray()) {
				classset.add(returnClazz.getComponentType().getName());
			}
			recursiveGetClasses(returnClazz, classset);
			if (method.getReturnType().isArray()) {
				recursiveGetClasses(returnClazz.getComponentType(), classset);
			}
		}
	}
	
	/**
	 * Return true if is base type
	 * 
	 * @param type the type to check
	 * @return true if is base type
	 */
	private static boolean isBaseType(String type){
		return baseClasses.contains(type);
	}
	
	/**
	 * Return true if is handled
	 * 
	 * @param type the type to check
	 * @return true if is handled
	 */
	private static boolean isHandled(String type) {
		return handledClasses.contains(type);
	}
	
	/**
	 * Deploys a WS
	 */
	public void deploy() {
		Object obj = axisServer.getApplicationSession().get(serviceName);
		
		if (obj == null) {
			deployWSDD(deployDoc());
			axisServer.getApplicationSession().set(serviceName, serviceObject);
			log.info("Deployed object=" + serviceObject.getClass().getName()
				+ "\nService Name=" + serviceName 
				+ "\nAllowedMethods=" + allowedMethods + "\n");
		}
		else {
			log.error("ObjectSOAPService::deploy() service " + serviceName 
				+ " do already exist");
		}
	}
	
	/**
	 * Undeploys a WS
	 */
	public void undeploy() {
		Object obj = axisServer.getApplicationSession().get(serviceName);
		
		if (obj == null) {
			log.error("ObjectSOAPService::undeploy() service " + serviceName 
				+ " does not exist");
		}
		else {
			deployWSDD(undeployDoc());
			axisServer.getApplicationSession().remove(serviceName);
			log.info("undeployed object=" + serviceObject.getClass().getName()
				+ ", name=" + serviceName);
		}
	}
	
	/**
	 * Creates a deploy document
	 * 
	 * @return string the deploy document
	 */
	private String deployDoc() {
		StringBuffer sb = new StringBuffer();
		HashSet classet = getMethodObjects();
		Iterator it =  classet.iterator();
		
		while (it.hasNext()) {
			String classname  = (String) it.next();
			int qnamestart = classname.lastIndexOf('.');
			if (qnamestart < 0) {
				qnamestart = 0;
			}
			else {
				qnamestart++;
			}
			
			try {
				Class mybean = Class.forName(classname);
				if (mybean.isArray()) {
					Class component = mybean.getComponentType();
					String componentName = component.getSimpleName();
					if (!classet.contains(component.getName())) continue;
					String namespace = 
						org.apache.axis.wsdl.fromJava.Namespaces.makeNamespace(mybean.getName());
					String componentNamespace = 
						org.apache.axis.wsdl.fromJava.Namespaces.makeNamespace(component.getName());
					
					int lastDot = component.getName().lastIndexOf('.');
					if (lastDot == -1) {
						return "";
					}
					
					String packageName =  component.getName().substring(0, lastDot);
					
					sb.append("<arrayMapping xmlns:ns=\"" + namespace + "\" "
						+ "qname=\"ns:" + componentName + "s\" "
						+ "type=\"java:" + packageName + "." + mybean.getSimpleName() + "\" " 
						+ "innerType=\"cmp-ns:" + componentName + "\" "
						+ "xmlns:cmp-ns=\"" + componentNamespace + "\" "
						+ "encodingStyle=\"\" />\n");
				}
				else {
					String namespace = 
						org.apache.axis.wsdl.fromJava.Namespaces.makeNamespace(mybean.getName());
					
					sb.append("<typeMapping xmlns:ns=\"" + namespace + "\" "
						+ "qname=\"ns:" + classname.substring(qnamestart) + "\" "
						+ "type=\"java:" + classname + "\" "
						+ "serializer=\"org.apache.axis.encoding.ser.BeanSerializerFactory\" "
						+ "deserializer=\"org.apache.axis.encoding.ser.BeanDeserializerFactory\" "
						+ "encodingStyle=\"\" />\n");
				}
			} catch (Exception e) {
				log.error("Failed to get parameter class: "  + classname, e);
			}
		}

		String className = "";
		try {
			className = Class.forName(classNames[0]).getName();
		} catch (ClassNotFoundException e) {
			log.error(e);
		}
		
		String namespace;
		if (className != "") {
			namespace = "<namespace>"
				+ org.apache.axis.wsdl.fromJava.Namespaces.makeNamespace(className) 
				+ "</namespace>\n";
		}
		else {
			namespace = "";
		}
	
		String wsdd = "<deployment"
			+ " xmlns=\"http://xml.apache.org/axis/wsdd/\"\n"
			+ " xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\"\n"
			+ ">\n"
			+ " <service name=\"" + serviceName + "\"\n"
			+ " provider=\"java:RPC\" use=\"literal\">\n"
			+ " <parameter name=\"allowedMethods\"\n"
			+ " value=\"" + allowedMethods + "\"/>\n"
			+ " <parameter name=\"className\"\n"
			+ " value=\"" + classNames[0] + "\"/>\n"
			+ " <parameter name=\"scope\"\n"
			+ " value=\"Application\"/>\n"
			+ " <parameter name=\"typeMappingVersion\" value=\"1.2\"\n/>"
			+ namespace + sb.toString() + getLinkSmartSecurityConfiguration()
			+ " </service>\n"
			+ "</deployment>";
		return wsdd;
	}
	
	/**
	 * Deploys a WS from a WSDD document
	 * 
	 * @param sdoc de WSDD document
	 */
	private void deployWSDD(String sdoc) {
		try {
			WSDDEngineConfiguration config = 
				(WSDDEngineConfiguration) axisServer.getConfig();
			WSDDDeployment deployment = config.getDeployment();
			WSDDDocument doc = new WSDDDocument(XMLUtils.newDocument(
				new ByteArrayInputStream(sdoc.getBytes())));
			
			doc.deploy(deployment);
			axisServer.refreshGlobalOptions();
		} catch (Exception e) {
			log.error("ObjectSOAPService::deployWSDD() exception", e);
		}
	}

	/**
	 * Creates an undeploy document
	 * 
	 * @return string the undeploy document
	 */
	private String undeployDoc() {
		return "<undeployment xmlns=\"http://xml.apache.org/axis/wsdd/\">" 
			+ "<service name=\"" + serviceName + "\"/>" + "</undeployment>";
	}
	
	/**
	 * Gets the service object
	 * 
	 * @return the service object
	 */
	public Object getServiceObject() {
		return serviceObject;
	}
	
	/**
	 * Method that generates the security deployment code in case the OSGi
	 * service has been configured for doing it
	 * 
	 * @return string
	 */
	public String getLinkSmartSecurityConfiguration() {
		String config = "";
		
		if (this.security) {
			config = "<requestFlow>\n"
				+ "<handler type=\"java:eu.linksmart.security.axis.CoreSecurityRequestHandler\">\n"
				+ "<parameter name=\"scope\" value=\"session\" />\n"
				+ "</handler>\n"
				+ "</requestFlow>\n"
				+ "<responseFlow>\n"
				+ "<handler type=\"java:eu.linksmart.security.axis.CoreSecurityResponseHandler\">\n"
				+ "<parameter name=\"scope\" value=\"session\" />\n"
				+ "</handler>\n"
				+ "</responseFlow>\n";
		}
		
		return config;
	}
	
}
