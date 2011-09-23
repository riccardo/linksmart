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

package eu.linksmart.limbo.filesystemdevice;

import java.util.Vector;
import java.util.Hashtable;

public class FileSystemDeviceParser {

	private java.util.Vector args;
	private String operation;
	private int nrArgs;
	private String request;
	private String SOAPAction;

	public FileSystemDeviceParser(String total, String soapaction){
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
		op = this.clearFile(result);
		this.operation = "clearFile";
		if(op == null){
		op = this.copy(result);
		this.operation = "copy";
		if(op == null){
		op = this.createDirectory(result);
		this.operation = "createDirectory";
		if(op == null){
		op = this.createFile(result);
		this.operation = "createFile";
		if(op == null){
		op = this.existsPath(result);
		this.operation = "existsPath";
		if(op == null){
		op = this.getDirectoryEntries(result);
		this.operation = "getDirectoryEntries";
		if(op == null){
		op = this.getFile(result);
		this.operation = "getFile";
		if(op == null){
		op = this.getFreeSpace(result);
		this.operation = "getFreeSpace";
		if(op == null){
		op = this.getID(result);
		this.operation = "getID";
		if(op == null){
		op = this.getSize(result);
		this.operation = "getSize";
		if(op == null){
		op = this.getStatFS(result);
		this.operation = "getStatFS";
		if(op == null){
		op = this.move(result);
		this.operation = "move";
		if(op == null){
		op = this.readFile(result);
		this.operation = "readFile";
		if(op == null){
		op = this.removeDirectory(result);
		this.operation = "removeDirectory";
		if(op == null){
		op = this.removeFile(result);
		this.operation = "removeFile";
		if(op == null){
		op = this.setFileProperties(result);
		this.operation = "setFileProperties";
		if(op == null){
		op = this.setFileProperty(result);
		this.operation = "setFileProperty";
		if(op == null){
		op = this.truncateFile(result);
		this.operation = "truncateFile";
		if(op == null){
		op = this.writeFile(result);
		this.operation = "writeFile";
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
			if(subRequest.startsWith(before) && subRequest.endsWith(after)){
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

        

	public Vector clearFile(String subRequest){
		
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
		before ="clearFile";
		after = "clearFile>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<path",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<path";
			after = "</path>";
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
	public Vector copy(String subRequest){
		
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
		before ="copy";
		after = "copy>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<source",0)!=-1) && (subRequest.indexOf("<destination",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<source";
			after = "</source>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.indexOf(after);
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			before = "<destination";
			after = "</destination>";
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
	public Vector createDirectory(String subRequest){
		
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
		before ="createDirectory";
		after = "createDirectory>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<path",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<path";
			after = "</path>";
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
	public Vector createFile(String subRequest){
		
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
		before ="createFile";
		after = "createFile>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<path",0)!=-1) && (subRequest.indexOf("<properties",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<path";
			after = "</path>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.indexOf(after);
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			before = "<properties";
			after = "</properties>";
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
	public Vector existsPath(String subRequest){
		
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
		before ="existsPath";
		after = "existsPath>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<path",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<path";
			after = "</path>";
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
	public Vector getDirectoryEntries(String subRequest){
		
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
		before ="getDirectoryEntries";
		after = "getDirectoryEntries>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<path",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<path";
			after = "</path>";
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
	public Vector getFile(String subRequest){
		
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
		before ="getFile";
		after = "getFile>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<path",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<path";
			after = "</path>";
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
	public Vector getFreeSpace(String subRequest){
		
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
		before ="getFreeSpace";
		after = "getFreeSpace>";

		if((subRequest.indexOf("getFreeSpace")!=-1) && (subRequest.endsWith("/>"))) {
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
	public Vector getID(String subRequest){
		
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
		before ="getID";
		after = "getID>";

		if((subRequest.indexOf("getID")!=-1) && (subRequest.endsWith("/>"))) {
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
	public Vector getSize(String subRequest){
		
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
		before ="getSize";
		after = "getSize>";

		if((subRequest.indexOf("getSize")!=-1) && (subRequest.endsWith("/>"))) {
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
	public Vector getStatFS(String subRequest){
		
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
		before ="getStatFS";
		after = "getStatFS>";

		if((subRequest.indexOf("getStatFS")!=-1) && (subRequest.endsWith("/>"))) {
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
	public Vector move(String subRequest){
		
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
		before ="move";
		after = "move>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<source",0)!=-1) && (subRequest.indexOf("<destination",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<source";
			after = "</source>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.indexOf(after);
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			before = "<destination";
			after = "</destination>";
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
	public Vector readFile(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 3;
		V = new Vector(nArgs);
		before ="readFile";
		after = "readFile>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<path",0)!=-1) && (subRequest.indexOf("<start",0)!=-1) && (subRequest.indexOf("<size",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<path";
			after = "</path>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.indexOf(after);
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			before = "<start";
			after = "</start>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.indexOf(after);
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			before = "<size";
			after = "</size>";
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
	public Vector removeDirectory(String subRequest){
		
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
		before ="removeDirectory";
		after = "removeDirectory>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<path",0)!=-1) && (subRequest.indexOf("<recursive",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<path";
			after = "</path>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.indexOf(after);
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			before = "<recursive";
			after = "</recursive>";
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
	public Vector removeFile(String subRequest){
		
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
		before ="removeFile";
		after = "removeFile>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<path",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<path";
			after = "</path>";
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
	public Vector setFileProperties(String subRequest){
		
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
		before ="setFileProperties";
		after = "setFileProperties>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<path",0)!=-1) && (subRequest.indexOf("<properties",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<path";
			after = "</path>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.indexOf(after);
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			before = "<properties";
			after = "</properties>";
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
	public Vector setFileProperty(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 3;
		V = new Vector(nArgs);
		before ="setFileProperty";
		after = "setFileProperty>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<path",0)!=-1) && (subRequest.indexOf("<propertiesName",0)!=-1) && (subRequest.indexOf("<propertiesValue",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<path";
			after = "</path>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.indexOf(after);
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			before = "<propertiesName";
			after = "</propertiesName>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.indexOf(after);
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			before = "<propertiesValue";
			after = "</propertiesValue>";
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
	public Vector truncateFile(String subRequest){
		
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
		before ="truncateFile";
		after = "truncateFile>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<path",0)!=-1) && (subRequest.indexOf("<size",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<path";
			after = "</path>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.indexOf(after);
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			before = "<size";
			after = "</size>";
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
	public Vector writeFile(String subRequest){
		
		int nArgs;
		Vector V;
		String result = null;
		String before;
		String after;
		int index = subRequest.lastIndexOf('<');
		StringTokenizer st;
		subRequest = subRequest.substring(0, index);
		nArgs = 3;
		V = new Vector(nArgs);
		before ="writeFile";
		after = "writeFile>";
		if((subRequest.indexOf(before)!=-1) && (subRequest.endsWith(after))) {
		if((subRequest.indexOf("<path",0)!=-1) && (subRequest.indexOf("<start",0)!=-1) && (subRequest.indexOf("<data",0)!=-1)) {
			st = new StringTokenizer(subRequest,">");
			before = st.nextToken();
			before = before.concat(">");
			result = subRequest.substring(before.length(), subRequest.lastIndexOf('<'));
			before = "<path";
			after = "</path>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.indexOf(after);
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			before = "<start";
			after = "</start>";
			result = result.substring(before.length());
			index = result.indexOf('>');
			result = result.substring(index+1);
			index = result.indexOf(after);
			V.addElement(result.substring(0, index));
			result = result.substring(index);
			result = result.substring(after.length());
			before = "<data";
			after = "</data>";
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

	
}