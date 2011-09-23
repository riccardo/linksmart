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
package eu.linksmart.selfstar.cc.asl.core;

import java.io.StringReader;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Icon;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import eu.linksmart.selfstar.cc.asl.parser.Parameter;
import eu.linksmart.selfstar.cc.asl.parser.Parser;
import eu.linksmart.selfstar.cc.asl.parser.Scanner;
import eu.linksmart.selfstar.cc.asl.parser.SymTree;

public class ASLInterpreter implements ASLService{

	//ASLContext context;
	BundleContext bc;
	OSGi_Device thisdevice;
	
	static Hashtable<String,Operations> opnames;
	static { 
		Operations[] opvals=Operations.values();
		opnames=new Hashtable<String,Operations>(opvals.length);
		for(Operations o:opvals)
			opnames.put(o.toString(), o);
		
	}
	
	public ASLInterpreter(BundleContext bc){
		thisdevice=new OSGi_Device();
		thisdevice.setBundleContext(bc);
		this.bc=bc;
		debug=System.getProperty("ASL.debug","false").toLowerCase().equals("true");
	}

static boolean debug=true,log=true;
	
	private void debug(String msg){
		if (debug)
			System.out.println(msg);
	}
	
	private void log(String msg){
		if (log)
			System.out.println(msg);
	}
	
	/**
	 * Given a script S, this method generates a script I such that executing I reverts the
	 * effects of executing S. 
	 * 
	 * It produces I from S as follows:
	 * 1. All string declarations from S are transferred to I without change
	 * 2. For each 
	 *       init_component(<varname>, <path>) 
	 *    in S, an 
	 *       init_component(<varname>, &Location=file://<path>)
	 *    is included in I
	 * 2. Each  
	 *       init_component(<varname>,<designator>) is included in I as is.
	 *    This may introduce faults if the designator uses bundle IDs or other transient 
	 *    properties of components.
	 * 3. Each 
	 *       init_service(d,s,c) from S is included as is in I
	 * 
	 *  Everything from step 1-3 is put at the beginning of the script.
	 *  
	 *  4. For each start_service(s) in S, a stop_service(s) is put in I. 
	 *     These operations in I are in the reverse order of the corresponding ones in S.
	 *  5. For each deploy_component(d,c) in S, a undeploy_component(d,c) is added to I
	 *     The order in I is reversed as in step 4.
	 *     For each undeploy_component(d,c) in S, a deploy_component(d,c) is added to I.
	 *  6. For each stop_service in S, a start_service is added to I, again in reverse order
	 *  
	 * Sketch proof of correctness:
	 *  All scripts* can be rearranged to group operations into four segments without 
	 *  affecting semantics:
	 *  
	 *  declarations (variables, handles for services devices etc)
	 *  deactivation (stopping services)
	 *  undeployment (...)
	 *  deployment   (components)
	 *  activation   (starting services)
	 *  
	 *  Following the above procedure, declarations in S and I are identical.
	 *  
	 *  services that were stopped in S are started in in S 
	 *  
	 *  The deployment block in I cancels the deployment block in S by undeploying what s installed
	 *  and re-deploying what whas undeployed in S.
	 *  
	 *  * if s includes e.g. 
	 *  	deploy(d,c);
	 *  	undeploy(d,c);
	 *  then d won't be at c, but switching the two statements will leave c at d. However,
	 *  removing these two statements from S will preserve it's semantics...
	 *  
	 * @param script
	 * @return
	 */
	private String inverseScript(SymTree script){
		StringBuilder sb=new StringBuilder();
		
		return sb.toString();
	}
	
	/**
	 * Unparsing a script simply generates a string representation that is functionally equivalent
	 * to the script given as symbolic representation. 
	 * 
	 * If s is a parsed representation of a script, s.equals(parseScript(unParse(s))) is true 
	 * (if SymTree implemented equals(), which it doesn't: TODO)
	 * @param script
	 * @return
	 */
	private String unParse(SymTree script){
		StringBuilder sb=new StringBuilder();
		script.appendStringRepresentation(sb);
		return sb.toString();
	}
	
	/** Parses the given string as a script
	 * 
	 * @param script The string representation of the script to be parsed
	 * @return the parsed representation, or null if the string was empty or an error occurred
	 */
	private SymTree parseScript(String script){
		if (script.equals("")){
			System.out.println("ASL parser: Empty string given as script !");
			return null;
		}
		System.out.println("Executing script:");
		int i=0;
		try {
			StringReader sr = new StringReader(script);
			Scanner s = new Scanner(sr);
			Parser p = new Parser(s);
			SymTree t = ((SymTree)p.parse().value);
			return t;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * defined as <code>return executeScript(ASLScript.parseScript(script));</code>
	 */
	public int executeScript(String script) {
		SymTree t = parseScript(script);
		if (debug){
			t.printTree();
			System.out.println("unparsed script:\n"+unParse(t));
		}
		int i=executeScript(t);
		return i;
	}

/*	public void setDevice(Device d){
		//context.setDeviceHandle("local", d);
	}

	private Bundle resolveBundleSymbolicName(String symname){
		Bundle[] bl = bc.getBundles();
		for (int i=0;i<bl.length;i++)
			if(bl[i].getSymbolicName().equals(symname))
				return bl[i];
		return null;
	}
	*/
	private Hashtable<String,Bundle> componentDict;
	private Hashtable<String,String> fileDict;
	
	private void init_component(String varname, Parameter p){
		if (p.isString()){
			fileDict.put(varname,p.getString());
			debug("Varname "+varname+" added as "+p.getString());
			return;
		}
		Bundle b;
		if(!p.isDesignator()){
			debug("ASL INTERNAL ERROR: Parameter type not recognized.");
			return;
		}
		componentDict.put(varname, resolve2Bundle(p.getDesignator()));	
	}

	/**
	 * Checks the properties against the currently installed bundles and returns a matching bundle, 
	 * or null if no bundles match. The
	 * recognized properties are: BundleId, Location, SymbolicName, State
	 * The a property matches if it's a string in one of the above names, and the value, as
	 * a string equals the value returned by the corresponding bundle method.

	 * @param properties the key-value pairs designating the bundle
	 * @return
	 */
	private Bundle resolve2Bundle(Hashtable<String,String> properties){
		debug("Trying to resolve bundle based on "+properties+" bc:"+bc);
		String desired,actual;
		Bundle rval=null;
		b_loop:
		for (Bundle b:bc.getBundles()){
			debug("Matching against bundle with Location="+b.getLocation());
			p_loop:
			for (String key:properties.keySet()){
				desired=properties.get(key);
				actual=null;
				if ("BundleId".equals(key))
					actual=""+b.getBundleId();
				if ("Location".equals(key))
					actual=b.getLocation();
				if ("SymbolicName".equals(key))
					actual=b.getSymbolicName();
				if ("State".equals(key))
					actual=""+b.getState();
				if (actual==null){
					debug("ERROR, cannot resolve bundle: asl don't know how to match "+key+" as a bundle property property");
					return null;
				} else {
					if (actual.equals(desired)){
						// so far no mismatches for this bundle, check next property...
//						debug("Bundle "+b+"(sn:"+b.getSymbolicName()+") matched "+key+"with value "+desired);
						continue p_loop; 
					} else{
						// right key, but values doesn't match for this bundle, check next bundle...
	//					debug("Bundle "+b+"(sn:"+b.getSymbolicName()+") did not match "+key+"with value "+desired);

						continue b_loop; 
					}
				}
			}
			// getting here means all checked keys matched for the current bundle
			rval=b;
		}				
		return rval; // it's either set as above, or the outer loop terminated without any matching bundles being found
	}

	private Operations getOpType(SymTree expr){
		return opnames.get((String) expr.getChild(0).getValue());
	}
	private Vector<Parameter> getParameters(SymTree expr){
		return (Vector<Parameter>) expr.getChild(0).getChild(0).getValue();
	}
	
	private void resetInterpretationContext(){
		componentDict= new Hashtable<String,Bundle>(10);
		fileDict = new Hashtable<String,String>(10);	
	}

	/**
	 * Executes the given symbolic tree as a script. The given SymTree is normally preceeded by parsing
	 * when invoked through the executeScript(String script) method.
	 * @param script The script to execute
	 * @return
	 */
	public int executeScript(SymTree script){
		resetInterpretationContext();
		Vector<SymTree> elist=script.getChild(0).getChildren();
		Operations optype;
		Vector<Parameter> parameters;
		String devicename, componenturl, componentname, servicename, servicename1,servicename2,interfacename,url;
		Bundle b1,b2,client,server;
		SymTree expr;
		for (int i=0;i<elist.size();i++){
			expr=elist.get(i);
			optype=getOpType(expr);
			parameters=getParameters(expr);
			switch (optype){
			case init_device:
				devicename = parameters.get(0).getString();
				if (!devicename.equals("local")){
					log("Error, unknown device id "+devicename+" (distribution not supported). Aborting script!");
					return i;
				}
				break;
			case init_component:
				componentname=parameters.get(0).getString();
				init_component(componentname,parameters.get(1));
				break;
			case deploy_component:
				devicename=parameters.get(0).getString();
				componentname=parameters.get(1).getString();
				if (!fileDict.containsKey(componentname)){
					System.out.println("Error in operation #"+i+": component "+componentname+" has not been defined");
					return i;	
				}
				url=fileDict.get(componentname);
				debug("Deploying component "+componentname+" from file "+url);
				b1=thisdevice.deploy_component(url);
				if (b1==null){
					System.out.println("Error in operation #"+i+": component "+componentname+" not deployed");
					return i;		
				}
				componentDict.put(componentname, b1);
				break;
			case init_service:
				// just map the installed bundle to the service
				devicename=parameters.get(0).getString();
				componentname=parameters.get(1).getString();
				servicename=parameters.get(2).getString();
				if (!componentDict.containsKey(componentname)){
					System.out.println("Error in operation #"+i+": component "+componentname+" not deployed");
					return i;		
				}
				componentDict.put(servicename, componentDict.get(componentname));
				break;
			case print_status:
				devicename = parameters.get(0).getString();
				if (!devicename.equals("local")){
					System.out.println("Error, unknown device id "+devicename+". Aborting script!");
					return i;
				}
				System.out.println(thisdevice.printStatus());
				break;
			case start_device:
				devicename = parameters.get(0).getString();
				if (!devicename.equals("local")){
					System.out.println("Error, unknown device id "+devicename+". Aborting script!");
					return i;
				}
				System.out.println("Device already started.");
				break;
			case start_service:
				servicename=parameters.get(0).getString();
				b1=componentDict.get(servicename);
				debug("Starting service "+servicename);
				if (b1==null){
					System.out.println("Error, unknown service id "+servicename+". Aborting script!");
					return i;
				}
				if (b1.getState()==Bundle.ACTIVE){
					debug("Bundle already running");
					break;
				}
				if (b1.getState()==Bundle.UNINSTALLED){
					debug("This bundle has been uninstalled !");
					break;
				}
				try {
						b1.start();
					} catch (BundleException e1) {
						e1.printStackTrace();
					}
				break;
			case stop_device:
				if(parameters.get(0).getString().equals("local"))
					System.exit(0);
				else
					debug("Cannot stop non-local device.");
				break;
			case stop_service:
				servicename=parameters.get(0).getString();
				b1=componentDict.get(servicename);
				debug("Stopping service "+servicename);
				if (b1==null){
					System.out.println("Error, unknown service id "+servicename+". Aborting script!");
					return i;
				}
				try {
						b1.stop();
					} catch (BundleException e1) {
						e1.printStackTrace();
					}
				break;
			case bind_services:
				servicename1=parameters.get(0).getString();
				servicename2=parameters.get(1).getString();
				interfacename=parameters.get(2).getString();
				client=componentDict.get(servicename1);
				server=componentDict.get(servicename2);
				if (client==null){
					System.out.println("Error in operation #"+i+": bundle id "+servicename1+" for client service not initialized");
					return i;
				}
				if (client==null){
					System.out.println("Error in operation #"+i+": bundle id "+servicename2+" for server service not initialized");
					return i;
				}
				thisdevice.bind(client, server, interfacename);
				break;
			case unbind_services:
				servicename1=parameters.get(0).getString();
				servicename2=parameters.get(1).getString();
				interfacename=parameters.get(2).getString();
				client=componentDict.get(servicename1);
				server=componentDict.get(servicename2);
				if (client==null){
					System.out.println("Error in operation #"+i+": bundle id "+servicename1+" for client service not initialized");
					return i;
				}
				if (client==null){
					System.out.println("Error in operation #"+i+": bundle id "+servicename2+" for server service not initialized");
					return i;
				}
				thisdevice.unbind(client, server, interfacename);
				break;
			case undeploy_component:
				componentname=parameters.get(0).getString();
				b1=componentDict.get(componentname);
				if(b1==null){
					System.out.println("Error in operation #"+i+": bundle id "+componentname+"not initialized");
					return i;
				}
				try {
						debug("Undeploying component "+componentname +"("+b1+")");
						b1.uninstall();
					} catch (BundleException e) {
						e.printStackTrace();
					}
				break;
			case set_property:
				String key,value;
				key=parameters.get(0).getString();
				value=parameters.get(1).getString();
				System.setProperty(key, value);
				System.out.println("AQL:Set property"+key+" to "+System.getProperty(key));
				log("Properties:");
				for(Object k:System.getProperties().keySet())
					log("\t"+k+"="+System.getProperty((String) k));
				break;
			case change_protocol:
			default:
				log("Operation "+optype+" wasn't recognized by the ASL interpreter.");
				// changeProtocol(initiatingEndpointid, passiveEndpointId, protocol id);
				//int r=Registry.changeProtocol(opcall.parameters[0], opcall.parameters[1], opcall.parameters[2]);
				//if (0==r) System.out.println("Protocol");
				//if (-1==r) System.out.println("Error changing protocol; connection("+opcall.parameters[0]+","+opcall.parameters[1]+") not registered.");
				//if (-2==r) System.out.println("Error invoking changeProtocol on connector. See exception above.");
			
			}
		}
		return 0;
	}

	public boolean getDebugEnabled() {
		return debug;
	}
	
	public void setDebugEnabled(boolean enabled){
		debug=enabled;
	}
	
/*	
	public int executeScript(ASLScript script) {
		String dev,com,svc1,svc2,symname,url,interfacename;
		Hashtable<String,Bundle> sc_bundles=new Hashtable<String,Bundle>();
		Hashtable<String,String> componenturls = new Hashtable<String,String>();
		Bundle b1,client,server;
		
		for (int i=0;i<script.script.length;i++){;
			ASLScript.OInstance opcall = script.script[i];
			System.out.println("processing:"+opcall);
			//Component component;
			//Device device;
			//Service service1,service2;
			switch (opcall.opname){
			case init_device:
				dev = opcall.parameters[0];
				if (!dev.equals("local")){
					System.out.println("Error, unknown device id "+dev+" (distribution not supported). Aborting script!");
					return i;
				}
				//device = context.getDeviceHandle(dev);
				break;
			case init_component:
				com=opcall.parameters[0];
				url=opcall.parameters[1];
				componenturls.put(com,url);
				System.out.println("added"+com+"with url:"+url);
				//b1=resolveBundleSymbolicName(symname);
				//if(b1==null){
				//	System.out.println("Error in operation #"+i+": bundle with Bundle-SymbolicName "+symname+"cannot be found");
				//	return i;
				//}
				//sc_bundles.put(com, b1);
				break;
			case deploy_component:
				dev=opcall.parameters[0];
				com=opcall.parameters[1];
				if (!componenturls.containsKey(com)){
					System.out.println("Error in operation #"+i+": component "+com+" has not been defined");
					return i;	
				}
				url=componenturls.get(com);
				b1=thisdevice.deploy_component(url);
				if (b1==null){
					System.out.println("Error in operation #"+i+": component "+com+" not deployed");
					return i;		
				}
				sc_bundles.put(com, b1);
				break;
			case init_service:
				// just map the installed bundle to the service
				dev=opcall.parameters[0];
				com=opcall.parameters[1];
				svc1=opcall.parameters[2];
				if (!sc_bundles.containsKey(com)){
					System.out.println("Error in operation #"+i+": component "+com+" not deployed");
					return i;		
				}
				sc_bundles.put(svc1, sc_bundles.get(com));
				break;
			case print_status:
				dev = opcall.parameters[0];
				if (!dev.equals("local")){
					System.out.println("Error, unknown device id "+dev+". Aborting script!");
					return i;
				}
				thisdevice.printStatus();
				break;
			case start_device:
				dev = opcall.parameters[0];
				if (!dev.equals("local")){
					System.out.println("Error, unknown device id "+dev+". Aborting script!");
					return i;
				}
				System.out.println("Device already started.");
				break;
			case start_service:
				svc1=opcall.parameters[0];
				b1=sc_bundles.get(svc1);
				if (b1==null){
					System.out.println("Error, unknown service id "+svc1+". Aborting script!");
					return i;
				}
				try {
						b1.start();
					} catch (BundleException e1) {
						e1.printStackTrace();
					}
				break;
			case stop_device:
				System.exit(0);
				break;
			case stop_service:
				svc1=opcall.parameters[0];
				b1=sc_bundles.get(svc1);
				if (b1==null){
					System.out.println("Error, unknown service id "+svc1+". Aborting script!");
					return i;
				}
				try {
						b1.stop();
					} catch (BundleException e1) {
						e1.printStackTrace();
					}
				break;
			case bind_services:
				svc1=opcall.parameters[0];
				svc2=opcall.parameters[1];
				interfacename=opcall.parameters[2];
				client=sc_bundles.get(svc1);
				server=sc_bundles.get(svc2);
				if (client==null){
					System.out.println("Error in operation #"+i+": bundle id "+svc1+" for client service not initialized");
					return i;
				}
				if (client==null){
					System.out.println("Error in operation #"+i+": bundle id "+svc2+" for server service not initialized");
					return i;
				}
				thisdevice.bind(client, server, interfacename);
				break;
			case unbind_services:
				svc1=opcall.parameters[0];
				svc2=opcall.parameters[1];
				interfacename=opcall.parameters[2];
				client=sc_bundles.get(svc1);
				server=sc_bundles.get(svc2);
				if (client==null){
					System.out.println("Error in operation #"+i+": bundle id "+svc1+" for client service not initialized");
					return i;
				}
				if (client==null){
					System.out.println("Error in operation #"+i+": bundle id "+svc2+" for server service not initialized");
					return i;
				}
				thisdevice.unbind(client, server, interfacename);
				break;
			case undeploy_component:
				com=opcall.parameters[0];
				b1=sc_bundles.get(com);
				if(b1==null){
					System.out.println("Error in operation #"+i+": bundle id "+com+"not initialized");
					return i;
				}
				try {
						b1.uninstall();
					} catch (BundleException e) {
						e.printStackTrace();
					}
				break;
			case change_protocol:
				// changeProtocol(initiatingEndpointid, passiveEndpointId, protocol id);
				//int r=Registry.changeProtocol(opcall.parameters[0], opcall.parameters[1], opcall.parameters[2]);
				//if (0==r) System.out.println("Protocol");
				//if (-1==r) System.out.println("Error changing protocol; connection("+opcall.parameters[0]+","+opcall.parameters[1]+") not registered.");
				//if (-2==r) System.out.println("Error invoking changeProtocol on connector. See exception above.");
				System.out.println("NOT SUPPORTED IN OS VERSION");
			}
		}
		
		return 0;
	}
	*/
	
	
}
