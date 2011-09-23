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

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Component;


import eu.linksmart.limbo.constants.LimboConstants;
import eu.linksmart.limbo.repository.Repository;

@Component
public class RepositoryComponent implements Repository {

	private HashMap<String,Object> configuration;
	private Definition definition;
	
	
	public RepositoryComponent() {
		this.configuration = new HashMap<String,Object>();
	}
	
	protected void activate(ComponentContext ctxt) throws Exception {
		initConfiguration();
		// Just putting all system properties into configuration...
		for (Object key : System.getProperties().keySet()) {
			configuration.put((String) key, System.getProperty((String)key));
		}
		try {
			WSDLFactory factory = WSDLFactory.newInstance();
			WSDLReader reader = factory.newWSDLReader();
	
			reader.setFeature("javax.wsdl.verbose", false);
			
			this.definition = reader.readWSDL(null, new File((String)getParameter(LimboConstants.WSDLFILE)).getAbsolutePath());
		}catch(Exception e) {e.printStackTrace();}
	}
	
	private void initConfiguration() {
		// Default configuration
		configuration.put(LimboConstants.PLATFORM.toString(), "standalone");
		configuration.put(LimboConstants.LANGUAGE.toString(), "jse");
		configuration.put(LimboConstants.PROTOCOL.toString(), "TCP");
		configuration.put(LimboConstants.GENERATIONTYPE.toString(), "all");
		
		configuration.put(LimboConstants.HANDLERSLIST.toString(), new LinkedList<String>()); 
		configuration.put(LimboConstants.OUTPUTDIRECTORY.toString(), "generated");
		configuration.put(LimboConstants.LOGHANDLER.toString(), "false");
		configuration.put(LimboConstants.PROBEHANDLER.toString(), "false");
		configuration.put(LimboConstants.UPNP.toString(), "false");
		
	}

	public HashMap<String,Object> getLimboConfiguration() {
		return this.configuration;
	}

	
	public URI getOntologyURI() throws Exception {
		return getLinkSmartOntologyExtension(new File((String)getParameter(LimboConstants.WSDLFILE)));
	}
	
	/**
	 * <b>getLinkSmartOntologyExtension</b>
	 * Returns a URI for device ontology description.
	 * @param wsdlFile File containing the WSDL specification.
	 * @return URI for device ontology description.
	 */
	@SuppressWarnings("unchecked")
	public URI getLinkSmartOntologyExtension (File wsdlFile) {
		URI result = null;
		try {

			WSDLFactory factory = WSDLFactory.newInstance();
			WSDLReader reader = factory.newWSDLReader();

			reader.setFeature("javax.wsdl.verbose", false);
			Definition definition = reader.readWSDL(null, wsdlFile.toString());

			Iterator iterator =  definition.getBindings().keySet().iterator();
			while (iterator.hasNext()) {
				Binding binding = (Binding)definition.getBindings().get(iterator.next());
				for (int i = 0; i < binding.getExtensibilityElements().size(); i++) {
					if (binding.getExtensibilityElements().get(i) instanceof UnknownExtensibilityElement) {
						UnknownExtensibilityElement ee 
							= (UnknownExtensibilityElement)binding.getExtensibilityElements().get(i);
					
						if ("http://linksmart.eu/".equals(ee.getElementType().getNamespaceURI()) &&
								"binding".equals(ee.getElementType().getLocalPart())) {
	
							result = new URI(ee.getElement().getAttribute("device"));
							if (result != null) {
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	public void addParameter(String parameter, Object value) {
		this.configuration.put(parameter, value);
	}
	
	
	public Definition getWSDL() throws Exception {
		
		return this.definition;
	}

	
	public File getWSDLFile() throws Exception {
		// TODO Auto-generated method stub
		return new File((String)getParameter(LimboConstants.WSDLFILE));
	}

	
	public Object getParameter(LimboConstants constant) {
		return getLimboConfiguration().get(constant.toString());
	}
}
