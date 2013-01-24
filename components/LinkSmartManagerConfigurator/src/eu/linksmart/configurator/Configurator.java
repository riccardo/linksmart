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
 * This interface can be used for configuring the LinkSmart middleware
 */

package eu.linksmart.configurator;

import java.util.Dictionary;


/**
 * Configurator interface. Provides methods for getting available
 * configurations, for getting a concrete configuration, and for setting
 * properties of a configuration
 */
public interface Configurator {
	
	/**
	 * Provides the list of identifiers of the available LinkSmart middleware
	 * configurations. It only returns the configurations that have been
	 * registered by managers. It also provides the chance for configuring
	 * the Http Service.
	 * 
	 * @return the identifiers of the available LinkSmart middleware configurations.
	 */
	public String[] getAvailableConfigurations();
	
	/**
	 * Returns a {@link Dictionary} containing the configuration properties for
	 * a given configuration.
	 *
	 * @param id the identifier for the desired configuration
	 * @return the configuration properties (key, value) for the given
	 * configuration or null if no configuration was found for the id.
	 */
	public Dictionary getConfiguration(String id);
	
	/**
	 * Configures a configuration (id) with a configuration {@link Dictionary}.
	 *
	 * @param id identifier for the desired configuration.
	 * @param config {@link Dictionary} containing the configuration properties.
	 * A null configuration will delete the current configuration for the id
	 * and will force to load the default configuration.
	 */
	public void configure(String id, Dictionary config);
	
	/**
	 * Configures a configuration (id) with a property pair, key-value. If the
	 * configuration property already exists, it will be overwritten. If the
	 * key didn't exist before, it will be added to the configuration.
	 * 
	 * @param id identifier for the desired configuration.
	 * @param key the property key. null key is not allowed
	 * @param value the property value. null value is not allowed.
	 */
	public void configure(String id, Object key, Object value);
	
}
