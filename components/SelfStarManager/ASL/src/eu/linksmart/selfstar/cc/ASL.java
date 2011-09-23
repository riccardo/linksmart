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
package eu.linksmart.selfstar.cc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

import eu.linksmart.selfstar.aql.AQLService;
import eu.linksmart.selfstar.cc.asl.core.ASLInterpreter;
import eu.linksmart.selfstar.cc.asl.interactive.ASLShell;
import eu.linksmart.selfstar.cc.asl.parser.SymTree;
 
public class ASL implements EventHandler {
	private LogService log;
	private EventAdmin eventAdmin;
	private ASLInterpreter interpreter;
	private ASLShell shell;

	private void log(String txt){
		if (log!=null)
			log.log(LogService.LOG_INFO,txt);
		else
			System.out.println(txt);
	}
	
	//@ Override
	/** 
	 * Assumes the event carries an asl script as payload, 
	 * stored in the event as a property named "script"
	 * 
	 * The script can either be a String, or for a more efficient
	 * execution, a pre-parsed script object of class ASLScript.
	 * 
	 * It is recommended to use pre-parsed scripts when possible since
	 * parsing errors can be discovered in the context where they likely 
	 * originate. 
	 * 
	 */

	public void handleEvent(Event event) {
		log.log(LogService.LOG_INFO, "received event : " + event);
		if (interpreter==null)
			log.log(LogService.LOG_ERROR, "No interpreter initialized");
		Object script = event.getProperty("script");
		try {
			if (script instanceof String)
				interpreter.executeScript((String)script);
			else if (script instanceof SymTree)
				interpreter.executeScript((SymTree)script);
		} catch (Exception e){
			log.log(LogService.LOG_ERROR, "Error executing script:"+e);
		}
	}

	protected void setLog(LogService log) {
		this.log = log;
	}
	
	protected void unsetLog(LogService log) {
		this.log = null;
	}

	protected void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;	
	}

	protected void unsetEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = null;
	}
	
	private AQLService aql;
	

	
	protected void unsetAql(AQLService aqls) {
		this.aql=null;
	}

	public void setAql(AQLService aql) {
		this.aql = aql;
	}

	/**
	 * Runs an initialization script if the system property "asl_init_script" is 
	 * set to a path.
	 */
	private void runInitScript(){
		String pname="asl_init_script";
		if (!System.getProperties().containsKey(pname))
			return;
		String scriptpath=System.getProperty(pname);
		if (scriptpath==null || "".equals(scriptpath))
			return;
		interpreter.executeScript(getFileAsString(scriptpath));
	}
	
	@SuppressWarnings("unchecked")
	protected void activate(ComponentContext context) {
		// Initialize interpreter
		System.out.println("**Starting ASL");
		interpreter = new ASLInterpreter(context.getBundleContext());
		// Subscribe
		Dictionary properties = new Hashtable();
		properties.put(EventConstants.EVENT_TOPIC, new String[]{"componentcontrol/newplan"});
		context.getBundleContext().registerService(EventHandler.class.getName(), this, properties);
		// run init script if present:
		runInitScript();
		if (System.getProperty("ASL.withShell", "false").toLowerCase().equals("true")){
			shell=new ASLShell(context.getBundleContext(), interpreter);
			log("ASL:creating shell.");
		} else
			log("ASL: no shell will be created.");
	}
	
	static String getFileAsString(String f){
		System.out.println("Getting string"+f);
		try {
			BufferedReader bi = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			StringBuffer sb=new StringBuffer(2024);
			String s;
			while((s=bi.readLine()) !=null)
				sb.append(s+"\n");
			bi.close();
			return sb.toString();
		} catch (IOException e){ 
			e.printStackTrace(System.out);
		}
		return "";
	}

}
