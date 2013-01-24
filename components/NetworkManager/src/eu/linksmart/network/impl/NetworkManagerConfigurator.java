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
 * Configuration parameters of the Network Manager
 */

package eu.linksmart.network.impl;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.utils.Configurator;


public class NetworkManagerConfigurator extends Configurator {

	/* Configuration PID & file path. */
	public static String NM_PID = "eu.linksmart.network";
	public static String CONFIGURATION_FILE = "/NM.properties";
	
	/* Configuration Keys. */
	public static final String NM_HID = "NetworkManager.HID";
	public static final String NM_DESCRIPTION = "NetworkManager.Description";
	public static final String CERTIFICATE_REF = "Network.CertificateRef";
	public static final String COMMUNICATION_TYPE = "Network.CommunicationType";
	
	public static final String MULTIMEDIA_PORT = "Network.MultimediaPort";
		
	public static final String JXTA_LOGS = "Backbone.JXTALogs";
	public static final String PEER_NAME = "Backbone.PeerName";
	public static final String ANNOUNCE_VALIDITY = "Backbone.AnnounceValidity";
	public static final String FACTOR = "Backbone.Factor";
	public static final String WAIT_FOR_RDV_TIME = "Backbone.WaitForRdvTime";
	public static final String SYNCHRONIZED = "Backbone.Synchronized";
	public static final String MODE = "Backbone.Mode";
	public static final String RELAYED = "Backbone.Relayed";
	public static final String EXT_TCP_ADDR = "Backbone.ExtTcpAddr";
	public static final String MULTICAST = "Backbone.Multicast";
	public static final String JXTA_TCP_PORT = "Backbone.TcpPort";
	public static final String JXTA_HTTP_PORT = "Backbone.HttpPort";
	public static final String PIPE_LIFETIME = "Backbone.PipeLifeTime";
	
	public static final String SESSION_ID_GENERATOR = "Session.GeneratorName";
	public static final String SESSION_DELAY = "Session.Delay";
	public static final String SESSION_DATA_PATH = "Session.DataPath";
	public static final String SESSION_MAX_CLIENTS = "Session.MaxClients";
	public static final String SESSION_MAX_SERVERS = "Session.MaxServers";
	public static final String SESSION_CLEANING_FREQ = "Session.CleaningFrequency";
	public static final String SESSION_SYNC_FREQ = "Session.SyncFrequency";
	
	public static final String SECURITY_PROTOCOL = "Security.Protocol";
	public static final String TRUSTMANAGER_TRUST_THRESHOLD = "TrustManager.trustThreshold";
	public static final String TRUSTMANAGER_URL = "TrustManager.trustManagerURL";
	
	//TODO Currently this functionality is not provided
//	public static final String USE_CORE_SECURITY = "Security.UseCoreSecurity";
	
	public static final String DEFAULT_TO_DENY_ON_PEP_RESPONSE = "Security.Access.DefaultDeny";
	
	private NetworkManagerApplicationSoapBindingImpl nm;
	
	/**
	 * Constructor. Creates a new "NetworkManagerConfigurator" object
	 * 
	 * @param nm the network manager implementation
	 * @param context the bundle's execution context
	 */
	public NetworkManagerConfigurator(NetworkManagerApplicationSoapBindingImpl nm, 
			BundleContext context) {
		
		super(context, Logger.getLogger(NetworkManagerConfigurator.class.getName()),
			NM_PID, CONFIGURATION_FILE);
		this.nm = nm;
		//set initial configurations
		this.nm.setTrustThreshold(Double.valueOf((String)this.getConfiguration().get(TRUSTMANAGER_TRUST_THRESHOLD)));
		this.nm.setTrustManager((String)this.getConfiguration().get(TRUSTMANAGER_URL));
	}
	
	/**
	 * Apply the configuration changes
	 * 
	 * @param updates the configuration changes
	 */
	@Override
	public void applyConfigurations(Hashtable updates) {
		if (this.nm.backboneMgr != null) {
			this.nm.backboneMgr.applyConfigurations(updates);
		}
		if(updates.containsKey(TRUSTMANAGER_TRUST_THRESHOLD)){
			this.nm.setTrustThreshold(Double.valueOf((String)updates.get(TRUSTMANAGER_TRUST_THRESHOLD)));
		}
		if(updates.containsKey(TRUSTMANAGER_URL)){
			this.nm.setTrustManager((String)updates.get(TRUSTMANAGER_URL));
		}
	}

}
