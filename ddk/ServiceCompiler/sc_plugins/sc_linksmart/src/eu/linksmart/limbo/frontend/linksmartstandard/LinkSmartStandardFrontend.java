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
package eu.linksmart.limbo.frontend.linksmartstandard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import eu.linksmart.limbo.frontend.Frontend;
import eu.linksmart.limbo.repository.Repository;

@Component(properties={
		"limbo.platform=.*"
})
public class LinkSmartStandardFrontend implements Frontend {

	
	private static Logger log = Logger.getLogger(LinkSmartStandardFrontend.class.getName());
	private Repository repository;
	private ComponentContext context;
	
	protected void activate(ComponentContext ctxt)  {
		this.context = ctxt;
	}
	
	protected void deactivate(ComponentContext ctxt) {	
	}
	
	@Reference
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	
	public void unsetRepository(Repository repository) {
		this.repository = null;
	}
	
	
	public void process() {
		Definition definition = null;
		try {
			 definition = this.repository.getWSDL();
		} catch (Exception e) {
			log.log(Level.SEVERE, "LinkSmart Standard Frontend error getting wsdl from Repository!");
			e.printStackTrace();
		}
		if(!definition.equals(null)) {
			try {
				WSDLFactory factory = WSDLFactory.newInstance();
				WSDLReader reader = factory.newWSDLReader();
		
				reader.setFeature("javax.wsdl.verbose", false);
				Definition linksmartStandardDefinition = null;
				new File("resources/").mkdirs();
			    this.copyFileFromInputStream(LinkSmartStandardFrontend.class.getResourceAsStream("/resources/LinkSmartWS.wsdl"), new File("resources/LinkSmartWS.wsdl"));
				linksmartStandardDefinition = reader.readWSDL(null,new File("resources/LinkSmartWS.wsdl").getAbsolutePath());
				for(QName qName: (Set<QName>)linksmartStandardDefinition.getServices().keySet())
					definition.addService((Service)linksmartStandardDefinition.getServices().get(qName));
				for (QName bindingName: (Set<QName>)linksmartStandardDefinition.getBindings().keySet()) 
					definition.addBinding((Binding)linksmartStandardDefinition.getBindings().get(bindingName));
				for (QName portType: (Set<QName>)linksmartStandardDefinition.getPortTypes().keySet()) 
					definition.addPortType((PortType)linksmartStandardDefinition.getPortTypes().get(portType));
			} catch (WSDLException e) {
				e.printStackTrace();
			}
		}		
	}
	
	private void copyFileFromInputStream(InputStream source, File dst) {
		if(source!=null) {
	    	if(!dst.exists()) {
	    		try {
					dst.createNewFile();
		    		OutputStream out = new FileOutputStream(dst);
		    		// Transfer bytes from in to out
		    		byte[] buf = new byte[1024];
		        int len;
		        while ((len = source.read(buf)) > 0) {
		            out.write(buf, 0, len);
		        }
		        source.close();
		        out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    }
	}

}
