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

package eu.linksmart.limbo.storagemanagerdevice;

import java.util.Vector;
import java.util.Hashtable;

public class LinkSmartServicePortParser {

	private java.util.Vector args;
	private String operation;
	private int nrArgs;
	private String request;
	private String SOAPAction;

	public LinkSmartServicePortParser(String total, String soapaction){
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
		if(hasMultiRefs(result)) {
		    java.util.Vector ids = getMultiRefIDs(result);
		    result = convertMessage(ids, result);
		}
		result = this.soapenv(result);
		if(result == null)
			return false;
		result = this.soapbody(result);
		if(result == null)
			return false;
		Vector op;
		op = this.GetLinkSmartID(result);
		this.operation = "GetLinkSmartID";
		if(op == null){
		op = this.CreateWS(result);
		this.operation = "CreateWS";
		if(op == null){
		op = this.GetStatus(result);
		this.operation = "GetStatus";
		if(op == null){
		op = this.GetProperty(result);
		this.operation = "GetProperty";
		if(op == null){
		op = this.GetHasError(result);
		this.operation = "GetHasError";
		if(op == null){
		op = this.GetErrorMessage(result);
		this.operation = "GetErrorMessage";
		if(op == null){
		op = this.GetDiscoveryInfo(result);
		this.operation = "GetDiscoveryInfo";
		if(op == null){
		op = this.GetDACEndpoint(result);
		this.operation = "GetDACEndpoint";
		if(op == null){
		op = this.GetWSEndpoint(result);
		this.operation = "GetWSEndpoint";
		if(op == null){
		op = this.GetLinkSmartWSEndpoint(result);
		this.operation = "GetLinkSmartWSEndpoint";
		if(op == null){
		op = this.GetWSDL(result);
		this.operation = "GetWSDL";
		if(op == null){
		op = this.SetLinkSmartID(result);
		this.operation = "SetLinkSmartID";
		if(op == null){
		op = this.SetStatus(result);
		this.operation = "SetStatus";
		if(op == null){
		op = this.SetDACEndpoint(result);
		this.operation = "SetDACEndpoint";
		if(op == null){
		op = this.SetProperty(result);
		this.operation = "SetProperty";
		if(op == null){
		op = this.Stop(result);
		this.operation = "Stop";
		if(op == null){
		op = this.StopWS(result);
		this.operation = "StopWS";
		if(op == null){
		op = this.StopLinkSmartWS(result);
		this.operation = "StopLinkSmartWS";
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

	public java.util.Vector getMultiRefIDs(String subRequest) {
		
		java.util.Vector ids = new java.util.Vector();
		
		boolean hasMoreIds = true;
		while(hasMoreIds){
			int beforeIndex = subRequest.indexOf("href=\"#")+"href=\"#".length();
			String after = subRequest.substring(beforeIndex, subRequest.length());
			int afterIndex = after.indexOf("\"")-1; 
			String id = subRequest.substring(beforeIndex, afterIndex+"\"".length()+beforeIndex);	
			ids.add(id);
			subRequest = subRequest.substring(afterIndex+"\"".length()+beforeIndex);
			if(!subRequest.contains("href=\"#"))
				hasMoreIds = false;
		}
		
		return ids;
	}
	
	public boolean hasMultiRefs(String subRequest) {
		if(subRequest.contains("multiRef"))
			return true;
		return false;
	}

	public String convertMessage(java.util.Vector ids, String request) {
		String result ="";
		Hashtable multiRefs = new Hashtable();
		
		boolean hasMoreMultiRefs = true;
		String tempRequest = request;
		while(hasMoreMultiRefs){
			int beforeIndex = tempRequest.indexOf("<multiRef");
			int afterIndex = tempRequest.indexOf("/multiRef>");
			String multiRefBody = tempRequest.substring(beforeIndex,afterIndex+"/multiRef>".length());
			String id = getMultiRefId(multiRefBody);
			String multiRefData = getMultiRefData(multiRefBody);
			multiRefs.put(id, multiRefData);
			tempRequest = tempRequest.substring(afterIndex+"/multiRef>".length());
			if(!tempRequest.contains("<multiRef"))
				hasMoreMultiRefs = false;
		}
		result = replaceRefs(multiRefs, request);
	
		return result;
	}
	
	public String getMultiRefId(String multiRef) {
		
		int beginIndex = multiRef.indexOf("id=\"")+"id=\"".length();
		String afterMulti = multiRef.substring(beginIndex);
		int endIndex = afterMulti.indexOf("\"");
		String id = afterMulti.substring(0, endIndex);
		return id;
	}
	
	public String getMultiRefData(String multiRef) {
		int beginIndex = multiRef.indexOf(">");
		int endIndex = multiRef.lastIndexOf("</multiRef>");
		String multiRefData = multiRef.substring(beginIndex+1, endIndex);
		return multiRefData;
	}
	
	public String replaceRefs(Hashtable multiRefs, String request) {
		
	    int multiRefBegin = request.indexOf("<multiRef");
	    int multiRefEnd = request.lastIndexOf("</multiRef>")+"</multiRef>".length();
	    String multiRefsBlock = request.substring(multiRefBegin, multiRefEnd);
	    request = request.replace(multiRefsBlock, "");
	    
	    boolean hasMoreIds = true;
		while(hasMoreIds){
			int beforeIndex = request.indexOf("href=\"#")+"href=\"#".length();
			String after = request.substring(beforeIndex, request.length());
			int afterIndex = after.indexOf("\"")-1; 
			String id = request.substring(beforeIndex, afterIndex+"\"".length()+beforeIndex);
			String before = request.substring(0, beforeIndex);
			String tagElement = before.substring(before.lastIndexOf("<")+1, before.length()-"href=\"#".length()-1);
			int beginTag = before.lastIndexOf("<");
			int endTag = request.indexOf(">", beginTag);
			String refData = multiRefs.get(id).toString();
			request = request.replace( request.substring(beginTag, endTag), "<"+tagElement+">"+refData+"</"+tagElement);
			if(!request.contains("href=\"#"))
				hasMoreIds = false;
		}
		return request;
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

        

	public Vector GetLinkSmartID(String subRequest){
		
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
		before ="GetLinkSmartID";
		after = "GetLinkSmartID>";

		if((subRequest.indexOf("GetLinkSmartID")!=-1) && (subRequest.endsWith("/>"))) {
			result = "";
			this.nrArgs= nArgs;
			return V;
		}
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector CreateWS(String subRequest){
		
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
		before ="CreateWS";
		after = "CreateWS>";

		if((subRequest.indexOf("CreateWS")!=-1) && (subRequest.endsWith("/>"))) {
			result = "";
			this.nrArgs= nArgs;
			return V;
		}
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector GetStatus(String subRequest){
		
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
		before ="GetStatus";
		after = "GetStatus>";

		if((subRequest.indexOf("GetStatus")!=-1) && (subRequest.endsWith("/>"))) {
			result = "";
			this.nrArgs= nArgs;
			return V;
		}
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector GetProperty(String subRequest){
		
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
		before ="GetProperty";
		after = "GetProperty>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<Property",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<Property";
			after = "</Property>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.indexOf(after);
			V.addElement(result.substring(0, index));
			this.nrArgs = nArgs;
			return V;
		}
		}
		return null;
	}
	public Vector GetHasError(String subRequest){
		
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
		before ="GetHasError";
		after = "GetHasError>";

		if((subRequest.indexOf("GetHasError")!=-1) && (subRequest.endsWith("/>"))) {
			result = "";
			this.nrArgs= nArgs;
			return V;
		}
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector GetErrorMessage(String subRequest){
		
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
		before ="GetErrorMessage";
		after = "GetErrorMessage>";

		if((subRequest.indexOf("GetErrorMessage")!=-1) && (subRequest.endsWith("/>"))) {
			result = "";
			this.nrArgs= nArgs;
			return V;
		}
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector GetDiscoveryInfo(String subRequest){
		
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
		before ="GetDiscoveryInfo";
		after = "GetDiscoveryInfo>";

		if((subRequest.indexOf("GetDiscoveryInfo")!=-1) && (subRequest.endsWith("/>"))) {
			result = "";
			this.nrArgs= nArgs;
			return V;
		}
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector GetDACEndpoint(String subRequest){
		
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
		before ="GetDACEndpoint";
		after = "GetDACEndpoint>";

		if((subRequest.indexOf("GetDACEndpoint")!=-1) && (subRequest.endsWith("/>"))) {
			result = "";
			this.nrArgs= nArgs;
			return V;
		}
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector GetWSEndpoint(String subRequest){
		
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
		before ="GetWSEndpoint";
		after = "GetWSEndpoint>";

		if((subRequest.indexOf("GetWSEndpoint")!=-1) && (subRequest.endsWith("/>"))) {
			result = "";
			this.nrArgs= nArgs;
			return V;
		}
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector GetLinkSmartWSEndpoint(String subRequest){
		
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
		before ="GetLinkSmartWSEndpoint";
		after = "GetLinkSmartWSEndpoint>";

		if((subRequest.indexOf("GetLinkSmartWSEndpoint")!=-1) && (subRequest.endsWith("/>"))) {
			result = "";
			this.nrArgs= nArgs;
			return V;
		}
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector GetWSDL(String subRequest){
		
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
		before ="GetWSDL";
		after = "GetWSDL>";

		if((subRequest.indexOf("GetWSDL")!=-1) && (subRequest.endsWith("/>"))) {
			result = "";
			this.nrArgs= nArgs;
			return V;
		}
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector SetLinkSmartID(String subRequest){
		
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
		before ="SetLinkSmartID";
		after = "SetLinkSmartID>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<LinkSmartID",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<LinkSmartID";
			after = "</LinkSmartID>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.indexOf(after);
			V.addElement(result.substring(0, index));
			this.nrArgs = nArgs;
			return V;
		}
		}
		return null;
	}
	public Vector SetStatus(String subRequest){
		
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
		before ="SetStatus";
		after = "SetStatus>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<Status",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<Status";
			after = "</Status>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.indexOf(after);
			V.addElement(result.substring(0, index));
			this.nrArgs = nArgs;
			return V;
		}
		}
		return null;
	}
	public Vector SetDACEndpoint(String subRequest){
		
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
		before ="SetDACEndpoint";
		after = "SetDACEndpoint>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<DACEndpoint",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<DACEndpoint";
			after = "</DACEndpoint>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.indexOf(after);
			V.addElement(result.substring(0, index));
			this.nrArgs = nArgs;
			return V;
		}
		}
		return null;
	}
	public Vector SetProperty(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 2;
		V = new Vector(nArgs);
		before ="SetProperty";
		after = "SetProperty>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<Property",0)!=-1) && (subRequest.indexOf("<Value",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<Property";
			after = "</Property>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.indexOf(after);
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			before = "<Value";
			after = "</Value>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.indexOf(after);
			V.addElement(result.substring(0, index));
			this.nrArgs = nArgs;
			return V;
		}
		}
		return null;
	}
	public Vector Stop(String subRequest){
		
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
		before ="Stop";
		after = "Stop>";

		if((subRequest.indexOf("Stop")!=-1) && (subRequest.endsWith("/>"))) {
			result = "";
			this.nrArgs= nArgs;
			return V;
		}
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector StopWS(String subRequest){
		
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
		before ="StopWS";
		after = "StopWS>";

		if((subRequest.indexOf("StopWS")!=-1) && (subRequest.endsWith("/>"))) {
			result = "";
			this.nrArgs= nArgs;
			return V;
		}
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}
	public Vector StopLinkSmartWS(String subRequest){
		
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
		before ="StopLinkSmartWS";
		after = "StopLinkSmartWS>";

		if((subRequest.indexOf("StopLinkSmartWS")!=-1) && (subRequest.endsWith("/>"))) {
			result = "";
			this.nrArgs= nArgs;
			return V;
		}
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			this.nrArgs = nArgs;
			return V;
		}
		return null;
	}

	
}