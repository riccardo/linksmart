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
 * Copyright (C) 2006-2010 [Telefonica I+D]
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

/**
 * Implementation of the interface Configurator, that can can be used for
 * configuring the  middleware.
 */

package eu.linksmart.configurator.impl;

import java.io.IOException;
import java.util.Dictionary;

import org.apache.log4j.Logger;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.configurator.Configurator;

/**
 * Implementation of the interface Configurator
 */
public class ConfiguratorImpl implements Configurator {

	private Logger logger = Logger.getLogger(ConfiguratorImpl.class.getName());
	
	private ComponentContext context;
	private ConfigurationAdmin cm;
	
	/**
	 * Constructor of the class ConfiguratorImpl
	 * 
	 * @param context the bundle's execution context
	 */
	public ConfiguratorImpl(ComponentContext context) {
		this.context = context;
		this.cm = (ConfigurationAdmin) context.locateService("ConfigurationAdmin");
	}
	
	/**
	 * Provides the list of identifiers of the available  middleware
	 * configurations. It only returns the configurations that have been
	 * registered by managers. It also provides the chance for configuring
	 * the Http Service.
	 * 
	 * @return the identifiers of the available  middleware configurations.
	 */
	public String[] getAvailableConfigurations() {
		try {
			/* Gets all configurations in the framework. */
			Configuration[] configs = cm.listConfigurations("(service.pid=*)");
			if (configs == null) return null;
			String[] result = new String[configs.length];
			for (int i = 0; i < configs.length; i++) {
				result[i] = configs[i].getPid();
			}
			return result;
		} catch (IOException e) {
			logger.error(e);
		} catch (InvalidSyntaxException e) {
			logger.error(e);
		}
		return null;
	}

	/**
	 * Returns a {@link Dictionary} containing the configuration properties for
	 * a given configuration.
	 *
	 * @param id the identifier for the desired configuration
	 * @return the configuration properties (key, value) for the given
	 * configuration or null if no configuration was found for the id.
	 */
	public Dictionary getConfiguration(String id) {
		try {
			Configuration c = cm.getConfiguration(id);
			if (c != null) return cm.getConfiguration(id).getProperties();
		} catch (IOException e) {
			logger.error(e);
		}
		return null;
	}

	/**
	 * Configures a configuration (id) with a configuration {@link Dictionary}.
	 *
	 * @param id identifier for the desired configuration.
	 * @param config {@link Dictionary} containing the configuration properties.
	 * A null configuration will delete the current configuration for the id
	 * and will force to load the default configuration.
	 */
	public void configure(String id, Dictionary config) {
		try {
			Configuration c = cm.getConfiguration(id);
			if (c != null) {
				if (config == null) {
					c.delete();
					return;
				}
				c.update(config);
			}
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * Configures a configuration (id) with a property pair, key-value. If the
	 * configuration property already exists, it will be overwritten. If the
	 * key didn't exist before, it will be added to the configuration.
	 * 
	 * @param id identifier for the desired configuration.
	 * @param key the property key. null key is not allowed
	 * @param value the property value. null value is not allowed.
	 */
	public void configure(String id, Object key, Object value) {
		try {
			Configuration c = cm.getConfiguration(id);
			if (c != null) {
				Dictionary old = c.getProperties();
				old.put(key, value);
				c.update(old);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (NullPointerException e) {
			logger.error("PID not available.", e);
		}
	}

}
