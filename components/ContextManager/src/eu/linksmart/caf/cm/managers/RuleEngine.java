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
package eu.linksmart.caf.cm.managers;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;


import eu.linksmart.caf.Parameter;
import eu.linksmart.caf.cm.ContextManagerError;
import eu.linksmart.caf.cm.engine.ContextFilter;
import eu.linksmart.caf.cm.engine.contexts.BaseContext;
import eu.linksmart.caf.cm.engine.contexts.Device;
import eu.linksmart.caf.cm.engine.contexts.Location;
import eu.linksmart.caf.cm.engine.contexts.MemberedContext;
import eu.linksmart.caf.cm.engine.contexts.Service;
import eu.linksmart.caf.cm.engine.event.BaseEvent;
import eu.linksmart.caf.cm.engine.members.ContextMember;
import eu.linksmart.caf.cm.engine.members.EventMember;
import eu.linksmart.caf.cm.exceptions.ContextManagerException;
import eu.linksmart.caf.cm.exceptions.ErrorListException;
import eu.linksmart.caf.cm.impl.CmManagerHub;
import eu.linksmart.caf.cm.impl.util.TypeFactory;

/**
 * The Context Manager Rule Engine exposes a DROOLS Rule Engine to the Rules
 * submitted after being converted to DRL form. It maintains a
 * {@link StatefulKnowledgeSession} and a {@link KnowledgeBase}.
 * 
 * Provides functionality for managing rules, adding / updating / removal
 * 
 */
public final class RuleEngine extends CmInternalManager {

	/** The Id for this Internal Manager */
	public static final String MANAGER_ID = "eu.linksmart.caf.cm.RuleEngine";

	/** the {@link Logger} */
	private static final Logger logger = Logger.getLogger(RuleEngine.class);

	/** the {@link RuleEngine} instance */
	private static RuleEngine instance = null;

	/** the {@link StatefulKnowledgeSession} */
	private final StatefulKnowledgeSession ksession;

	/** the {@link KnowledgeBase} */
	private final KnowledgeBase kbase;

	/**
	 * Singleton Constructor
	 */
	private RuleEngine() {
		KnowledgeBaseConfiguration config =
				KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
		config.setOption(EventProcessingOption.STREAM);
		this.kbase = KnowledgeBaseFactory.newKnowledgeBase(config);

		KnowledgeSessionConfiguration sessionConfig =
				KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
		sessionConfig.setOption(ClockTypeOption.get("realtime"));

		ksession = kbase.newStatefulKnowledgeSession(sessionConfig, null);
	}

	/**
	 * Singleton static method for retrieving the {@link RuleEngine} instance
	 * 
	 * @return the {@link RuleEngine}
	 */
	public static synchronized RuleEngine getSingleton() {
		if (instance == null) {
			instance = new RuleEngine();
		}
		return instance;
	}

	@Override
	public void initialise(CmManagerHub hub) {
		// None
	}

	@Override
	public void completedInit() {
		//Do nothing		
	}
	
	/**
	 * Disposes and shuts down the Rule Engine
	 */
	@Override
	public void shutdown() {
		if (ksession != null) {
			ksession.halt();
			ksession.dispose();
		}
	}

	@Override
	public String getManagerId() {
		return MANAGER_ID;
	}

	/**
	 * Loads the give Object to the Rule Engine as a global, with the given
	 * varaible name
	 * 
	 * @param variableName
	 *            the name of the global
	 * @param globalObject
	 *            the object
	 */
	public void loadGlobal(String variableName, Object globalObject) {
		try {
			ksession.setGlobal(variableName, globalObject);
		} catch (Exception e) {
			logger.error("Error loading global '" + variableName + "'", e);
		}
	}

	/**
	 * Attempts to load/compile a rule from an {@link InputStream} and add to
	 * Rule Engine
	 * 
	 * @param input
	 *            the {@link InputStream}
	 * @return the built {@link Resource}
	 */
	public Resource buildRuleResource(InputStream input) {
		Reader reader = new InputStreamReader(input);
		return buildRuleResource(reader);
	}

	/**
	 * Attempt to load/compile a rule from a String and add to Rule Engine
	 * 
	 * @param ruleString
	 *            the rule as DRL String
	 * @return the built {@link Resource}
	 */
	public Resource buildRuleResource(String ruleString) {
		return ResourceFactory.newByteArrayResource(ruleString.getBytes());
	}

	/**
	 * Attempt to load/compile rule from a {@link Reader}, then add rule with
	 * given provided reader to RuleBase
	 * 
	 * @param reader
	 *            the {@link Reader}
	 * @return the built {@link Resource}
	 */
	public Resource buildRuleResource(Reader reader) {
		return ResourceFactory.newReaderResource(reader);
	}

	/**
	 * Add the built knowledge, as a {@link KnowledgeBuilder} to the Rule Engine
	 * 
	 * @param builder
	 *            the {@link KnowledgeBuilder}
	 */
	public void addBuiltKnowledge(KnowledgeBuilder builder) {
		kbase.addKnowledgePackages(builder.getKnowledgePackages());
	}

	/**
	 * Returns a new {@link KnowledgeBuilder} to be used to compile rules
	 * 
	 * @return the {@link KnowledgeBuilder}
	 */
	public KnowledgeBuilder getKnowledgeBuilderSession() {
		// Properties props = new Properties();
		// props.setProperty("drools.dialect.java.compiler", "JANINO");
		// KnowledgeBuilderConfiguration config =
		// KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(props,
		// null);
		// return KnowledgeBuilderFactory.newKnowledgeBuilder(config);
		return KnowledgeBuilderFactory.newKnowledgeBuilder();
	}

	/**
	 * Compiles the given DRL into the provided {@link KnowledgeBuilder}. Any
	 * errors with the DRL are passed in the {@link ErrorListException}
	 * 
	 * @param drl
	 *            the DRL as String
	 * @param builder
	 *            the {@link KnowledgeBuilder}
	 * @throws RuleParserException
	 */
	public void compileRulePackage(String drl, KnowledgeBuilder builder)
			throws ErrorListException {
		Resource res = buildRuleResource(drl);
		compileRulePackage(res, builder);
	}

	/**
	 * Compiles the given DRL into the provided {@link KnowledgeBuilder}. Any
	 * errors with the DRL are passed in the {@link ErrorListException}
	 * 
	 * @param res
	 *            the DRL as {@link Resource}
	 * @param builder
	 *            the {@link KnowledgeBuilder}
	 * @throws RuleParserException
	 */
	public void compileRulePackage(Resource res, KnowledgeBuilder builder)
			throws ErrorListException {

		builder.add(res, ResourceType.DRL);
		if (builder.hasErrors()) {
			ErrorListException errorSet = new ErrorListException();
			int cnt = 1;
			KnowledgeBuilderErrors errors = builder.getErrors();
			Iterator<KnowledgeBuilderError> it = errors.iterator();
			while (it.hasNext()) {

				KnowledgeBuilderError error = it.next();
				ContextManagerError cmError =
						new ContextManagerError(error.getMessage(), Integer
								.toString(cnt), "Error");
				errorSet.addError(cmError);
				cnt++;
			}
			throw errorSet;
		}
	}

	/**
	 * Removes all rules with the given package name from the Rule Engine
	 * 
	 * @param packageName
	 *            the package name
	 */
	public void removePackage(String packageName) {
		try {
			kbase.removeKnowledgePackage(packageName);
		} catch (Exception e) {
			logger.error("Error removing RulePackage '" + packageName + "': "
					+ e.getLocalizedMessage(), e);
		}

	}

	/**
	 * Removes the individual rule with the given package name and rule name
	 * 
	 * @param pkgName
	 *            the package name
	 * @param ruleName
	 *            the rule name
	 */
	public void removeRule(String pkgName, String ruleName) {
		kbase.removeRule(pkgName, ruleName);
	}

	/**
	 * Fires all Rules in the Rule Engine. To be called when Context Data is
	 * updated
	 */
	public void fireAllRules() {
		ksession.fireAllRules();
	}

	/**
	 * Inserts the {@link Object} into the Rule Engine. Used to insert all non-
	 * {@link BaseContext} objects
	 * 
	 * @param obj
	 *            the object to insert
	 */
	public void insert(Object obj) {

		if (obj == null)
			return;

		FactHandle handle = ksession.getFactHandle(obj);
		if (handle == null) {
			ksession.insert(obj);
		} else {
			ksession.update(handle, obj);
		}
	}

	/**
	 * Removes the given {@link Object} from the Rule Engine
	 * 
	 * @param object
	 *            the {@link Object} to remove
	 */
	public void remove(Object object) {
		try {
			FactHandle handle = ksession.getFactHandle(object);
			if (handle != null)
				ksession.retract(handle);
		} catch (Exception e) {
			logger.error("Error removing fact: " + e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * Inserts a {@link BaseContext} into the Rule Engine, also all sub objects
	 * such as {@link Location}s, {@link ContextMember}s of a
	 * {@link MemberedContext}
	 * 
	 * @param context
	 *            the {@link BaseContext} to add
	 */
	public void insert(BaseContext context) {

		if (context instanceof MemberedContext) {
			MemberedContext memCtx = (MemberedContext) context;
			Iterator<ContextMember> it = memCtx.getMembers().iterator();
			while (it.hasNext()) {
				insert(it.next());
			}
		}

		if (context instanceof Device) {
			Device devCtx = (Device) context;
			Iterator<Service> it =
					devCtx.getDeviceServices().values().iterator();
			while (it.hasNext()) {
				insert(it.next());
			}
		}

		Iterator<Location> locsIt = context.getHasLocations().iterator();
		while (locsIt.hasNext()) {
			insert(locsIt.next());
		}

		// add the context
		FactHandle handle = ksession.getFactHandle(context);
		if (handle == null) {
			ksession.insert(context);
		} else {
			ksession.update(handle, context);
		}
	}

	/**
	 * Updates the given Object in the {@link RuleEngine}.
	 * 
	 * @param object
	 *            the object, can be any type of fact in the {@link RuleEngine}
	 */
	public void update(Object object) {
		FactHandle handle = ksession.getFactHandle(object);
		if (handle != null) {
			ksession.update(handle, object);
		}
	}

	/**
	 * Removes the {@link BaseContext} from the Rule Engine, along with all
	 * inserted sub-objects.
	 * 
	 * @param context
	 *            the {@link BaseContext}
	 */
	public void remove(BaseContext context) {
		if (context instanceof MemberedContext) {
			MemberedContext memCtx = (MemberedContext) context;
			Iterator<ContextMember> it = memCtx.getMembers().iterator();
			while (it.hasNext()) {
				remove(it.next());
			}
		}

		if (context instanceof Device) {
			Device devCtx = (Device) context;
			Iterator<Service> it =
					devCtx.getDeviceServices().values().iterator();
			while (it.hasNext()) {
				remove(it.next());
			}
		}

		Iterator<Location> locsIt = context.getHasLocations().iterator();
		while (locsIt.hasNext()) {
			remove(locsIt.next());
		}

		// remove the context
		FactHandle handle = ksession.getFactHandle(context);
		if (handle != null)
			ksession.retract(handle);

	}

	/**
	 * Execute Query loaded to Rule Engine, and return the {@link QueryResults}
	 * 
	 * @param name
	 *            the name of the query
	 * @param queryArgs
	 *            the arguments to pass
	 * @return the {@link QueryResults}
	 * @throws ContextManagerException
	 */
	public QueryResults executeNamedQuery(String name, Parameter[] queryArgs)
			throws ContextManagerException {

		if ((queryArgs == null) || (queryArgs.length == 0))
			return ksession.getQueryResults(name);

		// create argument Object array
		Object[] objArray = new Object[queryArgs.length];
		for (int i = 0; i < queryArgs.length; i++) {
			Parameter param = queryArgs[i];

			// convert to type
			Object obj =
					TypeFactory.getObjectAsType(param.getValue(), param
							.getType());
			if (obj == null)
				throw new ContextManagerException(name,
						"Error processing name query",
						"Could not convert to type");
			objArray[i] = obj;
		}
		return executeNamedQueryWithObjArgs(name, objArray);
	}

	/**
	 * Executes a Query with the arguments, as an Object array, passed
	 * 
	 * @param name
	 *            the name of the query
	 * @param args
	 *            the array of Objects to pass as arguments
	 * @return the {@link QueryResults}
	 */
	public QueryResults executeNamedQueryWithObjArgs(String name, Object[] args) {
		return ksession.getQueryResults(name, args);
	}

	/**
	 * Gets the Context of given {@link Class} with the given contextId from the
	 * {@link RuleEngine}
	 * 
	 * @param clazz
	 *            the {@link Class} of the context
	 * @param contextId
	 *            the contextId
	 * @return the {@link BaseContext}
	 */
	public BaseContext getContextByContextId(Class clazz, String contextId) {
		ContextFilter filter =
				new ContextFilter(clazz, ContextFilter.CONTEXT_ID_FILTER,
						contextId);
		return getContext(filter);
	}

	/**
	 * Gets the Context of given {@link Class} with the given name from the
	 * {@link RuleEngine}
	 * 
	 * @param clazz
	 *            the {@link Class} of the context
	 * @param name
	 *            the name
	 * @return the {@link BaseContext}
	 */
	public BaseContext getContextByName(Class clazz, String name) {
		ContextFilter filter =
				new ContextFilter(clazz, ContextFilter.CONTEXT_NAME_FILTER,
						name);
		return getContext(filter);
	}

	/**
	 * Gets the Context of given {@link Class} with the hid from the
	 * {@link RuleEngine}
	 * 
	 * @param clazz
	 *            the {@link Class} of the context
	 * @param hid
	 *            the hid
	 * @return the {@link BaseContext}
	 */
	public BaseContext getContextByHid(Class clazz, String hid) {
		ContextFilter filter =
				new ContextFilter(clazz, ContextFilter.CONTEXT_HID_FILTER, hid);
		return getContext(filter);
	}

	/**
	 * Gets the matching {@link BaseContext} for the {@link ContextFilter}
	 * 
	 * @param filter
	 *            the {@link ContextFilter}
	 * @return the matching {@link BaseContext} or null
	 */
	private BaseContext getContext(ContextFilter filter) {
		Collection<FactHandle> results = ksession.getFactHandles(filter);
		if (results.isEmpty())
			return null;
		FactHandle handle = results.iterator().next();
		return (BaseContext) ksession.getObject(handle);
	}

	/**
	 * Gets all {@link FactHandle} that match the given {@link ObjectFilter}
	 * 
	 * @param filter
	 *            the {@link ObjectFilter} instance
	 * @return a {@link Collection} of {@link FactHandle}s
	 */
	public Collection<FactHandle> getFactHandles(ObjectFilter filter) {
		return ksession.getFactHandles(filter);
	}

	/**
	 * Gets the {@link Object} represented by the {@link FactHandle}.
	 * 
	 * @param handle
	 *            the {@link FactHandle}
	 * @return the {@link Object}
	 */
	public Object getObject(FactHandle handle) {
		return ksession.getObject(handle);
	}

	/**
	 * Removes the {@link BaseContext} from the Rule Engine
	 * 
	 * @param contextId
	 *            the contextId to remove
	 */
	public void removeContext(String contextId) {
		BaseContext ctx = getContextByContextId(BaseContext.class, contextId);
		if (ctx != null) {
			remove(ctx);
		}

	}
}
