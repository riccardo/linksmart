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
package eu.linksmart.policy.pdp.ext.impl;

import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.w3c.dom.Node;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.AnyURIAttribute;
import com.sun.xacml.attr.AttributeFactory;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.BooleanAttribute;
import com.sun.xacml.attr.DateAttribute;
import com.sun.xacml.attr.DateTimeAttribute;
import com.sun.xacml.attr.DayTimeDurationAttribute;
import com.sun.xacml.attr.DoubleAttribute;
import com.sun.xacml.attr.IntegerAttribute;
import com.sun.xacml.attr.RFC822NameAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.attr.TimeAttribute;
import com.sun.xacml.attr.X500NameAttribute;
import com.sun.xacml.attr.YearMonthDurationAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.cond.Function;
import com.sun.xacml.cond.FunctionFactory;
import com.sun.xacml.cond.FunctionFactoryProxy;
import com.sun.xacml.cond.StandardFunctionFactory;
import com.sun.xacml.finder.AttributeFinder;

import eu.linksmart.policy.pdp.ext.function.impl.PdpFunctionScope;
import eu.linksmart.policy.pdp.ext.function.impl.XPathFunctions;
import eu.linksmart.policy.pdp.impl.LinkSmartAttributeFactory;

/**
 * <p>Local base implementation and extension bundle integration class for 
 * LinkSmart PolicyInformationPoints (PIPs)</p>
 * 
 * <p>All external LinkSmart PIP bundles that are to be registered by this class 
 * must extend the abstract super class {@link PipModule}. LinkSmart PIP bundles 
 * register themselves as {@link AttributeFinder}s and additionally register 
 * {@link Function}s with the {@link PipCore} instance.</p>
 *
 * @author Michael Crouch
 * @author Marco Tiemann
 *
 */
public class PipCore extends AttributeFinder {

	/** logger */
	static final Logger logger = Logger.getLogger(PipCore.class);
	
	/** attribute types */
	private static Set<String> types = new HashSet<String>();
	
	/** standard functions */
	static {
		types.add(IntegerAttribute.identifier);
		types.add(AnyURIAttribute.identifier);
		types.add(BooleanAttribute.identifier);
		types.add(DateAttribute.identifier);
		types.add(DateTimeAttribute.identifier);
		types.add(DayTimeDurationAttribute.identifier);
		types.add(DoubleAttribute.identifier);
		types.add(RFC822NameAttribute.identifier);
		types.add(StringAttribute.identifier);
		types.add(TimeAttribute.identifier);
		types.add(X500NameAttribute.identifier);
		types.add(YearMonthDurationAttribute.identifier);
	}
	
	/** {@link ServiceReference}s */
	private Set<ServiceReference> designatorPluginModules 
			= new HashSet<ServiceReference>();
	
	/** {@link ServiceReference}s */
	private Set<ServiceReference> selectorPluginModules 
			= new HashSet<ServiceReference>();
	
	/** {@link FunctionFactoryProxy} */
	private FunctionFactoryProxy functionFactoryProxy;
	
	/** {@link AttributeFactory } */
	@SuppressWarnings("unused")
	private AttributeFactory attributeFactory;
	
	/** {@link ServiceTracker} */
	private ServiceTracker tracker;
	
	/**
	 * Constructor
	 * 
	 * @param theContext
	 * 				the {@link BundleContext}
	 */
	public PipCore(BundleContext theContext) {
		super();
		logger.debug("Initializing");
		// set up function factory
		functionFactoryProxy = StandardFunctionFactory.getNewFactoryProxy();
		FunctionFactory functionFactory 
				= functionFactoryProxy.getGeneralFactory();	
		for (String id : types) {
			functionFactory.addFunction(XPathFunctions.getSingleFunction(id));
			functionFactory.addFunction(XPathFunctions.getBagFunction(id));
		}		
		FunctionFactory.setDefaultFactory(functionFactoryProxy);
		// can't use default getInstance, as that cannot be extended
		attributeFactory = new LinkSmartAttributeFactory();
		tracker = new ServiceTracker(theContext, PipModule.class.getName(), 
				null);
		tracker.open();
		ServiceReference[] refs = tracker.getServiceReferences();
		if (refs == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Found no references");
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Found " + refs.length + " references");
			}
			int rl = refs.length;
			for (int i=0; i < rl; i++) {
				register(refs[i]);
			}
		}
		ServiceListener sl = new ServiceListener() {
			@Override
			public void serviceChanged(ServiceEvent theEvent) {
				switch (theEvent.getType()) {
					case ServiceEvent.REGISTERED : {
						register(theEvent.getServiceReference());
						return;
					}
					case ServiceEvent.MODIFIED : { 
						logger.debug("MODIFIED");
						return; 
					}
					case ServiceEvent.UNREGISTERING : {
						ServiceReference sr = theEvent.getServiceReference();
						logger.debug("Removing " 
								+ sr.getBundle().getSymbolicName());
						remove(theEvent.getServiceReference());
						return;
					}
					default: {
						// intentionally left blank
					}
				}
			}
		};
		try {
			String filter = "(objectclass=" + PipModule.class.getName() + ")";
			theContext.addServiceListener(sl, filter);
		} catch(Exception e) {
			logger.warn("Exception while adding ServiceListener: "
					+ e.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", e);
			}
		}
	}
	
	/**
	 * Attribute selector that first attempts to get a result from any locally 
	 * loaded attribute finder modules and afterwards attempts to use PDP 
	 * extension plugins
	 * 
	 * @param theContextPath
	 * 				the context path
	 * @param theNamespaceNode
	 * 				the namespace {@link Node}
	 * @param theAttrType
	 * 				the attribute type {@link URI}
	 * @param theContext
	 * 				the {@link EvaluationCtx}
	 * @param theXpathVersion
	 * 				the XPATH version
	 * @return
	 * 				the {@link EvaluationResult}
	 */
	@Override
	public EvaluationResult findAttribute(String theContextPath, 
			Node theNamespaceNode, URI theAttrType,
			EvaluationCtx theContext, String theXpathVersion) {
		// first attempt: call to local modules 
		EvaluationResult result = super.findAttribute(theContextPath, 
				theNamespaceNode, theAttrType, theContext, theXpathVersion);
		BagAttribute bag = (BagAttribute) result.getAttributeValue();
        if (!bag.isEmpty()) {
             return result;
        }
		Iterator<ServiceReference> it = selectorPluginModules.iterator();
		while (it.hasNext()) {
			ServiceReference ref = it.next();
			PipModule module = (PipModule) tracker.getService(ref);
			if (module == null) {
				selectorPluginModules.remove(ref);
			} else {
				result = module.findAttribute(theContextPath, theNamespaceNode, 
						theAttrType, theContext, theXpathVersion);
				// if there was an error, we stop right away
				if (result.indeterminate()) {
					if (logger.isInfoEnabled()) {
						logger.info("Error while resolving values: "
								+ result.getStatus().getMessage());
					}
					return result;
				}
				bag = (BagAttribute) result.getAttributeValue();
				if (!bag.isEmpty()) {
					return result;
				}
			}
		}
		return new EvaluationResult(BagAttribute.createEmptyBag(theAttrType));
	}
	
	/**
	 * Attribute designator that first attempts to get a result from any 
	 * locally loaded attribute finder modules, then attempts to use PDP 
	 * extension plugins
	 * 
	 * @param theAttrType
	 * 				the attribute type {@link URI}
	 * @param theAttrId
	 * 				the attribute ID {@link URI}
	 * @param theIssuer
	 * 				the issued {@link URI}
	 * @param theSubjectCategory
	 * 				the subject category {@link URI}
	 * @param theContext
	 * 				the {@link EvaluationCtx}
	 * @param theDesignatorType
	 * 				the designator type
	 * @return
	 * 				the {@link EvaluationResult}
	 */
	@Override
	public EvaluationResult findAttribute(URI theAttrType, URI theAttrId, 
			URI theIssuer, URI theSubjectCategory, EvaluationCtx theContext, 
			int theDesignatorType) {
		// first: attempt call to local modules 
		EvaluationResult result = super.findAttribute(theAttrType, 
				theAttrId, theIssuer, theSubjectCategory, theContext, 
				theDesignatorType);
		BagAttribute bag = (BagAttribute) result.getAttributeValue();
        if (!bag.isEmpty()) {
             return result;
        }        
        Iterator<ServiceReference> it = designatorPluginModules.iterator();
		while (it.hasNext()) {
			ServiceReference ref = it.next();
			PipModule module = (PipModule) tracker.getService(ref);			
			if (module == null) {
				designatorPluginModules.remove(ref);
			} else {
				result = module.findAttribute(theAttrType, theAttrId, theIssuer, 
						theSubjectCategory, theContext, theDesignatorType);
				// if there was an error, we stop right away
				if (result.indeterminate()) {
					if (logger.isInfoEnabled()) {
						logger.info("Error while trying to resolve values: " 
								+ result.getStatus().getMessage());
					}
					return result;
				}
				bag = (BagAttribute) result.getAttributeValue();
				if (!bag.isEmpty()) {
					return result;
				}
			}
		}
		return new EvaluationResult(BagAttribute.createEmptyBag(theAttrType));
	}
	
	/**
	 * Adds {@link PipModule}s by {@link ServiceReference}
	 * 
	 * @param theService
	 * 				the {@link ServiceReference}
	 */
	final void register(ServiceReference theService) {
		PipModule module = (PipModule) tracker.getService(theService);
		if (module == null) {
			logger.error("Could not retrieve any registered PDP attribute " 
					+ "finder extension");
			return;
		}
		logger.debug("Registering PipModule " + module.getIdentifier());
		// register PIP to designator and/or selector supporting modules
		if ((module.isDesignatorSupported()) 
				&& (!designatorPluginModules.contains(theService))) {
			designatorPluginModules.add(theService);			
		}
		if ((module.isSelectorSupported()) 
				&& (!selectorPluginModules.contains(theService))) {
			selectorPluginModules.add(theService);			
		}
		// register functions supplied by the PIP
		FunctionFactory conFactory = functionFactoryProxy.getConditionFactory();
		for (Function f : module.getFunctions(PdpFunctionScope.CONDITION)) {
			logger.debug("Registering condition function " 
					+ f.getIdentifier().toString());
			conFactory.addFunction(f);
		}
		FunctionFactory genFactory = functionFactoryProxy.getGeneralFactory();
		for (Function f : module.getFunctions(PdpFunctionScope.GENERAL)) {
			logger.debug("Registering general function " 
					+ f.getIdentifier().toString());
			genFactory.addFunction(f);
		}
		FunctionFactory tarFactory = functionFactoryProxy.getTargetFactory();
		for (Function f : module.getFunctions(PdpFunctionScope.TARGET)) {
			logger.debug("Registering target function " 
					+ f.getIdentifier().toString());
			tarFactory.addFunction(f);
		}
		FunctionFactory.setDefaultFactory(functionFactoryProxy);		
	}
	
	/**
	 * Removes {@link PipModule}s by {@link ServiceReference}
	 * 
	 * @param theService
	 * 				the {@link ServiceReference}
	 */
	void remove(ServiceReference theService) {
		/* 
		 * OSGi tracker returns null when the unregister state has been messaged
		 * for a service; the service can no longer be retrieved via the 
		 * service reference
		 */
		logger.info("Deregistering PipModule: " 
				+ theService.getBundle().getSymbolicName());
		if (designatorPluginModules.contains(theService)) {
			designatorPluginModules.remove(theService);			
		}
		if (selectorPluginModules.contains(theService)) {
			selectorPluginModules.remove(theService);			
		}		
	}
	
}