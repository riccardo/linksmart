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

package eu.linksmart.limbo.lockmanager.upnp;

import java.util.HashMap;
import java.util.Enumeration;



import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import eu.linksmart.limbo.lockmanager.*;



public class LinkSmartServicePortUPnPService implements UPnPService {

	final private String SERVICE_ID = "urn:upnp-org:serviceId:LinkSmartServicePort";
	final private String SERVICE_TYPE = "urn:schemas-upnp-org:service:LinkSmartServicePort:1";
	final private String VERSION ="1";
	
	private PropertyValueStateVariable PropertyValue;
	private ErrorMessageStateVariable ErrorMessage;
	private LinkSmartWSEndpointStateVariable LinkSmartWSEndpoint;
	private DiscoveryInfoStateVariable DiscoveryInfo;
	private wsdlStateVariable wsdl;
	private LinkSmartIDStateVariable LinkSmartID;
	private WSEndpointStateVariable WSEndpoint;
	private HasErrorStateVariable HasError;
	private ValueStateVariable Value;
	private PropertyStateVariable Property;
	private DACEndpointStateVariable DACEndpoint;
	private StatusStateVariable Status;
	private UPnPStateVariable[] states;
	private HashMap actions = new HashMap();
	private LockManagerDeviceDevice device;
	
	private Hashtable<String, String> properties = new Hashtable<String, String>();

	public LinkSmartServicePortUPnPService(LockManagerDeviceDevice device) {
		this.device = device;
		PropertyValue = new PropertyValueStateVariable();
		ErrorMessage = new ErrorMessageStateVariable();
		LinkSmartWSEndpoint = new LinkSmartWSEndpointStateVariable();
		DiscoveryInfo = new DiscoveryInfoStateVariable();
		wsdl = new wsdlStateVariable();
		LinkSmartID = new LinkSmartIDStateVariable();
		WSEndpoint = new WSEndpointStateVariable();
		HasError = new HasErrorStateVariable();
		Value = new ValueStateVariable();
		Property = new PropertyStateVariable();
		DACEndpoint = new DACEndpointStateVariable();
		Status = new StatusStateVariable();
		this.states = new UPnPStateVariable[]{PropertyValue,ErrorMessage,LinkSmartWSEndpoint,DiscoveryInfo,wsdl,LinkSmartID,WSEndpoint,HasError,Value,Property,DACEndpoint,Status};

		UPnPAction SetProperty = new SetPropertyAction(Property,Value, this);
		actions.put(SetProperty.getName(),SetProperty);
		UPnPAction GetWSDL = new GetWSDLAction(wsdl, this);
		actions.put(GetWSDL.getName(),GetWSDL);
		UPnPAction GetErrorMessage = new GetErrorMessageAction(ErrorMessage, this);
		actions.put(GetErrorMessage.getName(),GetErrorMessage);
		UPnPAction CreateWS = new CreateWSAction(WSEndpoint, this);
		actions.put(CreateWS.getName(),CreateWS);
		UPnPAction GetHasError = new GetHasErrorAction(HasError, this);
		actions.put(GetHasError.getName(),GetHasError);
		UPnPAction GetLinkSmartWSEndpoint = new GetLinkSmartWSEndpointAction(LinkSmartWSEndpoint, this);
		actions.put(GetLinkSmartWSEndpoint.getName(),GetLinkSmartWSEndpoint);
		UPnPAction SetDACEndpoint = new SetDACEndpointAction(DACEndpoint, this);
		actions.put(SetDACEndpoint.getName(),SetDACEndpoint);
		UPnPAction SetLinkSmartID = new SetLinkSmartIDAction(LinkSmartID, this);
		actions.put(SetLinkSmartID.getName(),SetLinkSmartID);
		UPnPAction SetStatus = new SetStatusAction(Status, this);
		actions.put(SetStatus.getName(),SetStatus);
		UPnPAction GetLinkSmartID = new GetLinkSmartIDAction(LinkSmartID, this);
		actions.put(GetLinkSmartID.getName(),GetLinkSmartID);
		UPnPAction GetStatus = new GetStatusAction(Status, this);
		actions.put(GetStatus.getName(),GetStatus);
		UPnPAction GetDiscoveryInfo = new GetDiscoveryInfoAction(DiscoveryInfo, this);
		actions.put(GetDiscoveryInfo.getName(),GetDiscoveryInfo);
		UPnPAction StopLinkSmartWS = new StopLinkSmartWSAction( this);
		actions.put(StopLinkSmartWS.getName(),StopLinkSmartWS);
		UPnPAction GetProperty = new GetPropertyAction(Property,PropertyValue, this);
		actions.put(GetProperty.getName(),GetProperty);
		UPnPAction GetDACEndpoint = new GetDACEndpointAction(DACEndpoint, this);
		actions.put(GetDACEndpoint.getName(),GetDACEndpoint);
		UPnPAction Stop = new StopAction( this);
		actions.put(Stop.getName(),Stop);
		UPnPAction GetWSEndpoint = new GetWSEndpointAction(WSEndpoint, this);
		actions.put(GetWSEndpoint.getName(),GetWSEndpoint);
		UPnPAction StopWS = new StopWSAction( this);
		actions.put(StopWS.getName(),StopWS);
		DiscoveryInfo.set("Limbo generated WS code");
		HasError.set(false);
		
		InputStream is = LinkSmartServicePortUPnPService.class.getResourceAsStream("/resources/LockManagerDevice.wsdl");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        StringBuilder sb = new StringBuilder();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
		    }
		} catch (IOException e) {
		   e.printStackTrace();
		}           
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public UPnPAction getAction(String name) {
		return (UPnPAction)actions.get(name);
	}

	public UPnPAction[] getActions() {
		return (UPnPAction[])(actions.values()).toArray(new UPnPAction[]{});
	}

	public String getId() {
		return SERVICE_ID;
	}

	public UPnPStateVariable getStateVariable(String name) {

		if (name.equals(PropertyValue.getName())) return PropertyValue;
		else if (name.equals(ErrorMessage.getName())) return ErrorMessage;
		else if (name.equals(LinkSmartWSEndpoint.getName())) return LinkSmartWSEndpoint;
		else if (name.equals(DiscoveryInfo.getName())) return DiscoveryInfo;
		else if (name.equals(wsdl.getName())) return wsdl;
		else if (name.equals(LinkSmartID.getName())) return LinkSmartID;
		else if (name.equals(WSEndpoint.getName())) return WSEndpoint;
		else if (name.equals(HasError.getName())) return HasError;
		else if (name.equals(Value.getName())) return Value;
		else if (name.equals(Property.getName())) return Property;
		else if (name.equals(DACEndpoint.getName())) return DACEndpoint;
		else if (name.equals(Status.getName())) return Status;
		return null;
	}

	public UPnPStateVariable[] getStateVariables() {
		return states;
	}

	public String getType() {
		return SERVICE_TYPE;
	}

	public String getVersion() {
		return VERSION;
	}

	public String GetLinkSmartID(){
		return this.LinkSmartID.get();
	}

	public String CreateWS(){
		this.WSEndpoint.set(LockManagerServlet.getEndpoint());
		this.LinkSmartWSEndpoint.set(LinkSmartServicePortServlet.getEndpoint());
		return this.WSEndpoint.get();
	}

	public String GetStatus(){
		return this.Status.get();
	}

	public String GetProperty(String Property ){
		if (Property.equals("devicepropertyxml")) {
			String props = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><root>";
			
			Enumeration<String> en =properties.keys();
			while (en.hasMoreElements()) {
				String key = en.nextElement();
				String value = properties.get(key);
				props+= "<" + key +" xmlns=\"linksmart\">"+ value+ "</"+key +">";
			}
			props+="</root>";
			return props;
			
			
		}
		else {
			String res = this.properties.get(Property);
			if (res == null) res = "";
			return res;
		}
	}

	public boolean GetHasError(){
		return this.HasError.get();
	}

	public String GetErrorMessage(){
		return this.ErrorMessage.get();
	}

	public String GetDiscoveryInfo(){
		return this.DiscoveryInfo.get();
	}

	public String GetDACEndpoint(){
		return this.DACEndpoint.get();
	}

	public String GetWSEndpoint(){
		return this.WSEndpoint.get();
	}

	public String GetLinkSmartWSEndpoint(){
		return this.LinkSmartWSEndpoint.get();
	}

	public String GetWSDL(){
		return this.wsdl.get();
	}

	public void SetLinkSmartID(String LinkSmartID ){
		this.LinkSmartID.set(LinkSmartID);
	}

	public void SetStatus(String Status ){
		this.Status.set(Status);
	}

	public void SetDACEndpoint(String DACEndpoint ){
		this.DACEndpoint.set(DACEndpoint);
	}

	public void SetProperty(String Property, String Value ){
		this.properties.put(Property, Value);
		this.device.changeProperty("linksmart", Property + "_&%_" + Value);
	}

	public void Stop(){
		//TODO
	}

	public void StopWS(){
		//TODO
	}

	public void StopLinkSmartWS(){
		//TODO
	}
	


}



