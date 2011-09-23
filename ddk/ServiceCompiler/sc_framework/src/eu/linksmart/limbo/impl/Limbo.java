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
package eu.linksmart.limbo.impl;

import java.util.LinkedList;
import java.util.logging.Logger;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;


import eu.linksmart.limbo.backend.Backend;
import eu.linksmart.limbo.constants.BackendTypes;
import eu.linksmart.limbo.constants.LimboConstants;
import eu.linksmart.limbo.frontend.Frontend;
import eu.linksmart.limbo.generator.Generator;
import eu.linksmart.limbo.repository.Repository;

@Component(immediate=true)
public class Limbo implements Generator {
	private LinkedList<Backend> backends = new LinkedList<Backend>();
	private LinkedList<Frontend> frontends = new LinkedList<Frontend>();
	
	Logger log = Logger.getLogger(Limbo.class.getName());
	
	private ComponentContext context;
	private Repository repository;
	protected void activate(ComponentContext context) {
		this.context = context;
	}
	
	@Reference(type='+')
	public void setBackend(Backend e) {
		backends.add(e);		
	}

	@Reference(type='+')
	public void setFrontend(Frontend f) {
		frontends.add(f);		
	}

	@Reference
	public void setRepository(Repository r) {
	//	log.info("Add repository: " + r);
		repository = r;		
	}

	private ServiceReference getServiceReference(String clazz, Object service) {
		ServiceReference result = null;
		try {
			context.getBundleContext().getServiceReferences(service.getClass().getName(), null);
			ServiceReference[] srs = context.getBundleContext().getServiceReferences(clazz, null);
			if (srs != null) {
				for (ServiceReference sr: srs) {
					if (context.getBundleContext().getService(sr) == service) {
						result = sr;
						break;
					}
				}
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace(); // Cannot happen
		}
		return result;
	}
	
	private ServiceReference getFrontendServiceReference(Frontend frontend) {
		return getServiceReference(Frontend.class.getName(), frontend);
	}
	
	private ServiceReference getBackendServiceReference(Backend backend) {
		return getServiceReference(Backend.class.getName(), backend);
	}
	
	private LinkedList<Backend> insertBackendOrdered(Backend b, int backendPriority, LinkedList<Backend> backends) {
		if(backends.size()== 0)
			backends.addFirst(b);
		else {
			BackendTypes[] backendTypes = BackendTypes.values();
			int currentBackendPriority = -1;
			for(int i = 0; i < backends.size(); i++) {
				String backendType = (String)getBackendServiceReference(backends.get(i)).getProperty(LimboConstants.GENERATES.toString());
				for(BackendTypes bts : backendTypes) {
					if(bts.toString().equals(backendType))
						currentBackendPriority = bts.getPriority();
				}
				if(backendPriority < currentBackendPriority) {
					backends.add(i, b);
					return backends;
				}
			}
			backends.addLast(b);
		}
		return backends;
	}
	
	//This method orders the List of Backends by its generation feature:
	//FIXME: Extend to other types of backends! Handle of constants improvement needed!
	private LinkedList<Backend> orderBackends() {
		
		LinkedList<Backend> orderedBackends = new LinkedList<Backend>();
		for(Backend b : backends) {
			
			String generationFeature = (String) getBackendServiceReference(b).getProperty(LimboConstants.GENERATES.toString());
			int backendPriority = -1;
			BackendTypes[] backendTypes = BackendTypes.values();
			
			for(BackendTypes bt : backendTypes) {
				if(bt.toString().equals(generationFeature))
					backendPriority = bt.getPriority();
			}
			if(generationFeature != null && backendPriority!=-1) {
				orderedBackends = insertBackendOrdered(b,backendPriority, orderedBackends);
				
			}
			else
				orderedBackends.addFirst(b);
		}
		
		return orderedBackends;
		
	}
	
	private boolean matchComponent(String[] requiredValues, String[] providedValues) {
		
		int i = 0;
		boolean matchedProperty = false;
		for(String s : providedValues) {
			matchedProperty = false;
			if(s != null) {
				String[] values = s.split(";");
				for(String value : values) {
					if(requiredValues[i].matches(value) || value.equals(requiredValues[i])) {
						matchedProperty = true;
					}
				}
				if(!matchedProperty) 
					return false;
			}
			i++;
		}
		return true;
	}
	
	
	public void generateCode(String[] config) throws Exception {
		String requiredPlatform = (String)repository.getParameter(LimboConstants.PLATFORM);
		String requiredLanguage = (String)repository.getParameter(LimboConstants.LANGUAGE);
		String requiredGenerationType = (String)repository.getParameter(LimboConstants.GENERATIONTYPE);
		String requiredProtocol = (String)repository.getParameter(LimboConstants.PROTOCOL);
		String wsdlFileLocation = (String)repository.getParameter(LimboConstants.WSDLFILE);
		
		log.info("Limbo Configuration Values: " + 
				"\n Language: "+ requiredLanguage +
				"\n Platform: "+ requiredPlatform + 
				"\n GenerationType: " + requiredGenerationType +
				"\n Protocol: " + requiredProtocol +
				"\n UPnP: " + repository.getParameter(LimboConstants.UPNP) +
				"\n wsdlFile: "+ wsdlFileLocation);
		
		// Find a frontend that matches
		Frontend matchingFrontend = null;
		for (Frontend frontend: frontends) {
			String providedPlatform 
				= (String) getFrontendServiceReference(frontend).getProperty(LimboConstants.PLATFORM.toString());
			if (providedPlatform != null && requiredPlatform.matches(providedPlatform)) {
				matchingFrontend = frontend;
				matchingFrontend.process();
			}
		}
		if (matchingFrontend == null) {
			throw new Exception("No matching frontend found (platform = " + requiredPlatform + ")");
		}
		// Find a backend that matches
		Backend matchingBackend = null;
		LinkedList<Backend> backnds = orderBackends();
		for (Backend backend : backnds) {
			String providedPlatform 
				= (String) getBackendServiceReference(backend).getProperty(LimboConstants.PLATFORM.toString());
			String providedLanguage
		        = (String) getBackendServiceReference(backend).getProperty(LimboConstants.LANGUAGE.toString());
		    String providedGenerationType
		        = (String) getBackendServiceReference(backend).getProperty(LimboConstants.GENERATIONTYPE.toString());
		    boolean matched = matchComponent(
		    		new String[]{requiredPlatform, requiredLanguage, requiredGenerationType}, 
		    					new String[]{providedPlatform,providedLanguage,providedGenerationType});
			if (matched) {
				matchingBackend = backend;
				log.info("Backend Generate called on : "+backend.getClass().getName());
				try {
					matchingBackend.generate();
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}
		if (matchingBackend == null) {
			throw new Exception("No matching backend found (platform = " + requiredPlatform + ")");
		}
	}

	public void unsetBackend(Backend b) {
		backends.remove(b);
	}

	public void unsetFrontend(Frontend f) {
		frontends.remove(f);
	}
	
	public void unsetRepository(Repository r) {
		repository = null;
	}
}
