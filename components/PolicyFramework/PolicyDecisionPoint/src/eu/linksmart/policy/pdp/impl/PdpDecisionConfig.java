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
 * Copyright (C) 2006-2010 University of Reading,
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
package eu.linksmart.policy.pdp.impl;

import com.sun.xacml.finder.AttributeFinder;
import com.sun.xacml.finder.AttributeFinderModule;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.ResourceFinder;
import com.sun.xacml.finder.ResourceFinderModule;
import com.sun.xacml.finder.impl.CurrentEnvModule;

import eu.linksmart.policy.pdp.PdpAdmin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>LinkSmart PDP configuration</p>
 * 
 * <p>Bundles required finders (Resource, Attribute and Policy) for LinkSmart 
 * PDP.</p>
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 * 
 */
public class PdpDecisionConfig {

    /** {@link AttributeFinder} */
    private AttributeFinder attributeFinder  = null;

    /** {@link PolicyFinder} */
    private PolicyFinder policyFinder = null;

    /** {@link ResourceFinder} */
    private ResourceFinder resourceFinder  = null;
    
    /** {@link PdpAdmin} */
    private PdpAdmin pdpAdmin = null;

    /** No-args constructor */
    public PdpDecisionConfig() {
    	this(null, null, null, null);
    }
    
    /**
     * Constructor
     * 
     * @param theAttrFinder
     * 				the {@link AttributeFinder}
     * @param thePolicyFinder
     * 				the {@link PolicyFinder}
     * @param theResourceFinder
     * 				the {@link ResourceFinder}
     * @param thePdpAdmin
     * 				the {@link PdpAdmin}
     */
    @SuppressWarnings("unchecked")
	public PdpDecisionConfig(AttributeFinder theAttrFinder, 
    		PolicyFinder thePolicyFinder, ResourceFinder theResourceFinder,
            PdpAdmin thePdpAdmin) {
        super();
        attributeFinder = theAttrFinder;
        policyFinder = thePolicyFinder;
        resourceFinder = theResourceFinder;
        pdpAdmin = thePdpAdmin;
        if (attributeFinder == null) {
        	attributeFinder = new AttributeFinder();
        	attributeFinder.setModules(new ArrayList());
        }
        if (policyFinder == null) {
        	policyFinder = new PolicyFinder();
        	policyFinder.setModules(new HashSet());
        }
        if (resourceFinder == null) {
        	resourceFinder = new ResourceFinder();
        	resourceFinder.setModules(new ArrayList());
        }
        addAttributeFinderModule(new CurrentEnvModule());
    }
    
    /**
     * Constructor
     * 
     * @param theAttrFinder
     * 				the {@link AttributeFinder}
     * @param thePolicyFinder
     * 				the {@link PolicyFinder}
     * @param theResourceFinder
     * 				the {@link ResourceFinder}
     * @param thePdpAdmin
     * 				the {@link PdpAdmin}
     * @param theFinderModule
     * 				a {@link PolicyFinderModule}
     */
    public PdpDecisionConfig(AttributeFinder theAttrFinder, 
    		PolicyFinder thePolicyFinder, ResourceFinder theResourceFinder,
            PdpAdmin thePdpAdmin, PolicyFinderModule theFinderModule) {
    	this(theAttrFinder, thePolicyFinder, theResourceFinder, 
    			thePdpAdmin);
    	addPolicyFinderModule(theFinderModule);
    }

    /**
     * @return
     * 				the {@link AttributeFinder}
     */
    public AttributeFinder getAttributeFinder() {
    	return attributeFinder;
    }

    /**
     * @param theModule
     * 				an {@link AttributeFinderModule}
     */
    @SuppressWarnings("unchecked")
	public final void addAttributeFinderModule(AttributeFinderModule theModule) {
    	if (attributeFinder == null) {
			attributeFinder = new AttributeFinder();
		}	
    	if (!attributeFinder.getModules().contains(theModule)) {
    		List list = attributeFinder.getModules();
    		list.add(theModule);    		
    		attributeFinder.setModules(list);
    	}
    }    

    /**
     * @return
     * 				the {@link PolicyFinder}
     */
    public PolicyFinder getPolicyFinder() {
    	return policyFinder;
    }
    
    /**
     * @param theModule
     * 				a {@link PolicyFinderModule}
     */
    @SuppressWarnings("unchecked")
	public final void addPolicyFinderModule(PolicyFinderModule theModule) {
    	if (policyFinder == null) {
    		policyFinder = new PolicyFinder();
    	}
    	if ((policyFinder != null)  
    			&& (!policyFinder.getModules().contains(theModule))) {
    		Set set = policyFinder.getModules();
    		set.add(theModule);
    		policyFinder.setModules(set);
    	}
    }
    
    /**
     * @return
     * 				the {@link ResourceFinder}
     */
    public ResourceFinder getResourceFinder() {
    	return resourceFinder;
    }

    /**
     * @param theModule
     * 				the {@link ResourceFinderModule}
     */
    @SuppressWarnings("unchecked")
	public void addResourceFinderModule(ResourceFinderModule theModule) {
    	if (resourceFinder == null) {
    		resourceFinder = new ResourceFinder();
    	}
    	if (!resourceFinder.getModules().contains(theModule)) {
    		List list = resourceFinder.getModules();
    		list.add(theModule);    		
    		resourceFinder.setModules(list);
    	}
    }    
    
    /**
     * @return
     * 				the {@link PdpAdmin}
     */
    public PdpAdmin getPdpAdminService() {
    	return pdpAdmin;
    }

    /**
     * @param thePdpAdmin
     * 				the {@link PdpAdmin}
     */
    public void setPdpAdminService(PdpAdmin thePdpAdmin) {
    	pdpAdmin = thePdpAdmin;
    }

}
