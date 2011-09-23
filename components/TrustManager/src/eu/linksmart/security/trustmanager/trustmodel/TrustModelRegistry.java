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
 * Copyright (C) 2006-2010 Fraunhofer SIT,
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

package eu.linksmart.security.trustmanager.trustmodel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.linksmart.security.trustmanager.util.ConfigurationFileHandler;
import eu.linksmart.security.trustmanager.util.Util;

/**
 * This is the Trust Model Registry
 *
 * Different Trust Models can be registered and removed here. This class is
 * implemented as a Singleton in order to ensure only one of these exists.
 * 
 * @author Julian Schütte (julian.schuette@sit.fraunhofer.de)
 * @author Stephan Heuser (stephan.heuser@sit.fraunhofer.de)
 */
public final class TrustModelRegistry {

	/** The instance variable for the singleton pattern */
	private static TrustModelRegistry instance = null;

	/** ArrayList containing the active Trust Models */
	private ArrayList<TrustModel> trustmodels = null;

	/** The logger, used for logging */
	private static final Logger logger =
			Logger.getLogger(TrustModelRegistry.class);

	/** The index of the current Trust Model in the Registry */
	private int currentTrustModel = -1;

	/**
	 * Constructor is private, Singleton pattern
	 */
	private TrustModelRegistry() {
		trustmodels = new ArrayList<TrustModel>();
	}

	/**
	 * This is the way one gets access to the singleton class TrustModelRegistry
	 * 
	 * @return the trustmodel instance
	 * @uml.property name="instance"
	 */
	public static TrustModelRegistry getInstance() {
		if (instance == null) {
			instance = new TrustModelRegistry();
			instance.initialize();
		}
		return instance;
	}

	/**
	 * Add a new Trust Model to the TrustModelRegistry
	 * 
	 * @param trustmodel
	 *            a Trust Model which should be added to the registry
	 */
	public void addTrustModel(TrustModel trustmodel) {
		trustmodels.add(trustmodel);
		logger.debug("Trustmodel " + trustmodel.getIdentifier() + " added");

	}

	/**
	 * Remove an existing and previously added Trust Model from the Trust Model
	 * database
	 * 
	 * @param identifier
	 *            the Identifier of the Trust Model to be removed
	 */
	public void removeTrustModel(String identifier) {
		for (int i = 0; i < trustmodels.size(); i++) {
			if (trustmodels.get(i).getIdentifier().equals(identifier)) {
				trustmodels.remove(i);
				logger.debug("Trustmodel " + identifier + " removed");
			}
		}
	}

	/**
	 * Get a List of Strings, containing the Identifiers of the currently loaded
	 * Trust Models
	 * 
	 * @return List Trust Model Identifiers
	 */
	public List<String> getTrustModels() {
		ArrayList<String> value = new ArrayList<String>();
		for (TrustModel t : trustmodels) {
			value.add(t.getIdentifier());
		}
		return value;
	}

	/**
	 * 
	 * Initialize the TrustModelRegistry. Configuration will be loaded from XML
	 * file conf/config.xml using ConfigurationFileHandler class. Corresponding
	 * trust models will be loaded.
	 * 
	 */
	private void initialize() {
		ConfigurationFileHandler handler = new ConfigurationFileHandler();
		List<String> classnames = handler.getTrustModelClassNames();
		for (String current : classnames) {
			try {
				Class c = this.getClass().getClassLoader().loadClass(current);
				TrustModel trustmodel =
						(TrustModel) c.getConstructor(null).newInstance(null);
				addTrustModel(trustmodel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get the (loaded) Trust Model with its Identifier given
	 * 
	 * @param identifier
	 *            the Identifier of the Trust Model
	 * @return the corresponding Trust Model if found, or null if not
	 */

	public TrustModel getTrustModel(String identifier) {

		for (TrustModel trustmodel : trustmodels) {
			if (trustmodel.getIdentifier().equals(identifier)) {
				return trustmodel;
			}
		}
		return null;
	}

	/**
	 * Get the class names of the loaded trust models.
	 * 
	 * @return class names of the trust models, as list
	 */
	public List<String> getTrustModelClassNames() {
		ArrayList<String> value = new ArrayList<String>();
		for (TrustModel t : trustmodels) {
			value.add(t.getClass().getName());
		}
		return value;
	}

	/**
	 * Get the currently active Trust Model
	 * 
	 * @return the currently active class model, or null if none is loaded
	 * 
	 */

	public TrustModel getCurrentTrustModel() {
		logger.debug("Current Trust Model: "
				+ trustmodels.get(currentTrustModel).getIdentifier());
		return trustmodels.get(currentTrustModel);
	}

	/**
	 * Set the current Trust Model
	 * 
	 * @param identifier
	 *            the Identifier of the (loaded) Trust Model
	 * @return true if action was successfull is loaded, false if not
	 */

	public boolean setCurrentTrustModel(String identifier) {
		int i = 0;
		for (TrustModel trustmodel : trustmodels) {
			if (trustmodel.getIdentifier().equals(identifier)) {
				logger.debug("Trust model changed to: "
						+ trustmodel.getIdentifier());
				//initialize trustmodel before using it
				trustmodel.initialize();
				currentTrustModel = i;
				return true;
			}
			i++;
		}
		logger.debug("Trust Model change failed");
		return false;
	}
}
