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

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;


import eu.linksmart.caf.cm.engine.contexts.SecurityContext;
import eu.linksmart.caf.cm.engine.members.ContextMember;
import eu.linksmart.caf.cm.impl.CmManagerHub;
import eu.linksmart.utils.Configurator;

/**
 * Performs the initialisation and maintenance of the
 * <code>SecurityContext</code>.<p> Gets the relevant configurations from the
 * {@link ConfigurationAdmin} configurations for the security related LinkSmart
 * managers, and also listens for updates.
 * 
 * @author Michael Crouch
 * 
 */
public class SecurityContextManager extends CmInternalManager implements
		EventHandler {

	/** The Id for this Internal Manager */
	public static final String MANAGER_ID =
			"eu.linksmart.caf.cm.SecurityContextManager";

	/**
	 * ID of the Event fired (to the OSGi {@link EventAdmin} when a
	 * configuration has been registered
	 */
	private static final String CONFIGURATION_REGISTERED_EVENT =
			"eu/linksmartmiddlware/configuration/registered";

	/**
	 * ID of the Event fired (to the OSGi {@link EventAdmin} when a
	 * configuration has been updated
	 */
	private static final String CONFIGURATION_UPDATED_EVENT =
			"eu/linksmartmiddlware/configuration/updated";

	/** ID of the Network Manager configuration */
	private static final String NM_CONFIG = "eu.linksmart.network";

	/** IDs of relevant NM configurations */
	private static final String[] NM_CONFIG_IDS =
			{ "Network.CommunicationType", "Security.Access.DefaultDeny",
					"Security.Protocol", "Security.UseCoreSecurity",
					"symmetricKeyGeneratorAlgorithm", "trustThreshold",
					"Backbone.Mode", "Backbone.Multicast", "Backbone.PeerName",
					"Backbone.PipeLifeTime" };

	/** ID of the Core Security configuration */
	private static final String CORE_SECURITY_CONFIG =
			"eu.linksmart.security.core";

	/** IDs of relevant Core Security configurations */
	private static final String[] CORE_SECURITY_CONFIG_IDS =
			{ "linksmart.security.core.config" };

	/** ID of the Core Security configuration */
	private static final String PEP_CONFIG = "eu.linksmart.policy.pep";

	/** IDs of relevant PEP configurations */
	private static final String[] PEP_CONFIG_IDS =
			{ "Pep.DefaultToDenyResponse", "Pep.DenyOnUnfulfilledObligation",
					"Pep.PID", "Pep.SessionCache.Active",
					"Pep.UseLocalSessions", "Pep.PdpPID",
					"Pep.LazyObligationHandling" };

	/** The {@link ConfigurationAdmin} */
	private ConfigurationAdmin configAdmin;

	/** The {@link EventAdmin} */
	private EventAdmin eventAdmin;

	/** The {@link RuleEngine} */
	private RuleEngine ruleEngine;

	/** EventHandler {@link ServiceRegistration} */
	private ServiceRegistration eventReg;

	/** The Security contextId */
	private String securityCtxId = null;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the {@link ComponentContext}
	 */
	public SecurityContextManager(ComponentContext context) {
		configAdmin =
				(ConfigurationAdmin) context
						.locateService("ConfigurationAdmin");
		eventAdmin = (EventAdmin) context.locateService("EventAdmin");

		if (eventAdmin != null) {
			String[] topics =
					{ CONFIGURATION_REGISTERED_EVENT,
							CONFIGURATION_UPDATED_EVENT };
			Dictionary props = new Hashtable();
			props.put(EventConstants.EVENT_TOPIC, topics);
			eventReg =
					context.getBundleContext().registerService(
							EventHandler.class.getName(), this, props);
		}
	}

	/**
	 * Gets the existing {@link SecurityContext} from the {@link RuleEngine}
	 * 
	 * @return the {@link SecurityContext} or null
	 */
	public SecurityContext getSecurityContext() {
		if (ruleEngine != null) {
			if (securityCtxId != null)
				return (SecurityContext) ruleEngine.getContextByContextId(
						SecurityContext.class, securityCtxId);
		}
		return null;
	}

	/**
	 * Generates the {@link SecurityContext}
	 * 
	 * @return the {@link SecurityContext}
	 */
	public SecurityContext generateSecurityContext() {

		SecurityContext sc = new SecurityContext();

		refreshSecurityContext(sc, NM_CONFIG, getConfiguration(NM_CONFIG),
				NM_CONFIG_IDS);
		refreshSecurityContext(sc, CORE_SECURITY_CONFIG,
				getConfiguration(CORE_SECURITY_CONFIG),
				CORE_SECURITY_CONFIG_IDS);
		refreshSecurityContext(sc, PEP_CONFIG, getConfiguration(PEP_CONFIG),
				PEP_CONFIG_IDS);

		securityCtxId = sc.getContextId();

		return sc;
	}

	/**
	 * Refreshes the {@link SecurityContext} with the configurations listed in
	 * the array of ids, from the {@link Dictionary} configuration.
	 * 
	 * @param secContext
	 *            the {@link SecurityContext}
	 * @param configPid
	 *            the pid of the configuration being added
	 * @param configuration
	 *            the {@link Dictionary} configuration for the PID
	 * @param ids
	 *            the relevant configuration ids to extract
	 */
	private void refreshSecurityContext(SecurityContext secContext,
			String configPid, Dictionary<String, String> configuration,
			String[] ids) {
		if (secContext == null || configuration == null)
			return;

		if (NM_CONFIG.equals(configPid)) {
			secContext.setCryptoManagerUrl((String) configuration
					.get("cryptoManagerURL"));
			secContext.setTrustManagerUrl((String) configuration
					.get("trustManagerURL"));
			secContext.setNmDescription((String) configuration
					.get("NetworkManager.Description"));
			secContext.setNmHid((String) configuration
					.get("NetworkManager.HID"));
		}

		for (String key : ids) {
			ContextMember existingMember = secContext.getMember(key);
			if (existingMember == null) {
				String value = configuration.get(key);
				if (value != null) {
					ContextMember secMember =
							new ContextMember(secContext, key, configuration
									.get(key),
									"http://www.w3.org/2001/XMLSchema#string",
									configPid);
					secContext.addMember(secMember);
				}
			} else {
				String newValue = configuration.get(key);
				if (!existingMember.getStrValue().equals(newValue))
					existingMember.setStrValue(newValue);
			}
		}
	}

	/**
	 * Gets the given Configuration dictionary from the
	 * {@link ConfigurationAdmin}
	 * 
	 * @param pid
	 *            the pid of the {@link Configuration} to get
	 * @return the {@link Dictionary} from the retrieved {@link Configuration}
	 */
	private Dictionary getConfiguration(String pid) {
		try {
			Configuration config = configAdmin.getConfiguration(pid);
			if (config != null) {
				return config.getProperties();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getManagerId() {
		return MANAGER_ID;
	}

	@Override
	public void initialise(CmManagerHub hub) {
		ruleEngine = (RuleEngine) hub.getManager(RuleEngine.MANAGER_ID);
	}
	
	@Override
	public void completedInit() {
		SecurityContext sc = generateSecurityContext();
		ruleEngine.insert(sc);		
	}

	@Override
	public void shutdown() {
		if (eventReg != null)
			eventReg.unregister();

	}

	/**
	 * Handles the configuration events fired by the {@link Configurator} for
	 * the relevant Security managers
	 * 
	 * @param event
	 *            the {@link Event} from the EventAdmin
	 */
	@Override
	public void handleEvent(Event event) {

		String servicePid = (String) event.getProperty("service.pid");
		Dictionary<String, String> config =
				(Dictionary) event.getProperty("config");
		String[] ids = null;

		if (servicePid.equals(NM_CONFIG)) {
			ids = NM_CONFIG_IDS;
		} else if (servicePid.equals(CORE_SECURITY_CONFIG)) {
			ids = CORE_SECURITY_CONFIG_IDS;
		} else if (servicePid.equals(PEP_CONFIG)) {
			ids = PEP_CONFIG_IDS;
		}

		if (ids == null)
			return;

		SecurityContext sc = getSecurityContext();
		if (sc == null)
			return;

		refreshSecurityContext(sc, servicePid, config, ids);

		// insert / update Security context
		ruleEngine.insert(sc);
	}

}
