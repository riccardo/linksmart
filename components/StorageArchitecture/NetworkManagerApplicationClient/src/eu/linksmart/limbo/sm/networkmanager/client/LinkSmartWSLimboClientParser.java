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

public class LinkSmartWSLimboClientParser {

	private java.util.Vector args;
	private String operation;
	private int nrArgs;
	private String request;
	private String SOAPAction;

	public LinkSmartWSLimboClientParser(String total, String soapaction){
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
		op = this.GetPhysicalDiscoveryInfo(result);
		this.operation = "GetPhysicalDiscoveryInfo";
		if(op == null){
		op = this.GetProperty(result);
		this.operation = "GetProperty";
		if(op == null){
		op = this.GetHasError(result);
		this.operation = "GetHasError";
		if(op == null){
		op = this.SetLinkSmartID(result);
		this.operation = "SetLinkSmartID";
		if(op == null){
		op = this.SetProperty(result);
		this.operation = "SetProperty";
		if(op == null){
		op = this.SetStatus(result);
		this.operation = "SetStatus";
		if(op == null){
		op = this.GetErrorMessage(result);
		this.operation = "GetErrorMessage";
		if(op == null){
		op = this.SetDACEndpoint(result);
		this.operation = "SetDACEndpoint";
		if(op == null){
		op = this.GetStatus(result);
		this.operation = "GetStatus";
		if(op == null){
		op = this.StopDevice(result);
		this.operation = "StopDevice";
		if(op == null){
		op = this.GetLinkSmartID(result);
		this.operation = "GetLinkSmartID";
		if(op == null){
		op = this.StartDevice(result);
		this.operation = "StartDevice";
		if(op == null){
		op = this.GetDACEndpoint(result);
		this.operation = "GetDACEndpoint";
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

        

	public Vector GetPhysicalDiscoveryInfo(String subRequest){
		
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
		before ="getPhysicalDiscoveryInfoResponse";
		after = "getPhysicalDiscoveryInfoResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<info";
			after = "</info>";
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
		before ="getPropertyResponse";
		after = "getPropertyResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<propvalue";
			after = "</propvalue>";
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
	public Vector GetHasError(String subRequest){
		
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
		before ="getHasErrorResponse";
		after = "getHasErrorResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<error";
			after = "</error>";
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
	public Vector SetLinkSmartID(String subRequest){
		
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
		before ="setLinkSmartIDResponse";
		after = "setLinkSmartIDResponse>";

		if((subRequest.indexOf("setLinkSmartIDResponse") != -1) && (subRequest.endsWith("/>"))) {
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
	public Vector SetProperty(String subRequest){
		
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
		before ="setPropertyResponse";
		after = "setPropertyResponse>";

		if((subRequest.indexOf("setPropertyResponse") != -1) && (subRequest.endsWith("/>"))) {
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
	public Vector SetStatus(String subRequest){
		
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
		before ="setStatusResponse";
		after = "setStatusResponse>";

		if((subRequest.indexOf("setStatusResponse") != -1) && (subRequest.endsWith("/>"))) {
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
	public Vector GetErrorMessage(String subRequest){
		
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
		before ="getErrorMessageResponse";
		after = "getErrorMessageResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<error";
			after = "</error>";
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
	public Vector SetDACEndpoint(String subRequest){
		
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
		before ="setDACEndpointResponse";
		after = "setDACEndpointResponse>";

		if((subRequest.indexOf("setDACEndpointResponse") != -1) && (subRequest.endsWith("/>"))) {
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
	public Vector GetStatus(String subRequest){
		
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
		before ="getStatusResponse";
		after = "getStatusResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<status";
			after = "</status>";
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
	public Vector StopDevice(String subRequest){
		
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
		before ="stopDeviceResponse";
		after = "stopDeviceResponse>";

		if((subRequest.indexOf("stopDeviceResponse") != -1) && (subRequest.endsWith("/>"))) {
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
	public Vector GetLinkSmartID(String subRequest){
		
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
		before ="getLinkSmartIDResponse";
		after = "getLinkSmartIDResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<linksmartid";
			after = "</linksmartid>";
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
	public Vector StartDevice(String subRequest){
		
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
		before ="startDeviceResponse";
		after = "startDeviceResponse>";

		if((subRequest.indexOf("startDeviceResponse") != -1) && (subRequest.endsWith("/>"))) {
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
	public Vector GetDACEndpoint(String subRequest){
		
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
		before ="getDACEndpointResponse";
		after = "getDACEndpointResponse>";
		if(subRequest.indexOf(before)!=-1 && subRequest.endsWith(after)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<endpoint";
			after = "</endpoint>";
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