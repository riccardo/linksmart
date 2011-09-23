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
package eu.linksmart.selfstar.aql.osgisensor;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import eu.linksmart.selfstar.aql.db.Schema;
import eu.linksmart.selfstar.aql.db.Table;
import eu.linksmart.selfstar.aql.db.TableRegistry;
import eu.linksmart.selfstar.aql.db.Tuple;
import eu.linksmart.selfstar.aql.db.TupleIterator;
import eu.linksmart.selfstar.aql.distribution.QueryManager;

public class FelixOSGiSensor implements BundleListener {
	
	Table bundletable,devicetable,deploymenttable;
	BundleContext bc;
	Hashtable<Bundle,Tuple> bundles2tuples=new Hashtable<Bundle,Tuple>(20);
	Hashtable<Bundle,Tuple> bundles2deploymenttuples = new Hashtable<Bundle,Tuple>(20);
	
	public FelixOSGiSensor(BundleContext bc){
		this.bc=bc;
		makeDeviceTable();
		//makedeploymenttable called from makeBundleTable()...
		makeBundleTable();
		makeClientConnectionTable();
		makeServerConnectionTable();
		bc.addBundleListener(this);
	}

	private void makeDeploymentTable(){
		deploymenttable = new Table("Deployment", DeploymentSchema.instance());
	}
	
	private void makeDeviceTable(){
		devicetable = new Table("Devices",DeviceSchema.instance());
		// add this device, ie add a tuple with the uuid...
		devicetable.addTuple(DeviceSchema.instance().newTuple(QueryManager.getInstance().getUUID().toString()));
	}
	
	private void makeBundleTable(){
		BundleSchema bundleschema = BundleSchema.instance();
		bundletable= new Table("Bundles",bundleschema);
		makeDeploymentTable();
		//add current bundles:
		Tuple t;
		for (Bundle b:bc.getBundles()){
			t=bundleschema.newTuple(b);
			bundletable.addTuple(t);
			bundles2tuples.put(b, t);
			// add entry to deploymenttable...
			t=DeploymentSchema.instance().newTuple(b.getBundleId());
			deploymenttable.addTuple(t);
			bundles2deploymenttuples.put(b, t);
		}
	}

	
	@SuppressWarnings("static-access")
	public void bundleChanged(BundleEvent be) {
		if (be.getType()==be.INSTALLED){
			Tuple t = BundleSchema.instance().newTuple(be.getBundle());
			bundletable.addTuple(t);
			bundles2tuples.put(be.getBundle(), t);
			t=DeploymentSchema.instance().newTuple(be.getBundle().getBundleId());
			deploymenttable.addTuple(t);
		}
		if (be.getType()==be.UNINSTALLED){
			bundletable.removeTuple(bundles2tuples.get(be.getBundle()));
			bundles2tuples.remove(be.getBundle());
			// and update the deploymenttable
			deploymenttable.removeTuple(bundles2deploymenttuples.get(be.getBundle()));
			bundles2deploymenttuples.remove(be.getBundle());
		}
	}
	
	private Table cctable,sctable;
	private ClientConnectionSchema ccschema=ClientConnectionSchema.instance();
	private ServerConnectionSchema scschema=ServerConnectionSchema.instance();
	
	private void makeClientConnectionTable(){
		cctable = new Table(null,ClientConnectionSchema.instance());
		TableRegistry.getInstance().registerSource("ClientEndpoints", new TableRegistry.IteratorFactory() {
			
			public TupleIterator iterator() {
				updateClientConnectionTable();
				return cctable.iterator();
			}
		});
	}
	
	private void makeServerConnectionTable(){
		sctable = new Table(null,ServerConnectionSchema.instance());
		TableRegistry.getInstance().registerSource("ServerEndpoints", new TableRegistry.IteratorFactory() {
			
			public TupleIterator iterator() {
				updateServerConnectionTable();
				return sctable.iterator();
			}
		});
	}
	
	Matcher pkeymatcher,ckeymatcher,skeymatcher;
	private final String connectionkey = "http.clientprotocol.connections.";
	private final String protocolkey = "http.clientprotocol.";
	private void makeClientMatchers(){
		Pattern p = Pattern.compile("http\\.clientprotocol\\.connections\\.((\\d+))");
		ckeymatcher = p.matcher("");
	}
	
	private void updateServerConnectionTable(){
		sctable.reset();
		if (skeymatcher==null)
			skeymatcher=Pattern.compile("serverendpoint\\.((\\w+))").matcher("");
		Properties p = System.getProperties();
		Iterator<Object> keys = p.keySet().iterator();
		String key,protocol,value;
		while(keys.hasNext()){
			key=(String)keys.next();
			skeymatcher.reset(key); 
			if (skeymatcher.matches()){
				if (skeymatcher.groupCount()!=2)
					error("The key "+key+" confused the pattern matcher in FelixOSGiSensor.makeClientConnectionTable");
				protocol = skeymatcher.group(1);
				value = p.getProperty(key);
				String[] endpoints=value.split(",");
				//protocol = p.getProperty(protocolkey+cid, "tcp");
				 // now did,cid,endpoints={adresses}, protocol specifies all tuples for this client
				for (String endpoint:endpoints){
					sctable.addTuple(scschema.newTuple(QueryManager.getInstance().getUUID().toString(), 
							endpoint, protocol));	
				}	
			}
		}
	}

	private void updateClientConnectionTable(){
		cctable.reset();
		if (pkeymatcher==null || ckeymatcher==null)
			makeClientMatchers();
		Properties p = System.getProperties();
		Iterator<Object> keys = p.keySet().iterator();
		String key,protocol,cid,value;
		while(keys.hasNext()){
			key=(String)keys.next();
			ckeymatcher.reset(key); 
			if (ckeymatcher.matches()){
				if (ckeymatcher.groupCount()!=2)
					error("The key "+key+" confused the pattern matcher in FelixOSGiSensor.makeClientConnectionTable");
				cid = ckeymatcher.group(1);
				value = p.getProperty(key);
				String[] endpoints=value.split(",");
				protocol = p.getProperty(protocolkey+cid, "tcp");
				 // now did,cid,endpoints={adresses}, protocol specifies all tuples for this client
				for (String endpoint:endpoints){
					cctable.addTuple(ccschema.newTuple(QueryManager.getInstance().getUUID().toString(), 
							cid, endpoint, protocol));	
				}	
			}
		}
	}

	private void error(String message){
		throw new RuntimeException(message);
	}
	
}
