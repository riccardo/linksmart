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
package eu.linksmart.limbo.soap.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import eu.linksmart.limbo.constants.LimboConstants;
import eu.linksmart.limbo.frontend.Frontend;
import eu.linksmart.limbo.repository.Repository;
import com.ibm.wsdl.PortImpl;
import com.ibm.wsdl.ServiceImpl;
import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;




@Component(properties={"limbo.platform=.*"})
public class SOAPFrontend implements Frontend {

	private static Logger log = Logger.getLogger(SOAPFrontend.class.getName());
	private Repository repository;
	private HashMap<String,String> portAddresses;
	
	public SOAPFrontend() {
	}
	
	protected void activate(ComponentContext ctxt)  {
		this.portAddresses = new HashMap<String,String>();
	}
	
	protected void deactivate(ComponentContext ctxt) {	
	}

	@SuppressWarnings("unchecked")
	public void process() {
		Definition def = null;
		try {
			def = repository.getWSDL();
		} catch (Exception e1) {
		
			e1.printStackTrace();
		} 
		
	
		QName serviceQName = null;
		for (QName service: (Set<QName>)def.getServices().keySet()) {
			Collection<String> portNames = def.getService(service).getPorts().keySet();
			for(String portQName : portNames) {
				if(!this.portAddresses.containsKey(portQName)) {
					String address = getPortURL(def, service);
			//		System.out.println("address: "+address);
					URL url = null;
					try {
						url = new URL(address);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
			//		System.out.println("portQName: "+portQName);
					this.portAddresses.put(portQName, url.getPath());
				}
			}
		}

		for (QName qName: (Set<QName>)def.getServices().keySet()) {
			serviceQName = qName;
			def.getService(serviceQName).getExtensibilityElements();
			Vector<String> bindingOperations = new Vector<String>();
			for (QName bindingName: (Set<QName>)def.getBindings().keySet()) {
				Binding binding = def.getBinding(bindingName);
				for (BindingOperation operation: (List<BindingOperation>)binding.getBindingOperations()) {
					try {
						processBindingOperation(def, serviceQName, operation);
						bindingOperations.add(operation.getOperation().getName());
					} catch (Exception e) {
						log.log(Level.SEVERE, "Error in SOAP frontend processing");
						e.printStackTrace();
					}
				}
			}
		}
	
		this.repository.addParameter(LimboConstants.PORTADDRESS.toString(), this.portAddresses);
	}
	
	@SuppressWarnings("unchecked")
	private void processBindingOperation(Definition definition, QName service, BindingOperation bindingOperation) {
		List eeList = bindingOperation.getExtensibilityElements();
		for(Object ee : eeList) {
			if(ee instanceof SOAPOperationImpl) {
				this.repository.addParameter(LimboConstants.SOAP.toString(), "true");
				String address = getPortURL(definition, service);
				try {
					LinkedList<String> handlers = (LinkedList<String>)this.repository.getParameter(LimboConstants.HANDLERSLIST);
					if(!handlers.contains("SOAPHandler"))
						handlers.add("SOAPHandler");
				} catch (Exception e) {
					log.log(Level.SEVERE, "Error ProcessingBindingOperation");
					e.printStackTrace();
				}	
			}
			else
				this.repository.addParameter(LimboConstants.SOAP.toString(), "false");
			break;	
		}
	}
	
	
	/**Moved to OSGi backend**/
	@SuppressWarnings("unchecked")
	private String getPortURL(Definition definition, QName serviceQName){
		ServiceImpl service = (ServiceImpl)definition.getService(serviceQName);
		Collection<PortImpl> ports = service.getPorts().values();
		for(PortImpl port : ports) {
			List eeList = port.getExtensibilityElements();
			for(Object ee : eeList) {
				if(ee instanceof SOAPAddressImpl) {
					SOAPAddressImpl soapAdd = (SOAPAddressImpl)ee;
					return soapAdd.getLocationURI(); 
				}
			}
		}
		return null;
	}
	
	
	@Reference
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	
	public void unsetRepository(Repository repository) {
		this.repository = null;
	}
}
