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
 * Copyright (C) 2006-2010 [University of Paderborn]
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

package eu.linksmart.limbo.sm.networkmanager.client;

import java.util.Vector;
import java.util.Hashtable;

public class NetworkManagerApplicationLimboClientParser {

	private java.util.Vector args;
	private String operation;
	private int nrArgs;
	private String request;
	private String SOAPAction;

	public NetworkManagerApplicationLimboClientParser(String total, String soapaction){
		this.request = total;
		this.SOAPAction = soapaction; 
	}

	public Vector getArg(){return this.args;}

	public Object getArg(int position){return this.args.elementAt(position);}

	public int getNrArgs(){return this.nrArgs;}

	public String getOperation(){return this.operation;}

	public String getRequest(){return this.request;}

	public String getSOAPAction() {return this.SOAPAction;}	
	public boolean convertStringToBoolean(String target){
		if(target.equalsIgnoreCase("true"))
			return true;
		return false;
	}

	public boolean parseRequest(String request){
		String result = this.preamble(request);
		if(result == null)
			return false;
		result = this.soapenv(result);
		if(result == null)
			return false;
		result = this.soapbody(result);
		if(result == null)
			return false;
		Vector op;
		op = this.createHID(result);
		this.operation = "createHID";
		if(op == null){
		op = this.closeSession(result);
		this.operation = "closeSession";
		if(op == null){
		op = this.getHIDs(result);
		this.operation = "getHIDs";
		if(op == null){
		op = this.renewHID(result);
		this.operation = "renewHID";
		if(op == null){
		op = this.createHIDwDesc(result);
		this.operation = "createHIDwDesc";
		if(op == null){
		op = this.getContextHIDs(result);
		this.operation = "getContextHIDs";
		if(op == null){
		op = this.removeHID(result);
		this.operation = "removeHID";
		if(op == null){
		op = this.getHIDsbyDescriptionAsString(result);
		this.operation = "getHIDsbyDescriptionAsString";
		if(op == null){
		op = this.getHostHIDs(result);
		this.operation = "getHostHIDs";
		if(op == null){
		op = this.getHIDsbyDescription(result);
		this.operation = "getHIDsbyDescription";
		if(op == null){
		op = this.createHIDwDesc(result);
		this.operation = "createHIDwDesc";
		if(op == null){
		op = this.getNMPosition(result);
		this.operation = "getNMPosition";
		if(op == null){
		op = this.getContextHIDsAsString(result);
		this.operation = "getContextHIDsAsString";
		if(op == null){
		op = this.addSessionRemoteClient(result);
		this.operation = "addSessionRemoteClient";
		if(op == null){
		op = this.addContext(result);
		this.operation = "addContext";
		if(op == null){
		op = this.setSessionParameter(result);
		this.operation = "setSessionParameter";
		if(op == null){
		op = this.renewHIDInfo(result);
		this.operation = "renewHIDInfo";
		if(op == null){
		op = this.getHostHIDsAsString(result);
		this.operation = "getHostHIDsAsString";
		if(op == null){
		op = this.synchronizeSessionsList(result);
		this.operation = "synchronizeSessionsList";
		if(op == null){
		op = this.getHIDsAsString(result);
		this.operation = "getHIDsAsString";
		if(op == null){
		op = this.startNM(result);
		this.operation = "startNM";
		if(op == null){
		op = this.receiveData(result);
		this.operation = "receiveData";
		if(op == null){
		op = this.openSession(result);
		this.operation = "openSession";
		if(op == null){
		op = this.getSessionParameter(result);
		this.operation = "getSessionParameter";
		if(op == null){
		op = this.createHID(result);
		this.operation = "createHID";
		if(op == null){
		op = this.removeAllHID(result);
		this.operation = "removeAllHID";
		if(op == null){
		op = this.sendData(result);
		this.operation = "sendData";
		if(op == null){
		op = this.stopNM(result);
		this.operation = "stopNM";
		if(op == null){
		return false;
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		}
		this.args = op;
		return true;
	}


	public String preamble(String subRequest){
		String result;
		String preamble = "<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"utf-8"+'"'+"?>";
		if(subRequest.regionMatches(true, 0, preamble, 0, preamble.length()))
			result = subRequest.substring(preamble.length(), subRequest.length());
		else
			result = null;
		return result;
	}


	public String soapenv(String subRequest){
		String result;
		StringTokenizer st2 = new StringTokenizer(subRequest,": ");
		st2.nextToken();
		if(st2.hasMoreTokens()){
			String env = st2.nextToken();
			String before = "<";
			String after = "Envelope>";
			if(subRequest.startsWith(before) && subRequest.endsWith(after) &&(env.equalsIgnoreCase("Envelope"))){
				StringTokenizer st = new StringTokenizer(subRequest,">");
				before = st.nextToken();
				before = before.concat(">");
				result = subRequest.substring(before.length(), (subRequest.length() - after.length()));
			}
			else
				result = null;
		}
		else
			result = null;
		return result;
	}

	public String soapbody(String subRequest){
		String result;
		StringTokenizer st = new StringTokenizer(subRequest,":>");
		st.nextToken();
		if(st.hasMoreTokens()){
			String body = st.nextToken();
			String before = "<";
			String after = ":Body>";
			int index = subRequest.lastIndexOf('<');
			subRequest = subRequest.substring(0, index);
			if(subRequest.startsWith(before) && subRequest.endsWith(after)&&(body.equalsIgnoreCase("Body"))){
				int firstIndex = subRequest.indexOf(">");
				result = subRequest.substring(firstIndex+1, (subRequest.length() - after.length()));
			}
			else
				result = null;
		}
		else
			result = null;
		return result;
	}

        

	public Vector createHID(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="createHIDResponse";
		after = "createHIDResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<createHIDReturn";
			after = "</createHIDReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		nArgs = 1;
		V = new Vector(nArgs);
		before ="createHIDResponse1";
		after = "createHIDResponse1>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<createHIDReturn";
			after = "</createHIDReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector closeSession(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 0;
		V = new Vector(nArgs);
		before ="closeSessionResponse";
		after = "closeSessionResponse>";

		if((subRequest.indexOf("closeSessionResponse") != -1) && (subRequest.endsWith("/>"))) {
			result = "";
			this.nrArgs= nArgs;
			return V;
		}		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector getHIDs(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="getHIDsResponse";
		after = "getHIDsResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<getHIDsReturn";
			after = "</getHIDsReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector renewHID(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="renewHIDResponse";
		after = "renewHIDResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<renewHIDReturn";
			after = "</renewHIDReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector createHIDwDesc(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="createHIDwDescResponse";
		after = "createHIDwDescResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<createHIDwDescReturn";
			after = "</createHIDwDescReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		nArgs = 1;
		V = new Vector(nArgs);
		before ="createHIDwDescResponse1";
		after = "createHIDwDescResponse1>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<createHIDwDescReturn";
			after = "</createHIDwDescReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector getContextHIDs(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="getContextHIDsResponse";
		after = "getContextHIDsResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<getContextHIDsReturn";
			after = "</getContextHIDsReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector removeHID(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 0;
		V = new Vector(nArgs);
		before ="removeHIDResponse";
		after = "removeHIDResponse>";

		if((subRequest.indexOf("removeHIDResponse") != -1) && (subRequest.endsWith("/>"))) {
			result = "";
			this.nrArgs= nArgs;
			return V;
		}		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector getHIDsbyDescriptionAsString(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="getHIDsbyDescriptionAsStringResponse";
		after = "getHIDsbyDescriptionAsStringResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<getHIDsbyDescriptionAsStringReturn";
			after = "</getHIDsbyDescriptionAsStringReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector getHostHIDs(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="getHostHIDsResponse";
		after = "getHostHIDsResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<getHostHIDsReturn";
			after = "</getHostHIDsReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector getHIDsbyDescription(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="getHIDsbyDescriptionResponse";
		after = "getHIDsbyDescriptionResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<getHIDsbyDescriptionReturn";
			after = "</getHIDsbyDescriptionReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector getNMPosition(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="getNMPositionResponse";
		after = "getNMPositionResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<getNMPositionReturn";
			after = "</getNMPositionReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector getContextHIDsAsString(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="getContextHIDsAsStringResponse";
		after = "getContextHIDsAsStringResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<getContextHIDsAsStringReturn";
			after = "</getContextHIDsAsStringReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector addSessionRemoteClient(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 0;
		V = new Vector(nArgs);
		before ="addSessionRemoteClientResponse";
		after = "addSessionRemoteClientResponse>";

		if((subRequest.indexOf("addSessionRemoteClientResponse") != -1) && (subRequest.endsWith("/>"))) {
			result = "";
			this.nrArgs= nArgs;
			return V;
		}		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector addContext(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="addContextResponse";
		after = "addContextResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<addContextReturn";
			after = "</addContextReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector setSessionParameter(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 0;
		V = new Vector(nArgs);
		before ="setSessionParameterResponse";
		after = "setSessionParameterResponse>";

		if((subRequest.indexOf("setSessionParameterResponse") != -1) && (subRequest.endsWith("/>"))) {
			result = "";
			this.nrArgs= nArgs;
			return V;
		}		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector renewHIDInfo(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="renewHIDInfoResponse";
		after = "renewHIDInfoResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<renewHIDInfoReturn";
			after = "</renewHIDInfoReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector getHostHIDsAsString(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="getHostHIDsAsStringResponse";
		after = "getHostHIDsAsStringResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<getHostHIDsAsStringReturn";
			after = "</getHostHIDsAsStringReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector synchronizeSessionsList(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="synchronizeSessionsListResponse";
		after = "synchronizeSessionsListResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<synchronizeSessionsListReturn";
			after = "</synchronizeSessionsListReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector getHIDsAsString(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="getHIDsAsStringResponse";
		after = "getHIDsAsStringResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<getHIDsAsStringReturn";
			after = "</getHIDsAsStringReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector startNM(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="startNMResponse";
		after = "startNMResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<startNMReturn";
			after = "</startNMReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector receiveData(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="receiveDataResponse";
		after = "receiveDataResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<receiveDataReturn";
			after = "</receiveDataReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector openSession(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="openSessionResponse";
		after = "openSessionResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<openSessionReturn";
			after = "</openSessionReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector getSessionParameter(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="getSessionParameterResponse";
		after = "getSessionParameterResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<getSessionParameterReturn";
			after = "</getSessionParameterReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector removeAllHID(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 0;
		V = new Vector(nArgs);
		before ="removeAllHIDResponse";
		after = "removeAllHIDResponse>";

		if((subRequest.indexOf("removeAllHIDResponse") != -1) && (subRequest.endsWith("/>"))) {
			result = "";
			this.nrArgs= nArgs;
			return V;
		}		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector sendData(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="sendDataResponse";
		after = "sendDataResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<sendDataReturn";
			after = "</sendDataReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector stopNM(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 1;
		V = new Vector(nArgs);
		before ="stopNMResponse";
		after = "stopNMResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<stopNMReturn";
			after = "</stopNMReturn>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.lastIndexOf('<');
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}

	
}