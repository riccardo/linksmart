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
package eu.linksmart.limbo.eclipse;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import eu.linksmart.limbo.constants.LimboConstants;
import eu.linksmart.limbo.backend.Backend;
import eu.linksmart.limbo.repository.Repository;
import eu.linksmart.limbo.library.OSGiResourceLoader;

@Component
public class EclipseProjectBackend implements Backend {

	private Repository repository;
	private String language;
	private String generationType;
	private String serverType;
	private String webServiceName;
	private String outputDirectory;
	private String transportProtocol;
	private VelocityEngine engine = new VelocityEngine();
	private ComponentContext context; 
	private String upnp;


	protected void activate(ComponentContext ctxt)  {
		this.context = ctxt;
	}

	public void generate() throws Exception {

		engine.setProperty("resource.loader", "mine"); 
		engine.setProperty("mine.resource.loader.instance", new OSGiResourceLoader(this.context.getBundleContext().getBundle()));

		this.language = (String)this.repository.getParameter(LimboConstants.LANGUAGE);
		this.upnp = (String)this.repository.getParameter(LimboConstants.UPNP);

		this.generationType = (String)this.repository.getParameter(LimboConstants.GENERATIONTYPE);
		this.serverType = (String)this.repository.getParameter(LimboConstants.PLATFORM);
		this.transportProtocol = (String)this.repository.getParameter(LimboConstants.PROTOCOL);
		String wsdlFileName = this.repository.getWSDLFile().getName();
		StringTokenizer st = new StringTokenizer(wsdlFileName,".");
		this.webServiceName = st.nextToken();
		this.outputDirectory = (String)this.repository.getParameter(LimboConstants.OUTPUTDIRECTORY);
		if((this.generationType.equals(LimboConstants.SERVER.toString())) || (this.generationType.equals(LimboConstants.ALL.toString())))
			generateServerProjectResources();
		if((this.generationType.equals(LimboConstants.CLIENT.toString())) || (this.generationType.equals(LimboConstants.ALL.toString())))
			generateClientProjectResources();

	}

	@Reference
	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void unsetRepository(Repository repository) {
		this.repository = null;
	}

	private void generateServerProjectResources() {
		if(this.language.equals(LimboConstants.JME.toString()))
			generateJMEServerResources();

		if((this.language.equals(LimboConstants.JSE.toString())) && (this.serverType.equals(LimboConstants.STANDALONE.toString())))
			generateJSEStandaloneServerResources();

		if((this.language.equals(LimboConstants.JSE.toString())) && (this.serverType.equals(LimboConstants.OSGI.toString())))
			generateOSGiServerResources();
	}

	private void generateClientProjectResources() {
		if(this.language.equals(LimboConstants.JME.toString()))
			generateJMEClientResources();
		if(this.language.equals(LimboConstants.JSE.toString()))
			generateJSEClientResources();
	}

	private void generateOSGiServerResources() {
		//VelocityEngine ve = new VelocityEngine();
		File lib = new File(outputDirectory+ "/" + webServiceName+"Server/lib");
		lib.mkdirs();
		try {
			copyFileFromInputStream(EclipseProjectBackend.class.getResourceAsStream("/resources/jars/org.eclipse.osgi_3.3.0.v20070530.jar"), new File(outputDirectory + "/"+webServiceName+"Server/lib/org.eclipse.osgi_3.3.0.v20070530.jar"));
			copyFileFromInputStream(EclipseProjectBackend.class.getResourceAsStream("/resources/jars/javax.servlet_2.4.0.v200706061611.jar"), new File(outputDirectory + "/"+webServiceName+"Server/lib/javax.servlet_2.4.0.v200706061611.jar"));
			copyFileFromInputStream(EclipseProjectBackend.class.getResourceAsStream("/resources/jars/org.eclipse.osgi.services_3.1.200.v20070605.jar"), new File(outputDirectory + "/"+webServiceName+"Server/lib/org.eclipse.osgi.services_3.1.200.v20070605.jar"));
			if(this.upnp.equals("true"))
				copyFileFromInputStream(EclipseProjectBackend.class.getResourceAsStream("/resources/jars/org.apache.felix.upnp.extra-0.3.0.jar"), new File(outputDirectory + "/"+webServiceName+"Server/lib/org.apache.felix.upnp.extra-0.3.0.jar"));
			Template t = engine.getTemplate( "resources/Project/PluginClasspath.vm" );
			VelocityContext context = new VelocityContext();
			String directory = outputDirectory + "/"+webServiceName+"Server";
			if(this.transportProtocol.equals(LimboConstants.BLUETOOTH.toString())) {
				directory = outputDirectory + "/"+webServiceName+"Server/lib";
				new File(directory).mkdirs();

				copyFileFromInputStream(EclipseProjectBackend.class.getResourceAsStream("/resources/jars/bluecove-2.0.3.jar"), new File(outputDirectory + "/"+webServiceName+"Server/lib/bluecove-2.0.3.jar"));
				String libraries = "\t<classpathentry kind=\"lib\" path=\"lib/bluecove-2.0.3.jar\"/>\n\t" +
				"<classpathentry kind=\"lib\" path=\"lib/org.eclipse.osgi.services_3.1.200.v20070605.jar\"/>\n\t" +
				"<classpathentry kind=\"lib\" path=\"lib/javax.servlet_2.4.0.v200706061611.jar\"/>\n\t" +
				"<classpathentry kind=\"lib\" path=\"lib/org.eclipse.osgi_3.3.0.v20070530.jar\"/>";
				if(this.upnp.equals("true")) 
					libraries = libraries.concat("\n\t<classpathentry kind=\"lib\" path=\"lib/org.apache.felix.upnp.extra-0.3.0.jar\"/>");
				context.put("lib", libraries);
			}
			else {
				String libraries =  "<classpathentry kind=\"lib\" path=\"lib/org.eclipse.osgi.services_3.1.200.v20070605.jar\"/>\n\t" +
				"<classpathentry kind=\"lib\" path=\"lib/javax.servlet_2.4.0.v200706061611.jar\"/>\n\t" +
				"<classpathentry kind=\"lib\" path=\"lib/org.eclipse.osgi_3.3.0.v20070530.jar\"/>";
				if(this.upnp.equals(true)) 
					libraries = libraries.concat("\n\t<classpathentry kind=\"lib\" path=\"lib/org.apache.felix.upnp.extra-0.3.0.jar\"/>");
				context.put("lib",libraries);
			}
			directory = outputDirectory + "/"+webServiceName+"Server";
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/.classpath" ));
			t.merge( context, writer );
			writer.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}
		try {
			Template t = engine.getTemplate( "resources/Project/PluginProject.vm" );
			VelocityContext context = new VelocityContext();
			context.put("projectName", webServiceName+"Server");
			String directory = outputDirectory + "/"+webServiceName+"Server";
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/.project" ));
			t.merge( context, writer );
			writer.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Plug-in build.properties
		try {
			Template t = engine.getTemplate( "resources/Project/PluginBuild.vm" );
			VelocityContext context = new VelocityContext();
			String bin ="";
			if(!this.transportProtocol.equals(LimboConstants.BLUETOOTH.toString())) 
				bin = "META-INF/,\\\n\t.,\\\n\tlib/org.eclipse.osgi_3.3.0.v20070530.jar,\\\n\t" +
				"lib/org.eclipse.osgi.services_3.1.200.v20070605.jar,\\\n\tlib/javax.servlet_2.4.0.v200706061611.jar";

			else 
				bin = "META-INF/,\\\n\t.,\\\n\tlib/bluecove-2.0.3.jar,\\\n\tlib/org.eclipse.osgi_3.3.0.v20070530.jar,\\\n\t" +
				"lib/org.eclipse.osgi.services_3.1.200.v20070605.jar,\\\n\tlib/javax.servlet_2.4.0.v200706061611.jar";
			if(this.upnp.equals("true"))
				bin = bin.concat(",\\\n\tlib/org.apache.felix.upnp.extra-0.3.0.jar");

			context.put("bin", bin);
			String directory = outputDirectory + "/"+webServiceName+"Server";
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/build.properties" ));
			t.merge( context, writer );
			writer.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Plug-in MANIFEST
		try {
			Template t = engine.getTemplate( "resources/Project/Manifest.vm" );
			VelocityContext context = new VelocityContext();
			String bundleName = webServiceName.substring(0, 1);
			bundleName = bundleName.toUpperCase();
			bundleName = bundleName.concat(webServiceName.substring(1, webServiceName.length()));
			bundleName = bundleName.concat("Server Plug-in");
			context.put("bundle_name", bundleName);
			context.put("project", webServiceName+"Server");
			//Imports
			String imports = "";
			String exports = "";
			String classpath = "";
			if(!this.transportProtocol.equals(LimboConstants.BLUETOOTH.toString())) {
				imports =  "Import-Package: javax.servlet,\n javax.servlet.http,\n org.osgi.framework,\n org.osgi.service.http,\n org.osgi.util.tracker";
				exports = "\nExport-Package: eu.linksmart.limbo,\n eu.linksmart.limbo.handler";
				if(this.upnp.equals("true")) {
					imports = imports.concat(",\n org.osgi.service.device,\n org.osgi.service.upnp,\n org.apache.felix.upnp.extra.util");
					exports = exports.concat(",\n eu.linksmart.limbo.upnp");
					classpath = "\nBundle-ClassPath: lib/javax.servlet_2.4.0.v200706061611.jar,\n" +
					" lib/org.eclipse.osgi.services_3.1.200.v20070605.jar,\n lib/org.eclipse.osgi_3.3.0.v20070530.jar,\n lib/org.apache.felix.upnp.extra-0.3.0.jar,\n .";
				}
				else
					classpath = "\nBundle-ClassPath: lib/javax.servlet_2.4.0.v200706061611.jar,\n" +
					" lib/org.eclipse.osgi.services_3.1.200.v20070605.jar,\n lib/org.eclipse.osgi_3.3.0.v20070530.jar,\n .";
			}
			else {
				imports = "Import-Package: com.ibm.oti.connection.btgoep,\n com.ibm.oti.connection.btl2cap,\n com.ibm.oti.connection.btspp,\n" +
				" com.ibm.oti.connection.tcpobex,\n com.intel.bluetooth,\n com.intel.bluetooth.btgoep,\n com.intel.bluetooth.btl2cap,\n" +
				" com.intel.bluetooth.btspp,\n com.intel.bluetooth.gcf.socket,\n com.intel.bluetooth.obex,\n com.intel.bluetooth.tcpobex,\n" +
				" com.sun.cdc.io.j2me.btgoep,\n com.sun.cdc.io.j2me.btl2cap,\n com.sun.cdc.io.j2me.btspp,\n com.sun.cdc.io.j2me.tcpobex,\n" +
				" javax.bluetooth,\n javax.microedition.io,\n javax.obex,\n javax.servlet,\n javax.servlet.http,\n org.osgi.framework,\n" +
				" org.osgi.service.http,\n org.osgi.util.tracker";
				exports = "\nExport-Package: com.ibm.oti.connection.btgoep,\n com.ibm.oti.connection.btl2cap,\n com.ibm.oti.connection.btspp,\n" +
				" com.ibm.oti.connection.tcpobex,\n com.intel.bluetooth,\n com.intel.bluetooth.btgoep,\n com.intel.bluetooth.btl2cap,\n" +
				" com.intel.bluetooth.btspp,\n com.intel.bluetooth.gcf.socket,\n com.intel.bluetooth.obex,\n com.intel.bluetooth.tcpobex,\n" +
				" com.sun.cdc.io.j2me.btgoep,\n com.sun.cdc.io.j2me.btl2cap,\n com.sun.cdc.io.j2me.btspp,\n com.sun.cdc.io.j2me.tcpobex,\n" +
				" javax.bluetooth,\n javax.microedition.io,\n javax.obex,\n eu.linksmart.limbo,\n eu.linksmart.limbo.handler";
				if(this.upnp.equals("true")) {
					imports = imports.concat(",\n org.osgi.service.device,\n org.osgi.service.upnp,\n org.apache.felix.upnp.extra.util");
					exports = exports.concat(",\n eu.linksmart.limbo.upnp");
					classpath = "\nBundle-ClassPath: lib/bluecove-2.0.3.jar,\n lib/javax.servlet_2.4.0.v200706061611.jar,\n" +
					" lib/org.eclipse.osgi.services_3.1.200.v20070605.jar,\n lib/org.eclipse.osgi_3.3.0.v20070530.jar,\n lib/org.apache.felix.upnp.extra-0.3.0.jar,\n .";
				}
				else
					classpath = "\nBundle-ClassPath: lib/bluecove-2.0.3.jar,\n lib/javax.servlet_2.4.0.v200706061611.jar,\n" +
					" lib/org.eclipse.osgi.services_3.1.200.v20070605.jar,\n lib/org.eclipse.osgi_3.3.0.v20070530.jar,\n .";

			}
			String impExpClass = imports+exports+classpath;
			context.put("impExpClass", impExpClass);
			if(this.upnp.equals("true"))
				context.put("activator", "eu.linksmart.limbo.upnp."+webServiceName+"UPnPActivator");
			else
				context.put("activator", "eu.linksmart.limbo."+webServiceName+"Activator");
			String directory = outputDirectory + "/"+webServiceName+"Server/META-INF";
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/MANIFEST.MF" ));
			t.merge( context, writer );
			writer.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void generateJSEStandaloneServerResources() {
		//VelocityEngine ve = new VelocityEngine();
		try {
			Template t = engine.getTemplate( "resources/Project/Project.vm" );
			VelocityContext context = new VelocityContext();
			context.put("projectName", webServiceName+"Server");
			String directory = outputDirectory + "/"+webServiceName+"Server";
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/.project" ));
			t.merge( context, writer );
			writer.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Template t = engine.getTemplate( "resources/Project/Classpath.vm" );
			VelocityContext context = new VelocityContext();
			String directory = outputDirectory + "/"+webServiceName+"Server";
			if(this.transportProtocol.equalsIgnoreCase(LimboConstants.BLUETOOTH.toString())||this.transportProtocol.equalsIgnoreCase(LimboConstants.ALL.toString())) {
				//create lib, copy bluecove-2.0.3.jar and comm.jar to lib
				directory = outputDirectory + "/"+webServiceName+"Server/lib";
				new File(directory).mkdirs();
				copyFileFromInputStream(EclipseProjectBackend.class.getResourceAsStream("/resources/jars/bluecove-2.0.3.jar"), new File(outputDirectory + "/"+webServiceName+"Server/lib/bluecove-2.0.3.jar"));
				copyFileFromInputStream(EclipseProjectBackend.class.getResourceAsStream("/resources/jars/comm.jar"), new File(outputDirectory + "/"+webServiceName+"Server/lib/comm.jar"));
				if(this.upnp.equals("true")) {
					copyFileFromInputStream(EclipseProjectBackend.class.getResourceAsStream("/resources/jars/linksmartupnplib.jar"), new File(outputDirectory + "/"+webServiceName+"Server/lib/linksmartupnplib.jar"));
					context.put("lib", "\t<classpathentry kind=\"lib\" path=\"lib/bluecove-2.0.3.jar\"/>\n\t<classpathentry kind=\"lib\" path=\"lib/comm.jar\"/>\n\t<classpathentry kind=\"lib\" path=\"lib/linksmartupnplib.jar\"/>");
				}
				else
					context.put("lib", "\t<classpathentry kind=\"lib\" path=\"lib/bluecove-2.0.3.jar\"/>\n\t<classpathentry kind=\"lib\" path=\"lib/comm.jar\"/>");
			}
			else
				if(this.upnp.equals("true")) {
					directory = outputDirectory + "/"+webServiceName+"Server/lib";
					new File(directory).mkdirs();
					copyFileFromInputStream(EclipseProjectBackend.class.getResourceAsStream("/resources/jars/linksmartupnplib.jar"), new File(outputDirectory + "/"+webServiceName+"Server/lib/linksmartupnplib.jar"));
					context.put("lib", "\t<classpathentry kind=\"lib\" path=\"lib/linksmartupnplib.jar\"/>");
				}
				else
					context.put("lib", "");
			directory = outputDirectory + "/"+webServiceName+"Server";
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/.classpath" ));
			t.merge( context, writer );
			writer.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void generateJMEServerResources() {
		//Generation of JME server
		//VelocityEngine ve = new VelocityEngine();
		try {
			Template t = engine.getTemplate( "resources/Project/JMEProject.vm" );
			VelocityContext context = new VelocityContext();
			String directory = outputDirectory + "/"+webServiceName+"Server";
			context.put("project", webServiceName+"Server");
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/.project" ));
			t.merge( context, writer );
			writer.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Template t = engine.getTemplate( "resources/Project/JMEClasspath.vm" );
			VelocityContext context = new VelocityContext();
			String directory = outputDirectory + "/"+webServiceName+"Server";
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/.classpath" ));
			t.merge( context, writer );
			writer.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Template t = engine.getTemplate( "resources/Project/MTJ.vm" );
			VelocityContext context = new VelocityContext();
			String directory = outputDirectory + "/"+webServiceName+"Server";
			context.put("jad", webServiceName+"Server.jad");
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/.mtj" ));
			t.merge( context, writer );
			writer.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Template t = engine.getTemplate( "resources/Project/ApplicationDescriptor.vm" );
			VelocityContext context = new VelocityContext();
			String directory = outputDirectory + "/"+webServiceName+"Server";
			context.put("jar", webServiceName+"Server.jar");
			context.put("project", webServiceName+"Server");
			context.put("midletClass", "eu.linksmart.limbo.LimboServer");
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/Application Descriptor" ));
			t.merge( context, writer );
			writer.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String res = outputDirectory + "/"+webServiceName+"Server/res";
		new File(res).mkdirs();
		String settings = outputDirectory + "/"+webServiceName+"Server/.settings";
		new File(settings).mkdirs();
		Date d = new Date();
		try {
			Template t = engine.getTemplate( "resources/Project/Preferences.vm" );
			VelocityContext context = new VelocityContext();
			String directory = outputDirectory + "/"+webServiceName+"Server/.settings";
			context.put("date", "#"+d.toString());
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/org.eclipse.jdt.core.prefs" ));
			t.merge( context, writer );
			writer.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void generateJSEClientResources() {
		//Generate .classpath and .project files
		//	VelocityEngine ve = new VelocityEngine();
		try {
			Template t = engine.getTemplate( "resources/Project/Project.vm" );
			VelocityContext context = new VelocityContext();
			context.put("projectName", webServiceName+"Client");
			String directory = outputDirectory + "/"+webServiceName+"Client";
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/.project" ));
			t.merge( context, writer );
			writer.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Template t = engine.getTemplate( "resources/Project/Classpath.vm" );
			VelocityContext context = new VelocityContext();
			String directory = outputDirectory + "/"+webServiceName+"Client";
			//	System.out.println("this.transportProtocol  "+this.transportProtocol);
			if(this.transportProtocol.equalsIgnoreCase(LimboConstants.BLUETOOTH.toString())||this.transportProtocol.equalsIgnoreCase(LimboConstants.ALL.toString())) {
				//create lib, copy bluecove-2.0.3.jar and comm.jar to lib
				directory = outputDirectory + "/"+webServiceName+"Client/lib";
				new File(directory).mkdirs();
				copyFileFromInputStream(EclipseProjectBackend.class.getResourceAsStream("/resources/jars/bluecove-2.0.3.jar"), new File(outputDirectory + "/"+webServiceName+"Client/lib/bluecove-2.0.3.jar"));
				copyFileFromInputStream(EclipseProjectBackend.class.getResourceAsStream("/resources/jars/comm.jar"), new File(outputDirectory + "/"+webServiceName+"Client/lib/comm.jar"));
				context.put("lib", "\t<classpathentry kind=\"lib\" path=\"lib/bluecove-2.0.3.jar\"/>\n\t<classpathentry kind=\"lib\" path=\"lib/comm.jar\"/>");
			}
			else
				context.put("lib", "");
			directory = outputDirectory + "/"+webServiceName+"Client";
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/.classpath" ));
			t.merge( context, writer );
			writer.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void copyFileFromInputStream(InputStream source, File dst) {
		if(source!=null) {
			if(!dst.exists()) {
				try {
					dst.createNewFile();
					OutputStream out = new FileOutputStream(dst);
					// Transfer bytes from in to out
					byte[] buf = new byte[1024];
					int len;
					while ((len = source.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					source.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void generateJMEClientResources() {
		//VelocityEngine ve = new VelocityEngine();
		try {
			Template t = engine.getTemplate( "resources/Project/JMEProject.vm" );
			VelocityContext context = new VelocityContext();
			String directory = outputDirectory + "/"+webServiceName+"Client";
			context.put("project", webServiceName+"Client");
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/.project" ));
			t.merge( context, writer );
			writer.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Template t = engine.getTemplate( "resources/Project/JMEClasspath.vm" );
			VelocityContext context = new VelocityContext();
			String directory = outputDirectory + "/"+webServiceName+"Client";
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/.classpath" ));
			t.merge( context, writer );
			writer.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Template t = engine.getTemplate( "resources/Project/MTJ.vm" );
			VelocityContext context = new VelocityContext();
			String directory = outputDirectory + "/"+webServiceName+"Client";
			context.put("jad", webServiceName+"Client.jad");
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/.mtj" ));
			t.merge( context, writer );
			writer.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Template t = engine.getTemplate( "resources/Project/ApplicationDescriptor.vm" );
			VelocityContext context = new VelocityContext();
			String directory = outputDirectory + "/"+webServiceName+"Client";
			context.put("jar", webServiceName+"Client.jar");
			context.put("project", webServiceName+"Client");
			context.put("midletClass", "eu.linksmart.limbo.client.LimboClient");
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/Application Descriptor" ));
			t.merge( context, writer );
			writer.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String res = outputDirectory + "/"+webServiceName+"Client/res";
		new File(res).mkdirs();
		String settings = outputDirectory + "/"+webServiceName+"Client/.settings";
		new File(settings).mkdirs();
		Date d = new Date();
		try {
			Template t = engine.getTemplate( "resources/Project/Preferences.vm" );
			VelocityContext context = new VelocityContext();
			String directory = outputDirectory + "/"+webServiceName+"Client/.settings";
			context.put("date", "#"+d.toString());
			new File(directory).mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/org.eclipse.jdt.core.prefs" ));
			t.merge( context, writer );
			writer.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}



}
