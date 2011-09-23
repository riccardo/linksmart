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
package eu.linksmart.limbo.frontend.upnp;

import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import eu.linksmart.limbo.constants.LimboConstants;
import eu.linksmart.limbo.frontend.Frontend;
import eu.linksmart.limbo.frontend.ontology.OntologyHandler;
import eu.linksmart.limbo.repository.Repository;

@Component(properties={
		"limbo.platform=.*"
})
public class UPnPFrontend implements Frontend {

	
	private static Logger log = Logger.getLogger(UPnPFrontend.class.getName());
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
		OntologyHandler oh = null;
		try {
		URI ontURI = this.repository.getOntologyURI();
		if(ontURI != null) {
			oh = new OntologyHandler(new URI("file:resources/Device.owl"));
			Map<String,String> map = oh.getLinkSmartOnProperties(ontURI);
			if(map != null)
				this.repository.addParameter(LimboConstants.UPNP_ONTOLOGY.toString(), "true");
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
