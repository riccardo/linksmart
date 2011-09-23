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
package eu.linksmart.selfstarmanager.gm.planner;

import java.util.ArrayList;
import java.util.Hashtable;

public class ASLTranslator {

	static Hashtable<String,String> dict;
	static Hashtable<String,Integer> argcounts;
	
	static {
		Object[] table=new Object[]{
		//	pddlname		aslname			pddl argcount   asl strip    
			"SETPROPERTY",	"set_property",			4,			
			"STARTDEVICE", 	"start_device",			1,			
			"STOPDEVICE",	"stop_device",			1,			
			"DEPLOY",		"deploy_component",		2,			
			"UNDEPLOY",		"undeploy_component",	2,			
			"STARTSERVICE", "start_service",		3,			
			"STOPSERVICE",	"stop_service",			2,
			"BINDINTERFACES","bind_services",		3,
			"UNBINDINTERFACES", "unbind_services",	3,
			"PRINTSTATUS", 	"print_status",			0,
			"INITDEVICE",	"init_device",			-1, // planner won't generate this, so report error if encountered
			"INITCOMPONENT","init_component",		-1,
			"INITSERVICE",	"init_service",			-1,
		};
		int i=0;
		dict=new Hashtable<String,String>(table.length);
		argcounts=new Hashtable<String,Integer>(table.length);
		while(i<table.length){
			dict.put((String)table[i], (String)table[i+1]);
			argcounts.put((String)table[i], (Integer)table[i+2]);
			i+=3;
		}
	}
	
	String replace(String s){
		if(dict.containsKey(s))
			return dict.get(s);
		else
			return s;
	}

	String prefix="httpclientprotocol";
	String rprefix="http.clientprotocol.";

	String replaceArg(String ar){
		String arg = ar.toLowerCase();
		if(arg.startsWith("device"))
			return arg.substring(6).toLowerCase();
		if(arg.startsWith(prefix))
			return arg.replace(prefix, rprefix);
		return arg;
	}
	
	String[] getArgs(String opname, Object[] args){
		int ac = argcounts.get(opname);
		String[] argv = new String[ac];
		for (int i=0;i<ac;i++){
			argv[i]=replaceArg((String) args[i]);
		}
		return argv;
	}
	
	String getArgString(String[] ags){	
		String args="";
		for(String s: ags)
			args =args+s+", ";
		return args.substring(0, args.length()-2); // remove last trailing comma...
	}
	
	public ArrayList<String> translatePlan(ArrayList<Object> plan){
		ArrayList<OperationCall> scr = new ArrayList<OperationCall>(plan.size());
		for (Object o:plan){
			Object[] opcall = (Object[]) o;
			System.out.println(opcall[0]);
			String operation = (String) opcall[0];
			scr.add(new OperationCall(operation,getArgs(operation,(Object[])opcall[1])));
		}			
		runScriptProcessors(scr);
		return toStringArrayList(scr);
	}

	
	public static class OperationCall{
		String opname; 
		String[] args;
		OperationCall(String name, String[] args){ 
			this.opname=name; this.args=args;
		}	
	}

	ArrayList<String> toStringArrayList(ArrayList<OperationCall> sc){
		ArrayList<String> script=new ArrayList<String>(sc.size());
		for(OperationCall oc: sc)
			script.add(replace(oc.opname)+"("+getArgString(oc.args)+");\n");
		return script;
	}
	
	public static interface ScriptProcessor{
		void processScript(ArrayList<OperationCall> script);
	}
	
	ArrayList<ScriptProcessor> processors = new ArrayList<ScriptProcessor>();
	
	void runScriptProcessors(ArrayList<OperationCall> script){
		for(ScriptProcessor sp:processors)
			sp.processScript(script);
	}
	
	public ASLTranslator(){
		processors.add(new ScriptProcessor(){
			public void processScript(ArrayList<OperationCall> script){
				for(OperationCall oc : script){
					if(oc.opname.equals("SETPROPERTY")){
						oc.args=new String[]{oc.args[2],oc.args[3]};
						continue;
					}
					
				}
			}
		});
	}
	
}
	

